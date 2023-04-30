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
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public class ListAll_Root implements ISearchMoveList {
	
	
	private long[] moves; 
	private int size;
	private int cur;
	
	private int prevBestMove;
	
	private boolean generated;
	
	private SearchEnv env;
	
	
	public ListAll_Root(SearchEnv _env, OrderingStatistics _orderingStatistics) { 
		env = _env;
		moves = new long[256];
	}
	
	
	@Override
	public void newSearch() {
	}
	
	
	public void clear() {
		
		cur = 0;
		size = 0;
		
		prevBestMove = 0;
		
		generated = false;
	}
	
	
	public int next() {
		
		if (!generated) {
			
			genMoves();
			
			for (int i=0; i<size; i++) {
				moves[i] = MoveInt.addOrderingValue((int)moves[i], genOrdVal((int) moves[i]));
			}
			
			if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, cur, size);
			if (env.getSearchConfig().sortMoveLists()) Utils.bubbleSort(cur, size, moves);
		}
		
		if (cur < size) {
			
			long move = moves[cur++];
			
			//System.out.println(MoveInt.getOrderingValue(move));
			
			return (int) move;
			
		} else {
			
			return 0;
		}
	}
	
	
	public void genMoves() {
		
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		boolean gen = true;
		
		if (env.getOpeningBook() != null) {
			
			IOpeningEntry entry = env.getOpeningBook().getEntry(env.getBitboard().getHashKey(), env.getBitboard().getColourToMove());
			if (entry != null && entry.getWeight() >= OpeningBook.OPENING_BOOK_MIN_MOVES) {
				
				int[] ob_moves = entry.getMoves();
				int[] ob_counts = entry.getCounts();
				
				for (int i=0; i<ob_moves.length; i++) {					
					reserved_add(ob_moves[i]);
				}
				
				
				gen = false;
			}
		}
		
		if (gen) {
			env.getBitboard().genAllMoves(this);
		}
		
		generated = true;
	}
	
	
	public int size() {
		return size;
	}
	
	
	public void reserved_add(int move) {
		moves[size++] = move;
	}
	
	
	private long genOrdVal(int move) {
		
		int ordval = 10000;
		
		if (env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			int see = env.getBitboard().getSEEScore(move);
			if (see > 0) {
				ordval += env.getOrderingStatistics().getOrdVal_WINCAP() + see;
			} else if (see == 0) {
				ordval += env.getOrderingStatistics().getOrdVal_EQCAP();
			} else {
				ordval += env.getOrderingStatistics().getOrdVal_LOSECAP() + see / 100;
			}
		}
		
		if (move == prevBestMove) {
			ordval += env.getOrderingStatistics().getOrdVal_PREVBEST();
		}
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			ordval += env.getOrderingStatistics().getOrdVal_COUNTER();
		}
		
		ordval += env.getHistory_All().getScores(move) * env.getOrderingStatistics().getOrdVal_HISTORY();
		
		/*TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKeyAfterMove(move));
		if (entry != null) {
			if (entry.getBestMove_lower() != 0) {
				ordval += OrderingStatistics.MAX_VAL;
				ordval += -entry.getLowerBound();
			}
		}*/
		
		//System.out.println(ordval);
		
		return ordval;
	}
	
	
	public void setPrevBestMove(int _prevBestMove) {
		prevBestMove = _prevBestMove;
	}
	
	
	public void countTotal(int move) {
		
	}
	
	
	public void countSuccess(int bestmove) {
		
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
	
	public void setMateMove(int mateMove) {
		throw new IllegalStateException();
	}
	
	public void setTptMove(int tptMove) {
	}
	
	public void setPrevpvMove(int prevpvMove) {
		throw new IllegalStateException();
	}

	@Override
	public void reset() {
		throw new IllegalStateException();
	}
}
