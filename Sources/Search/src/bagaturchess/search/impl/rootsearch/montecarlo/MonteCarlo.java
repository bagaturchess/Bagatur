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
package bagaturchess.search.impl.rootsearch.montecarlo;


import java.util.HashMap;
import java.util.Map;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.alg.SearchUtils;


public class MonteCarlo {
	
	
	private static final int ACCEPTED_MOVE_DIFF = 15;
	
	private IBitBoard bitboard;
	private IEvaluator evaluator;
	
	private IMoveList movesBuffer = new BaseMoveList(333); 
	private IMoveList movesBuffer_root = new BaseMoveList(333);
	
	
	public MonteCarlo(IBitBoard _bitboard, IEvaluator _evaluator) {
		bitboard = _bitboard;
		evaluator = _evaluator;
	}
	
	
	public void play_iterations(int maxGames, ISearchStopper stopper, IMonteCarloListener listener) {
		
		//long startTime = System.currentTimeMillis();
		
		Map<Integer, GamesResult> global_map = new HashMap<Integer, GamesResult>();
		
		for (int gamesCount = 1; gamesCount < maxGames; gamesCount++) {
			
			if (stopper != null) stopper.stopIfNecessary(10, bitboard.getColourToMove(), Integer.MIN_VALUE, Integer.MAX_VALUE);
			
			Map<Integer, GamesResult> movesMap = play(gamesCount, stopper);
			
			for (Integer move : movesMap.keySet()) {
				GamesResult result = movesMap.get(move);
				if (global_map.containsKey(move)) {
					GamesResult global_result = global_map.get(move);
					global_result.wins += result.wins;
					global_result.draws += result.draws;
					global_result.loses += result.loses;
				} else {
					global_map.put(move, result);
				}
			}
			
			listener.newData(global_map, gamesCount);
			
			/*System.out.println(gamesCount + " > "
					+ MoveInt.moveToString(best_move)
					+ ", rate " + best_rate
					+ ", time " + (System.currentTimeMillis() - startTime) + "ms");
					*/
		}
		
		//System.out.println("games " + gamesCount
		//		+ ", time " + (System.currentTimeMillis() - startTime) + "ms");
	}
	
	
	private Map<Integer, GamesResult> play(int gamesCount, ISearchStopper stopper) {
		
		Map<Integer, GamesResult> result = new HashMap<Integer, GamesResult>();
		
		movesBuffer_root.clear();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(movesBuffer_root);
		} else {
			bitboard.genAllMoves(movesBuffer_root);
		}
		
		int cur_move = 0;
		while ((cur_move = movesBuffer_root.next()) != 0) {
			
			bitboard.makeMoveForward(cur_move);
			
			GamesResult gamesResult = playGames(gamesCount, stopper);
			
			bitboard.makeMoveBackward(cur_move);
			
			int wins = gamesResult.wins;
			int loses = gamesResult.loses;
			gamesResult.loses = wins;
			gamesResult.wins = loses;
			
			result.put(cur_move, gamesResult);
		}
		
