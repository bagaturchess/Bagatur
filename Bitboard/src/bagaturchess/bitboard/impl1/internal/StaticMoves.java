package bagaturchess.bitboard.impl1.internal;

import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;


public class StaticMoves {

	public static final long[] KNIGHT_MOVES = new long[64];
	public static final long[] KING_MOVES = new long[64];

	public static final long[][] PAWN_ATTACKS = new long[2][64];

	// PAWN
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {
			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// attacks
				if (newPosition == currentPosition + 7 && newPosition % 8 != 7) {
					PAWN_ATTACKS[WHITE][currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition + 9 && newPosition % 8 != 0 ) {
					PAWN_ATTACKS[WHITE][currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 7 && newPosition % 8 != 0) {
					PAWN_ATTACKS[BLACK][currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 9 && newPosition % 8 != 7) {
					PAWN_ATTACKS[BLACK][currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
			}
		}
	}

	// knight
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {

			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// check if newPosition is a correct move
				if (isKnightMove(currentPosition, newPosition)) {
					KNIGHT_MOVES[currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
			}
		}
	}

	// king
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {
			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// check if newPosition is a correct move
				if (isKingMove(currentPosition, newPosition)) {
					KING_MOVES[currentPosition] |= Util.POWER_LOOKUP[newPosition];
				}
			}
		}
	}

	private static boolean isKnightMove(int currentPosition, int newPosition) {
		if (currentPosition / 8 - newPosition / 8 == 1) {
			return currentPosition - 10 == newPosition || currentPosition - 6 == newPosition;
		}
		if (newPosition / 8 - currentPosition / 8 == 1) {
			return currentPosition + 10 == newPosition || currentPosition + 6 == newPosition;
		}
		if (currentPosition / 8 - newPosition / 8 == 2) {
			return currentPosition - 17 == newPosition || currentPosition - 15 == newPosition;
		}
		if (newPosition / 8 - currentPosition / 8 == 2) {
			return currentPosition + 17 == newPosition || currentPosition + 15 == newPosition;
		}
		return false;
	}

	private static boolean isKingMove(int currentPosition, int newPosition) {
		if (currentPosition / 8 - newPosition / 8 == 0) {
			return currentPosition - newPosition == -1 || currentPosition - newPosition == 1;
		}
		if (currentPosition / 8 - newPosition / 8 == 1) {
			return currentPosition - newPosition == 7 || currentPosition - newPosition == 8 || currentPosition - newPosition == 9;
		}
		if (currentPosition / 8 - newPosition / 8 == -1) {
			return currentPosition - newPosition == -7 || currentPosition - newPosition == -8 || currentPosition - newPosition == -9;
		}
		return false;
	}

}
