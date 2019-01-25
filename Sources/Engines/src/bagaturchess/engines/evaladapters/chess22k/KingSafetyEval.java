package bagaturchess.engines.evaladapters.chess22k;


import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BISHOP;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BLACK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.KING;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.NIGHT;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.PAWN;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.QUEEN;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.ROOK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.WHITE;


public class KingSafetyEval {

	public static int calculateKingSafetyScores(final IChessBoard cb) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			int counter = EvalConstants.KS_RANK[(7 * kingColor) + ChessConstants.COLOR_FACTOR[kingColor] * cb.getKingIndex(kingColor) / 8];

			counter += EvalConstants.KS_NO_FRIENDS[Long.bitCount(cb.getKingArea(kingColor) & ~cb.getFriendlyPieces(kingColor))];
			counter += openFiles(cb, kingColor, cb.getPieces(kingColor, PAWN));

			// king can move?
			if ((cb.getEvalInfo().attacks[kingColor][KING] & ~cb.getFriendlyPieces(kingColor)) == 0) {
				counter++;
			}
			counter += EvalConstants.KS_ATTACKS[Long.bitCount(cb.getKingArea(kingColor) & cb.getEvalInfo().attacksAll[enemyColor])];
			counter += checks(cb, kingColor);

			counter += EvalConstants.KS_DOUBLE_ATTACKS[Long
					.bitCount(StaticMoves.KING_MOVES[cb.getKingIndex(kingColor)] & cb.getEvalInfo().doubleAttacks[enemyColor] & ~cb.getEvalInfo().attacks[kingColor][PAWN])];

			if ((cb.getCheckingPieces() & cb.getFriendlyPieces(enemyColor)) != 0) {
				counter++;
			}

			// bonus for stm
			counter += 1 - cb.getColorToMove() ^ enemyColor;

			// bonus if there are discovered checks possible
			counter += Long.bitCount(cb.getDiscoveredPieces() & cb.getFriendlyPieces(enemyColor)) * 2;

			// pinned at first rank
			if ((cb.getPinnedPieces() & Bitboard.RANK_FIRST[kingColor]) != 0) {
				counter++;
			}

			if (cb.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
				// bonus for small king-queen distance
				if ((cb.getEvalInfo().attacksAll[kingColor] & cb.getPieces(enemyColor, QUEEN)) == 0) {
					counter += EvalConstants.KS_QUEEN_TROPISM[Util.getDistance(cb.getKingIndex(kingColor),
							Long.numberOfTrailingZeros(cb.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += EvalConstants.KS_ATTACK_PATTERN[cb.getEvalInfo().kingAttackersFlag[enemyColor]];
			score += ChessConstants.COLOR_FACTOR[enemyColor] * EvalConstants.KS_SCORES[Math.min(counter, EvalConstants.KS_SCORES.length - 1)];
		}

		return score;
	}

	private static int openFiles(final IChessBoard cb, final int kingColor, final long pawns) {

		if (cb.getPieces(1 - kingColor, QUEEN) == 0) {
			return 0;
		}
		if (Long.bitCount(cb.getPieces(1 - kingColor, ROOK)) < 2) {
			return 0;
		}

		if ((Bitboard.RANK_FIRST[kingColor] & cb.getPieces(kingColor, KING)) != 0) {
			if ((Bitboard.KING_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((Bitboard.FILE_G & pawns) == 0 || (Bitboard.FILE_H & pawns) == 0) {
					return EvalConstants.KS_OTHER[2];
				}
			} else if ((Bitboard.QUEEN_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((Bitboard.FILE_A & pawns) == 0 || (Bitboard.FILE_B & pawns) == 0) {
					return EvalConstants.KS_OTHER[2];
				}
			}
		}
		return 0;
	}

	private static int checks(final IChessBoard cb, final int kingColor) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.getKingIndex(kingColor);
		final long possibleSquares = ~cb.getFriendlyPieces(enemyColor)
				& (~StaticMoves.KING_MOVES[kingIndex] | StaticMoves.KING_MOVES[kingIndex] & cb.getEvalInfo().doubleAttacks[enemyColor] & ~cb.getEvalInfo().doubleAttacks[kingColor]);

		int counter = checkNight(cb, kingColor, StaticMoves.KNIGHT_MOVES[kingIndex] & possibleSquares & cb.getEvalInfo().attacks[enemyColor][NIGHT]);

		long moves;
		long queenMoves = 0;
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, BISHOP)) != 0) {
			moves = MagicUtil.getBishopMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(cb, kingColor, moves & cb.getEvalInfo().attacks[enemyColor][BISHOP]);
		}
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) != 0) {
			moves = MagicUtil.getRookMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(cb, kingColor, moves & cb.getEvalInfo().attacks[enemyColor][ROOK]);
		}

