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
package bagaturchess.bitboard.impl;

import java.util.HashMap;
import java.util.Map;

import bagaturchess.bitboard.common.Utils;


public class Fields extends Bits {

	public static final int ID_MAX = PRIME_67;
	//public static final int ID_FIELD_MAX = PRIME_67;
	//public static final int ID_FIGURE_MAX = PRIME_67;
	
	public static final int DUMMY_FIELD_ID = NUMBER_69;
	
	public static final long A1 = BIT_0;
	public static final long B1 = BIT_1;
	public static final long C1 = BIT_2;
	public static final long D1 = BIT_3;
	public static final long E1 = BIT_4;
	public static final long F1 = BIT_5;
	public static final long G1 = BIT_6;
	public static final long H1 = BIT_7;
	public static final long A2 = BIT_8;
	public static final long B2 = BIT_9;
	public static final long C2 = BIT_10;
	public static final long D2 = BIT_11;
	public static final long E2 = BIT_12;
	public static final long F2 = BIT_13;
	public static final long G2 = BIT_14;
	public static final long H2 = BIT_15;
	public static final long A3 = BIT_16;
	public static final long B3 = BIT_17;
	public static final long C3 = BIT_18;
	public static final long D3 = BIT_19;
	public static final long E3 = BIT_20;
	public static final long F3 = BIT_21;
	public static final long G3 = BIT_22;
	public static final long H3 = BIT_23;
	public static final long A4 = BIT_24;
	public static final long B4 = BIT_25;
	public static final long C4 = BIT_26;
	public static final long D4 = BIT_27;
	public static final long E4 = BIT_28;
	public static final long F4 = BIT_29;
	public static final long G4 = BIT_30;
	public static final long H4 = BIT_31;
	public static final long A5 = BIT_32;
	public static final long B5 = BIT_33;
	public static final long C5 = BIT_34;
	public static final long D5 = BIT_35;
	public static final long E5 = BIT_36;
	public static final long F5 = BIT_37;
	public static final long G5 = BIT_38;
	public static final long H5 = BIT_39;
	public static final long A6 = BIT_40;
	public static final long B6 = BIT_41;
	public static final long C6 = BIT_42;
	public static final long D6 = BIT_43;
	public static final long E6 = BIT_44;
	public static final long F6 = BIT_45;
	public static final long G6 = BIT_46;
	public static final long H6 = BIT_47;
	public static final long A7 = BIT_48;
	public static final long B7 = BIT_49;
	public static final long C7 = BIT_50;
	public static final long D7 = BIT_51;
	public static final long E7 = BIT_52;
	public static final long F7 = BIT_53;
	public static final long G7 = BIT_54;
	public static final long H7 = BIT_55;
	public static final long A8 = BIT_56;
	public static final long B8 = BIT_57;
	public static final long C8 = BIT_58;
	public static final long D8 = BIT_59;
	public static final long E8 = BIT_60;
	public static final long F8 = BIT_61;
	public static final long G8 = BIT_62;
	public static final long H8 = BIT_63;
	
	public static final int A1_ID = 0;
	public static final int B1_ID = 1;
	public static final int C1_ID = 2;
	public static final int D1_ID = 3;
	public static final int E1_ID = 4;
	public static final int F1_ID = 5;
	public static final int G1_ID = 6;
	public static final int H1_ID = 7;
	public static final int A2_ID = 8;
	public static final int B2_ID = 9;
	public static final int C2_ID = 10;
	public static final int D2_ID = 11;
	public static final int E2_ID = 12;
	public static final int F2_ID = 13;
	public static final int G2_ID = 14;
	public static final int H2_ID = 15;
	public static final int A3_ID = 16;
	public static final int B3_ID = 17;
	public static final int C3_ID = 18;
	public static final int D3_ID = 19;
	public static final int E3_ID = 20;
	public static final int F3_ID = 21;
	public static final int G3_ID = 22;
	public static final int H3_ID = 23;
	public static final int A4_ID = 24;
	public static final int B4_ID = 25;
	public static final int C4_ID = 26;
	public static final int D4_ID = 27;
	public static final int E4_ID = 28;
	public static final int F4_ID = 29;
	public static final int G4_ID = 30;
	public static final int H4_ID = 31;
	public static final int A5_ID = 32;
	public static final int B5_ID = 33;
	public static final int C5_ID = 34;
	public static final int D5_ID = 35;
	public static final int E5_ID = 36;
	public static final int F5_ID = 37;
	public static final int G5_ID = 38;
	public static final int H5_ID = 39;
	public static final int A6_ID = 40;
	public static final int B6_ID = 41;
	public static final int C6_ID = 42;
	public static final int D6_ID = 43;
	public static final int E6_ID = 44;
	public static final int F6_ID = 45;
	public static final int G6_ID = 46;
	public static final int H6_ID = 47;
	public static final int A7_ID = 48;
	public static final int B7_ID = 49;
	public static final int C7_ID = 50;
	public static final int D7_ID = 51;
	public static final int E7_ID = 52;
	public static final int F7_ID = 53;
	public static final int G7_ID = 54;
	public static final int H7_ID = 55;
	public static final int A8_ID = 56;
	public static final int B8_ID = 57;
	public static final int C8_ID = 58;
	public static final int D8_ID = 59;
	public static final int E8_ID = 60;
	public static final int F8_ID = 61;
	public static final int G8_ID = 62;
	public static final int H8_ID = 63;
	
