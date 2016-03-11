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
package bagaturchess.bitboard.tests.pawnstructure.passers;

import org.junit.Test;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;

public class Passers2 extends PawnStructureTest {
	
	@Override
	public String getFEN() {
		return "7k/p1p1p1p1/3p4/8/8/8/1P3P1P/7K b";
	}
	
	@Test
	public void validate() {
		//System.out.println(bitboard);
		validatePassers(Figures.COLOUR_WHITE, 0L, 0);
		validatePassers(Figures.COLOUR_BLACK, D6, 2);
		validateUnstoppablePassers(Figures.COLOUR_WHITE, 0L);
		validateUnstoppablePassers(Figures.COLOUR_BLACK, 0L);
		validateHalfOpenedFiles(Figures.COLOUR_WHITE, LETTER_A | LETTER_C | LETTER_D | LETTER_E | LETTER_G);
		validateHalfOpenedFiles(Figures.COLOUR_BLACK, LETTER_B | LETTER_F | LETTER_H);
		validateOpenedFiles(0L);
	}
}
