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
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
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


public class Search_PVS_NWS1 extends SearchImpl {
	
	
	private double LMR_REDUCTION_MULTIPLIER 			= 1;
	private double NULL_MOVE_REDUCTION_MULTIPLIER 		= 1;
	private double IID_DEPTH_MULTIPLIER 				= 1;
	private boolean STATIC_PRUNING1						= true;
	private boolean STATIC_PRUNING2 					= true;
	private static final int[] MARGIN_STATIC_NULLMOVE 	= { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] MARGIN_RAZORING 			= { 0, 240, 280, 320 };
	
	
	private BacktrackingInfo[] backtracking 			= new BacktrackingInfo[MAX_DEPTH + 1];
	
	private static final double EVAL_DIFF_MAX 			= 50;
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	public Search_PVS_NWS1(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS1(SearchEnv _env) {
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
		
		return this.search(mediator, info, initial_maxdepth, maxdepth, depth, alpha_org, beta, rootColour, true);
	}


	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return search(mediator, info, initial_maxdepth, maxdepth, depth, beta - 1, beta, rootColour, false);
	}
	
	
	public int search(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int maxdepth, int depth, int alpha_org, int beta, int rootColour, boolean pv) {
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.static_eval = fullEval(depth, alpha_org, beta, rootColour);
		backtrackingInfo.best_move = 0;
		
		
		if (alpha_org >= beta) {
			throw new IllegalStateException("alpha=" + alpha_org + ", beta=" + beta);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		long hashkey = env.getBitboard().getHashKey();
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores(rootColour);
			return node.eval;
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		int rest = normDepth(maxdepth) - depth;
		
		
		if (depth > 1
    	    	&& depth <= rest
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			&& env.getBitboard().getColourToMove() == rootColour
    			){
			
			if (inCheck) {
				if (!env.getBitboard().hasMoveInCheck()) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				}
			} else {
				if (!env.getBitboard().hasMoveInNonCheck()) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					node.nullmove = false;
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
						node.eval = 10 * (distanceToDraw - dtz);
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
					} else {
						node.bestmove = 0;
						node.eval = getDrawScores(rootColour);
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
					}
				} else if (egtbscore == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				}
			}
        }
		
		
		boolean disableExts = false;
		/*if (inCheck && rest < 1) {
			if (depth >= normDepth(maxdepth)) {
				maxdepth = PLY * (depth + 1);
				disableExts = true;
			}
		}*/
		
		
		rest = normDepth(maxdepth) - depth;
		
		boolean tpt_found = false;
		boolean tpt_exact = false;
		int tpt_depth = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(hashkey);
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
		
		if (backtrackingInfo.excluded_move == 0
				&& tpt_found && tpt_depth >= rest
			) {
			if (tpt_exact) {
				node.bestmove = tpt_move;
				node.eval = tpt_lower;
				node.leaf = true;
				node.nullmove = false;
				
				env.getTPT().lock();
				buff_tpt_depthtracking[0] = 0;
				extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
				env.getTPT().unlock();
				
				if (buff_tpt_depthtracking[0] >= rest) {
					return node.eval;
				}
			} else {
				if (tpt_lower >= beta) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.leaf = true;
					node.nullmove = false;
					
					
					env.getTPT().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
					env.getTPT().unlock();
					
					if (buff_tpt_depthtracking[0] >= rest) {
						return node.eval;
					}
				}
				if (tpt_upper <= alpha_org) {
					node.bestmove = tpt_move;
					node.eval = tpt_upper;
					node.leaf = true;
					node.nullmove = false;
					
					
					env.getTPT().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, rest, node, false, buff_tpt_depthtracking, rootColour, env.getTPT());
					env.getTPT().unlock();
					
					if (buff_tpt_depthtracking[0] >= rest) {
						return node.eval;
					}
				}
			}
		}
    	
    	
		if (depth >= normDepth(maxdepth)) {
			
			/*if (inCheck) {
				throw new IllegalStateException("inCheck: depth >= normDepth(maxdepth)");
			}*/
			
			node.eval = pv_qsearch(mediator, info, initial_maxdepth, depth, alpha_org, beta, rootColour, pv);	
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		if (!pv && !inCheck) {
			
	        if (STATIC_PRUNING1) {
	            
	            if (tpt_lower > TPTEntry.MIN_VALUE) {
	                if (alpha_org > tpt_lower + getAlphaTrustWindow(mediator, rest) ) {
	                	
	                    node.eval = tpt_lower;
	                    node.leaf = true;
	                    node.nullmove = false;
	                    
	                    node.bestmove = 0;
	                    env.getTPT().lock();
	                    buff_tpt_depthtracking[0] = 0;
	                    extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
	                    env.getTPT().unlock();
	                    
	                    
	                    return node.eval;
	                }
	            }
	            
				if (alpha_org > backtrackingInfo.static_eval + getAlphaTrustWindow(mediator, rest)) {
					int qeval = pv_qsearch(mediator, info, initial_maxdepth, depth, alpha_org, beta, rootColour, false);
					if (alpha_org > qeval + getAlphaTrustWindow(mediator, rest) ) {
						
	                    node.eval = qeval;
	                    node.leaf = true;
	                    node.nullmove = false;
	                    
	                    node.bestmove = 0;
	                    env.getTPT().lock();
	                    buff_tpt_depthtracking[0] = 0;
	                    extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
	                    env.getTPT().unlock();
	                    
	                    
	                    return node.eval;
					}
				}
				
				//Static null move pruning
				if (rest < MARGIN_STATIC_NULLMOVE.length) {
					if (backtrackingInfo.static_eval - MARGIN_STATIC_NULLMOVE[rest] >= beta) {
						
	                    node.eval = backtrackingInfo.static_eval;
	                    node.leaf = true;
	                    node.nullmove = false;
	                    
	                    node.bestmove = 0;
	                    env.getTPT().lock();
	                    buff_tpt_depthtracking[0] = 0;
	                    extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
	                    env.getTPT().unlock();
	                    
	                    
	                    return node.eval;
					}
				}
				
				
				//Razoring
				if (rest < MARGIN_RAZORING.length && Math.abs(alpha_org) < MAX_MAT_INTERVAL) {
					if (backtrackingInfo.static_eval + MARGIN_RAZORING[rest] < alpha_org) {
						int qeval = pv_qsearch(mediator, info, initial_maxdepth, depth, alpha_org - MARGIN_RAZORING[rest], alpha_org - MARGIN_RAZORING[rest] + 1, rootColour, false);
						if (qeval + MARGIN_RAZORING[rest] <= alpha_org) {

		                    node.eval = qeval;
		                    node.leaf = true;
		                    node.nullmove = false;
		                    
		                    node.bestmove = 0;
		                    env.getTPT().lock();
		                    buff_tpt_depthtracking[0] = 0;
		                    extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
		                    env.getTPT().unlock();
		                    
		                    
		                    return node.eval;
						}
					}
				}
	        }
			
			
			boolean hasAtLeastOnePiece = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
				env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
			
			boolean hasAtLeastThreePieces = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 9 :
				env.getBitboard().getMaterialFactor().getBlackFactor() >= 9;
			
			if (hasAtLeastOnePiece) {
							
				if (backtrackingInfo.static_eval >= beta) {
					
					//int null_reduction = PLY * (rest >= 6 ? 3 : 2);
					int null_reduction = PLY * (rest >= 6 ? 4 : 3);
					null_reduction = (int) (NULL_MOVE_REDUCTION_MULTIPLIER * Math.max(null_reduction, PLY * (rest / 2)));
					
					int null_maxdepth = maxdepth - null_reduction;
					
					env.getBitboard().makeNullMoveForward();
					int null_val = -search(mediator, info, initial_maxdepth, null_maxdepth, depth + 1, -beta, -(beta - 1), rootColour, false);
					env.getBitboard().makeNullMoveBackward();
					
					if (null_val >= beta) {
						
						if (hasAtLeastThreePieces) {
							return null_val;
						}
						
						int null_val_ver = search(mediator, info, initial_maxdepth, null_maxdepth, depth, alpha_org, beta, rootColour, false);
						
						if (null_val_ver >= beta) {
							return null_val_ver;
						}
					
						env.getTPT().lock();
						{
							TPTEntry tptEntry = env.getTPT().get(hashkey);
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
		}
		
		
		//IID PV Node
		if (!tpt_found) {
			
			int reduction = (int) (IID_DEPTH_MULTIPLIER * Math.max(2, rest / 2));
			int iidRest = normDepth(maxdepth - PLY * reduction) - depth;
			
			if (tpt_depth < iidRest
				&& normDepth(maxdepth) - reduction > depth
				) {
				
				search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, alpha_org, beta, rootColour, false);
				
				env.getTPT().lock();
				TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
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
		}
        
        
		if (!pv && !inCheck) {
			
	        if (STATIC_PRUNING1) {
	        	
	            if (tpt_lower > TPTEntry.MIN_VALUE) {
	                if (alpha_org > tpt_lower + getAlphaTrustWindow(mediator, rest) ) {
	                	
	                    node.eval = tpt_lower;
	                    node.leaf = true;
	                    node.nullmove = false;
	                    
	                    node.bestmove = 0;
	                    env.getTPT().lock();
	                    buff_tpt_depthtracking[0] = 0;
	                    extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
	                    env.getTPT().unlock();
	                    
	                    
	                    return node.eval;
	                }
	            }
	        }
		}
		
		
		//Singular move extension
		int singularExtension = 0;
		
		{
			env.getTPT().lock();
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			env.getTPT().unlock();
			
	        if (depth > 0
	        		//&& rest >= 6//depth
	        		&& !disableExts
	        		//&& backtracking[depth - 1].excluded_move == 0 //Skip recursive calls
	        		&& tptEntry != null
	        		//&& tptEntry.getDepth() >= rest - 3
	        		) {
	        	
		        boolean hasSingleMove = env.getBitboard().hasSingleMove();
		        
				if (hasSingleMove) {
					
					singularExtension = PLY;
					
				} else if (tptEntry.getBestMove_lower() != 0) {
						
					int ttValue = tptEntry.getLowerBound();
					
					int reduction = (PLY * rest) / 2;
					if (reduction >= PLY) {
						
						int singularBeta = ttValue;// - 2 * rest;
						
						backtrackingInfo.excluded_move = tptEntry.getBestMove_lower();
						int singularEval = search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, singularBeta - 1, singularBeta, rootColour, false);
						backtrackingInfo.excluded_move = 0;
						
						if (singularEval < singularBeta) {
							singularExtension = PLY;
							//System.out.println("singularExtension hit");
						}
					}
				}
	        }
		}
		
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		double evalDiff = depth >= 2 ? backtrackingInfo.static_eval - backtracking[depth - 2].static_eval : 0;
		if (evalDiff > EVAL_DIFF_MAX) evalDiff = EVAL_DIFF_MAX;
		if (evalDiff < -EVAL_DIFF_MAX) evalDiff = -EVAL_DIFF_MAX;
		
		
		ISearchMoveList list = null;
		
		
		if (!inCheck) {
			
			list = lists_all[depth];
			list.clear();
			
			list.setTptMove(tpt_move);
			list.setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
			
		} else {
			list = lists_escapes[depth];
			list.clear();
			
			list.setTptMove(tpt_move);
			list.setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
		}
		
		
		int searchedCount = 0;
		int legalMoves = 0;
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
				
				
				boolean isCapOrProm = env.getBitboard().isCaptureOrPromotionMove(cur_move);
				int moveSee = -1;
				if (isCapOrProm) {
					moveSee = env.getBitboard().getSEEScore(cur_move);
				}
				
				
				//Static pruning
				if (STATIC_PRUNING2 && !inCheck && !env.getBitboard().isCheckMove(cur_move)) {
					
					if (searchedCount >= 4 && rest <= 8) {
						
						if (!isCapOrProm) {
							
							//Static pruning - move count based
							if (searchedCount >= 3 + Math.pow(rest, 2)) {
								continue;
							}
							
							//Static pruning - history based
							if (getHistory(inCheck).getScores(cur_move) <= 0.32 / Math.pow(2, rest)) {
		 						continue;
		 					}
							
							//Static pruning - evaluation based
							if (evalDiff < -(EVAL_DIFF_MAX - EVAL_DIFF_MAX / rest)) {
								continue;
							}
						}
						
						//Static pruning - SEE based
						if (rest <= 6) {
							int moveSee_tmp = (moveSee == -1) ? env.getBitboard().getSEEScore(cur_move) : moveSee;
							if (moveSee_tmp < -20 * rest * rest) {
								continue;
							}
						}
					}
				}
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				
				legalMoves++;
				
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				
				
				int new_maxdepth = maxdepth;
				if (depth > 0 && !disableExts) {
					//Do extensions here
					if (cur_move == tpt_move) {
						new_maxdepth += singularExtension;
					}
				}
				
				
				int cur_eval;
				if (cur_move == tpt_move) {
					
					cur_eval = -search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha, rootColour, pv);
				} else {
					
					int lmrReduction = 0;
					if (!inCheck
						 //&& !isCheckMove
						 //&& !((ListAll)list).isGoodMove(cur_move)
						 //&& !mateThreat
						 //&& !isCapOrProm
						 //&& moveSee < 0
						 //&& rest > 3
						) {
						
						double rate = Math.max(1, Math.log(searchedCount) * Math.log(rest) / 2);
						if (!isCapOrProm && !isCheckMove) rate += 2;//for pv nodes
						//if (isCapOrProm) rate -= 1;
						//if (!isCapOrProm && evalDiff > 0) rate -= 2 * (evalDiff / EVAL_DIFF_MAX);
						//rate *= (1 - getHistory(inCheck).getScores(cur_move));//In [0, 1]
						//rate *= (1 - (evalDiff / EVAL_DIFF_MAX));//In [0, 2]
						lmrReduction += (int) (PLY * rate * LMR_REDUCTION_MULTIPLIER);
					}
					int lmrRest = normDepth(maxdepth - lmrReduction) - depth - 1;
					if (lmrRest < 0) {
						lmrRest = 0;
					}
					
					
					cur_eval = -search(mediator, info, initial_maxdepth, new_maxdepth - lmrReduction, depth + 1, -(alpha + 1), -alpha, rootColour, false);
					
					if (cur_eval > alpha && lmrReduction > 0 ) {
						
						cur_eval = -search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -(alpha + 1), -alpha, rootColour, false);
					}
					
					if (pv) {
						if (cur_eval > best_eval) {
							
							cur_eval = -search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha, rootColour, pv);
						}
					}
				}
				
				
				env.getBitboard().makeMoveBackward(cur_move);
				
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
					
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					node.nullmove = false;
					
					if (depth + 1 < MAX_DEPTH) {
						pvman.store(depth + 1, node, pvman.load(depth + 1), true);
					}
					
					if (best_eval >= beta) {												
						break;
					}
					
					if (best_eval > alpha) {
						alpha = best_eval; 
						//throw new IllegalStateException();
					}
				}
				
				searchedCount++;
			} while ((cur_move = list.next()) != 0);
		}
		
		if (best_move != 0 && (best_eval == MIN || best_eval == MAX)) {
			throw new IllegalStateException();
		}
		
		if (best_move == 0) {
			if (inCheck) {
				if (legalMoves == 0) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					throw new IllegalStateException("hashkey=" + hashkey);
				}
			} else {
				if (legalMoves == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					//throw new IllegalStateException("hashkey=" + hashkey);
					node.bestmove = 0;
					node.eval = backtrackingInfo.static_eval;
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				}
			}
		}
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		
		if (backtrackingInfo.excluded_move == 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, alpha_org, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}
	
	
	private int pv_qsearch(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int depth, int alpha_org, int beta, int rootColour, boolean pv) {
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}

		if (depth >= MAX_DEPTH) {
			return fullEval(depth, alpha_org, beta, rootColour);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		PVNode node = pvman.load(depth);
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores(rootColour);
			return node.eval;
		}
				
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		boolean tpt_exact = false;
		boolean tpt_found = false;
		int tpt_move = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		
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
				node.bestmove = tpt_move;
				node.eval = tpt_lower;
				node.leaf = true;
				node.nullmove = false;
				
				env.getTPT().lock();
				buff_tpt_depthtracking[0] = 0;
				extractFromTPT(info, 0, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
				env.getTPT().unlock();
				
				if (buff_tpt_depthtracking[0] >= 0) {
					return node.eval;
				}
			} else {
				if (tpt_lower >= beta) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.leaf = true;
					node.nullmove = false;
					
					env.getTPT().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, 0, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
					env.getTPT().unlock();
					
					if (buff_tpt_depthtracking[0] >= 0) {
						return node.eval;
					}
				}
				if (tpt_upper <= alpha_org) {
					node.bestmove = tpt_move;
					node.eval = tpt_upper;
					node.leaf = true;
					node.nullmove = false;
					
					env.getTPT().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, 0, node, false, buff_tpt_depthtracking, rootColour, env.getTPT());
					env.getTPT().unlock();
					
					if (buff_tpt_depthtracking[0] >= 0) {
						return node.eval;
					}
				}
			}
		}
		
		
		int staticEval = -1;
		if (!inCheck) {
			staticEval = lazyEval(depth, alpha_org, beta, rootColour);
			
			if (staticEval >= beta) {
				staticEval = fullEval(depth, alpha_org, beta, rootColour);
			}
		}
		
		
		if (!inCheck) {
			
			//Beta cutoff
			if (staticEval >= beta) {
				node.eval = staticEval;
				return node.eval;
			}
			
			//Alpha cutoff
			if (staticEval + env.getBitboard().getBaseEvaluation().getMaterial(Figures.TYPE_QUEEN) < alpha_org) {
				node.eval = staticEval;
				return node.eval;
			}
		}
    	
    	
		ISearchMoveList list = inCheck ? lists_escapes[depth] : lists_capsproms[depth];
		list.clear();
		list.setTptMove(tpt_move);
		
		
		int legalMoves = 0;
		int best_eval = inCheck ? MIN : staticEval;
		int best_move = 0;
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		
		int alpha = alpha_org;
		
		
		int searchedMoves = 0;
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == tpt_move) {
				continue;
			}
			searchedMoves++;
			
			
			if (!inCheck) {
				
				//Skip under promotions
				if (MoveInt.isPromotion(cur_move)) {
					if (MoveInt.getPromotionFigureType(cur_move) != Constants.TYPE_QUEEN) {
						continue;
					}
				} else if (MoveInt.isCapture(cur_move)
						&& staticEval + env.getBitboard().getBaseEvaluation().getMaterial(MoveInt.getCapturedFigureType(cur_move)) < alpha) {
					//Futility pruning
					continue;
				}
				
				//Skip bad captures
				int moveSee = env.getBitboard().getSEEScore(cur_move);
				if (moveSee <= 0) {
					continue;
				}
			}
			
			
			env.getBitboard().makeMoveForward(cur_move);
			
			
			legalMoves++;
			
			int cur_eval = -pv_qsearch(mediator, info, initial_maxdepth, depth + 1, -beta, -alpha, rootColour, pv);
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				//backtrackingInfo.best_move = best_move;
				
				node.bestmove = best_move;
				node.eval = best_eval;
				node.leaf = false;
				node.nullmove = false;
				
				if (depth + 1 < MAX_DEPTH) {
					pvman.store(depth + 1, node, pvman.load(depth + 1), true);
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
				if (legalMoves == 0) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
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
	
	
	private double getAlphaTrustWindow(ISearchMediator mediator, int rest) {
		
		//int DEPTH1_INTERVAL = 100;
		//int DEPTH1_INTERVAL = (int) (move_eval_diff.getDisperse());
		//int DEPTH1_INTERVAL = (int) (move_eval_diff.getEntropy());
		//int DEPTH1_INTERVAL = (int) ((move_eval_diff.getEntropy() + move_eval_diff.getDisperse()) / 2);
		
		//return 32;//Math.max(33,  DEPTH1_INTERVAL * (rest / (double) 2));
		//return Math.max(1,  DEPTH1_INTERVAL * (rest / (double) 2));
		
		//return 1 * Math.max(1, (rest / (double) 2 )) * mediator.getTrustWindow_AlphaAspiration();
		return 1 * mediator.getTrustWindow_AlphaAspiration();
	}
}