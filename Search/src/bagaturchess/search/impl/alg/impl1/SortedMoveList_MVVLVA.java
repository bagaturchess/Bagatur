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
package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl2.MoveUtil;
import bagaturchess.search.impl.env.SearchEnv;


public class SortedMoveList_MVVLVA implements IMoveList {
	
	private SearchEnv env;
	
	private long[] moves;
	private int count;
	private int cur = 0;
	
	
	public SortedMoveList_MVVLVA(int max, SearchEnv _env) {
		moves = new long[max];
		env = _env;
	}
	
	public void reserved_clear() {
		count = 0;
	}
	
	public final void reserved_add(int move) {
		
		int score = getOrderingValue(move);
		
		long move_val = addOrderingValue(move, score);
		
        int insert_index = findInsertIndex(move_val);

        // Shift elements to the right
        for (int i = count; i > insert_index; i--) {
        	moves[i] = moves[i - 1];
        }

        moves[insert_index] = move_val;
        count++;
	}

	private int getOrderingValue(int move) {
		
		//getAttackedPieceIndex and getSourcePieceIndex returns value in [1, 6]
		int score = (6 * MoveUtil.getAttackedPieceIndex(move) - 1 * MoveUtil.getSourcePieceIndex(move));
		
		if (MoveUtil.isPromotion(move)) {
			
			//MoveUtil.getMoveType(move) returns value in [2, 5] when the move is promotion
			score += 1 * MoveUtil.getMoveType(move);
			
		}
		
		score = 100 * score;
		
		return score;
	}
	
	private int findInsertIndex(long value) {
	    int low = 0, high = count;
	    long orderValue = value >>> 32;

	    while (low < high) {
	        int mid = (low + high) >>> 1;
	        long midOrderValue = moves[mid] >>> 32;

	        if (midOrderValue > orderValue) {
	            low = mid + 1;
	        } else {
	            high = mid;
	        }
	    }

	    return low;
	}
	
	public final void reserved_removeLast() {
		count--;
	}
	
	public final int reserved_getCurrentSize() {
		return count;
	}
	
	public final int[] reserved_getMovesBuffer() {

		throw new UnsupportedOperationException();
	}

	public void clear() {
		reserved_clear();
		cur = 0;
	}

	public int next() {
		if (cur < count) {
			return (int) moves[cur++];
		} else {
			return 0;
		}
	}

	public int size() {
		return count;
	}
	
	private static long addOrderingValue(int move, long ord_val) {
		
		return (ord_val << 32) | move;
	}
}
