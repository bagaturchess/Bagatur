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
package bagaturchess.ucitracker.api;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;
import bagaturchess.ucitracker.impl.gamemodel.serialization.GameModelReader;


public class PositionsTraverser {
	
	
	public static void traverseAll(String filePath, PositionsVisitor visitor, int maxPositionsCount, IBoardConfig boardConfig, String pawnsCache) throws IOException {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath), 10 * 1024 * 1024));
		
		IBitBoard bitboard = null;
		if (pawnsCache != null) {
			bitboard = BoardUtils.createBoard_WithPawnsCache(pawnsCache, boardConfig);
		} else {
			bitboard = BoardUtils.createBoard_WithPawnsCache(boardConfig);
		}
		
		visitor.begin(bitboard);
		
		traverseAllGames(dis, visitor, bitboard, maxPositionsCount);
		
		visitor.end();
		
		try {
			dis.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	public static void traverseAll(String filePath, PositionsVisitor visitor, int maxPositionsCount, IBoardConfig boardConfig) throws IOException {
		traverseAll(filePath, visitor, maxPositionsCount, boardConfig, null);
	}
	
	public static void traverseAll(String filePath, PositionsVisitor visitor, int maxPositionsCount) throws IOException {
		traverseAll(filePath, visitor, maxPositionsCount, null);
	}
	
	public static void traverseAll(String filePath, PositionsVisitor visitor) throws IOException {
		traverseAll(filePath, visitor, Integer.MAX_VALUE);
	}
	
	
	private static void traverseAllGames(DataInputStream dis, PositionsVisitor visitor, IBitBoard bitboard, int maxPositionsCount) throws IOException {
		
		EvaluatedGame game = null;
		while ((game = GameModelReader.readEvaluatedGame(dis)) != null) {
			
			List<EvaluatedPosition> boards = game.getBoardStates();
			for (int j=0; j<boards.size(); j++) {
				
				EvaluatedPosition board = boards.get(j);
				
				Set<EvaluatedMove> emoves = board.getChildren();
				
				for (EvaluatedMove emove: emoves) {
					int eval_ofOriginatePlayer = emove.eval_ofOriginatePlayer();
					IGameStatus status = emove.getStatus();
					
					if (status == IGameStatus.NONE) { //Still possible to have forced win
						int[] moves = emove.getMoves();
						
						int whitePlayerEval = eval_ofOriginatePlayer;
						if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
							whitePlayerEval = -whitePlayerEval;
						}
						
						for (int k=0; k<moves.length; k++) {
							bitboard.makeMoveForward(moves[k]);
						}
						
						visitor.visitPosition(bitboard, status, whitePlayerEval);
						
						for (int k=moves.length-1; k>=0; k--) {
							bitboard.makeMoveBackward(moves[k]);	
						}
						
						maxPositionsCount--;
						if (maxPositionsCount <= 0) {
							bitboard.revert();
							return;
						}
					}
				}
				
				int originateMove = board.getOriginateMove();
				bitboard.makeMoveForward(originateMove);
				//bitboard.makeMoveBackward(originateMove);	
			}
			
			bitboard.revert();

		}
	}
}
