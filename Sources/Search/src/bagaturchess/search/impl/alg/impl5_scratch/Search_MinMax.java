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
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
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


public class Search_MinMax extends SearchImpl {
	
	
	public Search_MinMax(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_MinMax(SearchEnv _env) {
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
		
		return minmax(mediator, rootWin, info, maxdepth, depth);
	}
	
	
	private int minmax(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info, int maxdepth, int depth) {
		
		
		if (depth >= normDepth(maxdepth)) {
			return qsearch(mediator, rootWin, info, maxdepth, depth, 0);
		}
		
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		if (depth >= MAX_DEPTH) {
			return eval(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		long hashkey = env.getBitboard().getHashKey();
		int rest = normDepth(maxdepth) - depth;
		
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
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
		
		if (tpt_found && tpt_depth >= rest) {
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
				
				throw new IllegalStateException();
				
				/*if (tpt_lower >= beta) {
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
				}*/
			}
		}
		
		
		
		//if (depth >= normDepth(maxdepth)) {
		//	return eval(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		//}
		
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(maxdepth), colourToMove, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		
		//Start move iteration
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		//Obtain and prepare the move iterator
		ISearchMoveList list = lists_all[depth];
		list.clear();
		

		int best_move = 0;
		int best_eval = Integer.MIN_VALUE;
		int cur_move;
		while ((cur_move = list.next()) != 0) {
			
			int cur_eval;
			if (MoveInt.isCapture(cur_move)
					&& MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
				cur_eval = getMateVal(depth - 1);
			} else {
				env.getBitboard().makeMoveForward(cur_move);
				
				cur_eval = -minmax(mediator, rootWin, info, maxdepth, depth + 1);
				
				env.getBitboard().makeMoveBackward(cur_move);
			}
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				
				env.getHistory_all().goodMove(best_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
				env.getHistory_all().counterMove(env.getBitboard().getLastMove(), best_move);
				
				node.bestmove = best_move;
				node.eval = best_eval;
				node.leaf = false;
				node.nullmove = false;
				
				if (depth + 1 < MAX_DEPTH) {
					pvman.store(depth + 1, node, pvman.load(depth + 1), true);
				}
			}
		} 
		
		
		env.getTPT().lock();
		env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, Integer.MIN_VALUE, Integer.MAX_VALUE, best_move, (byte)0);
		env.getTPT().unlock();
		
		
		return best_eval;
	}
	
	
	private int qsearch(ISearchMediator mediator, IRootWindow rootWin, ISearchInfo info, int maxdepth, int depth, int materialGain) {
		
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < depth) {
			info.setSelDepth(depth);
		}
		
		
		int staticEval = eval(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		
		if (depth >= MAX_DEPTH) {
			return staticEval;
		}
		
		
		int colourToMove = env.getBitboard().getColourToMove();
		long hashkey = env.getBitboard().getHashKey();
		int rest = normDepth(maxdepth) - depth;
		
		
		PVNode node = pvman.load(depth);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
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
		
		if (tpt_found && tpt_depth >= rest) {
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
				
				throw new IllegalStateException();
				
				/*if (tpt_lower >= beta) {
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
				}*/
			}
		}
		
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(normDepth(maxdepth), colourToMove, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		
		//Starts moves iteration
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		//Obtain and prepare the move iterator
		ISearchMoveList list = lists_capsproms[depth];
		list.clear();
		

		int best_move = 0;
		int best_eval = Integer.MIN_VALUE;
		int cur_move;
		while ((cur_move = list.next()) != 0) {
			
			if (!MoveInt.isCaptureOrPromotion(cur_move)) {
				throw new IllegalStateException();
			}
			
			int cur_eval;
			if (MoveInt.isCapture(cur_move)
					&& MoveInt.getCapturedFigureType(cur_move) == Figures.TYPE_KING) {
				cur_eval = getMateVal(depth - 1);
			} else {
				
				int newMaterialGain = env.getBitboard().getMaterialFactor().getMaterialGain(cur_move);
				
				int delta = newMaterialGain + materialGain;
				if (delta >= 0) {
					env.getBitboard().makeMoveForward(cur_move);
					
					cur_eval = -qsearch(mediator, rootWin, info, maxdepth, depth + 1, -delta);
					
					env.getBitboard().makeMoveBackward(cur_move);
				} else {
					//Not winning capture
					continue;
				}
			}
			
			if (cur_eval > best_eval) {
				
				best_eval = cur_eval;
				best_move = cur_move;
				
				
				env.getHistory_all().goodMove(best_move, rest * rest, best_eval > 0 && isMateVal(best_eval));
				env.getHistory_all().counterMove(env.getBitboard().getLastMove(), best_move);
				
				node.bestmove = best_move;
				node.eval = best_eval;
				node.leaf = false;
				node.nullmove = false;
				
				if (depth + 1 < MAX_DEPTH) {
					pvman.store(depth + 1, node, pvman.load(depth + 1), true);
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
			env.getTPT().put(hashkey, normDepth(maxdepth), depth, colourToMove, best_eval, Integer.MIN_VALUE, Integer.MAX_VALUE, best_move, (byte)0);
			env.getTPT().unlock();
		}
		
		
		return best_eval;
	}

	
	private int eval(int depth, int alpha, int beta) {
		return fullEval(depth, alpha, beta, -1);
		//return lazyEval(depth, alpha, beta);
	}
	
	
	protected ISearchMoveListFactory getMoveListFactory() {
		return new SearchMoveListFactory5();
	}
}
