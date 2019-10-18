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
package bagaturchess.search.impl.alg.impl0;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;

import bagaturchess.search.impl.alg.BacktrackingInfo;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private boolean FORWARD_PRUNING						= true;
	private static final int[] MARGIN_STATIC_NULLMOVE 	= { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] MARGIN_RAZORING 			= { 0, 240, 280, 320 };
	
	private static final int[][] LMR_REDUCTIONS = new int[64][64];
	static {
		for (int searchedCount = 0; searchedCount < 64; searchedCount++) {
			for (int restdepth = 0; restdepth < 64; restdepth++) {
				LMR_REDUCTIONS[searchedCount][restdepth] = 1 + (int) Math.ceil(Math.max(1, Math.log(searchedCount) * Math.log(restdepth) / (double) 2));
			}
		}
	}
	
	private static final int[] STATIC_PRUNING_MOVE_COUNT = new int[64];
	static {
		for (int restdepth = 0; restdepth < STATIC_PRUNING_MOVE_COUNT.length; restdepth++) {
			STATIC_PRUNING_MOVE_COUNT[restdepth] = (int) (3 + Math.pow(restdepth, 2));
		}
	}
	
	private static final double[] STATIC_PRUNING_HISTORY = new double[64];
	static {
		for (int restdepth = 0; restdepth < STATIC_PRUNING_HISTORY.length; restdepth++) {
			STATIC_PRUNING_HISTORY[restdepth] = 0.32 / Math.pow(2, restdepth);
		}
	}
	
	private BacktrackingInfo[] backtracking 			= new BacktrackingInfo[MAX_DEPTH + 1];
	
	private static final double EVAL_DIFF_MAX 			= 50;
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	public Search_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		super(_env);
		for (int i=0; i<backtracking.length; i++) {
			backtracking[i] = new BacktrackingInfo(); 
		}
	}
	
	
	@Override
	public String toString() {
		String result = "";//"" + this + " ";
		
		result += Thread.currentThread().getName() + "	>	";
		result += getEnv().toString();
		
		return result;
	}
	
	
	public void newSearch() {
		
		super.newSearch();
		
		lastSentMinorInfo_nodesCount = 0;
		
		lastSentMinorInfo_timestamp = 0;
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return pv_search(mediator, info, initial_maxdepth, maxdepth, depth, alpha_org, beta, rootColour);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return nullwin_search(mediator, info, initial_maxdepth, maxdepth, depth, beta, rootColour, false);
	}
	
	
	public int pv_search(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int maxdepth, int depth, int alpha_org, int beta, int rootColour) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		if (backtrackingInfo.excluded_move != 0) {
			backtrackingInfo.hash_key ^= ((long) backtrackingInfo.excluded_move);
		}
		backtrackingInfo.static_eval = lazyEval(depth, alpha_org, beta, rootColour);
		
		
		if (alpha_org >= beta) {
			throw new IllegalStateException("alpha=" + alpha_org + ", beta=" + beta);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		
		PVNode node = pvman.load(depth);
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores(rootColour);
			return node.eval;
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		int rest = normDepth(maxdepth) - depth;
		
		
		if (depth > 1
    	    	&& rest >= 7
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			if (inCheck) {
				if (!env.getBitboard().hasMoveInCheck()) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					return node.eval;
				}
			} else {
				if (!env.getBitboard().hasMoveInNonCheck()) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					return node.eval;
				}
			}
			
			int result = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
			if (result != -1) {
				int dtz = (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				int egtbscore =  SyzygyTBProbing.getSingleton().getWDLScore(wdl, depth);
				if (egtbscore > 0) {
					int distanceToDraw = 100 - env.getBitboard().getDraw50movesRule();
					if (distanceToDraw > dtz) {
						node.bestmove = 0;
						node.eval = 9 * (distanceToDraw - dtz);
						node.leaf = true;
						return node.eval;
					} else {
						node.bestmove = 0;
						node.eval = getDrawScores(rootColour);
						node.leaf = true;
						return node.eval;
					}
				} else if (egtbscore == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					return node.eval;
				}
			}
        }
    	
    	
		if (depth >= normDepth(maxdepth)) {
			node.eval = pv_qsearch(mediator, info, initial_maxdepth, depth, alpha_org, beta, rootColour);	
			return node.eval;
		}
		
		
		boolean tpt_found = false;
		boolean tpt_exact = false;
		int tpt_depth = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_depth = tptEntry.getDepth();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
		}
		env.getTPT().unlock();
		
		
		//IID PV Node
		if (!tpt_found && rest >= 3) {
			
			int reduction = 2;
			
			nullwin_search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, beta, rootColour, false);
			
			env.getTPT().lock();
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
			env.getTPT().unlock();
		}
        
        
		backtrackingInfo.static_eval = tpt_lower == MIN ? backtrackingInfo.static_eval : tpt_lower;
		
		
		//Singular move check
		boolean singleReply = env.getBitboard().hasSingleMove();
		boolean singularMove = false;
        /*if (depth > 0
         		&& !singleReply
        		&& backtrackingInfo.excluded_move == 0
        		&& rest >= 2
         		&& tpt_lower >= beta
         		&& tpt_depth >= rest / 2
        	) {
			
			int reduction = (PLY * rest) / 2;
			int singularBeta = nullwin_search(mediator, info, initial_maxdepth, maxdepth - reduction, depth, beta, rootColour, false);
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				int excluded_move = tptEntry.getBestMove_lower();
				if (excluded_move != 0) {
					backtrackingInfo.excluded_move = excluded_move;
					int singularEval = nullwin_search(mediator, info, initial_maxdepth, maxdepth - reduction, depth, singularBeta, rootColour, false);
					backtrackingInfo.excluded_move = 0;
					if (singularEval < singularBeta) {
						singularMove = true;
					}
				}				
			}
        }*/
		
		
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		int eval_inc_sum = 0;
		for (int i = depth; i >= 2; i -= 2) {
			int prev_eval = backtracking[i - 2].static_eval;
			int cur_eval = backtracking[i].static_eval;
			eval_inc_sum += (cur_eval - prev_eval);
		}
		if (eval_inc_sum < -getEvalSumMax(mediator)){
			eval_inc_sum = -getEvalSumMax(mediator);
		}
		if (eval_inc_sum > getEvalSumMax(mediator)){
			eval_inc_sum = getEvalSumMax(mediator);
		}
		
		
		ISearchMoveList list = !inCheck ? lists_all[depth] : lists_escapes[depth];
		if (!inCheck && depth <= rest) {
			list = lists_all_root[depth];
		}
		list.clear();
		list.setTptMove(tpt_move);
		list.setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
		
		
		int searchedCount = 0;
		int alpha = alpha_org;
		int best_eval = MIN;
		int best_move = 0;
		
		
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		if (cur_move != 0) {
			do {
				
				
				if (cur_move == backtrackingInfo.excluded_move) {
					continue;
				}
				
				
				if (searchedCount > 0 && cur_move == tpt_move) {
					continue;
				}
				
				
				//Build and sent minor info
				if (depth == 0) {
					info.setCurrentMove(cur_move);
					info.setCurrentMoveNumber((searchedCount + 1));
				}
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				
				boolean isCapOrProm = env.getBitboard().getMoveOps().isCaptureOrPromotion(cur_move);
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				
				boolean givesCheck = env.getBitboard().isInCheck();
				
				
				int new_maxdepth = maxdepth;
				if (depth > 0) {
					//Do extensions here
					if (singleReply) {
						new_maxdepth += PLY;
					} else {
						new_maxdepth += (PLY * eval_inc_sum * rest) / (getEvalSumMax(mediator) * (depth + rest));
					}
				}
				
				
				boolean pv_search = false;
				int cur_eval;
				if (searchedCount == 0) {
					
					pv_search = true;
					
					cur_eval = -pv_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha, rootColour);
					
					if (singularMove && cur_eval > alpha && new_maxdepth == maxdepth) {
						new_maxdepth += PLY;
						cur_eval = -pv_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha, rootColour);
					}
					
				} else {
					
					int lmrReduction = 0;
					if (!inCheck) {
						int rate = LMR_REDUCTIONS[Math.min(63, searchedCount)][Math.min(63, rest)];
						if (!isCapOrProm && !givesCheck) {
							rate += 1;
						}
						lmrReduction += PLY * rate;
					}					
					
					cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth - lmrReduction, depth + 1, -alpha, rootColour, !givesCheck);
					
					if (cur_eval > alpha) {
						
						pv_search = true;
						
						cur_eval = -pv_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha, rootColour);
					}
				}
				
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				
				searchedCount++;
				
				
				//Add history records for the current move
				list.countTotal(cur_move);
				if (cur_eval < beta) {
					getHistory(inCheck).countFailure(cur_move, rest);
				} else {
					list.countSuccess(cur_move);//Should be before addCounterMove call
					getHistory(inCheck).countSuccess(cur_move, rest);
					getHistory(inCheck).addCounterMove(env.getBitboard().getLastMove(), cur_move);
				}
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					best_move = cur_move;
					
					backtrackingInfo.best_move = best_move;
					
					if (pv_search) {
						node.bestmove = best_move;
						node.eval = best_eval;
						node.leaf = false;
						
						if (depth + 1 < MAX_DEPTH) {
							pvman.store(depth + 1, node, pvman.load(depth + 1), true);
						}
					}
					
					if (best_eval >= beta) {
						break;
					}
					
					if (best_eval > alpha) {
						alpha = best_eval; 
						//throw new IllegalStateException();
					}
				}
			} while ((cur_move = list.next()) != 0);
		}
		
		
		if (best_move != 0 && (best_eval == MIN || best_eval == MAX)) {
			throw new IllegalStateException();
		}
		
		
		if (best_move == 0) {
			if (inCheck) {
				if (searchedCount == 0) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					return node.eval;
				} else {
					throw new IllegalStateException();
				}
			} else {
				if (searchedCount == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					return node.eval;
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		
		env.getTPT().lock();
		env.getTPT().put(backtrackingInfo.hash_key, normDepth(maxdepth), depth, colourToMove, best_eval, alpha_org, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		
		return best_eval;
	}
	
	
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int maxdepth, int depth, int beta, int rootColour, boolean useStaticPrunning) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		int alpha_org = beta - 1;
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		if (backtrackingInfo.excluded_move != 0) {
			backtrackingInfo.hash_key ^= ((long) backtrackingInfo.excluded_move);
		}
		backtrackingInfo.static_eval = lazyEval(depth, alpha_org, beta, rootColour);
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		
		if (isDraw()) {
			return getDrawScores(rootColour);
		}
    	
		
		if (depth >= normDepth(maxdepth)) {
			int eval = nullwin_qsearch(mediator, info, initial_maxdepth, depth, beta, rootColour);
			return eval;
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		int rest = normDepth(maxdepth) - depth;
		
		
		if (depth > 1
				&& rest >= 7
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			if (inCheck) {
				if (!env.getBitboard().hasMoveInCheck()) {
					return -getMateVal(depth);
				}
			} else {
				if (!env.getBitboard().hasMoveInNonCheck()) {
					return getDrawScores(rootColour);
				}
			}
			
			int result = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
			if (result != -1) {
				int dtz = (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				int egtbscore =  SyzygyTBProbing.getSingleton().getWDLScore(wdl, depth);
				if (egtbscore > 0) {
					int distanceToDraw = 100 - env.getBitboard().getDraw50movesRule();
					if (distanceToDraw > dtz) {
						return 9 * (distanceToDraw - dtz);
					} else {
						return getDrawScores(rootColour);
					}
				} else if (egtbscore == 0) {
					return getDrawScores(rootColour);
				}
			}
		}
		
		
		boolean tpt_found = false;
		boolean tpt_exact = false;
		int tpt_depth = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
        
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_depth = tptEntry.getDepth();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
		}
		env.getTPT().unlock();
		
		
		if (tpt_found && tpt_depth >= rest) {
			if (tpt_exact) {
				return tpt_lower;
			} else {
				if (tpt_lower >= beta) {
					return tpt_lower;
				}
				if (tpt_upper <= alpha_org) {
					return tpt_upper;
				}
			}
		}
		
		
		backtrackingInfo.static_eval = tpt_lower == MIN ? backtrackingInfo.static_eval : tpt_lower;
		
		
        if (FORWARD_PRUNING && useStaticPrunning
                ) {
            
            if (inCheck) {
                throw new IllegalStateException("In check in useStaticPrunning");
            }
            
            
			//Static null move pruning
			if (rest < MARGIN_STATIC_NULLMOVE.length) {
				if (backtrackingInfo.static_eval >= beta + MARGIN_STATIC_NULLMOVE[rest]) {
					return backtrackingInfo.static_eval;
				}
			}
			
			
			//Razoring for all depths based on the eval deviation detected into the root node
			if (backtrackingInfo.static_eval < alpha_org - getAlphaTrustWindow(mediator, rest)) {
				int qeval = nullwin_qsearch(mediator, info, initial_maxdepth, depth, beta/*alpha_org - getAlphaTrustWindow(mediator, rest) + 1*/, rootColour);
				if (qeval <= alpha_org - getAlphaTrustWindow(mediator, rest)) {
					return qeval;
				}
			}
			
			
			//Standard Razoring
			if (rest < MARGIN_RAZORING.length) {
				if (backtrackingInfo.static_eval < alpha_org - MARGIN_RAZORING[rest]) {
					int qeval = nullwin_qsearch(mediator, info, initial_maxdepth, depth, beta/*alpha_org - MARGIN_RAZORING[rest] + 1*/, rootColour);
					if (qeval <= alpha_org - MARGIN_RAZORING[rest]) {
						return qeval;
					}
				}
			}
        }
        
		boolean hasAtLeastOnePiece = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
			env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
		
		boolean hasAtLeastThreePieces = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 9 :
			env.getBitboard().getMaterialFactor().getBlackFactor() >= 9;
		
		boolean zungzwang = false;
		if (!inCheck
				&& depth > 0
				&& hasAtLeastOnePiece
				) {
			
			if (backtrackingInfo.static_eval >= beta) {
				
				//int null_reduction = PLY * (rest >= 6 ? 3 : 2);
				int null_reduction = PLY * (rest >= 6 ? 4 : 3);
				null_reduction = (int) Math.max(null_reduction, PLY * (rest / 2));
				
				int null_maxdepth = maxdepth - null_reduction;
				
				env.getBitboard().makeNullMoveForward();
				int null_val = -nullwin_search(mediator, info, initial_maxdepth, null_maxdepth, depth + 1, -(beta - 1), rootColour, false);
				env.getBitboard().makeNullMoveBackward();
				
				if (null_val >= beta) {
					
					if (hasAtLeastThreePieces) {
						return null_val;
					}
					
					int null_val_ver = nullwin_search(mediator, info, initial_maxdepth, null_maxdepth, depth, beta, rootColour, useStaticPrunning);
					
					if (null_val_ver >= beta) {
						return null_val_ver;
					} else {
						zungzwang = true;
						//System.out.println("zungzwang hit");
					}
					
					env.getTPT().lock();
					{
						TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
						if (tptEntry != null) {
							tpt_found = true;
							tpt_exact = tptEntry.isExact();
							tpt_depth = tptEntry.getDepth();
							tpt_lower = tptEntry.getLowerBound();
							tpt_upper = tptEntry.getUpperBound();
							if (tpt_exact) {
								tpt_move = tptEntry.getBestMove_lower();
							} else if (tpt_lower >= beta) {
								tpt_move = tptEntry.getBestMove_lower();
							} else if (tpt_upper <= alpha_org) {
								tpt_move = tptEntry.getBestMove_upper();
							} else {
								tpt_move = tptEntry.getBestMove_lower();
								if (tpt_move == 0) {
									tpt_move = tptEntry.getBestMove_upper();
								}
							}
						}
					}
					env.getTPT().unlock();
				}
			}
		}
		
		
		//IID NONPV Node
		if (!tpt_found && rest >= 3) {
			
			int reduction = 2;
			
			nullwin_search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, beta, rootColour, false);
			
			env.getTPT().lock();
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_depth = tptEntry.getDepth();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
			env.getTPT().unlock();
		}
		
		
		if (tpt_found && tpt_depth >= rest) {
			if (tpt_exact) {
				return tpt_lower;
			} else {
				if (tpt_lower >= beta) {
					return tpt_lower;
				}
				if (tpt_upper <= alpha_org) {
					return tpt_upper;
				}
			}
		}
		
		
		backtrackingInfo.static_eval = tpt_lower == MIN ? backtrackingInfo.static_eval : tpt_lower;
		
		
        if (FORWARD_PRUNING && useStaticPrunning
                ) {
            
            if (inCheck) {
                throw new IllegalStateException("In check in useStaticPrunning");
            }
            
            if (backtrackingInfo.static_eval < alpha_org - getAlphaTrustWindow(mediator, rest)) {
            	return backtrackingInfo.static_eval;
            }
        }
		
		
		//Singular move check
		boolean singleReply = env.getBitboard().hasSingleMove();
		boolean singularMove = false;
        /*if (depth > 0
         		&& !singleReply
        		&& backtrackingInfo.excluded_move == 0
        		&& rest >= 2
         		&& tpt_lower >= beta
         		&& tpt_depth >= rest / 2
        	) {
			
			int reduction = (PLY * rest) / 2;
			int singularBeta = nullwin_search(mediator, info, initial_maxdepth, maxdepth - reduction, depth, beta, rootColour, false);
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				int excluded_move = tptEntry.getBestMove_lower();
				if (excluded_move != 0) {
					backtrackingInfo.excluded_move = excluded_move;
					int singularEval = nullwin_search(mediator, info, initial_maxdepth, maxdepth - reduction, depth, singularBeta, rootColour, false);
					backtrackingInfo.excluded_move = 0;
					if (singularEval < singularBeta) {
						singularMove = true;
					}
				}				
			}
        }*/
        
		
		int eval_inc_sum = 0;
		for (int i = depth; i >= 2; i -= 2) {
			int prev_eval = backtracking[i - 2].static_eval;
			int cur_eval = backtracking[i].static_eval;
			eval_inc_sum += (cur_eval - prev_eval);
		}
		if (eval_inc_sum < -getEvalSumMax(mediator)){
			eval_inc_sum = -getEvalSumMax(mediator);
		}
		if (eval_inc_sum > getEvalSumMax(mediator)){
			eval_inc_sum = getEvalSumMax(mediator);
		}
		
		double evalDiff = depth >= 2 ? backtrackingInfo.static_eval - backtracking[depth - 2].static_eval : 0;
		if (evalDiff > EVAL_DIFF_MAX) evalDiff = EVAL_DIFF_MAX;
		if (evalDiff < -EVAL_DIFF_MAX) evalDiff = -EVAL_DIFF_MAX;
		
		
		ISearchMoveList list = !inCheck ? lists_all[depth] : lists_escapes[depth];
		if (!inCheck && depth <= rest) {
			list = lists_all_root[depth];
		}
		list.clear();
		list.setTptMove(tpt_move);
		list.setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
		
		
		int searchedCount = 0;
		int best_eval = MIN;
		int best_move = 0;
		
		
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		if (cur_move != 0) {
			do {
				
				
				if (cur_move == backtrackingInfo.excluded_move) {
					continue;
				}
				
				
				if (searchedCount > 0 && cur_move == tpt_move) {
					continue;
				}
				
				
				//Build and sent minor info
				if (depth == 0) {
					info.setCurrentMove(cur_move);
					info.setCurrentMoveNumber((searchedCount + 1));
				}
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				
				boolean isCapOrProm = env.getBitboard().getMoveOps().isCaptureOrPromotion(cur_move);
				boolean givesCheck = env.getBitboard().isCheckMove(cur_move);
				
				
				if (!inCheck && !givesCheck) {
					
					if (searchedCount >= 4 && rest <= 8 && depth >= rest) {
						
						if (!isCapOrProm) {
							
							//Static pruning - move count based
							if (searchedCount >= STATIC_PRUNING_MOVE_COUNT[rest]) {
								continue;
							}
							
							//Static pruning - history based
							if (getHistory(inCheck).getScores(cur_move) <= STATIC_PRUNING_HISTORY[rest]) {
		 						continue;
		 					}
							
							//Static pruning - evaluation based
							if (evalDiff < -(EVAL_DIFF_MAX - EVAL_DIFF_MAX / rest)) {
								continue;
							}
						}
						
						//Static pruning - SEE based
						if (rest <= 6) {
							int see = env.getBitboard().getSEEScore(cur_move);
							if (see < -20 * rest * rest) {
								continue;
							}
						}
					}
				}
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				
				int new_maxdepth = maxdepth;
				if (depth > 0) {
					//Do extensions here
					if (singleReply) {
						new_maxdepth += PLY;
					} else if (zungzwang) {
						new_maxdepth += PLY;
					} else {
						new_maxdepth += (PLY * eval_inc_sum * rest) / (getEvalSumMax(mediator) * (depth + rest));
					}
				}
				
				
				int cur_eval;
				if (searchedCount == 0) {
					
					cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -alpha_org, rootColour, false);
					
					if (singularMove && cur_eval > alpha_org && new_maxdepth == maxdepth) {
						new_maxdepth += PLY;
						cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -alpha_org, rootColour, false);
					}
					
				} else {
					
					boolean staticPrunning = false;
					
					if (!givesCheck) {
						staticPrunning = true;
					}
					
					int lmrReduction = 0;
					if (!inCheck) {
						int rate = LMR_REDUCTIONS[Math.min(63, searchedCount)][Math.min(63, rest)];
						if (!isCapOrProm && !givesCheck) {
							rate += 1;
						}
						lmrReduction += PLY * rate;
					}
					
					cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth - lmrReduction, depth + 1, -alpha_org, rootColour, staticPrunning);
					
					if (cur_eval > alpha_org && (lmrReduction > 0 || staticPrunning)) {
						
						cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -alpha_org, rootColour, false);
					}
				}
				
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				
				searchedCount++;
				
				
				//Add history records for the current move
				list.countTotal(cur_move);
				if (cur_eval < beta) {
					getHistory(inCheck).countFailure(cur_move, rest);
				} else {
					list.countSuccess(cur_move);//Should be before addCounterMove call
					getHistory(inCheck).countSuccess(cur_move, rest);
					getHistory(inCheck).addCounterMove(env.getBitboard().getLastMove(), cur_move);
				}
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					best_move = cur_move;
					
					backtrackingInfo.best_move = best_move;
					
					if (best_eval >= beta) {
						break;
					}
					
					if (best_eval > alpha_org) {
						throw new IllegalStateException(); 
					}
				}
			} while ((cur_move = list.next()) != 0);
		}
		
		
		if (best_move != 0 && (best_eval == MIN || best_eval == MAX)) {
			throw new IllegalStateException();
		}
		
		
		if (best_move == 0) {
			if (inCheck) {
				if (searchedCount == 0) {
					return -getMateVal(depth);
				} else {
					throw new IllegalStateException();
				}
			} else {
				if (searchedCount == 0) {
					return getDrawScores(rootColour);
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		
		env.getTPT().lock();
		env.getTPT().put(backtrackingInfo.hash_key, normDepth(maxdepth), depth, colourToMove, best_eval, alpha_org, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		
		return best_eval;
	}
	
	
	private int pv_qsearch(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int depth, int alpha_org, int beta, int rootColour) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		if (depth >= MAX_DEPTH) {
			return lazyEval(depth, alpha_org, beta, rootColour);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		
		PVNode node = pvman.load(depth);
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores(rootColour);
			return node.eval;
		}
		    	
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		boolean tpt_exact = false;
		int tpt_move = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(hashkey);
			if (tptEntry != null) {
				tpt_exact = tptEntry.isExact();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
		}
		env.getTPT().unlock();
		
		
		int staticEval = tpt_lower == MIN ? lazyEval(depth, alpha_org, beta, rootColour) : tpt_lower;
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		if (!inCheck) {
			
			//Beta cutoff
			if (staticEval >= beta) {
				node.eval = staticEval;
				return node.eval;
			}
			
			//Alpha cutoff
			if (staticEval + env.getBitboard().getBoardConfig().getMaterial_QUEEN_E() < alpha_org) {
				node.eval = staticEval;
				return node.eval;
			}
		}
		
		
		ISearchMoveList list = inCheck ? lists_escapes[depth] : lists_capsproms[depth];
		list.clear();
		list.setTptMove(tpt_move);
		
		
		int searchedMoves = 0;
		int best_eval = inCheck ? MIN : staticEval;
		int alpha = alpha_org;
		int best_move = 0;
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		
		
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == tpt_move) {
				continue;
			}
			
			
			env.getBitboard().makeMoveForward(cur_move);
			
			
			boolean pv_search = false;
			int cur_eval;
			if (searchedMoves == 0) {
				
				pv_search = true;
				
				cur_eval = -pv_qsearch(mediator, info, initial_maxdepth, depth + 1, -beta, -alpha, rootColour);
				
			} else {
				
				cur_eval = -nullwin_qsearch(mediator, info, initial_maxdepth, depth + 1, -alpha, rootColour);
				
				if (cur_eval > alpha) {
					
					pv_search = true;
					
					cur_eval = -pv_qsearch(mediator, info, initial_maxdepth, depth + 1, -beta, -alpha, rootColour);
				}
			}
			
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			
			searchedMoves++;
			
			
			//Add history records for the current move
			if (cur_eval < beta) {
				getHistory(inCheck).countFailure(cur_move, 1);
			} else {
				getHistory(inCheck).countSuccess(cur_move, 1);
			}
			
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				if (pv_search) {
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					
					if (depth + 1 < MAX_DEPTH) {
						pvman.store(depth + 1, node, pvman.load(depth + 1), true);
					}
				}
				
				if (best_eval >= beta) {						
					break;
				}
				
				if (best_eval > alpha) {
					alpha = best_eval;
					//throw new IllegalStateException();
				}
			}
			
		} while ((cur_move = list.next()) != 0);
		
		if (best_move == 0) {
			if (inCheck) {
				if (searchedMoves == 0) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					return node.eval;
				} else {
					throw new IllegalStateException("!!" + env.getBitboard().toString());
				}
			} else {
				//All captures lead to evaluation which is less than the static eval
			}
		}
		
		if (best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, 0, 0, env.getBitboard().getColourToMove(), best_eval, alpha_org, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}
	
	
	private int nullwin_qsearch(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int depth, int beta, int rootColour) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		int alpha_org = beta - 1;
		
		if (depth >= MAX_DEPTH) {
			return lazyEval(depth, alpha_org, beta, rootColour);
		}
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		if (isDraw()) {
			return getDrawScores(rootColour);
		}
		
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		boolean tpt_found = false;
		boolean tpt_exact = false;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(hashkey);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				if (tpt_exact) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_lower >= beta) {
					tpt_move = tptEntry.getBestMove_lower();
				} else if (tpt_upper <= alpha_org) {
					tpt_move = tptEntry.getBestMove_upper();
				} else {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
		}
		env.getTPT().unlock();
		
		if (tpt_found) {
			if (tpt_exact) {
				return tpt_lower;
			} else {
				if (tpt_lower >= beta) {
					return tpt_lower;
				}
				if (tpt_upper <= alpha_org) {
					return tpt_upper;
				}
			}
		}
		
		
		int staticEval = tpt_lower == MIN ? lazyEval(depth, alpha_org, beta, rootColour) : tpt_lower;
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		if (!inCheck) {
			
			//Beta cutoff
			if (staticEval >= beta) {
				return staticEval;
			}
			
			//Alpha cutoff
			if (staticEval + env.getBitboard().getBoardConfig().getMaterial_QUEEN_E() < alpha_org) {
				return staticEval;
			}
		}
    	
		
		ISearchMoveList list = inCheck ? lists_escapes[depth] : lists_capsproms[depth];
		list.clear();
		list.setTptMove(tpt_move);
		
		
		int searchedMoves = 0;
		int best_eval = inCheck ? MIN : staticEval;
		int best_move = 0;
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		
		
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == tpt_move) {
				continue;
			}
			
			
			if (!inCheck) {
				//Skip bad captures
				int moveSee = env.getBitboard().getSEEScore(cur_move);
				if (moveSee < 0) {
					break;
				}
			}
			
			
			env.getBitboard().makeMoveForward(cur_move);
			
			
			int cur_eval = -nullwin_qsearch(mediator, info, initial_maxdepth, depth + 1, -alpha_org, rootColour);
			
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			
			searchedMoves++;
			
			
			//Add history records for the current move
			if (cur_eval < beta) {
				getHistory(inCheck).countFailure(cur_move, 1);
			} else {
				getHistory(inCheck).countSuccess(cur_move, 1);
			}
			
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;

				if (best_eval >= beta) {
					break;
				}
			}
			
		} while ((cur_move = list.next()) != 0);
		
		if (best_move == 0) {
			if (inCheck) {
				if (searchedMoves == 0) {
					return -getMateVal(depth);
				} else {
					throw new IllegalStateException("best_move == 0 && searchedMoves != 0");
				}
			} else {
				//All captures lead to evaluation which is less than the static eval
			}
		}
		
		if (best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, 0, 0, env.getBitboard().getColourToMove(), best_eval, alpha_org, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}
	
	
	private int getAlphaTrustWindow(ISearchMediator mediator, int rest) {
		
		//int DEPTH1_INTERVAL = 100;
		//int DEPTH1_INTERVAL = (int) (move_eval_diff.getDisperse());
		//int DEPTH1_INTERVAL = (int) (move_eval_diff.getEntropy());
		//int DEPTH1_INTERVAL = (int) ((move_eval_diff.getEntropy() + move_eval_diff.getDisperse()) / 2);
		
		//return 32;//Math.max(33,  DEPTH1_INTERVAL * (rest / (double) 2));
		//return Math.max(1,  DEPTH1_INTERVAL * (rest / (double) 2));
		
		//return 1 * Math.max(1, (rest / (double) 2 )) * mediator.getTrustWindow_AlphaAspiration();
		return 1 * mediator.getTrustWindow_AlphaAspiration();
	}
	
	
	private int getEvalSumMax(ISearchMediator mediator) {
		
		//System.out.println(getAlphaTrustWindow(mediator, 0));
		
		return 20000;//getAlphaTrustWindow(mediator, 0);
	}
}
