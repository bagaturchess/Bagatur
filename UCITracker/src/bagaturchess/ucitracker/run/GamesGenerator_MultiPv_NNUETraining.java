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
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.UCIEnginesManager;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.ucitracker.impl2.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl2.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl2.gamemodel.GameModelWriter;


public class GamesGenerator_MultiPv_NNUETraining {
	
	
	private static final boolean USE_FEN = false;
	
	
	private static int SEARCH_DEPTH_MIN = 1;
	private static int SEARCH_DEPTH_MAX = 10;
	
	private static int BEST_MOVE_DIFF = 1; //Small randomness
	
	
	private UCIEnginesManager runner;
	
	private OpeningBook ob;
	
	
	public GamesGenerator_MultiPv_NNUETraining() throws FileNotFoundException, ClassNotFoundException, IOException {
		
		runner = new UCIEnginesManager();
		
		ob = OpeningBookFactory.load("./../WorkDir/data/w.ob", "./../WorkDir/data/b.ob");
	}
	
	
	public static void main(String[] args) {
		
		ChannelManager.setChannel(new Channel_Console());
		
		
		try {
			
			GamesGenerator_MultiPv_NNUETraining control = new GamesGenerator_MultiPv_NNUETraining();
			
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
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka\\Rybkav2.3.2a.mp.x64.exe",
					new String [0],
				"C:\\Users\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Rybka");
			*/
			
			/*EngineProcess engine = new EngineProcess("C:\\Users\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107\\texel64.exe",
					new String [0],
					"C:\\Users\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\texel107");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\stockfish-NNUE\\sf-nnue-bmi2.exe",
					new String [0],
					"C:\\DATA\\Engines\\stockfish-NNUE");*/
			
			/*EngineProcess engine = new EngineProcess("C:\\DATA\\Engines\\lc0-v0.25.1-windows-cpu-openblas\\lc0.exe",
					new String [0],
					"C:\\DATA\\Engines\\lc0-v0.25.1-windows-cpu-openblas\\");*/
			
			EngineProcess engine = new EngineProcess("C:\\DATA\\ARENA\\arena_3.5.1\\Engines\\stockfish-16.1-sse41-popcnt\\stockfish-windows-x86-64-sse41-popcnt.exe",
					new String [0],
					"C:\\DATA\\ARENA\\arena_3.5.1\\Engines\\stockfish-16.1-sse41-popcnt\\");
			
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
			
			//control.execute(engine, "./stockfish-14.1.cg", 1000000, true);
			//control.execute(engine, "./stockfish-14.1-4N.cg", 1000000, true);
			//control.execute(engine, "./glaurung-2.2.cg", 1000000, true);
			//control.execute(engine, "./pedone-3.1.cg", 1000000, true);
			//control.execute(engine, "./wasp-5-0-0.cg", 1000000, true);
			//control.execute(engine, "./bagatur-2.3.cg", 1000000, true);
			control.execute(engine, "./stockfish-16.1-NNUE.txt", 1000000, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void execute(EngineProcess engine, String toFileName, int gamesCount, boolean appendToFile) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(toFileName, true), 1024 * 1024);
		
		int positions = 0;
		
		runner.addEngine(engine);
		
		for (int i=0; i<gamesCount; i++) {
			
			runner.startEngines();
			runner.uciOK();
			
			List<String> options = new ArrayList<String>();
			options.add("setoption name MultiPV value 99");
			options.add("setoption name Ponder value false");
			options.add("setoption name OwnBook value false");
			options.add("setoption name SyzygyPath value C:\\dummy\\");
			
			runner.setOptions(options);
			
			runner.isReady();
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
			
			playRandomOpening(bitboard);
			
			System.out.println(bitboard);
					
			EvaluatedGame game = playGame(bitboard);
			
			positions += game.getPositionsCount();
			
			GameModelWriter.writeEvaluatedGame(game, bw);
			
			System.out.println("Game " + (i+1) + " saved in " + toFileName + ", positions are " + positions);
			
			runner.stopEngines();
		}
		
		runner.destroyEngines();
	}
	
	
	private void playRandomOpening(IBitBoard bitboard) {
		
		while (true) {
			
			IOpeningEntry entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			
			//System.out.println("entry=" + entry);
					
			if (entry == null) {
				
				break;
			}
			
			/*if (entry.getWeight() < OpeningBook.OPENING_BOOK_MIN_MOVES) {
				
				break;
			}*/
			
			//OPENING_BOOK_MODE_POWER2=most played first, OPENING_BOOK_MODE_POWER1=random intermediate, OPENING_BOOK_MODE_POWER0=random full
			int mode = OpeningBook.OPENING_BOOK_MODE_POWER0;

			int move = entry.getRandomEntry(mode);
			
			bitboard.makeMoveForward(move);
		}
	}


	private EvaluatedGame playGame(IBitBoard bitboard) throws IOException {
		
		runner.newGame();
		
		EvaluatedGame game = new EvaluatedGame();
		
		while (true) {
			
			Set<EvaluatedMove> movesEvals = evalVariations(bitboard);
			
			if (movesEvals.size() == 0) {
				
				throw new IllegalStateException();
			}
			
			EvaluatedMove best = getBestVariation(bitboard.isInCheck(), movesEvals);
					
			bitboard.makeMoveForward(best.getMoves()[0]);
			
			if (!bitboard.getStatus().equals(IGameStatus.NONE)) {
				
				System.out.println("STATUS: " + bitboard.getStatus());
				
				game.setResult(getGameTerminationScore(bitboard.getStatus()));
				
				break;
			}
			
			int eval = bitboard.getColourToMove() == Constants.COLOUR_BLACK ? best.eval_ofOriginatePlayer() : -best.eval_ofOriginatePlayer();

			if (best.getMoves().length == 1) {
				
				game.addBoard(bitboard.getMoveOps().moveToString(best.getMoves()[0]), bitboard.toEPD(), eval);
				
			} else {
				
				//TODO: play moves and add the corresponding board
			}
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
				if (cur.eval_ofOriginatePlayer() + BEST_MOVE_DIFF <= besteval) {
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
					
				evals.add(move);
			}
		}
		
		
		return evals;
	}
	
	
	private float getGameTerminationScore(IGameStatus status) {
		
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				return 0.5f;
				
			case MATE_WHITE_WIN:
				return 1;
				
			case MATE_BLACK_WIN:
				return 0;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				return 0.5f;
				
			case STALEMATE_BLACK_NO_MOVES:
				return 0.5f;
				
			case DRAW_50_MOVES_RULE:
				return 0.5f;
				
			case NO_SUFFICIENT_MATERIAL:
				return 0.5f;
				
			case PASSER_WHITE:
				throw new IllegalStateException("status=" + status);
				
			case PASSER_BLACK:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_WHITE_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_BLACK_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			default:
				throw new IllegalStateException("status=" + status);
				
		}
	}
}
