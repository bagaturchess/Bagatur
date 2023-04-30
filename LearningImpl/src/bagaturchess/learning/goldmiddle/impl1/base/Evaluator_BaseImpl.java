/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.learning.goldmiddle.impl1.base;

import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.BLACK;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.WHITE;


public class Evaluator_BaseImpl {
	
	
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
	
	
	//START StaticMoves
	public static final long[] KNIGHT_MOVES = new long[64];
	public static final long[] KING_MOVES = new long[64];

	public static final long[][] PAWN_ATTACKS = new long[2][64];

	// PAWN
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

	// knight
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

	// king
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
	//END StaticMoves
	
	
	//START Magic
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
			int variationCount = (int) POWER_LOOKUP[Long.bitCount(movementMasks[index])];
			occupancyVariations[index] = new long[variationCount];

			for (int variationIndex = 1; variationIndex < variationCount; variationIndex++) {
				long currentMask = movementMasks[index];

				for (int i = 0; i < 32 - Integer.numberOfLeadingZeros(variationIndex); i++) {
					if ((POWER_LOOKUP[i] & variationIndex) != 0) {
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
				rookMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// down
			for (int j = index - 8; j >= 0 + 8; j -= 8) {
				rookMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// left
			for (int j = index + 1; j % 8 != 0 && j % 8 != 7; j++) {
				rookMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// right
			for (int j = index - 1; j % 8 != 7 && j % 8 != 0 && j > 0; j--) {
				rookMovementMasks[index] |= POWER_LOOKUP[j];
			}
		}
	}

	private static void calculateBishopMovementMasks() {
		for (int index = 0; index < 64; index++) {

			// up-right
			for (int j = index + 7; j < 64 - 7 && j % 8 != 7 && j % 8 != 0; j += 7) {
				bishopMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// up-left
			for (int j = index + 9; j < 64 - 9 && j % 8 != 7 && j % 8 != 0; j += 9) {
				bishopMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// down-right
			for (int j = index - 9; j >= 0 + 9 && j % 8 != 7 && j % 8 != 0; j -= 9) {
				bishopMovementMasks[index] |= POWER_LOOKUP[j];
			}
			// down-left
			for (int j = index - 7; j >= 0 + 7 && j % 8 != 7 && j % 8 != 0; j -= 7) {
				bishopMovementMasks[index] |= POWER_LOOKUP[j];
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
					validMoves |= POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 8; j >= 0; j -= 8) {
					validMoves |= POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index + 1; j % 8 != 0; j++) {
					validMoves |= POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 1; j % 8 != 7 && j >= 0; j--) {
					validMoves |= POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
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
					validMoves |= POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// up-left
				for (int j = index + 9; j % 8 != 0 && j < 64; j += 9) {
					validMoves |= POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-right
				for (int j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
					validMoves |= POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-left
				for (int j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
					validMoves |= POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & POWER_LOOKUP[j]) != 0) {
						break;
					}
				}

				bishopMagicMoves[index][magicIndex] = validMoves;
			}
		}
	}
	//END Magic
}
