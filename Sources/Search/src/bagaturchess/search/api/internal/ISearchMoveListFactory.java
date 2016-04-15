package bagaturchess.search.api.internal;


import bagaturchess.search.impl.env.SearchEnv;


public interface ISearchMoveListFactory {
	public ISearchMoveList createListAll(SearchEnv env);
	public ISearchMoveList createListAll_inCheck(SearchEnv env);
	public ISearchMoveList createListCaptures(SearchEnv env);
	public void newSearch();
}
