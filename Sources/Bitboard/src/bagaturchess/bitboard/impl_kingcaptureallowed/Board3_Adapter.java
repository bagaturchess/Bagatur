package bagaturchess.bitboard.impl_kingcaptureallowed;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveOpsImpl;
import bagaturchess.bitboard.impl.state.PiecesList;


public class Board3_Adapter extends Board3 implements IBitBoard {
	
	
	private IMoveOps moveOps;
	
	
	public Board3_Adapter(String fenStr, IBoardConfig boardConfig) {
		super(fenStr, boardConfig);
		moveOps = new MoveOpsImpl(this);
	}
	
	
	public Board3_Adapter(String fenStr, PawnsEvalCache pawnsCache, IBoardConfig boardConfig) {
		super(fenStr, pawnsCache, boardConfig);
		moveOps = new MoveOpsImpl(this);
	}

	
	public Board3_Adapter() {
		super();
		moveOps = new MoveOpsImpl(this);
	}


	public Board3_Adapter(String fen) {
		super(fen);
		moveOps = new MoveOpsImpl(this);
	}


	@Override
	public int genKingEscapes(IInternalMoveList list) {
		return genAllMoves(list);
	}
	
	
	@Override
	public int getFigureType(int fieldID) {
		return Figures.getFigureType(getFigureID(fieldID));
	}
	
	
	@Override
	public int getFigureColour(int fieldID) {
		return Figures.getFigureColour(getFigureID(fieldID));
	}
	
	
	@Override
	public boolean isPossible(int move) {
		throw new UnsupportedOperationException();
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
	public IPlayerAttacks getPlayerAttacks(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFieldsAttacks getFieldsAttacks() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean hasSingleMove() {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public boolean isCheckMove(int move) {
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
	 * @see bagaturchess.bitboard.api.IBoard#getSEEScore(int)
	 */
	@Override
	public int getSEEScore(int move) {
		return getSee().evalExchange(move);
	}
	
	
	@Override
	public int getSEEFieldScore(int squareID) {
		return getSee().seeField(squareID);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getHashKeyAfterMove(int)
	 */
	@Override
	public long getHashKeyAfterMove(int move) {
		throw new UnsupportedOperationException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBoard#getMoveOps()
	 */
	@Override
	public IMoveOps getMoveOps() {
		return moveOps;
	}


	@Override
	public int getEnpassantSquareID() {
		throw new UnsupportedOperationException();
	}
}
