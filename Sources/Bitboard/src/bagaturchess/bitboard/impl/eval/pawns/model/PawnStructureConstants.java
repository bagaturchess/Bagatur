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
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.Move;

public class PawnStructureConstants extends Fields {

	private static final long WHITE_PASSED_A1 = A2 | A3 | A4 | A5 | A6 | A7 | A8 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSED_B1 = A2 | A3 | A4 | A5 | A6 | A7 | A8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSED_C1 = B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSED_D1 = C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSED_E1 = D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSED_F1 = E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSED_G1 = F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_H1 = G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_A2 = A3 | A4 | A5 | A6 | A7 | A8 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSED_B2 = A3 | A4 | A5 | A6 | A7 | A8 | B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSED_C2 = B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSED_D2 = C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSED_E2 = D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSED_F2 = E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSED_G2 = F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_H2 = G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_A3 = A4 | A5 | A6 | A7 | A8 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSED_B3 = A4 | A5 | A6 | A7 | A8 | B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSED_C3 = B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSED_D3 = C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSED_E3 = D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSED_F3 = E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSED_G3 = F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_H3 = G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_A4 = A5 | A6 | A7 | A8 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSED_B4 = A5 | A6 | A7 | A8 | B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSED_C4 = B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSED_D4 = C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSED_E4 = D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSED_F4 = E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSED_G4 = F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_H4 = G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSED_A5 = A6 | A7 | A8 | B6 | B7 | B8;
	private static final long WHITE_PASSED_B5 = A6 | A7 | A8 | B6 | B7 | B8 | C6 | C7 | C8;
	private static final long WHITE_PASSED_C5 = B6 | B7 | B8 | C6 | C7 | C8 | D6 | D7 | D8;
	private static final long WHITE_PASSED_D5 = C6 | C7 | C8 | D6 | D7 | D8 | E6 | E7 | E8;
	private static final long WHITE_PASSED_E5 = D6 | D7 | D8 | E6 | E7 | E8 | F6 | F7 | F8;
	private static final long WHITE_PASSED_F5 = E6 | E7 | E8 | F6 | F7 | F8 | G6 | G7 | G8;
	private static final long WHITE_PASSED_G5 = F6 | F7 | F8 | G6 | G7 | G8 | H6 | H7 | H8;
	private static final long WHITE_PASSED_H5 = G6 | G7 | G8 | H6 | H7 | H8;
	private static final long WHITE_PASSED_A6 = A7 | A8 | B7 | B8;
	private static final long WHITE_PASSED_B6 = A7 | A8 | B7 | B8 | C7 | C8;
	private static final long WHITE_PASSED_C6 = B7 | B8 | C7 | C8 | D7 | D8;
	private static final long WHITE_PASSED_D6 = C7 | C8 | D7 | D8 | E7 | E8;
	private static final long WHITE_PASSED_E6 = D7 | D8 | E7 | E8 | F7 | F8;
	private static final long WHITE_PASSED_F6 = E7 | E8 | F7 | F8 | G7 | G8;
	private static final long WHITE_PASSED_G6 = F7 | F8 | G7 | G8 | H7 | H8;
	private static final long WHITE_PASSED_H6 = G7 | G8 | H7 | H8;
	private static final long WHITE_PASSED_A7 = A8 | B8;
	private static final long WHITE_PASSED_B7 = A8 | B8 | C8;
	private static final long WHITE_PASSED_C7 = B8 | C8 | D8;
	private static final long WHITE_PASSED_D7 = C8 | D8 | E8;
	private static final long WHITE_PASSED_E7 = D8 | E8 | F8;
	private static final long WHITE_PASSED_F7 = E8 | F8 | G8;
	private static final long WHITE_PASSED_G7 = F8 | G8 | H8;
	private static final long WHITE_PASSED_H7 = G8 | H8;
	private static final long WHITE_PASSED_A8 = 0L;
	private static final long WHITE_PASSED_B8 = 0L;
	private static final long WHITE_PASSED_C8 = 0L;
	private static final long WHITE_PASSED_D8 = 0L;
	private static final long WHITE_PASSED_E8 = 0L;
	private static final long WHITE_PASSED_F8 = 0L;
	private static final long WHITE_PASSED_G8 = 0L;
	private static final long WHITE_PASSED_H8 = 0L;
	private static final long[] WHITE_PASSED_ORDERED = new long[] {WHITE_PASSED_A1, WHITE_PASSED_B1, WHITE_PASSED_C1, WHITE_PASSED_D1, WHITE_PASSED_E1, WHITE_PASSED_F1, WHITE_PASSED_G1, WHITE_PASSED_H1, WHITE_PASSED_A2, WHITE_PASSED_B2, WHITE_PASSED_C2, WHITE_PASSED_D2, WHITE_PASSED_E2, WHITE_PASSED_F2, WHITE_PASSED_G2, WHITE_PASSED_H2, WHITE_PASSED_A3, WHITE_PASSED_B3, WHITE_PASSED_C3, WHITE_PASSED_D3, WHITE_PASSED_E3, WHITE_PASSED_F3, WHITE_PASSED_G3, WHITE_PASSED_H3, WHITE_PASSED_A4, WHITE_PASSED_B4, WHITE_PASSED_C4, WHITE_PASSED_D4, WHITE_PASSED_E4, WHITE_PASSED_F4, WHITE_PASSED_G4, WHITE_PASSED_H4, WHITE_PASSED_A5, WHITE_PASSED_B5, WHITE_PASSED_C5, WHITE_PASSED_D5, WHITE_PASSED_E5, WHITE_PASSED_F5, WHITE_PASSED_G5, WHITE_PASSED_H5, WHITE_PASSED_A6, WHITE_PASSED_B6, WHITE_PASSED_C6, WHITE_PASSED_D6, WHITE_PASSED_E6, WHITE_PASSED_F6, WHITE_PASSED_G6, WHITE_PASSED_H6, WHITE_PASSED_A7, WHITE_PASSED_B7, WHITE_PASSED_C7, WHITE_PASSED_D7, WHITE_PASSED_E7, WHITE_PASSED_F7, WHITE_PASSED_G7, WHITE_PASSED_H7, WHITE_PASSED_A8, WHITE_PASSED_B8, WHITE_PASSED_C8, WHITE_PASSED_D8, WHITE_PASSED_E8, WHITE_PASSED_F8, WHITE_PASSED_G8, WHITE_PASSED_H8, };
	private static final long BLACK_PASSED_H8 = G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_G8 = F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_F8 = E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSED_E8 = D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSED_D8 = C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSED_C8 = B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSED_B8 = A7 | A6 | A5 | A4 | A3 | A2 | A1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSED_A8 = A7 | A6 | A5 | A4 | A3 | A2 | A1 | B7 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSED_H7 = G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_G7 = F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_F7 = E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSED_E7 = D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSED_D7 = C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSED_C7 = B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSED_B7 = A6 | A5 | A4 | A3 | A2 | A1 | B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSED_A7 = A6 | A5 | A4 | A3 | A2 | A1 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSED_H6 = G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_G6 = F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_F6 = E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSED_E6 = D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSED_D6 = C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSED_C6 = B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSED_B6 = A5 | A4 | A3 | A2 | A1 | B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSED_A6 = A5 | A4 | A3 | A2 | A1 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSED_H5 = G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_G5 = F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSED_F5 = E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSED_E5 = D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSED_D5 = C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSED_C5 = B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSED_B5 = A4 | A3 | A2 | A1 | B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSED_A5 = A4 | A3 | A2 | A1 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSED_H4 = G3 | G2 | G1 | H3 | H2 | H1;
	private static final long BLACK_PASSED_G4 = F3 | F2 | F1 | G3 | G2 | G1 | H3 | H2 | H1;
	private static final long BLACK_PASSED_F4 = E3 | E2 | E1 | F3 | F2 | F1 | G3 | G2 | G1;
	private static final long BLACK_PASSED_E4 = D3 | D2 | D1 | E3 | E2 | E1 | F3 | F2 | F1;
	private static final long BLACK_PASSED_D4 = C3 | C2 | C1 | D3 | D2 | D1 | E3 | E2 | E1;
	private static final long BLACK_PASSED_C4 = B3 | B2 | B1 | C3 | C2 | C1 | D3 | D2 | D1;
	private static final long BLACK_PASSED_B4 = A3 | A2 | A1 | B3 | B2 | B1 | C3 | C2 | C1;
	private static final long BLACK_PASSED_A4 = A3 | A2 | A1 | B3 | B2 | B1;
	private static final long BLACK_PASSED_H3 = G2 | G1 | H2 | H1;
	private static final long BLACK_PASSED_G3 = F2 | F1 | G2 | G1 | H2 | H1;
	private static final long BLACK_PASSED_F3 = E2 | E1 | F2 | F1 | G2 | G1;
	private static final long BLACK_PASSED_E3 = D2 | D1 | E2 | E1 | F2 | F1;
	private static final long BLACK_PASSED_D3 = C2 | C1 | D2 | D1 | E2 | E1;
	private static final long BLACK_PASSED_C3 = B2 | B1 | C2 | C1 | D2 | D1;
	private static final long BLACK_PASSED_B3 = A2 | A1 | B2 | B1 | C2 | C1;
	private static final long BLACK_PASSED_A3 = A2 | A1 | B2 | B1;
	private static final long BLACK_PASSED_H2 = G1 | H1;
	private static final long BLACK_PASSED_G2 = F1 | G1 | H1;
	private static final long BLACK_PASSED_F2 = E1 | F1 | G1;
	private static final long BLACK_PASSED_E2 = D1 | E1 | F1;
	private static final long BLACK_PASSED_D2 = C1 | D1 | E1;
	private static final long BLACK_PASSED_C2 = B1 | C1 | D1;
	private static final long BLACK_PASSED_B2 = A1 | B1 | C1;
	private static final long BLACK_PASSED_A2 = A1 | B1;
	private static final long BLACK_PASSED_H1 = 0L;
	private static final long BLACK_PASSED_G1 = 0L;
	private static final long BLACK_PASSED_F1 = 0L;
	private static final long BLACK_PASSED_E1 = 0L;
	private static final long BLACK_PASSED_D1 = 0L;
	private static final long BLACK_PASSED_C1 = 0L;
	private static final long BLACK_PASSED_B1 = 0L;
	private static final long BLACK_PASSED_A1 = 0L;
	private static final long[] BLACK_PASSED_ORDERED = new long[] {BLACK_PASSED_A1, BLACK_PASSED_B1, BLACK_PASSED_C1, BLACK_PASSED_D1, BLACK_PASSED_E1, BLACK_PASSED_F1, BLACK_PASSED_G1, BLACK_PASSED_H1, BLACK_PASSED_A2, BLACK_PASSED_B2, BLACK_PASSED_C2, BLACK_PASSED_D2, BLACK_PASSED_E2, BLACK_PASSED_F2, BLACK_PASSED_G2, BLACK_PASSED_H2, BLACK_PASSED_A3, BLACK_PASSED_B3, BLACK_PASSED_C3, BLACK_PASSED_D3, BLACK_PASSED_E3, BLACK_PASSED_F3, BLACK_PASSED_G3, BLACK_PASSED_H3, BLACK_PASSED_A4, BLACK_PASSED_B4, BLACK_PASSED_C4, BLACK_PASSED_D4, BLACK_PASSED_E4, BLACK_PASSED_F4, BLACK_PASSED_G4, BLACK_PASSED_H4, BLACK_PASSED_A5, BLACK_PASSED_B5, BLACK_PASSED_C5, BLACK_PASSED_D5, BLACK_PASSED_E5, BLACK_PASSED_F5, BLACK_PASSED_G5, BLACK_PASSED_H5, BLACK_PASSED_A6, BLACK_PASSED_B6, BLACK_PASSED_C6, BLACK_PASSED_D6, BLACK_PASSED_E6, BLACK_PASSED_F6, BLACK_PASSED_G6, BLACK_PASSED_H6, BLACK_PASSED_A7, BLACK_PASSED_B7, BLACK_PASSED_C7, BLACK_PASSED_D7, BLACK_PASSED_E7, BLACK_PASSED_F7, BLACK_PASSED_G7, BLACK_PASSED_H7, BLACK_PASSED_A8, BLACK_PASSED_B8, BLACK_PASSED_C8, BLACK_PASSED_D8, BLACK_PASSED_E8, BLACK_PASSED_F8, BLACK_PASSED_G8, BLACK_PASSED_H8, };
	private static final long WHITE_BACKWARD_A1 = 0L;
	private static final long WHITE_BACKWARD_B1 = 0L;
	private static final long WHITE_BACKWARD_C1 = 0L;
	private static final long WHITE_BACKWARD_D1 = 0L;
	private static final long WHITE_BACKWARD_E1 = 0L;
	private static final long WHITE_BACKWARD_F1 = 0L;
	private static final long WHITE_BACKWARD_G1 = 0L;
	private static final long WHITE_BACKWARD_H1 = 0L;
	private static final long WHITE_BACKWARD_A2 = B2 | B1;
	private static final long WHITE_BACKWARD_B2 = A2 | A1 | C2 | C1;
	private static final long WHITE_BACKWARD_C2 = B2 | B1 | D2 | D1;
	private static final long WHITE_BACKWARD_D2 = C2 | C1 | E2 | E1;
	private static final long WHITE_BACKWARD_E2 = D2 | D1 | F2 | F1;
	private static final long WHITE_BACKWARD_F2 = E2 | E1 | G2 | G1;
	private static final long WHITE_BACKWARD_G2 = F2 | F1 | H2 | H1;
	private static final long WHITE_BACKWARD_H2 = G2 | G1;
	private static final long WHITE_BACKWARD_A3 = B3 | B2 | B1;
	private static final long WHITE_BACKWARD_B3 = A3 | A2 | A1 | C3 | C2 | C1;
	private static final long WHITE_BACKWARD_C3 = B3 | B2 | B1 | D3 | D2 | D1;
	private static final long WHITE_BACKWARD_D3 = C3 | C2 | C1 | E3 | E2 | E1;
	private static final long WHITE_BACKWARD_E3 = D3 | D2 | D1 | F3 | F2 | F1;
	private static final long WHITE_BACKWARD_F3 = E3 | E2 | E1 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_G3 = F3 | F2 | F1 | H3 | H2 | H1;
	private static final long WHITE_BACKWARD_H3 = G3 | G2 | G1;
	private static final long WHITE_BACKWARD_A4 = B4 | B3 | B2 | B1;
	private static final long WHITE_BACKWARD_B4 = A4 | A3 | A2 | A1 | C4 | C3 | C2 | C1;
	private static final long WHITE_BACKWARD_C4 = B4 | B3 | B2 | B1 | D4 | D3 | D2 | D1;
	private static final long WHITE_BACKWARD_D4 = C4 | C3 | C2 | C1 | E4 | E3 | E2 | E1;
	private static final long WHITE_BACKWARD_E4 = D4 | D3 | D2 | D1 | F4 | F3 | F2 | F1;
	private static final long WHITE_BACKWARD_F4 = E4 | E3 | E2 | E1 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_G4 = F4 | F3 | F2 | F1 | H4 | H3 | H2 | H1;
	private static final long WHITE_BACKWARD_H4 = G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_A5 = B5 | B4 | B3 | B2 | B1;
	private static final long WHITE_BACKWARD_B5 = A5 | A4 | A3 | A2 | A1 | C5 | C4 | C3 | C2 | C1;
	private static final long WHITE_BACKWARD_C5 = B5 | B4 | B3 | B2 | B1 | D5 | D4 | D3 | D2 | D1;
	private static final long WHITE_BACKWARD_D5 = C5 | C4 | C3 | C2 | C1 | E5 | E4 | E3 | E2 | E1;
	private static final long WHITE_BACKWARD_E5 = D5 | D4 | D3 | D2 | D1 | F5 | F4 | F3 | F2 | F1;
	private static final long WHITE_BACKWARD_F5 = E5 | E4 | E3 | E2 | E1 | G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_G5 = F5 | F4 | F3 | F2 | F1 | H5 | H4 | H3 | H2 | H1;
	private static final long WHITE_BACKWARD_H5 = G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_A6 = B6 | B5 | B4 | B3 | B2 | B1;
	private static final long WHITE_BACKWARD_B6 = A6 | A5 | A4 | A3 | A2 | A1 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long WHITE_BACKWARD_C6 = B6 | B5 | B4 | B3 | B2 | B1 | D6 | D5 | D4 | D3 | D2 | D1;
	private static final long WHITE_BACKWARD_D6 = C6 | C5 | C4 | C3 | C2 | C1 | E6 | E5 | E4 | E3 | E2 | E1;
	private static final long WHITE_BACKWARD_E6 = D6 | D5 | D4 | D3 | D2 | D1 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long WHITE_BACKWARD_F6 = E6 | E5 | E4 | E3 | E2 | E1 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_G6 = F6 | F5 | F4 | F3 | F2 | F1 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long WHITE_BACKWARD_H6 = G6 | G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_A7 = B7 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long WHITE_BACKWARD_B7 = A7 | A6 | A5 | A4 | A3 | A2 | A1 | C7 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long WHITE_BACKWARD_C7 = B7 | B6 | B5 | B4 | B3 | B2 | B1 | D7 | D6 | D5 | D4 | D3 | D2 | D1;
	private static final long WHITE_BACKWARD_D7 = C7 | C6 | C5 | C4 | C3 | C2 | C1 | E7 | E6 | E5 | E4 | E3 | E2 | E1;
	private static final long WHITE_BACKWARD_E7 = D7 | D6 | D5 | D4 | D3 | D2 | D1 | F7 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long WHITE_BACKWARD_F7 = E7 | E6 | E5 | E4 | E3 | E2 | E1 | G7 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_G7 = F7 | F6 | F5 | F4 | F3 | F2 | F1 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long WHITE_BACKWARD_H7 = G7 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long WHITE_BACKWARD_A8 = 0L;
	private static final long WHITE_BACKWARD_B8 = 0L;
	private static final long WHITE_BACKWARD_C8 = 0L;
	private static final long WHITE_BACKWARD_D8 = 0L;
	private static final long WHITE_BACKWARD_E8 = 0L;
	private static final long WHITE_BACKWARD_F8 = 0L;
	private static final long WHITE_BACKWARD_G8 = 0L;
	private static final long WHITE_BACKWARD_H8 = 0L;
	private static final long[] WHITE_BACKWARD_ORDERED = new long[] {WHITE_BACKWARD_A1, WHITE_BACKWARD_B1, WHITE_BACKWARD_C1, WHITE_BACKWARD_D1, WHITE_BACKWARD_E1, WHITE_BACKWARD_F1, WHITE_BACKWARD_G1, WHITE_BACKWARD_H1, WHITE_BACKWARD_A2, WHITE_BACKWARD_B2, WHITE_BACKWARD_C2, WHITE_BACKWARD_D2, WHITE_BACKWARD_E2, WHITE_BACKWARD_F2, WHITE_BACKWARD_G2, WHITE_BACKWARD_H2, WHITE_BACKWARD_A3, WHITE_BACKWARD_B3, WHITE_BACKWARD_C3, WHITE_BACKWARD_D3, WHITE_BACKWARD_E3, WHITE_BACKWARD_F3, WHITE_BACKWARD_G3, WHITE_BACKWARD_H3, WHITE_BACKWARD_A4, WHITE_BACKWARD_B4, WHITE_BACKWARD_C4, WHITE_BACKWARD_D4, WHITE_BACKWARD_E4, WHITE_BACKWARD_F4, WHITE_BACKWARD_G4, WHITE_BACKWARD_H4, WHITE_BACKWARD_A5, WHITE_BACKWARD_B5, WHITE_BACKWARD_C5, WHITE_BACKWARD_D5, WHITE_BACKWARD_E5, WHITE_BACKWARD_F5, WHITE_BACKWARD_G5, WHITE_BACKWARD_H5, WHITE_BACKWARD_A6, WHITE_BACKWARD_B6, WHITE_BACKWARD_C6, WHITE_BACKWARD_D6, WHITE_BACKWARD_E6, WHITE_BACKWARD_F6, WHITE_BACKWARD_G6, WHITE_BACKWARD_H6, WHITE_BACKWARD_A7, WHITE_BACKWARD_B7, WHITE_BACKWARD_C7, WHITE_BACKWARD_D7, WHITE_BACKWARD_E7, WHITE_BACKWARD_F7, WHITE_BACKWARD_G7, WHITE_BACKWARD_H7, WHITE_BACKWARD_A8, WHITE_BACKWARD_B8, WHITE_BACKWARD_C8, WHITE_BACKWARD_D8, WHITE_BACKWARD_E8, WHITE_BACKWARD_F8, WHITE_BACKWARD_G8, WHITE_BACKWARD_H8, };
	private static final long BLACK_BACKWARD_A1 = 0L;
	private static final long BLACK_BACKWARD_B1 = 0L;
	private static final long BLACK_BACKWARD_C1 = 0L;
	private static final long BLACK_BACKWARD_D1 = 0L;
	private static final long BLACK_BACKWARD_E1 = 0L;
	private static final long BLACK_BACKWARD_F1 = 0L;
	private static final long BLACK_BACKWARD_G1 = 0L;
	private static final long BLACK_BACKWARD_H1 = 0L;
	private static final long BLACK_BACKWARD_A2 = B2 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long BLACK_BACKWARD_B2 = A2 | A3 | A4 | A5 | A6 | A7 | A8 | C2 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long BLACK_BACKWARD_C2 = B2 | B3 | B4 | B5 | B6 | B7 | B8 | D2 | D3 | D4 | D5 | D6 | D7 | D8;
	private static final long BLACK_BACKWARD_D2 = C2 | C3 | C4 | C5 | C6 | C7 | C8 | E2 | E3 | E4 | E5 | E6 | E7 | E8;
	private static final long BLACK_BACKWARD_E2 = D2 | D3 | D4 | D5 | D6 | D7 | D8 | F2 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long BLACK_BACKWARD_F2 = E2 | E3 | E4 | E5 | E6 | E7 | E8 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_G2 = F2 | F3 | F4 | F5 | F6 | F7 | F8 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long BLACK_BACKWARD_H2 = G2 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_A3 = B3 | B4 | B5 | B6 | B7 | B8;
	private static final long BLACK_BACKWARD_B3 = A3 | A4 | A5 | A6 | A7 | A8 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long BLACK_BACKWARD_C3 = B3 | B4 | B5 | B6 | B7 | B8 | D3 | D4 | D5 | D6 | D7 | D8;
	private static final long BLACK_BACKWARD_D3 = C3 | C4 | C5 | C6 | C7 | C8 | E3 | E4 | E5 | E6 | E7 | E8;
	private static final long BLACK_BACKWARD_E3 = D3 | D4 | D5 | D6 | D7 | D8 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long BLACK_BACKWARD_F3 = E3 | E4 | E5 | E6 | E7 | E8 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_G3 = F3 | F4 | F5 | F6 | F7 | F8 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long BLACK_BACKWARD_H3 = G3 | G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_A4 = B4 | B5 | B6 | B7 | B8;
	private static final long BLACK_BACKWARD_B4 = A4 | A5 | A6 | A7 | A8 | C4 | C5 | C6 | C7 | C8;
	private static final long BLACK_BACKWARD_C4 = B4 | B5 | B6 | B7 | B8 | D4 | D5 | D6 | D7 | D8;
	private static final long BLACK_BACKWARD_D4 = C4 | C5 | C6 | C7 | C8 | E4 | E5 | E6 | E7 | E8;
	private static final long BLACK_BACKWARD_E4 = D4 | D5 | D6 | D7 | D8 | F4 | F5 | F6 | F7 | F8;
	private static final long BLACK_BACKWARD_F4 = E4 | E5 | E6 | E7 | E8 | G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_G4 = F4 | F5 | F6 | F7 | F8 | H4 | H5 | H6 | H7 | H8;
	private static final long BLACK_BACKWARD_H4 = G4 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_A5 = B5 | B6 | B7 | B8;
	private static final long BLACK_BACKWARD_B5 = A5 | A6 | A7 | A8 | C5 | C6 | C7 | C8;
	private static final long BLACK_BACKWARD_C5 = B5 | B6 | B7 | B8 | D5 | D6 | D7 | D8;
	private static final long BLACK_BACKWARD_D5 = C5 | C6 | C7 | C8 | E5 | E6 | E7 | E8;
	private static final long BLACK_BACKWARD_E5 = D5 | D6 | D7 | D8 | F5 | F6 | F7 | F8;
	private static final long BLACK_BACKWARD_F5 = E5 | E6 | E7 | E8 | G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_G5 = F5 | F6 | F7 | F8 | H5 | H6 | H7 | H8;
	private static final long BLACK_BACKWARD_H5 = G5 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_A6 = B6 | B7 | B8;
	private static final long BLACK_BACKWARD_B6 = A6 | A7 | A8 | C6 | C7 | C8;
	private static final long BLACK_BACKWARD_C6 = B6 | B7 | B8 | D6 | D7 | D8;
	private static final long BLACK_BACKWARD_D6 = C6 | C7 | C8 | E6 | E7 | E8;
	private static final long BLACK_BACKWARD_E6 = D6 | D7 | D8 | F6 | F7 | F8;
	private static final long BLACK_BACKWARD_F6 = E6 | E7 | E8 | G6 | G7 | G8;
	private static final long BLACK_BACKWARD_G6 = F6 | F7 | F8 | H6 | H7 | H8;
	private static final long BLACK_BACKWARD_H6 = G6 | G7 | G8;
	private static final long BLACK_BACKWARD_A7 = B7 | B8;
	private static final long BLACK_BACKWARD_B7 = A7 | A8 | C7 | C8;
	private static final long BLACK_BACKWARD_C7 = B7 | B8 | D7 | D8;
	private static final long BLACK_BACKWARD_D7 = C7 | C8 | E7 | E8;
	private static final long BLACK_BACKWARD_E7 = D7 | D8 | F7 | F8;
	private static final long BLACK_BACKWARD_F7 = E7 | E8 | G7 | G8;
	private static final long BLACK_BACKWARD_G7 = F7 | F8 | H7 | H8;
	private static final long BLACK_BACKWARD_H7 = G7 | G8;
	private static final long BLACK_BACKWARD_A8 = 0L;
	private static final long BLACK_BACKWARD_B8 = 0L;
	private static final long BLACK_BACKWARD_C8 = 0L;
	private static final long BLACK_BACKWARD_D8 = 0L;
	private static final long BLACK_BACKWARD_E8 = 0L;
	private static final long BLACK_BACKWARD_F8 = 0L;
	private static final long BLACK_BACKWARD_G8 = 0L;
	private static final long BLACK_BACKWARD_H8 = 0L;
	private static final long[] BLACK_BACKWARD_ORDERED = new long[] {BLACK_BACKWARD_A1, BLACK_BACKWARD_B1, BLACK_BACKWARD_C1, BLACK_BACKWARD_D1, BLACK_BACKWARD_E1, BLACK_BACKWARD_F1, BLACK_BACKWARD_G1, BLACK_BACKWARD_H1, BLACK_BACKWARD_A2, BLACK_BACKWARD_B2, BLACK_BACKWARD_C2, BLACK_BACKWARD_D2, BLACK_BACKWARD_E2, BLACK_BACKWARD_F2, BLACK_BACKWARD_G2, BLACK_BACKWARD_H2, BLACK_BACKWARD_A3, BLACK_BACKWARD_B3, BLACK_BACKWARD_C3, BLACK_BACKWARD_D3, BLACK_BACKWARD_E3, BLACK_BACKWARD_F3, BLACK_BACKWARD_G3, BLACK_BACKWARD_H3, BLACK_BACKWARD_A4, BLACK_BACKWARD_B4, BLACK_BACKWARD_C4, BLACK_BACKWARD_D4, BLACK_BACKWARD_E4, BLACK_BACKWARD_F4, BLACK_BACKWARD_G4, BLACK_BACKWARD_H4, BLACK_BACKWARD_A5, BLACK_BACKWARD_B5, BLACK_BACKWARD_C5, BLACK_BACKWARD_D5, BLACK_BACKWARD_E5, BLACK_BACKWARD_F5, BLACK_BACKWARD_G5, BLACK_BACKWARD_H5, BLACK_BACKWARD_A6, BLACK_BACKWARD_B6, BLACK_BACKWARD_C6, BLACK_BACKWARD_D6, BLACK_BACKWARD_E6, BLACK_BACKWARD_F6, BLACK_BACKWARD_G6, BLACK_BACKWARD_H6, BLACK_BACKWARD_A7, BLACK_BACKWARD_B7, BLACK_BACKWARD_C7, BLACK_BACKWARD_D7, BLACK_BACKWARD_E7, BLACK_BACKWARD_F7, BLACK_BACKWARD_G7, BLACK_BACKWARD_H7, BLACK_BACKWARD_A8, BLACK_BACKWARD_B8, BLACK_BACKWARD_C8, BLACK_BACKWARD_D8, BLACK_BACKWARD_E8, BLACK_BACKWARD_F8, BLACK_BACKWARD_G8, BLACK_BACKWARD_H8, };
	private static final long WHITE_FRONT_A1 = 0L;
	private static final long WHITE_FRONT_B1 = 0L;
	private static final long WHITE_FRONT_C1 = 0L;
	private static final long WHITE_FRONT_D1 = 0L;
	private static final long WHITE_FRONT_E1 = 0L;
	private static final long WHITE_FRONT_F1 = 0L;
	private static final long WHITE_FRONT_G1 = 0L;
	private static final long WHITE_FRONT_H1 = 0L;
	private static final long WHITE_FRONT_A2 = A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_FRONT_B2 = B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_FRONT_C2 = C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_FRONT_D2 = D3 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_FRONT_E2 = E3 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_FRONT_F2 = F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_FRONT_G2 = G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_FRONT_H2 = H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_FRONT_A3 = A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_FRONT_B3 = B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_FRONT_C3 = C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_FRONT_D3 = D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_FRONT_E3 = E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_FRONT_F3 = F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_FRONT_G3 = G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_FRONT_H3 = H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_FRONT_A4 = A5 | A6 | A7 | A8;
	private static final long WHITE_FRONT_B4 = B5 | B6 | B7 | B8;
	private static final long WHITE_FRONT_C4 = C5 | C6 | C7 | C8;
	private static final long WHITE_FRONT_D4 = D5 | D6 | D7 | D8;
	private static final long WHITE_FRONT_E4 = E5 | E6 | E7 | E8;
	private static final long WHITE_FRONT_F4 = F5 | F6 | F7 | F8;
	private static final long WHITE_FRONT_G4 = G5 | G6 | G7 | G8;
	private static final long WHITE_FRONT_H4 = H5 | H6 | H7 | H8;
	private static final long WHITE_FRONT_A5 = A6 | A7 | A8;
	private static final long WHITE_FRONT_B5 = B6 | B7 | B8;
	private static final long WHITE_FRONT_C5 = C6 | C7 | C8;
	private static final long WHITE_FRONT_D5 = D6 | D7 | D8;
	private static final long WHITE_FRONT_E5 = E6 | E7 | E8;
	private static final long WHITE_FRONT_F5 = F6 | F7 | F8;
	private static final long WHITE_FRONT_G5 = G6 | G7 | G8;
	private static final long WHITE_FRONT_H5 = H6 | H7 | H8;
	private static final long WHITE_FRONT_A6 = A7 | A8;
	private static final long WHITE_FRONT_B6 = B7 | B8;
	private static final long WHITE_FRONT_C6 = C7 | C8;
	private static final long WHITE_FRONT_D6 = D7 | D8;
	private static final long WHITE_FRONT_E6 = E7 | E8;
	private static final long WHITE_FRONT_F6 = F7 | F8;
	private static final long WHITE_FRONT_G6 = G7 | G8;
	private static final long WHITE_FRONT_H6 = H7 | H8;
	private static final long WHITE_FRONT_A7 = A8;
	private static final long WHITE_FRONT_B7 = B8;
	private static final long WHITE_FRONT_C7 = C8;
	private static final long WHITE_FRONT_D7 = D8;
	private static final long WHITE_FRONT_E7 = E8;
	private static final long WHITE_FRONT_F7 = F8;
	private static final long WHITE_FRONT_G7 = G8;
	private static final long WHITE_FRONT_H7 = H8;
	private static final long WHITE_FRONT_A8 = 0L;
	private static final long WHITE_FRONT_B8 = 0L;
	private static final long WHITE_FRONT_C8 = 0L;
	private static final long WHITE_FRONT_D8 = 0L;
	private static final long WHITE_FRONT_E8 = 0L;
	private static final long WHITE_FRONT_F8 = 0L;
	private static final long WHITE_FRONT_G8 = 0L;
	private static final long WHITE_FRONT_H8 = 0L;
	private static final long[] WHITE_FRONT_ORDERED = new long[] {WHITE_FRONT_A1, WHITE_FRONT_B1, WHITE_FRONT_C1, WHITE_FRONT_D1, WHITE_FRONT_E1, WHITE_FRONT_F1, WHITE_FRONT_G1, WHITE_FRONT_H1, WHITE_FRONT_A2, WHITE_FRONT_B2, WHITE_FRONT_C2, WHITE_FRONT_D2, WHITE_FRONT_E2, WHITE_FRONT_F2, WHITE_FRONT_G2, WHITE_FRONT_H2, WHITE_FRONT_A3, WHITE_FRONT_B3, WHITE_FRONT_C3, WHITE_FRONT_D3, WHITE_FRONT_E3, WHITE_FRONT_F3, WHITE_FRONT_G3, WHITE_FRONT_H3, WHITE_FRONT_A4, WHITE_FRONT_B4, WHITE_FRONT_C4, WHITE_FRONT_D4, WHITE_FRONT_E4, WHITE_FRONT_F4, WHITE_FRONT_G4, WHITE_FRONT_H4, WHITE_FRONT_A5, WHITE_FRONT_B5, WHITE_FRONT_C5, WHITE_FRONT_D5, WHITE_FRONT_E5, WHITE_FRONT_F5, WHITE_FRONT_G5, WHITE_FRONT_H5, WHITE_FRONT_A6, WHITE_FRONT_B6, WHITE_FRONT_C6, WHITE_FRONT_D6, WHITE_FRONT_E6, WHITE_FRONT_F6, WHITE_FRONT_G6, WHITE_FRONT_H6, WHITE_FRONT_A7, WHITE_FRONT_B7, WHITE_FRONT_C7, WHITE_FRONT_D7, WHITE_FRONT_E7, WHITE_FRONT_F7, WHITE_FRONT_G7, WHITE_FRONT_H7, WHITE_FRONT_A8, WHITE_FRONT_B8, WHITE_FRONT_C8, WHITE_FRONT_D8, WHITE_FRONT_E8, WHITE_FRONT_F8, WHITE_FRONT_G8, WHITE_FRONT_H8, };
	private static final long BLACK_FRONT_FULL_A1 = 0L;
	private static final long BLACK_FRONT_FULL_B1 = 0L;
	private static final long BLACK_FRONT_FULL_C1 = 0L;
	private static final long BLACK_FRONT_FULL_D1 = 0L;
	private static final long BLACK_FRONT_FULL_E1 = 0L;
	private static final long BLACK_FRONT_FULL_F1 = 0L;
	private static final long BLACK_FRONT_FULL_G1 = 0L;
	private static final long BLACK_FRONT_FULL_H1 = 0L;
	private static final long BLACK_FRONT_FULL_A2 = A1;
	private static final long BLACK_FRONT_FULL_B2 = B1;
	private static final long BLACK_FRONT_FULL_C2 = C1;
	private static final long BLACK_FRONT_FULL_D2 = D1;
	private static final long BLACK_FRONT_FULL_E2 = E1;
	private static final long BLACK_FRONT_FULL_F2 = F1;
	private static final long BLACK_FRONT_FULL_G2 = G1;
	private static final long BLACK_FRONT_FULL_H2 = H1;
	private static final long BLACK_FRONT_FULL_A3 = A2 | A1;
	private static final long BLACK_FRONT_FULL_B3 = B2 | B1;
	private static final long BLACK_FRONT_FULL_C3 = C2 | C1;
	private static final long BLACK_FRONT_FULL_D3 = D2 | D1;
	private static final long BLACK_FRONT_FULL_E3 = E2 | E1;
	private static final long BLACK_FRONT_FULL_F3 = F2 | F1;
	private static final long BLACK_FRONT_FULL_G3 = G2 | G1;
	private static final long BLACK_FRONT_FULL_H3 = H2 | H1;
	private static final long BLACK_FRONT_FULL_A4 = A3 | A2 | A1;
	private static final long BLACK_FRONT_FULL_B4 = B3 | B2 | B1;
	private static final long BLACK_FRONT_FULL_C4 = C3 | C2 | C1;
	private static final long BLACK_FRONT_FULL_D4 = D3 | D2 | D1;
	private static final long BLACK_FRONT_FULL_E4 = E3 | E2 | E1;
	private static final long BLACK_FRONT_FULL_F4 = F3 | F2 | F1;
	private static final long BLACK_FRONT_FULL_G4 = G3 | G2 | G1;
	private static final long BLACK_FRONT_FULL_H4 = H3 | H2 | H1;
	private static final long BLACK_FRONT_FULL_A5 = A4 | A3 | A2 | A1;
	private static final long BLACK_FRONT_FULL_B5 = B4 | B3 | B2 | B1;
	private static final long BLACK_FRONT_FULL_C5 = C4 | C3 | C2 | C1;
	private static final long BLACK_FRONT_FULL_D5 = D4 | D3 | D2 | D1;
	private static final long BLACK_FRONT_FULL_E5 = E4 | E3 | E2 | E1;
	private static final long BLACK_FRONT_FULL_F5 = F4 | F3 | F2 | F1;
	private static final long BLACK_FRONT_FULL_G5 = G4 | G3 | G2 | G1;
	private static final long BLACK_FRONT_FULL_H5 = H4 | H3 | H2 | H1;
	private static final long BLACK_FRONT_FULL_A6 = A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_FRONT_FULL_B6 = B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_FRONT_FULL_C6 = C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_FRONT_FULL_D6 = D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_FRONT_FULL_E6 = E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_FRONT_FULL_F6 = F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_FRONT_FULL_G6 = G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_FRONT_FULL_H6 = H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_FRONT_FULL_A7 = A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_FRONT_FULL_B7 = B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_FRONT_FULL_C7 = C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_FRONT_FULL_D7 = D6 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_FRONT_FULL_E7 = E6 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_FRONT_FULL_F7 = F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_FRONT_FULL_G7 = G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_FRONT_FULL_H7 = H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_FRONT_FULL_A8 = 0L;
	private static final long BLACK_FRONT_FULL_B8 = 0L;
	private static final long BLACK_FRONT_FULL_C8 = 0L;
	private static final long BLACK_FRONT_FULL_D8 = 0L;
	private static final long BLACK_FRONT_FULL_E8 = 0L;
	private static final long BLACK_FRONT_FULL_F8 = 0L;
	private static final long BLACK_FRONT_FULL_G8 = 0L;
	private static final long BLACK_FRONT_FULL_H8 = 0L;
	private static final long[] BLACK_FRONT_FULL_ORDERED = new long[] {BLACK_FRONT_FULL_A1, BLACK_FRONT_FULL_B1, BLACK_FRONT_FULL_C1, BLACK_FRONT_FULL_D1, BLACK_FRONT_FULL_E1, BLACK_FRONT_FULL_F1, BLACK_FRONT_FULL_G1, BLACK_FRONT_FULL_H1, BLACK_FRONT_FULL_A2, BLACK_FRONT_FULL_B2, BLACK_FRONT_FULL_C2, BLACK_FRONT_FULL_D2, BLACK_FRONT_FULL_E2, BLACK_FRONT_FULL_F2, BLACK_FRONT_FULL_G2, BLACK_FRONT_FULL_H2, BLACK_FRONT_FULL_A3, BLACK_FRONT_FULL_B3, BLACK_FRONT_FULL_C3, BLACK_FRONT_FULL_D3, BLACK_FRONT_FULL_E3, BLACK_FRONT_FULL_F3, BLACK_FRONT_FULL_G3, BLACK_FRONT_FULL_H3, BLACK_FRONT_FULL_A4, BLACK_FRONT_FULL_B4, BLACK_FRONT_FULL_C4, BLACK_FRONT_FULL_D4, BLACK_FRONT_FULL_E4, BLACK_FRONT_FULL_F4, BLACK_FRONT_FULL_G4, BLACK_FRONT_FULL_H4, BLACK_FRONT_FULL_A5, BLACK_FRONT_FULL_B5, BLACK_FRONT_FULL_C5, BLACK_FRONT_FULL_D5, BLACK_FRONT_FULL_E5, BLACK_FRONT_FULL_F5, BLACK_FRONT_FULL_G5, BLACK_FRONT_FULL_H5, BLACK_FRONT_FULL_A6, BLACK_FRONT_FULL_B6, BLACK_FRONT_FULL_C6, BLACK_FRONT_FULL_D6, BLACK_FRONT_FULL_E6, BLACK_FRONT_FULL_F6, BLACK_FRONT_FULL_G6, BLACK_FRONT_FULL_H6, BLACK_FRONT_FULL_A7, BLACK_FRONT_FULL_B7, BLACK_FRONT_FULL_C7, BLACK_FRONT_FULL_D7, BLACK_FRONT_FULL_E7, BLACK_FRONT_FULL_F7, BLACK_FRONT_FULL_G7, BLACK_FRONT_FULL_H7, BLACK_FRONT_FULL_A8, BLACK_FRONT_FULL_B8, BLACK_FRONT_FULL_C8, BLACK_FRONT_FULL_D8, BLACK_FRONT_FULL_E8, BLACK_FRONT_FULL_F8, BLACK_FRONT_FULL_G8, BLACK_FRONT_FULL_H8, };
	private static final long WHITE_SUPPORT_A1 = B1;
	private static final long WHITE_SUPPORT_B1 = A1 | C1;
	private static final long WHITE_SUPPORT_C1 = B1 | D1;
	private static final long WHITE_SUPPORT_D1 = C1 | E1;
	private static final long WHITE_SUPPORT_E1 = D1 | F1;
	private static final long WHITE_SUPPORT_F1 = E1 | G1;
	private static final long WHITE_SUPPORT_G1 = F1 | H1;
	private static final long WHITE_SUPPORT_H1 = G1;
	private static final long WHITE_SUPPORT_A2 = B2 | B1;
	private static final long WHITE_SUPPORT_B2 = A2 | A1 | C2 | C1;
	private static final long WHITE_SUPPORT_C2 = B2 | B1 | D2 | D1;
	private static final long WHITE_SUPPORT_D2 = C2 | C1 | E2 | E1;
	private static final long WHITE_SUPPORT_E2 = D2 | D1 | F2 | F1;
	private static final long WHITE_SUPPORT_F2 = E2 | E1 | G2 | G1;
	private static final long WHITE_SUPPORT_G2 = F2 | F1 | H2 | H1;
	private static final long WHITE_SUPPORT_H2 = G2 | G1;
	private static final long WHITE_SUPPORT_A3 = B3 | B2;
	private static final long WHITE_SUPPORT_B3 = A3 | A2 | C3 | C2;
	private static final long WHITE_SUPPORT_C3 = B3 | B2 | D3 | D2;
	private static final long WHITE_SUPPORT_D3 = C3 | C2 | E3 | E2;
	private static final long WHITE_SUPPORT_E3 = D3 | D2 | F3 | F2;
	private static final long WHITE_SUPPORT_F3 = E3 | E2 | G3 | G2;
	private static final long WHITE_SUPPORT_G3 = F3 | F2 | H3 | H2;
	private static final long WHITE_SUPPORT_H3 = G3 | G2;
	private static final long WHITE_SUPPORT_A4 = B4 | B3;
	private static final long WHITE_SUPPORT_B4 = A4 | A3 | C4 | C3;
	private static final long WHITE_SUPPORT_C4 = B4 | B3 | D4 | D3;
	private static final long WHITE_SUPPORT_D4 = C4 | C3 | E4 | E3;
	private static final long WHITE_SUPPORT_E4 = D4 | D3 | F4 | F3;
	private static final long WHITE_SUPPORT_F4 = E4 | E3 | G4 | G3;
	private static final long WHITE_SUPPORT_G4 = F4 | F3 | H4 | H3;
	private static final long WHITE_SUPPORT_H4 = G4 | G3;
	private static final long WHITE_SUPPORT_A5 = B5 | B4;
	private static final long WHITE_SUPPORT_B5 = A5 | A4 | C5 | C4;
	private static final long WHITE_SUPPORT_C5 = B5 | B4 | D5 | D4;
	private static final long WHITE_SUPPORT_D5 = C5 | C4 | E5 | E4;
	private static final long WHITE_SUPPORT_E5 = D5 | D4 | F5 | F4;
	private static final long WHITE_SUPPORT_F5 = E5 | E4 | G5 | G4;
	private static final long WHITE_SUPPORT_G5 = F5 | F4 | H5 | H4;
	private static final long WHITE_SUPPORT_H5 = G5 | G4;
	private static final long WHITE_SUPPORT_A6 = B6 | B5;
	private static final long WHITE_SUPPORT_B6 = A6 | A5 | C6 | C5;
	private static final long WHITE_SUPPORT_C6 = B6 | B5 | D6 | D5;
	private static final long WHITE_SUPPORT_D6 = C6 | C5 | E6 | E5;
	private static final long WHITE_SUPPORT_E6 = D6 | D5 | F6 | F5;
	private static final long WHITE_SUPPORT_F6 = E6 | E5 | G6 | G5;
	private static final long WHITE_SUPPORT_G6 = F6 | F5 | H6 | H5;
	private static final long WHITE_SUPPORT_H6 = G6 | G5;
	private static final long WHITE_SUPPORT_A7 = B7 | B6;
	private static final long WHITE_SUPPORT_B7 = A7 | A6 | C7 | C6;
	private static final long WHITE_SUPPORT_C7 = B7 | B6 | D7 | D6;
	private static final long WHITE_SUPPORT_D7 = C7 | C6 | E7 | E6;
	private static final long WHITE_SUPPORT_E7 = D7 | D6 | F7 | F6;
	private static final long WHITE_SUPPORT_F7 = E7 | E6 | G7 | G6;
	private static final long WHITE_SUPPORT_G7 = F7 | F6 | H7 | H6;
	private static final long WHITE_SUPPORT_H7 = G7 | G6;
	private static final long WHITE_SUPPORT_A8 = B8 | B7;
	private static final long WHITE_SUPPORT_B8 = A8 | A7 | C8 | C7;
	private static final long WHITE_SUPPORT_C8 = B8 | B7 | D8 | D7;
	private static final long WHITE_SUPPORT_D8 = C8 | C7 | E8 | E7;
	private static final long WHITE_SUPPORT_E8 = D8 | D7 | F8 | F7;
	private static final long WHITE_SUPPORT_F8 = E8 | E7 | G8 | G7;
	private static final long WHITE_SUPPORT_G8 = F8 | F7 | H8 | H7;
	private static final long WHITE_SUPPORT_H8 = G8 | G7;
	private static final long[] WHITE_SUPPORT_ORDERED = new long[] {WHITE_SUPPORT_A1, WHITE_SUPPORT_B1, WHITE_SUPPORT_C1, WHITE_SUPPORT_D1, WHITE_SUPPORT_E1, WHITE_SUPPORT_F1, WHITE_SUPPORT_G1, WHITE_SUPPORT_H1, WHITE_SUPPORT_A2, WHITE_SUPPORT_B2, WHITE_SUPPORT_C2, WHITE_SUPPORT_D2, WHITE_SUPPORT_E2, WHITE_SUPPORT_F2, WHITE_SUPPORT_G2, WHITE_SUPPORT_H2, WHITE_SUPPORT_A3, WHITE_SUPPORT_B3, WHITE_SUPPORT_C3, WHITE_SUPPORT_D3, WHITE_SUPPORT_E3, WHITE_SUPPORT_F3, WHITE_SUPPORT_G3, WHITE_SUPPORT_H3, WHITE_SUPPORT_A4, WHITE_SUPPORT_B4, WHITE_SUPPORT_C4, WHITE_SUPPORT_D4, WHITE_SUPPORT_E4, WHITE_SUPPORT_F4, WHITE_SUPPORT_G4, WHITE_SUPPORT_H4, WHITE_SUPPORT_A5, WHITE_SUPPORT_B5, WHITE_SUPPORT_C5, WHITE_SUPPORT_D5, WHITE_SUPPORT_E5, WHITE_SUPPORT_F5, WHITE_SUPPORT_G5, WHITE_SUPPORT_H5, WHITE_SUPPORT_A6, WHITE_SUPPORT_B6, WHITE_SUPPORT_C6, WHITE_SUPPORT_D6, WHITE_SUPPORT_E6, WHITE_SUPPORT_F6, WHITE_SUPPORT_G6, WHITE_SUPPORT_H6, WHITE_SUPPORT_A7, WHITE_SUPPORT_B7, WHITE_SUPPORT_C7, WHITE_SUPPORT_D7, WHITE_SUPPORT_E7, WHITE_SUPPORT_F7, WHITE_SUPPORT_G7, WHITE_SUPPORT_H7, WHITE_SUPPORT_A8, WHITE_SUPPORT_B8, WHITE_SUPPORT_C8, WHITE_SUPPORT_D8, WHITE_SUPPORT_E8, WHITE_SUPPORT_F8, WHITE_SUPPORT_G8, WHITE_SUPPORT_H8, };
	private static final long BLACK_SUPPORT_A1 = B1 | B2;
	private static final long BLACK_SUPPORT_B1 = A1 | A2 | C1 | C2;
	private static final long BLACK_SUPPORT_C1 = B1 | B2 | D1 | D2;
	private static final long BLACK_SUPPORT_D1 = C1 | C2 | E1 | E2;
	private static final long BLACK_SUPPORT_E1 = D1 | D2 | F1 | F2;
	private static final long BLACK_SUPPORT_F1 = E1 | E2 | G1 | G2;
	private static final long BLACK_SUPPORT_G1 = F1 | F2 | H1 | H2;
	private static final long BLACK_SUPPORT_H1 = G1 | G2;
	private static final long BLACK_SUPPORT_A2 = B2 | B3;
	private static final long BLACK_SUPPORT_B2 = A2 | A3 | C2 | C3;
	private static final long BLACK_SUPPORT_C2 = B2 | B3 | D2 | D3;
	private static final long BLACK_SUPPORT_D2 = C2 | C3 | E2 | E3;
	private static final long BLACK_SUPPORT_E2 = D2 | D3 | F2 | F3;
	private static final long BLACK_SUPPORT_F2 = E2 | E3 | G2 | G3;
	private static final long BLACK_SUPPORT_G2 = F2 | F3 | H2 | H3;
	private static final long BLACK_SUPPORT_H2 = G2 | G3;
	private static final long BLACK_SUPPORT_A3 = B3 | B4;
	private static final long BLACK_SUPPORT_B3 = A3 | A4 | C3 | C4;
	private static final long BLACK_SUPPORT_C3 = B3 | B4 | D3 | D4;
	private static final long BLACK_SUPPORT_D3 = C3 | C4 | E3 | E4;
	private static final long BLACK_SUPPORT_E3 = D3 | D4 | F3 | F4;
	private static final long BLACK_SUPPORT_F3 = E3 | E4 | G3 | G4;
	private static final long BLACK_SUPPORT_G3 = F3 | F4 | H3 | H4;
	private static final long BLACK_SUPPORT_H3 = G3 | G4;
	private static final long BLACK_SUPPORT_A4 = B4 | B5;
	private static final long BLACK_SUPPORT_B4 = A4 | A5 | C4 | C5;
	private static final long BLACK_SUPPORT_C4 = B4 | B5 | D4 | D5;
	private static final long BLACK_SUPPORT_D4 = C4 | C5 | E4 | E5;
	private static final long BLACK_SUPPORT_E4 = D4 | D5 | F4 | F5;
	private static final long BLACK_SUPPORT_F4 = E4 | E5 | G4 | G5;
	private static final long BLACK_SUPPORT_G4 = F4 | F5 | H4 | H5;
	private static final long BLACK_SUPPORT_H4 = G4 | G5;
	private static final long BLACK_SUPPORT_A5 = B5 | B6;
	private static final long BLACK_SUPPORT_B5 = A5 | A6 | C5 | C6;
	private static final long BLACK_SUPPORT_C5 = B5 | B6 | D5 | D6;
	private static final long BLACK_SUPPORT_D5 = C5 | C6 | E5 | E6;
	private static final long BLACK_SUPPORT_E5 = D5 | D6 | F5 | F6;
	private static final long BLACK_SUPPORT_F5 = E5 | E6 | G5 | G6;
	private static final long BLACK_SUPPORT_G5 = F5 | F6 | H5 | H6;
	private static final long BLACK_SUPPORT_H5 = G5 | G6;
	private static final long BLACK_SUPPORT_A6 = B6 | B7;
	private static final long BLACK_SUPPORT_B6 = A6 | A7 | C6 | C7;
	private static final long BLACK_SUPPORT_C6 = B6 | B7 | D6 | D7;
	private static final long BLACK_SUPPORT_D6 = C6 | C7 | E6 | E7;
	private static final long BLACK_SUPPORT_E6 = D6 | D7 | F6 | F7;
	private static final long BLACK_SUPPORT_F6 = E6 | E7 | G6 | G7;
	private static final long BLACK_SUPPORT_G6 = F6 | F7 | H6 | H7;
	private static final long BLACK_SUPPORT_H6 = G6 | G7;
	private static final long BLACK_SUPPORT_A7 = B7 | B8;
	private static final long BLACK_SUPPORT_B7 = A7 | A8 | C7 | C8;
	private static final long BLACK_SUPPORT_C7 = B7 | B8 | D7 | D8;
	private static final long BLACK_SUPPORT_D7 = C7 | C8 | E7 | E8;
	private static final long BLACK_SUPPORT_E7 = D7 | D8 | F7 | F8;
	private static final long BLACK_SUPPORT_F7 = E7 | E8 | G7 | G8;
	private static final long BLACK_SUPPORT_G7 = F7 | F8 | H7 | H8;
	private static final long BLACK_SUPPORT_H7 = G7 | G8;
	private static final long BLACK_SUPPORT_A8 = B8;
	private static final long BLACK_SUPPORT_B8 = A8 | C8;
	private static final long BLACK_SUPPORT_C8 = B8 | D8;
	private static final long BLACK_SUPPORT_D8 = C8 | E8;
	private static final long BLACK_SUPPORT_E8 = D8 | F8;
	private static final long BLACK_SUPPORT_F8 = E8 | G8;
	private static final long BLACK_SUPPORT_G8 = F8 | H8;
	private static final long BLACK_SUPPORT_H8 = G8;
	private static final long[] BLACK_SUPPORT_ORDERED = new long[] {BLACK_SUPPORT_A1, BLACK_SUPPORT_B1, BLACK_SUPPORT_C1, BLACK_SUPPORT_D1, BLACK_SUPPORT_E1, BLACK_SUPPORT_F1, BLACK_SUPPORT_G1, BLACK_SUPPORT_H1, BLACK_SUPPORT_A2, BLACK_SUPPORT_B2, BLACK_SUPPORT_C2, BLACK_SUPPORT_D2, BLACK_SUPPORT_E2, BLACK_SUPPORT_F2, BLACK_SUPPORT_G2, BLACK_SUPPORT_H2, BLACK_SUPPORT_A3, BLACK_SUPPORT_B3, BLACK_SUPPORT_C3, BLACK_SUPPORT_D3, BLACK_SUPPORT_E3, BLACK_SUPPORT_F3, BLACK_SUPPORT_G3, BLACK_SUPPORT_H3, BLACK_SUPPORT_A4, BLACK_SUPPORT_B4, BLACK_SUPPORT_C4, BLACK_SUPPORT_D4, BLACK_SUPPORT_E4, BLACK_SUPPORT_F4, BLACK_SUPPORT_G4, BLACK_SUPPORT_H4, BLACK_SUPPORT_A5, BLACK_SUPPORT_B5, BLACK_SUPPORT_C5, BLACK_SUPPORT_D5, BLACK_SUPPORT_E5, BLACK_SUPPORT_F5, BLACK_SUPPORT_G5, BLACK_SUPPORT_H5, BLACK_SUPPORT_A6, BLACK_SUPPORT_B6, BLACK_SUPPORT_C6, BLACK_SUPPORT_D6, BLACK_SUPPORT_E6, BLACK_SUPPORT_F6, BLACK_SUPPORT_G6, BLACK_SUPPORT_H6, BLACK_SUPPORT_A7, BLACK_SUPPORT_B7, BLACK_SUPPORT_C7, BLACK_SUPPORT_D7, BLACK_SUPPORT_E7, BLACK_SUPPORT_F7, BLACK_SUPPORT_G7, BLACK_SUPPORT_H7, BLACK_SUPPORT_A8, BLACK_SUPPORT_B8, BLACK_SUPPORT_C8, BLACK_SUPPORT_D8, BLACK_SUPPORT_E8, BLACK_SUPPORT_F8, BLACK_SUPPORT_G8, BLACK_SUPPORT_H8, };
	private static final long WHITE_POSSIBLE_ATTACKS_A1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_B1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_C1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_D1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_E1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_F1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_G1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_H1 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_A2 = B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B2 = A3 | A4 | A5 | A6 | A7 | A8 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C2 = B3 | B4 | B5 | B6 | B7 | B8 | D3 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D2 = C3 | C4 | C5 | C6 | C7 | C8 | E3 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E2 = D3 | D4 | D5 | D6 | D7 | D8 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F2 = E3 | E4 | E5 | E6 | E7 | E8 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G2 = F3 | F4 | F5 | F6 | F7 | F8 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H2 = G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A3 = B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B3 = A4 | A5 | A6 | A7 | A8 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C3 = B4 | B5 | B6 | B7 | B8 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D3 = C4 | C5 | C6 | C7 | C8 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E3 = D4 | D5 | D6 | D7 | D8 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F3 = E4 | E5 | E6 | E7 | E8 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G3 = F4 | F5 | F6 | F7 | F8 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H3 = G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A4 = B5 | B6 | B7 | B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B4 = A5 | A6 | A7 | A8 | C5 | C6 | C7 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C4 = B5 | B6 | B7 | B8 | D5 | D6 | D7 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D4 = C5 | C6 | C7 | C8 | E5 | E6 | E7 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E4 = D5 | D6 | D7 | D8 | F5 | F6 | F7 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F4 = E5 | E6 | E7 | E8 | G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G4 = F5 | F6 | F7 | F8 | H5 | H6 | H7 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H4 = G5 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A5 = B6 | B7 | B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B5 = A6 | A7 | A8 | C6 | C7 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C5 = B6 | B7 | B8 | D6 | D7 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D5 = C6 | C7 | C8 | E6 | E7 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E5 = D6 | D7 | D8 | F6 | F7 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F5 = E6 | E7 | E8 | G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G5 = F6 | F7 | F8 | H6 | H7 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H5 = G6 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A6 = B7 | B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B6 = A7 | A8 | C7 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C6 = B7 | B8 | D7 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D6 = C7 | C8 | E7 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E6 = D7 | D8 | F7 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F6 = E7 | E8 | G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G6 = F7 | F8 | H7 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H6 = G7 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A7 = B8;
	private static final long WHITE_POSSIBLE_ATTACKS_B7 = A8 | C8;
	private static final long WHITE_POSSIBLE_ATTACKS_C7 = B8 | D8;
	private static final long WHITE_POSSIBLE_ATTACKS_D7 = C8 | E8;
	private static final long WHITE_POSSIBLE_ATTACKS_E7 = D8 | F8;
	private static final long WHITE_POSSIBLE_ATTACKS_F7 = E8 | G8;
	private static final long WHITE_POSSIBLE_ATTACKS_G7 = F8 | H8;
	private static final long WHITE_POSSIBLE_ATTACKS_H7 = G8;
	private static final long WHITE_POSSIBLE_ATTACKS_A8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_B8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_C8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_D8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_E8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_F8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_G8 = 0L;
	private static final long WHITE_POSSIBLE_ATTACKS_H8 = 0L;
	private static final long[] WHITE_POSSIBLE_ATTACKS_ORDERED = new long[] {WHITE_POSSIBLE_ATTACKS_A1, WHITE_POSSIBLE_ATTACKS_B1, WHITE_POSSIBLE_ATTACKS_C1, WHITE_POSSIBLE_ATTACKS_D1, WHITE_POSSIBLE_ATTACKS_E1, WHITE_POSSIBLE_ATTACKS_F1, WHITE_POSSIBLE_ATTACKS_G1, WHITE_POSSIBLE_ATTACKS_H1, WHITE_POSSIBLE_ATTACKS_A2, WHITE_POSSIBLE_ATTACKS_B2, WHITE_POSSIBLE_ATTACKS_C2, WHITE_POSSIBLE_ATTACKS_D2, WHITE_POSSIBLE_ATTACKS_E2, WHITE_POSSIBLE_ATTACKS_F2, WHITE_POSSIBLE_ATTACKS_G2, WHITE_POSSIBLE_ATTACKS_H2, WHITE_POSSIBLE_ATTACKS_A3, WHITE_POSSIBLE_ATTACKS_B3, WHITE_POSSIBLE_ATTACKS_C3, WHITE_POSSIBLE_ATTACKS_D3, WHITE_POSSIBLE_ATTACKS_E3, WHITE_POSSIBLE_ATTACKS_F3, WHITE_POSSIBLE_ATTACKS_G3, WHITE_POSSIBLE_ATTACKS_H3, WHITE_POSSIBLE_ATTACKS_A4, WHITE_POSSIBLE_ATTACKS_B4, WHITE_POSSIBLE_ATTACKS_C4, WHITE_POSSIBLE_ATTACKS_D4, WHITE_POSSIBLE_ATTACKS_E4, WHITE_POSSIBLE_ATTACKS_F4, WHITE_POSSIBLE_ATTACKS_G4, WHITE_POSSIBLE_ATTACKS_H4, WHITE_POSSIBLE_ATTACKS_A5, WHITE_POSSIBLE_ATTACKS_B5, WHITE_POSSIBLE_ATTACKS_C5, WHITE_POSSIBLE_ATTACKS_D5, WHITE_POSSIBLE_ATTACKS_E5, WHITE_POSSIBLE_ATTACKS_F5, WHITE_POSSIBLE_ATTACKS_G5, WHITE_POSSIBLE_ATTACKS_H5, WHITE_POSSIBLE_ATTACKS_A6, WHITE_POSSIBLE_ATTACKS_B6, WHITE_POSSIBLE_ATTACKS_C6, WHITE_POSSIBLE_ATTACKS_D6, WHITE_POSSIBLE_ATTACKS_E6, WHITE_POSSIBLE_ATTACKS_F6, WHITE_POSSIBLE_ATTACKS_G6, WHITE_POSSIBLE_ATTACKS_H6, WHITE_POSSIBLE_ATTACKS_A7, WHITE_POSSIBLE_ATTACKS_B7, WHITE_POSSIBLE_ATTACKS_C7, WHITE_POSSIBLE_ATTACKS_D7, WHITE_POSSIBLE_ATTACKS_E7, WHITE_POSSIBLE_ATTACKS_F7, WHITE_POSSIBLE_ATTACKS_G7, WHITE_POSSIBLE_ATTACKS_H7, WHITE_POSSIBLE_ATTACKS_A8, WHITE_POSSIBLE_ATTACKS_B8, WHITE_POSSIBLE_ATTACKS_C8, WHITE_POSSIBLE_ATTACKS_D8, WHITE_POSSIBLE_ATTACKS_E8, WHITE_POSSIBLE_ATTACKS_F8, WHITE_POSSIBLE_ATTACKS_G8, WHITE_POSSIBLE_ATTACKS_H8, };
	private static final long BLACK_POSSIBLE_ATTACKS_A1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_B1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_C1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_D1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_E1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_F1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_G1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_H1 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_A2 = B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B2 = A1 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C2 = B1 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D2 = C1 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E2 = D1 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F2 = E1 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G2 = F1 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H2 = G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A3 = B2 | B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B3 = A2 | A1 | C2 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C3 = B2 | B1 | D2 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D3 = C2 | C1 | E2 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E3 = D2 | D1 | F2 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F3 = E2 | E1 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G3 = F2 | F1 | H2 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H3 = G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A4 = B3 | B2 | B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B4 = A3 | A2 | A1 | C3 | C2 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C4 = B3 | B2 | B1 | D3 | D2 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D4 = C3 | C2 | C1 | E3 | E2 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E4 = D3 | D2 | D1 | F3 | F2 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F4 = E3 | E2 | E1 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G4 = F3 | F2 | F1 | H3 | H2 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H4 = G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A5 = B4 | B3 | B2 | B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B5 = A4 | A3 | A2 | A1 | C4 | C3 | C2 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C5 = B4 | B3 | B2 | B1 | D4 | D3 | D2 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D5 = C4 | C3 | C2 | C1 | E4 | E3 | E2 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E5 = D4 | D3 | D2 | D1 | F4 | F3 | F2 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F5 = E4 | E3 | E2 | E1 | G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G5 = F4 | F3 | F2 | F1 | H4 | H3 | H2 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H5 = G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A6 = B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B6 = A5 | A4 | A3 | A2 | A1 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C6 = B5 | B4 | B3 | B2 | B1 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D6 = C5 | C4 | C3 | C2 | C1 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E6 = D5 | D4 | D3 | D2 | D1 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F6 = E5 | E4 | E3 | E2 | E1 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G6 = F5 | F4 | F3 | F2 | F1 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H6 = G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A7 = B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_POSSIBLE_ATTACKS_B7 = A6 | A5 | A4 | A3 | A2 | A1 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_POSSIBLE_ATTACKS_C7 = B6 | B5 | B4 | B3 | B2 | B1 | D6 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_POSSIBLE_ATTACKS_D7 = C6 | C5 | C4 | C3 | C2 | C1 | E6 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_POSSIBLE_ATTACKS_E7 = D6 | D5 | D4 | D3 | D2 | D1 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_POSSIBLE_ATTACKS_F7 = E6 | E5 | E4 | E3 | E2 | E1 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_G7 = F6 | F5 | F4 | F3 | F2 | F1 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_POSSIBLE_ATTACKS_H7 = G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_POSSIBLE_ATTACKS_A8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_B8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_C8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_D8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_E8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_F8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_G8 = 0L;
	private static final long BLACK_POSSIBLE_ATTACKS_H8 = 0L;
	private static final long[] BLACK_POSSIBLE_ATTACKS_ORDERED = new long[] {BLACK_POSSIBLE_ATTACKS_A1, BLACK_POSSIBLE_ATTACKS_B1, BLACK_POSSIBLE_ATTACKS_C1, BLACK_POSSIBLE_ATTACKS_D1, BLACK_POSSIBLE_ATTACKS_E1, BLACK_POSSIBLE_ATTACKS_F1, BLACK_POSSIBLE_ATTACKS_G1, BLACK_POSSIBLE_ATTACKS_H1, BLACK_POSSIBLE_ATTACKS_A2, BLACK_POSSIBLE_ATTACKS_B2, BLACK_POSSIBLE_ATTACKS_C2, BLACK_POSSIBLE_ATTACKS_D2, BLACK_POSSIBLE_ATTACKS_E2, BLACK_POSSIBLE_ATTACKS_F2, BLACK_POSSIBLE_ATTACKS_G2, BLACK_POSSIBLE_ATTACKS_H2, BLACK_POSSIBLE_ATTACKS_A3, BLACK_POSSIBLE_ATTACKS_B3, BLACK_POSSIBLE_ATTACKS_C3, BLACK_POSSIBLE_ATTACKS_D3, BLACK_POSSIBLE_ATTACKS_E3, BLACK_POSSIBLE_ATTACKS_F3, BLACK_POSSIBLE_ATTACKS_G3, BLACK_POSSIBLE_ATTACKS_H3, BLACK_POSSIBLE_ATTACKS_A4, BLACK_POSSIBLE_ATTACKS_B4, BLACK_POSSIBLE_ATTACKS_C4, BLACK_POSSIBLE_ATTACKS_D4, BLACK_POSSIBLE_ATTACKS_E4, BLACK_POSSIBLE_ATTACKS_F4, BLACK_POSSIBLE_ATTACKS_G4, BLACK_POSSIBLE_ATTACKS_H4, BLACK_POSSIBLE_ATTACKS_A5, BLACK_POSSIBLE_ATTACKS_B5, BLACK_POSSIBLE_ATTACKS_C5, BLACK_POSSIBLE_ATTACKS_D5, BLACK_POSSIBLE_ATTACKS_E5, BLACK_POSSIBLE_ATTACKS_F5, BLACK_POSSIBLE_ATTACKS_G5, BLACK_POSSIBLE_ATTACKS_H5, BLACK_POSSIBLE_ATTACKS_A6, BLACK_POSSIBLE_ATTACKS_B6, BLACK_POSSIBLE_ATTACKS_C6, BLACK_POSSIBLE_ATTACKS_D6, BLACK_POSSIBLE_ATTACKS_E6, BLACK_POSSIBLE_ATTACKS_F6, BLACK_POSSIBLE_ATTACKS_G6, BLACK_POSSIBLE_ATTACKS_H6, BLACK_POSSIBLE_ATTACKS_A7, BLACK_POSSIBLE_ATTACKS_B7, BLACK_POSSIBLE_ATTACKS_C7, BLACK_POSSIBLE_ATTACKS_D7, BLACK_POSSIBLE_ATTACKS_E7, BLACK_POSSIBLE_ATTACKS_F7, BLACK_POSSIBLE_ATTACKS_G7, BLACK_POSSIBLE_ATTACKS_H7, BLACK_POSSIBLE_ATTACKS_A8, BLACK_POSSIBLE_ATTACKS_B8, BLACK_POSSIBLE_ATTACKS_C8, BLACK_POSSIBLE_ATTACKS_D8, BLACK_POSSIBLE_ATTACKS_E8, BLACK_POSSIBLE_ATTACKS_F8, BLACK_POSSIBLE_ATTACKS_G8, BLACK_POSSIBLE_ATTACKS_H8, };

