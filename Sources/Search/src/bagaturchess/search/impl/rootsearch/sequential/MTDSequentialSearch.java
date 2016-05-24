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
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.utils.DEBUGSearch;


public class MTDSequentialSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	private ISearch searcher;
	

	public MTDSequentialSearch(Object[] args) {
		super(args);
		executor = Executors.newFixedThreadPool(1);
	}
	
	
	public IRootSearchConfig_SMP getRootSearchConfig() {
		return (IRootSearchConfig_SMP) super.getRootSearchConfig();
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup);
		
		String searchClassName =  getRootSearchConfig().getSearchClassName();
		searcher = (ISearch) ReflectionUtils.createObjectByClassName_ObjectsConstructor(
						searchClassName,
						new Object[] {getBitboardForSetup(),  getRootSearchConfig(), getSharedData()}
					);
	}
	
	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback finishCallback) {
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
		} else {
			if (maxIterations < maxIterations + getRootSearchConfig().getHiddenDepth()) {//Type overflow
				maxIterations += getRootSearchConfig().getHiddenDepth();
			}
		}
		
		searcher.newSearch();
		
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) mediator.dump("Sequential search started from depth " + 1 + " to depth " + maxIterations);
		
		
		//UCISearchMediatorImpl_Base
		mediator = (mediator instanceof MultiPVMediator) ? mediator : new Mediator_AlphaAndBestMoveWindow(mediator, this);
		final SearchManager distribution = new SearchManager(mediator, getBitboardForSetup(), getSharedData(), getBitboardForSetup().getHashKey(),
				startIteration, maxIterations, finishCallback);
		
		final ISearchStopper mtd_stopper = new MTDStopper(getBitboardForSetup().getColourToMove(), distribution);
		mediator.getStopper().setSecondaryStopper(mtd_stopper);
		
		
		final ISearchMediator final_mediator = mediator;
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (!final_mediator.getStopper().isStopped()) {
						Runnable task = new NullwinSearchTask(executor, searcher, distribution, getBitboardForSetup(),
								final_mediator, getSharedData(), useMateDistancePrunning
																);
						task.run();
					}
					
					//finishCallback.ready();
					
				} catch(Throwable t) {
					final_mediator.dump(t);
					final_mediator.dump(t.getMessage());
				}
			}
		});
	}


	@Override
	public void shutDown() {
		try {
			
			executor.shutdownNow();
			searcher = null;
			
		} catch(Throwable t) {
			//Do nothing
		}
	}


	@Override
	public int getTPTUsagePercent() {
		return searcher.getEnv().getTPTUsagePercent();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		searcher.getEnv().getTPT().correctAllDepths(reduction);
	}
}
