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
package bagaturchess.deeplearning_deepnetts.impl_nnue.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning_deepnetts.impl_nnue.NNUE_Constants;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE_Train_Convolutional implements PositionsVisitor {
	
	
	private static int CHUNK_SIZE = 10000;
	
	
	private long startTime;	
	
	private int counter_positions;
	
	private int counter_chunks;
	
	private DataSet_1 dataset;
	
	private ConvolutionalNetwork network;
	
	
	public DeepLearningVisitorImpl_NNUE_Train_Convolutional(ConvolutionalNetwork _network) throws Exception {
		
		dataset = new DataSet_1();
		
		network = _network;
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
		Tensor input = NNUE_Constants.createInput(bitboard, inputs_3d);
        
        float[] output = new float[1];
        output[0] = ActivationFunction.SIGMOID.gety(expectedWhitePlayerEval);
        
        dataset.addItem(input, output);
        
		counter_positions++;
		
		if ((counter_positions % 100000) == 0) {
			
			//System.out.println("Time " + (System.currentTimeMillis() - startTime) + "ms, positions: " + counter_positions);
			
		}
		
		
		if (counter_positions % CHUNK_SIZE == 0) {
			
			counter_chunks++;
			
			network.getTrainer().train(dataset);
			
			//System.out.println("Chunk: " + counter_chunks + ", Time: " + (System.currentTimeMillis() - startTime) + "ms, Positions: " + counter_positions);
			
			dataset = new DataSet_1();
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter_positions = 0;
		
		counter_chunks = 0;
	}
	
	
	public void end() {
		
		System.out.println("Training END: Time " + (System.currentTimeMillis() - startTime) + "ms, Positions: " + counter_positions);
	}
}
