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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.visitors.ActivationFunctions;
import bagaturchess.deeplearning.impl_nnue.visitors.DataSet_1;
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
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class GamesPlayer {
	
	
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
	
	private NeuralNetwork network;
	
	private DataSet_1 dataset;
	
	
	public GamesPlayer(IBitBoard _bitboard, IRootSearch _searcher) throws Exception {
		
		bitboard = _bitboard;
		
		searcher = _searcher;
		
		if (!(new File(NNUE_Constants.NET_FILE)).exists()) {
			
			throw new IllegalStateException("NNUE file not found: " + NNUE_Constants.NET_FILE);
			
		} else {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NNUE_Constants.NET_FILE));
			
			network = (ConvolutionalNetwork) ois.readObject();
			
			ois.close();
		}
	}
	
	
	public void playGames() throws IOException, InterruptedException {
		
		
		searcher.createBoard(bitboard);
		
		
		while (true) {
			
			
			reloadNetwork();
			
			
			dataset = new DataSet_1();
			
			
			playGame();
			
			
            // create a trainer and train network
            BackpropagationTrainer trainer = (BackpropagationTrainer) network.getTrainer();
            
            trainer.setLearningRate(0.01f)
                    .setMaxError(0.01f)
                    .setMaxEpochs(1);
                    //.setBatchMode(true)
                    //.setBatchSize(1000);
            
            trainer.train(dataset);
			
			
			FileIO.writeToFile(network, NNUE_Constants.NET_FILE);
			
			
			gamesCounter++;
			
			if (gamesCounter % 10 == 0) {
				
				System.out.println("Games: " + gamesCounter
						+ ", Positions: " + evaluatedPositionsCounter
						+ ", Draws: " + stats_draws
						+ ", White Win: " + stats_wins_white
						+ ", Black Win: " + stats_wins_black
						+ ", Accuracy: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%"
						+ ", Stats.avg=" + stats.getEntropy()
						+ ", Stats.stdev=" + stats.getDisperse()
						);
			}
		}
	}
	
	
	private void reloadNetwork() {
		
		//Update NN in memory - recreate searcher and evaluator
		
		searcher.recreateEvaluator();
		
		evaluator = searcher.getSharedData().getEvaluatorFactory().create(bitboard, null);
	}
	
	
	private void playGame() throws IOException, InterruptedException {
		
		
		List<float[][][]> inputs_per_move = new ArrayList<float[][][]>();
		
		
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
					
					evaluatedPositionsCounter++;
					
					double eval_search = color_sign * info.getEval();
					
					//Evaluate position
					double eval_static = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
					
					//System.out.println("eval_search=" + eval_search + ", eval_static=" + eval_static);
					
					sumDiffs1 += Math.abs(0 - eval_search);
					
					sumDiffs2 += Math.abs(eval_search - eval_static);
					
					//System.out.println("sumDiffs1=" + sumDiffs1 + ", sumDiffs2=" + sumDiffs2);
					
					stats.addValue(Math.abs(eval_search - eval_static));
					
					
					float[][][] inputs_3d = new float[8][8][15];
					
					inputs_per_move.add(inputs_3d);
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
		}
		
		//System.out.println(bitboard.toEPD() + " " + bitboard.getStatus());
		
		
		float result = getGameTerminationScore_SIGMOID(bitboard.getStatus());
		
		for (int i = 0; i < inputs_per_move.size(); i++) {
			
			float[][][] inputs_3d = inputs_per_move.get(i);
			
			Tensor input = new Tensor(inputs_3d);
			
	        float[] output = new float[1];
	        output[0] = result;
	        
	        dataset.addItem(input, output);
		}
		
		
		//Revert board to the initial position
		for (int i = movesList.size() - 1; i >= 0; i--) {
			
			//System.out.println("BACK move: " + movesList.get(i));
			
			bitboard.makeMoveBackward(movesList.get(i));
		}
	}
	
	
	private float getGameTerminationScore_SIGMOID(IGameStatus status) {
		
		
		float SIGMOID_WIN_BLACK 	= ActivationFunctions.sigmoid_gety(IEvaluator.MIN_EVAL);
		
		float SIGMOID_DRAW 			= 0.5f;
		
		float SIGMOID_WIN_WHITE 	= ActivationFunctions.sigmoid_gety(IEvaluator.MAX_EVAL);
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				stats_draws++;
				return SIGMOID_DRAW;
				
			case MATE_WHITE_WIN:
				stats_wins_white++;
				return SIGMOID_WIN_WHITE;
				
			case MATE_BLACK_WIN:
				stats_wins_black++;
				return SIGMOID_WIN_BLACK;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				stats_draws++;
				return SIGMOID_DRAW;
				
			case STALEMATE_BLACK_NO_MOVES:
				stats_draws++;
				return SIGMOID_DRAW;
				
			case DRAW_50_MOVES_RULE:
				stats_draws++;
				return SIGMOID_DRAW;
				
			case NO_SUFFICIENT_MATERIAL:
				stats_draws++;
				return SIGMOID_DRAW;
				
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
