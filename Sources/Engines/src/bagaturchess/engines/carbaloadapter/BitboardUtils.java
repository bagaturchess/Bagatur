package bagaturchess.engines.carbaloadapter;


public class BitboardUtils {

	// Board borders
	public static final long b_d = 0x00000000000000ffL; // down
	public static final long b_u = 0xff00000000000000L; // up
	public static final long b_r = 0x0101010101010101L; // right
	public static final long b_l = 0x8080808080808080L; // left

	// Board borders (2 squares),for the knight
	public static final long b2_d = 0x000000000000ffffL; // down
	public static final long b2_u = 0xffff000000000000L; // up
	public static final long b2_r = 0x0303030303030303L; // right
	public static final long b2_l = 0xC0C0C0C0C0C0C0C0L; // left

	public static final long A = b_l;
	public static final long B = b_r << 6;
	public static final long C = b_r << 5;
	public static final long D = b_r << 4;
	public static final long E = b_r << 3;
	public static final long F = b_r << 2;
	public static final long G = b_r << 1;
	public static final long H = b_r;

	// 0 is a, 7 is g
	public static final long[] FILE = {A, B, C, D, E, F, G, H};
	public static final long[] FILES_ADJACENT = { //
			FILE[1], //
			FILE[0] | FILE[2], //
			FILE[1] | FILE[3], //
			FILE[2] | FILE[4], //
			FILE[3] | FILE[5], //
			FILE[4] | FILE[6], //
			FILE[5] | FILE[7], //
			FILE[6] //
	};