	public static final long ALL_ORDERED_A1H1[] = new long[] {A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2, E2, F2, G2, H2, A3, B3, C3, D3, E3, F3, G3, H3, A4, B4, C4, D4, E4, F4, G4, H4, A5, B5, C5, D5, E5, F5, G5, H5, A6, B6, C6, D6, E6, F6, G6, H6, A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8, G8, H8, };
	  
	/*public static final long A1A8_A1 = BIT_0;
	public static final long A1A8_A2 = BIT_1;
	public static final long A1A8_A3 = BIT_2;
	public static final long A1A8_A4 = BIT_3;
	public static final long A1A8_A5 = BIT_4;
	public static final long A1A8_A6 = BIT_5;
	public static final long A1A8_A7 = BIT_6;
	public static final long A1A8_A8 = BIT_7;
	public static final long A1A8_B1 = BIT_8;
	public static final long A1A8_B2 = BIT_9;
	public static final long A1A8_B3 = BIT_10;
	public static final long A1A8_B4 = BIT_11;
	public static final long A1A8_B5 = BIT_12;
	public static final long A1A8_B6 = BIT_13;
	public static final long A1A8_B7 = BIT_14;
	public static final long A1A8_B8 = BIT_15;
	public static final long A1A8_C1 = BIT_16;
	public static final long A1A8_C2 = BIT_17;
	public static final long A1A8_C3 = BIT_18;
	public static final long A1A8_C4 = BIT_19;
	public static final long A1A8_C5 = BIT_20;
	public static final long A1A8_C6 = BIT_21;
	public static final long A1A8_C7 = BIT_22;
	public static final long A1A8_C8 = BIT_23;
	public static final long A1A8_D1 = BIT_24;
	public static final long A1A8_D2 = BIT_25;
	public static final long A1A8_D3 = BIT_26;
	public static final long A1A8_D4 = BIT_27;
	public static final long A1A8_D5 = BIT_28;
	public static final long A1A8_D6 = BIT_29;
	public static final long A1A8_D7 = BIT_30;
	public static final long A1A8_D8 = BIT_31;
	public static final long A1A8_E1 = BIT_32;
	public static final long A1A8_E2 = BIT_33;
	public static final long A1A8_E3 = BIT_34;
	public static final long A1A8_E4 = BIT_35;
	public static final long A1A8_E5 = BIT_36;
	public static final long A1A8_E6 = BIT_37;
	public static final long A1A8_E7 = BIT_38;
	public static final long A1A8_E8 = BIT_39;
	public static final long A1A8_F1 = BIT_40;
	public static final long A1A8_F2 = BIT_41;
	public static final long A1A8_F3 = BIT_42;
	public static final long A1A8_F4 = BIT_43;
	public static final long A1A8_F5 = BIT_44;
	public static final long A1A8_F6 = BIT_45;
	public static final long A1A8_F7 = BIT_46;
	public static final long A1A8_F8 = BIT_47;
	public static final long A1A8_G1 = BIT_48;
	public static final long A1A8_G2 = BIT_49;
	public static final long A1A8_G3 = BIT_50;
	public static final long A1A8_G4 = BIT_51;
	public static final long A1A8_G5 = BIT_52;
	public static final long A1A8_G6 = BIT_53;
	public static final long A1A8_G7 = BIT_54;
	public static final long A1A8_G8 = BIT_55;
	public static final long A1A8_H1 = BIT_56;
	public static final long A1A8_H2 = BIT_57;
	public static final long A1A8_H3 = BIT_58;
	public static final long A1A8_H4 = BIT_59;
	public static final long A1A8_H5 = BIT_60;
	public static final long A1A8_H6 = BIT_61;
	public static final long A1A8_H7 = BIT_62;
	public static final long A1A8_H8 = BIT_63;*/
	