	private static final long WHITE_PASSER_PARAM_A1 = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSER_PARAM_B1 = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C1 = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D1 = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_E1 = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_F1 = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_G1 = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_H1 = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_A2 = A2 | A3 | A4 | A5 | A6 | A7 | A8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSER_PARAM_B2 = B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C2 = C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D2 = D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_E2 = E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_F2 = F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_G2 = G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_H2 = H2 | H3 | H4 | H5 | H6 | H7 | H8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_A3 = A3 | A4 | A5 | A6 | A7 | A8 | B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSER_PARAM_B3 = B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C3 = C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D3 = D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_E3 = E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_F3 = F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_G3 = G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | F3 | F4 | F5 | F6 | F7 | F8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_H3 = H3 | H4 | H5 | H6 | H7 | H8 | G3 | G4 | G5 | G6 | G7 | G8 | F3 | F4 | F5 | F6 | F7 | F8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_A4 = A4 | A5 | A6 | A7 | A8 | B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSER_PARAM_B4 = B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C4 = C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D4 = D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_E4 = E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_F4 = F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_G4 = G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | F4 | F5 | F6 | F7 | F8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_H4 = H4 | H5 | H6 | H7 | H8 | G4 | G5 | G6 | G7 | G8 | F4 | F5 | F6 | F7 | F8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_PARAM_A5 = A5 | A6 | A7 | A8 | B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_PARAM_B5 = B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C5 = C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | B5 | B6 | B7 | B8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D5 = D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | C5 | C6 | C7 | C8 | B5 | B6 | B7 | B8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_E5 = E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | D5 | D6 | D7 | D8 | C5 | C6 | C7 | C8 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_F5 = F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | E5 | E6 | E7 | E8 | D5 | D6 | D7 | D8 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_G5 = G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | F5 | F6 | F7 | F8 | E5 | E6 | E7 | E8 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_PARAM_H5 = H5 | H6 | H7 | H8 | G5 | G6 | G7 | G8 | F5 | F6 | F7 | F8 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSER_PARAM_A6 = A6 | A7 | A8 | B6 | B7 | B8 | C6 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_B6 = B6 | B7 | B8 | C6 | C7 | C8 | D6 | D7 | D8 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C6 = C6 | C7 | C8 | D6 | D7 | D8 | E6 | E7 | E8 | B6 | B7 | B8 | A6 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_D6 = D6 | D7 | D8 | E6 | E7 | E8 | F6 | F7 | F8 | C6 | C7 | C8 | B6 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_E6 = E6 | E7 | E8 | F6 | F7 | F8 | G6 | G7 | G8 | D6 | D7 | D8 | C6 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_F6 = F6 | F7 | F8 | G6 | G7 | G8 | H6 | H7 | H8 | E6 | E7 | E8 | D6 | D7 | D8;
	private static final long WHITE_PASSER_PARAM_G6 = G6 | G7 | G8 | H6 | H7 | H8 | F6 | F7 | F8 | E6 | E7 | E8;
	private static final long WHITE_PASSER_PARAM_H6 = H6 | H7 | H8 | G6 | G7 | G8 | F6 | F7 | F8;
	private static final long WHITE_PASSER_PARAM_A7 = A7 | A8 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_B7 = B7 | B8 | C7 | C8 | A7 | A8;
	private static final long WHITE_PASSER_PARAM_C7 = C7 | C8 | D7 | D8 | B7 | B8;
	private static final long WHITE_PASSER_PARAM_D7 = D7 | D8 | E7 | E8 | C7 | C8;
	private static final long WHITE_PASSER_PARAM_E7 = E7 | E8 | F7 | F8 | D7 | D8;
	private static final long WHITE_PASSER_PARAM_F7 = F7 | F8 | G7 | G8 | E7 | E8;
	private static final long WHITE_PASSER_PARAM_G7 = G7 | G8 | H7 | H8 | F7 | F8;
	private static final long WHITE_PASSER_PARAM_H7 = H7 | H8 | G7 | G8;
	private static final long WHITE_PASSER_PARAM_A8 = A8;
	private static final long WHITE_PASSER_PARAM_B8 = B8;
	private static final long WHITE_PASSER_PARAM_C8 = C8;
	private static final long WHITE_PASSER_PARAM_D8 = D8;
	private static final long WHITE_PASSER_PARAM_E8 = E8;
	private static final long WHITE_PASSER_PARAM_F8 = F8;
	private static final long WHITE_PASSER_PARAM_G8 = G8;
	private static final long WHITE_PASSER_PARAM_H8 = H8;
	
