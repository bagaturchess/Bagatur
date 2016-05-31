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
import bagaturchess.search.api.internal.CompositeStopper;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.pv.PVHistoryEntry;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;


public class MTDSequentialSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	private ISearch searcher;
	

	public MTDSequentialSearch(Object[] args) {
		super(args);
		executor = Executors.newFixedThreadPool(1);
	}
	
	
	public IRootSearchConfig getRootSearchConfig() {
		return (IRootSearchConfig) super.getRootSearchConfig();
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
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback, final int[] prevPV) {
		negamax(_bitboardForSetup, mediator, startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV, false, null);
	}
	
	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback,
			int[] prevPV, boolean dont_wrap_mediator, Integer initialValue) {
		
		//TODO: store pv in pvhistory
		
		if (stopper != null) {
			throw new IllegalStateException("MTDSequentialSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			throw new IllegalStateException("maxIterations=" + maxIterations + " > ISearch.MAX_DEPTH");
		}
		
		searcher.newSearch();
		
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch started from depth " + startIteration + " to depth " + maxIterations);
		
		
		if (prevPV == null) {
			PVHistoryEntry historyEntry = searcher.getEnv().getPVs().getPV(getBitboardForSetup().getHashKey());
			if (historyEntry != null) {
				prevPV = historyEntry.getPv();
				initialValue = historyEntry.getEval();
			}
		}
		final int[] final_prevPV = prevPV;
				
		
		if (!dont_wrap_mediator) {
			//Original mediator should be an instance of UCISearchMediatorImpl_Base
			mediator = (mediator instanceof MultiPVMediator) ?
					new Mediator_AlphaAndBestMoveWindow(mediator, this) :
					new NPSCollectorMediator(new Mediator_AlphaAndBestMoveWindow(mediator, this));
		}
		
		final SearchManager distribution = new SearchManager(mediator, getBitboardForSetup(), getSharedData(), getBitboardForSetup().getHashKey(),
				startIteration, maxIterations, initialValue);
		
		//final ISearchStopper stopper = new MTDStopper(getBitboardForSetup().getColourToMove(), distribution);
		mediator.setStopper(new CompositeStopper(new ISearchStopper[] {mediator.getStopper(), stopper} ));
		
		
		final ISearchMediator final_mediator = mediator;
		
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch before loop");
					
					while (!final_mediator.getStopper().isStopped() //Condition for normal play
							&& distribution.getCurrentDepth() <= distribution.getMaxIterations() //Condition for fixed depth
							) {
						
						Runnable task = new NullwinSearchTask(searcher, distribution, getBitboardForSetup(),
								final_mediator, useMateDistancePrunning, final_prevPV
																);
						task.run();
					}
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDSequentialSearch after loop stopper stopped " + final_mediator.getStopper().isStopped());
					
					if (stopper == null) {
						throw new IllegalStateException();
					}
					stopper.markStopped();
					stopper = null;
					
					
					if (multiPVCallback == null) {//Non multiPV search
						final_mediator.getBestMoveSender().sendBestMove();
					} else {
						//MultiPV search
						multiPVCallback.ready();
					}
					
				} catch(Throwable t) {
					ChannelManager.getChannel().dump(t);
					ChannelManager.getChannel().dump(t.getMessage());
				}
			}
		});
	}
	
	
	public boolean isStopped() {
		return stopper == null;
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
