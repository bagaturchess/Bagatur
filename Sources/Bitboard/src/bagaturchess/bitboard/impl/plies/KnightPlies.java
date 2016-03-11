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

import java.util.Arrays;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;



public class KnightPlies extends Fields {
	
	//---------------------------- START GENERATED BLOCK ----------------------------------------------
	
	public static final long ALL_KNIGHT_MOVES_FROM_A1 = C2 | B3;
	public static final long ALL_KNIGHT_MOVES_FROM_B1 = D2 | C3 | A3;
	public static final long ALL_KNIGHT_MOVES_FROM_C1 = E2 | A2 | D3 | B3;
	public static final long ALL_KNIGHT_MOVES_FROM_D1 = F2 | B2 | E3 | C3;
	public static final long ALL_KNIGHT_MOVES_FROM_E1 = G2 | C2 | F3 | D3;
	public static final long ALL_KNIGHT_MOVES_FROM_F1 = H2 | D2 | G3 | E3;
	public static final long ALL_KNIGHT_MOVES_FROM_G1 = E2 | H3 | F3;
	public static final long ALL_KNIGHT_MOVES_FROM_H1 = F2 | G3;
	public static final long ALL_KNIGHT_MOVES_FROM_A2 = C3 | C1 | B4;
	public static final long ALL_KNIGHT_MOVES_FROM_B2 = D3 | D1 | C4 | A4;
	public static final long ALL_KNIGHT_MOVES_FROM_C2 = E3 | E1 | A3 | A1 | D4 | B4;
	public static final long ALL_KNIGHT_MOVES_FROM_D2 = F3 | F1 | B3 | B1 | E4 | C4;
	public static final long ALL_KNIGHT_MOVES_FROM_E2 = G3 | G1 | C3 | C1 | F4 | D4;
	public static final long ALL_KNIGHT_MOVES_FROM_F2 = H3 | H1 | D3 | D1 | G4 | E4;
	public static final long ALL_KNIGHT_MOVES_FROM_G2 = E3 | E1 | H4 | F4;
	public static final long ALL_KNIGHT_MOVES_FROM_H2 = F3 | F1 | G4;
	public static final long ALL_KNIGHT_MOVES_FROM_A3 = C4 | C2 | B5 | B1;
	public static final long ALL_KNIGHT_MOVES_FROM_B3 = D4 | D2 | C5 | C1 | A5 | A1;
	public static final long ALL_KNIGHT_MOVES_FROM_C3 = E4 | E2 | A4 | A2 | D5 | D1 | B5 | B1;
	public static final long ALL_KNIGHT_MOVES_FROM_D3 = F4 | F2 | B4 | B2 | E5 | E1 | C5 | C1;
	public static final long ALL_KNIGHT_MOVES_FROM_E3 = G4 | G2 | C4 | C2 | F5 | F1 | D5 | D1;
	public static final long ALL_KNIGHT_MOVES_FROM_F3 = H4 | H2 | D4 | D2 | G5 | G1 | E5 | E1;
	public static final long ALL_KNIGHT_MOVES_FROM_G3 = E4 | E2 | H5 | H1 | F5 | F1;
	public static final long ALL_KNIGHT_MOVES_FROM_H3 = F4 | F2 | G5 | G1;
	public static final long ALL_KNIGHT_MOVES_FROM_A4 = C5 | C3 | B6 | B2;
	public static final long ALL_KNIGHT_MOVES_FROM_B4 = D5 | D3 | C6 | C2 | A6 | A2;
	public static final long ALL_KNIGHT_MOVES_FROM_C4 = E5 | E3 | A5 | A3 | D6 | D2 | B6 | B2;
	public static final long ALL_KNIGHT_MOVES_FROM_D4 = F5 | F3 | B5 | B3 | E6 | E2 | C6 | C2;
	public static final long ALL_KNIGHT_MOVES_FROM_E4 = G5 | G3 | C5 | C3 | F6 | F2 | D6 | D2;
	public static final long ALL_KNIGHT_MOVES_FROM_F4 = H5 | H3 | D5 | D3 | G6 | G2 | E6 | E2;
	public static final long ALL_KNIGHT_MOVES_FROM_G4 = E5 | E3 | H6 | H2 | F6 | F2;
	public static final long ALL_KNIGHT_MOVES_FROM_H4 = F5 | F3 | G6 | G2;
	public static final long ALL_KNIGHT_MOVES_FROM_A5 = C6 | C4 | B7 | B3;
	public static final long ALL_KNIGHT_MOVES_FROM_B5 = D6 | D4 | C7 | C3 | A7 | A3;
	public static final long ALL_KNIGHT_MOVES_FROM_C5 = E6 | E4 | A6 | A4 | D7 | D3 | B7 | B3;
	public static final long ALL_KNIGHT_MOVES_FROM_D5 = F6 | F4 | B6 | B4 | E7 | E3 | C7 | C3;
	public static final long ALL_KNIGHT_MOVES_FROM_E5 = G6 | G4 | C6 | C4 | F7 | F3 | D7 | D3;
	public static final long ALL_KNIGHT_MOVES_FROM_F5 = H6 | H4 | D6 | D4 | G7 | G3 | E7 | E3;
	public static final long ALL_KNIGHT_MOVES_FROM_G5 = E6 | E4 | H7 | H3 | F7 | F3;
	public static final long ALL_KNIGHT_MOVES_FROM_H5 = F6 | F4 | G7 | G3;
	public static final long ALL_KNIGHT_MOVES_FROM_A6 = C7 | C5 | B8 | B4;
	public static final long ALL_KNIGHT_MOVES_FROM_B6 = D7 | D5 | C8 | C4 | A8 | A4;
	public static final long ALL_KNIGHT_MOVES_FROM_C6 = E7 | E5 | A7 | A5 | D8 | D4 | B8 | B4;
	public static final long ALL_KNIGHT_MOVES_FROM_D6 = F7 | F5 | B7 | B5 | E8 | E4 | C8 | C4;
	public static final long ALL_KNIGHT_MOVES_FROM_E6 = G7 | G5 | C7 | C5 | F8 | F4 | D8 | D4;
	public static final long ALL_KNIGHT_MOVES_FROM_F6 = H7 | H5 | D7 | D5 | G8 | G4 | E8 | E4;
	public static final long ALL_KNIGHT_MOVES_FROM_G6 = E7 | E5 | H8 | H4 | F8 | F4;
	public static final long ALL_KNIGHT_MOVES_FROM_H6 = F7 | F5 | G8 | G4;
	public static final long ALL_KNIGHT_MOVES_FROM_A7 = C8 | C6 | B5;
	public static final long ALL_KNIGHT_MOVES_FROM_B7 = D8 | D6 | C5 | A5;
	public static final long ALL_KNIGHT_MOVES_FROM_C7 = E8 | E6 | A8 | A6 | D5 | B5;
	public static final long ALL_KNIGHT_MOVES_FROM_D7 = F8 | F6 | B8 | B6 | E5 | C5;
	public static final long ALL_KNIGHT_MOVES_FROM_E7 = G8 | G6 | C8 | C6 | F5 | D5;
	public static final long ALL_KNIGHT_MOVES_FROM_F7 = H8 | H6 | D8 | D6 | G5 | E5;
	public static final long ALL_KNIGHT_MOVES_FROM_G7 = E8 | E6 | H5 | F5;
	public static final long ALL_KNIGHT_MOVES_FROM_H7 = F8 | F6 | G5;
	public static final long ALL_KNIGHT_MOVES_FROM_A8 = C7 | B6;
	public static final long ALL_KNIGHT_MOVES_FROM_B8 = D7 | C6 | A6;
	public static final long ALL_KNIGHT_MOVES_FROM_C8 = E7 | A7 | D6 | B6;
	public static final long ALL_KNIGHT_MOVES_FROM_D8 = F7 | B7 | E6 | C6;
	public static final long ALL_KNIGHT_MOVES_FROM_E8 = G7 | C7 | F6 | D6;
	public static final long ALL_KNIGHT_MOVES_FROM_F8 = H7 | D7 | G6 | E6;
	public static final long ALL_KNIGHT_MOVES_FROM_G8 = E7 | H6 | F6;
	public static final long ALL_KNIGHT_MOVES_FROM_H8 = F7 | G6;
	
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][] {{B3}, {C2}, {  }, {  }, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1 = new int[] {0, 1};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][] {{C3}, {D2}, {  }, {  }, {  }, {  }, {  }, {A3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1 = new int[] {0, 1, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][] {{D3}, {E2}, {  }, {  }, {  }, {  }, {A2}, {B3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1 = new int[] {0, 1, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][] {{E3}, {F2}, {  }, {  }, {  }, {  }, {B2}, {C3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1 = new int[] {0, 1, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][] {{F3}, {G2}, {  }, {  }, {  }, {  }, {C2}, {D3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1 = new int[] {0, 1, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][] {{G3}, {H2}, {  }, {  }, {  }, {  }, {D2}, {E3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1 = new int[] {0, 1, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][] {{H3}, {  }, {  }, {  }, {  }, {  }, {E2}, {F3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1 = new int[] {0, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][] {{  }, {  }, {  }, {  }, {  }, {  }, {F2}, {G3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1 = new int[] {6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][] {{B4}, {C3}, {C1}, {  }, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2 = new int[] {0, 1, 2};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][] {{C4}, {D3}, {D1}, {  }, {  }, {  }, {  }, {A4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2 = new int[] {0, 1, 2, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][] {{D4}, {E3}, {E1}, {  }, {  }, {A1}, {A3}, {B4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2 = new int[] {0, 1, 2, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][] {{E4}, {F3}, {F1}, {  }, {  }, {B1}, {B3}, {C4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2 = new int[] {0, 1, 2, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][] {{F4}, {G3}, {G1}, {  }, {  }, {C1}, {C3}, {D4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2 = new int[] {0, 1, 2, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][] {{G4}, {H3}, {H1}, {  }, {  }, {D1}, {D3}, {E4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2 = new int[] {0, 1, 2, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][] {{H4}, {  }, {  }, {  }, {  }, {E1}, {E3}, {F4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2 = new int[] {0, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][] {{  }, {  }, {  }, {  }, {  }, {F1}, {F3}, {G4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2 = new int[] {5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][] {{B5}, {C4}, {C2}, {B1}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3 = new int[] {0, 1, 2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][] {{C5}, {D4}, {D2}, {C1}, {A1}, {  }, {  }, {A5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3 = new int[] {0, 1, 2, 3, 4, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][] {{D5}, {E4}, {E2}, {D1}, {B1}, {A2}, {A4}, {B5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][] {{E5}, {F4}, {F2}, {E1}, {C1}, {B2}, {B4}, {C5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][] {{F5}, {G4}, {G2}, {F1}, {D1}, {C2}, {C4}, {D5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][] {{G5}, {H4}, {H2}, {G1}, {E1}, {D2}, {D4}, {E5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][] {{H5}, {  }, {  }, {H1}, {F1}, {E2}, {E4}, {F5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3 = new int[] {0, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][] {{  }, {  }, {  }, {  }, {G1}, {F2}, {F4}, {G5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3 = new int[] {4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][] {{B6}, {C5}, {C3}, {B2}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4 = new int[] {0, 1, 2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][] {{C6}, {D5}, {D3}, {C2}, {A2}, {  }, {  }, {A6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4 = new int[] {0, 1, 2, 3, 4, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][] {{D6}, {E5}, {E3}, {D2}, {B2}, {A3}, {A5}, {B6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][] {{E6}, {F5}, {F3}, {E2}, {C2}, {B3}, {B5}, {C6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][] {{F6}, {G5}, {G3}, {F2}, {D2}, {C3}, {C5}, {D6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][] {{G6}, {H5}, {H3}, {G2}, {E2}, {D3}, {D5}, {E6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][] {{H6}, {  }, {  }, {H2}, {F2}, {E3}, {E5}, {F6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4 = new int[] {0, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][] {{  }, {  }, {  }, {  }, {G2}, {F3}, {F5}, {G6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4 = new int[] {4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][] {{B7}, {C6}, {C4}, {B3}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5 = new int[] {0, 1, 2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][] {{C7}, {D6}, {D4}, {C3}, {A3}, {  }, {  }, {A7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5 = new int[] {0, 1, 2, 3, 4, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][] {{D7}, {E6}, {E4}, {D3}, {B3}, {A4}, {A6}, {B7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][] {{E7}, {F6}, {F4}, {E3}, {C3}, {B4}, {B6}, {C7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][] {{F7}, {G6}, {G4}, {F3}, {D3}, {C4}, {C6}, {D7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][] {{G7}, {H6}, {H4}, {G3}, {E3}, {D4}, {D6}, {E7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][] {{H7}, {  }, {  }, {H3}, {F3}, {E4}, {E6}, {F7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5 = new int[] {0, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][] {{  }, {  }, {  }, {  }, {G3}, {F4}, {F6}, {G7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5 = new int[] {4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][] {{B8}, {C7}, {C5}, {B4}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6 = new int[] {0, 1, 2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][] {{C8}, {D7}, {D5}, {C4}, {A4}, {  }, {  }, {A8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6 = new int[] {0, 1, 2, 3, 4, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][] {{D8}, {E7}, {E5}, {D4}, {B4}, {A5}, {A7}, {B8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][] {{E8}, {F7}, {F5}, {E4}, {C4}, {B5}, {B7}, {C8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][] {{F8}, {G7}, {G5}, {F4}, {D4}, {C5}, {C7}, {D8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][] {{G8}, {H7}, {H5}, {G4}, {E4}, {D5}, {D7}, {E8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][] {{H8}, {  }, {  }, {H4}, {F4}, {E5}, {E7}, {F8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6 = new int[] {0, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][] {{  }, {  }, {  }, {  }, {G4}, {F5}, {F7}, {G8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6 = new int[] {4, 5, 6, 7};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][] {{  }, {C8}, {C6}, {B5}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7 = new int[] {1, 2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][] {{  }, {D8}, {D6}, {C5}, {A5}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7 = new int[] {1, 2, 3, 4};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][] {{  }, {E8}, {E6}, {D5}, {B5}, {A6}, {A8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7 = new int[] {1, 2, 3, 4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][] {{  }, {F8}, {F6}, {E5}, {C5}, {B6}, {B8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7 = new int[] {1, 2, 3, 4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][] {{  }, {G8}, {G6}, {F5}, {D5}, {C6}, {C8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7 = new int[] {1, 2, 3, 4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][] {{  }, {H8}, {H6}, {G5}, {E5}, {D6}, {D8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7 = new int[] {1, 2, 3, 4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][] {{  }, {  }, {  }, {H5}, {F5}, {E6}, {E8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7 = new int[] {3, 4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][] {{  }, {  }, {  }, {  }, {G5}, {F6}, {F8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7 = new int[] {4, 5, 6};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][] {{  }, {  }, {C7}, {B6}, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8 = new int[] {2, 3};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][] {{  }, {  }, {D7}, {C6}, {A6}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8 = new int[] {2, 3, 4};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][] {{  }, {  }, {E7}, {D6}, {B6}, {A7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8 = new int[] {2, 3, 4, 5};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][] {{  }, {  }, {F7}, {E6}, {C6}, {B7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8 = new int[] {2, 3, 4, 5};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][] {{  }, {  }, {G7}, {F6}, {D6}, {C7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8 = new int[] {2, 3, 4, 5};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][] {{  }, {  }, {H7}, {G6}, {E6}, {D7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8 = new int[] {2, 3, 4, 5};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][] {{  }, {  }, {  }, {H6}, {F6}, {E7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8 = new int[] {3, 4, 5};
	public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][] {{  }, {  }, {  }, {  }, {G6}, {F7}, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8 = new int[] {4, 5};
	
	public static final long[] ALL_ORDERED_KNIGHT_MOVES = new long[] {ALL_KNIGHT_MOVES_FROM_A1, ALL_KNIGHT_MOVES_FROM_B1, ALL_KNIGHT_MOVES_FROM_C1, ALL_KNIGHT_MOVES_FROM_D1, ALL_KNIGHT_MOVES_FROM_E1, ALL_KNIGHT_MOVES_FROM_F1, ALL_KNIGHT_MOVES_FROM_G1, ALL_KNIGHT_MOVES_FROM_H1, ALL_KNIGHT_MOVES_FROM_A2, ALL_KNIGHT_MOVES_FROM_B2, ALL_KNIGHT_MOVES_FROM_C2, ALL_KNIGHT_MOVES_FROM_D2, ALL_KNIGHT_MOVES_FROM_E2, ALL_KNIGHT_MOVES_FROM_F2, ALL_KNIGHT_MOVES_FROM_G2, ALL_KNIGHT_MOVES_FROM_H2, ALL_KNIGHT_MOVES_FROM_A3, ALL_KNIGHT_MOVES_FROM_B3, ALL_KNIGHT_MOVES_FROM_C3, ALL_KNIGHT_MOVES_FROM_D3, ALL_KNIGHT_MOVES_FROM_E3, ALL_KNIGHT_MOVES_FROM_F3, ALL_KNIGHT_MOVES_FROM_G3, ALL_KNIGHT_MOVES_FROM_H3, ALL_KNIGHT_MOVES_FROM_A4, ALL_KNIGHT_MOVES_FROM_B4, ALL_KNIGHT_MOVES_FROM_C4, ALL_KNIGHT_MOVES_FROM_D4, ALL_KNIGHT_MOVES_FROM_E4, ALL_KNIGHT_MOVES_FROM_F4, ALL_KNIGHT_MOVES_FROM_G4, ALL_KNIGHT_MOVES_FROM_H4, ALL_KNIGHT_MOVES_FROM_A5, ALL_KNIGHT_MOVES_FROM_B5, ALL_KNIGHT_MOVES_FROM_C5, ALL_KNIGHT_MOVES_FROM_D5, ALL_KNIGHT_MOVES_FROM_E5, ALL_KNIGHT_MOVES_FROM_F5, ALL_KNIGHT_MOVES_FROM_G5, ALL_KNIGHT_MOVES_FROM_H5, ALL_KNIGHT_MOVES_FROM_A6, ALL_KNIGHT_MOVES_FROM_B6, ALL_KNIGHT_MOVES_FROM_C6, ALL_KNIGHT_MOVES_FROM_D6, ALL_KNIGHT_MOVES_FROM_E6, ALL_KNIGHT_MOVES_FROM_F6, ALL_KNIGHT_MOVES_FROM_G6, ALL_KNIGHT_MOVES_FROM_H6, ALL_KNIGHT_MOVES_FROM_A7, ALL_KNIGHT_MOVES_FROM_B7, ALL_KNIGHT_MOVES_FROM_C7, ALL_KNIGHT_MOVES_FROM_D7, ALL_KNIGHT_MOVES_FROM_E7, ALL_KNIGHT_MOVES_FROM_F7, ALL_KNIGHT_MOVES_FROM_G7, ALL_KNIGHT_MOVES_FROM_H7, ALL_KNIGHT_MOVES_FROM_A8, ALL_KNIGHT_MOVES_FROM_B8, ALL_KNIGHT_MOVES_FROM_C8, ALL_KNIGHT_MOVES_FROM_D8, ALL_KNIGHT_MOVES_FROM_E8, ALL_KNIGHT_MOVES_FROM_F8, ALL_KNIGHT_MOVES_FROM_G8, ALL_KNIGHT_MOVES_FROM_H8, };
	public static final long[][][] ALL_ORDERED_KNIGHT_DIRS = new long[][][] {ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H8, };
	public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][] {ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8, };

	//---------------------------- END GENERATED BLOCK ----------------------------------------------
	
	public static final long[] ALL_KNIGHT_MOVES = new long[Bits.PRIME_67];
	
	public static final int[][] ALL_KNIGHT_VALID_DIRS = new int[Bits.PRIME_67][];
	public static final int[][][] ALL_KNIGHT_DIRS_WITH_FIELD_IDS = new int[Bits.PRIME_67][][];
	public static final long[][][] ALL_KNIGHT_DIRS_WITH_BITBOARDS = new long[Bits.PRIME_67][][];
	
	public static final int[] W_MAGIC = Utils.reverseSpecial ( new int[]{
		0, 10, 20, 20, 20, 10, 10, 0 ,
		10, 24, 26, 26, 26, 24, 24, 10 ,
		10, 28, 40, 50, 50, 28, 28, 10,
		10, 23, 36, 40, 40, 23, 23, 10 ,
		10, 22, 28, 30, 30, 22, 22, 10 ,
		 0, 26, 26, 30, 30, 26, 26, 10 ,
		 5, 20, 20, 23, 20, 20, 20, 5 ,
		 0,  5, 15, 15, 15, 15,  0, 0,
	});
	
	public static final int[] B_MAGIC = Utils.reverseSpecial ( new int[]{
		0,  5, 15, 15, 15, 15,  0, 0 ,
		5, 20, 20, 23, 20, 20, 20, 5 ,
		0, 26, 26, 30, 30, 26, 26, 10 ,
		0, 22, 28, 30, 30, 22, 22, 10 ,
		0, 23, 36, 40, 40, 23, 23, 10 ,
		0, 28, 40, 50, 50, 28, 28, 10 ,
		0, 24, 26, 26, 26, 24, 24, 10 ,
		0, 10, 20, 20, 20, 10, 10, 0 ,
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
		for (int i=0; i<ALL_ORDERED_KNIGHT_MOVES.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_KNIGHT_MOVES[i];
			long[][] dirs = ALL_ORDERED_KNIGHT_DIRS[i];
			ALL_KNIGHT_MOVES[idx] = fieldMoves;
			ALL_KNIGHT_VALID_DIRS[idx] = ALL_ORDERED_KNIGHT_VALID_DIRS[i];
			ALL_KNIGHT_DIRS_WITH_BITBOARDS[idx] = dirs;
			ALL_KNIGHT_DIRS_WITH_FIELD_IDS[idx] = bitboards2fieldIDs(dirs);
		}
		
		verify();
	}
	
	private static final void verify() {
		for (int i=0; i<Bits.NUMBER_64; i++) {
			int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
			long moves = ALL_KNIGHT_MOVES[field_normalized_id];
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

    			String prefix = "public static final long ALL_KNIGHT_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
       		result = prefix;
   			
       		if (letter + 2 <= 7) {
       			if (digit + 1 <= 7) {
       				result += "" + letters[letter + 2] + digits[digit + 1] + " | ";
       			}
       			if (digit - 1 >= 0) {
       				result += "" + letters[letter + 2] + digits[digit - 1] + " | ";
       			}
       		}
       		
       		if (letter - 2 >= 0) {
       			if (digit + 1 <= 7) {
       				result += "" + letters[letter - 2] + digits[digit + 1] + " | ";
       			}
       			if (digit - 1 >= 0) {
       				result += "" + letters[letter - 2] + digits[digit - 1] + " | ";
       			}
       		}

       		if (letter + 1 <= 7) {
       			if (digit + 2 <= 7) {
       				result += "" + letters[letter + 1] + digits[digit + 2] + " | ";
       			}
       			if (digit - 2 >= 0) {
       				result += "" + letters[letter + 1] + digits[digit - 2] + " | ";
       			}
       		}
       		
       		if (letter - 1 >= 0) {
       			if (digit + 2 <= 7) {
       				result += "" + letters[letter - 1] + digits[digit + 2] + " | ";
       			}
       			if (digit - 2 >= 0) {
       				result += "" + letters[letter - 1] + digits[digit - 2] + " | ";
       			}
       		}
       		
       		if (result.endsWith(" | ")) {
       			result = result.substring(0, result.length() - 3);
       		}
       		
       		result += ";";
       		
      		System.out.println(result);            			
    		}
    	}
    	
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			String prefix = "public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
    			String prefix1 = "public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
       		result = prefix;
       		String result1 = prefix1;
        	
       		if (letter + 1 <= 7 && digit + 2 <= 7) {
       			result += "{" + letters[letter + 1] + digits[digit + 2] + "}, ";
       			result1 += "0, ";
       		} else {
         		result += "{  }, ";
         	}
       		
     			if (letter + 2 <= 7 && digit + 1 <= 7) {
     				result += "{" + letters[letter + 2] + digits[digit + 1] + "}, ";
     				result1 += "1, ";
     			} else {
       			result += "{  }, ";
       		}
     			
     			if (letter + 2 <= 7 && digit - 1 >= 0) {
     				result += "{" + letters[letter + 2] + digits[digit - 1] + "}, ";
     				result1 += "2, ";
     			} else {
       			result += "{  }, ";
       		}
       		
       		if (letter + 1 <= 7 && digit - 2 >= 0) {
       			result += "{" + letters[letter + 1] + digits[digit - 2] + "}, ";
       			result1 += "3, ";
       		} else {
         		result += "{  }, ";
         	}
       		
       		if (letter - 1 >= 0 && digit - 2 >= 0) {
     				result += "{" + letters[letter - 1] + digits[digit - 2] + "}, ";
     				result1 += "4, ";
     			} else {
       			result += "{  }, ";
       		}
       		
     			if (letter - 2 >= 0 && digit - 1 >= 0) {
     				result += "{" + letters[letter - 2] + digits[digit - 1] + "}, ";
     				result1 += "5, ";
     			} else {
       			result += "{  }, ";
       		}
     			
     			if (letter - 2 >= 0 && digit + 1 <= 7) {
     				result += "{" + letters[letter - 2] + digits[digit + 1] + "}, ";
     				result1 += "6, ";
     			} else {
       			result += "{  }, ";
       		}
     			
       		if (letter - 1 >= 0 && digit + 2 <= 7) {
       			result += "{" + letters[letter - 1] + digits[digit + 2] + "}, ";
       			result1 += "7, ";
       		} else {
         		result += "{  }, ";
         	}
       		
       		if (result.endsWith(", ")) {
       			result = result.substring(0, result.length() - 2);
       		}
       		
       		if (result1.endsWith(", ")) {
       			result1 = result1.substring(0, result1.length() - 2);
       		}
       		
       		result += "};";
       		result1 += "};";
       		
      		System.out.println(result);
      		System.out.println(result1);
    		}
    	}
    	
    	result = "public static final long[] ALL_ORDERED_KNIGHT_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
           		result += "ALL_KNIGHT_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[][][] ALL_ORDERED_KNIGHT_DIRS = new long[][][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result); 
    	
    	result = "public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result); 
	}
}
