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

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Board;

public class ModelBuilder {
	
	static PawnsModel tmp = new PawnsModel();
	
	/*public static void build(String fen, PawnsModel pawns) {
		IBitBoard bitboard = new Board(fen);
		pawns.rebuild(bitboard);
	}*/
	public static PawnsModel build(IBitBoard bitboard) {
		tmp.rebuild(bitboard);
		//System.out.println(bitboard);
		return tmp;
	}
	
	/*public static PawnsModel build(String fen) {
		IBitBoard bitboard = new Board(fen);
		tmp.rebuild(bitboard);
		//System.out.println(bitboard);
		return tmp;
	}*/
}
