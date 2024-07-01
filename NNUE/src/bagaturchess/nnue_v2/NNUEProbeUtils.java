package bagaturchess.nnue_v2;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.bitboard.api.IBitBoard;


public class NNUEProbeUtils {
	
	//Necessary to convert squares IDs encoded by H1A8 to encoded by A1H8
	private static int[] SQUARE_MAPPING = new int[] {
		7, 6, 5, 4, 3, 2, 1, 0,
		15, 14, 13, 12, 11, 10, 9, 8,
		23, 22, 21, 20, 19, 18, 17, 16,
		31, 30, 29, 28, 27, 26, 25, 24,
		39, 38, 37, 36, 35, 34, 33, 32,
		47, 46, 45, 44, 43, 42, 41, 40,
		55, 54, 53, 52, 51, 50, 49, 48,
		63, 62, 61, 60, 59, 58, 57, 56,
	};
	
	public static final void fillInput(IBitBoard bitboard, Input input) {
		
		ChessBoard cb = ((BoardImpl)bitboard).getChessBoard();
		
		long bb_w_king 		= cb.pieces[ChessConstants.WHITE][ChessConstants.KING];
		long bb_b_king 		= cb.pieces[ChessConstants.BLACK][ChessConstants.KING];
		long bb_w_queens 	= cb.pieces[ChessConstants.WHITE][ChessConstants.QUEEN];
		long bb_b_queens 	= cb.pieces[ChessConstants.BLACK][ChessConstants.QUEEN];
		long bb_w_rooks 	= cb.pieces[ChessConstants.WHITE][ChessConstants.ROOK];
		long bb_b_rooks 	= cb.pieces[ChessConstants.BLACK][ChessConstants.ROOK];
		long bb_w_bishops 	= cb.pieces[ChessConstants.WHITE][ChessConstants.BISHOP];
		long bb_b_bishops 	= cb.pieces[ChessConstants.BLACK][ChessConstants.BISHOP];
		long bb_w_knights 	= cb.pieces[ChessConstants.WHITE][ChessConstants.NIGHT];
		long bb_b_knights 	= cb.pieces[ChessConstants.BLACK][ChessConstants.NIGHT];
		long bb_w_pawns 	= cb.pieces[ChessConstants.WHITE][ChessConstants.PAWN];
		long bb_b_pawns 	= cb.pieces[ChessConstants.BLACK][ChessConstants.PAWN];
		
		int index_white 	= 0;
		int index_black 	= 0;
		
		//White king
		input.white_king_sq = getSquareID(bb_w_king);
		input.white_pieces[index_white] = convertPiece(ChessConstants.KING, ChessConstants.WHITE);
		input.white_squares[index_white] = input.white_king_sq;
		index_white++;
		
		
		//Black king
		input.black_king_sq	= getSquareID(bb_b_king);
		input.black_pieces[index_black] = convertPiece(ChessConstants.KING, ChessConstants.BLACK);
		input.black_squares[index_black] = input.black_king_sq;
		index_black++;
		
		
		//White queens
		while (bb_w_queens != 0) {
			input.white_pieces[index_white] 	= convertPiece(ChessConstants.QUEEN, ChessConstants.WHITE);
			input.white_squares[index_white] 	= getSquareID(bb_w_queens);
			index_white++;
			bb_w_queens &= bb_w_queens - 1;
		}
		
		//Black queens
		while (bb_b_queens != 0) {
			input.black_pieces[index_black] 	= convertPiece(ChessConstants.QUEEN, ChessConstants.BLACK);
			input.black_squares[index_black] 	= getSquareID(bb_b_queens);
			index_black++;
			bb_b_queens &= bb_b_queens - 1;
		}
		
		//White rooks
		while (bb_w_rooks != 0) {
			input.white_pieces[index_white] 	= convertPiece(ChessConstants.ROOK, ChessConstants.WHITE);
			input.white_squares[index_white] 	= getSquareID(bb_w_rooks);
			index_white++;
			bb_w_rooks &= bb_w_rooks - 1;
		}
		
		//Black rooks
		while (bb_b_rooks != 0) {
			input.black_pieces[index_black] 	= convertPiece(ChessConstants.ROOK, ChessConstants.BLACK);
			input.black_squares[index_black] 	= getSquareID(bb_b_rooks);
			index_black++;
			bb_b_rooks &= bb_b_rooks - 1;
		}
		
		//White bishops
		while (bb_w_bishops != 0) {
			input.white_pieces[index_white] 	= convertPiece(ChessConstants.BISHOP, ChessConstants.WHITE);
			input.white_squares[index_white] 	= getSquareID(bb_w_bishops);
			index_white++;
			bb_w_bishops &= bb_w_bishops - 1;
		}
		
		//Black bishops
		while (bb_b_bishops != 0) {
			input.black_pieces[index_black] 	= convertPiece(ChessConstants.BISHOP, ChessConstants.BLACK);
			input.black_squares[index_black] 	= getSquareID(bb_b_bishops);
			index_black++;
			bb_b_bishops &= bb_b_bishops - 1;
		}
		
		//White knights
		while (bb_w_knights != 0) {
			input.white_pieces[index_white] 	= convertPiece(ChessConstants.NIGHT, ChessConstants.WHITE);
			input.white_squares[index_white] 	= getSquareID(bb_w_knights);
			index_white++;
			bb_w_knights &= bb_w_knights - 1;
		}
		
		//Black knights
		while (bb_b_knights != 0) {
			input.black_pieces[index_black] 	= convertPiece(ChessConstants.NIGHT, ChessConstants.BLACK);
			input.black_squares[index_black] 	= getSquareID(bb_b_knights);
			index_black++;
			bb_b_knights &= bb_b_knights - 1;
		}
		
		//White pawns
		while (bb_w_pawns != 0) {
			input.white_pieces[index_white] 	= convertPiece(ChessConstants.PAWN, ChessConstants.WHITE);
			input.white_squares[index_white] 	= getSquareID(bb_w_pawns);
			index_white++;
			bb_w_pawns &= bb_w_pawns - 1;
		}
		
		//Black pawns
		while (bb_b_pawns != 0) {
			input.black_pieces[index_black] 	= convertPiece(ChessConstants.PAWN, ChessConstants.BLACK);
			input.black_squares[index_black] 	= getSquareID(bb_b_pawns);
			index_black++;
			bb_b_pawns &= bb_b_pawns - 1;
		}
		
		input.white_pieces[index_white] 	= -1;
		input.black_pieces[index_black] 	= -1;
	}
	
