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
package bagaturchess.bitboard.impl.dummy;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.api.IMoveIterator;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.BoardStat;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.SEE;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.Move;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.state.PiecesLists;

public class DummyBoard implements IBitBoard {

	private DummyMaterial material;
	//public PiecesLists stateManager;
	
	public DummyBoard() {
		material = new DummyMaterial();
		//stateManager = new PiecesLists(this);
		//stateManager.initializeAliveFigure(Castling.KINGS_IDS_BY_COLOUR[Figures.COLOUR_WHITE]);
		//stateManager.initializeAliveFigure(Castling.KINGS_IDS_BY_COLOUR[Figures.COLOUR_BLACK]);
	}
	
	public int gen2MovesPromotions(int colour, long[][] moves) {
		throw new UnsupportedOperationException();
	}

	public int genAllCheckMoves(int colour, long[][] moves) {
		return dummyMoveGen(5, moves);
	}

	public int genAllMoves(int colour, long[][] moves) {
		return dummyMoveGen(35, moves);
	}

	public int genAllMoves(int colour, long[][] moves, boolean checkKeepersAware) {
		return dummyMoveGen(35, moves);
	}

	public int genAllMoves_ByFigureID(int figureID, long excludedToFields, long[][] moves) {
		throw new UnsupportedOperationException();
	}

	public int genCapturePromotionCheckMoves(int colour, long[][] moves) {
		throw new UnsupportedOperationException();
	}

	public int genDirectCheckMoves(int colour, long[][] moves) {
		throw new UnsupportedOperationException();
	}

	public int genHiddenCheckMoves(int colour, long[][] moves) {
		throw new UnsupportedOperationException();
	}
	
	boolean hasCaptures = false;
	public int genCapturePromotionMoves(int colour, long[][] moves) {
		if (hasCaptures) {
			hasCaptures = false;
			return dummyMoveGen(5, moves);
		} else {
			hasCaptures = true;
			return 0;
		}
		//throw new UnsupportedOperationException();
	}
	
	public int genKingEscapes(int colour, long[][] moves) {
		return dummyMoveGen(5, moves);
	}

	public int genNonCaptureNonPromotionMoves(int colour, long[][] moves) {
		return dummyMoveGen(10, moves);
	}


	private int dummyMoveGen(int count, long[][] moves) {
		
		for (int i=0; i < count; i++) {
			double rand = Math.random();
			long[] move = moves[i];
			move[27] = i;
			move[28] = (long) (1000 * rand);
			move[9] = (long) (63 * rand);
			move[10] = 63 - move[9];
			//System.out.println("f=" + move[9] + " t=" + move[10]);
		}
		
		return count;
	}
	
	public int genPromotions(int colour, long[][] moves) {
		throw new UnsupportedOperationException();
	}

	public boolean getAttacksSupport() {
		return true;
	}

	public int getChecksCount(int colour) {
		throw new UnsupportedOperationException();
	}
	
	int colourToMove = Figures.COLOUR_WHITE;
	public int getColourToMove() {
		return colourToMove;
	}

	public int getFieldID(int figureID) {
		throw new UnsupportedOperationException();
	}

	public long getFigureBitboardByID(int figureID) {
		throw new UnsupportedOperationException();
	}

	public int getFigureID(int fieldID) {
		throw new UnsupportedOperationException();
	}

	public long getFiguresBitboardByColour(int colour) {
		throw new UnsupportedOperationException();
	}

	public long getFiguresBitboardByColourAndType(int colour, int type) {
		return hash;
	}

	public long getFreeBitboard() {
		throw new UnsupportedOperationException();
	}

	static long hash = 0;
	public long getHashKey() {
		return hash++;
	}

	public long getHashKeyAfterMove(long[] move) {
		return getHashKey();
	}

	public int getLastMove() {
		return 0;
	}

	static long pawnshash = 0;
	public long getPawnsHashKey() {
		return pawnshash++;
	}

	public int[] getPlayedMoves() {
		throw new UnsupportedOperationException();
	}

	public int getPlayedMovesCount() {
		return 30;
	}

	public IPlayerAttacks getPlayerAttacks(int colour) {
		return null;//throw new UnsupportedOperationException();
	}

	public IPiecesLists getPiecesLists() {
		return null;
	}

	public int getStateRepetition() {
		return 0;
	}

	public int getStateRepetition(long hashkey) {
		return 0;
	}

	public BoardStat getStatistics() {
		throw new UnsupportedOperationException();
	}

	public IGameStatus getStatus() {
		throw new UnsupportedOperationException();
	}

	public boolean has2MovePromotions(int colour) {
		throw new UnsupportedOperationException();
	}

	public boolean hasCapturePromotionCheck(int colour) {
		return true;
	}

	public boolean hasChecks(int colour) {
		throw new UnsupportedOperationException();
	}

	public boolean hasMove(int colour) {
		throw new UnsupportedOperationException();
	}

