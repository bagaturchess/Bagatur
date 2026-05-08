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


import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public abstract class SortedMoveList_BaseImpl implements IMoveList {
	
	
	protected SearchEnv env;
	
	boolean onTheFlySorting;
	
	private long[] moves;
	private int count;
	private int cur = 0;
	private int lastScore;
	
	
	public SortedMoveList_BaseImpl(int max, SearchEnv _env, boolean _onTheFlySorting) {
		
		moves = new long[max];
		env = _env;
		onTheFlySorting = _onTheFlySorting;
	}
	
	
	public void reserved_clear() {
		
		count = 0;
	}
	
	
	public final void reserved_add(int move) {
		
		long move_val = addOrderingValue(move, getOrderingValue(move));
		
		if (onTheFlySorting) {
			
	        // Just encode the score but do NOT sort yet
	        moves[count++] = move_val;
			
		} else {
			
	        int insert_index = findInsertIndex(move_val);
	
	        // Shift elements to the right
	        System.arraycopy(moves, insert_index, moves, insert_index + 1, count - insert_index);
	
	        moves[insert_index] = move_val;
	        count++;
		}
	}

	
	protected abstract int getOrderingValue(int move);
	
	
	private int findInsertIndex(long value) {
		
	    int low = 0;
	    int high = count;
	    int orderValue = getOrderingValueFromEncodedMove(value);

	    while (low < high) {
	    	
	        int mid = (low + high) >>> 1;
	        int midOrderValue = getOrderingValueFromEncodedMove(moves[mid]);

	        /*
	         * Sort descending by signed ordering value.
	         *
	         * Important:
	         * The ordering value may be negative. Do NOT use >>> 32 here,
	         * because that interprets negative int scores as huge unsigned
	         * positive values and moves them before good positive scores.
	         */
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
		
        if (cur >= count) {
        	
        	return 0;
        }
        
		if (onTheFlySorting) {

	        // Selection of best remaining move by signed ordering value
	        int bestIndex = cur;
	        long bestValue = moves[cur];
	        int bestOrderValue = getOrderingValueFromEncodedMove(bestValue);

	        for (int i = cur + 1; i < count; i++) {
	        	
	        	int orderValue = getOrderingValueFromEncodedMove(moves[i]);
	        	
	            if (orderValue > bestOrderValue) {
	            	
	                bestValue = moves[i];
	                bestOrderValue = orderValue;
	                bestIndex = i;
	            }
	        }

	        // Swap best with cur
	        if (bestIndex != cur) {
	        	
	            long tmp = moves[cur];
	            moves[cur] = moves[bestIndex];
	            moves[bestIndex] = tmp;
	        }
		}
		
        // Return current move
		long v = moves[cur++];
        lastScore = getOrderingValueFromEncodedMove(v);
        
        return (int) v;
	}

	
	public int getScore() {
		
		return lastScore;
	}
	
	
	public int size() {
		
		return count;
	}
	
	
    private static long addOrderingValue(int move, int ord_val) {
    	
        return (((long) ord_val) << 32) | (move & 0xFFFFFFFFL);
    }
    
    
    private static int getOrderingValueFromEncodedMove(long encodedMove) {
    	
    	return (int) (encodedMove >> 32);
    }
}