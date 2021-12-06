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
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private static final int PHASE_TT = 0;
	private static final int PHASE_ATTACKING = 1;
	private static final int PHASE_KILLER_1 = 2;
	private static final int PHASE_KILLER_2 = 3;
	private static final int PHASE_COUNTER = 4;
	private static final int PHASE_QUIET = 5;
	
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
	
	
	private Stack[] stack = new Stack[MAX_DEPTH];
	private MovePicker[] movePickers = new MovePicker[MAX_DEPTH];
	
	private CounterMoveHistory counterMoves;
	private ButterflyHistory mainHistory;
	private CapturePieceToHistory captureHistory;
	private ContinuationHistory continuationHistory;
	
	
	public Search_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
		
		TTUtil.setSizeMB(256);
		EvalUtil.setSizeMB(64);
		
		for (int i=0; i<stack.length; i++) {
			stack[i] = new Stack();
			stack[i].ply = i;
		}
		
		for (int i=0; i<movePickers.length; i++) {
			movePickers[i] = new MovePicker();
		}
		
		counterMoves = new CounterMoveHistory();
		mainHistory = new ButterflyHistory();
		captureHistory = new CapturePieceToHistory();
		continuationHistory = new ContinuationHistory();
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		super(_env);
		
		TTUtil.setSizeMB(256);
		EvalUtil.setSizeMB(64);
		
		for (int i=0; i<stack.length; i++) {
			stack[i] = new Stack();
			stack[i].ply = i;
		}
		
		for (int i=0; i<movePickers.length; i++) {
			movePickers[i] = new MovePicker();
		}
		
		counterMoves = new CounterMoveHistory();
		mainHistory = new ButterflyHistory();
		captureHistory = new CapturePieceToHistory();
		continuationHistory = new ContinuationHistory();
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		return (int) TTUtil.getUsagePercentage() / 10;
	}
	
	
	public void newSearch() {
		
		super.newSearch();
		
		((BoardImpl) env.getBitboard()).getMoveGenerator().clearHistoryHeuristics();
		
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
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, SearchUtils.normDepth(maxdepth), alpha_org, beta, true);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, SearchUtils.normDepth(maxdepth), beta - 1, beta, false);		
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
			return eval(evaluator, ply, alpha, beta);
		}
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		
		if (ply > 0 && isDraw()) {
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
		
		long ttValue = TTUtil.getTTValue(cb.zobristKey);
		int score = TTUtil.getScore(ttValue);
		if (ttValue != 0) {
			if (!isPv /*&& ply > 0*/) {

				if (TTUtil.getDepth(ttValue) >= depth) {
					switch (TTUtil.getFlag(ttValue)) {
					case TTUtil.FLAG_EXACT:
						extractFromTT(ply, node, ttValue, info, isPv);
						return node.eval;
					case TTUtil.FLAG_LOWER:
						if (score >= beta) {
							extractFromTT(ply, node, ttValue, info, isPv);
							return node.eval;
						}
						break;
					case TTUtil.FLAG_UPPER:
						if (score <= alpha) {
							extractFromTT(ply, node, ttValue, info, isPv);
							return node.eval;
						}
						break;
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
					node.eval = -SearchUtils.getMateVal(ply);
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
			int qeval = qsearch(evaluator, info, cb, moveGen, alpha, beta, ply);
			node.bestmove = 0;
			node.eval = qeval;
			node.leaf = true;
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		
		int eval = ISearch.MIN;
		if (!isPv && cb.checkingPieces == 0) {

			
			eval = eval(evaluator, ply, alphaOrig, beta);
			
			
			if (EngineConstants.USE_TT_SCORE_AS_EVAL && ttValue != 0) {
				if (TTUtil.getFlag(ttValue) == TTUtil.FLAG_EXACT || TTUtil.getFlag(ttValue) == TTUtil.FLAG_UPPER && score < eval
						|| TTUtil.getFlag(ttValue) == TTUtil.FLAG_LOWER && score > eval) {
					eval = score;
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
			
			
			if (EngineConstants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length && Math.abs(alpha) < IEvaluator.MAX_EVAL) {
				if (eval + RAZORING_MARGIN[depth] < alpha) {
					score = qsearch(evaluator, info, cb, moveGen, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1, ply);
					if (score + RAZORING_MARGIN[depth] <= alpha) {
						node.bestmove = 0;
						node.eval = score;
						node.leaf = true;
						return node.eval;
					}
				}
			}

			
			if (EngineConstants.ENABLE_NULL_MOVE && depth > 2) {
				if (eval >= beta && MaterialUtil.hasNonPawnPieces(cb.materialKey, cb.colorToMove)) {
					cb.doNullMove();
					final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
					score = depth - reduction <= 0 ? -qsearch(evaluator, info, cb, moveGen, -beta, -beta + 1, ply)
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

		
		MovePicker mp = movePickers[ply];
        mp.init(env.getBitboard(), TTUtil.getMove(ttValue), mainHistory, captureHistory,
        		ply >= 1 ? stack[ply - 1].continuationHistory : null,
        		ply >= 2 ? stack[ply - 2].continuationHistory : null,
        		ply >= 4 ? stack[ply - 4].continuationHistory : null,
        		0, stack[ply].killers);
        
        
        int quietsSearched[] = new int[64];
        int capturesSearched[] = new int[32];
        int quietCount = 0;
        int captureCount = 0;
        
        
		final boolean wasInCheck = cb.checkingPieces != 0;

		final int parentMove = ply == 0 ? 0 : moveGen.previous();
		int bestMove = 0;
		int bestScore = ISearch.MIN - 1;
		int ttMove = 0;
		int counterMove = 0;
		int killer1Move = 0;
		int killer2Move = 0;
		int movesPerformed = 0;
		
        int move;
        int moveCount = 0;
        int bestValue = MIN;
        while ((move = mp.next_move()) != 0) {

			//Build and sent minor info
			if (depth == 0) {
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

			if(!cb.isLegal(move)) {
				continue;
			}
			
			boolean captureOrPromotion = env.getBitboard().getMoveOps().isCaptureOrPromotion(move);
			
			if (!isPv && !wasInCheck && movesPerformed > 0 && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move))) {

				if (MoveUtil.isQuiet(move)) {
					
					if (EngineConstants.ENABLE_LMP && depth <= 4 && movesPerformed >= depth * 3 + 3) {
						continue;
					}
					
					if (EngineConstants.ENABLE_FUTILITY_PRUNING && depth < FUTILITY_MARGIN.length) {
						if (!MoveUtil.isPawnPush78(move)) {
							if (eval == ISearch.MIN) {
								eval = eval(evaluator, ply, alphaOrig, beta);
							}
							if (eval + FUTILITY_MARGIN[depth] <= alpha) {
								continue;
							}
						}
					}
				} else if (EngineConstants.ENABLE_SEE_PRUNING && depth <= 6
						&& SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth) {
					continue;
				}
			}

			cb.doMove(move);
			movesPerformed++;
			
			score = alpha + 1;

			if (EngineConstants.ASSERT) {
				cb.changeSideToMove();
				Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
				cb.changeSideToMove();
			}

			int reduction = 1;
			if (depth > 2 && movesPerformed > 1 && MoveUtil.isQuiet(move) && !MoveUtil.isPawnPush78(move)) {

				reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed, 63)];
				/*if (moveGen.getScore() > 40) {
					reduction -= 1;
				}*/
				if (move == killer1Move || move == counterMove) {
					reduction -= 1;
				}
				if (!isPv) {
					reduction += 1;
				}
				reduction = Math.min(depth - 1, Math.max(reduction, 1));
			}
			
			if (EngineConstants.ENABLE_LMR && reduction != 1) {
				score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false);
			}
			
			if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed > 1) {
				score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -alpha - 1, -alpha, false);
			}
			
			if (score > alpha) {
				score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -beta, -alpha, isPv);
			}
			
			cb.undoMove(move);

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
					
					if (MoveUtil.isQuiet(move) && cb.checkingPieces == 0) {
						moveGen.addCounterMove(cb.colorToMove, parentMove, move);
						moveGen.addKillerMove(move, ply);
						moveGen.addHHValue(cb.colorToMove, move, parentMove, depth);
					}
					break;
				}
			}

			if (MoveUtil.isQuiet(move)) {
				moveGen.addBFValue(cb.colorToMove, move, parentMove, depth);
			}
			
            if (move != bestMove)
            {
                if (captureOrPromotion && captureCount < 32)
                    capturesSearched[captureCount++] = move;

                else if (!captureOrPromotion && quietCount < 64)
                    quietsSearched[quietCount++] = move;
            }
		}
		
		
        if (bestMove != 0)
	    {
	        // Quiet best move: update move sorting heuristics
	        if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(bestMove))
	            update_quiet_stats(ply, stack, bestMove, quietsSearched, quietCount, stat_bonus(depth));

	        update_capture_stats(ply, stack, bestMove, capturesSearched, captureCount, stat_bonus(depth));
	    }
		
		
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
		
		int flag = TTUtil.FLAG_EXACT;
		if (bestScore >= beta) {
			flag = TTUtil.FLAG_LOWER;
		} else if (bestScore <= alphaOrig) {
			flag = TTUtil.FLAG_UPPER;
		}

		if (!SearchUtils.isMateVal(bestScore)) {
			TTUtil.addValue(cb.zobristKey, bestScore, ply, depth, flag, bestMove);
		}
		
		return bestScore;
	}

	
	  // update_quiet_stats() updates move sorting heuristics when a new quiet best move is found

	  void update_quiet_stats(int ply, Stack[] stack, int bestMove, int[] quiets, int quietsCnt, int bonus) {

	    if (stack[ply].killers[0] != bestMove)
	    {
	    	stack[ply].killers[1] = stack[ply].killers[0];
	    	stack[ply].killers[0] = bestMove;
	    }

	    int us = env.getBitboard().getColourToMove();
	    int from_to = env.getBitboard().getMoveOps().getFromFieldID(bestMove) * env.getBitboard().getMoveOps().getToFieldID(bestMove);
	    mainHistory.array[us][from_to] += bonus;
	    update_continuation_histories(ply, stack, env.getBitboard().getMoveOps().getFigurePID(bestMove), env.getBitboard().getMoveOps().getToFieldID(bestMove), bonus);

	    if (ply > 0 && stack[ply - 1].currentMove != 0)
	    {
	        int prevSq = env.getBitboard().getMoveOps().getToFieldID(stack[ply - 1].currentMove);
	        counterMoves.array[env.getBitboard().getFigureType(prevSq)][prevSq] = bestMove;
	    }

	    // Decrease all the other played quiet moves
	    for (int i = 0; i < quietsCnt; ++i)
	    {
	    	from_to = env.getBitboard().getMoveOps().getFromFieldID(quiets[i]) * env.getBitboard().getMoveOps().getToFieldID(quiets[i]);
	        mainHistory.array[us][from_to] -= bonus;
	        update_continuation_histories(ply, stack, env.getBitboard().getMoveOps().getFigurePID(quiets[i]), env.getBitboard().getMoveOps().getToFieldID(quiets[i]), -bonus);
	    }
	  }


	  // update_continuation_histories() updates histories of the move pairs formed
	  // by moves at ply -1, -2, and -4 with current move.

	  void update_continuation_histories(int ply, Stack[] stack, int pc, int to, int bonus) {
		  if (ply >= 1 && stack[ply - 1].currentMove != 0)
	            stack[ply - 1].continuationHistory.array[pc][to] = stack[ply - 1].continuationHistory.array[pc][to] + bonus;
		  
		  if (ply >= 2 && stack[ply - 2].currentMove != 0)
	            stack[ply - 2].continuationHistory.array[pc][to] = stack[ply - 2].continuationHistory.array[pc][to] + bonus;

		  if (ply >=4 && stack[ply - 4].currentMove != 0)
	            stack[ply - 4].continuationHistory.array[pc][to] = stack[ply - 4].continuationHistory.array[pc][to] + bonus;
	  }
	  
	  
	  // update_capture_stats() updates move sorting heuristics when a new capture best move is found

	  void update_capture_stats(int ply, Stack[] stack, int bestMove, int[] captures, int captureCnt, int bonus) {

	      //CapturePieceToHistory& captureHistory =  pos.this_thread()->captureHistory;
	      int moved_piece = env.getBitboard().getMoveOps().getFigurePID(bestMove);
	      int capturedType = env.getBitboard().getMoveOps().getCapturedFigureType(bestMove);

        captureHistory.array[moved_piece][env.getBitboard().getMoveOps().getToFieldID(bestMove)][capturedType] += bonus;

	      // Decrease all the other played capture moves
	      for (int i = 0; i < captureCnt; ++i)
	      {
	          moved_piece = env.getBitboard().getMoveOps().getFigurePID(captures[i]);
	          capturedType = env.getBitboard().getMoveOps().getCapturedFigureType(captures[i]);
	          captureHistory.array[moved_piece][env.getBitboard().getMoveOps().getToFieldID(captures[i])][capturedType] -= bonus;
	      }
	  }
	  
	  
	  // History and stats update bonus, based on depth
	  int stat_bonus(int depth) {
	    //return depth > 17 ? 0 : 29 * depth * depth + 138 * depth - 134;
		  return depth * depth;
	  }
	  

	public int qsearch(IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen, int alpha, final int beta, final int ply) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		long ttValue = TTUtil.getTTValue(cb.zobristKey);
		int ttScore = TTUtil.getScore(ttValue);
		if (ttValue != 0) {
			if (!EngineConstants.TEST_TT_VALUES) {
				if (TTUtil.getDepth(ttValue) >= 0) {
					switch (TTUtil.getFlag(ttValue)) {
					case TTUtil.FLAG_EXACT:
						return ttScore;
					case TTUtil.FLAG_LOWER:
						if (ttScore >= beta) {
							return ttScore;
						}
						break;
					case TTUtil.FLAG_UPPER:
						if (ttScore <= alpha) {
							return ttScore;
						}
					}
				}
			}
		}
		
		final int alphaOrig = alpha;
		
		int eval = ISearch.MIN;
		if (cb.checkingPieces == 0) {
			eval = eval(evaluator, ply, alpha, beta);
			if (eval >= beta) {
				return eval;
			}
			alpha = Math.max(alpha, eval);
		}

		moveGen.startPly();
		
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING) {
			switch (phase) {
				case PHASE_TT:
					if (ttValue != 0) {
						int ttMove = TTUtil.getMove(ttValue);
						if (env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)) {
							moveGen.addMove(ttMove);
						}
					}
					break;
				case PHASE_ATTACKING:
					moveGen.generateAttacks(cb);
					moveGen.setMVVLVAScores(cb);
					moveGen.sort();
					break;
			}
			
			while (moveGen.hasNext()) {
				final int move = moveGen.next();
	
				if (cb.checkingPieces == 0) {
					if (MoveUtil.isPromotion(move)) {
						if (MoveUtil.getMoveType(move) != MoveUtil.TYPE_PROMOTION_Q) {
							continue;
						}
					} else if (EngineConstants.ENABLE_Q_FUTILITY_PRUNING
							&& eval + FUTILITY_MARGIN_Q_SEARCH + EvalConstants.MATERIAL[MoveUtil.getAttackedPieceIndex(move)] < alpha) {
						continue;
					}
				}
				
				if (!cb.isLegal(move)) {
					continue;
				}
				
				//if (cb.checkingPieces == 0) {
					if (EngineConstants.ENABLE_Q_PRUNE_BAD_CAPTURES && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move)) && SEEUtil.getSeeCaptureScore(cb, move) <= 0) {
						continue;
					}
				//}
	
				cb.doMove(move);
	
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				final int score = -qsearch(evaluator, info, cb, moveGen, -beta, -alpha, ply + 1);
	
				cb.undoMove(move);
	
				if (score > alpha) {
					int flag = TTUtil.FLAG_EXACT;
					if (score >= beta) {
						flag = TTUtil.FLAG_LOWER;
					} else if (score <= alphaOrig) {
						flag = TTUtil.FLAG_UPPER;
					}
					if (!SearchUtils.isMateVal(score)) {
						TTUtil.addValue(cb.zobristKey, score, ply, 0, flag, move);
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
	
	
	private int eval(IEvaluator evaluator, final int ply, int alpha, int beta) {
		long value = EvalUtil.getValue(env.getBitboard().getHashKey());
		if (value != 0) {
			return EvalUtil.getScore(value);
		}
		int eval = (int) evaluator.fullEval(ply, alpha, beta, 0);
		EvalUtil.addValue(env.getBitboard().getHashKey(), eval);
		return eval;
	}
	
	
	private boolean extractFromTT(int ply, PVNode result, long currentTTValue, ISearchInfo info, boolean isPv) {
		
		if (currentTTValue == 0) {
			throw new IllegalStateException("currentTTValue == 0");
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
		
		result.eval = TTUtil.getScore(currentTTValue);
		result.bestmove = TTUtil.getMove(currentTTValue);
		
		boolean draw = false;
		
		if (isPv) {
			
			env.getBitboard().makeMoveForward(result.bestmove);
			
			long hashkey = env.getBitboard().getHashKey();
			long nextTTValue = TTUtil.getTTValue(hashkey);
			
			if (nextTTValue != 0) {
				draw = extractFromTT(ply + 1, result.child, nextTTValue, info, isPv);
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
}
