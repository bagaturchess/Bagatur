package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import bagaturchess.bitboard.common.Properties;


public class MoveWrapper {

	public int fromRank;
	public char fromFile;

	/** 1 to 8 */
	public int toRank;

	/** a to h */
	public char toFile;

	public int fromIndex;
	public int toIndex;
	
	public int move;

	public int pieceIndex;
	public int pieceIndexAttacked;

	public boolean isNightPromotion = false;
	public boolean isQueenPromotion = false;
	public boolean isRookPromotion = false;
	public boolean isBishopPromotion = false;

	public boolean isEP = false;
	public boolean isCastling = false;

	public MoveWrapper(int move, boolean isFRC, CastlingConfig castling_cfg) {
		
		this.move = move;

		fromIndex = MoveUtil.getFromIndex(move);
		fromFile = (char) (104 - fromIndex % 8);
		fromRank = fromIndex / 8 + 1;

		toIndex = MoveUtil.getToIndex(move);
		toFile = (char) (104 - toIndex % 8);
		toRank = toIndex / 8 + 1;

		pieceIndex = MoveUtil.getSourcePieceIndex(move);
		pieceIndexAttacked = MoveUtil.getAttackedPieceIndex(move);

		switch (MoveUtil.getMoveType(move)) {
		
			case MoveUtil.TYPE_NORMAL:
				break;
				
			case MoveUtil.TYPE_CASTLING:
				
				isCastling = true;
				
				if (isFRC) {
					
					if (toIndex == CastlingConfig.G1) {
						
						toIndex = castling_cfg.from_SquareID_rook_kingside_w;
						
					} else if (toIndex == CastlingConfig.C1) {
						
						toIndex = castling_cfg.from_SquareID_rook_queenside_w;
						
					} else if (toIndex == CastlingConfig.G8) {
						
						toIndex = castling_cfg.from_SquareID_rook_kingside_b;
						
					} else if (toIndex == CastlingConfig.C8) {
						
						toIndex = castling_cfg.from_SquareID_rook_queenside_b;
						
					} else {
						
						throw new IllegalStateException("toIndex=" + toIndex);
					}
					
					toFile = (char) (104 - toIndex % 8);
					toRank = toIndex / 8 + 1;
					
					this.move = MoveUtil.createCastlingMove(fromIndex, toIndex);
				}
				
				break;
				
			case MoveUtil.TYPE_EP:
				isEP = true;
				break;
				
			case MoveUtil.TYPE_PROMOTION_B:
				isBishopPromotion = true;
				break;
				
			case MoveUtil.TYPE_PROMOTION_N:
				isNightPromotion = true;
				break;
				
			case MoveUtil.TYPE_PROMOTION_Q:
				isQueenPromotion = true;
				break;
				
			case MoveUtil.TYPE_PROMOTION_R:
				isRookPromotion = true;
				break;
				
			default:
				throw new RuntimeException("Unknown movetype: " + MoveUtil.getMoveType(move));
		}

	}