	//private static final long ALL_ORDERED_A1A8[] = new long[] {A1A8_A1, A1A8_A2, A1A8_A3, A1A8_A4, A1A8_A5, A1A8_A6, A1A8_A7, A1A8_A8, A1A8_B1, A1A8_B2, A1A8_B3, A1A8_B4, A1A8_B5, A1A8_B6, A1A8_B7, A1A8_B8, A1A8_C1, A1A8_C2, A1A8_C3, A1A8_C4, A1A8_C5, A1A8_C6, A1A8_C7, A1A8_C8, A1A8_D1, A1A8_D2, A1A8_D3, A1A8_D4, A1A8_D5, A1A8_D6, A1A8_D7, A1A8_D8, A1A8_E1, A1A8_E2, A1A8_E3, A1A8_E4, A1A8_E5, A1A8_E6, A1A8_E7, A1A8_E8, A1A8_F1, A1A8_F2, A1A8_F3, A1A8_F4, A1A8_F5, A1A8_F6, A1A8_F7, A1A8_F8, A1A8_G1, A1A8_G2, A1A8_G3, A1A8_G4, A1A8_G5, A1A8_G6, A1A8_G7, A1A8_G8, A1A8_H1, A1A8_H2, A1A8_H3, A1A8_H4, A1A8_H5, A1A8_H6, A1A8_H7, A1A8_H8, };
	
	//private static final int[] IDX_ORDERED_A1A8_2_A1A8 = new int[Bits.NUMBER_64];
	public static final int[] IDX_2_ORDERED_A1H1 = new int[Bits.PRIME_67];
	//private static final int[] IDX_A1A8_2_ORDERED_A1A8 = new int[Bits.PRIME_67];

