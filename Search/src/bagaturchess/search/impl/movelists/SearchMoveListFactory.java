package bagaturchess.search.impl.movelists;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory implements ISearchMoveListFactory {


	public SearchMoveListFactory() {
	}
	
	
	@Override
	public ISearchMoveList createListAll(SearchEnv env) {
		return new ListAll(env, env.getOrderingStatistics());
	}


	@Override
	public ISearchMoveList createListAll_Root(SearchEnv env) {
		return new ListAll_Root(env, env.getOrderingStatistics());
	}
	
	
	@Override
	public ISearchMoveList createListCaptures(SearchEnv env) {
		return new ListCapsProm(env, env.getOrderingStatistics());
	}


	@Override
	public ISearchMoveList createListAll_inCheck(SearchEnv env) {
		return new ListKingEscapes(env, env.getOrderingStatistics());
	}
	
	
	@Override
	public void newSearch() {
	}
}
