package bagaturchess.bitboard.impl2;


public class SEEUtil {

	private static int getSmallestAttackSeeMove(final ChessBoard cb, final int colorToMove, final int toIndex, final long allPieces, final long slidingMask) {
		
		// put 'super-piece' in see position
		long attackMove;

		// pawn non-promotion attacks
		attackMove = ChessConstants.PAWN_ATTACKS[1 - colorToMove][toIndex] & cb.getPiecesOfSideToMove(ChessConstants.PAWN) & allPieces & Bitboard.RANK_NON_PROMOTION[colorToMove];
		if (attackMove != 0) {
			return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.PAWN);
		}

		// knight attacks
		attackMove = cb.getPiecesOfSideToMove(ChessConstants.KNIGHT) & ChessConstants.KNIGHT_MOVES[toIndex] & allPieces;
		if (attackMove != 0) {
			return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.KNIGHT);
		}

		// bishop attacks
		if ((cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & slidingMask) != 0) {
			attackMove = cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & MagicUtil.getBishopMoves(toIndex, allPieces) & allPieces;
			if (attackMove != 0) {
				return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.BISHOP);
			}
		}

		// rook attacks
		if ((cb.getPiecesOfSideToMove(ChessConstants.ROOK) & slidingMask) != 0) {
			attackMove = cb.getPiecesOfSideToMove(ChessConstants.ROOK) & MagicUtil.getRookMoves(toIndex, allPieces) & allPieces;
			if (attackMove != 0) {
				return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.ROOK);
			}
		}

		// queen attacks
		if ((cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & slidingMask) != 0) {
			attackMove = cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & MagicUtil.getQueenMoves(toIndex, allPieces) & allPieces;
			if (attackMove != 0) {
				return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.QUEEN);
			}
		}

		// pawn promotion attacks
		if ((cb.getPiecesOfSideToMove(ChessConstants.PAWN) & Bitboard.RANK_PROMOTION[colorToMove]) != 0) {
			attackMove = ChessConstants.PAWN_ATTACKS[1 - colorToMove][toIndex] & cb.getPiecesOfSideToMove(ChessConstants.PAWN) & allPieces & Bitboard.RANK_PROMOTION[colorToMove];
			if (attackMove != 0) {
				return MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, Long.numberOfTrailingZeros(attackMove), toIndex, 0);
			}
		}

		// king attacks
		attackMove = cb.getPiecesOfSideToMove(ChessConstants.KING) & ChessConstants.KING_MOVES[toIndex];
		if (attackMove != 0) {
			return MoveUtil.createSeeAttackMove(attackMove, ChessConstants.KING);
		}

		return 0;
	}

	private static int getSeeScore(final ChessBoard cb, final int colorToMove, final int toIndex, final int attackedPieceIndex, long allPieces,
			long slidingMask) {

		final int move = getSmallestAttackSeeMove(cb, colorToMove, toIndex, allPieces, slidingMask);

		/* skip if the square isn't attacked anymore by this side */
		if (move == 0) {
			return 0;
		}
		if (attackedPieceIndex == ChessConstants.KING) {
			return 3000;
		}

		allPieces ^= ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)];
		slidingMask &= allPieces;

		// add score when promotion
		if (MoveUtil.isPromotion(move)) {

			/* Do not consider captures if they lose material, therefore max zero */
			return Math.max(0, ChessConstants.PROMOTION_SCORE_SEE[ChessConstants.QUEEN] + ChessConstants.MATERIAL_SEE[attackedPieceIndex]
					- getSeeScore(cb, 1 - colorToMove, toIndex, ChessConstants.QUEEN, allPieces, slidingMask));
		} else {

			/* Do not consider captures if they lose material, therefore max zero */
			return Math.max(0, ChessConstants.MATERIAL_SEE[attackedPieceIndex]
					- getSeeScore(cb, 1 - colorToMove, toIndex, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask));
		}

	}

	public static int getSeeCaptureScore(final ChessBoard cb, final int move) {

		/*if (EngineConstants.ASSERT) {
			if (MoveUtil.getAttackedPieceIndex(move) == 0) {
				Assert.isTrue(MoveUtil.getMoveType(move) != 0);
			}
		}*/

		final int index = MoveUtil.getToIndex(move);
		final long allPieces = cb.all_pieces & ~ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)];
		final long slidingMask = MagicUtil.getQueenMovesEmptyBoard(index) & allPieces;

		// add score when promotion
		if (MoveUtil.isPromotion(move)) {
			return ChessConstants.PROMOTION_SCORE_SEE[MoveUtil.getMoveType(move)] + ChessConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)]
					- getSeeScore(cb, 1 - cb.color_to_move, index, MoveUtil.getMoveType(move), allPieces, slidingMask);
		} else {
			return ChessConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)]
					- getSeeScore(cb, 1 - cb.color_to_move, index, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask);
		}
	}
	
	public static int getSeeFieldScore(final ChessBoard cb, int squareID) {
		
		final long allPieces = cb.all_pieces & ~ChessConstants.POWER_LOOKUP[squareID];
		final long slidingMask = MagicUtil.getQueenMovesEmptyBoard(squareID) & allPieces;

		return -getSeeScore(cb, 1 - cb.color_to_move, squareID, cb.piece_indexes[squareID], allPieces, slidingMask);
	}
}