	public boolean hasMoveInCheck(int colour) {
		return true;
	}

	public boolean hasMoveInNonCheck(int colour) {
		return true;
	}

	public boolean hasPromotions(int colour) {
		return false;
		//throw new UnsupportedOperationException();
	}

	public boolean hasRightsToKingCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	public boolean hasRightsToQueenCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	public boolean hasSingleMove(int colour) {
		return false;
		//throw new UnsupportedOperationException();
	}

	public int getCastlingType(int colour) {
		return 0;
	}

	public boolean isCheckMove(long[] move) {
		return false;
	}

	public boolean isDirectCheckMove(long[] move) {
		throw new UnsupportedOperationException();
	}

	public boolean isInCheck(int colour) {
		return false;
	}

	public boolean isPossible(long[] move) {
		return false;
	}

	public IMoveIterator iterator(int iteratorFactoryHandler) {
		throw new UnsupportedOperationException();
	}

	public void makeMoveForward(long[] move) {
		switchColours();
	}

	public void makeMoveBackward(long[] move) {
		switchColours();
	}

	public void makeNullMoveForward() {
		switchColours();
	}

	public void makeNullMoveBackward() {
		switchColours();
	}
	
	private void switchColours() {
		if (colourToMove == Figures.COLOUR_WHITE) {
			colourToMove = Figures.COLOUR_BLACK;
		} else {
			colourToMove = Figures.COLOUR_WHITE;
		}
	}
	
	public void reinit() {
		throw new UnsupportedOperationException();
	}

	public void setAttacksSupport(boolean attacksSupport) {
		
	}

	public void test(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBitBoard clone() {
		return new DummyBoard();
	}

	public void clearInCheckCounters() {
		// TODO Auto-generated method stub
		
	}

	public int getBlackInCheckCounts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWhiteInCheckCounts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genMinorMoves(int colour, long[][] moves) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void clearKingMovesCounters() {
		// TODO Auto-generated method stub
		
	}

	public int getBlackKingMovesCounts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWhiteKingMovesCounts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasMinorOrMajorPieces(int colour) {
		// TODO Auto-generated method stub
		return true;
	}

	public void clearQueenMovesCounters() {
		// TODO Auto-generated method stub
		
	}

	public int getBlackQueensMovesCounts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWhiteQueensMovesCounts() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean equals(Object o) {
		return true;
	}

	public IFieldsAttacks getFieldsAttacks() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getGamePhase() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SEE getSee() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getPawnHashKeyAfterMove(long[] move) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int gen2MovesPromotions(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genAllCheckMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genAllMoves(IInternalMoveList list) {
		return dummyMoveGen(8, list);
	}

	private int dummyMoveGen(int count, IInternalMoveList list) {
		
		for (int i=0; i < count; i++) {
			double rand = Math.random();
			
			
			int move = i;
			move ^= (long) (1000 * i);
			move ^= (long) (63 * move);
			
			list.reserved_add(move);
			//System.out.println("f=" + move[9] + " t=" + move[10]);
		}
		
		return count;
	}
	
	public int genAllMoves(IInternalMoveList list, boolean checkKeepersAware) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genCapturePromotionCheckMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genCapturePromotionMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genDirectCheckMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genHiddenCheckMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genKingEscapes(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genMinorMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int genPromotions(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	public BackupInfo[] getBackups() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBaseEval getBaseEvaluation() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getFieldsStateSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	public long getHashKeyAfterMove(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getPawnHashKeyAfterMove(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPlayedMovesCount_Total() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean has2MovePromotions() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasCapturePromotionCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMove() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMoveInCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMoveInNonCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasPromotions() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSingleMove() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCheckMove(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDirectCheckMove(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPossible(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	public void makeMoveBackward(int move) {
		// TODO Auto-generated method stub
		
	}

	public void makeMoveForward(int move) {
		// TODO Auto-generated method stub
		
	}

	public void mark() {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
		// TODO Auto-generated method stub
		
	}

	public String toEPD() {
		// TODO Auto-generated method stub
		return null;
	}

	public PawnsEvalCache getPawnsCache() {
		// TODO Auto-generated method stub
		return null;
	}

	public PawnsModelEval getPawnsStructure() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasSufficientMaterial() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasUnstoppablePasser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDraw50movesRule() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		// TODO Auto-generated method stub
		
	}

	public void revert() {
		// TODO Auto-generated method stub
		
	}

	public int[] getOpeningMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPasserPush(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getDraw50movesRule() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int genAllMoves(IInternalMoveList list, long excludedToFieldsBoard) {
		throw new UnsupportedOperationException();
	}

	public int getLastCaptrueFieldID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getUnstoppablePasser() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMaterialFactor getMaterialFactor() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public IMobility getMobility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMaterialState getMaterialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoardConfig getBoardConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getFiguresBitboardByPID(int pid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastLastMove() {
		// TODO Auto-generated method stub
		return 0;
	}
}
