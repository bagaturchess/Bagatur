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
package bagaturchess.search.impl.alg.impl2;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.IEngineConfig;
import bagaturchess.search.api.internal.IRootWindow;
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
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class SearchAB2 extends SearchImpl {
	
	
	private BacktrackingInfo[] backtracking = new BacktrackingInfo[MAX_DEPTH + 1];
	
	
	public SearchAB2(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public SearchAB2(SearchEnv _env) {
		super(_env);
		for (int i=0; i<backtracking.length; i++) {
			backtracking[i] = new BacktrackingInfo(); 
		}
	}
	
	
	@Override
	public String toString() {
		String result = "" + this + " ";
		return result;
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha, int beta, int prevbest, int prevprevbest, int[] prevPV,
			boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		backtracking[0].null_move = false;
		
		return negasearch(mediator, rootWin, info, maxdepth, depth, beta, true, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		throw new IllegalStateException();
	}
	
	
	private int negasearch(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info,
			int maxdepth, int depth, int beta, boolean pv, boolean storeInTPT) {
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
		backtrackingInfo.hash_move = 0;
		backtrackingInfo.null_move = depth > 0 ? backtracking[depth - 1].null_move: false;
		backtrackingInfo.eval = BacktrackingInfo.EVAL_NOT_CALCULATED;
		backtrackingInfo.best_move = 0;
		backtrackingInfo.mate_move = 0;
		backtrackingInfo.material_exchanged = depth > 0 ? -backtracking[depth - 1].material_exchanged : 0;
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		if (depth >= MAX_DEPTH) {
			return eval(depth, beta);
		}
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(maxdepth), backtrackingInfo.colour_to_move, beta - 1, beta);
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		if (inCheck) {
			if (!env.getBitboard().hasMoveInCheck()) {
				
				node.eval = -getMateVal(depth);
				
				return node.eval;
			}
		}
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores();
			
			return node.eval;
		}
		
		int rest = normDepth(maxdepth) - depth;
		
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
				tpt_move = tptEntry.getBestMove_lower();
				if (tpt_move == 0) {
					tpt_move = tptEntry.getBestMove_upper();
				}
				backtrackingInfo.hash_move = tpt_move;
				backtrackingInfo.eval = tpt_exact ? tpt_lower : 0;
			}
		}
		env.getTPT().unlock();
		
		if (tpt_found && tpt_depth >= rest
				
				) {
			
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.nullmove = false;
					node.leaf = true;
					
					extractFromTPT(info, rest, node, beta - 1, beta, false);
					
					return node.eval;
				}
			} else if (!pv) {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = tpt_move;
						node.eval = tpt_lower;
						node.nullmove = false;
						node.leaf = true;
						
						extractFromTPT(info, rest, node, beta - 1, beta, false);
						
						return node.eval;
					}
				}
				if (tpt_upper <= beta - 1) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						node.bestmove = tpt_move;
						node.eval = tpt_upper;
						node.nullmove = false;
						node.leaf = true;
						
						extractFromTPT(info, rest, node, beta - 1, beta, false);
						
						return node.eval;
					}
				}
			}
		}
		
		//Check extension
		int extend_position = inCheck ? PLY : 0;
		
		//Recapture extension
		if (extend_position == 0) {
			if (backtrackingInfo.material_exchanged == 0
					&& MoveInt.isCaptureOrPromotion(env.getBitboard().getLastMove())
				) {
				//extend_position = PLY;
			}
		}
		
		
		rest = normDepth(maxdepth) - depth;
		
		
		//Quiescence search
		if (rest + normDepth(extend_position) <= 0) {
			node.eval = qsearch(mediator, info, depth, beta, 100, pv, storeInTPT, true);
			
			if (pv) {
				if (node.eval >= beta && env.getTactics().silentButDeadly()) {
					extend_position = PLY;
				} else {
					return node.eval;
				}
			}  else {
				return node.eval;
			}
		}
		
		
		rest = normDepth(maxdepth) - depth;
		
		
		//Null move
		boolean prevIsNullmove = depth > 0 ? backtracking[depth - 1].null_move : false;
		if (!inCheck
			//&& !pv
			&& depth > 0
			&& rest >= 1
			&& !isMateVal(beta - 1)
			&& !isMateVal(beta)
			&& !prevIsNullmove
			) {
			
			boolean hasAtLeastOnePiece = (backtrackingInfo.colour_to_move == Figures.COLOUR_WHITE) ?
					env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
					env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
					
			if (hasAtLeastOnePiece) {
				
				if (backtrackingInfo.eval == BacktrackingInfo.EVAL_NOT_CALCULATED) {
					backtrackingInfo.eval = eval(depth, beta);
				}
				
				if (backtrackingInfo.eval >= beta) {
					
					int reduction = (rest / 2) * PLY;
					reduction = Math.max(reduction, PLY);
					
					node.bestmove = 0;
					node.eval = MIN;
					node.nullmove = true;
					node.leaf = true;
					backtrackingInfo.null_move = true;
					env.getBitboard().makeNullMoveForward();
					int null_eval = -negasearch(mediator, rootWin, info, maxdepth - reduction, depth + 1, -(beta + 1), false, storeInTPT);
					
					if (//null_eval < 0 && isMateVal(null_eval)
							true//backtrackingInfo.eval - null_eval > 0
							) {
						TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKey());
						if (entry != null) {
							backtrackingInfo.mate_move = entry.getBestMove_lower();
							if (backtrackingInfo.mate_move == 0) {
								backtrackingInfo.mate_move = entry.getBestMove_upper();
							}
						}
					}
					
					env.getBitboard().makeNullMoveBackward();
					backtrackingInfo.null_move = false;
					
					if (!pv && null_eval > beta) {
						node.bestmove = 0;
						node.eval = null_eval;
						node.nullmove = true;
						node.leaf = true;
						
						return node.eval;
					}
				}
			}
		}
		
		
        // Razoring
        /*if (!inCheck
        		&& rest <= 4
        		&& rest >= 2
        		&& !isMateVal(beta - 1)
    			&& !isMateVal(beta)
    			) {
        	
			if (backtrackingInfo.eval == BacktrackingInfo.EVAL_NOT_CALCULATED) {
				backtrackingInfo.eval = eval(depth, beta);
			}
			
            final int razorMargin = 255;
            if (backtrackingInfo.eval < beta - razorMargin) {
            	int razoring_eval = qsearch(mediator, info, depth, beta - razorMargin, 100, pv, false, true);
            	
                if (razoring_eval < beta - razorMargin) {
                	razoring_eval = negasearch(mediator, rootWin, info, maxdepth - (rest / 2) * PLY, depth, beta - razorMargin, pv, false);
                	
                	if (razoring_eval < beta - razorMargin) {
                		node.eval = razoring_eval;
						return node.eval;
                	}
                }
            }
        }*/
		
		rest = normDepth(maxdepth) - depth;
		
        boolean futility_enabled = false;
        int futility_eval = beta - 1;
        if (rest <= 5
        	&& !pv
    		&& extend_position == 0 //e.g. not in check
    		&& !isMateVal(beta - 1)
			&& !isMateVal(beta)
			) {
        	
            int margin;
            if (rest <= 1) {
                margin = 61;
            } else if (rest <= 2) {
                margin = 144;
            } else if (rest <= 3) {
                margin = 268;
            } else if (rest <= 4) {
                margin = 334;
            } else if (rest <= 5) {
            	margin = 500;
            } else {
            	throw new IllegalStateException("rest=" + rest);
            }
            
			if (backtrackingInfo.eval == BacktrackingInfo.EVAL_NOT_CALCULATED) {
				backtrackingInfo.eval = eval(depth, beta);
			}
			
            futility_eval = backtrackingInfo.eval + margin;
            if (futility_eval < beta) {
                futility_enabled = true;
            }
        }
		
        int mate_move = depth > 0 ? backtracking[depth - 1].mate_move : 0;
        
        if (tpt_move == 0) {
			
			int reduction = PLY * (rest / 2);
			//reduction = Math.max(reduction, PLY);
			
			if (reduction >= 1) {
				
				negasearch(mediator, rootWin, info, maxdepth - reduction, depth, beta, pv, storeInTPT);
				
				env.getTPT().lock();
				{
					TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
					if (tptEntry != null) {
						tpt_found = true;
						tpt_exact = tptEntry.isExact();
						tpt_depth = tptEntry.getDepth();
						tpt_lower = tptEntry.getLowerBound();
						tpt_upper = tptEntry.getUpperBound();
						tpt_move = tptEntry.getBestMove_lower();
						if (tpt_move == 0) {
							tpt_move = tptEntry.getBestMove_upper();
						}
						backtrackingInfo.hash_move = tpt_move;
						backtrackingInfo.eval = tpt_exact ? tpt_lower : 0;
					}
				}
				env.getTPT().unlock();
			}
        }
        
        
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		ISearchMoveList list = null;
		
		if (!inCheck) {
			list = lists_all[depth];
			list.clear();
			((ListAll)list).setTptMove(tpt_move);
			((ListAll)list).setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
			((ListAll)list).setMateMove(mate_move);
		} else {
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(tpt_move);
			((ListKingEscapes)list).setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
		}
		
		
		boolean statisticAdded = false;
		
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
					if (depth == 0) {
						info.setCurrentMove(cur_move);
						info.setCurrentMoveNumber((searchedCount + 1));
					}
					if ((maxdepth / PLY) <= 7) {
						if (depth == 0) { 
							mediator.changedMinor(info);
						}
					} else {
						double send_depth = (maxdepth / PLY) / (double) 2;
						if (depth <= send_depth) { 
							mediator.changedMinor(info);
						}
					}
				}
				
				
				boolean isCapOrProm = MoveInt.isCaptureOrPromotion(cur_move);
				boolean isPasserPush = isPasserPush(cur_move);
				
				
				//Futility pruning
				if (futility_enabled
                		//&& !pv
						//&& !(cur_move == tpt_move)
						//&& !(cur_move == mate_move)
						&& searchedCount > 0
						&& !isPasserPush
						&& !isCapOrProm
						&& !env.getBitboard().isCheckMove(cur_move)
					) {
					
					if (futility_eval > best_eval) {
						
						best_eval = futility_eval;
						best_move = cur_move;
						
						backtrackingInfo.best_move = best_move;
					}
					
					continue;
				}
				
				int moveSee = -1;
				if (isCapOrProm) {
					moveSee = env.getBitboard().getSee().evalExchange(cur_move);
				}
				
				//int new_materialGain = materialGain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				if  (isCapOrProm) {
					backtrackingInfo.material_exchanged += env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				}
				
				env.getBitboard().makeMoveForward(cur_move);
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				boolean reductionAllowed = !inCheck
											//&& !(cur_move == tpt_move)
											//&& !(cur_move == mate_move)
											&& !isCapOrProm
											//&& moveSee < 0
											&& !isPasserPush
											&& !isCheckMove;
				
				int extend = extend_position;// + (moveSee > 0 ? PLY / 4 : 0);
				
				//LMR
                int reduction = 0;
                if (reductionAllowed) {
                	
                	reduction += PLY;
                	//if (rest >= 2 /*&& searchedCount >= getLMR1(list)*/) {
                		reduction += PLY;
                	//}
                	
                	//if (rest >= 2) {
                	//	reduction += searchedCount >= getLMR1(list) ? PLY : 0;
                	//}
                	//if (rest >= 3) {
                	//	reduction += searchedCount >= getLMR2(list) ? PLY : 0;
                	//}
                }
				
				if (env.getBitboard().isInCheck(backtrackingInfo.colour_to_move)) {
					throw new IllegalStateException();
				}
				legalMoves++;
				
				//boolean isCheckMove = env.getBitboard().isInCheck();
				
				
				int new_maxdepth = maxdepth + extend;
				
				int cur_eval = -negasearch(mediator, rootWin, info, new_maxdepth - reduction, depth + 1, -(beta - 1), false, storeInTPT);
				if (reduction > 0 && cur_eval >= beta) {
					cur_eval = -negasearch(mediator, rootWin, info, new_maxdepth, depth + 1, -(beta - 1), false, storeInTPT);
				}
				
				if (pv && cur_eval > best_eval) {
					cur_eval = -negasearch(mediator, rootWin, info, new_maxdepth, depth + 1, -(beta - 1), pv, storeInTPT);
				}
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				if (cur_eval > best_eval) {
					
					best_eval = cur_eval;
					best_move = cur_move;
					
					backtrackingInfo.best_move = best_move;
					
					//if (isNonAlphaNode(cur_eval, best_eval, beta - 1, beta)) {
						
						node.bestmove = best_move;
						node.eval = best_eval;
						node.leaf = false;
						node.nullmove = false;
						
						if (depth + 1 < MAX_DEPTH) {
							pvman.store(depth + 1, node, pvman.load(depth + 1), true);
						}
					//}
					
					if (best_eval >= beta) {
						
						if (tpt_move == best_move) {
							list.countStatistics(best_move);
						}
						list.updateStatistics(best_move);
						
						statisticAdded = true;
						
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
		
		if (!statisticAdded) {
			if (tpt_move == best_move) {
				list.countStatistics(best_move);
			}
			list.updateStatistics(best_move);
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
					throw new IllegalStateException("hashkey=" + backtrackingInfo.hash_key);
				}
			} else {
				if (legalMoves == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores();
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					throw new IllegalStateException("hashkey=" + backtrackingInfo.hash_key);
				}
			}
		}
		
		if (best_move == 0 || best_eval == MIN || best_eval == MAX) {
			throw new IllegalStateException();
		}
		
		/*if (best_eval < alpha) {
			best_eval = alpha;
		}
		if (best_eval > beta) {
			best_eval = beta;
		}*/
		
		if (storeInTPT) {
			env.getTPT().lock();
			env.getTPT().put(backtrackingInfo.hash_key, normDepth(maxdepth), depth, backtrackingInfo.colour_to_move, best_eval, beta - 1, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		return best_eval;
	}


	private int qsearch(ISearchMediator mediator, ISearchInfo info, int depth, int beta, int checksCount, boolean pv, boolean storeInTPT, boolean firstQlevel) {
		
		
		if (!firstQlevel) {
			info.setSearchedNodes(info.getSearchedNodes() + 1);	
			if (info.getSelDepth() < depth) {
				info.setSelDepth(depth);
			}
		}
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		long hashkey = env.getBitboard().getHashKey();
		if (backtrackingInfo.hash_key != hashkey) {
			backtrackingInfo.hash_key = hashkey;
			backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
			backtrackingInfo.hash_move = 0;
			backtrackingInfo.null_move = false;//depth > 0 ? backtracking[depth - 1].null_move: false;
			backtrackingInfo.eval = BacktrackingInfo.EVAL_NOT_CALCULATED;
			backtrackingInfo.best_move = 0;
			backtrackingInfo.mate_move = 0;
			backtrackingInfo.material_exchanged = 0;
			
			
			//Get tpt move
			int tpt_move = 0;
			
			env.getTPT().lock();
			{
				TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
				if (tptEntry != null) {
					tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
				}
			}
			env.getTPT().unlock();
			
			backtrackingInfo.hash_move = tpt_move;
		}
		
		
		backtrackingInfo.eval = eval(depth, beta);
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.eval;
		}
		
		//int colourToMove = env.getBitboard().getColourToMove();
		
		
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(0), backtrackingInfo.colour_to_move, beta - 1, beta);
		
		
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
		
		if (!inCheck) {
			if (backtrackingInfo.eval >= beta) {
				node.eval = backtrackingInfo.eval;
				return node.eval;
			}
		}
		
		
		IMoveList list = null;
		if (inCheck) { 
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(backtrackingInfo.hash_move);
		} else {
			list = lists_capsproms[depth];
			list.clear();
			((ListCapsProm)list).setTptMove(backtrackingInfo.hash_move);
		}
		
		int legalMoves = 0;
		int best_eval = MIN;
		int best_move = 0;
		int cur_move = 0;
		
		if (inCheck) {
			cur_move = (backtrackingInfo.hash_move != 0) ? backtrackingInfo.hash_move : list.next();
		} else {
			cur_move = (backtrackingInfo.hash_move != 0 && MoveInt.isCaptureOrPromotion(backtrackingInfo.hash_move))
							? backtrackingInfo.hash_move : list.next();
			//cur_move = (tpt_move != 0) ? tpt_move : list.next();
		}
		
		int searchedMoves = 0;
		if (cur_move != 0) 
		do {
			
			if (searchedMoves > 0 && cur_move == backtrackingInfo.hash_move) {
				continue;
			}
			searchedMoves++;
			
			if (MoveInt.isCapture(cur_move)) {
				if (MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
					throw new IllegalStateException(env.getBitboard().toString());
				}
			}
			
			int moveSee = env.getBitboard().getSee().evalExchange(cur_move);
			
			if (inCheck) {
				//All moves
			} else {
				if (MoveInt.isCaptureOrPromotion(cur_move)) {
					if (moveSee >= 0) {
						//All moves
					} else {
		                int optimisticScore = 100 + backtrackingInfo.eval
		                	+ env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
		                
		                if (optimisticScore <= beta - 1) { // Delta pruning
		                	continue;
		                }
					}
				} else {
					if (checksCount <= 0 || !env.getBitboard().isCheckMove(cur_move)) {
						continue;
					} else {
						//All moves
						checksCount--;
					}
				}
			}
			
			env.getBitboard().makeMoveForward(cur_move);
			
			if (env.getBitboard().isInCheck(backtrackingInfo.colour_to_move)) {
				throw new IllegalStateException("! " + env.getBitboard().toString());
			}
			legalMoves++;
			
			int cur_eval = -qsearch(mediator, info, depth + 1, -(beta - 1), checksCount, pv, storeInTPT, false);
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			if (cur_eval > best_eval) {
				best_eval = cur_eval;
				best_move = cur_move;
				
				backtrackingInfo.best_move = best_move;
				
				if (best_eval > beta - 1) {
					
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					if (depth + 1 < MAX_DEPTH) {
						pvman.store(depth + 1, node, pvman.load(depth + 1), true);
					}
				}
				
				if (best_eval >= beta) {
					
					if (inCheck) {
						env.getHistory_check().goodMove(cur_move, 1, best_eval > 0 && isMateVal(best_eval));
					} else {
					}
					
					break;
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
		
		if (!inCheck && backtrackingInfo.eval > best_eval) {
			best_move = 0;
			best_eval = backtrackingInfo.eval;
			
			node.leaf = true;
			node.eval = backtrackingInfo.eval;
			node.bestmove = 0;
			node.nullmove = false;
		}
		
		/*if (best_eval < alpha) {
			best_eval = alpha;
		}
		if (best_eval > beta) {
			best_eval = beta;
		}*/
		
		if (storeInTPT) {
			if (best_move != 0) {
				env.getTPT().lock();
				env.getTPT().put(hashkey, 0, 0, env.getBitboard().getColourToMove(), best_eval, beta - 1, beta, best_move, (byte)0);
				env.getTPT().unlock();
			}
		}
		
		return best_eval;
	}
	
	
	private int eval(int depth, int beta) {
		//return fullEval(depth, alpha, beta, -1);
		return lazyEval(depth, beta - 1, beta, -1);
	}
	
	
	private boolean isPasserPush(int cur_move) {
		boolean passerPush = env.getBitboard().isPasserPush(cur_move);
		return passerPush;
	}
	
	private int getLMR1(ISearchMoveList list) {
		return (int) Math.max(1, Math.sqrt(list.size()) / (double)2);
	}
	
	private int getLMR2(ISearchMoveList list) {
		return (int) Math.max(1, Math.sqrt(list.size()));
	}
	
	protected boolean extractFromTPT(ISearchInfo info, int depth, PVNode result, int alpha, int beta, boolean markup) {
		env.getTPT().lock();
		boolean res = extractFromTPT(info, depth, result, alpha, beta);
		env.getTPT().unlock();
		return res;
	}
	
	private boolean extractFromTPT(ISearchInfo info, int depth, PVNode result, int alpha, int beta) {
		
		//if (true) throw new IllegalStateException("Not thread safe"); 
		
		if (result == null) {
			return false;
			//throw new IllegalStateException();
		}
		
		result.bestmove = 0;
		result.leaf = true;
		result.nullmove = false;
		
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		//if (depth <= 0) {
		//	return false;
		//}
		
		if (isDrawPV(depth)) {
			result.eval = getDrawScores();
			//TODO: Consider result.eval = getDrawScores();
			return true;
		}
		
		boolean draw = false;
		
		long hashkey = env.getBitboard().getHashKey();
		
		TPTEntry entry = env.getTPT().get(hashkey);
		
		if (entry == null) {
			//throw new IllegalStateException("entry == null");
			return false;
		}
		
		if (entry != null) {
			//if (entry.getDepth() >= depth) {
				
				info.setSearchedNodes(info.getSearchedNodes() + 1);
				
				if (entry.isExact()) {
					result.bestmove = entry.getBestMove_lower();
				} else {
					if (entry.getLowerBound() >= beta) {
						result.bestmove = entry.getBestMove_lower();
					} else if (entry.getUpperBound() <= alpha) {
						result.bestmove = entry.getBestMove_upper();
					} else {
						//throw new IllegalStateException("alpha=" + alpha + ", beta=" + beta + ", entry.getBestMove_lower()=" + entry.getBestMove_lower() + ", entry.getBestMove_upper()=" + entry.getBestMove_upper());
						result.bestmove = entry.getBestMove_lower();
						if (result.bestmove == 0) {
							result.bestmove = entry.getBestMove_upper();
						}
					}
				}

				
				//if (result.bestmove == 0) {
				//	return false;
				//}
				
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
				
				draw = extractFromTPT(info, depth - 1, result.child, -beta, -alpha);
				//TODO: Consider if (draw) result.eval = getDrawScores();
				
				if (result.bestmove == 0) {
					env.getBitboard().makeNullMoveBackward();
				} else {
					env.getBitboard().makeMoveBackward(result.bestmove);
				}
			//}
		}
		
		return draw;
	}
}