		return result;
	}
	
	
	private GamesResult playGames(int count, ISearchStopper stopper) {
		
		GamesResult gamesResult = new GamesResult();
		
		for (int i=0; i<count; i++) {
			
			if (stopper != null) stopper.stopIfNecessary(10, bitboard.getColourToMove(), Integer.MIN_VALUE, Integer.MAX_VALUE);
			
			bitboard.mark();
			
			while (gameIsOk(bitboard.getStatus())) {
				int move = selectMove();
				//System.out.println(MoveInt.moveToString(move));
				bitboard.makeMoveForward(move);
			}
			
			addResultForWhite(bitboard.getStatus(), gamesResult);
			
			bitboard.reset();
			
			if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
				int wins = gamesResult.wins;
				int loses = gamesResult.loses;
				gamesResult.loses = wins;
				gamesResult.wins = loses;
			}
			//System.out.println(gamesResult);
		}
		
		//System.out.println(bitboard);
		
		return gamesResult;
	}
	
	
	private int selectMove() {
		
		movesBuffer.clear();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(movesBuffer);
		} else {
			bitboard.genAllMoves(movesBuffer);
		}
		
		int selectedMove_1 = -1;
		int selectedEval_1 = Integer.MIN_VALUE;
		int selectedMove_2 = -1;
		int selectedEval_2 = Integer.MIN_VALUE;
		
		int cur_move = 0;
		while ((cur_move = movesBuffer.next()) != 0) {
			
			int seeMove = bitboard.getSee().evalExchange(cur_move);
			int seeField = -bitboard.getSee().seeField(bitboard.getMoveOps().getFromFieldID(cur_move));
			
			/*if (seeField != 0) {
				System.out.println(MoveInt.moveToString(cur_move));
				String move = MoveInt.moveToString(cur_move);
			}*/
			
			bitboard.makeMoveForward(cur_move);
			
			int cur_eval;
			IGameStatus status = bitboard.getStatus();
			if (status != IGameStatus.NONE) {
				
				if (status == IGameStatus.MATE_WHITE_WIN) {
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						throw new IllegalStateException();
					}
					cur_eval = SearchUtils.getMateVal(1);
					
				} else if (status == IGameStatus.MATE_BLACK_WIN) {
					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						throw new IllegalStateException();
					}
					cur_eval = SearchUtils.getMateVal(1);
					
				} else if (status == IGameStatus.DRAW_3_STATES_REPETITION
						|| status == IGameStatus.DRAW_50_MOVES_RULE
						|| status == IGameStatus.STALEMATE_WHITE_NO_MOVES
						|| status == IGameStatus.STALEMATE_BLACK_NO_MOVES
						|| status == IGameStatus.NO_SUFFICIENT_MATERIAL) {
					
					cur_eval = 0;
					
				} else if (status == IGameStatus.PASSER_WHITE
						|| status == IGameStatus.PASSER_BLACK
						|| status == IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL
						|| status == IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL) {
					
					cur_eval = (int) -evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
					if (bitboard.getMoveOps().isCapture(cur_move)) {
						cur_eval -= bitboard.getBaseEvaluation().getMaterial(bitboard.getMoveOps().getCapturedFigureType(cur_move));
					}
					cur_eval += seeMove;
					cur_eval += seeField / 10;
					
				} else {
					throw new IllegalStateException("error: " + status);
				}
			} else {
				
				cur_eval = (int) -evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
				if (bitboard.getMoveOps().isCapture(cur_move)) {
					cur_eval -= bitboard.getBaseEvaluation().getMaterial(bitboard.getMoveOps().getCapturedFigureType(cur_move));
				}
				cur_eval += seeMove;
				cur_eval += seeField / 10;
				
			}
			
			bitboard.makeMoveBackward(cur_move);
			
			if (cur_eval > selectedEval_1) {
				
				selectedEval_2 = selectedEval_1;
				selectedMove_2 = selectedMove_1;
				
				selectedEval_1 = cur_eval;
				selectedMove_1 = cur_move;
			}
		}
		
		if (selectedMove_1 != -1 && selectedMove_2 != -1) {
			
			if (Math.abs(selectedEval_1 - selectedEval_2) < ACCEPTED_MOVE_DIFF) {
				return (Math.random() < 0.5) ? selectedMove_1 : selectedMove_2;				
			} else {
				return selectedMove_1;
			}
			
		} else {
			return selectedMove_1;
		}
	}
	

	private boolean gameIsOk(IGameStatus status) {
		return status == IGameStatus.NONE
			|| status == IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL
			|| status == IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL
			|| status == IGameStatus.PASSER_BLACK
			|| status == IGameStatus.PASSER_WHITE;
	}
	
	
	private void addResultForWhite(IGameStatus status, GamesResult gamesResult) {
		if (status == IGameStatus.DRAW_3_STATES_REPETITION) {
			gamesResult.draws++;
		} else if (status == IGameStatus.DRAW_50_MOVES_RULE) {
			gamesResult.draws++;
		} else if (status == IGameStatus.MATE_BLACK_WIN) {
			gamesResult.loses++;
		} else if (status == IGameStatus.MATE_WHITE_WIN) {
			gamesResult.wins++;
		} else if (status == IGameStatus.NO_SUFFICIENT_MATERIAL) {
			gamesResult.draws++;
		} else if (status == IGameStatus.STALEMATE_BLACK_NO_MOVES) {
			gamesResult.draws++;
		} else if (status == IGameStatus.STALEMATE_WHITE_NO_MOVES) {
			gamesResult.draws++;
		} else {
			throw new IllegalStateException("status=" + status);
		}
	}
}
