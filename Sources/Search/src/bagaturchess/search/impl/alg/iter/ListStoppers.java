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
package bagaturchess.search.impl.alg.iter;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.HistoryTable;
import bagaturchess.search.impl.utils.Sorting;



public class ListStoppers implements IMoveList {
	
	private int ORD_VAL_TPT_MOVE        = 10000;
	private int ORD_VAL_WIN_CAP         =  7000;
	private int ORD_VAL_EQ_CAP          =  6000;
	private int ORD_VAL_PREV_BEST_MOVE  =  5000;
	private int ORD_VAL_COUNTER         =  4500;
	private int ORD_VAL_PASSER_PUSH 	 =  4000;
	private int ORD_VAL_KILLER          =  3000;
	private int ORD_VAL_PREVPV_MOVE     =  2000;
	private int ORD_VAL_CASTLING 	 	 =  1000;
	private int ORD_VAL_LOSE_CAP        =  -2000;
	
	private static final int HISTORY_SHIFT           =  HistoryTable.MAX_SCORES;
	
	private long[] moves; 
	private int size; 
	
	private int cur;
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	
	private int tptMove = 0;
	private int prevBestMove = 0;
	private int prevPvMove = 0;
	
	private long excluedToFieldsBitboard = 0;
	
	private boolean hasDominantMove;
	
	private SearchEnv env;
	
	
	public ListStoppers(SearchEnv _env) { 
		env = _env;
		moves = new long[256];
		
		ORD_VAL_TPT_MOVE        = env.getSearchConfig().getOrderingWeight_TPT_MOVE();
		ORD_VAL_WIN_CAP         = env.getSearchConfig().getOrderingWeight_WIN_CAP();
		ORD_VAL_EQ_CAP          = env.getSearchConfig().getOrderingWeight_EQ_CAP();
		ORD_VAL_COUNTER         = env.getSearchConfig().getOrderingWeight_COUNTER();
		ORD_VAL_PREV_BEST_MOVE  = env.getSearchConfig().getOrderingWeight_PREV_BEST_MOVE();
		ORD_VAL_CASTLING 	 	= env.getSearchConfig().getOrderingWeight_CASTLING();
		ORD_VAL_PASSER_PUSH 	= env.getSearchConfig().getOrderingWeight_PASSER_PUSH();
		ORD_VAL_KILLER          = env.getSearchConfig().getOrderingWeight_KILLER();
		ORD_VAL_PREVPV_MOVE     = env.getSearchConfig().getOrderingWeight_PREVPV_MOVE();
		ORD_VAL_LOSE_CAP        = env.getSearchConfig().getOrderingWeight_LOSE_CAP();
	}
	
	public void clear() {
		cur = 0;
		size = 0;
		
		generated = false;
		
		tptTried = false;
		tptPlied = false;
		
		tptMove = 0;
		prevBestMove = 0;
		prevPvMove = 0;
		hasDominantMove = false;
	}
	
	private boolean isOk(int move) {
		return !MoveInt.isCastling(move) && !MoveInt.isEnpassant(move);
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) && env.getBitboard().isPossible(tptMove)) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		if (!generated) {
			genMoves();							
		}
		
		if (cur < size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, 1, size);
				if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(1, size, moves);
			}
			int move = (int) moves[cur++];
			return move;
		} else {
			return 0;
		}
	}

	public void genMoves() {
		
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		if (excluedToFieldsBitboard == 0) {
			throw new IllegalStateException();
		}
		
		env.getBitboard().genAllMoves(this, excluedToFieldsBitboard);
		
		generated = true;
	}
	
	public int size() {
		return size;
	}
	
	public void reserved_add(int move) {
		
		env.getHistory_all().countMove(move);
		
		if (!env.getSearchConfig().sortMoveLists()) {
			add(move);
		}
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
		}
		
		long ordval = genOrdVal(move);
		
		long move_ord = MoveInt.addOrderingValue(move, ordval);
		
		add(move_ord);
	}

	private long genOrdVal(int move) {
		
		long ordval = 0;
		
		if (move == tptMove) {
			hasDominantMove = true;
			ordval += HISTORY_SHIFT * ORD_VAL_TPT_MOVE;
		}
		
		if (move == prevPvMove) {
			hasDominantMove = true;
			ordval += HISTORY_SHIFT * ORD_VAL_PREVPV_MOVE;
		}
		
		if (move == prevBestMove) {
			hasDominantMove = true;
			ordval += HISTORY_SHIFT * ORD_VAL_PREV_BEST_MOVE;
		}
		
		/*
		int[] killers = env.getHistory_all().getNonCaptureKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<killers.length; i++) {
			if (move == killers[i]) {
				hasDominantMove = true;
				ordval += HISTORY_SHIFT * ORD_VAL_KILLER;				
				break;
			}
		}
		*/
		
		if (env.getBitboard().isPasserPush(move)) {
			ordval += HISTORY_SHIFT * ORD_VAL_PASSER_PUSH;
		}
		
		if (MoveInt.isCastling(move)) {
			ordval += HISTORY_SHIFT * ORD_VAL_CASTLING;
		}
		
		if (MoveInt.isCaptureOrPromotion(move)) {
			
			int see = env.getBitboard().getSee().evalExchange(move);
			if (see > 0) {
				hasDominantMove = true;
			}
			
			//ordval += (see >= 0 ? 1 : -1) * (HISTORY_SHIFT * ORD_VAL_WIN_CAP + see);
			if (see > 0) {
				ordval += HISTORY_SHIFT * ORD_VAL_WIN_CAP + see;
			} else if (see == 0) {
				ordval += HISTORY_SHIFT * ORD_VAL_EQ_CAP;
			} else {
				ordval += HISTORY_SHIFT * ORD_VAL_LOSE_CAP + see;
			}
		}
		
		if (env.getHistory_all().getCounterMove(env.getBitboard().getLastMove()) == move) {
			ordval += HISTORY_SHIFT * ORD_VAL_COUNTER;
		}
		
		ordval += env.getHistory_all().getScores(move);
		
		//ordval += env.getBitboard().getBaseEvaluation().getPSTMoveScores(move);
		
		return ordval;
	}
	
	private void add(long move) {	
		if (size == 0) {
			moves[size] = move;
		} else {
			if (move > moves[0]) {
				moves[size] = moves[0];
				moves[0] = move;
			} else {
				moves[size] = move;
			}
		}
		size++;
	}
	
	/**
	 * Unsupported operations 
	 */
	
	public void reserved_clear() {
		throw new IllegalStateException();
	}
	
	public int reserved_getCurrentSize() {
		throw new IllegalStateException();
	}
	
	public int[] reserved_getMovesBuffer() {
		throw new IllegalStateException();
	}
	
	public void reserved_removeLast() {
		throw new IllegalStateException();
	}
	
	public void setPrevBestMove(int prevBestMove) {
		this.prevBestMove = prevBestMove;
	}
	
	public void setTptMove(int tptMove) {
		this.tptMove = tptMove;
	}

	public void setPrevpvMove(int prevpvMove) {
		this.prevPvMove = prevpvMove;
	}

	public boolean hasDominantMove() {
		return hasDominantMove;
	}
}
