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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;
import bagaturchess.ucitracker.impl.gamemodel.serialization.GameModelWriter;


public class GamesGenerator_MultiPv {
	
	
	private static final boolean USE_FEN = false;
	
	
	private static int SEARCH_DEPTH_MIN = 1;
	private static int SEARCH_DEPTH_MAX = 10;
	
	private static int MAX_EVAL_DIFF = 3000;
	private static int MAX_MOVES = 300;
	private static int BEST_MOVE_DIFF = 10000;
	private static int MIN_PIECES = 8;
	
	
	private UCIEnginesManager runner;
	
	private String initial_fen;
	
	
	public GamesGenerator_MultiPv(String _initial_fen) {
		
		runner = new UCIEnginesManager();
		
		initial_fen = _initial_fen;
	}
	
	
	public static void main(String[] args) {
		
		ChannelManager.setChannel(new Channel_Console());
		
		GamesGenerator_MultiPv control = new GamesGenerator_MultiPv(Constants.INITIAL_BOARD);
		//GamesGenerator_MultiPv control = new GamesGenerator_MultiPv("8/p5pp/1pk5/5p2/P1nn4/2NN3P/5PPK/8 w - - 0 1");
		
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
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka\\Rybkav2.3.2a.mp.x64.exe",
					new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka");
			*/
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107\\texel64.exe",
					new String [0],
					"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\stockfish-NNUE\\sf-nnue-bmi2.exe",
					new String [0],
					"C:\\DATA\\Engines\\stockfish-NNUE");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\lc0-v0.25.1-windows-cpu-openblas\\lc0.exe",
					new String [0],
					"C:\\DATA\\Engines\\lc0-v0.25.1-windows-cpu-openblas\\");*/
			
			EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\stockfish_14.1_win_x64_popcnt\\stockfish_14.1_win_x64_popcnt.exe",
					new String [0],
					"C:\\DATA\\OWN\\stockfish_14.1_win_x64_popcnt");
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Glaurung2.2\\glaurung-w64.exe",
					new String [0],
				"C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Glaurung2.2");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Pedone_3.1\\Pedone_win.exe",
					new String [0],
					"C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Pedone_3.1");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Fat_titz_v1.1\\fat_titz_windows_modern.exe",
					new String [0],
					"C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\Fat_titz_v1.1");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\WASP_500-nn\\Wasp500-windows.exe",
					new String [0],
					"C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\WASP_500-nn");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.2.3\\Bagatur_64_1_core.exe",
					new String [0],
					"C:\\DATA\\OWN\\BAGATUR\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.2.3");*/
			
			
			//EngineProcess engine = new EngineProcess_BagaturImpl_WorkspaceImpl("BagaturEngineClient", "");
			
			control.execute(engine, "./stockfish-14.1.cg", 1000000, true);
			//control.execute(engine, "./stockfish-14.1-4N.cg", 1000000, true);
			//control.execute(engine, "./glaurung-2.2.cg", 1000000, true);
			//control.execute(engine, "./pedone-3.1.cg", 1000000, true);
			//control.execute(engine, "./wasp-5-0-0.cg", 1000000, true);
			//control.execute(engine, "./bagatur-2.3.cg", 1000000, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void execute(EngineProcess engine, String toFileName, int gamesCount, boolean appendToFile) throws IOException {
		
		
		runner.addEngine(engine);
		
		
		for (int i=0; i<gamesCount; i++) {
			
			runner.startEngines();
			runner.uciOK();
			
			List<String> options = new ArrayList<String>();
			options.add("setoption name MultiPV value 99");
			options.add("setoption name Ponder value false");
			options.add("setoption name OwnBook value false");
			options.add("setoption name SyzygyPath value c:\\dummy");
			
			runner.setOptions(options);
			
			runner.isReady();
			
			
			EvaluatedGame game = playGame();
			
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(toFileName, appendToFile), 10 * 1024 * 1024));
			
			GameModelWriter.writeEvaluatedGame(game, dos);
			
			dos.flush();
			
			dos.close();
			
			System.out.println("Game " + (i+1) + " saved in " + toFileName);
			
			runner.stopEngines();
		}
		
		runner.destroyEngines();
	}
	
	
	private EvaluatedGame playGame() throws IOException {
		
		runner.newGame();
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(initial_fen);
		
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
			
			//if (bitboard.isInCheck()) System.out.println("isInCheck " + best.getMoves().length);
			//System.out.println(best.getMoves().length);
			
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
		
		
		Set<EvaluatedMove> evals = new TreeSet<EvaluatedMove>();
			
		List<String> infos = null;
		
		int depth = SEARCH_DEPTH_MIN;
		//int depth = (Math.random() >= 0.5) ? SEARCH_DEPTH_MIN : SEARCH_DEPTH_MIN + 1;
		
		boolean loop = true;
		
		while (loop) {
			
			if (USE_FEN) {
				
				runner.setupPosition("fen " + bitboard.toEPD());
				
			} else {
				
				String allMovesStr = BoardUtils.getPlayedMoves(bitboard);
				
				runner.setupPosition("startpos moves " + allMovesStr);
			}
			
			runner.disable();
			
			runner.go_Depth(depth);
			
			try {
				
				infos = runner.getInfoLines(new LineCallBack() {
					
					
					private List<String> lines = new ArrayList<String>();
					private String exitLines = null; 
					
					
					@Override
					public void newLine(String line) {
						
						//System.out.println("EngineProcess: getInfoLine new line is: '" + line + "'");
						
						if (line.contains("LOG")) {
							return;
						}
						
						lines.add(line);
						
						if (line.contains("bestmove")) {
							for (int i=lines.size() - 1; i >=0; i--) {
								
								//System.out.println("EngineProcess: getInfoLine " + lines.get(i));
								
								if (lines.get(i).contains("info "/*depth"*/) && lines.get(i).contains(" pv ")) {
									if (exitLines == null) {
										exitLines = lines.get(i) + ";";
									} else {
										exitLines += lines.get(i) + ";";
									}	
								}
							}
							if (exitLines == null) {
								throw new IllegalStateException("No pv: " + lines);
							}
						}
					}
					
					
					@Override
					public String exitLine() {
						return exitLines;
					}
				});
				
			} catch (java.lang.IllegalStateException ise) {
				//No pv
			}
			
			if (infos != null && infos.size() > 1) {
				
				throw new IllegalStateException("Only one engine is supported");
			}
			
			if (infos == null || infos.size() == 0 || infos.get(0) == null) {
				
				depth++;
				
				if (depth > SEARCH_DEPTH_MAX) {
					
					throw new IllegalStateException("depth >  " + SEARCH_DEPTH_MAX + " and no PV info");
				}
				
			} else {
				
				loop = false;
			}
			
			//System.out.println("depth " + depth);
		}
		
		
		runner.enable();
		
		
		for (String info: infos) {
			
			StringTokenizer st = new StringTokenizer(info, ";");
			
			while (st.hasMoreTokens()) {
				
				EvaluatedMove move = new EvaluatedMove(bitboard, st.nextToken());
				
				if (move.getStatus() == IGameStatus.NONE) {
					
					evals.add(move);
				}
			}
		}
		
		
		return evals;
	}
}
