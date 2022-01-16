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
		
		
		Tensor tensor = createInput(bitboard);
		
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
	
	
	private static final Tensor createInput(IBitBoard bitboard) {
		
		/*float[] inputs_1d = (float[]) bitboard.getNNUEInputs();
		
		Tensor tensor = new Tensor(inputs_1d.length, 1, inputs_1d);
		
		return tensor;*/
		
		
		float[] inputs = (float[]) bitboard.getNNUEInputs();
		
		float[][][] inputs_3d = new float[8][8][15];
		
		for (int index = 0; index < inputs.length; index++) {
			
			int piece_type = index / 64;
			
			if (piece_type < 0 || piece_type > 11) {
				
				throw new IllegalStateException("piece_type=" + piece_type);
			}
			
			int sqare_id = index % 64;
			int file = sqare_id & 7;
			int rank = sqare_id >>> 3;
			
			inputs_3d[file][rank][piece_type] = inputs[index];
		}
		
		
		inputs_3d[0][0][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		inputs_3d[0][1][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		inputs_3d[0][2][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		inputs_3d[0][3][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		
		int moves_before_draw = bitboard.getDraw50movesRule() - 37;
		
		if (moves_before_draw >= 0) {
			
			int file = moves_before_draw & 7;
			int rank = moves_before_draw >>> 3;
			
			inputs_3d[file][rank][13] = 1;
		}
		
		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
			
			inputs_3d[0][0][14] = 1;
			inputs_3d[0][1][14] = 0;
			
		} else {
			
			inputs_3d[0][0][14] = 0;
			inputs_3d[0][1][14] = 1;
		}
		
		
		Tensor tensor = new Tensor(inputs_3d);
		
		return tensor;
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
