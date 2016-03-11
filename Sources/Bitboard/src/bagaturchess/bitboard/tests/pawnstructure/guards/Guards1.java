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
package bagaturchess.bitboard.tests.pawnstructure.guards;

import org.junit.Test;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;

public class Guards1 extends PawnStructureTest {
	
	@Override
	public String getFEN() {
		return "7k/7p/1pP5/8/8/8/5PPP/7K b";
	}

	@Test
	public void validate() {
		//System.out.println(bitboard);
		validateGuards(Figures.COLOUR_WHITE, G2 | H2, 2);
		validateGuards(Figures.COLOUR_BLACK, H7, 1);
		//validatePassers(Figures.COLOUR_WHITE, C2);
		//validatePassers(Figures.COLOUR_BLACK, A7);
		//validateUnstoppablePassers(Figures.COLOUR_WHITE, 0L);
		//validateUnstoppablePassers(Figures.COLOUR_BLACK, A7);
		validateKingOpenedAndSemiOpened(Figures.COLOUR_WHITE, 0, 0, 1);
		validateKingOpenedAndSemiOpened(Figures.COLOUR_BLACK, 0, 1, 0);
	}
}
