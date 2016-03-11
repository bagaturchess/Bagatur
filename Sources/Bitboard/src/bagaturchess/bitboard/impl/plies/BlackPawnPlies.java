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

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class BlackPawnPlies extends Fields {
	
	//---------------------------- START GENERATED BLOCK ----------------------------------------------
	
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H1 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A2 = B1 | A1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A2 = B1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A2 = A1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B2 = C1 | A1 | B1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B2 = C1 | A1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B2 = B1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C2 = D1 | B1 | C1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C2 = D1 | B1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C2 = C1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D2 = E1 | C1 | D1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D2 = E1 | C1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D2 = D1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E2 = F1 | D1 | E1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E2 = F1 | D1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E2 = E1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F2 = G1 | E1 | F1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F2 = G1 | E1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F2 = F1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G2 = H1 | F1 | G1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G2 = H1 | F1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G2 = G1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H2 = G1 | H1;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H2 = G1;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H2 = H1;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A3 = B2 | A2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A3 = B2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A3 = A2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B3 = C2 | A2 | B2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B3 = C2 | A2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B3 = B2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C3 = D2 | B2 | C2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C3 = D2 | B2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C3 = C2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D3 = E2 | C2 | D2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D3 = E2 | C2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D3 = D2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E3 = F2 | D2 | E2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E3 = F2 | D2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E3 = E2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F3 = G2 | E2 | F2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F3 = G2 | E2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F3 = F2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G3 = H2 | F2 | G2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G3 = H2 | F2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G3 = G2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H3 = G2 | H2;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H3 = G2;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H3 = H2;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A4 = B3 | A3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A4 = B3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A4 = A3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B4 = C3 | A3 | B3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B4 = C3 | A3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B4 = B3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C4 = D3 | B3 | C3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C4 = D3 | B3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C4 = C3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D4 = E3 | C3 | D3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D4 = E3 | C3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D4 = D3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E4 = F3 | D3 | E3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E4 = F3 | D3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E4 = E3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F4 = G3 | E3 | F3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F4 = G3 | E3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F4 = F3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G4 = H3 | F3 | G3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G4 = H3 | F3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G4 = G3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H4 = G3 | H3;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H4 = G3;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H4 = H3;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A5 = B4 | A4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A5 = B4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A5 = A4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B5 = C4 | A4 | B4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B5 = C4 | A4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B5 = B4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C5 = D4 | B4 | C4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C5 = D4 | B4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C5 = C4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D5 = E4 | C4 | D4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D5 = E4 | C4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D5 = D4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E5 = F4 | D4 | E4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E5 = F4 | D4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E5 = E4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F5 = G4 | E4 | F4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F5 = G4 | E4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F5 = F4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G5 = H4 | F4 | G4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G5 = H4 | F4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G5 = G4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H5 = G4 | H4;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H5 = G4;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H5 = H4;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A6 = B5 | A5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A6 = B5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A6 = A5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B6 = C5 | A5 | B5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B6 = C5 | A5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B6 = B5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C6 = D5 | B5 | C5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C6 = D5 | B5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C6 = C5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D6 = E5 | C5 | D5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D6 = E5 | C5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D6 = D5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E6 = F5 | D5 | E5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E6 = F5 | D5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E6 = E5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F6 = G5 | E5 | F5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F6 = G5 | E5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F6 = F5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G6 = H5 | F5 | G5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G6 = H5 | F5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G6 = G5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H6 = G5 | H5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H6 = G5;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H6 = H5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A7 = B6 | A6 | A5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A7 = B6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A7 = A6 | A5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B7 = C6 | A6 | B6 | B5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B7 = C6 | A6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B7 = B6 | B5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C7 = D6 | B6 | C6 | C5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C7 = D6 | B6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C7 = C6 | C5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D7 = E6 | C6 | D6 | D5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D7 = E6 | C6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D7 = D6 | D5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E7 = F6 | D6 | E6 | E5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E7 = F6 | D6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E7 = E6 | E5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F7 = G6 | E6 | F6 | F5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F7 = G6 | E6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F7 = F6 | F5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G7 = H6 | F6 | G6 | G5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G7 = H6 | F6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G7 = G6 | G5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H7 = G6 | H6 | H5;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H7 = G6;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H7 = H6 | H5;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_A8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_B8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_C8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_D8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_E8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_F8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_G8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_MOVES_FROM_H8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H8 = NUMBER_0;
	public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H8 = NUMBER_0;
	
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H1 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H1 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H1 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H1 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A2 = new long[][] {{  }, {B1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A2 = new long[][] {{A1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A2 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B2 = new long[][] {{A1}, {C1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B2 = new long[][] {{B1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C2 = new long[][] {{B1}, {D1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C2 = new long[][] {{C1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D2 = new long[][] {{C1}, {E1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D2 = new long[][] {{D1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E2 = new long[][] {{D1}, {F1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E2 = new long[][] {{E1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F2 = new long[][] {{E1}, {G1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F2 = new long[][] {{F1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G2 = new long[][] {{F1}, {H1}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G2 = new long[][] {{G1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G2 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H2 = new long[][] {{G1}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H2 = new long[][] {{H1}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H2 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H2 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A3 = new long[][] {{  }, {B2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A3 = new long[][] {{A2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A3 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B3 = new long[][] {{A2}, {C2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B3 = new long[][] {{B2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C3 = new long[][] {{B2}, {D2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C3 = new long[][] {{C2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D3 = new long[][] {{C2}, {E2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D3 = new long[][] {{D2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E3 = new long[][] {{D2}, {F2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E3 = new long[][] {{E2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F3 = new long[][] {{E2}, {G2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F3 = new long[][] {{F2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G3 = new long[][] {{F2}, {H2}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G3 = new long[][] {{G2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G3 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H3 = new long[][] {{G2}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H3 = new long[][] {{H2}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H3 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H3 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A4 = new long[][] {{  }, {B3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A4 = new long[][] {{A3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A4 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B4 = new long[][] {{A3}, {C3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B4 = new long[][] {{B3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C4 = new long[][] {{B3}, {D3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C4 = new long[][] {{C3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D4 = new long[][] {{C3}, {E3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D4 = new long[][] {{D3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E4 = new long[][] {{D3}, {F3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E4 = new long[][] {{E3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F4 = new long[][] {{E3}, {G3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F4 = new long[][] {{F3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G4 = new long[][] {{F3}, {H3}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G4 = new long[][] {{G3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G4 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H4 = new long[][] {{G3}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H4 = new long[][] {{H3}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H4 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H4 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A5 = new long[][] {{  }, {B4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A5 = new long[][] {{A4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A5 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B5 = new long[][] {{A4}, {C4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B5 = new long[][] {{B4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C5 = new long[][] {{B4}, {D4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C5 = new long[][] {{C4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D5 = new long[][] {{C4}, {E4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D5 = new long[][] {{D4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E5 = new long[][] {{D4}, {F4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E5 = new long[][] {{E4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F5 = new long[][] {{E4}, {G4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F5 = new long[][] {{F4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G5 = new long[][] {{F4}, {H4}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G5 = new long[][] {{G4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G5 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H5 = new long[][] {{G4}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H5 = new long[][] {{H4}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H5 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H5 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A6 = new long[][] {{  }, {B5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A6 = new long[][] {{A5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A6 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B6 = new long[][] {{A5}, {C5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B6 = new long[][] {{B5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C6 = new long[][] {{B5}, {D5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C6 = new long[][] {{C5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D6 = new long[][] {{C5}, {E5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D6 = new long[][] {{D5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E6 = new long[][] {{D5}, {F5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E6 = new long[][] {{E5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F6 = new long[][] {{E5}, {G5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F6 = new long[][] {{F5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G6 = new long[][] {{F5}, {H5}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G6 = new long[][] {{G5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G6 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H6 = new long[][] {{G5}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H6 = new long[][] {{H5}, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H6 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H6 = new int[] {0};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A7 = new long[][] {{  }, {B6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A7 = new long[][] {{A6}, {A5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A7 = new int[] {1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B7 = new long[][] {{A6}, {C6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B7 = new long[][] {{B6}, {B5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C7 = new long[][] {{B6}, {D6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C7 = new long[][] {{C6}, {C5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D7 = new long[][] {{C6}, {E6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D7 = new long[][] {{D6}, {D5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E7 = new long[][] {{D6}, {F6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E7 = new long[][] {{E6}, {E5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F7 = new long[][] {{E6}, {G6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F7 = new long[][] {{F6}, {F5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G7 = new long[][] {{F6}, {H6}};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G7 = new long[][] {{G6}, {G5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G7 = new int[] {0, 1};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H7 = new long[][] {{G6}, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H7 = new long[][] {{H6}, {H5}};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H7 = new int[] {0};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H7 = new int[] {0, 1};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G8 = new int[] {};
	public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H8 = new long[][] {{  }, {  }};
	public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H8 = new long[][] {{  }, {  }};
	public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H8 = new int[] {};
	public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H8 = new int[] {};
	
	public static final long[] ALL_ORDERED_BLACK_PAWN_MOVES = new long[] {ALL_BLACK_PAWN_MOVES_FROM_A1, ALL_BLACK_PAWN_MOVES_FROM_B1, ALL_BLACK_PAWN_MOVES_FROM_C1, ALL_BLACK_PAWN_MOVES_FROM_D1, ALL_BLACK_PAWN_MOVES_FROM_E1, ALL_BLACK_PAWN_MOVES_FROM_F1, ALL_BLACK_PAWN_MOVES_FROM_G1, ALL_BLACK_PAWN_MOVES_FROM_H1, ALL_BLACK_PAWN_MOVES_FROM_A2, ALL_BLACK_PAWN_MOVES_FROM_B2, ALL_BLACK_PAWN_MOVES_FROM_C2, ALL_BLACK_PAWN_MOVES_FROM_D2, ALL_BLACK_PAWN_MOVES_FROM_E2, ALL_BLACK_PAWN_MOVES_FROM_F2, ALL_BLACK_PAWN_MOVES_FROM_G2, ALL_BLACK_PAWN_MOVES_FROM_H2, ALL_BLACK_PAWN_MOVES_FROM_A3, ALL_BLACK_PAWN_MOVES_FROM_B3, ALL_BLACK_PAWN_MOVES_FROM_C3, ALL_BLACK_PAWN_MOVES_FROM_D3, ALL_BLACK_PAWN_MOVES_FROM_E3, ALL_BLACK_PAWN_MOVES_FROM_F3, ALL_BLACK_PAWN_MOVES_FROM_G3, ALL_BLACK_PAWN_MOVES_FROM_H3, ALL_BLACK_PAWN_MOVES_FROM_A4, ALL_BLACK_PAWN_MOVES_FROM_B4, ALL_BLACK_PAWN_MOVES_FROM_C4, ALL_BLACK_PAWN_MOVES_FROM_D4, ALL_BLACK_PAWN_MOVES_FROM_E4, ALL_BLACK_PAWN_MOVES_FROM_F4, ALL_BLACK_PAWN_MOVES_FROM_G4, ALL_BLACK_PAWN_MOVES_FROM_H4, ALL_BLACK_PAWN_MOVES_FROM_A5, ALL_BLACK_PAWN_MOVES_FROM_B5, ALL_BLACK_PAWN_MOVES_FROM_C5, ALL_BLACK_PAWN_MOVES_FROM_D5, ALL_BLACK_PAWN_MOVES_FROM_E5, ALL_BLACK_PAWN_MOVES_FROM_F5, ALL_BLACK_PAWN_MOVES_FROM_G5, ALL_BLACK_PAWN_MOVES_FROM_H5, ALL_BLACK_PAWN_MOVES_FROM_A6, ALL_BLACK_PAWN_MOVES_FROM_B6, ALL_BLACK_PAWN_MOVES_FROM_C6, ALL_BLACK_PAWN_MOVES_FROM_D6, ALL_BLACK_PAWN_MOVES_FROM_E6, ALL_BLACK_PAWN_MOVES_FROM_F6, ALL_BLACK_PAWN_MOVES_FROM_G6, ALL_BLACK_PAWN_MOVES_FROM_H6, ALL_BLACK_PAWN_MOVES_FROM_A7, ALL_BLACK_PAWN_MOVES_FROM_B7, ALL_BLACK_PAWN_MOVES_FROM_C7, ALL_BLACK_PAWN_MOVES_FROM_D7, ALL_BLACK_PAWN_MOVES_FROM_E7, ALL_BLACK_PAWN_MOVES_FROM_F7, ALL_BLACK_PAWN_MOVES_FROM_G7, ALL_BLACK_PAWN_MOVES_FROM_H7, ALL_BLACK_PAWN_MOVES_FROM_A8, ALL_BLACK_PAWN_MOVES_FROM_B8, ALL_BLACK_PAWN_MOVES_FROM_C8, ALL_BLACK_PAWN_MOVES_FROM_D8, ALL_BLACK_PAWN_MOVES_FROM_E8, ALL_BLACK_PAWN_MOVES_FROM_F8, ALL_BLACK_PAWN_MOVES_FROM_G8, ALL_BLACK_PAWN_MOVES_FROM_H8, };
	public static final long[] ALL_ORDERED_BLACK_PAWN_ATTACKS = new long[] {ALL_BLACK_PAWN_ATTACKS_FROM_A1, ALL_BLACK_PAWN_ATTACKS_FROM_B1, ALL_BLACK_PAWN_ATTACKS_FROM_C1, ALL_BLACK_PAWN_ATTACKS_FROM_D1, ALL_BLACK_PAWN_ATTACKS_FROM_E1, ALL_BLACK_PAWN_ATTACKS_FROM_F1, ALL_BLACK_PAWN_ATTACKS_FROM_G1, ALL_BLACK_PAWN_ATTACKS_FROM_H1, ALL_BLACK_PAWN_ATTACKS_FROM_A2, ALL_BLACK_PAWN_ATTACKS_FROM_B2, ALL_BLACK_PAWN_ATTACKS_FROM_C2, ALL_BLACK_PAWN_ATTACKS_FROM_D2, ALL_BLACK_PAWN_ATTACKS_FROM_E2, ALL_BLACK_PAWN_ATTACKS_FROM_F2, ALL_BLACK_PAWN_ATTACKS_FROM_G2, ALL_BLACK_PAWN_ATTACKS_FROM_H2, ALL_BLACK_PAWN_ATTACKS_FROM_A3, ALL_BLACK_PAWN_ATTACKS_FROM_B3, ALL_BLACK_PAWN_ATTACKS_FROM_C3, ALL_BLACK_PAWN_ATTACKS_FROM_D3, ALL_BLACK_PAWN_ATTACKS_FROM_E3, ALL_BLACK_PAWN_ATTACKS_FROM_F3, ALL_BLACK_PAWN_ATTACKS_FROM_G3, ALL_BLACK_PAWN_ATTACKS_FROM_H3, ALL_BLACK_PAWN_ATTACKS_FROM_A4, ALL_BLACK_PAWN_ATTACKS_FROM_B4, ALL_BLACK_PAWN_ATTACKS_FROM_C4, ALL_BLACK_PAWN_ATTACKS_FROM_D4, ALL_BLACK_PAWN_ATTACKS_FROM_E4, ALL_BLACK_PAWN_ATTACKS_FROM_F4, ALL_BLACK_PAWN_ATTACKS_FROM_G4, ALL_BLACK_PAWN_ATTACKS_FROM_H4, ALL_BLACK_PAWN_ATTACKS_FROM_A5, ALL_BLACK_PAWN_ATTACKS_FROM_B5, ALL_BLACK_PAWN_ATTACKS_FROM_C5, ALL_BLACK_PAWN_ATTACKS_FROM_D5, ALL_BLACK_PAWN_ATTACKS_FROM_E5, ALL_BLACK_PAWN_ATTACKS_FROM_F5, ALL_BLACK_PAWN_ATTACKS_FROM_G5, ALL_BLACK_PAWN_ATTACKS_FROM_H5, ALL_BLACK_PAWN_ATTACKS_FROM_A6, ALL_BLACK_PAWN_ATTACKS_FROM_B6, ALL_BLACK_PAWN_ATTACKS_FROM_C6, ALL_BLACK_PAWN_ATTACKS_FROM_D6, ALL_BLACK_PAWN_ATTACKS_FROM_E6, ALL_BLACK_PAWN_ATTACKS_FROM_F6, ALL_BLACK_PAWN_ATTACKS_FROM_G6, ALL_BLACK_PAWN_ATTACKS_FROM_H6, ALL_BLACK_PAWN_ATTACKS_FROM_A7, ALL_BLACK_PAWN_ATTACKS_FROM_B7, ALL_BLACK_PAWN_ATTACKS_FROM_C7, ALL_BLACK_PAWN_ATTACKS_FROM_D7, ALL_BLACK_PAWN_ATTACKS_FROM_E7, ALL_BLACK_PAWN_ATTACKS_FROM_F7, ALL_BLACK_PAWN_ATTACKS_FROM_G7, ALL_BLACK_PAWN_ATTACKS_FROM_H7, ALL_BLACK_PAWN_ATTACKS_FROM_A8, ALL_BLACK_PAWN_ATTACKS_FROM_B8, ALL_BLACK_PAWN_ATTACKS_FROM_C8, ALL_BLACK_PAWN_ATTACKS_FROM_D8, ALL_BLACK_PAWN_ATTACKS_FROM_E8, ALL_BLACK_PAWN_ATTACKS_FROM_F8, ALL_BLACK_PAWN_ATTACKS_FROM_G8, ALL_BLACK_PAWN_ATTACKS_FROM_H8, };
	public static final long[] ALL_ORDERED_BLACK_PAWN_NONATTACKS = new long[] {ALL_BLACK_PAWN_NONATTACKS_FROM_A1, ALL_BLACK_PAWN_NONATTACKS_FROM_B1, ALL_BLACK_PAWN_NONATTACKS_FROM_C1, ALL_BLACK_PAWN_NONATTACKS_FROM_D1, ALL_BLACK_PAWN_NONATTACKS_FROM_E1, ALL_BLACK_PAWN_NONATTACKS_FROM_F1, ALL_BLACK_PAWN_NONATTACKS_FROM_G1, ALL_BLACK_PAWN_NONATTACKS_FROM_H1, ALL_BLACK_PAWN_NONATTACKS_FROM_A2, ALL_BLACK_PAWN_NONATTACKS_FROM_B2, ALL_BLACK_PAWN_NONATTACKS_FROM_C2, ALL_BLACK_PAWN_NONATTACKS_FROM_D2, ALL_BLACK_PAWN_NONATTACKS_FROM_E2, ALL_BLACK_PAWN_NONATTACKS_FROM_F2, ALL_BLACK_PAWN_NONATTACKS_FROM_G2, ALL_BLACK_PAWN_NONATTACKS_FROM_H2, ALL_BLACK_PAWN_NONATTACKS_FROM_A3, ALL_BLACK_PAWN_NONATTACKS_FROM_B3, ALL_BLACK_PAWN_NONATTACKS_FROM_C3, ALL_BLACK_PAWN_NONATTACKS_FROM_D3, ALL_BLACK_PAWN_NONATTACKS_FROM_E3, ALL_BLACK_PAWN_NONATTACKS_FROM_F3, ALL_BLACK_PAWN_NONATTACKS_FROM_G3, ALL_BLACK_PAWN_NONATTACKS_FROM_H3, ALL_BLACK_PAWN_NONATTACKS_FROM_A4, ALL_BLACK_PAWN_NONATTACKS_FROM_B4, ALL_BLACK_PAWN_NONATTACKS_FROM_C4, ALL_BLACK_PAWN_NONATTACKS_FROM_D4, ALL_BLACK_PAWN_NONATTACKS_FROM_E4, ALL_BLACK_PAWN_NONATTACKS_FROM_F4, ALL_BLACK_PAWN_NONATTACKS_FROM_G4, ALL_BLACK_PAWN_NONATTACKS_FROM_H4, ALL_BLACK_PAWN_NONATTACKS_FROM_A5, ALL_BLACK_PAWN_NONATTACKS_FROM_B5, ALL_BLACK_PAWN_NONATTACKS_FROM_C5, ALL_BLACK_PAWN_NONATTACKS_FROM_D5, ALL_BLACK_PAWN_NONATTACKS_FROM_E5, ALL_BLACK_PAWN_NONATTACKS_FROM_F5, ALL_BLACK_PAWN_NONATTACKS_FROM_G5, ALL_BLACK_PAWN_NONATTACKS_FROM_H5, ALL_BLACK_PAWN_NONATTACKS_FROM_A6, ALL_BLACK_PAWN_NONATTACKS_FROM_B6, ALL_BLACK_PAWN_NONATTACKS_FROM_C6, ALL_BLACK_PAWN_NONATTACKS_FROM_D6, ALL_BLACK_PAWN_NONATTACKS_FROM_E6, ALL_BLACK_PAWN_NONATTACKS_FROM_F6, ALL_BLACK_PAWN_NONATTACKS_FROM_G6, ALL_BLACK_PAWN_NONATTACKS_FROM_H6, ALL_BLACK_PAWN_NONATTACKS_FROM_A7, ALL_BLACK_PAWN_NONATTACKS_FROM_B7, ALL_BLACK_PAWN_NONATTACKS_FROM_C7, ALL_BLACK_PAWN_NONATTACKS_FROM_D7, ALL_BLACK_PAWN_NONATTACKS_FROM_E7, ALL_BLACK_PAWN_NONATTACKS_FROM_F7, ALL_BLACK_PAWN_NONATTACKS_FROM_G7, ALL_BLACK_PAWN_NONATTACKS_FROM_H7, ALL_BLACK_PAWN_NONATTACKS_FROM_A8, ALL_BLACK_PAWN_NONATTACKS_FROM_B8, ALL_BLACK_PAWN_NONATTACKS_FROM_C8, ALL_BLACK_PAWN_NONATTACKS_FROM_D8, ALL_BLACK_PAWN_NONATTACKS_FROM_E8, ALL_BLACK_PAWN_NONATTACKS_FROM_F8, ALL_BLACK_PAWN_NONATTACKS_FROM_G8, ALL_BLACK_PAWN_NONATTACKS_FROM_H8, };
	
	public static final long[][][] ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS = new long[][][] {ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H8, };
	public static final long[][][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS = new long[][][] {ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H8, };
	
	public static final int[][] ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[][] {ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H8, };
	public static final int[][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[][] {ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H8, };

	//---------------------------- END GENERATED BLOCK ----------------------------------------------
	
	public static final long[] ALL_BLACK_PAWN_MOVES = new long[Bits.PRIME_67];
	public static final long[] ALL_BLACK_PAWN_ATTACKS_MOVES = new long[Bits.PRIME_67];
	public static final long[] ALL_BLACK_PAWN_NONATTACKS_MOVES = new long[Bits.PRIME_67];
	
	public static final int[][] ALL_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[Bits.PRIME_67][];
	public static final long[][][] ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS = new long[Bits.PRIME_67][][];
	public static final int[][][] ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS = new int[Bits.PRIME_67][][];

	public static final int[][] ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[Bits.PRIME_67][];
	public static final long[][][] ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS = new long[Bits.PRIME_67][][];
	public static final int[][][] ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS = new int[Bits.PRIME_67][][];

	public static final int[] B_MAGIC = Utils.reverseSpecial ( new int[]{
			 0, 0, 0, 0, 0, 0, 0, 0,
			 1, 1, 1, 1, 1, 1, 1, 1,
			 2, 2, 2, 2, 2, 2, 2, 2,
			 3, 3, 3, 3, 3, 3, 3, 3,
			 4, 4, 4, 4, 4, 4, 4, 4,
			 5, 5, 5, 5, 5, 5, 5, 5,
			 6, 6, 6, 6, 6, 6, 6, 6,
			 7, 7, 7, 7, 7, 7, 7, 7,
	});
	
	public static int getMagic(int colour, int fromID, int toID) {
		if (colour != Figures.COLOUR_BLACK) {
			throw new IllegalStateException();
		}
		return -B_MAGIC[fromID] + B_MAGIC[toID];
	}
	
	static {
		for (int i=0; i<ALL_ORDERED_BLACK_PAWN_MOVES.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_BLACK_PAWN_MOVES[i];
			ALL_BLACK_PAWN_MOVES[idx] = fieldMoves;
		}
		
		for (int i=0; i<ALL_ORDERED_BLACK_PAWN_ATTACKS.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_BLACK_PAWN_ATTACKS[i];
			ALL_BLACK_PAWN_ATTACKS_MOVES[idx] = fieldMoves;
		}
		
		for (int i=0; i<ALL_ORDERED_BLACK_PAWN_NONATTACKS.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long fieldMoves = ALL_ORDERED_BLACK_PAWN_NONATTACKS[i];
			ALL_BLACK_PAWN_NONATTACKS_MOVES[idx] = fieldMoves;
		}
		
		for (int i=0; i<ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long[][] dirs = ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS[i];
			ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[idx] = ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS[i];
			ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[idx] = dirs;
			ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[idx] = bitboards2fieldIDs(dirs);
		}
		
		for (int i=0; i<ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS.length; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long[][] dirs = ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS[i];
			ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS[idx] = ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS[i];
			ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS[idx] = dirs;
			ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS[idx] = bitboards2fieldIDs(dirs);
		}
		
		verify();
	}
	
	private static final void verify() {
		for (int i=0; i<Bits.NUMBER_64; i++) {
			int idx = Fields.IDX_ORDERED_2_A1H1[i];
			long moves = ALL_BLACK_PAWN_MOVES[idx];
			String result = "Field[{" + i + ", " + Fields.IDX_ORDERED_2_A1H1[i] +  "}: " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
			
			if (moves != Bits.NUMBER_0) {
				int j = Bits.nextSetBit_L2R(0, moves);
				for (; j <= 63 && j != -1;
					   j = Bits.nextSetBit_L2R(j + 1, moves)) {
					result += Fields.ALL_ORDERED_NAMES[j] + " ";
				}
			} else {
				result += "NO_MOVES";
			}
			
			//ystem.out.println(result);
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
				String prefix = "public static final long ALL_BLACK_PAWN_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
				String prefix1 = "public static final long ALL_BLACK_PAWN_ATTACKS_FROM_" + letters[letter] + digits[digit] + " = ";
				String prefix2 = "public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_" + letters[letter] + digits[digit] + " = ";
			
				result = prefix;
		   		String result1 = prefix1;
		   		String result2 = prefix2;
				
		   		if (digit >= 1 && digit <= 6 && letter <= 6) {
		   				result += "" + letters[letter + 1] + digits[digit - 1] + " | ";
		   				result1 += "" + letters[letter + 1] + digits[digit - 1] + " | ";
		   		}
		   		
		   		if (digit >= 1 && digit <= 6 && letter >= 1) {
		   				result += "" + letters[letter - 1] + digits[digit - 1] + " | ";
		   				result1 += "" + letters[letter - 1] + digits[digit - 1] + " | ";
		   		}
		   		
		   		if (digit >= 1 && digit <= 6) {
		   			result += "" + letters[letter] + digits[digit - 1] + " | ";
		   			result2 += "" + letters[letter] + digits[digit - 1] + " | ";;
		   		}
		   		
		   		if (digit == 6) {
		   			result += "" + letters[letter] + digits[digit - 2] + " | ";
		   			result2 += "" + letters[letter] + digits[digit - 2] + " | ";;
		   		}
		   		
		   		if (result.equals(prefix)) {
		   			result += "NUMBER_0";
		   		}
		   		if (result1.equals(prefix1)) {
		   			result1 += "NUMBER_0";
		   		}
		   		if (result2.equals(prefix2)) {
		   			result2 += "NUMBER_0";
		   		}
		   		
		   		if (result.endsWith(" | ")) {
		   			result = result.substring(0, result.length() - 3);
		   		}
		   		if (result1.endsWith(" | ")) {
		   			result1 = result1.substring(0, result1.length() - 3);
		   		}
		   		if (result2.endsWith(" | ")) {
		   			result2 = result2.substring(0, result2.length() - 3);
		   		}
		
		   		result += ";";
		   		result1 += ";";
		   		result2 += ";";
		   		
		  		System.out.println(result);
		  		System.out.println(result1);
		  		System.out.println(result2);
			}
    	}
    	
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

					//String prefix = "public static final long[][] ALL_BLACK_PAWN_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
					String prefix1 = "public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
					String prefix2 = "public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
					String prefix3 = "public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
					String prefix4 = "public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
       		
					//result = prefix;
        	String result1 = prefix1;
        	String result2 = prefix2;
        	String result3 = prefix3;
        	String result4 = prefix4;
       		
       		if (digit >= 1 && digit <= 6) {
       			//result += "{" + letters[letter] + digits[digit + 1] + "}, ";
       			result2 += "{" + letters[letter] + digits[digit - 1] + "}, ";
       			result4 += "0, ";
       		} else {
       			result2 += "{  }, ";
       		}
       		
       		if (digit == 6) {
       			//result += "{" + letters[letter] + digits[digit + 2] + "}, ";
       			result2 += "{" + letters[letter] + digits[digit - 2] + "}, ";
       			result4 += "1, ";
       		} else {
       			result2 += "{  }, ";
       		}
       		
       		if (digit >= 1 && digit <= 6 && letter >= 1) {
     				//result += "{" + letters[letter - 1] + digits[digit + 1] + "}, ";
       			result1 += "{" + letters[letter - 1] + digits[digit - 1] + "}, ";
     				result3 += "0, ";
       		} else {
     				result1 += "{  }, ";
     			}
       		
       		if (digit >= 1 && digit <= 6 && letter <= 6) {
       				//result += "{" + letters[letter + 1] + digits[digit + 1] + "}, ";
       			result1 += "{" + letters[letter + 1] + digits[digit - 1] + "}, ";
       			result3 += "1, ";
       		} else {
       			result1 += "{  }, ";
       		}

       		
       		if (result1.equals(prefix1)) {
       			result1 += "};";
       		} else {
	       		if (result1.endsWith(", ")) {
	       			result1 = result1.substring(0, result1.length() - 2);
	       		}
	       		result1 += "};";
       		}
       		
       		if (result2.equals(prefix2)) {
       			result2 += "};";
       		} else {
	       		if (result2.endsWith(", ")) {
	       			result2 = result2.substring(0, result2.length() - 2);
	       		}
	       		result2 += "};";
       		}
       		
       		if (result3.equals(prefix3)) {
       			result3 += "};";
       		} else {
	       		if (result3.endsWith(", ")) {
	       			result3 = result3.substring(0, result3.length() - 2);
	       		}
	       		result3 += "};";
       		}
       		
       		if (result4.equals(prefix4)) {
       			result4 += "};";
       		} else {
	       		if (result4.endsWith(", ")) {
	       			result4 = result4.substring(0, result4.length() - 2);
	       		}
	       		result4 += "};";
       		}
       		
      		//System.out.println(result);
      		System.out.println(result1);
      		System.out.println(result2);
      		System.out.println(result3);
      		System.out.println(result4);
    		}
    	}
    	
    	result = "public static final long[] ALL_ORDERED_BLACK_PAWN_MOVES = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_BLACK_PAWN_ATTACKS = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_ATTACKS_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[] ALL_ORDERED_BLACK_PAWN_NONATTACKS = new long[] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_NONATTACKS_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result); 
    	
    	result = "public static final long[][][] ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS = new long[][][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final long[][][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS = new long[][][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final int[][] ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
    	
    	result = "public static final int[][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[][] {";
    	for (int digit=0; digit<8; digit++) {
    		for (int letter=0; letter<8; letter++) {

    			//String prefix = "" + letters[letter] + digits[digit] + " = ";
          result += "ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
    		}
    	}
    	result += "};";
    	
    	System.out.println(result);
	}
}
