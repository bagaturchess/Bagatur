package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MaterialUtil;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.utils.SearchUtils;


public final class NegamaxUtil {

	
	private static final int PHASE_TT = 0;
	private static final int PHASE_ATTACKING = 1;
	private static final int PHASE_KILLER_1 = 2;
	private static final int PHASE_KILLER_2 = 3;
	private static final int PHASE_COUNTER = 4;
	private static final int PHASE_QUIET = 5;

	// Margins shamelessly stolen from Laser
	private static final int[] STATIC_NULLMOVE_MARGIN = { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] RAZORING_MARGIN = { 0, 240, 280, 300 };
	private static final int[] FUTILITY_MARGIN = { 0, 80, 170, 270, 380, 500, 630 };
	private static final int[][] LMR_TABLE = new int[64][64];
	static {
		// Ethereal LMR formula with depth and number of performed moves
		for (int depth = 1; depth < 64; depth++) {
			for (int moveNumber = 1; moveNumber < 64; moveNumber++) {
				//TODO LMR_TABLE[depth][moveNumber] = (int) (0.5f + Math.log(depth) * Math.log(moveNumber * 1.2f) / 2.5f);
				LMR_TABLE[depth][moveNumber] = 1 + (int) Math.ceil(Math.max(1, Math.log(moveNumber) * Math.log(depth) / (double) 2));
			}
		}
	}
	
