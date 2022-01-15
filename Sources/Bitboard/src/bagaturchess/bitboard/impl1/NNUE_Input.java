package bagaturchess.bitboard.impl1;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;


public class NNUE_Input implements MoveListener {
	
	
	public static final int INPUT_SIZE 		= 12 * 64;
	
	public static final int SHIFT_KING 		= 0 * 64;
	public static final int SHIFT_PAWNS 	= 1 * 64;
	public static final int SHIFT_KNIGHTS 	= 2 * 64;
	public static final int SHIFT_BISHOP 	= 3 * 64;
	public static final int SHIFT_ROOK 		= 4 * 64;
	public static final int SHIFT_QUEEN 	= 5 * 64;

	private static final boolean CHECK_CONSISTENCY = true;
	
	
	private float[] inputs;
	
	private IBitBoard board;
	
	
	public NNUE_Input(IBitBoard _board) {
		
		inputs = new float[INPUT_SIZE];
		
		board = _board;
	}
	
	
	public float[] getInputs() {
		
		return inputs;
	}
	
	
	@Override
	public void preForwardMove(int color, int move) {
		move(move, color);
	}
	
	
	@Override
	public void postForwardMove(int color, int move) {
		//Do nothing
	}
	
	
	@Override
	public void preBackwardMove(int color, int move) {
		//Do nothing
	}
	
	@Override
	public void postBackwardMove(int color, int move) {
		unmove(move, color);
	}
	
	
	@Override
	public void addPiece_Special(int color, int type) {
		//Do nothing
	}
	
	
	@Override
	public void initially_addPiece(int color, int type, long bb_pieces) {
		
        while (bb_pieces != 0) {
        	
        	int square_id = Long.numberOfTrailingZeros(bb_pieces);
        	
        	inputs[getInputIndex(color, type, square_id)] 		= 1;
        	
        	bb_pieces &= bb_pieces - 1;
        }
	}
	
	
	public void move(int move, int color) {
		
		int pieceType = board.getMoveOps().getFigureType(move);
		int fromFieldID = board.getMoveOps().getFromFieldID(move);
		int toFieldID = board.getMoveOps().getToFieldID(move);
		
		setInputAt(color, pieceType, fromFieldID, 0);
		if (!board.getMoveOps().isPromotion(move)) setInputAt(color, pieceType, toFieldID, 1);
		
		
		if (board.getMoveOps().isEnpassant(move)) {
			
			int ep_index = board.getEnpassantSquareID();
			int captured_pawn_index = ep_index + (((1 - color) == 0) ? 8 : -8);
			
			setInputAt(1 - color, Constants.TYPE_PAWN, captured_pawn_index, 0);
		
		} else if (board.getMoveOps().isCastling(move)) {
				
			switch (toFieldID) {
				
				case 1:
					// white rook from 0 to 2
					setInputAt(color, Constants.TYPE_ROOK, 0, 0);
					setInputAt(color, Constants.TYPE_ROOK, 2, 1);
					break;
					
				case 57:
					// black rook from 56 to 58
					setInputAt(color, Constants.TYPE_ROOK, 56, 0);
					setInputAt(color, Constants.TYPE_ROOK, 58, 1);
					break;
					
				case 5:
					// white rook from 7 to 4
					setInputAt(color, Constants.TYPE_ROOK, 7, 0);
					setInputAt(color, Constants.TYPE_ROOK, 4, 1);
					break;
					
				case 61:
					// black rook from 63 to 60
					setInputAt(color, Constants.TYPE_ROOK, 63, 0);
					setInputAt(color, Constants.TYPE_ROOK, 60, 1);
					break;
					
				default:
					throw new RuntimeException("Incorrect king index: " + toFieldID);
			}
			
		} else {
			
			if (board.getMoveOps().isCapture(move)) {
				
				int capType = board.getMoveOps().getCapturedFigureType(move);
				setInputAt(1 - color, capType, toFieldID, 0);
			}
			
			if (board.getMoveOps().isPromotion(move)) {
				
				int promType = board.getMoveOps().getPromotionFigureType(move);
				setInputAt(color, promType, toFieldID, 1);
			}
		}
	}


