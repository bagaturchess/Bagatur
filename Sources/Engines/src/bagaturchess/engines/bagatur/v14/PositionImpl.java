package bagaturchess.engines.bagatur.v14;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.PSTConstants;
import bagaturchess.bitboard.impl.state.PiecesList;


public class PositionImpl implements IPosition {
	
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,
	});
	
	
	private IBoard board;
	private PSTConstants pst;
	
	
	public PositionImpl(IBoard _board) {
		board = _board;
		pst = new PSTConstants(board.getBoardConfig());
	}
	
	
	private long convertBB(long bb) {
		//return bb;
		return Bits.reverse(bb);
	}
	
	
	private int convertPID_c2b(int pid) {
		
		if (pid == Piece.EMPTY) {
			return Constants.PID_NONE;
		}
		
		switch(pid) {
			case Piece.WPAWN:
				return Constants.PID_W_PAWN;
			case Piece.BPAWN:
				return Constants.PID_B_PAWN;
			case Piece.WKNIGHT:
				return Constants.PID_W_KNIGHT;
			case Piece.BKNIGHT:
				return Constants.PID_B_KNIGHT;
			case Piece.WKING:
				return Constants.PID_W_KING;
			case Piece.BKING:
				return Constants.PID_B_KING;
			case Piece.WBISHOP:
				return Constants.PID_W_BISHOP;
			case Piece.BBISHOP:
				return Constants.PID_B_BISHOP;
			case Piece.WROOK:
				return Constants.PID_W_ROOK;
			case Piece.BROOK:
				return Constants.PID_B_ROOK;
			case Piece.WQUEEN:
				return Constants.PID_W_QUEEN;
			case Piece.BQUEEN:
				return Constants.PID_B_QUEEN;
			default:
				throw new IllegalStateException("pid=" + pid);
		}
	}
	
	
	private int convertPID_b2c(int pid) {
		
		if (pid == Constants.PID_NONE) {
			return Piece.EMPTY;
		}
		
		switch(pid) {
			case Constants.PID_W_PAWN:
				return Piece.WPAWN;
			case Constants.PID_B_PAWN:
				return Piece.BPAWN;
			case Constants.PID_W_KNIGHT:
				return Piece.WKNIGHT;
			case Constants.PID_B_KNIGHT:
				return Piece.BKNIGHT;
			case Constants.PID_W_KING:
				return Piece.WKING;
			case Constants.PID_B_KING:
				return Piece.BKING;
			case Constants.PID_W_BISHOP:
				return Piece.WBISHOP;
			case Constants.PID_B_BISHOP:
				return Piece.BBISHOP;
			case Constants.PID_W_ROOK:
				return Piece.WROOK;
			case Constants.PID_B_ROOK:
				return Piece.BROOK;
			case Constants.PID_W_QUEEN:
				return Piece.WQUEEN;
			case Constants.PID_B_QUEEN:
				return Piece.BQUEEN;
			default:
				throw new IllegalStateException("pid=" + pid);
		}
	}
	
	
	@Override
	public int getPST1() {
		return board.getBaseEvaluation().getPST_o();
	}
	
	
	@Override
	public int getPST2() {
		return board.getBaseEvaluation().getPST_e();
	}
	
	
	@Override
	public int getPST1(int pid) {
		int pid1 = convertPID_c2b(pid);
		PiecesList pieces = board.getPiecesLists().getPieces(pid1);
		int[] data = pieces.getData();
		int size = pieces.getDataSize();
		int result = 0;
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			double[] arr = pst.getArray_o(Constants.PIECE_IDENTITY_2_TYPE[pid1]);
			int colour = Constants.getColourByPieceIdentity(pid1);
			if (colour == Figures.COLOUR_BLACK) {
				fieldID = HORIZONTAL_SYMMETRY[fieldID];
			}
			result += arr[fieldID];
		}
		return result;
	}
	
	
	@Override
	public int getPST2(int pid) {
		int pid1 = convertPID_c2b(pid);
		PiecesList pieces = board.getPiecesLists().getPieces(pid1);
		int[] data = pieces.getData();
		int size = pieces.getDataSize();
		int result = 0;
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			double[] arr = pst.getArray_e(Constants.PIECE_IDENTITY_2_TYPE[pid1]);
			int colour = Constants.getColourByPieceIdentity(pid1);
			if (colour == Figures.COLOUR_BLACK) {
				fieldID = HORIZONTAL_SYMMETRY[fieldID];
			}
			result += arr[fieldID];
		}
		return result;
	}
	
	
	@Override
	public long getPieceTypeBB(int pid) {
		int pid1 = convertPID_c2b(pid);
		return convertBB(board.getFiguresBitboardByPID(pid1));
	}


	@Override
	public long getWhiteBB() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE));
	}
	
	
	@Override
	public long getBlackBB() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_BLACK));
	}
	
	
	@Override
	public int getPIDBySquare(int squareID) {
		return convertPID_b2c(board.getFigureID(squareID));
	}
	
	
	@Override
	public int getWKingSQ() {
		return board.getPiecesLists().getPieces(Constants.PID_W_KING).getData()[0];
	}
	
	
	@Override
	public int getBKingSQ() {
		return board.getPiecesLists().getPieces(Constants.PID_B_KING).getData()[0];
	}
	
	
	@Override
	public long pawnZobristHash() {
		return board.getPawnsHashKey();
	}
	
	
	@Override
	public long kingZobristHash() {
		throw new IllegalStateException();
		//return 0;
		//return board.getPawnsHashKey();
	}
	
	
	@Override
	public int getKingSq(boolean whiteMove) {
		return isWhiteMove() ? getWKingSQ() : getBKingSQ();
	}
	
	
	@Override
	public int getwMtrl() {
		int score = 0;
		
		score += getwMtrlPawns();
		score += Evaluate.nV * board.getPiecesLists().getPieces(Constants.PID_W_KNIGHT).getDataSize();
		score += Evaluate.bV * board.getPiecesLists().getPieces(Constants.PID_W_BISHOP).getDataSize();
		score += Evaluate.rV * board.getPiecesLists().getPieces(Constants.PID_W_ROOK).getDataSize();
		score += Evaluate.qV * board.getPiecesLists().getPieces(Constants.PID_W_QUEEN).getDataSize();
		score += Evaluate.kV * board.getPiecesLists().getPieces(Constants.PID_W_KING).getDataSize();
		
		return score;
	}
	
	
	@Override
	public int getbMtrl() {
		int score = 0;
		
		score += getbMtrlPawns();
		score += Evaluate.nV * board.getPiecesLists().getPieces(Constants.PID_B_KNIGHT).getDataSize();
		score += Evaluate.bV * board.getPiecesLists().getPieces(Constants.PID_B_BISHOP).getDataSize();
		score += Evaluate.rV * board.getPiecesLists().getPieces(Constants.PID_B_ROOK).getDataSize();
		score += Evaluate.qV * board.getPiecesLists().getPieces(Constants.PID_B_QUEEN).getDataSize();
		score += Evaluate.kV * board.getPiecesLists().getPieces(Constants.PID_B_KING).getDataSize();
		
		return score;
	}
	
	
	@Override
	public int getwMtrlPawns() {
		return Evaluate.pV * board.getPiecesLists().getPieces(Constants.PID_W_PAWN).getDataSize();
	}
	
	
	@Override
	public int getbMtrlPawns() {
		return Evaluate.pV * board.getPiecesLists().getPieces(Constants.PID_B_PAWN).getDataSize();
	}
	
	
	@Override
	public boolean isWhiteMove() {
		return board.getColourToMove() == Constants.COLOUR_WHITE;
	}
	
	
	@Override
	public int getCastleMask() {
		return 0;
		/*int result = 0;
		if (board.getCastlingType(board.getColourToMove()) != CastlingType.NONE) {
			result += 3;
		} else {
			if (board.hasRightsToKingCastle(board.getColourToMove())) {
				result += 1;
			}
			if (board.hasRightsToQueenCastle(board.getColourToMove())) {
				result += 1;
			}
		}
		return result;*/
	}
	
	
	@Override
	public boolean a1Castle() {
		return board.hasRightsToQueenCastle(Constants.COLOUR_WHITE);
	}
	
	
	@Override
	public boolean h1Castle() {
		return board.hasRightsToKingCastle(Constants.COLOUR_WHITE);
	}
	
	
	@Override
	public boolean a8Castle() {
		return board.hasRightsToQueenCastle(Constants.COLOUR_BLACK);
	}
	
	
	@Override
	public boolean h8Castle() {
		return board.hasRightsToKingCastle(Constants.COLOUR_BLACK);
	}
	
	
	@Override
	public int getY(int sq) {
		return BitBoard.getY(sq);
	}
	
	
	@Override
	public int getX(int sq) {
		return BitBoard.getX(sq);
	}

}
