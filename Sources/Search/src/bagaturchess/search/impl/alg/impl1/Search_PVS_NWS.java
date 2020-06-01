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


import java.util.EmptyStackException;
import java.util.Stack;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MaterialUtil;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;

import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private static final int PHASE_TT = 0;
	private static final int PHASE_ATTACKING_GOOD = 1;
	private static final int PHASE_KILLER_1 = 2;
	private static final int PHASE_KILLER_2 = 3;
	private static final int PHASE_COUNTER = 4;
	private static final int PHASE_QUIET = 5;
	private static final int PHASE_ATTACKING_BAD = 6;
	
	private static final int[] STATIC_NULLMOVE_MARGIN = { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] RAZORING_MARGIN = { 0, 240, 280, 300 };
	private static final int[] FUTILITY_MARGIN = { 0, 80, 170, 270, 380, 500, 630 };
	private static final int[][] LMR_TABLE = new int[64][64];
	static {
		for (int depth = 1; depth < 64; depth++) {
			for (int moveNumber = 1; moveNumber < 64; moveNumber++) {
				//LMR_TABLE[depth][moveNumber] = (int) (0.5f + Math.log(depth) * Math.log(moveNumber * 1.2f) / 2.5f);
				LMR_TABLE[depth][moveNumber] = 1 + (int) Math.ceil(Math.max(1, Math.log(moveNumber) * Math.log(depth) / (double) 2));
			}
		}
	}
	
	private static final int FUTILITY_MARGIN_Q_SEARCH = 200;
	
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	private VarStatistic avgHistory_attacks_good;
	private VarStatistic avgHistory_attacks_bad;
	private VarStatistic avgHistory_quiet;
	
	
	public Search_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		super(_env);
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		return (int) env.getTPT().getUsage();
	}
	
	
	public void newSearch() {
		
		super.newSearch();
		
		((BoardImpl) env.getBitboard()).getMoveGenerator().clearHistoryHeuristics();
		
		lastSentMinorInfo_nodesCount = 0;
		lastSentMinorInfo_timestamp = 0;
		
		avgHistory_quiet = new VarStatistic(false);
		avgHistory_attacks_good = new VarStatistic(false);
		avgHistory_attacks_bad = new VarStatistic(false);
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, PVManager pvman,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, normDepth(maxdepth), alpha_org, beta, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, normDepth(maxdepth), beta - 1, beta, false);		
	}
	
	
	public int calculateBestMove(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
			final int ply, int depth, int alpha, int beta, boolean isPv) {

		
		if (mediator != null && mediator.getStopper() != null) {
			mediator.getStopper().stopIfNecessary(ply + depth, env.getBitboard().getColourToMove(), alpha, beta);
		}
		
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			return eval(evaluator, ply, alpha, beta, isPv);
		}
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		
	    // Check if we have an upcoming move which draws by repetition, or
	    // if the opponent had an alternative move earlier to this position.
	    if (/*alpha < EvalConstants.SCORE_DRAW
	        &&*/ ply > 0
	        && isDraw()
	        ) {
			node.eval = EvalConstants.SCORE_DRAW;
			return node.eval;
	    }
		
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
			Assert.isTrue(alpha >= ISearch.MIN && alpha <= ISearch.MAX);
			Assert.isTrue(beta >= ISearch.MIN && beta <= ISearch.MAX);
		}
		
		final int alphaOrig = alpha;
		
		depth += extensions(cb, moveGen, ply);
		
		if (EngineConstants.ENABLE_MATE_DISTANCE_PRUNING) {
			if (ply > 0) {
				alpha = Math.max(alpha, -SearchUtils.getMateVal(ply));
				beta = Math.min(beta, +SearchUtils.getMateVal(ply + 1));
				if (alpha >= beta) {
					return alpha;
				}
			}
		}
		
		
		env.getTPT().get(cb.zobristKey, tt_cached);
		if (!tt_cached.isEmpty() && cb.isValidMove(tt_cached.getBestMove())) {
			
			int tpt_depth = tt_cached.getDepth();
			
			if (tpt_depth >= depth) {
				if (tt_cached.getFlag() == ITTEntry.FLAG_EXACT) {
					extractFromTT(ply, node, tt_cached, info, isPv);
					return node.eval;
				} else {
					if (tt_cached.getFlag() == ITTEntry.FLAG_LOWER && tt_cached.getEval() >= beta) {
						extractFromTT(ply, node, tt_cached, info, isPv);
						return node.eval;
					}
					if (tt_cached.getFlag() == ITTEntry.FLAG_UPPER && tt_cached.getEval() <= alpha) {
						extractFromTT(ply, node, tt_cached, info, isPv);
						return node.eval;
					}
				}
			}
		}
		
		
		if (ply > 1
    	    	&& depth >= 7
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			if (cb.checkingPieces != 0) {
				if (!env.getBitboard().hasMoveInCheck()) {
					node.bestmove = 0;
					node.eval = -getMateVal(ply);
					node.leaf = true;
					return node.eval;
				}
			} else {
				if (!env.getBitboard().hasMoveInNonCheck()) {
					node.bestmove = 0;
					node.eval = EvalConstants.SCORE_DRAW;
					node.leaf = true;
					return node.eval;
				}
			}
			
			int result = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
			if (result != -1) {
				int dtz = (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				int egtbscore =  SyzygyTBProbing.getSingleton().getWDLScore(wdl, ply);
				if (egtbscore > 0) {
					int distanceToDraw = 100 - env.getBitboard().getDraw50movesRule();
					if (distanceToDraw > dtz) {
						node.bestmove = 0;
						node.eval = 9 * (distanceToDraw - dtz);
						node.leaf = true;
						return node.eval;
					} else {
						node.bestmove = 0;
						node.eval = EvalConstants.SCORE_DRAW;
						node.leaf = true;
						return node.eval;
					}
				} else if (egtbscore == 0) {
					node.bestmove = 0;
					node.eval = EvalConstants.SCORE_DRAW;
					node.leaf = true;
					return node.eval;
				}
			}
        }
		
		
		if (depth == 0) {
			int qeval = qsearch(evaluator, info, cb, moveGen, alpha, beta, ply, isPv);
			node.bestmove = 0;
			node.eval = qeval;
			node.leaf = true;
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		
		int eval = ISearch.MIN;
		if (!isPv && cb.checkingPieces == 0) {

			
			eval = eval(evaluator, ply, alphaOrig, beta, isPv);
			
			
			if (EngineConstants.USE_TT_SCORE_AS_EVAL && !tt_cached.isEmpty()) {
				if (tt_cached.getFlag() == ITTEntry.FLAG_EXACT
						|| (tt_cached.getFlag() == ITTEntry.FLAG_UPPER && tt_cached.getEval() < eval)
						|| (tt_cached.getFlag() == ITTEntry.FLAG_LOWER && tt_cached.getEval() > eval)
					) {
					eval = tt_cached.getEval();
				}
			}
			
			
			if (EngineConstants.ENABLE_STATIC_NULL_MOVE && depth < STATIC_NULLMOVE_MARGIN.length) {
				if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
					node.bestmove = 0;
					node.eval = eval;
					node.leaf = true;
					return node.eval;
				}
			}
			
			
			//Razoring for all depths based on the eval deviation detected into the root node
			/*int rbeta = alpha - mediator.getTrustWindow_AlphaAspiration();
			if (eval < rbeta) {
				score = calculateBestMove(evaluator, info, cb, moveGen, rbeta, rbeta + 1, ply);
				if (score <= rbeta) {
					node.bestmove = 0;
					node.eval = score;
					node.leaf = true;
					return node.eval;
				}
			}*/
			
			
			if (EngineConstants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length && !isMateVal(alpha)) {
				if (eval + RAZORING_MARGIN[depth] < alpha) {
					int score = qsearch(evaluator, info, cb, moveGen, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1, ply, isPv);
					if (score + RAZORING_MARGIN[depth] <= alpha) {
						node.bestmove = 0;
						node.eval = score;
						node.leaf = true;
						return node.eval;
					}
				}
			}

			
			if (EngineConstants.ENABLE_NULL_MOVE && MaterialUtil.hasNonPawnPieces(cb.materialKey, cb.colorToMove)) {
				if (eval >= beta) {
					cb.doNullMove();
					final int reduction = Math.max(depth/ 2, depth / 4 + 3 + Math.min((eval - beta) / 80, 3));
					int score = depth - reduction <= 0 ? -qsearch(evaluator, info, cb, moveGen, -beta, -beta + 1, ply, false)
							: -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -beta, -beta + 1, false);
					cb.undoNullMove();
					if (score >= beta) {
						node.bestmove = 0;
						node.eval = score;
						node.leaf = true;
						return node.eval;
					}
				}
			}
		}

		
		final boolean wasInCheck = cb.checkingPieces != 0;

		final int parentMove = ply == 0 ? 0 : moveGen.previous();
		int bestMove = 0;
		int bestScore = ISearch.MIN;
		int ttMove = 0;
		int counterMove = 0;
		int killer1Move = 0;
		int killer2Move = 0;
		int movesPerformed = 0;

		moveGen.startPly();
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING_BAD) {
			
			switch (phase) {
			
			case PHASE_TT:
				env.getTPT().get(cb.zobristKey, tt_cached);
				if (tt_cached.isEmpty()) {
					if (EngineConstants.ENABLE_IID && depth >= 3) {
						if (MaterialUtil.containsMajorPieces(cb.materialKey)) {
							calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply, depth - Math.max(2, depth / 2), alpha, beta, isPv);
							env.getTPT().get(cb.zobristKey, tt_cached);
						}
					}
				}
				if (!tt_cached.isEmpty()) {
					if (cb.isValidMove(tt_cached.getBestMove())) {
						ttMove = tt_cached.getBestMove();
						moveGen.addMove(ttMove);
					}
				}
				break;
			case PHASE_ATTACKING_GOOD:
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
				break;
			case PHASE_KILLER_1:
				killer1Move = moveGen.getKiller1(ply);
				if (killer1Move != 0 && killer1Move != ttMove && cb.isValidMove(killer1Move) && cb.isLegal(killer1Move)) {
					moveGen.addMove(killer1Move);
					break;
				} else {
					phase++;
				}
			case PHASE_KILLER_2:
				killer2Move = moveGen.getKiller2(ply);
				if (killer2Move != 0 && killer2Move != ttMove && cb.isValidMove(killer2Move) && cb.isLegal(killer2Move)) {
					moveGen.addMove(killer2Move);
					break;
				} else {
					phase++;
				}
			case PHASE_COUNTER:
				counterMove = moveGen.getCounter(cb.colorToMove, parentMove);
				if (counterMove != 0 && counterMove != ttMove && counterMove != killer1Move && counterMove != killer2Move && cb.isValidMove(counterMove)
						&& cb.isLegal(counterMove)) {
					moveGen.addMove(counterMove);
					break;
				} else {
					phase++;
				}
			case PHASE_QUIET:
				moveGen.generateMoves(cb);
				moveGen.setHHScores(cb.colorToMove, parentMove);
				moveGen.sort();
				break;
			case PHASE_ATTACKING_BAD:
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
			}
		
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				
				//Build and sent minor info
				if (ply == 0) {
					info.setCurrentMove(move);
					info.setCurrentMoveNumber((movesPerformed + 1));
				}
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				
				if (phase == PHASE_ATTACKING_GOOD) {
					if (SEEUtil.getSeeCaptureScore(cb, move) < 0) {
						continue;
					}
				}
				
				if (phase == PHASE_ATTACKING_BAD) {
					if (SEEUtil.getSeeCaptureScore(cb, move) >= 0) {
						continue;
					}
				}
				
				if (phase == PHASE_QUIET) {
					if (move == ttMove || move == killer1Move || move == killer2Move || move == counterMove || !cb.isLegal(move)) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove || !cb.isLegal(move)) {
						continue;
					}
				}
				
				
				VarStatistic historyStat = null;
				switch (phase) {
					case PHASE_TT:
						break;
					case PHASE_ATTACKING_GOOD:
						historyStat = avgHistory_attacks_good;
						break;
					case PHASE_KILLER_1:
						break;
					case PHASE_KILLER_2:
						break;
					case PHASE_COUNTER:
						break;
					case PHASE_QUIET:
						historyStat = avgHistory_quiet;
						break;
					case PHASE_ATTACKING_BAD:
						historyStat = avgHistory_attacks_bad;
						break;
				}
				
				
				if (historyStat != null) historyStat.addValue(moveGen.getScore(), moveGen.getScore());
				
				
				if (!isPv && !wasInCheck && movesPerformed > 0 && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move))) {
					
					if (phase == PHASE_QUIET) {
						
						/*if (moveGen.getScore() < historyStat.getEntropy() / depth) {
							continue;
						}*/
						
						if (EngineConstants.ENABLE_LMP && depth <= 4 && movesPerformed >= depth * 3 + 3) {
							continue;
						}
						
						if (EngineConstants.ENABLE_FUTILITY_PRUNING && depth < FUTILITY_MARGIN.length) {
							if (!MoveUtil.isPawnPush78(move)) {
								if (eval == ISearch.MIN) {
									eval = eval(evaluator, ply, alphaOrig, beta, isPv);
								}
								if (eval + FUTILITY_MARGIN[depth] <= alpha) {
									continue;
								}
							}
						}
					} else if (phase == PHASE_ATTACKING_BAD && depth <= 6 && SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth) {
						continue;
					}
				}
				
				cb.doMove(move);
				movesPerformed++;
				
				int score = alpha + 1;
				
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				int reduction = 1;
				if (depth >= 2 && movesPerformed > 1 && MoveUtil.isQuiet(move)) {
					
					reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed, 63)];
					
					if (historyStat != null) {
						if (moveGen.getScore() > historyStat.getEntropy()) {
							reduction -= 1;
							if (moveGen.getScore() > historyStat.getEntropy() + historyStat.getDisperse()) {
								reduction -= 1;
							}
						}
					}
					
					if (move == killer1Move || move == killer2Move || move == counterMove) {
						reduction -= 1;
					}
					
					if (!isPv) {
						reduction += 1;
					}
					
					reduction = Math.min(depth - 1, Math.max(reduction, 1));
				}
				
				try {
					if (EngineConstants.ENABLE_LMR && reduction != 1) {
						score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false);
					}
					
					if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed > 1) {
						score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -alpha - 1, -alpha, false);
					}
					
					if (score > alpha) {
						score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -beta, -alpha, isPv);
					}
				} catch(SearchInterruptedException sie) {
					moveGen.endPly();
					throw sie;
				}
				
				cb.undoMove(move);
				
				if (MoveUtil.isQuiet(move)) {
					moveGen.addBFValue(cb.colorToMove, move, parentMove, depth);
				}
				
				if (score > bestScore) {
					
					bestScore = score;
					bestMove = move;

					node.bestmove = bestMove;
					node.eval = bestScore;
					node.leaf = false;
					
					if (ply + 1 < ISearch.MAX_DEPTH) {
						pvman.store(ply + 1, node, pvman.load(ply + 1), true);
					}
					
					alpha = Math.max(alpha, score);
					if (alpha >= beta) {
						
						if (MoveUtil.isQuiet(bestMove) && cb.checkingPieces == 0) {
							moveGen.addCounterMove(cb.colorToMove, parentMove, bestMove);
							moveGen.addKillerMove(bestMove, ply);
							moveGen.addHHValue(cb.colorToMove, bestMove, parentMove, depth);
						}

						phase += 10;
						break;
					}
				}
			}
			phase++;
		}
		moveGen.endPly();
		
		if (movesPerformed == 0) {
			if (cb.checkingPieces == 0) {
				node.bestmove = 0;
				node.eval = EvalConstants.SCORE_DRAW;
				node.leaf = true;
				return node.eval;
			} else {
				node.bestmove = 0;
				node.eval = -SearchUtils.getMateVal(ply);
				node.leaf = true;
				return node.eval;
			}
		}

		if (EngineConstants.ASSERT) {
			Assert.isTrue(bestMove != 0);
		}
		

		if (!SearchUtils.isMateVal(bestScore)) {
			env.getTPT().put(cb.zobristKey, depth, bestScore, alphaOrig, beta, bestMove);
		}
		
		//validatePV(node, depth, isPv);
		
		return bestScore;
	}


	public int qsearch(IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen, int alpha, final int beta, final int ply, final boolean isPv) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		env.getTPT().get(cb.zobristKey, tt_cached);
		if (!tt_cached.isEmpty()) {
			int tpt_depth = tt_cached.getDepth();
			
			if (tpt_depth >= 0) {
				if (tt_cached.getFlag() == ITTEntry.FLAG_EXACT) {
					return tt_cached.getEval();
				} else {
					if (tt_cached.getFlag() == ITTEntry.FLAG_LOWER && tt_cached.getEval() >= beta) {
						return tt_cached.getEval();
					}
					if (tt_cached.getFlag() == ITTEntry.FLAG_UPPER && tt_cached.getEval() <= alpha) {
						return tt_cached.getEval();
					}
				}
			}
		}
		
		if (cb.checkingPieces != 0) {
			return alpha;
		}
		
		int eval = eval(evaluator, ply, alpha, beta, isPv);
		if (eval >= beta) {
			return eval;
		}
		
		final int alphaOrig = alpha;
		
		alpha = Math.max(alpha, eval);
		
		moveGen.startPly();
		
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING_GOOD) {
			switch (phase) {
				case PHASE_TT:
					if (!tt_cached.isEmpty()) {
						int ttMove = tt_cached.getBestMove();
						if (cb.isValidMove(ttMove)) {
							if (env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)) {
								moveGen.addMove(ttMove);
							}
						}
					}
					break;
				case PHASE_ATTACKING_GOOD:
					moveGen.generateAttacks(cb);
					moveGen.setMVVLVAScores(cb);
					moveGen.sort();
					break;
			}
			
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				if (!cb.isLegal(move)) {
					continue;
				}
				
				if (MoveUtil.isPromotion(move)) {
					if (MoveUtil.getMoveType(move) != MoveUtil.TYPE_PROMOTION_Q) {
						continue;
					}
				} else if (EngineConstants.ENABLE_Q_FUTILITY_PRUNING
						&& eval + FUTILITY_MARGIN_Q_SEARCH + EvalConstants.MATERIAL[MoveUtil.getAttackedPieceIndex(move)] < alpha) {
					continue;
				}
				
				if (EngineConstants.ENABLE_Q_PRUNE_BAD_CAPTURES && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move)) && SEEUtil.getSeeCaptureScore(cb, move) <= 0) {
					continue;
				}
	
				cb.doMove(move);
	
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				final int score = -qsearch(evaluator, info, cb, moveGen, -beta, -alpha, ply + 1, isPv);
				
				cb.undoMove(move);
				
				if (score > alpha) {
					if (!SearchUtils.isMateVal(score)) {
						env.getTPT().put(cb.zobristKey, 0, score, alphaOrig, beta, move);
					}
				}
				
				if (score >= beta) {
					moveGen.endPly();				
					return score;
				}
				alpha = Math.max(alpha, score);
			}
			
			phase++;
		}
		moveGen.endPly();
		
		return alpha;
	}
	
	
	private int extensions(final ChessBoard cb, final MoveGenerator moveGen, final int ply) {
		if (EngineConstants.ENABLE_CHECK_EXTENSION && cb.checkingPieces != 0) {
			return 1;
		}
		return 0;
	}
	
	
	private int eval(IEvaluator evaluator, final int ply, final int alpha, final int beta, final boolean isPv) {
		int eval = (int) evaluator.fullEval(ply, alpha, beta, 0);
		return eval;
	}
	
	
	private boolean extractFromTT(int ply, PVNode result, ITTEntry entry, ISearchInfo info, boolean isPv) {
		
		if (entry.isEmpty()) {
			throw new IllegalStateException("entry.isEmpty()");
		}
		
		if (result == null) {
			return false;
		}
		
		result.leaf = true;
		
		if (ply > 0 && isDraw()) {
			result.eval = EvalConstants.SCORE_DRAW;
			result.bestmove = 0;
			return true;
		}

		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		result.eval = entry.getEval();
		result.bestmove = entry.getBestMove();
		
		boolean draw = false;
		
		if (isPv && ((BoardImpl) env.getBitboard()).getChessBoard().isValidMove(result.bestmove)) {
			
			env.getBitboard().makeMoveForward(result.bestmove);
			
			env.getTPT().get(env.getBitboard().getHashKey(), tt_cached);
			
			if (!tt_cached.isEmpty()) {
				draw = extractFromTT(ply + 1, result.child, tt_cached, info, isPv);
				if (draw) {
					result.eval = EvalConstants.SCORE_DRAW;
				} else {
					result.leaf = false;
				}
			}
			
			env.getBitboard().makeMoveBackward(result.bestmove);
		}
		
		return draw;
	}
	
	
	private Stack<Integer> stack = new Stack<Integer>();
	
	private void validatePV(PVNode node, int expectedDepth, boolean isPv) {
		
		if (node.leaf || node.bestmove == 0) {
			throw new IllegalStateException();
		}
		
		int actualDepth = 0;
		PVNode cur = node;
		while(cur != null && cur.bestmove != 0) {
			
			actualDepth++;
			
			if (env.getBitboard().isPossible(cur.bestmove)) {
				env.getBitboard().makeMoveForward(cur.bestmove);
				stack.push(cur.bestmove);
			} else {
				throw new IllegalStateException("not valid move " + env.getBitboard().getMoveOps().moveToString(cur.bestmove));
			}
			
			if (cur.leaf) {
				break;
			}
			
			cur = cur.child;
		}
		
		if (actualDepth < expectedDepth) {
			if (isPv) {
				if (!isDraw()) {
					if (env.getBitboard().isInCheck()) {
						if (env.getBitboard().hasMoveInCheck()) {
							//throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
							System.out.println("NOT ok in check");	
						}
					} else {
						if (env.getBitboard().hasMoveInNonCheck()) {
							//throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
							System.out.println("NOT ok in noncheck");
						}
					}
				}
			}
		}
		
		try {
			Integer move;
			while ((move = stack.pop()) != null) {
				env.getBitboard().makeMoveBackward(move);
			}
		} catch(EmptyStackException ese) {
			//Do nothing
		}
	}
}
