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


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import bagaturchess.bitboard.api.IBoard;
import bagaturchess.tools.pgn.api.IGameIterator;
import bagaturchess.tools.pgn.impl.PGNGame;


public class GameIterator_ExtractWinners implements IGameIterator {
	
	private String outFileName;
	private String resultPattern;
	private int minPlayersElo_white;
	private int minPlayersElo_black;
	
	private OutputStream os;
	private int counter = 0;
	
	
	public GameIterator_ExtractWinners(String _fileName, String _resultPattern, int _minPlayersElo_white, int _minPlayersElo_black) {
		outFileName = _fileName;
		resultPattern = _resultPattern;
		minPlayersElo_white = _minPlayersElo_white;
		minPlayersElo_black = _minPlayersElo_black;
	}
	
	@Override
	public void preIteration(IBoard bitboard) {
		try {
			os = new FileOutputStream(outFileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void postIteration() {
		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID,
			IBoard bitboard) {
		
		String result = pgnGame.getResult();
		Integer w_Elo_Integer = pgnGame.getWhiteELO();
		Integer b_Elo_Integer = pgnGame.getBlackELO();
		int w_Elo = w_Elo_Integer == null ? 0 : w_Elo_Integer;
		int b_Elo = b_Elo_Integer == null ? 0 : b_Elo_Integer;
		
		if (w_Elo >= minPlayersElo_white && b_Elo >= minPlayersElo_black && resultPattern.equals(result)) {
			try {
				os.write("\r\n\r\n".getBytes());
				os.write(pgnGame.getGameSource().getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			counter++;
			if (counter % 1000 == 0) {
				System.out.println("counter=" + counter);
			}
		}
	}
	
	@Override
	public void postGame() {
	}
}
