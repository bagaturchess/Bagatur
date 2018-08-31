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

import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;


public class NeuralNetworkUtils_AllFeatures {
	
	
	public static int getInputsSize() {
		return 2 * 47;
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
				//getInputsSize(),
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
	
	
	public static void calculate(MultiLayerPerceptron mlp) {
		mlp.calculate();
	}
	
	
	public static double getOutput(MultiLayerPerceptron mlp) {
		double[] networkOutput = mlp.getOutput();
		return networkOutput[0];
	}
}
