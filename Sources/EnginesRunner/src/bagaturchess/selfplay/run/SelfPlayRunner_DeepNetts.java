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
import java.io.FileOutputStream;
import java.io.PrintStream;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVRootSearch;
import bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD;
import bagaturchess.selfplay.logic.GamesPlayer;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.Channel_Console;


public class SelfPlayRunner_DeepNetts {
	
	
	public static void main(String[] args) {
		
		try {
			
			//IRootSearchConfig cfg = RootSearchConfig_BaseImpl_1Core.NNUE;
			IRootSearchConfig cfg = RootSearchConfig_BaseImpl_1Core.NNUE_EVALIMPL4;
			//IRootSearchConfig cfg = RootSearchConfig_BaseImpl_1Core.EVALIMPL4;
			
			PrintStream log = new PrintStream(new FileOutputStream(new File("bagatur.log")));
			
			ChannelManager.setChannel(new Channel_Console(System.in, log, log));
		
			SharedData sharedData = new SharedData(ChannelManager.getChannel(), cfg);
		
			IRootSearch search = new SequentialSearch_MTD(new Object[] {cfg, sharedData});
			//IRootSearch searchMultiPV = new MultiPVRootSearch(cfg, search);
			
			sharedData = search.getSharedData();
			
			
			/*MultiPVMediator multipvMediator = new MultiPVMediator(cfg, search, bitboard, mediator1, go);
			multipvMediator.ready();*/
			
			
			/*MultiLayerPerceptron network;
			if ((new File("net.bin")).exists() ){
				network = NeuralNetworkUtils.loadNetwork("net.bin");
			} else {
				network = NeuralNetworkUtils_AllFeatures.buildNetwork();
			}
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			NeuralNetworkEvaluator evaluator = new NeuralNetworkEvaluator(bitboard, null, null, network);*/
			
			//UCIEnginesManager runner = createEngineManager();
			
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD, cfg.getBoardConfig());
			
			GamesPlayer player = new GamesPlayer(bitboard, search);
			
			player.playGames();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
