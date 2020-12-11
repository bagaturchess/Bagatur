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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.baseimpl.Features;


public class SelfLearningImpl_Own implements ISelfLearning {


	private IBitBoard bitboard;
	private Features features;
	private ISignals signals;
	
	
	public SelfLearningImpl_Own(IBitBoard _bitboard, Features _features, ISignals _signals) {
		bitboard = _bitboard;
		features = _features;
		signals = _signals;
	}
	

	@Override
	public void addCase(double expectedWhitePlayerEval, double actualWhitePlayerEval) {
		double deltaValueFromWhitePlayerPerspective = expectedWhitePlayerEval - actualWhitePlayerEval;
		if (deltaValueFromWhitePlayerPerspective != 0) {
			for (int i=0; i<features.getFeatures().length; i++) {
				int featureID = features.getFeatures()[i].getId();
				ISignal cur_signal = signals.getSignal(featureID);
				if (cur_signal.getStrength() != 0) {
					double adjustment = deltaValueFromWhitePlayerPerspective > 0 ? 1 : -1;
					((IAdjustableFeature)features.getFeatures()[i]).adjust(cur_signal, adjustment, bitboard.getMaterialFactor().getOpenningPart());
				}
			}
		}
	}


	@Override
	public void endEpoch() {
		for (int i=0; i < features.getFeatures().length; i++) {
			IFeature currFeature = features.getFeatures()[i];
			((IAdjustableFeature)currFeature).applyChanges();
			((IAdjustableFeature)currFeature).clear();
		}
		features.store();
	}
}
