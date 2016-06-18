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
package bagaturchess.tools.opening.generator.impl;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.tools.pgn.api.IPlyIterator;
import bagaturchess.tools.pgn.impl.PGNGame;


public class OpeningGamesIterator implements IPlyIterator {

	private static int MAX_MOVES = 30;
	
	private int colour;
	private OpeningBook openings;
	
	private int counter = 0;
	
	private int result;
	
	
	public OpeningGamesIterator(int _colour, OpeningBook _openings) {
		colour = _colour;
		openings = _openings;
	}
	
	@Override
	public void postGame() {
		counter++;
		if (counter != 0 && counter % 10000 == 0) {
			System.out.println("Processed games count: " + counter);
		}
	}
	
	@Override
	public void postIteration() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void postMove() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID, IBoard bitboard) {
		
		result = -2;
		
		String resultStr = pgnGame.getResult();
		
		if (colour == Figures.COLOUR_WHITE) {
			if (resultStr.equals("1-0")) {
				result = 1;
			} else if (resultStr.equals("1/2-1/2")) {
				result = 0;
			} else if (resultStr.equals("0-1")) {
				result = -1;
			} else {
				//throw new IllegalStateException("resultStr=" + resultStr);
			}
		} else {
			if (resultStr.equals("1-0")) {
				result = -1;
			} else if (resultStr.equals("1/2-1/2")) {
				result = 0;
			} else if (resultStr.equals("0-1")) {
				result = 1;
			} else {
				//throw new IllegalStateException("resultStr=" + resultStr);
			}
		}
	}

	@Override
	public void preIteration(IBoard bitboard) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preMove(int colourToMove, int move, IBoard bitboard, int moveNumber) {
		if (colour == colourToMove) {
			
			if (moveNumber > MAX_MOVES
					|| result == -2
				) {
				return;
			}
			
			long hashkey_before = bitboard.getHashKey();
			
			openings.add(hashkey_before, move, result);
			
			//System.out.println(bitboard);
		}
	}
}
