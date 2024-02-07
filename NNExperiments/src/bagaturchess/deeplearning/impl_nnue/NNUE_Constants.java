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
package bagaturchess.deeplearning.impl_nnue;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import deepnetts.util.Tensor;


public class NNUE_Constants {
	
	
	public static final String NET_FILE = "nnue.dn.bin";
	
	
	public static final void clear3DArray(float[][][] inputs_3d) {
		
	    for (int i = 0; i < inputs_3d.length; i++) {
	    	
	        for (int j = 0; j < inputs_3d[i].length; j++) {
	        	
	            for (int k = 0; k < inputs_3d[i][j].length; k++) {
	            	
	                inputs_3d[i][j][k] = 0.0f;
	            }
	        }
	    }	
	}
	
	
	public static final Tensor createInput(IBitBoard bitboard, float[][][] result_inputs_3d) {
		
		/*float[] inputs_1d = (float[]) bitboard.getNNUEInputs();
		
		Tensor tensor = new Tensor(inputs_1d.length, 1, inputs_1d);
		
		return tensor;*/
		
		
		float[] inputs = (float[]) bitboard.getNNUEInputs();
		
		for (int index = 0; index < inputs.length; index++) {
			
			int piece_type = index / 64;
			
			if (piece_type < 0 || piece_type > 11) {
				
				throw new IllegalStateException("piece_type=" + piece_type);
			}
			
			int sqare_id = index % 64;
			int file = sqare_id & 7;
			int rank = sqare_id >>> 3;
			
			result_inputs_3d[file][rank][piece_type] = inputs[index];
		}
		
		
		result_inputs_3d[0][0][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		result_inputs_3d[0][1][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		result_inputs_3d[0][2][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		result_inputs_3d[0][3][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		
		int moves_before_draw = bitboard.getDraw50movesRule() - 37;
		
		if (moves_before_draw >= 0) {
			
			int file = moves_before_draw & 7;
			int rank = moves_before_draw >>> 3;
			
			result_inputs_3d[file][rank][13] = 1;
		}
		
		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
			
			result_inputs_3d[0][0][14] = 1;
			result_inputs_3d[0][1][14] = 0;
			
		} else {
			
			result_inputs_3d[0][0][14] = 0;
			result_inputs_3d[0][1][14] = 1;
		}
		
		
		Tensor tensor = new Tensor(result_inputs_3d);
		
		return tensor;
	}
}
