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
import java.io.IOException;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.NeuralNetworkUtils_NNUE_PSQT;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE_PrintSuccessRate implements PositionsVisitor {
	
	
	private int counter;
	
	private NeuralNetwork network;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;	
	
	
	public DeepLearningVisitorImpl_NNUE_PrintSuccessRate() throws Exception {
		
		if ((new File(NNUE_Constants.NET_FILE)).exists()) {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NNUE_Constants.NET_FILE));
			
			network = (ConvolutionalNetwork) ois.readObject();
			
			ois.close();
			
			//NNUE_Constants.printWeights(network.getWeights());
			
		} else {
			
			throw new IllegalStateException();
		}
		
		
		/*for (int eval = IEvaluator.MIN_EVAL; eval <= IEvaluator.MAX_EVAL; eval++) {
			
			double win_prob = sigmoid_gety(eval);
			
			if (win_prob > 0.05 && win_prob < 0.95)  {
				System.out.println("eval=" + eval + ", win_prob=" + win_prob);	
			}
		}
		System.exit(0);*/
		
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		if (expectedWhitePlayerEval < IEvaluator.MIN_EVAL || expectedWhitePlayerEval > IEvaluator.MAX_EVAL) {
			
			throw new IllegalStateException("expectedWhitePlayerEval=" + expectedWhitePlayerEval);
		}
		
		
		float[][][] inputs_3d = new float[8][8][15];
		
		Tensor tensor = NNUE_Constants.createInput(bitboard, inputs_3d);
		
		network.setInput(tensor);
		
		//forward method is already called in setInput(tensor)
		//network.forward();
		
		float[] outputs = network.getOutput();
		
		double actualWhitePlayerEval = outputs[0];
		
		
		sumDiffs1 += Math.abs(0 - ActivationFunctions.sigmoid_gety(expectedWhitePlayerEval));
		sumDiffs2 += Math.abs(ActivationFunctions.sigmoid_gety(expectedWhitePlayerEval) - actualWhitePlayerEval);
        
        
		counter++;
		
		if ((counter % 10000) == 0) {
			
			System.out.println("Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, positions: " + counter);
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		
		sumDiffs1 = 0;
		
		sumDiffs2 = 0;
	}
	
	
	public void end() {
		
		System.out.println("END: positions=" + counter);
	}
}
