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
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;

public class BlackPawnsChecks extends BlackPawnPlies {
	
	public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_IDS = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_SEQS = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static long[][][] CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS = new long[Bits.PRIME_67][Bits.PRIME_67][];
	
	public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_IDS = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_SEQS = new int[Bits.PRIME_67][Bits.PRIME_67][];
	public static long[][][] CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS = new long[Bits.PRIME_67][Bits.PRIME_67][];
	
	/**
	 * Without promotions and enpassant
	 */
	public static long[] FIELDS_ATTACK_2_ALL = new long[Bits.PRIME_67];
	public static long[] FIELDS_ATTACK_2_CAPTURES = new long[Bits.PRIME_67];
	public static long[] FIELDS_ATTACK_2_NONCAPTURES = new long[Bits.PRIME_67];
	
	public static long[] FIELDS_ATTACKERS_2 = new long[Bits.PRIME_67];
	
	static {
		try {
			genAll_Attacks_Dynamic();
			genAll_NonAttacks_Dynamic();
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void initPair_Attacks_Dynamic(int fromFieldID, int toFieldID) {
		final int[] fromFieldValidDirIDs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[fromFieldID];
		final int[][] middleFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[fromFieldID];
		long[][] middleFieldBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size1 = fromFieldValidDirIDs.length;
		for (int i=0; i<size1; i++) {
			
			int dirID1 = fromFieldValidDirIDs[i];

			int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
			long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
			
			long path1 = 0L;
			for (int seq1=0; seq1<middleFieldIDsByDir.length; seq1++) {
				
				int middleFieldID = middleFieldIDsByDir[seq1];
				long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
				
				final int[] middleFieldValidDirIDs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[middleFieldID];
				final int[][] toFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[middleFieldID];
				final long[][] toFieldBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[middleFieldID];
				
				final int size2 = middleFieldValidDirIDs.length;
				for (int j=0; j<size2; j++) {
					
					int dirID2 = middleFieldValidDirIDs[j];
					
					int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
					long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
					long path2 = 0L;
					for (int seq2=0; seq2<cur_toFieldIDsByDir.length; seq2++) {
						
						int cur_toFieldID = cur_toFieldIDsByDir[seq2];
						long endFieldBitboard = endFieldBitboardsByDir[seq2];
						
						FIELDS_ATTACK_2_ALL[fromFieldID] |= endFieldBitboard;
						FIELDS_ATTACK_2_CAPTURES[fromFieldID] |= endFieldBitboard;
						
						if (toFieldID == cur_toFieldID) {
							
							FIELDS_ATTACKERS_2[toFieldID] |= ALL_A1H1[fromFieldID];
							
							if (CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null
									|| CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 1) {
								
								CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID]
								    = extendArray(CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
								CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID]
								 	= extendArray(CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
								CHECK_ATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID]
								    = extendArray(CHECK_ATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
								
								CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID]
								    = extendArray(CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
								/*FIELDS_ATTACK_PATH1[fromFieldID][toFieldID]
										= extendArray(FIELDS_ATTACK_PATH1[fromFieldID][toFieldID], path1);
								FIELDS_ATTACK_PATH2[fromFieldID][toFieldID]
										= extendArray(FIELDS_ATTACK_PATH2[fromFieldID][toFieldID], path2);
								FIELDS_ATTACK_WHOLE_PATH[fromFieldID][toFieldID]
										= extendArray(FIELDS_ATTACK_WHOLE_PATH[fromFieldID][toFieldID], path1 | path2);*/
							} else {
								throw new IllegalStateException();
							}
							break;
						} else {
							path2 |= endFieldBitboard;
						}
					}
				}
				
				path1 |= middleFieldBitboard;
			}
		}
	}
	
	public static void genAll_Attacks_Dynamic() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				if (fromID != toID) {
					initPair_Attacks_Dynamic(fromID, toID);
				}
			}
		}
	}
	
	public static void initPair_NonAttacks_Dynamic(int fromFieldID, int toFieldID) {
		final int[] fromFieldValidDirIDs = ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS[fromFieldID];
		final int[][] middleFieldIDs = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS[fromFieldID];
		long[][] middleFieldBitboards = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size1 = fromFieldValidDirIDs.length;
		for (int i=0; i<size1; i++) {
			
			int dirID1 = fromFieldValidDirIDs[i];

			int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
			long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
			
			long path1 = 0L;
			for (int seq1=0; seq1<middleFieldIDsByDir.length; seq1++) {
				
				int middleFieldID = middleFieldIDsByDir[seq1];
				long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
				
				final int[] middleFieldValidDirIDs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[middleFieldID];
				final int[][] toFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[middleFieldID];
				final long[][] toFieldBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[middleFieldID];
				
				final int size2 = middleFieldValidDirIDs.length;
				for (int j=0; j<size2; j++) {
					
					int dirID2 = middleFieldValidDirIDs[j];
					
					int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
					long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
					long path2 = 0L;
					for (int seq2=0; seq2<cur_toFieldIDsByDir.length; seq2++) {
						
						int cur_toFieldID = cur_toFieldIDsByDir[seq2];
						long endFieldBitboard = endFieldBitboardsByDir[seq2];
							
						FIELDS_ATTACK_2_ALL[fromFieldID] |= endFieldBitboard;
						FIELDS_ATTACK_2_NONCAPTURES[fromFieldID] |= endFieldBitboard;
						
						if (toFieldID == cur_toFieldID) {
							
							FIELDS_ATTACKERS_2[toFieldID] |= ALL_A1H1[fromFieldID];
							
							if (CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null
									|| CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 1) {
								
								CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID]
								    = extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
								CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID]
								 	= extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
								CHECK_NONATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID]
								    = extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
								
								CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID]
								    = extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
							} else {
								throw new IllegalStateException();
							}
							break;
						} else {
							path2 |= endFieldBitboard;
						}
					}
				}
				
				path1 |= middleFieldBitboard;
			}
		}
	}
	
	public static void genAll_NonAttacks_Dynamic() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				if (fromID != toID) {
					initPair_NonAttacks_Dynamic(fromID, toID);
				}
			}
		}
	}
	
	private static long[] extendArray(long[] source, long el) {
		long[] result = null;
		if (source != null) {
			result = new long[source.length + 1];
			System.arraycopy(source, 0, result, 0, source.length);
			result[source.length] = el;
		} else {
			result = new long[1];
			result[0] = el;
		}
		return result;
	}
	
	private static int[] extendArray(int[] source, int el) {
		int[] result = null;
		if (source != null) {
			result = new int[source.length + 1];
			System.arraycopy(source, 0, result, 0, source.length);
			result[source.length] = el;
		} else {
			result = new int[1];
			result[0] = el;
		}
		return result;
	}
	
	public static String testAttackChecks(int fromFieldID, int toFieldID) {
		String result = "White pawn atack checks from " + getFieldSign_UC(fromFieldID)
			+ " to " + getFieldSign_UC(toFieldID) + " -> ";
		int[] fields =  CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
		long[] fieldBoards =  CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
		
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
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(fieldBoards[i]));
					}
					/*if ((ALL_A1H1[fields[i]] & ALL_WHITE_FIELDS) != 0L && FIELDS_ATTACK_2[fromFieldID][toFieldID] != ALL_WHITE_FIELDS) {
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[fromFieldID][toFieldID]));
					}
					if ((ALL_A1H1[fields[i]] & ALL_BLACK_FIELDS) != 0L && FIELDS_ATTACK_2[fromFieldID][toFieldID] != ALL_BLACK_FIELDS) {
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[fromFieldID][toFieldID]));
					}*/
				}
			}
		}
		
		if (fields == null) {
			result += "NO";
		} else if (fields.length > 2) {
			throw new IllegalStateException();
		} else {
			for (int i=0; i<fields.length; i++) {
				result += getFieldSign_UC(fields[i]) + ", ";
			}
			
			/**
			 * Dump
			 */
			if (fields != null) {
				result += "\r\n";
				for (int i=0; i<fields.length; i++) {
					result += Bits.toBinaryStringMatrix(FIELDS_ATTACK_2_CAPTURES[fromFieldID]) + "\r\n";
				}
			}
			System.out.println(result);
		}
		
		return result;
	}	
	
	public static String testNonAttackChecks(int fromFieldID, int toFieldID) {
		String result = "White pawn nonattack checks from " + getFieldSign_UC(fromFieldID)
			+ " to " + getFieldSign_UC(toFieldID) + " -> ";
		int[] fields =  CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
		long[] fieldBoards =  CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
		
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
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(fieldBoards[i]));
					}
					/*if ((ALL_A1H1[fields[i]] & ALL_WHITE_FIELDS) != 0L && FIELDS_ATTACK_2[fromFieldID][toFieldID] != ALL_WHITE_FIELDS) {
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[fromFieldID][toFieldID]));
					}
					if ((ALL_A1H1[fields[i]] & ALL_BLACK_FIELDS) != 0L && FIELDS_ATTACK_2[fromFieldID][toFieldID] != ALL_BLACK_FIELDS) {
						throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[fromFieldID][toFieldID]));
					}*/
				}
			}
		}
		
		if (fields == null) {
			result += "NO";
		} else if (fields.length > 1) {
			throw new IllegalStateException();
		} else {
			for (int i=0; i<fields.length; i++) {
				result += getFieldSign_UC(fields[i]) + ", ";
			}
			
			/**
			 * Dump
			 */
			if (fields != null) {
				result += "\r\n";
				for (int i=0; i<fields.length; i++) {
					result += Bits.toBinaryStringMatrix(FIELDS_ATTACK_2_NONCAPTURES[fromFieldID]) + "\r\n";
				}
			}
			System.out.println(result);
		}
		
		return result;
	}	

	
	public static void testAll() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				testAttackChecks(fromID, toID);
				testNonAttackChecks(fromID, toID);
			}
		}
	}
	
	public static void main(String[] args) {
		int from = get67IDByBitboard(E2);
		int to = get67IDByBitboard(E5);
		//System.out.println(testChecks(from, to));
		//System.out.println(Bits.toBinaryStringMatrix(FIELDS_ATTACK_2_CAPTURES[from]));
		testAll();
	}
}
