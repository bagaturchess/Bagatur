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

package bagaturchess.engines.run;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.engines.bagatur.eval.BagaturEvaluator;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.uci.api.BestMoveSender;



public class MediatorDummper implements ISearchMediator {
	
	private boolean DUMP_EVAL = false;
	
	private ISearchStopper stopper;
	//private long nodes;
	private long startTime;
	private ISearchInfo lastinfo;
	private boolean dump;
	private final IBitBoard board;
	private final IEvaluator eval;
	
	
	public MediatorDummper(IBitBoard board, IEvaluator eval, long time, boolean _dump) {
		this.board = board;
		this.eval = eval;
		startTime = System.currentTimeMillis();
		stopper = new SearchStopperImpl(startTime + time);
		dump = _dump;
		
		
	}
	
	//public void addSearchedNodes(long _nodes) {
	//	nodes += _nodes;
	//}
	
	public void changedMajor(ISearchInfo info) {
		
		lastinfo = info;
		
		if (dump) System.out.println(
  			"D: " + info.getDepth() +
  			"	SD: " + info.getSelDepth() +
  			" Time: " + ((System.currentTimeMillis()-startTime)/(double)1000) + " s" +
  			//" Mate: " + info.isMateScore() +
  			"	Eval: " + (info.isMateScore() ? (info.getMateScore() + "M") : info.getEval() ) +
  			"	NPS: " + (int)(info.getSearchedNodes()/((System.currentTimeMillis()-startTime)/(double)1000)) +
  			//" Thread: " + Thread.currentThread().getName() +
  			"	PV: " + MoveInt.movesToString(info.getPV())
  		);
		
		int colour = board.getColourToMove();
		int sign = 1;
		int[] pv = info.getPV();
		int last_index = pv.length - 1;
		for (int i=0; i<pv.length; i++) {
			int move = pv[i];
			if (board.isPossible(move)) {
				board.makeMoveForward(move);
				sign *= -1;
			} else {
				last_index = i - 1;
				break;
			}
		}
		
		//int eval_int = (int) eval.fullEval(10, -1000000, 1000000, colour);
		if (DUMP_EVAL) System.out.println(board);
		//if (DUMP_EVAL) System.out.println("sign: " + sign + ", eval: " + (sign * eval_int));
		if (DUMP_EVAL) System.out.println(((BagaturEvaluator)eval).dump(colour));
		
		for (int i=last_index; i>=0; i--) {
			int move = pv[i];
			board.makeMoveBackward(move);
		}
	}
	
	public void dump(String msg) {
		System.out.println(msg);
	}
	
	public void dump(Throwable t) {
		t.printStackTrace();
	}
	
	public ISearchInfo getLastInfo() {
		return lastinfo;
	}
	
	public ISearchStopper getStopper() {
		return stopper;
	}
	
	private static class SearchStopperImpl implements ISearchStopper {

		private ISearchStopper internal;
		private long stopTime;
		private boolean stopped;
		
		SearchStopperImpl(long _stopTime) {
			stopTime = _stopTime;
		}
		
		public boolean isStopped() {
			if (stopped) return true;
			if (internal != null) return internal.isStopped();
			return false;
		}

		public void markStopped() {
			stopped = true;
			//throw new UnsupportedOperationException();
		}

		public void setSecondaryStopper(ISearchStopper secondaryStopper) {
			internal = secondaryStopper;
		}

		public void stopIfNecessary(int maxdepth, int colour, double alpha, double beta) throws SearchInterruptedException {
			if (System.currentTimeMillis() > stopTime) {
				stopped = true;
				throw new SearchInterruptedException();
			}
			if (internal != null) internal.stopIfNecessary(maxdepth, colour, alpha, beta);
		}
		
	}
	
	public BestMoveSender getBestMoveSender() {
		return null;
	}
	
	public void changedMinor(ISearchInfo info) {
	}
	
	public void addSearchedNodes(long searchedNodes) {
		//throw new IllegalStateException();
	}
	
	public void startIteration(int iteration) {
	}

	@Override
	public void send(String msg) {
		System.out.println(msg);
	}

	@Override
	public void registerInfoObject(ISearchInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTrustWindow_BestMove() {
		return 64;
	}

	@Override
	public int getTrustWindow_AlphaAspiration() {
		return 1024;
	}

	@Override
	public int getTrustWindow_MTD_Step() {
		return 4;
	}

	@Override
	public void setStopper(ISearchStopper _stopper) {
		stopper = _stopper;
	}
}
