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
package bagaturchess.learning.goldmiddle.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class LearningVisitorImpl implements PositionsVisitor {
	
	
	boolean PERSISTENT = true;
	
	
	private int iteration = 0;
	
	private int counter;
	
	private ISignalFiller filler;
	
	private Features_Splitter features_splitter;
	
	private IEvaluator evaluator;
	
	private double sumDiffs1;
	
	private double sumDiffs2;
	
	private long startTime;
	
	private FeaturesFilter filter;
	
	private IBitBoard bitboard;
	
	private IEvalConfig evalConfig;
	
	
	public LearningVisitorImpl(IEvalConfig _evalConfig) throws Exception {
		
		this(new FeaturesFilter() {
			
			@Override
			public boolean isAdjustable(int featureID) {
				
				return true;
			}
		});
		
		evalConfig = _evalConfig;
	}
	
	
	private LearningVisitorImpl(FeaturesFilter _filter) throws Exception {
		
		filter = _filter;
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		if (bitboard.getStatus() != IGameStatus.NONE) {
			
			//throw new IllegalStateException("bitboard.getStatus()=" + bitboard.getStatus());
			return;
		}
		
		double actualWhitePlayerEval = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
		
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		newAdjustment(actualWhitePlayerEval, expectedWhitePlayerEval, openingPart);
		
		
		counter++;
		
		if ((counter % 100000) == 0) {
			//System.out.println(counter);
		}
	}
	
	
	public void newAdjustment(double actualWhitePlayerEval, double expectedWhitePlayerEval, double openingPart) {
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		
		double deltaP = expectedWhitePlayerEval - actualWhitePlayerEval;
		//double deltaP = actualWhitePlayerEval - expectedWhitePlayerEval;
		
		IFeature[] features = features_splitter.getFeatures(bitboard);
		
		if (deltaP != 0) {
			
			ISignals signals = new Signals(features);
			
			filler.fill(signals);
			
			for (int i = 0; i < features.length; i++) {
				
				IFeature feature = features[i];
				
				if (feature != null /*&& feature.getComplexity() == IFeatureComplexity.GROUP3*/) {
					
					int featureID = feature.getId();
					
					if (filter.isAdjustable(featureID)) {
						
						if (feature instanceof AdjustableFeatureArray) {
							
							ISignal cur_signal = signals.getSignal(featureID);
							
							double adjustment = deltaP > 0 ? 1 : -1;
								
							((IAdjustableFeature) features[i]).adjust(cur_signal, adjustment, -1);
							
						} else {
						
							ISignal cur_signal = signals.getSignal(featureID);
							
							if (cur_signal.getStrength() != 0) {
								
								double adjustment = deltaP > 0 ? 1 : -1;
								
								((IAdjustableFeature) features[i]).adjust(cur_signal, adjustment, -1);
							}
						}
					}
				}
			}
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		
		iteration++;
		
		sumDiffs1 = 0;
		
		sumDiffs2 = 0;
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
		
		this.bitboard = bitboard;
		
		filler = input.createFiller(bitboard);
		
		features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		String className = evalConfig.getEvaluatorFactoryClassName();
		
		IEvaluatorFactory evaluator_factory = (IEvaluatorFactory) SharedData.class.getClassLoader().loadClass(className).newInstance();
		
		evaluator = evaluator_factory.create(bitboard, null, evalConfig);
	}
	
	
	@Override
	public void end() throws Exception {
		
		System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success percent before this iteration: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		
		Features_Splitter.updateWeights(features_splitter, true);
		
		Features_Splitter.store(Features_Splitter.FEATURES_FILE_NAME, features_splitter);
	}
	
	
	public static interface FeaturesFilter {
		
		public boolean isAdjustable(int featureID);
	}
}