	private static final long[] WHITE_PASSER_PARAM_ORDERED = new long[] {WHITE_PASSER_PARAM_A1, WHITE_PASSER_PARAM_B1, WHITE_PASSER_PARAM_C1, WHITE_PASSER_PARAM_D1, WHITE_PASSER_PARAM_E1, WHITE_PASSER_PARAM_F1, WHITE_PASSER_PARAM_G1, WHITE_PASSER_PARAM_H1, WHITE_PASSER_PARAM_A2, WHITE_PASSER_PARAM_B2, WHITE_PASSER_PARAM_C2, WHITE_PASSER_PARAM_D2, WHITE_PASSER_PARAM_E2, WHITE_PASSER_PARAM_F2, WHITE_PASSER_PARAM_G2, WHITE_PASSER_PARAM_H2, WHITE_PASSER_PARAM_A3, WHITE_PASSER_PARAM_B3, WHITE_PASSER_PARAM_C3, WHITE_PASSER_PARAM_D3, WHITE_PASSER_PARAM_E3, WHITE_PASSER_PARAM_F3, WHITE_PASSER_PARAM_G3, WHITE_PASSER_PARAM_H3, WHITE_PASSER_PARAM_A4, WHITE_PASSER_PARAM_B4, WHITE_PASSER_PARAM_C4, WHITE_PASSER_PARAM_D4, WHITE_PASSER_PARAM_E4, WHITE_PASSER_PARAM_F4, WHITE_PASSER_PARAM_G4, WHITE_PASSER_PARAM_H4, WHITE_PASSER_PARAM_A5, WHITE_PASSER_PARAM_B5, WHITE_PASSER_PARAM_C5, WHITE_PASSER_PARAM_D5, WHITE_PASSER_PARAM_E5, WHITE_PASSER_PARAM_F5, WHITE_PASSER_PARAM_G5, WHITE_PASSER_PARAM_H5, WHITE_PASSER_PARAM_A6, WHITE_PASSER_PARAM_B6, WHITE_PASSER_PARAM_C6, WHITE_PASSER_PARAM_D6, WHITE_PASSER_PARAM_E6, WHITE_PASSER_PARAM_F6, WHITE_PASSER_PARAM_G6, WHITE_PASSER_PARAM_H6, WHITE_PASSER_PARAM_A7, WHITE_PASSER_PARAM_B7, WHITE_PASSER_PARAM_C7, WHITE_PASSER_PARAM_D7, WHITE_PASSER_PARAM_E7, WHITE_PASSER_PARAM_F7, WHITE_PASSER_PARAM_G7, WHITE_PASSER_PARAM_H7, WHITE_PASSER_PARAM_A8, WHITE_PASSER_PARAM_B8, WHITE_PASSER_PARAM_C8, WHITE_PASSER_PARAM_D8, WHITE_PASSER_PARAM_E8, WHITE_PASSER_PARAM_F8, WHITE_PASSER_PARAM_G8, WHITE_PASSER_PARAM_H8, };

