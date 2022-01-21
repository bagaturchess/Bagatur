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
	private IEvaluator evaluator;
	
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
		
		evaluator = searcher.getSharedData().getEvaluatorFactory().create(bitboard, null);
		
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
		
		movesList.clear();
		
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			
			final ISearchMediator mediator = new UCISearchMediatorImpl_NormalSearch(ChannelManager.getChannel(),
					
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
			
			
			searcher.negamax(bitboard, mediator, timeController, go);
			
			synchronized (sync_move) {
				
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
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		
		//Revert board to the initial position
		for (int i = movesList.size() - 1; i >= 0; i--) {
			
			//System.out.println("BACK move: " + movesList.get(i));
			
			bitboard.makeMoveBackward(movesList.get(i));
		}
	}
	
	
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
}
