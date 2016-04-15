package bagaturchess.search.impl.alg.impl7_old_reimpl;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.alg.iter.ListKingEscapes;
import bagaturchess.search.impl.alg.iter.OrderingStatistics;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory7 implements ISearchMoveListFactory {

	
	protected OrderingStatistics[] stats_all;
	protected OrderingStatistics orderingStatistics = new OrderingStatistics();
	protected OrderingStatistics orderingStatistics_InCheck = new OrderingStatistics();
	
	
	public SearchMoveListFactory7() {
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
		return new ListAll_InCheck(env, orderingStatistics_InCheck);
	}
	
	
	@Override
	public void newSearch() {
		orderingStatistics.normalize();		
	}


	@Override
	public OrderingStatistics getOrderingStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
