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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD;
import bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator;
import bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MTDParallelSearch_ThreadsImpl extends MTDParallelSearch_BaseImpl {
	
	
	public MTDParallelSearch_ThreadsImpl(Object[] args) {
		
		super(args);
		
	}
	
	
	@Override
	protected void sequentialSearchers_Create() {
		
		//ITTable last_tt 					= null;
		int root_search_first_move_index 	= 0;
		
		for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++ ) {
			
			try {
				
				SequentialSearch_MTD searcher = (SequentialSearch_MTD)
						ReflectionUtils.createObjectByClassName_ObjectsConstructor(SequentialSearch_MTD.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
				
				//getTPT - throws NPE as searcher still doesn;t have TT set.
				//Will not be used anyway, because with root_search_first_move_index ELO is less
				/*ITTable current_tt = searcher.getTPT();
				
				if (current_tt != last_tt) {
					
					root_search_first_move_index = 0;
				}
				
				last_tt = current_tt;
				*/
				
				searcher.setRootSearchFirstMoveIndex(root_search_first_move_index++);
				
				addSearcher(searcher);
				
				
			} catch (Throwable t) {
				ChannelManager.getChannel().dump(t);
			}
		}
	}
	
	
	@Override
	protected ISearchMediator sequentialSearchers_WrapMediator(ISearchMediator mediator) {
		
		return new Mediator_AlphaAndBestMoveWindow(mediator);
	}
	
	
	@Override
	protected void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, Go go, boolean dont_wrap_mediator) {
		
		((SequentialSearch_MTD)searcher).negamax(_bitboardForSetup, mediator, timeController, multiPVCallback, go, dont_wrap_mediator);
	}
	
	
	@Override
	protected SearchersInfo createSearchersInfo(final int startIteration) {
		
		// Is one thread is enough to start the new depth?
		//return new SearchersInfo(startIteration, 0.00001d); //0.00001d (small 0+ number) - Send info when the first thread reaches new depth
		//return new SearchersInfo(startIteration, 0.5d); //0.5d - Send info when the half of the threads reach the current depth
		//return new SearchersInfo(startIteration, 1d); //1d - Send info when all threads reach the current depth. 1d is a risky extreme - if one thread hangs, search of the current depth will never end.
		//0.75d - Send info when 75% of the threads reach the current depth
		
		//Assumptions what is the difference:
		//1. If there is 1 TT (or less TTs) than the consensus of a smaller amount of threads should be necessary to prove the new_depth move is valid and search errors are compensated, because they share mostly the same search data stored in the TT(s).
		//3. The small amount of TTs is used, the more help each thread receives (although the NPS degradation on some OS/Java versions), the bigger depth each one thread will reach faster.
		//4. We use at min 2 threads and max MAX_THREADS - 2, in order to stay away from both extremes.
		//2. Just note: The more aggressive static and forward pruning in the single core version of the search is, the more need of validation of the move in the new depth is necessary.
		double at_least_2_threads = 2 * 1 / (double) getRootSearchConfig().getThreadsCount();
		//double max_threads_minus_2 = Math.min(1, 1 / (double) getRootSearchConfig().getTPTsCount()) - at_least_2_threads;
		//return new SearchersInfo(startIteration, Math.max(at_least_2_threads, max_threads_minus_2));
		return new SearchersInfo(startIteration, at_least_2_threads);
	}
	
	
	@Override
	protected boolean restartSearchersOnNewDepth() {
		
		return false;
	}
}
