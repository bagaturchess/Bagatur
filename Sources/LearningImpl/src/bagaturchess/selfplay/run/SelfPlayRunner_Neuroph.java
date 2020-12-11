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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neuroph.nnet.MultiLayerPerceptron;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.deeplearning.impl4_v20.NeuralNetworkUtils_AllFeatures;
import bagaturchess.deeplearning.impl4_v20.eval.NeuralNetworkEvaluator;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.selfplay.logic.GamesPlayer;
import bagaturchess.selfplay.logic.ISelfLearning;
import bagaturchess.selfplay.logic.SelfLearningImpl_Neuroph;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.impl.Channel_Console;


public class SelfPlayRunner_Neuroph {
	
	
	public static void main(String[] args) {
		
		try {
			
			MultiLayerPerceptron network;
			if ((new File("net.bin")).exists() ){
				network = NeuralNetworkUtils.loadNetwork("net.bin");
			} else {
				network = NeuralNetworkUtils_AllFeatures.buildNetwork();
			}
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			IEvaluator evaluator = new NeuralNetworkEvaluator(bitboard, null, null, network);
			
			Bagatur_ALL_SignalFiller_InArray filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
			
			ISelfLearning learning = new SelfLearningImpl_Neuroph(bitboard, filler, network);
			
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
