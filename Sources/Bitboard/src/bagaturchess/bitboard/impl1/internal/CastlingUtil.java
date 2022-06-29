package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.EMPTY;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;

import bagaturchess.bitboard.common.Properties;


public final class CastlingUtil {
	
	
	//castlingRights is 4 bits: white-king, white-queen, black-king, black-queen
	public static long getCastlingIndexes(int colorToMove, int castlingRights, final CastlingConfig castlingConfig) {
		
		if (castlingRights == 0) {
			
			return 0;
		}
		
		if (colorToMove == WHITE) {
			
			switch (castlingRights) {
			
				case 0:
				case 1:
				case 2:
				case 3:
					return 0;
					
				case 4:
				case 5:
				case 6:
				case 7:
					return Bitboard.C1;
					
				case 8:
				case 9:
				case 10:
				case 11:
					return Bitboard.G1;
					
				case 12:
				case 13:
				case 14:
				case 15:
					return Bitboard.C1_G1;
					
				default:
					throw new IllegalStateException("castlingRights=" + castlingRights);
			}
			
		} else if (colorToMove == BLACK) {
			
			switch (castlingRights) {
			
				case 0:
				case 4:
				case 8:
				case 12:
					return 0;
					
				case 1:
				case 5:
				case 9:
				case 13:
					return Bitboard.C8;
					
				case 2:
				case 6:
				case 10:
				case 14:
					return Bitboard.G8;
					
				case 3:
				case 7:
				case 11:
				case 15:
					return Bitboard.C8_G8;
					
				default:
					throw new IllegalStateException("castlingRights=" + castlingRights);
			}
			
		} else {
			
			throw new IllegalStateException("colorToMove=" + colorToMove);
		}
	}
	
	
	public static int getRookMovedOrAttackedCastlingRights(final int castlingRights, final int rookFromIndex, final CastlingConfig castlingConfig) {
		
		if (rookFromIndex == castlingConfig.from_SquareID_rook_kingside_w) {
			
			return castlingRights & 7; // 0111
			
		} else if (rookFromIndex == castlingConfig.from_SquareID_rook_queenside_w) {
			
			return castlingRights & 11; // 1011
			
		} else if (rookFromIndex == castlingConfig.from_SquareID_rook_kingside_b) {
			
			return castlingRights & 13; // 1101
			
		} else if (rookFromIndex == castlingConfig.from_SquareID_rook_queenside_b) {
			
			return castlingRights & 14; // 1110
		}
		
		return castlingRights;
	}
	
	
	/*public static int getKingMovedCastlingRights(final int castlingRights, final int kingFromIndex, final CastlingConfig castlingConfig) {
		
		if (kingFromIndex == castlingConfig.from_SquareID_king_w) {
			
			return castlingRights & 3; // 0011
			
		} else if (kingFromIndex == castlingConfig.from_SquareID_king_b) {
			
			return castlingRights & 12; // 1100
			
		} else {
			
			throw new RuntimeException("Incorrect kingFromIndex: " + kingFromIndex);
			//return castlingRights;
		}
	}
	*/
	
	
	public static int getKingMovedCastlingRights(final int castlingRights, final int color, final CastlingConfig castlingConfig) {
		
		if (color == WHITE) {
			
			return castlingRights & 3; // 0011
			
		} else if (color == BLACK) {
			
			return castlingRights & 12; // 1100
			
		} else {
			
			throw new RuntimeException("Incorrect color: " + color);
		}
	}
	
	
	public static boolean isValidCastlingMove(final ChessBoard cb, final int fromIndex, final int toIndex) {
		
		
		if (cb.checkingPieces != 0) {
			
			return false;
		}
		
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.isValidCastlingMove: fromIndex=" + fromIndex + ", toIndex=" + toIndex);
		
		
		long bb_RookInBetween;
		long bb_KingInBetween;
		
		//Create bitboards without king and rook
		long bb_all_pieces_no_king;
		long bb_all_pieces_no_rook;
		
		if (toIndex == CastlingConfig.G1) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_w;
			bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_kingside_w;
			
			bb_all_pieces_no_king = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w]);
			bb_all_pieces_no_rook = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_w]);
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] & cb.pieces[WHITE][KING]) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_w] & cb.pieces[WHITE][ROOK]) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.C1) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_w;
			bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_queenside_w;
			
			bb_all_pieces_no_king = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w]);
			bb_all_pieces_no_rook = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_w]);
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] & cb.pieces[WHITE][KING]) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_w] & cb.pieces[WHITE][ROOK]) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.G8) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_b;
			bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_kingside_b;
			
			bb_all_pieces_no_king = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b]);
			bb_all_pieces_no_rook = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_b]);
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] & cb.pieces[BLACK][KING]) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_b] & cb.pieces[BLACK][ROOK]) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.C8) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_b;
			bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_queenside_b;
			
			bb_all_pieces_no_king = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b]);
			bb_all_pieces_no_rook = cb.allPieces & ~(Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_b]);
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] & cb.pieces[BLACK][KING]) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_b] & cb.pieces[BLACK][ROOK]) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else {
			
			throw new RuntimeException("Incorrect castling-index: " + toIndex);
		}
		
		if ((bb_all_pieces_no_rook & bb_KingInBetween) != 0
			|| (bb_all_pieces_no_king & bb_RookInBetween) != 0) {
			
			return false;
		}
		
		
		//Finally, check if the KingInBetween squares are attacked by the opponent
		while (bb_KingInBetween != 0) {
			
			int intermediate_square_id = Long.numberOfTrailingZeros(bb_KingInBetween);
			
			// king does not move through a checked position?
			if (CheckUtil.isInCheckIncludingKing(
						intermediate_square_id,
						cb.colorToMove,
						cb.pieces[cb.colorToMoveInverse],
						cb.allPieces,
						MaterialUtil.getMajorPieces(cb.materialKey, cb.colorToMoveInverse)
					)
				) {
				
				return false;
			}
			
			bb_KingInBetween &= bb_KingInBetween - 1;
		}

		return true;
	}
	
	
	public static void castleRookUpdateKeyAndPsqt(final ChessBoard cb, final int kingToIndex) {
		
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.castleRookUpdateKeyAndPsqt: kingToIndex=" + kingToIndex);
		
		
		int[] rook_from_to 			= getRookFromToSquareIDs(cb, kingToIndex);
		
		int from 					= rook_from_to[0];
		
		int to 						= rook_from_to[1];
		
		
		int colour 					= cb.colorToMove;
		
		
		long bb 					= Util.POWER_LOOKUP[from] | Util.POWER_LOOKUP[to];
		
		cb.pieces[colour][ROOK] 	^= bb;
		cb.friendlyPieces[colour] 	^= bb;
		
		cb.pieceIndexes[from] 		= EMPTY;
		cb.pieceIndexes[to] 		= ROOK;
		
		cb.zobristKey 				^= Zobrist.piece[from][colour][ROOK] ^ Zobrist.piece[to][colour][ROOK];
		
		cb.psqtScore_mg				+= EvalConstants.PSQT_MG[ROOK][colour][to] - EvalConstants.PSQT_MG[ROOK][colour][from];
		cb.psqtScore_eg 			+= EvalConstants.PSQT_EG[ROOK][colour][to] - EvalConstants.PSQT_EG[ROOK][colour][from];
	}
	
	
	public static void uncastleRookUpdatePsqt(final ChessBoard cb, final int kingToIndex) {
		
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.uncastleRookUpdatePsqt: kingToIndex=" + kingToIndex);
		
		
		int[] rook_from_to 			= getRookFromToSquareIDs(cb, kingToIndex);
		
		int from 					= rook_from_to[0];
		
		int to 						= rook_from_to[1];
		
		
		int colour 					= cb.colorToMoveInverse;
		
		
		long bb 					= Util.POWER_LOOKUP[from] | Util.POWER_LOOKUP[to];
		
		cb.pieces[colour][ROOK] 	^= bb;
		cb.friendlyPieces[colour] 	^= bb;
		
		cb.pieceIndexes[from] 		= ROOK;
		cb.pieceIndexes[to] 		= EMPTY;
						
		cb.psqtScore_mg 			+= EvalConstants.PSQT_MG[ROOK][colour][from] - EvalConstants.PSQT_MG[ROOK][colour][to];
		cb.psqtScore_eg 			+= EvalConstants.PSQT_EG[ROOK][colour][from] - EvalConstants.PSQT_EG[ROOK][colour][to];
	}
	
	
	private static final int[] getRookFromToSquareIDs(final ChessBoard cb, final int kingToIndex) {
		
		int from;
		int to;
		
		switch (kingToIndex) {
		
			case 1:
				
				// White king side
				
				from 	= cb.castlingConfig.from_SquareID_rook_kingside_w;
				to 		= CastlingConfig.F1;
				
				break;
			
			case 5:
				
				// White queen side
				
				from 	= cb.castlingConfig.from_SquareID_rook_queenside_w;
				to 		= CastlingConfig.D1;
				
				break;
				
			case 57:
				
				// Black king side
				
				from 	= cb.castlingConfig.from_SquareID_rook_kingside_b;
				to 		= CastlingConfig.F8;
				
				break;
				
			case 61:
				
				// Black queen side
				
				from 	= cb.castlingConfig.from_SquareID_rook_queenside_b;
				to 		= CastlingConfig.D8;
				
				break;
				
			default:
				
				throw new RuntimeException("Incorrect king castling to-index: " + kingToIndex);
		}
		
		return new int[] {from, to};
	}
}
