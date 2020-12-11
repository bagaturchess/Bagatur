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
package bagaturchess.selfplay;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.IEvaluator;


public class GamesPlayer {
	
	
	private IBitBoard bitboard;
	private IEvaluator evaluator;
	
	private IMoveList movesBuffer = new BaseMoveList(250);
	private int[] seeBuffer = new int[250];
	private List<Integer> movesList = new ArrayList<Integer>();
	
	
	public GamesPlayer(IBitBoard _bitboard, IEvaluator _evaluator) {
		bitboard = _bitboard;
		evaluator = _evaluator;
	}
	
	public void playGames() {
		while (true) {
			playGame();
		}
	}
	
	
	private void playGame() {
		
		movesList.clear();
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			int best_move = getBestMove();
			bitboard.makeMoveForward(best_move);
			movesList.add(best_move);
		}
		
		System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		//Revert board to the initial position
		for (int i = movesList.size() - 1; i >= 0; i--) {
			bitboard.makeMoveBackward(movesList.get(i));
		}
	}
	
	
	private int getBestMove() {
		
		movesBuffer.clear();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(movesBuffer);
		} else {
			bitboard.genAllMoves(movesBuffer);
		}
		
		//Randomize moves
		int moves_size = movesBuffer.reserved_getCurrentSize();
		int[] moves = movesBuffer.reserved_getMovesBuffer();
		Utils.randomize(moves, 0, moves_size);
		
		//Sort by SEE
		for (int i = 0; i<moves_size; i++) {
			int see = bitboard.getSee().evalExchange(moves[i]);
		}
		
		//Iterate moves and find best one
		int best_move = 0;
		int best_eval = Integer.MIN_VALUE;
		
		int cur_move = 0;
		while ((cur_move = movesBuffer.next()) != 0) {
			
			bitboard.makeMoveForward(cur_move);
			
			int cur_eval = getSpecialValue();
			if (cur_eval == -1) {
				cur_eval = (int) -evaluator.fullEval(0, 0, 0, 0);
			}
			
			bitboard.makeMoveBackward(cur_move);
			
			if (cur_eval > best_eval) {
				best_eval = cur_eval;
				best_move = cur_move;
				//System.out.println(best_eval);
			}
		}
		
		if (best_move == 0) {
			throw new IllegalStateException("No moves");
		}
		
		//System.out.println("end");
				
		return best_move;
	}
	
	
	private int getSpecialValue() {
		
		if (bitboard.getStateRepetition() >= 2) {
			return 0;
		}
		
		if (!bitboard.hasSufficientMaterial()) {
			return 0;
		}
		
		if (bitboard.isDraw50movesRule()) {
			return 0;
		}
		
		if (bitboard.isInCheck()) {
			if (!bitboard.hasMoveInCheck()) {
				//Mate
				return 50000;
			}
		} else {
			if (!bitboard.hasMoveInNonCheck()) {
				//Stale Mate
				return 0;
			}
		}
		
		return -1;
	}
}
