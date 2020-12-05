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
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class NeuralNetworkUtils_NNUE {
	
	
	public static int getInputsSize() {
		return 64 * 10 * 64;
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
	}
	
	
	public static MultiLayerPerceptron buildNetwork() {
		
		
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(TransferFunctionType.LINEAR,
				getInputsSize(),
				512,
				32,
				32,
				1);
		
		mlp.randomizeWeights(new WeightsRandomizer(new Random(777)));
        
        //mlp.setLearningRule(new BackPropagation());
        //mlp.setLearningRule(new ConvolutionalBackpropagation());
        mlp.setLearningRule(new MomentumBackpropagation());
        
        mlp.getLearningRule().setLearningRate(0.05);
        //mlp.getLearningRule().setLearningRate(0.00005);
        
        return mlp;
	}
	
	
	public static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board) {
		fillInputs(inputs, board);
		mlp.setInput(inputs);
	}
	
	
	public static void fillInputs(double[] inputs, IBitBoard board) {
		fillInputs_HalfKP(inputs, board, Constants.COLOUR_WHITE, 0);
		fillInputs_HalfKP(inputs, board, Constants.COLOUR_BLACK, 64 * 5 * 64);
	}
	
	
	private static void fillInputs_HalfKP(double[] result, IBitBoard board, int colour, int shift) {
		
		long bb_king = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KING);
		long bb_queens = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_QUEEN);
		long bb_rooks = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_ROOK);
		long bb_bishops = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_BISHOP);
		long bb_knights = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_KNIGHT);
		long bb_pawns = board.getFiguresBitboardByColourAndType(colour, Constants.TYPE_PAWN);
		
		int squareID_king = Long.numberOfTrailingZeros(bb_king);
		int shift_king = shift + squareID_king * 5 * 64;
		
    	int shift_pawns = 0 * 64;
        while (bb_pawns != 0) {
        	int squareID_pawn = Long.numberOfTrailingZeros(bb_pawns);
        	result[shift_king + shift_pawns + squareID_pawn] = 1;
        	bb_pawns &= bb_pawns - 1;
        }
        
    	int shift_knights = 1 * 64;
        while (bb_knights != 0) {
        	int squareID_knight = Long.numberOfTrailingZeros(bb_knights);
        	result[shift_king + shift_knights + squareID_knight] = 1;
        	bb_knights &= bb_knights - 1;
        }
        
    	int shift_bishops = 2 * 64;
        while (bb_bishops != 0) {
        	int squareID_bishop = Long.numberOfTrailingZeros(bb_bishops);
        	result[shift_king + shift_bishops + squareID_bishop] = 1;
        	bb_bishops &= bb_bishops - 1;
        }
        
      	int shift_rooks = 3 * 64;
        while (bb_rooks != 0) {
        	int squareID_rook = Long.numberOfTrailingZeros(bb_rooks);
        	result[shift_king + shift_rooks + squareID_rook] = 1;
        	bb_rooks &= bb_rooks - 1;
        }
        
      	int shift_queens = 4 * 64;
        while (bb_queens != 0) {
        	int squareID_queen = Long.numberOfTrailingZeros(bb_queens);
        	result[shift_king + shift_queens + squareID_queen] = 1;
        	bb_queens &= bb_queens - 1;
        }
	}
}
