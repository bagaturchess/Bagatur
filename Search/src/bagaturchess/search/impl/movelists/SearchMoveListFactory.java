package bagaturchess.search.impl.movelists;


import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMoveListFactory implements ISearchMoveListFactory {


	public SearchMoveListFactory() {
	}
	
	
	@Override
	public IMoveList createListAll_Root(SearchEnv env, int ply, boolean onTheFlySorting) {
		return new SortedMoveList_Root(333, env, onTheFlySorting);
	}
	
	
	@Override
	public IMoveList createListHistory(SearchEnv env, int ply, boolean onTheFlySorting) {
		return new SortedMoveList_History(333, env, ply, onTheFlySorting);
	}
	
	
	@Override
	public IMoveList createListStaticEval(SearchEnv env, int ply, boolean onTheFlySorting) {
		return new SortedMoveList_StaticEval(333, env, ply, onTheFlySorting);
	}
	
	
	@Override
	public IMoveList createListCaptures(SearchEnv env, boolean onTheFlySorting) {
		//return new SortedMoveList_MVVLVA(333, env, onTheFlySorting);
		return new SortedMoveList_SEEMVVLVA(333, env, onTheFlySorting);
		//return new SortedMoveList_CaptureHistory(333, env, onTheFlySorting);
	}
}
