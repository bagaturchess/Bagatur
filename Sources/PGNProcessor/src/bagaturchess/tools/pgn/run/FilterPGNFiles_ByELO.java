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
package bagaturchess.tools.pgn.run;


import java.io.File;

import bagaturchess.tools.pgn.api.IGameIterator;
import bagaturchess.tools.pgn.api.PGNParser;


public class FilterPGNFiles_ByELO {
	
	
	public static void main(String[] args) {
		
		//http://www.gladiators-chess.ru/downloads.php?cat_id=2&rowstart=60
		
		//Directory with zipped files downloaded from http://www.chess.co.uk/twic/twic
		//String pgnFile = "C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\PGN";
		String pgnFile = ".\\all2500.zip";
		
		//IGameIterator myiter = new GameIterator_CollectAllInOne("./all.pgn");
		IGameIterator iter = new GameIterator_ELOFilter("all2600.pgn", 2600, 2600);
		//IGameIterator witer = new GameIterator_ExtractWinners("../WorkDir/white_winners.pgn", "1-0", 2600, -1);
		//IGameIterator biter = new GameIterator_ExtractWinners("../WorkDir/black_winners.pgn", "0-1", -1, 2600);
		//IGameIterator dummy = new DummyGameIterator();
		
		PGNParser parser = new PGNParser();
		try {
			//parser.importPGNGamesInDir(new File(pgnFile), dummy);
			parser.importPGNGamesInDir(new File(pgnFile), iter);
			//parser.importPGNGamesInDir(new File(pgnFile), new IGameIterator[] {myiter});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
