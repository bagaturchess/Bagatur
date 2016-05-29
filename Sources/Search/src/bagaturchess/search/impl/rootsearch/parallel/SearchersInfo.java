package bagaturchess.search.impl.rootsearch.parallel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.SearchUtils;


public class SearchersInfo {

	
	private Map<IRootSearch, SearcherInfo> searchersInfo;
	
	
	public SearchersInfo() {
		searchersInfo = new HashMap<IRootSearch, SearcherInfo>();
	}
	
	
	public void update(IRootSearch searcher, ISearchInfo info) {
		
		SearcherInfo searcherinfo = searchersInfo.get(searcher);
		if (searcherinfo == null) {
			searcherinfo = new SearcherInfo();
			searchersInfo.put(searcher, searcherinfo);
		}
		
		searcherinfo.update(info);
	}
	
	
	//Rusult can be null
	public ISearchInfo getInfoToSend(int depth) {
		
		
		long totalNodes = 0;
		Map<Integer, MoveInfo> movesInfoPerDepth = new HashMap<Integer, MoveInfo>();
		
		
		for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			ISearchInfo cur_last_info = cur_searcher_infos.getLastSearchInfo(depth);
			totalNodes += cur_searcher_infos.getSearchedNodes();
			
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
	
	
	public boolean hasDepthInfo(int depth) {
		
		ISearchInfo prevDepthInfo = getInfoToSend(depth - 1);
		
		int countResponded = 0;
		for (IRootSearch cur_searcher: searchersInfo.keySet()) {
			
			SearcherInfo cur_searcher_infos = searchersInfo.get(cur_searcher);
			ISearchInfo depth_last_info = cur_searcher_infos.getLastSearchInfo(depth);
			
			if (depth_last_info != null) {
				
				countResponded++;
				
				if (prevDepthInfo == null) {
					return true;
				}
				
				if (depth_last_info.getBestMove() == prevDepthInfo.getBestMove()) {
					return true;
				}
			}
		}
		
		return countResponded > 0 && countResponded == searchersInfo.size();
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
		}
		
		void addInfo(ISearchInfo info) {
			sum += info.getEval();
			cnt += 1;
			if (info.getEval() > best_eval) {
				best_eval = info.getEval();
				best_info = info;
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
