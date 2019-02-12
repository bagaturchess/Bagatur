package bagaturchess.engines.evaladapters.chess22k;


import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BLACK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.ROOK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.WHITE;


public class PassedPawnEval {

	public static int calculatePassedPawnScores(final IChessBoard cb) {

		int score = 0;

		int whitePromotionDistance = Util.SHORT_MAX;
		int blackPromotionDistance = Util.SHORT_MAX;

		// white passed pawns
		long passedPawns = cb.getEvalInfo().passedPawnsAndOutposts & cb.getPieces(WHITE, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			score += getPassedPawnScore(cb, index, WHITE);

			if (whitePromotionDistance == Util.SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~Bitboard.FILES[index & 7];
		}

		// black passed pawns
		passedPawns = cb.getEvalInfo().passedPawnsAndOutposts & cb.getPieces(BLACK, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			score -= getPassedPawnScore(cb, index, BLACK);

			if (blackPromotionDistance == Util.SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~Bitboard.FILES[index & 7];
		}

		if (whitePromotionDistance < blackPromotionDistance - 1) {
			score += 350;
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			score -= 350;
		}

		return score;
	}

	private static int getPassedPawnScore(final IChessBoard cb, final int index, final int color) {

		final int nextIndex = index + ChessConstants.COLOR_FACTOR_8[color];
		final long square = Util.POWER_LOOKUP[index];
		final long maskNextSquare = Util.POWER_LOOKUP[nextIndex];
		final long maskPreviousSquare = Util.POWER_LOOKUP[index - ChessConstants.COLOR_FACTOR_8[color]];
		final long maskFile = Bitboard.FILES[index & 7];
		final int enemyColor = 1 - color;
		float multiplier = 1;

		// is piece blocked?
		if ((cb.getAllPieces() & maskNextSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[0];
		}

		// is next squared attacked?
		if ((cb.getEvalInfo().attacksAll[enemyColor] & maskNextSquare) == 0) {

			// complete path free of enemy attacks?
			if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & cb.getEvalInfo().attacksAll[enemyColor]) == 0) {
				multiplier *= EvalConstants.PASSED_MULTIPLIERS[7];
			} else {
				multiplier *= EvalConstants.PASSED_MULTIPLIERS[1];
			}
		}

		// is next squared defended?
		if ((cb.getEvalInfo().attacksAll[color] & maskNextSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[3];
		}

		// is enemy king in front?
		if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & cb.getPieces(enemyColor, ChessConstants.KING)) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (cb.getColorToMove() != color && (cb.getEvalInfo().attacksAll[enemyColor] & square) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & cb.getPieces(color, ROOK)) != 0 && (cb.getEvalInfo().attacks[color][ROOK] & square) != 0 && (cb.getEvalInfo().attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & cb.getPieces(enemyColor, ROOK)) != 0 && (cb.getEvalInfo().attacks[enemyColor][ROOK] & square) != 0
				&& (cb.getEvalInfo().attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= EvalConstants.PASSED_KING_MULTI[Util.getDistance(cb.getKingIndex(color), index)];
		multiplier *= EvalConstants.PASSED_KING_MULTI[8 - Util.getDistance(cb.getKingIndex(enemyColor), index)];

		final int scoreIndex = (7 * color) + ChessConstants.COLOR_FACTOR[color] * index / 8;
		return EvalUtil.score((int) (EvalConstants.PASSED_SCORE_MG[scoreIndex] * multiplier), (int) (EvalConstants.PASSED_SCORE_EG[scoreIndex] * multiplier));
	}

	private static int getBlackPromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = index >>> 3;
		if (promotionDistance == 1 && cb.getColorToMove() == BLACK) {
			if ((Util.POWER_LOOKUP[index - 8] & (cb.getEvalInfo().attacksAll[WHITE] | cb.getAllPieces())) == 0) {
				if ((Util.POWER_LOOKUP[index] & cb.getEvalInfo().attacksAll[WHITE]) == 0) {
					return 1;
				}
			}
		}
		return Util.SHORT_MAX;
	}

	private static int getWhitePromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = 7 - index / 8;
		if (promotionDistance == 1 && cb.getColorToMove() == WHITE) {
			if ((Util.POWER_LOOKUP[index + 8] & (cb.getEvalInfo().attacksAll[BLACK] | cb.getAllPieces())) == 0) {
				if ((Util.POWER_LOOKUP[index] & cb.getEvalInfo().attacksAll[BLACK]) == 0) {
					return 1;
				}
			}
		}
		return Util.SHORT_MAX;
	}

}
