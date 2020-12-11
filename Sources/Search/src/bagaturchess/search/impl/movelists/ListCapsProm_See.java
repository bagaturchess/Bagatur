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
package bagaturchess.search.impl.movelists;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public class ListCapsProm_See implements ISearchMoveList {

	private int ORD_VAL_TPT_MOVE;
	private int ORD_VAL_WIN_CAP;
	private int ORD_VAL_EQ_CAP;
	private int ORD_VAL_LOSE_CAP;
	
	private long[] caps; 
	private int caps_size; 

	private int cur;
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	
	private int tptMove = 0;
	
	private SearchEnv env;
	
	public ListCapsProm_See(SearchEnv _env) { 
		env = _env;
		caps = new long[62];
		
		ORD_VAL_TPT_MOVE        = env.getSearchConfig().getOrderingWeight_TPT_MOVE();
		ORD_VAL_WIN_CAP         = env.getSearchConfig().getOrderingWeight_WIN_CAP();
		ORD_VAL_EQ_CAP          = env.getSearchConfig().getOrderingWeight_EQ_CAP();
		ORD_VAL_LOSE_CAP        = env.getSearchConfig().getOrderingWeight_LOSE_CAP();
	}
	
	public void clear() {
		caps_size = 0;
		cur = 0;
		generated = false;
		tptTried = false;
		tptPlied = false;
		tptMove = 0;
	}
	
	private boolean isOk(int move) {
		return !MoveInt.isCastling(move) && !MoveInt.isEnpassant(move);
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) && env.getBitboard().isPossible(tptMove)
					&& MoveInt.isCaptureOrPromotion(tptMove)) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		if (!generated) {
			if (env.getBitboard().isInCheck()) {
				throw new IllegalStateException();
			}
			env.getBitboard().genCapturePromotionMoves(this);
			generated = true;							
		}
		
		if (cur < caps_size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(caps, 1, caps_size);
				if (env.getSearchConfig().sortMoveLists()) Utils.bubbleSort(1, caps_size, caps);
			}
			return (int) caps[cur++];
		} else {
			return 0;
		}
	}

	public int size() {
		return caps_size;
	}
	
	public void reserved_add(int move) {
		if (MoveInt.isCaptureOrPromotion(move)) {
			
			if (!env.getSearchConfig().sortMoveLists()) {
				add(move);
			}
			
			long ordval = 0;
			
			if (move == tptMove) {
				if (tptPlied) {
					return;
				}
				ordval += ORD_VAL_TPT_MOVE;
			}
			
			if (MoveInt.isCaptureOrPromotion(move)) {
				
				int see = env.getBitboard().getSee().evalExchange(move);
				
				if (see > 0) {
					ordval += ORD_VAL_WIN_CAP + see;
				} else if (see == 0) {
					ordval += ORD_VAL_EQ_CAP;
				} else {
					ordval += ORD_VAL_LOSE_CAP + see;
				}
			}
			
			ordval += 100 * env.getHistory_All().getScores(move);
			
			long move_ord = (ordval << 32) | move;
			
			add(move_ord);
			
		} else {
			throw new IllegalStateException();
		}
	}

	private void add(long move) {
		
		if (caps_size == 0) {
			caps[caps_size++] = move;
		} else {
			if (move > caps[0]) {
				caps[caps_size++] = caps[0];
				caps[0] = move;
			} else {
				caps[caps_size++] = move;
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
	
	public void setTptMove(int tptMove) {
		this.tptMove = tptMove;
	}

	public void setPrevBestMove(int move) {
		throw new UnsupportedOperationException();
	}

	public void setPrevpvMove(int move) {
		throw new UnsupportedOperationException();
	}
	
	public void countSuccess(int bestmove) {
		
	}

	public void countTotal(int move) {
		// TODO Auto-generated method stub
		
	}

	public void newSearch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMateMove(int mateMove) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		cur = 0;
	}
}
