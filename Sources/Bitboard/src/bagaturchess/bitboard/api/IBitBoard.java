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
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;

//import bagaturchess.bitboagetrd.BackupInfo;
//import bagaturchess.bitboard.BoardStat;
//import bagaturchess.bitboard.eval.BaseEvaluation;

public interface IBitBoard extends IBoard {
	
	
	public int[] getOpeningMoves();
	
	public void mark();
	public void reset();
	
	public void test(int colour);

	public void setPawnsCache(PawnsEvalCache pawnsCache);
	public boolean hasMinorOrMajorPieces(int colour);
	
	public void revert();
	
	//public BackupInfo[] getBackups();
	
	public int getLastCaptrueFieldID();
	
	/*public void clearInCheckCounters();
	public int getWhiteInCheckCounts();
	public int getBlackInCheckCounts();

	public void clearKingMovesCounters();
	public int getWhiteKingMovesCounts();
	public int getBlackKingMovesCounts();
	
	public int getBlackQueensMovesCounts();
	public int getWhiteQueensMovesCounts();
	public void clearQueenMovesCounters();*/
	
	public IPlayerAttacks getPlayerAttacks(int colour);
	public IFieldsAttacks getFieldsAttacks();
	
	//public BoardStat getStatistics();
	public IGameStatus getStatus();
	
	/**
	 * Base engine's methods  
	 */
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			final IInternalMoveList list);
	public int genAllMoves(final IInternalMoveList list, long excludedToFieldsBoard);
	public int genAllMoves(final IInternalMoveList list, boolean checkKeepersAware);
	public int genMinorMoves(final IInternalMoveList list);
	
	public int genNonCaptureNonPromotionMoves(final IInternalMoveList list);
	public int genPromotions(final IInternalMoveList list);
	public int gen2MovesPromotions(final IInternalMoveList list);
	public int genDirectCheckMoves(final IInternalMoveList list);
	public int genHiddenCheckMoves(final IInternalMoveList list);
	public int genAllCheckMoves(final IInternalMoveList list);
	public int genCapturePromotionCheckMoves(final IInternalMoveList list);
	public int genKingEscapes(final IInternalMoveList list);
	
	public boolean hasMove();
	public boolean hasMoveInNonCheck();
	public boolean hasMoveInCheck();
	public boolean hasCapturePromotionCheck();
	public boolean hasSingleMove();
	public boolean hasPromotions();
	public boolean has2MovePromotions();
	public boolean hasChecks(int colour);
	

	public int getCastlingType(int colour);

	public boolean hasRightsToKingCastle(int colour);
	public boolean hasRightsToQueenCastle(int colour);
	
	public boolean isInCheck();
	public boolean isInCheck(int colour);
	public boolean isCheckMove(int move);
	public boolean isDirectCheckMove(int move);
	public int getChecksCount(int colour);
	
	public boolean isPossible(int move);
	//public boolean opensCheck(long[] move);
	
	/**
	 * Game related methods
	 */
	public int getPlayedMovesCount();
	public int getPlayedMovesCount_Total();
	public int[] getPlayedMoves();
	public int getLastMove();
	public int getLastLastMove();
	
	
	public int getStateRepetition(long hashkey);
	
	
	/**
	 * Hash keys
	 */
	public long getHashKeyAfterMove(final int move);
	public long getPawnHashKeyAfterMove(final int move);
	
	/**
	 * Birboards
	 */
	public long getFigureBitboardByID(int figureID);
	public long getFreeBitboard();
	
	public int getFieldID(int figureID);
	
	public IMoveIterator iterator(int iteratorFactoryHandler);
	//public IBitBoard clone();
	
	public boolean getAttacksSupport();
	public boolean getFieldsStateSupport();
	public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport);
	
	public int getGamePhase();
}
