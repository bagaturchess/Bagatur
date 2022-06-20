package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.EMPTY;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

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
		
		//throw new RuntimeException("Unknown castling-right: " + castlingRights);
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
	
	
	public static int getKingMovedCastlingRights(final int castlingRights, final int kingFromIndex, final CastlingConfig castlingConfig) {
		
		if (kingFromIndex == castlingConfig.from_SquareID_king_w) {
			
			return castlingRights & 3; // 0011
			
		} else if (kingFromIndex == castlingConfig.from_SquareID_king_b) {
			
			return castlingRights & 12; // 1100
		}
		
		return castlingRights;
	}
	
	
	public static boolean isValidCastlingMove(final ChessBoard cb, final int fromIndex, final int toIndex) {
		
		
		if (cb.checkingPieces != 0) {
			
			return false;
		}
		
		
		long bb_RookInBetween;
		
		if (toIndex == 1) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_w;
			
		} else if (toIndex == 5) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_w;
			
		} else if (toIndex == 57) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_b;
			
		} else if (toIndex == 61) {
			
			bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_b;
			
		} else {
			
			throw new RuntimeException("Incorrect castling-index: " + toIndex);
		}
		
		
		//TODO: Chess960 exclude king and rooks
		if ((cb.allPieces & bb_RookInBetween) != 0) {
			
			return false;
		}
		
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.isValidCastlingMove: fromIndex=" + fromIndex + ", toIndex=" + toIndex);
		
		long kingIndexes = ChessConstants.IN_BETWEEN[fromIndex][toIndex] | Util.POWER_LOOKUP[toIndex];
		
		while (kingIndexes != 0) {
			
			int intermediateIndex = Long.numberOfTrailingZeros(kingIndexes);
			
			// king does not move through a checked position?
			if (CheckUtil.isInCheckIncludingKing(
						intermediateIndex,
						cb.colorToMove,
						cb.pieces[cb.colorToMoveInverse],
						cb.allPieces,
						MaterialUtil.getMajorPieces(cb.materialKey, cb.colorToMoveInverse)
					)
				) {
				
				return false;
			}
			
			kingIndexes &= kingIndexes - 1;
		}

		return true;
	}
	
	
	public static void castleRookUpdateKeyAndPsqt(final ChessBoard cb, final int kingToIndex) {
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.castleRookUpdateKeyAndPsqt: kingToIndex=" + kingToIndex);
		
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
		
		
		int colour 							= cb.colorToMove;
		
		long bb 							= Util.POWER_LOOKUP[from] | Util.POWER_LOOKUP[to];
		
		cb.pieces[colour][ROOK] 	^= bb;
		cb.friendlyPieces[colour] 	^= bb;
		
		cb.pieceIndexes[from] 				= EMPTY;
		cb.pieceIndexes[to] 				= ROOK;
		
		cb.zobristKey 						^= Zobrist.piece[from][colour][ROOK] ^ Zobrist.piece[to][colour][ROOK];
		
		cb.psqtScore_mg						+= EvalConstants.PSQT_MG[ROOK][colour][to] - EvalConstants.PSQT_MG[ROOK][colour][from];
		cb.psqtScore_eg 					+= EvalConstants.PSQT_EG[ROOK][colour][to] - EvalConstants.PSQT_EG[ROOK][colour][from];
	}
	
	
	public static void uncastleRookUpdatePsqt(final ChessBoard cb, final int kingToIndex) {
		
		if (Properties.DUMP_CASTLING) System.out.println("CastlingUtil.uncastleRookUpdatePsqt: kingToIndex=" + kingToIndex);
		
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
		
		
		int colour 							= cb.colorToMoveInverse;
		
		long bb 							= Util.POWER_LOOKUP[from] | Util.POWER_LOOKUP[to];
		
		cb.pieces[colour][ROOK] 	^= bb;
		cb.friendlyPieces[colour] 	^= bb;
		
		cb.pieceIndexes[from] 				= ROOK;
		cb.pieceIndexes[to] 				= EMPTY;
						
		cb.psqtScore_mg 					+= EvalConstants.PSQT_MG[ROOK][colour][from] - EvalConstants.PSQT_MG[ROOK][colour][to];
		cb.psqtScore_eg 					+= EvalConstants.PSQT_EG[ROOK][colour][from] - EvalConstants.PSQT_EG[ROOK][colour][to];
	}
}
