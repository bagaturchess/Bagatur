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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.visitors.DataSet_1;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvalConfig;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class Trainer_IMPL4 extends Trainer_Base {
	
		
	private static final float LEARNING_RATE = 0.01f;
	
	
	private DataSet_1 dataset;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	
	private NeuralNetwork network;
	
	private BackpropagationTrainer trainer;
	
	
	public Trainer_IMPL4(IBitBoard _bitboard, String _filename_NN, IEvalConfig _evalConfig) throws Exception {
		
		
		super(_bitboard, _filename_NN, _evalConfig, ActivationFunction.LINEAR);
		
		
		dataset = new DataSet_1();
		
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		
		reloadNet();
	}

	
	@Override
	protected void reloadFromFile() throws Exception {
		
		
		super.reloadFromFile();
		
		
		//TODO: This has to be called each Epoch, but currently it throws OutOfMemoryError, so there is a memory leak ...
		//reloadNet();
	}


	private void reloadNet() throws IOException, FileNotFoundException, ClassNotFoundException {
		
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
	public void setGameOutcome(float game_result) throws Exception {
		
		
		if (dataset.isEmpty()) {
			
			//System.out.println("Game with no search moves. It has only opening moves and will be skiped.");
			
			return;
		}
		
		
		float EVAL_CHANGE_RATE = 0.5f;//0.1f;
		float eval_increase_factor = 1 + EVAL_CHANGE_RATE;
		float eval_decrease_factor = 1 - EVAL_CHANGE_RATE;
		
		for (int i = 0; i < outputs_per_move_actual.size(); i++) {
			
			float actual_eval_white = outputs_per_move_actual.get(i);
			
			float expected_eval_white = outputs_per_move_expected.get(i);
			
			if (expected_eval_white > 0) {
				
				if (expected_eval_white > actual_eval_white) {
					
					outputs_per_move_expected.remove(i);
					outputs_per_move_expected.add(i, actual_eval_white * eval_increase_factor);
					
				} else if (expected_eval_white < actual_eval_white) {
					
					outputs_per_move_expected.remove(i);
					outputs_per_move_expected.add(i, actual_eval_white * eval_decrease_factor);
				}
				
			} else if (expected_eval_white < 0) {
				
				if (expected_eval_white > actual_eval_white) {
					
					outputs_per_move_expected.remove(i);
					outputs_per_move_expected.add(i, actual_eval_white * eval_decrease_factor);
					
				} else if (expected_eval_white < actual_eval_white) {
					
					outputs_per_move_expected.remove(i);
					outputs_per_move_expected.add(i, actual_eval_white * eval_increase_factor);
				}
			}
		}
		
		
		super.setGameOutcome(game_result);
	}
	
	
	@Override
	public void addBoardPosition(IBitBoard bitboard) {
		
		
		float[] inputs_1d = new float[55];
		
		filler.fillSignals(inputs_1d, 0);
		
		Tensor tensor = new Tensor(inputs_1d);
		
		inputs_per_move.add(tensor);
		
		
		super.addBoardPosition(bitboard);
	}
	
	
	@Override
	public void backwardView() throws Exception {		
		
		
		for (int moveindex = 0; moveindex < inputs_per_move.size(); moveindex++) {
			
			
			//float actualWhitePlayerEval 	= outputs_per_move_actual.get(moveindex);
			
			float expectedWhitePlayerEval 	= outputs_per_move_expected.get(moveindex);
			
			//double deltaP 					= expectedWhitePlayerEval - actualWhitePlayerEval;
			
			Tensor inputs 					= (Tensor) inputs_per_move.get(moveindex);

			dataset.addItem(inputs, new float[] {expectedWhitePlayerEval});
		}
		
		
		super.backwardView();
	}
	
	
	@Override
	public void updateWeights() throws Exception {
		
		
        trainer.train(dataset);
        
        
        System.out.println("Trainer_IMPL4.updateWeights: Training Accuracy=" + trainer.getTrainingAccuracy());
        
        
		FileIO.writeToFile(network, filename_NN);
		
		
		reloadFromFile();
        
        
		dataset = new DataSet_1();
        
        
		super.updateWeights();
	}
}
