package bagaturchess.bitboard.impl2;


public final class CheckUtil {

	public static boolean isInCheck(final ChessBoard cb, int color) {
		final int kingIndex = cb.getKingIndex(color);
		return (cb.getPieces(1 - color, ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[kingIndex]
				| (cb.getPieces(1 - color, ChessConstants.ROOK) | cb.getPieces(1 - color, ChessConstants.QUEEN)) & MagicUtil.getRookMoves(kingIndex, cb.all_pieces)
				| (cb.getPieces(1 - color, ChessConstants.BISHOP) | cb.getPieces(1 - color, ChessConstants.QUEEN)) & MagicUtil.getBishopMoves(kingIndex, cb.all_pieces) 
				| cb.getPieces(1 - color, ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[color][kingIndex]
			) != 0L;
	}
	
	public static long getCheckingPieces(final ChessBoard cb) {
		final int kingIndex = cb.getKingIndexOfSideToMove();

		// put 'super-piece' in kings position
		return (cb.getPiecesOfSideNotToMove(ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[kingIndex]
				| (cb.getPiecesOfSideNotToMove(ChessConstants.ROOK)| cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getRookMoves(kingIndex, cb.all_pieces)
				| (cb.getPiecesOfSideNotToMove(ChessConstants.BISHOP)| cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getBishopMoves(kingIndex, cb.all_pieces) 
				| cb.getPiecesOfSideNotToMove(ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[cb.color_to_move][kingIndex]
			);
	}
	
	public static long getCheckingPieces(final ChessBoard cb, final int sourcePieceIndex) {
		
		switch(sourcePieceIndex) {
			case ChessConstants.PAWN:
				return cb.getPiecesOfSideNotToMove(ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[cb.color_to_move][cb.getKingIndexOfSideToMove()];
			case ChessConstants.KNIGHT:
				return cb.getPiecesOfSideNotToMove(ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[cb.getKingIndexOfSideToMove()];
			case ChessConstants.BISHOP:
				return cb.getPiecesOfSideNotToMove(ChessConstants.BISHOP) & MagicUtil.getBishopMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
			case ChessConstants.ROOK:
				return cb.getPiecesOfSideNotToMove(ChessConstants.ROOK) & MagicUtil.getRookMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
			case ChessConstants.QUEEN:
				return cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN) & MagicUtil.getQueenMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
			default:
				//king can never set the other king in check
				return 0;	
		}
	}

	public static boolean isInCheck(final ChessBoard cb, final int kingIndex, final int colorToMove, final long allPieces) {
	
		// put 'super-piece' in kings position
		return (cb.getPiecesOfSideNotToMove(ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[kingIndex]
				| (cb.getPiecesOfSideNotToMove(ChessConstants.ROOK) | cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getRookMoves(kingIndex, allPieces)
				| (cb.getPiecesOfSideNotToMove(ChessConstants.BISHOP) | cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getBishopMoves(kingIndex, allPieces) 
				| cb.getPiecesOfSideNotToMove(ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[colorToMove][kingIndex]
			) != 0;
	}

	/*public static boolean isInCheckIncludingKing(final int kingIndex, final int colorToMove, final long[] enemyPieces, final long allPieces, final int enemyMajorPieces) {

		//TODO
		if(enemyMajorPieces==0) {
			return (enemyPieces[PAWN] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex]
					| enemyPieces[KING] & StaticMoves.KING_MOVES[kingIndex]
				) != 0;
		}
		
		// put 'super-piece' in kings position
		return (enemyPieces[NIGHT] & StaticMoves.KNIGHT_MOVES[kingIndex]
				| (enemyPieces[ROOK] | enemyPieces[QUEEN]) & MagicUtil.getRookMoves(kingIndex, allPieces)
				| (enemyPieces[BISHOP] | enemyPieces[QUEEN]) & MagicUtil.getBishopMoves(kingIndex, allPieces) 
				| enemyPieces[PAWN] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex]
				| enemyPieces[KING] & StaticMoves.KING_MOVES[kingIndex]
			) != 0;
	}*/
	
	public static boolean isInCheckIncludingKing(final ChessBoard cb, final int kingIndex, final int colorToMove, final long allPieces) {
		
		// put 'super-piece' in kings position
		return (cb.getPiecesOfSideNotToMove(ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[kingIndex]
				| (cb.getPiecesOfSideNotToMove(ChessConstants.ROOK) | cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getRookMoves(kingIndex, allPieces)
				| (cb.getPiecesOfSideNotToMove(ChessConstants.BISHOP) | cb.getPiecesOfSideNotToMove(ChessConstants.QUEEN)) & MagicUtil.getBishopMoves(kingIndex, allPieces) 
				| cb.getPiecesOfSideNotToMove(ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[colorToMove][kingIndex]
				| cb.getPiecesOfSideNotToMove(ChessConstants.KING) & ChessConstants.KING_MOVES[kingIndex]
			) != 0;
	}
}
