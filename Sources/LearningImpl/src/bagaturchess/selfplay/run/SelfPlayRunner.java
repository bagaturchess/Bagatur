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
package bagaturchess.selfplay.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluator;
import bagaturchess.learning.impl.features.advanced.FeaturesMerger;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.selfplay.GamesPlayer;


public class SelfPlayRunner {

	public static void main(String[] args) {
		
		try {
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			ISignalFiller filler = input.createFiller(bitboard);
			Features features = Features.load(input.getFeaturesConfigurationClassName(), new FeaturesMerger());
			ISignals signals = features.createSignals();
			
			IEvaluator evaluator = new FeaturesEvaluator(bitboard, null, filler, features, signals);
			
			GamesPlayer player = new GamesPlayer(bitboard, evaluator);
			player.playGames();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