	//private static final String ALL_ORDERED_A1A8_NAMES[] = new String[] {"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", };
	public static final String ALL_ORDERED_NAMES[] = new String[] {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", };
	
	public static final int[] IDX_ORDERED_2_A1H1 = new int[Bits.NUMBER_64];
	
	public static final long ALL_A1H1[] = new long[Bits.PRIME_67];
	public static final long ALL_A1A8[] = new long[Bits.PRIME_67];
	
	public static final int LETTERS[] = new int[Bits.PRIME_67];
	public static final int DIGITS[] = new int[Bits.PRIME_67];
	
	public static final int LETTER_A_ID = 0;
	public static final int LETTER_B_ID = 1;
	public static final int LETTER_C_ID = 2;
	public static final int LETTER_D_ID = 3;
	public static final int LETTER_E_ID = 4;
	public static final int LETTER_F_ID = 5;
	public static final int LETTER_G_ID = 6;
	public static final int LETTER_H_ID = 7;
	public static final int LETTER_NONE_ID = 8;
	
	public static final int DIGIT_1_ID = 0;
	public static final int DIGIT_2_ID = 1;
	public static final int DIGIT_3_ID = 2;
	public static final int DIGIT_4_ID = 3;
	public static final int DIGIT_5_ID = 4;
	public static final int DIGIT_6_ID = 5;
	public static final int DIGIT_7_ID = 6;
	public static final int DIGIT_8_ID = 7;
	public static final int DIGIT_NONE_ID = 8;
	
	public static final long LETTER_A = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	public static final long LETTER_B = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
	public static final long LETTER_C = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8;
	public static final long LETTER_D = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8;
	public static final long LETTER_E = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8;
	public static final long LETTER_F = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8;
	public static final long LETTER_G = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	public static final long LETTER_H = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	
	public static final long DIGIT_1 = A1 | B1 | C1 | D1 | E1 | F1 | G1 | H1;
	public static final long DIGIT_2 = A2 | B2 | C2 | D2 | E2 | F2 | G2 | H2;
	public static final long DIGIT_3 = A3 | B3 | C3 | D3 | E3 | F3 | G3 | H3;
	public static final long DIGIT_4 = A4 | B4 | C4 | D4 | E4 | F4 | G4 | H4;
	public static final long DIGIT_5 = A5 | B5 | C5 | D5 | E5 | F5 | G5 | H5;
	public static final long DIGIT_6 = A6 | B6 | C6 | D6 | E6 | F6 | G6 | H6;
	public static final long DIGIT_7 = A7 | B7 | C7 | D7 | E7 | F7 | G7 | H7;
	public static final long DIGIT_8 = A8 | B8 | C8 | D8 | E8 | F8 | G8 | H8;
	
	public static final long INITIAL_BOARD = DIGIT_1 | DIGIT_2 | DIGIT_7 | DIGIT_8;
	public static final long ALL_FIELDS = LETTER_A | LETTER_B | LETTER_C | LETTER_D | LETTER_E | LETTER_F | LETTER_G | LETTER_H;
	
	public static final long CORNERS = LETTER_A | LETTER_B | DIGIT_1 | DIGIT_8;
	
	public static final long CENTER_1 = D4 | E4 | D5 | E5;
	
	public static final long CENTER_2 = C3 | D3 | E3 | F3
								| C4 | D4 | E4 | F4
								| C5 | D5 | E5 | F5
								| C6 | D6 | E6 | F6;
	
	public static final long ED2 = D2 | E2;
	public static final long ED7 = D7 | E7;
	
	public static final long CENTER_3 = (ALL_FIELDS & ~(CENTER_1 | CENTER_2 | CORNERS));
	
	public static final int[] CENTRALIZATION = Utils.reverseSpecial ( new int[]{	
			0,   0,   0,   0,   0,   0,   0,   0,
			0,   1,   1,   1,   1,   1,   1,   0, 
			0,   1,   2,   2,   2,   2,   1,   0, 
			0,   1,   2,   4,   4,   2,   1,   0, 
			0,   1,   2,   4,   4,   2,   1,   0, 
			0,   1,   2,   2,   2,   2,   1,   0, 
			0,   1,   1,   1,   1,   1,   1,   0, 
			0,   0,   0,   0,   0,   0,   0,   0,
		});
	
	public static int[] CENTER_MANHATTAN_DISTANCE = Utils.reverseSpecial ( new int[]{	
			  6, 5, 4, 3, 3, 4, 5, 6,
			  5, 4, 3, 2, 2, 3, 4, 5,
			  4, 3, 2, 1, 1, 2, 3, 4,
			  3, 2, 1, 0, 0, 1, 2, 3,
			  3, 2, 1, 0, 0, 1, 2, 3,
			  4, 3, 2, 1, 1, 2, 3, 4,
			  5, 4, 3, 2, 2, 3, 4, 5,
			  6, 5, 4, 3, 3, 4, 5, 6
		});
	
	public static final long ALL_WHITE_FIELDS = B1 | D1 | F1 | H1
																						| A2 | C2 | E2 | G2
																						| B3 | D3 | F3 | H3
																						| A4 | C4 | E4 | G4
																						| B5 | D5 | F5 | H5
																						| A6 | C6 | E6 | G6
																						| B7 | D7 | F7 | H7
																						| A8 | C8 | E8 | G8;

	public static final long ALL_BLACK_FIELDS = ~ALL_WHITE_FIELDS;

	public static final long SPACE_WHITE = Fields.C2 | Fields.C3 | Fields.C4
											    | Fields.D2 | Fields.D3 | Fields.D4
												| Fields.E2 | Fields.E3 | Fields.E4
												| Fields.F2 | Fields.F3 | Fields.F4;

	public static final long SPACE_BLACK = Fields.C7 | Fields.C6 | Fields.C5
												| Fields.D7 | Fields.D6 | Fields.D5
												| Fields.E7 | Fields.E6 | Fields.E5
												| Fields.F7 | Fields.F6 | Fields.F5;
	
	public static final long[] ALL_OFFICERS_FIELDS = new long[Bits.PRIME_67];

	public static final long[] LETTERS_BY_FIELD_ID = new long[Bits.PRIME_67];
	public static final long[] LETTERS_NEIGHBOURS_BY_FIELD_ID = new long[Bits.PRIME_67];
	public static final long[] LETTERS_LEFT_BY_FIELD_ID = new long[Bits.PRIME_67];
	public static final long[] LETTERS_RIGHT_BY_FIELD_ID = new long[Bits.PRIME_67];
	
	
	//public static final long[] DIGITS_BY_FIELD_ID = new long[Bits.PRIME_67];
	
	//public static final long A1H1_2_A1A8[] = new long[Bits.PRIME_67];
	//private static final long A1A8_2_A1H1[] = new long[Bits.PRIME_67];
	
	private static Map<String, Integer> fieldSignToFieldID = new HashMap<String, Integer>();
	private static Map<Integer, String> fieldIDToFieldSign = new HashMap<Integer, String>();
	
	static {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			ALL_A1H1[id] = a1h1;
		}
		
		for (int i=0; i<ALL_ORDERED_NAMES.length; i++) {
			String a1h1 = ALL_ORDERED_NAMES[i];
			int id = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			fieldSignToFieldID.put(a1h1, id);
			fieldIDToFieldSign.put(id, a1h1);
		}
		
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			if ((a1h1 & ALL_WHITE_FIELDS) != 0L) {
				ALL_OFFICERS_FIELDS[id] = ALL_WHITE_FIELDS;
			} else if ((a1h1 & ALL_BLACK_FIELDS) != 0L) {
				ALL_OFFICERS_FIELDS[id] = ALL_BLACK_FIELDS;
			} else {
				throw new IllegalStateException();
			}
		}
		
