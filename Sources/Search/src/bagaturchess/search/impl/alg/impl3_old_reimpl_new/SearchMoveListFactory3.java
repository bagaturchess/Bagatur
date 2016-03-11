package bagaturchess.search.impl.alg.impl3_old_reimpl_new;


import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.alg.impl5_scratch.ListAll_InCheck;
import bagaturchess.search.impl.alg.impl5_scratch.SearchMoveListFactory5;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory3 extends SearchMoveListFactory5 {

	
	public SearchMoveListFactory3() {
	}
	
	
	@Override
	public ISearchMoveList createListAll_inCheck(SearchEnv env) {
		//return new ListAll_InCheck(env);
		return new ListAll_InCheck(env);
		//return new ListAll_old(env, orderingStatistics);
	}
}
