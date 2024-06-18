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


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.deeplearning.impl_nnue_v3.NNUEEvaluatorFactory;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.ucitracker.impl2.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl2.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl2.gamemodel.GameModelWriter;


public class GamesGenerator_Evaluator {
	
	
	private static int BEST_MOVE_DIFF 	= 5;
	private static int MAX_EVAL 		= 3000;
	
	private String initial_fen;
	
	
	public GamesGenerator_Evaluator(String _initial_fen) {
		
		initial_fen = _initial_fen;
	}
	
	
	public static void main(String[] args) {
		
		ChannelManager.setChannel(new Channel_Console());
		
		GamesGenerator_Evaluator control = new GamesGenerator_Evaluator(Constants.INITIAL_BOARD);
		
		try {
			
			control.execute("./self-play-eval-only.txt", 1000000, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void execute(String toFileName, int gamesCount, boolean appendToFile) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(toFileName, true), 512 * 1024);
		
		int positions = 0;
		
		for (int i=0; i<gamesCount; i++) {
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(initial_fen);
			
			IEvaluatorFactory eval_factory = new NNUEEvaluatorFactory();
			IEvaluator evaluator = eval_factory.create(bitboard, null);
			
			EvaluatedGame game = playGame(bitboard, evaluator);
			
			positions += game.getPositionsCount();
			
			GameModelWriter.writeEvaluatedGame(game, bw);
			
			bw.flush();
			
			System.out.println("Game " + (i+1) + " saved in " + toFileName + ", positions are " + positions);
		}
	}
	
	
	private EvaluatedGame playGame(IBitBoard bitboard, IEvaluator evaluator) throws IOException {
		
		EvaluatedGame game = new EvaluatedGame();
		
		while (true) {
			
			Set<EvaluatedMove> movesEvals = evalVariations(bitboard, evaluator);
			
			if (movesEvals.size() == 0) {
				
				throw new IllegalStateException("No valid moves");
				
				/*System.out.println("STATUS: " + bitboard.getStatus() + ", bitboard=" + bitboard);
				
				game.setResult(getGameTerminationScore(bitboard.getStatus()));
				
				break;
				*/
			}
			
			/*if (true) {
				
				throw new UnsupportedOperationException("Without qsearch doesn't work correctly");
			}*/
			
			EvaluatedMove best = getBestVariation(bitboard.isInCheck(), movesEvals);
			
			bitboard.makeMoveForward(best.getMoves()[0]);
			
			//System.out.println(bitboard);
			
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				
				System.out.println("STATUS: " + bitboard.getStatus());
				
				game.setResult(getGameTerminationScore(bitboard.getStatus()));
				
				break;
			}
			
			if (best.eval_ofOriginatePlayer() != 0 && best.eval_ofOriginatePlayer() % EvaluatedMove.MATE == 0) {

				if (best.getMoves().length == 1) {
					//Sometimes the pv is cut
					//throw new IllegalStateException(best.eval_ofOriginatePlayer() + " " + bitboard.toString());
				}
				
			} else {
				
				int eval = -evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, -1);
				if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
					eval = -eval;
				}
				game.addBoard(bitboard.getMoveOps().moveToString(best.getMoves()[0]), bitboard.toEPD(), eval);
				
				//System.out.println("best.eval_ofOriginatePlayer()=" + best.eval_ofOriginatePlayer());
				
				if (Math.abs(eval) > MAX_EVAL) {
					
					//System.out.println("");
					//if (true) throw new IllegalStateException();
					
					System.out.println("STATUS: " + bitboard.getStatus() + " terminated by score");
					
					if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
						
						if (eval > 0) {
							
							game.setResult(getGameTerminationScore(IGameStatus.MATE_WHITE_WIN));
						} else {
							
							game.setResult(getGameTerminationScore(IGameStatus.MATE_BLACK_WIN));
						}
						
					} else {
						
						if (eval > 0) {
							
							game.setResult(getGameTerminationScore(IGameStatus.MATE_BLACK_WIN));
						} else {
							
							game.setResult(getGameTerminationScore(IGameStatus.MATE_WHITE_WIN));
						}
					}
					
					break;
				}
			}
		}
		
		//System.out.println(bitboard);
		//System.out.println(game);
		
		return game;
	}

	
	private EvaluatedMove getBestVariation(boolean inCheck, Set<EvaluatedMove> evals) {
		
		//System.out.println(evals);
		
		EvaluatedMove best = null;
		if (inCheck) {
			best = evals.iterator().next();
		} else {
			best = evals.iterator().next();
			int besteval = best.eval_ofOriginatePlayer();
			int size = 0;
			for (EvaluatedMove cur: evals) {
				if (cur.eval_ofOriginatePlayer() + BEST_MOVE_DIFF <= besteval) {
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
	
	
	private Set<EvaluatedMove> evalVariations(IBitBoard bitboard, IEvaluator evaluator) throws IOException {
		IMoveList moves = new BaseMoveList(150);
		
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(moves);
		} else {
			bitboard.genAllMoves(moves);
		}
		
		Set<EvaluatedMove> evals = new TreeSet<EvaluatedMove>();
		
		int cur_move = 0;
		while ((cur_move = moves.next()) != 0) {
			
			//String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
			//System.out.println("allMovesStr=" + allMovesStr);
			
			//String moveStr = bitboard.getMoveOps().moveToString(cur_move);
			//System.out.println("moveStr=" + moveStr);
			
			int see_score = bitboard.getSEEScore(cur_move);
			int see_field = bitboard.getSEEFieldScore(bitboard.getMoveOps().getFromFieldID(cur_move));
			//if (see_score != 0) System.out.println("see_score=" + see_score);
			
			bitboard.makeMoveForward(cur_move);
			/*if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				bitboard.makeMoveBackward(cur_move);
				continue;
			}*/
			
			int actualPlayerEval = see_field + see_score -evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, -1);
			
			//actualPlayerEval = (2 * actualPlayerEval) / 3;
			
			/*if (bitboard.getColourToMove() == BLACK) {
				
				actualWhitePlayerEval = -actualWhitePlayerEval;
			}*/
			
			bitboard.makeMoveBackward(cur_move);
			
			
			//System.out.println("bitboard=" + bitboard.toEPD());
			
			String info = "info score cp " + actualPlayerEval + " pv " + bitboard.getMoveOps().moveToString(cur_move);
			
			//System.out.println("info=" + info);
			
			EvaluatedMove move = new EvaluatedMove(bitboard, info);
			
			evals.add(move);
		}
		
		//System.exit(0);
		
		return evals;
	}
	
	
	private float getGameTerminationScore(IGameStatus status) {
		
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				return 0.5f;
				
			case MATE_WHITE_WIN:
				return 1;
				
			case MATE_BLACK_WIN:
				return 0;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				return 0.5f;
				
			case STALEMATE_BLACK_NO_MOVES:
				return 0.5f;
				
			case DRAW_50_MOVES_RULE:
				return 0.5f;
				
			case NO_SUFFICIENT_MATERIAL:
				return 0.5f;
				
			case PASSER_WHITE:
				throw new IllegalStateException("status=" + status);
				
			case PASSER_BLACK:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_WHITE_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_BLACK_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			default:
				throw new IllegalStateException("status=" + status);
				
		}
	}
}
