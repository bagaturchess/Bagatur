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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_DistributionImpl;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_WorkspaceImpl;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.impl.commands.Go;
import bagaturchess.uci.impl.commands.info.Info;
import bagaturchess.uci.impl.commands.options.UCIOptions;


public class SequentialSearch_SeparateProcess extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private UCIEnginesManager runner;
	
	private int hashfull;
	
	private final Object sync_stop = new Object();
	
	
	public SequentialSearch_SeparateProcess(Object[] args) {
		
		super(args);
		
		executor = Executors.newFixedThreadPool(2);
		
		
		runner = new UCIEnginesManager();
		
		
		/*EngineProcess engine = new EngineProcess_BagaturImpl_WorkspaceImpl("BagaturEngine_WorkerNode",
				"C:\\Users\\DATA\\OWN\\chess\\GIT_REPO\\Bagatur-Chess-Engine-And-Tools\\Sources\\",
				"",
				getRootSearchConfig().getThreadMemory_InMegabytes());*/
		
		String workdir = new File(".").getAbsolutePath();
		ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: Starting Java process of engine in workdir '" + workdir + "'" + " with " + getRootSearchConfig().getThreadMemory_InMegabytes() + "MB of memory" );
		EngineProcess engine = new EngineProcess_BagaturImpl_DistributionImpl("BagaturEngine_WorkerNode",
				workdir + File.separatorChar,
				"",
				getRootSearchConfig().getThreadMemory_InMegabytes());
		
		
		runner.addEngine(engine);
		
		try {
			
			ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: startEngines");
			runner.startEngines();
			
			ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: uciOK");
			runner.uciOK();
			
			List<String> options = new ArrayList<String>();
			//options.add("setoption name UCIOptions.OPTION_NAME_Logging_Policy value multiple files");
			options.add("setoption name OwnBook value false");//The separate process should not use openning book moves, because they have to be already moved by the master process.
			options.add("setoption name Ponder value false");
			//The UCI options of the slave engine should be set as for the master engine, especially the UCI option for Memory Optimizations.
			options.add("setoption name SyzygyPath value " + getRootSearchConfig().getTbPath());
			
			//options.add("setoption name Openning Mode value random intermediate");
			
			/*315 <Bagatur1.5f(1): option name UCIOptions.OPTION_NAME_Logging_Policy type combo default none var single file var multiple files var none
			316 <Bagatur1.5f(1): option name OwnBook type check default true
			316 <Bagatur1.5f(1): option name Ponder type check default true
			316 <Bagatur1.5f(1): option name UCI_AnalyseMode type check default false
			317 <Bagatur1.5f(1): option name MultiPV type spin default 1 min 1 max 100
			317 <Bagatur1.5f(1): option name GaviotaTbPath type string default C:\DATA\BagaturEngine.1.5f\.\data\egtb
			318 <Bagatur1.5f(1): option name GaviotaTbCache type spin default 8 min 4 max 512
			319 <Bagatur1.5f(1): option name Time Control Optimizations type combo default for 40/40 var for 40/40 var for 1/1
			320 <Bagatur1.5f(1): option name Hidden Depth type spin default 0 min 0 max 10
			320 <Bagatur1.5f(1): option name Openning Mode type combo default most played first var most played first var random intermediate var random full
			*/
			
			ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: setoptions: " + options);
			runner.setOptions(options);
			
			ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: isReady");
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
	public void createBoard(IBitBoard _bitboardForSetup) {
		
		super.createBoard(_bitboardForSetup);
		
		try {
			
			runner.newGame();
			
			setUpEnginePosition(_bitboardForSetup);
			
		} catch (Throwable t) {
			ChannelManager.getChannel().dump(t);
		}
	}


	private void setUpEnginePosition(IBitBoard _bitboardForSetup)
			throws IOException {
		
		//Initialize engine by FEN and moves
		
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = Utils.copy(_bitboardForSetup.getPlayedMoves());
		
		_bitboardForSetup.revert();
		
		String initialFEN = _bitboardForSetup.toEPD();
		
		for (int i=0; i<movesCount; i++) {
			_bitboardForSetup.makeMoveForward(moves[i]);
		}
		
		String allMovesStr = BoardUtils.getPlayedMoves(_bitboardForSetup);
		
		if (initialFEN.equals(Constants.INITIAL_BOARD)) {
			runner.setupPosition("startpos moves " + allMovesStr);	
		} else {
			runner.setupPosition("fen " + initialFEN + " moves " + allMovesStr);	
		}
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
			//t.printStackTrace();
		}
	}
	
	
	@Override
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, ITimeController timeController, final IFinishCallback multiPVCallback, Go go) {		
		
		//ChannelManager.getChannel().dump(new Exception("JUST STACK"));
		
		if (stopper != null) {
			throw new IllegalStateException(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		setupBoard(bitboardForSetup);
		
		
		try {
			
			String allMovesStr = BoardUtils.getPlayedMoves(getBitboardForSetup());
			if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: allMovesStr=" + allMovesStr);
			
			//runner.setupPosition("startpos moves " + allMovesStr);
			runner.setupPosition("moves " + allMovesStr);
			
			runner.go(go);
			
			runner.disable();
			
			
			final ISearchMediator final_mediator = mediator;
			
			//OutboundQueueProcessor
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: OutboundQueueProcessor before loop");
						
						while (!final_mediator.getStopper().isStopped() //If the time is over, than exit from loop and stop engine
								&& stopper != null && !stopper.isStopped()) {
							
							Thread.sleep(15);
							
						}
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: OutboundQueueProcessor after loop stopped="
								+ final_mediator.getStopper().isStopped());
						
						
						synchronized (sync_stop) {
							
							if (!isStopped()) {// If the exit already happened in the InboundQueueProcessor below than the engine should not be stopped again
								
								ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: OutboundQueueProcessor - stopping engine and exit the queue");
								
								runner.stopEngines();
								//runner.enable();	
							}
						}
						
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
						
						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: InboundQueueProcessor: before getInfoLines");
						
						LineCallBack callback = new LineCallBack() {
							
							
							private List<String> lines = new ArrayList<String>();
							private String exitLine = null; 
							
							
							@Override
							public void newLine(String line) {
								
								if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: getInfoLine '" + line + "'");
								
								if (line.contains("LOG")) {
									if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: getInfoLine SKIPED, contains LOG");
									return;
								}
								
								lines.add(line);
								
								if (line.contains("bestmove")) {
									
									for (int i=lines.size() - 1; i >=0; i--) {
										if (lines.get(i).contains("info ") && lines.get(i).contains(" pv ")) {
											exitLine = lines.get(i);
											return;
										}
									}
									
									throw new IllegalStateException("No pv: " + lines);
									
								} else if (line.contains("info ")) {
									if (line.contains(" pv ")) {
										
										if (line.contains(" upperbound ")) {
											throw new IllegalStateException("line.contains(upperbound)");
										}
										
										Info info = new Info(line);
										//System.out.println("MAJOR: " + info);
										
										ISearchInfo searchInfo = SearchInfoFactory.getFactory().createSearchInfo(info, getBitboardForSetup());
										if (searchInfo.getPV() != null && searchInfo.getPV().length > 0) {
											final_mediator.changedMajor(searchInfo);
										}
									} else if (!line.contains(" upperbound ")) {//Not major line
										//System.out.println("MINOR: " + line);
										if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: getInfoLine minor line");
										
										Info info = new Info(line);
										//System.out.println("MAJOR: " + info);
										
										hashfull = info.getHashfull() / 10;
										
										ISearchInfo searchInfo = SearchInfoFactory.getFactory().createSearchInfo_Minor(info, getBitboardForSetup());
										final_mediator.changedMinor(searchInfo);
									} else {
										//Do nothing
										//upperbound
									}
								}
							}
							
							
							@Override
							public String exitLine() {
								return exitLine;
							}	
						};
						
						List<String> infos = runner.getInfoLines(callback);
						
						if (infos.size() > 1) {
							throw new IllegalStateException("Only one engine is supported");
						}
						
						if (infos.size() == 0 || infos.get(0) == null) {
							throw new IllegalStateException("infos.size() == 0 || infos.get(0) == null");
						}

						if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: InboundQueueProcessor after loop stopped="
								+ final_mediator.getStopper().isStopped());
						
						
						synchronized (sync_stop) {

							if (!isStopped()) {//Not stopped from the UI. Otherwise the best move is already send from the InboundQueueProcessor above
								
								ChannelManager.getChannel().dump("SequentialSearch_SeparateProcess: InboundQueueProcessor - stopping search and exit the queue");
								
								//runner.stopEngines();//Engine has stopped itself already (got out of the getInfoLines blocking call)
								//runner.enable();
								
								stopper.markStopped();
								stopper = null;
								
								if (multiPVCallback == null) {//Non multiPV search
									ChannelManager.getChannel().dump(Thread.currentThread().getName() + " " + "SequentialSearch_SeparateProcess: InboundQueueProcessor - call final_mediator.getBestMoveSender().sendBestMove()");
									final_mediator.getBestMoveSender().sendBestMove();
								} else {
									//MultiPV search
									multiPVCallback.ready();
								}
							}
						}
						
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
		return hashfull;
	}
	
	
	@Override
	public void decreaseTPTDepths(int reduction) {
		//Do nothing
		//As UCI doesn't support such options, this have to be implemented "on top" or "in addition" of UCI communication between this object and currently running engine process(es)
	}
}