	private static final int FUTILITY_MARGIN_Q_SEARCH = 200;
	
	
	public static void start(final ChessBoard cb) {
		//cb.moveCount = 0;
		TTUtil.init(false);
	}
	
	
	public static int calculateBestMove(ISearchMediator mediator, ISearchInfo info, PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
			final int threadNumber, final int ply, int depth, int alpha, int beta, final int nullMoveCounter, boolean isPv) {
		
		
		if (mediator != null && mediator.getStopper() != null) mediator.getStopper().stopIfNecessary(depth, 0, alpha, beta);
		
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			return evaluator.lazyEval(ply, alpha, beta, 0);
		}
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
		}

		final int alphaOrig = alpha;

		// get extensions
		//depth += extensions(cb, moveGen, ply);

		/* transposition-table */
		long ttValue = TTUtil.getTTValue(cb.zobristKey);
		int score = TTUtil.getScore(ttValue);
		if (!isPv && ttValue != 0) {
			if (!EngineConstants.TEST_TT_VALUES) {

				if (TTUtil.getDepth(ttValue) >= depth) {
					switch (TTUtil.getFlag(ttValue)) {
					case TTUtil.FLAG_EXACT:
						node.bestmove = TTUtil.getMove(ttValue);
						node.eval = score;
						return node.eval;
					case TTUtil.FLAG_LOWER:
						if (score >= beta) {
							node.bestmove = TTUtil.getMove(ttValue);
							node.eval = score;
							return node.eval;
						}
						break;
					case TTUtil.FLAG_UPPER:
						if (score <= alpha) {
							node.bestmove = TTUtil.getMove(ttValue);
							node.eval = score;
							return node.eval;
						}
					}
				}
			}
		}
		
		if (depth == 0) {
			node.eval = calculateBestMove(evaluator, info, cb, moveGen, alpha, beta, ply);
			node.bestmove = 0;
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		
		int eval = ISearch.MIN;
		//final boolean isPv = beta - alpha != 1;
		if (!isPv && cb.checkingPieces == 0) {

			eval = evaluator.lazyEval(ply, alphaOrig, beta, 0);

			/* use tt value as eval */
			if (EngineConstants.USE_TT_SCORE_AS_EVAL && ttValue != 0) {
				if (TTUtil.getFlag(ttValue) == TTUtil.FLAG_EXACT || TTUtil.getFlag(ttValue) == TTUtil.FLAG_UPPER && score < eval
						|| TTUtil.getFlag(ttValue) == TTUtil.FLAG_LOWER && score > eval) {
					eval = score;
				}
			}

			/* static null move pruning */
			if (EngineConstants.ENABLE_STATIC_NULL_MOVE && depth < STATIC_NULLMOVE_MARGIN.length) {
				if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
					node.eval = eval;
					return node.eval;
				}
			}
			
			//Razoring for all depths based on the eval deviation detected into the root node
			/*if (eval < alpha - mediator.getTrustWindow_AlphaAspiration()) {
				score = calculateBestMove(evaluator, info, cb, moveGen, alpha - mediator.getTrustWindow_AlphaAspiration(), alpha - mediator.getTrustWindow_AlphaAspiration() + 1, ply);
				if (score <= alpha - mediator.getTrustWindow_AlphaAspiration()) {
					node.eval = score;
					return node.eval;
				}
			}*/
			
			/* razoring */
			if (EngineConstants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length && Math.abs(alpha) < ISearch.MAX_MAT_INTERVAL) {
				if (eval + RAZORING_MARGIN[depth] < alpha) {
					score = calculateBestMove(evaluator, info, cb, moveGen, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1, ply);
					if (score + RAZORING_MARGIN[depth] <= alpha) {
						node.eval = score;
						return node.eval;
					}
				}
			}

			/* null-move */
			if (EngineConstants.ENABLE_NULL_MOVE) {
				if (/*nullMoveCounter < 2 &&*/ eval >= beta && MaterialUtil.hasNonPawnPieces(cb.materialKey, cb.colorToMove)) {
					cb.doNullMove();
					final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
					score = depth - reduction <= 0 ? -calculateBestMove(evaluator, info, cb, moveGen, -beta, -beta + 1, ply)
							: -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, threadNumber, ply + 1, depth - reduction, -beta, -beta + 1, nullMoveCounter + 1, false);
					cb.undoNullMove();
					if (score >= beta) {
						node.eval = score;
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
		while (phase <= PHASE_QUIET) {
			switch (phase) {
			case PHASE_TT:
				/*if (ttValue == 0) {
					// IID
					if (EngineConstants.ENABLE_IID && depth > 5 && isPv) {
						// no iid in pawn-endgame because the extension could cause an endless loop
						if (MaterialUtil.containsMajorPieces(cb.materialKey)) {
							calculateBestMove(mediator, pvman, evaluator, cb, moveGen, threadNumber, ply, depth - EngineConstants.IID_REDUCTION - 1, alpha, beta, 0, isPv);
							ttValue = TTUtil.getTTValue(cb.zobristKey);
						}
					}
				}*/
				if (ttValue != 0) {
					ttMove = TTUtil.getMove(ttValue);

					// verify TT-move?
					if (EngineConstants.VERIFY_TT_MOVE) {
						if (!cb.isValidMove(ttMove)) {
							throw new RuntimeException("Invalid tt-move found! " + new MoveWrapper(ttMove) + " - " + cb.toString());
						}
					}

					moveGen.addMove(ttMove);
				}
				break;
			case PHASE_ATTACKING:
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores();
				moveGen.sort();
				break;
			case PHASE_KILLER_1:
				killer1Move = moveGen.getKiller1(ply);
				if (killer1Move != 0 && killer1Move != ttMove && cb.isValidQuietMove(killer1Move) && cb.isLegal(killer1Move)) {
					moveGen.addMove(killer1Move);
					break;
				} else {
					phase++;
				}
			case PHASE_KILLER_2:
				killer2Move = moveGen.getKiller2(ply);
				if (killer2Move != 0 && killer2Move != ttMove && cb.isValidQuietMove(killer2Move) && cb.isLegal(killer2Move)) {
					moveGen.addMove(killer2Move);
					break;
				} else {
					phase++;
				}
			case PHASE_COUNTER:
				counterMove = moveGen.getCounter(cb.colorToMove, parentMove);
				if (counterMove != 0 && counterMove != ttMove && counterMove != killer1Move && counterMove != killer2Move && cb.isValidQuietMove(counterMove)
						&& cb.isLegal(counterMove)) {
					moveGen.addMove(counterMove);
					break;
				} else {
					phase++;
				}
			case PHASE_QUIET:
				moveGen.generateMoves(cb);
				moveGen.setHHScores(cb.colorToMove);
				moveGen.sort();
			}

			while (moveGen.hasNext()) {
				final int move = moveGen.next();

				if (phase == PHASE_QUIET) {
					if (move == ttMove || move == killer1Move || move == killer2Move || move == counterMove || !cb.isLegal(move)) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING) {
					if (move == ttMove || !cb.isLegal(move)) {
						continue;
					}
				}

				// pruning allowed?
				if (!isPv && !wasInCheck && movesPerformed > 0 && moveGen.getScore() < 100 && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move))) {

					if (MoveUtil.isQuiet(move)) {

						/* late move pruning */
						if (EngineConstants.ENABLE_LMP && depth <= 4 && movesPerformed >= depth * 3 + 3) {
							continue;
						}

						/* futility pruning */
						if (EngineConstants.ENABLE_FUTILITY_PRUNING && depth < FUTILITY_MARGIN.length) {
							if (!MoveUtil.isPawnPush78(move)) {
								if (eval == ISearch.MIN) {
									eval = evaluator.lazyEval(ply, alphaOrig, beta, 0);
								}
								if (eval + FUTILITY_MARGIN[depth] <= alpha) {
									continue;
								}
							}
						}
					}

					/* SEE Pruning */
					else if (EngineConstants.ENABLE_SEE_PRUNING && depth <= 6 && phase == PHASE_ATTACKING
							&& SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth) {
						continue;
					}
				}

				cb.doMove(move);
				movesPerformed++;

				/* draw check */
				if (cb.isRepetition(move) || MaterialUtil.isDrawByMaterial(cb)) {
					score = EvalConstants.SCORE_DRAW;
				} else {
					score = alpha + 1; // initial is above alpha

					if (EngineConstants.ASSERT) {
						cb.changeSideToMove();
						Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
						cb.changeSideToMove();
					}

					int reduction = 1;
					if (depth > 2 && movesPerformed > 1 && MoveUtil.isQuiet(move) && !MoveUtil.isPawnPush78(move)) {

						reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed, 63)];
						if (moveGen.getScore() > 40) {
							reduction -= 1;
						}
						if (move == killer1Move || move == counterMove) {
							reduction -= 1;
						}
						if (!isPv) {
							reduction += 1;
						}
						reduction = Math.min(depth - 1, Math.max(reduction, 1));
					}

					try {
						/* LMR */
						if (EngineConstants.ENABLE_LMR && reduction != 1) {
							score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, threadNumber, ply + 1, depth - reduction, -alpha - 1, -alpha, 0, false);
						}
	
						/* PVS */
						if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed > 1) {
							score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, threadNumber, ply + 1, depth - 1, -alpha - 1, -alpha, 0, false);
						}
	
						/* normal bounds */
						if (score > alpha) {
							score = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, threadNumber, ply + 1, depth - 1, -beta, -alpha, 0, isPv);
						}
					} catch(SearchInterruptedException sie) {
						moveGen.endPly();
						throw sie;
					}
					
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

						/* killer and history */
						if (MoveUtil.isQuiet(move) && cb.checkingPieces == 0) {
							moveGen.addCounterMove(cb.colorToMove, parentMove, move);
							moveGen.addKillerMove(move, ply);
							moveGen.addHHValue(cb.colorToMove, move, depth);
						}

						phase += 10;
						break;
					}
				}

				if (MoveUtil.isQuiet(move)) {
					moveGen.addBFValue(cb.colorToMove, move, depth);
				}
			}
			phase++;
		}
		moveGen.endPly();
		
		/* checkmate or stalemate */
		if (movesPerformed == 0) {
			if (cb.checkingPieces == 0) {
				node.eval = EvalConstants.SCORE_DRAW;
				return node.eval;
			} else {
				node.eval = -SearchUtils.getMateVal(ply);
				return node.eval;
			}
		}
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(bestMove != 0);
		}
		
		// set tt-flag
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
	

	public static int calculateBestMove(IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen, int alpha, final int beta, int ply) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		/* transposition-table */
		/*long ttValue = TTUtil.getTTValue(cb.zobristKey);
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
		
		
		final int alphaOrig = alpha;*/
		
		
		/* stand-pat check */
		int eval = ISearch.MIN;
		if (cb.checkingPieces == 0) {
			eval = evaluator.lazyEval(0, alpha, beta, 0);
			if (eval >= beta) {
				return eval;
			}
			alpha = Math.max(alpha, eval);
		}

		moveGen.startPly();
		moveGen.generateAttacks(cb);
		moveGen.setMVVLVAScores();
		moveGen.sort();

		while (moveGen.hasNext()) {
			final int move = moveGen.next();

			// skip under promotions
			if (MoveUtil.isPromotion(move)) {
				if (MoveUtil.getMoveType(move) != MoveUtil.TYPE_PROMOTION_Q) {
					continue;
				}
			} else if (EngineConstants.ENABLE_Q_FUTILITY_PRUNING
					&& eval + FUTILITY_MARGIN_Q_SEARCH + EvalConstants.MATERIAL[MoveUtil.getAttackedPieceIndex(move)] < alpha) {
				// futility pruning
				continue;
			}

			if (!cb.isLegal(move)) {
				continue;
			}

			// skip bad-captures
			if (EngineConstants.ENABLE_Q_PRUNE_BAD_CAPTURES && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move)) && SEEUtil.getSeeCaptureScore(cb, move) <= 0) {
				continue;
			}

			cb.doMove(move);

			if (EngineConstants.ASSERT) {
				cb.changeSideToMove();
				Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
				cb.changeSideToMove();
			}

			final int score = -calculateBestMove(evaluator, info, cb, moveGen, -beta, -alpha, ply + 1);

			cb.undoMove(move);

			if (score >= beta) {
				moveGen.endPly();
				
				// set tt-flag
				/*int flag = TTUtil.FLAG_EXACT;
				if (score >= beta) {
					flag = TTUtil.FLAG_LOWER;
				} else if (score <= alphaOrig) {
					flag = TTUtil.FLAG_UPPER;
				}
				if (!SearchUtils.isMateVal(score)) {
					TTUtil.addValue(cb.zobristKey, score, ply, 0, flag, move);
				}*/
				
				return score;
			}
			alpha = Math.max(alpha, score);
		}

		moveGen.endPly();
		
		return alpha;
	}
	
	
	private static int extensions(final ChessBoard cb, final MoveGenerator moveGen, final int ply) {
		/* extension when the pawn endgame starts */
		if (EngineConstants.ENABLE_ENDGAME_EXTENSION && ply > 0 && MoveUtil.getAttackedPieceIndex(moveGen.previous()) > ChessConstants.PAWN
				&& !MaterialUtil.containsMajorPieces(cb.materialKey)) {
			return EngineConstants.ENDGAME_EXTENSION_DEPTH;
		}
		/* check-extension */
		if (EngineConstants.ENABLE_CHECK_EXTENSION && cb.checkingPieces != 0) {
			return 1;
		}
		return 0;
	}
}