		/*for (int i=0; i<64; i++) {
			long a1a8 = ALL_ORDERED_A1A8[i];
			int a1a8_id = getIDByBitboard(a1a8, Bits.PRIME_67);
			ALL_A1A8[a1a8_id] = a1a8;
		}*/
		
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			if ((LETTER_A & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_A;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_B;
				LETTERS_LEFT_BY_FIELD_ID[id] = 0L;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_B;
			} else if ((LETTER_B & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_B;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_A | LETTER_C;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_A;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_C;
			} else if ((LETTER_C & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_C;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_B | LETTER_D;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_B;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_D;
			} else if ((LETTER_D & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_D;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_C | LETTER_E;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_C;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_E;
			} else if ((LETTER_E & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_E;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_D | LETTER_F;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_D;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_F;
			} else if ((LETTER_F & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_F;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_E | LETTER_G;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_E;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_G;
			} else if ((LETTER_G & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_G;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_F | LETTER_H;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_F;
				LETTERS_RIGHT_BY_FIELD_ID[id] = LETTER_H;
			} else if ((LETTER_H & a1h1) != 0L) {
				LETTERS_BY_FIELD_ID[id] = LETTER_H;
				LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = LETTER_G;
				LETTERS_LEFT_BY_FIELD_ID[id] = LETTER_G;
				LETTERS_RIGHT_BY_FIELD_ID[id] = 0L;
			} else {
				throw new IllegalStateException();
			}
		}
		
		/*for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			long a1h1 = ALL_ORDERED_A1H1[i];
			int id = get67IDByBitboard(a1h1);
			if ((DIGIT_1 & a1h1) != 0L) {
				DIGITS_BY_FIELD_ID[id] = DIGIT_1;
			} else if ((DIGIT_2 & a1h1) != 0L) {
			} else if ((DIGIT_3 & a1h1) != 0L) {
			} else if ((DIGIT_4 & a1h1) != 0L) {
			} else if ((DIGIT_5 & a1h1) != 0L) {
			} else if ((DIGIT_6 & a1h1) != 0L) {
			} else if ((DIGIT_7 & a1h1) != 0L) {
			} else if ((DIGIT_8 & a1h1) != 0L) {
			} else {
				throw new IllegalStateException();
			}
		}*/
		
		for (int id=0; id<ALL_A1H1.length; id++) {
			
			long a1h1 = ALL_A1H1[id];
			
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				long tmp = ALL_ORDERED_A1H1[j];
				if (tmp == a1h1) {
					//int div = j / 8;
					//int mod = j % 8;
					
					//int a1a8_id = 8 * mod + div;
					//A1H1_2_A1A8[id] = ALL_ORDERED_A1A8[a1a8_id];
					IDX_ORDERED_2_A1H1[j] = id;
					IDX_2_ORDERED_A1H1[id] = j;
					break;
				} else if (j == ALL_ORDERED_A1H1.length - 1) {
					throw new IllegalStateException();
				}
			}
		}
		
		/*for (int id=0; id<ALL_A1A8.length; id++) {
			if (id == 0 || id == 17 || id ==34 ) {
				continue;
			}
			
			long a1a8 = ALL_A1A8[id];
			
			for (int j=0; j<ALL_ORDERED_A1A8.length; j++) {
				long tmp = ALL_ORDERED_A1A8[j];
				if (tmp == a1a8) {
					int div = j / 8;
					int mod = j % 8;
					
					int ord_id = 8 * mod + div;
					//A1A8_2_A1H1[id] = ALL_ORDERED_A1H1[id];
					IDX_ORDERED_A1A8_2_A1A8[j] = id;
					IDX_A1A8_2_ORDERED_A1A8[ord_id] = j;
					break;
				} else if (j == ALL_ORDERED_A1A8.length - 1) {
					throw new IllegalStateException();
				}
			}
		}*/
		
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
    			int ordered_id = getFieldIDByFileAndRank(digit, letter);
    			LETTERS[IDX_ORDERED_2_A1H1[ordered_id]] = letter;
    			DIGITS[IDX_ORDERED_2_A1H1[ordered_id]] = digit;
    		}
    	}
    	
    	verify();
	}

	public static int getRank_W(int fieldID) {
		return DIGITS[fieldID];
	}
	
	public static int getRank_B(int fieldID) {
		return 7 - DIGITS[fieldID];
	}
	
	public static int getFieldIDByFileAndRank(int digit, int letter) {
		return digit * 8 + letter;
	}
	
	private static final long BINARY_SEARCH_1_1 = DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4;  
	private static final long BINARY_SEARCH_1_2 = DIGIT_5 | DIGIT_6 | DIGIT_7 | DIGIT_8;

	private static final long BINARY_SEARCH_1_1_1 = DIGIT_1 | DIGIT_2;  
	private static final long BINARY_SEARCH_1_1_2 = DIGIT_3 | DIGIT_4;

	private static final long BINARY_SEARCH_1_1_1_1 = DIGIT_1;  
	private static final long BINARY_SEARCH_1_1_1_2 = DIGIT_2;
	private static final long BINARY_SEARCH_1_1_2_1 = DIGIT_3;
	private static final long BINARY_SEARCH_1_1_2_2 = DIGIT_4;

	private static final long BINARY_SEARCH_1_1_1_1_1 = A1 | B1 | C1 | D1;
	private static final long BINARY_SEARCH_1_1_1_1_2 = E1 | F1 | G1 | H1;
	private static final long BINARY_SEARCH_1_1_1_2_1 = A2 | B2 | C2 | D2;
	private static final long BINARY_SEARCH_1_1_1_2_2 = E2 | F2 | G2 | H2;
	private static final long BINARY_SEARCH_1_1_2_1_1 = A3 | B3 | C3 | D3;
	private static final long BINARY_SEARCH_1_1_2_1_2 = E3 | F3 | G3 | H3;
	private static final long BINARY_SEARCH_1_1_2_2_1 = A4 | B4 | C4 | D4;
	private static final long BINARY_SEARCH_1_1_2_2_2 = E4 | F4 | G4 | H4;
	
	private static final long BINARY_SEARCH_1_2_1 = DIGIT_5 | DIGIT_6;
	private static final long BINARY_SEARCH_1_2_2 = DIGIT_7 | DIGIT_8;

	private static final long BINARY_SEARCH_1_2_1_1 = DIGIT_5;
	private static final long BINARY_SEARCH_1_2_1_2 = DIGIT_6;
	private static final long BINARY_SEARCH_1_2_2_1 = DIGIT_7;
	private static final long BINARY_SEARCH_1_2_2_2 = DIGIT_8;

	private static final long BINARY_SEARCH_1_2_1_1_1 = A5 | B5 | C5 | D5;
	private static final long BINARY_SEARCH_1_2_1_1_2 = E5 | F5 | G5 | H5;
	private static final long BINARY_SEARCH_1_2_1_2_1 = A6 | B6 | C6 | D6;
	private static final long BINARY_SEARCH_1_2_1_2_2 = E6 | F6 | G6 | H6;
	private static final long BINARY_SEARCH_1_2_2_1_1 = A7 | B7 | C7 | D7;
	private static final long BINARY_SEARCH_1_2_2_1_2 = E7 | F7 | G7 | H7;
	private static final long BINARY_SEARCH_1_2_2_2_1 = A8 | B8 | C8 | D8;
	private static final long BINARY_SEARCH_1_2_2_2_2 = E8 | F8 | G8 | H8;
	
	public static final int get67IDByBitboard(long bitBoard) {
		if ((BINARY_SEARCH_1_1 & bitBoard) != 0) {
			if ((BINARY_SEARCH_1_1_1 & bitBoard) != 0) {
				if ((BINARY_SEARCH_1_1_1_1 & bitBoard) != 0) {
					if ((BINARY_SEARCH_1_1_1_1_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 0, 4);
					} else {
						return get67IDByBitboard(bitBoard, 4, 8);
					}
				} else { //BINARY_SEARCH_1_1_1_2
					if ((BINARY_SEARCH_1_1_1_2_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 8, 12);
					} else {
						return get67IDByBitboard(bitBoard, 12, 16);
					}
				}
			} else { //BINARY_SEARCH_1_1_2
				if ((BINARY_SEARCH_1_1_2_1 & bitBoard) != 0) {
					if ((BINARY_SEARCH_1_1_2_1_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 16, 20);
					} else {
						return get67IDByBitboard(bitBoard, 20, 24);
					}
				} else { //BINARY_SEARCH_1_1_2_2
					if ((BINARY_SEARCH_1_1_2_2_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 24, 28);
					} else {
						return get67IDByBitboard(bitBoard, 28, 32);
					}
				}
			}
		} else { //BINARY_SEARCH_1_2
			if ((BINARY_SEARCH_1_2_1 & bitBoard) != 0) {
				if ((BINARY_SEARCH_1_2_1_1 & bitBoard) != 0) {
					if ((BINARY_SEARCH_1_2_1_1_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 32, 36);
					} else {
						return get67IDByBitboard(bitBoard, 36, 40);
					}
				} else { //BINARY_SEARCH_1_2_1_2
					if ((BINARY_SEARCH_1_2_1_2_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 40, 44);
					} else {
						return get67IDByBitboard(bitBoard, 44, 48);
					}
				}
			} else { //BINARY_SEARCH_1_2_2
				if ((BINARY_SEARCH_1_2_2_1 & bitBoard) != 0) {
					if ((BINARY_SEARCH_1_2_2_1_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 48, 52);
					} else {
						return get67IDByBitboard(bitBoard, 52, 56);
					}
				} else { //BINARY_SEARCH_1_2_2_2
					if ((BINARY_SEARCH_1_2_2_2_1 & bitBoard) != 0) {
						return get67IDByBitboard(bitBoard, 56, 60);
					} else {
						return get67IDByBitboard(bitBoard, 60, 64);
					}
				}
			}
		}
	}
	
	public int getDigitsDiff(int fieldID1, int fieldID2) {
		int d1 = DIGITS[fieldID1];
		int d2 = DIGITS[fieldID2];

		return Math.abs(d1 - d2);
	}
	
	public int getDistance(int fieldID1, int fieldID2) {
		int l1 = LETTERS[fieldID1];
		int l2 = LETTERS[fieldID2];
		int d1 = DIGITS[fieldID1];
		int d2 = DIGITS[fieldID2];

		int dl = Math.abs(l1 - l2);
		int dd = Math.abs(d1 - d2);
		
		return Math.max(dl, dd);
	}
	
	private static final int get67IDByBitboard(long bitBoard, int from, int to) {
		int id = -1;
		for (int i=from; i<to; i++) {
			if (ALL_BITS[i] == bitBoard) {
				id = i;
				break;
			}
		}
		/*if (id == -1) {
			throw new IllegalStateException();
		}
		if (id > 63) {
			throw new IllegalStateException();
		}*/
		
		return id;
	}
	
	/*public static final int get67IDByBitboard(long bitBoard) {
		int id = -1;
		for (int i=0; i<ALL_BITS.length; i++) {
			if (ALL_BITS[i] == bitBoard) {
				id = i;
				break;
			}
		}
		if (id == -1) {
			throw new IllegalStateException();
		}
		if (id > 63) {
			throw new IllegalStateException();
		}
		
		if (get67IDByBitboard1(bitBoard) != id) {
			throw new IllegalStateException();
		}
		
		return id;
	}*/
	
	public static final int[][] rotateBoard(int[][] _matrix) {
		int[][] matrix = new int[8][8];
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				matrix[j][7 - i] = _matrix[i][j];
			}
		}
		return matrix;
	}
	
