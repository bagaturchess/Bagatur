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

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.alg.BetaGenerator;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.pv.PVHistoryEntry;
import bagaturchess.search.impl.pv.PVNode;


public class NullwinSearchTask implements Runnable {
	
	
	private boolean DEBUG = false;
	
	
	private Executor tpool;
	private SearchersPool searchers;
	private SearchManager distribution;
	private IBitBoard bitboard;
	private ISearchMediator mediator;
	private SharedData sharedData;
	private boolean useMateDistancePrunning;
	
	
	public NullwinSearchTask(Executor _tpool, SearchersPool _searchers, SearchManager _distribution,
			IBitBoard _bitboard, ISearchMediator _mediator, SharedData _sharedData, boolean _useMateDistancePrunning) {
		tpool = _tpool;
		searchers = _searchers;
		distribution = _distribution;
		bitboard = _bitboard;
		mediator = _mediator;
		sharedData = _sharedData;
		useMateDistancePrunning = _useMateDistancePrunning;
	}
	
	
	public void run() {
		
		
		ISearch searcher = null;
		boolean unlock = false;
		
		try {
			
			distribution.writeLock();
			unlock = true;
			
			
			if (mediator.getStopper().isStopped()) return;
			
			//System.out.println(sharedData);
			
			ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
			mediator.registerInfoObject(info);
			
			//System.out.println("working");
			
			//System.out.println("run: in " + Thread.currentThread());
			
			//System.out.println("run: not stopped");
			
			//mediator.dump(Thread.currentThread().getName() + "B	" + distribution.toString());
			
			int maxdepth = distribution.getCurrentDepth();
			if (maxdepth > ISearch.MAX_DEPTH) {
				//distribution.writeUnlock();
				//Unlocked in the finally block
				return;
			}
			
			//System.out.println("win");
			if (maxdepth > distribution.getMaxIterations()) {
				//distribution.writeUnlock();
				//Unlocked in the finally block
				//System.out.println("return");
				return;
			}
			
			int beta = distribution.nextBeta();
			
			info.setDepth(maxdepth);
			info.setSelDepth(maxdepth);
			
			Runnable task = new NullwinSearchTask(tpool, searchers, distribution, bitboard, mediator, sharedData, useMateDistancePrunning);
			
			//IBitBoard new_bitboard = new Board(bitboard.toEPD());
			//tpool.execute(new WorkPortion(tpool, searchers, distribution, new_bitboard, mediator, sharedData));
			
			//System.out.println("run: forked");
			tpool.execute(task);
			
			
			//searchEnv = new SearchEnv(bitboard, sharedData);
			//Search search = new Search(searchEnv);
			
			searcher = searchers.getSearcher(bitboard, sharedData);
			
			int[] prevPV = null;
			PVHistoryEntry entry = searcher.getEnv().getPVs().getPV(bitboard.getHashKey());
			if (entry != null) {
				prevPV = searcher.getEnv().getPVs().getPV(searcher.getEnv().getBitboard().getHashKey()).getPv();
			}
			
			RootSearchMTDImpl rootWin = new RootSearchMTDImpl(bitboard.getColourToMove(), distribution.getBetasGen());
			
			if (DEBUG) mediator.dump(Thread.currentThread().getName() + ":	start search with depth=" + maxdepth + ", beta=" + beta +" and distribution is " + distribution.toString());
			
			distribution.writeUnlock();
			unlock = false;
			
			
			//((SearchMTD_PV)searcher).analyze(maxdepth);
			
			//((SearchMTD_PV)searcher).setTempoless(0);
			//((SearchMTD_PV)searcher).pv_search(mediator, mediator.getStopper(), rootWin, info,
				//	ISearch.PLY * maxdepth, ISearch.PLY * maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0, bitboard.getColourToMove());
			
			//searcher.getEnv().getTPT().obsoleteAll();
			
			//((SearchMTD_PV)searcher).setTempoless(SearchMTD_PV.MAX_tempoless);
			
			/*int eval = searcher.pv_search(mediator, rootWin, info,
					ISearch.PLY * maxdepth, ISearch.PLY * maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0,
					bitboard.getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);*/
			int eval = searcher.nullwin_search(mediator, info, ISearch.PLY * (maxdepth - 0), ISearch.PLY * (maxdepth - 0),
					0, beta, false, 0, 0, prevPV, searcher.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
			if (eval >= beta) {
				eval = searcher.pv_search(mediator, rootWin, info, ISearch.PLY * maxdepth, ISearch.PLY * maxdepth,
					0, beta - 1, beta, 0, 0, prevPV, false, 0, searcher.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
			}
			
			
			distribution.writeLock();
			unlock = true;
			
			//distribution.addNodes(info.getSearchedNodes());
			
			if (maxdepth != info.getDepth()) {
				distribution.writeUnlock();
				throw new IllegalStateException("maxdepth=" + maxdepth + " info.getDepth()=" + info.getDepth());
			}
			
			
			/**
			 * Critical section for distribution, be careful with the usage of other locking methods inside
			 */
			if (maxdepth == distribution.getCurrentDepth()) {
				if (eval >= beta) {
					//eval is lower bound
					
					info.setPV(PVNode.convertPV(PVNode.extractPV(searcher.getPvman().load(0))));
					if (info.getPV().length > 0) {
						info.setBestMove(info.getPV()[0]);
					}
					info.setEval(eval);
					
					if (DEBUG) mediator.dump(Thread.currentThread().getName() + ":	stop search (increaseLowerBound) with eval " + eval);
					
					distribution.increaseLowerBound(eval, info, bitboard);
					
				} else {
					//eval is upper bound
					//eval < beta <=> eval <= beta - 1 <=> eval <= alpha
					if (DEBUG) mediator.dump(Thread.currentThread().getName() + ":	stop search (decreaseUpperBound) with eval " + eval);
					
					distribution.decreaseUpperBound(eval, bitboard);
				}
			} else if (maxdepth > distribution.getCurrentDepth()) {
				throw new IllegalStateException("maxdepth=" + maxdepth + " distribution.getMaxdepth()=" + distribution.getCurrentDepth());
			} else {
				if (DEBUG) mediator.dump(Thread.currentThread().getName() + ":	stop search -> depth is less");
				//System.out.println("PINKO");
				//Do nothing
			}
			
		} catch(Throwable t) {
			if (t instanceof SearchInterruptedException) {
				//distribution.addNodes(info.getSearchedNodes());	
			} else {
				mediator.dump(t);
			}
		} finally {
			if (searcher != null) searchers.releaseSearcher(searcher);
			if (unlock) distribution.writeUnlock();
		}
	}
	
	private static final class RootSearchMTDImpl implements IRootWindow {

		private int colour;
		private BetaGenerator win;
		
		RootSearchMTDImpl(int _colour, BetaGenerator _win) {
			colour = _colour;
			win = _win;
		}
		
		public boolean isInside(int eval, int cur_colour) {
			
			int lower = win.getLowerBound();
			int upper = win.getUpperBound();
			
			if (colour != cur_colour) {
				int lower_tmp = lower;
				int upper_tmp = upper;
				
				lower = -upper_tmp;
				upper = -lower_tmp;
			}
			
			return eval > lower && eval < upper;
		}
		
	}
}
