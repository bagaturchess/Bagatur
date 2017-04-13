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
package bagaturchess.learning.goldmiddle.impl.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.Bagatur_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.Bagatur_ALL_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.ALL_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.old0.FeaturesConfigurationBagaturImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.old0.LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.old0.SignalFiller;
import bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluator;
import bagaturchess.learning.impl.features.advanced.FeaturesMerger;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class LearningVisitorImpl implements PositionsVisitor {
	
	
	boolean PERSISTENT = true;
	
	
	private int iteration = 0;
	
	private int counter;
	
	private IEvaluator evaluator;
	private ISignalFiller filler;
	private ISignals signals;
	private Features features;
	private IFeature[] featuresArr;
	
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;
	
	
	public LearningVisitorImpl() throws Exception {
	}
	
	
	public void newAdjustment(double actualWhitePlayerEval, double expectedWhitePlayerEval, double openingPart) {
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		
		double deltaP = expectedWhitePlayerEval - actualWhitePlayerEval;
		//double deltaP = actualWhitePlayerEval - expectedWhitePlayerEval;
		
		for (int i=0; i<featuresArr.length; i++) {
			ISignal cur_signal = signals.getSignal(featuresArr[i].getId());
			
			if (deltaP != 0) {
				//if (cur_signal instanceof SingleSignal) {
					((IAdjustableFeature)featuresArr[i]).adjust(cur_signal, deltaP > 0 ? 1 : -1, openingPart);
				/*} else if (cur_signal instanceof SignalArray) {
					SignalArray signalarray = (SignalArray)cur_signal;
					
					int count = signalarray.getSubsignalsCount();
					int[] ids = signalarray.getSubIDs();
					double[] strengths = signalarray.getSubsignals();
					
					for(int s=0; s<count; s++) {
						
						int id = ids[s];
						
						if (cur_signal.getStrength() > 0) {
							featuresArr[i].adjust(cur_signal, deltaP > 0 ? 1 : -1, openingPart);
						} else if (cur_signal.getStrength() < 0) {
							featuresArr[i].adjust(cur_signal, deltaP > 0 ? -1 : 1, openingPart);
						}
					}
				}*/

			}
		}
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + status);
		}
		
		//signals.clear();
		//filler.fill(signals);
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		/*double actualWhitePlayerEval = 0;
		for (int i=0; i < featuresArr.length; i++) {
			IFeature currFeature = featuresArr[i];
			ISignal currSignal = signals.getSignal(currFeature.getId());
			actualWhitePlayerEval += currFeature.eval(currSignal, openingPart);
		}*/
		
		double actualWhitePlayerEval = evaluator.fullEval(-1, 0, 0, -1);
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		/*if (eval != actualWhitePlayerEval) {
			System.out.println("eval=" + eval + ", actualWhitePlayerEval=" + actualWhitePlayerEval);
		}*/
		
		/*double actualWhitePlayerEval = actualEval;
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			actualWhitePlayerEval = -actualEval;
		}*/
		
		newAdjustment(actualWhitePlayerEval, expectedWhitePlayerEval, openingPart);
		
		counter++;
		
		if ((counter % 1000000) == 0) {
			//System.out.println(counter);
			for (int i=0; i < featuresArr.length; i++) {
				IFeature currFeature = featuresArr[i];
				//System.out.println(currFeature);
			}
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
		
		ILearningInput input = new Bagatur_ALL_LearningInputImpl();
		
		filler = input.createFiller(bitboard);
		
		//features = Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName());
		//if (PERSISTENT) { 
			Features features_p = Features.load(input.getFeaturesConfigurationClassName(), new FeaturesMerger());
			//if (features_p != null) {
				features = features_p;
			//}
		//}
		signals = features.createSignals();
		
		featuresArr = features.getFeatures();
		
		/*for (int i=0; i < featuresArr.length; i++) {
			IFeature currFeature = featuresArr[i];
			ISignal currSignal = signals.getSignal(currFeature.getId());
			System.out.println(currSignal);
		}*/
		
		evaluator = new FeaturesEvaluator(bitboard, null, filler, features, signals);
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success percent before this iteration: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		for (int i=0; i < featuresArr.length; i++) {
			IFeature currFeature = featuresArr[i];
			((IAdjustableFeature)currFeature).applyChanges();
			//System.out.println(currFeature);
			((IAdjustableFeature)currFeature).clear();
		}
		
		if (PERSISTENT) features.store();
	}
}
