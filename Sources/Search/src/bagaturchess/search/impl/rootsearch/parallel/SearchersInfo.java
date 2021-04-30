package bagaturchess.search.impl.rootsearch.parallel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchUtils;
import bagaturchess.uci.api.ChannelManager;


public class SearchersInfo {

	
	private Map<IRootSearch, SearcherInfo> searchersInfo;
	private int cur_depth;
	private double nextDepthThreshold;
	
	private Map<IRootSearch, ISearchInfo> searchersNodesInfo;
	
	
	public SearchersInfo(int startDepth, double _nextDepthThreshold) {
		searchersInfo = new HashMap<IRootSearch, SearcherInfo>();
		cur_depth = startDepth;
		nextDepthThreshold = _nextDepthThreshold;
		
		searchersNodesInfo = new HashMap<IRootSearch, ISearchInfo>();
	}
	
	
	public int getCurrentDepth() {
		return cur_depth;
	}
	
	
	public void updateNodesCount(IRootSearch searcher, ISearchInfo info) {
		ISearchInfo oldInfo = searchersNodesInfo.get(searcher);
		if (oldInfo == null)  {
			searchersNodesInfo.put(searcher, info);
		} else {
			if (info.getSearchedNodes() > oldInfo.getSearchedNodes()) {
				searchersNodesInfo.put(searcher, info);
			}
		}
	}
	
	
	private long getNodesCount() {
		long nodes = 0;
		for (IRootSearch searcher: searchersNodesInfo.keySet()) {
			ISearchInfo info = searchersNodesInfo.get(searcher);
			if (info != null) {
				nodes += info.getSearchedNodes();
			}
		}
		return nodes;
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
			//throw new IllegalStateException();
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
	
	
	//Result can be null
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
	
	
	public boolean needRestart(IRootSearch searcher) {
		
		SearcherInfo searcherinfo = searchersInfo.get(searcher);
		if (searcherinfo != null && searcherinfo.getMaxDepth() < cur_depth) {
			return true;
		}
		
		return false;
	}
	
	
	public ISearchInfo getNewInfoToSendIfPresented() {
		
		if (hasDepthInfo(cur_depth + 1)) {
			
			cur_depth++;
			
			if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("SearchersInfo: increase depth to " + cur_depth);
		}
		
		ISearchInfo cur_depth_info = getAccumulatedInfo(cur_depth);
		
		return cur_depth_info;
	}
	
	
	//Result can be null
	private ISearchInfo getAccumulatedInfo(int depth) {
		
		
		long totalNodes = getNodesCount();
		/*for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			if (cur_searcher_infos != null){
				//System.out.println(cur_searcher_infos + " " + cur_searcher_infos.getSearchedNodes());
				totalNodes += cur_searcher_infos.getSearchedNodes();
			}
		}*/
		
		
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
		info_to_send.setSearchedNodes(totalNodes);
		
		
		return info_to_send;
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
	
	
	private static class SearcherInfo {
		
		
		private Map<Integer, SearcherDepthInfo> depthsInfo;
		
		
		public SearcherInfo() {
			depthsInfo = new HashMap<Integer, SearchersInfo.SearcherInfo.SearcherDepthInfo>();
		}
		
		
		public long getSearchedNodes() {
			
			ISearchInfo last_info = getLastSearchInfo(getMaxDepth());
			
			if (last_info == null) {
				return 0;
			}
			
			return last_info.getSearchedNodes();
		}


		public void update(ISearchInfo info) {
			SearcherDepthInfo searcherDepthInfo = depthsInfo.get(info.getDepth());
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
		
		
		int sum;
		int cnt;
		int best_eval;
		ISearchInfo best_info;
		
		
		MoveInfo(ISearchInfo first_info) {
			
			sum = first_info.getEval();
			cnt = 1;
			best_eval = first_info.getEval();
			best_info = first_info;
			
			if (best_info == null) {
				throw new IllegalStateException("best_info == null");
			}
		}
		
		
		void addInfo(ISearchInfo info) {
			
			sum += info.getEval();
			cnt += 1;
			if (info.getEval() > best_eval) {
				best_eval = info.getEval();
				best_info = info;
			}
			
			if (best_info == null) {
				throw new IllegalStateException("best_info == null");
			}
		}
		
		
		int getEval() {
			if (SearchUtils.isMateVal(best_eval)) {
				return best_eval;
			}
			return sum / cnt;
		}
	}
}
