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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;


public class GamesPlayer {
	
	
	private IBitBoard bitboard;
	private IEvaluator evaluator;
	
	private IMoveList movesBuffer = new BaseMoveList(250);
	private int[] seeBuffer = new int[250];
	private List<Integer> movesList = new ArrayList<Integer>();
	
	private long gamesCounter;
	private long evaluatedPositionsCounter;
	
	private UCIEnginesManager runner;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private ISelfLearning learning;
	
	
	public GamesPlayer(IBitBoard _bitboard, IEvaluator _evaluator, UCIEnginesManager _runner, ISelfLearning _learning) throws IOException {
		
		bitboard = _bitboard;
		evaluator = _evaluator;
		runner = _runner;
		learning = _learning;
	}


	public void playGames() throws IOException {
		
		while (true) {
			
			runner.newGame();
			
			playGame();
			
			gamesCounter++;
			
			if (gamesCounter % 100 == 0) {
				System.out.println("Count of played games is " + gamesCounter + ", evaluated positions are " + evaluatedPositionsCounter + ", Success is " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
				learning.endEpoch();
			}
		}
	}
	
	
	private void playGame() throws IOException {
		
		movesList.clear();
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
			List<String> engineMultiPVs = getMultiPVs(allMovesStr);
			for (String infoLine: engineMultiPVs) {
				EvaluatedMove em = new EvaluatedMove(bitboard, infoLine);
				if (em.getStatus() == IGameStatus.NONE) {
					
					int root_colour = bitboard.getColourToMove();
					
					//Setup position
					int[] pv_moves = em.getMoves();
					int eval_sign = 1;
					for (int i=0; i <= pv_moves.length - 1; i++) {
						bitboard.makeMoveForward(pv_moves[i]);
						eval_sign *= -1;
					}
					int our_eval = (int) (eval_sign * evaluator.fullEval(0, 0, 0, 0));
					
					//Do the adjustments
					int engine_eval = em.eval_ofOriginatePlayer();
					double actualWhitePlayerEval = root_colour == Constants.COLOUR_WHITE ? our_eval : -our_eval;
					double expectedWhitePlayerEval = root_colour == Constants.COLOUR_WHITE ? engine_eval : -engine_eval;
					
					sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
					sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
					
					double deltaValueFromWhitePlayerPerspective = expectedWhitePlayerEval - actualWhitePlayerEval;
					
					learning.addCase(deltaValueFromWhitePlayerPerspective);
					evaluatedPositionsCounter++;
					
					//Revert position
					for (int i=pv_moves.length - 1; i >= 0; i--) {
						bitboard.makeMoveBackward(pv_moves[i]);
					}
				}
			}
			
			int best_move = getBestMove();
			
			bitboard.makeMoveForward(best_move);
			movesList.add(best_move);
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
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
		
		//Sort by SEE and return best forced move if any
		for (int i = 0; i<moves_size; i++) {
			seeBuffer[i] = SEEUtil.getSeeCaptureScore(((BoardImpl)bitboard).getChessBoard(), moves[i]);
		}
		Utils.bubbleSort(seeBuffer, moves, moves_size);
		if (moves_size >= 1 && seeBuffer[0] > 0) {
			return moves[0];
		}
		
		//Iterate moves and find best one
		int best_move = 0;
		int best_eval = Integer.MIN_VALUE;
		
		int cur_move = 0;
		while ((cur_move = movesBuffer.next()) != 0) {
			
			//Skip bad moves, which loose material
			int moveSee = SEEUtil.getSeeCaptureScore(((BoardImpl)bitboard).getChessBoard(), cur_move);
			if (best_move != 0 && moveSee < 0) {
				continue;
			}
			
			bitboard.makeMoveForward(cur_move);
			
			//Check for game termination score. If not than evaluate position normally.
			int cur_eval = getGameTerminationScore();
			if (cur_eval == -1) {
				cur_eval = (int) -evaluator.fullEval(0, 0, 0, 0);
			}
			
			bitboard.makeMoveBackward(cur_move);
			
			if (cur_eval > best_eval) {
				best_eval = cur_eval;
				best_move = cur_move;
			}
		}
		
		if (best_move == 0) {
			throw new IllegalStateException("No moves");
		}
				
		return best_move;
	}
	
	
	private int getGameTerminationScore() {
		
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
	
	
	private List<String> getMultiPVs(String allMovesStr) throws IOException {
		
		int depth = 1;
		
		String info = null;
		List<String> infos = null;
				
		boolean loop = true;
		while (loop) {
			
			runner.setupPosition("startpos moves " + allMovesStr);
			runner.disable();

			runner.go_Depth(depth);
			
			infos = runner.getInfoLines(new LineCallBack() {
					
					
					private List<String> lines = new ArrayList<String>();
					private String exitLines = null; 
					
					
					@Override
					public void newLine(String line) {
						
						//System.out.println("EngineProcess: getInfoLine new line is: '" + line + "'");
						
						if (line.contains("LOG")) {
							return;
						}
						
						lines.add(line);
						
						if (line.contains("bestmove")) {
							for (int i=lines.size() - 1; i >=0; i--) {
								//System.out.println("EngineProcess: getInfoLine " + lines.get(i));
								if (lines.get(i).contains("info "/*depth"*/) && lines.get(i).contains(" pv ")) {
									if (exitLines == null) {
										exitLines = lines.get(i) + ";";
									} else {
										exitLines += lines.get(i) + ";";
									}	
								}
							}
							if (exitLines == null) {
								//System.out.println("No pv: " + lines);
								throw new IllegalStateException("No pv: " + lines);
							}
						}
					}
					
					
					@Override
					public String exitLine() {
						return exitLines;
					}
				});
			
			if (infos != null && infos.size() > 1) {
				throw new IllegalStateException("Only one engine is supported");
			}
			
			if (infos == null || infos.size() == 0 || infos.get(0) == null) {
				depth++;
				System.out.println("depth = " + depth);
				if (depth > 5) {
					throw new IllegalStateException("depth >  " + 5 + " and no PV info");
				}
			} else {
				
				info = infos.get(0);
				loop = false;
			}
		}
		runner.enable();
		
		List<String> result = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(info, ";");
		while (st.hasMoreTokens()) {
			result.add(st.nextToken());
		}
		
		return result;
	}
}
