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
package bagaturchess.learning;


import java.io.File;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.deeplearning.impl1_v7.NeuralNetworkUtils_AllFeatures;
import bagaturchess.learning.goldmiddle.impl1.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.tools.pgn.api.IGameIterator;
import bagaturchess.tools.pgn.api.IPlyIterator;
import bagaturchess.tools.pgn.impl.PGNGame;



public class GameAndPlyIterator implements IGameIterator, IPlyIterator {
	
	
	private int counter = 0;
	private double currentGameResult;
	private int currentGameMovesCount;
	
	private static final String NET_FILE = "net.bin";
	private MultiLayerPerceptron network;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	double[] inputs;
	
	private double sumDiffs1;
	private double sumDiffs2;
	private long startTime;
	
	
	public GameAndPlyIterator() {
		
		if ((new File(NET_FILE)).exists() ){
			network = NeuralNetworkUtils.loadNetwork(NET_FILE);
		} else {
			network = NeuralNetworkUtils_AllFeatures.buildNetwork();
		}
		
        network.getLearningRule().addListener(new LearningEventListener() {
			
			@Override
			public void handleLearningEvent(LearningEvent event) {
		        BackPropagation bp = (BackPropagation)event.getSource();
		        
		        //if (event.getEventType() != LearningEvent.Type.LEARNING_STOPPED)
		            //System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
		        
		        bp.stopLearning();
			}
		});
        
        inputs = new double[NeuralNetworkUtils_AllFeatures.getInputsSize()];
	}
	
	
	@Override
	public void preIteration(IBoard bitboard) {
		
		filler = new Bagatur_ALL_SignalFiller_InArray((IBitBoard) bitboard);
		startTime = System.currentTimeMillis();
		counter = 0;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
	}
	
	
	@Override
	public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID, IBoard bitboard) {
		
		String result = pgnGame.getResult();
		
		if (result.equals("1-0")) {
			currentGameResult = 1000;
		} else if (result.equals("0-1")) {
			currentGameResult = -1000;
		} else if (result.equals("1/2-1/2")) {
			currentGameResult = 0;
		} else {
			currentGameResult = 12345;
		}
		
		currentGameMovesCount = 2 * pgnGame.getTurns().size();
		
		counter++;
		if (counter % 1000 == 0) {
			System.out.println("counter=" + counter + " : Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
			
			network.save("net.bin");
			
		}
	}
	
	
	@Override
	public void preMove(int colour, int move, IBoard bitboard, int moveNumber) {
		if (currentGameResult == 12345) {
			return;
		}
		
		bitboard.makeMoveForward(move);
		
		NeuralNetworkUtils.clearInputsArray(inputs);
		NeuralNetworkUtils_AllFeatures.fillInputs(network, inputs, (IBitBoard) bitboard, filler);
		NeuralNetworkUtils.calculate(network);
		double actualWhitePlayerEval = NeuralNetworkUtils.getOutput(network);
		
		/*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}*/
		
		sumDiffs1 += Math.abs(0 - currentGameResult);
		sumDiffs2 += Math.abs(currentGameResult - actualWhitePlayerEval);
		
		
		double adjustment = 1;//moveNumber / (double) currentGameMovesCount;
		
		DataSet trainingSet = new DataSet(NeuralNetworkUtils_AllFeatures.getInputsSize(), 1);
		NeuralNetworkUtils.clearInputsArray(inputs);
		NeuralNetworkUtils_AllFeatures.fillInputs(network, inputs, (IBitBoard) bitboard, filler);
        trainingSet.addRow(new DataSetRow(inputs, new double[]{adjustment * currentGameResult}));
        network.learn(trainingSet);
		
		bitboard.makeMoveBackward(move);
	}
	
	
	@Override
	public void postMove() {
		if (currentGameResult == 12345) {
			return;
		}
		
		//Do nothing
	}
	
	
	@Override
	public void postGame() {
		//Do nothing
	}
	
	
	@Override
	public void postIteration() {
		
		network.save("net.bin");
		
		System.out.println("counter=" + counter + " : Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
	}
}
