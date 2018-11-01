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


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.egtb.gaviota.GTBProbeInput;
import bagaturchess.egtb.gaviota.GTBProbeOutput;
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
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchUtils;
import bagaturchess.uci.api.ChannelManager;


public abstract class SearchImpl extends SearchUtils implements ISearch {
	
	
	private ISearchConfig_AB searchConfig;
	
	private static final int DRAW_SCORE_O = -50;
	private static final int DRAW_SCORE_E = 50;
	
	protected ISearchMoveList[] lists_all;
	protected ISearchMoveList[] lists_escapes;
	protected ISearchMoveList[] lists_capsproms;
	protected PVManager pvman;
	protected SearchEnv env;
	
	protected int[] buff_tpt_depthtracking = new int[1];
	
	protected GTBProbeInput temp_input = new GTBProbeInput();
	
	private List<Integer> pv_buffer = new ArrayList<Integer>();
	
	
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
		
		pvman = new PVManager(MAX_DEPTH);
		lists_all = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_all.length; i++) {
			lists_all[i] = env.getMoveListFactory().createListAll(env);
		}
		
		lists_escapes = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_escapes.length; i++) {
			lists_escapes[i] = 	env.getMoveListFactory().createListAll_inCheck(env);
		}
		
		lists_capsproms = new ISearchMoveList[MAX_DEPTH];
		for (int i=0; i<lists_capsproms.length; i++) {
			lists_capsproms[i] = env.getMoveListFactory().createListCaptures(env);
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
	

	protected int sentFromTPT(ISearchMediator mediator, int min_depth) {
		int depth = 0;
		
		env.getTPT().lock();
		ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();

		TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());

		if (tptEntry != null
				&& tptEntry.isExact()
				//&& tptEntry.getBestMove_lower() != 0
				) {

			PVNode node = pvman.load(0);

			node.bestmove = 0;
			node.eval = MIN;
			node.nullmove = false;
			node.leaf = true;
			
			depth = tptEntry.getDepth();

			env.getTPT().lock();
			buff_tpt_depthtracking[0] = 0;
			extractFromTPT(info, depth, node, true, buff_tpt_depthtracking, env.getBitboard().getColourToMove(), env.getTPT());
			env.getTPT().unlock();

			node.eval = tptEntry.getLowerBound();

			pv_buffer.clear();

			int pv[] = PVNode.convertPV(pvman.load(0), pv_buffer);
			if (pv != null && pv.length > 0) {
				info.setPV(pv);
				info.setBestMove(info.getPV()[0]);
				info.setEval(node.eval);
				info.setDepth(depth);
				
				depth = Math.min(depth, buff_tpt_depthtracking[0]);

				if (mediator != null && depth >= min_depth) {
					mediator.changedMajor(info);
					if (DEBUGSearch.DEBUG_MODE) testPV(info);
				}

			} else {
				depth = 0;
			}
		}
		env.getTPT().unlock();

		return depth + 1;
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
	}
	
	
	public abstract int pv_search(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int alpha, int beta,
			int prevbest, int prevprevbest, int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove, int mateMove, boolean useMateDistancePrunning);
	
	/*protected abstract int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV, int rootColour, int totalLMReduction, int materialGain, boolean inNullMove, int mateMove, boolean useMateDistancePrunning);
	*/
	
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
	
	
	protected int extractEGTBMateValue(int depth) {
		int egtb_val = 0;
		if (gtb_probe_result[0] == GTBProbeOutput.DRAW) {
			throw new IllegalStateException("GTBProbeOutput.DRAW");
		} else if (gtb_probe_result[0] == GTBProbeOutput.WMATE) {
			egtb_val = getMateVal(Math.min(MAX_DEPTH, depth + gtb_probe_result[1]));
			//egtb_val = 700 + 3 * getMateVal(Math.min(MAX_DEPTH, depth + gtb_probe_result[1])) / ISearch.MAX_MAT_INTERVAL;
			if (env.getBitboard().getColourToMove() == Constants.COLOUR_BLACK) {
				egtb_val = -egtb_val;
			}
		} else if (gtb_probe_result[0] == GTBProbeOutput.BMATE) {
			egtb_val = getMateVal(Math.min(MAX_DEPTH, depth + gtb_probe_result[1]));
			//egtb_val = 700 + 3 * getMateVal(Math.min(MAX_DEPTH, depth + gtb_probe_result[1])) / ISearch.MAX_MAT_INTERVAL;;
			if (env.getBitboard().getColourToMove() == Constants.COLOUR_WHITE) {
				egtb_val = -egtb_val;
			}
		}
		//System.out.println("egtb_val=" + egtb_val);
		return egtb_val;
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
	
	
	protected boolean isPVNode(int cur_eval, int best_eval, int alpha, int beta) {
		return cur_eval > alpha && cur_eval < beta;
	}
	
	protected boolean isNonAlphaNode(int cur_eval, int best_eval, int alpha, int beta) {
		return cur_eval > alpha;
	}
	
	@Override
	public void search(ISearchMediator mediator) {
		search(mediator, MAX_DEPTH, true);
	}
	
	
	@Override
	public void search(ISearchMediator mediator, int max_iterations, boolean useMateDistancePrunning) {
		search(mediator, 1, max_iterations, useMateDistancePrunning);
	}
	
	
	@Override
	public void search(ISearchMediator mediator, int startIteration,
			int max_iterations, boolean useMateDistancePrunning) {
		throw new UnsupportedOperationException();
	}
	
	
	protected boolean hasExpectedEval() {
		env.getTPT().lock();
		TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
		env.getTPT().unlock();
		return tptEntry != null;
	}
	
	protected int getExpectedEval() {
		env.getTPT().lock();
		TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
		if (tptEntry != null) {
			env.getTPT().unlock();
			return tptEntry.getLowerBound();
		}
		env.getTPT().unlock();
		return 0;
	}
	
	
	protected boolean extractFromTPT(ISearchInfo info, int depth, PVNode result, boolean useLower, int[] depthtracking, int rootColour, TPTable tpt) {
		//env.getTPT().lock();
		boolean res = extractFromTPT(info, depth, result, useLower, MIN, MAX, depthtracking, rootColour, tpt);
		/*//TODO: Consider if (res) {
			result.eval = DRAW;
		}*/
		//env.getTPT().unlock();
		return res;
	}
	
	private boolean extractFromTPT(ISearchInfo info, int depth, PVNode result, boolean useLower, int alpha, int beta, int[] depthtracking, int rootColour, TPTable tpt) {
		
		//if (true) throw new IllegalStateException("Not thread safe"); 
		
		if (result == null) {
			return false;
		}
		
		depthtracking[0]++;
		
		result.bestmove = 0;
		result.leaf = true;
		result.nullmove = false;
		
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		if (depth <= 0) {
			//return false;
		}
		
		if (isDrawPV(depth)) {
			//result.eval = getDrawScores(rootColour);
			return true;
		}
		
		boolean draw = false;
		
		long hashkey = env.getBitboard().getHashKey();
		
		TPTEntry entry = tpt.get(hashkey);
		
		if (entry == null) {
			return false;
		}
		
		if (entry != null) {
			if (entry.getDepth() >= depth) {
				
				//info.setSearchedNodes(info.getSearchedNodes() + 1);
				
				if (entry.isExact()) {
					result.bestmove = entry.getBestMove_lower();
				} else if (entry.getLowerBound() >= beta) {
					result.bestmove = entry.getBestMove_lower();
				} else if (entry.getUpperBound() <= alpha) {
					result.bestmove = entry.getBestMove_upper();
				} else {
					result.bestmove = useLower ? entry.getBestMove_lower() : entry.getBestMove_upper();
				}
				
				/*if (result.bestmove == 0) {
					result.bestmove = useLower ? entry.getBestMove_upper() : entry.getBestMove_lower();
					useLower = !useLower;
				}*/
				
				if (result.bestmove == 0) {
					return false;
				}
				
				//if (result.bestmove != 0 && !env.getBitboard().isPossible(result.bestmove)) {
					//if (true) throw new IllegalStateException(env.getBitboard() +
					//		"\r\n Not possible " + MoveInt.moveToString(result.bestmove));
				//	return false;
				//}
				
				result.leaf = false;
				
				if (result.bestmove == 0) {
					env.getBitboard().makeNullMoveForward();
				} else {
					env.getBitboard().makeMoveForward(result.bestmove);
				}
				
				draw = extractFromTPT(info, depth - 1, result.child, !useLower, -beta, -alpha, depthtracking, rootColour, tpt);
				/*if (draw) {
					result.eval = getDrawScores(rootColour);
				}*/
				
				if (result.bestmove == 0) {
					env.getBitboard().makeNullMoveBackward();
				} else {
					env.getBitboard().makeMoveBackward(result.bestmove);
				}
			}
		}
		
		return draw;
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
	
	protected void testNode(PVNode root) {
		
		//if (true) return;
		
		//if (!env.getEngineConfiguration().verifyPVAfterSearch()) return;
		
		int rootColour = env.getBitboard().getColourToMove();
		
		int sign = 1;
		
		//int[] moves = info.getPV();
		int rootEval = root.eval;
		
		env.getBitboard().mark();
		
		PVNode curr = root;
		while (!isLeaf(curr)) {
			
			if (curr.bestmove == 0) {
				env.getBitboard().makeNullMoveForward();
			} else {
				env.getBitboard().makeMoveForward(curr.bestmove);
			}
			sign *= -1;
			
			curr = curr.child;
			if (isLeaf(curr)) {
				break;
			}
		}
		
		IEvaluator evaluator = env.getEval();
		int curEval = (int) (sign * evaluator.fullEval(0, ISearch.MIN, ISearch.MAX, rootColour));
		
		if (curEval != rootEval) {
			IGameStatus status = env.getBitboard().getStatus();
			if (status == IGameStatus.NONE) {
				System.out.println("SearchImpl> diff evals: curEval=" + curEval + ",	eval=" + rootEval);
			}
		}
		
		env.getBitboard().reset();
		
		/*
		
		do {
			if (curr.bestmove == 0) {
				env.getBitboard().makeNullMoveBackward();
			} else {
				env.getBitboard().makeMoveBackward(curr.bestmove);
			}
			curr = curr.parent;
		} while (curr != root);
		
		*/
	}
	
	private boolean isLeaf(PVNode node) {
		if (node == null) {
			return true;
		}
		return node.leaf || (node.bestmove == 0 && !node.nullmove);
	}
	
	public PVManager getPvman() {
		return pvman;
	}
	
	public static final class RootWindowImpl implements IRootWindow {
		public boolean isInside(int eval, int colour) {
			return true;
		}
	}
}
