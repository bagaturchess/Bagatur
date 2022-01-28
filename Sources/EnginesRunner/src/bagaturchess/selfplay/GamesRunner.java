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
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
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
	
	
	private static final int SEARCH_DEPTH = 3;
	
	
	private IBitBoard bitboard;
	
	private IRootSearch searcher;
	
	private IEvaluator evaluator;
	
	private long gamesCounter;
	
	private long evaluatedPositionsCounter;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private VarStatistic stats = new VarStatistic();
	
	private long stats_draws;
	private long stats_wins_white;
	private long stats_wins_black;
	
	private Trainer trainer;
	
	private OpeningBook ob;
	
	
	public GamesRunner(IBitBoard _bitboard, IRootSearch _searcher, Trainer _dataset_builder) throws Exception {
		
		bitboard = _bitboard;
		
		searcher = _searcher;
		
		trainer = _dataset_builder;
		
		ob = OpeningBookFactory.load("./data/w.ob", "./data/b.ob");
	}
	
	
	public void playGames(int max_games) throws Exception {
		
		
		searcher.createBoard(bitboard);
		
		while (max_games > 0) {
			
			
			reloadNetworkInSearcher();
			
			
			trainer.clear();
			
			
			List<Integer> opening_moves = playRandomOpening();
			
			
			List<Integer> played_moves = playGame();
			
			
			float game_result = getGameTerminationScore(bitboard.getStatus());
			
			
			trainer.setGameOutcome(game_result);
			
			
            trainer.doEpoch();
            
            
			//Revert played moves
			for (int i = played_moves.size() - 1; i >= 0; i--) {
				
				bitboard.makeMoveBackward(played_moves.get(i));
			}
			
			
			//Revert opening moves
			for (int i = opening_moves.size() - 1; i >= 0; i--) {
				
				bitboard.makeMoveBackward(opening_moves.get(i));
			}
			
			
			gamesCounter++;
			
			if (gamesCounter % 1000 == 0) {
				
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
	
	
	private List<Integer> playRandomOpening() {
		
		List<Integer> opening_moves = new ArrayList<Integer>();
		
		int counter = 0;
		
		IOpeningEntry entry;
		
		while ((entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove())) != null) {
			
			if (entry.getWeight() < 50) {
				
				break;
			}

			//get next opening move using mode "random intermediate", which creates good randomness and still the starting postions are good enough (the chance of wining for both side is around ~50% in more than 99% of the cases)
			int move = entry.getRandomEntry(OpeningBook.OPENING_BOOK_MODE_POWER1);
			
			bitboard.makeMoveForward(move);
			
			opening_moves.add(move);
			
			counter++;
		}
		
		//System.out.println("GamesRunner.playRandomOpening: out of opening after " + counter + " moves, starting FEN is " + bitboard.toEPD());
		
		return opening_moves;
	}
	
	
	private List<Integer> playGame() throws IOException, InterruptedException {
		
		
		Go go = new Go(ChannelManager.getChannel(), "go depth " + SEARCH_DEPTH);
		//Go go = new Go(ChannelManager.getChannel(), "go infinite");
		
		Object sync_move = new Object();
		
		List<Integer> played_moves = new ArrayList<Integer>();
		
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
			
			played_moves.add(best_move);
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		return played_moves;
	}
	
	
	private float getGameTerminationScore(IGameStatus status) {
		
		
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
