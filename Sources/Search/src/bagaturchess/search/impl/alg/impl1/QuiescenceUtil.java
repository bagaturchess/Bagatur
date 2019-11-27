package bagaturchess.search.impl.alg.impl1;

import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.search.api.IEvaluator;


public class QuiescenceUtil {

	private static final int FUTILITY_MARGIN = 200;

	public static int calculateBestMove(IEvaluator evaluator, final ChessBoard cb, final MoveGenerator moveGen, int alpha, final int beta) {

		/* stand-pat check */
		int eval = Util.SHORT_MIN;
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
					&& eval + FUTILITY_MARGIN + EvalConstants.MATERIAL[MoveUtil.getAttackedPieceIndex(move)] < alpha) {
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

			final int score = -calculateBestMove(evaluator, cb, moveGen, -beta, -alpha);

			cb.undoMove(move);

			if (score >= beta) {
				moveGen.endPly();
				return score;
			}
			alpha = Math.max(alpha, score);
		}

		moveGen.endPly();
		return alpha;
	}
}
