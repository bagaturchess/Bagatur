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
package bagaturchess.montecarlo;


import java.util.Map;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl2;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.rootsearch.montecarlo.GamesResult;
import bagaturchess.search.impl.rootsearch.montecarlo.IMonteCarloListener;
import bagaturchess.search.impl.rootsearch.montecarlo.MonteCarlo;


public class MonteCarloMain {
	
	
	public static void main(String[] args) {
		
		
		String fen = Constants.INITIAL_BOARD;
		//String fen = "5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - bm Qxf7+ Rxf7+; id WAC.235";
		//String fen = "8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2";
		//String fen = "2r1n2r/1q4k1/2p1pn2/ppR4p/4PNbP/P1BBQ3/1P4P1/R5K1 b - - 1 32";
		
		IBoardConfig boardConfig = new bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_BoardConfigImpl();
		String pawnsCacheFactoryClassName = bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval.BagaturPawnsEvalFactory.class.getName();
		final IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen, pawnsCacheFactoryClassName, boardConfig, 10000);
		
		IEvalCache evalCache = new EvalCache_Impl2(64);
		IEvaluatorFactory evalFactory = new bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval.BagaturEvaluatorFactory();
		IEvaluator evaluator = evalFactory.create(bitboard, evalCache);
		
		
		MonteCarlo monteCarlo = new MonteCarlo(bitboard, evaluator);
		
		monteCarlo.play_iterations(100, null, new IMonteCarloListener() {
			
			@Override
			public void newData(Map<Integer, GamesResult> global_map, int gamesCount) {
				
				int best_move = 0;
				double best_rate = Integer.MIN_VALUE;
				for (Integer cur_move: global_map.keySet()) {
					if (best_rate < global_map.get(cur_move).getRate()) {
						best_rate = global_map.get(cur_move).getRate();
						best_move = cur_move;
					}
				}
				
				System.out.println(gamesCount + " > " + bitboard.getMoveOps().moveToString(best_move) + " " + best_rate);
			}
		});
	}
}
