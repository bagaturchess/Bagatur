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
package bagaturchess.search.impl.alg.impl3;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.movelists.ListAll;
import bagaturchess.search.impl.movelists.ListKingEscapes;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class Search_NegaScout extends SearchImpl {
	
	
	// Margins shamelessly stolen from Laser
	private static final int[] STATIC_NULLMOVE_MARGIN = { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] RAZORING_MARGIN = { 0, 240, 280, 300 };
	private static final int[] FUTILITY_MARGIN = { 0, 80, 170, 270, 380, 500, 630 };
	private static final int[][] LMR_TABLE = new int[64][64];
	static {
		// Ethereal LMR formula with depth and number of performed moves
		for (int depth = 1; depth < 64; depth++) {
			for (int moveNumber = 1; moveNumber < 64; moveNumber++) {
				LMR_TABLE[depth][moveNumber] = (int) (0.5f + Math.log(depth) * Math.log(moveNumber * 1.2f) / 2.5f);
			}
		}
	}
	private static final int FUTILITY_MARGIN_QSEARCH = 200;
	
	
	public Search_NegaScout(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_NegaScout(SearchEnv _env) {
		super(_env);
	}
	
	@Override
	public int pv_search(ISearchMediator mediator, IRootWindow rootWin,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha, int beta, int prevbest, int prevprevbest, int[] prevPV,
			boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		return calculateBestMove(mediator, info, depth, maxdepth / PLY, alpha, beta, 0, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		return calculateBestMove(mediator, info, depth, maxdepth / PLY, beta - 1, beta, 0, false);
	}
	
	
	public int calculateBestMove(ISearchMediator mediator, ISearchInfo info, final int ply, int depth, int alpha, int beta, final int nullMoveCounter, final boolean isPv) {
		
		final int alphaOrig = alpha;
		//final boolean isPv = beta - alpha != 1;
		
		//Stop search
		if (mediator != null && mediator.getStopper() != null)
			mediator.getStopper().stopIfNecessary(info.getDepth(), env.getBitboard().getColourToMove(), alphaOrig, beta);
		
		
		PVNode node = pvman.load(ply);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		//Draw check
		if (isPv) {
			if (isDrawPV(depth)) {
				node.eval = 0;//TODO getDrawScores(rootColour);
				return node.eval;
			}
		} else {
			if (isDraw()) {
				node.eval = 0;//TODO getDrawScores(rootColour);
				return node.eval;
			}
		}
		
		
		//Mate and stalemate check
		boolean inCheck = env.getBitboard().isInCheck();
		/*if (inCheck) {
			if (!env.getBitboard().hasMoveInCheck()) {
				
				node.eval = -getMateVal(ply);
				
				return node.eval;
			}
		} else {
			if (!env.getBitboard().hasMoveInNonCheck()) {
				
				node.eval = 0;//TODO getDrawScores(rootColour);
				
				return node.eval;
			}
		}*/
		
		
		// get extensions
		depth += extensions(env.getBitboard(), ply);
		
		
		/* mate-distance pruning */
		/*TODO
		if (ply > 0) {
			alpha = Math.max(alpha, MIN + ply);
			beta = Math.min(beta, MAX - ply - 1);
			if (alpha >= beta) {
				return alpha;
			}
		}*/
		
		
		/* transposition-table */
		int ttMove = 0;
		//Get TPT entry
		{
			boolean tpt_found = false;
			boolean tpt_exact = false;
			int tpt_depth = 0;
			int tpt_lower = MIN;
			int tpt_upper = MAX;
			
			env.getTPT().lock();
			{
				TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
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
					ttMove = tpt_move;
				}
			}
			env.getTPT().unlock();
			
			
			if (tpt_found && tpt_depth >= depth
					) {
				
				if (tpt_exact) {
					if (!SearchUtils.isMateVal(tpt_lower)) {
						node.bestmove = ttMove;
						node.eval = tpt_lower;
						node.nullmove = false;
						node.leaf = true;
						
						env.getTPT().lock();
						buff_tpt_depthtracking[0] = 0;
						extractFromTPT(info, depth, node, true, buff_tpt_depthtracking, -1, env.getTPT());
						env.getTPT().unlock();
						
						if (buff_tpt_depthtracking[0] >= depth) {
							return node.eval;
						}
					}
				} else {
					if (tpt_lower >= beta) {
						if (!SearchUtils.isMateVal(tpt_lower)) {
							node.bestmove = ttMove;
							node.eval = tpt_lower;
							node.nullmove = false;
							node.leaf = true;
							
							env.getTPT().lock();
							buff_tpt_depthtracking[0] = 0;
							extractFromTPT(info, depth, node, true, buff_tpt_depthtracking, -1, env.getTPT());
							env.getTPT().unlock();
							
							if (buff_tpt_depthtracking[0] >= depth) {
								return node.eval;
							}
						}
					}
					if (tpt_upper <= alphaOrig) {
						if (!SearchUtils.isMateVal(tpt_upper)) {
							node.bestmove = ttMove;
							node.eval = tpt_upper;
							node.nullmove = false;
							node.leaf = true;
							
							env.getTPT().lock();
							buff_tpt_depthtracking[0] = 0;
							extractFromTPT(info, depth, node, true, buff_tpt_depthtracking, -1, env.getTPT());
							env.getTPT().unlock();
							
							if (buff_tpt_depthtracking[0] >= depth) {
								return node.eval;
							}
						}
					}
				}
			}
		}
		
		
		if (depth == 0) {
			return calculateBestMove(info, env.getBitboard(), alpha, beta, ply);
		}
		

		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		int score;
		int eval = MIN;
		if (!isPv && !inCheck) {

			eval = (int) env.getEval().fullEval(ply, alpha, beta, -1);

			/* use tt value as eval */
			if (ttMove != 0 && node.eval != 0) {
				eval = node.eval;
			}
			
			/* static null move pruning */
			if (depth < STATIC_NULLMOVE_MARGIN.length) {
				if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
					return eval;
				}
			}

			/* razoring */
			if (depth < RAZORING_MARGIN.length && Math.abs(alpha) < MAX_MAT_INTERVAL) {
				if (eval + RAZORING_MARGIN[depth] < alpha) {
					score = calculateBestMove(info, env.getBitboard(), alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1, ply);
					if (score + RAZORING_MARGIN[depth] <= alpha) {
						return score;
					}
				}
			}

			/* null-move */
			if (true) {
				boolean hasAtLeastOnePiece = (env.getBitboard().getColourToMove() == Figures.COLOUR_WHITE) ?
						env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
						env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
				if (nullMoveCounter < 2 && eval >= beta && hasAtLeastOnePiece) {
					env.getBitboard().makeNullMoveForward();
					final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
					score = depth - reduction <= 0 ? -calculateBestMove(info, env.getBitboard(), -beta, -beta + 1, ply + 1)
							: -calculateBestMove(mediator, info, ply + 1, depth - reduction, -beta, -beta + 1, nullMoveCounter + 1, false);
					env.getBitboard().makeNullMoveBackward();
					if (score >= beta) {
						return score;
					}
				}
			}
		}
		
		if (ttMove == 0) {
			/* IID */
			if (depth > 5 && isPv) {
				calculateBestMove(mediator, info, ply, depth - 1 - 1, alpha, beta, 0, false);
				
				env.getTPT().lock();
				{
					TPTEntry tptEntry = env.getTPT().get(env.getBitboard().getHashKey());
					if (tptEntry != null) {
						int tpt_move = tptEntry.getBestMove_lower();
						if (tpt_move == 0) {
							tpt_move = tptEntry.getBestMove_upper();
						}
						ttMove = tpt_move;
					}
				}
				env.getTPT().unlock();
				
			}
		}
		
		final boolean wasInCheck = inCheck;

		//final int parentMove = ply == 0 ? 0 : env.getBitboard().getLastMove();
		int bestMove = 0;
		int bestScore = MIN - 1;
		int movesPerformed = 0;

		ISearchMoveList list = null;
		if (inCheck) { 
			list = lists_escapes[ply];
			list.clear();
			((ListKingEscapes)list).setTptMove(ttMove);
		} else {
			list = lists_all[ply];
			list.clear();
			((ListAll)list).setTptMove(ttMove);
		}
		
		int move = 0;
		while ((move = list.next()) != 0) {

			// pruning allowed?
			if (!isPv && !wasInCheck && movesPerformed > 0) {

				if (!MoveInt.isCaptureOrPromotion(move)) {

					/* late move pruning */
					if (depth <= 4 && movesPerformed >= depth * 3 + 3) {
						continue;
					}

					/* futility pruning */
					if (depth < FUTILITY_MARGIN.length) {
						//if (!env.getBitboard().isPasserPush(move)) {
							if (eval == MIN) {
								eval = (int) env.getEval().fullEval(ply, alpha, beta, -1);
							}
							if (eval + FUTILITY_MARGIN[depth] <= alpha) {
								continue;
							}
						//}
					}
				}

				/* SEE Pruning */
				else if (depth <= 6 && env.getBitboard().getSee().evalExchange(move) < -20 * depth * depth) {
					continue;
				}
			}
			
			movesPerformed++;
			
			env.getBitboard().makeMoveForward(move);
			
			score = alpha + 1; // initial is above alpha
			
			int reduction = 1;
			if (depth > 2 && movesPerformed > 1 && !MoveInt.isCaptureOrPromotion(move)) {

				reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed, 63)];
				reduction += 2;
				/*if (env.getHistory_All().getScores(move) > 0.4) {
					reduction -= 1;
				}*/
				/*if (move == killer1Move || move == counterMove) {
					reduction -= 1;
				}*/
				if (!isPv) {
					reduction += 1;
				}
				reduction = Math.min(depth - 1, Math.max(reduction, 1));
			}

			/* LMR */
			if (reduction != 1) {
				score = -calculateBestMove(mediator, info, ply + 1, depth - reduction, -alpha - 1, -alpha, 0, false);
			}

			/* PVS */
			if (score > alpha && movesPerformed > 1) {
				score = -calculateBestMove(mediator, info, ply + 1, depth - 1, -alpha - 1, -alpha, 0, false);
			}

			/* normal bounds */
			if (score > alpha) {
				score = -calculateBestMove(mediator, info, ply + 1, depth - 1, -beta, -alpha, 0, isPv);
			}
			
			env.getBitboard().makeMoveBackward(move);
			
			//Add history records for the current move
			list.countTotal(move);
			if (score <= alphaOrig) {
				getHistory(inCheck).countFailure(move, depth);
			} else {
				list.countSuccess(move);//Should be before addCounterMove call
				getHistory(inCheck).countSuccess(move, depth);
				getHistory(inCheck).addCounterMove(env.getBitboard().getLastMove(), move);
			}
			
			if (score > bestScore) {
				
				bestScore = score;
				bestMove = move;

				node.bestmove = bestMove;
				node.eval = bestScore;
				node.leaf = false;
				node.nullmove = false;
				
				if (ply + 1 < MAX_DEPTH) {
					pvman.store(ply + 1, node, pvman.load(ply + 1), true);
				}

				alpha = Math.max(alpha, score);
				if (alpha >= beta) {
					break;
				}
			}
		}
		
		
		if (bestMove == 0) {
			if (inCheck) {
				if (movesPerformed == 0) {
					node.bestmove = 0;
					node.eval = -getMateVal(depth);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					throw new IllegalStateException();
				}
			} else {
				if (movesPerformed == 0) {
					node.bestmove = 0;
					node.eval = 0;//TODO getDrawScores(rootColour);
					node.leaf = true;
					node.nullmove = false;
					return node.eval;
				} else {
					throw new IllegalStateException();
				}
			}
		}
		
		if (bestMove == 0 || bestScore == MIN || bestScore == MAX) {
			throw new IllegalStateException();
		}
		
		env.getTPT().lock();
		env.getTPT().put(env.getBitboard().getHashKey(), depth, 0, env.getBitboard().getColourToMove(), bestScore, alphaOrig, beta, bestMove, (byte)0);
		env.getTPT().unlock();

		return bestScore;
	}


	public int calculateBestMove(ISearchInfo info, final IBitBoard cb, int alpha, final int beta, final int ply) {
		
		
		//Update info
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		PVNode node = pvman.load(ply);
		
		node.bestmove = 0;
		node.eval = MIN;
		node.nullmove = false;
		node.leaf = true;
		
		
		/* stand-pat check */
		int eval = MIN;
		if (!cb.isInCheck()) {
			eval = (int) env.getEval().fullEval(0, alpha, beta, -1);
			if (eval >= beta) {
				node.eval = eval;
				return eval;
			}
			alpha = Math.max(alpha, eval);
		}
		
		IMoveList list = null;
		if (cb.isInCheck()) { 
			list = lists_escapes[ply];
			list.clear();
			//TODO ((ListKingEscapes)list).setTptMove(backtrackingInfo.hash_move);
		} else {
			list = lists_capsproms[ply];
			list.clear();
			//TODO ((ListCapsProm)list).setTptMove(backtrackingInfo.hash_move);
		}
		
		int movesPerformed = 0;
		int move = 0;
		while ((move = list.next()) != 0) {

			// skip under promotions
			if (MoveInt.isPromotion(move)) {
				if (MoveInt.getPromotionFigureType(move) != Constants.TYPE_QUEEN) {
					continue;
				}
			} else if (MoveInt.isCapture(move) && eval + FUTILITY_MARGIN_QSEARCH + env.getBitboard().getBaseEvaluation().getMaterial(MoveInt.getCapturedFigureType(move)) < alpha) {
				// futility pruning
				continue;
			}

			if (!cb.isPossible(move)) {
				throw new IllegalStateException();
			}

			// skip bad-captures
			int moveSee = env.getBitboard().getSee().evalExchange(move);
			if (moveSee <= 0) {
				continue;
			}
			
			movesPerformed++;

			cb.makeMoveForward(move);

			final int score = -calculateBestMove(info, cb, -beta, -alpha, ply + 1);

			cb.makeMoveBackward(move);
			
			if (score >= beta) {
				
				node.bestmove = move;
				node.eval = score;
				node.leaf = false;
				node.nullmove = false;
				
				if (ply + 1 < MAX_DEPTH) {
					pvman.store(ply + 1, node, pvman.load(ply + 1), true);
				}
				
				return score;
			}
			alpha = Math.max(alpha, score);
		}
		
		/* checkmate or stalemate */
		if (env.getBitboard().isInCheck() && movesPerformed == 0) {
			node.eval = getMateVal(ply);
			return node.eval;
		}
		
		if (alpha == MIN || alpha == MAX) {
			throw new IllegalStateException();
		}
		
		node.eval = eval;
		return node.eval;
	}
	
	
	private static int extensions(final IBitBoard cb, final int ply) {
		/* check-extension */
		if (cb.isInCheck()) {
			return 1;
		}
		return 0;
	}
}
