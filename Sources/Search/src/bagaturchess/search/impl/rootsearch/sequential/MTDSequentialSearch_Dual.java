/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.search.impl.rootsearch.sequential;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;


public class MTDSequentialSearch_Dual extends RootSearch_BaseImpl {
	
	private static int MATERIAL_DEPTH_INCREMENT = 1;
	private static int MATERIAL_MARGIN = 90;
	
	
	private ExecutorService executor;
	private ISearch searcher;
	private ISearch searcher_material;
	
	
	public MTDSequentialSearch_Dual(Object[] args) {
		super(args);
		executor = Executors.newFixedThreadPool(1);
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup);
		
		String searchClassName =  getRootSearchConfig().getSearchClassName();
		searcher = (ISearch) ReflectionUtils.createObjectByClassName_ObjectsConstructor(
						searchClassName,
						new Object[] {getBitboardForSetup(),  getRootSearchConfig(), getSharedData()}
					);
		
		createMaterialSearcher();
	}


	private void createMaterialSearcher() {
		IRootSearchConfig material_rootSearchConfig = (IRootSearchConfig) ReflectionUtils.createObjectByClassName_StringsConstructor(
			"bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl",
			new String[] {
					"bagaturchess.search.impl.alg.impl0.SearchMTD0",
					"bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD",
					"bagaturchess.engines.material.MaterialBoardConfigImpl",
					"bagaturchess.engines.material.MaterialEvalConfigImpl"
					}
		);
		
		SharedData material_sharedData = new SharedData(ChannelManager.getChannel(), material_rootSearchConfig);
		
		searcher_material = (ISearch) ReflectionUtils.createObjectByClassName_ObjectsConstructor(
				material_rootSearchConfig.getSearchClassName(),
				new Object[] {getBitboardForSetup(),  material_rootSearchConfig, material_sharedData}
			);
	}
	
	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, boolean useMateDistancePrunning, IFinishCallback finishCallback, int[] prevPV) {
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
		}
		
		searcher.newSearch();
		searcher_material.newSearch();
		
		if (DEBUGSearch.DEBUG_MODE) mediator.dump("Sequential search started from depth " + 1 + " to depth " + maxIterations);
		
		setupBoard(_bitboardForSetup);
		
		executor.execute(
				new Task(mediator, startIteration, maxIterations,
						useMateDistancePrunning, finishCallback)
			);
	}
	
	
	private class Task implements Runnable {
		
		
		private ISearchMediator mediator;
		private int startIteration;
		private int maxIterations;
		private boolean useMateDistancePrunning;
		private IFinishCallback callback;
		
		
		Task(ISearchMediator _mediator, int _startIteration, int _maxIterations,
				boolean _useMateDistancePrunning, IFinishCallback _callback) {
			mediator = _mediator;
			startIteration = _startIteration;
			maxIterations = _maxIterations;
			useMateDistancePrunning = _useMateDistancePrunning;
			callback = _callback;
		}
		
		
		public void run() {
			try {
				
				DualSearchMediatorImpl1 materialMediator = new DualSearchMediatorImpl1(mediator);
				DualSearchMediatorImpl1 fullEvalMediator = new DualSearchMediatorImpl1(mediator);
				
				for (int depth = startIteration; depth <= maxIterations; depth++) {
					
					if (searcher.getEnv().getBitboard().getMaterialFactor().getTotalFactor() < 31) {
						searcher.search(mediator, depth, depth, useMateDistancePrunning);
					} else {
					
						int root_material_eval = (int) searcher_material.getEnv().getEval().fullEval(0, ISearch.MIN, ISearch.MAX, searcher_material.getEnv().getBitboard().getColourToMove());
						searcher_material.search(materialMediator,
								depth + MATERIAL_DEPTH_INCREMENT, depth + MATERIAL_DEPTH_INCREMENT, useMateDistancePrunning);
						ISearchInfo materialSearchInfo1 = materialMediator.getLastInfo();
						int material_eval = materialSearchInfo1.getEval();
						int material_evall_diff = Math.abs(material_eval - root_material_eval);
						if (material_evall_diff >= MATERIAL_MARGIN) {
							mediator.changedMajor(materialSearchInfo1);
						} else {
							searcher.search(fullEvalMediator, depth, depth, useMateDistancePrunning);
							ISearchInfo fullEvalSearchInfo = fullEvalMediator.getLastInfo();
							int fullEvalBestMove = fullEvalSearchInfo.getBestMove();
							
							searcher_material.getEnv().getBitboard().makeMoveForward(fullEvalBestMove);
							searcher_material.search(materialMediator,
									depth + (MATERIAL_DEPTH_INCREMENT - 1), depth + (MATERIAL_DEPTH_INCREMENT - 1), useMateDistancePrunning);
							ISearchInfo materialSearchInfo = materialMediator.getLastInfo();
							material_eval = -materialSearchInfo.getEval();
							material_evall_diff = Math.abs(material_eval - root_material_eval);
							if (material_evall_diff >= MATERIAL_MARGIN) {
								mediator.changedMajor(materialSearchInfo1);
							} else {
								mediator.changedMajor(fullEvalSearchInfo);
							}
							searcher_material.getEnv().getBitboard().makeMoveBackward(fullEvalBestMove);
						}
					}
				}

				callback.ready();
				
			} catch(SearchInterruptedException sie) {
				//Do Nothing
			} catch(Throwable t) {
				mediator.dump(t);
			}
		}
	}
	
	@Override
	public void shutDown() {
		try {
			
			executor.shutdownNow();
			searcher = null;
			searcher_material = null;
			
		} catch(Throwable t) {
			//Do nothing
		}
	}


	@Override
	public int getTPTUsagePercent() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		searcher.getEnv().getTPT().correctAllDepths(reduction);
		searcher_material.getEnv().getTPT().correctAllDepths(reduction);
	}


	@Override
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations,
			boolean useMateDistancePrunning, int[] prevPV) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopSearchAndWait() {
		throw new UnsupportedOperationException();
	}
}
