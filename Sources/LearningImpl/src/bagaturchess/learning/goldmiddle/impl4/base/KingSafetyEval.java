package bagaturchess.learning.goldmiddle.impl4.base;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;
import bagaturchess.bitboard.impl1.internal.Util;


public class KingSafetyEval {

	public static int calculateScores(final ChessBoard cb, final EvalInfo evalInfo) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			final int kingIndex = cb.kingIndex[kingColor];
			int counter = 0;

			counter += EvalConstants.KS_NO_FRIENDS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & ~evalInfo.getFriendlyPieces(kingColor))];
			counter += EvalConstants.KS_ATTACKS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & evalInfo.attacksAll[enemyColor])];

			counter += EvalConstants.KS_DOUBLE_ATTACKS[Long
					.bitCount(StaticMoves.KING_MOVES[kingIndex] & evalInfo.doubleAttacks[enemyColor] & ~evalInfo.attacks[kingColor][PAWN])];

			counter += checks(cb, kingColor, evalInfo);

			// bonus for stm
			counter += 1 - cb.colorToMove ^ enemyColor;

			// bonus if there are discovered checks possible
			if (cb.discoveredPieces != 0) {
				counter += Long.bitCount(cb.discoveredPieces & evalInfo.getFriendlyPieces(enemyColor)) * 2;
			}

			if (evalInfo.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else {
				// bonus for small king-queen distance
				if ((evalInfo.attacksAll[kingColor] & evalInfo.getPieces(enemyColor, QUEEN)) == 0) {
					counter += EvalConstants.KS_QUEEN_TROPISM[Util.getDistance(kingIndex, Long.numberOfTrailingZeros(evalInfo.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += EvalConstants.KS_ATTACK_PATTERN[evalInfo.kingAttackersFlag[enemyColor]];
			score += ChessConstants.COLOR_FACTOR[enemyColor] * EvalConstants.KS_SCORES[Math.min(counter, EvalConstants.KS_SCORES.length - 1)];
		}

		return score;
	}

	private static int checks(final ChessBoard cb, final int kingColor, final EvalInfo evalInfo) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.kingIndex[kingColor];
		final long notAttacked = ~evalInfo.attacksAll[kingColor];
		final long possibleSquares = ~evalInfo.getFriendlyPieces(enemyColor)
				& (~StaticMoves.KING_MOVES[kingIndex] | evalInfo.doubleAttacks[enemyColor] & ~evalInfo.doubleAttacks[kingColor]);

		int counter = 0;
		if (evalInfo.getPieces(enemyColor, NIGHT) != 0) {
			counter += checkNight(notAttacked, StaticMoves.KNIGHT_MOVES[kingIndex] & possibleSquares & evalInfo.attacks[enemyColor][NIGHT]);
		}

		long moves;
		long queenMoves = 0;
		if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, BISHOP)) != 0) {
			moves = MagicUtil.getBishopMoves(kingIndex, evalInfo.bb_all ^ evalInfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(notAttacked, moves & evalInfo.attacks[enemyColor][BISHOP]);
		}
		if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, ROOK)) != 0) {
			moves = MagicUtil.getRookMoves(kingIndex, evalInfo.bb_all ^ evalInfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(cb, kingColor, moves & evalInfo.attacks[enemyColor][ROOK], evalInfo);
		}

		queenMoves &= evalInfo.attacks[enemyColor][QUEEN];
		if (queenMoves != 0) {

			// safe check queen
			if ((queenMoves & notAttacked) != 0) {
				counter += EvalConstants.KS_CHECK_QUEEN[Long.bitCount(evalInfo.getFriendlyPieces(kingColor))];
			}
			// safe check queen touch
			if ((queenMoves & StaticMoves.KING_MOVES[kingIndex]) != 0) {
				counter += EvalConstants.KS_OTHER[0];
			}
		}

		return counter;
	}

	private static int checkRook(final ChessBoard cb, final int kingColor, final long rookMoves, final EvalInfo evalInfo) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~evalInfo.attacksAll[kingColor]) != 0) {
			counter += EvalConstants.KS_OTHER[2];

			// last rank?
			if (kingBlockedAtLastRank(StaticMoves.KING_MOVES[cb.kingIndex[kingColor]] & evalInfo.bb_free & ~evalInfo.attacksAll[1 - kingColor])) {
				counter += EvalConstants.KS_OTHER[1];
			}

		} else {
			counter += EvalConstants.KS_OTHER[3];
		}

		return counter;
	}

	private static int checkBishop(final long safeSquares, final long bishopMoves) {
		if (bishopMoves == 0) {
			return 0;
		}
		if ((bishopMoves & safeSquares) == 0) {
			return EvalConstants.KS_OTHER[3];
		} else {
			return EvalConstants.KS_OTHER[2];
		}
	}

	private static int checkNight(final long safeSquares, final long nightMoves) {
		if (nightMoves == 0) {
			return 0;
		}
		if ((nightMoves & safeSquares) == 0) {
			return EvalConstants.KS_OTHER[3];
		} else {
			return EvalConstants.KS_OTHER[2];
		}
	}

	private static boolean kingBlockedAtLastRank(final long safeKingMoves) {
		return (Bitboard.RANK_234567 & safeKingMoves) == 0;
	}

}
