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


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.IEvalConfig;


public class Trainer_GOLDENMIDDLE extends Trainer_Base {
	
	
	private boolean DUMP_FEATURES = true;
	
	
	private List<IFeature[]> features_per_move_for_update;
	
	private long stats_count_weights_changes;
	private long stats_sum_weights_changes;
	private long stats_sum_deltaP;
	
	//These members have to be recreated after each Epoch in order to read the last weights
	private ILearningInput input;
	private ISignalFiller filler;
	private Features_Splitter features_splitter;
	
	
	public Trainer_GOLDENMIDDLE(IBitBoard _bitboard, String _filename_NN, IEvalConfig evalConfig) throws Exception {
		
		super(_bitboard, _filename_NN, evalConfig, ActivationFunction.LINEAR, 0.991f);
		
		features_per_move_for_update 	= new ArrayList<IFeature[]>();
	}
	
	
	@Override
	protected void reloadFromFile() throws Exception {
		
		super.reloadFromFile();
		
		input = LearningInputFactory.createDefaultInput();
		
		filler = input.createFiller(bitboard);
		
		features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		if (DUMP_FEATURES) {
			
			System.out.println("Trainer_GOLDENMIDDLE.reloadFromFile: weights dump ...");
			
			Features_Splitter.dump(features_splitter);
		}
	}
	
	
	@Override
	public void newGame() {
		
		super.newGame();
		
		inputs_per_move.clear();
		
		features_per_move_for_update.clear();		
	}
	
	
	@Override
	public void addBoardPosition(IBitBoard bitboard) {
		
		
		IFeature[] features = features_splitter.getFeatures(bitboard);
		
		features_per_move_for_update.add(features);
		
		
		ISignals signals = new Signals(features);
		
		filler.fill(signals);
		
		inputs_per_move.add(signals);
		
		
		super.addBoardPosition(bitboard);
	}
	
	
	@Override
	public void backwardView() throws Exception {		
		
		
		for (int moveindex = 0; moveindex < inputs_per_move.size(); moveindex++) {
			
			
			float actualWhitePlayerEval 	= outputs_per_move_actual.get(moveindex);
			
			float expectedWhitePlayerEval 	= outputs_per_move_expected.get(moveindex);
			
			
			double deltaP = expectedWhitePlayerEval - actualWhitePlayerEval;
			
			if (deltaP != 0) {
				
				stats_sum_deltaP += Math.abs(deltaP);
				
				ISignals signals 				= (ISignals) inputs_per_move.get(moveindex);
				
				IFeature[] features 			= features_per_move_for_update.get(moveindex);
				
				for (int i = 0; i < features.length; i++) {
					
					IFeature feature = features[i];
					
					if (feature != null) {
						
						int featureID = feature.getId();
							
						ISignal cur_signal = signals.getSignal(featureID);
						
						if (cur_signal.getStrength() != 0) {
							
							double adjustment = deltaP > 0 ? 1 : -1;
							//double adjustment = deltaP;
							
							((IAdjustableFeature) features[i]).adjust(cur_signal, adjustment, -1);
							
							stats_count_weights_changes++;
							stats_sum_weights_changes += adjustment;
						}
					}
				}
			}
		}
		
		
		super.backwardView();
	}
	
	
	@Override
	public void updateWeights() throws Exception {
		
		
		System.out.println("Trainer_GOLDENMIDDLE.doEpoch[updating weights]: stats_sum_deltaP=" + stats_sum_deltaP + ", stats_count_weights_changes=" + stats_count_weights_changes + ", stats_sum_weights_changes=" + stats_sum_weights_changes);
		
		
		//Features_Splitter.updateWeights(features_splitter, false);
		Features_Splitter.updateWeights(features_splitter, true);
		
		Features_Splitter.store(filename_NN, features_splitter);
		
		
		reloadFromFile();
		
		
		stats_count_weights_changes = 0;
		stats_sum_weights_changes = 0;
		stats_sum_deltaP = 0;
		
		
		super.updateWeights();
	}
}
