package bagaturchess.bitboard.impl1;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMoveIterator;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;


public class Board3_Adapter extends Board3 implements IBitBoard {
	
	
	private IMoveList legalMovesChecker = new BaseMoveList(256);
	
	
	public Board3_Adapter(String fenStr, IBoardConfig boardConfig) {
		super(fenStr, boardConfig);
	}
	
	
	public Board3_Adapter(String fenStr, PawnsEvalCache pawnsCache, IBoardConfig boardConfig) {
		super(fenStr, pawnsCache, boardConfig);
	}

	
	public Board3_Adapter() {
		super();
	}


	public Board3_Adapter(String fen) {
		super(fen);
	}


	@Override
	public boolean isPossible(int move) {
		//throw new UnsupportedOperationException();
		return true;
	}
	
	@Override
	public void setPawnsCache(PawnsEvalCache _pawnsCache) {
		pawnsCache = _pawnsCache;
	}
	
	
	@Override
	public boolean isInCheck(int colour) {
		return super.isInCheck(colour);
	}
	
	
	public final long getFiguresBitboardByPID(int pid) {
		
		//if (true) throw new UnsupportedOperationException();
		
		//throw new IllegalStateException();
		
		PiecesList piecesList = pieces.getPieces(pid);
		int size = piecesList.getDataSize();
		int[] ids = piecesList.getData();
		long bitboard = 0L;
		for (int i=0; i<size; i++) {
			int fieldID = ids[i];
			bitboard |= Fields.ALL_A1H1[fieldID];
		}
		return bitboard;
		
	}
	
	
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		
		//if (true) throw new UnsupportedOperationException();
		
		return getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][type]);
	}
	
	
	public long getFiguresBitboardByColour(int colour) {
		
		//if (true) throw new UnsupportedOperationException();
		
		long result = 0L;
		if (colour == Constants.COLOUR_WHITE) {
			result |= getFiguresBitboardByPID(Constants.PID_W_KING);
			result |= getFiguresBitboardByPID(Constants.PID_W_PAWN);
			result |= getFiguresBitboardByPID(Constants.PID_W_BISHOP);
			result |= getFiguresBitboardByPID(Constants.PID_W_KNIGHT);
			result |= getFiguresBitboardByPID(Constants.PID_W_QUEEN);
			result |= getFiguresBitboardByPID(Constants.PID_W_ROOK);
		} else {
			result |= getFiguresBitboardByPID(Constants.PID_B_KING);
			result |= getFiguresBitboardByPID(Constants.PID_B_PAWN);
			result |= getFiguresBitboardByPID(Constants.PID_B_BISHOP);
			result |= getFiguresBitboardByPID(Constants.PID_B_KNIGHT);
			result |= getFiguresBitboardByPID(Constants.PID_B_QUEEN);
			result |= getFiguresBitboardByPID(Constants.PID_B_ROOK);
		}
		return result;
	}
	
	
	public final long getFreeBitboard() {
		long all = getFiguresBitboardByColour(Constants.COLOUR_WHITE) | getFiguresBitboardByColour(Constants.COLOUR_BLACK);
		return ~all;
	}
	
	
	@Override
	public IGameStatus getStatus() {
		//return IGameStatus.NONE;
		throw new UnsupportedOperationException();
	}
	

	@Override
	public void test(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMinorOrMajorPieces(int colour) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public int getLastCaptrueFieldID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFieldsAttacks getFieldsAttacks() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genMinorMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genPromotions(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int gen2MovesPromotions(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genDirectCheckMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genHiddenCheckMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genAllCheckMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genCapturePromotionCheckMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genKingEscapes(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMove() {
		
		if (true) throw new UnsupportedOperationException();
		
		int colourToMove = getColourToMove();
		
		legalMovesChecker.clear();
		genAllMoves(legalMovesChecker);
		
		boolean hasLegalMove = false;
		int move = 0;
		while ((move = legalMovesChecker.next()) != 0L) {
			makeMoveForward(move);
			if (!isInCheck(colourToMove)) {
				hasLegalMove = true;
			}
			makeMoveBackward(move);
			if (hasLegalMove) {
				break;
			}
		}
		
		return hasLegalMove;
	}

	@Override
	public boolean hasCapturePromotionCheck() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSingleMove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPromotions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean has2MovePromotions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChecks(int colour) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public boolean isCheckMove(int move) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirectCheckMove(int move) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getChecksCount(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPlayedMovesCount_Total() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getHashKeyAfterMove(int move) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getPawnHashKeyAfterMove(int move) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public long getFigureBitboardByID(int figureID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFieldID(int figureID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMoveIterator iterator(int iteratorFactoryHandler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getAttacksSupport() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getFieldsStateSupport() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getGamePhase() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

}
