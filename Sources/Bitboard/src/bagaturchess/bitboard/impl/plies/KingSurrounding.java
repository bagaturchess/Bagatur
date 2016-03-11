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
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class KingSurrounding extends Fields {
	
	public static final long[] SURROUND_LEVEL1 = new long[PRIME_67];
	public static final long[] SURROUND_LEVEL2 = new long[PRIME_67];
	
	public static final int[][] W_BACK = new int[PRIME_67][0];
	public static final int[][] B_BACK = new int[PRIME_67][0];
	public static final int[][] W_FRONT = new int[PRIME_67][0];
	public static final int[][] B_FRONT = new int[PRIME_67][0];
	public static final int[][] W_FFRONT = new int[PRIME_67][0];
	public static final int[][] B_FFRONT = new int[PRIME_67][0];
	
	private static void add(int[][] arr, int idx, long fieldBoard) {
		int[] fieldIDs = arr[idx];
		//if (fieldIDs == null) {
		//	fieldIDs = new int[0]; 
		//}
		
		int newSize = fieldIDs.length + 1;
		int[] newArr = new int[newSize];
		for (int i=0; i<fieldIDs.length; i++) {
			newArr[i] = fieldIDs[i];
		}
		newArr[fieldIDs.length] = get67IDByBitboard(fieldBoard);
		
		arr[idx] = newArr; 
	}
	
	static {
		
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			
			long a1h1 = ALL_ORDERED_A1H1[i];
			//System.out.println("field=" + Move.toBinaryStringMatrix(a1h1));
			
			int id = get67IDByBitboard(a1h1);
			
			long w_back = 0L;
			if ((a1h1 & DIGIT_1) == 0L) {
				w_back |= (a1h1 << 8);
				add(W_BACK, id, a1h1 << 8);
				
				if ((a1h1 & LETTER_H) == 0L) {
					w_back |= a1h1 >>> 1;
					add(W_BACK, id, a1h1 >>> 1);
					w_back |= a1h1 << 7;
					add(W_BACK, id, a1h1 << 7);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					w_back |= a1h1 << 1;
					add(W_BACK, id, a1h1 << 1);
					w_back |= a1h1 << 9;
					add(W_BACK, id, a1h1 << 9);
				}
			} else {
				if ((a1h1 & LETTER_H) == 0L) {
					w_back |= a1h1 >>> 1;
					add(W_BACK, id, a1h1 >>> 1);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					w_back |= a1h1 << 1;
					add(W_BACK, id, a1h1 << 1);
				}
			}
			//System.out.println("w_back=" + Move.toBinaryStringMatrix(w_back));
			
			long w_front = 0L;
			if ((a1h1 & DIGIT_8) == 0L) {
				w_front |= (a1h1 >>> 8);
				add(W_FRONT, id, a1h1 >>> 8);
				
				if ((a1h1 & LETTER_H) == 0L) {
					w_front |= a1h1 >>> 9;
					add(W_FRONT, id, a1h1 >>> 9);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					w_front |= a1h1 >>> 7;
					add(W_FRONT, id, a1h1 >>> 7);
				}
			}
			//System.out.println("w_front=" + Move.toBinaryStringMatrix(w_front));
			
			long w_ffront = 0L;
			if ((a1h1 & DIGIT_8) == 0L && (a1h1 & DIGIT_7) == 0L) {
				w_ffront |= (a1h1 >>> 16);
				add(W_FFRONT, id, a1h1 >>> 16);
				
				if ((a1h1 & LETTER_H) == 0L) {
					w_ffront |= a1h1 >>> 17;
					add(W_FFRONT, id, a1h1 >>> 17);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					w_ffront |= a1h1 >>> 15;
					add(W_FFRONT, id, a1h1 >>> 15);
				}
			}
			//System.out.println("w_ffront=" + Move.toBinaryStringMatrix(w_ffront));
			
			long b_back = 0;
			if ((a1h1 & DIGIT_8) == 0L) {
				b_back |= (a1h1 >>> 8);
				add(B_BACK, id, a1h1 >>> 8); 
				
				if ((a1h1 & LETTER_H) == 0L) {
					b_back |= a1h1 >>> 1;
					add(B_BACK, id, a1h1 >>> 1); 
					b_back |= a1h1 >>> 9;
					add(B_BACK, id, a1h1 >>> 9); 
				}
				if ((a1h1 & LETTER_A) == 0L) {
					b_back |= a1h1 << 1;
					add(B_BACK, id, a1h1 << 1); 
					b_back |= a1h1 >>> 7;
					add(B_BACK, id, a1h1 >>> 7); 
				}
			} else {
				if ((a1h1 & LETTER_H) == 0L) {
					b_back |= a1h1 >>> 1;
					add(B_BACK, id, a1h1 >>> 1); 
				}
				if ((a1h1 & LETTER_A) == 0L) {
					b_back |= a1h1 << 1;
					add(B_BACK, id, a1h1 << 1); 
				}
			}
			//System.out.println("b_back=" + Move.toBinaryStringMatrix(b_back));
			
			long b_front = 0L;
			if ((a1h1 & DIGIT_1) == 0L) {
				b_front |= (a1h1 << 8);
				add(B_FRONT, id, a1h1 << 8);
				
				if ((a1h1 & LETTER_H) == 0L) {
					b_front |= a1h1 << 7;
					add(B_FRONT, id, a1h1 << 7);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					b_front |= a1h1 << 9;
					add(B_FRONT, id, a1h1 << 9);
				}
			}
			//System.out.println("b_front=" + Move.toBinaryStringMatrix(b_front));
			
			long b_ffront = 0L;
			if ((a1h1 & DIGIT_1) == 0L && (a1h1 & DIGIT_2) == 0L) {
				b_ffront |= (a1h1 << 16);
				add(B_FFRONT, id, a1h1 << 16);
				
				if ((a1h1 & LETTER_H) == 0L) {
					b_ffront |= a1h1 << 15;
					add(B_FFRONT, id, a1h1 << 15);
				}
				if ((a1h1 & LETTER_A) == 0L) {
					b_ffront |= a1h1 << 17;
					add(B_FFRONT, id, a1h1 << 17);
				}
			}
			//System.out.println("b_ffront=" + Move.toBinaryStringMatrix(b_ffront));
		}
		
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			long result = KingPlies.ALL_KING_MOVES[id];
			
			if ((result & Fields.LETTER_H) != 0 & (result & Fields.LETTER_F) == 0) {
				result |= result << 1;
			}
			if ((result & Fields.LETTER_A) != 0 & (result & Fields.LETTER_C) == 0) {
				result |= result >> 1;
			}
			
			SURROUND_LEVEL1[id] = result;
			
			//System.out.println("FIELD  " + Bits.toBinaryString(a1h1));
			//System.out.println("LEVEL1 " + Bits.toBinaryString(SURROUND_LEVEL1[id]));
		}
		
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			
			long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[id];
			
			long officerMoves = 0;
			
			int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[id];
			long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[id];
			
			int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				int dirID = validDirIDs[dir];
				//long[] dirBitboards = dirs[dirID];
				int seq = 1;
				if (dirs[dirID].length > 1) {
					long toBitboard = dirs[dirID][seq];
					officerMoves |= toBitboard;
				}
			}
			
			long castleMoves = 0;
			
			validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[id];
			dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[id];
			
			size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				int dirID = validDirIDs[dir];
				//long[] dirBitboards = dirs[dirID];
				int seq = 1;
				if (dirs[dirID].length > 1) {
					long toBitboard = dirs[dirID][seq];
					castleMoves |= toBitboard;
				}
			}
			
			long result = knightMoves | officerMoves | castleMoves;
			
			if ((result & Fields.LETTER_H) != 0 & (result & Fields.LETTER_E) == 0) {
				result |= result << 1;
			}
			if ((result & Fields.LETTER_A) != 0 & (result & Fields.LETTER_D) == 0) {
				result |= result >> 1;
			}
			
			SURROUND_LEVEL2[id] = result;
			
			//System.out.println("FIELD  " + Bits.toBinaryString(a1h1));
			//System.out.println("LEVEL2 " + Bits.toBinaryString(SURROUND_LEVEL2[id]));
		}
	}
	
    public static void main(String[] args) {
    	new KingSurrounding();
    }
}
