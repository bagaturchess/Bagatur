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
package bagaturchess.tools.opening.generator.run;


import java.io.File;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.impl.model.OpeningBookImpl_FullEntries;
import bagaturchess.tools.opening.generator.impl.OpeningGamesIterator;
import bagaturchess.tools.pgn.api.IGameIterator;
import bagaturchess.tools.pgn.api.PGNParser;


public class OpeningsGenerator {
	
	public static void main(String[] args) {
		
		//File w_root = new File("./../WorkDir/white_winners.pgn");
		//File b_root = new File("./../WorkDir/black_winners.pgn");
		//File w_root = new File("./../PGNProcessor/white2300.pgn");
		//File b_root = new File("./../PGNProcessor/black2300.pgn");
		File w_root = new File("white_winners.zip");
		File b_root = new File("black_winners.zip");
		
		if (!w_root.exists() || !b_root.exists()) {
			throw new IllegalStateException();
		}
		
		try {
			PGNParser parser = new PGNParser();
			
			OpeningBook whiteOpenings = new OpeningBookImpl_FullEntries();
			IGameIterator wgi = new OpeningGamesIterator(Figures.COLOUR_WHITE, whiteOpenings);
			parser.importPGNGamesInDir(w_root, wgi, true);
			whiteOpenings.store("w.ob");
			
			OpeningBook blackOpenings = new OpeningBookImpl_FullEntries();			
			IGameIterator bgi = new OpeningGamesIterator(Figures.COLOUR_BLACK, blackOpenings);
			parser.importPGNGamesInDir(b_root, bgi, true);
			blackOpenings.store("b.ob");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
