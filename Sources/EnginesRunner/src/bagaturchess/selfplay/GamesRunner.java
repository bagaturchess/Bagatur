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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.engines.cfg.base.TimeConfigImpl;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.selfplay.train.Trainer;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class GamesRunner {
	
	
	private IBitBoard bitboard;
	
	private IRootSearch searcher;
	
	private IEvaluator evaluator;
	
	private List<Integer> movesList = new ArrayList<Integer>();
	
	private long gamesCounter;
	
	private long evaluatedPositionsCounter;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private VarStatistic stats = new VarStatistic();
	
	private long stats_draws;
	private long stats_wins_white;
	private long stats_wins_black;
	
	private Trainer trainer;
	
	
	public GamesRunner(IBitBoard _bitboard, IRootSearch _searcher, Trainer _dataset_builder) throws Exception {
		
		bitboard = _bitboard;
		
		searcher = _searcher;
		
		trainer = _dataset_builder;
	}
	
	
	public void playGames(int max_games) throws Exception {
		
		
		searcher.createBoard(bitboard);
		
		while (max_games > 0) {
			
			
			reloadNetworkInSearcher();
			
			
			trainer.clear();
			
			
			playGame();
			
            
            trainer.doEpoch();
            
			
			gamesCounter++;
			
			if (gamesCounter % 10 == 0) {
				
				System.out.println("Games: " + gamesCounter
						+ ", Draws: " + (100 * (stats_draws) / (stats_draws + stats_wins_white + stats_wins_black)) + "%"
						+ ", Draws: " + stats_draws
						+ ", White Win: " + stats_wins_white
						+ ", Black Win: " + stats_wins_black
						+ ", Positions: " + evaluatedPositionsCounter
						+ ", PV Accuracy: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%"
						+ ", PV Accuracy: Stats.avg=" + stats.getEntropy()
						+ ", PV Accuracy: Stats.stdev=" + stats.getDisperse()
						);
			}
			
			max_games--;
		}
	}
	
	
	private void reloadNetworkInSearcher() throws FileNotFoundException, ClassNotFoundException, IOException {
		
		//Update NN in memory - recreate searcher and evaluator
		
		searcher.recreateEvaluator();
		
		evaluator = searcher.getSharedData().getEvaluatorFactory().create(bitboard, null);
		
	}
	
	
	private void playGame() throws IOException, InterruptedException {
		
		
		Go go = new Go(ChannelManager.getChannel(), "go depth 1");
		//Go go = new Go(ChannelManager.getChannel(), "go infinite");
		
		Object sync_move = new Object();
		
		movesList.clear();
		
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			final ITimeController timeController = TimeControllerFactory.createTimeController(new TimeConfigImpl(), bitboard.getColourToMove(), go);
			
			final ISearchMediator mediator = new UCISearchMediatorImpl_NormalSearch(ChannelManager.getChannel(),
					
					go,
					
					timeController,
					
					bitboard.getColourToMove(),
					
					new BestMoveSender() {
						@Override
						public void sendBestMove() {
							
							synchronized (sync_move) {
								
								sync_move.notifyAll();
							}
						}
					},
					
					searcher, true);
			
			synchronized (sync_move) {
				
				searcher.negamax(bitboard, mediator, timeController, go);
				
				sync_move.wait();
			}
			
			
			ISearchInfo info = mediator.getLastInfo();
			
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
					
					evaluatedPositionsCounter++;
					
					double eval_search = color_sign * info.getEval();
					
					//Evaluate position
					double eval_static = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
					
					//System.out.println("eval_search=" + eval_search + ", eval_static=" + eval_static);
					
					sumDiffs1 += Math.abs(0 - eval_search);
					
					sumDiffs2 += Math.abs(eval_search - eval_static);
					
					//System.out.println("sumDiffs1=" + sumDiffs1 + ", sumDiffs2=" + sumDiffs2);
					
					stats.addValue(Math.abs(eval_search - eval_static));
					
					trainer.addBoardPosition(bitboard);
				}
				
				
				if (move_index >= bestline.length) {
					move_index = bestline.length - 1;
				}
				for (int i = move_index; i >= 0; i--) {
					
					bitboard.makeMoveBackward(bestline[i]);
				}
			}
	        
			
			int best_move = info.getBestMove();
			
			//System.out.println("best_move=" + best_move);
			
			bitboard.makeMoveForward(best_move);
			
			movesList.add(best_move);
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		
		float game_result = getGameTerminationScore_SIGMOID(bitboard.getStatus());
		
		trainer.setGameOutcome(game_result);
		
		
		//Revert board to the initial position
		for (int i = movesList.size() - 1; i >= 0; i--) {
			
			//System.out.println("BACK move: " + movesList.get(i));
			
			bitboard.makeMoveBackward(movesList.get(i));
		}
	}
	
	
	private float getGameTerminationScore_SIGMOID(IGameStatus status) {
		
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				stats_draws++;
				return 0;
				
			case MATE_WHITE_WIN:
				stats_wins_white++;
				return 1;
				
			case MATE_BLACK_WIN:
				stats_wins_black++;
				return -1;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				stats_draws++;
				return 0;
				
			case STALEMATE_BLACK_NO_MOVES:
				stats_draws++;
				return 0;
				
			case DRAW_50_MOVES_RULE:
				stats_draws++;
				return 0;
				
			case NO_SUFFICIENT_MATERIAL:
				stats_draws++;
				return 0;
				
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
