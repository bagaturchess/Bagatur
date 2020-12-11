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


import org.neuroph.nnet.MultiLayerPerceptron;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.impl4_v20.NeuralNetworkUtils_AllFeatures;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;


public class SelfLearningImpl_Neuroph implements ISelfLearning {


	private IBitBoard bitboard;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	private MultiLayerPerceptron network;
	
	private double[] inputs = new double[NeuralNetworkUtils_AllFeatures.getInputsSize()];
	
	
	public SelfLearningImpl_Neuroph(IBitBoard _bitboard, Bagatur_ALL_SignalFiller_InArray _filler, MultiLayerPerceptron _network) {
		bitboard = _bitboard;
		filler = _filler;
		network = _network;
	}
	

	@Override
	public void addCase(double deltaValueFromWhitePlayerPerspective) {
		if (deltaValueFromWhitePlayerPerspective != 0) {
			
		}
	}
	

	@Override
	public void endEpoch() {
		network.save("net.bin");
	}
}
