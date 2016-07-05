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
package bagaturchess.search.impl.rootsearch.parallel;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.CompositeStopper;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public abstract class MTDParallelSearch_BaseImpl extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private List<IRootSearch> searchers_ready;
	private List<IRootSearch> searchers_notready;
	
	private Object synch_Board 					= new Object();
	
	private final double TARGET_CPUS_LOAD 		= 50;
	private final double MIN_REMAINNING_TIME	= 500;
	
	private final VarStatistic stat_cpus_load 	= new VarStatistic(false);
	
	
	public MTDParallelSearch_BaseImpl(Object[] args) {
		
		super(args);
		
		executor 				= Executors.newFixedThreadPool(2);
		
		searchers_ready 		= new Vector<IRootSearch>();
		searchers_notready 		= new Vector<IRootSearch>();
		
		sequentialSearchers_Create();
		
	}
	
	
	protected abstract void sequentialSearchers_Create();
	
	
	protected abstract ISearchMediator sequentialSearchers_WrapMediator(ISearchMediator root_mediator);
	
	
	protected abstract void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, Go go, boolean dont_wrap_mediator);
	
	
	protected void addSearcher(IRootSearch searcher) {
		
		if (getBitboardForSetup() == null) { //createBoard of this root search is not called yet
			
			searchers_notready.add(searcher);
			
			ChannelManager.getChannel().dump("MTDParallelSearch_BaseImpl addSearcher to searchers_notready. Current size is " + searchers_notready.size());
			
		} else {
			
			synchronized(synch_Board) {
				searcher.createBoard(getBitboardForSetup());
			}
			
			searchers_ready.add(searcher);
			
			ChannelManager.getChannel().dump("MTDParallelSearch_BaseImpl addSearcher to searchers_ready. Current size is " + searchers_ready.size());
		}
	}
	
	
	@Override
	public void createBoard(IBitBoard _bitboardForSetup) {
		
		super.createBoard(_bitboardForSetup);
		
		ChannelManager.getChannel().dump("MTDParallelSearch_BaseImpl createBoard called. Will transfer " + searchers_notready.size() + " searchers from searchers_notready to searchers_ready");
		
		for (int i = 0; i < searchers_notready.size(); i++) {
			IRootSearch searcher = searchers_notready.get(i);
			addSearcher(searcher);
		}	
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator root_mediator, final ITimeController timeController, final IFinishCallback multiPVCallback, final Go initialgo) {
		
		//TODO: store pv in pvhistory
		
		if (stopper != null) {
			throw new IllegalStateException("MTDParallelSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		setupBoard(_bitboardForSetup);
		
		final int startIteration = (initialgo.getStartDepth() == Go.UNDEF_STARTDEPTH) ? 1 : initialgo.getStartDepth();
		int maxIterations = (initialgo.getDepth() == Go.UNDEF_DEPTH) ? ISearch.MAX_DEPTH : initialgo.getDepth();
		//Integer initialValue = (initialgo.getBeta() == Go.UNDEF_BETA) ? null : initialgo.getBeta();
		//int[] prevPV = MoveInt.getPV(initialgo.getPv(), _bitboardForSetup);
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			maxIterations = ISearch.MAX_DEPTH;
			initialgo.setDepth(maxIterations);
		}
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("Parallel search started from depth " + startIteration + " to depth " + maxIterations);
		
		
		root_mediator.setStopper(new CompositeStopper(new ISearchStopper[] {root_mediator.getStopper(), stopper}));
		
		
		final ISearchMediator final_mediator = root_mediator;
		
		
		//Searchers balancer - achieves specified CPUs load of the system by starting and stopping searchers
		executor.execute(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					int check_interval = 33;//33 ms
					
					while (
							(!final_mediator.getStopper().isStopped() //Stopped
								&& !isStopped())
							) {
						
						//Update statistics
						int current_cpus_load = 50;//Runtime.getRuntime().
						stat_cpus_load.addValue(current_cpus_load, current_cpus_load);
						
						
						//Make the calculation and start/stop searchers
						double expected_load_per_seacrcher = 100d / getRootSearchConfig().getThreadsCount();
						if (stat_cpus_load.getEntropy() > TARGET_CPUS_LOAD) {
							//TODO: STOP one searcher
							
						} else if (stat_cpus_load.getEntropy() < TARGET_CPUS_LOAD - expected_load_per_seacrcher) {
							//TODO: START one searcher
							
						} else {
							//Do nothing - there is no need to neighter start nor stop searchers
						}
						
						
						//Wait some time and than loop again
						Thread.sleep(check_interval);
					}
					
					
				} catch(Throwable t) {
					ChannelManager.getChannel().dump(t);
					ChannelManager.getChannel().dump(t.getMessage());
				}
			}
		});
		
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					
					
					final List<ISearchMediator> mediators = new ArrayList<ISearchMediator>();
					final List<BucketMediator> mediators_bucket = new ArrayList<BucketMediator>();
					
					//Create all mediators, which will be potentially used by the searchers
					for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++) {
						BucketMediator cur_bucket = new BucketMediator(final_mediator);
						mediators_bucket.add(cur_bucket);
						mediators.add(sequentialSearchers_WrapMediator(cur_bucket));
					}
					
					
					//Start searchers initially
					for (int i = 0; i < searchers_ready.size(); i++) {
						synchronized(synch_Board) {							
							Go cur_go = initialgo;
							ITimeController cur_timecontroller = timeController;
							sequentialSearchers_Negamax(searchers_ready.get(i), getBitboardForSetup(), mediators.get(i), cur_timecontroller, multiPVCallback, cur_go, true);
						}
					}
					
					
					int CHECK_INTERVAL_MIN = 15;
					int CHECK_INTERVAL_MAX = 15;
					int check_interval = CHECK_INTERVAL_MIN;
					
					long start_time = System.currentTimeMillis();
					
					
					SearchersInfo searchersInfo = new SearchersInfo(startIteration, 0.377d);
					
					
					boolean allSearchersReady = false;
					//boolean hasSendAtLest1Info = false;
					while (
							(!final_mediator.getStopper().isStopped() //Stopped
									&& !allSearchersReady //Search is done
								)// || !hasSendAtLest1Info
							) {
						
							//if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Loop > before start threads");
							
							//Start all stopped searchers
							long time_delta = System.currentTimeMillis() - start_time;
							long expected_count_workers = time_delta / 1;//100;
							for (int i = 0; i < Math.min(searchers_ready.size(), expected_count_workers); i++) {
								if (searchers_ready.get(i).isStopped()){
									
									//TODO: Start the search with the best current PV
									ISearchInfo cur_deepest_best = searchersInfo.getDeepestBestInfo();
									if (cur_deepest_best != null) {
										
										Go cur_go = initialgo;
										ITimeController cur_timecontroller = timeController;
										
										if (timeController != null) {
											long remainningTime = timeController.getRemainningTime();
											if (remainningTime > MIN_REMAINNING_TIME) {
												
												StringBuilder pv = new StringBuilder(128);
												if (cur_deepest_best.getPV() != null) {
													for (int j=0; j<cur_deepest_best.getPV().length; j++) {
														MoveInt.moveToStringUCI(cur_deepest_best.getPV()[j], pv);
														if (j != cur_deepest_best.getPV().length - 1) {
															pv.append(" ");
														}
													}
												}
												
												cur_go = new Go(ChannelManager.getChannel(), "go movetime " + remainningTime
														//+ " startdepth " + (cur_deepest_best.getDepth() + 1)
														+ " beta " + cur_deepest_best.getEval()
														+ " pv " + pv.toString()
														);
												//cur_timecontroller = 
												
												if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: restarted searcher " + i + " with new go " + cur_go);
											}
										}
										
										synchronized(synch_Board) {
											sequentialSearchers_Negamax(searchers_ready.get(i), getBitboardForSetup(), mediators.get(i), cur_timecontroller, multiPVCallback, cur_go, true);
										}
									}
								}
							}
							
							
							//Collect all major infos, put them in searchersInfo, and send the best info if available
							boolean hasSendInfo = collectAndSendInfos(final_mediator,	mediators_bucket, searchersInfo);
							
							
							for (int i = 0; i < searchers_ready.size(); i++) {
								if (!searchers_ready.get(i).isStopped()) {
									if (searchersInfo.needRestart(searchers_ready.get(i))) {
										searchers_ready.get(i).stopSearchAndWait();
										if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: restarted searcher " + i);
									}
								}
							}
							
							if (!hasSendInfo) {
								//Wait some time and than make check again
								Thread.sleep(check_interval);
								
								check_interval = 2 * check_interval;
								if (check_interval > CHECK_INTERVAL_MAX) {
									check_interval = CHECK_INTERVAL_MAX;
								}
							}
							
							try {
								
								int colourToMove;
								synchronized(synch_Board) {
									colourToMove = getBitboardForSetup().getColourToMove();
								}
								
								final_mediator.getStopper().stopIfNecessary(searchersInfo.getCurrentDepth(), colourToMove, ISearch.MIN, ISearch.MAX);
							} catch(SearchInterruptedException sie) {
							}
							
							
							boolean hasRunningSearcher = false;
							for (int i = 0; i < searchers_ready.size(); i++) {
								if (!searchers_ready.get(i).isStopped()) {
									hasRunningSearcher = true;
									break;
								}
							}
							allSearchersReady = !hasRunningSearcher;
					}
					
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Out of loop final_mediator.getStopper().isStopped()=" + final_mediator.getStopper().isStopped());
					
					
					for (int i = 0; i < searchers_ready.size(); i++) {
						if (!searchers_ready.get(i).isStopped()) {
							searchers_ready.get(i).stopSearchAndWait();
						}
					}
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Searchers are stopped");
					
					
					//Send all infos after the searchers are stopped
					collectAndSendInfos(final_mediator, mediators_bucket, searchersInfo);
					
					
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
					
					
				} catch(Throwable t) {
					ChannelManager.getChannel().dump(t);
					ChannelManager.getChannel().dump(t.getMessage());
				}
			}
			
			
			private boolean collectAndSendInfos(
					final ISearchMediator final_mediator,
					final List<BucketMediator> mediators_bucket,
					SearchersInfo searchersInfo) {
				
				boolean hasSendInfo = false;
				for (int i_mediator = 0; i_mediator < mediators_bucket.size(); i_mediator++) {
					
					//if (searchers_started[i_mediator]) {
						
						BucketMediator cur_mediator = mediators_bucket.get(i_mediator);
						
						for (int i_major = cur_mediator.lastSendMajorIndex + 1; i_major < cur_mediator.majorInfos.size() ; i_major++) {							
							
							ISearchInfo curinfo = cur_mediator.majorInfos.get(i_major);
							
							if (curinfo == null) { //Because of multi threaded access to arraylist
								continue;
							}
							
							if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: select info from mediator (" + i_mediator + ")"
									+ ", curinfo.getDepth()=" + curinfo.getDepth()
									+ ", curinfo.getBestMove()=" + curinfo.getBestMove()
									+ ", curinfo.getPV()=" + curinfo.getPV()
									+ ", curinfo.isUpperBound()=" + curinfo.isUpperBound()
									+ (curinfo.getPV() == null ? "" : ", info.getPV().length=" + curinfo.getPV().length)
									);
							
							cur_mediator.lastSendMajorIndex = i_major;
							
							//if (!curinfo.isUpperBound()) {
								searchersInfo.update(searchers_ready.get(i_mediator), curinfo);
								ISearchInfo toSend = searchersInfo.getNewInfoToSendIfPresented();
								while (toSend != null) {
									if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: hasInfoToSend=true, infoToSend="
												+ toSend);
									final_mediator.changedMajor(toSend);
									hasSendInfo = true;
									//hasSendAtLest1Info = true;
									toSend = searchersInfo.getNewInfoToSendIfPresented();
								}
							//}
						}
					//}
				}
				
				return hasSendInfo;
			}
		});
		
	}
	
	
	@Override
	public void shutDown() {
		try {
			
			executor.shutdownNow();
			
			executor = null;
			
			for (int i = 0; i < searchers_ready.size(); i++) {
				searchers_ready.get(i).shutDown();
			}
			
			searchers_ready = null;
			
		} catch(Throwable t) {
			//Do nothing
		}
	}

	
	protected boolean isTerminated() {
		return executor == null;
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		
		if (searchers_ready.size() == 0) {//Not yet initialized
			return 0;
		}
		
		int sum = 0;
		for (int i = 0; i < searchers_ready.size(); i++) {
			sum += searchers_ready.get(i).getTPTUsagePercent();
		}
		return sum / searchers_ready.size();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		for (int i = 0; i < searchers_ready.size(); i++) {
			searchers_ready.get(i).decreaseTPTDepths(reduction);
		}
	}
	
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\r\n";
		result += searchers_ready.toString();
		
		return result;
	}
	
	
	private static class BucketMediator extends SearchMediatorProxy {
		
		
		protected int lastSendMajorIndex = -1;
		protected List<ISearchInfo> minorInfos;
		protected List<ISearchInfo> majorInfos;
		
		private ISearchStopper stopper;
		//private ISearchStopper root_stopper;
		private BestMoveSender bestmovesender;
		
		
		public BucketMediator(ISearchMediator _parent) {
			
			super(_parent);
			
			//root_stopper = _parent.getStopper();
			
			minorInfos = new ArrayList<ISearchInfo>();
			majorInfos = new ArrayList<ISearchInfo>();
			
			
			
			bestmovesender = new BestMoveSender() {
				@Override
				public void sendBestMove() {
					//Do nothing
				}
			};
			
			clearStopper();
		}
		
		
		protected void clearStopper() {
			stopper = new ISearchStopper() {
				
				@Override
				public void stopIfNecessary(int maxdepth, int colour, double alpha,
						double beta) throws SearchInterruptedException {
					//Do nothing
				}
				
				@Override
				public void markStopped() {
					//Do nothing
				}
				
				@Override
				public boolean isStopped() {
					return false;//root_stopper.isStopped();
				}
			};
		}
		
		
		@Override
		public void changedMajor(ISearchInfo info) {
			
			majorInfos.add(info);
			
			if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("BucketMediator: changedMajor "
					+ ", info.getDepth()=" + info.getDepth()
					+ ", info.getBestMove()=" + info.getBestMove()
					+ ", info.getPV()=" + info.getPV()
					+ ", info.isUpperBound()=" + info.isUpperBound()
					+ (info.getPV() == null ? "" : ", info.getPV().length=" + info.getPV().length)
					);
		}
		
		
		@Override
		public void changedMinor(ISearchInfo info) {
			minorInfos.add(info);
		}
		
		
		@Override
		public ISearchStopper getStopper() {
			return stopper;
		}
		
		
		@Override
		public void setStopper(ISearchStopper _stopper) {
			stopper = _stopper;
		}
		
		
		@Override
		public BestMoveSender getBestMoveSender() {
			return bestmovesender;
		}
	}
}