	public MoveWrapper(String moveString, ChessBoard cb, boolean isFRC) {
		
		
		//Castling is notated as O-O or O-O-O in FRC/960 chess and with from-to square notations in classic chess
		if ("O-O".equals(moveString)) {
			
			isCastling = true;
			
			fromIndex 	= cb.colorToMove == WHITE ? cb.castlingConfig.from_SquareID_king_w : cb.castlingConfig.from_SquareID_king_b;
			
			toIndex 	= cb.colorToMove == WHITE ? CastlingConfig.G1 : CastlingConfig.G8;
			
			fromFile 	= (char) (104 - fromIndex % 8);
			fromRank 	= fromIndex / 8 + 1;

			toFile 		= (char) (104 - toIndex % 8);
			toRank 		= toIndex / 8 + 1;
			
		} else if ("O-O-O".equals(moveString)) {
			
			isCastling = true;
			
			fromIndex 	= cb.colorToMove == WHITE ? cb.castlingConfig.from_SquareID_king_w : cb.castlingConfig.from_SquareID_king_b;
			
			toIndex 	= cb.colorToMove == WHITE ? CastlingConfig.C1 : CastlingConfig.C8;
			
			fromFile 	= (char) (104 - fromIndex % 8);
			fromRank 	= fromIndex / 8 + 1;

			toFile 		= (char) (104 - toIndex % 8);
			toRank 		= toIndex / 8 + 1;
			
		} else {
			
			fromFile = moveString.charAt(0);
			fromRank = Integer.parseInt(moveString.substring(1, 2));
			fromIndex = (fromRank - 1) * 8 + 104 - fromFile;
	
			toFile = moveString.charAt(2);
			toRank = Integer.parseInt(moveString.substring(3, 4));
			toIndex = (toRank - 1) * 8 + 104 - toFile;
		}
		
		
		pieceIndex = 
				  (cb.pieces[cb.colorToMove][ChessConstants.PAWN] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.PAWN
				: (cb.pieces[cb.colorToMove][ChessConstants.BISHOP] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.BISHOP
				: (cb.pieces[cb.colorToMove][ChessConstants.NIGHT] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.NIGHT
				: (cb.pieces[cb.colorToMove][ChessConstants.KING] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.KING
				: (cb.pieces[cb.colorToMove][ChessConstants.QUEEN] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.QUEEN
				: (cb.pieces[cb.colorToMove][ChessConstants.ROOK] & Util.POWER_LOOKUP[fromIndex]) != 0 ? ChessConstants.ROOK 
				: -1;
		
		if (pieceIndex == -1) {
			
			throw new RuntimeException("Source piece not found at index "
					 + ", cb.pieces[cb.colorToMove][type]=" + cb.pieces[cb.colorToMove][ChessConstants.NIGHT]
							 + ", fromIndex=" + fromIndex + ", cb.colorToMove=" + cb.colorToMove + ", move=" + moveString + ", board=" + cb.toString());
		}
		
		
		pieceIndexAttacked = 
				  (cb.pieces[cb.colorToMoveInverse][ChessConstants.PAWN] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.PAWN
				: (cb.pieces[cb.colorToMoveInverse][ChessConstants.BISHOP] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.BISHOP
				: (cb.pieces[cb.colorToMoveInverse][ChessConstants.NIGHT] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.NIGHT
				: (cb.pieces[cb.colorToMoveInverse][ChessConstants.KING] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.KING
				: (cb.pieces[cb.colorToMoveInverse][ChessConstants.QUEEN] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.QUEEN
				: (cb.pieces[cb.colorToMoveInverse][ChessConstants.ROOK] & Util.POWER_LOOKUP[toIndex]) != 0 ? ChessConstants.ROOK 
				: 0;
		

		if (pieceIndexAttacked == 0) {
			
			if (pieceIndex == ChessConstants.PAWN && (toRank == 1 || toRank == 8)) {
				if (moveString.length() == 5) {
					if (moveString.substring(4, 5).equals("n")) {
						isNightPromotion = true;
						move = MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex);
					} else if (moveString.substring(4, 5).equals("r")) {
						isRookPromotion = true;
						move = MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex);
					} else if (moveString.substring(4, 5).equals("b")) {
						isBishopPromotion = true;
						move = MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex);
					} else if (moveString.substring(4, 5).equals("q")) {
						isQueenPromotion = true;
						move = MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex);
					}
					
				} else {
					
					isQueenPromotion = true;
					move = MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex);
				}
				
			} else {
				
				
				if (!isCastling && pieceIndex == ChessConstants.KING) {
					
					int from_file = fromIndex & 7;
					int from_rank = fromIndex >>> 3;
					
					int to_file = toIndex & 7;
					int to_rank = toIndex >>> 3;
					
					int delta_file = Math.abs(from_file - to_file);
					int delta_rank = Math.abs(from_rank - to_rank);
					
					if (delta_rank > 1) {
						
						throw new IllegalStateException("King move with delta_rank=" + delta_rank);
					}
					
					if (!isFRC) {
						
						//Classic chess
							
						if (delta_file > 1) {
							
							isCastling = true;
							
						}
						
					} else {
					
						//FRC
					
						//In Cutechess GUI Chess960 castling moves are send with from_square of the king and the from_square of rook
						
						if (fromIndex == toIndex) {
							
							if (toIndex == CastlingConfig.G1 || toIndex == CastlingConfig.C1
									|| toIndex == CastlingConfig.G8 || toIndex == CastlingConfig.C8) {
								
								//Do nothing
								
							} else {
								
								throw new IllegalStateException("King move is castling and the move has the same from_to squares, which are not G1 C1 G8 C8.");
							}
							
							isCastling = true;
							
						} else if ((cb.pieces[cb.colorToMove][ChessConstants.ROOK] & Util.POWER_LOOKUP[toIndex]) != 0) {
							
							if (toIndex == cb.castlingConfig.from_SquareID_rook_kingside_w) {
								
								isCastling = true;
								
								toIndex = CastlingConfig.G1;
								
							} else if (toIndex == cb.castlingConfig.from_SquareID_rook_queenside_w) {
								
								isCastling = true;
								
								toIndex = CastlingConfig.C1;
								
							} else if (toIndex == cb.castlingConfig.from_SquareID_rook_kingside_b) {
								
								isCastling = true;
								
								toIndex = CastlingConfig.G8;
								
							} else if (toIndex == cb.castlingConfig.from_SquareID_rook_queenside_b) {
								
								isCastling = true;
								
								toIndex = CastlingConfig.C8;
								
							} else {
								
								throw new IllegalStateException("King move is castling and king goes to a rook, which is not on the initial square.");
							}
							
						} else if (delta_file > 1) {
							
							isCastling = true;
							
						} else {
							
							isCastling = false;
						}
					}
				}
				
				
				if (isCastling) {
					
					move = MoveUtil.createCastlingMove(fromIndex, toIndex);
					
					if (cb.isValidMove(move)) {
						
						return;
						
					} else {
						
						throw new IllegalStateException("Not valid castling move");
					}
				}

				
				if (pieceIndex == ChessConstants.PAWN && toIndex % 8 != fromIndex % 8) {
					
					move = MoveUtil.createEPMove(fromIndex, toIndex);
					
				} else {
					
					move = MoveUtil.createMove(fromIndex, toIndex, pieceIndex);
				}
			}
			
		} else {
			if (pieceIndex == ChessConstants.PAWN && (toRank == 1 || toRank == 8)) {
				if (moveString.length() == 5) {
					if (moveString.substring(4, 5).equals("n")) {
						isNightPromotion = true;
						move = MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex, pieceIndexAttacked);
					} else if (moveString.substring(4, 5).equals("r")) {
						isRookPromotion = true;
						move = MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex, pieceIndexAttacked);
					} else if (moveString.substring(4, 5).equals("b")) {
						isBishopPromotion = true;
						move = MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex, pieceIndexAttacked);
					} else if (moveString.substring(4, 5).equals("q")) {
						isQueenPromotion = true;
						move = MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex, pieceIndexAttacked);
					}
				} else {
					move = MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex, pieceIndexAttacked);
				}
			} else {
				move = MoveUtil.createAttackMove(fromIndex, toIndex, pieceIndex, pieceIndexAttacked);
			}
		}
	}

	@Override
	public String toString() {
		String moveString = "" + fromFile + fromRank + toFile + toRank;
		if (isQueenPromotion) {
			return moveString + "q";
		} else if (isNightPromotion) {
			return moveString + "n";
		} else if (isRookPromotion) {
			return moveString + "r";
		} else if (isBishopPromotion) {
			return moveString + "b";
		}
		return moveString;
	}
	
	
	public void toString(StringBuilder text_buffr) {
		text_buffr.append("" + fromFile + fromRank + toFile + toRank);
		if (isQueenPromotion) {
			text_buffr.append("q");
		} else if (isNightPromotion) {
			text_buffr.append("n");
		} else if (isRookPromotion) {
			text_buffr.append("r");
		} else if (isBishopPromotion) {
			text_buffr.append("b");
		}
	}
	

	@Override
	public boolean equals(Object obj) {
		MoveWrapper compare = (MoveWrapper) obj;
		return compare.toString().equals(toString());
	}

}
