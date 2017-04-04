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


import java.util.HashMap;
import java.util.Map;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.impl.cfg.allfeatures.ALL_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.Bagatur_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.base.FeaturesConfigurationBagaturImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.base.LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.base.SignalFiller;
import bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluator;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.learning.impl.signals.SignalArray;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class EvaluatorsComparatorVisitorImpl implements PositionsVisitor {
	
	private int counter;
	
	private IEvaluator evaluator2;
	private IEvaluator evaluator1;
	private ISignalFiller filler;
	private ISignals signals;
	private Features features;
	private IFeature[] featuresArr;
	
	private Map<Integer, IFeature> fs_all;
	//private Map<Integer, IFeature> fs_ok;
	
	
	public EvaluatorsComparatorVisitorImpl() throws Exception {
	}
	
	private int cur_signals_count = 1000;
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + status);
		}
		
		int factor = bitboard.getMaterialFactor().getTotalFactor();
		if (factor < 20 //endgame
			|| bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN).getDataSize() == 0
			|| bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN).getDataSize() == 0
			) {
			return;
		}
		
		double eval1 = evaluator1.fullEval(-1, 0, 0, -1);
		int movesBeforeDraw = 100 - bitboard.getDraw50movesRule();
		double percents = movesBeforeDraw / (double)100;
		double abs = Math.abs(eval1);
		abs = (int) ((abs + percents * abs) / (double)2);
		if (eval1 < 0) {
			abs = -abs;
		}
		eval1 = abs;
		
		double eval2 = evaluator2.fullEval(-1, 0, 0, -1);
		
		int delta = (int) Math.abs(eval1 - eval2);
		if (delta > 90) {
			System.out.println("counter: " + counter + ", diff: " + delta + ", feature eval: " + eval1 + ", fast eval: " + eval2);
			eval1 = evaluator1.fullEval(-1, 0, 0, -1);
			eval2 = evaluator2.fullEval(-1, 0, 0, -1);
			
			int signals_count = 0;
			signals.clear();
			filler.fill(signals);
			for (int i=0; i<featuresArr.length; i++) {
				int id = featuresArr[i].getId();
				ISignal cur_signal = signals.getSignal(id);
				if (!(cur_signal instanceof SignalArray)) {
					double strength = cur_signal.getStrength();
					if (Math.abs(strength) != 0) {
						signals_count++;
					}
				}
			}
			if (signals_count < cur_signals_count) {
				cur_signals_count = signals_count;
				for (int i=0; i<featuresArr.length; i++) {
					int id = featuresArr[i].getId();
					ISignal cur_signal = signals.getSignal(id);
					if (!(cur_signal instanceof SignalArray)) {
						double strength = cur_signal.getStrength();
						if (Math.abs(strength) != 0) {
							System.out.println("strength: " + strength + ", " + featuresArr[i].getName());
						}
					}
				}
				System.out.println("");
				System.out.println("");
			}
			
			
			/*for (Integer f: fs_all.keySet()) {
				System.out.println(fs_all.get(f));
			}
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");*/
		}
		
		if (delta == 0) {
			signals.clear();
			filler.fill(signals);
			for (int i=0; i<featuresArr.length; i++) {
				int id = featuresArr[i].getId();
				ISignal cur_signal = signals.getSignal(id);
				if (!(cur_signal instanceof SignalArray)) {
					double strength = cur_signal.getStrength();
					if (Math.abs(strength) > 3) {
						fs_all.remove(id);
					}
				}
			}
		}
		
		counter++;
		
		if ((counter % 1000000) == 0) {
			//System.out.println(counter);
			for (int i=0; i < featuresArr.length; i++) {
				//IFeature currFeature = featuresArr[i];
				//System.out.println(currFeature);
			}
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		ILearningInput input = new ALL_LearningInputImpl();
		
		filler = input.createFiller(bitboard);
		
		//features = Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName());
		//if (PERSISTENT) { 
			Features features_p = Features.load(input.getFeaturesConfigurationClassName(), null);
			//if (features_p != null) {
				features = features_p;
		
		signals = features.createSignals();
		
		featuresArr = features.getFeatures();
		
		fs_all = new HashMap<Integer, IFeature>();
		//fs_ok = new HashMap<Integer, IFeature>();
		
		for (int i=0; i < featuresArr.length; i++) {
			IFeature currFeature = featuresArr[i];
			fs_all.put(currFeature.getId(), currFeature);
		}
		
		evaluator1 = new FeaturesEvaluator(bitboard, null, filler, features, signals);
		evaluator2 = null;//new FastEvaluator(bitboard, null, null);
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		/*System.out.println("Iteration " + iteration + ". Success percent before this iteration: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		for (int i=0; i < featuresArr.length; i++) {
			IFeature currFeature = featuresArr[i];
			((IAdjustableFeature)currFeature).applyChanges();
			System.out.println(currFeature);
			((IAdjustableFeature)currFeature).clear();
		}
		*/
	}
}
