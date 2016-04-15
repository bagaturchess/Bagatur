package bagaturchess.search.impl.alg.iter;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory implements ISearchMoveListFactory {

	
	protected OrderingStatistics[] stats_all;
	protected OrderingStatistics orderingStatistics = new OrderingStatistics();
	
	
	public OrderingStatistics getOrderingStatistics() {
		return orderingStatistics;
	}


	public SearchMoveListFactory() {
	}
	
	
	@Override
	public ISearchMoveList createListAll(SearchEnv env) {
		return new ListAll(env, orderingStatistics);
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
		orderingStatistics.normalize();
	}

}
