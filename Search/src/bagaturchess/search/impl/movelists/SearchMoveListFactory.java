package bagaturchess.search.impl.movelists;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory implements ISearchMoveListFactory {


	public SearchMoveListFactory() {
	}
	
	
	@Override
	public ISearchMoveList createListAll(SearchEnv env, int ply) {
		return new ListAll(env, ply);
	}


	@Override
	public ISearchMoveList createListAll_Root(SearchEnv env, int ply) {
		return new ListAll(env, ply);
	}
	
	
	@Override
	public ISearchMoveList createListCaptures(SearchEnv env) {
		return new ListCapsProm(env, env.getOrderingStatistics());
	}


	@Override
	public ISearchMoveList createListAll_inCheck(SearchEnv env, int ply) {
		return new ListKingEscapes(env, ply);
	}
	
	
	@Override
	public void newSearch() {
	}
}
