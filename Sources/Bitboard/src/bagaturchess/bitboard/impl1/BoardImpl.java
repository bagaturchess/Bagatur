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
package bagaturchess.bitboard.impl1;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class BoardImpl implements IBitBoard {
	
	
	private ChessBoard chessBoard;
	private MoveGenerator generator;
	
	
	public BoardImpl(String fen) {
		chessBoard = ChessBoardUtil.getNewCB(fen);
		generator = new MoveGenerator();
	}
	
	
	@Override
	public boolean isInCheck() {
		return chessBoard.checkingPieces != 0;
	}
	
	
	@Override
	public String toString() {
		return chessBoard.toString();
	}
	
	
	@Override
	public int genAllMoves(IInternalMoveList list) {
		
		generator.startPly();
		
		generator.generateAttacks(chessBoard);
		generator.generateMoves(chessBoard);
		
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (MoveUtil.getAttackedPieceIndex(cur_move) == KING) {
				continue;//TODO remove king captures
			}
			list.reserved_add(cur_move);
		}
		
		generator.endPly();
		
		return list.reserved_getCurrentSize();
	}
	
	
	@Override
	public int genKingEscapes(IInternalMoveList list) {
		
		generator.startPly();
		
		generator.generateAttacks(chessBoard);
		generator.generateMoves(chessBoard);
		
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (MoveUtil.getAttackedPieceIndex(cur_move) == KING) {
				continue;//TODO remove king captures
			}
			list.reserved_add(cur_move);
		}
		
		generator.endPly();
		
		return list.reserved_getCurrentSize();
	}
	
	
	@Override
	public void makeMoveForward(int move) {
		chessBoard.doMove(move);
	}
	
	
	@Override
	public void makeMoveBackward(int move) {
		chessBoard.undoMove(move);
	}
	
	
	@Override
	public void makeNullMoveForward() {
		chessBoard.doNullMove();
	}
	
	
	@Override
	public void makeNullMoveBackward() {
		chessBoard.undoNullMove();
	}
	
	
	@Override
	public int getColourToMove() {
		return chessBoard.colorToMove; 
	}
	
	
	@Override
	public boolean isCaptureMove(int move) {
		return MoveUtil.getAttackedPieceIndex(move) != 0;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMatrix()
	 */
	@Override
	public int[] getMatrix() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsCache()
	 */
	@Override
	public PawnsEvalCache getPawnsCache() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#setPawnsCache(bagaturchess.bitboard.api.PawnsEvalCache)
	 */
	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsStructure()
	 */
	@Override
	public PawnsModelEval getPawnsStructure() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getBoardConfig()
	 */
	@Override
	public IBoardConfig getBoardConfig() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPiecesLists()
	 */
	@Override
	public IPiecesLists getPiecesLists() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genCapturePromotionMoves(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genNonCaptureNonPromotionMoves(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genAllMoves_ByFigureID(int, long, bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeMoveForward(java.lang.String)
	 */
	@Override
	public void makeMoveForward(String ucimove) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getHashKey()
	 */
	@Override
	public long getHashKey() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsHashKey()
	 */
	@Override
	public long getPawnsHashKey() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getStateRepetition()
	 */
	@Override
	public int getStateRepetition() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getFigureID(int)
	 */
	@Override
	public int getFigureID(int fieldID) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getSee()
	 */
	@Override
	public ISEE getSee() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#mark()
	 */
	@Override
	public void mark() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#reset()
	 */
	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#revert()
	 */
	@Override
	public void revert() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#toEPD()
	 */
	@Override
	public String toEPD() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMaterialState()
	 */
	@Override
	public IMaterialState getMaterialState() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMaterialFactor()
	 */
	@Override
	public IMaterialFactor getMaterialFactor() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getBaseEvaluation()
	 */
	@Override
	public IBaseEval getBaseEvaluation() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isPasserPush(int)
	 */
	@Override
	public boolean isPasserPush(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getUnstoppablePasser()
	 */
	@Override
	public int getUnstoppablePasser() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isDraw50movesRule()
	 */
	@Override
	public boolean isDraw50movesRule() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getDraw50movesRule()
	 */
	@Override
	public int getDraw50movesRule() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasSufficientMaterial()
	 */
	@Override
	public boolean hasSufficientMaterial() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isInCheck(int)
	 */
	@Override
	public boolean isInCheck(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasMoveInCheck()
	 */
	@Override
	public boolean hasMoveInCheck() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasMoveInNonCheck()
	 */
	@Override
	public boolean hasMoveInNonCheck() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isCheckMove(int)
	 */
	@Override
	public boolean isCheckMove(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isPossible(int)
	 */
	@Override
	public boolean isPossible(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasSingleMove()
	 */
	@Override
	public boolean hasSingleMove() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getCastlingType(int)
	 */
	@Override
	public int getCastlingType(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasRightsToKingCastle(int)
	 */
	@Override
	public boolean hasRightsToKingCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasRightsToQueenCastle(int)
	 */
	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPlayedMovesCount()
	 */
	@Override
	public int getPlayedMovesCount() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPlayedMoves()
	 */
	@Override
	public int[] getPlayedMoves() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getLastMove()
	 */
	@Override
	public int getLastMove() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getStatus()
	 */
	@Override
	public IGameStatus getStatus() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFreeBitboard()
	 */
	@Override
	public long getFreeBitboard() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByPID(int)
	 */
	@Override
	public long getFiguresBitboardByPID(int pid) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByColourAndType(int, int)
	 */
	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByColour(int)
	 */
	@Override
	public long getFiguresBitboardByColour(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getAttacksSupport()
	 */
	@Override
	public boolean getAttacksSupport() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFieldsStateSupport()
	 */
	@Override
	public boolean getFieldsStateSupport() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#setAttacksSupport(boolean, boolean)
	 */
	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getPlayerAttacks(int)
	 */
	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFieldsAttacks()
	 */
	@Override
	public IFieldsAttacks getFieldsAttacks() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#isPromotionMove(int)
	 */
	@Override
	public boolean isPromotionMove(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#isCaptureOrPromotionMove(int)
	 */
	@Override
	public boolean isCaptureOrPromotionMove(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#isEnpassantMove(int)
	 */
	@Override
	public boolean isEnpassantMove(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#isCastlingMove(int)
	 */
	@Override
	public boolean isCastlingMove(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getSEEScore(int)
	 */
	@Override
	public int getSEEScore(int move) {
		throw new UnsupportedOperationException();
	}
}