	public static int getFieldID(String fieldSign) {
		return fieldSignToFieldID.get(fieldSign);
	}
	
	public static String getFieldSign(int fieldID) {
		return fieldIDToFieldSign.get(fieldID);
	}
	
	public static String getFieldSign_UC(int fieldID) {
		return getFieldSign(fieldID).toUpperCase();
	}
	
	public static int getDistancePoints(int fieldID1, int fieldID2) {
		int l1 = Fields.LETTERS[fieldID1];
		int d1 = Fields.DIGITS[fieldID1];
		int l2 = Fields.LETTERS[fieldID2];
		int d2 = Fields.DIGITS[fieldID2];
		
		int delta_l = Math.abs(l1 - l2);
		int delta_d = Math.abs(d1 - d2);
		int max_delta = Math.max(delta_l, delta_d);
		
		return max_delta;
	}
	
	public static boolean areOnTheSameLine(int fieldID1, int fieldID2) {
		int l1 = Fields.LETTERS[fieldID1];
		int d1 = Fields.DIGITS[fieldID1];
		int l2 = Fields.LETTERS[fieldID2];
		int d2 = Fields.DIGITS[fieldID2];
		
		return (l1 == l2) || (d1 == d2);
	}
	
	public static int getDistancePoints_reversed(int fieldID1, int fieldID2) {
		return 7 - getDistancePoints(fieldID1, fieldID2);
	}
	
