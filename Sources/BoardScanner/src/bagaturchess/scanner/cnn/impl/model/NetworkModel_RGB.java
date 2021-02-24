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
package bagaturchess.scanner.cnn.impl.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.Tensor;


public class NetworkModel_RGB extends NetworkModel {
	
	
	public NetworkModel_RGB(InputStream networkFileStream, int squareSize) throws ClassNotFoundException, IOException {
		
		super();
		
		if (networkFileStream != null) {
			System.out.println("Loading network ...");
			ObjectInputStream ois = new ObjectInputStream(networkFileStream);
			network = (ConvolutionalNetwork) ois.readObject();
			System.out.println("Network loaded.");
		} else {
			System.out.println("Creating network ...");
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(squareSize, squareSize, 3)
	                .addConvolutionalLayer(5, 5, 64)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(5, 5, 16)
	                .addOutputLayer(14, ActivationType.SOFTMAX)
	                .hiddenActivationFunction(ActivationType.LINEAR)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(777)
	                .build();
			System.out.println("Network created.");	
		}
	}
	
	
	@Override
	public Object createInput(Object image) {
		return convertInt2Float((int[][][])image);
	}
	
	
	@Override
	public void setInputs(Object input) {
		network.setInput(new Tensor((float[][][])input));
	}
	
	
	private static float[][][] convertInt2Float(int[][][] array) {
		float[][][] result = new float[array.length][array.length][array[0][0].length];
		for (int i = 0 ; i < array.length; i++) {
			for (int j = 0 ; j < array[0].length; j++) {
				for (int k = 0 ; k < array[0][0].length; k++) {
					result[i][j][k] = array[i][j][k];
				}
			}
		}
		return result;
	}
}
