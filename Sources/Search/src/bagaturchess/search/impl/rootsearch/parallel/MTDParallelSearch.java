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
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
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
import bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;


public class MTDParallelSearch extends RootSearch_BaseImpl {
	
	
	private ExecutorService executor;
	
	private List<IRootSearch> searchers;
	
	
	public MTDParallelSearch(Object[] args) {
		
		super(args);
		
		//executor = Executors.newFixedThreadPool(getRootSearchConfig().getThreadsCount());
		executor = Executors.newFixedThreadPool(1);
		
		searchers = new ArrayList<IRootSearch>();
		
		for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++ ) {
			
			try {
				IRootSearch searcher = (IRootSearch)
						ReflectionUtils.createObjectByClassName_ObjectsConstructor(MTDSequentialSearch.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
				
				searchers.add(searcher);
			} catch (Throwable t) {
				ChannelManager.getChannel().dump(t);
			}
		}
		
		ChannelManager.getChannel().dump("Thread pool created with " + getRootSearchConfig().getThreadsCount() + " threads.");
	}
	
	
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
			final boolean useMateDistancePrunning, final IFinishCallback finishCallback, final int[] prevPV) {
		
		
		if (stopper != null) {
			throw new IllegalStateException("MTDParallelSearch started whithout beeing stopped");
		}
		stopper = new Stopper();
		
		
		if (maxIterations > ISearch.MAX_DEPTH) {
			throw new IllegalStateException("maxIterations=" + maxIterations + " > ISearch.MAX_DEPTH");
		}
		//searchers.waitSearchersToStop(); //Removed because of pondering. During pondering all threads are busy.
		setupBoard(_bitboardForSetup);
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("Parallel search started from depth " + startIteration + " to depth " + maxIterations);
		
		
		root_mediator.setStopper(new CompositeStopper(new ISearchStopper[] {root_mediator.getStopper(), stopper}));
		
		
		final ISearchMediator final_mediator = root_mediator;
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				
				try {
					
					
					final List<BucketMediator> mediators = new ArrayList<BucketMediator>();
					for (int i = 0; i < searchers.size(); i++) {
						mediators.add(new BucketMediator(final_mediator));
					}
					
					
					for (int i = 0; i < searchers.size(); i++) {
						searchers.get(i).negamax(getBitboardForSetup(), mediators.get(i), startIteration, maxIterations, useMateDistancePrunning, finishCallback, prevPV);
					}
					
					
					int CHECK_INTERVAL_MIN = 15;
					int CHECK_INTERVAL_MAX = 15;
					int check_interval = CHECK_INTERVAL_MIN;
					
					int cur_depth = startIteration;
					
					
					boolean isReady = false;
					while (!final_mediator.getStopper().isStopped() //Condition for normal play
							|| !isReady //Ready is when all best infos are send, even after the stopper has stopped
							) {
							
							try {
								final_mediator.getStopper().stopIfNecessary(maxIterations, getBitboardForSetup().getColourToMove(), ISearch.MIN, ISearch.MAX);
							} catch(SearchInterruptedException sie) {
								
							}
							
							int countWinners = 0;
							//Collect major infos by depth
							//final_mediator.dump("Collect major infos for depth " + cur_depth);
							List<ISearchInfo> majorInfosForFixDepth = new ArrayList<ISearchInfo>();
							for (int i_mediator = 0; i_mediator < mediators.size(); i_mediator++) {
								
								BucketMediator cur_mediator = mediators.get(i_mediator);
								
								ISearchInfo cur_mediator_lastinfo = null;
								for (int i_major = cur_mediator.majorInfos.size() - 1; i_major >= 0; i_major--) {								
									ISearchInfo curinfo = cur_mediator.majorInfos.get(i_major);
									if (!curinfo.isUpperBound() && curinfo.getDepth() == cur_depth) {
										cur_mediator_lastinfo = curinfo;
										break;
									}
								}
								
								if (cur_mediator_lastinfo != null) {
									countWinners++;
								}
								
								majorInfosForFixDepth.add(cur_mediator_lastinfo != null ? cur_mediator_lastinfo : null);
							}
							
							
							//Select best major by depth
							//final_mediator.dump("Select best major for depth " + cur_depth);
							ISearchInfo bestMajor = null;
							int bestInfoIndex = -1;
							for (int i = 0; i < majorInfosForFixDepth.size(); i++) {
								
								ISearchInfo curMajor = majorInfosForFixDepth.get(i);
								
								if (curMajor != null) { //Searcher i has produced info object
									if (bestMajor == null) {
										bestMajor = curMajor;
										bestInfoIndex = i;
									} else if (curMajor.getEval() > bestMajor.getEval()) {
										bestMajor = curMajor;
										bestInfoIndex = i;
									}
								}
							}
							
							
							//Send major
							if (bestMajor != null) {
								
								if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Selected bestMajor = " + bestMajor);
								;
								final_mediator.changedMajor(bestMajor);
								
								//if (mediators.get(bestInfoIndex).completedDepth == cur_depth) {
									cur_depth++;
								//}
								
								if (cur_depth > maxIterations) {
									break;
								}
								
								check_interval = CHECK_INTERVAL_MIN;
								
								if (bestInfoIndex == -1) {
									throw new IllegalStateException("bestInfoIndex = -1");
								}
								
								if (countWinners == 1) {
									
									if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: countWinners is 1");
									
									if (!final_mediator.getStopper().isStopped()) {
										
										//Stop and start all searchers except the winner
										
										for (int i = 0; i < searchers.size(); i++) {
											if (i != bestInfoIndex) {
												if (cur_depth <= maxIterations) {
													searchers.get(i).stopSearchAndWait();
												}
											}
										}
										
										if (cur_depth <= maxIterations) {
											for (int i = 0; i < searchers.size(); i++) {
												if (i != bestInfoIndex) {
													
														mediators.get(i).clearStopper();
														//searchers.get(i).negamax(getBitboardForSetup(), mediators.get(i), cur_depth, maxIterations,
														//		useMateDistancePrunning, finishCallback, bestMajor.getPV());
														searchers.get(i).negamax(getBitboardForSetup(), mediators.get(i), cur_depth - 1, maxIterations,
																useMateDistancePrunning, finishCallback, bestMajor.getPV());
												}
											}
										}
										
									}
								}
								
								
							} else {
								
								if (final_mediator.getStopper().isStopped()) {//All infos send and search is stoped: isReady = true;
									isReady = true;
								}
								
								//ChannelManager.getChannel().dump("MTDParallelSearch: bestMajor not found");
								
								//Wait some time and than make check again
								Thread.sleep(check_interval);
								
								check_interval = 2 * check_interval;
								if (check_interval > CHECK_INTERVAL_MAX) {
									check_interval = CHECK_INTERVAL_MAX;
								}
							}
					}
					
					if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("MTDParallelSearch: Out of loop");
					
					for (int i = 0; i < searchers.size(); i++) {
						searchers.get(i).stopSearchAndWait();
					}
					
					
					if (stopper == null) {
						throw new IllegalStateException();
					}
					stopper.markStopped();
					stopper = null;
					
					
					final_mediator.getBestMoveSender().sendBestMove();
					
					
				} catch(Throwable t) {
					ChannelManager.getChannel().dump(t);
					ChannelManager.getChannel().dump(t.getMessage());
				}
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
	
	
	private class BucketMediator extends SearchMediatorProxy {
		
		
		protected List<ISearchInfo> minorInfos;
		protected List<ISearchInfo> majorInfos;
		
		private ISearchStopper stopper;
		private BestMoveSender bestmovesender;
		
		
		public BucketMediator(ISearchMediator _parent) {
			
			super(_parent);
			
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
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void markStopped() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isStopped() {
					// TODO Auto-generated method stub
					return false;
				}
			};
		}
		
		
		@Override
		public void changedMajor(ISearchInfo info) {
			majorInfos.add(info);
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
