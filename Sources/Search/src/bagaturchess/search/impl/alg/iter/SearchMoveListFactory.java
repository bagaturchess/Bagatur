package bagaturchess.search.impl.alg.iter;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory implements ISearchMoveListFactory {

	
	protected OrderingStatistics[] stats_all;
	protected OrderingStatistics rootOrderingStatistics;


	public SearchMoveListFactory() {
	}
	
	
	@Override
	public ISearchMoveList createListAll(SearchEnv env) {
		
		OrderingStatistics cur = new OrderingStatistics();
		
		if (rootOrderingStatistics == null) {
			rootOrderingStatistics = cur;
		}
		return new ListAll(env, cur);
	}


	@Override
	public ISearchMoveList createListCaptures(SearchEnv env) {
		return new ListCapsProm(env);
	}


	@Override
	public ISearchMoveList createListAll_inCheck(SearchEnv env) {
		return new ListKingEscapes(env);
	}
	
	
	@Override
	public void newSearch() {
	}

	@Override
	public String toString() {
		String msg = "";
		
		if (rootOrderingStatistics != null) {
			msg += rootOrderingStatistics.toString();
		}
		
		return msg;
	}
}
