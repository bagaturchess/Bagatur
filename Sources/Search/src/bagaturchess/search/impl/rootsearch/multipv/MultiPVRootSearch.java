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
package bagaturchess.search.impl.rootsearch.multipv;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;


public class MultiPVRootSearch extends RootSearch_BaseImpl {
	
	
	private IRootSearch rootSearch;
	private MultiPVMediator current_mediator_multipv;
	private ISearchStopper current_stopper;
	
	
	public MultiPVRootSearch(IRootSearchConfig _engineConfiguration, IRootSearch _rootSearch) {
		super(new Object[] {_engineConfiguration, _rootSearch.getSharedData()});
		rootSearch = _rootSearch;
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		super.newGame(_bitboardForSetup); //Keep it for multipv mediator
		
		rootSearch.newGame(_bitboardForSetup);
	}


	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations,
			boolean useMateDistancePrunning, IFinishCallback finishCallback, int[] prevPV) {
		
		if (current_mediator_multipv != null) {
			throw new IllegalStateException("MultiPV search started without beeing stopped.");
		}
		
		setupBoard(_bitboardForSetup);
		
		//!!!Do not setup the board of rootSearch. multiPV mediator will set it up for each move
		//rootSearch.setupBoard(_bitboardForSetup);
		
		current_mediator_multipv = new MultiPVMediator(getRootSearchConfig(), rootSearch,
				getBitboardForSetup(), mediator, startIteration - 1, maxIterations - 1, //Should be -1, because it plays each move and than search with depth maxIterations
				useMateDistancePrunning, finishCallback);
		
		current_stopper = mediator.getStopper();
		
		current_mediator_multipv.ready();
	}
	
	
	@Override
	public void shutDown() {
		rootSearch.shutDown();
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		return rootSearch.getTPTUsagePercent();
	}
	
	
	@Override
	public void decreaseTPTDepths(int reduction) {
		rootSearch.decreaseTPTDepths(reduction);
	}
	
	
	@Override
	public void stopSearchAndWait() {
		
		if (current_stopper != null) {
			
			current_stopper.markStopped();
			
			rootSearch.stopSearchAndWait();
			
			current_mediator_multipv = null;
			current_stopper = null;
		}
	}
}
