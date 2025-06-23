package bagaturchess.search.api.internal;


import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public interface ISearchMoveListFactory {
	public IMoveList createListAll_Root(SearchEnv env, int ply, boolean onTheFlySorting);
	public IMoveList createListHistory(SearchEnv env, int ply, boolean onTheFlySorting);
	public IMoveList createListStaticEval(SearchEnv env, int ply, boolean onTheFlySorting);
	public IMoveList createListCaptures(SearchEnv env, boolean onTheFlySorting);
}
