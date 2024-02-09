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
package bagaturchess.deeplearning.impl_nnue.visitors;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE_PrintSuccessRate_FeedForward implements PositionsVisitor {
	
	
	private int counter;
	
	private FeedForwardNetwork network;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;	
	
	private ActivationFunction output_activation_function;
	
	
	public DeepLearningVisitorImpl_NNUE_PrintSuccessRate_FeedForward(ActivationFunction _output_activation_function) throws Exception {
		
		if ((new File(NNUE_Constants.NET_FILE)).exists()) {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NNUE_Constants.NET_FILE));
			
			network = (FeedForwardNetwork) ois.readObject();
			
			ois.close();
			
		} else {
			
			throw new IllegalStateException();
		}
		
		output_activation_function = _output_activation_function;
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		if (expectedWhitePlayerEval < IEvaluator.MIN_EVAL || expectedWhitePlayerEval > IEvaluator.MAX_EVAL) {
			
			throw new IllegalStateException("expectedWhitePlayerEval=" + expectedWhitePlayerEval);
		}
		
		
		float[] nnue_input = (float[])bitboard.getNNUEInputs();
		
		network.setInput(nnue_input);
		
		//forward method is already called in setInput(tensor)
		network.forward();
		
		float[] outputs = network.getOutput();
		
		double actualWhitePlayerEval = outputs[0];
		
		double actualWhitePlayerEval_x = output_activation_function.getx((float) actualWhitePlayerEval);
		//double expectedWhitePlayerEval_y = ActivationFunction.SIGMOID.gety(expectedWhitePlayerEval);
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		//sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		//sumDiffs2 += Math.abs(expectedWhitePlayerEval - Math.signum(actualWhitePlayerEval) * ActivationFunction.SIGMOID.getx((float) Math.abs(actualWhitePlayerEval)));
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval_x);
		
		//DataSet_1 dataset = new DataSet_1();
		//dataset.addItem(tensor, new float[] {(float) expectedWhitePlayerEval_y});
		//network.getTrainer().train(dataset);

		
		//System.out.println();
		counter++;
		
		if ((counter % 10000) == 0) {
			
			//System.out.println("Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, positions: " + counter);
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		
		sumDiffs1 = 0;
		
		sumDiffs2 = 0;
	}
	
	
	public void end() {
		
		System.out.println("END: Positions=" + counter + ", Time " + (System.currentTimeMillis() - startTime) + "ms, "
				+ "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, "
				//+ "Error: " + network.getTrainer().getMaxError() + ", Loss: " + network.getTrainer().getTrainingLoss()
				);
	}
}
