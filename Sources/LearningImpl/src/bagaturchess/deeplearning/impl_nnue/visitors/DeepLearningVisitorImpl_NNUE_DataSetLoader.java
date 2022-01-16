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
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.data.MLDataItem;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE_DataSetLoader implements PositionsVisitor {
	
	
	private static final float DATASET_USAGE_PERCENT = 0.33f;
	
	
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
		
		
		Tensor input = createInput(bitboard);
        
        float[] output = new float[1];
        output[0] = ActivationFunctions.sigmoid_gety(expectedWhitePlayerEval);
        
        dataset.addItem(input, output);
        
        
		counter++;
		
		if ((counter % 100000) == 0) {
			
			System.out.println("Time " + (System.currentTimeMillis() - startTime) + "ms, positions: " + counter);
			
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
	}
	
	
	public void end() {
		
		System.out.println("END: Time " + (System.currentTimeMillis() - startTime) + "ms, positions: " + counter);
	}
	
	
	public DataSet<MLDataItem> getDataSet() {
		
		return dataset;
	}
}
