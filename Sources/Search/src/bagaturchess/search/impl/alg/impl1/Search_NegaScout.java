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
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.gaviota.GTBProbeOutput;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.alg.BacktrackingInfo;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.movelists.ListAll;
import bagaturchess.search.impl.movelists.ListCapsProm;
import bagaturchess.search.impl.movelists.ListKingEscapes;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class Search_NegaScout extends SearchImpl {
	
	
	private BacktrackingInfo[] backtracking = new BacktrackingInfo[MAX_DEPTH + 1];
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	private static final double LMR_REDUCTION_MULTIPLIER 		= 1;//1 * 1.222 * 1;
	private static final double NULL_MOVE_REDUCTION_MULTIPLIER 	= 1;//1 * 0.777 * 1;
	
	
	public Search_NegaScout(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_NegaScout(SearchEnv _env) {
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
		
		return negasearch(mediator, info, maxdepth, depth, alpha, beta, true, useMateDistancePrunning, rootColour, prevPV, false);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return negasearch(mediator, info, maxdepth, depth, beta - 1, beta, false, useMateDistancePrunning, rootColour, prevPV, false);
	}
	
	
	private int negasearch(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int depth, int alpha_org, int beta, boolean pv, boolean useMateDistancePrunning,
			int rootColour, int[] prevPV, boolean disableExts) {
		
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
		backtrackingInfo.hash_move = 0;
		backtrackingInfo.null_move = false;
		backtrackingInfo.static_eval = eval(depth, alpha_org, beta, pv, rootColour);
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
			return eval(depth, alpha_org, beta, pv, rootColour);
		}
		
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(info.getDepth(), backtrackingInfo.colour_to_move, alpha_org, beta);
		
		
		//Start search iteration
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		//Draw check
		if (pv) {
			if (isDrawPV(depth)) {
				node.eval = getDrawScores(rootColour);
				return node.eval;
			}
		} else {
			if (isDraw()) {
				node.eval = getDrawScores(rootColour);
				return node.eval;
			}
		}
		
		
		//Mate check
		boolean inCheck = env.getBitboard().isInCheck();
		if (inCheck) {
			if (!env.getBitboard().hasMoveInCheck()) {
				
				node.eval = -getMateVal(depth);
				
				return node.eval;
			}
		}
		
		
	    //Mate distance pruning
		if (!inCheck && useMateDistancePrunning && depth >= 1) {
		      
		      // lower bound - the site to move is mate on the move after the next move
		      int value = -getMateVal(depth+2); // does not work if the current position is mate
		      if (value > alpha_org) {
		    	  alpha_org = value;
			      if (alpha_org >= beta) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
			      }
		      }
		      
		      // upper bound - opponent mate in next move
		      value = getMateVal(depth+1);
		      if (value < beta) {
			        beta = value;
			        if (beta <= alpha_org) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
			        }
		      }
		}
		
		
		int rest = normDepth(maxdepth) - depth;
		
		
		//Extensions
		if (!disableExts) {
			if (inCheck) {
				if (rest < 1) {
					//Do not allow the leaf node to be in check
					maxdepth += PLY;
					disableExts = true;
				} else {
					//Check extension
					//maxdepth += PLY;
				}
			} else {
				//Recapture extension
				if (backtrackingInfo.material_exchanged >= 0
						&& MoveInt.isCaptureOrPromotion(env.getBitboard().getLastMove())
					) {
					//maxdepth += PLY;
				}
			}
		}
		
		
		rest = normDepth(maxdepth) - depth;
		
		
		//Get TPT entry
		{
			boolean tpt_found = false;
			boolean tpt_exact = false;
			int tpt_depth = 0;
			int tpt_lower = MIN;
			int tpt_upper = MAX;
			
			env.getTPT().lock();
			{
				TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
				if (tptEntry != null) {
					tpt_found = true;
					tpt_exact = tptEntry.isExact();
					tpt_depth = tptEntry.getDepth();
					tpt_lower = tptEntry.getLowerBound();
					tpt_upper = tptEntry.getUpperBound();
					int tpt_move = tptEntry.getBestMove_lower();
					if (tpt_move == 0) {
						tpt_move = tptEntry.getBestMove_upper();
					}
					backtrackingInfo.hash_move = tpt_move;
				}
			}
			env.getTPT().unlock();
			
			
			if (tpt_found && tpt_depth >= rest
					&& backtrackingInfo.excluded_move == 0
					) {
				
				if (tpt_exact) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = backtrackingInfo.hash_move;
						node.eval = tpt_lower;
						node.nullmove = false;
						node.leaf = true;
						
						env.getTPT().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
						env.getTPT().unlock();
						
						if (buff_tpt_depthtracking[0] >= rest) {
							return node.eval;
						}
					}
				} else {
					if (tpt_lower >= beta) {
						if (!SearchUtils.isMateVal(tpt_lower)) {
							node.bestmove = backtrackingInfo.hash_move;
							node.eval = tpt_lower;
							node.nullmove = false;
							node.leaf = true;
							
							env.getTPT().lock();
							buff_tpt_depthtracking[0] = 0;
							extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
							env.getTPT().unlock();
							
							if (buff_tpt_depthtracking[0] >= rest) {
								return node.eval;
							}
						}
					}
					if (tpt_upper <= alpha_org) {
						if (!SearchUtils.isMateVal(tpt_upper)) {
							node.bestmove = backtrackingInfo.hash_move;
							node.eval = tpt_upper;
							node.nullmove = false;
							node.leaf = true;
							
							env.getTPT().lock();
							buff_tpt_depthtracking[0] = 0;
							extractFromTPT(info, rest, node, true, buff_tpt_depthtracking, rootColour, env.getTPT());
							env.getTPT().unlock();
							
							if (buff_tpt_depthtracking[0] >= rest) {
								return node.eval;
							}
						}
					}
				}
			}
		}
		
		
		//Quiescence search
		if (rest <= 0) {
			node.eval = qsearch(mediator, info, depth, alpha_org, beta, pv, rootColour);
			return node.eval;
		}
		
		
		//Null move
		boolean prevIsNullmove = depth > 0 ? backtracking[depth - 1].null_move : false;
		if (!inCheck
			&& !prevIsNullmove
			&& !pv
			&& depth > 0
			&& rest >= 1
			&& !isMateVal(alpha_org)
			&& !isMateVal(beta)
			) {
			
			boolean hasAtLeastOnePiece = (backtrackingInfo.colour_to_move == Figures.COLOUR_WHITE) ?
					env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
					env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
					
			if (hasAtLeastOnePiece) {
				
				if (backtrackingInfo.static_eval >= beta) {
					
					int reduction = (int) ((NULL_MOVE_REDUCTION_MULTIPLIER * (PLY * rest) / 2));
					reduction = Math.max(reduction, PLY);
					
					node.bestmove = 0;
					node.eval = MIN;
					node.nullmove = true;
					node.leaf = true;
					backtrackingInfo.null_move = true;
					env.getBitboard().makeNullMoveForward();
					int null_eval = -negasearch(mediator, info, maxdepth - reduction, depth + 1, -beta, -(beta - 1), false, useMateDistancePrunning, rootColour, prevPV, disableExts);
					
					
					//Get mate move of opponent
					TPTEntry entry = env.getTPT().get(env.getBitboard().getHashKey());
					if (entry != null) {
						backtrackingInfo.mate_move = entry.getBestMove_lower();
						if (backtrackingInfo.mate_move == 0) {
							backtrackingInfo.mate_move = entry.getBestMove_upper();
						}
					}

					
					env.getBitboard().makeNullMoveBackward();
					backtrackingInfo.null_move = false;
					
					if (null_eval > beta) {
						node.bestmove = 0;
						node.eval = null_eval;
						node.nullmove = true;
						node.leaf = true;
						
						return node.eval;
					}
				}
			}
		}
		
        
        //IID - internal iterative deepening
        if (backtrackingInfo.hash_move == 0) {
			
			int reduction = (PLY * rest) / 2;
			
			if (reduction >= PLY) {
				
				negasearch(mediator, info, maxdepth - reduction, depth, alpha_org, beta, false, useMateDistancePrunning, rootColour, prevPV, true);
				
				env.getTPT().lock();
				{
					TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
					if (tptEntry != null) {
						int tpt_move = tptEntry.getBestMove_lower();
						if (tpt_move == 0) {
							tpt_move = tptEntry.getBestMove_upper();
						}
						backtrackingInfo.hash_move = tpt_move;
					}
				}
				env.getTPT().unlock();
			}
        }
        
        
		//Singular move extension
		int singularExtension = 0;
		
		{
			env.getTPT().lock();
			TPTEntry tptEntry = env.getTPT().get(backtrackingInfo.hash_key);
			env.getTPT().unlock();
			
	        if (depth > 0
	        		&& rest >= depth
	        		&& !disableExts
	        		&& backtracking[depth - 1].excluded_move == 0 //Skip recursive calls
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
						
						int singularBeta = ttValue - 2 * rest;
						
						backtrackingInfo.excluded_move = tptEntry.getBestMove_lower();
						int singularEval = negasearch(mediator, info, maxdepth - reduction, depth, singularBeta - 1, singularBeta, pv, useMateDistancePrunning, rootColour, prevPV, disableExts);
						backtrackingInfo.excluded_move = 0;
						
						if (singularEval < singularBeta) {
							singularExtension = PLY;
							//System.out.println("singularExtension hit");
						}
					}
				}
	        }
		}
		
        
		//Static pruning conditions for all depths
        boolean staticPruningEnabled = false;
        if (!inCheck
        	//&& rest <= 7
    		&& !isMateVal(alpha_org)
			&& !isMateVal(beta)
			) {
            			
            if (alpha_org > backtrackingInfo.static_eval + getAlphaTrustWindow(mediator, rest)) {
                staticPruningEnabled = true;
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
			((ListAll)list).setTptMove(backtrackingInfo.hash_move);
			((ListAll)list).setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
			
			 int mate_move = depth > 0 ? backtracking[depth - 1].mate_move : 0;
			
			((ListAll)list).setMateMove(mate_move);
			
			if (prevPV != null && depth < prevPV.length) {
				list.setPrevpvMove(prevPV[depth]);
			}
		} else {
			list = lists_escapes[depth];
			list.clear();
			((ListKingEscapes)list).setTptMove(backtrackingInfo.hash_move);
			((ListKingEscapes)list).setPrevBestMove(depth > 1 ? backtracking[depth - 2].best_move : 0);
		}
		
		
		int alpha_cur = alpha_org;
		int searchedCount = 0;
		int legalMoves = 0;
		int best_eval = MIN;
		int best_move = 0;
		
		int cur_move = (backtrackingInfo.hash_move != 0) ? backtrackingInfo.hash_move : list.next();
		
		if (cur_move != 0) {
			do {
				
				if (cur_move == backtrackingInfo.excluded_move) {
					continue;
				}
				
				if (searchedCount > 0 && cur_move == backtrackingInfo.hash_move) {
					continue;
				}
				
				
				//Build and sent minor info
				buildAndSendMinorInfo(mediator, info, depth, searchedCount, cur_move);
				
				
				boolean isCapOrProm = MoveInt.isCaptureOrPromotion(cur_move);
				//boolean isPasserPush = env.getBitboard().isPasserPush(cur_move);
				
				int moveSee = env.getBitboard().getSee().evalExchange(cur_move);
				
				//Static pruning
				if (searchedCount > 0
						&& !inCheck
						&& !env.getBitboard().isCheckMove(cur_move)
						
					) {
					
					if (!isCapOrProm) {
						
						//Static pruning for all depths
						if (staticPruningEnabled) {
							continue;
						}
						
						//Static pruning - move count based
						if (rest <= 16) {
							if (searchedCount >= 2.4 + 0.74 * Math.pow(rest, 1.78)) {
								continue;
							}
						}
						
						//Static pruning - history based
						if (rest <= 8) {
							if (getHistory(inCheck).getScores(cur_move) <= 0.32 / Math.pow(2, rest)) {
	 							continue;
	 						}
						}
					}
					
					if (rest <= 9) {
						
						//Static pruning - move SEE based
						if (moveSee < -35 * rest * rest) {
							continue;
						}
						
						if (moveSee < -100 * rest) {
							continue;
						}
					}
				}
				
				
				if  (isCapOrProm) {
					backtrackingInfo.material_exchanged += env.getBitboard().getBaseEvaluation().getMaterialGain(cur_move);
				}
				
				env.getBitboard().makeMoveForward(cur_move);
				
				boolean isCheckMove = env.getBitboard().isInCheck();
				
				boolean reductionAllowed = searchedCount > 0
											&& !inCheck
											&& !isCheckMove
											//&& !isCapOrProm
											&& moveSee <= 0
											//&& rest >= 3
											;
				
				//LMR - late move reduction
                int reduction = 0;
                if (reductionAllowed) {
					double rate = Math.log(searchedCount) * Math.log(rest) / 2;
					rate += 2;
					rate *= (1 - getHistory(inCheck).getScores(cur_move));
					reduction += (int) (PLY * rate * LMR_REDUCTION_MULTIPLIER);
                }
                
                
				legalMoves++;
				
				int extension = 0;
				if (cur_move == backtrackingInfo.hash_move) {
					extension += singularExtension;
				}
				
				int new_maxdepth = maxdepth + extension;
				
				int cur_eval = -negasearch(mediator, info, new_maxdepth - reduction, depth + 1, -(alpha_cur + 1), -alpha_cur, false, useMateDistancePrunning, rootColour, prevPV, disableExts);
				if (reduction > 0 && cur_eval >= beta) {
				//if (reduction > 0 && cur_eval >= alpha_cur) {
					cur_eval = -negasearch(mediator, info, new_maxdepth, depth + 1, -(alpha_cur + 1), -alpha_cur, false, useMateDistancePrunning, rootColour, prevPV, disableExts);
				}
				
				if (pv && cur_eval > best_eval) {
					cur_eval = -negasearch(mediator, info, new_maxdepth, depth + 1, -beta, -alpha_cur, true, useMateDistancePrunning, rootColour, prevPV, disableExts);
				}
				
				env.getBitboard().makeMoveBackward(cur_move);
				
				//Add history records for the current move
				list.countTotal(cur_move);
				if (cur_eval <= alpha_cur) {
					getHistory(inCheck).countFailure(cur_move, rest);
				} else {
					list.countSuccess(cur_move);//Should be before addCounterMove call
					getHistory(inCheck).countSuccess(cur_move, rest);
					getHistory(inCheck).addCounterMove(env.getBitboard().getLastMove(), cur_move);
				}
				
				if (cur_eval > best_eval) {
					
					if (cur_eval > alpha_cur) {
						alpha_cur = cur_eval;
					}
					
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
					throw new IllegalStateException("Mate not detected in the beginning of the search");
				} else {
					throw new IllegalStateException("hashkey=" + backtrackingInfo.hash_key);
				}
			} else {
				if (legalMoves == 0) {
					node.bestmove = 0;
					node.eval = getDrawScores(rootColour);
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
		env.getTPT().put(backtrackingInfo.hash_key, normDepth(maxdepth), depth, backtrackingInfo.colour_to_move, best_eval, alpha_org, beta, best_move, (byte)0);
		env.getTPT().unlock();
		
		return best_eval;
	}
	
	
	private int qsearch(ISearchMediator mediator, ISearchInfo info, int depth, int alpha_org, int beta, boolean pv, int rootColour) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);	
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		BacktrackingInfo backtrackingInfo = backtracking[depth];
		backtrackingInfo.hash_key = env.getBitboard().getHashKey();
		backtrackingInfo.colour_to_move = env.getBitboard().getColourToMove();
		backtrackingInfo.hash_move = 0;
		backtrackingInfo.null_move = false;
		backtrackingInfo.static_eval = eval(depth, alpha_org, beta, pv, rootColour);
		backtrackingInfo.best_move = 0;
		backtrackingInfo.mate_move = 0;
		backtrackingInfo.material_exchanged = depth > 0 ? -backtracking[depth - 1].material_exchanged : 0;
		
		
		//Check for max depth
		if (depth >= MAX_DEPTH) {
			return backtrackingInfo.static_eval;
		}
		
		//Start search iteration
		PVNode node = pvman.load(depth);
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		//Draw check
		if (pv) {
			if (isDrawPV(depth)) {
				node.eval = getDrawScores(rootColour);
				return node.eval;
			}
		} else {
			if (isDraw()) {
				node.eval = getDrawScores(rootColour);
				return node.eval;
			}
		}
		
		
		//Mate check
		boolean inCheck = env.getBitboard().isInCheck();
		if (inCheck) {
			if (!env.getBitboard().hasMoveInCheck()) {
				
				node.eval = -getMateVal(depth);
				
				return node.eval;
			}
		}
		
		
		//Get TPT entry
		int tpt_move = 0;
		{
			boolean tpt_exact = false;
			int tpt_lower = MIN;
			int tpt_upper = MAX;
			
			env.getTPTQS().lock();
			{
				TPTEntry tptEntry = env.getTPTQS().get(backtrackingInfo.hash_key);
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
			env.getTPTQS().unlock();
			
			if (tpt_exact) {
				if (!SearchUtils.isMateVal(tpt_lower)) {
					node.bestmove = tpt_move;
					node.eval = tpt_lower;
					node.nullmove = false;
					node.leaf = true;
					
					env.getTPTQS().lock();
					buff_tpt_depthtracking[0] = 0;
					extractFromTPT(info, 0, node, true, buff_tpt_depthtracking, rootColour, env.getTPTQS());
					env.getTPTQS().unlock();
					
					if (buff_tpt_depthtracking[0] >= 0) {
						return node.eval;
					}
				}
			} else {
				if (tpt_lower >= beta) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = tpt_move;
						node.eval = tpt_lower;
						node.nullmove = false;
						node.leaf = true;
						
						env.getTPTQS().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, 0, node, true, buff_tpt_depthtracking, rootColour, env.getTPTQS());
						env.getTPTQS().unlock();
						
						if (buff_tpt_depthtracking[0] >= 0) {
							return node.eval;
						}
					}
				}
				if (tpt_upper <= alpha_org) {
					if (!SearchUtils.isMateVal(tpt_upper)) {
						node.bestmove = tpt_move;
						node.eval = tpt_upper;
						node.nullmove = false;
						node.leaf = true;
						
						env.getTPTQS().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, 0, node, true, buff_tpt_depthtracking, rootColour, env.getTPTQS());
						env.getTPTQS().unlock();
						
						if (buff_tpt_depthtracking[0] >= 0) {
							return node.eval;
						}
					}
				}
			}
		}
		
		backtrackingInfo.hash_move = tpt_move;
		
		
		//Static pruning
		if (!inCheck) {
			
			//Beta cutoff
			if (backtrackingInfo.static_eval >= beta) {
				node.eval = backtrackingInfo.static_eval;
				return node.eval;
			}
			
			//Alpha cutoff
			if (!isMateVal(alpha_org)
					&& !isMateVal(beta)
					&& backtrackingInfo.static_eval + env.getBitboard().getBaseEvaluation().getMaterial(Figures.TYPE_QUEEN) + getAlphaTrustWindow(mediator, 1) < alpha_org) {
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
		
		int alpha_cur = alpha_org;
		int best_eval = MIN;
		int best_move = 0;
		int cur_move = (backtrackingInfo.hash_move != 0) ? backtrackingInfo.hash_move : list.next();
		
		
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
			
			env.getBitboard().makeMoveForward(cur_move);
			
			int cur_eval = -qsearch(mediator, info, depth + 1, -beta, -alpha_cur, pv, rootColour);
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			if (cur_eval > best_eval) {
				
				if (cur_eval > alpha_cur) {
					alpha_cur = cur_eval;
				}
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				backtrackingInfo.best_move = best_move;
				
				node.bestmove = best_move;
				node.eval = best_eval;
				node.leaf = false;
				if (depth + 1 < MAX_DEPTH) {
					pvman.store(depth + 1, node, pvman.load(depth + 1), true);
				}
				
				if (best_eval >= beta) {
					break;
				}
			}
			
		} while ((cur_move = list.next()) != 0);
		
		
		if (!inCheck && backtrackingInfo.static_eval > best_eval) {
			best_move = 0;
			best_eval = backtrackingInfo.static_eval;
			
			node.leaf = true;
			node.eval = backtrackingInfo.static_eval;
			node.bestmove = 0;
			node.nullmove = false;
		}
		
		
		if (best_move != 0) {
			env.getTPTQS().lock();
			env.getTPTQS().put(backtrackingInfo.hash_key, 0, 0, env.getBitboard().getColourToMove(), best_eval, alpha_org, beta, best_move, (byte)0);
			env.getTPTQS().unlock();
		}
		
		return best_eval;
	}
	
	
	private int eval(int depth, int alpha, int beta, boolean pv, int rootColour) {
		if (pv) {
			return fullEval(depth, alpha, beta, rootColour);
		} else {
			return lazyEval(depth, alpha, beta, rootColour);
		}
	}
	
	
	private int getAlphaTrustWindow(ISearchMediator mediator, int rest) {
		return 1 * mediator.getTrustWindow_AlphaAspiration();
	}
	
	
	private void buildAndSendMinorInfo(ISearchMediator mediator, ISearchInfo info, int depth, int searchedCount, int cur_move) {
		
		if (depth == 0) {
			info.setCurrentMove(cur_move);
			info.setCurrentMoveNumber(searchedCount + 1);
		}
		
		if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
			
			long timestamp = System.currentTimeMillis();
			
			if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
			
				mediator.changedMinor(info);
				
				lastSentMinorInfo_timestamp = timestamp;
			}
			
			lastSentMinorInfo_nodesCount = info.getSearchedNodes();
		}
	}
}
