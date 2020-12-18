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
package bagaturchess.bitboard.impl;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;

/**
 * @author i027638
 *
 */
public class BoardProxy_ReversedBBs implements IBitBoard {
	
	
	private final IBitBoard bitboard;
	
	
	public BoardProxy_ReversedBBs(IBitBoard _bitboard) {
		bitboard = _bitboard;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFreeBitboard()
	 */
	@Override
	public long getFreeBitboard() {
		return convertBB(bitboard.getFreeBitboard());
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByPID(int)
	 */
	@Override
	public long getFiguresBitboardByPID(int pid) {
		return convertBB(bitboard.getFiguresBitboardByPID(pid));
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByColourAndType(int, int)
	 */
	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		return convertBB(bitboard.getFiguresBitboardByColourAndType(colour, type));
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFiguresBitboardByColour(int)
	 */
	@Override
	public long getFiguresBitboardByColour(int colour) {
		return convertBB(bitboard.getFiguresBitboardByColour(colour));
	}
	
	
	private static final long convertBB(long bb) {
		return Bits.reverse(bb);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMatrix()
	 */
	@Override
	public int[] getMatrix() {
		return bitboard.getMatrix();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsCache()
	 */
	@Override
	public PawnsEvalCache getPawnsCache() {
		return bitboard.getPawnsCache();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#setPawnsCache(bagaturchess.bitboard.api.PawnsEvalCache)
	 */
	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		bitboard.setPawnsCache(pawnsCache);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsStructure()
	 */
	@Override
	public PawnsModelEval getPawnsStructure() {
		return bitboard.getPawnsStructure();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getBoardConfig()
	 */
	@Override
	public IBoardConfig getBoardConfig() {
		return bitboard.getBoardConfig();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPiecesLists()
	 */
	@Override
	public IPiecesLists getPiecesLists() {
		return bitboard.getPiecesLists();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getColourToMove()
	 */
	@Override
	public int getColourToMove() {
		return bitboard.getColourToMove();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genAllMoves(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genAllMoves(IInternalMoveList list) {
		return bitboard.genAllMoves(list);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genKingEscapes(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genKingEscapes(IInternalMoveList list) {
		return bitboard.genKingEscapes(list);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genCapturePromotionMoves(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {
		return bitboard.genCapturePromotionMoves(list);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genNonCaptureNonPromotionMoves(bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
		return bitboard.genNonCaptureNonPromotionMoves(list);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#genAllMoves_ByFigureID(int, long, bagaturchess.bitboard.api.IInternalMoveList)
	 */
	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {
		return bitboard.genAllMoves_ByFigureID(fieldID, excludedToFields, list);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeMoveForward(int)
	 */
	@Override
	public void makeMoveForward(int move) {
		bitboard.makeMoveForward(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeMoveForward(java.lang.String)
	 */
	@Override
	public void makeMoveForward(String ucimove) {
		bitboard.makeMoveForward(ucimove);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeMoveBackward(int)
	 */
	@Override
	public void makeMoveBackward(int move) {
		bitboard.makeMoveBackward(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeNullMoveForward()
	 */
	@Override
	public void makeNullMoveForward() {
		bitboard.makeNullMoveForward();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#makeNullMoveBackward()
	 */
	@Override
	public void makeNullMoveBackward() {
		bitboard.makeNullMoveBackward();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getHashKey()
	 */
	@Override
	public long getHashKey() {
		return bitboard.getHashKey();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsHashKey()
	 */
	@Override
	public long getPawnsHashKey() {
		return bitboard.getPawnsHashKey();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getStateRepetition()
	 */
	@Override
	public int getStateRepetition() {
		return bitboard.getStateRepetition();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getFigureID(int)
	 */
	@Override
	public int getFigureID(int fieldID) {
		return bitboard.getFigureID(fieldID);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getSee()
	 */
	@Override
	public ISEE getSee() {
		return bitboard.getSee();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#mark()
	 */
	@Override
	public void mark() {
		bitboard.mark();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#reset()
	 */
	@Override
	public void reset() {
		bitboard.reset();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#revert()
	 */
	@Override
	public void revert() {
		bitboard.revert();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#toEPD()
	 */
	@Override
	public String toEPD() {
		return bitboard.toEPD();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMaterialState()
	 */
	@Override
	public IMaterialState getMaterialState() {
		return bitboard.getMaterialState();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMaterialFactor()
	 */
	@Override
	public IMaterialFactor getMaterialFactor() {
		return bitboard.getMaterialFactor();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getBaseEvaluation()
	 */
	@Override
	public IBaseEval getBaseEvaluation() {
		return bitboard.getBaseEvaluation();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isPasserPush(int)
	 */
	@Override
	public boolean isPasserPush(int move) {
		return bitboard.isPasserPush(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getUnstoppablePasser()
	 */
	@Override
	public int getUnstoppablePasser() {
		return bitboard.getUnstoppablePasser();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isDraw50movesRule()
	 */
	@Override
	public boolean isDraw50movesRule() {
		return bitboard.isDraw50movesRule();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getDraw50movesRule()
	 */
	@Override
	public int getDraw50movesRule() {
		return bitboard.getDraw50movesRule();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasSufficientMaterial()
	 */
	@Override
	public boolean hasSufficientMaterial() {
		return bitboard.hasSufficientMaterial();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isInCheck()
	 */
	@Override
	public boolean isInCheck() {
		return bitboard.isInCheck();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isInCheck(int)
	 */
	@Override
	public boolean isInCheck(int colour) {
		return bitboard.isInCheck(colour);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasMoveInCheck()
	 */
	@Override
	public boolean hasMoveInCheck() {
		return bitboard.hasMoveInCheck();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasMoveInNonCheck()
	 */
	@Override
	public boolean hasMoveInNonCheck() {
		return bitboard.hasMoveInNonCheck();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isCheckMove(int)
	 */
	@Override
	public boolean isCheckMove(int move) {
		return bitboard.isCheckMove(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#isPossible(int)
	 */
	@Override
	public boolean isPossible(int move) {
		return bitboard.isPossible(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasSingleMove()
	 */
	@Override
	public boolean hasSingleMove() {
		return bitboard.hasSingleMove();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getCastlingType(int)
	 */
	@Override
	public int getCastlingType(int colour) {
		return bitboard.getCastlingType(colour);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasRightsToKingCastle(int)
	 */
	@Override
	public boolean hasRightsToKingCastle(int colour) {
		return bitboard.hasRightsToKingCastle(colour);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#hasRightsToQueenCastle(int)
	 */
	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		return bitboard.hasRightsToQueenCastle(colour);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPlayedMovesCount()
	 */
	@Override
	public int getPlayedMovesCount() {
		return bitboard.getPlayedMovesCount();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPlayedMoves()
	 */
	@Override
	public int[] getPlayedMoves() {
		return bitboard.getPlayedMoves();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getLastMove()
	 */
	@Override
	public int getLastMove() {
		return bitboard.getLastMove();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getStatus()
	 */
	@Override
	public IGameStatus getStatus() {
		return bitboard.getStatus();
	}
	

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getAttacksSupport()
	 */
	@Override
	public boolean getAttacksSupport() {
		return bitboard.getAttacksSupport();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFieldsStateSupport()
	 */
	@Override
	public boolean getFieldsStateSupport() {
		return bitboard.getFieldsStateSupport();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#setAttacksSupport(boolean, boolean)
	 */
	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		bitboard.setAttacksSupport(attacksSupport, fieldsStateSupport);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getPlayerAttacks(int)
	 */
	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {
		return bitboard.getPlayerAttacks(colour);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getFieldsAttacks()
	 */
	@Override
	public IFieldsAttacks getFieldsAttacks() {
		return bitboard.getFieldsAttacks();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBitBoard#getSEEScore(int)
	 */
	@Override
	public int getSEEScore(int move) {
		return bitboard.getSEEScore(move);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMoveOps()
	 */
	@Override
	public IMoveOps getMoveOps() {
		return bitboard.getMoveOps();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getFigureType(int)
	 */
	@Override
	public int getFigureType(int fieldID) {
		return bitboard.getFigureType(fieldID);
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getHashKeyAfterMove(int)
	 */
	@Override
	public long getHashKeyAfterMove(int move) {
		return bitboard.getHashKeyAfterMove(move);
	}
	
	
	@Override
	public String toString() {
		return bitboard.toString();
	}


	@Override
	public int getFigureColour(int fieldID) {
		return bitboard.getFigureColour(fieldID);
	}


	@Override
	public int getSEEFieldScore(int squareID) {
		return bitboard.getSEEFieldScore(squareID);
	}
}
