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
package bagaturchess.search.impl.rootsearch.remote;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.impl.commands.Go;
import bagaturchess.uci.impl.commands.info.Info;


public class SequentialSearch_SeparateProcess extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private UCIEnginesManager runner;
	
	
	public SequentialSearch_SeparateProcess(Object[] args) {
		
		super(args);
		
		executor = Executors.newFixedThreadPool(2);
		
		runner = new UCIEnginesManager();
		
		EngineProcess engine = new EngineProcess_BagaturImpl("BagaturEngineClient", "");
		
		runner.addEngine(engine);
		
		try {
			
			runner.startEngines();
			
			runner.uciOK();
			
			runner.isReady();
			
			runner.disable();
			
		} catch (Throwable t) {
			ChannelManager.getChannel().dump(t);
		}
	}
	
	
	public IRootSearchConfig getRootSearchConfig() {
		return (IRootSearchConfig) super.getRootSearchConfig();
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup);
		
		try {
			
			runner.newGame();
			
			//runner.setupPosition("fen " + _bitboardForSetup.toEPD());
			
			//String allMovesStr = MoveInt.getMovesUCI(getBitboardForSetup());
			
			//runner.setupPosition("moves " + allMovesStr);
			runner.setupPosition("startpos");
					
		} catch (Throwable t) {
			ChannelManager.getChannel().dump(t);
		}
	}
	
	
	public boolean isStopped() {
		return stopper == null;
	}
	
	
	@Override
	public void shutDown() {
		try {
			
			runner.stopEngines();
			
			runner.destroyEngines();
			
			//runner.enable();
			
			executor.shutdownNow();
			
		} catch(Throwable t) {
			//Do nothing
		}
	}
	
	
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback, final int[] prevPV) {
		negamax(_bitboardForSetup, mediator, startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV, false, null);
	}
	
	
	public void negamax(final IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback,
			int[] prevPV, boolean dont_wrap_mediator, Integer initialValue) {
		throw new IllegalStateException();
	}
	
	
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, final IFinishCallback multiPVCallback, Go go) {
			
		
		if (stopper != null) {
			throw new IllegalStateException("MTDSequentialSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		setupBoard(bitboardForSetup);
		
		
		String allMovesStr = MoveInt.getMovesUCI(getBitboardForSetup());
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: allMovesStr=" + allMovesStr);
		
		//StringBuilder message = new StringBuilder(32);
		//MoveInt.moveToStringUCI(cur_move, message);
		//String moveStr = message.toString();
		
		try {
			
			runner.setupPosition("startpos moves " + allMovesStr);
			
			runner.disable();
			
			runner.go(go);
			
			
			final ISearchMediator final_mediator = mediator;
			
			//OutboundQueueProcessor
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: OutboundQueueProcessor before loop");
						
						while (!final_mediator.getStopper().isStopped() //Condition for normal play
								&& stopper != null && !stopper.isStopped()) {
							
							Thread.sleep(15);
						}
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: OutboundQueueProcessor after loop stopped="
								+ final_mediator.getStopper().isStopped());
						
						
						//runner.stopEngines();
						
						
						//runner.enable();
						
						
					} catch(Throwable t) {
						ChannelManager.getChannel().dump(t);
						ChannelManager.getChannel().dump(t.getMessage());
					}
				}
			});
			
			
			//InboundQueueProcessor
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: InboundQueueProcessor: before getInfoLines");
						
						List<String> infos = runner.getInfoLines(new LineCallBack() {
							
							@Override
							public void newLine(String line) {
								
								if (line.indexOf("info") > 0) {
									if (line.indexOf(" pv ") > 0) {
										
										Info info = new Info(line);
										//System.out.println("MAJOR: " + info);
										
										ISearchInfo searchInfo = SearchInfoFactory.getFactory().createSearchInfo(info, getBitboardForSetup());
										if (searchInfo.getPV() != null && searchInfo.getPV().length > 0) {
											final_mediator.changedMajor(searchInfo);
										}
									} else {
										//System.out.println("MINOR: " + line);
									}
								} else if (line.indexOf("bestmove") > 0) {
									
								}
								
								
							}
							
						});
						
						if (infos.size() > 1) {
							throw new IllegalStateException("Only one engine is supported");
						}
						
						if (infos.size() == 0 || infos.get(0) == null) {
							throw new IllegalStateException("infos.size() == 0 || infos.get(0) == null");
						}
						//System.out.println("depth " + depth);
						
						//runner.stopEngines();
						
						if (stopper == null) {
							throw new IllegalStateException();
						}
						stopper.markStopped();
						stopper = null;
						
						if (multiPVCallback == null) {//Non multiPV search
							final_mediator.getBestMoveSender().sendBestMove();
						} else {
							//MultiPV search
							multiPVCallback.ready();
						}
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: InboundQueueProcessor after loop stopped="
								+ final_mediator.getStopper().isStopped());
						
						//runner.enable();
						
					} catch(Throwable t) {
						ChannelManager.getChannel().dump(t);
						ChannelManager.getChannel().dump(t.getMessage());
					}
				}
			});
			
		} catch (Throwable t) {
			ChannelManager.getChannel().dump(t);
		}
	}


	@Override
	public int getTPTUsagePercent() {
		return 0;//searcher.getEnv().getTPTUsagePercent();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		//searcher.getEnv().getTPT().correctAllDepths(reduction);
	}
}
