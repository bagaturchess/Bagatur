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
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.tpt.TTEntry_BaseImpl;


public class MoveGenFragmentImpl_TT extends MoveGenFragmentImpl_Base {
	
	private ITTable tt;
	private ITTEntry tt_entry = new TTEntry_BaseImpl();
	private int ttMove;
	
	
	public MoveGenFragmentImpl_TT(ChessBoard _cb, MoveGenerator _gen, ITTable _tt) {
		super(_cb, _gen);
		tt = _tt;
	}
	
	
	@Override
	public void genMoves(int parentMove, int ply, int depth, boolean dummy) {
		ttMove = 0;
		tt.get(cb.zobristKey, tt_entry);
		if (!tt_entry.isEmpty()) {
			if (cb.isValidMove(tt_entry.getBestMove())) {
				ttMove = tt_entry.getBestMove();
				if (!dummy) gen.addMove(ttMove);
				count_move_total(1, depth);
			}
		}
	}
	
	
	@Override
	public void updateWithBestMove(int bestMove, int depth) {
		if (bestMove == ttMove) {
			count_move_cutoff(depth);
		}
	}
	
	
	@Override
	public int getSearchedMove() {
		return ttMove;
	}
	
	
	@Override
	public double getRate() {
		return 1;
	}
}
