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
package bagaturchess.selfplay.train;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.visitors.DataSet_1;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvaluator;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class Trainer_IMPL4 implements Trainer {
	
	
	private static final float LEARNING_RATE 		= 1f;//0.01f;//0.000000000000000000000000000000000000000000001f;
	
	//private static final ActivationFunction activation_function = ActivationFunction.SIGMOID;
	
	private static final ActivationFunction activation_function = ActivationFunction.LINEAR;
	
	private static final float MAX_EVAL = 7777;
	
	
	private IBitBoard bitboard;
	
	private String filename_NN;
	
	private DataSet_1 dataset;
	
	private List<Tensor> inputs_per_move;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	
	private NeuralNetwork network;
	
	private BackpropagationTrainer trainer;
	
	
	public Trainer_IMPL4(IBitBoard _bitboard, String _filename_NN) throws Exception {
		
		bitboard = _bitboard;
		
		filename_NN = _filename_NN;
		
		if (!(new File(filename_NN)).exists()) {
			
			throw new IllegalStateException("NN file not found: " + filename_NN);
			
		}
		
		inputs_per_move = new ArrayList<Tensor>();
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename_NN));
		
		network = (NeuralNetwork) ois.readObject();
		
		ois.close();
		
		
		trainer = (BackpropagationTrainer) network.getTrainer();
        
        trainer.setMaxEpochs(1)
        //.setOptimizer(OptimizerType.MOMENTUM)
        		.setLearningRate(LEARNING_RATE);
                //.setMaxError(0.000000000000001f);
                //.setBatchMode(true)
                //.setBatchSize(dataset.size());
	}

	
	@Override
	public void clear() {
		
		inputs_per_move.clear();
		
		dataset = new DataSet_1();
	}
	
	
	@Override
	public void doEpoch() throws Exception {
		
		if (dataset.isEmpty()) {
			
			return;
		}
		
		
        trainer.train(dataset);
		
		
		FileIO.writeToFile(network, filename_NN);
	}
	
	
	@Override
	public void addBoardPosition(IBitBoard bitboard) {
		
		float[] inputs_1d = new float[55];
		
		filler.fillSignals(inputs_1d, 0);
		
		Tensor tensor = new Tensor(inputs_1d);
		
		inputs_per_move.add(tensor);
	}
	
	
	@Override
	public void setGameOutcome(float game_result) {
		
		float step;
		
		if (game_result == 0) { //Draw
			
			step = 0;
					
		} else if (game_result == 1) { //White wins
			
			//step = (activation_function.gety(IEvaluator.MAX_EVAL) / (float) inputs_per_move.size());
			step = (activation_function.gety(MAX_EVAL) / (float) inputs_per_move.size());
			
		} else { //Black wins
			
			//step = (activation_function.gety(IEvaluator.MIN_EVAL) / (float) inputs_per_move.size());
			step = (activation_function.gety(-MAX_EVAL) / (float) inputs_per_move.size());
		}
		
		for (int i = 0; i < inputs_per_move.size(); i++) {
			
	        float[] output = new float[1];
	        
	        //output[0] = (float) (0.5 + i * step);
	        output[0] = i * step;
	        
	        dataset.addItem(inputs_per_move.get(i), output);
		}
	}
	
	
	/*public void setGameOutcome_Sigmoid(float game_result) {
		
		boolean draw = game_result == 0;
		
		boolean white_win = game_result == 1;
		
		float step;
		
		if (draw) {
			
			step = 0;
					
		} else if (white_win) {
			
			step = +(float) ((activation_function.gety(IEvaluator.MAX_EVAL) - 0.5) / (float) inputs_per_move.size());
			
		} else {
			
			step = -(float) ((0.5 - activation_function.gety(IEvaluator.MIN_EVAL)) / (float) inputs_per_move.size());
		}
		
		for (int i = 0; i < inputs_per_move.size(); i++) {
			
	        float[] output = new float[1];
	        
	        output[0] = (float) (0.5 + i * step);
	        
	        dataset.addItem(inputs_per_move.get(i), output);
		}
	}*/
}