	public static int getTropismPoint(int fieldID1, int fieldID2) {
		int l1 = Fields.LETTERS[fieldID1];
		int d1 = Fields.DIGITS[fieldID1];
		int l2 = Fields.LETTERS[fieldID2];
		int d2 = Fields.DIGITS[fieldID2];
		
		int delta_l = Math.abs(l1 - l2);
		int delta_d = Math.abs(d1 - d2);
		int sum  = delta_l + delta_d;
		
		if (14 - sum < 0) {
			throw new IllegalStateException("sum=" + sum);
		}
		
		return 14 - sum;
	}
	
	public static int getCenteredPoint(int fieldID) {
		return CENTRALIZATION[fieldID];
	}
	
	protected static final int[][] bitboards2fieldIDs(long[][] dirsBitboards) {
		int[][] result = new int[dirsBitboards.length][];
		for (int i=0; i<dirsBitboards.length; i++) {
			long[] dirBitboards = dirsBitboards[i];
			result[i] = new int[dirBitboards.length];
			for (int j=0; j<dirBitboards.length; j++) {
				long bitboard = dirBitboards[j];
				result[i][j] = get67IDByBitboard(bitboard);
				if (result[i][j] < 0 || result[i][j] > 63) {
					throw new IllegalStateException();
				}
			}
		}
		return result;
	}
	
