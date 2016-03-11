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
package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;
//import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.alg.iter.ListAll;
import bagaturchess.search.impl.alg.iter.ListCapsProm;
import bagaturchess.search.impl.alg.iter.ListKingEscapes;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.exts.ExtStat;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class SearchAB1 extends SearchImpl {
	
	
	private static final int PV_LMR1_MOVE_EVAL_DIFF = 1000;
	private static final int PV_LMR2_MOVE_EVAL_DIFF = 33;
	private static final int NONPV_LMR1_MOVE_EVAL_DIFF = 1000;
	private static final int NONPV_LMR2_MOVE_EVAL_DIFF = 33;
	
	
	public SearchAB1(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public SearchAB1(SearchEnv _env) {
		super(_env);
	}
	
	
	public ISearchConfig1_AB getSearchConfig() {
		return (ISearchConfig1_AB) super.getSearchConfig();
	}
	
	
	@Override
	public String toString() {
		String result = "" + this + " ";
		
		//result += getEnv().getExtensions().toString();
		
		return result;
	}
	
	
	public void newSearch() {
		super.newSearch();
		//evals = new MoveEvalStat();		
	}
	
	
	private boolean isPasserPushPV(int cur_move) {
		//return false;
		boolean passerPush = env.getBitboard().isPasserPush(cur_move);
		return passerPush;
	}
	
	
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info, int initial_maxdepth,
			int maxdepth, int depth, int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour, int totalLMReduction,
			int materialGain, boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		
		if (alpha_org >= beta) {
			throw new IllegalStateException("alpha=" + alpha_org + ", beta=" + beta);
		}
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		int colourToMove = env.getBitboard().getColourToMove();
		long hashkey = env.getBitboard().getHashKey();
		
		if (depth >= MAX_DEPTH) {
			return fullEval(depth, alpha_org, beta, rootColour);
		}
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores();
			return node.eval;
		}
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
	    // Mate distance pruning
		if (getSearchConfig().getPV_Pruning_MateDistance() && useMateDistancePrunning && depth >= 1) {
			
		      if (inCheck && !env.getBitboard().hasMove()) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
		      }
		      
		      // lower bound
		      int value = -getMateVal(depth+2); // does not work if the current position is mate
		      if (value > alpha_org) {
		    	  alpha_org = value;
		         if (value >= beta) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }
		      
		      // upper bound
		      
		      value = getMateVal(depth+1);
		      
		      if (value < beta) {
		         beta = value;
		         if (value <= alpha_org) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }
		}

		
		int rest = normDepth(maxdepth) - depth;
		
		boolean disableCheckExtension = false;
		if (inCheck) {
			disableCheckExtension = true;
			maxdepth += PLY;
			if (rest < 1) {
				if (depth >= normDepth(maxdepth)) {
					maxdepth = PLY * (depth + 1);
				}
			}
		}
		
		rest = normDepth(maxdepth) - depth;
		
		if (depth >= normDepth(maxdepth)) {
			
			if (inCheck) {
				throw new IllegalStateException();
			}
			
			int staticEval = fullEval(depth, alpha_org, beta, rootColour);
			//if (staticEval >= beta || staticEval + env.getEval().getMaterialQueen() + 100 < alpha_org) {
			//	node.eval = staticEval;
			//} else {
				node.eval = pv_qsearch(mediator, info, initial_maxdepth, depth, alpha_org, beta, 0, staticEval, true, rootColour);	
			//}
			
			//if (node.eval > alpha_org && env.getTactics().silentButDeadly()) {
			//	maxdepth = PLY * (depth + 1);
			//} else {
				return node.eval;
			//}
		}
		
		
		int roughEval = roughEval(depth, rootColour);
		
		boolean mateThreat = false;
		int new_mateMove = 0;
		mateThreat = false;
		if (getSearchConfig().getPV_Pruning_NullMove()
				&& !inCheck
				&& !prevNullMove
				&& rest >= 2
				&& roughEval >= beta + getSearchConfig().getPV_Pruning_NullMove_Margin()) {
			
			int null_maxdepth = maxdepth - PLY * (rest >= 6 ? 3 : 2);
			
			env.getBitboard().makeNullMoveForward();
			int null_val = -nullwin_search(mediator, info, initial_maxdepth, null_maxdepth,
					depth + 1, -(beta - 1), true, prevprevbest, prevbest, prevPV, rootColour, totalLMReduction,
					-materialGain, true, 0, useMateDistancePrunning);
			
			if (null_val < 0 && isMateVal(null_val)
					) {
				
				mateThreat = true;
				
				TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKey());
				if (entry != null) {
					new_mateMove = entry.getBestMove_lower();
				}
			}
			
			env.getBitboard().makeNullMoveBackward();
			
			node.bestmove = 0;
			node.eval = MIN;
			node.nullmove = false;
			node.leaf = true;
		}
		
		/*int tpt_move = 0;
		int tpt_depth = -1;
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(hashkey);
			if (tptEntry != null) {
				tpt_move = tptEntry.getBestMove_lower();
				tpt_depth = tptEntry.getDepth();
			}
		}
		env.getTPT().unlock();
		*/
		
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
				tpt_move = tptEntry.getBestMove_lower();
			}
		}
		env.getTPT().unlock();
		
		if (getSearchConfig().getPV_Optimization_TPTScores() && tpt_found && tpt_depth >= rest) {
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.leaf = true;
					node.nullmove = false;
					
					env.getTPT().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, rest, node, true, buff_tpt_depthtracking);
					env.getTPT().unlock();
					
					if (buff_tpt_depthtracking[0] >= rest) {
						return node.eval;
					}
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = tpt_move;
						node.eval = tpt_lower;
						node.leaf = true;
						node.nullmove = false;
						
						
						env.getTPT().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, rest, node, true, buff_tpt_depthtracking);
						env.getTPT().unlock();
						
						if (buff_tpt_depthtracking[0] >= rest) {
							return node.eval;
						}
					}
				}
				if (tpt_upper <= beta - 1) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						node.bestmove = tpt_move;
						node.eval = tpt_upper;
						node.leaf = true;
						node.nullmove = false;
						
						
						env.getTPT().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, rest, node, false, buff_tpt_depthtracking);
						env.getTPT().unlock();
						
						if (buff_tpt_depthtracking[0] >= rest) {
							return node.eval;
						}
					}
				}
			}
		}
		
		
		/*if (true && depth > 0) {
			
			int reduction = Math.max(2, rest / 2);
			int iidRest = normDepth(maxdepth - PLY * reduction) - depth;
			
			if (tpt_depth < iidRest
				&& normDepth(maxdepth) - reduction > depth
				) {
				
				nullwin_search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, beta,
						prevNullMove, prevbest, prevprevbest, prevPV, rootColour, totalLMReduction, materialGain, true, mateMove, useMateDistancePrunning);
				
				tpt_move = getRootTPTMove(hashkey);
			}
		}*/
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		ISearchMoveList list = null;
		
		if (!inCheck) {
			list = lists_all[depth];
			list.clear();
			((ListAll)list).setTptMove(tpt_move);
			((ListAll)list).setPrevBestMove(prevprevbest);
			((ListAll)list).setMateMove(mateMove);
			
			if (prevPV != null && depth < prevPV.length) {
				((ListAll)list).setPrevpvMove(prevPV[depth]);
			}
		} else {
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(tpt_move);
			((ListKingEscapes)list).setPrevBestMove(prevprevbest);
		}
		
		
		//boolean pvNode = false;
		int searchedCount = 0;
		int legalMoves = 0;
		int alpha = alpha_org;
		int best_eval = MIN;
		int best_move = 0;
		
		
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		if (cur_move != 0) {
			do {
				
				if (searchedCount > 0 && cur_move == tpt_move) {
					continue;
				}
				
				if (mediator != null) {
					if (depth == 0) {
						info.setCurrentMove(cur_move);
						info.setCurrentMoveNumber((searchedCount + 1));
					}
					if ((initial_maxdepth / PLY) <= 7) {
						if (depth == 0) { 
							mediator.changedMinor(info);
						}
					} else {
						double send_depth = (initial_maxdepth / PLY) / (double) 2;
						if (depth <= send_depth) { 
							mediator.changedMinor(info);
						}
					}
				}
				
				
				boolean isCapOrProm = MoveInt.isCaptureOrPromotion(cur_move);
				boolean passerPush = isPasserPushPV(cur_move);
				
				//int moveSee = -1;
				//if (isCapOrProm) {
				//	moveSee = env.getBitboard().getSee().evalExchange(cur_move);
				//}
				int new_materialGain = materialGain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				
				
				int moveeval_b = fullEval(tpt_depth, alpha, beta, rootColour);
				
				env.getBitboard().makeMoveForward(cur_move);
				
				if (env.getBitboard().isInCheck(colourToMove)) {
					throw new IllegalStateException();
				}
				legalMoves++;
				
				int moveeval_a = -fullEval(tpt_depth, -beta, -alpha, rootColour);
				
				int moveeval_diff = moveeval_a - moveeval_b;
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				
				//if (!isCapOrProm) evals.addMoveEval(move_eval, colourToMove, isCapOrProm, true);				
				
				int new_maxdepth = maxdepth;
				
				if (!disableCheckExtension) {
					if (isCapOrProm) {
						//if (moveSee > 0) {
						//	new_maxdepth += PLY / 2;
						//}
					}
					if (mateThreat) {
						//new_maxdepth += PLY / 2;
					}
				}
				if (new_maxdepth > maxdepth + PLY) {
					new_maxdepth = maxdepth + PLY;
				}
				
				//if (depth > 0) {
				//	new_maxdepth = new_maxdepth_pv(colourToMove, maxdepth, cur_move, inCheck, false, moveSee, false, 0, materialGain, new_materialGain, false);
				//}
				
				
				int cur_eval = 0;
				if (searchedCount == 0) {
					cur_eval = -pv_search(mediator, rootWin, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha,
							best_move, prevbest, prevPV, false, -0, rootColour,
							totalLMReduction, -new_materialGain, inNullMove, 0, useMateDistancePrunning);
				} else {
					
					if (!inCheck
							&& !isCheckMove
							&& !isCapOrProm
							&& !mateThreat
							&& !passerPush
							&& cur_move != tpt_move
							) {
						
						int reduction = 0;
						if (searchedCount >= getSearchConfig().getPV_reduction_lmr1()
								&& moveeval_diff < PV_LMR1_MOVE_EVAL_DIFF
									) {
							reduction += PLY;
							if (searchedCount >= getSearchConfig().getPV_reduction_lmr2()
									&& (moveeval_diff < PV_LMR2_MOVE_EVAL_DIFF /*|| moveeval_a < alpha_org*/)
									) {
								reduction += PLY;
							}
						}
						//-150ELO diff if (searchedCount >= 1) {
						//-90ELO diff if (searchedCount >= 2) {
						//-131ELO diff of 100games if (searchedCount >= 3) {
						
						/*if (moveevalgain >= 0) {
							reduction = 0;
						}*/
						
						cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth - reduction, depth + 1, -alpha, false,
								best_move, prevbest, prevPV, rootColour, totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
					
						if (isPVNode(cur_eval, best_eval, alpha, beta)) {
							cur_eval = -pv_search(mediator, rootWin, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha,
									best_move, prevbest, prevPV, false, -0, rootColour,
									totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
						}
					} else {
						cur_eval = -pv_search(mediator, rootWin, info, initial_maxdepth, new_maxdepth, depth + 1, -beta, -alpha,
								best_move, prevbest, prevPV, false, -0, rootColour,
								totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
					}
				}
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					best_move = cur_move;
					
					if (isNonAlphaNode(cur_eval, best_eval, alpha, beta)) {
						
						node.bestmove = best_move;
						node.eval = best_eval;
						node.leaf = false;
						node.nullmove = false;
						
						if (depth + 1 < MAX_DEPTH) {
							pvman.store(depth + 1, node, pvman.load(depth + 1), true);
						}
					}
					
					if (best_eval > alpha) {
						alpha = best_eval; 
					}
					
					if (best_eval >= beta) {
						
						if (tpt_move == best_move) {
							list.countStatistics(best_move);
						}
						list.updateStatistics(best_move);
						
						
						if (inCheck) {
							env.getHistory_check().goodMove(cur_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
						} else {
							env.getHistory_all().goodMove(cur_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
							env.getHistory_all().counterMove(env.getBitboard().getLastMove(), cur_move);
						}
						
						break;
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
					node.eval = getDrawScores();
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					throw new IllegalStateException("hashkey=" + hashkey);
				}
			}
		}
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		
		env.getTPT().lock();
		env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, alpha_org, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		return best_eval;
	}
	
	
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth,
			int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV, int rootColour, int totalLMReduction, int materialGain, boolean inNullMove, int mateMove,
			boolean useMateDistancePrunning) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		long hashkey = env.getBitboard().getHashKey();
		
		
		if (depth >= MAX_DEPTH) {
			return lazyEval(depth, beta - 1, beta, rootColour);
		}
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, beta - 1, beta);
				
		
		if (isDraw()) {
			return getDrawScores();
		}
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		
		int alpha_org = beta - 1;
		
	    // Mate distance pruning
		if (getSearchConfig().getNONPV_Pruning_MateDistance() && useMateDistancePrunning && depth >= 1) {
		      
			// lower bound
		      if (inCheck && !env.getBitboard().hasMove()) {
					return -getMateVal(depth);
		      }
		      
		      int value = -getMateVal(depth+2); // does not work if the current position is mate

		      if (value > alpha_org) {
		         if (value >= beta) {
					return value;
		         }
		      }

		      // upper bound

		      value = getMateVal(depth+1);

		      if (value < beta) {
		         beta = value;
		         if (value <= alpha_org) {
						return value;
		         }
		      }
		}
		
		
		int rest = normDepth(maxdepth) - depth;
		
		boolean disableCheckExtension = false;
		if (inCheck) {
			disableCheckExtension = true;
			maxdepth += PLY;
			if (rest < 1) {
				if (depth >= normDepth(maxdepth)) {
					maxdepth = PLY * (depth + 1);
				}	
			}
		}
		
		rest = normDepth(maxdepth) - depth;
		
		if (depth >= normDepth(maxdepth)) {
			
			if (inCheck) {
				throw new IllegalStateException();
			}
			
			int staticEval = lazyEval(depth, beta -1, beta, rootColour);
			//if (staticEval >= beta || staticEval + env.getEval().getMaterialQueen() + 100 < beta - 1) {
				//return staticEval;
			//} else {
				int eval = nullwin_qsearch(mediator, info, initial_maxdepth, depth, beta, 0, staticEval, true, rootColour);
			//}
				
			//if (eval > alpha_org && env.getTactics().silentButDeadly()) {
			//	maxdepth = PLY * (depth + 1);
			//} else {
				return eval;
			//}
		}
		
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
				tpt_move = tptEntry.getBestMove_lower();
			}
		}
		env.getTPT().unlock();
		
		if (getSearchConfig().getNONPV_Optimization_TPTScores() && tpt_found && tpt_depth >= rest) {
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					return tpt_lower;
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						return tpt_lower;
					}
				}
				if (tpt_upper <= beta - 1) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						return tpt_upper;
					}
				}
			}
		}
		
		
		int roughEval = roughEval(depth, rootColour);
		boolean hasAtLeastOnePiece = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
																				env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
																	
		boolean hasAtLeastThreePieces = (colourToMove == Figures.COLOUR_WHITE) ? env.getBitboard().getMaterialFactor().getWhiteFactor() >= 9 :
																				env.getBitboard().getMaterialFactor().getBlackFactor() >= 9;
																				
		boolean mateThreat = false;
		int new_mateMove = 0;
		if (getSearchConfig().getNONPV_Pruning_NullMove()
				&& !inCheck
				&& !prevNullMove
				&& hasAtLeastThreePieces
				&& rest >= 2
				&& roughEval >= beta + getSearchConfig().getNONPV_Pruning_NullMove_Margin()
				) {
			
			//if (true) throw new IllegalStateException("implement null move");
			
			int null_maxdepth = maxdepth - PLY * (rest >= 6 ? 3 : 2);
			
			env.getBitboard().makeNullMoveForward();
			int null_val = -nullwin_search(mediator, info, initial_maxdepth, null_maxdepth,
					depth + 1, -(beta - 1), true, prevprevbest, prevbest, prevPV, rootColour, totalLMReduction, -materialGain, true, 0, useMateDistancePrunning);
			
			if (null_val >= beta) {
				
				env.getBitboard().makeNullMoveBackward();
				
				if (hasAtLeastThreePieces) {
					return null_val;
				}
				
				int null_val_ver = nullwin_search(mediator, info, initial_maxdepth, null_maxdepth, depth,
						beta, prevNullMove, prevbest, prevprevbest, prevPV, rootColour, totalLMReduction, materialGain, true, mateMove, useMateDistancePrunning);
				
				if (null_val_ver >= beta) {
					return null_val_ver;
				} else {
					//zungzwang = true;
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
						tpt_move = tptEntry.getBestMove_lower();
					}
				}
				env.getTPT().unlock();
				
			} else {
				if (roughEval > beta - 1) { //PV node candidate
					if (null_val <= beta - 1) { //but bad thing appears
						if (null_val < 0 && isMateVal(null_val)) {//and the bad thing is mate
							mateThreat = true;
							
							env.getTPT().lock();
							TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKey());
							if (entry != null) {
								new_mateMove = entry.getBestMove_lower();
								if (new_mateMove == 0) {
									new_mateMove = entry.getBestMove_upper();
								}
							}
							env.getTPT().unlock();
						}
					}
				}
				
				env.getBitboard().makeNullMoveBackward();
			}
		}
		
		
		/*if (tpt_move == 0) {
			
			int reduction = Math.max(2, rest / 2);
			int iidRest = normDepth(maxdepth - PLY * reduction) - depth;
			
			if (tpt_depth < iidRest
					&& normDepth(maxdepth) - reduction > depth) {
				
				nullwin_search(mediator, info, initial_maxdepth, maxdepth - PLY * reduction, depth, beta,
						prevNullMove, prevbest, prevprevbest, prevPV, rootColour, totalLMReduction, materialGain, inNullMove, mateMove, useMateDistancePrunning);
				
				tpt_move = getRootTPTMove(hashkey);
			}
		}*/
		
		ISearchMoveList list = null;
		
		if (!inCheck) {
			list = lists_all[depth];
			list.clear();
			((ListAll)list).setTptMove(tpt_move);
			((ListAll)list).setPrevBestMove(prevprevbest);
			((ListAll)list).setMateMove(mateMove);
			
			if (prevPV != null && depth < prevPV.length) {
				((ListAll)list).setPrevpvMove(prevPV[depth]);
			}
		} else {
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(tpt_move);
			((ListKingEscapes)list).setPrevBestMove(prevprevbest);
		}
		
		
		int searchedCount = 0;
		int legalMoves = 0;
		int best_eval = MIN;
		int best_move = 0;
		
		int cur_move = (tpt_move != 0) ? tpt_move : list.next();
		if (cur_move != 0) {
			do {
				
				if (searchedCount > 0 && cur_move == tpt_move) {
					continue;
				}
				
				if (mediator != null) {
					if ((initial_maxdepth / PLY) <= 7) {
						//Do nothing
					} else {
						double send_depth = (initial_maxdepth / PLY) / (double) 2;
						if (depth <= send_depth) { 
							mediator.changedMinor(info);
						}
					}
				}
				
				boolean isCapOrProm = MoveInt.isCaptureOrPromotion(cur_move);
				boolean passerPush = isPasserPushPV(cur_move);
				
				if (getSearchConfig().getNONPV_Pruning_Futiliy()
					&& rest <= 3
					&& searchedCount > 0
					&& roughEval + 100 < alpha_org
					&& !inCheck
					&& !mateThreat
					&& !isCapOrProm
					&& !env.getBitboard().isCheckMove(cur_move)) {
					
					int eval_b = lazyEval(tpt_depth, beta - 1, beta, rootColour);
					
					switch (rest) {
						case 1:
							if (eval_b + 300 < alpha_org) {
								if (eval_b > best_eval) {
									best_eval = eval_b;
									best_move = cur_move;
								}
								continue;
							}
							break;
						case 2:
							if (eval_b + 500 < alpha_org) {
								if (eval_b > best_eval) {
									best_eval = eval_b;
									best_move = cur_move;
								}
								continue;
							}
							break;
						case 3:
							if (eval_b + 900 < alpha_org) {
								if (eval_b > best_eval) {
									best_eval = eval_b;
									best_move = cur_move;
								}
								continue;
							}
							break;
						default:
							break;
					}
				}
				
				int new_materialGain = materialGain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				
				int moveeval_b = getSearchConfig().getNONPV_reduction_too_good_scores() ? lazyEval(tpt_depth, beta - 1, beta, rootColour) : 0;
				//int moveeval_b = lazyEval(tpt_depth, beta - 1, beta, rootColour);
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				//int moveeval_a = -lazyEval(tpt_depth, -beta, -(beta-1), rootColour);
				
				//int moveeval_diff = moveeval_a - moveeval_b;
				
				if (env.getBitboard().isInCheck(colourToMove)) {
					throw new IllegalStateException();
				}
				legalMoves++;
				
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				
				int new_maxdepth = maxdepth;
				
				int cur_eval = 0;
				//if (searchedCount == 0) {
				//	cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -(beta - 1), false,
				//		best_move, prevbest, prevPV, rootColour, totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
				//} else {
					
					if (!inCheck
						&& !isCheckMove
						&& !isCapOrProm
						&& !mateThreat
						//&& !passerPush
						&& cur_move != tpt_move
						) {
						
						int reduction = 0;
						if (searchedCount >= getSearchConfig().getNONPV_reduction_lmr1()
								//&& moveeval_diff < NONPV_LMR1_MOVE_EVAL_DIFF
								) {
							reduction += PLY;
							if (searchedCount >= getSearchConfig().getNONPV_reduction_lmr2()
									//&& moveeval_diff < NONPV_LMR2_MOVE_EVAL_DIFF
									) {
								reduction += PLY;
							}
						}
						
						cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth - reduction, depth + 1, -(beta - 1), false,
								best_move, prevbest, prevPV, rootColour, totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
						if (cur_eval > beta - 1) {
							
							if (getSearchConfig().getNONPV_reduction_too_good_scores()
									&& moveeval_b > beta - 1
									&& !isMateVal(beta - 1)
									&& !isMateVal(beta)
								) {
								//Do nothing
							} else {
								cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -(beta - 1), false,
									best_move, prevbest, prevPV, rootColour, totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
							}
						}
					} else {
						cur_eval = -nullwin_search(mediator, info, initial_maxdepth, new_maxdepth, depth + 1, -(beta - 1), false,
								best_move, prevbest, prevPV, rootColour, totalLMReduction, -new_materialGain, inNullMove, new_mateMove, useMateDistancePrunning);
					}
				//}
				
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					best_move = cur_move;
					
					if (best_eval >= beta) {
						
						if (tpt_move == best_move) {
							list.countStatistics(best_move);
						}
						list.updateStatistics(best_move);
						
						
						if (inCheck) {
							env.getHistory_check().goodMove(cur_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
						} else {
							env.getHistory_all().goodMove(cur_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
							env.getHistory_all().counterMove(env.getBitboard().getLastMove(), cur_move);
						}
						
						break;
					}
					
					if (best_eval > beta - 1) {
						throw new IllegalStateException(); 
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
					return -getMateVal(depth);
				} else {
					throw new IllegalStateException("hashkey=" + hashkey);
					//return best_eval;
				}
			} else {
				if (legalMoves == 0) {
					return getDrawScores();
				} else {
					throw new IllegalStateException("hashkey=" + hashkey);
					//return best_eval;
				}
			}
		}
		
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		
		env.getTPT().lock();
		env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, beta - 1, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		return best_eval;

	}
	
	private int pv_qsearch(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int depth, int alpha_org, int beta, int matgain, int initialStaticEval, boolean firstTime, int rootColour) {
		
		if (!firstTime) info.setSearchedNodes(info.getSearchedNodes() + 1);	
		
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		long hashkey = env.getBitboard().getHashKey();

		int staticEval = firstTime ? initialStaticEval : fullEval(depth, alpha_org, beta, rootColour);
		if (depth >= MAX_DEPTH) {
			return staticEval;
		}
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, alpha_org, beta);
		
		PVNode node = pvman.load(depth);
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores();
			return node.eval;
		}
		
		boolean inCheck = env.getBitboard().isInCheck();
		
	    // Mate distance pruning
		if (getSearchConfig().getPV_QSearch_Pruning_MateDistance() && depth >= 1) {

		      if (inCheck && !env.getBitboard().hasMove()) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
		      }
		      
		      // lower bound
		      int value = -getMateVal(depth+2); // does not work if the current position is mate

		      if (value > alpha_org) {
		    	  alpha_org = value;
		         if (value >= beta) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }

		      // upper bound

		      value = getMateVal(depth+1);

		      if (value < beta) {
		         beta = value;
		         if (value <= alpha_org) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }
		}
		
		if (!inCheck) {
			if (staticEval >= beta) {
				node.eval = staticEval;
				return node.eval;
			}
			
			if (getSearchConfig().getPV_QSearch_Use_Queen_Material_Margin()
					&& !isMateVal(alpha_org)
					&& staticEval + env.getEval().getMaterialQueen() < alpha_org) {
				node.eval = staticEval;
				return node.eval;
			}
			
			if (staticEval > alpha_org) {
				alpha_org = staticEval;
			}
		}

		//int tpt_move = getTPTMove(hashkey);
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
				tpt_move = tptEntry.getBestMove_lower();
				
				tpt_exact = tptEntry.isExact();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
			}
		}
		env.getTPT().unlock();
		
		if (getSearchConfig().getPV_QSearch_Optimization_TPTScores() && tpt_found) {
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.leaf = true;
					node.nullmove = false;
					return tpt_lower;
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = tpt_move;
						node.eval = tpt_lower;
						node.leaf = true;
						node.nullmove = false;
						return tpt_lower;
					}
				}
				if (tpt_upper <= alpha_org) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						node.bestmove = tpt_move;
						node.eval = tpt_upper;
						node.leaf = true;
						node.nullmove = false;
						return tpt_upper;
					}
				}
			}
		}
		
		
		IMoveList list = null;
		if (inCheck) { 
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(tpt_move);
		} else {
			list = lists_capsproms[depth];
			list.clear();
			((ListCapsProm)list).setTptMove(tpt_move);
		}
		
		int legalMoves = 0;
		int best_eval = MIN;
		int best_move = 0;
		int cur_move = 0;
		
		int alpha = alpha_org;
		
		if (inCheck) {
			cur_move = (tpt_move != 0) ? tpt_move : list.next();
		} else {
			cur_move = (tpt_move != 0 && MoveInt.isCaptureOrPromotion(tpt_move)) ? tpt_move : list.next();
			//cur_move = (tpt_move != 0) ? tpt_move : list.next();
		}
		
		int searchedMoves = 0;
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == tpt_move) {
				continue;
			}
			searchedMoves++;
			
			if (MoveInt.isCapture(cur_move)) {
				if (MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
					throw new IllegalStateException(env.getBitboard().toString());
				}
			}
			
			int new_matgain = matgain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
			
			int moveSee = getSearchConfig().getPV_QSearch_Use_SEE() ? env.getBitboard().getSee().evalExchange(cur_move) : 0;
			
			if (inCheck
					|| (moveSee >= 0 && getSearchConfig().getPV_QSearch_Use_SEE())
					|| new_matgain >= 0
					|| (getSearchConfig().getPV_QSearch_Move_Checks() && env.getBitboard().isCheckMove(cur_move))
					) {
				
				env.getBitboard().makeMoveForward(cur_move);
				
				if (env.getBitboard().isInCheck(colourToMove)) {
					throw new IllegalStateException("! " + env.getBitboard().toString());
				}
				legalMoves++;
				
				int cur_eval = -pv_qsearch(mediator, info, initial_maxdepth, depth + 1, -beta, -alpha, -new_matgain, 0, false, rootColour);
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				if (cur_eval > best_eval) {
				//if ((inCheck && cur_eval > best_eval) || (!inCheck && cur_eval > alpha)) {
					best_eval = cur_eval;
					best_move = cur_move;
					
					if (best_eval > alpha) {
						
						node.bestmove = best_move;
						node.eval = best_eval;
						node.leaf = false;
						if (depth + 1 < MAX_DEPTH) {
							pvman.store(depth + 1, node, pvman.load(depth + 1), true);
						}
						
						alpha = best_eval;
					}
					
					if (best_eval >= beta) {
						
						if (inCheck) {
							env.getHistory_check().goodMove(cur_move, 1, best_eval > 0 && isMateVal(best_eval));
						} else {
						}
						
						break;
					}
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
		
		if (!inCheck && staticEval > best_eval) {
			best_move = 0;
			best_eval = staticEval;
			
			node.leaf = true;
			node.eval = staticEval;
			node.bestmove = 0;
			node.nullmove = false;
		}
		
		if (getSearchConfig().getPV_QSearch_Store_TPT_Scores() && best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, 0, 0, env.getBitboard().getColourToMove(), best_eval, alpha_org, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}
	
	private int nullwin_qsearch(ISearchMediator mediator, ISearchInfo info, int initial_maxdepth, int depth, int beta, int matgain, int initialStaticEval, boolean firstTime, int rootColour) {
		
		if (!firstTime) info.setSearchedNodes(info.getSearchedNodes() + 1);	
		
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		int staticEval = firstTime ? initialStaticEval : lazyEval(depth, beta - 1, beta, rootColour);
		if (depth >= MAX_DEPTH) {
			return staticEval;
		}
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, beta - 1, beta);
		
		if (isDraw()) {
			return getDrawScores();
		}
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		int alpha_org = beta - 1;
	    // Mate distance pruning
		if (getSearchConfig().getNONPV_QSearch_Pruning_MateDistance() && depth >= 1) {
			
		      if (inCheck && !env.getBitboard().hasMove()) {
					return -getMateVal(depth);
		      }
			
		      int value = -getMateVal(depth+2); // does not work if the current position is mate

		      if (value > alpha_org) {
		         if (value >= beta) {
					return value;
		         }
		      }

		      // upper bound

		      value = getMateVal(depth+1);

		      if (value < beta) {
		         beta = value;
		         if (value <= alpha_org) {
						return value;
		         }
		      }
		}
		
		if (!inCheck) {
			if (staticEval >= beta) {
				return staticEval;
			}
			
			if (getSearchConfig().getNONPV_QSearch_Use_Queen_Material_Margin()
					&& !isMateVal(beta - 1)
					&& staticEval + env.getEval().getMaterialQueen() < beta - 1) {
				return staticEval;
			}
			
			if (staticEval > beta - 1) {
				throw new IllegalStateException();
			}
		}

		//int tpt_move = getTPTMove(hashkey);
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
				tpt_move = tptEntry.getBestMove_lower();
			}
		}
		env.getTPT().unlock();
		
		if (getSearchConfig().getNONPV_QSearch_Optimization_TPTScores() && tpt_found) {
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					return tpt_lower;
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						return tpt_lower;
					}
				}
				if (tpt_upper <= beta - 1) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						return tpt_upper;
					}
				}
			}
		}
		
		IMoveList list = null;
		if (inCheck) { 
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(tpt_move);
		} else {
			list = lists_capsproms[depth];
			list.clear();
			((ListCapsProm)list).setTptMove(tpt_move);
		}
		
		int legalMoves = 0;
		int best_eval = MIN;
		int best_move = 0;
		int cur_move = 0;
		
		int alpha = beta - 1;
		
		if (inCheck) {
			cur_move = (tpt_move != 0) ? tpt_move : list.next();
		} else {
			cur_move = (tpt_move != 0 && MoveInt.isCaptureOrPromotion(tpt_move)) ? tpt_move : list.next();
		}
		
		int searchedMoves = 0;
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == tpt_move) {
				continue;
			}
			searchedMoves++;
			
			if (MoveInt.isCapture(cur_move)) {
				if (MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
					throw new IllegalStateException();
				}
			}
			
			int new_matgain = matgain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
			
			int moveSee = getSearchConfig().getNONPV_QSearch_Use_SEE() ? env.getBitboard().getSee().evalExchange(cur_move) : 0;
			
			if (inCheck
					|| (moveSee >= 0 && getSearchConfig().getNONPV_QSearch_Use_SEE())
					|| new_matgain >= 0
					|| (getSearchConfig().getNONPV_QSearch_Move_Checks() && env.getBitboard().isCheckMove(cur_move))
					) {
				
				env.getBitboard().makeMoveForward(cur_move);
				
				if (env.getBitboard().isInCheck(colourToMove)) {
					throw new IllegalStateException();
				}
				legalMoves++;
				
				int cur_eval = -nullwin_qsearch(mediator, info, initial_maxdepth, depth + 1, -alpha, -new_matgain, 0, false, rootColour);
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				if (cur_eval > best_eval) {
				//if ((inCheck && cur_eval > best_eval) || (!inCheck && cur_eval > alpha)) {
					best_eval = cur_eval;
					best_move = cur_move;

					if (best_eval >= beta) {
						break;
					}
					
					if (best_eval > alpha) {
						alpha = best_eval;
					}
				}
			}
			
		} while ((cur_move = list.next()) != 0);
		
		if (best_move == 0) {
			if (inCheck) {
				if (legalMoves == 0) {
					return -getMateVal(depth);
				} else {
					throw new IllegalStateException();
				}
			} else {
				//All captures lead to evaluation which is less than the static eval
			}
		}
		
		if (!inCheck && staticEval >= best_eval) {
			best_move = 0;
			best_eval = staticEval;
		}
		
		if (getSearchConfig().getNONPV_QSearch_Store_TPT_Scores() && best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, 0, 0, env.getBitboard().getColourToMove(), best_eval, beta - 1, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}
}
