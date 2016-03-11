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

import java.util.BitSet;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class OfficerPlies extends Fields {
	
	//---------------------------- START GENERATED BLOCK ----------------------------------------------
	
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A1 = B2 | C3 | D4 | E5 | F6 | G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A1 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][] {{B2, C3, D4, E5, F6, G7, H8}, {}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A1 = new int[] {0};
	public static final long ALL_OFFICER_MOVES_FROM_A1 = ALL_OFFICER_DIR0_MOVES_FROM_A1 | ALL_OFFICER_DIR1_MOVES_FROM_A1 | ALL_OFFICER_DIR2_MOVES_FROM_A1 | ALL_OFFICER_DIR3_MOVES_FROM_A1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B1 = C2 | D3 | E4 | F5 | G6 | H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B1 = A2;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][] {{C2, D3, E4, F5, G6, H7}, {}, {}, {A2}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B1 = ALL_OFFICER_DIR0_MOVES_FROM_B1 | ALL_OFFICER_DIR1_MOVES_FROM_B1 | ALL_OFFICER_DIR2_MOVES_FROM_B1 | ALL_OFFICER_DIR3_MOVES_FROM_B1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C1 = D2 | E3 | F4 | G5 | H6;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C1 = B2 | A3;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][] {{D2, E3, F4, G5, H6}, {}, {}, {B2, A3}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C1 = ALL_OFFICER_DIR0_MOVES_FROM_C1 | ALL_OFFICER_DIR1_MOVES_FROM_C1 | ALL_OFFICER_DIR2_MOVES_FROM_C1 | ALL_OFFICER_DIR3_MOVES_FROM_C1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D1 = E2 | F3 | G4 | H5;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D1 = C2 | B3 | A4;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][] {{E2, F3, G4, H5}, {}, {}, {C2, B3, A4}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D1 = ALL_OFFICER_DIR0_MOVES_FROM_D1 | ALL_OFFICER_DIR1_MOVES_FROM_D1 | ALL_OFFICER_DIR2_MOVES_FROM_D1 | ALL_OFFICER_DIR3_MOVES_FROM_D1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E1 = F2 | G3 | H4;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E1 = D2 | C3 | B4 | A5;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][] {{F2, G3, H4}, {}, {}, {D2, C3, B4, A5}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E1 = ALL_OFFICER_DIR0_MOVES_FROM_E1 | ALL_OFFICER_DIR1_MOVES_FROM_E1 | ALL_OFFICER_DIR2_MOVES_FROM_E1 | ALL_OFFICER_DIR3_MOVES_FROM_E1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F1 = G2 | H3;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F1 = E2 | D3 | C4 | B5 | A6;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][] {{G2, H3}, {}, {}, {E2, D3, C4, B5, A6}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F1 = ALL_OFFICER_DIR0_MOVES_FROM_F1 | ALL_OFFICER_DIR1_MOVES_FROM_F1 | ALL_OFFICER_DIR2_MOVES_FROM_F1 | ALL_OFFICER_DIR3_MOVES_FROM_F1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G1 = H2;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G1 = F2 | E3 | D4 | C5 | B6 | A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][] {{H2}, {}, {}, {F2, E3, D4, C5, B6, A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G1 = new int[] {0, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G1 = ALL_OFFICER_DIR0_MOVES_FROM_G1 | ALL_OFFICER_DIR1_MOVES_FROM_G1 | ALL_OFFICER_DIR2_MOVES_FROM_G1 | ALL_OFFICER_DIR3_MOVES_FROM_G1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H1 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H1 = G2 | F3 | E4 | D5 | C6 | B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][] {{}, {}, {}, {G2, F3, E4, D5, C6, B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H1 = new int[] {3};
	public static final long ALL_OFFICER_MOVES_FROM_H1 = ALL_OFFICER_DIR0_MOVES_FROM_H1 | ALL_OFFICER_DIR1_MOVES_FROM_H1 | ALL_OFFICER_DIR2_MOVES_FROM_H1 | ALL_OFFICER_DIR3_MOVES_FROM_H1;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A2 = B3 | C4 | D5 | E6 | F7 | G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A2 = B1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A2 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A2 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][] {{B3, C4, D5, E6, F7, G8}, {B1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A2 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A2 = ALL_OFFICER_DIR0_MOVES_FROM_A2 | ALL_OFFICER_DIR1_MOVES_FROM_A2 | ALL_OFFICER_DIR2_MOVES_FROM_A2 | ALL_OFFICER_DIR3_MOVES_FROM_A2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B2 = C3 | D4 | E5 | F6 | G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B2 = C1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B2 = A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B2 = A3;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][] {{C3, D4, E5, F6, G7, H8}, {C1}, {A1}, {A3}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B2 = ALL_OFFICER_DIR0_MOVES_FROM_B2 | ALL_OFFICER_DIR1_MOVES_FROM_B2 | ALL_OFFICER_DIR2_MOVES_FROM_B2 | ALL_OFFICER_DIR3_MOVES_FROM_B2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C2 = D3 | E4 | F5 | G6 | H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C2 = D1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C2 = B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C2 = B3 | A4;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][] {{D3, E4, F5, G6, H7}, {D1}, {B1}, {B3, A4}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C2 = ALL_OFFICER_DIR0_MOVES_FROM_C2 | ALL_OFFICER_DIR1_MOVES_FROM_C2 | ALL_OFFICER_DIR2_MOVES_FROM_C2 | ALL_OFFICER_DIR3_MOVES_FROM_C2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D2 = E3 | F4 | G5 | H6;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D2 = E1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D2 = C1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D2 = C3 | B4 | A5;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][] {{E3, F4, G5, H6}, {E1}, {C1}, {C3, B4, A5}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D2 = ALL_OFFICER_DIR0_MOVES_FROM_D2 | ALL_OFFICER_DIR1_MOVES_FROM_D2 | ALL_OFFICER_DIR2_MOVES_FROM_D2 | ALL_OFFICER_DIR3_MOVES_FROM_D2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E2 = F3 | G4 | H5;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E2 = F1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E2 = D1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E2 = D3 | C4 | B5 | A6;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][] {{F3, G4, H5}, {F1}, {D1}, {D3, C4, B5, A6}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E2 = ALL_OFFICER_DIR0_MOVES_FROM_E2 | ALL_OFFICER_DIR1_MOVES_FROM_E2 | ALL_OFFICER_DIR2_MOVES_FROM_E2 | ALL_OFFICER_DIR3_MOVES_FROM_E2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F2 = G3 | H4;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F2 = G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F2 = E1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F2 = E3 | D4 | C5 | B6 | A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][] {{G3, H4}, {G1}, {E1}, {E3, D4, C5, B6, A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F2 = ALL_OFFICER_DIR0_MOVES_FROM_F2 | ALL_OFFICER_DIR1_MOVES_FROM_F2 | ALL_OFFICER_DIR2_MOVES_FROM_F2 | ALL_OFFICER_DIR3_MOVES_FROM_F2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G2 = H3;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G2 = H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G2 = F1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G2 = F3 | E4 | D5 | C6 | B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][] {{H3}, {H1}, {F1}, {F3, E4, D5, C6, B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G2 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G2 = ALL_OFFICER_DIR0_MOVES_FROM_G2 | ALL_OFFICER_DIR1_MOVES_FROM_G2 | ALL_OFFICER_DIR2_MOVES_FROM_G2 | ALL_OFFICER_DIR3_MOVES_FROM_G2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H2 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H2 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H2 = G1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H2 = G3 | F4 | E5 | D6 | C7 | B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][] {{}, {}, {G1}, {G3, F4, E5, D6, C7, B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H2 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H2 = ALL_OFFICER_DIR0_MOVES_FROM_H2 | ALL_OFFICER_DIR1_MOVES_FROM_H2 | ALL_OFFICER_DIR2_MOVES_FROM_H2 | ALL_OFFICER_DIR3_MOVES_FROM_H2;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A3 = B4 | C5 | D6 | E7 | F8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A3 = B2 | C1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A3 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A3 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][] {{B4, C5, D6, E7, F8}, {B2, C1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A3 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A3 = ALL_OFFICER_DIR0_MOVES_FROM_A3 | ALL_OFFICER_DIR1_MOVES_FROM_A3 | ALL_OFFICER_DIR2_MOVES_FROM_A3 | ALL_OFFICER_DIR3_MOVES_FROM_A3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B3 = C4 | D5 | E6 | F7 | G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B3 = C2 | D1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B3 = A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B3 = A4;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][] {{C4, D5, E6, F7, G8}, {C2, D1}, {A2}, {A4}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B3 = ALL_OFFICER_DIR0_MOVES_FROM_B3 | ALL_OFFICER_DIR1_MOVES_FROM_B3 | ALL_OFFICER_DIR2_MOVES_FROM_B3 | ALL_OFFICER_DIR3_MOVES_FROM_B3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C3 = D4 | E5 | F6 | G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C3 = D2 | E1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C3 = B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C3 = B4 | A5;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][] {{D4, E5, F6, G7, H8}, {D2, E1}, {B2, A1}, {B4, A5}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C3 = ALL_OFFICER_DIR0_MOVES_FROM_C3 | ALL_OFFICER_DIR1_MOVES_FROM_C3 | ALL_OFFICER_DIR2_MOVES_FROM_C3 | ALL_OFFICER_DIR3_MOVES_FROM_C3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D3 = E4 | F5 | G6 | H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D3 = E2 | F1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D3 = C2 | B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D3 = C4 | B5 | A6;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][] {{E4, F5, G6, H7}, {E2, F1}, {C2, B1}, {C4, B5, A6}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D3 = ALL_OFFICER_DIR0_MOVES_FROM_D3 | ALL_OFFICER_DIR1_MOVES_FROM_D3 | ALL_OFFICER_DIR2_MOVES_FROM_D3 | ALL_OFFICER_DIR3_MOVES_FROM_D3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E3 = F4 | G5 | H6;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E3 = F2 | G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E3 = D2 | C1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E3 = D4 | C5 | B6 | A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][] {{F4, G5, H6}, {F2, G1}, {D2, C1}, {D4, C5, B6, A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E3 = ALL_OFFICER_DIR0_MOVES_FROM_E3 | ALL_OFFICER_DIR1_MOVES_FROM_E3 | ALL_OFFICER_DIR2_MOVES_FROM_E3 | ALL_OFFICER_DIR3_MOVES_FROM_E3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F3 = G4 | H5;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F3 = G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F3 = E2 | D1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F3 = E4 | D5 | C6 | B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][] {{G4, H5}, {G2, H1}, {E2, D1}, {E4, D5, C6, B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F3 = ALL_OFFICER_DIR0_MOVES_FROM_F3 | ALL_OFFICER_DIR1_MOVES_FROM_F3 | ALL_OFFICER_DIR2_MOVES_FROM_F3 | ALL_OFFICER_DIR3_MOVES_FROM_F3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G3 = H4;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G3 = H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G3 = F2 | E1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G3 = F4 | E5 | D6 | C7 | B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][] {{H4}, {H2}, {F2, E1}, {F4, E5, D6, C7, B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G3 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G3 = ALL_OFFICER_DIR0_MOVES_FROM_G3 | ALL_OFFICER_DIR1_MOVES_FROM_G3 | ALL_OFFICER_DIR2_MOVES_FROM_G3 | ALL_OFFICER_DIR3_MOVES_FROM_G3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H3 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H3 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H3 = G2 | F1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H3 = G4 | F5 | E6 | D7 | C8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][] {{}, {}, {G2, F1}, {G4, F5, E6, D7, C8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H3 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H3 = ALL_OFFICER_DIR0_MOVES_FROM_H3 | ALL_OFFICER_DIR1_MOVES_FROM_H3 | ALL_OFFICER_DIR2_MOVES_FROM_H3 | ALL_OFFICER_DIR3_MOVES_FROM_H3;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A4 = B5 | C6 | D7 | E8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A4 = B3 | C2 | D1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A4 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A4 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][] {{B5, C6, D7, E8}, {B3, C2, D1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A4 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A4 = ALL_OFFICER_DIR0_MOVES_FROM_A4 | ALL_OFFICER_DIR1_MOVES_FROM_A4 | ALL_OFFICER_DIR2_MOVES_FROM_A4 | ALL_OFFICER_DIR3_MOVES_FROM_A4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B4 = C5 | D6 | E7 | F8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B4 = C3 | D2 | E1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B4 = A3;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B4 = A5;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][] {{C5, D6, E7, F8}, {C3, D2, E1}, {A3}, {A5}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B4 = ALL_OFFICER_DIR0_MOVES_FROM_B4 | ALL_OFFICER_DIR1_MOVES_FROM_B4 | ALL_OFFICER_DIR2_MOVES_FROM_B4 | ALL_OFFICER_DIR3_MOVES_FROM_B4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C4 = D5 | E6 | F7 | G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C4 = D3 | E2 | F1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C4 = B3 | A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C4 = B5 | A6;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][] {{D5, E6, F7, G8}, {D3, E2, F1}, {B3, A2}, {B5, A6}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C4 = ALL_OFFICER_DIR0_MOVES_FROM_C4 | ALL_OFFICER_DIR1_MOVES_FROM_C4 | ALL_OFFICER_DIR2_MOVES_FROM_C4 | ALL_OFFICER_DIR3_MOVES_FROM_C4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D4 = E5 | F6 | G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D4 = E3 | F2 | G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D4 = C3 | B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D4 = C5 | B6 | A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][] {{E5, F6, G7, H8}, {E3, F2, G1}, {C3, B2, A1}, {C5, B6, A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D4 = ALL_OFFICER_DIR0_MOVES_FROM_D4 | ALL_OFFICER_DIR1_MOVES_FROM_D4 | ALL_OFFICER_DIR2_MOVES_FROM_D4 | ALL_OFFICER_DIR3_MOVES_FROM_D4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E4 = F5 | G6 | H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E4 = F3 | G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E4 = D3 | C2 | B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E4 = D5 | C6 | B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][] {{F5, G6, H7}, {F3, G2, H1}, {D3, C2, B1}, {D5, C6, B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E4 = ALL_OFFICER_DIR0_MOVES_FROM_E4 | ALL_OFFICER_DIR1_MOVES_FROM_E4 | ALL_OFFICER_DIR2_MOVES_FROM_E4 | ALL_OFFICER_DIR3_MOVES_FROM_E4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F4 = G5 | H6;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F4 = G3 | H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F4 = E3 | D2 | C1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F4 = E5 | D6 | C7 | B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][] {{G5, H6}, {G3, H2}, {E3, D2, C1}, {E5, D6, C7, B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F4 = ALL_OFFICER_DIR0_MOVES_FROM_F4 | ALL_OFFICER_DIR1_MOVES_FROM_F4 | ALL_OFFICER_DIR2_MOVES_FROM_F4 | ALL_OFFICER_DIR3_MOVES_FROM_F4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G4 = H5;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G4 = H3;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G4 = F3 | E2 | D1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G4 = F5 | E6 | D7 | C8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][] {{H5}, {H3}, {F3, E2, D1}, {F5, E6, D7, C8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G4 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G4 = ALL_OFFICER_DIR0_MOVES_FROM_G4 | ALL_OFFICER_DIR1_MOVES_FROM_G4 | ALL_OFFICER_DIR2_MOVES_FROM_G4 | ALL_OFFICER_DIR3_MOVES_FROM_G4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H4 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H4 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H4 = G3 | F2 | E1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H4 = G5 | F6 | E7 | D8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][] {{}, {}, {G3, F2, E1}, {G5, F6, E7, D8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H4 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H4 = ALL_OFFICER_DIR0_MOVES_FROM_H4 | ALL_OFFICER_DIR1_MOVES_FROM_H4 | ALL_OFFICER_DIR2_MOVES_FROM_H4 | ALL_OFFICER_DIR3_MOVES_FROM_H4;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A5 = B6 | C7 | D8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A5 = B4 | C3 | D2 | E1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A5 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A5 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][] {{B6, C7, D8}, {B4, C3, D2, E1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A5 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A5 = ALL_OFFICER_DIR0_MOVES_FROM_A5 | ALL_OFFICER_DIR1_MOVES_FROM_A5 | ALL_OFFICER_DIR2_MOVES_FROM_A5 | ALL_OFFICER_DIR3_MOVES_FROM_A5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B5 = C6 | D7 | E8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B5 = C4 | D3 | E2 | F1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B5 = A4;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B5 = A6;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][] {{C6, D7, E8}, {C4, D3, E2, F1}, {A4}, {A6}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B5 = ALL_OFFICER_DIR0_MOVES_FROM_B5 | ALL_OFFICER_DIR1_MOVES_FROM_B5 | ALL_OFFICER_DIR2_MOVES_FROM_B5 | ALL_OFFICER_DIR3_MOVES_FROM_B5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C5 = D6 | E7 | F8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C5 = D4 | E3 | F2 | G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C5 = B4 | A3;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C5 = B6 | A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][] {{D6, E7, F8}, {D4, E3, F2, G1}, {B4, A3}, {B6, A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C5 = ALL_OFFICER_DIR0_MOVES_FROM_C5 | ALL_OFFICER_DIR1_MOVES_FROM_C5 | ALL_OFFICER_DIR2_MOVES_FROM_C5 | ALL_OFFICER_DIR3_MOVES_FROM_C5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D5 = E6 | F7 | G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D5 = E4 | F3 | G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D5 = C4 | B3 | A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D5 = C6 | B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][] {{E6, F7, G8}, {E4, F3, G2, H1}, {C4, B3, A2}, {C6, B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D5 = ALL_OFFICER_DIR0_MOVES_FROM_D5 | ALL_OFFICER_DIR1_MOVES_FROM_D5 | ALL_OFFICER_DIR2_MOVES_FROM_D5 | ALL_OFFICER_DIR3_MOVES_FROM_D5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E5 = F6 | G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E5 = F4 | G3 | H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E5 = D4 | C3 | B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E5 = D6 | C7 | B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][] {{F6, G7, H8}, {F4, G3, H2}, {D4, C3, B2, A1}, {D6, C7, B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E5 = ALL_OFFICER_DIR0_MOVES_FROM_E5 | ALL_OFFICER_DIR1_MOVES_FROM_E5 | ALL_OFFICER_DIR2_MOVES_FROM_E5 | ALL_OFFICER_DIR3_MOVES_FROM_E5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F5 = G6 | H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F5 = G4 | H3;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F5 = E4 | D3 | C2 | B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F5 = E6 | D7 | C8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][] {{G6, H7}, {G4, H3}, {E4, D3, C2, B1}, {E6, D7, C8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F5 = ALL_OFFICER_DIR0_MOVES_FROM_F5 | ALL_OFFICER_DIR1_MOVES_FROM_F5 | ALL_OFFICER_DIR2_MOVES_FROM_F5 | ALL_OFFICER_DIR3_MOVES_FROM_F5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G5 = H6;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G5 = H4;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G5 = F4 | E3 | D2 | C1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G5 = F6 | E7 | D8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][] {{H6}, {H4}, {F4, E3, D2, C1}, {F6, E7, D8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G5 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G5 = ALL_OFFICER_DIR0_MOVES_FROM_G5 | ALL_OFFICER_DIR1_MOVES_FROM_G5 | ALL_OFFICER_DIR2_MOVES_FROM_G5 | ALL_OFFICER_DIR3_MOVES_FROM_G5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H5 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H5 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H5 = G4 | F3 | E2 | D1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H5 = G6 | F7 | E8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][] {{}, {}, {G4, F3, E2, D1}, {G6, F7, E8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H5 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H5 = ALL_OFFICER_DIR0_MOVES_FROM_H5 | ALL_OFFICER_DIR1_MOVES_FROM_H5 | ALL_OFFICER_DIR2_MOVES_FROM_H5 | ALL_OFFICER_DIR3_MOVES_FROM_H5;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A6 = B7 | C8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A6 = B5 | C4 | D3 | E2 | F1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A6 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A6 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][] {{B7, C8}, {B5, C4, D3, E2, F1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A6 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A6 = ALL_OFFICER_DIR0_MOVES_FROM_A6 | ALL_OFFICER_DIR1_MOVES_FROM_A6 | ALL_OFFICER_DIR2_MOVES_FROM_A6 | ALL_OFFICER_DIR3_MOVES_FROM_A6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B6 = C7 | D8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B6 = C5 | D4 | E3 | F2 | G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B6 = A5;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B6 = A7;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][] {{C7, D8}, {C5, D4, E3, F2, G1}, {A5}, {A7}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B6 = ALL_OFFICER_DIR0_MOVES_FROM_B6 | ALL_OFFICER_DIR1_MOVES_FROM_B6 | ALL_OFFICER_DIR2_MOVES_FROM_B6 | ALL_OFFICER_DIR3_MOVES_FROM_B6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C6 = D7 | E8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C6 = D5 | E4 | F3 | G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C6 = B5 | A4;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C6 = B7 | A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][] {{D7, E8}, {D5, E4, F3, G2, H1}, {B5, A4}, {B7, A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C6 = ALL_OFFICER_DIR0_MOVES_FROM_C6 | ALL_OFFICER_DIR1_MOVES_FROM_C6 | ALL_OFFICER_DIR2_MOVES_FROM_C6 | ALL_OFFICER_DIR3_MOVES_FROM_C6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D6 = E7 | F8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D6 = E5 | F4 | G3 | H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D6 = C5 | B4 | A3;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D6 = C7 | B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][] {{E7, F8}, {E5, F4, G3, H2}, {C5, B4, A3}, {C7, B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D6 = ALL_OFFICER_DIR0_MOVES_FROM_D6 | ALL_OFFICER_DIR1_MOVES_FROM_D6 | ALL_OFFICER_DIR2_MOVES_FROM_D6 | ALL_OFFICER_DIR3_MOVES_FROM_D6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E6 = F7 | G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E6 = F5 | G4 | H3;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E6 = D5 | C4 | B3 | A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E6 = D7 | C8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][] {{F7, G8}, {F5, G4, H3}, {D5, C4, B3, A2}, {D7, C8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E6 = ALL_OFFICER_DIR0_MOVES_FROM_E6 | ALL_OFFICER_DIR1_MOVES_FROM_E6 | ALL_OFFICER_DIR2_MOVES_FROM_E6 | ALL_OFFICER_DIR3_MOVES_FROM_E6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F6 = G7 | H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F6 = G5 | H4;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F6 = E5 | D4 | C3 | B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F6 = E7 | D8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][] {{G7, H8}, {G5, H4}, {E5, D4, C3, B2, A1}, {E7, D8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F6 = ALL_OFFICER_DIR0_MOVES_FROM_F6 | ALL_OFFICER_DIR1_MOVES_FROM_F6 | ALL_OFFICER_DIR2_MOVES_FROM_F6 | ALL_OFFICER_DIR3_MOVES_FROM_F6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G6 = H7;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G6 = H5;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G6 = F5 | E4 | D3 | C2 | B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G6 = F7 | E8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][] {{H7}, {H5}, {F5, E4, D3, C2, B1}, {F7, E8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G6 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G6 = ALL_OFFICER_DIR0_MOVES_FROM_G6 | ALL_OFFICER_DIR1_MOVES_FROM_G6 | ALL_OFFICER_DIR2_MOVES_FROM_G6 | ALL_OFFICER_DIR3_MOVES_FROM_G6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H6 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H6 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H6 = G5 | F4 | E3 | D2 | C1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H6 = G7 | F8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][] {{}, {}, {G5, F4, E3, D2, C1}, {G7, F8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H6 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H6 = ALL_OFFICER_DIR0_MOVES_FROM_H6 | ALL_OFFICER_DIR1_MOVES_FROM_H6 | ALL_OFFICER_DIR2_MOVES_FROM_H6 | ALL_OFFICER_DIR3_MOVES_FROM_H6;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A7 = B8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A7 = B6 | C5 | D4 | E3 | F2 | G1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A7 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A7 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][] {{B8}, {B6, C5, D4, E3, F2, G1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A7 = new int[] {0, 1};
	public static final long ALL_OFFICER_MOVES_FROM_A7 = ALL_OFFICER_DIR0_MOVES_FROM_A7 | ALL_OFFICER_DIR1_MOVES_FROM_A7 | ALL_OFFICER_DIR2_MOVES_FROM_A7 | ALL_OFFICER_DIR3_MOVES_FROM_A7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B7 = C8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B7 = C6 | D5 | E4 | F3 | G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B7 = A6;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B7 = A8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][] {{C8}, {C6, D5, E4, F3, G2, H1}, {A6}, {A8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_B7 = ALL_OFFICER_DIR0_MOVES_FROM_B7 | ALL_OFFICER_DIR1_MOVES_FROM_B7 | ALL_OFFICER_DIR2_MOVES_FROM_B7 | ALL_OFFICER_DIR3_MOVES_FROM_B7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C7 = D8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C7 = D6 | E5 | F4 | G3 | H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C7 = B6 | A5;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C7 = B8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][] {{D8}, {D6, E5, F4, G3, H2}, {B6, A5}, {B8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_C7 = ALL_OFFICER_DIR0_MOVES_FROM_C7 | ALL_OFFICER_DIR1_MOVES_FROM_C7 | ALL_OFFICER_DIR2_MOVES_FROM_C7 | ALL_OFFICER_DIR3_MOVES_FROM_C7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D7 = E8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D7 = E6 | F5 | G4 | H3;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D7 = C6 | B5 | A4;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D7 = C8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][] {{E8}, {E6, F5, G4, H3}, {C6, B5, A4}, {C8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_D7 = ALL_OFFICER_DIR0_MOVES_FROM_D7 | ALL_OFFICER_DIR1_MOVES_FROM_D7 | ALL_OFFICER_DIR2_MOVES_FROM_D7 | ALL_OFFICER_DIR3_MOVES_FROM_D7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E7 = F8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E7 = F6 | G5 | H4;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E7 = D6 | C5 | B4 | A3;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E7 = D8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][] {{F8}, {F6, G5, H4}, {D6, C5, B4, A3}, {D8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_E7 = ALL_OFFICER_DIR0_MOVES_FROM_E7 | ALL_OFFICER_DIR1_MOVES_FROM_E7 | ALL_OFFICER_DIR2_MOVES_FROM_E7 | ALL_OFFICER_DIR3_MOVES_FROM_E7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F7 = G8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F7 = G6 | H5;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F7 = E6 | D5 | C4 | B3 | A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F7 = E8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][] {{G8}, {G6, H5}, {E6, D5, C4, B3, A2}, {E8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_F7 = ALL_OFFICER_DIR0_MOVES_FROM_F7 | ALL_OFFICER_DIR1_MOVES_FROM_F7 | ALL_OFFICER_DIR2_MOVES_FROM_F7 | ALL_OFFICER_DIR3_MOVES_FROM_F7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G7 = H8;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G7 = H6;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G7 = F6 | E5 | D4 | C3 | B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G7 = F8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][] {{H8}, {H6}, {F6, E5, D4, C3, B2, A1}, {F8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G7 = new int[] {0, 1, 2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_G7 = ALL_OFFICER_DIR0_MOVES_FROM_G7 | ALL_OFFICER_DIR1_MOVES_FROM_G7 | ALL_OFFICER_DIR2_MOVES_FROM_G7 | ALL_OFFICER_DIR3_MOVES_FROM_G7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H7 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H7 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H7 = G6 | F5 | E4 | D3 | C2 | B1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H7 = G8;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][] {{}, {}, {G6, F5, E4, D3, C2, B1}, {G8}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H7 = new int[] {2, 3};
	public static final long ALL_OFFICER_MOVES_FROM_H7 = ALL_OFFICER_DIR0_MOVES_FROM_H7 | ALL_OFFICER_DIR1_MOVES_FROM_H7 | ALL_OFFICER_DIR2_MOVES_FROM_H7 | ALL_OFFICER_DIR3_MOVES_FROM_H7;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_A8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_A8 = B7 | C6 | D5 | E4 | F3 | G2 | H1;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_A8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_A8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][] {{}, {B7, C6, D5, E4, F3, G2, H1}, {}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A8 = new int[] {1};
	public static final long ALL_OFFICER_MOVES_FROM_A8 = ALL_OFFICER_DIR0_MOVES_FROM_A8 | ALL_OFFICER_DIR1_MOVES_FROM_A8 | ALL_OFFICER_DIR2_MOVES_FROM_A8 | ALL_OFFICER_DIR3_MOVES_FROM_A8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_B8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_B8 = C7 | D6 | E5 | F4 | G3 | H2;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_B8 = A7;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_B8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][] {{}, {C7, D6, E5, F4, G3, H2}, {A7}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_B8 = ALL_OFFICER_DIR0_MOVES_FROM_B8 | ALL_OFFICER_DIR1_MOVES_FROM_B8 | ALL_OFFICER_DIR2_MOVES_FROM_B8 | ALL_OFFICER_DIR3_MOVES_FROM_B8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_C8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_C8 = D7 | E6 | F5 | G4 | H3;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_C8 = B7 | A6;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_C8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][] {{}, {D7, E6, F5, G4, H3}, {B7, A6}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_C8 = ALL_OFFICER_DIR0_MOVES_FROM_C8 | ALL_OFFICER_DIR1_MOVES_FROM_C8 | ALL_OFFICER_DIR2_MOVES_FROM_C8 | ALL_OFFICER_DIR3_MOVES_FROM_C8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_D8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_D8 = E7 | F6 | G5 | H4;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_D8 = C7 | B6 | A5;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_D8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][] {{}, {E7, F6, G5, H4}, {C7, B6, A5}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_D8 = ALL_OFFICER_DIR0_MOVES_FROM_D8 | ALL_OFFICER_DIR1_MOVES_FROM_D8 | ALL_OFFICER_DIR2_MOVES_FROM_D8 | ALL_OFFICER_DIR3_MOVES_FROM_D8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_E8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_E8 = F7 | G6 | H5;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_E8 = D7 | C6 | B5 | A4;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_E8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][] {{}, {F7, G6, H5}, {D7, C6, B5, A4}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_E8 = ALL_OFFICER_DIR0_MOVES_FROM_E8 | ALL_OFFICER_DIR1_MOVES_FROM_E8 | ALL_OFFICER_DIR2_MOVES_FROM_E8 | ALL_OFFICER_DIR3_MOVES_FROM_E8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_F8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_F8 = G7 | H6;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_F8 = E7 | D6 | C5 | B4 | A3;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_F8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][] {{}, {G7, H6}, {E7, D6, C5, B4, A3}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_F8 = ALL_OFFICER_DIR0_MOVES_FROM_F8 | ALL_OFFICER_DIR1_MOVES_FROM_F8 | ALL_OFFICER_DIR2_MOVES_FROM_F8 | ALL_OFFICER_DIR3_MOVES_FROM_F8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_G8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_G8 = H7;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_G8 = F7 | E6 | D5 | C4 | B3 | A2;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_G8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][] {{}, {H7}, {F7, E6, D5, C4, B3, A2}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G8 = new int[] {1, 2};
	public static final long ALL_OFFICER_MOVES_FROM_G8 = ALL_OFFICER_DIR0_MOVES_FROM_G8 | ALL_OFFICER_DIR1_MOVES_FROM_G8 | ALL_OFFICER_DIR2_MOVES_FROM_G8 | ALL_OFFICER_DIR3_MOVES_FROM_G8;
	public static final long ALL_OFFICER_DIR0_MOVES_FROM_H8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR1_MOVES_FROM_H8 = NUMBER_0;
	public static final long ALL_OFFICER_DIR2_MOVES_FROM_H8 = G7 | F6 | E5 | D4 | C3 | B2 | A1;
	public static final long ALL_OFFICER_DIR3_MOVES_FROM_H8 = NUMBER_0;
	public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][] {{}, {}, {G7, F6, E5, D4, C3, B2, A1}, {}};
	public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H8 = new int[] {2};
	public static final long ALL_OFFICER_MOVES_FROM_H8 = ALL_OFFICER_DIR0_MOVES_FROM_H8 | ALL_OFFICER_DIR1_MOVES_FROM_H8 | ALL_OFFICER_DIR2_MOVES_FROM_H8 | ALL_OFFICER_DIR3_MOVES_FROM_H8;

	public static final long[] ALL_ORDERED_OFFICER_MOVES = new long[] {ALL_OFFICER_MOVES_FROM_A1, ALL_OFFICER_MOVES_FROM_B1, ALL_OFFICER_MOVES_FROM_C1, ALL_OFFICER_MOVES_FROM_D1, ALL_OFFICER_MOVES_FROM_E1, ALL_OFFICER_MOVES_FROM_F1, ALL_OFFICER_MOVES_FROM_G1, ALL_OFFICER_MOVES_FROM_H1, ALL_OFFICER_MOVES_FROM_A2, ALL_OFFICER_MOVES_FROM_B2, ALL_OFFICER_MOVES_FROM_C2, ALL_OFFICER_MOVES_FROM_D2, ALL_OFFICER_MOVES_FROM_E2, ALL_OFFICER_MOVES_FROM_F2, ALL_OFFICER_MOVES_FROM_G2, ALL_OFFICER_MOVES_FROM_H2, ALL_OFFICER_MOVES_FROM_A3, ALL_OFFICER_MOVES_FROM_B3, ALL_OFFICER_MOVES_FROM_C3, ALL_OFFICER_MOVES_FROM_D3, ALL_OFFICER_MOVES_FROM_E3, ALL_OFFICER_MOVES_FROM_F3, ALL_OFFICER_MOVES_FROM_G3, ALL_OFFICER_MOVES_FROM_H3, ALL_OFFICER_MOVES_FROM_A4, ALL_OFFICER_MOVES_FROM_B4, ALL_OFFICER_MOVES_FROM_C4, ALL_OFFICER_MOVES_FROM_D4, ALL_OFFICER_MOVES_FROM_E4, ALL_OFFICER_MOVES_FROM_F4, ALL_OFFICER_MOVES_FROM_G4, ALL_OFFICER_MOVES_FROM_H4, ALL_OFFICER_MOVES_FROM_A5, ALL_OFFICER_MOVES_FROM_B5, ALL_OFFICER_MOVES_FROM_C5, ALL_OFFICER_MOVES_FROM_D5, ALL_OFFICER_MOVES_FROM_E5, ALL_OFFICER_MOVES_FROM_F5, ALL_OFFICER_MOVES_FROM_G5, ALL_OFFICER_MOVES_FROM_H5, ALL_OFFICER_MOVES_FROM_A6, ALL_OFFICER_MOVES_FROM_B6, ALL_OFFICER_MOVES_FROM_C6, ALL_OFFICER_MOVES_FROM_D6, ALL_OFFICER_MOVES_FROM_E6, ALL_OFFICER_MOVES_FROM_F6, ALL_OFFICER_MOVES_FROM_G6, ALL_OFFICER_MOVES_FROM_H6, ALL_OFFICER_MOVES_FROM_A7, ALL_OFFICER_MOVES_FROM_B7, ALL_OFFICER_MOVES_FROM_C7, ALL_OFFICER_MOVES_FROM_D7, ALL_OFFICER_MOVES_FROM_E7, ALL_OFFICER_MOVES_FROM_F7, ALL_OFFICER_MOVES_FROM_G7, ALL_OFFICER_MOVES_FROM_H7, ALL_OFFICER_MOVES_FROM_A8, ALL_OFFICER_MOVES_FROM_B8, ALL_OFFICER_MOVES_FROM_C8, ALL_OFFICER_MOVES_FROM_D8, ALL_OFFICER_MOVES_FROM_E8, ALL_OFFICER_MOVES_FROM_F8, ALL_OFFICER_MOVES_FROM_G8, ALL_OFFICER_MOVES_FROM_H8, };
	public static final long[] ALL_ORDERED_DIR0_OFFICER_MOVES = new long[] {ALL_OFFICER_DIR0_MOVES_FROM_A1, ALL_OFFICER_DIR0_MOVES_FROM_B1, ALL_OFFICER_DIR0_MOVES_FROM_C1, ALL_OFFICER_DIR0_MOVES_FROM_D1, ALL_OFFICER_DIR0_MOVES_FROM_E1, ALL_OFFICER_DIR0_MOVES_FROM_F1, ALL_OFFICER_DIR0_MOVES_FROM_G1, ALL_OFFICER_DIR0_MOVES_FROM_H1, ALL_OFFICER_DIR0_MOVES_FROM_A2, ALL_OFFICER_DIR0_MOVES_FROM_B2, ALL_OFFICER_DIR0_MOVES_FROM_C2, ALL_OFFICER_DIR0_MOVES_FROM_D2, ALL_OFFICER_DIR0_MOVES_FROM_E2, ALL_OFFICER_DIR0_MOVES_FROM_F2, ALL_OFFICER_DIR0_MOVES_FROM_G2, ALL_OFFICER_DIR0_MOVES_FROM_H2, ALL_OFFICER_DIR0_MOVES_FROM_A3, ALL_OFFICER_DIR0_MOVES_FROM_B3, ALL_OFFICER_DIR0_MOVES_FROM_C3, ALL_OFFICER_DIR0_MOVES_FROM_D3, ALL_OFFICER_DIR0_MOVES_FROM_E3, ALL_OFFICER_DIR0_MOVES_FROM_F3, ALL_OFFICER_DIR0_MOVES_FROM_G3, ALL_OFFICER_DIR0_MOVES_FROM_H3, ALL_OFFICER_DIR0_MOVES_FROM_A4, ALL_OFFICER_DIR0_MOVES_FROM_B4, ALL_OFFICER_DIR0_MOVES_FROM_C4, ALL_OFFICER_DIR0_MOVES_FROM_D4, ALL_OFFICER_DIR0_MOVES_FROM_E4, ALL_OFFICER_DIR0_MOVES_FROM_F4, ALL_OFFICER_DIR0_MOVES_FROM_G4, ALL_OFFICER_DIR0_MOVES_FROM_H4, ALL_OFFICER_DIR0_MOVES_FROM_A5, ALL_OFFICER_DIR0_MOVES_FROM_B5, ALL_OFFICER_DIR0_MOVES_FROM_C5, ALL_OFFICER_DIR0_MOVES_FROM_D5, ALL_OFFICER_DIR0_MOVES_FROM_E5, ALL_OFFICER_DIR0_MOVES_FROM_F5, ALL_OFFICER_DIR0_MOVES_FROM_G5, ALL_OFFICER_DIR0_MOVES_FROM_H5, ALL_OFFICER_DIR0_MOVES_FROM_A6, ALL_OFFICER_DIR0_MOVES_FROM_B6, ALL_OFFICER_DIR0_MOVES_FROM_C6, ALL_OFFICER_DIR0_MOVES_FROM_D6, ALL_OFFICER_DIR0_MOVES_FROM_E6, ALL_OFFICER_DIR0_MOVES_FROM_F6, ALL_OFFICER_DIR0_MOVES_FROM_G6, ALL_OFFICER_DIR0_MOVES_FROM_H6, ALL_OFFICER_DIR0_MOVES_FROM_A7, ALL_OFFICER_DIR0_MOVES_FROM_B7, ALL_OFFICER_DIR0_MOVES_FROM_C7, ALL_OFFICER_DIR0_MOVES_FROM_D7, ALL_OFFICER_DIR0_MOVES_FROM_E7, ALL_OFFICER_DIR0_MOVES_FROM_F7, ALL_OFFICER_DIR0_MOVES_FROM_G7, ALL_OFFICER_DIR0_MOVES_FROM_H7, ALL_OFFICER_DIR0_MOVES_FROM_A8, ALL_OFFICER_DIR0_MOVES_FROM_B8, ALL_OFFICER_DIR0_MOVES_FROM_C8, ALL_OFFICER_DIR0_MOVES_FROM_D8, ALL_OFFICER_DIR0_MOVES_FROM_E8, ALL_OFFICER_DIR0_MOVES_FROM_F8, ALL_OFFICER_DIR0_MOVES_FROM_G8, ALL_OFFICER_DIR0_MOVES_FROM_H8, };
	public static final long[] ALL_ORDERED_DIR1_OFFICER_MOVES = new long[] {ALL_OFFICER_DIR1_MOVES_FROM_A1, ALL_OFFICER_DIR1_MOVES_FROM_B1, ALL_OFFICER_DIR1_MOVES_FROM_C1, ALL_OFFICER_DIR1_MOVES_FROM_D1, ALL_OFFICER_DIR1_MOVES_FROM_E1, ALL_OFFICER_DIR1_MOVES_FROM_F1, ALL_OFFICER_DIR1_MOVES_FROM_G1, ALL_OFFICER_DIR1_MOVES_FROM_H1, ALL_OFFICER_DIR1_MOVES_FROM_A2, ALL_OFFICER_DIR1_MOVES_FROM_B2, ALL_OFFICER_DIR1_MOVES_FROM_C2, ALL_OFFICER_DIR1_MOVES_FROM_D2, ALL_OFFICER_DIR1_MOVES_FROM_E2, ALL_OFFICER_DIR1_MOVES_FROM_F2, ALL_OFFICER_DIR1_MOVES_FROM_G2, ALL_OFFICER_DIR1_MOVES_FROM_H2, ALL_OFFICER_DIR1_MOVES_FROM_A3, ALL_OFFICER_DIR1_MOVES_FROM_B3, ALL_OFFICER_DIR1_MOVES_FROM_C3, ALL_OFFICER_DIR1_MOVES_FROM_D3, ALL_OFFICER_DIR1_MOVES_FROM_E3, ALL_OFFICER_DIR1_MOVES_FROM_F3, ALL_OFFICER_DIR1_MOVES_FROM_G3, ALL_OFFICER_DIR1_MOVES_FROM_H3, ALL_OFFICER_DIR1_MOVES_FROM_A4, ALL_OFFICER_DIR1_MOVES_FROM_B4, ALL_OFFICER_DIR1_MOVES_FROM_C4, ALL_OFFICER_DIR1_MOVES_FROM_D4, ALL_OFFICER_DIR1_MOVES_FROM_E4, ALL_OFFICER_DIR1_MOVES_FROM_F4, ALL_OFFICER_DIR1_MOVES_FROM_G4, ALL_OFFICER_DIR1_MOVES_FROM_H4, ALL_OFFICER_DIR1_MOVES_FROM_A5, ALL_OFFICER_DIR1_MOVES_FROM_B5, ALL_OFFICER_DIR1_MOVES_FROM_C5, ALL_OFFICER_DIR1_MOVES_FROM_D5, ALL_OFFICER_DIR1_MOVES_FROM_E5, ALL_OFFICER_DIR1_MOVES_FROM_F5, ALL_OFFICER_DIR1_MOVES_FROM_G5, ALL_OFFICER_DIR1_MOVES_FROM_H5, ALL_OFFICER_DIR1_MOVES_FROM_A6, ALL_OFFICER_DIR1_MOVES_FROM_B6, ALL_OFFICER_DIR1_MOVES_FROM_C6, ALL_OFFICER_DIR1_MOVES_FROM_D6, ALL_OFFICER_DIR1_MOVES_FROM_E6, ALL_OFFICER_DIR1_MOVES_FROM_F6, ALL_OFFICER_DIR1_MOVES_FROM_G6, ALL_OFFICER_DIR1_MOVES_FROM_H6, ALL_OFFICER_DIR1_MOVES_FROM_A7, ALL_OFFICER_DIR1_MOVES_FROM_B7, ALL_OFFICER_DIR1_MOVES_FROM_C7, ALL_OFFICER_DIR1_MOVES_FROM_D7, ALL_OFFICER_DIR1_MOVES_FROM_E7, ALL_OFFICER_DIR1_MOVES_FROM_F7, ALL_OFFICER_DIR1_MOVES_FROM_G7, ALL_OFFICER_DIR1_MOVES_FROM_H7, ALL_OFFICER_DIR1_MOVES_FROM_A8, ALL_OFFICER_DIR1_MOVES_FROM_B8, ALL_OFFICER_DIR1_MOVES_FROM_C8, ALL_OFFICER_DIR1_MOVES_FROM_D8, ALL_OFFICER_DIR1_MOVES_FROM_E8, ALL_OFFICER_DIR1_MOVES_FROM_F8, ALL_OFFICER_DIR1_MOVES_FROM_G8, ALL_OFFICER_DIR1_MOVES_FROM_H8, };
	public static final long[] ALL_ORDERED_DIR2_OFFICER_MOVES = new long[] {ALL_OFFICER_DIR2_MOVES_FROM_A1, ALL_OFFICER_DIR2_MOVES_FROM_B1, ALL_OFFICER_DIR2_MOVES_FROM_C1, ALL_OFFICER_DIR2_MOVES_FROM_D1, ALL_OFFICER_DIR2_MOVES_FROM_E1, ALL_OFFICER_DIR2_MOVES_FROM_F1, ALL_OFFICER_DIR2_MOVES_FROM_G1, ALL_OFFICER_DIR2_MOVES_FROM_H1, ALL_OFFICER_DIR2_MOVES_FROM_A2, ALL_OFFICER_DIR2_MOVES_FROM_B2, ALL_OFFICER_DIR2_MOVES_FROM_C2, ALL_OFFICER_DIR2_MOVES_FROM_D2, ALL_OFFICER_DIR2_MOVES_FROM_E2, ALL_OFFICER_DIR2_MOVES_FROM_F2, ALL_OFFICER_DIR2_MOVES_FROM_G2, ALL_OFFICER_DIR2_MOVES_FROM_H2, ALL_OFFICER_DIR2_MOVES_FROM_A3, ALL_OFFICER_DIR2_MOVES_FROM_B3, ALL_OFFICER_DIR2_MOVES_FROM_C3, ALL_OFFICER_DIR2_MOVES_FROM_D3, ALL_OFFICER_DIR2_MOVES_FROM_E3, ALL_OFFICER_DIR2_MOVES_FROM_F3, ALL_OFFICER_DIR2_MOVES_FROM_G3, ALL_OFFICER_DIR2_MOVES_FROM_H3, ALL_OFFICER_DIR2_MOVES_FROM_A4, ALL_OFFICER_DIR2_MOVES_FROM_B4, ALL_OFFICER_DIR2_MOVES_FROM_C4, ALL_OFFICER_DIR2_MOVES_FROM_D4, ALL_OFFICER_DIR2_MOVES_FROM_E4, ALL_OFFICER_DIR2_MOVES_FROM_F4, ALL_OFFICER_DIR2_MOVES_FROM_G4, ALL_OFFICER_DIR2_MOVES_FROM_H4, ALL_OFFICER_DIR2_MOVES_FROM_A5, ALL_OFFICER_DIR2_MOVES_FROM_B5, ALL_OFFICER_DIR2_MOVES_FROM_C5, ALL_OFFICER_DIR2_MOVES_FROM_D5, ALL_OFFICER_DIR2_MOVES_FROM_E5, ALL_OFFICER_DIR2_MOVES_FROM_F5, ALL_OFFICER_DIR2_MOVES_FROM_G5, ALL_OFFICER_DIR2_MOVES_FROM_H5, ALL_OFFICER_DIR2_MOVES_FROM_A6, ALL_OFFICER_DIR2_MOVES_FROM_B6, ALL_OFFICER_DIR2_MOVES_FROM_C6, ALL_OFFICER_DIR2_MOVES_FROM_D6, ALL_OFFICER_DIR2_MOVES_FROM_E6, ALL_OFFICER_DIR2_MOVES_FROM_F6, ALL_OFFICER_DIR2_MOVES_FROM_G6, ALL_OFFICER_DIR2_MOVES_FROM_H6, ALL_OFFICER_DIR2_MOVES_FROM_A7, ALL_OFFICER_DIR2_MOVES_FROM_B7, ALL_OFFICER_DIR2_MOVES_FROM_C7, ALL_OFFICER_DIR2_MOVES_FROM_D7, ALL_OFFICER_DIR2_MOVES_FROM_E7, ALL_OFFICER_DIR2_MOVES_FROM_F7, ALL_OFFICER_DIR2_MOVES_FROM_G7, ALL_OFFICER_DIR2_MOVES_FROM_H7, ALL_OFFICER_DIR2_MOVES_FROM_A8, ALL_OFFICER_DIR2_MOVES_FROM_B8, ALL_OFFICER_DIR2_MOVES_FROM_C8, ALL_OFFICER_DIR2_MOVES_FROM_D8, ALL_OFFICER_DIR2_MOVES_FROM_E8, ALL_OFFICER_DIR2_MOVES_FROM_F8, ALL_OFFICER_DIR2_MOVES_FROM_G8, ALL_OFFICER_DIR2_MOVES_FROM_H8, };
	public static final long[] ALL_ORDERED_DIR3_OFFICER_MOVES = new long[] {ALL_OFFICER_DIR3_MOVES_FROM_A1, ALL_OFFICER_DIR3_MOVES_FROM_B1, ALL_OFFICER_DIR3_MOVES_FROM_C1, ALL_OFFICER_DIR3_MOVES_FROM_D1, ALL_OFFICER_DIR3_MOVES_FROM_E1, ALL_OFFICER_DIR3_MOVES_FROM_F1, ALL_OFFICER_DIR3_MOVES_FROM_G1, ALL_OFFICER_DIR3_MOVES_FROM_H1, ALL_OFFICER_DIR3_MOVES_FROM_A2, ALL_OFFICER_DIR3_MOVES_FROM_B2, ALL_OFFICER_DIR3_MOVES_FROM_C2, ALL_OFFICER_DIR3_MOVES_FROM_D2, ALL_OFFICER_DIR3_MOVES_FROM_E2, ALL_OFFICER_DIR3_MOVES_FROM_F2, ALL_OFFICER_DIR3_MOVES_FROM_G2, ALL_OFFICER_DIR3_MOVES_FROM_H2, ALL_OFFICER_DIR3_MOVES_FROM_A3, ALL_OFFICER_DIR3_MOVES_FROM_B3, ALL_OFFICER_DIR3_MOVES_FROM_C3, ALL_OFFICER_DIR3_MOVES_FROM_D3, ALL_OFFICER_DIR3_MOVES_FROM_E3, ALL_OFFICER_DIR3_MOVES_FROM_F3, ALL_OFFICER_DIR3_MOVES_FROM_G3, ALL_OFFICER_DIR3_MOVES_FROM_H3, ALL_OFFICER_DIR3_MOVES_FROM_A4, ALL_OFFICER_DIR3_MOVES_FROM_B4, ALL_OFFICER_DIR3_MOVES_FROM_C4, ALL_OFFICER_DIR3_MOVES_FROM_D4, ALL_OFFICER_DIR3_MOVES_FROM_E4, ALL_OFFICER_DIR3_MOVES_FROM_F4, ALL_OFFICER_DIR3_MOVES_FROM_G4, ALL_OFFICER_DIR3_MOVES_FROM_H4, ALL_OFFICER_DIR3_MOVES_FROM_A5, ALL_OFFICER_DIR3_MOVES_FROM_B5, ALL_OFFICER_DIR3_MOVES_FROM_C5, ALL_OFFICER_DIR3_MOVES_FROM_D5, ALL_OFFICER_DIR3_MOVES_FROM_E5, ALL_OFFICER_DIR3_MOVES_FROM_F5, ALL_OFFICER_DIR3_MOVES_FROM_G5, ALL_OFFICER_DIR3_MOVES_FROM_H5, ALL_OFFICER_DIR3_MOVES_FROM_A6, ALL_OFFICER_DIR3_MOVES_FROM_B6, ALL_OFFICER_DIR3_MOVES_FROM_C6, ALL_OFFICER_DIR3_MOVES_FROM_D6, ALL_OFFICER_DIR3_MOVES_FROM_E6, ALL_OFFICER_DIR3_MOVES_FROM_F6, ALL_OFFICER_DIR3_MOVES_FROM_G6, ALL_OFFICER_DIR3_MOVES_FROM_H6, ALL_OFFICER_DIR3_MOVES_FROM_A7, ALL_OFFICER_DIR3_MOVES_FROM_B7, ALL_OFFICER_DIR3_MOVES_FROM_C7, ALL_OFFICER_DIR3_MOVES_FROM_D7, ALL_OFFICER_DIR3_MOVES_FROM_E7, ALL_OFFICER_DIR3_MOVES_FROM_F7, ALL_OFFICER_DIR3_MOVES_FROM_G7, ALL_OFFICER_DIR3_MOVES_FROM_H7, ALL_OFFICER_DIR3_MOVES_FROM_A8, ALL_OFFICER_DIR3_MOVES_FROM_B8, ALL_OFFICER_DIR3_MOVES_FROM_C8, ALL_OFFICER_DIR3_MOVES_FROM_D8, ALL_OFFICER_DIR3_MOVES_FROM_E8, ALL_OFFICER_DIR3_MOVES_FROM_F8, ALL_OFFICER_DIR3_MOVES_FROM_G8, ALL_OFFICER_DIR3_MOVES_FROM_H8, };
	public static final long[][][] ALL_ORDERED_OFFICER_DIRS = new long[][][] {ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H8, };
	public static final int[][] ALL_ORDERED_OFFICER_VALID_DIRS = new int[][] {ALL_OFFICER_VALID_DIR_INDEXES_FROM_A1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H8, };
	
	//---------------------------- END GENERATED BLOCK ----------------------------------------------
	
	public static final long[] ALL_OFFICER_MOVES = new long[Bits.PRIME_67];
	public static final long[][] ALL_OFFICER_DIR_MOVES = new long[4][Bits.PRIME_67];
	public static final long[] ALL_OFFICER_DIR0_MOVES = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_DIR1_MOVES = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_DIR2_MOVES = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_DIR3_MOVES = new long[Bits.PRIME_67];
	
	public static final long[] ALL_OFFICER_MOVES_1P = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_MOVES_2P = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_MOVES_34P = new long[Bits.PRIME_67];
	public static final long[] ALL_OFFICER_MOVES_567P = new long[Bits.PRIME_67];
	
	public static final int UP_LEFT_DIR = 3;
	public static final int UP_RIGHT_DIR = 0;
	public static final int DOWN_LEFT_DIR = 2;
	public static final int DOWN_RIGHT_DIR = 1;
	
	public static final int[][] ALL_OFFICER_VALID_DIRS = new int[Bits.PRIME_67][];
	public static final int[][][] ALL_OFFICER_DIRS_WITH_FIELD_IDS = new int[Bits.PRIME_67][][];
	public static final long[][][] ALL_OFFICER_DIRS_WITH_BITBOARDS = new long[Bits.PRIME_67][][];
	
	public static final long[][] PATHS = new long[Bits.PRIME_67][Bits.PRIME_67];
	public static final long PATH_NONE = -1; //A1 field cannot be a part of any officer path
	
	public static final int[] W_MAGIC = Utils.reverseSpecial ( new int[]{
	 -20, -15, -15, -13, -13, -15, -15, -20, 
		-5,   0,  -5,   0,   0,  -5,   0,  -5,
		-6,  -2,   4,   2,   2,   4,  -2,  -6,
		-4,   0,   2,  10,  10,   2,   0,  -4,
		-4,   0,   2,  10,  10,   2,   0,  -4,
		-6,  -2,   4,   2,   2,   4,  -2,  -6,
		-5,   0,  -2,   0,   0,  -2,   0,  -5,
	  -8,  -8,  -6,  -4,  -4,  -6,  -8,  -8,
	});
	
	public static final int[] B_MAGIC = Utils.reverseSpecial ( new int[]{
		  -8,  -8,  -6,  -4,  -4,  -6,  -8,  -8,
			-5,   0,  -5,   0,   0,  -5,   0,  -5,
			-6,  -2,   4,   2,   2,   4,  -2,  -6,
			-4,   0,   2,  10,  10,   2,   0,  -4,
			-4,   0,   2,  10,  10,   2,   0,  -4,
			-6,  -2,   4,   2,   2,   4,  -2,  -6,
			-5,   0,  -5,   0,   0,  -5,   0,  -5,
			-20, -15, -15, -13, -13, -15, -15, -20, 
		});
	
	public static int getMagic(int colour, int fieldID) {
		if (colour == Figures.COLOUR_WHITE) {
			return W_MAGIC[fieldID];
		} else {
			return B_MAGIC[fieldID];
		}
	}
	
	public static int getMagic(int colour, int fromID, int toID) {
		if (colour == Figures.COLOUR_WHITE) {
			return -W_MAGIC[fromID] + W_MAGIC[toID];
		} else {
			return -B_MAGIC[fromID] + B_MAGIC[toID];
		}
	}
	
	static {
		for (int i=0; i<ALL_ORDERED_OFFICER_MOVES.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_OFFICER_MOVES[i];
			long[][] dirs = ALL_ORDERED_OFFICER_DIRS[i];
			ALL_OFFICER_MOVES[idx] = fieldMoves;
			ALL_OFFICER_VALID_DIRS[idx] = ALL_ORDERED_OFFICER_VALID_DIRS[i];
			ALL_OFFICER_DIRS_WITH_BITBOARDS[idx] = dirs;
			ALL_OFFICER_DIRS_WITH_FIELD_IDS[idx] = bitboards2fieldIDs(dirs);
			
			ALL_OFFICER_DIR0_MOVES[idx] = ALL_ORDERED_DIR0_OFFICER_MOVES[i];
			ALL_OFFICER_DIR1_MOVES[idx] = ALL_ORDERED_DIR1_OFFICER_MOVES[i];
			ALL_OFFICER_DIR2_MOVES[idx] = ALL_ORDERED_DIR2_OFFICER_MOVES[i];
			ALL_OFFICER_DIR3_MOVES[idx] = ALL_ORDERED_DIR3_OFFICER_MOVES[i];
			
			/**
			 * Arrays are created and filled for idx filedID. Use them to generate PLY bitboards
			 */
			final int [] validDirIDs = ALL_OFFICER_VALID_DIRS[idx];
			final int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				int dirID = validDirIDs[dir];
				long[] dirBitboards = dirs[dirID];
				
				for (int seq=0; seq<dirBitboards.length; seq++) {
					long toBitboard = dirs[dirID][seq];
					if (seq == 0) {
						ALL_OFFICER_MOVES_1P[idx] |= toBitboard;
					} else if (seq == 1) {
						ALL_OFFICER_MOVES_2P[idx] |= toBitboard;
					} else if (seq == 2 || seq == 3) {
						ALL_OFFICER_MOVES_34P[idx] |= toBitboard;
					} else {
						ALL_OFFICER_MOVES_567P[idx] |= toBitboard;
					}
				}
			}
		}
		
		for (int from=0; from<ALL_ORDERED_OFFICER_MOVES.length; from++) {
			for (int to=0; to<ALL_ORDERED_OFFICER_MOVES.length; to++) {
				int fromID = Fields.IDX_ORDERED_2_A1H1[from];
				int toID = Fields.IDX_ORDERED_2_A1H1[to];
				long fromAttacks = ALL_OFFICER_MOVES[fromID];
				long toBitboard = ALL_A1H1[toID];
				if ((fromAttacks & toBitboard) != NUMBER_0) {
					//PATHS[fromID][toID] = 0;
					int[] fieldIDs;
					long[] fieldBiboards;
					if ((ALL_OFFICER_DIR0_MOVES[fromID] & toBitboard) != NUMBER_0) {
						fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][0];
						fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][0];
					} else if ((ALL_OFFICER_DIR1_MOVES[fromID] & toBitboard) != NUMBER_0) {
						fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][1];
						fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][1];						
					} else if ((ALL_OFFICER_DIR2_MOVES[fromID] & toBitboard) != NUMBER_0) {
						fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][2];
						fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][2];						
					} else if ((ALL_OFFICER_DIR3_MOVES[fromID] & toBitboard) != NUMBER_0) {
						fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][3];
						fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][3];						
					} else {
						throw new IllegalStateException();
					}
					
					for (int i=0;i<fieldIDs.length; i++) {
						if (fieldIDs[i] != toID) {
							PATHS[fromID][toID] |= fieldBiboards[i];
						} else {
							break;
						}
						if (i == fieldIDs.length - 1) {
							throw new IllegalStateException();
						}
					}
					
					if (PATHS[fromID][toID] == PATH_NONE) {
						throw new IllegalStateException();
					}
				} else {
					PATHS[fromID][toID] = PATH_NONE;
				}
			}
		}

		for (int i=0; i<Bits.PRIME_67; i++) {
			ALL_OFFICER_DIR_MOVES[0][i] = ALL_OFFICER_DIR0_MOVES[i]; 
			ALL_OFFICER_DIR_MOVES[1][i] = ALL_OFFICER_DIR1_MOVES[i];
			ALL_OFFICER_DIR_MOVES[2][i] = ALL_OFFICER_DIR2_MOVES[i];
			ALL_OFFICER_DIR_MOVES[3][i] = ALL_OFFICER_DIR3_MOVES[i];
		}
		
		verify();
	}
	
	public static final int mobility(int fieldID, long availableFields) {
		
		long mobility_1p = ALL_OFFICER_MOVES_1P[fieldID] & availableFields;
		if (mobility_1p == 0) {
			return 0;
		}
		
		int max = 16;
		int mobility = 0;
		
		int mobility_1p_count = Utils.countBits(mobility_1p);
		max = (max * mobility_1p_count) / 4;
		mobility += mobility_1p_count;
		
		if (max > 0) {
			long mobility_2p = ALL_OFFICER_MOVES_2P[fieldID] & availableFields;
			int mobility_2p_count = Utils.countBits(mobility_2p);
			max = (max * mobility_2p_count) / 4;
			mobility += mobility_2p_count;
			
			if (max > 0) {
				long mobility_34p = ALL_OFFICER_MOVES_34P[fieldID] & availableFields;
				int mobility_34p_count = Utils.countBits(mobility_34p);
				max = (max * mobility_34p_count) / 8;
				mobility += mobility_34p_count;
				
				if (max > 0) {
					long mobility_567p = ALL_OFFICER_MOVES_567P[fieldID] & availableFields;
					int mobility_567p_count = Utils.countBits(mobility_567p);
					max = (max * mobility_567p_count) / 12;
					mobility += mobility_567p_count;
				}
			}
		}
				
		return mobility;
	}
	
	private static final void verify() {
		for (int i=0; i<Bits.NUMBER_64; i++) {
			int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
			long moves = ALL_OFFICER_MOVES[field_normalized_id];
			String result = "Field[" + i +  ": " + Fields.ALL_ORDERED_NAMES[i]
					+ "]= ";
			
			int j = Bits.nextSetBit_L2R(0, moves);
			for (; j <= 63 && j != -1;
				   j = Bits.nextSetBit_L2R(j + 1, moves)) {
				result += Fields.ALL_ORDERED_NAMES[j] + " ";
			}
			
			//System.out.println(result);
		}
	}
	
    public static void main(String[] args) {	
    	genMembers();
    }
	
	private static void genMembers() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			String prefix1 = "public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
    			String prefix2 = "public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
    			String prefix3 = "public static final long ALL_OFFICER_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
    			String result1 = prefix1;
    			String result2 = prefix2;
    			String result3 = prefix3;
	       		
				//Direction 0 
	    		String prefix = "public static final long ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
    			result = prefix;
	    		
	        	int dir_letter = letter + 1;	 
	        	int dir_digit = digit + 1;
	        	
	        	if (letter == 7 || digit == 7) {
	        		result += "" + "NUMBER_0";
	        	} else {
	        		result2 += "0, ";
	        	}
	        	
	        	result1 += "{";
	        		
	        	if (dir_letter <= 7 && dir_digit <= 7) {
	        		result = prefix;
	        		while (dir_letter <= 7 && dir_digit <= 7) {
	        			result += "" + letters[dir_letter] + digits[dir_digit] + " | ";
	     				result1 += letters[dir_letter] + digits[dir_digit] + ", ";
	        			dir_letter++;
	        			dir_digit++;
	        		}
	        		
	           		if (result.endsWith(" | ")) {
	           			result = result.substring(0, result.length() - 3);
	           		}
	        	}
	        	
	    		if (!result.equals(prefix)) {
	        		result += ";";
	        		System.out.println(result);
	    		}
	    		
	       		if (result1.endsWith(", ")) {
	       			result1 = result1.substring(0, result1.length() - 2);
	       		}
	       		
	       		result1 += "}, ";
	      		
	       		result3 += "ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
	       		
	       		
    			//Direction 1 
        		prefix = "public static final long ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
           		result = prefix;
           		

            	dir_letter = letter + 1;	 
            	dir_digit = digit - 1;
            	
	        	if (letter == 7 || digit == 0) {
	        		result += "" + "NUMBER_0";
	        	} else {
	        		result2 += "1, ";
	        	}
	        	
	        	result1 += "{";
            	
            	if (dir_letter <= 7 && dir_digit >= 0) {
            		result = prefix;
            		while (dir_letter <= 7 && dir_digit >= 0) {
            			result += "" + letters[dir_letter] + digits[dir_digit] + " | ";
            			result1 += letters[dir_letter] + digits[dir_digit] + ", ";
            			dir_letter++;
            			dir_digit--;
            		}
            		
               		if (result.endsWith(" | ")) {
               			result = result.substring(0, result.length() - 3);
               		}
            	}
            	
        		if (!result.equals(prefix)) {
            		result += ";";
            		
            		System.out.println(result);
        		}
        		
	       		if (result1.endsWith(", ")) {
	       			result1 = result1.substring(0, result1.length() - 2);
	       		}
	       		
        		result1 += "}, ";
        		
        		result3 += "ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
        		
        		
    			//Direction 2 
        		prefix = "public static final long ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
           		result = prefix;
           		

            	dir_letter = letter - 1;	 
            	dir_digit = digit - 1;
            	
	        	if (letter == 0 || digit == 0) {
	        		result += "" + "NUMBER_0";
	        	} else {
	        		result2 += "2, ";
	        	}
	        	
	        	result1 += "{";
            	
            	if (dir_letter >= 0 && dir_digit >= 0) {
            		result = prefix;
            		while (dir_letter >= 0 && dir_digit >= 0) {
            			result += "" + letters[dir_letter] + digits[dir_digit] + " | ";
            			result1 += letters[dir_letter] + digits[dir_digit] + ", ";
            			dir_letter--;
            			dir_digit--;
            		}
            		
               		if (result.endsWith(" | ")) {
               			result = result.substring(0, result.length() - 3);
               		}
            	}
            	
        		if (!result.equals(prefix)) {
            		result += ";";
            		
            		System.out.println(result);
        		}
        		
	       		if (result1.endsWith(", ")) {
	       			result1 = result1.substring(0, result1.length() - 2);
	       		}
	       		
        		result1 += "}, ";
        		
        		result3 += "ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
        		
        		
				//Direction 3 
	    		prefix = "public static final long ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
    			result = prefix;
	    		
	        	dir_letter = letter - 1;	 
	        	dir_digit = digit + 1;
	        	
	        	if (letter == 0 || digit == 7) {
	        		result += "" + "NUMBER_0";
	        	} else {
	        		result2 += "3, ";
	        	}
	        	
	        	result1 += "{";
	        		
	        	if (dir_letter >= 0 && dir_digit <= 7) {
	        		result = prefix;
	        		while (dir_letter >= 0 && dir_digit <= 7) {
	        			result += "" + letters[dir_letter] + digits[dir_digit] + " | ";
	     				result1 += letters[dir_letter] + digits[dir_digit] + ", ";
	        			dir_letter--;
	        			dir_digit++;
	        		}
	        		
	           		if (result.endsWith(" | ")) {
	           			result = result.substring(0, result.length() - 3);
	           		}
	        	}
	        	
	    		if (!result.equals(prefix)) {
	        		result += ";";
	        		System.out.println(result);
	    		}
	    		
	       		if (result1.endsWith(", ")) {
	       			result1 = result1.substring(0, result1.length() - 2);
	       		}
	       		
	       		result1 += "}, ";
	
	       		result3 += "ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ";";
	       		
	       		
          		if (result1.endsWith(", ")) {
       				result1 = result1.substring(0, result1.length() - 2);
       			}
          		
          		if (result2.endsWith(", ")) {
       				result2 = result2.substring(0, result2.length() - 2);
       			}
       		
          		result1 += "};";
          		result2 += "};";
        		
          		System.out.println(result1);
          		System.out.println(result2);
          		System.out.println(result3);
	       	}
    	}


    	System.out.println("\r\n");
    	
    	//Gen Arrays
    	result = "public static final long[] ALL_ORDERED_OFFICER_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
           		result += "ALL_OFFICER_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_DIR0_OFFICER_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
           		result += "ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_DIR1_OFFICER_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
           		result += "ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_DIR2_OFFICER_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
           		result += "ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_DIR3_OFFICER_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
           		result += "ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[][][] ALL_ORDERED_OFFICER_DIRS = new long[][][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
    			result += "ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result); 
    	
    	result = "public static final int[][] ALL_ORDERED_OFFICER_VALID_DIRS = new int[][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {
    			result += "ALL_OFFICER_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
	}
}
