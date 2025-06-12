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


import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.IHistoryTable;


public class SortedMoveList_Root extends SortedMoveList_BaseImpl {
	
	
	private int tt_move;
	
	
	public SortedMoveList_Root(int max, SearchEnv _env, boolean onTheFlySorting) {
		super(max, _env, onTheFlySorting);
	}
	
	
	public void setTTMove(int _tt_move) {
		
		tt_move = _tt_move;
	}
	

	@Override
	protected int getOrderingValue(int move) {
		
		//For root node only -> ply = 0
		int ply = 0;
		
		int killer1Move = env.getKillers().getKiller1(env.getBitboard().getColourToMove(), ply);
		int killer2Move = env.getKillers().getKiller2(env.getBitboard().getColourToMove(), ply);		
		
		int ordval = 100000 * 100;
		
		if (tt_move == move) {
			
			ordval += 20000 * 100;
		
		}
		
		if (killer1Move == move) {
			
			ordval += 5000 * 100;
			
		}
		
		if (killer2Move == move) {
			
			ordval += 4000 * 100;
			
		}
		
		if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			
			ordval += env.getHistory().getScores(-1, move);
			ordval += env.getContinuationHistory().getScores(env.getBitboard().getLastMove(), move);
			
		} else {
			
			if (env.getBitboard().getSEEScore(move) >= 0) {
			
				ordval += 7000 * 100 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
				
			} else {
				
				ordval += -5000 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
			}
		}
		
		
		if (ordval < 0) {
			
		    throw new ArithmeticException("Ordering value overflowed!");
		}
		
		
		return ordval;
	}
}
