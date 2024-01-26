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
package bagaturchess.ucitracker.run;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.nnue.NNUEProbeUtils;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;
import bagaturchess.ucitracker.impl.gamemodel.serialization.GameModelWriter;


public class GamesGenerator_NNUE {
	
	
	static {
		
		NNUEJNIBridge.loadLib();
		
		NNUEJNIBridge.init();
	}
	
	
	private static int MAX_EVAL_DIFF = 3000;
	private static int MAX_MOVES = 300;
	private static int BEST_MOVE_DIFF = 50;
	private static int MIN_PIECES = 6;
	
	
	private NNUEProbeUtils.Input input;
	
	
	public GamesGenerator_NNUE() {
		
		input = new NNUEProbeUtils.Input();
	}
	
	
	public static void main(String[] args) {
		
		GamesGenerator_NNUE control = new GamesGenerator_NNUE();
		
		try {

			control.execute("./NNUE.cg", 1000000, false);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private void execute(String toFileName, int gamesCount, boolean appendToFile) throws IOException {
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(toFileName, appendToFile), 10 * 1024 * 1024));
		
		for (int i=0; i<gamesCount; i++) {
			EvaluatedGame game = playGame();
			GameModelWriter.writeEvaluatedGame(game, dos);
			dos.flush();
			System.out.println("Game " + (i+1) + " saved in " + toFileName);
		}
		
		dos.close();
	}
	
	
	private EvaluatedGame playGame() throws IOException {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
		
		EvaluatedGame game = new EvaluatedGame(bitboard.toEPD());
		
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			Set<EvaluatedMove> movesEvals = evalVariations(bitboard);
			if (movesEvals.size() == 0) {
				break;
			}
			
			EvaluatedMove best = getBestVariation(bitboard.isInCheck(), movesEvals);
			
			//if (bitboard.getPlayedMovesCount() == 20) {
			//	System.out.println(bitboard);
			//}
			
			//System.out.println(bitboard);
			//System.out.println("BEST MOVE: " + best);			
			bitboard.makeMoveForward(best.getMoves()[0]);
			
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				break;
			}
			if (Math.abs(best.eval_ofOriginatePlayer()) > MAX_EVAL_DIFF) {
				break;
			}
			if (bitboard.getPlayedMovesCount() > MAX_MOVES) {
				break;
			}
			if (bitboard.getMaterialState().getPiecesCount() < MIN_PIECES) {
				break;
			}
			
			//if (bitboard.isInCheck()) System.out.println("isInCheck " + best.getMoves().length);
			//System.out.println(best.getMoves().length);
			
			EvaluatedPosition position = new EvaluatedPosition(bitboard.toEPD(), best.getMoves()[0]);
			position.setChildren(movesEvals);
			game.addBoard(position);
		}
		
		//System.out.println(bitboard);
		//System.out.println(game);
		
		return game;
	}

	
	private EvaluatedMove getBestVariation(boolean inCheck, Set<EvaluatedMove> evals) {
		
		EvaluatedMove best = null;
		if (inCheck) {
			best = evals.iterator().next();
		} else {
			best = evals.iterator().next();
			int besteval = best.eval_ofOriginatePlayer();
			int size = 0;
			for (EvaluatedMove cur: evals) {
				if (cur.eval_ofOriginatePlayer() + BEST_MOVE_DIFF < besteval) {
					break;
				}
				size++;
			}
			
			int index = (int) (Math.random() * size);
			//System.out.println("index=" + index);
			
			int counter1 = 0;
			for (EvaluatedMove cur: evals) {
				if (counter1 == index) {
					best = cur;
					break;
				}
				counter1++;
			}
		}

		return best;
	}
	
	
	private Set<EvaluatedMove> evalVariations(IBitBoard bitboard) throws IOException {

		IMoveList moves = new BaseMoveList(150);
		
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(moves);
		} else {
			bitboard.genAllMoves(moves);
		}
		
		Set<EvaluatedMove> evals = new TreeSet<EvaluatedMove>();
		
		int cur_move = 0;
		while ((cur_move = moves.next()) != 0) {
			
			String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
			//System.out.println("allMovesStr=" + allMovesStr);
			
			String moveStr = bitboard.getMoveOps().moveToString(cur_move);
			//System.out.println("moveStr=" + moveStr);
			
			bitboard.makeMoveForward(cur_move);
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				bitboard.makeMoveBackward(cur_move);
				continue;
			}
			
			NNUEProbeUtils.fillInput(bitboard, input);
			
			int actualPlayerEval = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
			
			actualPlayerEval = (2 * actualPlayerEval) / 3;
			
			/*if (bitboard.getColourToMove() == BLACK) {
				
				actualWhitePlayerEval = -actualWhitePlayerEval;
			}*/
			
			bitboard.makeMoveBackward(cur_move);
			
			
			//System.out.println("bitboard=" + bitboard.toEPD());
			
			String info = "info score cp " + actualPlayerEval + " pv ";
			
			//System.out.println("info=" + info);
			
			EvaluatedMove move = new EvaluatedMove(bitboard, cur_move, info);
			
			evals.add(move);
		}
		
		return evals;
	}
}
