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
package bagaturchess.selfplay.logic;


import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import bagaturchess.deeplearning.impl4_v20.NeuralNetworkUtils_AllFeatures;


public class SelfLearningImpl_Neuroph implements ISelfLearning {
	
	
	private MultiLayerPerceptron network;
	
	private double[] inputs;
	
	private long counter;
	private int batchSize = 100000;
	private DataSet trainingSetBatch;
	
	
	public SelfLearningImpl_Neuroph(double[] _inputs, MultiLayerPerceptron _network) {
		inputs = _inputs;
		network = _network;
		trainingSetBatch = new DataSet(NeuralNetworkUtils_AllFeatures.getInputsSize(), 1);
	}
	
	
	@Override
	public void addCase(double expectedWhitePlayerEval, double actualWhitePlayerEval) {
		
		counter++;
		
		trainingSetBatch.addRow(new DataSetRow(createCopy(inputs), new double[]{expectedWhitePlayerEval}));
		
		if (counter % batchSize == 0) {
		    network.getLearningRule().doLearningEpoch(trainingSetBatch);
			trainingSetBatch.clear();
		}
	}


	@Override
	public void endEpoch() {
		network.save("net.bin");
	}
	
	
	private double[] createCopy(double[] inputs) {
		double[] result = new double[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			result[i] = inputs[i];
		}
		return result;
	}
}
