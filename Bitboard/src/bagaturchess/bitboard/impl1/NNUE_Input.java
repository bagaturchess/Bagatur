package bagaturchess.bitboard.impl1;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;


public class NNUE_Input implements MoveListener {
	
	
	public static final int INPUT_SIZE 				= 12 * 64;
	
	public static final int SHIFT_KING 				= 0 * 64;
	public static final int SHIFT_PAWNS 			= 1 * 64;
	public static final int SHIFT_KNIGHTS 			= 2 * 64;
	public static final int SHIFT_BISHOP 			= 3 * 64;
	public static final int SHIFT_ROOK 				= 4 * 64;
	public static final int SHIFT_QUEEN 			= 5 * 64;

	private static final boolean CHECK_CONSISTENCY 	= true;
	
	
	private float[] inputs;
	
	private IBitBoard board;
	
	
	public NNUE_Input(IBitBoard _board) {
		
		inputs = new float[INPUT_SIZE];
		
		board = _board;
	}
	
	
	public final float[] getInputs() {
		
		return inputs;
	}
	
	
	@Override
	public final void preForwardMove(int color, int move) {
		move(move, color);
	}
	
	
	@Override
	public final void postForwardMove(int color, int move) {
		//Do nothing
	}
	
	
	@Override
	public final void preBackwardMove(int color, int move) {
		//Do nothing
	}
	
	@Override
	public final void postBackwardMove(int color, int move) {
		unmove(move, color);
	}
	
	
	@Override
	public final void addPiece_Special(int color, int type) {
		//Do nothing
	}
	
	
	@Override
	public final void initially_addPiece(int color, int type, long bb_pieces) {
		
        while (bb_pieces != 0) {
        	
        	int square_id = Long.numberOfTrailingZeros(bb_pieces);
        	
        	inputs[getInputIndex(color, type, square_id)] 		= 1;
        	
        	bb_pieces &= bb_pieces - 1;
        }
	}
	
	
	public final void move(int move, int color) {
		
		int pieceType = board.getMoveOps().getFigureType(move);
		int fromFieldID = board.getMoveOps().getFromFieldID(move);
		int toFieldID = board.getMoveOps().getToFieldID(move);
		
		if (Properties.DUMP_CASTLING) System.out.println("NNUE_Input.MOVE=" + board.getMoveOps().moveToString(move));
		
		
		if (board.getMoveOps().isCastling(move) && fromFieldID == toFieldID) {
			
			//Do nothing
			
		} else {
			
			setInputAt(color, pieceType, fromFieldID, 0);
			
			if (!board.getMoveOps().isPromotion(move)) {
				
				setInputAt(color, pieceType, toFieldID, 1);
			}
		}
		
		
		if (board.getMoveOps().isEnpassant(move)) {
			
			int ep_index = board.getEnpassantSquareID();
			int captured_pawn_index = ep_index + (((1 - color) == 0) ? 8 : -8);
			
			setInputAt(1 - color, Constants.TYPE_PAWN, captured_pawn_index, 0);
		
		} else if (board.getMoveOps().isCastling(move)) {
			
			if (Properties.DUMP_CASTLING) System.out.println("NNUE_Input.MOVE.Castling=" + board.getMoveOps().moveToString(move) + ", toFieldID=" + toFieldID);
			
			switch (toFieldID) {
				
				case CastlingConfig.G1:
					
					//White king side
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_kingside_w, 0);
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.F1, 1);
					break;
				
				case CastlingConfig.C1:
					
					//White queen side
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_queenside_w, 0);
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.D1, 1);
					break;
					
				case CastlingConfig.G8:
					//Black king side
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_kingside_b, 0);
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.F8, 1);
					break;
					
				case CastlingConfig.C8:
					//Black queen side
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_queenside_b, 0);
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.D8, 1);
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


	public final void unmove(int move, int color) {

		int pieceType = board.getMoveOps().getFigureType(move);
		int fromFieldID = board.getMoveOps().getFromFieldID(move);
		int toFieldID = board.getMoveOps().getToFieldID(move);
		
		if (Properties.DUMP_CASTLING) System.out.println("NNUE_Input.UNMOVE=" + board.getMoveOps().moveToString(move));
		
		
		if (board.getMoveOps().isCastling(move) && fromFieldID == toFieldID) {
			
			//Do nothing
			
		} else {
			
			setInputAt(color, pieceType, fromFieldID, 1);
			
			if (!board.getMoveOps().isPromotion(move)) {
				
				setInputAt(color, pieceType, toFieldID, 0);
			}
		}
		
		
		if (board.getMoveOps().isEnpassant(move)) {
			
			int ep_index = board.getEnpassantSquareID();
			int captured_pawn_index = ep_index + (((1 - color) == 0) ? 8 : -8);
			
			setInputAt(1 - color, Constants.TYPE_PAWN, captured_pawn_index, 1);
			
		} else if (board.getMoveOps().isCastling(move)) {
			
			if (Properties.DUMP_CASTLING) System.out.println("NNUE_Input.UNMOVE.Castling=" + board.getMoveOps().moveToString(move) + ", toFieldID=" + toFieldID);
			
			switch (toFieldID) {
			
				case CastlingConfig.G1:
					
					//White king side
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.F1, 0);
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_kingside_w, 1);
					break;
				
				case CastlingConfig.C1:
					
					//White queen side
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.D1, 0);
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_queenside_w, 1);
					break;
					
				case CastlingConfig.G8:
					
					//Black king side
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.F8, 0);
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_kingside_b, 1);
					break;
					
				case CastlingConfig.C8:
					
					//Black queen side
					setInputAt(color, Constants.TYPE_ROOK, CastlingConfig.D8, 0);
					setInputAt(color, Constants.TYPE_ROOK, board.getCastlingConfig().from_SquareID_rook_queenside_b, 1);
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
	
	
	public static final void printWeights(Double[] nnue_weights) {
		
		System.out.println("nnue_weights=" + nnue_weights.length);
		
		for (int color = 0; color < 2; color++) {
			
			for (int piece_type = PAWN; piece_type <= KING; piece_type++) {
				
				System.out.println("******************************************************************************************************************************************");
				System.out.println("COLOR: " + color + ", TYPE: " + piece_type);
				
				VarStatistic stats = new VarStatistic();
				
				for (int rank = 7; rank >= 0; rank--) {
					
					String board_line = "";
					
					for (int file = 0; file < 8; file++) {
						
						int square_id = 8 * rank + file;
						
						int nnue_index = NNUE_Input.getInputIndex(color, piece_type, square_id);
						
						double nnue_weight = nnue_weights[nnue_index];
						
						stats.addValue(nnue_weight);
								
						board_line += nnue_weight + ", ";
					}
					
					System.out.println(board_line);
				}
				
				System.out.println("STATS: " + stats);
				System.out.println("******************************************************************************************************************************************");
			}
		}
		
		System.exit(0);
	}
}
