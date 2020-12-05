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


import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.LayerFactory;
import org.neuroph.util.TransferFunctionType;


public class NeuralNetworkNNUE extends NeuralNetwork {
	
	public NeuralNetworkNNUE() {
		Layer inputLayer = LayerFactory.createLayer(64 * 5 * 64, TransferFunctionType.LINEAR);
		Layer outputLayer = LayerFactory.createLayer(256, TransferFunctionType.LINEAR);
		ConnectionFactory.fullConnect(inputLayer, outputLayer);
		
	}
}
