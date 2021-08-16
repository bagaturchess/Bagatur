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
package bagaturchess.deeplearning_deepnetts.impl1.visitor;


import java.io.File;
import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.ucitracker.api.PositionsVisitor;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_AllFeatures implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private static final String NET_FILE = "net.dn.bin";
	private NeuralNetwork<?> network;
	
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	private double[] inputs_d;
	private float[] inputs_f;
	
	private BackpropagationTrainer trainer;
	private DataSetLearning dataset;
	
	
	public DeepLearningVisitorImpl_AllFeatures() throws Exception {
		
		if ((new File(NET_FILE)).exists() ){
			network = (FeedForwardNetwork) FileIO.createFromFile(new File(NET_FILE));
		} else {
			
			network =  FeedForwardNetwork.builder()
	                .addInputLayer(110)
	                .addOutputLayer(1, ActivationType.LINEAR)
	                .hiddenActivationFunction(ActivationType.LINEAR)
	                .lossFunction(LossType.MEAN_SQUARED_ERROR)
	                .randomSeed(777)
	                .build();
		}
        
		inputs_d = new double[110];
		inputs_f = new float[110];
		
		dataset = new DataSetLearning();
		
		trainer = new BackpropagationTrainer(network);
		
		trainer.setLearningRate(0.000001f);
        
        trainer.setBatchMode(true);
        trainer.setBatchSize(100000);
        
        trainer.addListener(new TrainingListener() {

				@Override
				public void handleEvent(TrainingEvent event) {
					if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
						event.getSource().stop();
					} else if (event.getType().equals(TrainingEvent.Type.ITERATION_FINISHED)) {
						//System.out.println("done");
					}
				}
        	}
        );
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + status);
		}
		
		for (int i = 0; i < inputs_d.length; i++) {
			inputs_d[i] = 0;
		}
		filler.fillSignals(inputs_d, 0);
		
		for (int i = 0; i < inputs_d.length; i++) {
			inputs_f[i] = (float) inputs_d[i];
		}
		
		network.setInput(new Tensor(inputs_f));
		network.forward();
		float actualWhitePlayerEval = network.getOutput()[0];
		
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		
		
		dataset.addItem(createCopy(inputs_f), new float[]{expectedWhitePlayerEval});
        
        
		counter++;
		if ((counter % 1000000) == 0) {
			
			//System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, Error: " + network.getLossFunction().getTotal());
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
	}
	
	
	public void end() {
		
		trainer.train(dataset);
		
		dataset.clear();
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, Error: " + network.getLossFunction().getTotal());
		
		try {
			FileIO.writeToFile(network, NET_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private float[] createCopy(float[] inputs) {
		float[] result = new float[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			result[i] = inputs[i];
		}
		return result;
	}
}
