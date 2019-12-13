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


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MultiPVRootSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	private IRootSearch rootSearch;
	private MultiPVMediator current_mediator_multipv;
	private ISearchStopper current_stopper;
	
	
	public MultiPVRootSearch(IRootSearchConfig _engineConfiguration, IRootSearch _rootSearch) {
		super(new Object[] {_engineConfiguration, _rootSearch.getSharedData()});
		rootSearch = _rootSearch;
		executor = Executors.newFixedThreadPool(1);
	}
	
	
	@Override
	public void createBoard(IBitBoard _bitboardForSetup) {
		super.createBoard(_bitboardForSetup); //Keep it for multipv mediator
		
		rootSearch.createBoard(_bitboardForSetup);
	}


	@Override
	public void negamax(IBitBoard _bitboardForSetup, final ISearchMediator mediator, ITimeController timeController, IFinishCallback finishCallback, Go go) {
		
		if (current_mediator_multipv != null) {
			throw new IllegalStateException("MultiPV search started without beeing stopped.");
		}
		
		setupBoard(_bitboardForSetup);
		
		//!!!Do not setup the board of rootSearch. multiPV mediator will set it up for each move
		//rootSearch.setupBoard(_bitboardForSetup);
		
		//adjust go: startIteration - 1, maxIterations - 1, //Should be -1, because it plays each move and than search with depth=maxIterations
		int max_depth = Go.UNDEF_DEPTH;
		if (go.getDepth() != Go.UNDEF_DEPTH) {
			max_depth = go.getDepth() - 1;
			if (max_depth <= 0) {//Doesnt'work with 0, because getMateScores(0) throws exception
				max_depth = 1;
			}
			go.setDepth(max_depth);
		}
		if (go.getStartDepth() != Go.UNDEF_STARTDEPTH) {
			if (go.getStartDepth() <= 0) {//Doesnt'work with 0, because getMateScores(0) throws exception
				go.setStartDepth(1);
			} else {
				go.setStartDepth(go.getStartDepth() - 1);
			}
		}
		
		current_mediator_multipv = new MultiPVMediator(getRootSearchConfig(), rootSearch,
				getBitboardForSetup(), mediator,
				go);
		
		current_stopper = mediator.getStopper();
		
		current_mediator_multipv.ready();
		
		
		final int final_max_depth = max_depth;
		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (!current_stopper.isStopped()
							&& current_mediator_multipv.getCurrentDepth() <= final_max_depth
							) {
						
						try {
							Thread.sleep(15);
						} catch (InterruptedException e) {}
					}
					
					stopSearchAndWait();
					
					mediator.getBestMoveSender().sendBestMove();
					
				} catch(Throwable t) {
					ChannelManager.getChannel().dump(t);
				}
			}
		});
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
