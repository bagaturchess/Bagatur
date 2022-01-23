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
package bagaturchess.selfplay.train;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.impl4.eval.BagaturEvaluatorFactory_GOLDENMIDDLE;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_SignalFiller;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;


public class Trainer_GOLDENMIDDLE implements Trainer {
	
	
	//These members have to be recreated after each Epoch in order to read the last weights
	private ILearningInput input;
	private ISignalFiller filler;
	private Features_Splitter features_splitter;
	private IEvaluator evaluator;
	
	private IBitBoard bitboard;
	
	private String filename_NN;
	
	private List<ISignals> inputs_per_move;
	private List<Float> outputs_per_move_actual;
	private List<Float> outputs_per_move_expected;
	
	private ActivationFunction activation_function = ActivationFunction.LINEAR;
	
	
	public Trainer_GOLDENMIDDLE(IBitBoard _bitboard, String _filename_NN) throws Exception {
		
		bitboard = _bitboard;
		
		filename_NN = _filename_NN;
		
		if (!(new File(filename_NN)).exists()) {
			
			throw new IllegalStateException("NN file not found: " + filename_NN);
			
		}
		
		inputs_per_move = new ArrayList<ISignals>();
		
		outputs_per_move_actual = new ArrayList<Float>();
		
		outputs_per_move_expected = new ArrayList<Float>();
				
		reloadFromFile();
	}
	
	
	private void reloadFromFile() throws Exception {
		
		input = LearningInputFactory.createDefaultInput();
		
		filler = input.createFiller(bitboard);
		
		features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		evaluator = (new BagaturEvaluatorFactory_GOLDENMIDDLE()).create(bitboard, null, Bagatur_V20_SignalFiller.eval_config);
	}
	
	
	@Override
	public void clear() {
		
		inputs_per_move.clear();
		
		outputs_per_move_actual.clear();
		
		outputs_per_move_expected.clear();
	}
	
	
	@Override
	public void addBoardPosition(IBitBoard bitboard) {
		
		/*if (filler == null) {
			
			filler = new Bagatur_V20_SignalFiller(bitboard);
		}*/
		
		ISignals signals = new Signals(features_splitter.getFeatures(bitboard));
		
		filler.fill(signals);
		
		inputs_per_move.add(signals);
		
		double actual_eval = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
		
		if (bitboard.getColourToMove() == Constants.COLOUR_BLACK) {
			
			actual_eval = -actual_eval;
		}
		
		outputs_per_move_actual.add((float) actual_eval);
	}
	
	
	@Override
	public void setGameOutcome(float game_result) {
		
		float step;
		
		if (game_result == 0) { //Draw
			
			step = 0;
					
		} else if (game_result == 1) { //White wins
			
			//step = (activation_function.gety(IEvaluator.MAX_EVAL) / (float) inputs_per_move.size());
			step = (activation_function.gety(1000) / (float) inputs_per_move.size());
			
		} else { //Black wins
			
			//step = (activation_function.gety(IEvaluator.MIN_EVAL) / (float) inputs_per_move.size());
			step = (activation_function.gety(-1000) / (float) inputs_per_move.size());
		}
		
		for (int i = 0; i < inputs_per_move.size(); i++) {
	        
	        float output = i * step;
	        
	        outputs_per_move_expected.add(output);
		}
	}
	
	
	@Override
	public void doEpoch() throws Exception {		
		
		
		reloadFromFile();
		
		
		if (inputs_per_move.size() != outputs_per_move_actual.size()) {
			
			throw new IllegalStateException();
		}
		
		if (outputs_per_move_actual.size() != outputs_per_move_expected.size()) {
			
			throw new IllegalStateException();
		}
		
		
		for (int moveindex = 0; moveindex < inputs_per_move.size(); moveindex++) {
			
			ISignals signals = inputs_per_move.get(moveindex);
			
			float actualWhitePlayerEval = outputs_per_move_actual.get(moveindex);
			float expectedWhitePlayerEval = outputs_per_move_expected.get(moveindex);
		
			double deltaP = expectedWhitePlayerEval - actualWhitePlayerEval;
			
			IFeature[] features = features_splitter.getFeatures(null);
			
			if (deltaP != 0) {
				
				for (int i = 0; i < features.length; i++) {
					
					IFeature feature = features[i];
					
					if (feature != null) {
						
						int featureID = feature.getId();
							
						ISignal cur_signal = signals.getSignal(featureID);
						
						if (cur_signal.getStrength() != 0) {
							
							double adjustment = deltaP > 0 ? 1 : -1;
							
							((IAdjustableFeature) features[i]).adjust(cur_signal, adjustment, -1);
						}
					}
				}
			}
		}
		
		//Features_Splitter.updateWeights(features_splitter, false);
		Features_Splitter.updateWeights(features_splitter, true);
		
		Features_Splitter.store(filename_NN, features_splitter);
	}
}