	private static final long BLACK_PASSER_PARAM_A1 = A1;
	private static final long BLACK_PASSER_PARAM_B1 = B1;
	private static final long BLACK_PASSER_PARAM_C1 = C1;
	private static final long BLACK_PASSER_PARAM_D1 = D1;
	private static final long BLACK_PASSER_PARAM_E1 = E1;
	private static final long BLACK_PASSER_PARAM_F1 = F1;
	private static final long BLACK_PASSER_PARAM_G1 = G1;
	private static final long BLACK_PASSER_PARAM_H1 = H1;
	private static final long BLACK_PASSER_PARAM_A2 = A2 | A1 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_B2 = B2 | B1 | C2 | C1 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C2 = C2 | C1 | D2 | D1 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_D2 = D2 | D1 | E2 | E1 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_E2 = E2 | E1 | F2 | F1 | D2 | D1;
	private static final long BLACK_PASSER_PARAM_F2 = F2 | F1 | G2 | G1 | E2 | E1;
	private static final long BLACK_PASSER_PARAM_G2 = G2 | G1 | H2 | H1 | F2 | F1;
	private static final long BLACK_PASSER_PARAM_H2 = H2 | H1 | G2 | G1;
	private static final long BLACK_PASSER_PARAM_A3 = A3 | A2 | A1 | B3 | B2 | B1 | C3 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_B3 = B3 | B2 | B1 | C3 | C2 | C1 | D3 | D2 | D1 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C3 = C3 | C2 | C1 | D3 | D2 | D1 | E3 | E2 | E1 | B3 | B2 | B1 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D3 = D3 | D2 | D1 | E3 | E2 | E1 | F3 | F2 | F1 | C3 | C2 | C1 | B3 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_E3 = E3 | E2 | E1 | F3 | F2 | F1 | G3 | G2 | G1 | D3 | D2 | D1 | C3 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_F3 = F3 | F2 | F1 | G3 | G2 | G1 | H3 | H2 | H1 | E3 | E2 | E1 | D3 | D2 | D1;
	private static final long BLACK_PASSER_PARAM_G3 = G3 | G2 | G1 | H3 | H2 | H1 | F3 | F2 | F1 | E3 | E2 | E1;
	private static final long BLACK_PASSER_PARAM_H3 = H3 | H2 | H1 | G3 | G2 | G1 | F3 | F2 | F1;
	private static final long BLACK_PASSER_PARAM_A4 = A4 | A3 | A2 | A1 | B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_PARAM_B4 = B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C4 = C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | B4 | B3 | B2 | B1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D4 = D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | C4 | C3 | C2 | C1 | B4 | B3 | B2 | B1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_E4 = E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | D4 | D3 | D2 | D1 | C4 | C3 | C2 | C1 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_F4 = F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | E4 | E3 | E2 | E1 | D4 | D3 | D2 | D1 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_G4 = G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | F4 | F3 | F2 | F1 | E4 | E3 | E2 | E1 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_PARAM_H4 = H4 | H3 | H2 | H1 | G4 | G3 | G2 | G1 | F4 | F3 | F2 | F1 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSER_PARAM_A5 = A5 | A4 | A3 | A2 | A1 | B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSER_PARAM_B5 = B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C5 = C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D5 = D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_E5 = E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_F5 = F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_G5 = G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | F5 | F4 | F3 | F2 | F1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_H5 = H5 | H4 | H3 | H2 | H1 | G5 | G4 | G3 | G2 | G1 | F5 | F4 | F3 | F2 | F1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_PARAM_A6 = A6 | A5 | A4 | A3 | A2 | A1 | B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSER_PARAM_B6 = B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C6 = C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D6 = D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_E6 = E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_F6 = F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_G6 = G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | F6 | F5 | F4 | F3 | F2 | F1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_H6 = H6 | H5 | H4 | H3 | H2 | H1 | G6 | G5 | G4 | G3 | G2 | G1 | F6 | F5 | F4 | F3 | F2 | F1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_PARAM_A7 = A7 | A6 | A5 | A4 | A3 | A2 | A1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSER_PARAM_B7 = B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C7 = C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D7 = D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_E7 = E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_F7 = F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_G7 = G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_H7 = H7 | H6 | H5 | H4 | H3 | H2 | H1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_PARAM_A8 = A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSER_PARAM_B8 = B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_C8 = C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_D8 = D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_E8 = E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_F8 = F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_G8 = G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_PARAM_H8 = H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	
	private static final long[] BLACK_PASSER_PARAM_ORDERED = new long[] {BLACK_PASSER_PARAM_A1, BLACK_PASSER_PARAM_B1, BLACK_PASSER_PARAM_C1, BLACK_PASSER_PARAM_D1, BLACK_PASSER_PARAM_E1, BLACK_PASSER_PARAM_F1, BLACK_PASSER_PARAM_G1, BLACK_PASSER_PARAM_H1, BLACK_PASSER_PARAM_A2, BLACK_PASSER_PARAM_B2, BLACK_PASSER_PARAM_C2, BLACK_PASSER_PARAM_D2, BLACK_PASSER_PARAM_E2, BLACK_PASSER_PARAM_F2, BLACK_PASSER_PARAM_G2, BLACK_PASSER_PARAM_H2, BLACK_PASSER_PARAM_A3, BLACK_PASSER_PARAM_B3, BLACK_PASSER_PARAM_C3, BLACK_PASSER_PARAM_D3, BLACK_PASSER_PARAM_E3, BLACK_PASSER_PARAM_F3, BLACK_PASSER_PARAM_G3, BLACK_PASSER_PARAM_H3, BLACK_PASSER_PARAM_A4, BLACK_PASSER_PARAM_B4, BLACK_PASSER_PARAM_C4, BLACK_PASSER_PARAM_D4, BLACK_PASSER_PARAM_E4, BLACK_PASSER_PARAM_F4, BLACK_PASSER_PARAM_G4, BLACK_PASSER_PARAM_H4, BLACK_PASSER_PARAM_A5, BLACK_PASSER_PARAM_B5, BLACK_PASSER_PARAM_C5, BLACK_PASSER_PARAM_D5, BLACK_PASSER_PARAM_E5, BLACK_PASSER_PARAM_F5, BLACK_PASSER_PARAM_G5, BLACK_PASSER_PARAM_H5, BLACK_PASSER_PARAM_A6, BLACK_PASSER_PARAM_B6, BLACK_PASSER_PARAM_C6, BLACK_PASSER_PARAM_D6, BLACK_PASSER_PARAM_E6, BLACK_PASSER_PARAM_F6, BLACK_PASSER_PARAM_G6, BLACK_PASSER_PARAM_H6, BLACK_PASSER_PARAM_A7, BLACK_PASSER_PARAM_B7, BLACK_PASSER_PARAM_C7, BLACK_PASSER_PARAM_D7, BLACK_PASSER_PARAM_E7, BLACK_PASSER_PARAM_F7, BLACK_PASSER_PARAM_G7, BLACK_PASSER_PARAM_H7, BLACK_PASSER_PARAM_A8, BLACK_PASSER_PARAM_B8, BLACK_PASSER_PARAM_C8, BLACK_PASSER_PARAM_D8, BLACK_PASSER_PARAM_E8, BLACK_PASSER_PARAM_F8, BLACK_PASSER_PARAM_G8, BLACK_PASSER_PARAM_H8, };
	
