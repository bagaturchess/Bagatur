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
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;

import bagaturchess.search.impl.alg.BacktrackingInfo;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalEntry;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.ITTEntry;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private static final int[][] LMR_REDUCTIONS = new int[64][64];
	static {
		for (int searchedCount = 0; searchedCount < 64; searchedCount++) {
			for (int restdepth = 0; restdepth < 64; restdepth++) {
				LMR_REDUCTIONS[searchedCount][restdepth] = (int) Math.ceil(Math.max(1, Math.log(searchedCount) * Math.log(restdepth) / (double) 2));
			}
		}
	}
	
	private static final double PRUNING_AGGRESSIVENESS 				= 1;
	
	private static final int FUTILITY_MAXDEPTH 						= 7;
	private static final int FUTILITY_MARGIN 						= 80;
	
	private static final int STATIC_NULL_MOVE_MAXDEPTH 				= 9;
	private static final int STATIC_NULL_MOVE_MARGIN 				= 60;
	
	private static final int RAZORING_MAXDEPTH 						= 4;
	private static final int RAZORING_MARGIN 						= 240;
	
	
	private BacktrackingInfo[] backtracking 						= new BacktrackingInfo[MAX_DEPTH + 1];
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	private static final boolean USE_DTZ_CACHE 						= true;
	private IEvalEntry temp_cache_entry;
	
	
	public Search_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		
		super(_env);
		
		if (USE_DTZ_CACHE) {
	    	
	    	temp_cache_entry = new EvalEntry_BaseImpl();
		}
		
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
	public int pv_search(ISearchMediator mediator, PVManager pvman,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return search(mediator, pvman, info, initial_maxdepth, 0, SearchUtils.normDepth(maxdepth), alpha_org, beta, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		throw new UnsupportedOperationException();
	}
	
	
	public int search(ISearchMediator mediator, PVManager pvman, ISearchInfo info, int initial_maxdepth, int ply, int depth, int alpha_org, int beta, boolean isPv) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (alpha_org >= beta) {
			throw new IllegalStateException("alpha=" + alpha_org + ", beta=" + beta);
		}
		
		
		BacktrackingInfo backtrackingInfo = backtracking[ply];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.static_eval = fullEval(ply, alpha_org, beta, -1);
		
		if (ply >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(SearchUtils.normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		if (isDraw(true)) {
			node.eval = getDrawScores(-1);
			return node.eval;
		}
    	
    	
		if (depth <= 0) {
			node.eval = qsearch(mediator, pvman, info, initial_maxdepth, ply, alpha_org, beta);	
			return node.eval;
		}
		
		
		int tt_move 	= 0;
		int tt_flag 	= -1;
		int tt_value 	= IEvaluator.MIN_EVAL;
		{
			env.getTPT().get(backtrackingInfo.hash_key, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				tt_move = tt_entries_per_ply[ply].getBestMove();
				tt_flag = tt_entries_per_ply[ply].getFlag();
				tt_value = tt_entries_per_ply[ply].getEval();
				
				int tt_depth = tt_entries_per_ply[ply].getDepth();
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (!isPv && tt_depth >= depth) {
						
						if (tt_flag == ITTEntry.FLAG_EXACT) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
							
							return node.eval;
							
						} else {
							
							if (tt_flag == ITTEntry.FLAG_LOWER && tt_value >= beta) {
								
								extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
								
								return node.eval;
							}
							
							if (tt_flag == ITTEntry.FLAG_UPPER && tt_value <= alpha_org) {
								
								extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
								
								return node.eval;
							}
						}
					}
				}
			}
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		int egtb_eval = ISearch.MIN;
		
		if (SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			int probe_result = probeWDL_WithCache();
			
			if (probe_result != -1) {
				
				info.setTBhits(info.getTBhits() + 1);
							
				int wdl = (probe_result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				
				//Winner is minimizing DTZ and the loser is maximizing DTZ
		        switch (wdl) {
		        
	            	case SyzygyConstants.TB_WIN:
	            		
	    				//int dtz = (probe_result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
	            		int dtz = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
	            		
	    				if (dtz < 0) {
	    					
	    					/**
	    					 * In this not mate position "8/6P1/8/2kB2K1/8/8/8/4r3 w - - 1 19", DTZ is -1 and WDL is 2 (WIN).
	    					 */
	    					break;
	    				}
	    				
						int distanceToDraw_50MoveRule = 99 - env.getBitboard().getDraw50movesRule();
						
						if (distanceToDraw_50MoveRule >= dtz) {
							
							egtb_eval = MAX_MATERIAL_INTERVAL / (ply + dtz + 1); //+1 in order to be less than a mate in max_depth plies.
							
						}
	            		
						break;
						
		            case SyzygyConstants.TB_LOSS:
		            	
	    				/*
	    				This code doesn't work correctly
	    				//getMateVal with parameter set to 1 achieves max and with ISearch.MAX_DEPTH achieves min
	    				egtb_eval = -SearchUtils.getMateVal(ply + dtz);
	    				*/
		            	break;
		            
		            case SyzygyConstants.TB_DRAW:
		            	
						egtb_eval = getDrawScores(-1);
		                
						break;
						
		            case SyzygyConstants.TB_BLESSED_LOSS:
		            	
		            	egtb_eval = getDrawScores(-1);
		                
						break;
						
		            case SyzygyConstants.TB_CURSED_WIN:
		            	
		            	egtb_eval = getDrawScores(-1);
		                
						break;
						
		            default:
		            	
		            	throw new IllegalStateException("wdl=" + wdl);
		        }
			}
        }
		
		
		if (egtb_eval != ISearch.MIN) {
			
			if (inCheck) {
				
				if (!env.getBitboard().hasMoveInCheck()) {
					
					egtb_eval = -SearchUtils.getMateVal(ply);
				}
				
			} else {
				
				if (!env.getBitboard().hasMoveInNonCheck()) {
					
					egtb_eval = getDrawScores(-1);
				}
			}
			
			if (ply > 7) {
			
				node.bestmove = 0;
				node.eval = egtb_eval;
				node.leaf = true;
				
				return node.eval;
			}
		}
		
		
		if (!isPv
				&& !inCheck
				&& !SearchUtils.isMateVal(alpha_org)
				&& !SearchUtils.isMateVal(beta)
			) {
			
			
			int eval = backtrackingInfo.static_eval;
			
			if (tt_value != IEvaluator.MIN_EVAL) {
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (tt_flag == ITTEntry.FLAG_EXACT
							|| (tt_flag == ITTEntry.FLAG_UPPER && tt_value < eval)
							|| (tt_flag == ITTEntry.FLAG_LOWER && tt_value > eval)
						) {
						
						eval = tt_value;
					}
				}
			}
			
			
			if (depth >= 2 && tt_flag == -1) {
				
				depth -= 1;
			}
			
			
			if (eval >= beta) {
				
				
				if (depth <= STATIC_NULL_MOVE_MAXDEPTH) {
					
					if (eval - depth * STATIC_NULL_MOVE_MARGIN / PRUNING_AGGRESSIVENESS >= beta) {
						
						node.bestmove = 0;
						node.eval = eval;
						node.leaf = true;
							
						return node.eval;
					}
				}
				
				
				boolean hasAtLeastOnePiece = (colourToMove == Constants.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
					env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
					
				if (hasAtLeastOnePiece && depth >= 3) {
					
					env.getBitboard().makeNullMoveForward();
					
					final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
					
					int score = depth - reduction <= 0 ? -qsearch(mediator, pvman, info, initial_maxdepth, ply + 1, -beta, -beta + 1)
							: -search(mediator, pvman, info, initial_maxdepth, ply + 1, depth - reduction, -beta, -beta + 1, isPv);
					
					env.getBitboard().makeNullMoveBackward();
					
					if (score >= beta) {
						
						node.bestmove = 0;
						node.eval = score;
						node.leaf = true;
						
						return node.eval;
					}
				}
				
			} else if (eval <= alpha_org) {
				
				
				if (depth <= RAZORING_MAXDEPTH) {
					
					int razoringMargin = (int) (RAZORING_MARGIN * depth / PRUNING_AGGRESSIVENESS);
					
					if (eval + razoringMargin < alpha_org) {
						
						int score = qsearch(mediator, pvman, info, initial_maxdepth, ply, alpha_org - razoringMargin - 1, alpha_org - razoringMargin);
						
						if (score <= alpha_org - razoringMargin - 1) {
							
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							
							return node.eval;
						}
					}
				}
			}
		}
		
		
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		ISearchMoveList list = !inCheck ? lists_all[ply] : lists_escapes[ply];
		list.clear();
		list.setTptMove(tt_move);
		list.setPrevBestMove(ply > 1 ? backtracking[ply - 2].best_move : 0);
		
		
		int searchedCount = 0;
		int alpha = alpha_org;
		int best_eval = MIN;
		int best_move = 0;
		
		
		int cur_move = (tt_move != 0) ? tt_move : list.next();
		if (cur_move != 0) {
			do {
				
				
				
				if (searchedCount > 0 && cur_move == tt_move) {
					continue;
				}
				
				
				//Build and sent minor info
				if (ply == 0) {
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
				
				
				if (!isPv
						&& depth <= 7
						&& !inCheck
						&& searchedCount > 0
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
					) {
					
					if (!isCapOrProm) {
						
						if (searchedCount >= (3 + depth * depth) / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
						if (backtrackingInfo.static_eval != ISearch.MIN) { //eval is set
							
							if (depth <= FUTILITY_MAXDEPTH
									&& backtrackingInfo.static_eval + depth * FUTILITY_MARGIN / PRUNING_AGGRESSIVENESS <= alpha) {
								
								continue;
							}
						}
						
					} else if (isCapOrProm
							&& env.getBitboard().getSEEScore(cur_move) < -20 * depth * depth / PRUNING_AGGRESSIVENESS
						) {
						
						continue;
					}
				}
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				
				//boolean givesCheck = env.getBitboard().isInCheck();
				
				
				int cur_eval;
				if (searchedCount == 0) {
					
					cur_eval = -search(mediator, pvman, info, initial_maxdepth, ply + 1, depth - 1, -beta, -alpha, isPv);
					
				} else {
					
					int lmrReduction = LMR_REDUCTIONS[Math.min(63, searchedCount)][Math.min(63, depth)];
					
					if (!isPv) {
						
						lmrReduction++;
					}
					
					cur_eval = -search(mediator, pvman, info, initial_maxdepth, ply + 1, depth - lmrReduction, -alpha - 1, -alpha, false);
					
					if (cur_eval > alpha) {
						
						cur_eval = -search(mediator, pvman, info, initial_maxdepth, ply + 1, depth - 1, -alpha - 1, -alpha, false);
						
						if (cur_eval > alpha) {
							
							cur_eval = -search(mediator, pvman, info, initial_maxdepth, ply + 1, depth - 1, -beta, -alpha, isPv);
						}
					}
				}
				
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				
				searchedCount++;
				
				
				if (!isCapOrProm) {
					
					env.getHistory_All().registerAll(env.getBitboard().getColourToMove(), cur_move, depth);
					
					if (cur_eval < beta) {
						
						env.getHistory_All().registerBad(env.getBitboard().getColourToMove(), cur_move, depth);
					}
				}
				
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					
					best_move = cur_move;
					
					backtrackingInfo.best_move = best_move;
					
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					
					if (ply + 1 < MAX_DEPTH) {
						pvman.store(ply + 1, node, pvman.load(ply + 1), true);
					}
					
					if (best_eval > alpha) {
						alpha = best_eval; 
					}
					
					if (alpha >= beta) {
						
						if (!isCapOrProm) {
							
							env.getHistory_All().addCounterMove(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove(), cur_move);
							env.getHistory_All().addKillerMove(env.getBitboard().getColourToMove(), cur_move, ply);
							env.getHistory_All().registerGood(env.getBitboard().getColourToMove(), cur_move, depth);
						}
						
						break;
					}
				}
			} while ((cur_move = list.next()) != 0);
		}
		
		
		if (best_move != 0 && (best_eval == MIN || best_eval == MAX)) {
			throw new IllegalStateException("EXC1: best_move=" + best_move + ", best_eval=" + best_eval);
		}
		
		
		if (best_move == 0) {
			if (inCheck) {
				if (searchedCount == 0) {
					node.bestmove = 0;
					node.eval = -SearchUtils.getMateVal(ply);
					node.leaf = true;
					return node.eval;
				} else {
					throw new IllegalStateException("best_move == 0, inCheck, searchedCount=" + searchedCount);
				}
			} else {
				if (searchedCount == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(-1);
					node.leaf = true;
					return node.eval;
				} else {
					throw new IllegalStateException("best_move == 0, !inCheck, searchedCount=" + searchedCount);
				}
			}
		}
		
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException("EXC2: best_move=" + best_move + ", best_eval=" + best_eval);
		}
		
		
		env.getTPT().put(backtrackingInfo.hash_key, depth, best_eval, alpha_org, beta, best_move);
		
		
		return best_eval;
	}
	
	
	private int qsearch(ISearchMediator mediator, PVManager pvman, ISearchInfo info, int initial_maxdepth, int ply, int alpha_org, int beta) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (ply >= MAX_DEPTH) {
			return fullEval(ply, alpha_org, beta, -1);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(SearchUtils.normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = MIN;
		node.leaf = true;
		
		
		if (isDraw(true)) {
			node.eval = getDrawScores(-1);
			return node.eval;
		}
		
		
		long hashkey = env.getBitboard().getHashKey();
		
		int tt_move 	= 0;
		int tt_flag 	= -1;
		int tt_value 	= IEvaluator.MIN_EVAL;
		{
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				tt_move = tt_entries_per_ply[ply].getBestMove();
				tt_flag = tt_entries_per_ply[ply].getFlag();
				tt_value = tt_entries_per_ply[ply].getEval();
				
				if (getSearchConfig().isOther_UseTPTScores()) {
						
					if (tt_flag == ITTEntry.FLAG_EXACT) {
						
						extractFromTT(ply, node, tt_entries_per_ply[ply], info, true);
						
						return node.eval;
						
					} else {
						
						if (tt_flag == ITTEntry.FLAG_LOWER && tt_value >= beta) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, true);
							
							return node.eval;
						}
						
						if (tt_flag == ITTEntry.FLAG_UPPER && tt_value <= alpha_org) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, true);
							
							return node.eval;
						}
					}
				}
			}
		}
		
		
		int staticEval = fullEval(ply, alpha_org, beta, -1);
		
		
		if (tt_value != IEvaluator.MIN_EVAL) {
			
			if (getSearchConfig().isOther_UseTPTScores()) {
				
				if (tt_flag == ITTEntry.FLAG_EXACT
						|| (tt_flag == ITTEntry.FLAG_UPPER && tt_value < staticEval)
						|| (tt_flag == ITTEntry.FLAG_LOWER && tt_value > staticEval)
					) {
					
					staticEval = tt_value;
				}
			}
		}
		
		
		//Beta cutoff
		if (staticEval >= beta) {
			node.eval = staticEval;
			return node.eval;
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		ISearchMoveList list = inCheck ? lists_escapes[ply] : lists_capsproms[ply];
		list.clear();
		list.setTptMove(tt_move);
		
		
		int alpha = Math.max(alpha_org, staticEval);
		
		int best_eval = ISearch.MIN;
		int best_move = 0;
		
		int cur_move = (tt_move != 0) ? tt_move : list.next();
		if (cur_move != 0) 
		do {
			
			/*if (cur_move == tt_move && !env.getBitboard().getMoveOps().isCaptureOrPromotion(cur_move)) {
				if (env.getBitboard().isCheckMove(cur_move)
						&& env.getBitboard().getMoveOps().getFigureType(cur_move) == Constants.TYPE_QUEEN
					) {
					continue;
				}
			}*/
			
			
			//Skip bad captures
			int moveSee = env.getBitboard().getSEEScore(cur_move);
			if (moveSee < 0) {
				continue;
			}
			
			
			env.getBitboard().makeMoveForward(cur_move);
			
			
			int cur_eval = -qsearch(mediator, pvman, info, initial_maxdepth, ply + 1, -beta, -alpha);
			
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				node.bestmove = best_move;
				node.eval = best_eval;
				node.leaf = false;
				
				if (ply + 1 < MAX_DEPTH) {
					pvman.store(ply + 1, node, pvman.load(ply + 1), true);
				}
				
				if (best_eval > alpha) {
					alpha = best_eval;
				}
				
				if (alpha >= beta) {						
					break;
				}
			}
			
		} while ((cur_move = list.next()) != 0);
		
		
		if (staticEval >= best_eval) {
			
			node.bestmove = 0;
			node.leaf = true;
			node.eval = staticEval;
			
			best_eval = staticEval;
			best_move = 0;
		}
		
		if (alpha_org > node.eval) {
			
			node.bestmove = 0;
			node.leaf = true;
			node.eval = alpha_org;
			
			best_eval = alpha_org;
			best_move = 0;
		}
		
		
		if (best_move != 0) {
			env.getTPT().put(hashkey, 0, best_eval, alpha_org, beta, best_move);
		}
		
		return best_eval;
	}
	
	
	private boolean extractFromTT(int ply, PVNode result, ITTEntry entry, ISearchInfo info, boolean isPv) {
		
		if (entry.isEmpty()) {
			
			throw new IllegalStateException("entry.isEmpty()");
		}
		
		if (result == null) {
			
			return false;
		}
		
		result.leaf = true;
		
		if (ply > 0
				&& isDraw(isPv)) {
	    	
			result.eval = getDrawScores(-1);
			
			result.bestmove = 0;
			
			return true;
	    }
		
		
		if (info != null && info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		result.eval = entry.getEval();
		result.bestmove = entry.getBestMove();
		
		
		boolean draw = false;
		
		if (env.getTPT() != null) {
			
			//if (isPv) {
				
				ply++;
				
				if (ply < ISearch.MAX_DEPTH) {
					
					if (result.bestmove != 0) {
						
						if (!env.getBitboard().isPossible(result.bestmove)) {
							
							throw new IllegalStateException("!env.getBitboard().isPossible(result.bestmove)");
						}
						
						env.getBitboard().makeMoveForward(result.bestmove);
						
						env.getTPT().get(env.getBitboard().getHashKey(), tt_entries_per_ply[ply]);
						
						if (!tt_entries_per_ply[ply].isEmpty()) {
							
							result.leaf = false;
							
							draw = extractFromTT(ply, result.child, tt_entries_per_ply[ply], info, isPv);
							
							
							if (draw) {
								
								result.eval = getDrawScores(-1);
							}
						}
						
						env.getBitboard().makeMoveBackward(result.bestmove);
						
					} else {
						
						//It is currently possible in positions with EGTB hit
						
						draw = (result.eval == getDrawScores(-1));
					}
				}
			}
		//}
		
		
		return draw;
	}
	
	
	private int probeWDL_WithCache() {
	    
	    if (USE_DTZ_CACHE) {
	    	
	    	long hash50movesRule = 128 + env.getBitboard().getDraw50movesRule();
		    hash50movesRule += hash50movesRule << 8;
		    hash50movesRule += hash50movesRule << 16;
		    hash50movesRule += hash50movesRule << 32;
		    
		    long hashkey = hash50movesRule ^ env.getBitboard().getHashKey();
		    
	    	env.getSyzygyDTZCache().get(hashkey, temp_cache_entry);
			
			if (!temp_cache_entry.isEmpty()) {
				
				int probe_result = temp_cache_entry.getEval();
		        
				return probe_result;
				
			} else {
		    
		        int probe_result = SyzygyTBProbing.getSingleton().probeWDL(env.getBitboard());
		        
		        env.getSyzygyDTZCache().put(hashkey, 5, probe_result);
		        
		        return probe_result;
			}
			
	    } else {
	    	
	    	int probe_result = SyzygyTBProbing.getSingleton().probeWDL(env.getBitboard());
	        
	        return probe_result;
	    }

	}
}
