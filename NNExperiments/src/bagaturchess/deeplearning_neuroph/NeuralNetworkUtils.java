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
package bagaturchess.deeplearning_neuroph;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;


public class NeuralNetworkUtils {
	
	
	public static void clearInputsArray(double[] inputs) {
		for (int i=0; i<inputs.length; i++) {
			inputs[i] = 0;
		}
	}
	
	
	public static MultiLayerPerceptron loadNetwork(String fileName) {
		MultiLayerPerceptron net = (MultiLayerPerceptron) NeuralNetwork.createFromFile(fileName);
		
		return net;
	}
	
	
	public static void calculate(MultiLayerPerceptron mlp) {
		mlp.calculate();
	}
	
	
	public static double getOutput(MultiLayerPerceptron mlp) {
		double[] networkOutput = mlp.getOutput();
		return networkOutput[0];
	}
}
