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
package bagaturchess.bitboard.impl.plies.specials;


import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;


public class Enpassanting extends Fields {
	
	
	public static final int ENPASSANT_DIR_ID = 1; 
	
	
	public static final long[][] ADJOINING_FILES = new long[Figures.COLOUR_MAX][Bits.PRIME_67];
	static {
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(A4)] = B4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(B4)] = A4 | C4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(C4)] = B4 | D4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(D4)] = C4 | E4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(E4)] = D4 | F4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(F4)] = E4 | G4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(G4)] = F4 | H4;
		ADJOINING_FILES[Figures.COLOUR_WHITE][get67IDByBitboard(H4)] = G4;
		
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(A5)] = B5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(B5)] = A5 | C5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(C5)] = B5 | D5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(D5)] = C5 | E5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(E5)] = D5 | F5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(F5)] = E5 | G5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(G5)] = F5 | H5;
		ADJOINING_FILES[Figures.COLOUR_BLACK][get67IDByBitboard(H5)] = G5;
	}
	
	public static final long[][][] ADJOINING_FILE_BITBOARD_AT_CAPTURE = new long[Figures.COLOUR_MAX][Bits.PRIME_67][2];
	static {
		//White
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(A5)][1] = B5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(B5)][0] = A5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(B5)][1] = C5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(C5)][0] = B5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(C5)][1] = D5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(D5)][0] = C5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(D5)][1] = E5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(E5)][0] = D5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(E5)][1] = F5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(F5)][0] = E5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(F5)][1] = G5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(G5)][0] = F5;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(G5)][1] = H5;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(H5)][0] = G5;
		
		//Black
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(A4)][1] = B4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(B4)][0] = A4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(B4)][1] = C4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(C4)][0] = B4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(C4)][1] = D4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(D4)][0] = C4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(D4)][1] = E4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(E4)][0] = D4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(E4)][1] = F4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(F4)][0] = E4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(F4)][1] = G4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(G4)][0] = F4;
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(G4)][1] = H4;
		
		ADJOINING_FILE_BITBOARD_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(H4)][0] = G4;
	}
	
	public static final int[][][] ADJOINING_FILE_FIELD_ID_AT_CAPTURE = new int[Figures.COLOUR_MAX][Bits.PRIME_67][2];
	static {
		//White
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(A5)][1] = get67IDByBitboard(B5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(B5)][0] = get67IDByBitboard(A5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(B5)][1] = get67IDByBitboard(C5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(C5)][0] = get67IDByBitboard(B5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(C5)][1] = get67IDByBitboard(D5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(D5)][0] = get67IDByBitboard(C5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(D5)][1] = get67IDByBitboard(E5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(E5)][0] = get67IDByBitboard(D5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(E5)][1] = get67IDByBitboard(F5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(F5)][0] = get67IDByBitboard(E5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(F5)][1] = get67IDByBitboard(G5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(G5)][0] = get67IDByBitboard(F5);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(G5)][1] = get67IDByBitboard(H5);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_WHITE][get67IDByBitboard(H5)][0] = get67IDByBitboard(G5);
		
		//Black
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(A4)][1] = get67IDByBitboard(B4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(B4)][0] = get67IDByBitboard(A4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(B4)][1] = get67IDByBitboard(C4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(C4)][0] = get67IDByBitboard(B4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(C4)][1] = get67IDByBitboard(D4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(D4)][0] = get67IDByBitboard(C4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(D4)][1] = get67IDByBitboard(E4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(E4)][0] = get67IDByBitboard(D4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(E4)][1] = get67IDByBitboard(F4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(F4)][0] = get67IDByBitboard(E4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(F4)][1] = get67IDByBitboard(G4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(G4)][0] = get67IDByBitboard(F4);
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(G4)][1] = get67IDByBitboard(H4);
		
		ADJOINING_FILE_FIELD_ID_AT_CAPTURE[Figures.COLOUR_BLACK][get67IDByBitboard(H4)][0] = get67IDByBitboard(G4);
	}
	public static long getEnemyBitboard(String enpassantTargetSquar) {
		int enpassTargetFieldID = getFieldID(enpassantTargetSquar);
		long enpassTargetBoard = Fields.ALL_ORDERED_A1H1[enpassTargetFieldID];
		
		//0-7, 8-15, 16-23, 24-31, 32-39, 40-47, 48-55, 56-63
		if (enpassTargetFieldID >= 16 && enpassTargetFieldID <= 23) {
			enpassTargetBoard = enpassTargetBoard >> 8;
		} else if (enpassTargetFieldID >= 40 && enpassTargetFieldID <= 47) {
			enpassTargetBoard = enpassTargetBoard << 8;
		} else {
			throw new IllegalStateException("enpassTargetFieldID=" + enpassTargetFieldID);
		}
		
		return enpassTargetBoard;
	}
	
	public static int converteEnpassantTargetToFENFormat(long enpassantTargetSquare) {
		int enpassTargetFieldID = get67IDByBitboard(enpassantTargetSquare);
		//System.out.println(Fields.toBinaryStringMatrix(enpassantTargetSquare));
		
		if (enpassTargetFieldID <= 31) {
			enpassTargetFieldID -= 8;
		} else {
			enpassTargetFieldID += 8;
		}
		
		return enpassTargetFieldID;
	}
}
