package bagaturchess.nnue;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.bitboard.api.IBitBoard;


public class NNUEProbeUtils {
	
	
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
	
	
	/**
	* Evaluation subroutine suitable for chess engines.
	* -------------------------------------------------
	* Piece codes are
	*     wking=1, wqueen=2, wrook=3, wbishop= 4, wknight= 5, wpawn= 6,
	*     bking=7, bqueen=8, brook=9, bbishop=10, bknight=11, bpawn=12,
	* Squares are
	*     A1=0, B1=1 ... H8=63
	* Input format:
	*     piece[0] is white king, square[0] is its location
	*     piece[1] is black king, square[1] is its location
	*     ..
	*     piece[x], square[x] can be in any order
	*     ..
	*     piece[n+1] is set to 0 to represent end of array
	* Returns
	*   Score relative to side to move in approximate centi-pawns
	*/
	public static final void fillInput(IBitBoard bitboard, Input input) {
		
		ChessBoard cb = ((BoardImpl)bitboard).getChessBoard();
		
		input.color = cb.colorToMove == ChessConstants.WHITE ? 0 : 1;
		
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
		
		int index 			= 0;
		int square_type 	= 1;
		
		//White king
		input.pieces[index] 	= square_type;
		input.squares[index] 	= getSquareID(bb_w_king);
		index++;
		
		//Black king
		input.pieces[index] 	= square_type + 6;
		input.squares[index] 	= getSquareID(bb_b_king);
		index++;		
		
		square_type++;
		
		//White queens
		while (bb_w_queens != 0) {
			input.pieces[index] 	= square_type;
			input.squares[index] 	= getSquareID(bb_w_queens);
			index++;
			bb_w_queens &= bb_w_queens - 1;
		}
		
		//Black queens
		while (bb_b_queens != 0) {
			input.pieces[index] 	= square_type + 6;
			input.squares[index] 	= getSquareID(bb_b_queens);
			index++;
			bb_b_queens &= bb_b_queens - 1;
		}
		
		square_type++;
		
		//White rooks
		while (bb_w_rooks != 0) {
			input.pieces[index] 	= square_type;
			input.squares[index] 	= getSquareID(bb_w_rooks);
			index++;
			bb_w_rooks &= bb_w_rooks - 1;
		}
		
		//Black rooks
		while (bb_b_rooks != 0) {
			input.pieces[index] 	= square_type + 6;
			input.squares[index] 	= getSquareID(bb_b_rooks);
			index++;
			bb_b_rooks &= bb_b_rooks - 1;
		}
		
		square_type++;
		
		//White bishops
		while (bb_w_bishops != 0) {
			input.pieces[index] 	= square_type;
			input.squares[index] 	= getSquareID(bb_w_bishops);
			index++;
			bb_w_bishops &= bb_w_bishops - 1;
		}
		
		//Black bishops
		while (bb_b_bishops != 0) {
			input.pieces[index] 	= square_type + 6;
			input.squares[index] 	= getSquareID(bb_b_bishops);
			index++;
			bb_b_bishops &= bb_b_bishops - 1;
		}
		
		square_type++;
		
		//White knights
		while (bb_w_knights != 0) {
			input.pieces[index] 	= square_type;
			input.squares[index] 	= getSquareID(bb_w_knights);
			index++;
			bb_w_knights &= bb_w_knights - 1;
		}
		
		//Black knights
		while (bb_b_knights != 0) {
			input.pieces[index] 	= square_type + 6;
			input.squares[index] 	= getSquareID(bb_b_knights);
			index++;
			bb_b_knights &= bb_b_knights - 1;
		}
		
		square_type++;
		
		//White pawns
		while (bb_w_pawns != 0) {
			input.pieces[index] 	= square_type;
			input.squares[index] 	= getSquareID(bb_w_pawns);
			index++;
			bb_w_pawns &= bb_w_pawns - 1;
		}
		
		//Black pawns
		while (bb_b_pawns != 0) {
			input.pieces[index] 	= square_type + 6;
			input.squares[index] 	= getSquareID(bb_b_pawns);
			index++;
			bb_b_pawns &= bb_b_pawns - 1;
		}
		
		input.pieces[index] 	= 0;
		input.squares[index] 	= 0;
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
			case Constants.TYPE_PAWN: return color == ChessConstants.WHITE ? 6 : 12;
			case Constants.TYPE_KNIGHT: return color == ChessConstants.WHITE ? 5 : 11;
			case Constants.TYPE_BISHOP: return color == ChessConstants.WHITE ? 4 : 10;
			case Constants.TYPE_ROOK: return color == ChessConstants.WHITE ? 3 : 9;
			case Constants.TYPE_QUEEN: return color == ChessConstants.WHITE ? 2 : 8;
			case Constants.TYPE_KING: throw new IllegalStateException(); //return color == ChessConstants.WHITE ? 1 : 7;
			default: throw new IllegalStateException();
		}
	}
	
	public static int convertSquare(int squareID) {
		return getSquareID(Util.POWER_LOOKUP[squareID]);
	}
	
	public static class Input {
		
		public int color;
		public int[] pieces = new int[33];
		public int[] squares = new int[33];
	}
}
