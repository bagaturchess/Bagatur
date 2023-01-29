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
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
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
	
	private MoveListener[] moveListeners;
	
	private NNUE_Input nnue_input;
	
	private boolean enable_NNUE_Input = true;
	
	protected IBoard.CastlingType[] castledByColour;
	
	private boolean isFRC;
	
	
	public BoardImpl(String fen, IBoardConfig _boardConfig, boolean _isFRC) {
		
		boardConfig = _boardConfig;
		
		isFRC = _isFRC;
		
		generator = new MoveGenerator();
		
		pieces = new PiecesListsImpl(this);
		
		materialFactor = new MaterialFactorImpl();
		
		materialState = new MaterialStateImpl();
		
		moveOps = new MoveOpsImpl();
		
		hasMovesList = new BaseMoveList(250);
		
		castledByColour = new IBoard.CastlingType[2];
		castledByColour[Constants.COLOUR_WHITE] = IBoard.CastlingType.NONE;
		castledByColour[Constants.COLOUR_BLACK] = IBoard.CastlingType.NONE;
		
		moveListeners = new MoveListener[0];
		
		addMoveListener(materialFactor);
		
		
		if (boardConfig != null) {
			
			EvalConstants.initPSQT(boardConfig);
			
			chessBoard = ChessBoardUtil.getNewCB(fen);
			
			baseEval = new BaseEvaluation(boardConfig, this);
			
			for (int color = 0; color < 2; color++) {
				for (int piece = PAWN; piece <= KING; piece++) {
					long pieces = chessBoard.pieces[color][piece];
					while (pieces != 0) {
						baseEval.initially_addPiece(color, piece, pieces);
						pieces &= pieces - 1;
					}
				}
			}
			
			addMoveListener(baseEval);
			
		} else {
			
			chessBoard = ChessBoardUtil.getNewCB(fen);
		}
		
		
		if (enable_NNUE_Input) {
			
			nnue_input = new NNUE_Input(this);
			
			for (int color = 0; color < 2; color++) {
				for (int piece = PAWN; piece <= KING; piece++) {
					long pieces = chessBoard.pieces[color][piece];
					nnue_input.initially_addPiece(color, piece, pieces);
				}
			}
			
			addMoveListener(nnue_input);
		}
	}
	
	
	public boolean isFRC() {
		
		return isFRC;
	}
	
	
	private void addMoveListener(MoveListener listener) {
		MoveListener[] oldMoveListeners = moveListeners;
		MoveListener[] newMoveListeners = new MoveListener[moveListeners.length + 1];
		if (oldMoveListeners.length > 0) {
			for (int i=0; i<oldMoveListeners.length; i++) {
				newMoveListeners[i] = oldMoveListeners[i];
			}
		}
		
		newMoveListeners[oldMoveListeners.length] = listener;
		
		moveListeners = newMoveListeners;
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
		
		String moves_str = "";
		int[] moves = chessBoard.playedMoves;
		for (int i = 0; i < chessBoard.playedMovesCount; i++) {
			moves_str += moveOps.moveToString(moves[i]) + " ";
		}
		
		return chessBoard.toString() + " moves " + moves_str;
	}
	
	
	@Override
	public synchronized int genAllMoves(IInternalMoveList list) {
		
		generator.startPly();
		
		generator.generateAttacks(chessBoard);
		generator.generateMoves(chessBoard);
		
		int counter = 0;
		while (generator.hasNext()) {
			int cur_move = generator.next();
			if (!isPossible(cur_move)) {
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
			if (!isPossible(cur_move)) {
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
		
		
		try {
			
			
			if (moveOps.isCastling(move)) {
				
				castledByColour[getColourToMove()] = moveOps.isCastlingKingSide(move) ? IBoard.CastlingType.KINGSIDE : IBoard.CastlingType.QUEENSIDE;
			}
			
			
			if (moveListeners.length > 0) {
				
				for (int i=0; i<moveListeners.length; i++) {
					
					moveListeners[i].preForwardMove(chessBoard.colorToMove, move);
				}
			}
			

		
			chessBoard.doMove(move);
			
			
			if (moveListeners.length > 0) {
				
				for (int i=0; i<moveListeners.length; i++) {
					
					moveListeners[i].postForwardMove(chessBoard.colorToMoveInverse, move);
				}
			}
		
		} catch(Exception cause) {
			
			throw new IllegalStateException(this.toString(), cause);
		}
	}
	
	
	@Override
	public void makeMoveBackward(int move) {
		
		
		try {
			
			
			if (moveListeners.length > 0) {
				for (int i=0; i<moveListeners.length; i++) {
					moveListeners[i].preBackwardMove(chessBoard.colorToMoveInverse, move);
				}
			}
		
		
			chessBoard.undoMove(move);
			
			
			if (moveOps.isCastling(move)) {
				
				castledByColour[getColourToMove()] = IBoard.CastlingType.NONE;
			}
			
			
			if (moveListeners.length > 0) {
				for (int i=0; i<moveListeners.length; i++) {
					moveListeners[i].postBackwardMove(getColourToMove(), move);
				}
			}
		
		} catch(Exception cause) {
			
			throw new IllegalStateException(this.toString(), cause);
		}
	}
	
	
	@Override
	public void makeMoveForward(String ucimove) {
		MoveWrapper move = new MoveWrapper(ucimove, chessBoard, isFRC);
		makeMoveForward(move.move);
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
	public int getEnpassantSquareID() {
		return chessBoard.epIndex;
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
				makeNullMoveBackward();
			} else {
				makeMoveBackward(move);
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
	public IBoard.CastlingType getCastlingType(int colour) {
		
		return castledByColour[colour];
	}
	
	
	@Override
	public CastlingPair getCastlingPair() {
		
		
		if (castledByColour[Constants.COLOUR_WHITE] == null || castledByColour[Constants.COLOUR_BLACK] == null) {
			
			throw new IllegalStateException();
		}
		
		
		switch (castledByColour[Constants.COLOUR_WHITE]) {
		
			case NONE:
				
				switch (castledByColour[Constants.COLOUR_BLACK]) {
				
					case NONE:
						
						return CastlingPair.NONE_NONE;
						
					case KINGSIDE:
						
						return CastlingPair.NONE_KINGSIDE;
						
					case QUEENSIDE:
						
						return CastlingPair.NONE_QUEENSIDE;
						
					default:
						
						throw new IllegalStateException();
				}
				
			case KINGSIDE:
				
				switch (castledByColour[Constants.COLOUR_BLACK]) {
				
					case NONE:
						
						return CastlingPair.KINGSIDE_NONE;
						
					case KINGSIDE:
						
						return CastlingPair.KINGSIDE_KINGSIDE;
						
					case QUEENSIDE:
						
						return CastlingPair.KINGSIDE_QUEENSIDE;
						
					default:
						
						throw new IllegalStateException();
				}
				
			case QUEENSIDE:
				
				switch (castledByColour[Constants.COLOUR_BLACK]) {
				
					case NONE:
						
						return CastlingPair.QUEENSIDE_NONE;
						
					case KINGSIDE:
						
						return CastlingPair.QUEENSIDE_KINGSIDE;
						
					case QUEENSIDE:
						
						return CastlingPair.QUEENSIDE_QUEENSIDE;
						
					default:
						
						throw new IllegalStateException();
				}
				
			default:
				
				throw new IllegalStateException();
		}
	}
	
	
	@Override
	public CastlingConfig getCastlingConfig() {
		
		return chessBoard.castlingConfig;
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
	public boolean hasSufficientMatingMaterial() {
		
		return hasSufficientMatingMaterial(Figures.COLOUR_WHITE) || hasSufficientMatingMaterial(Figures.COLOUR_BLACK);
	}
	
	
	@Override
	public boolean hasSufficientMatingMaterial(int color) {
		
		
		/**
		 * If has pawn = true
		 */
		long pawns = getFiguresBitboardByColourAndType(color, Figures.TYPE_PAWN);
		if (pawns != 0L) {
			return true;
		}
		
		
		/**
		 * If has queen = true
		 */
		long queens = getFiguresBitboardByColourAndType(color, Figures.TYPE_QUEEN);
		if (queens != 0L) {
			return true;
		}
		
		
		/**
		 * If has rook = true
		 */
		long rooks = getFiguresBitboardByColourAndType(color, Figures.TYPE_CASTLE);
		if (rooks != 0L) {
			return true;
		}
		
		
		long bishops = getFiguresBitboardByColourAndType(color, Figures.TYPE_OFFICER);
		long knights = getFiguresBitboardByColourAndType(color, Figures.TYPE_KNIGHT);
		
		
		/**
		 * If has 3 or more bishops and knights = true
		 */
		if (Utils.countBits(bishops) + Utils.countBits(knights) >= 3) {
			
			return true;
		}
		
		
		/**
		 * If has 2 different colors bishop = true
		 */
		if (bishops != 0L) {
			
			if ((bishops & Fields.ALL_WHITE_FIELDS) != 0 && (bishops & Fields.ALL_BLACK_FIELDS) != 0) {
				
				return true;
			}
		}
		
		
		/**
		 * If has 1 bishop and 1 knight = true
		 */
		if (Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1) {
			
			if ((bishops & Fields.ALL_WHITE_FIELDS) != 0 && (bishops & Fields.ALL_BLACK_FIELDS) != 0) {
				
				return true;
			}
		}
		
		
		/**
		 * In all other cases = false
		 */
		return false;
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
		
		if (!hasSufficientMatingMaterial()) {
			return IGameStatus.NO_SUFFICIENT_MATERIAL;
		}
		
		if (isDraw50movesRule()) {
			return IGameStatus.DRAW_50_MOVES_RULE;
		}
		
		return IGameStatus.NONE;
	}
	
	
	@Override
	public float[] getNNUEInputs() {
		
		if (!enable_NNUE_Input) {
			
			throw new UnsupportedOperationException();
		}
		
		return nnue_input.getInputs();
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
		
		
		private static final int TOTAL_FACTOR_MAX = 2 * 9 + 4 * 5 + 4 * 3 + 4 * 3; 
		//public static final int[] PHASE 					= {0, 0, 3, 3, 5, 9};
		
		
		public MaterialFactorImpl() {
		}
		
		
		@Override
		public int getBlackFactor() {
			return chessBoard.material_factor_black;
		}
		
		
		@Override
		public int getWhiteFactor() {
			return chessBoard.material_factor_white;
		}
		
		
		@Override
		public int getTotalFactor() {
			
			return getWhiteFactor() + getBlackFactor();
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
			double result = (val_o * openningPart + (val_e * (1 - openningPart)));
			return (int) result;
		}


		@Override
		public void addPiece_Special(int pid, int fieldID) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void preForwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void postForwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void preBackwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void postBackwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void initially_addPiece(int color, int type, long bb_pieces) {
			// TODO Auto-generated method stub
			
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
		public final int getFigureType(int move) {
			return MoveUtil.getSourcePieceIndex(move);
		}
		
		
		@Override
		public final int getToFieldID(int move) {
			return MoveUtil.getToIndex(move);
		}
		
		@Override
		public final boolean isCapture(int move) {
			return MoveUtil.getAttackedPieceIndex(move) != 0;
		}
		
		
		@Override
		public final boolean isPromotion(int move) {
			return MoveUtil.isPromotion(move);
		}
		
		
		@Override
		public final boolean isCaptureOrPromotion(int move) {
			return isCapture(move) || isPromotion(move);
		}

		
		@Override
		public final boolean isEnpassant(int move) {
			return MoveUtil.isEPMove(move);
		}

		
		@Override
		public final boolean isCastling(int move) {
			return MoveUtil.isCastlingMove(move);
		}
		
		
		@Override
		public final int getFigurePID(int move) {
			
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
		public final boolean isCastlingKingSide(int move) {
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == CastlingConfig.G1 || index == CastlingConfig.G8;
			}
			
			return false;
		}
		
		
		@Override
		public final boolean isCastlingQueenSide(int move) {
			
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == CastlingConfig.C1 || index == CastlingConfig.C8; 
			}
			
			return false;
		}
		
		
		@Override
		public final int getFromFieldID(int move) {
			return MoveUtil.getFromIndex(move);
		}
		
		
		@Override
		public final int getPromotionFigureType(int move) {
			if (!isPromotion(move)) {
				return 0;
			}
			return MoveUtil.getMoveType(move);
		}
		
		
		@Override
		public final int getCapturedFigureType(int cur_move) {
			return MoveUtil.getAttackedPieceIndex(cur_move);
		}
		
		
		@Override
		public final String moveToString(int move) {
			return (new MoveWrapper(move, isFRC, chessBoard.castlingConfig)).toString();
		}
		
		
		@Override
		public final void moveToString(int move, StringBuilder text_buffer) {
			(new MoveWrapper(move, isFRC, chessBoard.castlingConfig)).toString(text_buffer);
		}
		
		
		@Override
		public final int stringToMove(String move) {
			MoveWrapper moveObj = new MoveWrapper(move, chessBoard, isFRC);
			return moveObj.move;
		}
		
		
		@Override
		public final int getToField_File(int move) {
			return FILES[getToFieldID(move) & 7];
		}
		
		
		@Override
		public final int getToField_Rank(int move) {
			return RANKS[getToFieldID(move) >>> 3];
		}
		
		
		@Override
		public final int getFromField_File(int move) {
			return FILES[getFromFieldID(move) & 7];
		}
		
		
		@Override
		public final int getFromField_Rank(int move) {
			return RANKS[getFromFieldID(move) >>> 3];
		}
	}
}
