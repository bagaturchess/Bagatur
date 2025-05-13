package bagaturchess.search.api.internal;


import bagaturchess.search.impl.env.SearchEnv;


public interface ISearchMoveListFactory {
	public ISearchMoveList createListAll(SearchEnv env, int ply);
	public ISearchMoveList createListAll_Root(SearchEnv env, int ply);
	public ISearchMoveList createListAll_inCheck(SearchEnv env, int ply);
	public ISearchMoveList createListCaptures(SearchEnv env);
	public void newSearch();
}
