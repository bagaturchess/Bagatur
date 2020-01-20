package bagaturchess.learning.goldmiddle.impl4;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
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

	public static int calculateScores(final ChessBoard cb) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((cb.pieces[enemyColor][QUEEN] | cb.pieces[enemyColor][ROOK]) == 0) {
				continue;
			}

			final int kingIndex = cb.kingIndex[kingColor];
			int counter = 0;

			counter += EvalConstants.KS_NO_FRIENDS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & ~cb.friendlyPieces[kingColor])];
			counter += EvalConstants.KS_ATTACKS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & cb.attacksAll[enemyColor])];

			counter += EvalConstants.KS_DOUBLE_ATTACKS[Long
					.bitCount(StaticMoves.KING_MOVES[kingIndex] & cb.doubleAttacks[enemyColor] & ~cb.attacks[kingColor][PAWN])];

			counter += checks(cb, kingColor);

			// bonus for stm
			counter += 1 - cb.colorToMove ^ enemyColor;

			// bonus if there are discovered checks possible
			if (cb.discoveredPieces != 0) {
				counter += Long.bitCount(cb.discoveredPieces & cb.friendlyPieces[enemyColor]) * 2;
			}

			if (cb.pieces[enemyColor][QUEEN] == 0) {
				counter /= 2;
			} else {
				// bonus for small king-queen distance
				if ((cb.attacksAll[kingColor] & cb.pieces[enemyColor][QUEEN]) == 0) {
					counter += EvalConstants.KS_QUEEN_TROPISM[Util.getDistance(kingIndex, Long.numberOfTrailingZeros(cb.pieces[enemyColor][QUEEN]))];
				}
			}

			counter += EvalConstants.KS_ATTACK_PATTERN[cb.kingAttackersFlag[enemyColor]];
			score += ChessConstants.COLOR_FACTOR[enemyColor] * EvalConstants.KS_SCORES[Math.min(counter, EvalConstants.KS_SCORES.length - 1)];
		}

		return score;
	}

	private static int checks(final ChessBoard cb, final int kingColor) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.kingIndex[kingColor];
		final long notAttacked = ~cb.attacksAll[kingColor];
		final long possibleSquares = ~cb.friendlyPieces[enemyColor]
				& (~StaticMoves.KING_MOVES[kingIndex] | cb.doubleAttacks[enemyColor] & ~cb.doubleAttacks[kingColor]);

		int counter = 0;
		if (cb.pieces[enemyColor][NIGHT] != 0) {
			counter += checkNight(notAttacked, StaticMoves.KNIGHT_MOVES[kingIndex] & possibleSquares & cb.attacks[enemyColor][NIGHT]);
		}

		long moves;
		long queenMoves = 0;
		if ((cb.pieces[enemyColor][QUEEN] | cb.pieces[enemyColor][BISHOP]) != 0) {
			moves = MagicUtil.getBishopMoves(kingIndex, cb.allPieces ^ cb.pieces[kingColor][QUEEN]) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(notAttacked, moves & cb.attacks[enemyColor][BISHOP]);
		}
		if ((cb.pieces[enemyColor][QUEEN] | cb.pieces[enemyColor][ROOK]) != 0) {
			moves = MagicUtil.getRookMoves(kingIndex, cb.allPieces ^ cb.pieces[kingColor][QUEEN]) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(cb, kingColor, moves & cb.attacks[enemyColor][ROOK]);
		}

		queenMoves &= cb.attacks[enemyColor][QUEEN];
		if (queenMoves != 0) {

			// safe check queen
			if ((queenMoves & notAttacked) != 0) {
				counter += EvalConstants.KS_CHECK_QUEEN[Long.bitCount(cb.friendlyPieces[kingColor])];
			}
			// safe check queen touch
			if ((queenMoves & StaticMoves.KING_MOVES[kingIndex]) != 0) {
				counter += EvalConstants.KS_OTHER[0];
			}
		}

		return counter;
	}

	private static int checkRook(final ChessBoard cb, final int kingColor, final long rookMoves) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~cb.attacksAll[kingColor]) != 0) {
			counter += EvalConstants.KS_OTHER[2];

			// last rank?
			if (kingBlockedAtLastRank(StaticMoves.KING_MOVES[cb.kingIndex[kingColor]] & cb.emptySpaces & ~cb.attacksAll[1 - kingColor])) {
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
