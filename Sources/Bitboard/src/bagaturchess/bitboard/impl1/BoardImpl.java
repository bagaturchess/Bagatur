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
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
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
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.bitboard.impl1.internal.SEEUtil;


public class BoardImpl implements IBitBoard {
	
	
	private ChessBoard chessBoard;
	private MoveGenerator generator;
	
	private IPiecesLists pieces;
	private IMaterialFactor materialFactor;
	private IBaseEval baseEval;
	private IMaterialState materialState;
	private IBoardConfig boardConfig;
	private IMoveOps moveOps;
	
	private IMoveList hasMovesList;
	
	
	public BoardImpl(String fen, IBoardConfig _boardConfig) {
		generator = new MoveGenerator();
		pieces = new PiecesListsImpl(this);
		materialFactor = new MaterialFactorImpl();
		baseEval = new BaseEvalImpl();
		materialState = new MaterialStateImpl();
		boardConfig = _boardConfig;
		moveOps = new MoveOpsImpl();
		
		if (boardConfig != null) {
			EvalConstants.initPSQT(boardConfig);
		}
		
		chessBoard = ChessBoardUtil.getNewCB(fen);
		
		hasMovesList = new BaseMoveList(250);
	}
	
	
	public ChessBoard getChessBoard() {
		return chessBoard;
	}
	
	
	public MoveGenerator getMoveGenerator() {
		return generator;
	}
	
	
	@Override
	public boolean isInCheck() {
		return chessBoard.checkingPieces != 0;
	}
	
	
	@Override
	public boolean isInCheck(int colour) {
		return CheckUtil.isInCheck(chessBoard, colour);
	}
	
	
	@Override
	public String toString() {
		return chessBoard.toString();
	}
	
	
	@Override
	public synchronized int genAllMoves(IInternalMoveList list) {
		
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
		return genAllMoves(list);
	}
	
	
	@Override
	public synchronized int genCapturePromotionMoves(IInternalMoveList list) {
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
	public void makeMoveForward(String ucimove) {
		MoveWrapper move = new MoveWrapper(ucimove, chessBoard);
		chessBoard.doMove(move.move);
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
	public int getSEEScore(int move) {
		return SEEUtil.getSeeCaptureScore(chessBoard, move);
	}
	
	
	@Override
	public int getSEEFieldScore(int squareID) {
		return SEEUtil.getSeeFieldScore(chessBoard, squareID);
	}
	
	
	@Override
	public void revert() {
		for(int i = chessBoard.playedMovesCount - 1; i >= 0; i--) {
			int move = chessBoard.playedMoves[i];
			if (move == 0) {
				chessBoard.undoNullMove();
			} else {
				chessBoard.undoMove(move);
			}
		}
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
		return chessBoard.pieces[colour][type];
	}
	
	
	@Override
	public long getFiguresBitboardByColour(int colour) {
		return getFiguresBitboardByColourAndType(colour, PAWN)
				| getFiguresBitboardByColourAndType(colour, NIGHT)
				| getFiguresBitboardByColourAndType(colour, BISHOP)
				| getFiguresBitboardByColourAndType(colour, ROOK)
				| getFiguresBitboardByColourAndType(colour, QUEEN)
				| getFiguresBitboardByColourAndType(colour, KING);
	}
	
	
	@Override
	public long getFreeBitboard() {
		return chessBoard.emptySpaces;
	}
	
	
	@Override
	public boolean hasRightsToKingCastle(int colour) {
		if (colour == WHITE) {
			return (chessBoard.castlingRights & 8) != 0;
		} else {
			return (chessBoard.castlingRights & 2) != 0;
		}
	}
	
	
	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		if (colour == WHITE) {
			return (chessBoard.castlingRights & 4) != 0;
		} else {
			return (chessBoard.castlingRights & 1) != 0;
		}
	}
	
	
	@Override
	public int getFigureID(int squareID) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public int getFigureType(int squareID) {
		int pieceType = chessBoard.pieceIndexes[squareID];
		return pieceType;
	}
	
	
	@Override
	public int getFigureColour(int squareID) {
		long bb = (1L << squareID);
		if ((bb & getFiguresBitboardByColour(Constants.COLOUR_WHITE)) != 0) {
			return Constants.COLOUR_WHITE;
		} else {
			return Constants.COLOUR_BLACK;
		}
	}
	
	
	public boolean isDraw50movesRule() {
		return chessBoard.lastCaptureOrPawnMoveBefore >= 100;
	}
	
	public int getDraw50movesRule() {
		return chessBoard.lastCaptureOrPawnMoveBefore;
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
		return chessBoard.getRepetition();
	}
	
	
	@Override
	public boolean hasSufficientMaterial() {
		
		if (materialFactor.getTotalFactor() > 24) { // 2w knights + 2b knights
			return true;
		}
		
		
		/**
		 * If has pawn - true
		 */
		long w_pawns = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		if (w_pawns != 0L) {
			return true;
		}
		long b_pawns = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		if (b_pawns != 0L) {
			return true;
		}
		
		/**
		 * If has queen - true
		 */
		long w_queens = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		if (w_queens != 0L) {
			return true;
		}
		long b_queens = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		if (b_queens != 0L) {
			return true;
		}

		/**
		 * If has rook - true
		 */
		long w_rooks = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		if (w_rooks != 0L) {
			return true;
		}
		long b_rooks = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		if (b_rooks != 0L) {
			return true;
		}
		
		int o1 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER));
		int k1 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT));
		
		int mi1 = o1 + k1;
		
		int o2 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER));
		int k2 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT));
		
		int mi2 = o2 + k2;

		if (mi1 <= 1 && mi2 <= 1) {
			return false;
		}
		
		if (o1 == 0 && o2 == 0) {
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public int getLastMove() {
		if (chessBoard.playedMovesCount == 0) {
			return 0;
		}
		return chessBoard.playedMoves[chessBoard.playedMovesCount - 1];
	}
	
	
	@Override
	public boolean isCheckMove(int move) {
		
		boolean inCheck = false;
		
		chessBoard.doMove(move);
		inCheck = chessBoard.checkingPieces != 0;
		chessBoard.undoMove(move);
		
		return inCheck;
	}
	
	
	@Override
	public boolean isPossible(int move) {
		return chessBoard.isValidMove(move) && chessBoard.isLegal(move);
	}
	
	
	@Override
	public IMaterialState getMaterialState() {
		return materialState;
	}
	
	
	@Override
	public IBoardConfig getBoardConfig() {
		return boardConfig;
	}
	
	
	@Override
	public int[] getMatrix() {
		//throw new UnsupportedOperationException();
		return chessBoard.pieceIndexes;
	}
	
	
	@Override
	public synchronized boolean hasMoveInCheck() {
		hasMovesList.clear();
		genAllMoves(hasMovesList);
		return hasMovesList.reserved_getCurrentSize() > 0;
	}
	
	
	@Override
	public synchronized boolean hasMoveInNonCheck() {
		hasMovesList.clear();
		genAllMoves(hasMovesList);
		return hasMovesList.reserved_getCurrentSize() > 0;
	}
	
	
	@Override
	public synchronized boolean hasSingleMove() {
		hasMovesList.clear();
		genAllMoves(hasMovesList);
		return hasMovesList.reserved_getCurrentSize() == 1;
	}
	
	
	@Override
	public IMoveOps getMoveOps() {
		return moveOps;
	}
	
	
	@Override
	public int getPlayedMovesCount() {
		return chessBoard.playedMovesCount;
	}
	
	
	@Override
	public int[] getPlayedMoves() {
		return chessBoard.playedMoves;
	}
	
	
	@Override
	public String toEPD() {
		return chessBoard.toString();
	}
	
	
	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		//Do nothing
	}
	
	
	@Override
	public long getHashKeyAfterMove(int move) {
		throw new UnsupportedOperationException("TODO");
	}
	
	
	public final IGameStatus getStatus() {
		
		
		int colourToMove = getColourToMove();
		
		
		if (getStateRepetition() >= 2) {
			//3 states repetition draw
			return IGameStatus.DRAW_3_STATES_REPETITION;
		}
		
		if (isInCheck()) {
			if (!hasMoveInCheck()) {
				//Mate
				if (colourToMove == Figures.COLOUR_WHITE) {
					return IGameStatus.MATE_BLACK_WIN;
				} else {
					return IGameStatus.MATE_WHITE_WIN;
				}
			}
		} else {
			if (!hasMoveInNonCheck()) {
				//Stale Mate
				if (colourToMove == Figures.COLOUR_WHITE) {
					return IGameStatus.STALEMATE_WHITE_NO_MOVES;
				} else {
					return IGameStatus.STALEMATE_BLACK_NO_MOVES;
				}
			}
		}
		
		if (!hasSufficientMaterial()) {
			return IGameStatus.NO_SUFFICIENT_MATERIAL;
		}
		
		if (isDraw50movesRule()) {
			return IGameStatus.DRAW_50_MOVES_RULE;
		}
		
		return IGameStatus.NONE;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getPawnsStructure()
	 */
	@Override
	public PawnsModelEval getPawnsStructure() {
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
	 * @see bagaturchess.bitboard.api.IBoard#getCastlingType(int)
	 */
	@Override
	public int getCastlingType(int colour) {
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
		
		
		private static final int TOTAL_FACTOR_MAX = 2 * 28 + 4 * 13 + 4 * 6 + 4 * 6; 
		//public static final int[] PHASE 					= {0, 0, 6, 6, 13, 28};
		
		
		public MaterialFactorImpl() {
		}
		
		
		@Override
		public int getBlackFactor() {
			return getTotalFactor() / 2;
		}
		
		
		@Override
		public int getWhiteFactor() {
			return getTotalFactor() / 2;
		}
		
		
		@Override
		public int getTotalFactor() {
			return TOTAL_FACTOR_MAX - chessBoard.phase;
		}
		
		
		@Override
		public double getOpenningPart() {
			if (getTotalFactor() < 0) {
				throw new IllegalStateException();
			}
			return Math.min(1, getTotalFactor() / (double) TOTAL_FACTOR_MAX);
		}
		
		
		@Override
		public int interpolateByFactor(int val_o, int val_e) {
			double openningPart = getOpenningPart();
			int result = (int) (val_o * openningPart + (val_e * (1 - openningPart)));
			return result;
		}
		
		
		@Override
		public int interpolateByFactor(double val_o, double val_e) {
			double openningPart = getOpenningPart();
			int result = (int) (val_o * openningPart + (val_e * (1 - openningPart)));
			return result;
		}
	}
	
	
	protected class BaseEvalImpl implements IBaseEval {

		
		public BaseEvalImpl() {
		}
		
		
		@Override
		public int getPST_o() {
			return chessBoard.psqtScore_mg;
		}
		
		
		@Override
		public int getPST_e() {
			return chessBoard.psqtScore_eg;
		}
		
		
		@Override
		public int getMaterial(int pieceType) {
			
			switch (pieceType) {
				case ChessConstants.PAWN: return (int) Math.max(boardConfig.getMaterial_PAWN_O(), boardConfig.getMaterial_PAWN_E());
				case ChessConstants.NIGHT: return (int) Math.max(boardConfig.getMaterial_KNIGHT_O(), boardConfig.getMaterial_KNIGHT_E());
				case ChessConstants.BISHOP: return (int) Math.max(boardConfig.getMaterial_BISHOP_O(), boardConfig.getMaterial_BISHOP_E());
				case ChessConstants.ROOK: return (int) Math.max(boardConfig.getMaterial_ROOK_O(), boardConfig.getMaterial_ROOK_E());
				case ChessConstants.QUEEN: return (int) Math.max(boardConfig.getMaterial_QUEEN_O(), boardConfig.getMaterial_QUEEN_E());
				case ChessConstants.KING: return (int) Math.max(boardConfig.getMaterial_KING_O(), boardConfig.getMaterial_KING_E());
			}
			
			throw new IllegalStateException("pieceType=" + pieceType);
		}
		
		
		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_o()
		 */
		@Override
		public int getMaterial_o() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_e()
		 */
		@Override
		public int getMaterial_e() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_o()
		 */
		@Override
		public int getWhiteMaterialPawns_o() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_e()
		 */
		@Override
		public int getWhiteMaterialPawns_e() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_o()
		 */
		@Override
		public int getBlackMaterialPawns_o() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_e()
		 */
		@Override
		public int getBlackMaterialPawns_e() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_o()
		 */
		@Override
		public int getWhiteMaterialNonPawns_o() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_e()
		 */
		@Override
		public int getWhiteMaterialNonPawns_e() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_o()
		 */
		@Override
		public int getBlackMaterialNonPawns_o() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_e()
		 */
		@Override
		public int getBlackMaterialNonPawns_e() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_O()
		 */
		@Override
		public int getMaterial_BARIER_NOPAWNS_O() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_E()
		 */
		@Override
		public int getMaterial_BARIER_NOPAWNS_E() {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getMaterialGain(int)
		 */
		@Override
		public int getMaterialGain(int move) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IBaseEval#getPSTMoveGoodPercent(int)
		 */
		@Override
		public double getPSTMoveGoodPercent(int move) {
			throw new UnsupportedOperationException();
		}
	}
	
	
	protected class PiecesListsImpl implements IPiecesLists {
		
		
		private PiecesList list;
		
		
		PiecesListsImpl(IBoard board) {
			list = new PiecesList(board, 8);
			list.add(16);
			list.add(32);
		}
		
		
		@Override
		public PiecesList getPieces(int pid) {
			return list;
		}
		
		
		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#rem(int, int)
		 */
		@Override
		public void rem(int pid, int fieldID) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#add(int, int)
		 */
		@Override
		public void add(int pid, int fieldID) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#move(int, int, int)
		 */
		@Override
		public void move(int pid, int fromFieldID, int toFieldID) {
			throw new UnsupportedOperationException();
		}
	}
	
	
	private class MoveOpsImpl implements IMoveOps {
		
		
		private final int FILES[] = { 7, 6, 5, 4, 3, 2, 1, 0 };
		private final int RANKS[] = { 0, 1, 2, 3, 4, 5, 6, 7 };
		
		
		@Override
		public int getFigureType(int move) {
			return MoveUtil.getSourcePieceIndex(move);
		}
		
		
		@Override
		public int getToFieldID(int move) {
			return MoveUtil.getToIndex(move);
		}
		
		@Override
		public boolean isCapture(int move) {
			return MoveUtil.getAttackedPieceIndex(move) != 0;
		}
		
		
		@Override
		public boolean isPromotion(int move) {
			return MoveUtil.isPromotion(move);
		}
		
		
		@Override
		public boolean isCaptureOrPromotion(int move) {
			return isCapture(move) || isPromotion(move);
		}

		
		@Override
		public boolean isEnpassant(int move) {
			return MoveUtil.isEPMove(move);
		}

		
		@Override
		public boolean isCastling(int move) {
			return MoveUtil.isCastlingMove(move);
		}
		
		
		@Override
		public int getFigurePID(int move) {
			
			int pieceType = MoveUtil.getSourcePieceIndex(move);
			int colour = chessBoard.colorToMove;
			
			if (colour == WHITE) {
				switch(pieceType) {
					case ChessConstants.PAWN: return Constants.PID_W_PAWN;
					case ChessConstants.NIGHT: return Constants.PID_W_KNIGHT;
					case ChessConstants.BISHOP: return Constants.PID_W_BISHOP;
					case ChessConstants.ROOK: return Constants.PID_W_ROOK;
					case ChessConstants.QUEEN: return Constants.PID_W_QUEEN;
					case ChessConstants.KING: return Constants.PID_W_KING;
				}
			} else {
				switch(pieceType) {
					case ChessConstants.PAWN: return Constants.PID_B_PAWN;
					case ChessConstants.NIGHT: return Constants.PID_B_KNIGHT;
					case ChessConstants.BISHOP: return Constants.PID_B_BISHOP;
					case ChessConstants.ROOK: return Constants.PID_B_ROOK;
					case ChessConstants.QUEEN: return Constants.PID_B_QUEEN;
					case ChessConstants.KING: return Constants.PID_B_KING;
				}
			}
			
			throw new IllegalStateException("pieceType=" + pieceType);
		}
		
		
		@Override
		public boolean isCastlingKingSide(int move) {
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == 1 || index == 57;
			}
			
			return false;
		}
		
		
		@Override
		public boolean isCastlingQueenSide(int move) {
			
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == 5 || index == 61; 
			}
			
			return false;
		}
		
		
		@Override
		public int getFromFieldID(int move) {
			return MoveUtil.getFromIndex(move);
		}
		
		
		@Override
		public int getPromotionFigureType(int move) {
			if (!isPromotion(move)) {
				return 0;
			}
			return MoveUtil.getMoveType(move);
		}
		
		
		@Override
		public int getCapturedFigureType(int cur_move) {
			return MoveUtil.getAttackedPieceIndex(cur_move);
		}
		
		
		@Override
		public String moveToString(int move) {
			return (new MoveWrapper(move)).toString();
		}
		
		@Override
		public int stringToMove(String move) {
			MoveWrapper moveObj = new MoveWrapper(move, chessBoard);
			return moveObj.move;
		}
		
		
		@Override
		public int getToField_File(int move) {
			return FILES[getToFieldID(move) & 7];
		}
		
		
		@Override
		public int getToField_Rank(int move) {
			return RANKS[getToFieldID(move) >>> 3];
		}
		
		
		@Override
		public int getFromField_File(int move) {
			return FILES[getFromFieldID(move) & 7];
		}
		
		
		@Override
		public int getFromField_Rank(int move) {
			return RANKS[getFromFieldID(move) >>> 3];
		}
	}
}