	public void unmove(int move, int color) {

		int pieceType = board.getMoveOps().getFigureType(move);
		int fromFieldID = board.getMoveOps().getFromFieldID(move);
		int toFieldID = board.getMoveOps().getToFieldID(move);
		
		setInputAt(color, pieceType, fromFieldID, 1);
		if (!board.getMoveOps().isPromotion(move)) setInputAt(color, pieceType, toFieldID, 0);
		
		
		if (board.getMoveOps().isEnpassant(move)) {
			
			int ep_index = board.getEnpassantSquareID();
			int captured_pawn_index = ep_index + (((1 - color) == 0) ? 8 : -8);
			
			setInputAt(1 - color, Constants.TYPE_PAWN, captured_pawn_index, 1);
			
		} else if (board.getMoveOps().isCastling(move)) {
				
			switch (toFieldID) {
			
				case 1:
					// white rook from 0 to 2
					setInputAt(color, Constants.TYPE_ROOK, 0, 1);
					setInputAt(color, Constants.TYPE_ROOK, 2, 0);
					break;
					
				case 57:
					// black rook from 56 to 58
					setInputAt(color, Constants.TYPE_ROOK, 56, 1);
					setInputAt(color, Constants.TYPE_ROOK, 58, 0);
					break;
					
				case 5:
					// white rook from 7 to 4
					setInputAt(color, Constants.TYPE_ROOK, 7, 1);
					setInputAt(color, Constants.TYPE_ROOK, 4, 0);
					break;
					
				case 61:
					// black rook from 60 to 63
					setInputAt(color, Constants.TYPE_ROOK, 63, 1);
					setInputAt(color, Constants.TYPE_ROOK, 60, 0);
					break;
					
				default:
					throw new RuntimeException("Incorrect king castling to-index: " + toFieldID);
			}
			
		} else {
			
			if (board.getMoveOps().isCapture(move)) {
				
				int capType = board.getMoveOps().getCapturedFigureType(move);
				setInputAt(1 - color, capType, toFieldID, 1);
			}
			
			if (board.getMoveOps().isPromotion(move)) {
				
				int promType = board.getMoveOps().getPromotionFigureType(move);
				setInputAt(color, promType, toFieldID, 0);
			}
		}
	}
	
	
	public static final int getInputIndex(int color, int type, int square_id) {
		
		int index = (color == Constants.COLOUR_WHITE) ? 0 : INPUT_SIZE / 2;
		
		switch (type) {
		
			case Constants.TYPE_PAWN:
				return index + SHIFT_PAWNS + square_id;
				
			case Constants.TYPE_KNIGHT:
				return index + SHIFT_KNIGHTS + square_id;
				
			case Constants.TYPE_BISHOP:
				return index + SHIFT_BISHOP + square_id;
				
			case Constants.TYPE_ROOK:
				return index + SHIFT_ROOK + square_id;
				
			case Constants.TYPE_QUEEN:
				return index + SHIFT_QUEEN + square_id;
				
			case Constants.TYPE_KING:
				return index + SHIFT_KING + square_id;
				
			default:
				throw new IllegalStateException("type=" + type);
		}
	}
	
	
	private final void setInputAt(int color, int piece_type, int square_id, float signal) {
		
		int index = getInputIndex(color, piece_type, square_id);
		
		if (CHECK_CONSISTENCY) {
			
			if (signal == 0) {
				
				if (inputs[index] != 1) {
					
					throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
				}
				
			} else if (signal == 1) {
				
				if (inputs[index] != 0) {
					
					throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
				}
				
			} else {
				
				throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
			}
		}
		
		inputs[index] = signal;
	}
}