	private static int getSquareID(long bitboard) {
		
		int result =  Long.numberOfTrailingZeros(bitboard);
		
		result = SQUARE_MAPPING[result];
		
		return result;
	}
	
	public static int convertColor(int color) {
		return color == ChessConstants.WHITE ? 0 : 1;
	}
	
	public static int convertPiece(int pieceType, int color) {
		switch(pieceType) {
			case Constants.TYPE_PAWN: return color == ChessConstants.WHITE ? 0 : 0;
			case Constants.TYPE_KNIGHT: return color == ChessConstants.WHITE ? 1 : 1;
			case Constants.TYPE_BISHOP: return color == ChessConstants.WHITE ? 2 : 2;
			case Constants.TYPE_ROOK: return color == ChessConstants.WHITE ? 3 : 3;
			case Constants.TYPE_QUEEN: return color == ChessConstants.WHITE ? 4 : 4;
			case Constants.TYPE_KING: return color == ChessConstants.WHITE ? 5 : 5;
			default: throw new IllegalStateException();
		}
	}
	
	public static int convertSquare(int squareID) {
		return getSquareID(Util.POWER_LOOKUP[squareID]);
	}
	
	public static class Input {
		
		public int white_king_sq;
		public int black_king_sq;
		public int[] white_pieces = new int[17];
		public int[] white_squares = new int[17];
		public int[] black_pieces = new int[17];
		public int[] black_squares = new int[17];
	}
}
