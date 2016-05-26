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
package bagaturchess.search.impl.rootsearch.mixed;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch;
import bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch;


public class MTDMixedSearch extends RootSearch_BaseImpl {
	
	
	private static final int SEQUENTIAL_DEPTH = 6;
	
	private MTDSequentialSearch sequentialSearch;
	private MTDParallelSearch parallelSearch;
	
	
	public MTDMixedSearch(Object[] args) {
		super(args);
		sequentialSearch = new MTDSequentialSearch(new Object[] {args[0], getSharedData()});
		parallelSearch = new MTDParallelSearch(new Object[] {args[0], getSharedData()});
	}
	
	
	@Override
	public void newGame(IBitBoard bitboardForSetup) {
		sequentialSearch.newGame(bitboardForSetup);
		parallelSearch.newGame(bitboardForSetup);
	}
		
	
	@Override
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, int startIteration, int maxIterations,
			boolean useMateDistancePrunning, IFinishCallback finishCallback, int[] prevPV) {
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
		}
		
		if (maxIterations <= SEQUENTIAL_DEPTH) {
			
			sequentialSearch.negamax(bitboardForSetup, mediator, startIteration, maxIterations, useMateDistancePrunning, finishCallback, prevPV);
			
		} else {
			if (startIteration <= SEQUENTIAL_DEPTH) {
				
				IFinishCallback parallelSearchStarter = new FinishCallback_StartParallelSearch(parallelSearch, bitboardForSetup,
						mediator, SEQUENTIAL_DEPTH + 1, maxIterations, useMateDistancePrunning, finishCallback);
				sequentialSearch.negamax(bitboardForSetup, mediator, 1, SEQUENTIAL_DEPTH, useMateDistancePrunning, parallelSearchStarter, prevPV);
				
			} else {
				
				IFinishCallback parallelSearchStarter = new FinishCallback_StartParallelSearch(parallelSearch, bitboardForSetup,
						mediator, SEQUENTIAL_DEPTH + 1, maxIterations, useMateDistancePrunning, finishCallback);
				parallelSearchStarter.ready();
			}
		}
	}
	
	@Override
	public void shutDown() {
		try {
			
			sequentialSearch.shutDown();
			parallelSearch.shutDown();
			
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
		sequentialSearch.decreaseTPTDepths(reduction);
		parallelSearch.decreaseTPTDepths(reduction);
	}
}
