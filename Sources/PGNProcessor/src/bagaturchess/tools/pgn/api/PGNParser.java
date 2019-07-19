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
package bagaturchess.tools.pgn.api;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl1.Board3;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.learning.goldmiddle.impl1.cfg.BoardConfigImpl_V17;
import bagaturchess.tools.pgn.impl.ExcludedGames;
import bagaturchess.tools.pgn.impl.PGNConstants;
import bagaturchess.tools.pgn.impl.PGNGame;
import bagaturchess.tools.pgn.impl.PGNInputStream;
import bagaturchess.tools.pgn.impl.PGNTurn;
import bagaturchess.tools.pgn.impl.PGNUtils;


public class PGNParser {
	
	
	private IBoard bitboard;
	
	
	public PGNParser() {
		//bitboard = BoardUtils.createBoard_WithPawnsCache();
		bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD, BagaturPawnsEvalFactory.class.getName(), new BoardConfigImpl_V17(), 10000);
		//bitboard = new Board3();
		//bitboard.setAttacksSupport(EngineConfig.getSingleton().getFieldsStatesSupport(), EngineConfig.getSingleton().getFieldsStatesSupport());
	}
	
	public void importPGNGamesInDir(File pgnFileDir, IGameIterator gi) throws Exception {
		importPGNGamesInDir(pgnFileDir, gi, false);
	}
	
	public void importPGNGamesInDir(File pgnFileDir, IGameIterator gi, boolean ignoreErrors) throws Exception {
		importPGNGamesInDir(pgnFileDir, new IGameIterator[] {gi}, ignoreErrors);
	}
	
	public void importPGNGamesInDir(File pgnFileDir, IGameIterator[] gi) throws Exception {
		importPGNGamesInDir(pgnFileDir, gi, false);
	}
	
	public void importPGNGamesInDir(File pgnFileDir, IGameIterator[] gi, boolean ignoreErrors) throws Exception {
		if (gi != null) {
			for (int i=0; i<gi.length; i++) {
				gi[i].preIteration(bitboard);	
			}
		}
		if (pgnFileDir.isDirectory()) {
			recursiveDepthSearch(pgnFileDir, gi, ignoreErrors);
		} else {
			processSingleFile(pgnFileDir, gi, ignoreErrors);
		}
		if (gi != null) {
			for (int i=0; i<gi.length; i++) {
				gi[i].postIteration();	
			}
		}
	}

	private void recursiveDepthSearch(File pFile, IGameIterator[] gi, boolean ignoreErrors) throws Exception {
		File lFiles[] = pFile.listFiles();
		if (lFiles != null) {
			int lFileSize = lFiles.length;

			for (int counter = 0; counter < lFileSize; counter++) {
				File lTempFile = lFiles[counter];
				if (lTempFile.isDirectory()) {
					recursiveDepthSearch(lTempFile, gi, ignoreErrors);
				} else {
					processSingleFile(lTempFile, gi, ignoreErrors);
				}
			}
		}
	}

	private void processSingleFile(File pFile, IGameIterator[] gi, boolean ignoreErrors) throws Exception {
		String lTempFileName = pFile.getName();
		System.out.println("Processing " + lTempFileName + " ... ");
		if (lTempFileName.endsWith(PGNConstants.FILE_PGN_SUFFIX)) {
			//try {
				InputStream pgnFileIS = new FileInputStream(pFile);
				importSinglePGNFile(pgnFileIS, pFile, gi, ignoreErrors);
			//} catch (java.lang.Throwable ioe) {
			//	ioe.printStackTrace();
			//}
		} else if (
			lTempFileName.toLowerCase().endsWith(PGNConstants.FILE_JAR_SUFFIX)
				|| lTempFileName.toLowerCase().endsWith(PGNConstants.FILE_ZIP_SUFFIX)) {
			//try {
				ZipFile lZip = new ZipFile(pFile);
				Enumeration<?> lEnum = lZip.entries();

				while (lEnum.hasMoreElements()) {
					ZipEntry lEntry = (ZipEntry) lEnum.nextElement();
					if (!lEntry.isDirectory()) {
						String lEntryName = lEntry.getName();
						if (lEntryName
							.toLowerCase()
							.endsWith(PGNConstants.FILE_PGN_SUFFIX)) {
							InputStream pgnFileIS = lZip.getInputStream(lEntry);
							importSinglePGNFile(pgnFileIS, pFile, gi, ignoreErrors);
						}
					}
				}
			//} catch (java.lang.Throwable ioe) {
			//	ioe.printStackTrace();
			//}
		}
	}

	private void importSinglePGNFile(InputStream pPGNFileIS, File file, IGameIterator[] gi, boolean ignoreErrors)
		throws Exception {
		
		PGNInputStream pgnIS = new PGNInputStream(pPGNFileIS);
		
		PGNGame pgnGame = null;
		int gameCount = 0;
		boolean hasGames = true;
		while (hasGames) {
			gameCount++;
			//try {
				pgnGame = pgnIS.readGame();
				if (pgnGame == null) {
					hasGames = false;
				} else {
					pgnGame.setArchiveFileName(file.getAbsolutePath());
					String pgnGameID = pgnGame.getStringIdentification().trim();
					
					if (ExcludedGames.isExcluded(pgnGameID)) {
						break;
					}
					
					bitboard.mark();
					
					if (gi != null) {
						for (int i=0; i<gi.length; i++) {
							gi[i].preGame(gameCount, pgnGame, pgnGameID, bitboard);
						}
					}
					if (gi != null) {
						for (int i=0; i<gi.length; i++) {
							if (gi[i] instanceof IPlyIterator) {
								try {
									playGame(pgnGame, bitboard, new IPlyIterator[] {(IPlyIterator) gi[i]});
								} catch (Exception e) {
									if (ignoreErrors) {
										e.printStackTrace();
									} else {
										throw e;
									}
								}
							}
						}
					}
					if (gi != null) {
						for (int i=0; i<gi.length; i++) {
							gi[i].postGame();
						}
					}
					
					bitboard.reset();
				}
			//} catch (Throwable t) {
			//	t.printStackTrace();
			//}
		}
		
		pgnIS.close();
	}
	
	private void playGame(PGNGame pgnGame, IBoard bitboard, IPlyIterator[] pi) {
		List<PGNTurn> pgnMoves = pgnGame.getTurns();
		int pgnMovesCount = pgnMoves.size();
		//System.out.println("Game " + pgnGame);
		
		int moveNumber = 1;
		for (int i=0; i<pgnMovesCount; i++) {
			PGNTurn pgnMove = (PGNTurn) pgnMoves.get(i);
			
			String whitePly = pgnMove.getWhitePly();
			if (whitePly != null) {
				int w_move = PGNUtils.translatePGNMove(bitboard, Figures.COLOUR_WHITE, whitePly, false, pgnGame);
				if (w_move == -1){
					break;//Wrong move
				}
				
				if (pi != null) {
					if (pi != null) {
						for (int j=0; j<pi.length; j++) {
							pi[j].preMove(Figures.COLOUR_WHITE, w_move, bitboard, moveNumber++);
						}
					}
				}
				bitboard.makeMoveForward(w_move);
				if (pi != null) {
					if (pi != null) {
						for (int j=0; j<pi.length; j++) {
							pi[j].postMove();
						}
					}
				}
				
				if (bitboard.isInCheck()) {
					if (!bitboard.hasMoveInCheck()) {
						return;
					}
				} else {
					if (!bitboard.hasMoveInNonCheck()) {
						return;
					}			
				}
				
				String blackPly = pgnMove.getBlackPly();
				if (blackPly != null) {
					
					/*if (i == 18 && "f5".equals(blackPly)) {
						int g = 0;
					}*/
					
					int b_move = PGNUtils.translatePGNMove(bitboard, Figures.COLOUR_BLACK, blackPly, false, pgnGame);
					if (b_move == -1){
						break;//Wrong move
					}
					
					if (pi != null) {
						if (pi != null) {
							for (int j=0; j<pi.length; j++) {
								pi[j].preMove(Figures.COLOUR_BLACK, b_move, bitboard, moveNumber++);
							}
						}
					}
					
					bitboard.makeMoveForward(b_move);
					if (pi != null) {
						if (pi != null) {
							for (int j=0; j<pi.length; j++) {
								pi[j].postMove();
							}
						}
					}
					
					if (bitboard.isInCheck()) {
						if (!bitboard.hasMoveInCheck()) {
							return;
						}
					} else {
						if (!bitboard.hasMoveInNonCheck()) {
							return;
						}			
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
	}
}
