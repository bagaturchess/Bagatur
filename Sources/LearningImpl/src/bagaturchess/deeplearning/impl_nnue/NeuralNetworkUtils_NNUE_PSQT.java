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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;


public class NeuralNetworkUtils_NNUE_PSQT {
	
	
	public static int getInputsSize() {
		return 12 * 64;
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
	}
	
	
	public static NeuralNetwork buildNetwork() {
		
        /*ConvolutionalNetwork nnet =  ConvolutionalNetwork.builder()
                .addInputLayer(8, 8, 12)
                //.addConvolutionalLayer(3, 3, 12, 1, ActivationType.RELU)
                //.addConvolutionalLayer(3, 3, 12, 1, ActivationType.RELU)
                //.addFullyConnectedLayer(8 * 8, ActivationType.SOFTMAX)
                .addOutputLayer(1, ActivationType.LINEAR)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(System.currentTimeMillis() % 778)
                .build();*/
        
		FeedForwardNetwork nnet =  FeedForwardNetwork.builder()
                .addInputLayer(8 * 8 * 12)
                //.addConvolutionalLayer(3, 3, 12, 1, ActivationType.RELU)
                //.addConvolutionalLayer(3, 3, 12, 1, ActivationType.RELU)
                .addFullyConnectedLayer(12, ActivationType.SOFTMAX)
                .addOutputLayer(1, ActivationType.LINEAR)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(System.currentTimeMillis() % 778)
                .build();
        
		nnet.getTrainer().setLearningRate(0.00001f);
        
        return nnet;
	}
	
	
	/*private static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board) {
		fillInputs(inputs, board);
		mlp.setInput(inputs);
	}
	
	
	private static void fillInputs(double[] inputs, IBitBoard board) {
		fillInputs(inputs, board, Constants.COLOUR_WHITE, 0, 1);
		fillInputs(inputs, board, Constants.COLOUR_BLACK, 6 * 64, 1);
	}
	
	
	private static void fillInputs(double[] result, IBitBoard board, int colour, int shift, int signal) {
		
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
	}*/
}
