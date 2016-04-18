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


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.utils.DEBUGSearch;


public class MTDSequentialSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	private ISearch searcher;
	

	public MTDSequentialSearch(Object[] args) {
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
	}
	
	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, boolean useMateDistancePrunning, IFinishCallback finishCallback) {
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
		}
		
		searcher.newSearch();
		
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
				
				searcher.search(mediator, startIteration, maxIterations, useMateDistancePrunning);
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
			
		} catch(Throwable t) {
			//Do nothing
		}
	}
}
