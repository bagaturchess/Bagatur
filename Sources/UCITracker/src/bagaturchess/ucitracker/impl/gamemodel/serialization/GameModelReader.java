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
package bagaturchess.ucitracker.impl.gamemodel.serialization;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;


public class GameModelReader {
	
	
	public static void readListOfgames(DataInputStream dis) throws IOException {
		
		int counter = 0;
		int allStates = 0;
		EvaluatedGame game = null;
		while ((game = readEvaluatedGame(dis)) != null) {
			counter++;
			
			List<EvaluatedPosition> boards = game.getBoardStates();
			for (int j=0; j<boards.size(); j++) {
				
				EvaluatedPosition board = boards.get(j);
				
				Set<EvaluatedMove> moves = board.getChildren();
				
				for (int k=0; k<moves.size(); k++) {
					allStates++;
				}
			}
			
			if ((counter % 1000) == 0) {
				System.out.println("Readed dames count: " + counter + ", states: " + allStates);
			}
		}
	}
	
	
	public static EvaluatedGame readEvaluatedGame(DataInputStream dis) throws IOException {
		
		EvaluatedGame game = new EvaluatedGame(Constants.INITIAL_BOARD);
		
		int openingLengh = -1;
		try {
			openingLengh = dis.readInt();
		} catch(java.io.EOFException eof) {
			return null;//End of dis stream
		}
		
		int[] opening = new int[openingLengh];
		for (int i=0; i<openingLengh; i++) {
			opening[i] = dis.readInt();
		}
		
		int positionsLengh = dis.readInt();
		
		for (int i=0; i<positionsLengh; i++) {
			game.addBoard(readEvaluatedPosition(dis));
		}
		
		
		return game;
	}
	
	
	private static EvaluatedPosition readEvaluatedPosition(DataInputStream dis) throws IOException {
		
		int originateMove = dis.readInt();
		int otherMovesSize = dis.readInt();
		
		EvaluatedPosition position = new EvaluatedPosition(null, originateMove);
		
		Set<EvaluatedMove> emoves = new HashSet<EvaluatedMove>(otherMovesSize); 	
		for (int i=0; i<otherMovesSize; i++) {
			emoves.add(readEvaluatedMove(dis));
		}
		
		position.setChildren(emoves);
		
		return position;
	}
	
	
	private static EvaluatedMove readEvaluatedMove(DataInputStream dis) throws IOException {
		
		int eval = dis.readInt();
		int statusOrdinal = dis.readInt();
		
		int movesLengh = dis.readInt();
		int[] moves = new int[movesLengh];
		for (int i=0; i<movesLengh; i++) {
			moves[i] = dis.readInt();
		}
		
		EvaluatedMove move = new EvaluatedMove(eval, statusOrdinal, moves);
		
		return move;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("./DATA/Houdini.15a.cg"), 10 * 1024 * 1024));
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			readListOfgames(dis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");
		
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