	public static final long[] FILES_LEFT = { //
			0, //
			FILE[0], //
			FILE[0] | FILE[1], //
			FILE[0] | FILE[1] | FILE[2], //
			FILE[0] | FILE[1] | FILE[2] | FILE[3], //
			FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4], //
			FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5], //
			FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6] //
	};

	public static final long[] FILES_RIGHT = { //
			FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], //
			FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], //
			FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], //
			FILE[4] | FILE[5] | FILE[6] | FILE[7], //
			FILE[5] | FILE[6] | FILE[7], //
			FILE[6] | FILE[7], //
			FILE[7], //
			0 //
	};

	public static final long R1 = b_d;
	public static final long R2 = b_d << 8;
	public static final long R3 = b_d << 16;
	public static final long R4 = b_d << 24;
	public static final long R5 = b_d << 32;
	public static final long R6 = b_d << 40;
	public static final long R7 = b_d << 48;
	public static final long R8 = b_d << 56;

	public static final long[] RANK = {R1, R2, R3, R4, R5, R6, R7, R8}; // 0 is 1, 7 is 8
	public static final long[] RANKS_UPWARDS = { //
			RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[5] | RANK[6] | RANK[7], //
			RANK[6] | RANK[7], //
			RANK[7], //
			0 //
	};
	public static final long[] RANK_AND_UPWARDS = { //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[4] | RANK[5] | RANK[6] | RANK[7], //
			RANK[5] | RANK[6] | RANK[7], //
			RANK[6] | RANK[7], //
			RANK[7], //
	};
	public static final long[] RANKS_DOWNWARDS = { //
			0, //
			RANK[0], //
			RANK[0] | RANK[1], //
			RANK[0] | RANK[1] | RANK[2], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] //
	};
	public static final long[] RANK_AND_DOWNWARDS = { //
			RANK[0], //
			RANK[0] | RANK[1], //
			RANK[0] | RANK[1] | RANK[2], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6], //
			RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7] //
	};

	// Ranks forward in pawn direction W, B
	public static final long[][] RANKS_FORWARD = {RANKS_UPWARDS, RANKS_DOWNWARDS};
	public static final long[][] RANKS_BACKWARD = {RANKS_DOWNWARDS, RANKS_UPWARDS};
	public static final long[][] RANK_AND_FORWARD = {RANK_AND_UPWARDS, RANK_AND_DOWNWARDS};
	public static final long[][] RANK_AND_BACKWARD = {RANK_AND_DOWNWARDS, RANK_AND_UPWARDS};

	public static final String[] SQUARE_NAMES = changeEndianArray64(new String[] //
			{"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", //
					"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", //
					"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", //
					"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", //
					"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", //
					"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", //
					"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", //
					"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"});

	// To use with square2Index
	public static final byte[] BIT_TABLE = {63, 30, 3, 32, 25, 41, 22, 33, 15, 50, 42, 13, 11, 53, 19, 34, 61, 29, 2, 51, 21, 43, 45, 10, 18, 47, 1, 54, 9, 57,
			0, 35, 62, 31, 40, 4, 49, 5, 52, 26, 60, 6, 23, 44, 46, 27, 56, 16, 7, 39, 48, 24, 59, 14, 12, 55, 38, 28, 58, 20, 37, 17, 36, 8};

	/**
	 * Converts a square to its index 0=H1, 63=A8
	 */
	public static byte square2Index(long square) {
		long b = square ^ (square - 1);
		int fold = (int) (b ^ (b >>> 32));
		return BIT_TABLE[(fold * 0x783a9b23) >>> 26];
	}

	/**
	 * And viceversa
	 */
	public static long index2Square(int index) {
		return Square.H1 << index;
	}

	/**
	 * Changes element 0 with 63 and consecuvely: this way array constants are
	 * more legible
	 */
	public static String[] changeEndianArray64(String sarray[]) {
		String out[] = new String[64];
		for (int i = 0; i < 64; i++) {
			out[i] = sarray[63 - i];
		}
		return out;
	}

	public static int[] changeEndianArray64(int sarray[]) {
		int out[] = new int[64];
		for (int i = 0; i < 64; i++) {
			out[i] = sarray[63 - i];
		}
		return out;
	}

	/**
	 * Prints a BitBoard to standard output
	 */
	public static String toString(long b) {
		StringBuilder sb = new StringBuilder();
		long i = Square.A8;
		while (i != 0) {
			sb.append(((b & i) != 0 ? "1 " : "0 "));
			if ((i & b_r) != 0) {
				sb.append("\n");
			}
			i >>>= 1;
		}
		return sb.toString();
	}

	/**
	 * Flips board vertically
	 * https://chessprogramming.wikispaces.com/Flipping+Mirroring+and+Rotating
	 *
	 * @param in
	 * @return
	 */
	public static long flipVertical(long in) {
		final long k1 = 0x00FF00FF00FF00FFL;
		final long k2 = 0x0000FFFF0000FFFFL;
		in = ((in >>> 8) & k1) | ((in & k1) << 8);
		in = ((in >>> 16) & k2) | ((in & k2) << 16);
		in = (in >>> 32) | (in << 32);
		return in;
	}

	public static int flipHorizontalIndex(int index) {
		return (index & 0xF8) | (7 - (index & 7));
	}

	/**
	 * Counts the number of bits of one long
	 * http://chessprogramming.wikispaces.com/Population+Count
	 *
	 * @param x
	 * @return
	 */
	public static int popCount(long x) {
		if (x == 0) {
			return 0;
		}
		final long k1 = 0x5555555555555555L;
		final long k2 = 0x3333333333333333L;
		final long k4 = 0x0f0f0f0f0f0f0f0fL;
		final long kf = 0x0101010101010101L;
		x = x - ((x >> 1) & k1); // put count of each 2 bits into those 2 bits
		x = (x & k2) + ((x >> 2) & k2); // put count of each 4 bits into those 4
		// bits
		x = (x + (x >> 4)) & k4; // put count of each 8 bits into those 8 bits
		x = (x * kf) >> 56; // returns 8 most significant bits of x + (x<<8) +
		// (x<<16) + (x<<24) + ...
		return (int) x;
	}

	/**
	 * Convert a bitboard square to algebraic notation Number depends of rotated
	 * board.
	 *
	 * @param square
	 * @return
	 */
	public static String square2Algebraic(long square) {
		return SQUARE_NAMES[square2Index(square)];
	}

	public static String index2Algebraic(int index) {
		return SQUARE_NAMES[index];
	}

	public static int algebraic2Index(String name) {
		for (int i = 0; i < 64; i++) {
			if (name.equals(SQUARE_NAMES[i])) {
				return i;
			}
		}
		return -1;
	}

	public static long algebraic2Square(String name) {
		long aux = Square.H1;
		for (int i = 0; i < 64; i++) {
			if (name.equals(SQUARE_NAMES[i])) {
				return aux;
			}
			aux <<= 1;
		}
		return 0;
	}

	/**
	 * Gets the file (0..7) for (a..h) of the square
	 *
	 * @param square
	 * @return
	 */
	public static int getFile(long square) {
		for (int file = 0; file < 8; file++) {
			if ((FILE[file] & square) != 0) {
				return file;
			}
		}
		return 0;
	}

	public static int getRankLsb(long square) {
		for (int rank = 0; rank <= 7; rank++) {
			if ((RANK[rank] & square) != 0) {
				return rank;
			}
		}
		return 0;
	}

	public static int getRankMsb(long square) {
		for (int rank = 7; rank >= 0; rank--) {
			if ((RANK[rank] & square) != 0) {
				return rank;
			}
		}
		return 0;
	}

	public static int getFileOfIndex(int index) {
		return 7 - index & 7;
	}

	public static int getRankOfIndex(int index) {
		return index >> 3;
	}

	/**
	 * Gets a long with the less significant bit of the board
	 */
	public static long lsb(long board) {
		return board & (-board);
	}

	public static long msb(long board) {
		board |= board >>> 32;
		board |= board >>> 16;
		board |= board >>> 8;
		board |= board >>> 4;
		board |= board >>> 2;
		board |= board >>> 1;
		return board == 0 ? 0 : (board >>> 1) + 1;
	}

	/**
	 * Distance between two indexes
	 */
	public static int distance(int index1, int index2) {
		return Math.max(Math.abs((index1 & 7) - (index2 & 7)), Math.abs((index1 >> 3) - (index2 >> 3)));
	}

	/**
	 * Gets the horizontal line between two squares (including the origin and destiny squares)
	 * square1 must be to the left of square2 (square1 must be a higher bit)
	 */
	public static long getHorizontalLine(long square1, long square2) {
		return (square1 | (square1 - 1)) & ~(square2 - 1);
	}

	public static boolean isWhiteSquare(long square) {
		return (square & Square.WHITES) != 0;
	}

	public static boolean isBlackSquare(long square) {
		return (square & Square.BLACKS) != 0;
	}

	public static long getSameColorSquares(long square) {
		return (square & Square.WHITES) != 0 ? Square.WHITES : Square.BLACKS;
	}

	public static long frontPawnSpan(long pawn, int color) {
		int index = square2Index(pawn);
		int rank = index >> 3;
		int file = 7 - index & 7;

		return RANKS_FORWARD[color][rank] &
				(FILE[file] | FILES_ADJACENT[file]);
	}

	public static long frontFile(long square, int color) {
		int index = square2Index(square);
		int rank = index >> 3;
		int file = 7 - index & 7;

		return RANKS_FORWARD[color][rank] & FILE[file];
	}

	public static boolean sameRankOrFile(int index1, int index2) {
		return ((index1 >> 3) == (index2 >> 3)) ||
				((index1 & 7) == (index2 & 7));
	}
}