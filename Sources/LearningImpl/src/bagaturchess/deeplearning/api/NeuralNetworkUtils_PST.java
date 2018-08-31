package bagaturchess.deeplearning.api;
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
import java.util.Arrays;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public class NeuralNetworkUtils_PST {
	
	
	public static int getInputsSize() {
		return 64 * 12 //board matrix
				+ 1 //colour to move
				+ 4 //castling
				+ 1 //repetition
				+ 1 //50 moves rule
				+ 1 //Moves count
				;
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
	}
	
	
	public static void clearInputsArray(double[] inputs) {
		for (int i=0; i<inputs.length; i++) {
			inputs[i] = 0;
		}
	}
	
	
	public static MultiLayerPerceptron loadNetwork(String fileName) {
		MultiLayerPerceptron net = (MultiLayerPerceptron) NeuralNetwork.createFromFile(fileName);
		
		return net;
	}
	
	
	public static MultiLayerPerceptron buildNetwork() {
		
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(TransferFunctionType.LINEAR,
				getInputsSize(),
				//64,
				1);
		mlp.randomizeWeights(new WeightsRandomizer(new Random(777)));
        
        //System.out.println(Arrays.toString(mlp.getWeights()));
        
        //mlp.setLearningRule(new BackPropagation());
        //mlp.setLearningRule(new ConvolutionalBackpropagation());
        mlp.setLearningRule(new MomentumBackpropagation());
        
        mlp.getLearningRule().setLearningRate(0.05);
        //mlp.getLearningRule().setLearningRate(0.00005);
        
        return mlp;
	}
	
	
	public static void fillInputs(double[] result, IBitBoard board) {
		
		int[] boardMatrix = board.getMatrix();
		
		for (int i=0; i<64; i++) {
			
			int pieceID = boardMatrix[i];
			
			int indexShift = -1;
			switch(pieceID) {
				case Constants.PID_W_PAWN:
					indexShift = 0;
					break;
				case Constants.PID_W_KNIGHT:
					indexShift = 1;
					break;
				case Constants.PID_W_BISHOP:
					indexShift = 2;
					break;
				case Constants.PID_W_ROOK:
					indexShift = 3;
					break;
				case Constants.PID_W_QUEEN:
					indexShift = 4;
					break;
				case Constants.PID_W_KING:
					indexShift = 5;
					break;
				case Constants.PID_B_PAWN:
					indexShift = 6;
					break;
				case Constants.PID_B_KNIGHT:
					indexShift = 7;
					break;
				case Constants.PID_B_BISHOP:
					indexShift = 8;
					break;
				case Constants.PID_B_ROOK:
					indexShift = 9;
					break;
				case Constants.PID_B_QUEEN:
					indexShift = 10;
					break;
				case Constants.PID_B_KING:
					indexShift = 11;
					break;
			}
			
			if (indexShift != -1) {//There is a piece
				result[i * 12 + indexShift] = 1;
			}
		}
		
		//Set color to move
		result[768] = board.getColourToMove() == Constants.COLOUR_WHITE ? 1 : 0;
		
		//Set castling
		result[769] = board.hasRightsToKingCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		result[770] = board.hasRightsToQueenCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		result[771] = board.hasRightsToKingCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		result[772] = board.hasRightsToQueenCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		
		//Set repetition
		result[773] = board.getStateRepetition();
		
		//Set 50 moves rule
		result[774] = board.getDraw50movesRule() / (double) 50; //Normalized in 0-1
		
		//Set moves count
		IMoveList movelist = new BaseMoveList();
		if (board.isInCheck()) {
			board.genKingEscapes(movelist);
		} else {
			board.genAllMoves(movelist);
		}
		result[775] = movelist.size() / (double) 100; //Normalized
	}
	
	
	public static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board) {
		fillInputs(inputs, board);
		mlp.setInput(inputs);
	}
	
	
	public static void calculate(MultiLayerPerceptron mlp) {
		mlp.calculate();
	}
	
	
	public static double getOutput(MultiLayerPerceptron mlp) {
		double[] networkOutput = mlp.getOutput();
		return networkOutput[0];
	}
}
