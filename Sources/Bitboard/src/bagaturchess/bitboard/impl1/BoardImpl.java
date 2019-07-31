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
	
	private IPiecesLists pieces;
	private IMaterialFactor materialFactor;
	private IBaseEval baseEval;
	private IMaterialState materialState;
	
	
	public BoardImpl(String fen) {
		chessBoard = ChessBoardUtil.getNewCB(fen);
		generator = new MoveGenerator();
		pieces = new PiecesListsImpl(this);
		materialFactor = new MaterialFactorImpl();
		baseEval = new BaseEvalImpl();
		materialState = new MaterialStateImpl();
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
		
		int counter = 0;
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (!chessBoard.isLegal(cur_move)) {
				continue;
			}
			list.reserved_add(cur_move);
			counter++;
		}
		
		generator.endPly();
		
		return counter;
	}
	
	
	@Override
	public int genKingEscapes(IInternalMoveList list) {
		
		generator.startPly();
		
		generator.generateAttacks(chessBoard);
		generator.generateMoves(chessBoard);
		
		int counter = 0;
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (!chessBoard.isLegal(cur_move)) {
				continue;
			}
			list.reserved_add(cur_move);
			counter++;
		}
		
		generator.endPly();
		
		return counter;
	}
	
	
	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {
		generator.startPly();
		
		generator.generateAttacks(chessBoard);
		
		int counter = 0;
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (!chessBoard.isLegal(cur_move)) {
				continue;
			}
			list.reserved_add(cur_move);
			counter++;
		}
		
		generator.endPly();
		
		return counter;
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
	
	
	@Override
	public boolean isPromotionMove(int move) {
		return MoveUtil.isPromotion(move);
	}
	
	
	@Override
	public boolean isCaptureOrPromotionMove(int move) {
		return isCaptureMove(move) || isPromotionMove(move);
	}

	
	@Override
	public boolean isEnpassantMove(int move) {
		return MoveUtil.isEPMove(move);
	}

	
	@Override
	public boolean isCastlingMove(int move) {
		return MoveUtil.isCastlingMove(move);
	}
	
	
	@Override
	public int getSEEScore(int move) {
		return 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));//TODO implement
	}
	
	
	@Override
	public void revert() {
		//TODO
	}
	
	
	@Override
	public long getHashKey() {
		return chessBoard.zobristKey;
	}
	
	
	@Override
	public IPiecesLists getPiecesLists() {
		return pieces;
	}
	
	
	@Override
	public IMaterialFactor getMaterialFactor() {
		return materialFactor;
	}
	
	
	@Override
	public IBaseEval getBaseEvaluation() {
		return baseEval;
	}
	
	
	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		return chessBoard.pieces[colour][type];//TODO Check type
	}
	
	
	@Override
	public long getFreeBitboard() {
		return chessBoard.emptySpaces;
	}
	
	
	@Override
	public boolean hasRightsToKingCastle(int colour) {
		return false;//TODO chessBoard.castlingRights;
	}
	
	
	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		return false;//TODO chessBoard.castlingRights;
	}

	
	@Override
	public int getFigureID(int fieldID) {
		throw new UnsupportedOperationException();
		//return chessBoard.pieceIndexes[fieldID];//TODO Check
	}
	
	
	@Override
	public int getDraw50movesRule() {
		return 0;//TODO
	}
	
	
	@Override
	public boolean isDraw50movesRule() {
		return false;//TODO
	}
	
	
	@Override
	public PawnsEvalCache getPawnsCache() {
		return null;
	}
	
	
	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		//Do nothing
	}
	
	
	@Override
	public int getStateRepetition() {
		return 0;//TODO chessBoard.isRepetition(move)
	}
	
	
	@Override
	public boolean hasSufficientMaterial() {
		return true;//TODO
	}
	
	
	@Override
	public int getLastMove() {
		return 0;//TODO
	}
	
	
	@Override
	public boolean isCheckMove(int move) {
		return false;//TODO
	}
	
	
	@Override
	public boolean isPossible(int move) {
		return chessBoard.isLegal(move);//TODO Check
		//return false;
	}
	
	
	@Override
	public boolean hasSingleMove() {
		return false;//TODO
	}
	
	
	@Override
	public IMaterialState getMaterialState() {
		return materialState;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMatrix()
	 */
	@Override
	public int[] getMatrix() {
		throw new UnsupportedOperationException();
		//TODO return chessBoard.pieceIndexes;
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
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsHashKey()
	 */
	@Override
	public long getPawnsHashKey() {
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
	 * @see bagaturchess.bitboard.api.IBoard#toEPD()
	 */
	@Override
	public String toEPD() {
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
	 * @see bagaturchess.bitboard.api.IBoard#getCastlingType(int)
	 */
	@Override
	public int getCastlingType(int colour) {
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
	 * @see bagaturchess.bitboard.api.IBoard#getStatus()
	 */
	@Override
	public IGameStatus getStatus() {
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
	
	
	protected class MaterialStateImpl implements IMaterialState {
		
		
		@Override
		public int getPiecesCount() {
			return Long.bitCount(chessBoard.allPieces);
		}
		
		
		@Override
		public int[] getPIDsCounts() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	protected class MaterialFactorImpl implements IMaterialFactor {
		
		
		public MaterialFactorImpl() {
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#getBlackFactor()
		 */
		@Override
		public int getBlackFactor() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#getWhiteFactor()
		 */
		@Override
		public int getWhiteFactor() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#getTotalFactor()
		 */
		@Override
		public int getTotalFactor() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#getOpenningPart()
		 */
		@Override
		public double getOpenningPart() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactor(int, int)
		 */
		@Override
		public int interpolateByFactor(int val_o, int val_e) {
			//throw new UnsupportedOperationException();
			return (val_o + val_e) / 2;
		}
		
		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactorAndColour(int, int, int)
		 */
		@Override
		public int interpolateByFactorAndColour(int colour, int val_o, int val_e) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactor(double, double)
		 */
		@Override
		public int interpolateByFactor(double val_o, double val_e) {
			throw new UnsupportedOperationException();
		}

	}
}
