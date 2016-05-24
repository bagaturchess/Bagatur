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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.rootsearch.sequential.MTDStopper;
import bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator;
import bagaturchess.search.impl.rootsearch.sequential.NullwinSearchTask;
import bagaturchess.search.impl.rootsearch.sequential.SearchManager;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;


public class MTDParallelSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private List<IRootSearch> searchers;
	
	
	public MTDParallelSearch(Object[] args) {
		super(args);
		
		executor = Executors.newFixedThreadPool(getRootSearchConfig().getThreadsCount());
		
		ChannelManager.getChannel().dump("Thread pool created with " + getRootSearchConfig().getThreadsCount() + " threads.");
		
		searchers = new ArrayList<IRootSearch>();
	}
	
	
	public IRootSearchConfig_SMP getRootSearchConfig() {
		return (IRootSearchConfig_SMP) super.getRootSearchConfig();
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup); 
		
		for (int i = 0; i < searchers.size(); i++) {
			searchers.get(i).newGame(getBitboardForSetup());
		}
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator root_mediator, int startIteration, int maxIterations,
			boolean useMateDistancePrunning, IFinishCallback finishCallback) {
		
		//searchers.waitSearchersToStop(); //Removed because of pondering. During pondering all threads are busy.
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) root_mediator.dump("Parallel search started from depth " + startIteration + " to depth " + maxIterations);
		
		
		List<ISearchMediator> mediators = new ArrayList<ISearchMediator>();
		for (int i = 0; i < searchers.size(); i++) {
			mediators.add(new BucketMediator(root_mediator));
		}
		
		
		//root_mediator.dump("root_mediator=" + root_mediator.getClass().getName());
		
		//root_mediator.dump("negamax: startIteration=" + startIteration + ", maxIterations=" + maxIterations);
		
		ISearchMediator parallel_mediator = (root_mediator instanceof MultiPVMediator) ? root_mediator : new NPSCollectorMediator(root_mediator);
		//ISearchMediator parallel_mediator = new ParallelMediator(root_mediator);
		//root_mediator.dump("parallel_mediator=" + parallel_mediator.getClass().getName());
		
		SearchManager distribution = new SearchManager(parallel_mediator, getBitboardForSetup(), getSharedData(), getBitboardForSetup().getHashKey(),
				startIteration, maxIterations, finishCallback);
		
		ISearchStopper mtd_stopper = new MTDStopper(getBitboardForSetup().getColourToMove(), distribution /*, parallel_mediator*/);
		root_mediator.getStopper().setSecondaryStopper(mtd_stopper);
		
		//executor.execute(new NullwinSearchTask(executor, searchers, distribution, getBitboardForSetup(),
		//										parallel_mediator, getSharedData(), useMateDistancePrunning)
		//				);
		
		/*
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
		*/
	}
	
	
	@Override
	public void shutDown() {
		try {
			
			executor.shutdownNow();
			
			for (int i = 0; i < searchers.size(); i++) {
				searchers.get(i).shutDown();
			}
			
			searchers = null;
			
		} catch(Throwable t) {
			//Do nothing
		}
	}


	@Override
	public int getTPTUsagePercent() {
		int sum = 0;
		for (int i = 0; i < searchers.size(); i++) {
			sum += searchers.get(i).getTPTUsagePercent();
		}
		return sum / searchers.size();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		for (int i = 0; i < searchers.size(); i++) {
			searchers.get(i).decreaseTPTDepths(reduction);
		}
	}
	
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\r\n";
		result += searchers.toString();
		
		return result;
	}
	
	
	private class BucketMediator extends SearchMediatorProxy {
		
		
		List<ISearchInfo> minorInfos;
		List<ISearchInfo> majorInfos;
		
		
		public BucketMediator(ISearchMediator _parent) {
			super(_parent);
			minorInfos = new ArrayList<ISearchInfo>();
			majorInfos = new ArrayList<ISearchInfo>();
		}
		
		
		public void changedMajor(ISearchInfo info) {
			majorInfos.add(info);
		}
		
		
		public void changedMinor(ISearchInfo info) {
			minorInfos.add(info);
		}
	}
}
