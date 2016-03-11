/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class KnightChecks extends KnightPlies {
	
	public static int[][][] CHECK_MIDDLE_FIELDS_IDS = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static int[][][] CHECK_MIDDLE_FIELDS_DIR_ID = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static long[][][] CHECK_MIDDLE_FIELDS_BITBOARDS = new long[Bits.PRIME_67][Bits.PRIME_67][];
	public static long[] FIELDS_ATTACK_2 = new long[Bits.PRIME_67];
	
	static {
		genAll_Dynamic();
	}
	
	public static void initPair_Dynamic(int fromFieldID, int toFieldID) {
		final int[] fromFieldValidDirIDs = ALL_KNIGHT_VALID_DIRS[fromFieldID];
		final int[][] middleFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
		long[][] middleFieldBitboards = ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size1 = fromFieldValidDirIDs.length;
		for (int i=0; i<size1; i++) {
			
			int dirID1 = fromFieldValidDirIDs[i];
			int middleFieldID = middleFieldIDs[dirID1][0];
			long middleFieldBitboard = middleFieldBitboards[dirID1][0];
			
			final int[] middleFieldValidDirIDs = ALL_KNIGHT_VALID_DIRS[middleFieldID];
			final int[][] toFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS[middleFieldID];
			final long[][] toFieldBitboards = ALL_KNIGHT_DIRS_WITH_BITBOARDS[middleFieldID];
			
			final int size2 = middleFieldValidDirIDs.length;
			for (int j=0; j<size2; j++) {
				
				int dirID2 = middleFieldValidDirIDs[j];
				int cur_toFieldID = toFieldIDs[dirID2][0];
				
				long endFieldBitboard = toFieldBitboards[dirID2][0];
				FIELDS_ATTACK_2[fromFieldID] |= endFieldBitboard;
				
				if (toFieldID == cur_toFieldID) {
					int[] list1 = CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
					int[] list2 = CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID];
					long[] list3 = CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
					
					if (list1 == null) {
						CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = new int[1];
						CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][0] = middleFieldID;
						
						CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = new int[1];
						CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] = dirID1;
						
						CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = new long[1];
						CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][0] = middleFieldBitboard;
					} else if (list1.length == 1) {
						CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = new int[2];
						CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][0] = list1[0];
						CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][1] = middleFieldID;
						
						CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = new int[2];
						CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] = list2[0];
						CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][1] = dirID1;
						
						CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = new long[2];
						CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][0] = list3[0];
						CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][1] = middleFieldBitboard;
					} else {
						throw new IllegalStateException();
					}
				}
			}	
		}
	}
	
	public static void genAll_Dynamic() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				if (fromID != toID) {
					initPair_Dynamic(fromID, toID);
				}
			}
		}
	}
	
	public static String testChecks(int fromFieldID, int toFieldID) {
		String result = "Knight checks from " + getFieldSign_UC(fromFieldID)
			+ " to " + getFieldSign_UC(toFieldID) +  " -> ";
		int[] fields =  CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
		long[] fieldBoards =  CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
		
		if (fields == null && fieldBoards != null) {
			throw new IllegalStateException();
		} else if (fields != null && fieldBoards == null) {
			throw new IllegalStateException();
		} else if (fields != null) {
			if (fields.length != fieldBoards.length) {
				throw new IllegalStateException();
			} else {
				for (int i=0; i<fields.length; i++) {
					if (ALL_A1H1[fields[i]] != fieldBoards[i]) {
						throw new IllegalStateException(Bits.toBinaryStringMatrix(fieldBoards[i]));
					}
				}
			}
		}
		
		if (fields == null) {
			result += "NO";
		} else if (fields.length == 1) {
			result += getFieldSign_UC(fields[0]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] + ")";
		} else if (fields.length == 2) {
			result += getFieldSign_UC(fields[0]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] + ")"
			+ " and " + getFieldSign_UC(fields[1]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][1] + ")";
		} else {
			throw new IllegalStateException();
		}
		
		//result += Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[fromFieldID][toFieldID]);
		
		return result;
	}
	
	public static void testAll() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				System.out.println(testChecks(fromID, toID));
			}
		}
	}
	
	public static void main(String[] args) {
		int from = get67IDByBitboard(E1);
		int to = get67IDByBitboard(E5);
		System.out.println(testChecks(from, to));
		System.out.println(Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[from]));
		//testAll();
	}
}
