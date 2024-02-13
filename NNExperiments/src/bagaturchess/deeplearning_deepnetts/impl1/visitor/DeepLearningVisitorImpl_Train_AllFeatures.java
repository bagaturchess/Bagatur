/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.deeplearning_deepnetts.impl1.visitor;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning_deepnetts.DeepLearningVisitorImpl_Train;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;

import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_Train_AllFeatures extends DeepLearningVisitorImpl_Train {
	
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	
	
	public DeepLearningVisitorImpl_Train_AllFeatures(NeuralNetwork<?> network, ActivationFunction output_activation_function) throws Exception {
		
		super(network, output_activation_function, 55);
	}
	
	
	@Override
	public void begin(IBitBoard bitboard) throws Exception {
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		super.begin(bitboard);
	}


	@Override
	protected Tensor createNNInput(IBitBoard bitboard) {
		float[] inputs = new float[55];
		filler.fillSignals(inputs, 0);
		return new Tensor(inputs);
	}
}
