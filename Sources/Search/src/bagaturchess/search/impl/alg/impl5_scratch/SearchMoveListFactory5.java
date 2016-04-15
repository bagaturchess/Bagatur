package bagaturchess.search.impl.alg.impl5_scratch;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.alg.iter.ListKingEscapes;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory5 implements ISearchMoveListFactory {

	
	//protected OrderingStatistics orderingStatistics = new OrderingStatistics();
	
	
	public SearchMoveListFactory5() {
	}
	
	
	@Override
	public ISearchMoveList createListAll(SearchEnv env) {
		return new ListAll(env);
		//return new ListAll_old(env, orderingStatistics);
	}
	
	
	@Override
	public ISearchMoveList createListCaptures(SearchEnv env) {
		return new ListCapsProm(env);
	}


	@Override
	public ISearchMoveList createListAll_inCheck(SearchEnv env) {
		//return new ListAll_InCheck(env);
		//return new ListAll(env);
		//return new ListAll_old(env, orderingStatistics);
		return new ListKingEscapes(env);
		//return new ListAll_InCheck(env);
	}
	
	
	@Override
	public void newSearch() {
		//orderingStatistics.normalize();
	}
}
