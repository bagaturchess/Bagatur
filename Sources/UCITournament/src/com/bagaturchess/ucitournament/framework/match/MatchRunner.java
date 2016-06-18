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
package com.bagaturchess.ucitournament.framework.match;


import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.specials.Promotioning;
import bagaturchess.uci.impl.commands.info.Info;
import bagaturchess.ucitracker.impl.Engine;


public abstract class MatchRunner {
	
	
	private static int MAX_EVAL_DIFF = 1000;
	
	
	public MatchRunner() {
	}
	
	public abstract void newGame();
	protected abstract void beforeGo(int colourToMove);
	protected abstract void go(Engine engine) throws IOException;
	protected abstract void afterGo(int colourToMove);
	protected abstract int getRemainingTime(int colourToMove);
	
	public int execute(Engine white, Engine black) throws IOException {
		
		//white.start();
		white.supportsUCI();
		white.isReady();
		
		//black.start();
		black.supportsUCI();
		black.isReady();
		
		int result = playGame(white, black);
		return result;
	}
	
	
	private int playGame(Engine white, Engine black) throws IOException {
		
		int result = 0;
		
		white.newGame();
		black.newGame();
		
		IBitBoard bitboard  = BoardUtils.createBoard_WithPawnsCache();//new Board();
		
		Engine engine = white;
		while (gameIsOk(bitboard)) {
			
			//System.out.println("pinko");
			
			String allMovesStr = getMovesUCI(bitboard);
			
			engine.setupPossition("startpos moves " + allMovesStr);
			
			beforeGo(bitboard.getColourToMove());
			
			go(engine);
			String info = engine.getInfoLine();
			Info infoObj = new Info(info);
			//System.out.println(infoObj);
			
			afterGo(bitboard.getColourToMove());
			
			/**
			 * Check for time
			 */
			int time = getRemainingTime(bitboard.getColourToMove());
			if (time <= 0) {
				if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
					result = -1;
				} else {
					result = 1;
				}
				System.out.println("Out of time for engine: " + engine);
				break;
			}
			
			/**
			 * Check for big score diff
			 */
			int best_eval = infoObj.getEval();
			if (Math.abs(best_eval) > MAX_EVAL_DIFF) {
				if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
					result = best_eval > 0 ? 1 : -1;
				} else {
					result = best_eval > 0 ? -1 : 1;
				}
				break;
			}
			
			int best_move = BoardUtils.uciStrToMove(bitboard, infoObj.getPv()[0]);
			bitboard.makeMoveForward(best_move);
			if (!gameIsOk(bitboard)) {
				result = getResult(bitboard.getStatus());
				break;
			}
			
			engine = (engine == white ? black : white);
		}
		
		//System.out.println(bitboard);
		
		return result;
	}
	
	
	private boolean gameIsOk(IBitBoard bitboard) {
		return bitboard.getStatus().equals(IGameStatus.NONE)
			|| bitboard.getStatus().equals(IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL)
			|| bitboard.getStatus().equals(IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL);
	}
	
	
	private int getResult(IGameStatus status) {
		int result = 0;
		if (status == IGameStatus.DRAW_3_STATES_REPETITION) {
			result = 0;
		} else if (status == IGameStatus.DRAW_50_MOVES_RULE) {
			result = 0;
		} else if (status == IGameStatus.MATE_BLACK_WIN) {
			result = -1;
		} else if (status == IGameStatus.MATE_WHITE_WIN) {
			result = 1;
		} else if (status == IGameStatus.NO_SUFFICIENT_MATERIAL) {
			result = 0;
		} else if (status == IGameStatus.PASSER_BLACK) {
			result = -1;
		} else if (status == IGameStatus.PASSER_WHITE) {
			result = 1;
		} else if (status == IGameStatus.STALEMATE_BLACK_NO_MOVES) {
			result = 0;
		} else if (status == IGameStatus.STALEMATE_WHITE_NO_MOVES) {
			result = 0;
		} else {
			throw new IllegalStateException("status=" + status);
		}
		return result;
	}
	
	
	private static String getMovesUCI(IBitBoard bitboard) {
		
		String result = "";
		
		int count = bitboard.getPlayedMovesCount();
		int[] moves = bitboard.getPlayedMoves();
		for (int i=0; i<count; i++) {
			int curMove = moves[i];
			StringBuilder message = new StringBuilder(32);
			MoveInt.moveToStringUCI(curMove, message);
			result += message.toString() + " ";
		}
	
		return result;
	}	
}
