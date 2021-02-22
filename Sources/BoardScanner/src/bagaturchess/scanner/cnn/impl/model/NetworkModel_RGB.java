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


import java.awt.image.BufferedImage;
import java.io.IOException;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.Tensor;


public class NetworkModel_RGB extends NetworkModel {
	
	
	public NetworkModel_RGB(String networkFilePath, BoardProperties boardProperties) throws ClassNotFoundException, IOException {
		
		super(networkFilePath, boardProperties);
		
		if (network == null) {
			System.out.println("Creating network ...");
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(boardProperties.getSquareSize(), boardProperties.getSquareSize(), 3)
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
		return ScannerUtils.convertInt2Float((int[][][])image);
	}
	
	
	@Override
	public void setInputs(Object input) {
		network.setInput(new Tensor((float[][][])input));
	}
	
	
	@Override
	public DataSetInitPair createDataSetInitPair(BufferedImage boardImage) {
		return new DataSetInitPair_ByBoardImage_RGB(boardImage);
	}
}
