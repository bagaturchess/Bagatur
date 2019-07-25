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
package bagaturchess.learning.goldmiddle.impl3.eval;


public class Magics {
	
	
	public static final long[] POWER_LOOKUP = new long[64];
	static {
		for (int i = 0; i < 64; i++) {
			POWER_LOOKUP[i] = 1L << i;
		}
	}
	
	
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

	public static long getRookMoves(final int fromIndex, final long occupied) {
		return rookMagicMoves[fromIndex][(int) ((occupied & rookMovementMasks[fromIndex]) * rookMagicNumbers[fromIndex] >>> rookShifts[fromIndex])];
	}

	public static long getBishopMoves(final int fromIndex, final long occupied) {
		return bishopMagicMoves[fromIndex][(int) ((occupied & bishopMovementMasks[fromIndex]) * bishopMagicNumbers[fromIndex] >>> bishopShifts[fromIndex])];
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