	private static final long WHITE_PASSER_EXT_PARAM_A1 = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSER_EXT_PARAM_B1 = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C1 = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D1 = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E1 = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_F1 = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_G1 = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_H1 = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_A2 = A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8;
	private static final long WHITE_PASSER_EXT_PARAM_B2 = B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C2 = C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D2 = D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E2 = E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_F2 = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_G2 = G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_H2 = H1 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D1 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B1 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A1 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_A3 = A2 | A3 | A4 | A5 | A6 | A7 | A8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8;
	private static final long WHITE_PASSER_EXT_PARAM_B3 = B2 | B3 | B4 | B5 | B6 | B7 | B8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C3 = C2 | C3 | C4 | C5 | C6 | C7 | C8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D3 = D2 | D3 | D4 | D5 | D6 | D7 | D8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E3 = E2 | E3 | E4 | E5 | E6 | E7 | E8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_F3 = F2 | F3 | F4 | F5 | F6 | F7 | F8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_G3 = G2 | G3 | G4 | G5 | G6 | G7 | G8 | H2 | H3 | H4 | H5 | H6 | H7 | H8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8 | A2 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_H3 = H2 | H3 | H4 | H5 | H6 | H7 | H8 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | D2 | D3 | D4 | D5 | D6 | D7 | D8 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | B2 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_A4 = A3 | A4 | A5 | A6 | A7 | A8 | B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8;
	private static final long WHITE_PASSER_EXT_PARAM_B4 = B3 | B4 | B5 | B6 | B7 | B8 | C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C4 = C3 | C4 | C5 | C6 | C7 | C8 | D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D4 = D3 | D4 | D5 | D6 | D7 | D8 | E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E4 = E3 | E4 | E5 | E6 | E7 | E8 | F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_F4 = F3 | F4 | F5 | F6 | F7 | F8 | G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8 | A3 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_G4 = G3 | G4 | G5 | G6 | G7 | G8 | H3 | H4 | H5 | H6 | H7 | H8 | F3 | F4 | F5 | F6 | F7 | F8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8 | B3 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_H4 = H3 | H4 | H5 | H6 | H7 | H8 | G3 | G4 | G5 | G6 | G7 | G8 | F3 | F4 | F5 | F6 | F7 | F8 | E3 | E4 | E5 | E6 | E7 | E8 | D3 | D4 | D5 | D6 | D7 | D8 | C3 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_A5 = A4 | A5 | A6 | A7 | A8 | B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSER_EXT_PARAM_B5 = B4 | B5 | B6 | B7 | B8 | C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C5 = C4 | C5 | C6 | C7 | C8 | D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D5 = D4 | D5 | D6 | D7 | D8 | E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E5 = E4 | E5 | E6 | E7 | E8 | F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8 | A4 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_F5 = F4 | F5 | F6 | F7 | F8 | G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8 | B4 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_G5 = G4 | G5 | G6 | G7 | G8 | H4 | H5 | H6 | H7 | H8 | F4 | F5 | F6 | F7 | F8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8 | C4 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_H5 = H4 | H5 | H6 | H7 | H8 | G4 | G5 | G6 | G7 | G8 | F4 | F5 | F6 | F7 | F8 | E4 | E5 | E6 | E7 | E8 | D4 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_EXT_PARAM_A6 = A5 | A6 | A7 | A8 | B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_EXT_PARAM_B6 = B5 | B6 | B7 | B8 | C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C6 = C5 | C6 | C7 | C8 | D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | B5 | B6 | B7 | B8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D6 = D5 | D6 | D7 | D8 | E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | C5 | C6 | C7 | C8 | B5 | B6 | B7 | B8 | A5 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_E6 = E5 | E6 | E7 | E8 | F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | D5 | D6 | D7 | D8 | C5 | C6 | C7 | C8 | B5 | B6 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_F6 = F5 | F6 | F7 | F8 | G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | E5 | E6 | E7 | E8 | D5 | D6 | D7 | D8 | C5 | C6 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_G6 = G5 | G6 | G7 | G8 | H5 | H6 | H7 | H8 | F5 | F6 | F7 | F8 | E5 | E6 | E7 | E8 | D5 | D6 | D7 | D8;
	private static final long WHITE_PASSER_EXT_PARAM_H6 = H5 | H6 | H7 | H8 | G5 | G6 | G7 | G8 | F5 | F6 | F7 | F8 | E5 | E6 | E7 | E8;
	private static final long WHITE_PASSER_EXT_PARAM_A7 = A6 | A7 | A8 | B6 | B7 | B8 | C6 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_B7 = B6 | B7 | B8 | C6 | C7 | C8 | D6 | D7 | D8 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C7 = C6 | C7 | C8 | D6 | D7 | D8 | E6 | E7 | E8 | B6 | B7 | B8 | A6 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_D7 = D6 | D7 | D8 | E6 | E7 | E8 | F6 | F7 | F8 | C6 | C7 | C8 | B6 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_E7 = E6 | E7 | E8 | F6 | F7 | F8 | G6 | G7 | G8 | D6 | D7 | D8 | C6 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_F7 = F6 | F7 | F8 | G6 | G7 | G8 | H6 | H7 | H8 | E6 | E7 | E8 | D6 | D7 | D8;
	private static final long WHITE_PASSER_EXT_PARAM_G7 = G6 | G7 | G8 | H6 | H7 | H8 | F6 | F7 | F8 | E6 | E7 | E8;
	private static final long WHITE_PASSER_EXT_PARAM_H7 = H6 | H7 | H8 | G6 | G7 | G8 | F6 | F7 | F8;
	private static final long WHITE_PASSER_EXT_PARAM_A8 = A7 | A8 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_B8 = B7 | B8 | C7 | C8 | A7 | A8;
	private static final long WHITE_PASSER_EXT_PARAM_C8 = C7 | C8 | D7 | D8 | B7 | B8;
	private static final long WHITE_PASSER_EXT_PARAM_D8 = D7 | D8 | E7 | E8 | C7 | C8;
	private static final long WHITE_PASSER_EXT_PARAM_E8 = E7 | E8 | F7 | F8 | D7 | D8;
	private static final long WHITE_PASSER_EXT_PARAM_F8 = F7 | F8 | G7 | G8 | E7 | E8;
	private static final long WHITE_PASSER_EXT_PARAM_G8 = G7 | G8 | H7 | H8 | F7 | F8;
	private static final long WHITE_PASSER_EXT_PARAM_H8 = H7 | H8 | G7 | G8;
	private static final long[] WHITE_PASSER_EXT_PARAM_ORDERED = new long[] {WHITE_PASSER_EXT_PARAM_A1, WHITE_PASSER_EXT_PARAM_B1, WHITE_PASSER_EXT_PARAM_C1, WHITE_PASSER_EXT_PARAM_D1, WHITE_PASSER_EXT_PARAM_E1, WHITE_PASSER_EXT_PARAM_F1, WHITE_PASSER_EXT_PARAM_G1, WHITE_PASSER_EXT_PARAM_H1, WHITE_PASSER_EXT_PARAM_A2, WHITE_PASSER_EXT_PARAM_B2, WHITE_PASSER_EXT_PARAM_C2, WHITE_PASSER_EXT_PARAM_D2, WHITE_PASSER_EXT_PARAM_E2, WHITE_PASSER_EXT_PARAM_F2, WHITE_PASSER_EXT_PARAM_G2, WHITE_PASSER_EXT_PARAM_H2, WHITE_PASSER_EXT_PARAM_A3, WHITE_PASSER_EXT_PARAM_B3, WHITE_PASSER_EXT_PARAM_C3, WHITE_PASSER_EXT_PARAM_D3, WHITE_PASSER_EXT_PARAM_E3, WHITE_PASSER_EXT_PARAM_F3, WHITE_PASSER_EXT_PARAM_G3, WHITE_PASSER_EXT_PARAM_H3, WHITE_PASSER_EXT_PARAM_A4, WHITE_PASSER_EXT_PARAM_B4, WHITE_PASSER_EXT_PARAM_C4, WHITE_PASSER_EXT_PARAM_D4, WHITE_PASSER_EXT_PARAM_E4, WHITE_PASSER_EXT_PARAM_F4, WHITE_PASSER_EXT_PARAM_G4, WHITE_PASSER_EXT_PARAM_H4, WHITE_PASSER_EXT_PARAM_A5, WHITE_PASSER_EXT_PARAM_B5, WHITE_PASSER_EXT_PARAM_C5, WHITE_PASSER_EXT_PARAM_D5, WHITE_PASSER_EXT_PARAM_E5, WHITE_PASSER_EXT_PARAM_F5, WHITE_PASSER_EXT_PARAM_G5, WHITE_PASSER_EXT_PARAM_H5, WHITE_PASSER_EXT_PARAM_A6, WHITE_PASSER_EXT_PARAM_B6, WHITE_PASSER_EXT_PARAM_C6, WHITE_PASSER_EXT_PARAM_D6, WHITE_PASSER_EXT_PARAM_E6, WHITE_PASSER_EXT_PARAM_F6, WHITE_PASSER_EXT_PARAM_G6, WHITE_PASSER_EXT_PARAM_H6, WHITE_PASSER_EXT_PARAM_A7, WHITE_PASSER_EXT_PARAM_B7, WHITE_PASSER_EXT_PARAM_C7, WHITE_PASSER_EXT_PARAM_D7, WHITE_PASSER_EXT_PARAM_E7, WHITE_PASSER_EXT_PARAM_F7, WHITE_PASSER_EXT_PARAM_G7, WHITE_PASSER_EXT_PARAM_H7, WHITE_PASSER_EXT_PARAM_A8, WHITE_PASSER_EXT_PARAM_B8, WHITE_PASSER_EXT_PARAM_C8, WHITE_PASSER_EXT_PARAM_D8, WHITE_PASSER_EXT_PARAM_E8, WHITE_PASSER_EXT_PARAM_F8, WHITE_PASSER_EXT_PARAM_G8, WHITE_PASSER_EXT_PARAM_H8, };
	private static final long BLACK_PASSER_EXT_PARAM_A1 = A2 | A1 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_B1 = B2 | B1 | C2 | C1 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C1 = C2 | C1 | D2 | D1 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_D1 = D2 | D1 | E2 | E1 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_E1 = E2 | E1 | F2 | F1 | D2 | D1;
	private static final long BLACK_PASSER_EXT_PARAM_F1 = F2 | F1 | G2 | G1 | E2 | E1;
	private static final long BLACK_PASSER_EXT_PARAM_G1 = G2 | G1 | H2 | H1 | F2 | F1;
	private static final long BLACK_PASSER_EXT_PARAM_H1 = H2 | H1 | G2 | G1;
	private static final long BLACK_PASSER_EXT_PARAM_A2 = A3 | A2 | A1 | B3 | B2 | B1 | C3 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_B2 = B3 | B2 | B1 | C3 | C2 | C1 | D3 | D2 | D1 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C2 = C3 | C2 | C1 | D3 | D2 | D1 | E3 | E2 | E1 | B3 | B2 | B1 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D2 = D3 | D2 | D1 | E3 | E2 | E1 | F3 | F2 | F1 | C3 | C2 | C1 | B3 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_E2 = E3 | E2 | E1 | F3 | F2 | F1 | G3 | G2 | G1 | D3 | D2 | D1 | C3 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_F2 = F3 | F2 | F1 | G3 | G2 | G1 | H3 | H2 | H1 | E3 | E2 | E1 | D3 | D2 | D1;
	private static final long BLACK_PASSER_EXT_PARAM_G2 = G3 | G2 | G1 | H3 | H2 | H1 | F3 | F2 | F1 | E3 | E2 | E1;
	private static final long BLACK_PASSER_EXT_PARAM_H2 = H3 | H2 | H1 | G3 | G2 | G1 | F3 | F2 | F1;
	private static final long BLACK_PASSER_EXT_PARAM_A3 = A4 | A3 | A2 | A1 | B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_EXT_PARAM_B3 = B4 | B3 | B2 | B1 | C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C3 = C4 | C3 | C2 | C1 | D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | B4 | B3 | B2 | B1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D3 = D4 | D3 | D2 | D1 | E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | C4 | C3 | C2 | C1 | B4 | B3 | B2 | B1 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E3 = E4 | E3 | E2 | E1 | F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | D4 | D3 | D2 | D1 | C4 | C3 | C2 | C1 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_F3 = F4 | F3 | F2 | F1 | G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | E4 | E3 | E2 | E1 | D4 | D3 | D2 | D1 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_G3 = G4 | G3 | G2 | G1 | H4 | H3 | H2 | H1 | F4 | F3 | F2 | F1 | E4 | E3 | E2 | E1 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_EXT_PARAM_H3 = H4 | H3 | H2 | H1 | G4 | G3 | G2 | G1 | F4 | F3 | F2 | F1 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSER_EXT_PARAM_A4 = A5 | A4 | A3 | A2 | A1 | B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1;
	private static final long BLACK_PASSER_EXT_PARAM_B4 = B5 | B4 | B3 | B2 | B1 | C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C4 = C5 | C4 | C3 | C2 | C1 | D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D4 = D5 | D4 | D3 | D2 | D1 | E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E4 = E5 | E4 | E3 | E2 | E1 | F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_F4 = F5 | F4 | F3 | F2 | F1 | G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_G4 = G5 | G4 | G3 | G2 | G1 | H5 | H4 | H3 | H2 | H1 | F5 | F4 | F3 | F2 | F1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_H4 = H5 | H4 | H3 | H2 | H1 | G5 | G4 | G3 | G2 | G1 | F5 | F4 | F3 | F2 | F1 | E5 | E4 | E3 | E2 | E1 | D5 | D4 | D3 | D2 | D1;
	private static final long BLACK_PASSER_EXT_PARAM_A5 = A6 | A5 | A4 | A3 | A2 | A1 | B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1;
	private static final long BLACK_PASSER_EXT_PARAM_B5 = B6 | B5 | B4 | B3 | B2 | B1 | C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C5 = C6 | C5 | C4 | C3 | C2 | C1 | D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D5 = D6 | D5 | D4 | D3 | D2 | D1 | E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E5 = E6 | E5 | E4 | E3 | E2 | E1 | F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_F5 = F6 | F5 | F4 | F3 | F2 | F1 | G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_G5 = G6 | G5 | G4 | G3 | G2 | G1 | H6 | H5 | H4 | H3 | H2 | H1 | F6 | F5 | F4 | F3 | F2 | F1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_H5 = H6 | H5 | H4 | H3 | H2 | H1 | G6 | G5 | G4 | G3 | G2 | G1 | F6 | F5 | F4 | F3 | F2 | F1 | E6 | E5 | E4 | E3 | E2 | E1 | D6 | D5 | D4 | D3 | D2 | D1 | C6 | C5 | C4 | C3 | C2 | C1;
	private static final long BLACK_PASSER_EXT_PARAM_A6 = A7 | A6 | A5 | A4 | A3 | A2 | A1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1;
	private static final long BLACK_PASSER_EXT_PARAM_B6 = B7 | B6 | B5 | B4 | B3 | B2 | B1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C6 = C7 | C6 | C5 | C4 | C3 | C2 | C1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D6 = D7 | D6 | D5 | D4 | D3 | D2 | D1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E6 = E7 | E6 | E5 | E4 | E3 | E2 | E1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_F6 = F7 | F6 | F5 | F4 | F3 | F2 | F1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_G6 = G7 | G6 | G5 | G4 | G3 | G2 | G1 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_H6 = H7 | H6 | H5 | H4 | H3 | H2 | H1 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B7 | B6 | B5 | B4 | B3 | B2 | B1;
	private static final long BLACK_PASSER_EXT_PARAM_A7 = A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSER_EXT_PARAM_B7 = B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C7 = C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D7 = D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E7 = E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_F7 = F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_G7 = G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_H7 = H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_A8 = A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1;
	private static final long BLACK_PASSER_EXT_PARAM_B8 = B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_C8 = C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_D8 = D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_E8 = E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_F8 = F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_G8 = G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long BLACK_PASSER_EXT_PARAM_H8 = H8 | H7 | H6 | H5 | H4 | H3 | H2 | H1 | G8 | G7 | G6 | G5 | G4 | G3 | G2 | G1 | F8 | F7 | F6 | F5 | F4 | F3 | F2 | F1 | E8 | E7 | E6 | E5 | E4 | E3 | E2 | E1 | D8 | D7 | D6 | D5 | D4 | D3 | D2 | D1 | C8 | C7 | C6 | C5 | C4 | C3 | C2 | C1 | B8 | B7 | B6 | B5 | B4 | B3 | B2 | B1 | A8 | A7 | A6 | A5 | A4 | A3 | A2 | A1;
	private static final long[] BLACK_PASSER_EXT_PARAM_ORDERED = new long[] {BLACK_PASSER_EXT_PARAM_A1, BLACK_PASSER_EXT_PARAM_B1, BLACK_PASSER_EXT_PARAM_C1, BLACK_PASSER_EXT_PARAM_D1, BLACK_PASSER_EXT_PARAM_E1, BLACK_PASSER_EXT_PARAM_F1, BLACK_PASSER_EXT_PARAM_G1, BLACK_PASSER_EXT_PARAM_H1, BLACK_PASSER_EXT_PARAM_A2, BLACK_PASSER_EXT_PARAM_B2, BLACK_PASSER_EXT_PARAM_C2, BLACK_PASSER_EXT_PARAM_D2, BLACK_PASSER_EXT_PARAM_E2, BLACK_PASSER_EXT_PARAM_F2, BLACK_PASSER_EXT_PARAM_G2, BLACK_PASSER_EXT_PARAM_H2, BLACK_PASSER_EXT_PARAM_A3, BLACK_PASSER_EXT_PARAM_B3, BLACK_PASSER_EXT_PARAM_C3, BLACK_PASSER_EXT_PARAM_D3, BLACK_PASSER_EXT_PARAM_E3, BLACK_PASSER_EXT_PARAM_F3, BLACK_PASSER_EXT_PARAM_G3, BLACK_PASSER_EXT_PARAM_H3, BLACK_PASSER_EXT_PARAM_A4, BLACK_PASSER_EXT_PARAM_B4, BLACK_PASSER_EXT_PARAM_C4, BLACK_PASSER_EXT_PARAM_D4, BLACK_PASSER_EXT_PARAM_E4, BLACK_PASSER_EXT_PARAM_F4, BLACK_PASSER_EXT_PARAM_G4, BLACK_PASSER_EXT_PARAM_H4, BLACK_PASSER_EXT_PARAM_A5, BLACK_PASSER_EXT_PARAM_B5, BLACK_PASSER_EXT_PARAM_C5, BLACK_PASSER_EXT_PARAM_D5, BLACK_PASSER_EXT_PARAM_E5, BLACK_PASSER_EXT_PARAM_F5, BLACK_PASSER_EXT_PARAM_G5, BLACK_PASSER_EXT_PARAM_H5, BLACK_PASSER_EXT_PARAM_A6, BLACK_PASSER_EXT_PARAM_B6, BLACK_PASSER_EXT_PARAM_C6, BLACK_PASSER_EXT_PARAM_D6, BLACK_PASSER_EXT_PARAM_E6, BLACK_PASSER_EXT_PARAM_F6, BLACK_PASSER_EXT_PARAM_G6, BLACK_PASSER_EXT_PARAM_H6, BLACK_PASSER_EXT_PARAM_A7, BLACK_PASSER_EXT_PARAM_B7, BLACK_PASSER_EXT_PARAM_C7, BLACK_PASSER_EXT_PARAM_D7, BLACK_PASSER_EXT_PARAM_E7, BLACK_PASSER_EXT_PARAM_F7, BLACK_PASSER_EXT_PARAM_G7, BLACK_PASSER_EXT_PARAM_H7, BLACK_PASSER_EXT_PARAM_A8, BLACK_PASSER_EXT_PARAM_B8, BLACK_PASSER_EXT_PARAM_C8, BLACK_PASSER_EXT_PARAM_D8, BLACK_PASSER_EXT_PARAM_E8, BLACK_PASSER_EXT_PARAM_F8, BLACK_PASSER_EXT_PARAM_G8, BLACK_PASSER_EXT_PARAM_H8, };

	
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H1 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A2 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A2 = B2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B2 = A2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B2 = C2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C2 = B2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C2 = D2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D2 = C2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D2 = E2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E2 = D2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E2 = F2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F2 = E2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F2 = G2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G2 = F2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G2 = H2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H2 = G2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H2 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A3 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A3 = B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B3 = A3 | A2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B3 = C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C3 = B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C3 = D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D3 = C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D3 = E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E3 = D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E3 = F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F3 = E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F3 = G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G3 = F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G3 = H3 | H2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H3 = G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H3 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A4 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A4 = B4 | B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B4 = A4 | A3 | A2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B4 = C4 | C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C4 = B4 | B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C4 = D4 | D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D4 = C4 | C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D4 = E4 | E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E4 = D4 | D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E4 = F4 | F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F4 = E4 | E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F4 = G4 | G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G4 = F4 | F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G4 = H4 | H3 | H2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H4 = G4 | G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H4 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A5 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A5 = B5 | B4 | B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B5 = A5 | A4 | A3 | A2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B5 = C5 | C4 | C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C5 = B5 | B4 | B3 | B2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C5 = D5 | D4 | D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D5 = C5 | C4 | C3 | C2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D5 = E5 | E4 | E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E5 = D5 | D4 | D3 | D2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E5 = F5 | F4 | F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F5 = E5 | E4 | E3 | E2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F5 = G5 | G4 | G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G5 = F5 | F4 | F3 | F2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G5 = H5 | H4 | H3 | H2;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H5 = G5 | G4 | G3 | G2;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H5 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A6 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A6 = B6 | B5 | B4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B6 = A6 | A5 | A4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B6 = C6 | C5 | C4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C6 = B6 | B5 | B4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C6 = D6 | D5 | D4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D6 = C6 | C5 | C4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D6 = E6 | E5 | E4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E6 = D6 | D5 | D4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E6 = F6 | F5 | F4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F6 = E6 | E5 | E4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F6 = G6 | G5 | G4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G6 = F6 | F5 | F4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G6 = H6 | H5 | H4;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H6 = G6 | G5 | G4;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H6 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A7 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A7 = B7 | B6 | B5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B7 = A7 | A6 | A5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B7 = C7 | C6 | C5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C7 = B7 | B6 | B5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C7 = D7 | D6 | D5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D7 = C7 | C6 | C5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D7 = E7 | E6 | E5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E7 = D7 | D6 | D5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E7 = F7 | F6 | F5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F7 = E7 | E6 | E5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F7 = G7 | G6 | G5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G7 = F7 | F6 | F5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G7 = H7 | H6 | H5;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H7 = G7 | G6 | G5;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H7 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H8 = 0L;
	private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H8 = 0L;
	private static final long[] WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {WHITE_CAN_BE_SUPPORTED_LEFT_A1, WHITE_CAN_BE_SUPPORTED_LEFT_B1, WHITE_CAN_BE_SUPPORTED_LEFT_C1, WHITE_CAN_BE_SUPPORTED_LEFT_D1, WHITE_CAN_BE_SUPPORTED_LEFT_E1, WHITE_CAN_BE_SUPPORTED_LEFT_F1, WHITE_CAN_BE_SUPPORTED_LEFT_G1, WHITE_CAN_BE_SUPPORTED_LEFT_H1, WHITE_CAN_BE_SUPPORTED_LEFT_A2, WHITE_CAN_BE_SUPPORTED_LEFT_B2, WHITE_CAN_BE_SUPPORTED_LEFT_C2, WHITE_CAN_BE_SUPPORTED_LEFT_D2, WHITE_CAN_BE_SUPPORTED_LEFT_E2, WHITE_CAN_BE_SUPPORTED_LEFT_F2, WHITE_CAN_BE_SUPPORTED_LEFT_G2, WHITE_CAN_BE_SUPPORTED_LEFT_H2, WHITE_CAN_BE_SUPPORTED_LEFT_A3, WHITE_CAN_BE_SUPPORTED_LEFT_B3, WHITE_CAN_BE_SUPPORTED_LEFT_C3, WHITE_CAN_BE_SUPPORTED_LEFT_D3, WHITE_CAN_BE_SUPPORTED_LEFT_E3, WHITE_CAN_BE_SUPPORTED_LEFT_F3, WHITE_CAN_BE_SUPPORTED_LEFT_G3, WHITE_CAN_BE_SUPPORTED_LEFT_H3, WHITE_CAN_BE_SUPPORTED_LEFT_A4, WHITE_CAN_BE_SUPPORTED_LEFT_B4, WHITE_CAN_BE_SUPPORTED_LEFT_C4, WHITE_CAN_BE_SUPPORTED_LEFT_D4, WHITE_CAN_BE_SUPPORTED_LEFT_E4, WHITE_CAN_BE_SUPPORTED_LEFT_F4, WHITE_CAN_BE_SUPPORTED_LEFT_G4, WHITE_CAN_BE_SUPPORTED_LEFT_H4, WHITE_CAN_BE_SUPPORTED_LEFT_A5, WHITE_CAN_BE_SUPPORTED_LEFT_B5, WHITE_CAN_BE_SUPPORTED_LEFT_C5, WHITE_CAN_BE_SUPPORTED_LEFT_D5, WHITE_CAN_BE_SUPPORTED_LEFT_E5, WHITE_CAN_BE_SUPPORTED_LEFT_F5, WHITE_CAN_BE_SUPPORTED_LEFT_G5, WHITE_CAN_BE_SUPPORTED_LEFT_H5, WHITE_CAN_BE_SUPPORTED_LEFT_A6, WHITE_CAN_BE_SUPPORTED_LEFT_B6, WHITE_CAN_BE_SUPPORTED_LEFT_C6, WHITE_CAN_BE_SUPPORTED_LEFT_D6, WHITE_CAN_BE_SUPPORTED_LEFT_E6, WHITE_CAN_BE_SUPPORTED_LEFT_F6, WHITE_CAN_BE_SUPPORTED_LEFT_G6, WHITE_CAN_BE_SUPPORTED_LEFT_H6, WHITE_CAN_BE_SUPPORTED_LEFT_A7, WHITE_CAN_BE_SUPPORTED_LEFT_B7, WHITE_CAN_BE_SUPPORTED_LEFT_C7, WHITE_CAN_BE_SUPPORTED_LEFT_D7, WHITE_CAN_BE_SUPPORTED_LEFT_E7, WHITE_CAN_BE_SUPPORTED_LEFT_F7, WHITE_CAN_BE_SUPPORTED_LEFT_G7, WHITE_CAN_BE_SUPPORTED_LEFT_H7, WHITE_CAN_BE_SUPPORTED_LEFT_A8, WHITE_CAN_BE_SUPPORTED_LEFT_B8, WHITE_CAN_BE_SUPPORTED_LEFT_C8, WHITE_CAN_BE_SUPPORTED_LEFT_D8, WHITE_CAN_BE_SUPPORTED_LEFT_E8, WHITE_CAN_BE_SUPPORTED_LEFT_F8, WHITE_CAN_BE_SUPPORTED_LEFT_G8, WHITE_CAN_BE_SUPPORTED_LEFT_H8, };
	private static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {WHITE_CAN_BE_SUPPORTED_RIGHT_A1, WHITE_CAN_BE_SUPPORTED_RIGHT_B1, WHITE_CAN_BE_SUPPORTED_RIGHT_C1, WHITE_CAN_BE_SUPPORTED_RIGHT_D1, WHITE_CAN_BE_SUPPORTED_RIGHT_E1, WHITE_CAN_BE_SUPPORTED_RIGHT_F1, WHITE_CAN_BE_SUPPORTED_RIGHT_G1, WHITE_CAN_BE_SUPPORTED_RIGHT_H1, WHITE_CAN_BE_SUPPORTED_RIGHT_A2, WHITE_CAN_BE_SUPPORTED_RIGHT_B2, WHITE_CAN_BE_SUPPORTED_RIGHT_C2, WHITE_CAN_BE_SUPPORTED_RIGHT_D2, WHITE_CAN_BE_SUPPORTED_RIGHT_E2, WHITE_CAN_BE_SUPPORTED_RIGHT_F2, WHITE_CAN_BE_SUPPORTED_RIGHT_G2, WHITE_CAN_BE_SUPPORTED_RIGHT_H2, WHITE_CAN_BE_SUPPORTED_RIGHT_A3, WHITE_CAN_BE_SUPPORTED_RIGHT_B3, WHITE_CAN_BE_SUPPORTED_RIGHT_C3, WHITE_CAN_BE_SUPPORTED_RIGHT_D3, WHITE_CAN_BE_SUPPORTED_RIGHT_E3, WHITE_CAN_BE_SUPPORTED_RIGHT_F3, WHITE_CAN_BE_SUPPORTED_RIGHT_G3, WHITE_CAN_BE_SUPPORTED_RIGHT_H3, WHITE_CAN_BE_SUPPORTED_RIGHT_A4, WHITE_CAN_BE_SUPPORTED_RIGHT_B4, WHITE_CAN_BE_SUPPORTED_RIGHT_C4, WHITE_CAN_BE_SUPPORTED_RIGHT_D4, WHITE_CAN_BE_SUPPORTED_RIGHT_E4, WHITE_CAN_BE_SUPPORTED_RIGHT_F4, WHITE_CAN_BE_SUPPORTED_RIGHT_G4, WHITE_CAN_BE_SUPPORTED_RIGHT_H4, WHITE_CAN_BE_SUPPORTED_RIGHT_A5, WHITE_CAN_BE_SUPPORTED_RIGHT_B5, WHITE_CAN_BE_SUPPORTED_RIGHT_C5, WHITE_CAN_BE_SUPPORTED_RIGHT_D5, WHITE_CAN_BE_SUPPORTED_RIGHT_E5, WHITE_CAN_BE_SUPPORTED_RIGHT_F5, WHITE_CAN_BE_SUPPORTED_RIGHT_G5, WHITE_CAN_BE_SUPPORTED_RIGHT_H5, WHITE_CAN_BE_SUPPORTED_RIGHT_A6, WHITE_CAN_BE_SUPPORTED_RIGHT_B6, WHITE_CAN_BE_SUPPORTED_RIGHT_C6, WHITE_CAN_BE_SUPPORTED_RIGHT_D6, WHITE_CAN_BE_SUPPORTED_RIGHT_E6, WHITE_CAN_BE_SUPPORTED_RIGHT_F6, WHITE_CAN_BE_SUPPORTED_RIGHT_G6, WHITE_CAN_BE_SUPPORTED_RIGHT_H6, WHITE_CAN_BE_SUPPORTED_RIGHT_A7, WHITE_CAN_BE_SUPPORTED_RIGHT_B7, WHITE_CAN_BE_SUPPORTED_RIGHT_C7, WHITE_CAN_BE_SUPPORTED_RIGHT_D7, WHITE_CAN_BE_SUPPORTED_RIGHT_E7, WHITE_CAN_BE_SUPPORTED_RIGHT_F7, WHITE_CAN_BE_SUPPORTED_RIGHT_G7, WHITE_CAN_BE_SUPPORTED_RIGHT_H7, WHITE_CAN_BE_SUPPORTED_RIGHT_A8, WHITE_CAN_BE_SUPPORTED_RIGHT_B8, WHITE_CAN_BE_SUPPORTED_RIGHT_C8, WHITE_CAN_BE_SUPPORTED_RIGHT_D8, WHITE_CAN_BE_SUPPORTED_RIGHT_E8, WHITE_CAN_BE_SUPPORTED_RIGHT_F8, WHITE_CAN_BE_SUPPORTED_RIGHT_G8, WHITE_CAN_BE_SUPPORTED_RIGHT_H8, };
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H1 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A2 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A2 = B2 | B3 | B4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B2 = A2 | A3 | A4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B2 = C2 | C3 | C4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C2 = B2 | B3 | B4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C2 = D2 | D3 | D4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D2 = C2 | C3 | C4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D2 = E2 | E3 | E4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E2 = D2 | D3 | D4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E2 = F2 | F3 | F4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F2 = E2 | E3 | E4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F2 = G2 | G3 | G4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G2 = F2 | F3 | F4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G2 = H2 | H3 | H4;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H2 = G2 | G3 | G4;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H2 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A3 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A3 = B3 | B4 | B5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B3 = A3 | A4 | A5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B3 = C3 | C4 | C5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C3 = B3 | B4 | B5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C3 = D3 | D4 | D5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D3 = C3 | C4 | C5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D3 = E3 | E4 | E5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E3 = D3 | D4 | D5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E3 = F3 | F4 | F5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F3 = E3 | E4 | E5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F3 = G3 | G4 | G5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G3 = F3 | F4 | F5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G3 = H3 | H4 | H5;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H3 = G3 | G4 | G5;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H3 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A4 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A4 = B4 | B5 | B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B4 = A4 | A5 | A6 | A7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B4 = C4 | C5 | C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C4 = B4 | B5 | B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C4 = D4 | D5 | D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D4 = C4 | C5 | C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D4 = E4 | E5 | E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E4 = D4 | D5 | D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E4 = F4 | F5 | F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F4 = E4 | E5 | E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F4 = G4 | G5 | G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G4 = F4 | F5 | F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G4 = H4 | H5 | H6 | H7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H4 = G4 | G5 | G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H4 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A5 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A5 = B5 | B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B5 = A5 | A6 | A7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B5 = C5 | C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C5 = B5 | B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C5 = D5 | D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D5 = C5 | C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D5 = E5 | E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E5 = D5 | D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E5 = F5 | F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F5 = E5 | E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F5 = G5 | G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G5 = F5 | F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G5 = H5 | H6 | H7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H5 = G5 | G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H5 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A6 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A6 = B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B6 = A6 | A7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B6 = C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C6 = B6 | B7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C6 = D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D6 = C6 | C7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D6 = E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E6 = D6 | D7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E6 = F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F6 = E6 | E7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F6 = G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G6 = F6 | F7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G6 = H6 | H7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H6 = G6 | G7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H6 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A7 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A7 = B7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B7 = A7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B7 = C7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C7 = B7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C7 = D7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D7 = C7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D7 = E7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E7 = D7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E7 = F7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F7 = E7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F7 = G7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G7 = F7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G7 = H7;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H7 = G7;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H7 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H8 = 0L;
	private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H8 = 0L;
	private static final long[] BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {BLACK_CAN_BE_SUPPORTED_LEFT_A1, BLACK_CAN_BE_SUPPORTED_LEFT_B1, BLACK_CAN_BE_SUPPORTED_LEFT_C1, BLACK_CAN_BE_SUPPORTED_LEFT_D1, BLACK_CAN_BE_SUPPORTED_LEFT_E1, BLACK_CAN_BE_SUPPORTED_LEFT_F1, BLACK_CAN_BE_SUPPORTED_LEFT_G1, BLACK_CAN_BE_SUPPORTED_LEFT_H1, BLACK_CAN_BE_SUPPORTED_LEFT_A2, BLACK_CAN_BE_SUPPORTED_LEFT_B2, BLACK_CAN_BE_SUPPORTED_LEFT_C2, BLACK_CAN_BE_SUPPORTED_LEFT_D2, BLACK_CAN_BE_SUPPORTED_LEFT_E2, BLACK_CAN_BE_SUPPORTED_LEFT_F2, BLACK_CAN_BE_SUPPORTED_LEFT_G2, BLACK_CAN_BE_SUPPORTED_LEFT_H2, BLACK_CAN_BE_SUPPORTED_LEFT_A3, BLACK_CAN_BE_SUPPORTED_LEFT_B3, BLACK_CAN_BE_SUPPORTED_LEFT_C3, BLACK_CAN_BE_SUPPORTED_LEFT_D3, BLACK_CAN_BE_SUPPORTED_LEFT_E3, BLACK_CAN_BE_SUPPORTED_LEFT_F3, BLACK_CAN_BE_SUPPORTED_LEFT_G3, BLACK_CAN_BE_SUPPORTED_LEFT_H3, BLACK_CAN_BE_SUPPORTED_LEFT_A4, BLACK_CAN_BE_SUPPORTED_LEFT_B4, BLACK_CAN_BE_SUPPORTED_LEFT_C4, BLACK_CAN_BE_SUPPORTED_LEFT_D4, BLACK_CAN_BE_SUPPORTED_LEFT_E4, BLACK_CAN_BE_SUPPORTED_LEFT_F4, BLACK_CAN_BE_SUPPORTED_LEFT_G4, BLACK_CAN_BE_SUPPORTED_LEFT_H4, BLACK_CAN_BE_SUPPORTED_LEFT_A5, BLACK_CAN_BE_SUPPORTED_LEFT_B5, BLACK_CAN_BE_SUPPORTED_LEFT_C5, BLACK_CAN_BE_SUPPORTED_LEFT_D5, BLACK_CAN_BE_SUPPORTED_LEFT_E5, BLACK_CAN_BE_SUPPORTED_LEFT_F5, BLACK_CAN_BE_SUPPORTED_LEFT_G5, BLACK_CAN_BE_SUPPORTED_LEFT_H5, BLACK_CAN_BE_SUPPORTED_LEFT_A6, BLACK_CAN_BE_SUPPORTED_LEFT_B6, BLACK_CAN_BE_SUPPORTED_LEFT_C6, BLACK_CAN_BE_SUPPORTED_LEFT_D6, BLACK_CAN_BE_SUPPORTED_LEFT_E6, BLACK_CAN_BE_SUPPORTED_LEFT_F6, BLACK_CAN_BE_SUPPORTED_LEFT_G6, BLACK_CAN_BE_SUPPORTED_LEFT_H6, BLACK_CAN_BE_SUPPORTED_LEFT_A7, BLACK_CAN_BE_SUPPORTED_LEFT_B7, BLACK_CAN_BE_SUPPORTED_LEFT_C7, BLACK_CAN_BE_SUPPORTED_LEFT_D7, BLACK_CAN_BE_SUPPORTED_LEFT_E7, BLACK_CAN_BE_SUPPORTED_LEFT_F7, BLACK_CAN_BE_SUPPORTED_LEFT_G7, BLACK_CAN_BE_SUPPORTED_LEFT_H7, BLACK_CAN_BE_SUPPORTED_LEFT_A8, BLACK_CAN_BE_SUPPORTED_LEFT_B8, BLACK_CAN_BE_SUPPORTED_LEFT_C8, BLACK_CAN_BE_SUPPORTED_LEFT_D8, BLACK_CAN_BE_SUPPORTED_LEFT_E8, BLACK_CAN_BE_SUPPORTED_LEFT_F8, BLACK_CAN_BE_SUPPORTED_LEFT_G8, BLACK_CAN_BE_SUPPORTED_LEFT_H8, };
	private static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {BLACK_CAN_BE_SUPPORTED_RIGHT_A1, BLACK_CAN_BE_SUPPORTED_RIGHT_B1, BLACK_CAN_BE_SUPPORTED_RIGHT_C1, BLACK_CAN_BE_SUPPORTED_RIGHT_D1, BLACK_CAN_BE_SUPPORTED_RIGHT_E1, BLACK_CAN_BE_SUPPORTED_RIGHT_F1, BLACK_CAN_BE_SUPPORTED_RIGHT_G1, BLACK_CAN_BE_SUPPORTED_RIGHT_H1, BLACK_CAN_BE_SUPPORTED_RIGHT_A2, BLACK_CAN_BE_SUPPORTED_RIGHT_B2, BLACK_CAN_BE_SUPPORTED_RIGHT_C2, BLACK_CAN_BE_SUPPORTED_RIGHT_D2, BLACK_CAN_BE_SUPPORTED_RIGHT_E2, BLACK_CAN_BE_SUPPORTED_RIGHT_F2, BLACK_CAN_BE_SUPPORTED_RIGHT_G2, BLACK_CAN_BE_SUPPORTED_RIGHT_H2, BLACK_CAN_BE_SUPPORTED_RIGHT_A3, BLACK_CAN_BE_SUPPORTED_RIGHT_B3, BLACK_CAN_BE_SUPPORTED_RIGHT_C3, BLACK_CAN_BE_SUPPORTED_RIGHT_D3, BLACK_CAN_BE_SUPPORTED_RIGHT_E3, BLACK_CAN_BE_SUPPORTED_RIGHT_F3, BLACK_CAN_BE_SUPPORTED_RIGHT_G3, BLACK_CAN_BE_SUPPORTED_RIGHT_H3, BLACK_CAN_BE_SUPPORTED_RIGHT_A4, BLACK_CAN_BE_SUPPORTED_RIGHT_B4, BLACK_CAN_BE_SUPPORTED_RIGHT_C4, BLACK_CAN_BE_SUPPORTED_RIGHT_D4, BLACK_CAN_BE_SUPPORTED_RIGHT_E4, BLACK_CAN_BE_SUPPORTED_RIGHT_F4, BLACK_CAN_BE_SUPPORTED_RIGHT_G4, BLACK_CAN_BE_SUPPORTED_RIGHT_H4, BLACK_CAN_BE_SUPPORTED_RIGHT_A5, BLACK_CAN_BE_SUPPORTED_RIGHT_B5, BLACK_CAN_BE_SUPPORTED_RIGHT_C5, BLACK_CAN_BE_SUPPORTED_RIGHT_D5, BLACK_CAN_BE_SUPPORTED_RIGHT_E5, BLACK_CAN_BE_SUPPORTED_RIGHT_F5, BLACK_CAN_BE_SUPPORTED_RIGHT_G5, BLACK_CAN_BE_SUPPORTED_RIGHT_H5, BLACK_CAN_BE_SUPPORTED_RIGHT_A6, BLACK_CAN_BE_SUPPORTED_RIGHT_B6, BLACK_CAN_BE_SUPPORTED_RIGHT_C6, BLACK_CAN_BE_SUPPORTED_RIGHT_D6, BLACK_CAN_BE_SUPPORTED_RIGHT_E6, BLACK_CAN_BE_SUPPORTED_RIGHT_F6, BLACK_CAN_BE_SUPPORTED_RIGHT_G6, BLACK_CAN_BE_SUPPORTED_RIGHT_H6, BLACK_CAN_BE_SUPPORTED_RIGHT_A7, BLACK_CAN_BE_SUPPORTED_RIGHT_B7, BLACK_CAN_BE_SUPPORTED_RIGHT_C7, BLACK_CAN_BE_SUPPORTED_RIGHT_D7, BLACK_CAN_BE_SUPPORTED_RIGHT_E7, BLACK_CAN_BE_SUPPORTED_RIGHT_F7, BLACK_CAN_BE_SUPPORTED_RIGHT_G7, BLACK_CAN_BE_SUPPORTED_RIGHT_H7, BLACK_CAN_BE_SUPPORTED_RIGHT_A8, BLACK_CAN_BE_SUPPORTED_RIGHT_B8, BLACK_CAN_BE_SUPPORTED_RIGHT_C8, BLACK_CAN_BE_SUPPORTED_RIGHT_D8, BLACK_CAN_BE_SUPPORTED_RIGHT_E8, BLACK_CAN_BE_SUPPORTED_RIGHT_F8, BLACK_CAN_BE_SUPPORTED_RIGHT_G8, BLACK_CAN_BE_SUPPORTED_RIGHT_H8, };

	
	public static final long CENTRAL_PAWNS = LETTER_B | LETTER_C | LETTER_D | LETTER_E | LETTER_F | LETTER_G;
	
