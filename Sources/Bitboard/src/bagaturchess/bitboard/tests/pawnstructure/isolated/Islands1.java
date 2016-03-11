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
package bagaturchess.bitboard.tests.pawnstructure.isolated;

import org.junit.Test;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;

public class Islands1 extends PawnStructureTest {
	
	@Override
	public String getFEN() {
		return "7k/8/8/8/8/8/8/7K b";
	}

	@Test
	public void validate() {
		//System.out.println(bitboard);
		validateIslandsCount(Figures.COLOUR_WHITE, 0);
		validateIslandsCount(Figures.COLOUR_BLACK, 0);
		validateOpenedFiles(LETTER_A | LETTER_B | LETTER_C | LETTER_D | LETTER_E | LETTER_F | LETTER_G | LETTER_H);
		
		validateKingOpenedAndSemiOpened(Figures.COLOUR_WHITE, 2, 0, 0);
		validateKingOpenedAndSemiOpened(Figures.COLOUR_BLACK, 2, 0, 0);
	}
}
