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
package bagaturchess.bitboard.impl_kingcaptureallowed.plies;


import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;


public class Enpassanting extends Fields {
	
	
	public static final int ENPASSANT_DIR_ID = 1; 
	
	
	public static final int[][][] ADJOINING_FILES_FIELD_IDS = new int[Figures.COLOUR_MAX][Bits.PRIME_67][];
	static {
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][A4_ID] = new int[] {B4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][B4_ID] = new int[] {A4_ID, C4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][C4_ID] = new int[] {B4_ID, D4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][D4_ID] = new int[] {C4_ID, E4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][E4_ID] = new int[] {D4_ID, F4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][F4_ID] = new int[] {E4_ID, G4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][G4_ID] = new int[] {F4_ID, H4_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_WHITE][H4_ID] = new int[] {G4_ID};
		
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][A5_ID] = new int[] {B5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][B5_ID] = new int[] {A5_ID, C5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][C5_ID] = new int[] {B5_ID, D5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][D5_ID] = new int[] {C5_ID, E5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][E5_ID] = new int[] {D5_ID, F5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][F5_ID] = new int[] {E5_ID, G5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][G5_ID] = new int[] {F5_ID, H5_ID};
		ADJOINING_FILES_FIELD_IDS[Figures.COLOUR_BLACK][H5_ID] = new int[] {G5_ID};
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
	
	
	public static int getEnemyFieldID(String enpassantTargetSquar) {
		int enpassTargetFieldID = getFieldID(enpassantTargetSquar);
		
		//0-7, 8-15, 16-23, 24-31, 32-39, 40-47, 48-55, 56-63
		if (enpassTargetFieldID >= 16 && enpassTargetFieldID <= 23) {
			enpassTargetFieldID = enpassTargetFieldID - 8;
		} else if (enpassTargetFieldID >= 40 && enpassTargetFieldID <= 47) {
			enpassTargetFieldID = enpassTargetFieldID + 8;
		} else {
			throw new IllegalStateException("enpassTargetFieldID=" + enpassTargetFieldID);
		}
		
		return enpassTargetFieldID;
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