	public static final long[] WHITE_PASSED = new long[Bits.PRIME_67];
	public static final long[] BLACK_PASSED = new long[Bits.PRIME_67];
	public static final long[] WHITE_BACKWARD = new long[Bits.PRIME_67];
	public static final long[] BLACK_BACKWARD = new long[Bits.PRIME_67];
	public static final long[] WHITE_FRONT_FULL = new long[Bits.PRIME_67];
	public static final long[] BLACK_FRONT_FULL = new long[Bits.PRIME_67];
	public static final long[] WHITE_SUPPORT = new long[Bits.PRIME_67];
	public static final long[] BLACK_SUPPORT = new long[Bits.PRIME_67];
	public static final long[] WHITE_POSSIBLE_ATTACKS = new long[Bits.PRIME_67];
	public static final long[] BLACK_POSSIBLE_ATTACKS = new long[Bits.PRIME_67];
	public static final long[] WHITE_PASSER_PARAM = new long[Bits.PRIME_67];
	public static final long[] BLACK_PASSER_PARAM = new long[Bits.PRIME_67];
	public static final long[] WHITE_PASSER_EXT_PARAM = new long[Bits.PRIME_67];
	public static final long[] BLACK_PASSER_EXT_PARAM = new long[Bits.PRIME_67];
	public static final long[] WHITE_CAN_BE_SUPPORTED_LEFT = new long[Bits.PRIME_67];
	public static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT = new long[Bits.PRIME_67];
	public static final long[] BLACK_CAN_BE_SUPPORTED_LEFT = new long[Bits.PRIME_67];
	public static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT = new long[Bits.PRIME_67];
	
