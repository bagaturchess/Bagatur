/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.search.impl.alg;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.search.api.IEngineConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.history.IHistoryTable;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.tpt.TTEntry_BaseImpl;
import bagaturchess.uci.api.ChannelManager;


public abstract class SearchImpl implements ISearch {
	
	
	private ISearchConfig_AB searchConfig;
	
	protected SearchEnv env;
	
	protected ISearchMoveList[] lists_all;
	protected ISearchMoveList[] lists_all_root;
	protected ISearchMoveList[] lists_escapes;
	protected ISearchMoveList[] lists_capsproms;
	
	protected int[] buff_tpt_depthtracking 		= new int[1];
	
	protected ITTEntry[] tt_entries_per_ply 	= new ITTEntry[ISearch.MAX_DEPTH];
	
	protected int[] gtb_probe_result 			= new int[2];
	
	
	public void setup(IBitBoard bitboardForSetup) {
		
		env.getBitboard().revert();
		
		int count = bitboardForSetup.getPlayedMovesCount();
		int[] moves = bitboardForSetup.getPlayedMoves();
		
		for (int i=0; i<count; i++) {
			
			env.getBitboard().makeMoveForward(moves[i]);
		}
	}
	
	
	public SearchImpl(SearchEnv _env) {
		
		env = _env;

		lists_all = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_all.length; i++) {
			lists_all[i] = env.getMoveListFactory().createListAll(env);
		}
		
		lists_all_root = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_all_root.length; i++) {
			lists_all_root[i] = env.getMoveListFactory().createListAll_Root(env);
		}
		
		lists_escapes = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_escapes.length; i++) {
			lists_escapes[i] = 	env.getMoveListFactory().createListAll_inCheck(env);
		}
		
		lists_capsproms = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_capsproms.length; i++) {
			lists_capsproms[i] = env.getMoveListFactory().createListCaptures(env);
		}
		
		for (int i=0; i<tt_entries_per_ply.length; i++) {
			tt_entries_per_ply[i] = new TTEntry_BaseImpl();
		}
		
		initParams(env.getSearchConfig());
	}
	
	
	private void initParams(ISearchConfig_AB cfg) {
		
		searchConfig = cfg;
	}
	
	
	public ISearchConfig_AB getSearchConfig() {
		
		return searchConfig;
	}
	
	
	protected IHistoryTable getHistory(boolean inCheck) {
		
		return inCheck ? env.getHistory_InCheck() : env.getHistory_All();
	}
	
	
	protected static SharedData getOrCreateSearchEnv(Object[] args) {
		
		if (args[2] == null) {
			
			throw new IllegalStateException();
			//return new SharedData(ChannelManager.getChannel(), (IEngineConfig) args[1]);
			
		} else {
			
			return (SharedData) args[2];
		}
	}
	
	
	public void newSearch() {
		
		env.getHistory_All().newSearch();
		env.getHistory_InCheck().newSearch();
		
		env.getMoveListFactory().newSearch();
		
		env.getOrderingStatistics().normalize();
		
		for (int i=0; i<lists_all.length; i++) {
			
			lists_all[i].newSearch();
		}
		
		getEnv().getEval().beforeSearch();
	}
	
	
	public SearchEnv getEnv() {
		return env;
	}
	
	
	public void newGame() {
		env.clear();
	}
	
	
	protected boolean isDraw(boolean isPV) {
		
		if (!isPV && env.getBitboard().getStateRepetition() >= 2) {
			
			return true;
		}
		
		if (env.getBitboard().getStateRepetition() >= 3
				|| env.getBitboard().isDraw50movesRule()) {
			
			return true;
		}
		
		if (!env.getBitboard().hasSufficientMatingMaterial()) {
			
			return true;
		}
		
		
		return false;
	}
	
	
	public int getDrawScores(int root_player_colour) {
		
		return SearchUtils.getDrawScores(getEnv().getBitboard().getMaterialFactor(), root_player_colour);
	}
	
	
	protected int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		return (int) env.getEval().fullEval(depth, alpha, beta, rootColour);
	}
	
	
	protected int roughEval(int depth, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	protected int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	protected int root_search(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int depth, int alpha_org, int beta, int[] prevPV, int rootColour, boolean useMateDistancePrunning) {
		throw new IllegalStateException();
	}
	
	
	protected void testPV(ISearchInfo info) {
		
		//if (!env.getEngineConfiguration().verifyPVAfterSearch()) return;
		
		int rootColour = env.getBitboard().getColourToMove();
		
		int sign = 1;
		
		int[] moves = info.getPV();
		
		for (int i=0; i<moves.length; i++) {
			env.getBitboard().makeMoveForward(moves[i]);
			sign *= -1;
		}
		
		IEvaluator evaluator = env.getEval();
		int curEval = (int) (sign * evaluator.fullEval(0, ISearch.MIN, ISearch.MAX, rootColour));
		
		if (curEval != info.getEval()) {
			IGameStatus status = env.getBitboard().getStatus();
			if (status == IGameStatus.NONE) {
				System.out.println("SearchImpl> diff evals: curEval=" + curEval + ",	eval=" + info.getEval());
			}
		}
		
		for (int i=moves.length - 1; i >= 0; i--) {
			env.getBitboard().makeMoveBackward(moves[i]);
		}
	}
	
	public static final class RootWindowImpl implements IRootWindow {
		
		public boolean isInside(int eval, int colour) {
			
			return true;
		}
	}
}