	private static void genMembers_A1H1() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digit = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		result = "public static final long " + letters[i % 8] + digit[i / 8] + " = BIT_" + i + ";";
    		System.out.println(result);
    	}
    	
    	result = "public static final long ALL_A1H1[] = new long[] {";
    	for (int i=0; i<64; i++) {
    		result += "" + letters[i % 8] + digit[i / 8] + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_A1A8() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digit = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result;
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		result = "public static final long A1A8_" + letters[i / 8] + digit[i % 8] + " = BIT_" + i + ";";
    		//System.out.println(result);
    	}
    	
    	result = "public static final long ALL_A1A8[] = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		result += "A1A8_" + letters[i / 8] + digit[i % 8] + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_FieldNames() {
		String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
    	String[] digit = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	
    	String result = "public static final String ALL_ORDERED_NAMES[] = new String[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int div = i / 8;
    		int mod = i % 8;
    		result += "\"" + letters[mod] + digit[div] + "\", ";
    	}
    	result += "};";
    	System.out.println(result);
    	
    	result = "public static final String ALL_ORDERED_A1A8_NAMES[] = new String[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int div = i / 8;
    		int mod = i % 8;
    		result += "\"" + letters[div] + digit[mod] + "\", ";
    	}
    	result += "};";
    	System.out.println(result);
	}
	
	/*private static final long getFieldBitboardByID_A1H1(int fieldID) {
		long bb = ALL_A1H1[fieldID];//Bits.LONG_0;
		return bb;
	}
	
	private static final long getFieldBitboardByID_A1A8(int fieldID) {
		long bb = ALL_A1A8[fieldID];//Bits.LONG_0;
		return bb;
	}*/
	
	/*public static final long A1H1_2_A1A8(long fieldBitBoard) {
		int id = getIDByBitboard(fieldBitBoard, Bits.PRIME_67);
		return A1H1_2_A1A8[id];
	}*/

	/*public static final long A1A8_2_A1H1(long fieldBitBoard) {
		int id = getIDByBitboard(fieldBitBoard, Bits.PRIME_67);
		return A1A8_2_A1H1[id];
	}*/

	private static final void verify() {
		
		/*System.out.println("Checking array A1A8_2_A1H1 ... ");
		for(int i=0; i<Bits.PRIME_67; i++) {
			if (i == 0 || i == 17 || i ==34 ) {
				continue;
			}
			
			long a1a8 = ALL_A1A8[i];
			
			long a1a8_2_a1h1 = A1A8_2_A1H1(a1a8);
			long a1a8_2_a1a8 = 2_A1A8(a1a8_2_a1h1);
			
			System.out.println("" + Long.toBinaryString(a1a8) + " -> "  + Long.toBinaryString(a1a8_2_a1h1));
			
			if (a1a8 != a1a8_2_a1a8) {
				throw new IllegalStateException();
			}
		}
		System.out.println("OK");*/
	}

    public static void main(String[] args) {	
    	//genMembers_A1H1();
    	//genMembers_A1A8();
    	genMembers_FieldNames();
    }
}
