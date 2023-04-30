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
package bagaturchess.deeplearning.impl;


import java.util.Random;

import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_SignalFiller_InArray;


public class NeuralNetworkUtils_PST_And_AllFeatures {
	
	
	public static int getInputsSize() {
		return NeuralNetworkUtils_PST.getInputsSize() + NeuralNetworkUtils_AllFeatures.getInputsSize();
	}
	
	
	public static double[] createInputsArray() {
		return new double[getInputsSize()];
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
        
        mlp.getLearningRule().setLearningRate(0.00001);
        //mlp.getLearningRule().setLearningRate(0.0000001);
        
        return mlp;
	}
	
	
	public static void fillInputs(MultiLayerPerceptron mlp, double[] inputs, IBitBoard board, Bagatur_ALL_SignalFiller_InArray filler) {
		NeuralNetworkUtils.clearInputsArray(inputs);
		
		NeuralNetworkUtils_PST.fillInputs(mlp, inputs, board);
		filler.fillSignals(inputs, NeuralNetworkUtils_PST.getInputsSize());
		
		mlp.setInput(inputs);
	}
}
