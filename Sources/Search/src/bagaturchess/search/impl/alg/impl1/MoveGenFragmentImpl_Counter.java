/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;


public class MoveGenFragmentImpl_Counter extends MoveGenFragmentImpl_Base {
	
	
	private int counterMove;
	
	
	public MoveGenFragmentImpl_Counter(ChessBoard _cb, MoveGenerator _gen) {
		super(_cb, _gen);
	}
	
	
	@Override
	public void genMoves(int parentMove, int ply, int depth, boolean dummy) {
		counterMove = gen.getCounter(cb.colorToMove, parentMove);
		if (counterMove != 0 && cb.isValidMove(counterMove)) {
			if (!dummy) gen.addMove(counterMove);
			count_move_total(1, depth);
		} else {
			counterMove= 0;
		}
	}
	
	
	@Override
	public void updateWithBestMove(int bestMove, int depth) {
		if (bestMove == counterMove) {
			count_move_cutoff(depth);
		}
	}
	
	
	@Override
	public int getSearchedMove() {
		return counterMove;
	}
}
