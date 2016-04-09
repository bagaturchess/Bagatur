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
package bagaturchess.search.impl.rootsearch.parallel;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;


public class MTDParallelSearch extends RootSearch_BaseImpl {
	
	
	private Executor executor;
	private SearchersPool searchers;
	
	
	public MTDParallelSearch(Object[] args) {
		super(args);
		
		executor = Executors.newFixedThreadPool(getRootSearchConfig().getThreadsCount());
		
		ChannelManager.getChannel().dump("Thread pool created with " + getRootSearchConfig().getThreadsCount() + " threads.");
		
		searchers = new SearchersPool(getRootSearchConfig().getBoardConfig());
	}
	
	
	public IRootSearchConfig_SMP getRootSearchConfig() {
		return (IRootSearchConfig_SMP) super.getRootSearchConfig();
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup); 
		
		searchers.newGame(getBitboardForSetup(), getSharedData(), getRootSearchConfig().getThreadsCount());
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator root_mediator, int startIteration, int maxIterations,
			boolean useMateDistancePrunning, IFinishCallback finishCallback) {
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
		} else {
			if (maxIterations < maxIterations + getRootSearchConfig().getHiddenDepth()) {//Type overflow
				maxIterations += getRootSearchConfig().getHiddenDepth();
			}
		}
		
		//if (getSharedData().getTPT() != null) {
		//	getSharedData().getTPT().clearCount_UniqueInserts();
		//}
		
		//if (DEBUGSearch.DEBUG_MODE) searchers.dumpSearchers(mediator); // Too much logs
		
		searchers.newSearch();
		//searchers.waitSearchersToStop(); //Removed because of pondering. During pondering all threads are busy.
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) root_mediator.dump("Parallel search started from depth " + startIteration + " to depth " + maxIterations);
		
		//root_mediator.dump("root_mediator=" + root_mediator.getClass().getName());
		
		//root_mediator.dump("negamax: startIteration=" + startIteration + ", maxIterations=" + maxIterations);
		
		ISearchMediator parallel_mediator = (root_mediator instanceof MultiPVMediator) ? root_mediator : new ParallelMediator(root_mediator);
		//ISearchMediator parallel_mediator = new ParallelMediator(root_mediator);
		//root_mediator.dump("parallel_mediator=" + parallel_mediator.getClass().getName());
		
		SearchManager distribution = new SearchManager(parallel_mediator, getSharedData(), getBitboardForSetup().getHashKey(),
				startIteration, maxIterations, finishCallback);
		
		ISearchStopper mtd_stopper = new MTDStopper(getBitboardForSetup().getColourToMove(), distribution /*, parallel_mediator*/);
		root_mediator.getStopper().setSecondaryStopper(mtd_stopper);
		
		executor.execute(new NullwinSearchTask(executor, searchers, distribution, getBitboardForSetup(),
												parallel_mediator, getSharedData(), useMateDistancePrunning)
						);
	}
	
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\r\n";
		result += searchers.toString();
		
		return result;
	}
}
