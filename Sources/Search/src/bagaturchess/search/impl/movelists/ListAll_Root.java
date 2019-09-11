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
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.Sorting;


public class ListAll_Root implements ISearchMoveList {
	
	
	private long[] moves; 
	private int size;
	private int cur;
	
	private SearchEnv env;
	
	
	public ListAll_Root(SearchEnv _env, OrderingStatistics _orderingStatistics) { 
		env = _env;
		moves = new long[256];
	}
	
	
	public void clear() {
		
		cur = 0;
		size = 0;
		
		genMoves();
		
		for (int i=0; i<size; i++) {
			moves[i] = MoveInt.addOrderingValue((int)moves[i], genOrdVal((int) moves[i]));
		}
		
		if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, cur, size);
		if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(cur, size, moves);
	}
	
	
	public int next() {
		
		if (cur < size) {
			
			int move = (int) moves[cur++];
			
			//System.out.println(move);
			
			return move;
			
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
			if (entry != null && entry.getWeight() >= OpeningBook.OPENNING_BOOK_MIN_MOVES) {
				
				int[] ob_moves = entry.getMoves();
				int[] ob_counts = entry.getCounts();
				
				for (int i=0; i<ob_moves.length; i++) {
					
					//if (env.getSearchConfig().getOpenningBook_Mode() == ISearchConfig_AB.OPENNING_BOOK_MODE_POWER2) {
						//Most played first strategy - use ord val
						long move_ord = MoveInt.addOrderingValue(ob_moves[i], ob_counts == null ? 1 : ob_counts[i]);
						add(move_ord);
						
					//} else {
					//	//Random move - in addition randomize naturally without using ordval scores
					//	add(ob_moves[i]);
					//}
				}
				
				
				gen = false;
			}
		}
		
		if (gen) {
			env.getBitboard().genAllMoves(this);
		}
	}
	
	
	public int size() {
		return size;
	}
	
	
	public void reserved_add(int move) {
		add(move);
	}
	
	
	private long genOrdVal(int move) {
		
		int ordval = 10000;
		
		/*ordval += (int) (10000d * env.getHistory_All().getScores(move));
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			ordval += 10000;
		}
		
		int see = env.getBitboard().getSEEScore(move);
		if (see > 0) {
			ordval += 10000 + see;
		} else if (see == 0) {
			//ordval += 10000;
		} else {
			ordval += see / 100;
		}*/
		
		env.getBitboard().makeMoveForward(move);
		TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKey());
		env.getBitboard().makeMoveBackward(move);
		
		if (entry != null) {
			
			//ordval = 10000 * (ISearch.MAX_DEPTH - entry.getDepth());
			
			if (entry.getBestMove_lower() != 0) {
				//ordval += 10000;
				ordval += -entry.getLowerBound();	
			}
		}
		
		//System.out.println(ordval);
		
		return ordval;
	}
	
	
	public void countTotal(int move) {
		
	}
	
	
	public void countSuccess(int bestmove) {
		
	}
	
	private void add(long move) {	
		if (size == 0) {
			moves[0] = move;
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
	public void newSearch() {
	}

	@Override
	public void reset() {
		clear();
	}
}
