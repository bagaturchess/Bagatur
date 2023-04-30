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
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.SEEUtil;


public class MoveGenFragmentImpl_Attacks_GoodAndEqual extends MoveGenFragmentImpl_Base {
	
	
	public MoveGenFragmentImpl_Attacks_GoodAndEqual(ChessBoard _cb, MoveGenerator _gen) {
		super(_cb, _gen);
	}
	
	
	@Override
	public void genMoves(int parentMove, int ply, int depth, boolean dummy) {
		if (!dummy) {
			gen.generateAttacks(cb);
			gen.setMVVLVAScores(cb);
			gen.sort();
			
			count_move_total(gen.getCountGoodAndEqualAttacks(cb), depth);
		}
	}
	
	
	@Override
	public void updateWithBestMove(int bestMove, int depth) {
		if (!MoveUtil.isQuiet(bestMove) && SEEUtil.getSeeCaptureScore(cb, bestMove) >= 0) {
			count_move_cutoff(depth);
		}
	}
	
	
	@Override
	public boolean isLegal(int move) {
		return super.isLegal(move) && SEEUtil.getSeeCaptureScore(cb, move) >= 0;
	}
}
