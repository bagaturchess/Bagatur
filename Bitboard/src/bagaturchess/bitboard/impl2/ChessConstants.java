package bagaturchess.bitboard.impl2;


public class ChessConstants {

	public static final int EMPTY = 0;
	public static final int PAWN = 1;
	public static final int KNIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;

	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	public static final long[] POWER_LOOKUP = new long[64];
	static {
		for (int i = 0; i < 64; i++) {
			POWER_LOOKUP[i] = 1L << i;
		}
	}
	
	public static final long[] KNIGHT_MOVES = new long[64];
	public static final long[] KING_MOVES = new long[64];

	public static final long[][] PAWN_ATTACKS = new long[2][64];

	//Pawns
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {
			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// attacks
				if (newPosition == currentPosition + 7 && newPosition % 8 != 7) {
					PAWN_ATTACKS[WHITE][currentPosition] |= POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition + 9 && newPosition % 8 != 0 ) {
					PAWN_ATTACKS[WHITE][currentPosition] |= POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 7 && newPosition % 8 != 0) {
					PAWN_ATTACKS[BLACK][currentPosition] |= POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 9 && newPosition % 8 != 7) {
					PAWN_ATTACKS[BLACK][currentPosition] |= POWER_LOOKUP[newPosition];
				}
			}
		}
	}

	//Knight
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {

			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// check if newPosition is a correct move
				if (isKnightMove(currentPosition, newPosition)) {
					KNIGHT_MOVES[currentPosition] |= POWER_LOOKUP[newPosition];
				}
			}
		}
	}

	//King
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {
			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// check if newPosition is a correct move
				if (isKingMove(currentPosition, newPosition)) {
					KING_MOVES[currentPosition] |= POWER_LOOKUP[newPosition];
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
	
	public static final long[][] KING_AREA = new long[2][64];

	public static final long[][] IN_BETWEEN = new long[64][64];
	/** pinned-piece index, king index */
	public static final long[][] PINNED_MOVEMENT = new long[64][64];

	static {
		int i;

		// fill from->to where to > from
		for (int from = 0; from < 64; from++) {
			for (int to = from + 1; to < 64; to++) {

				// horizontal
				if (from / 8 == to / 8) {
					i = to - 1;
					while (i > from) {
						IN_BETWEEN[from][to] |= POWER_LOOKUP[i];
						i--;
					}
				}

				// vertical
				if (from % 8 == to % 8) {
					i = to - 8;
					while (i > from) {
						IN_BETWEEN[from][to] |= POWER_LOOKUP[i];
						i -= 8;
					}
				}
			}
		}

		// fill from->to where to < from
		for (int from = 0; from < 64; from++) {
			for (int to = 0; to < from; to++) {
				IN_BETWEEN[from][to] = IN_BETWEEN[to][from];
			}
		}
	}

	static {
		int i;

		// fill from->to where to > from
		for (int from = 0; from < 64; from++) {
			for (int to = from + 1; to < 64; to++) {

				// diagonal \
				if ((to - from) % 9 == 0 && to % 8 > from % 8) {
					i = to - 9;
					while (i > from) {
						IN_BETWEEN[from][to] |= POWER_LOOKUP[i];
						i -= 9;
					}
				}

				// diagonal /
				if ((to - from) % 7 == 0 && to % 8 < from % 8) {
					i = to - 7;
					while (i > from) {
						IN_BETWEEN[from][to] |= POWER_LOOKUP[i];
						i -= 7;
					}
				}
			}
		}

		// fill from->to where to < from
		for (int from = 0; from < 64; from++) {
			for (int to = 0; to < from; to++) {
				IN_BETWEEN[from][to] = IN_BETWEEN[to][from];
			}
		}
	}

	static {
		int[] DIRECTION = { -1, -7, -8, -9, 1, 7, 8, 9 };
		// PINNED MOVEMENT, x-ray from the king to the pinned-piece and beyond
		for (int pinnedPieceIndex = 0; pinnedPieceIndex < 64; pinnedPieceIndex++) {
			for (int kingIndex = 0; kingIndex < 64; kingIndex++) {
				int correctDirection = 0;
				for (int direction : DIRECTION) {
					if (correctDirection != 0) {
						break;
					}
					int xray = kingIndex + direction;
					while (xray >= 0 && xray < 64) {
						if (direction == -1 || direction == -9 || direction == 7) {
							if ((xray & 7) == 7) {
								break;
							}
						}
						if (direction == 1 || direction == 9 || direction == -7) {
							if ((xray & 7) == 0) {
								break;
							}
						}
						if (xray == pinnedPieceIndex) {
							correctDirection = direction;
							break;
						}
						xray += direction;
					}
				}

				if (correctDirection != 0) {
					int xray = kingIndex + correctDirection;
					while (xray >= 0 && xray < 64) {
						if (correctDirection == -1 || correctDirection == -9 || correctDirection == 7) {
							if ((xray & 7) == 7) {
								break;
							}
						}
						if (correctDirection == 1 || correctDirection == 9 || correctDirection == -7) {
							if ((xray & 7) == 0) {
								break;
							}
						}
						PINNED_MOVEMENT[pinnedPieceIndex][kingIndex] |= POWER_LOOKUP[xray];
						xray += correctDirection;
					}
				}
			}
		}
	}

	static {
		// fill king-safety masks:
		//
		// UUU front-further
		// FFF front
		// NKN next
		// BBB behind
		//
		for (int i = 0; i < 64; i++) {
			// NEXT
			KING_AREA[WHITE][i] |= KING_MOVES[i] | POWER_LOOKUP[i];
			KING_AREA[BLACK][i] |= KING_MOVES[i] | POWER_LOOKUP[i];

			if (i > 15) {
				KING_AREA[BLACK][i] |= KING_MOVES[i] >>> 8;
			}

			if (i < 48) {
				KING_AREA[WHITE][i] |= KING_MOVES[i] << 8;
			}
		}

		// always 3 wide, even at file 1 and 8
		for (int i = 0; i < 64; i++) {
			for (int color = 0; color < 2; color++) {
				if (i % 8 == 0) {
					KING_AREA[color][i] |= KING_AREA[color][i + 1];
				} else if (i % 8 == 7) {
					KING_AREA[color][i] |= KING_AREA[color][i - 1];
				}
			}
		}

		// always 4 long
		for (int i = 0; i < 64; i++) {
			if (i < 8) {
				KING_AREA[WHITE][i] = KING_AREA[WHITE][i + 8];
			} else if (i > 47) {
				if (i > 55) {
					KING_AREA[WHITE][i] = KING_AREA[WHITE][i - 16];
				} else {
					KING_AREA[WHITE][i] = KING_AREA[WHITE][i - 8];
				}
			}
		}
		for (int i = 0; i < 64; i++) {
			if (i > 55) {
				KING_AREA[BLACK][i] = KING_AREA[BLACK][i - 8];
			} else if (i < 16) {
				if (i < 8) {
					KING_AREA[BLACK][i] = KING_AREA[BLACK][i + 16];
				} else {
					KING_AREA[BLACK][i] = KING_AREA[BLACK][i + 8];
				}
			}
		}
	}
	
	public static final int[] COLOR_FACTOR 		= { 1, -1 };
	public static final int[] COLOR_FACTOR_8 	= { 8, -8 };
	
	public static final int[] MATERIAL_SEE		= {0, 100, 300, 300, 500, 900, 3000};
	
	public static final int[] PROMOTION_SCORE_SEE = {
		0,
		0,
		MATERIAL_SEE[ChessConstants.KNIGHT] 	- MATERIAL_SEE[ChessConstants.PAWN],
		MATERIAL_SEE[ChessConstants.BISHOP] - MATERIAL_SEE[ChessConstants.PAWN],
		MATERIAL_SEE[ChessConstants.ROOK] 	- MATERIAL_SEE[ChessConstants.PAWN],
		MATERIAL_SEE[ChessConstants.QUEEN] 	- MATERIAL_SEE[ChessConstants.PAWN],
};
}
