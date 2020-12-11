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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import bagaturchess.selfplay.logic.GamesPlayer;
import bagaturchess.selfplay.logic.ISelfLearning;
import bagaturchess.selfplay.logic.SelfLearningImpl_Own;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.impl.Channel_Console;


public class SelfPlayRunner_Own {
	
	
	public static void main(String[] args) {
		
		try {
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			ISignalFiller filler = input.createFiller(bitboard);
			Features features = Features.load(input.getFeaturesConfigurationClassName(), new FeaturesMerger());
			ISignals signals = features.createSignals();
			
			IEvaluator evaluator = new FeaturesEvaluator(bitboard, null, filler, features, signals);
			
			ISelfLearning learning = new SelfLearningImpl_Own(bitboard, features, signals);
			
			UCIEnginesManager runner = createEngineManager();
			
			GamesPlayer player = new GamesPlayer(bitboard, evaluator, runner, learning);
			
			player.playGames();
			
			runner.destroyEngines();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static UCIEnginesManager createEngineManager() throws IOException {
		
		ChannelManager.setChannel(new Channel_Console());
		
		UCIEnginesManager runner = new UCIEnginesManager();
		EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\stockfish-NNUE\\sf-nnue-bmi2.exe",
				new String [0],
				"C:\\DATA\\Engines\\stockfish-NNUE");
		runner.addEngine(engine);
		
		//Setup engine
		runner.startEngines();
		runner.uciOK();
		List<String> options = new ArrayList<String>();
		options.add("setoption name MultiPV value 500");
		runner.setOptions(options);
		runner.isReady();
		
		return runner;
	}
}
