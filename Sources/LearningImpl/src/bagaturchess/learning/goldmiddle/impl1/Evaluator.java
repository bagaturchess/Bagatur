package bagaturchess.learning.goldmiddle.impl1;


public class Evaluator {

	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	
	//START Util
	public static final short SHORT_MIN = -32767;
	public static final short SHORT_MAX = 32767;

	public static final long[] POWER_LOOKUP = new long[64];
	static {
		for (int i = 0; i < 64; i++) {
			POWER_LOOKUP[i] = 1L << i;
		}
	}

	public static void reverse(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int temp = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = temp;
		}
	}

	public static void reverse(long[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			long temp = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = temp;
		}
	}

	public static long mirrorHorizontal(long bitboard) {
		long k1 = 0x5555555555555555L;
		long k2 = 0x3333333333333333L;
		long k4 = 0x0f0f0f0f0f0f0f0fL;
		bitboard = ((bitboard >>> 1) & k1) | ((bitboard & k1) << 1);
		bitboard = ((bitboard >>> 2) & k2) | ((bitboard & k2) << 2);
		bitboard = ((bitboard >>> 4) & k4) | ((bitboard & k4) << 4);
		return bitboard;
	}

	public static int flipHorizontalIndex(int index) {
		return (index & 0xF8) | (7 - (index & 7));
	}

	public static long mirrorVertical(long bitboard) {
		return Long.reverseBytes(bitboard);
	}

	public static int getDistance(final int index1, final int index2) {
		return Math.max(Math.abs((index1 >>> 3) - (index2 >>> 3)), Math.abs((index1 & 7) - (index2 & 7)));
	}

	public static int getDistance(final long sq1, final long sq2) {
		return Math.max(Math.abs((Long.numberOfTrailingZeros(sq1) >>> 3) - (Long.numberOfTrailingZeros(sq2) >>> 3)),
				Math.abs((Long.numberOfTrailingZeros(sq1) & 7) - (Long.numberOfTrailingZeros(sq2) & 7)));
	}
	//END Util
	
	

	
	
	//START Static Moves
	public static final long[] KNIGHT_MOVES = new long[64];
	public static final long[] KING_MOVES = new long[64];

	public static final long[][] PAWN_ATTACKS = new long[2][64];

	// PAWN
	static {
		for (int currentPosition = 0; currentPosition < 64; currentPosition++) {
			for (int newPosition = 0; newPosition < 64; newPosition++) {
				// attacks
				if (newPosition == currentPosition + 7 && newPosition % 8 != 7) {
					PAWN_ATTACKS[WHITE][currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition + 9 && newPosition % 8 != 0 ) {
					PAWN_ATTACKS[WHITE][currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 7 && newPosition % 8 != 0) {
					PAWN_ATTACKS[BLACK][currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
				}
				if (newPosition == currentPosition - 9 && newPosition % 8 != 7) {
					PAWN_ATTACKS[BLACK][currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
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
					KNIGHT_MOVES[currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
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
					KING_MOVES[currentPosition] |= Evaluator.POWER_LOOKUP[newPosition];
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
	//END Static Moves
	
	
	//START Bitboards
	// rank 1
	public static final long H1 = 1L;
	public static final long G1 = H1 << 1;
	public static final long F1 = G1 << 1;
	public static final long E1 = F1 << 1;
	public static final long D1 = E1 << 1;
	public static final long C1 = D1 << 1;
	public static final long B1 = C1 << 1;
	public static final long A1 = B1 << 1;

	// rank 2
	public static final long H2 = A1 << 1;
	public static final long G2 = H2 << 1;
	public static final long F2 = G2 << 1;
	public static final long E2 = F2 << 1;
	public static final long D2 = E2 << 1;
	public static final long C2 = D2 << 1;
	public static final long B2 = C2 << 1;
	public static final long A2 = B2 << 1;

	// rank 3
	public static final long H3 = A2 << 1;
	public static final long G3 = H3 << 1;
	public static final long F3 = G3 << 1;
	public static final long E3 = F3 << 1;
	public static final long D3 = E3 << 1;
	public static final long C3 = D3 << 1;
	public static final long B3 = C3 << 1;
	public static final long A3 = B3 << 1;

	// rank 4
	public static final long H4 = A3 << 1;
	public static final long G4 = H4 << 1;
	public static final long F4 = G4 << 1;
	public static final long E4 = F4 << 1;
	public static final long D4 = E4 << 1;
	public static final long C4 = D4 << 1;
	public static final long B4 = C4 << 1;
	public static final long A4 = B4 << 1;

	// rank 5
	public static final long H5 = A4 << 1;
	public static final long G5 = H5 << 1;
	public static final long F5 = G5 << 1;
	public static final long E5 = F5 << 1;
	public static final long D5 = E5 << 1;
	public static final long C5 = D5 << 1;
	public static final long B5 = C5 << 1;
	public static final long A5 = B5 << 1;

	// rank 6
	public static final long H6 = A5 << 1;
	public static final long G6 = H6 << 1;
	public static final long F6 = G6 << 1;
	public static final long E6 = F6 << 1;
	public static final long D6 = E6 << 1;
	public static final long C6 = D6 << 1;
	public static final long B6 = C6 << 1;
	public static final long A6 = B6 << 1;

	// rank 7
	public static final long H7 = A6 << 1;
	public static final long G7 = H7 << 1;
	public static final long F7 = G7 << 1;
	public static final long E7 = F7 << 1;
	public static final long D7 = E7 << 1;
	public static final long C7 = D7 << 1;
	public static final long B7 = C7 << 1;
	public static final long A7 = B7 << 1;

	// rank 8
	public static final long H8 = A7 << 1;
	public static final long G8 = H8 << 1;
	public static final long F8 = G8 << 1;
	public static final long E8 = F8 << 1;
	public static final long D8 = E8 << 1;
	public static final long C8 = D8 << 1;
	public static final long B8 = C8 << 1;
	public static final long A8 = B8 << 1;

	// special squares
	public static final long A1_B1 = A1 | B1;
	public static final long A1_D1 = A1 | D1;
	public static final long B1_C1 = B1 | C1;
	public static final long C1_D1 = C1 | D1;
	public static final long C1_G1 = C1 | G1;
	public static final long D1_F1 = D1 | F1;
	public static final long F1_G1 = F1 | G1;
	public static final long F1_H1 = F1 | H1;
	public static final long F1_H8 = F1 | H8;
	public static final long G1_H1 = G1 | H1;
	public static final long B3_C2 = B3 | C2;
	public static final long G3_F2 = G3 | F2;
	public static final long D4_E5 = D4 | E5;
	public static final long E4_D5 = E4 | D5;
	public static final long B6_C7 = B6 | C7;
	public static final long G6_F7 = G6 | F7;
	public static final long A8_B8 = A8 | B8;
	public static final long A8_D8 = A8 | D8;
	public static final long B8_C8 = B8 | C8;
	public static final long C8_G8 = C8 | G8;
	public static final long D8_F8 = D8 | F8;
	public static final long F8_G8 = F8 | G8;
	public static final long F8_H8 = F8 | H8;
	public static final long G8_H8 = G8 | H8;
	public static final long A1B1C1 = A1 | B1 | C1;
	public static final long B1C1D1 = B1 | C1 | D1;
	public static final long A8B8C8 = A8 | B8 | C8;
	public static final long B8C8D8 = B8 | C8 | D8;
	public static final long A1B1A2B2 = A1 | B1 | A2 | B2;
	public static final long D1E1D2E2 = D1 | E1 | D2 | E2;
	public static final long G1H1G2H2 = G1 | H1 | G2 | H2;
	public static final long D7E7D8E8 = D7 | E7 | D8 | E8;
	public static final long A7B7A8B8 = A7 | B7 | A8 | B8;
	public static final long G7H7G8H8 = G7 | H7 | G8 | H8;
	public static final long WHITE_SQUARES = 0xaa55aa55aa55aa55L;
	public static final long BLACK_SQUARES = ~WHITE_SQUARES;
	public static final long CORNER_SQUARES = A1 | H1 | A8 | H8;

	// ranks
	public static final long RANK_1 = A1 | B1 | C1 | D1 | E1 | F1 | G1 | H1;
	public static final long RANK_2 = A2 | B2 | C2 | D2 | E2 | F2 | G2 | H2;
	public static final long RANK_3 = A3 | B3 | C3 | D3 | E3 | F3 | G3 | H3;
	public static final long RANK_4 = A4 | B4 | C4 | D4 | E4 | F4 | G4 | H4;
	public static final long RANK_5 = A5 | B5 | C5 | D5 | E5 | F5 | G5 | H5;
	public static final long RANK_6 = A6 | B6 | C6 | D6 | E6 | F6 | G6 | H6;
	public static final long RANK_7 = A7 | B7 | C7 | D7 | E7 | F7 | G7 | H7;
	public static final long RANK_8 = A8 | B8 | C8 | D8 | E8 | F8 | G8 | H8;

	// special ranks
	public static final long RANK_12 = RANK_1 | RANK_2;
	public static final long RANK_78 = RANK_7 | RANK_8;
	public static final long RANK_234 = RANK_2 | RANK_3 | RANK_4;
	public static final long RANK_567 = RANK_5 | RANK_6 | RANK_7;
	public static final long RANK_23456 = RANK_2 | RANK_3 | RANK_4 | RANK_5 | RANK_6;
	public static final long RANK_34567 = RANK_3 | RANK_4 | RANK_5 | RANK_6 | RANK_7;
	public static final long RANK_PROMOTION[] = { RANK_7, RANK_2 };
	public static final long RANK_NON_PROMOTION[] = { ~RANK_PROMOTION[0], ~RANK_PROMOTION[1] };
	public static final long RANK_FIRST[] = { RANK_1, RANK_8 };

	// files
	public static final long FILE_A = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	public static final long FILE_B = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
	public static final long FILE_C = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8;
	public static final long FILE_D = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8;
	public static final long FILE_E = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8;
	public static final long FILE_F = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8;
	public static final long FILE_G = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	public static final long FILE_H = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	public static final long FILE_ABC = FILE_A | FILE_B | FILE_C;
	public static final long FILE_FGH = FILE_F | FILE_G | FILE_H;
	public static final long FILE_CDEF = FILE_C | FILE_D | FILE_E | FILE_F;
	public static final long NOT_FILE_A = ~FILE_A;
	public static final long NOT_FILE_H = ~FILE_H;

	// special
	public static final long WHITE_CORNERS = 0xf8f0e0c183070f1fL;
	public static final long BLACK_CORNERS = 0x1f0f0783c1e0f0f8L;

	public static final long RANKS[] = { RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8 };
	public static final long FILES[] = { FILE_H, FILE_G, FILE_F, FILE_E, FILE_D, FILE_C, FILE_B, FILE_A };
	public static final long FILES_ADJACENT[] = { //
			FILE_G, //
			FILE_H | FILE_F, //
			FILE_G | FILE_E, //
			FILE_F | FILE_D, //
			FILE_E | FILE_C, //
			FILE_D | FILE_B, //
			FILE_C | FILE_A, //
			FILE_B };

	public static final long KING_SIDE = FILE_F | FILE_G | FILE_H;
	public static final long QUEEN_SIDE = FILE_A | FILE_B | FILE_C;

	public static final long WHITE_SIDE = RANK_1 | RANK_2 | RANK_3 | RANK_4;
	public static final long BLACK_SIDE = RANK_5 | RANK_6 | RANK_7 | RANK_8;

	public static final long WHITE_SPACE_ZONE = (RANK_2 | RANK_3 | RANK_4) & (FILE_C | FILE_D | FILE_E | FILE_F);
	public static final long BLACK_SPACE_ZONE = (RANK_7 | RANK_6 | RANK_5) & (FILE_C | FILE_D | FILE_E | FILE_F);

	public static long getWhitePawnAttacks(final long pawns) {
		return pawns << 9 & NOT_FILE_H | pawns << 7 & NOT_FILE_A;
	}

	public static long getBlackPawnAttacks(final long pawns) {
		return pawns >>> 9 & NOT_FILE_A | pawns >>> 7 & NOT_FILE_H;
	}

	public static long getPawnNeighbours(final long pawns) {
		return pawns << 1 & NOT_FILE_H | pawns >>> 1 & NOT_FILE_A;
	}

	/**
	 * @author Gerd Isenberg
	 */
	public static int manhattanCenterDistance(int sq) {
		int file = sq & 7;
		int rank = sq >>> 3;
		file ^= (file - 4) >>> 8;
		rank ^= (rank - 4) >>> 8;
		return (file + rank) & 7;
	}

	public static long getWhitePassedPawnMask(final int index) {
		return (FILES[index & 7] | FILES_ADJACENT[index & 7]) << ((index >>> 3 << 3) + 8);
	}

	public static long getBlackPassedPawnMask(final int index) {
		if (index < 8) {
			return 0;
		}
		return (FILES[index & 7] | FILES_ADJACENT[index & 7]) >>> ((71 - index) >>> 3 << 3);
	}

	public static long getWhiteAdjacentMask(final int index) {
		return getWhitePassedPawnMask(index) & ~FILES[index & 7];
	}

	public static long getBlackAdjacentMask(final int index) {
		return getBlackPassedPawnMask(index) & ~FILES[index & 7];
	}
	//END Bitboards
	
	
	//START Magics
	private static final long[] rookMovementMasks = new long[64];
	private static final long[] bishopMovementMasks = new long[64];
	private static final long[] rookMagicNumbers = { 0xa180022080400230L, 0x40100040022000L, 0x80088020001002L, 0x80080280841000L, 0x4200042010460008L,
			0x4800a0003040080L, 0x400110082041008L, 0x8000a041000880L, 0x10138001a080c010L, 0x804008200480L, 0x10011012000c0L, 0x22004128102200L,
			0x200081201200cL, 0x202a001048460004L, 0x81000100420004L, 0x4000800380004500L, 0x208002904001L, 0x90004040026008L, 0x208808010002001L,
			0x2002020020704940L, 0x8048010008110005L, 0x6820808004002200L, 0xa80040008023011L, 0xb1460000811044L, 0x4204400080008ea0L, 0xb002400180200184L,
			0x2020200080100380L, 0x10080080100080L, 0x2204080080800400L, 0xa40080360080L, 0x2040604002810b1L, 0x8c218600004104L, 0x8180004000402000L,
			0x488c402000401001L, 0x4018a00080801004L, 0x1230002105001008L, 0x8904800800800400L, 0x42000c42003810L, 0x8408110400b012L, 0x18086182000401L,
			0x2240088020c28000L, 0x1001201040c004L, 0xa02008010420020L, 0x10003009010060L, 0x4008008008014L, 0x80020004008080L, 0x282020001008080L,
			0x50000181204a0004L, 0x102042111804200L, 0x40002010004001c0L, 0x19220045508200L, 0x20030010060a900L, 0x8018028040080L, 0x88240002008080L,
			0x10301802830400L, 0x332a4081140200L, 0x8080010a601241L, 0x1008010400021L, 0x4082001007241L, 0x211009001200509L, 0x8015001002441801L,
			0x801000804000603L, 0xc0900220024a401L, 0x1000200608243L };
	private static final long[] bishopMagicNumbers = { 0x2910054208004104L, 0x2100630a7020180L, 0x5822022042000000L, 0x2ca804a100200020L, 0x204042200000900L,
			0x2002121024000002L, 0x80404104202000e8L, 0x812a020205010840L, 0x8005181184080048L, 0x1001c20208010101L, 0x1001080204002100L, 0x1810080489021800L,
			0x62040420010a00L, 0x5028043004300020L, 0xc0080a4402605002L, 0x8a00a0104220200L, 0x940000410821212L, 0x1808024a280210L, 0x40c0422080a0598L,
			0x4228020082004050L, 0x200800400e00100L, 0x20b001230021040L, 0x90a0201900c00L, 0x4940120a0a0108L, 0x20208050a42180L, 0x1004804b280200L,
			0x2048020024040010L, 0x102c04004010200L, 0x20408204c002010L, 0x2411100020080c1L, 0x102a008084042100L, 0x941030000a09846L, 0x244100800400200L,
			0x4000901010080696L, 0x280404180020L, 0x800042008240100L, 0x220008400088020L, 0x4020182000904c9L, 0x23010400020600L, 0x41040020110302L,
			0x412101004020818L, 0x8022080a09404208L, 0x1401210240484800L, 0x22244208010080L, 0x1105040104000210L, 0x2040088800c40081L, 0x8184810252000400L,
			0x4004610041002200L, 0x40201a444400810L, 0x4611010802020008L, 0x80000b0401040402L, 0x20004821880a00L, 0x8200002022440100L, 0x9431801010068L,
			0x1040c20806108040L, 0x804901403022a40L, 0x2400202602104000L, 0x208520209440204L, 0x40c000022013020L, 0x2000104000420600L, 0x400000260142410L,
			0x800633408100500L, 0x2404080a1410L, 0x138200122002900L };
	private static final long[][] rookMagicMoves = new long[64][];
	private static final long[][] bishopMagicMoves = new long[64][];
	private static final int[] rookShifts = new int[64];
	private static final int[] bishopShifts = new int[64];

	public static long getRookMoves(final int fromIndex, final long allPieces) {
		return rookMagicMoves[fromIndex][(int) ((allPieces & rookMovementMasks[fromIndex]) * rookMagicNumbers[fromIndex] >>> rookShifts[fromIndex])];
	}

	public static long getBishopMoves(final int fromIndex, final long allPieces) {
		return bishopMagicMoves[fromIndex][(int) ((allPieces & bishopMovementMasks[fromIndex]) * bishopMagicNumbers[fromIndex] >>> bishopShifts[fromIndex])];
	}

	public static long getQueenMoves(final int fromIndex, final long allPieces) {
		return rookMagicMoves[fromIndex][(int) ((allPieces & rookMovementMasks[fromIndex]) * rookMagicNumbers[fromIndex] >>> rookShifts[fromIndex])]
				| bishopMagicMoves[fromIndex][(int) ((allPieces & bishopMovementMasks[fromIndex]) * bishopMagicNumbers[fromIndex] >>> bishopShifts[fromIndex])];
	}

	public static long getRookMovesEmptyBoard(final int fromIndex) {
		return rookMagicMoves[fromIndex][0];
	}

	public static long getBishopMovesEmptyBoard(final int fromIndex) {
		return bishopMagicMoves[fromIndex][0];
	}

	public static long getQueenMovesEmptyBoard(final int fromIndex) {
		return bishopMagicMoves[fromIndex][0] | rookMagicMoves[fromIndex][0];
	}

	static {
		calculateBishopMovementMasks();
		calculateRookMovementMasks();
		generateShiftArrys();
		long[][] bishopOccupancyVariations = calculateVariations(bishopMovementMasks);
		long[][] rookOccupancyVariations = calculateVariations(rookMovementMasks);
		generateBishopMoveDatabase(bishopOccupancyVariations);
		generateRookMoveDatabase(rookOccupancyVariations);
	}

	private static void generateShiftArrys() {
		for (int i = 0; i < 64; i++) {
			rookShifts[i] = 64 - Long.bitCount(rookMovementMasks[i]);
			bishopShifts[i] = 64 - Long.bitCount(bishopMovementMasks[i]);
		}
	}

	private static long[][] calculateVariations(long[] movementMasks) {

		long[][] occupancyVariations = new long[64][];
		for (int index = 0; index < 64; index++) {
			int variationCount = (int) Evaluator.POWER_LOOKUP[Long.bitCount(movementMasks[index])];
			occupancyVariations[index] = new long[variationCount];

			for (int variationIndex = 1; variationIndex < variationCount; variationIndex++) {
				long currentMask = movementMasks[index];

				for (int i = 0; i < 32 - Integer.numberOfLeadingZeros(variationIndex); i++) {
					if ((Evaluator.POWER_LOOKUP[i] & variationIndex) != 0) {
						occupancyVariations[index][variationIndex] |= Long.lowestOneBit(currentMask);
					}
					currentMask &= currentMask - 1;
				}
			}
		}

		return occupancyVariations;
	}

	private static void calculateRookMovementMasks() {
		for (int index = 0; index < 64; index++) {

			// up
			for (int j = index + 8; j < 64 - 8; j += 8) {
				rookMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// down
			for (int j = index - 8; j >= 0 + 8; j -= 8) {
				rookMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// left
			for (int j = index + 1; j % 8 != 0 && j % 8 != 7; j++) {
				rookMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// right
			for (int j = index - 1; j % 8 != 7 && j % 8 != 0 && j > 0; j--) {
				rookMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
		}
	}

	private static void calculateBishopMovementMasks() {
		for (int index = 0; index < 64; index++) {

			// up-right
			for (int j = index + 7; j < 64 - 7 && j % 8 != 7 && j % 8 != 0; j += 7) {
				bishopMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// up-left
			for (int j = index + 9; j < 64 - 9 && j % 8 != 7 && j % 8 != 0; j += 9) {
				bishopMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// down-right
			for (int j = index - 9; j >= 0 + 9 && j % 8 != 7 && j % 8 != 0; j -= 9) {
				bishopMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
			// down-left
			for (int j = index - 7; j >= 0 + 7 && j % 8 != 7 && j % 8 != 0; j -= 7) {
				bishopMovementMasks[index] |= Evaluator.POWER_LOOKUP[j];
			}
		}
	}

	private static void generateRookMoveDatabase(long[][] rookOccupancyVariations) {
		for (int index = 0; index < 64; index++) {
			rookMagicMoves[index] = new long[rookOccupancyVariations[index].length];
			for (int variationIndex = 0; variationIndex < rookOccupancyVariations[index].length; variationIndex++) {
				long validMoves = 0;
				int magicIndex = (int) ((rookOccupancyVariations[index][variationIndex] * rookMagicNumbers[index]) >>> rookShifts[index]);

				for (int j = index + 8; j < 64; j += 8) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 8; j >= 0; j -= 8) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index + 1; j % 8 != 0; j++) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 1; j % 8 != 7 && j >= 0; j--) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}

				rookMagicMoves[index][magicIndex] = validMoves;
			}
		}
	}

	private static void generateBishopMoveDatabase(long[][] bishopOccupancyVariations) {
		for (int index = 0; index < 64; index++) {
			bishopMagicMoves[index] = new long[bishopOccupancyVariations[index].length];
			for (int variationIndex = 0; variationIndex < bishopOccupancyVariations[index].length; variationIndex++) {
				long validMoves = 0;
				int magicIndex = (int) ((bishopOccupancyVariations[index][variationIndex] * bishopMagicNumbers[index]) >>> bishopShifts[index]);

				// up-right
				for (int j = index + 7; j % 8 != 7 && j < 64; j += 7) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// up-left
				for (int j = index + 9; j % 8 != 0 && j < 64; j += 9) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-right
				for (int j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-left
				for (int j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
					validMoves |= Evaluator.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Evaluator.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}

				bishopMagicMoves[index][magicIndex] = validMoves;
			}
		}
	}
	//END Magics
	
	
	//START Constants
	public static final int EMPTY = 0;
	public static final int PAWN = 1;
	public static final int NIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;

	public static final int SCORE_NOT_RUNNING = 7777;

	public static final int[] COLOR_FACTOR = { 1, -1 };
	public static final int[] COLOR_FACTOR_8 = { 8, -8 };

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
						IN_BETWEEN[from][to] |= Evaluator.POWER_LOOKUP[i];
						i--;
					}
				}

				// vertical
				if (from % 8 == to % 8) {
					i = to - 8;
					while (i > from) {
						IN_BETWEEN[from][to] |= Evaluator.POWER_LOOKUP[i];
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
						IN_BETWEEN[from][to] |= Evaluator.POWER_LOOKUP[i];
						i -= 9;
					}
				}

				// diagonal /
				if ((to - from) % 7 == 0 && to % 8 < from % 8) {
					i = to - 7;
					while (i > from) {
						IN_BETWEEN[from][to] |= Evaluator.POWER_LOOKUP[i];
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
						PINNED_MOVEMENT[pinnedPieceIndex][kingIndex] |= Evaluator.POWER_LOOKUP[xray];
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
			KING_AREA[WHITE][i] |= Evaluator.KING_MOVES[i] | Evaluator.POWER_LOOKUP[i];
			KING_AREA[BLACK][i] |= Evaluator.KING_MOVES[i] | Evaluator.POWER_LOOKUP[i];

			if (i > 15) {
				KING_AREA[BLACK][i] |= Evaluator.KING_MOVES[i] >>> 8;
			}

			if (i < 48) {
				KING_AREA[WHITE][i] |= Evaluator.KING_MOVES[i] << 8;
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
	//END Constants

	
	//START Constants
	public static final int SIDE_TO_MOVE_BONUS = 16; //cannot be tuned //TODO lower in endgame
	public static final int IN_CHECK = 20;
	
	public static final int SCORE_DRAW 						= 0;
	public static final int SCORE_DRAWISH					= 10;
	public static final int SCORE_DRAWISH_KING_CORNERED		= 20;
	public static final int SCORE_MATE_BOUND 				= 30000;
	
	// other
	public static final int[] OTHER_SCORES = {-8, 12, 18, 8, 18, 12, 150, 12, 56};
	public static final int IX_ROOK_FILE_SEMI_OPEN	 		= 0;
	public static final int IX_ROOK_FILE_SEMI_OPEN_ISOLATED = 1;
	public static final int IX_ROOK_FILE_OPEN 				= 2;
	public static final int IX_ROOK_7TH_RANK 				= 3;
	public static final int IX_ROOK_BATTERY 				= 4;
	public static final int IX_BISHOP_LONG 					= 5;
	public static final int IX_BISHOP_PRISON 				= 6;
	public static final int IX_SPACE 						= 7;
	public static final int IX_DRAWISH 						= 8;
	
	// threats
	public static final int[] THREATS_MG = {38, 68, 100, 16, 56, 144, 66, 52, 8, 16, -6};
	public static final int[] THREATS_EG = {36, 10, -38, 16, 40, 156, 6, -12, 20, 4, 6};
	public static final int[] THREATS = new int[THREATS_MG.length];
	public static final int IX_MULTIPLE_PAWN_ATTACKS 		= 0;
	public static final int IX_PAWN_ATTACKS 				= 1;
	public static final int IX_QUEEN_ATTACKED 				= 2;
	public static final int IX_PAWN_PUSH_THREAT 			= 3;
	public static final int IX_NIGHT_FORK 					= 4;
	public static final int IX_NIGHT_FORK_KING 				= 5;
	public static final int IX_ROOK_ATTACKED 				= 6;
	public static final int IX_QUEEN_ATTACKED_MINOR			= 7;
	public static final int IX_MAJOR_ATTACKED				= 8;
	public static final int IX_UNUSED_OUTPOST				= 9;
	public static final int IX_PAWN_ATTACKED 				= 10;
	
	// pawn
	public static final int[] PAWN_SCORES = {6, 10, 12, 6};
	public static final int IX_PAWN_DOUBLE 					= 0;
	public static final int IX_PAWN_ISOLATED 				= 1;
	public static final int IX_PAWN_BACKWARD 				= 2;
	public static final int IX_PAWN_INVERSE					= 3;
	
	// imbalance
	public static final int[] IMBALANCE_SCORES = {32, 54, 16};
	public static final int IX_ROOK_PAIR		 			= 0;
	public static final int IX_BISHOP_DOUBLE 				= 1;
	public static final int IX_QUEEN_NIGHT 					= 2;
	
	public static final int[] PHASE 					= {0, 0, 6, 6, 13, 28};
	
	public static final int[] MATERIAL 					= {0, 100, 396, 416, 706, 1302, 3000};
	public static final int[] PINNED 					= {0, -2, 14, 42, 72, 88};
	public static final int[] PINNED_ATTACKED			= {0, 28, 128, 274, 330, 210};
	public static final int[] DISCOVERED		 		= {0, -14, 128, 110, 180, 0, 28};
	public static final int[] KNIGHT_OUTPOST			= {0, 0, 10, 26, 24, 36, 8, 38};
	public static final int[] BISHOP_OUTPOST			= {0, 0, 22, 22, 20, 22, 52, 50};
	public static final int[] DOUBLE_ATTACKED 			= {0, 16, 34, 64, -4, -6, 0};
	public static final int[] HANGING 					= {0, 16, 6, 0, -10, -18, 48}; //qsearch could set the other in check
	public static final int[] HANGING_2 				= {0, 38, 90, 94, 52, -230};
	public static final int[] ROOK_TRAPPED 				= {64, 62, 28};
	public static final int[] ONLY_MAJOR_DEFENDERS 		= {0, 6, 14, 24, 4, 10, 0};
	public static final int[] NIGHT_PAWN				= {68, -14, -2, 2, 8, 12, 20, 30, 36};
	public static final int[] ROOK_PAWN					= {48, -4, -4, -4, -4, 0, 0, 0, 0};
	public static final int[] BISHOP_PAWN 				= {-20, -8, -6, 0, 6, 12, 22, 32, 46};
	public static final int[] SPACE 					= {0, 0, 0, 0, 0, -6, -6, -8, -7, -4, -4, -2, 0, -1, 0, 3, 7};
	
	public static final int[] PAWN_BLOCKAGE 			= {0, 0, -10, 2, 6, 28, 66, 196};
	public static final int[] PAWN_CONNECTED			= {0, 0, 12, 14, 20, 58, 122};
	public static final int[] PAWN_NEIGHBOUR	 		= {0, 0, 4, 10, 26, 88, 326};
	
	public static final int[][] SHIELD_BONUS_MG			= {	{0, 18, 14, 4, -24, -38, -270},
															{0, 52, 36, 6, -44, 114, -250},
															{0, 52, 4, 4, 46, 152, 16},
															{0, 16, 4, 6, -16, 106, 2}};
	public static final int[][] SHIELD_BONUS_EG			= {	{0, -48, -18, -16, 8, -30, -28},
															{0, -16, -26, -10, 42, 6, 20},
															{0, 0, 8, 0, 28, 24, 38},
															{0, -22, -14, 0, 38, 10, 60}};
	public static final int[][] SHIELD_BONUS 			= new int[4][7];

	public static final int[] PASSED_SCORE_MG			= {0, -4, -2, 0, 18, 22, -6};
	public static final int[] PASSED_SCORE_EG			= {0, 18, 18, 38, 62, 136, 262};
	
	public static final int[] PASSED_CANDIDATE			= {0, 2, 2, 8, 14, 40};
	
	public static final float[] PASSED_KING_MULTI 		= {0, 1.4f, 1.3f, 1.1f, 1.1f, 1.0f, 0.8f, 0.8f};														
	public static final float[] PASSED_MULTIPLIERS	= {
			0.5f,	// blocked
			1.2f,	// next square attacked
			0.4f,	// enemy king in front
			1.2f,	// next square defended
			0.7f,	// attacked
			1.6f,	// defended by rook from behind
			0.6f,	// attacked by rook from behind
			1.7f	// no enemy attacks in front
	};	
	
	//concept borrowed from Ed Schroder
	public static final int[] KS_SCORES = { //TODO negative values? //TODO first values are not used
			0, 0, 0, 0, -140, -150, -120, -90, -40, 40, 70, 
			80, 100, 110, 130, 160, 200, 230, 290, 330, 400, 
			480, 550, 630, 660, 700, 790, 860, 920, 1200 };
	public static final int[] KS_QUEEN_TROPISM 		= {0, 0, 1, 1, 1, 1, 0, 0};	// index 0 and 1 are never evaluated	
	public static final int[] KS_RANK 				= {0, 0, 1, 1, 0, 0, 0, 0};
	public static final int[] KS_CHECK				= {0, 0, 3, 2, 3};
	public static final int[] KS_UCHECK				= {0, 0, 1, 1, 1};
	public static final int[] KS_CHECK_QUEEN 		= {0, 0, 0, 0, 2, 3, 4, 4, 4, 4, 3, 3, 3, 2, 1, 1, 0};
	public static final int[] KS_NO_FRIENDS 		= {6, 4, 0, 5, 5, 5, 6, 6, 7, 8, 9, 9};
	public static final int[] KS_ATTACKS 			= {0, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 2, 9};
	public static final int[] KS_DOUBLE_ATTACKS 	= {0, 1, 3, 5, 2, -8, 0, 0, 0};
	public static final int[] KS_ATTACK_PATTERN		= {	
		 //                                                 Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  
		 // 	                    R  R  R  R  R  R  R  R                          R  R  R  R  R  R  R  R  
		 //             B  B  B  B              B  B  B  B              B  B  B  B              B  B  B  B  
		 //       N  N        N  N        N  N        N  N        N  N        N  N        N  N        N  N  
		 //    P     P     P     P     P     P     P     P     P     P     P     P     P     P     P     P
			4, 1, 2, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 3, 3, 1, 2, 3, 3, 3, 3, 4, 4
	};
	
	public static final int[] KS_OTHER	= {		
			3,		// queen-touch check
			4,		// king at blocked first rank check
			1		// open file
	};		
		
	public static final int[] MOBILITY_KNIGHT_MG	= {-34, -16, -6, 0, 12, 16, 26, 28, 56};
	public static final int[] MOBILITY_KNIGHT_EG	= {-98, -34, -12, 0, 4, 12, 12, 14, 4};
	public static final int[] MOBILITY_BISHOP_MG	= {-16, 2, 16, 24, 28, 36, 38, 40, 36, 42, 58, 82, 28, 120};
	public static final int[] MOBILITY_BISHOP_EG	= {-36, -8, 6, 18, 28, 28, 36, 38, 42, 40, 32, 34, 54, 32};
	public static final int[] MOBILITY_ROOK_MG 		= {-34, -24, -18, -14, -12, -4, 0, 8, 16, 26, 30, 40, 52, 68, 66};
	public static final int[] MOBILITY_ROOK_EG 		= {-38, -12, 0, 8, 18, 24, 28, 28, 34, 34, 38, 40, 40, 42, 46};
	public static final int[] MOBILITY_QUEEN_MG		= {-12, -14, -10, -14, -8, -6, -8, -8, -6, -4, -2, -2, -4, 2, 0, 0, 2, 16, 8, 22, 32, 66, 48, 156, 172, 236, 68, 336};
	public static final int[] MOBILITY_QUEEN_EG 	= {-28, -82, -102, -82, -76, -54, -40, -24, -10, -2, 8, 24, 30, 32, 38, 54, 60, 46, 70, 72, 66, 66, 52, 18, -8, -32, 64, -94};
	public static final int[] MOBILITY_KING_MG		= {-10, -12, -8, 0, 10, 26, 36, 70, 122};
	public static final int[] MOBILITY_KING_EG		= {-38, -2, 8, 8, 2, -12, -12, -26, -60};
	public static final int[] MOBILITY_KNIGHT		= new int[MOBILITY_KNIGHT_MG.length];
	public static final int[] MOBILITY_BISHOP		= new int[MOBILITY_BISHOP_MG.length];
	public static final int[] MOBILITY_ROOK			= new int[MOBILITY_ROOK_MG.length];
	public static final int[] MOBILITY_QUEEN		= new int[MOBILITY_QUEEN_MG.length];
	public static final int[] MOBILITY_KING			= new int[MOBILITY_KING_MG.length];
		
	/** piece, color, square */
	public static final int[][][] PSQT				= new int[7][2][64];
	public static final int[][][] PSQT_MG			= new int[7][2][64];
	public static final int[][][] PSQT_EG			= new int[7][2][64];
	
	static
	{	
		PSQT_MG[Evaluator.PAWN][Evaluator.WHITE] = new int[] {
				   0,  0,  0,  0,  0,  0,  0,  0,
				   164,156,174,218,218,174,156,164,
				    12, 26, 66, 58, 58, 66, 26, 12,
				   -16, -4, -2, 14, 14, -2, -4,-16,
				   -32,-28,-12,  2,  2,-12,-28,-32,
				   -30,-20,-16,-16,-16,-16,-20,-30,
				   -24,  4,-16,-10,-10,-16,  4,-24,
				     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		PSQT_EG[Evaluator.PAWN][Evaluator.WHITE] = new int[] {
				   0,  0,  0,  0,  0,  0,  0,  0,
				   -44,-34,-50,-60,-60,-50,-34,-44,
				    32, 16, -8,-22,-22, -8, 16, 32,
				    28, 14,  6, -4, -4,  6, 14, 28,
				    16, 12,  2, -2, -2,  2, 12, 16,
				     6,  4,  4, 10, 10,  4,  4,  6,
				    16,  4, 14, 22, 22, 14,  4, 16,
				     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		PSQT_MG[Evaluator.NIGHT][Evaluator.WHITE] = new int[]{	
				 -214,-112,-130,-34,-34,-130,-112,-214,
				 -80,-62,  2,-34,-34,  2,-62,-80,
				 -24, 44, 18, 36, 36, 18, 44,-24,
				  18, 42, 38, 50, 50, 38, 42, 18,
				   8, 36, 36, 36, 36, 36, 36,  8,
				   8, 38, 34, 40, 40, 34, 38,  8,
				   0,  0, 24, 36, 36, 24,  0,  0,
				 -30,  6, -4, 20, 20, -4,  6,-30
		};
		
		PSQT_EG[Evaluator.NIGHT][Evaluator.WHITE] = new int[]{	
				 -16,  2, 32, 18, 18, 32,  2,-16,
				   2, 28, 12, 40, 40, 12, 28,  2,
				  -6,  6, 32, 28, 28, 32,  6, -6,
				  16, 20, 38, 42, 42, 38, 20, 16,
				  10, 18, 30, 40, 40, 30, 18, 10,
				   6,  8, 16, 30, 30, 16,  8,  6,
				 -10,  8,  6, 14, 14,  6,  8,-10,
				 -10,  2,  8, 16, 16,  8,  2,-10
		};
		
		PSQT_MG[Evaluator.BISHOP][Evaluator.WHITE] = new int[] {
				 -18, 12,-92,-84,-84,-92, 12,-18,
				 -44, -8,  0,-18,-18,  0, -8,-44,
				  40, 48, 42, 34, 34, 42, 48, 40,
				  28, 34, 40, 58, 58, 40, 34, 28,
				  28, 34, 36, 62, 62, 36, 34, 28,
				  36, 54, 50, 40, 40, 50, 54, 36,
				  36, 60, 48, 42, 42, 48, 60, 36,
				   8, 32, 30, 48, 48, 30, 32,  8
		};
		
		PSQT_EG[Evaluator.BISHOP][Evaluator.WHITE] = new int[]{	
				 -34,-18, -6,  0,  0, -6,-18,-34,
				  -4,-18, -4, -6, -6, -4,-18, -4,
				 -14,-12,-18, -8, -8,-18,-12,-14,
				  -6, -6,  0,  2,  2,  0, -6, -6,
				 -22,-12, -6, -6, -6, -6,-12,-22,
				 -20,-16,-18,  0,  0,-18,-16,-20,
				 -32,-40,-24,-12,-12,-24,-40,-32,
				 -36,-18,-12,-18,-18,-12,-18,-36
		};
		
		PSQT_MG[Evaluator.ROOK][Evaluator.WHITE] = new int[] {
				 -36,-26,-72, -4, -4,-72,-26,-36,
				 -36,-20, 14, 22, 22, 14,-20,-36,
				 -28,  2, -2, -8, -8, -2,  2,-28,
				 -40,-22, 10,  6,  6, 10,-22,-40,
				 -44,-14,-22,  2,  2,-22,-14,-44,
				 -38,-12, -2, -4, -4, -2,-12,-38,
				 -48, -2, -6, 10, 10, -6, -2,-48,
				 -10,-12,  0, 14, 14,  0,-12,-10
		};
		
		PSQT_EG[Evaluator.ROOK][Evaluator.WHITE] = new int[]{	
				  44, 46, 64, 46, 46, 64, 46, 44,
				  40, 40, 32, 24, 24, 32, 40, 40,
				  36, 36, 34, 34, 34, 34, 36, 36,
				  42, 38, 40, 34, 34, 40, 38, 42,
				  34, 32, 36, 24, 24, 36, 32, 34,
				  22, 26, 14, 14, 14, 14, 26, 22,
				  18,  6, 10, 10, 10, 10,  6, 18,
				   6, 16, 12,  2,  2, 12, 16,  6
		};
		
		PSQT_MG[Evaluator.QUEEN][Evaluator.WHITE] = new int[] {
				 -72,-44,-78,-66,-66,-78,-44,-72,
				 -38,-88,-70,-82,-82,-70,-88,-38,
				  -8,-38,-44,-64,-64,-44,-38, -8,
				 -38,-44,-46,-60,-60,-46,-44,-38,
				 -24,-34,-22,-30,-30,-22,-34,-24,
				  -8, 10,-14,-10,-10,-14, 10, -8,
				  -4, 12, 26, 20, 20, 26, 12, -4,
				  14,  6, 12, 24, 24, 12,  6, 14
		};
		
		PSQT_EG[Evaluator.QUEEN][Evaluator.WHITE] = new int[]{	
				  32, 14, 46, 38, 38, 46, 14, 32,
				   6, 22, 16, 48, 48, 16, 22,  6,
				  -4,  6, 12, 42, 42, 12,  6, -4,
				  36, 30, 14, 34, 34, 14, 30, 36,
				  14, 22,  4, 22, 22,  4, 22, 14,
				   6,-36,  0, -6, -6,  0,-36,  6,
				 -26,-42,-40,-18,-18,-40,-42,-26,
				 -42,-36,-32,-30,-30,-32,-36,-42
		};
		
		PSQT_MG[Evaluator.KING][Evaluator.WHITE] = new int[] {
				 -50,180,-26,  2,  2,-26,180,-50,
				  48,-10,-64,-24,-24,-64,-10, 48,
				  42, 52, 44,-44,-44, 44, 52, 42,
				 -40,-22,-46,-92,-92,-46,-22,-40,
				 -40,-20,-20,-76,-76,-20,-20,-40,
				  14, 16,  2,-10,-10,  2, 16, 14,
				  30, 10,-40,-56,-56,-40, 10, 30,
				  34, 44,  8, 18, 18,  8, 44, 34
		};
		
		PSQT_EG[Evaluator.KING][Evaluator.WHITE] = new int[] {
				 -90,-90, -2,-56,-56, -2,-90,-90,
				 -34, 14, 40, 24, 24, 40, 14,-34,
				  -8, 32, 36, 38, 38, 36, 32, -8,
				   0, 38, 44, 50, 50, 44, 38,  0,
				 -12, 14, 28, 44, 44, 28, 14,-12,
				 -18,  8, 18, 24, 24, 18,  8,-18,
				 -40, -4, 16, 24, 24, 16, -4,-40,
				 -74,-44,-24,-34,-34,-24,-44,-74
		};
		
	}
	
	public static final long[] ROOK_PRISON = { 
			0, Evaluator.A8, Evaluator.A8_B8, Evaluator.A8B8C8, 0, Evaluator.G8_H8, Evaluator.H8, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, Evaluator.A1, Evaluator.A1_B1, Evaluator.A1B1C1, 0, Evaluator.G1_H1, Evaluator.H1, 0 
	};
	
	public static final long[] BISHOP_PRISON = { 
			0, 0, 0, 0, 0, 0, 0, 0, //8
			Evaluator.B6_C7, 0, 0, 0, 0, 0, 0, Evaluator.G6_F7, //7
			0, 0, 0, 0, 0, 0, 0, 0, //6
			0, 0, 0, 0, 0, 0, 0, 0, //5
			0, 0, 0, 0, 0, 0, 0, 0, //4
			0, 0, 0, 0, 0, 0, 0, 0, //3
			Evaluator.B3_C2, 0, 0, 0, 0, 0, 0, Evaluator.G3_F2, //2
			0, 0, 0, 0, 0, 0, 0, 0  //1
		 // A  B  C  D  E  F  G  H
	};
	
	public static final int[] PROMOTION_SCORE = {
			0,
			0,
			MATERIAL[Evaluator.NIGHT] 	- MATERIAL[Evaluator.PAWN],
			MATERIAL[Evaluator.BISHOP] - MATERIAL[Evaluator.PAWN],
			MATERIAL[Evaluator.ROOK] 	- MATERIAL[Evaluator.PAWN],
			MATERIAL[Evaluator.QUEEN] 	- MATERIAL[Evaluator.PAWN],
	};
	
	
	public static void initMgEg() {
		initMgEg(MOBILITY_KNIGHT,	MOBILITY_KNIGHT_MG,	MOBILITY_KNIGHT_EG);
		initMgEg(MOBILITY_BISHOP, 	MOBILITY_BISHOP_MG, MOBILITY_BISHOP_EG);
		initMgEg(MOBILITY_ROOK,		MOBILITY_ROOK_MG,	MOBILITY_ROOK_EG);
		initMgEg(MOBILITY_QUEEN,	MOBILITY_QUEEN_MG,	MOBILITY_QUEEN_EG);
		initMgEg(MOBILITY_KING,		MOBILITY_KING_MG,	MOBILITY_KING_EG);
		initMgEg(THREATS,			THREATS_MG,			THREATS_EG);
		
		for (int i = 0; i < 4; i++) {
			initMgEg(SHIELD_BONUS[i], SHIELD_BONUS_MG[i], SHIELD_BONUS_EG[i]);
		}
		
		for (int color = Evaluator.WHITE; color <= Evaluator.BLACK; color++) {
			for (int piece = Evaluator.PAWN; piece <= Evaluator.KING; piece++) {
				initMgEg(PSQT[piece][color], PSQT_MG[piece][color], PSQT_EG[piece][color]);
			}
		}
	}

	private static void initMgEg(int[] array, int[] arrayMg, int[] arrayEg) {
		for(int i = 0; i < array.length; i++) {
			array[i] = Evaluator.score(arrayMg[i], arrayEg[i]);
		}
	}
	
	public static final int[] MIRRORED_LEFT_RIGHT = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_LEFT_RIGHT[i] = (i / 8) * 8 + 7 - (i & 7);
		}
	}

	public static final int[] MIRRORED_UP_DOWN = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_UP_DOWN[i] = (7 - i / 8) * 8 + (i & 7);
		}
	}
	
	static {
		
		// fix white arrays
		for (int piece = Evaluator.PAWN; piece <= Evaluator.KING; piece++){
			Evaluator.reverse(PSQT_MG[piece][Evaluator.WHITE]);
			Evaluator.reverse(PSQT_EG[piece][Evaluator.WHITE]);
		}

		// create black arrays
		for (int piece = Evaluator.PAWN; piece <= Evaluator.KING; piece++){
			for (int i = 0; i < 64; i++) {
				PSQT_MG[piece][Evaluator.BLACK][i] = -PSQT_MG[piece][Evaluator.WHITE][MIRRORED_UP_DOWN[i]];
				PSQT_EG[piece][Evaluator.BLACK][i] = -PSQT_EG[piece][Evaluator.WHITE][MIRRORED_UP_DOWN[i]];
			}
		}
		
		Evaluator.reverse(ROOK_PRISON);
		Evaluator.reverse(BISHOP_PRISON);
		
		initMgEg();
	}
	//END Constants
	
	
	public static final int FLAG_PAWN = 1 << (PAWN - 1);
	public static final int FLAG_NIGHT = 1 << (NIGHT - 1);
	public static final int FLAG_BISHOP = 1 << (BISHOP - 1);
	public static final int FLAG_ROOK = 1 << (ROOK - 1);
	public static final int FLAG_QUEEN = 1 << (QUEEN - 1);
	
	
	public static final int MG = 0;
	public static final int EG = 1;

	public static final int PHASE_TOTAL =
			
			4 * PHASE[NIGHT]
			+ 4 * PHASE[BISHOP]
			+ 4 * PHASE[ROOK]
			+ 2 * PHASE[QUEEN];
	

	private static EvalInfo evalinfo = new EvalInfo();
	
	
	public static int getScore1(final IChessBoard cb) {
		
		final int pawnScore = getPawnScores(cb);
		final int materialScore = getImbalances(cb);
		
		final int scoreMg = getMgScore(cb.getPSQTScore())
														+ pawnScore
														+ materialScore;
		
		final int scoreEg = getEgScore(cb.getPSQTScore())
														+ pawnScore
														+ materialScore;
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}

	
	public static int getScore2(final IChessBoard cb) {

		final int mgEgScore = calculateMobilityScoresAndSetAttackBoards(cb)
				+ calculatePassedPawnScores(cb)
				+ calculateThreats(cb)
				+ calculatePawnShieldBonus(cb);
		
		final int othersScore = calculateOthers(cb);
		
		final int scoreMg = getMgScore(mgEgScore)
								+ calculateKingSafetyScores(cb)
								+ calculateSpace(cb)
								+ othersScore;
		
		final int scoreEg = getEgScore(mgEgScore)
								+ othersScore;
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}
	
	
	public static int score(final int mgScore, final int egScore) {
		return (mgScore << 16) + egScore;
	}

	public static int getMgScore(final int score) {
		return (score + 0x8000) >> 16;
	}

	public static int getEgScore(final int score) {
		return (short) (score & 0xffff);
	}

	public static int calculateSpace(final IChessBoard cb) {

		int score = 0;

		score += OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(WHITE, PAWN) >>> 8) & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP)) & RANK_234);
		score -= OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(BLACK, PAWN) << 8) & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP)) & RANK_567);

		// idea taken from Laser
		long space = cb.getPieces(WHITE, PAWN) >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += SPACE[Long.bitCount(cb.getFriendlyPieces(WHITE))]
				* Long.bitCount(space & ~cb.getPieces(WHITE, PAWN) & ~evalinfo.attacks[BLACK][PAWN] & FILE_CDEF);
		space = cb.getPieces(BLACK, PAWN) << 8;
		space |= space << 8 | space << 16;
		score -= SPACE[Long.bitCount(cb.getFriendlyPieces(BLACK))]
				* Long.bitCount(space & ~cb.getPieces(BLACK, PAWN) & ~evalinfo.attacks[WHITE][PAWN] & FILE_CDEF);

		return score;
	}

	public static int getPawnScores(final IChessBoard cb) {
		return calculatePawnScores(cb);
	}

	private static int calculatePawnScores(final IChessBoard cb) {

		int score = 0;

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(cb.getPieces(WHITE, PAWN) & FILES[i]) > 1) {
				score -= PAWN_SCORES[IX_PAWN_DOUBLE];
			}
			if (Long.bitCount(cb.getPieces(BLACK, PAWN) & FILES[i]) > 1) {
				score += PAWN_SCORES[IX_PAWN_DOUBLE];
			}
		}

		// bonus for connected pawns
		long pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// bonus for neighbour pawns
		pawns = getPawnNeighbours(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getPawnNeighbours(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// set outposts
		evalinfo.passedPawnsAndOutposts = 0;
		pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(BLACK, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(WHITE, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}

		int index;

		// white
		pawns = cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
				score -= PAWN_SCORES[IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((getBlackAdjacentMask(index + 8) & cb.getPieces(WHITE, PAWN)) == 0) {
				if ((PAWN_ATTACKS[WHITE][index + 8] & cb.getPieces(BLACK, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
						score -= PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[WHITE][index] & cb.getPieces(WHITE, PAWN)) == 2) {
				score -= PAWN_SCORES[IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((getWhitePassedPawnMask(index) & cb.getPieces(BLACK, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index))) {
					score += PASSED_CANDIDATE[index / 8];
				}
			}

			pawns &= pawns - 1;
		}

		// black
		pawns = cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
				score += PAWN_SCORES[IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((getWhiteAdjacentMask(index - 8) & cb.getPieces(BLACK, PAWN)) == 0) {
				if ((PAWN_ATTACKS[BLACK][index - 8] & cb.getPieces(WHITE, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
						score += PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[BLACK][index] & cb.getPieces(BLACK, PAWN)) == 2) {
				score += PAWN_SCORES[IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((getBlackPassedPawnMask(index) & cb.getPieces(WHITE, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passers
			else if (Long.numberOfTrailingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index))) {
					score -= PASSED_CANDIDATE[7 - index / 8];
				}
			}

			pawns &= pawns - 1;
		}
		
		return score;
	}

	public static int getImbalances(final IChessBoard cb) {
		return calculateImbalances(cb);
	}

	private static int calculateImbalances(final IChessBoard cb) {

		int score = 0;

		// material
		score += calculateMaterialScore(cb);

		// knight bonus if there are a lot of pawns
		score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * NIGHT_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * NIGHT_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// rook bonus if there are no pawns
		score += Long.bitCount(cb.getPieces(WHITE, ROOK)) * ROOK_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, ROOK)) * ROOK_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// double bishop bonus
		if (Long.bitCount(cb.getPieces(WHITE, BISHOP)) == 2) {
			score += IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}
		if (Long.bitCount(cb.getPieces(BLACK, BISHOP)) == 2) {
			score -= IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}

		// queen and nights
		if (cb.getPieces(WHITE, QUEEN) != 0) {
			score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
		}
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
		}

		// rook pair
		if (Long.bitCount(cb.getPieces(WHITE, ROOK)) > 1) {
			score -= IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
		if (Long.bitCount(cb.getPieces(BLACK, ROOK)) > 1) {
			score += IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
		
		return score;
	}

	public static int calculateThreats(final IChessBoard cb) {
		int score = 0;
		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whiteMinorAttacks = evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP];
		final long blackMinorAttacks = evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP];
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		// double attacked pieces
		long piece = evalinfo.doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			score += DOUBLE_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}
		piece = evalinfo.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			score -= DOUBLE_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}

		// unused outposts
		score += Long.bitCount(evalinfo.passedPawnsAndOutposts & cb.getEmptySpaces() & whiteMinorAttacks & whitePawnAttacks)
				* THREATS[IX_UNUSED_OUTPOST];
		score -= Long.bitCount(evalinfo.passedPawnsAndOutposts & cb.getEmptySpaces() & blackMinorAttacks & blackPawnAttacks)
				* THREATS[IX_UNUSED_OUTPOST];

		// pawn push threat
		piece = (whitePawns << 8) & cb.getEmptySpaces() & ~blackAttacks;
		score += Long.bitCount(getWhitePawnAttacks(piece) & blacks) * THREATS[IX_PAWN_PUSH_THREAT];
		piece = (blackPawns >>> 8) & cb.getEmptySpaces() & ~whiteAttacks;
		score -= Long.bitCount(getBlackPawnAttacks(piece) & whites) * THREATS[IX_PAWN_PUSH_THREAT];

		// piece is attacked by a pawn
		score += Long.bitCount(whitePawnAttacks & blacks & ~blackPawns) * THREATS[IX_PAWN_ATTACKS];
		score -= Long.bitCount(blackPawnAttacks & whites & ~whitePawns) * THREATS[IX_PAWN_ATTACKS];

		// multiple pawn attacks possible
		if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
			score += THREATS[IX_MULTIPLE_PAWN_ATTACKS];
		}
		if (Long.bitCount(blackPawnAttacks & whites) > 1) {
			score -= THREATS[IX_MULTIPLE_PAWN_ATTACKS];
		}

		// minors under attack and not defended by a pawn
		score += Long.bitCount(whiteAttacks & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP) & ~blackAttacks))
				* THREATS[IX_MAJOR_ATTACKED];
		score -= Long.bitCount(blackAttacks & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP) & ~whiteAttacks))
				* THREATS[IX_MAJOR_ATTACKED];

		// pawn attacked
		score += Long.bitCount(whiteAttacks & blackPawns) * THREATS[IX_PAWN_ATTACKED];
		score -= Long.bitCount(blackAttacks & whitePawns) * THREATS[IX_PAWN_ATTACKED];

		if (cb.getPieces(BLACK, QUEEN) != 0) {
			// queen under attack by rook
			score += Long.bitCount(evalinfo.attacks[WHITE][ROOK] & cb.getPieces(BLACK, QUEEN)) * THREATS[IX_QUEEN_ATTACKED];
			// queen under attack by minors
			score += Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, QUEEN)) * THREATS[IX_QUEEN_ATTACKED_MINOR];
		}

		if (cb.getPieces(WHITE, QUEEN) != 0) {
			// queen under attack by rook
			score -= Long.bitCount(evalinfo.attacks[BLACK][ROOK] & cb.getPieces(WHITE, QUEEN)) * THREATS[IX_QUEEN_ATTACKED];
			// queen under attack by minors
			score -= Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, QUEEN)) * THREATS[IX_QUEEN_ATTACKED_MINOR];
		}

		// rook under attack by minors
		score += Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, ROOK)) * THREATS[IX_ROOK_ATTACKED];
		score -= Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, ROOK)) * THREATS[IX_ROOK_ATTACKED];

		// knight fork
		// skip when testing eval values because we break the loop if any fork has been found
		long forked;
		piece = evalinfo.attacks[WHITE][NIGHT] & ~blackAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = blacks & ~blackPawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(BLACK, KING) & forked) == 0) {
					score += THREATS[IX_NIGHT_FORK];
				} else {
					score += THREATS[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}
		piece = evalinfo.attacks[BLACK][NIGHT] & ~whiteAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = whites & ~whitePawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(WHITE, KING) & forked) == 0) {
					score -= THREATS[IX_NIGHT_FORK];
				} else {
					score -= THREATS[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}

		return score;
	}

	public static int calculateOthers(final IChessBoard cb) {
		int score = 0;
		long piece;

		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		// bonus for side to move
		score += COLOR_FACTOR[cb.getColorToMove()] * SIDE_TO_MOVE_BONUS;

		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP]);
		while (piece != 0) {
			score -= ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP]);
		while (piece != 0) {
			score += ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}

		// hanging pieces
		piece = whiteAttacks & blacks & ~blackAttacks;
		int hangingIndex;
		if (piece != 0) {
			if (Long.bitCount(piece) > 1) {
				hangingIndex = QUEEN;
				while (piece != 0) {
					hangingIndex = Math.min(hangingIndex, cb.getPieceType(Long.numberOfTrailingZeros(piece)));
					piece &= piece - 1;
				}

				score += HANGING_2[hangingIndex];
			} else {
				score += HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			}
		}
		piece = blackAttacks & whites & ~whiteAttacks;
		if (piece != 0) {
			if (Long.bitCount(piece) > 1) {
				hangingIndex = QUEEN;
				while (piece != 0) {
					hangingIndex = Math.min(hangingIndex, cb.getPieceType(Long.numberOfTrailingZeros(piece)));
					piece &= piece - 1;
				}
				score -= HANGING_2[hangingIndex];
			} else {
				score -= HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			}
		}

		// WHITE ROOK
		if (cb.getPieces(WHITE, ROOK) != 0) {

			piece = cb.getPieces(WHITE, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score += OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 7th, king on 8th
			if (cb.getKingIndex(BLACK) >= 56) {
				score += Long.bitCount(piece & RANK_7) * OTHER_SCORES[IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(WHITE)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped << i & whitePawns) != 0) {
						score -= ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += OTHER_SCORES[IX_ROOK_FILE_OPEN];
					} else if ((blackPawns & blackPawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score += OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
					}
				}

				piece &= piece - 1;
			}
		}

		// BLACK ROOK
		if (cb.getPieces(BLACK, ROOK) != 0) {

			piece = cb.getPieces(BLACK, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score -= OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 2nd, king on 1st
			if (cb.getKingIndex(WHITE) <= 7) {
				score -= Long.bitCount(piece & RANK_2) * OTHER_SCORES[IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(BLACK)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped >>> i & blackPawns) != 0) {
						score += ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				// TODO JITWatch unpredictable branch
				if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score -= OTHER_SCORES[IX_ROOK_FILE_OPEN];
					} else if ((whitePawns & whitePawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score -= OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score -= OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
					}
				}
				piece &= piece - 1;
			}

		}

		// WHITE BISHOP
		if (cb.getPieces(WHITE, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(WHITE, BISHOP) & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
			while (piece != 0) {
				score += BISHOP_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(WHITE, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					score -= OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(WHITE, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= BISHOP_PAWN[Long.bitCount(whitePawns & WHITE_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
			if ((cb.getPieces(WHITE, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= BISHOP_PAWN[Long.bitCount(whitePawns & BLACK_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}

		}

		// BLACK BISHOP
		if (cb.getPieces(BLACK, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(BLACK, BISHOP) & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
			while (piece != 0) {
				score -= BISHOP_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(BLACK, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					score += OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(BLACK, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += BISHOP_PAWN[Long.bitCount(blackPawns & WHITE_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
			if ((cb.getPieces(BLACK, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += BISHOP_PAWN[Long.bitCount(blackPawns & BLACK_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
		}

		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			score += PAWN_BLOCKAGE[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			score -= PAWN_BLOCKAGE[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = cb.getPieces(WHITE, NIGHT) & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
		while (piece != 0) {
			score += KNIGHT_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = cb.getPieces(BLACK, NIGHT) & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
		while (piece != 0) {
			score -= KNIGHT_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// penalty for having pinned-pieces
		if (cb.getPinnedPieces() != 0) {
			piece = cb.getPinnedPieces() & whites & ~blackPawnAttacks;
			while (piece != 0) {
				score -= PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & ~whitePawnAttacks;
			while (piece != 0) {
				score += PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}

			piece = cb.getPinnedPieces() & whites & blackPawnAttacks;
			while (piece != 0) {
				score -= PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & whitePawnAttacks;
			while (piece != 0) {
				score += PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// bonus for having discovered-pieces
		if (cb.getDiscoveredPieces() != 0) {
			piece = cb.getDiscoveredPieces() & whites;
			while (piece != 0) {
				score += DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getDiscoveredPieces() & blacks;
			while (piece != 0) {
				score -= DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// quiescence search could leave one side in check
		if (cb.getCheckingPieces() != 0) {
			score += COLOR_FACTOR[1 - cb.getColorToMove()] * IN_CHECK;
		}

		return score;
	}

	public static int calculatePawnShieldBonus(final IChessBoard cb) {

		int file;

		int whiteScore = 0;
		long piece = cb.getPieces(WHITE, PAWN) & cb.getKingArea(WHITE) & ~evalinfo.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore += SHIELD_BONUS[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~FILES[file];
		}
		if (cb.getPieces(BLACK, QUEEN) == 0) {
			whiteScore /= 2;
		}

		int blackScore = 0;
		piece = cb.getPieces(BLACK, PAWN) & cb.getKingArea(BLACK) & ~evalinfo.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore += SHIELD_BONUS[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~FILES[file];
		}
		if (cb.getPieces(WHITE, QUEEN) == 0) {
			blackScore /= 2;
		}

		return whiteScore - blackScore;
	}

	public static int calculateMobilityScoresAndSetAttackBoards(final IChessBoard cb) {

		// clear values
		evalinfo.clearEvalAttacks();

		long moves;

		// white pawns
		evalinfo.attacks[WHITE][PAWN] = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN) & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[WHITE][PAWN] & cb.getKingArea(BLACK)) != 0) {
			evalinfo.kingAttackersFlag[WHITE] = FLAG_PAWN;
		}
		long pinned = cb.getPieces(WHITE, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			evalinfo.attacks[WHITE][PAWN] |= PAWN_ATTACKS[WHITE][Long.numberOfTrailingZeros(pinned)]
					& PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(WHITE)];
			pinned &= pinned - 1;
		}
		evalinfo.attacksAll[WHITE] = evalinfo.attacks[WHITE][PAWN];
		// black pawns
		evalinfo.attacks[BLACK][PAWN] = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN) & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[BLACK][PAWN] & cb.getKingArea(WHITE)) != 0) {
			evalinfo.kingAttackersFlag[BLACK] = FLAG_PAWN;
		}
		pinned = cb.getPieces(BLACK, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			evalinfo.attacks[BLACK][PAWN] |= PAWN_ATTACKS[BLACK][Long.numberOfTrailingZeros(pinned)]
					& PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(BLACK)];
			pinned &= pinned - 1;
		}
		evalinfo.attacksAll[BLACK] = evalinfo.attacks[BLACK][PAWN];

		int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {

			int tempScore = 0;

			final long kingArea = cb.getKingArea(1 - color);
			final long safeMoves = ~cb.getFriendlyPieces(color) & ~evalinfo.attacks[1 - color][PAWN];

			// knights
			long piece = cb.getPieces(color, NIGHT) & ~cb.getPinnedPieces();
			while (piece != 0) {
				moves = KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= FLAG_NIGHT;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][NIGHT] |= moves;
				tempScore += MOBILITY_KNIGHT[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// bishops
			piece = cb.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = getBishopMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= FLAG_BISHOP;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][BISHOP] |= moves;
				tempScore += MOBILITY_BISHOP[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// rooks
			piece = cb.getPieces(color, ROOK);
			while (piece != 0) {
				moves = getRookMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, ROOK) ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= FLAG_ROOK;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][ROOK] |= moves;
				tempScore += MOBILITY_ROOK[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// queens
			piece = cb.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = getQueenMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces());
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= FLAG_QUEEN;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][QUEEN] |= moves;
				tempScore += MOBILITY_QUEEN[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			score += tempScore * COLOR_FACTOR[color];

		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = KING_MOVES[cb.getKingIndex(WHITE)] & ~KING_MOVES[cb.getKingIndex(BLACK)];
		evalinfo.attacks[WHITE][KING] = moves;
		evalinfo.doubleAttacks[WHITE] |= evalinfo.attacksAll[WHITE] & moves;
		evalinfo.attacksAll[WHITE] |= moves;
		score += MOBILITY_KING[Long.bitCount(moves & ~cb.getFriendlyPieces(WHITE) & ~evalinfo.attacksAll[BLACK])];

		// BLACK king
		moves = KING_MOVES[cb.getKingIndex(BLACK)] & ~KING_MOVES[cb.getKingIndex(WHITE)];
		evalinfo.attacks[BLACK][KING] = moves;
		evalinfo.doubleAttacks[BLACK] |= evalinfo.attacksAll[BLACK] & moves;
		evalinfo.attacksAll[BLACK] |= moves;
		score -= MOBILITY_KING[Long.bitCount(moves & ~cb.getFriendlyPieces(BLACK) & ~evalinfo.attacksAll[WHITE])];

		return score;
	}

	public static int calculatePositionScores(final IChessBoard cb) {

		int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {
			for (int pieceType = PAWN; pieceType <= KING; pieceType++) {
				long piece = cb.getPieces(color, pieceType);
				while (piece != 0) {
					score += PSQT[pieceType][color][Long.numberOfTrailingZeros(piece)];
					piece &= piece - 1;
				}
			}
		}
		return score;
	}

	public static int calculateMaterialScore(final IChessBoard cb) {
		return (Long.bitCount(cb.getPieces(WHITE, PAWN)) - Long.bitCount(cb.getPieces(BLACK, PAWN))) * MATERIAL[PAWN]
				+ (Long.bitCount(cb.getPieces(WHITE, NIGHT)) - Long.bitCount(cb.getPieces(BLACK, NIGHT))) * MATERIAL[NIGHT]
				+ (Long.bitCount(cb.getPieces(WHITE, BISHOP)) - Long.bitCount(cb.getPieces(BLACK, BISHOP))) * MATERIAL[BISHOP]
				+ (Long.bitCount(cb.getPieces(WHITE, ROOK)) - Long.bitCount(cb.getPieces(BLACK, ROOK))) * MATERIAL[ROOK]
				+ (Long.bitCount(cb.getPieces(WHITE, QUEEN)) - Long.bitCount(cb.getPieces(BLACK, QUEEN))) * MATERIAL[QUEEN];
	}

	
	public static int calculateKingSafetyScores(final IChessBoard cb) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			int counter = KS_RANK[(7 * kingColor) + COLOR_FACTOR[kingColor] * cb.getKingIndex(kingColor) / 8];

			counter += KS_NO_FRIENDS[Long.bitCount(cb.getKingArea(kingColor) & ~cb.getFriendlyPieces(kingColor))];
			counter += openFiles(cb, kingColor, cb.getPieces(kingColor, PAWN));

			// king can move?
			if ((evalinfo.attacks[kingColor][KING] & ~cb.getFriendlyPieces(kingColor)) == 0) {
				counter++;
			}
			counter += KS_ATTACKS[Long.bitCount(cb.getKingArea(kingColor) & evalinfo.attacksAll[enemyColor])];
			counter += checks(cb, kingColor);

			counter += KS_DOUBLE_ATTACKS[Long
					.bitCount(KING_MOVES[cb.getKingIndex(kingColor)] & evalinfo.doubleAttacks[enemyColor] & ~evalinfo.attacks[kingColor][PAWN])];

			if ((cb.getCheckingPieces() & cb.getFriendlyPieces(enemyColor)) != 0) {
				counter++;
			}

			// bonus for stm
			counter += 1 - cb.getColorToMove() ^ enemyColor;

			// bonus if there are discovered checks possible
			counter += Long.bitCount(cb.getDiscoveredPieces() & cb.getFriendlyPieces(enemyColor)) * 2;

			// pinned at first rank
			if ((cb.getPinnedPieces() & RANK_FIRST[kingColor]) != 0) {
				counter++;
			}

			if (cb.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
				// bonus for small king-queen distance
				if ((evalinfo.attacksAll[kingColor] & cb.getPieces(enemyColor, QUEEN)) == 0) {
					counter += KS_QUEEN_TROPISM[getDistance(cb.getKingIndex(kingColor),
							Long.numberOfTrailingZeros(cb.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += KS_ATTACK_PATTERN[evalinfo.kingAttackersFlag[enemyColor]];
			score += COLOR_FACTOR[enemyColor] * KS_SCORES[Math.min(counter, KS_SCORES.length - 1)];
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

		if ((RANK_FIRST[kingColor] & cb.getPieces(kingColor, KING)) != 0) {
			if ((KING_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((FILE_G & pawns) == 0 || (FILE_H & pawns) == 0) {
					return KS_OTHER[2];
				}
			} else if ((QUEEN_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((FILE_A & pawns) == 0 || (FILE_B & pawns) == 0) {
					return KS_OTHER[2];
				}
			}
		}
		return 0;
	}

	private static int checks(final IChessBoard cb, final int kingColor) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.getKingIndex(kingColor);
		final long possibleSquares = ~cb.getFriendlyPieces(enemyColor)
				& (~KING_MOVES[kingIndex] | KING_MOVES[kingIndex] & evalinfo.doubleAttacks[enemyColor] & ~evalinfo.doubleAttacks[kingColor]);

		int counter = checkNight(cb, kingColor, KNIGHT_MOVES[kingIndex] & possibleSquares & evalinfo.attacks[enemyColor][NIGHT]);

		long moves;
		long queenMoves = 0;
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, BISHOP)) != 0) {
			moves = getBishopMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(cb, kingColor, moves & evalinfo.attacks[enemyColor][BISHOP]);
		}
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) != 0) {
			moves = getRookMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(cb, kingColor, moves & evalinfo.attacks[enemyColor][ROOK]);
		}

		if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
			counter += safeCheckQueen(cb, kingColor, queenMoves & ~evalinfo.attacksAll[kingColor] & evalinfo.attacks[enemyColor][QUEEN]);
			counter += safeCheckQueenTouch(cb, kingColor);
		}

		return counter;
	}

	private static int safeCheckQueenTouch(final IChessBoard cb, final int kingColor) {
		if ((evalinfo.kingAttackersFlag[1 - kingColor] & FLAG_QUEEN) == 0) {
			return 0;
		}
		final int enemyColor = 1 - kingColor;
		if ((KING_MOVES[cb.getKingIndex(kingColor)] & ~cb.getFriendlyPieces(enemyColor) & evalinfo.attacks[enemyColor][QUEEN] & ~evalinfo.doubleAttacks[kingColor]
				& evalinfo.doubleAttacks[enemyColor]) != 0) {
			return KS_OTHER[0];
		}
		return 0;
	}

	private static int safeCheckQueen(final IChessBoard cb, final int kingColor, final long safeQueenMoves) {
		if (safeQueenMoves != 0) {
			return KS_CHECK_QUEEN[Long.bitCount(cb.getFriendlyPieces(kingColor))];
		}

		return 0;
	}

	private static int checkRook(final IChessBoard cb, final int kingColor, final long rookMoves) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
			counter += KS_CHECK[ROOK];

			// last rank?
			if (kingBlockedAtLastRank(kingColor, cb, KING_MOVES[cb.getKingIndex(kingColor)] & cb.getEmptySpaces() & ~evalinfo.attacksAll[1 - kingColor])) {
				counter += KS_OTHER[1];
			}

		} else {
			counter += KS_UCHECK[ROOK];
		}

		return counter;
	}

	private static int checkBishop(final IChessBoard cb, final int kingColor, final long bishopMoves) {
		if (bishopMoves != 0) {
			if ((bishopMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
				return KS_CHECK[BISHOP];
			} else {
				return KS_UCHECK[BISHOP];
			}
		}
		return 0;
	}

	private static int checkNight(final IChessBoard cb, final int kingColor, final long nightMoves) {
		if (nightMoves != 0) {
			if ((nightMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
				return KS_CHECK[NIGHT];
			} else {
				return KS_UCHECK[NIGHT];
			}
		}
		return 0;
	}

	private static boolean kingBlockedAtLastRank(final int kingColor, final IChessBoard cb, final long safeKingMoves) {
		return (RANKS[7 * kingColor] & cb.getPieces(kingColor, KING)) != 0 && (safeKingMoves & RANKS[7 * kingColor]) == safeKingMoves;
	}
	
	public static int calculatePassedPawnScores(final IChessBoard cb) {

		int score = 0;

		int whitePromotionDistance = SHORT_MAX;
		int blackPromotionDistance = SHORT_MAX;

		// white passed pawns
		long passedPawns = evalinfo.passedPawnsAndOutposts & cb.getPieces(WHITE, PAWN);
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			score += getPassedPawnScore(cb, index, WHITE);

			if (whitePromotionDistance == SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}

		// black passed pawns
		passedPawns = evalinfo.passedPawnsAndOutposts & cb.getPieces(BLACK, PAWN);
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			score -= getPassedPawnScore(cb, index, BLACK);

			if (blackPromotionDistance == SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}

		if (whitePromotionDistance < blackPromotionDistance - 1) {
			score += 350;
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			score -= 350;
		}

		return score;
	}

	private static int getPassedPawnScore(final IChessBoard cb, final int index, final int color) {

		final int nextIndex = index + COLOR_FACTOR_8[color];
		final long square = POWER_LOOKUP[index];
		final long maskNextSquare = POWER_LOOKUP[nextIndex];
		final long maskPreviousSquare = POWER_LOOKUP[index - COLOR_FACTOR_8[color]];
		final long maskFile = FILES[index & 7];
		final int enemyColor = 1 - color;
		float multiplier = 1;

		// is piece blocked?
		if ((cb.getAllPieces() & maskNextSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[0];
		}

		// is next squared attacked?
		if ((evalinfo.attacksAll[enemyColor] & maskNextSquare) == 0) {

			// complete path free of enemy attacks?
			if ((PINNED_MOVEMENT[nextIndex][index] & evalinfo.attacksAll[enemyColor]) == 0) {
				multiplier *= PASSED_MULTIPLIERS[7];
			} else {
				multiplier *= PASSED_MULTIPLIERS[1];
			}
		}

		// is next squared defended?
		if ((evalinfo.attacksAll[color] & maskNextSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[3];
		}

		// is enemy king in front?
		if ((PINNED_MOVEMENT[nextIndex][index] & cb.getPieces(enemyColor, KING)) != 0) {
			multiplier *= PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (cb.getColorToMove() != color && (evalinfo.attacksAll[enemyColor] & square) != 0) {
			multiplier *= PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & cb.getPieces(color, ROOK)) != 0 && (evalinfo.attacks[color][ROOK] & square) != 0 && (evalinfo.attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & cb.getPieces(enemyColor, ROOK)) != 0 && (evalinfo.attacks[enemyColor][ROOK] & square) != 0
				&& (evalinfo.attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= PASSED_KING_MULTI[getDistance(cb.getKingIndex(color), index)];
		multiplier *= PASSED_KING_MULTI[8 - getDistance(cb.getKingIndex(enemyColor), index)];

		final int scoreIndex = (7 * color) + COLOR_FACTOR[color] * index / 8;
		return Evaluator.score((int) (PASSED_SCORE_MG[scoreIndex] * multiplier), (int) (PASSED_SCORE_EG[scoreIndex] * multiplier));
	}

	private static int getBlackPromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = index >>> 3;
		if (promotionDistance == 1 && cb.getColorToMove() == BLACK) {
			if ((POWER_LOOKUP[index - 8] & (evalinfo.attacksAll[WHITE] | cb.getAllPieces())) == 0) {
				if ((POWER_LOOKUP[index] & evalinfo.attacksAll[WHITE]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}

	private static int getWhitePromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = 7 - index / 8;
		if (promotionDistance == 1 && cb.getColorToMove() == WHITE) {
			if ((POWER_LOOKUP[index + 8] & (evalinfo.attacksAll[BLACK] | cb.getAllPieces())) == 0) {
				if ((POWER_LOOKUP[index] & evalinfo.attacksAll[BLACK]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}
	
	private static class EvalInfo {

		// attack boards
		public final long[][] attacks = new long[2][7];
		public final long[] attacksAll = new long[2];
		public final long[] doubleAttacks = new long[2];
		public final int[] kingAttackersFlag = new int[2];

		public long passedPawnsAndOutposts;
		
		public void clearEvalAttacks() {
			kingAttackersFlag[Evaluator.WHITE] = 0;
			kingAttackersFlag[Evaluator.BLACK] = 0;
			attacks[Evaluator.WHITE][Evaluator.NIGHT] = 0;
			attacks[Evaluator.BLACK][Evaluator.NIGHT] = 0;
			attacks[Evaluator.WHITE][Evaluator.BISHOP] = 0;
			attacks[Evaluator.BLACK][Evaluator.BISHOP] = 0;
			attacks[Evaluator.WHITE][Evaluator.ROOK] = 0;
			attacks[Evaluator.BLACK][Evaluator.ROOK] = 0;
			attacks[Evaluator.WHITE][Evaluator.QUEEN] = 0;
			attacks[Evaluator.BLACK][Evaluator.QUEEN] = 0;
			doubleAttacks[Evaluator.WHITE] = 0;
			doubleAttacks[Evaluator.BLACK] = 0;
		}
	}
}
