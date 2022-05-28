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

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.CompositeStopper;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.search.impl.rootsearch.sequential.mtd.NullwinSearchTask;
import bagaturchess.search.impl.rootsearch.sequential.mtd.SearchManager;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class SequentialSearch_MTD extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private ISearch searcher;
	
	private PVManager pvman;
	
	
	public SequentialSearch_MTD(Object[] args) {
		
		super(args);
		
		executor = Executors.newFixedThreadPool(1);
		
		pvman = new PVManager(ISearch.MAX_DEPTH);
	}
	
	
	public IRootSearchConfig getRootSearchConfig() {
		
		return (IRootSearchConfig) super.getRootSearchConfig();
	}
	
	
	@Override
	public void recreateEvaluator() {

		searcher.getEnv().recreateEvaluator();
	}
	
	
	@Override
	public void createBoard(IBitBoard _bitboardForSetup) {
		
		super.createBoard(_bitboardForSetup);
		
		String searchClassName =  getRootSearchConfig().getSearchClassName();
		
		searcher = (ISearch) ReflectionUtils.createObjectByClassName_ObjectsConstructor(
				
						searchClassName,
						
						new Object[] {getBitboardForSetup(),  getRootSearchConfig(), getSharedData()}
					);
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, final Go go) {
		
		negamax(_bitboardForSetup, mediator, timeController, multiPVCallback, go, false);
	}

	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, final Go go, boolean dont_wrap_mediator) {
		
		if (stopper != null) {
			
			throw new IllegalStateException("MTDSequentialSearch started whithout beeing stopped");
		}
		
		stopper = new Stopper();
		
		searcher.newSearch();
		
		setupBoard(_bitboardForSetup);
		
		
		int startIteration = (go.getStartDepth() == Go.UNDEF_STARTDEPTH) ? 1 : go.getStartDepth();
		int maxIterations = (go.getDepth() == Go.UNDEF_DEPTH) ? ISearch.MAX_DEPTH : go.getDepth();
		Integer initialValue = (go.getBeta() == Go.UNDEF_BETA) ? null : go.getBeta();
		int[] prevPV = BoardUtils.getMoves(go.getPv(), _bitboardForSetup);
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
			go.setDepth(maxIterations);
		}
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch started from depth " + startIteration + " to depth " + maxIterations);
		
		
		final int[] final_prevPV = prevPV;
		
		if (initialValue == null) {
			
			initialValue = (int) searcher.getEnv().getEval().fullEval(0, ISearch.MIN, ISearch.MAX, getBitboardForSetup().getColourToMove());
		}
		
		
		if (!dont_wrap_mediator) {
			
			//Original mediator should be an instance of UCISearchMediatorImpl_Base
			mediator = (mediator instanceof MultiPVMediator) ?
					
					new Mediator_AlphaAndBestMoveWindow(mediator) :
						
					new NPSCollectorMediator(new Mediator_AlphaAndBestMoveWindow(mediator));
		}
		
		final SearchManager distribution = new SearchManager(mediator, startIteration, maxIterations, initialValue);
		
		//final ISearchStopper stopper = new MTDStopper(getBitboardForSetup().getColourToMove(), distribution);
		mediator.setStopper(new CompositeStopper(new ISearchStopper[] {mediator.getStopper(), stopper}, true ));
		
		
		final ISearchMediator final_mediator = mediator;
		
		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch before loop");
					
					while (!final_mediator.getStopper().isStopped() //Condition for normal play
							&& distribution.getCurrentDepth() <= distribution.getMaxIterations() //Condition for fixed depth
							) {
						
						//if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch in loop : task.run()");
						
						Runnable task = new NullwinSearchTask(searcher, distribution, getBitboardForSetup(), pvman,
								final_mediator, !go.isPonder(), final_prevPV
																);
						task.run();
					}
					
					ChannelManager.getChannel().dump("MTDSequentialSearch after loop final_mediator.getStopper().isStopped()="
							+ final_mediator.getStopper().isStopped()
							+ ", distribution.getCurrentDepth()=" + distribution.getCurrentDepth() + ", distribution.getMaxIterations()=" + distribution.getMaxIterations());
					
					if (!isStopped()) {
						
						ChannelManager.getChannel().dump("MTDSequentialSearch not stopped - stopping searcher ...");
						
						if (stopper == null) {
							
							throw new IllegalStateException();
						}
						
						stopper.markStopped();
						
						stopper = null;
						
						
						if (multiPVCallback == null) {
							
							//Non MultiPV search
							ChannelManager.getChannel().dump("MTDSequentialSearch calling final_mediator.getBestMoveSender().sendBestMove()");
								
							final_mediator.getBestMoveSender().sendBestMove();
							
						} else {
							
							//MultiPV search
							multiPVCallback.ready();
						}
					}
					
				} catch (Throwable t) {
					
					ChannelManager.getChannel().dump(t);
					
					ChannelManager.getChannel().dump(t.getMessage());
				}
			}
		});
	}
	
	
	@Override
	public void shutDown() {
		
		try {
			
			executor.shutdownNow();
			
			searcher = null;
			
		} catch (Throwable t) {
			
			//Do nothing
		}
	}


	@Override
	public int getTPTUsagePercent() {
		
		if (searcher.getEnv().getTPT() == null) {
			
			return 0;
		}
		
		return searcher.getEnv().getTPT().getUsage();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		
		searcher.getEnv().getTPT().correctAllDepths(reduction);
	}
}
