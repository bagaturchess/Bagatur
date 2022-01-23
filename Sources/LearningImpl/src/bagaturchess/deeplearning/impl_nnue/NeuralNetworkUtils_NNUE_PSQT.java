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


import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;


public class NeuralNetworkUtils_NNUE_PSQT {
	
	
	public static NeuralNetwork buildNetwork() {
		
		/*int CHANNELS_COUNT = 15;
		
        ConvolutionalNetwork nnet =  ConvolutionalNetwork.builder()
                .addInputLayer(8, 8, CHANNELS_COUNT)
                .addConvolutionalLayer(3, 3, CHANNELS_COUNT, 1, ActivationType.RELU)
                //.addConvolutionalLayer(3, 3, CHANNELS_COUNT / 2, 1, ActivationType.RELU)
                .addFullyConnectedLayer(128, ActivationType.RELU)
                .addOutputLayer(1, ActivationType.SIGMOID)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(System.currentTimeMillis() % 778)
                .build();*/
        
		FeedForwardNetwork nnet =  FeedForwardNetwork.builder()
                .addInputLayer(8 * 8 * 15)
                .hiddenActivationFunction(ActivationType.RELU)
                .addOutputLayer(1, ActivationType.SIGMOID)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .randomSeed(System.currentTimeMillis() % 778)
                .build();
        
		//nnet.getTrainer().setLearningRate(0.001f);
        
        return nnet;
	}
}
