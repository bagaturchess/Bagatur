package bagaturchess.bitboard.impl1.internal;

import java.security.SecureRandom;

public class Zobrist {

	public static long sideToMove;
	public static final long[] castling = new long[16];
	public static final long[] epIndex = new long[48];
	public static final long[][][] piece = new long[64][2][7];

	static {
		SecureRandom r = new SecureRandom();
		for (int bitIndex = 0; bitIndex < 64; bitIndex++) {
			for (int colorIndex = 0; colorIndex < piece[0].length; colorIndex++) {
				for (int pieceIndex = 0; pieceIndex < piece[0][0].length; pieceIndex++) {
					piece[bitIndex][colorIndex][pieceIndex] = r.nextLong();
				}
			}
		}
		for (int i = 0; i < castling.length; i++) {
			castling[i] = r.nextLong();
		}

		// skip first item: contains only zeros, default value and has no effect when xorring
		for (int i = 1; i < epIndex.length; i++) {
			epIndex[i] = r.nextLong();
		}
		sideToMove = r.nextLong();
	}

}
