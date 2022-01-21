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
package bagaturchess.selfplay.logic;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.engines.cfg.base.TimeConfigImpl;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class GamesPlayer {
	
	
	private IBitBoard bitboard;
	private IRootSearch searcher;
	
	//private IMoveList movesBuffer = new BaseMoveList(250);
	//private int[] seeBuffer = new int[250];
	private List<Integer> movesList = new ArrayList<Integer>();
	
	private long gamesCounter;
	private long evaluatedPositionsCounter;
	
	private double sumDiffs1;
	private double sumDiffs2;
	private VarStatistic stats = new VarStatistic();
	
	private ISelfLearning learning;
	
	
	public GamesPlayer(IBitBoard _bitboard, IRootSearch _searcher, ISelfLearning _learning) throws IOException {
		
		bitboard = _bitboard;
		
		searcher = _searcher;
		
		learning = _learning;
	}
	
	
	public void playGames() throws IOException, InterruptedException {
		
		searcher.createBoard(bitboard);
		
		while (true) {
			
			//TODO: update NN in memory - recreate searcher and evaluator
			//getEnv().getEval().beforeSearch();
			
			playGame();
			
			gamesCounter++;
			
			if (gamesCounter % 100 == 0) {
				
				System.out.println("Count of played games is " + gamesCounter + ", evaluated positions are " + evaluatedPositionsCounter
						+ ", accuracy is " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%"
						+ ", stats.avg=" + stats.getEntropy()
						+ ", stats.stdev=" + stats.getDisperse());
				
				learning.endEpoch();
				
				//sumDiffs1 = 0;
				//sumDiffs2 = 0;
			}
		}
	}
	
	
	private void playGame() throws IOException, InterruptedException {
		
		
		Go go = new Go(ChannelManager.getChannel(), "go depth 1");
		//Go go = new Go(ChannelManager.getChannel(), "go infinite");
		
		ITimeController timeController = TimeControllerFactory.createTimeController(new TimeConfigImpl(), bitboard.getColourToMove(), go);
		
		Object sync_move = new Object();
		
		IEvaluator evaluator = searcher.getSharedData().getEvaluatorFactory().create(bitboard, null);
		
		movesList.clear();
		
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			
			final ISearchMediator mediator1 = new UCISearchMediatorImpl_NormalSearch(ChannelManager.getChannel(),
					
					go,
					
					timeController,
					
					bitboard.getColourToMove(),
					
					new BestMoveSender() {
						@Override
						public void sendBestMove() {
							
							//System.out.println("MTDSchedulerMain: Best move send");
							
							synchronized (sync_move) {
							
								sync_move.notifyAll();
							}
						}
					},
					
					searcher, true);
			
			
			searcher.negamax(bitboard, mediator1, timeController, go);
			
			synchronized (sync_move) {
				
				sync_move.wait();
			}
			
			ISearchInfo info = mediator1.getLastInfo();
			
			if (!info.isMateScore()) {
				
				int[] bestline = info.getPV();
				
				int color_sign = 1;
				
				boolean pv_status_is_none = true;
				
				int move_index = 0;
				for (; move_index < bestline.length; move_index++) {
					
					bitboard.makeMoveForward(bestline[move_index]);
					
					color_sign *= -1;
					
					if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
						
						//System.out.println("Game status is not NONE: " + bitboard.getStatus());
						
						pv_status_is_none = false;
						
						break;
					}
				}
				
				
				if (pv_status_is_none) {
					
					double eval_search = color_sign * info.getEval();
					
					//Evaluate position
					double eval_static = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
					
					//System.out.println("eval_search=" + eval_search + ", eval_static=" + eval_static);
					
					sumDiffs1 += Math.abs(0 - eval_search);
					
					sumDiffs2 += Math.abs(eval_search - eval_static);
					
					//System.out.println("sumDiffs1=" + sumDiffs1 + ", sumDiffs2=" + sumDiffs2);
					
					stats.addValue(Math.abs(eval_search - eval_static));
				
				}
				
				
				if (move_index >= bestline.length) {
					move_index = bestline.length - 1;
				}
				for (int i = move_index; i >= 0; i--) {
					
					bitboard.makeMoveBackward(bestline[i]);
				}
			}
			
			
			int best_move = info.getBestMove();
			
			bitboard.makeMoveForward(best_move);
			
			movesList.add(best_move);
			
			evaluatedPositionsCounter++;
			
			
			/*String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
			
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
					
					learning.addCase(expectedWhitePlayerEval, actualWhitePlayerEval);
					evaluatedPositionsCounter++;
					
					//Revert position
					for (int i=pv_moves.length - 1; i >= 0; i--) {
						bitboard.makeMoveBackward(pv_moves[i]);
					}
				}
			}*/
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		
		//Revert board to the initial position
		for (int i = movesList.size() - 1; i >= 0; i--) {
			
			//System.out.println("BACK move: " + movesList.get(i));
			
			bitboard.makeMoveBackward(movesList.get(i));
		}
	}
	
	
	/*private int getBestMove() {
		
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
	}*/
	
	
	private int getGameTerminationScore() {
		
		if (bitboard.getStateRepetition() >= 2) {
			return 0;
		}
		
		if (!bitboard.hasSufficientMatingMaterial()) {
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
	
	
	/*private List<String> getMultiPVs(String allMovesStr) throws IOException {
		
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
								if (lines.get(i).contains("info ") && lines.get(i).contains(" pv ")) {
									if (exitLines == null) {
										exitLines = lines.get(i) + ";";
									} else {
										exitLines += lines.get(i) + ";";
									}	
								}
							}
							if (exitLines == null) {
								//System.out.println("No pv: " + lines);
								//throw new IllegalStateException("No pv: " + lines);
								exitLines = "";
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
	}*/
	
	
	private class BestMoveSender_Loop implements BestMoveSender {
		
		
		private int last_bestmove = 0;
		
		private Object sync_move;
		
		
		BestMoveSender_Loop(Object _sync_move) {
			
			sync_move = _sync_move;
		}
		
		
		@Override
		public void sendBestMove() {
			
			System.out.println("MTDSchedulerMain: Best move send");
			
			synchronized (sync_move) {
			
				sync_move.notifyAll();
			}
		}
	
		
		int getLast_Bestmove() {
			
			return last_bestmove;
		}
	}
}
