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


public class NeuralNetworkUtils_NNUE_Material {
	
	
	public static int getInputsSize() {
		return 12;
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
		fillInputs(inputs, board, Constants.COLOUR_WHITE, 0, 1);
		fillInputs(inputs, board, Constants.COLOUR_BLACK, 6, 1);
	}
	
	
	private static void fillInputs(double[] result, IBitBoard board, int colour, int shift, int sign) {
		
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
