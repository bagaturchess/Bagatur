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
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
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
		
		return negasearch(mediator, info, maxdepth, depth, beta, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return negasearch(mediator, info, maxdepth, depth, beta, false);
	}
	
	
	private int negasearch(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int depth, int beta, boolean pv) {
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
		backtrackingInfo.hash_move = 0;
		backtrackingInfo.null_move = false;
		backtrackingInfo.static_eval = BacktrackingInfo.EVAL_NOT_CALCULATED;
		backtrackingInfo.best_move = 0;
		backtrackingInfo.mate_move = 0;
		backtrackingInfo.material_exchanged = depth > 0 ? -backtracking[depth - 1].material_exchanged : 0;
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		//Check for max depth
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
		
		//Mate check
		if (inCheck) {
			if (!env.getBitboard().hasMoveInCheck()) {
				
				node.eval = -getMateVal(depth);
				
				return node.eval;
			}
		}
		
		//Draw check
		if (pv) {
			if (isDrawPV(depth)) {
				node.eval = getDrawScores();
				return node.eval;
			}
		} else {
			if (isDraw()) {
				node.eval = getDrawScores();
				return node.eval;
			}
		}
		
		
		//Get TPT entry
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
			}
		}
		env.getTPT().unlock();
		
		int rest = normDepth(maxdepth) - depth;
		
		if (!pv
				&& tpt_found && tpt_depth >= rest
				
				) {
			
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.nullmove = false;
					node.leaf = true;
					
					return node.eval;
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = tpt_move;
						node.eval = tpt_lower;
						node.nullmove = false;
						node.leaf = true;
						
						return node.eval;
					}
				}
				if (tpt_upper <= beta - 1) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						node.bestmove = tpt_move;
						node.eval = tpt_upper;
						node.nullmove = false;
						node.leaf = true;
						
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
		
		
		//Quiescence search
		if (rest + normDepth(extend_position) <= 0) {
			node.eval = qsearch(mediator, info, depth, beta);
			
			if (false && pv) {
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
			&& !prevIsNullmove
			&& !pv
			&& depth > 0
			&& rest >= 1
			&& !isMateVal(beta - 1)
			&& !isMateVal(beta)
			) {
			
			boolean hasAtLeastOnePiece = (backtrackingInfo.colour_to_move == Figures.COLOUR_WHITE) ?
					env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
					env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
					
			if (hasAtLeastOnePiece) {
				
				if (backtrackingInfo.static_eval == BacktrackingInfo.EVAL_NOT_CALCULATED) {
					backtrackingInfo.static_eval = eval(depth, beta);
				}
				
				if (backtrackingInfo.static_eval >= beta) {
					
					int reduction = (rest / 2) * PLY;
					reduction = Math.max(reduction, PLY);
					
					node.bestmove = 0;
					node.eval = MIN;
					node.nullmove = true;
					node.leaf = true;
					backtrackingInfo.null_move = true;
					env.getBitboard().makeNullMoveForward();
					int null_eval = -negasearch(mediator, info, maxdepth - reduction, depth + 1, -(beta + 1), false);
					
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
		
		
		rest = normDepth(maxdepth) - depth;
		
		//Static pruning conditions
        boolean futility_enabled = false;
        int futility_eval = beta - 1;
        if (!pv
    		&& extend_position == 0 //e.g. not in check
    		&& !isMateVal(beta - 1)
			&& !isMateVal(beta)
			) {
        	
        	//Static pruning for all depths
            int margin = (int) getAlphaTrustWindow(mediator, rest);
            
            /*int margin;
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
            }*/
            
			if (backtrackingInfo.static_eval == BacktrackingInfo.EVAL_NOT_CALCULATED) {
				backtrackingInfo.static_eval = eval(depth, beta);
			}
			
            futility_eval = backtrackingInfo.static_eval + margin;
            if (futility_eval < beta) {
                futility_enabled = true;
            }
        }
        
        
        //IID - internal iterative deepening
        if (tpt_move == 0) {
			
			int reduction = (PLY * rest) / 2;
			//reduction = Math.max(reduction, PLY);
			
			if (reduction >= PLY) {
				
				negasearch(mediator, info, maxdepth - reduction, depth, beta, false);
				
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
			
			 int mate_move = depth > 0 ? backtracking[depth - 1].mate_move : 0;
			
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
				
				
				boolean isCapOrProm = MoveInt.isCaptureOrPromotion(cur_move);
				//boolean isPasserPush = isPasserPush(cur_move);
				
				int moveSee = -1;
				if (isCapOrProm) {
					moveSee = env.getBitboard().getSee().evalExchange(cur_move);
				}
				
				
				//Futility pruning
				if (futility_enabled
                		//&& !pv
						//&& !(cur_move == tpt_move)
						//&& !(cur_move == mate_move)
						&& !inCheck
						&& moveSee < 0
						&& searchedCount > 0
						//&& !isPasserPush
						//&& !isCapOrProm
						&& !env.getBitboard().isCheckMove(cur_move)
					) {
					
					if (futility_eval > best_eval) {
						
						best_eval = futility_eval;
						best_move = cur_move;
						
						backtrackingInfo.best_move = best_move;
						
						node.bestmove = best_move;
						node.eval = best_eval;
						
						info.setSearchedNodes(info.getSearchedNodes() + 1);	
					}
					
					continue;
				}
				
				//int new_materialGain = materialGain + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				if  (isCapOrProm) {
					backtrackingInfo.material_exchanged += env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				}
				
				env.getBitboard().makeMoveForward(cur_move);
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				boolean reductionAllowed = !inCheck
											&& moveSee < 0
											&& searchedCount > 0
											&& !isCheckMove;
				
				int extend = extend_position;// + (moveSee > 0 ? PLY / 4 : 0);
				
				//LMR
                int reduction = 0;
                if (reductionAllowed) {
                	//reduction = 2 * PLY;
					double rate = Math.sqrt(searchedCount);
					reduction = (int) (PLY * rate);
					if (reduction < PLY) {
						reduction = PLY;
					}
					if (reduction >= (rest - 1) * PLY) {
						reduction = (rest - 1) * PLY;
					}
                }
				
				if (env.getBitboard().isInCheck(backtrackingInfo.colour_to_move)) {
					throw new IllegalStateException();
				}
				legalMoves++;
				
				//boolean isCheckMove = env.getBitboard().isInCheck();
				
				
				int new_maxdepth = maxdepth + extend;
				
				int cur_eval = -negasearch(mediator, info, new_maxdepth - reduction, depth + 1, -(beta - 1), false);
				if (reduction > 0 && cur_eval >= beta) {
					cur_eval = -negasearch(mediator, info, new_maxdepth, depth + 1, -(beta - 1), false);
				}
				
				if (pv && cur_eval > best_eval) {
					cur_eval = -negasearch(mediator, info, new_maxdepth, depth + 1, -(beta - 1), true);
				}
				
				env.getBitboard().makeMoveBackward(cur_move);
				
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
		
		
		env.getTPT().lock();
		env.getTPT().put(backtrackingInfo.hash_key, normDepth(maxdepth), depth, backtrackingInfo.colour_to_move, best_eval, beta - 1, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		return best_eval;
	}
	
	
	private int qsearch(ISearchMediator mediator, ISearchInfo info, int depth, int beta) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
		backtrackingInfo.hash_move = 0;
		backtrackingInfo.null_move = false;
		backtrackingInfo.static_eval = BacktrackingInfo.EVAL_NOT_CALCULATED;
		backtrackingInfo.best_move = 0;
		backtrackingInfo.mate_move = 0;
		backtrackingInfo.material_exchanged = 0;
		
		
		//Check for max depth
		backtrackingInfo.static_eval = eval(depth, beta);
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
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
		
		
		//Get TPT entry
		boolean tpt_exact = false;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
		
		env.getTPT().lock();
		{
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			if (tptEntry != null) {
				tpt_exact = tptEntry.isExact();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				tpt_move = tptEntry.getBestMove_lower();
				if (tpt_move == 0) {
					tpt_move = tptEntry.getBestMove_upper();
				}
			}
		}
		env.getTPT().unlock();
		
		if (tpt_exact) {
			if (!SearchUtils.isMateVal(tpt_lower)) {
				node.bestmove = tpt_move;
				node.eval = tpt_lower;
				node.nullmove = false;
				node.leaf = true;
				
				return node.eval;
			}
		} else {
			if (tpt_lower >= beta) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.nullmove = false;
					node.leaf = true;
					
					return node.eval;
				}
			}
			if (tpt_upper <= beta - 1) {
				if (!SearchUtils.isMateVal(tpt_upper)) {
					node.bestmove = tpt_move;
					node.eval = tpt_upper;
					node.nullmove = false;
					node.leaf = true;
					
					return node.eval;
				}
			}
		}

		
		backtrackingInfo.hash_move = tpt_move;
		
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		if (!inCheck) {
			if (backtrackingInfo.static_eval >= beta) {
				node.eval = backtrackingInfo.static_eval;
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
		                int optimisticScore = 100 + backtrackingInfo.static_eval
		                	+ env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
		                
		                if (optimisticScore <= beta - 1) { // Delta pruning
		                	continue;
		                }
					}
				} else {
					continue;
				}
			}
			
			env.getBitboard().makeMoveForward(cur_move);
			
			if (env.getBitboard().isInCheck(backtrackingInfo.colour_to_move)) {
				throw new IllegalStateException("! " + env.getBitboard().toString());
			}
			legalMoves++;
			
			int cur_eval = -qsearch(mediator, info, depth + 1, -(beta - 1));
			
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
		
		if (!inCheck && backtrackingInfo.static_eval > best_eval) {
			best_move = 0;
			best_eval = backtrackingInfo.static_eval;
			
			node.leaf = true;
			node.eval = backtrackingInfo.static_eval;
			node.bestmove = 0;
			node.nullmove = false;
		}
		
		
		if (best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(backtrackingInfo.hash_key, 0, 0, env.getBitboard().getColourToMove(), best_eval, beta - 1, beta, best_move, (byte)0);
			env.getTPT().unlock();
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
	
	private double getAlphaTrustWindow(ISearchMediator mediator, int rest) {
		return 1 * mediator.getTrustWindow_AlphaAspiration();
	}
}
