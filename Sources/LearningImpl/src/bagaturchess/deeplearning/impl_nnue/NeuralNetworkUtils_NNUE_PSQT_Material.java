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


import java.util.Random;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class NeuralNetworkUtils_NNUE_PSQT_Material {
	
	
	public static int getInputsSize() {
		return 12 * 64 + 12;
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
	}
	
	
	public static MultiLayerPerceptron buildNetwork() {
		
		
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(TransferFunctionType.LINEAR,
				getInputsSize(),
				1);
		
		mlp.randomizeWeights(new WeightsRandomizer(new Random(777)));
        
        mlp.setLearningRule(new BackPropagation());
        //mlp.setLearningRule(new ConvolutionalBackpropagation());
        //mlp.setLearningRule(new MomentumBackpropagation());
        //mlp.setLearningRule(new DynamicBackPropagation());
        
        mlp.getLearningRule().setLearningRate(0.005);
        //mlp.getLearningRule().setLearningRate(0.0001);
        
        return mlp;
	}
	
	
	public static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board) {
		fillInputs(inputs, board);
		mlp.setInput(inputs);
	}
	
	
	public static void fillInputs(double[] inputs, IBitBoard board) {
		fillInputs_PSQT(inputs, board, Constants.COLOUR_WHITE, 0, 1);
		fillInputs_PSQT(inputs, board, Constants.COLOUR_BLACK, 6 * 64, 1);
		fillInputs_Material(inputs, board, Constants.COLOUR_WHITE, 12 * 64 + 0, 1);
		fillInputs_Material(inputs, board, Constants.COLOUR_BLACK, 12 * 64 + 6, 1);
	}
	
	
	private static void fillInputs_PSQT(double[] result, IBitBoard board, int colour, int shift, int signal) {
		
		long bb_king = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KING);
		long bb_queens = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_QUEEN);
		long bb_rooks = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_ROOK);
		long bb_bishops = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_BISHOP);
		long bb_knights = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KNIGHT);
		long bb_pawns = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_PAWN);
		
		{
			int shift_king = 0 * 64;
			int squareID_king = Long.numberOfTrailingZeros(bb_king);
			result[shift + shift_king + squareID_king] = signal;
		}
		
    	int shift_pawns = 1 * 64;
        while (bb_pawns != 0) {
        	int squareID_pawn = Long.numberOfTrailingZeros(bb_pawns);
        	result[shift + shift_pawns + squareID_pawn] = signal;
        	bb_pawns &= bb_pawns - 1;
        }
        
    	int shift_knights = 2 * 64;
        while (bb_knights != 0) {
        	int squareID_knight = Long.numberOfTrailingZeros(bb_knights);
        	result[shift + shift_knights + squareID_knight] = signal;
        	bb_knights &= bb_knights - 1;
        }
        
    	int shift_bishops = 3 * 64;
        while (bb_bishops != 0) {
        	int squareID_bishop = Long.numberOfTrailingZeros(bb_bishops);
        	result[shift + shift_bishops + squareID_bishop] = signal;
        	bb_bishops &= bb_bishops - 1;
        }
        
      	int shift_rooks = 4 * 64;
        while (bb_rooks != 0) {
        	int squareID_rook = Long.numberOfTrailingZeros(bb_rooks);
        	result[shift + shift_rooks + squareID_rook] = signal;
        	bb_rooks &= bb_rooks - 1;
        }
        
      	int shift_queens = 5 * 64;
        while (bb_queens != 0) {
        	int squareID_queen = Long.numberOfTrailingZeros(bb_queens);
        	result[shift + shift_queens + squareID_queen] = signal;
        	bb_queens &= bb_queens - 1;
        }
	}
	
	
	private static void fillInputs_Material(double[] result, IBitBoard board, int colour, int shift, int sign) {
		
		long bb_king = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KING);
		long bb_queens = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_QUEEN);
		long bb_rooks = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_ROOK);
		long bb_bishops = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_BISHOP);
		long bb_knights = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KNIGHT);
		long bb_pawns = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_PAWN);
		
		result[shift + 0] = sign * Long.bitCount(bb_pawns);
		result[shift + 1] = sign * Long.bitCount(bb_king);
		result[shift + 2] = sign * Long.bitCount(bb_knights);
		result[shift + 3] = sign * Long.bitCount(bb_bishops);
		result[shift + 4] = sign * Long.bitCount(bb_rooks);
		result[shift + 5] = sign * Long.bitCount(bb_queens);
	}
}
