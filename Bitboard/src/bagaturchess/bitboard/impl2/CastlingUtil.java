package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;


public final class CastlingUtil {
	
	
	//castling_rights is 4 bits: white-king, white-queen, black-king, black-queen
	public static long getCastlingIndexes(int colorToMove, int castlingRights, final CastlingConfig castlingConfig) {
		
		if (castlingRights == 0) {
			
			return 0;
		}
		
		if (colorToMove == ChessConstants.WHITE) {
			
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
					throw new IllegalStateException("castling_rights=" + castlingRights);
			}
			
		} else if (colorToMove == ChessConstants.BLACK) {
			
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
					throw new IllegalStateException("castling_rights=" + castlingRights);
			}
			
		} else {
			
			throw new IllegalStateException("colorToMove=" + colorToMove);
		}
	}
	
	
	public static int getRookMovedOrAttackedCastlingRights(final int castlingRights, final int rook_square_id, final CastlingConfig castlingConfig) {
		
		if (rook_square_id == castlingConfig.from_SquareID_rook_kingside_w) {
			
			return castlingRights & 7; // 0111
			
		} else if (rook_square_id == castlingConfig.from_SquareID_rook_queenside_w) {
			
			return castlingRights & 11; // 1011
			
		} else if (rook_square_id == castlingConfig.from_SquareID_rook_kingside_b) {
			
			return castlingRights & 13; // 1101
			
		} else if (rook_square_id == castlingConfig.from_SquareID_rook_queenside_b) {
			
			return castlingRights & 14; // 1110
		}
		
		return castlingRights;
	}
	
	
	public static int getKingMovedCastlingRights(final int castlingRights, final int color, final CastlingConfig castlingConfig) {
		
		if (color == ChessConstants.WHITE) {
			
			return castlingRights & 3; // 0011
			
		} else if (color == ChessConstants.BLACK) {
			
			return castlingRights & 12; // 1100
			
		} else {
			
			throw new RuntimeException("Incorrect color: " + color);
		}
	}
	
	
	public static boolean isValidCastlingMove(final ChessBoard cb, final int fromIndex, final int toIndex) {
		
		
		if (cb.checking_pieces != 0) {
			
			return false;
		}
		
		
		long bb_RookInBetween;
		long bb_KingInBetween;
		
		//Create bitboard without the king and the rook, which are castling
		long bb_all_pieces_no_king_no_rook;
		
		if (toIndex == CastlingConfig.G1) {
			
			bb_RookInBetween = cb.castling_config.bb_inbetween_rook_kingside_w;
			bb_KingInBetween = cb.castling_config.bb_inbetween_king_kingside_w;
			
			bb_all_pieces_no_king_no_rook = cb.all_pieces & ~(ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_w] | ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_kingside_w]);
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_w] & cb.getPieces(ChessConstants.WHITE, ChessConstants.KING)) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_kingside_w] & cb.getPieces(ChessConstants.WHITE, ChessConstants.ROOK)) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.C1) {
			
			bb_RookInBetween = cb.castling_config.bb_inbetween_rook_queenside_w;
			bb_KingInBetween = cb.castling_config.bb_inbetween_king_queenside_w;
			
			bb_all_pieces_no_king_no_rook = cb.all_pieces & ~(ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_w] | ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_queenside_w]);
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_w] & cb.getPieces(ChessConstants.WHITE, ChessConstants.KING)) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_queenside_w] & cb.getPieces(ChessConstants.WHITE, ChessConstants.ROOK)) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.G8) {
			
			bb_RookInBetween = cb.castling_config.bb_inbetween_rook_kingside_b;
			bb_KingInBetween = cb.castling_config.bb_inbetween_king_kingside_b;
			
			bb_all_pieces_no_king_no_rook = cb.all_pieces & ~(ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_b] | ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_kingside_b]);
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_b] & cb.getPieces(ChessConstants.BLACK, ChessConstants.KING)) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_kingside_b] & cb.getPieces(ChessConstants.BLACK, ChessConstants.ROOK)) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else if (toIndex == CastlingConfig.C8) {
			
			bb_RookInBetween = cb.castling_config.bb_inbetween_rook_queenside_b;
			bb_KingInBetween = cb.castling_config.bb_inbetween_king_queenside_b;
			
			bb_all_pieces_no_king_no_rook = cb.all_pieces & ~(ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_b] | ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_queenside_b]);
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_king_b] & cb.getPieces(ChessConstants.BLACK, ChessConstants.KING)) == 0L) {
				
				throw new IllegalStateException();
			}
			
			if ((ChessConstants.POWER_LOOKUP[cb.castling_config.from_SquareID_rook_queenside_b] & cb.getPieces(ChessConstants.BLACK, ChessConstants.ROOK)) == 0L) {
				
				throw new IllegalStateException();
			}
			
		} else {
			
			throw new RuntimeException("Incorrect castling-index: " + toIndex);
		}
		
		if ((bb_all_pieces_no_king_no_rook & bb_KingInBetween) != 0
			|| (bb_all_pieces_no_king_no_rook & bb_RookInBetween) != 0) {
			
			return false;
		}
		
		
		int king_color = (ChessConstants.POWER_LOOKUP[fromIndex] & cb.getPieces(ChessConstants.WHITE, ChessConstants.KING)) != 0L ? ChessConstants.WHITE : ChessConstants.BLACK;
		
		//Finally, check if the KingInBetween squares are attacked by the opponent
		while (bb_KingInBetween != 0) {
			
			int intermediate_square_id = Long.numberOfTrailingZeros(bb_KingInBetween);
			
			// king does not move through a checked position?
			if (isInCheckIncludingKing(
						intermediate_square_id,
						king_color,
						cb,
						bb_all_pieces_no_king_no_rook
						//cb.allPieces
						//We need to exclude the rook, because in Chess960 the king could go to attacked square, which is covered by the rook.
						//Example: last move is white makes queen side castling and goes in check 1rn2r2/1pnkb3/2ppp3/p4p2/2P2P2/1P1BN3/3PPBP1/q1KR3R w - - 0 22
					)
				) {
				
				return false;
			}
			
			bb_KingInBetween &= bb_KingInBetween - 1;
		}

		return true;
	}
	
	
	private static boolean isInCheckIncludingKing(final int kingIndex, final int colorToMove, final ChessBoard cb, final long allPieces) {
		
		// put 'super-piece' in kings position
		return (cb.getPieces(1 - colorToMove, ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[kingIndex]
				| (cb.getPieces(1 - colorToMove, ChessConstants.ROOK) | cb.getPieces(1 - colorToMove, ChessConstants.QUEEN)) & MagicUtil.getRookMoves(kingIndex, allPieces)
				| (cb.getPieces(1 - colorToMove, ChessConstants.BISHOP) | cb.getPieces(1 - colorToMove, ChessConstants.QUEEN)) & MagicUtil.getBishopMoves(kingIndex, allPieces) 
				| cb.getPieces(1 - colorToMove, ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[colorToMove][kingIndex]
				| cb.getPieces(1 - colorToMove, ChessConstants.KING) & ChessConstants.KING_MOVES[kingIndex]
			) != 0;
	}
	
	public static final void getRookFromToSquareIDs(final ChessBoard cb, final int kingToIndex, int[] result) {
		
		int from;
		int to;
		
		switch (kingToIndex) {
		
			case CastlingConfig.G1:
				
				// White king side
				
				from 	= cb.castling_config.from_SquareID_rook_kingside_w;
				to 		= CastlingConfig.F1;
				
				break;
			
			case CastlingConfig.C1:
				
				// White queen side
				
				from 	= cb.castling_config.from_SquareID_rook_queenside_w;
				to 		= CastlingConfig.D1;
				
				break;
				
			case CastlingConfig.G8:
				
				// Black king side
				
				from 	= cb.castling_config.from_SquareID_rook_kingside_b;
				to 		= CastlingConfig.F8;
				
				break;
				
			case CastlingConfig.C8:
				
				// Black queen side
				
				from 	= cb.castling_config.from_SquareID_rook_queenside_b;
				to 		= CastlingConfig.D8;
				
				break;
				
			default:
				
				throw new RuntimeException("Incorrect king castling to-index: " + kingToIndex);
		}
		
		result[0] = from;
		result[1] = to;
	}
}
