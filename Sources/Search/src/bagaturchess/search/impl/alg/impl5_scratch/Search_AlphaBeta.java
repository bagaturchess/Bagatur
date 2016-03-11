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
package bagaturchess.search.impl.alg.impl5_scratch;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl1.movegen.MoveInt;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class Search_AlphaBeta extends SearchImpl {
	
	
	private IMoveList legalMovesChecker = new BaseMoveList(256);
	
	
	public Search_AlphaBeta(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_AlphaBeta(SearchEnv _env) {
		super(_env);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha, int beta, int prevbest, int prevprevbest, int[] prevPV,
			boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return minmax(mediator, rootWin, info, env.getBitboard().isInCheck(), maxdepth, maxdepth, depth, beta, 0, 0, 0, true, true, false, 0);
	}
	
	
	private int eval(int depth, int alpha, int beta, boolean pv) {
		if (pv) {
			return fullEval(depth, alpha, beta, -1);
		} else {
			return lazyEval(depth, alpha, beta, -1);
		}
	}
	
	
	protected ISearchMoveListFactory getMoveListFactory() {
		return new SearchMoveListFactory5();
	}
	
	
	private int getLMR1(ISearchMoveList list) {
		return (int) Math.max(1, Math.sqrt(list.size()) / (double)2);
	}
	
	
	private int getLMR2(ISearchMoveList list) {
		return (int) Math.max(1, Math.sqrt(list.size()));
	}
	
	
	private int minmax(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info, boolean inCheck, int initial_maxdepth, int maxdepth, int depth, int beta,
			int prevbest, int prevprevbest, int mateMove,
			boolean prevNullMove, boolean pv, boolean inQSearch, int materialGain) {
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, beta - 1, beta);
		
		
		if (depth >= normDepth(maxdepth)) {
			return qsearch(mediator, rootWin, info, inCheck, initial_maxdepth, maxdepth, depth, beta, inQSearch ? materialGain : 0, pv);
		}
		
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		if (depth >= MAX_DEPTH) {
			return eval(depth, beta - 1, beta, pv);
		}
		
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores();
			return node.eval;
		}
		
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		int rest = normDepth(maxdepth) - depth;
		
		
		if (inCheck) {
			
			if (!inQSearch) {
				maxdepth += PLY;
				//maxdepth += PLY - 1;
				//maxdepth += (3 * PLY) / 4;
				//maxdepth += PLY / 2;
			} else {
				//maxdepth += PLY - 1;
			}
			
			if (rest <= 1) {
				if (depth >= normDepth(maxdepth)) {
					maxdepth = PLY * (depth + 1);
				}
			}
		}
		
		rest = normDepth(maxdepth) - depth;
		
		
		if (rest < 1) {
			throw new IllegalStateException("1");
		}
		
		
		if (!inCheck && depth >= 1) {
			
		      // lower bound
		      int value = -getMateVal(depth+2); // does not work if the current position is mate
		      if (value > beta - 1) {
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
		         if (value <= beta - 1) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }
		}
		
		
		//TPT check
		boolean tpt_found = false;
		boolean tpt_exact = false;
		int tpt_depth = 0;
		int tpt_lower = MIN;
		int tpt_upper = MAX;
		int tpt_move = 0;
		
		{
			env.getTPT().lock();
			TPTEntry tptEntry = env.getTPT().get(hashkey);
			if (tptEntry != null) {
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_depth = tptEntry.getDepth();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				tpt_move = tptEntry.getBestMove_lower();
			}
			env.getTPT().unlock();
		}
		
		
		if (!pv && tpt_found && tpt_depth >= rest
				) {
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
		
		
		int staticEval = eval(depth, beta - 1, beta, pv);
		
		boolean hasAtLeastOnePiece = (colourToMove == Figures.COLOUR_WHITE) ?
			env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
			env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
		
		int new_mateMove = 0;
		if (!inCheck
				&& !prevNullMove
				&& staticEval > beta
				&& hasAtLeastOnePiece
				) {
			
			int null_reduction = PLY * (rest >= 6 ? 4 : 3);
			null_reduction = Math.max(null_reduction, PLY * (rest / 2));
			
			int null_maxdepth = maxdepth - null_reduction;
			
			
			env.getBitboard().makeNullMoveForward();
			
			int null_val = -minmax(mediator, rootWin, info, env.getBitboard().isInCheck(), initial_maxdepth,
					null_maxdepth, depth, - (beta - 1), 0, prevbest, 0, true, false, false, -materialGain);
			
			if (!pv && null_val >= beta) {
				
				env.getBitboard().makeNullMoveBackward();
				
				node.eval = null_val;
				return node.eval;
			}
			
			if (null_val < 0 
					& isMateVal(null_val)
					) {
				
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
			
			env.getBitboard().makeNullMoveBackward();
			
			node.bestmove = 0;
			node.eval = MIN;
			node.nullmove = false;
			node.leaf = true;
		}
		
		
		//IID
		if (tpt_move == 0
				&& rest > 1 //Stack overflow otherwise
			) {

			int reduction = Math.max(2, rest / 2);

			minmax(mediator, rootWin, info, inCheck, initial_maxdepth,
					maxdepth - PLY * reduction, depth, beta, prevbest, prevprevbest, mateMove, prevNullMove, false, inQSearch, materialGain);
			
			env.getTPT().lock();
			
			TPTEntry tptEntry = env.getTPT().get(hashkey);
			if (tptEntry != null) {//TODO: Check why is null sometimes
				tpt_found = true;
				tpt_exact = tptEntry.isExact();
				tpt_depth = tptEntry.getDepth();
				tpt_lower = tptEntry.getLowerBound();
				tpt_upper = tptEntry.getUpperBound();
				tpt_move = tptEntry.getBestMove_lower();
			}
			
			env.getTPT().unlock();
		}
				
		
		//Start move iteration
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		//if (hashkey == 3633235098870738992L) {
		//	int g = 0;
		//}
		
		//Obtain and prepare the move iterator
		ISearchMoveList list = lists_all[depth];
		list.clear();
		list.setTptMove(tpt_move);
		list.setPrevBestMove(prevprevbest);
		((ListAll)list).setMateMove(mateMove);
		
		boolean inCheck_opponent = false;//env.getBitboard().isInCheck(Constants.COLOUR_OP[colourToMove]);
		
		int firstKingCapture = 1000;
		int moveCount = -1;
		int best_move = 0;
		int best_eval = MIN;
		int cur_move;
		while ((cur_move = list.next()) != 0) {
			
			moveCount++;
			
			boolean isCapture = MoveInt.isCapture(cur_move);
			boolean isPromotion = MoveInt.isPromotion(cur_move);
			boolean isPasserPush = env.getBitboard().isPasserPush(cur_move);
			
			
			int cur_eval;
			if (isCapture
					&& MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
				cur_eval = getMateVal_0(depth - 1);
				
				if (moveCount < firstKingCapture) {
					firstKingCapture = moveCount;
				}
				if (firstKingCapture != 0) {
					throw new IllegalStateException("2");
				}
				
				inCheck_opponent = true;
				
			} else {
				
				
				if (inCheck_opponent) {
					if (true) throw new IllegalStateException("3");
				}
				
				
				env.getBitboard().makeMoveForward(cur_move);
				
				boolean checkMove = env.getBitboard().isInCheck();
				
				
				boolean reductionsDisabled = inCheck
										|| checkMove
										|| isCapture
										|| isPromotion
										|| isPasserPush
										|| cur_move == tpt_move
										|| cur_move == prevprevbest
										|| cur_move == mateMove;
				
				//Static prunning
				if (!pv
						&& !reductionsDisabled
						&& !isMateVal(beta - 1)
						&& !isMateVal(beta)
					) {
					
					if (rest < STATIC_REDUCTION_MARGIN_NONPV.length) {
						
						int staticPrunningEval = staticEval /*TODO: Consider + env.getBitboard().getMaterialFactor().getMaterialGain(cur_move)*/;
						
						if (beta - 1 >= STATIC_REDUCTION_MARGIN_PV[rest] + staticPrunningEval) {
							
							info.setSearchedNodes(info.getSearchedNodes() + 1);
							moveCount++;
							cur_eval = staticPrunningEval;
							
							env.getBitboard().makeMoveBackward(cur_move);
							
							if (cur_eval > best_eval) {
								
								best_eval = cur_eval;
								best_move = cur_move;
								
								if (isNonAlphaNode(cur_eval, best_eval, beta - 1, beta)) {
									node.bestmove = best_move;
									node.eval = best_eval;
									node.leaf = false;
									node.nullmove = false;
									
									if (depth + 1 < MAX_DEPTH) {
										pvman.store(depth + 1, node, pvman.load(depth + 1), true);
									}
								}
								
								if (cur_eval >= beta) {
									env.getHistory_all().goodMove(best_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
									env.getHistory_all().counterMove(env.getBitboard().getLastMove(), best_move);
									break;
								}
							}
							
							continue;
						}
					}
				}
				
				
				if (inCheck_opponent && checkMove) {
					throw new IllegalStateException("4");
				}
				
				
				//Extensions
				int ext_attack = 0;
				int ext_defence = 0;
				if (!inCheck) {
					if (isCapture) {
						ext_attack += 0;//PLY / 2;
						//ext_defence += PLY / 2;
					}
					if (isPromotion) {
						ext_attack += 0;//PLY / 2;
						//ext_defence += PLY / 2;
					}
					if (isPasserPush) {
						//ext_attack += PLY / 2;
						ext_defence += 0;//PLY / 2;
					}
					if (checkMove) {
						//ext_attack += PLY / 2;
						ext_defence += 0;//PLY / 2;
					}
				}
				
				
				//Start search
				int lmrReduction = 0;
				if (ext_attack == 0 && ext_defence == 0) {
					if (pv) {
						if (moveCount >= getLMR1(list)) {
							lmrReduction += PLY;
							if (rest > 2 && moveCount >= getLMR2(list)) {
								lmrReduction += PLY;
							}
						}
					} else {
						if (moveCount >= getLMR1(list)) {
							lmrReduction += PLY;
							if (rest > 2 && moveCount >= getLMR2(list)) {
								lmrReduction += PLY;
							}
						}
					}
				}

				
				int newMaterialGain = materialGain + ((isCapture || isPromotion) ? env.getBitboard().getMaterialFactor().getMaterialGain(cur_move) : 0);
				
				
				if (reductionsDisabled) {
					cur_eval = -minmax(mediator, rootWin, info, checkMove,
							 initial_maxdepth, maxdepth + ext_attack + ext_defence, depth + 1, -(beta - 1), best_move, prevbest, new_mateMove,
							false, true, inQSearch, -newMaterialGain);
				} else {
					
					if (!reductionsDisabled
							&& lmrReduction > 0
							) {
						
						cur_eval = -minmax(mediator, rootWin, info, checkMove,
								 initial_maxdepth, maxdepth - lmrReduction, depth + 1, -(beta - 1), best_move, prevbest, new_mateMove,
								false, false, inQSearch, -newMaterialGain);
						
						if (cur_eval > best_eval) {
							
							cur_eval = -minmax(mediator, rootWin, info, checkMove,
									 initial_maxdepth, maxdepth, depth + 1, -(beta - 1), best_move, prevbest, new_mateMove,
									false, false, inQSearch, -newMaterialGain);
						}
					} else {
						cur_eval = -minmax(mediator, rootWin, info, checkMove,
								 initial_maxdepth, maxdepth, depth + 1, -(beta - 1), best_move, prevbest, new_mateMove,
								false, false, inQSearch, -newMaterialGain);
					}
					
					if (pv && isPVNode(cur_eval, best_eval, beta - 1, beta)) {
						cur_eval = -minmax(mediator, rootWin, info, checkMove,
								initial_maxdepth, maxdepth + ext_attack + ext_defence, depth + 1, -(beta - 1), best_move, prevbest, new_mateMove,
								false, true, inQSearch, -newMaterialGain);
					}
				}
				
				env.getBitboard().makeMoveBackward(cur_move);
			}
			
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				if (isNonAlphaNode(cur_eval, best_eval, beta - 1, beta)) {
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					node.nullmove = false;
					
					if (depth + 1 < MAX_DEPTH) {
						pvman.store(depth + 1, node, pvman.load(depth + 1), true);
					}
				}

					
				if (cur_eval >= beta) {
					env.getHistory_all().goodMove(best_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
					env.getHistory_all().counterMove(env.getBitboard().getLastMove(), best_move);
					break;
				}
			}
		} 
		
		
		if (best_eval < 0 && isMateVal(best_eval)) {
			//Mate or stale-mate check
			
			//best_eval == -getMateVal_0(depth)
			
			if (!inCheck) {
				legalMovesChecker.clear();
				env.getBitboard().genAllMoves(legalMovesChecker);
				
				boolean hasLegalMove = false;
				int move = 0;
				while ((move = legalMovesChecker.next()) != 0L) {
					env.getBitboard().makeMoveForward(move);
					if (!env.getBitboard().isInCheck(colourToMove)) {
						hasLegalMove = true;
					}
					env.getBitboard().makeMoveBackward(move);
					if (hasLegalMove) {
						break;
					}
				}
				
				if (!hasLegalMove) {
					node.bestmove = 0;
					node.eval = getDrawScores();
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				}
			}
		}
		
		if (best_move == 0) {
			//TODO: Consider
			//throw new IllegalStateException("5");
			System.out.println("DEBUG5: beta="  +beta +", BOARD: \r\n" + env.getBitboard());
		}
		
		
		if (best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, beta - 1, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		
		return best_eval;
	}
	
	
	private int qsearch(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info, boolean inCheck,
			int initial_maxdepth, int maxdepth, int depth, int beta, int materialGain, boolean pv) {
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(initial_maxdepth), colourToMove, beta - 1, beta);
		
		
		if (inCheck) {
			return minmax(mediator, rootWin, info, inCheck, initial_maxdepth, PLY * (depth + 1), depth, beta,
								0, 0, 0, true, pv, true, materialGain);
		}
		
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		if (inCheck) {
			throw new IllegalStateException("6");
		}
		
		
		int staticEval = eval(depth, beta - 1, beta, pv);
		
		
		if (depth >= MAX_DEPTH) {
			return staticEval;
		}
		
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		if (isDrawPV(depth)) {
			node.eval = getDrawScores();
			return node.eval;
		}
		
		
		if (staticEval >= beta) {
			node.eval = staticEval;
			return node.eval;
		}
		
		
		if (depth >= 1) {
			
		      
		      // lower bound
		      int value = -getMateVal(depth+2); // does not work if the current position is mate
		      if (value > beta - 1) {
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
		         if (value <= beta - 1) {
						node.bestmove = 0;
						node.eval = value;
						node.leaf = true;
						node.nullmove = false;
						return node.eval;
		         }
		      }
		}
		
		
		long hashkey = env.getBitboard().getHashKey();
		int rest = normDepth(maxdepth) - depth;
		
		
		//TPT check
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
		
		if (!pv && tpt_found && tpt_depth >= rest) {
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
		
		
		//Starts moves iteration
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		//Obtain and prepare the move iterator
		ISearchMoveList list = null;
		list = lists_capsproms[depth];
		list.clear();
		list.setTptMove(MoveInt.isCaptureOrPromotion(tpt_move) ? tpt_move : 0);
		//list.setTptMove(tpt_move);
		
		boolean inCheck_opponent = false;//env.getBitboard().isInCheck(Constants.COLOUR_OP[colourToMove]);
		
		int firstKingCapture = 1000;
		int moveCount = -1;
		int best_move = 0;
		int best_eval = MIN;
		int cur_move;
		while ((cur_move = list.next()) != 0) {
			
			moveCount++;
			
			int cur_eval;
			if (MoveInt.isCapture(cur_move)
					&& MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
				cur_eval = getMateVal_0(depth - 1);
				
				if (moveCount < firstKingCapture) {
					firstKingCapture = moveCount;
				}
				if (firstKingCapture != 0) {
					throw new IllegalStateException("7");
				}
				
				inCheck_opponent = true;
			} else {
				
				int newMaterialGain = env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				
				int delta = newMaterialGain + materialGain;
				if (delta >= 0) {
					
					int optimisticScore = 100 + staticEval + newMaterialGain;
					
	                if (optimisticScore <= beta - 1) { // Delta pruning
	                	continue;
	                }
					
					if (inCheck_opponent) {
						if (!MoveInt.isCapture(cur_move)) {
							continue;
						}
						if (MoveInt.getCapturedFigureType(cur_move) != Figures.TYPE_KING) {
							continue;
						}
					}
	                
	                
	                env.getBitboard().makeMoveForward(cur_move);
	                
	                boolean checkMove = env.getBitboard().isInCheck();
	                
					if (inCheck_opponent && checkMove) {
						throw new IllegalStateException("8");
					}
	                
					cur_eval = -qsearch(mediator, rootWin, info, checkMove,
							 initial_maxdepth, maxdepth, depth + 1, -(beta - 1), -delta, pv);
					
					env.getBitboard().makeMoveBackward(cur_move);
					
				} else {
					//Not winning capture
					continue;
				}
			}
			
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				if (isNonAlphaNode(cur_eval, best_eval, beta - 1, beta)) {
					node.bestmove = best_move;
					node.eval = best_eval;
					node.leaf = false;
					node.nullmove = false;
					
					if (depth + 1 < MAX_DEPTH) {
						pvman.store(depth + 1, node, pvman.load(depth + 1), true);
					}
				}
				
				if (cur_eval >= beta) {
					
					env.getHistory_all().goodMove(best_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
					env.getHistory_all().counterMove(env.getBitboard().getLastMove(), best_move);
					
					break;
				}
			}
		} 
		
		
		if (staticEval > best_eval) {
			best_move = 0;
			best_eval = staticEval;
			
			node.leaf = true;
			node.eval = staticEval;
			node.bestmove = 0;
			node.nullmove = false;
		}
		
		
		if (best_move != 0) {
			env.getTPT().lock();
			env.getTPT().put(hashkey, 0, 0, colourToMove, best_eval, beta - 1, beta, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		
		return best_eval;
	}
}