	public static final long[] WHITE_KEY_SQUARES = new long[Bits.PRIME_67];
	public static final long[] BLACK_KEY_SQUARES = new long[Bits.PRIME_67];
	
	static {
		
		//White
		
		WHITE_KEY_SQUARES[A2_ID] = B7 | B8;
		WHITE_KEY_SQUARES[A3_ID] = B7 | B8;
		WHITE_KEY_SQUARES[A4_ID] = B7 | B8;
		WHITE_KEY_SQUARES[A5_ID] = B7 | B8;
		WHITE_KEY_SQUARES[A6_ID] = B7 | B8;
		WHITE_KEY_SQUARES[A7_ID] = B7 | B8;
		
		WHITE_KEY_SQUARES[B2_ID] = A4 | B4 | C4;
		WHITE_KEY_SQUARES[B3_ID] = A5 | B5 | C5;
		WHITE_KEY_SQUARES[B4_ID] = A6 | B6 | C6;
		WHITE_KEY_SQUARES[B5_ID] = A6 | B6 | C6 | A7 | B7 | C7;
		WHITE_KEY_SQUARES[B6_ID] = A7 | B7 | C7 | A8 | B8 | C8;
		WHITE_KEY_SQUARES[B7_ID] = A7 |      C7 | A8 | B8 | C8;
		
		WHITE_KEY_SQUARES[C2_ID] = B4 | C4 | D4;
		WHITE_KEY_SQUARES[C3_ID] = B5 | C5 | D5;
		WHITE_KEY_SQUARES[C4_ID] = B6 | C6 | D6;
		WHITE_KEY_SQUARES[C5_ID] = B6 | C6 | D6 | B7 | C7 | D7;
		WHITE_KEY_SQUARES[C6_ID] = B7 | C7 | D7 | B8 | C8 | D8;
		WHITE_KEY_SQUARES[C7_ID] = B7 |      D7 | B8 | C8 | D8;
		
		WHITE_KEY_SQUARES[D2_ID] = C4 | D4 | E4;
		WHITE_KEY_SQUARES[D3_ID] = C5 | D5 | E5;
		WHITE_KEY_SQUARES[D4_ID] = C6 | D6 | E6;
		WHITE_KEY_SQUARES[D5_ID] = C6 | D6 | E6 | C7 | D7 | E7;
		WHITE_KEY_SQUARES[D6_ID] = C7 | D7 | E7 | C8 | D8 | E8;
		WHITE_KEY_SQUARES[D7_ID] = C7 |      E7 | C8 | D8 | E8;
		
		WHITE_KEY_SQUARES[E2_ID] = D4 | E4 | F4;
		WHITE_KEY_SQUARES[E3_ID] = D5 | E5 | F5;
		WHITE_KEY_SQUARES[E4_ID] = D6 | E6 | F6;
		WHITE_KEY_SQUARES[E5_ID] = D6 | E6 | F6 | D7 | E7 | F7;
		WHITE_KEY_SQUARES[E6_ID] = D7 | E7 | F7 | D8 | E8 | F8;
		WHITE_KEY_SQUARES[E7_ID] = D7 |      F7 | D8 | E8 | F8;
		
		WHITE_KEY_SQUARES[F2_ID] = E4 | F4 | G4;
		WHITE_KEY_SQUARES[F3_ID] = E5 | F5 | G5;
		WHITE_KEY_SQUARES[F4_ID] = E6 | F6 | G6;
		WHITE_KEY_SQUARES[F5_ID] = E6 | F6 | G6 | E7 | F7 | G7;
		WHITE_KEY_SQUARES[F6_ID] = E7 | F7 | G7 | E8 | F8 | G8;
		WHITE_KEY_SQUARES[F7_ID] = E7 |      G7 | E8 | F8 | G8;
		
		WHITE_KEY_SQUARES[G2_ID] = F4 | G4 | H4;
		WHITE_KEY_SQUARES[G3_ID] = F5 | G5 | H5;
		WHITE_KEY_SQUARES[G4_ID] = F6 | G6 | H6;
		WHITE_KEY_SQUARES[G5_ID] = F6 | G6 | H6 | F7 | G7 | H7;
		WHITE_KEY_SQUARES[G6_ID] = F7 | G7 | H7 | F8 | G8 | H8;
		WHITE_KEY_SQUARES[G7_ID] = F7 |      H7 | F8 | G8 | H8;
		
		WHITE_KEY_SQUARES[H2_ID] = G7 | G8;
		WHITE_KEY_SQUARES[H3_ID] = G7 | G8;
		WHITE_KEY_SQUARES[H4_ID] = G7 | G8;
		WHITE_KEY_SQUARES[H5_ID] = G7 | G8;
		WHITE_KEY_SQUARES[H6_ID] = G7 | G8;
		WHITE_KEY_SQUARES[H7_ID] = G7 | G8;

		//Black
		
		BLACK_KEY_SQUARES[A7_ID] = B1 | B2;
		BLACK_KEY_SQUARES[A6_ID] = B1 | B2;
		BLACK_KEY_SQUARES[A5_ID] = B1 | B2;
		BLACK_KEY_SQUARES[A4_ID] = B1 | B2;
		BLACK_KEY_SQUARES[A3_ID] = B1 | B2;
		BLACK_KEY_SQUARES[A2_ID] = B1 | B2;

		BLACK_KEY_SQUARES[B7_ID] = A5 | B5 | C5;
		BLACK_KEY_SQUARES[B6_ID] = A4 | B4 | C4;
		BLACK_KEY_SQUARES[B5_ID] = A3 | B3 | C3;
		BLACK_KEY_SQUARES[B4_ID] = A3 | B3 | C3 | A2 | B2 | C2;
		BLACK_KEY_SQUARES[B3_ID] = A2 | B2 | C2 | A1 | B1 | C1;
		BLACK_KEY_SQUARES[B2_ID] = A2 |      C2 | A1 | B1 | C1;

		BLACK_KEY_SQUARES[C7_ID] = B5 | C5 | D5;
		BLACK_KEY_SQUARES[C6_ID] = B4 | C4 | D4;
		BLACK_KEY_SQUARES[C5_ID] = B3 | C3 | D3;
		BLACK_KEY_SQUARES[C4_ID] = B3 | C3 | D3 | B2 | C2 | D2;
		BLACK_KEY_SQUARES[C3_ID] = B2 | C2 | D2 | B1 | C1 | D1;
		BLACK_KEY_SQUARES[C2_ID] = B2 |      D2 | B1 | C1 | D1;
		
		BLACK_KEY_SQUARES[D7_ID] = C5 | D5 | E5;
		BLACK_KEY_SQUARES[D6_ID] = C4 | D4 | E4;
		BLACK_KEY_SQUARES[D5_ID] = C3 | D3 | E3;
		BLACK_KEY_SQUARES[D4_ID] = C3 | D3 | E3 | C2 | D2 | E2;
		BLACK_KEY_SQUARES[D3_ID] = C2 | D2 | E2 | C1 | D1 | E1;
		BLACK_KEY_SQUARES[D2_ID] = C2 |      E2 | C1 | D1 | E1;
		
		BLACK_KEY_SQUARES[E7_ID] = D5 | E5 | F5;
		BLACK_KEY_SQUARES[E6_ID] = D4 | E4 | F4;
		BLACK_KEY_SQUARES[E5_ID] = D3 | E3 | F3;
		BLACK_KEY_SQUARES[E4_ID] = D3 | E3 | F3 | D2 | E2 | F2;
		BLACK_KEY_SQUARES[E3_ID] = D2 | E2 | F2 | D1 | E1 | F1;
		BLACK_KEY_SQUARES[E2_ID] = D2 |      F2 | D1 | E1 | F1;
		
		BLACK_KEY_SQUARES[F7_ID] = E5 | F5 | G5;
		BLACK_KEY_SQUARES[F6_ID] = E4 | F4 | G4;
		BLACK_KEY_SQUARES[F5_ID] = E3 | F3 | G3;
		BLACK_KEY_SQUARES[F4_ID] = E3 | F3 | G3 | E2 | F2 | G2;
		BLACK_KEY_SQUARES[F3_ID] = E2 | F2 | G2 | E1 | F1 | G1;
		BLACK_KEY_SQUARES[F2_ID] = E2 |      G2 | E1 | F1 | G1;
		
		BLACK_KEY_SQUARES[G7_ID] = F5 | G5 | H5;
		BLACK_KEY_SQUARES[G6_ID] = F4 | G4 | H4;
		BLACK_KEY_SQUARES[G5_ID] = F3 | G3 | H3;
		BLACK_KEY_SQUARES[G4_ID] = F3 | G3 | H3 | F2 | G2 | H2;
		BLACK_KEY_SQUARES[G3_ID] = F2 | G2 | H2 | F1 | G1 | H1;
		BLACK_KEY_SQUARES[G2_ID] = F2 |      H2 | F1 | G1 | H1;
		
		WHITE_KEY_SQUARES[H7_ID] = G1 | G2;
		WHITE_KEY_SQUARES[H6_ID] = G1 | G2;
		WHITE_KEY_SQUARES[H5_ID] = G1 | G2;
		WHITE_KEY_SQUARES[H4_ID] = G1 | G2;
		WHITE_KEY_SQUARES[H3_ID] = G1 | G2;
		WHITE_KEY_SQUARES[H2_ID] = G1 | G2;
	}
	
