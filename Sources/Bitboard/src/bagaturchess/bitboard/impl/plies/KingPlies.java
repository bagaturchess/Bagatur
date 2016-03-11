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

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;


public class KingPlies extends Fields {
	
	//---------------------------- START GENERATED BLOCK ----------------------------------------------
	
	public static final long ALL_KING_MOVES_FROM_A1 = A2 | B1 | B2;
	public static final long ALL_KING_MOVES_FROM_B1 = B2 | C1 | C2 | A1 | A2;
	public static final long ALL_KING_MOVES_FROM_C1 = C2 | D1 | D2 | B1 | B2;
	public static final long ALL_KING_MOVES_FROM_D1 = D2 | E1 | E2 | C1 | C2;
	public static final long ALL_KING_MOVES_FROM_E1 = E2 | F1 | F2 | D1 | D2;
	public static final long ALL_KING_MOVES_FROM_F1 = F2 | G1 | G2 | E1 | E2;
	public static final long ALL_KING_MOVES_FROM_G1 = G2 | H1 | H2 | F1 | F2;
	public static final long ALL_KING_MOVES_FROM_H1 = H2 | G1 | G2;
	public static final long ALL_KING_MOVES_FROM_A2 = A3 | A1 | B2 | B3 | B1;
	public static final long ALL_KING_MOVES_FROM_B2 = B3 | B1 | C2 | C3 | C1 | A2 | A3 | A1;
	public static final long ALL_KING_MOVES_FROM_C2 = C3 | C1 | D2 | D3 | D1 | B2 | B3 | B1;
	public static final long ALL_KING_MOVES_FROM_D2 = D3 | D1 | E2 | E3 | E1 | C2 | C3 | C1;
	public static final long ALL_KING_MOVES_FROM_E2 = E3 | E1 | F2 | F3 | F1 | D2 | D3 | D1;
	public static final long ALL_KING_MOVES_FROM_F2 = F3 | F1 | G2 | G3 | G1 | E2 | E3 | E1;
	public static final long ALL_KING_MOVES_FROM_G2 = G3 | G1 | H2 | H3 | H1 | F2 | F3 | F1;
	public static final long ALL_KING_MOVES_FROM_H2 = H3 | H1 | G2 | G3 | G1;
	public static final long ALL_KING_MOVES_FROM_A3 = A4 | A2 | B3 | B4 | B2;
	public static final long ALL_KING_MOVES_FROM_B3 = B4 | B2 | C3 | C4 | C2 | A3 | A4 | A2;
	public static final long ALL_KING_MOVES_FROM_C3 = C4 | C2 | D3 | D4 | D2 | B3 | B4 | B2;
	public static final long ALL_KING_MOVES_FROM_D3 = D4 | D2 | E3 | E4 | E2 | C3 | C4 | C2;
	public static final long ALL_KING_MOVES_FROM_E3 = E4 | E2 | F3 | F4 | F2 | D3 | D4 | D2;
	public static final long ALL_KING_MOVES_FROM_F3 = F4 | F2 | G3 | G4 | G2 | E3 | E4 | E2;
	public static final long ALL_KING_MOVES_FROM_G3 = G4 | G2 | H3 | H4 | H2 | F3 | F4 | F2;
	public static final long ALL_KING_MOVES_FROM_H3 = H4 | H2 | G3 | G4 | G2;
	public static final long ALL_KING_MOVES_FROM_A4 = A5 | A3 | B4 | B5 | B3;
	public static final long ALL_KING_MOVES_FROM_B4 = B5 | B3 | C4 | C5 | C3 | A4 | A5 | A3;
	public static final long ALL_KING_MOVES_FROM_C4 = C5 | C3 | D4 | D5 | D3 | B4 | B5 | B3;
	public static final long ALL_KING_MOVES_FROM_D4 = D5 | D3 | E4 | E5 | E3 | C4 | C5 | C3;
	public static final long ALL_KING_MOVES_FROM_E4 = E5 | E3 | F4 | F5 | F3 | D4 | D5 | D3;
	public static final long ALL_KING_MOVES_FROM_F4 = F5 | F3 | G4 | G5 | G3 | E4 | E5 | E3;
	public static final long ALL_KING_MOVES_FROM_G4 = G5 | G3 | H4 | H5 | H3 | F4 | F5 | F3;
	public static final long ALL_KING_MOVES_FROM_H4 = H5 | H3 | G4 | G5 | G3;
	public static final long ALL_KING_MOVES_FROM_A5 = A6 | A4 | B5 | B6 | B4;
	public static final long ALL_KING_MOVES_FROM_B5 = B6 | B4 | C5 | C6 | C4 | A5 | A6 | A4;
	public static final long ALL_KING_MOVES_FROM_C5 = C6 | C4 | D5 | D6 | D4 | B5 | B6 | B4;
	public static final long ALL_KING_MOVES_FROM_D5 = D6 | D4 | E5 | E6 | E4 | C5 | C6 | C4;
	public static final long ALL_KING_MOVES_FROM_E5 = E6 | E4 | F5 | F6 | F4 | D5 | D6 | D4;
	public static final long ALL_KING_MOVES_FROM_F5 = F6 | F4 | G5 | G6 | G4 | E5 | E6 | E4;
	public static final long ALL_KING_MOVES_FROM_G5 = G6 | G4 | H5 | H6 | H4 | F5 | F6 | F4;
	public static final long ALL_KING_MOVES_FROM_H5 = H6 | H4 | G5 | G6 | G4;
	public static final long ALL_KING_MOVES_FROM_A6 = A7 | A5 | B6 | B7 | B5;
	public static final long ALL_KING_MOVES_FROM_B6 = B7 | B5 | C6 | C7 | C5 | A6 | A7 | A5;
	public static final long ALL_KING_MOVES_FROM_C6 = C7 | C5 | D6 | D7 | D5 | B6 | B7 | B5;
	public static final long ALL_KING_MOVES_FROM_D6 = D7 | D5 | E6 | E7 | E5 | C6 | C7 | C5;
	public static final long ALL_KING_MOVES_FROM_E6 = E7 | E5 | F6 | F7 | F5 | D6 | D7 | D5;
	public static final long ALL_KING_MOVES_FROM_F6 = F7 | F5 | G6 | G7 | G5 | E6 | E7 | E5;
	public static final long ALL_KING_MOVES_FROM_G6 = G7 | G5 | H6 | H7 | H5 | F6 | F7 | F5;
	public static final long ALL_KING_MOVES_FROM_H6 = H7 | H5 | G6 | G7 | G5;
	public static final long ALL_KING_MOVES_FROM_A7 = A8 | A6 | B7 | B8 | B6;
	public static final long ALL_KING_MOVES_FROM_B7 = B8 | B6 | C7 | C8 | C6 | A7 | A8 | A6;
	public static final long ALL_KING_MOVES_FROM_C7 = C8 | C6 | D7 | D8 | D6 | B7 | B8 | B6;
	public static final long ALL_KING_MOVES_FROM_D7 = D8 | D6 | E7 | E8 | E6 | C7 | C8 | C6;
	public static final long ALL_KING_MOVES_FROM_E7 = E8 | E6 | F7 | F8 | F6 | D7 | D8 | D6;
	public static final long ALL_KING_MOVES_FROM_F7 = F8 | F6 | G7 | G8 | G6 | E7 | E8 | E6;
	public static final long ALL_KING_MOVES_FROM_G7 = G8 | G6 | H7 | H8 | H6 | F7 | F8 | F6;
	public static final long ALL_KING_MOVES_FROM_H7 = H8 | H6 | G7 | G8 | G6;
	public static final long ALL_KING_MOVES_FROM_A8 = A7 | B8 | B7;
	public static final long ALL_KING_MOVES_FROM_B8 = B7 | C8 | C7 | A8 | A7;
	public static final long ALL_KING_MOVES_FROM_C8 = C7 | D8 | D7 | B8 | B7;
	public static final long ALL_KING_MOVES_FROM_D8 = D7 | E8 | E7 | C8 | C7;
	public static final long ALL_KING_MOVES_FROM_E8 = E7 | F8 | F7 | D8 | D7;
	public static final long ALL_KING_MOVES_FROM_F8 = F7 | G8 | G7 | E8 | E7;
	public static final long ALL_KING_MOVES_FROM_G8 = G7 | H8 | H7 | F8 | F7;
	public static final long ALL_KING_MOVES_FROM_H8 = H7 | G8 | G7;
	
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][] {{A2}, {B2}, {B1}, {  }, {  }, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1 = new int[] {0, 1, 2};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][] {{B2}, {C2}, {C1}, {  }, {  }, {  }, {A1}, {A2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][] {{C2}, {D2}, {D1}, {  }, {  }, {  }, {B1}, {B2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][] {{D2}, {E2}, {E1}, {  }, {  }, {  }, {C1}, {C2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][] {{E2}, {F2}, {F1}, {  }, {  }, {  }, {D1}, {D2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][] {{F2}, {G2}, {G1}, {  }, {  }, {  }, {E1}, {E2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][] {{G2}, {H2}, {H1}, {  }, {  }, {  }, {F1}, {F2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1 = new int[] {0, 1, 2, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][] {{H2}, {  }, {  }, {  }, {  }, {  }, {G1}, {G2}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1 = new int[] {0, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][] {{A3}, {B3}, {B2}, {B1}, {A1}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][] {{B3}, {C3}, {C2}, {C1}, {B1}, {A1}, {A2}, {A3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][] {{C3}, {D3}, {D2}, {D1}, {C1}, {B1}, {B2}, {B3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][] {{D3}, {E3}, {E2}, {E1}, {D1}, {C1}, {C2}, {C3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][] {{E3}, {F3}, {F2}, {F1}, {E1}, {D1}, {D2}, {D3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][] {{F3}, {G3}, {G2}, {G1}, {F1}, {E1}, {E2}, {E3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][] {{G3}, {H3}, {H2}, {H1}, {G1}, {F1}, {F2}, {F3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][] {{H3}, {  }, {  }, {  }, {H1}, {G1}, {G2}, {G3}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][] {{A4}, {B4}, {B3}, {B2}, {A2}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][] {{B4}, {C4}, {C3}, {C2}, {B2}, {A2}, {A3}, {A4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][] {{C4}, {D4}, {D3}, {D2}, {C2}, {B2}, {B3}, {B4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][] {{D4}, {E4}, {E3}, {E2}, {D2}, {C2}, {C3}, {C4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][] {{E4}, {F4}, {F3}, {F2}, {E2}, {D2}, {D3}, {D4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][] {{F4}, {G4}, {G3}, {G2}, {F2}, {E2}, {E3}, {E4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][] {{G4}, {H4}, {H3}, {H2}, {G2}, {F2}, {F3}, {F4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][] {{H4}, {  }, {  }, {  }, {H2}, {G2}, {G3}, {G4}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][] {{A5}, {B5}, {B4}, {B3}, {A3}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][] {{B5}, {C5}, {C4}, {C3}, {B3}, {A3}, {A4}, {A5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][] {{C5}, {D5}, {D4}, {D3}, {C3}, {B3}, {B4}, {B5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][] {{D5}, {E5}, {E4}, {E3}, {D3}, {C3}, {C4}, {C5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][] {{E5}, {F5}, {F4}, {F3}, {E3}, {D3}, {D4}, {D5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][] {{F5}, {G5}, {G4}, {G3}, {F3}, {E3}, {E4}, {E5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][] {{G5}, {H5}, {H4}, {H3}, {G3}, {F3}, {F4}, {F5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][] {{H5}, {  }, {  }, {  }, {H3}, {G3}, {G4}, {G5}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][] {{A6}, {B6}, {B5}, {B4}, {A4}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][] {{B6}, {C6}, {C5}, {C4}, {B4}, {A4}, {A5}, {A6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][] {{C6}, {D6}, {D5}, {D4}, {C4}, {B4}, {B5}, {B6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][] {{D6}, {E6}, {E5}, {E4}, {D4}, {C4}, {C5}, {C6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][] {{E6}, {F6}, {F5}, {F4}, {E4}, {D4}, {D5}, {D6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][] {{F6}, {G6}, {G5}, {G4}, {F4}, {E4}, {E5}, {E6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][] {{G6}, {H6}, {H5}, {H4}, {G4}, {F4}, {F5}, {F6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][] {{H6}, {  }, {  }, {  }, {H4}, {G4}, {G5}, {G6}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][] {{A7}, {B7}, {B6}, {B5}, {A5}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][] {{B7}, {C7}, {C6}, {C5}, {B5}, {A5}, {A6}, {A7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][] {{C7}, {D7}, {D6}, {D5}, {C5}, {B5}, {B6}, {B7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][] {{D7}, {E7}, {E6}, {E5}, {D5}, {C5}, {C6}, {C7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][] {{E7}, {F7}, {F6}, {F5}, {E5}, {D5}, {D6}, {D7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][] {{F7}, {G7}, {G6}, {G5}, {F5}, {E5}, {E6}, {E7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][] {{G7}, {H7}, {H6}, {H5}, {G5}, {F5}, {F6}, {F7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][] {{H7}, {  }, {  }, {  }, {H5}, {G5}, {G6}, {G7}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][] {{A8}, {B8}, {B7}, {B6}, {A6}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7 = new int[] {0, 1, 2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][] {{B8}, {C8}, {C7}, {C6}, {B6}, {A6}, {A7}, {A8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][] {{C8}, {D8}, {D7}, {D6}, {C6}, {B6}, {B7}, {B8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][] {{D8}, {E8}, {E7}, {E6}, {D6}, {C6}, {C7}, {C8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][] {{E8}, {F8}, {F7}, {F6}, {E6}, {D6}, {D7}, {D8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][] {{F8}, {G8}, {G7}, {G6}, {F6}, {E6}, {E7}, {E8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][] {{G8}, {H8}, {H7}, {H6}, {G6}, {F6}, {F7}, {F8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7 = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][] {{H8}, {  }, {  }, {  }, {H6}, {G6}, {G7}, {G8}};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7 = new int[] {0, 4, 5, 6, 7};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][] {{  }, {  }, {B8}, {B7}, {A7}, {  }, {  }, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8 = new int[] {2, 3, 4};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][] {{  }, {  }, {C8}, {C7}, {B7}, {A7}, {A8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][] {{  }, {  }, {D8}, {D7}, {C7}, {B7}, {B8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][] {{  }, {  }, {E8}, {E7}, {D7}, {C7}, {C8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][] {{  }, {  }, {F8}, {F7}, {E7}, {D7}, {D8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][] {{  }, {  }, {G8}, {G7}, {F7}, {E7}, {E8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][] {{  }, {  }, {H8}, {H7}, {G7}, {F7}, {F8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8 = new int[] {2, 3, 4, 5, 6};
	public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][] {{  }, {  }, {  }, {  }, {H7}, {G7}, {G8}, {  }};
	public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8 = new int[] {4, 5, 6};
	
	public static final long[] ALL_ORDERED_KING_MOVES = new long[] {ALL_KING_MOVES_FROM_A1, ALL_KING_MOVES_FROM_B1, ALL_KING_MOVES_FROM_C1, ALL_KING_MOVES_FROM_D1, ALL_KING_MOVES_FROM_E1, ALL_KING_MOVES_FROM_F1, ALL_KING_MOVES_FROM_G1, ALL_KING_MOVES_FROM_H1, ALL_KING_MOVES_FROM_A2, ALL_KING_MOVES_FROM_B2, ALL_KING_MOVES_FROM_C2, ALL_KING_MOVES_FROM_D2, ALL_KING_MOVES_FROM_E2, ALL_KING_MOVES_FROM_F2, ALL_KING_MOVES_FROM_G2, ALL_KING_MOVES_FROM_H2, ALL_KING_MOVES_FROM_A3, ALL_KING_MOVES_FROM_B3, ALL_KING_MOVES_FROM_C3, ALL_KING_MOVES_FROM_D3, ALL_KING_MOVES_FROM_E3, ALL_KING_MOVES_FROM_F3, ALL_KING_MOVES_FROM_G3, ALL_KING_MOVES_FROM_H3, ALL_KING_MOVES_FROM_A4, ALL_KING_MOVES_FROM_B4, ALL_KING_MOVES_FROM_C4, ALL_KING_MOVES_FROM_D4, ALL_KING_MOVES_FROM_E4, ALL_KING_MOVES_FROM_F4, ALL_KING_MOVES_FROM_G4, ALL_KING_MOVES_FROM_H4, ALL_KING_MOVES_FROM_A5, ALL_KING_MOVES_FROM_B5, ALL_KING_MOVES_FROM_C5, ALL_KING_MOVES_FROM_D5, ALL_KING_MOVES_FROM_E5, ALL_KING_MOVES_FROM_F5, ALL_KING_MOVES_FROM_G5, ALL_KING_MOVES_FROM_H5, ALL_KING_MOVES_FROM_A6, ALL_KING_MOVES_FROM_B6, ALL_KING_MOVES_FROM_C6, ALL_KING_MOVES_FROM_D6, ALL_KING_MOVES_FROM_E6, ALL_KING_MOVES_FROM_F6, ALL_KING_MOVES_FROM_G6, ALL_KING_MOVES_FROM_H6, ALL_KING_MOVES_FROM_A7, ALL_KING_MOVES_FROM_B7, ALL_KING_MOVES_FROM_C7, ALL_KING_MOVES_FROM_D7, ALL_KING_MOVES_FROM_E7, ALL_KING_MOVES_FROM_F7, ALL_KING_MOVES_FROM_G7, ALL_KING_MOVES_FROM_H7, ALL_KING_MOVES_FROM_A8, ALL_KING_MOVES_FROM_B8, ALL_KING_MOVES_FROM_C8, ALL_KING_MOVES_FROM_D8, ALL_KING_MOVES_FROM_E8, ALL_KING_MOVES_FROM_F8, ALL_KING_MOVES_FROM_G8, ALL_KING_MOVES_FROM_H8, };
	public static final long[][][] ALL_ORDERED_KING_DIRS = new long[][][] {ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H8, };
	public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][] {ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8, };
	
	//---------------------------- END GENERATED BLOCK ----------------------------------------------
	
	public static final long[] ALL_KING_MOVES = new long[Bits.PRIME_67];
	
	public static final int[][] ALL_KING_VALID_DIRS = new int[Bits.PRIME_67][];
	public static final int[][][] ALL_KING_DIRS_WITH_FIELD_IDS = new int[Bits.PRIME_67][][];
	public static final long[][][] ALL_KING_DIRS_WITH_BITBOARDS = new long[Bits.PRIME_67][][];
	
	static {
		for (int i=0; i<ALL_ORDERED_KING_MOVES.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_KING_MOVES[i];
			long[][] dirs = ALL_ORDERED_KING_DIRS[i];
			ALL_KING_MOVES[idx] = fieldMoves;
			ALL_KING_VALID_DIRS[idx] = ALL_ORDERED_KNIGHT_VALID_DIRS[i];
			ALL_KING_DIRS_WITH_BITBOARDS[idx] = dirs;
			ALL_KING_DIRS_WITH_FIELD_IDS[idx] = bitboards2fieldIDs(dirs);
		}
		
		verify();
	}
	
	private static final void verify() {
		for (int i=0; i<Bits.NUMBER_64; i++) {
			int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
			long moves = ALL_KING_MOVES[field_normalized_id];
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

    			String prefix = "public static final long ALL_KING_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
           		result = prefix;
            	
       			if (digit + 1 <= 7) {
       				result += "" + letters[letter] + digits[digit + 1] + " | ";
       			}
       			if (digit - 1 >= 0) {
       				result += "" + letters[letter] + digits[digit - 1] + " | ";
       			}
       			
           		if (letter + 1 <= 7) {
           			result += "" + letters[letter + 1] + digits[digit] + " | ";
           			if (digit + 1 <= 7) {
           				result += "" + letters[letter + 1] + digits[digit + 1] + " | ";
           			}
           			if (digit - 1 >= 0) {
           				result += "" + letters[letter + 1] + digits[digit - 1] + " | ";
           			}
           		}

           		if (letter - 1 >= 0) {
           			result += "" + letters[letter - 1] + digits[digit] + " | ";
           			if (digit + 1 <= 7) {
           				result += "" + letters[letter - 1] + digits[digit + 1] + " | ";
           			}
           			if (digit - 1 >= 0) {
           				result += "" + letters[letter - 1] + digits[digit - 1] + " | ";
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

    			String prefix = "public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
    			String prefix1 = "public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
       		result = prefix;
       		String result1 = prefix1;
            	
     			if (digit + 1 <= 7) {
     				result += "{" + letters[letter] + digits[digit + 1] + "}, ";
       			result1 += "0, ";
       		} else {
         		result += "{  }, ";
         	}
     			
     			if (letter + 1 <= 7 && digit + 1 <= 7) {
       			result += "{" + letters[letter + 1] + digits[digit + 1] + "}, ";
       			result1 += "1, ";
       		} else {
         		result += "{  }, ";
         	}
     			
     			if (letter + 1 <= 7) {
       			result += "{" + letters[letter + 1] + digits[digit] + "}, ";
       			result1 += "2, ";
       		} else {
         		result += "{  }, ";
         	}
     			
     			if (letter + 1 <= 7 && digit - 1 >= 0) {
       			result += "{" + letters[letter + 1] + digits[digit - 1] + "}, ";
       			result1 += "3, ";
       		} else {
         		result += "{  }, ";
         	}
     			
     			if (digit - 1 >= 0) {
     				result += "{" + letters[letter] + digits[digit - 1] + "}, ";
       			result1 += "4, ";
       		} else {
         		result += "{  }, ";
         	}  		

       		if (letter - 1 >= 0 && digit - 1 >= 0) {
       			result += "{" + letters[letter - 1] + digits[digit - 1] + "}, ";
       			result1 += "5, ";
       		} else {
         		result += "{  }, ";
         	}
       		
       		if (letter - 1 >= 0) {
       			result += "{" + letters[letter - 1] + digits[digit] + "}, ";
       			result1 += "6, ";
       		} else {
         		result += "{  }, ";
         	}
       		
       		if (letter - 1 >= 0 && digit + 1 <= 7) {
       			result += "{" + letters[letter - 1] + digits[digit + 1] + "}, ";
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
    	
    	result = "public static final long[] ALL_ORDERED_KING_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
           		result += "ALL_KING_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result); 
    	
    	result = "public static final long[][][] ALL_ORDERED_KING_DIRS = new long[][][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
           		result += "ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
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
