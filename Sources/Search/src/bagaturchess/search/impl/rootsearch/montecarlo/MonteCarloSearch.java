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
package bagaturchess.search.impl.rootsearch.montecarlo;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl1;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl2;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MonteCarloSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	private MonteCarlo monteCarlo;
	
	
	public MonteCarloSearch(Object[] args) {
		super(args);
		
		executor = Executors.newFixedThreadPool(1);
	}
	
	
	@Override
	public void negamax(IBitBoard bitboardForSetup, final ISearchMediator mediator,
			ITimeController timeController, final IFinishCallback finishCallback,
			Go go) {
		
		if (stopper != null) {
			throw new IllegalStateException("MTDSequentialSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		setupBoard(bitboardForSetup);
		
		IEvaluator evaluator = getSharedData().getEvaluatorFactory().create(
				getBitboardForSetup(),
				//new EvalCache_Impl1(100, true, new BinarySemaphore_Dummy()),
				new EvalCache_Impl2(2),
				getRootSearchConfig().getEvalConfig());
		
		monteCarlo = new MonteCarlo(getBitboardForSetup(), evaluator);
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					monteCarlo.play_iterations(100, mediator.getStopper(), new IMonteCarloListener() {
						
						@Override
						public void newData(Map<Integer, GamesResult> global_map, int gamesCount) {
							ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
							
							int best_move = 0;
							double best_rate = Integer.MIN_VALUE;
							for (Integer cur_move: global_map.keySet()) {
								if (best_rate < global_map.get(cur_move).getRate()) {
									best_rate = global_map.get(cur_move).getRate();
									best_move = cur_move;
									//System.out.println(gamesCount + " > " + MoveInt.moveToString(best_move) + " " + best_rate);
								}
							}
							
							info.setEval((int) (best_rate * 1000));
							info.setDepth(gamesCount);
							info.setSelDepth(gamesCount);
							info.setBestMove(best_move);
							info.setPV(new int[] {best_move});
							
							mediator.changedMajor(info);
							
						}
					});
				} catch (SearchInterruptedException sie) {
					//Do nothing
				}
				
				if (!isStopped()) {
					
					ChannelManager.getChannel().dump("MTDSequentialSearch not stopped - stopping searcher ...");
					
					if (stopper == null) {
						throw new IllegalStateException();
					}
					stopper.markStopped();
					stopper = null;
					
					if (finishCallback == null) {//Non multiPV search
						ChannelManager.getChannel().dump("MTDSequentialSearch calling final_mediator.getBestMoveSender().sendBestMove()");
						mediator.getBestMoveSender().sendBestMove();
					} else {
						//MultiPV search
						finishCallback.ready();
					}
				}
			}
		});
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		//Do nothing
		return 0;
	}
	
	
	@Override
	public void decreaseTPTDepths(int reduction) {
		//Do nothing
	}
	
	
	@Override
	public void shutDown() {
		try {
			
			executor.shutdownNow();
			
		} catch(Throwable t) {
			//Do nothing
		}
	}
}
