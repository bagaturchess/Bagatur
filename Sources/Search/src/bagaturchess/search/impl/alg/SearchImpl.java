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
import bagaturchess.bitboard.impl.Constants;
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
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.tpt.TTEntry_BaseImpl;
import bagaturchess.search.impl.utils.SearchUtils;
import bagaturchess.uci.api.ChannelManager;


public abstract class SearchImpl extends SearchUtils implements ISearch {
	
	
	private ISearchConfig_AB searchConfig;
	
	private static final int DRAW_SCORE_O = -50;
	private static final int DRAW_SCORE_E = 50;
	
	protected ISearchMoveList[] lists_all;
	protected ISearchMoveList[] lists_all_root;
	protected ISearchMoveList[] lists_escapes;
	protected ISearchMoveList[] lists_capsproms;
	protected SearchEnv env;
	
	protected int[] buff_tpt_depthtracking = new int[1];
	
	protected ITTEntry[] tt_entries_per_ply = new ITTEntry[ISearch.MAX_DEPTH];
	
	
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
	
	
	protected int getDrawScores(int rootColour) {
		//int scores = getEnv().getBitboard().getMaterialFactor().interpolateByFactor(-50, 50);
		int scores = getEnv().getBitboard().getMaterialFactor().interpolateByFactor(DRAW_SCORE_O, DRAW_SCORE_E);
		if (getEnv().getBitboard().getColourToMove() != rootColour) {
			scores = -scores;
		}
		return scores;
	}
	
	
	protected static SharedData getOrCreateSearchEnv(Object[] args) {
		if (args[2] == null) {
			return new SharedData(ChannelManager.getChannel(), (IEngineConfig)args[1]);
		} else {
			return (SharedData) args[2];
		}
	}
	
	
	public void newSearch() {
		
		env.getHistory_All().newSearch();
		env.getHistory_InCheck().newSearch();
		
		env.getMoveListFactory().newSearch();
		//env.getEval().beforeSearch();
		
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
		//Channel.dump("ISearch with env: " + this.getEnv());
		env.clear();
	}
	
	
	protected int[] gtb_probe_result = new int[2];
	
	
	protected int roughEval(int depth, int rootColour) {
		
		int roughEval = env.getEval().roughEval(depth, rootColour);
		
		return roughEval;
	}
	
	
	protected int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		int lazy_eval = env.getEval().lazyEval(depth, alpha, beta, rootColour);
		
		/*if (Math.abs(lazy_eval) <= getEnv().getBitboard().getMaterialFactor().interpolateByFactor(50, 15)) {
			if (lazy_eval > 0) {
				lazy_eval = 0;//-result;
			}
		}*/
		
		return lazy_eval;
		//return env.getEval().roughEval(depth, rootColour);//(depth, alpha, beta, rootColour);
	}
	
	
	protected int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int full_eval = (int) env.getEval().fullEval(depth, alpha, beta, rootColour);
		
		/*if (Math.abs(full_eval) <= getEnv().getBitboard().getMaterialFactor().interpolateByFactor(50, 15)) {
			if (full_eval > 0) {
				full_eval = 0;//-result;
			}
		}*/
		
		return full_eval;
		//return env.getEval().lazyEval(depth, alpha, beta, rootColour);
		//return env.getEval().roughEval(depth, rootColour);//(depth, alpha, beta, rootColour);
	}
	
	
	protected boolean isDraw() {
		return env.getBitboard().getStateRepetition() >= 2
				|| env.getBitboard().isDraw50movesRule()
				|| !env.getBitboard().hasSufficientMaterial();
	}
	
	
	protected boolean isDrawPV(int depth) {
		
		//Skip the draw check for the root, we need at least one move in the pv
		if (depth == 0) {
			return false;
		}
		
		if (env.getBitboard().getStateRepetition() >= 3
				|| env.getBitboard().isDraw50movesRule()) {
			return true;
		}
		
		if (!env.getBitboard().hasSufficientMaterial()) {
			return true;
		}
		
		return false;
	}
	
	
	protected int root_search(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int depth, int alpha_org, int beta, int[] prevPV, int rootColour, boolean useMateDistancePrunning) {
		throw new IllegalStateException();
	}
	
	private void testPV(ISearchInfo info) {
		
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
