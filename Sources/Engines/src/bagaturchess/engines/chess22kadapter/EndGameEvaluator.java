package bagaturchess.engines.chess22kadapter;


import static bagaturchess.engines.chess22kadapter.ChessConstants.BISHOP;
import static bagaturchess.engines.chess22kadapter.ChessConstants.BLACK;
import static bagaturchess.engines.chess22kadapter.ChessConstants.KING;
import static bagaturchess.engines.chess22kadapter.ChessConstants.PAWN;
import static bagaturchess.engines.chess22kadapter.ChessConstants.QUEEN;
import static bagaturchess.engines.chess22kadapter.ChessConstants.WHITE;


class EndGameEvaluator {

	public static int getDrawishScore(final IChessBoard cb, final int color) {
		// TODO KRKN: night close to king in the center?
		// TODO KRKB?
		return Bitboard.manhattanCenterDistance(cb.getKingIndex(1 - color)) * 10;
	}

	public static int calculateKBKNScore(final IChessBoard cb) {
		if (Long.bitCount(cb.getFriendlyPieces(WHITE)) > 1) {
			return 1000 + calculateKBKNScore(cb, WHITE);
		}
		return -1000 - calculateKBKNScore(cb, BLACK);
	}

	private static int calculateKBKNScore(final IChessBoard cb, final int color) {
		if ((cb.getPieces(color, BISHOP) & Bitboard.WHITE_SQUARES) != 0) {
			return Bitboard.manhattanCenterDistance(cb.getKingIndex(1 - color)) * 10 * ((Bitboard.WHITE_CORNERS & cb.getPieces(1 - color, KING)) != 0 ? 4 : -1);
		}
		return Bitboard.manhattanCenterDistance(cb.getKingIndex(1 - color)) * 10 * ((Bitboard.BLACK_CORNERS & cb.getPieces(1 - color, KING)) != 0 ? 4 : -1);
	}

	public static boolean isKBPKDraw(final IChessBoard cb) {

		if (cb.getPieces(WHITE, PAWN) != 0) {
			if ((cb.getPieces(WHITE, PAWN) & Bitboard.FILE_A) != 0 && (Bitboard.WHITE_SQUARES & cb.getPieces(WHITE, BISHOP)) == 0) {
				return (cb.getPieces(BLACK, KING) & Bitboard.A7B7A8B8) != 0;
			} else if ((cb.getPieces(WHITE, PAWN) & Bitboard.FILE_H) != 0 && (Bitboard.BLACK_SQUARES & cb.getPieces(WHITE, BISHOP)) == 0) {
				return (cb.getPieces(BLACK ,KING) & Bitboard.G7H7G8H8) != 0;
			}
		} else {
			if ((cb.getPieces(BLACK, PAWN) & Bitboard.FILE_A) != 0 && (Bitboard.BLACK_SQUARES & cb.getPieces(BLACK, BISHOP)) == 0) {
				return (cb.getPieces(WHITE, KING) & Bitboard.A1B1A2B2) != 0;
			} else if ((cb.getPieces(BLACK ,PAWN) & Bitboard.FILE_H) != 0 && (Bitboard.WHITE_SQUARES & cb.getPieces(BLACK, BISHOP)) == 0) {
				return (cb.getPieces(WHITE ,KING) & Bitboard.G1H1G2H2) != 0;
			}
		}

		return false;
	}

	public static boolean isKQKPDraw(final IChessBoard cb) {
		final int leadingColor = cb.getPieces(WHITE, QUEEN) != 0 ? WHITE : BLACK;
		final long ranks12 = leadingColor == WHITE ? Bitboard.RANK_12 : Bitboard.RANK_78;
		final long pawn = cb.getPieces(1 - leadingColor, PAWN);
		long pawnZone;

		if ((Bitboard.FILE_A & pawn) != 0) {
			pawnZone = Bitboard.FILE_ABC & ranks12;
		} else if ((Bitboard.FILE_C & pawn) != 0) {
			pawnZone = Bitboard.FILE_ABC & ranks12;
		} else if ((Bitboard.FILE_F & pawn) != 0) {
			pawnZone = Bitboard.FILE_FGH & ranks12;
		} else if ((Bitboard.FILE_H & pawn) != 0) {
			pawnZone = Bitboard.FILE_FGH & ranks12;
		} else {
			return false;
		}

		if ((pawn & pawnZone) == 0) {
			return false;
		}

		if ((pawnZone & cb.getPieces(1 - leadingColor, KING)) != 0) {
			if (Util.getDistance(cb.getKingIndex(leadingColor), Long.numberOfTrailingZeros(pawn)) >= 4) {
				return true;
			}
		}

		return false;
	}

}
