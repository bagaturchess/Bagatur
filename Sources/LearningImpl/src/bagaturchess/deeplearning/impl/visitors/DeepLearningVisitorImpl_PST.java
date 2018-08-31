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
package bagaturchess.deeplearning.impl.visitors;


import java.io.File;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.deeplearning.api.NeuralNetworkUtils_PST;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class DeepLearningVisitorImpl_PST implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private static final String NET_FILE = "net.bin";
	private MultiLayerPerceptron network;
	
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;
	
	double[] inputs = new double[NeuralNetworkUtils_PST.getInputsSize()];
	
	
	public DeepLearningVisitorImpl_PST() throws Exception {
		
		if ((new File(NET_FILE)).exists() ){
			network = NeuralNetworkUtils_PST.loadNetwork(NET_FILE);
		} else {
			network = NeuralNetworkUtils_PST.buildNetwork();
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
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + status);
		}
		
		NeuralNetworkUtils_PST.clearInputsArray(inputs);
		NeuralNetworkUtils_PST.fillInputs(network, inputs, bitboard);
		NeuralNetworkUtils_PST.calculate(network);
		double actualWhitePlayerEval = NeuralNetworkUtils_PST.getOutput(network);
		
		/*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}*/
		
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		
		//network.addAdjustment(expectedWhitePlayerEval);
		
		DataSet trainingSet = new DataSet(NeuralNetworkUtils_PST.getInputsSize(), 1);
		NeuralNetworkUtils_PST.clearInputsArray(inputs);
		NeuralNetworkUtils_PST.fillInputs(inputs, bitboard);
        trainingSet.addRow(new DataSetRow(inputs, new double[]{expectedWhitePlayerEval}));
        network.learn(trainingSet);
        
        
		counter++;
		if ((counter % 100000) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
			
			network.save("net.bin");
			
			//System.out.println(counter);
			//for (int i=0; i < featuresArr.length; i++) {
				//IFeature currFeature = featuresArr[i];
				//System.out.println(currFeature);
			//}

		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		
		network.save("net.bin");
	}
}