	static {
		for (int i=0; i<WHITE_PASSED_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_PASSED[idx] = WHITE_PASSED_ORDERED[i];
		}
		for (int i=0; i<BLACK_PASSED_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_PASSED[idx] = BLACK_PASSED_ORDERED[i];
		}
		for (int i=0; i<WHITE_BACKWARD_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_BACKWARD[idx] = WHITE_BACKWARD_ORDERED[i];
		}
		for (int i=0; i<BLACK_BACKWARD_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_BACKWARD[idx] = BLACK_BACKWARD_ORDERED[i];
		}
		for (int i=0; i<WHITE_FRONT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_FRONT_FULL[idx] = WHITE_FRONT_ORDERED[i];
		}
		for (int i=0; i<BLACK_FRONT_FULL_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_FRONT_FULL[idx] = BLACK_FRONT_FULL_ORDERED[i];
		}
		for (int i=0; i<WHITE_SUPPORT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_SUPPORT[idx] = WHITE_SUPPORT_ORDERED[i];
		}
		for (int i=0; i<BLACK_SUPPORT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_SUPPORT[idx] = BLACK_SUPPORT_ORDERED[i];
		}
		for (int i=0; i<WHITE_POSSIBLE_ATTACKS_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_POSSIBLE_ATTACKS[idx] = WHITE_POSSIBLE_ATTACKS_ORDERED[i];
		}
		for (int i=0; i<BLACK_POSSIBLE_ATTACKS_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_POSSIBLE_ATTACKS[idx] = BLACK_POSSIBLE_ATTACKS_ORDERED[i];
		}
		for (int i=0; i<WHITE_PASSER_PARAM_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_PASSER_PARAM[idx] = WHITE_PASSER_PARAM_ORDERED[i];
		}
		for (int i=0; i<BLACK_PASSER_PARAM_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_PASSER_PARAM[idx] = BLACK_PASSER_PARAM_ORDERED[i];
		}
		for (int i=0; i<WHITE_PASSER_EXT_PARAM_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_PASSER_EXT_PARAM[idx] = WHITE_PASSER_EXT_PARAM_ORDERED[i];
		}
		for (int i=0; i<BLACK_PASSER_EXT_PARAM_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_PASSER_EXT_PARAM[idx] = BLACK_PASSER_EXT_PARAM_ORDERED[i];
		}
		for (int i=0; i<WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_CAN_BE_SUPPORTED_LEFT[idx] = WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED[i];
		}
		for (int i=0; i<WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			WHITE_CAN_BE_SUPPORTED_RIGHT[idx] = WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED[i];
		}
		for (int i=0; i<BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_CAN_BE_SUPPORTED_LEFT[idx] = BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED[i];
		}
		for (int i=0; i<BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED.length; i++) {
			int idx = IDX_ORDERED_2_A1H1[i];
			BLACK_CAN_BE_SUPPORTED_RIGHT[idx] = BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED[i];
		}
	}
	
	public static final boolean isPasser(int colour, int pawnFieldID, long opPawns) {
		if (colour == Figures.COLOUR_WHITE) {
			long passed = WHITE_PASSED[pawnFieldID];
			long passedBoard = passed & opPawns;
			int passedHits = Utils.countBits(passedBoard);
			if (passedHits == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			long passed = BLACK_PASSED[pawnFieldID];
			long passedBoard = passed & opPawns;
			int passedHits = Utils.countBits(passedBoard);
			if (passedHits == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static final int getPasserRank(int colour, int pawnFieldID) {
		int rank = 0;
		
		if (colour == Figures.COLOUR_WHITE) {
			rank = DIGITS[pawnFieldID];
		} else {
			rank = 7 - DIGITS[pawnFieldID];
		}
		
		return rank;
	}
	
	private static void genMembers_WhitePassed() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long WHITE_PASSED_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 >= 0 && i / 8 <7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8) + 1; j <digits.length; j++) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	        		//result += "\r\n";
	    		}
	    		
	    		
	    		for (int j = (i / 8) + 1; j <digits.length; j++) {
	    			result += letters[i % 8] + digits[j] + " | ";
	    		}
	    		//result += "";
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8) + 1; j <digits.length; j++) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_PASSED_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_PASSED_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackPassed() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=Bits.NUMBER_64 - 1; i>=0; i--) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long BLACK_PASSED_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <= 7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8) - 1; j >= 0; j--) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	        		//result += "\r\n";
	    		}
	    		
	    		
	    		for (int j = (i / 8) - 1; j >= 0; j--) {
	    			result += letters[i % 8] + digits[j] + " | ";
	    		}
	    		//result += "";
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8) - 1; j >= 0; j--) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_PASSED_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_PASSED_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhiteBackward() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long WHITE_BACKWARD_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8); j >=0; j--) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	        		//result += "\r\n";
	    		}
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8); j >= 0; j--) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_BACKWARD_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_BACKWARD_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackBackward() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long BLACK_BACKWARD_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8); j < 8; j++) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	        		//result += "\r\n";
	    		}
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8); j < 8; j++) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_BACKWARD_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_BACKWARD_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhiteDoubled() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long WHITE_FRONT_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
        		for (int j = (i / 8); j < 7; j++) {
        			result += letters[i % 8] + digits[j + 1] + " | ";
        		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_FRONT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_FRONT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackDoubled() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long BLACK_FRONT_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
        		for (int j = (i / 8); j > 0; j--) {
        			result += letters[i % 8] + digits[j - 1] + " | ";
        		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_FRONT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_FRONT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhiteSupport() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long WHITE_SUPPORT_" + curLetter + curDigit + " = ";
    		
    		if (i % 8 > 0) {
    			result += letters[i % 8 - 1] + digits[i / 8] + " | ";
    			if (i / 8 > 0) {
    				result += letters[i % 8 - 1] + digits[i / 8 - 1] + " | ";
    			}
    		}
    		
    		if (i % 8 < 7) {
    			result += letters[i % 8 + 1] + digits[i / 8] + " | ";
    			if (i / 8 > 0) {
    				result += letters[i % 8 + 1] + digits[i / 8 - 1] + " | ";
    			}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_SUPPORT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_SUPPORT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackSupport() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long BLACK_SUPPORT_" + curLetter + curDigit + " = ";
    		
    		if (i % 8 > 0) {
    			result += letters[i % 8 - 1] + digits[i / 8] + " | ";
    			if (i / 8 < 7) {
    				result += letters[i % 8 - 1] + digits[i / 8 + 1] + " | ";
    			}
    		}
    		
    		if (i % 8 < 7) {
    			result += letters[i % 8 + 1] + digits[i / 8] + " | ";
    			if (i / 8 < 7) {
    				result += letters[i % 8 + 1] + digits[i / 8 + 1] + " | ";
    			}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_SUPPORT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_SUPPORT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhitePossibleAttacks() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long WHITE_POSSIBLE_ATTACKS_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8) + 1; j < 8; j++) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	    		}
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8) + 1; j < 8; j++) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
    	result = "private static final long[] WHITE_POSSIBLE_ATTACKS_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_POSSIBLE_ATTACKS_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackPossibleAttacks() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result = "private static final long BLACK_POSSIBLE_ATTACKS_" + curLetter + curDigit + " = ";
    		
    		if (i / 8 > 0 && i / 8 <7) {
	    		if (i % 8 > 0) {
	        		for (int j = (i / 8) - 1; j >= 0; j--) {
	        			result += letters[i % 8 - 1] + digits[j] + " | ";
	        		}
	    		}
	    		
	    		if (i % 8 < 7) {
	        		for (int j = (i / 8) - 1; j >= 0; j--) {
	        			result += letters[i % 8 + 1] + digits[j] + " | ";
	        		}
	    		}
    		}

    		if (result.endsWith(" = ")) {
    			result += "0L";
    		}
    		if (result.endsWith(" | ")) {
    			result = result.substring(0, result.length() - 3);
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
    	result = "private static final long[] BLACK_POSSIBLE_ATTACKS_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_POSSIBLE_ATTACKS_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhitePasserPerimeter() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int letter = i % 8;
    		int digit = i /  8;
    		String curLetter = letters[letter];
    		String curDigit = digits[digit];
    		
    		result = "private static final long WHITE_PASSER_PARAM_" + curLetter + curDigit + " = ";
    		
    		int to = letter + (7 - digit);
    		if (to > 7) {
    			to = 7;
    		}
    		for (int j = letter; j <= to; j++) {
    			for (int k=digit; k <= 7; k++) {
    				if (j != letter || k != digit) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		to = letter - (7 - digit);
    		if (to < 0) {
    			to = 0;
    		}
    		for (int j = letter - 1; j >= to; j--) {
    			for (int k=digit; k <= 7; k++) {
    				if (j != letter || k != digit) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_PASSER_PARAM_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_PASSER_PARAM_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackPasserPerimeter() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int letter = i % 8;
    		int digit = i /  8;
    		String curLetter = letters[letter];
    		String curDigit = digits[digit];
    		
    		result = "private static final long BLACK_PASSER_PARAM_" + curLetter + curDigit + " = ";
    		
    		int to = letter + digit;
    		if (to > 7) {
    			to = 7;
    		}
    		for (int j = letter; j <= to; j++) {
    			for (int k=digit; k >= 0; k--) {
    				if (j != letter || k != digit) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		to = letter - digit;
    		if (to < 0) {
    			to = 0;
    		}
    		for (int j = letter - 1; j >= to; j--) {
    			for (int k=digit; k >= 0; k--) {
    				if (j != letter || k != digit) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_PASSER_PARAM_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_PASSER_PARAM_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhitePasserExtPerimeter() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int letter = i % 8;
    		int digit = i /  8;
    		String curLetter = letters[letter];
    		String curDigit = digits[digit];
    		
    		result = "private static final long WHITE_PASSER_EXT_PARAM_" + curLetter + curDigit + " = ";
    		
    		int to = letter + (7 - digit) + 1;
    		if (to > 7) {
    			to = 7;
    		}
    		for (int j = letter; j <= to; j++) {
    			for (int k=digit>0 ? digit - 1 : digit; k <= 7; k++) {
    				if (j != letter || k != (digit>0 ? digit - 1 : digit)) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		to = letter - (7 - digit) - 1;
    		if (to < 0) {
    			to = 0;
    		}
    		for (int j = letter - 1; j >= to; j--) {
    			for (int k=digit>0 ? digit - 1 : digit; k <= 7; k++) {
    				if (j != letter || k != (digit>0 ? digit - 1 : digit)) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] WHITE_PASSER_EXT_PARAM_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "WHITE_PASSER_EXT_PARAM_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_BlackPasserExtPerimeter() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		int letter = i % 8;
    		int digit = i /  8;
    		String curLetter = letters[letter];
    		String curDigit = digits[digit];
    		
    		result = "private static final long BLACK_PASSER_EXT_PARAM_" + curLetter + curDigit + " = ";
    		
    		int to = letter + digit + 1;
    		if (to > 7) {
    			to = 7;
    		}
    		for (int j = letter; j <= to; j++) {
    			for (int k=digit < 7 ? digit + 1 : digit; k >= 0; k--) {
    				if (j != letter || k != (digit < 7 ? digit + 1 : digit)) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		to = letter - digit - 1;
    		if (to < 0) {
    			to = 0;
    		}
    		for (int j = letter - 1; j >= to; j--) {
    			for (int k=digit < 7 ? digit + 1 : digit; k >= 0; k--) {
    				if (j != letter || k != (digit < 7 ? digit + 1 : digit)) {
    					result +=  " | ";
    				}
        			result +=  letters[j] + digits[k];	
    			}
    		}
    		
    		result += ";";
    		
    		System.out.println(result);
    	}
    	
		result = "private static final long[] BLACK_PASSER_EXT_PARAM_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result += "BLACK_PASSER_EXT_PARAM_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result+="};";
    		}
    		
    	}
    	System.out.println(result);
	}
	
	private static void genMembers_WhiteCanBeSupported() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result_left = "";
    	String result_right = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result_left = "private static final long WHITE_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + " = ";
    		result_right = "private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + " = ";
    		
    		if (i % 8 > 0) {
    		  int cur = 0;
    			while (((cur < 3 && i / 8 != 4) || (cur <= 3 && i / 8 == 4))
    					&& i / 8 - cur > 0 && i / 8 - cur < 7) {
    				result_left += letters[i % 8 - 1] + digits[i / 8 - cur] + " | ";
    				cur++;
    			}
    		}
    		
    		if (i % 8 < 7) {
    			int cur = 0;
    			while (((cur < 3 && i / 8 != 4) || (cur <= 3 && i / 8 == 4))
    					&& i / 8 - cur > 0 && i / 8 - cur < 7) {
    				result_right += letters[i % 8 + 1] + digits[i / 8 - cur] + " | ";
    				cur++;
    			}
    		}

    		if (result_left.endsWith(" = ")) {
    			result_left += "0L";
    		}
    		if (result_left.endsWith(" | ")) {
    			result_left = result_left.substring(0, result_left.length() - 3);
    		}
    		result_left += ";";
    		System.out.println(result_left);
    		
    		if (result_right.endsWith(" = ")) {
    			result_right += "0L";
    		}
    		if (result_right.endsWith(" | ")) {
    			result_right = result_right.substring(0, result_right.length() - 3);
    		}
    		
    		result_right += ";";
    		System.out.println(result_right);
    	}
    	
    	result_left = "private static final long[] WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result_left += "WHITE_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result_left+="};";
    		}
    		
    	}
    	System.out.println(result_left);
    	
    	result_right = "private static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result_right += "WHITE_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result_right+="};";
    		}
    		
    	}
    	System.out.println(result_right);
	}
	
	private static void genMembers_BlackCanBeSupported() {
		String[] letters = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
    	String[] digits = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
    	String result_left = "";
    	String result_right = "";
    	
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		
    		result_left = "private static final long BLACK_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + " = ";
    		result_right = "private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + " = ";
    		
    		if (i % 8 > 0) {
    		  int cur = 0;
    			while (((cur < 3 && i / 8 != 3) || (cur <= 3 && i / 8 == 3))
    					&& i / 8 + cur > 0 && i / 8 + cur < 7) {
    				result_left += letters[i % 8 - 1] + digits[i / 8 + cur] + " | ";
    				cur++;
    			}
    		}
    		
    		if (i % 8 < 7) {
    			int cur = 0;
    			while (((cur < 3 && i / 8 != 3) || (cur <= 3 && i / 8 == 3))
    					&& i / 8 + cur > 0 && i / 8 + cur < 7) {
    				result_right += letters[i % 8 + 1] + digits[i / 8 + cur] + " | ";
    				cur++;
    			}
    		}

    		if (result_left.endsWith(" = ")) {
    			result_left += "0L";
    		}
    		if (result_left.endsWith(" | ")) {
    			result_left = result_left.substring(0, result_left.length() - 3);
    		}
    		result_left += ";";
    		System.out.println(result_left);
    		
    		if (result_right.endsWith(" = ")) {
    			result_right += "0L";
    		}
    		if (result_right.endsWith(" | ")) {
    			result_right = result_right.substring(0, result_right.length() - 3);
    		}
    		
    		result_right += ";";
    		System.out.println(result_right);
    	}
    	
    	result_left = "private static final long[] BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result_left += "BLACK_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result_left+="};";
    		}
    		
    	}
    	System.out.println(result_left);
    	
    	result_right = "private static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {";
    	for (int i=0; i<Bits.NUMBER_64; i++) {
    		String curLetter = letters[i % 8];
    		String curDigit = digits[i / 8];
    		result_right += "BLACK_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + ", ";
    		if (i == Bits.NUMBER_64 - 1) {
    			result_right+="};";
    		}
    		
    	}
    	System.out.println(result_right);
	}
	
	
	public static void main(String[] args) {
		genMembers_WhitePassed();
		genMembers_BlackPassed();
		/*genMembers_WhiteBackward();
		genMembers_BlackBackward();
		genMembers_WhiteDoubled();
		genMembers_BlackDoubled();
		genMembers_WhiteSupport();
		genMembers_BlackSupport();
		genMembers_WhitePossibleAttacks();
		genMembers_BlackPossibleAttacks();*/
		
		//genMembers_WhitePasserPerimeter();
		//genMembers_BlackPasserPerimeter();

		//genMembers_WhitePasserExtPerimeter();
		//genMembers_BlackPasserExtPerimeter();
		
		//genMembers_WhiteCanBeSupported();
		//genMembers_BlackCanBeSupported();
	}
}
