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
package bagaturchess.deeplearning.impl4_v20;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.neuroph.core.input.WeightedSum;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ManhattanPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;


public class NeuralNetworkUtils_AllFeatures {
	
	
	public static int getInputsSize() {
		return 2 * 55;
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
	}
	
	
	public static MultiLayerPerceptron buildNetwork() {
		
        // init neuron settings
        NeuronProperties neuronProperties = new NeuronProperties();
        neuronProperties.setProperty("useBias", false);
        neuronProperties.setProperty("transferFunction", TransferFunctionType.LINEAR);
        neuronProperties.setProperty("inputFunction", WeightedSum.class);

        int[] neuronsInLayers = new int[] {getInputsSize(), 1};
        List<Integer> neuronsInLayersVector = new ArrayList<Integer>();
        for (int i = 0; i < neuronsInLayers.length; i++) {
            neuronsInLayersVector.add(Integer.valueOf(neuronsInLayers[i]));
        }
        
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(neuronsInLayersVector, neuronProperties);
		
		//mlp.randomizeWeights(new WeightsRandomizer(new Random(777)));
		
		BackPropagation backprop = new BackPropagation();
		//backprop.setBatchMode(true);
		backprop.setLearningRate(0.00000001);
		
		mlp.setLearningRule(backprop);
        
        return mlp;
	}
	
	
	public static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board, Bagatur_ALL_SignalFiller_InArray filler) {
		NeuralNetworkUtils.clearInputsArray(inputs);
		
		filler.fillSignals(inputs, 0);
		
		mlp.setInput(inputs);
	}
}
