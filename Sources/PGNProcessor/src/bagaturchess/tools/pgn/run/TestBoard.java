/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.tools.pgn.run;


import java.io.File;

import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.tools.pgn.api.IPlyIterator;
import bagaturchess.tools.pgn.api.PGNParser;
import bagaturchess.tools.pgn.impl.PGNGame;


public class TestBoard implements IPlyIterator {
	
	
	private int totalGamesCount;
	private IMoveList movesBuffer = new BaseMoveList();
	
	
	public static void main(String[] args) {
				
				//File root = new File("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\PGN\\twic");
				//File root = new File("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\PGN\\pgnmentor");
				File root = new File("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\PGN");
				
				if (!root.exists()) {
					throw new IllegalStateException();
				}
				
				try {
					PGNParser parser = new PGNParser();
					
					IPlyIterator py = new TestBoard();
					parser.importPGNGamesInDir(root, py, true);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
	}

	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IGameIterator#preIteration(bagaturchess.bitboard.api.IBoard)
	 */
	@Override
	public void preIteration(IBoard bitboard) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IGameIterator#postIteration()
	 */
	@Override
	public void postIteration() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IGameIterator#preGame(int, bagaturchess.tools.pgn.impl.PGNGame, java.lang.String, bagaturchess.bitboard.api.IBoard)
	 */
	@Override
	public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID,
			IBoard bitboard) {
		totalGamesCount++;
		if (totalGamesCount % 10000 == 0) {
			System.out.println("game " + totalGamesCount);
		}
	}

	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IGameIterator#postGame()
	 */
	@Override
	public void postGame() {
		// TODO Auto-generated method stub
		
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IPlyIterator#preMove(int, int, bagaturchess.bitboard.api.IBoard, int)
	 */
	@Override
	public void preMove(int colour, int move, IBoard bitboard, int moveNumber) {
		
		testBoard(bitboard);
		
		movesBuffer.clear();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(movesBuffer);
		} else {
			bitboard.genAllMoves(movesBuffer);
		}
		
		int cur_move = 0;
		while ((cur_move = movesBuffer.next()) != 0) {
			bitboard.makeMoveForward(cur_move);
			testBoard(bitboard);
			bitboard.makeMoveBackward(cur_move);
		}
	}
	
	
	private void testBoard(IBoard bitboard) {
		bitboard.getStatus();
		bitboard.isInCheck();
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.tools.pgn.api.IPlyIterator#postMove()
	 */
	@Override
	public void postMove() {
		// TODO Auto-generated method stub
	}
}
