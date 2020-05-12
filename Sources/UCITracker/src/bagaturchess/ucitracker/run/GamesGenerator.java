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
package bagaturchess.ucitracker.run;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;
import bagaturchess.ucitracker.impl.gamemodel.serialization.GameModelWriter;


public class GamesGenerator {
	
	
	private static int SEARCH_DEPTH_MIN = 2;
	private static int SEARCH_DEPTH_MAX = 10;
	
	private static int MAX_EVAL_DIFF = 1500;
	private static int MAX_MOVES = 300;
	private static int BEST_MOVE_DIFF = 50;
	private static int MIN_PIECES = 8;
	
	
	private UCIEnginesManager runner;
	
	
	public GamesGenerator() {
		runner = new UCIEnginesManager();
	}
	
	
	public static void main(String[] args) {
		
		ChannelManager.setChannel(new Channel_Console());
		GamesGenerator control = new GamesGenerator();
		try {
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\Houdini_15a\\Houdini_15a_w32.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\Houdini_15a\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\arasan13.1\\arasanx.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\arasan13.1\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\Gandalf.6.0.UCI\\gandalf60_new.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\Gandalf.6.0.UCI\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\stockfish-211-ja\\Windows\\stockfish-211-32-ja.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\stockfish-211-ja\\Windows\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\Bison9.11\\Bison9.11w32.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\Bison9.11\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\Critter_1.01\\Critter_1.01_32bit.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\Critter_1.01\\");*/
			
			/*Engine engine = new Engine("C:\\own\\chess\\ENGINES\\komodo-13b1-ja\\Windows\\komodo-13b1-32-ja.exe",
					new String [0],
				"C:\\own\\chess\\ENGINES\\komodo-13b1-ja\\Windows\\");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\Glaurung2.2\\glaurung-w64.exe",
					new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\Glaurung2.2\\");
			*/
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka\\Rybkav2.3.2a.mp.x64.exe",
					new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka");
			*/
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107\\texel64.exe",
					new String [0],
					"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107");*/
			
			EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\stockfish-10-win\\Windows\\stockfish_10_x64.exe",
					new String [0],
					"C:\\DATA\\Engines\\stockfish-10-win\\Windows");
	
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Komodo9\\Windows\\komodo-9.02-64bit.exe",
					new String [0],
					"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Komodo9\\Windows\\");
			*/
			
			//EngineProcess engine = new EngineProcess_BagaturImpl_WorkspaceImpl("BagaturEngineClient", "");
			
			control.execute(engine, "./stockfish-10.cg", 1000000, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void execute(EngineProcess engine, String toFileName, int gamesCount, boolean appendToFile) throws IOException {
		
		runner.addEngine(engine);
		
		runner.startEngines();
		runner.uciOK();
		runner.isReady();
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(toFileName, appendToFile), 10 * 1024 * 1024));
		
		for (int i=0; i<gamesCount; i++) {
			EvaluatedGame game = playGame();
			GameModelWriter.writeEvaluatedGame(game, dos);
			dos.flush();
			System.out.println("Game " + (i+1) + " saved in " + toFileName);
		}
		
		dos.close();
		
		runner.destroyEngines();
	}
	
	
	private EvaluatedGame playGame() throws IOException {
		
		runner.newGame();
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
		
		EvaluatedGame game = new EvaluatedGame(bitboard.toEPD());
		
		while (bitboard.getStatus().equals(IGameStatus.NONE)) {
			
			Set<EvaluatedMove> movesEvals = evalVariations(bitboard);
			if (movesEvals.size() == 0) {
				break;
			}
			
			EvaluatedMove best = getBestVariation(bitboard.isInCheck(), movesEvals);
			
			//if (bitboard.getPlayedMovesCount() == 20) {
			//	System.out.println(bitboard);
			//}
			
			//System.out.println(bitboard);
			//System.out.println("BEST MOVE: " + best);			
			bitboard.makeMoveForward(best.getMoves()[0]);
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				break;
			}
			if (Math.abs(best.eval_ofOriginatePlayer()) > MAX_EVAL_DIFF) {
				break;
			}
			if (bitboard.getPlayedMovesCount() > MAX_MOVES) {
				break;
			}
			if (bitboard.getMaterialState().getPiecesCount() < MIN_PIECES) {
				break;
			}
			
			EvaluatedPosition position = new EvaluatedPosition(bitboard.toEPD(), best.getMoves()[0]);
			position.setChildren(movesEvals);
			game.addBoard(position);
		}
		
		//System.out.println(bitboard);
		//System.out.println(game);
		
		return game;
	}

	
	private EvaluatedMove getBestVariation(boolean inCheck, Set<EvaluatedMove> evals) {
		
		EvaluatedMove best = null;
		if (inCheck) {
			best = evals.iterator().next();
		} else {
			best = evals.iterator().next();
			int besteval = best.eval_ofOriginatePlayer();
			int size = 0;
			for (EvaluatedMove cur: evals) {
				if (cur.eval_ofOriginatePlayer() + BEST_MOVE_DIFF < besteval) {
					break;
				}
				size++;
			}
			
			int index = (int) (Math.random() * size);
			//System.out.println("index=" + index);
			
			int counter1 = 0;
			for (EvaluatedMove cur: evals) {
				if (counter1 == index) {
					best = cur;
					break;
				}
				counter1++;
			}
		}

		return best;
	}
	
	
	private Set<EvaluatedMove> evalVariations(IBitBoard bitboard) throws IOException {

		IMoveList moves = new BaseMoveList(150);
		
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(moves);
		} else {
			bitboard.genAllMoves(moves);
		}
		
		Set<EvaluatedMove> evals = new TreeSet<EvaluatedMove>();
		
		int cur_move = 0;
		while ((cur_move = moves.next()) != 0) {
			
			bitboard.makeMoveForward(cur_move);
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				//evals.add(new EvaluatedMove(cur_move, bitboard.getStatus()));
				bitboard.makeMoveBackward(cur_move);
				continue;
			} else {
				bitboard.makeMoveBackward(cur_move);
			}
			
			String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
			String moveStr = bitboard.getMoveOps().moveToString(cur_move);
					
			//System.out.println("startpos moves " + allMovesStr + moveStr);
			//System.out.println("MOVE " + moveStr);
			
			
			List<String> infos = null;
			
			int depth = SEARCH_DEPTH_MIN;
			
			boolean loop = true;
			while (loop) {
				
				runner.setupPosition("startpos moves " + allMovesStr + moveStr);
				runner.disable();

				runner.go_Depth(depth);
				
				infos = runner.getInfoLines();
				if (infos.size() > 1) {
					throw new IllegalStateException("Only one engine is supported");
				}
				
				depth++;
				if (depth > SEARCH_DEPTH_MAX) {
					throw new IllegalStateException("depth >  " + SEARCH_DEPTH_MAX + " and no PV info");
				}
				
				if (infos.size() == 0 || infos.get(0) == null) {
					//Continue
				} else {
					loop = false;
				}
				//System.out.println("depth " + depth);
			}
			
			//System.out.println("OUT");
			
			runner.enable();
			
			
			for (String info: infos) {
				if (info != null) {
					EvaluatedMove move = new EvaluatedMove(bitboard, cur_move, info);
					if (move.getStatus() == IGameStatus.NONE) {
						//TODO: Still need for double check with engine for fast wins (with bigger depth)
						evals.add(move);
					}
				}
			}
		}
		
		return evals;
	}
}
