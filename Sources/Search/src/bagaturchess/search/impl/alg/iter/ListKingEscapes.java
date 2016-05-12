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
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.HistoryTable;
import bagaturchess.search.impl.utils.Sorting;



public class ListKingEscapes implements ISearchMoveList {

	private int ORD_VAL_TPT_MOVE;
	private int ORD_VAL_WIN_CAP;
	private int ORD_VAL_EQ_CAP;
	private int ORD_VAL_COUNTER;
	private int ORD_VAL_PREV_BEST_MOVE;
	private int ORD_VAL_LOSE_CAP;
	
	private static final int ORD_VAL_SHIFT           =  HistoryTable.MAX_SCORES + 200;
	
	private long[] escapes; 
	private int escapes_size; 
	
	private int cur;
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	
	private int tptMove = 0;
	private int prevBestMove = 0;
	
	private SearchEnv env;
	
	public ListKingEscapes(SearchEnv _env) { 
		env = _env;
		escapes = new long[62];
		
		ORD_VAL_TPT_MOVE        = env.getSearchConfig().getOrderingWeight_TPT_MOVE();
		ORD_VAL_WIN_CAP         = env.getSearchConfig().getOrderingWeight_WIN_CAP();
		ORD_VAL_EQ_CAP          = env.getSearchConfig().getOrderingWeight_EQ_CAP();
		ORD_VAL_COUNTER         = env.getSearchConfig().getOrderingWeight_COUNTER();
		ORD_VAL_PREV_BEST_MOVE  = env.getSearchConfig().getOrderingWeight_PREV_BEST_MOVE();
		ORD_VAL_LOSE_CAP        = env.getSearchConfig().getOrderingWeight_LOSE_CAP();
	}
	
	public void clear() {
		escapes_size = 0;
		cur = 0;
		generated = false;
		tptTried = false;
		tptPlied = false;
		tptMove = 0;
		prevBestMove = 0;
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
			if (!env.getBitboard().isInCheck()) {
				throw new IllegalStateException();
			}
			env.getBitboard().genKingEscapes(this);
			generated = true;							
		}
		
		if (cur < escapes_size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(escapes, 1, escapes_size);
				if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(1, escapes_size, escapes);
			}
			return (int) escapes[cur++];
		} else {
			return 0;
		}
	}
	
	public int size() {
		return escapes_size;
	}
	
	public void reserved_add(int move) {
		
		env.getHistory_check().countMove(move);
		
		int ordval = 0;
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
			ordval += ORD_VAL_SHIFT * ORD_VAL_TPT_MOVE;
		}
		
		if (move == prevBestMove) {
			ordval += ORD_VAL_SHIFT * ORD_VAL_PREV_BEST_MOVE;
		}
		
		if (MoveInt.isCaptureOrPromotion(move)) {
			
			int see = env.getBitboard().getSee().evalExchange(move);
			
			if (see > 0) {
				ordval += ORD_VAL_SHIFT * ORD_VAL_WIN_CAP + see;
			} else if (see == 0) {
				ordval += ORD_VAL_SHIFT * ORD_VAL_EQ_CAP;// + 50;
			} else {
				ordval += ORD_VAL_SHIFT * ORD_VAL_LOSE_CAP + see;
			}
		}
		
		if (env.getHistory_all().getCounterMove(env.getBitboard().getLastMove()) == move) {
			ordval += ORD_VAL_SHIFT * ORD_VAL_COUNTER;
		}
		
		ordval += (env.getHistory_check().getScores(move)) / 2;
		ordval += (env.getHistory_all().getScores(move) / 4);
		
		
		long move_ord = MoveInt.addOrderingValue(move, ordval);
		
		add(move_ord);
	}
	
	private void add(long move) {	
		if (escapes_size == 0) {
			escapes[escapes_size++] = move;
		} else {
			if (move > escapes[0]) {
				escapes[escapes_size++] = escapes[0];
				escapes[0] = move;
			} else {
				escapes[escapes_size++] = move;
			}
		}
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

	public void setPrevpvMove(int move) {
		throw new UnsupportedOperationException();
	}
	
	public void updateStatistics(int bestmove) {
	}

	public void countStatistics(int move) {
		// TODO Auto-generated method stub
		
	}

	public void newSearch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMateMove(int mateMove) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void reset() {
		cur = 0;
	}
}
