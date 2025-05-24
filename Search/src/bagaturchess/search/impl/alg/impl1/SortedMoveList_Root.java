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
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.search.impl.env.SearchEnv;


public class SortedMoveList_Root implements IMoveList {
	
	private SearchEnv env;
	
	private long[] moves;
	private int count;
	private int cur = 0;
	
	private int tt_move;
	
	
	public SortedMoveList_Root(int max, SearchEnv _env) {
		moves = new long[max];
		env = _env;
	}
	
	public void setTTMove(int _tt_move) {
		
		tt_move = _tt_move;
	}
	
	
	public void reserved_clear() {
		count = 0;
	}
	
	public final void reserved_add(int move) {
		
		long move_val = addOrderingValue(move, getOrderingValue(move));
		
        int insert_index = findInsertIndex(move_val);

        // Shift elements to the right
        for (int i = count; i > insert_index; i--) {
        	moves[i] = moves[i - 1];
        }

        moves[insert_index] = move_val;
        count++;
	}

	private long getOrderingValue(int move) {
		
		//TODO: ply
		int ply = 0;
		
		int killer1Move = env.getHistory_All().getKiller1(env.getBitboard().getColourToMove(), ply);
		int killer2Move = env.getHistory_All().getKiller2(env.getBitboard().getColourToMove(), ply);
		int counterMove1 = env.getHistory_All().getCounter1(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		int counterMove2 = env.getHistory_All().getCounter2(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		
		
		long ordval = 100000 * 100;
		
		if (tt_move == move) {
			
			ordval += 20000 * 100;
		
		}
		
		if (killer1Move == move) {
			
			ordval += 5000 * 100;
			
		}
		
		if (killer2Move == move) {
			
			ordval += 4000 * 100;
			
		}
		
		if (counterMove1 == move) {
			
			ordval += 3000 * 100;
		}
		
		if (counterMove2 == move) {
			
			ordval += 2000 * 100;
			
		}
		
		if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			
			ordval += env.getHistory_All().getScores(env.getBitboard().getColourToMove(), move);
			
		} else {
			
			if (env.getBitboard().getSEEScore(move) >= 0) {
			
				ordval += 7000 * 100 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
				
			} else {
				
				ordval += -5000 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
			}
		}
		
		
		return ordval;
	}
	
    private int findInsertIndex(long value) {
        int low = 0, high = count;

        while (low < high) {
            int mid = (low + high) >>> 1;
            if (moves[mid] > value) {
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
