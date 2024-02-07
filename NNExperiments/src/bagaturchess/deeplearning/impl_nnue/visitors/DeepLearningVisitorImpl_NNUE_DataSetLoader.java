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


import javax.visrec.ml.data.DataSet;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.data.MLDataItem;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE_DataSetLoader implements PositionsVisitor {
	
	
	private static final float DATASET_USAGE_PERCENT = 1f;
	
	
	private long startTime;	
	
	private int counter;
	
	private DataSet_1 dataset = new DataSet_1();
	
	
	public DeepLearningVisitorImpl_NNUE_DataSetLoader() throws Exception {
		
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		if (expectedWhitePlayerEval < IEvaluator.MIN_EVAL || expectedWhitePlayerEval > IEvaluator.MAX_EVAL) {
			
			throw new IllegalStateException("expectedWhitePlayerEval=" + expectedWhitePlayerEval);
		}
		
		
		if (Math.random() > DATASET_USAGE_PERCENT) {
			
			return;
		}
		
		
		float[][][] inputs_3d = new float[8][8][15];
		
		Tensor input = NNUE_Constants.createInput(bitboard, inputs_3d);
        
        float[] output = new float[1];
        output[0] = ActivationFunction.SIGMOID.gety(expectedWhitePlayerEval);
        
        dataset.addItem(input, output);
        
        
		counter++;
		
		if ((counter % 100000) == 0) {
			
			System.out.println("Time " + (System.currentTimeMillis() - startTime) + "ms, positions: " + counter);
			
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
	}
	
	
	public void end() {
		
		System.out.println("END: Time " + (System.currentTimeMillis() - startTime) + "ms, positions: " + counter);
	}
	
	
	public DataSet<MLDataItem> getDataSet() {
		
		return dataset;
	}
}
