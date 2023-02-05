package bagaturchess.search.impl.rootsearch.parallel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;


public class SearchersInfo {

	
	private Map<IRootSearch, SearcherInfo> searchersInfo;
	private int cur_depth;
	private double nextDepthThreshold;
	
	private Map<IRootSearch, Long> searchers_searched_nodes_count;
	private Map<IRootSearch, Long> searchers_tb_hits;
	
	
	public SearchersInfo(int startDepth, double _nextDepthThreshold) {
		
		searchersInfo = new HashMap<IRootSearch, SearcherInfo>();
		cur_depth = startDepth;
		nextDepthThreshold = _nextDepthThreshold;
		
		searchers_searched_nodes_count = new HashMap<IRootSearch, Long>();
		searchers_tb_hits = new HashMap<IRootSearch, Long>();
		
		ChannelManager.getChannel().dump("SearchersInfo.init(...): nextDepthThreshold=" + nextDepthThreshold);
	}
	
	
	public int getCurrentDepth() {
		
		return cur_depth;
	}
	
	
	public void setNodesCountAndTBHits(IRootSearch searcher, long searched_nodes_count, long tb_hits_count) {
		
		searchers_searched_nodes_count.put(searcher, searched_nodes_count);
		searchers_tb_hits.put(searcher, tb_hits_count);
	}
	
	
	private long getNodesCount() {
		
		long nodes = 0;
		
		for (IRootSearch searcher: searchers_searched_nodes_count.keySet()) {
			
			Long searched_nodes_count = searchers_searched_nodes_count.get(searcher);
			
			if (searched_nodes_count != null) {
				
				nodes += searched_nodes_count;
			}
		}
		
		return nodes;
	}
	
	
	private long getTBHits() {
		
		long tb_hits = 0;
		
		for (IRootSearch searcher: searchers_tb_hits.keySet()) {
			
			Long tb_hits_count = searchers_tb_hits.get(searcher);
			
			if (tb_hits_count != null) {
				
				tb_hits += tb_hits_count;
			}
		}
		
		return tb_hits;
	}
	
	
	public void updateMajor(IRootSearch searcher, ISearchInfo info) {
		
		
		//Skip infos without PV and best move
		if (info.isUpperBound()) {
			
			return;
		}
		
		//In some rare cases, lowerbound is also without PV and best move (mainly with depth 1)
		if (info.getPV() == null
				|| info.getPV().length < 1
				|| info.getBestMove() == 0
				) {
			
			return;
		}
		
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SearchersInfo: update info=" + info + ", info.getDepth()=" + info.getDepth() + ", info.getPV().length=" + info.getPV().length);
		
		
		SearcherInfo searcherinfo = searchersInfo.get(searcher);
		
		if (searcherinfo == null) {
			
			searcherinfo = new SearcherInfo();
			
			searchersInfo.put(searcher, searcherinfo);
		}
		
		searcherinfo.update(info);
	}
	
	
	public boolean needRestart(IRootSearch searcher) {
		
		SearcherInfo searcherinfo = searchersInfo.get(searcher);
		
		if (searcherinfo != null && searcherinfo.getMaxDepth() < cur_depth) {
			
			return true;
		}
		
		return false;
	}
	
	
	//Result can be null
	//It should be used only for starting new searchers with the PV from the info, and of course only when the seacrhers are configured be restraed at all.
	public ISearchInfo getDeepestBestInfo() {
		
		
		ISearchInfo deepest_last_info = null;
		
		
		for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			
			ISearchInfo cur_deepest_last_info = cur_searcher_infos.getLastSearchInfo(cur_searcher_infos.getMaxDepth());
			
			if (cur_deepest_last_info != null) {
				
				if (deepest_last_info == null) {
					
					deepest_last_info = cur_deepest_last_info;
					
				} else {
					
					if (cur_deepest_last_info.getDepth() > deepest_last_info.getDepth()) {
						
						deepest_last_info = cur_deepest_last_info;
						
					} else if (cur_deepest_last_info.getDepth() == deepest_last_info.getDepth()) {
						
						if (cur_deepest_last_info.getEval() > deepest_last_info.getEval()) {
							
							deepest_last_info = cur_deepest_last_info;
						}
					}
				}
			}
		}
		
		
		return deepest_last_info;
	}
	
	
	public ISearchInfo getNewInfoToSendIfPresented() {
		
		if (hasDepthInfo(cur_depth + 1)) {
			
			cur_depth++;
			
			if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SearchersInfo: increase depth to " + cur_depth);
		}
		
		ISearchInfo cur_depth_info = getAccumulatedInfo(cur_depth);
		
		return cur_depth_info;
	}
	
	
	private boolean hasDepthInfo(int depth) {
		
		
		int countResponded = 0;
		
		
		for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			
			if (cur_searcher_infos != null) {
				
				if (cur_searcher_infos.getLastSearchInfo(depth) != null) {
					
					countResponded++;
				}
			}
		}
		
		
		return (countResponded / (double) searchersInfo.size() >= nextDepthThreshold);
	}
	
	
	//Result can be null
	private ISearchInfo getAccumulatedInfo(int depth) {
		
		Map<Integer, MoveInfo> movesInfoPerDepth = new HashMap<Integer, MoveInfo>();
		
		for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			
			ISearchInfo cur_last_info = cur_searcher_infos.getLastSearchInfo(depth);
			
			if (cur_last_info != null) {
				
				MoveInfo moveInfo = movesInfoPerDepth.get(cur_last_info.getBestMove());
				
				if (moveInfo == null) {
					
					movesInfoPerDepth.put(cur_last_info.getBestMove(), new MoveInfo(cur_last_info));
					
				} else {
					
					moveInfo.addInfo(cur_last_info);
				}
			}
		}
		
		
		MoveInfo bestMoveInfo = null;
		
		for (Integer move: movesInfoPerDepth.keySet()) {
			
			MoveInfo cur_moveInfo = movesInfoPerDepth.get(move);
			
			if (bestMoveInfo == null) {
				
				bestMoveInfo = cur_moveInfo;
				
			} else {
				
				if (cur_moveInfo.getEval() > bestMoveInfo.getEval()) {
					
					bestMoveInfo = cur_moveInfo;
				}
			}
		}
		
		if (bestMoveInfo == null) {
			
			return null;
		}
		
		ISearchInfo info_to_send = SearchInfoFactory.getFactory().createSearchInfo();
		
		info_to_send.setDepth(bestMoveInfo.best_info.getDepth());
		info_to_send.setSelDepth(bestMoveInfo.best_info.getSelDepth());
		info_to_send.setEval(bestMoveInfo.getEval());
		info_to_send.setBestMove(bestMoveInfo.best_info.getBestMove());
		info_to_send.setPV(bestMoveInfo.best_info.getPV());
		info_to_send.setSearchedNodes(getNodesCount());
		info_to_send.setTBhits(getTBHits());
		
		
		return info_to_send;
	}
	
	
	private static class SearcherInfo {
		
		
		private Map<Integer, SearcherDepthInfo> depthsInfo;
		
		
		public SearcherInfo() {
			
			depthsInfo = new HashMap<Integer, SearchersInfo.SearcherInfo.SearcherDepthInfo>();
		}


		public void update(ISearchInfo info) {
			
			int info_depth = info.getDepth();
			
			updateSearcherDepth(info, info_depth);
			
			//Influence the current depth - move and evaluation. This should help:
			//1. The time for the search could be extended in case of eval fluctuations.
			//2. The previous depth, from which still evals and moves are send to the UCI, will be sooner visible in the produced PVs in GUI or logs.
			updateSearcherDepth(info, info_depth - 1);
		}


		private void updateSearcherDepth(ISearchInfo info, int info_depth) {
			
			SearcherDepthInfo searcherDepthInfo = depthsInfo.get(info_depth);
			
			if (searcherDepthInfo == null) {
				
				searcherDepthInfo = new SearcherDepthInfo();
				
				depthsInfo.put(info.getDepth(), searcherDepthInfo);
			}
			
			searcherDepthInfo.update(info);
		}
		
		
		public ISearchInfo getLastSearchInfo(int depth) {
			SearcherDepthInfo searcherDepthInfo = depthsInfo.get(depth);
			if (searcherDepthInfo == null) {
				return null;
			}
			return searcherDepthInfo.getLastSearchInfo();
		}
		
		
		public int getMaxDepth() {
			
			int max_depth = 0;
			for (Integer depth: depthsInfo.keySet()) {
				if (depth > max_depth) {
					max_depth = depth;
				}
			}
			
			return max_depth;
		}
		
		
		private static class SearcherDepthInfo {
			
			
			private List<ISearchInfo> infos;
			
			
			public SearcherDepthInfo() {
				
				infos = new ArrayList<ISearchInfo>();
			}
			
			
			void update(ISearchInfo info) {
				
				infos.add(info);
			}
			
			
			public ISearchInfo getLastSearchInfo() {
				
				int last_index = infos.size() - 1;
				
				if (last_index < 0) {
					
					return null;
				}
				
				return infos.get(last_index);
			}
		}
	}
	
	
	private class MoveInfo {
		
		
		private int best_eval;
		private int sum;
		private int cnt;
		
		private boolean is_mate;
		private int mate_val;
		
		private ISearchInfo best_info;
		
		
		MoveInfo(ISearchInfo first_info) {
			
			best_eval = ISearch.MIN;
			mate_val = ISearch.MIN;
			
			addInfo(first_info);
		}
		
		
		void addInfo(ISearchInfo info) {
			
			int new_eval = info.getEval();
			
			if (is_mate) {
				
				if (SearchUtils.isMateVal(new_eval)) {
					
					if (new_eval > mate_val) {
						
						mate_val = new_eval;
						best_info = info;
					}
				}
				
			} else {
				
				if (SearchUtils.isMateVal(new_eval)) {
					
					is_mate = true;
					
					mate_val = new_eval;
					best_info = info;
					
				} else {
					
					if (new_eval > best_eval) {
						best_eval = new_eval;
						best_info = info;
					}
					
					sum += new_eval;
				}
			}
			
			cnt += 1;
		}
		
		
		int getEval() {
			
			if (is_mate) {
				
				return mate_val;
			}
			
			return sum / cnt;
		}
		
		
		int getCount() {
			
			return cnt;
		}
	}
}