		if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
			counter += safeCheckQueen(cb, kingColor, queenMoves & ~cb.getEvalInfo().attacksAll[kingColor] & cb.getEvalInfo().attacks[enemyColor][QUEEN]);
			counter += safeCheckQueenTouch(cb, kingColor);
		}

		return counter;
	}

	private static int safeCheckQueenTouch(final IChessBoard cb, final int kingColor) {
		if ((cb.getEvalInfo().kingAttackersFlag[1 - kingColor] & SchroderUtil.FLAG_QUEEN) == 0) {
			return 0;
		}
		final int enemyColor = 1 - kingColor;
		if ((StaticMoves.KING_MOVES[cb.getKingIndex(kingColor)] & ~cb.getFriendlyPieces(enemyColor) & cb.getEvalInfo().attacks[enemyColor][QUEEN] & ~cb.getEvalInfo().doubleAttacks[kingColor]
				& cb.getEvalInfo().doubleAttacks[enemyColor]) != 0) {
			return EvalConstants.KS_OTHER[0];
		}
		return 0;
	}

	private static int safeCheckQueen(final IChessBoard cb, final int kingColor, final long safeQueenMoves) {
		if (safeQueenMoves != 0) {
			return EvalConstants.KS_CHECK_QUEEN[Long.bitCount(cb.getFriendlyPieces(kingColor))];
		}

		return 0;
	}

	private static int checkRook(final IChessBoard cb, final int kingColor, final long rookMoves) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~cb.getEvalInfo().attacksAll[kingColor]) != 0) {
			counter += EvalConstants.KS_CHECK[ROOK];

			// last rank?
			if (kingBlockedAtLastRank(kingColor, cb, StaticMoves.KING_MOVES[cb.getKingIndex(kingColor)] & cb.getEmptySpaces() & ~cb.getEvalInfo().attacksAll[1 - kingColor])) {
				counter += EvalConstants.KS_OTHER[1];
			}

		} else {
			counter += EvalConstants.KS_UCHECK[ROOK];
		}

		return counter;
	}

	private static int checkBishop(final IChessBoard cb, final int kingColor, final long bishopMoves) {
		if (bishopMoves != 0) {
			if ((bishopMoves & ~cb.getEvalInfo().attacksAll[kingColor]) != 0) {
				return EvalConstants.KS_CHECK[BISHOP];
			} else {
				return EvalConstants.KS_UCHECK[BISHOP];
			}
		}
		return 0;
	}

	private static int checkNight(final IChessBoard cb, final int kingColor, final long nightMoves) {
		if (nightMoves != 0) {
			if ((nightMoves & ~cb.getEvalInfo().attacksAll[kingColor]) != 0) {
				return EvalConstants.KS_CHECK[NIGHT];
			} else {
				return EvalConstants.KS_UCHECK[NIGHT];
			}
		}
		return 0;
	}

	private static boolean kingBlockedAtLastRank(final int kingColor, final IChessBoard cb, final long safeKingMoves) {
		return (Bitboard.RANKS[7 * kingColor] & cb.getPieces(kingColor, KING)) != 0 && (safeKingMoves & Bitboard.RANKS[7 * kingColor]) == safeKingMoves;
	}

}
