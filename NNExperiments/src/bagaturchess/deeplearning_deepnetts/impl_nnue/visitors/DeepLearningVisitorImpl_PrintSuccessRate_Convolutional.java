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
import bagaturchess.deeplearning_deepnetts.DeepLearningVisitorImpl_PrintSuccessRate;
import bagaturchess.deeplearning_deepnetts.impl_nnue.NNUE_Constants;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_PrintSuccessRate_Convolutional extends DeepLearningVisitorImpl_PrintSuccessRate {
	

	public DeepLearningVisitorImpl_PrintSuccessRate_Convolutional(ActivationFunction output_activation_function) throws Exception {
		
		super(output_activation_function);		
	}
	
	
	@Override
	protected float getNNOutput(IBitBoard bitboard) {
		
		float[][][] inputs_3d = new float[8][8][15];
		
		Tensor tensor = NNUE_Constants.createInput(bitboard, inputs_3d);
		
		network.setInput(tensor);
		
		//forward method is already called in setInput(tensor)
		network.forward();
		
		return network.getOutput()[0];
	}


	@Override
	protected String getNNFileName() {
		
		return NNUE_Constants.NET_FILE;
	}
}
