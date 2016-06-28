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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.search.api.internal.CompositeStopper;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.rootsearch.RootSearch_BaseImpl;
import bagaturchess.search.impl.rootsearch.sequential.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;


public abstract class MTDParallelSearch_BaseImpl extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private List<IRootSearch> searchers;
	
	
	public MTDParallelSearch_BaseImpl(Object[] args) {
		
		super(args);
		
		executor = Executors.newFixedThreadPool(1);
		
		searchers = sequentialSearchers_Create();
		
		ChannelManager.getChannel().dump("MTDParallelSearch_BaseImpl search created with " + getRootSearchConfig().getThreadsCount() + " sequential searchers.");
	}
	
	
	protected abstract List<IRootSearch> sequentialSearchers_Create();
	
	protected abstract void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback,
			int[] prevPV, boolean dont_wrap_mediator, Integer initialValue);
	
	
	public IRootSearchConfig_SMP getRootSearchConfig() {
		return (IRootSearchConfig_SMP) super.getRootSearchConfig();
	}
	
	
	@Override
	public void newGame(IBitBoard _bitboardForSetup) {
		
		super.newGame(_bitboardForSetup); 
		
		for (int i = 0; i < searchers.size(); i++) {
			searchers.get(i).newGame(getBitboardForSetup());
		}
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator root_mediator, final int startIteration, final int maxIterations,
			final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback, final int[] prevPV) {
		
		//TODO: store pv in pvhistory
		
		if (stopper != null) {
			throw new IllegalStateException("MTDParallelSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			throw new IllegalStateException("maxIterations=" + maxIterations + " > ISearch.MAX_DEPTH");
		}
		
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("Parallel search started from depth " + startIteration + " to depth " + maxIterations);
		
		
		root_mediator.setStopper(new CompositeStopper(new ISearchStopper[] {root_mediator.getStopper(), stopper}));
		
		
		final ISearchMediator final_mediator = root_mediator;
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					
					final List<ISearchMediator> mediators = new ArrayList<ISearchMediator>();
					final List<BucketMediator> mediators_bucket = new ArrayList<BucketMediator>();
					
					for (int i = 0; i < searchers.size(); i++) {
						BucketMediator cur_bucket = new BucketMediator(final_mediator);
						mediators_bucket.add(cur_bucket);
						mediators.add(new NPSCollectorMediator(new Mediator_AlphaAndBestMoveWindow(cur_bucket)));
					}
					
					
					boolean[] searchers_started = new boolean[searchers.size()];
					//searchers.get(0).negamax(getBitboardForSetup(), mediators.get(0),
					//		startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV, true, null);
					//searchers_started[0] = true;
					for (int i = 0; i < searchers.size(); i++) {
						sequentialSearchers_Negamax(searchers.get(i), getBitboardForSetup(), mediators.get(i), startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV, true, null);
						//searchers.get(i).negamax(getBitboardForSetup(), mediators.get(i), startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV, true, null);
						searchers_started[i] = true;
					}
					
					
					int CHECK_INTERVAL_MIN = 15;
					int CHECK_INTERVAL_MAX = 15;
					int check_interval = CHECK_INTERVAL_MIN;
					
					long start_time = System.currentTimeMillis();
					
					SearchersInfo searchersInfo = new SearchersInfo(startIteration, 0.001d);
					
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
							for (int i = 0; i < Math.min(searchers.size(), expected_count_workers); i++) {
								if (!searchers_started[i]){
									//TODO: Start the search with the best current PV
									ISearchInfo cur_deepest_best = searchersInfo.getDeepestBestInfo();
									if (cur_deepest_best != null) {
										//searchers.get(i).negamax(getBitboardForSetup(), mediators.get(i),
											//	cur_deepest_best.getDepth(), maxIterations, useMateDistancePrunning, multiPVCallback, cur_deepest_best.getPV(), true, cur_deepest_best.getEval());
										sequentialSearchers_Negamax(searchers.get(i), getBitboardForSetup(), mediators.get(i),
												cur_deepest_best.getDepth(), maxIterations, useMateDistancePrunning, multiPVCallback, cur_deepest_best.getPV(), true, cur_deepest_best.getEval());
										searchers_started[i] = true;
									}
								}
							}
							
							
							//Collect all major infos, put them in searchersInfo, and send the best info if available
							boolean hasSendInfo = collectAndSendInfos(final_mediator,	mediators_bucket, searchersInfo);
							
							
							for (int i = 0; i < searchers.size(); i++) {
								if (searchers_started[i]) {
									if (searchersInfo.needRestart(searchers.get(i))) {
										searchers.get(i).stopSearchAndWait();
										searchers_started[i] = false;
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
								final_mediator.getStopper().stopIfNecessary(searchersInfo.getCurrentDepth(), getBitboardForSetup().getColourToMove(), ISearch.MIN, ISearch.MAX);
							} catch(SearchInterruptedException sie) {
							}
							
							
							boolean hasRunningSearcher = false;
							for (int i = 0; i < searchers.size(); i++) {
								if (searchers_started[i]) {
									if (!searchers.get(i).isStopped()) {
										hasRunningSearcher = true;
										break;
									}
								}
							}
							allSearchersReady = !hasRunningSearcher;
					}
					
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Out of loop final_mediator.getStopper().isStopped()=" + final_mediator.getStopper().isStopped());
					
					
					for (int i = 0; i < searchers.size(); i++) {
						if (searchers_started[i]) {
							searchers.get(i).stopSearchAndWait();
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
								searchersInfo.update(searchers.get(i_mediator), curinfo);
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
			
			for (int i = 0; i < searchers.size(); i++) {
				searchers.get(i).shutDown();
			}
			
			searchers = null;
			
		} catch(Throwable t) {
			//Do nothing
		}
	}


	@Override
	public int getTPTUsagePercent() {
		
		if (searchers.size() == 0) {//Not yet initialized
			return 0;
		}
		
		int sum = 0;
		for (int i = 0; i < searchers.size(); i++) {
			sum += searchers.get(i).getTPTUsagePercent();
		}
		return sum / searchers.size();
	}


	@Override
	public void decreaseTPTDepths(int reduction) {
		for (int i = 0; i < searchers.size(); i++) {
			searchers.get(i).decreaseTPTDepths(reduction);
		}
	}
	
	
	@Override
	public String toString() {
		String result = super.toString();
		
		result += "\r\n";
		result += searchers.toString();
		
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
