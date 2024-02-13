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
package bagaturchess.deeplearning_deepnetts.impl_nnue.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning_deepnetts.DeepLearningVisitorImpl_Train;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_Train_FeedForward extends DeepLearningVisitorImpl_Train {
	
	
	public DeepLearningVisitorImpl_Train_FeedForward(FeedForwardNetwork _network, ActivationFunction _output_activation_function) throws Exception {
		
		super(_network, _output_activation_function, 769);
	}
	
	
	@Override
	protected Tensor createNNInput(IBitBoard bitboard) {
		float[] nnue_input = (float[])bitboard.getNNUEInputs();
		//System.out.println("nnue_input=" + nnue_input.length);
		float[] inputs_1d = new float[nnue_input.length];
		System.arraycopy((float[])bitboard.getNNUEInputs(), 0, inputs_1d, 0, nnue_input.length);
		return new Tensor(inputs_1d);
	}
}
