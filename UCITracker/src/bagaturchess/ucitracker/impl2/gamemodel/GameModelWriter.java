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
package bagaturchess.ucitracker.impl2.gamemodel;


import java.io.BufferedWriter;
import java.io.IOException;

import bagaturchess.ucitracker.impl2.gamemodel.EvaluatedGame;


public class GameModelWriter {
	
	public static void writeEvaluatedGame(EvaluatedGame game, BufferedWriter bw) throws IOException {
		
		for (int i = 0; i < game.fens.size(); i++) {
			String line = game.fens.get(i) + " | " + game.evals.get(i) + " | " + game.result;
			bw.write(line);
			bw.newLine();
			//System.out.println(line);
		}
	}
}
