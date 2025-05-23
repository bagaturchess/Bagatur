package bagaturchess.bitboard.impl2;


import bagaturchess.bitboard.api.IInternalMoveList;


public class MoveGeneration {
	
	
	public static final void generateMoves(final ChessBoard cb, final IInternalMoveList list) {

		switch (Long.bitCount(cb.checking_pieces)) {
		
			case 0:
				// not in-check
				generateNotInCheckMoves(cb, list);
				break;
				
			case 1:
				// in-check
				switch (cb.piece_indexes[Long.numberOfTrailingZeros(cb.checking_pieces)]) {
				
					case ChessConstants.PAWN:
						// fall-through
					case ChessConstants.KNIGHT:
						// move king
						addKingMoves(cb, list);
						break;
						
					default:
						generateOutOfSlidingCheckMoves(cb, list);
				}
				break;
				
			default:
				// double check, only the king can move
				addKingMoves(cb, list);
		}
	}

	public static final void generateAttacks(final ChessBoard cb, final IInternalMoveList list) {

		switch (Long.bitCount(cb.checking_pieces)) {
			
			case 0:
				// not in-check
				generateNotInCheckAttacks(cb, list);
				break;
				
			case 1:
				generateOutOfCheckAttacks(cb, list);
				break;
				
			default:
				// double check, only the king can attack
				addKingAttacks(cb, list);
		}
	}
	
	
	private static final void addMove(final int move, final IInternalMoveList list) {
		
		list.reserved_add(move);
	}
	
	
	private static void generateNotInCheckMoves(final ChessBoard cb, final IInternalMoveList list) {

		// non pinned pieces
		addKingMoves(cb, list);
		addQueenMoves(cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & ~cb.pinned_pieces, cb.all_pieces, cb.empty_spaces, list);
		addRookMoves(cb.getPiecesOfSideToMove(ChessConstants.ROOK) & ~cb.pinned_pieces, cb.all_pieces, cb.empty_spaces, list);
		addBishopMoves(cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & ~cb.pinned_pieces, cb.all_pieces, cb.empty_spaces, list);
		addNightMoves(cb.getPiecesOfSideToMove(ChessConstants.KNIGHT) & ~cb.pinned_pieces, cb.empty_spaces, list);
		addPawnMoves(cb.getPiecesOfSideToMove(ChessConstants.PAWN) & ~cb.pinned_pieces, cb, cb.empty_spaces, list);

		// pinned pieces
		long piece = cb.getPiecesOfSideToMove_All() & cb.pinned_pieces;
		while (piece != 0) {
			switch (cb.piece_indexes[Long.numberOfTrailingZeros(piece)]) {
			case ChessConstants.PAWN:
				addPawnMoves(Long.lowestOneBit(piece), cb,
						cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
				break;
			case ChessConstants.BISHOP:
				addBishopMoves(Long.lowestOneBit(piece), cb.all_pieces,
						cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
				break;
			case ChessConstants.ROOK:
				addRookMoves(Long.lowestOneBit(piece), cb.all_pieces,
						cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
				break;
			case ChessConstants.QUEEN:
				addQueenMoves(Long.lowestOneBit(piece), cb.all_pieces,
						cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
			}
			piece &= piece - 1;
		}

	}

	private static void generateOutOfSlidingCheckMoves(final ChessBoard cb, final IInternalMoveList list) {

		// TODO when check is blocked -> pinned piece

		// move king or block sliding piece
		final long inBetween = ChessConstants.IN_BETWEEN[cb.getKingIndexOfSideToMove()][Long.numberOfTrailingZeros(cb.checking_pieces)];
		if (inBetween != 0) {
			addNightMoves(cb.getPiecesOfSideToMove(ChessConstants.KNIGHT) & ~cb.pinned_pieces, inBetween, list);
			addBishopMoves(cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & ~cb.pinned_pieces, cb.all_pieces, inBetween, list);
			addRookMoves(cb.getPiecesOfSideToMove(ChessConstants.ROOK) & ~cb.pinned_pieces, cb.all_pieces, inBetween, list);
			addQueenMoves(cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & ~cb.pinned_pieces, cb.all_pieces, inBetween, list);
			addPawnMoves(cb.getPiecesOfSideToMove(ChessConstants.PAWN) & ~cb.pinned_pieces, cb, inBetween, list);
		}

		addKingMoves(cb, list);
	}

	private static void generateNotInCheckAttacks(final ChessBoard cb, final IInternalMoveList list) {

		final long enemies = cb.getPiecesOfSideNotToMove_All();

		// non pinned pieces
		addEpAttacks(cb, list);
		addPawnAttacksAndPromotions(cb.getPiecesOfSideToMove(ChessConstants.PAWN) & ~cb.pinned_pieces, cb, enemies, cb.empty_spaces, list);
		addNightAttacks(cb.getPiecesOfSideToMove(ChessConstants.KNIGHT) & ~cb.pinned_pieces, cb.piece_indexes, enemies, list);
		addRookAttacks(cb.getPiecesOfSideToMove(ChessConstants.ROOK) & ~cb.pinned_pieces, cb, enemies, list);
		addBishopAttacks(cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & ~cb.pinned_pieces, cb, enemies, list);
		addQueenAttacks(cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & ~cb.pinned_pieces, cb, enemies, list);
		addKingAttacks(cb, list);

		// pinned pieces
		long piece = cb.getPiecesOfSideToMove_All() & cb.pinned_pieces;
		while (piece != 0) {
			switch (cb.piece_indexes[Long.numberOfTrailingZeros(piece)]) {
			case ChessConstants.PAWN:
				addPawnAttacksAndPromotions(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], 0, list);
				break;
			case ChessConstants.BISHOP:
				addBishopAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
				break;
			case ChessConstants.ROOK:
				addRookAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
				break;
			case ChessConstants.QUEEN:
				addQueenAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
			}
			piece &= piece - 1;
		}

	}

	private static void generateOutOfCheckAttacks(final ChessBoard cb, final IInternalMoveList list) {
		// attack attacker
		addEpAttacks(cb, list);
		addPawnAttacksAndPromotions(cb.getPiecesOfSideToMove(ChessConstants.PAWN) & ~cb.pinned_pieces, cb, cb.checking_pieces, cb.empty_spaces, list);
		addNightAttacks(cb.getPiecesOfSideToMove(ChessConstants.KNIGHT) & ~cb.pinned_pieces, cb.piece_indexes, cb.checking_pieces, list);
		addBishopAttacks(cb.getPiecesOfSideToMove(ChessConstants.BISHOP) & ~cb.pinned_pieces, cb, cb.checking_pieces, list);
		addRookAttacks(cb.getPiecesOfSideToMove(ChessConstants.ROOK) & ~cb.pinned_pieces, cb, cb.checking_pieces, list);
		addQueenAttacks(cb.getPiecesOfSideToMove(ChessConstants.QUEEN) & ~cb.pinned_pieces, cb, cb.checking_pieces, list);
		addKingAttacks(cb, list);
	}

	private static void addPawnAttacksAndPromotions(final long pawns, final ChessBoard cb, final long enemies, final long emptySpaces, final IInternalMoveList list) {

		if (pawns == 0) {
			return;
		}

		if (cb.color_to_move == ChessConstants.WHITE) {

			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[ChessConstants.WHITE] & Bitboard.getBlackPawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = ChessConstants.PAWN_ATTACKS[ChessConstants.WHITE][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.PAWN, cb.piece_indexes[toIndex]), list);
					moves &= moves - 1;
				}
				piece &= piece - 1;
			}

			// promoting
			piece = pawns & Bitboard.RANK_7;
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);

				// promotion move
				if ((Long.lowestOneBit(piece) << 8 & emptySpaces) != 0) {
					addPromotionMove(fromIndex, fromIndex + 8, list);
				}

				// promotion attacks
				addPromotionAttacks(ChessConstants.PAWN_ATTACKS[ChessConstants.WHITE][fromIndex] & enemies, fromIndex, cb.piece_indexes, list);

				piece &= piece - 1;
			}
		} else {
			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[ChessConstants.BLACK] & Bitboard.getWhitePawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = ChessConstants.PAWN_ATTACKS[ChessConstants.BLACK][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.PAWN, cb.piece_indexes[toIndex]), list);
					moves &= moves - 1;
				}
				piece &= piece - 1;
			}

			// promoting
			piece = pawns & Bitboard.RANK_2;
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);

				// promotion move
				if ((Long.lowestOneBit(piece) >>> 8 & emptySpaces) != 0) {
					addPromotionMove(fromIndex, fromIndex - 8, list);
				}

				// promotion attacks
				addPromotionAttacks(ChessConstants.PAWN_ATTACKS[ChessConstants.BLACK][fromIndex] & enemies, fromIndex, cb.piece_indexes, list);

				piece &= piece - 1;
			}
		}
	}

	private static void addBishopAttacks(long piece, final ChessBoard cb, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, cb.all_pieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.BISHOP, cb.piece_indexes[toIndex]), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addRookAttacks(long piece, final ChessBoard cb, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, cb.all_pieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.ROOK, cb.piece_indexes[toIndex]), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addQueenAttacks(long piece, final ChessBoard cb, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, cb.all_pieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.QUEEN, cb.piece_indexes[toIndex]), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addBishopMoves(long piece, final long allPieces, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ChessConstants.BISHOP), list);
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}
	
	
	private static void addQueenMoves(long piece, final long allPieces, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ChessConstants.QUEEN), list);
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}
	
	
	private static void addRookMoves(long piece, final long allPieces, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ChessConstants.ROOK), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addNightMoves(long piece, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = ChessConstants.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ChessConstants.KNIGHT), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addPawnMoves(final long pawns, final ChessBoard cb, final long possiblePositions, final IInternalMoveList list) {

		if (pawns == 0) {
			return;
		}

		if (cb.color_to_move == ChessConstants.WHITE) {
			// 1-move
			long piece = pawns & (possiblePositions >>> 8) & Bitboard.RANK_23456;
			while (piece != 0) {
				addMove(MoveUtil.createWhitePawnMove(Long.numberOfTrailingZeros(piece)), list);
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions >>> 16) & Bitboard.RANK_2;
			while (piece != 0) {
				if ((cb.empty_spaces & (Long.lowestOneBit(piece) << 8)) != 0) {
					addMove(MoveUtil.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)), list);
				}
				piece &= piece - 1;
			}
		} else {
			// 1-move
			long piece = pawns & (possiblePositions << 8) & Bitboard.RANK_34567;
			while (piece != 0) {
				addMove(MoveUtil.createBlackPawnMove(Long.numberOfTrailingZeros(piece)), list);
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions << 16) & Bitboard.RANK_7;
			while (piece != 0) {
				if ((cb.empty_spaces & (Long.lowestOneBit(piece) >>> 8)) != 0) {
					addMove(MoveUtil.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)), list);
				}
				piece &= piece - 1;
			}
		}
	}
	
	
	private static void addKingMoves(final ChessBoard cb, final IInternalMoveList list) {
				
		final int fromIndex = cb.getKingIndexOfSideToMove();
		
		long moves = ChessConstants.KING_MOVES[fromIndex] & cb.empty_spaces;
		
		while (moves != 0) {
			
			addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ChessConstants.KING), list);
			
			moves &= moves - 1;
		}

		
		// castling
		if (cb.checking_pieces == 0) {
			
			long castlingIndexes = CastlingUtil.getCastlingIndexes(cb.color_to_move, cb.castling_rights, cb.castling_config);
			
			while (castlingIndexes != 0) {
				
				final int toIndex_king = Long.numberOfTrailingZeros(castlingIndexes);
				
				// no piece in between?
				if (CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) {
					
					addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king), list);
				}
				
				castlingIndexes &= castlingIndexes - 1;
			}
		}
	}
	
	
	private static void addKingAttacks(final ChessBoard cb, final IInternalMoveList list) {
		final int fromIndex = cb.getKingIndexOfSideToMove();
		long moves = ChessConstants.KING_MOVES[fromIndex] & cb.getPiecesOfSideNotToMove_All();
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.KING, cb.piece_indexes[toIndex]), list);
			moves &= moves - 1;
		}
	}
	
	
	private static void addNightAttacks(long piece, final int[] pieceIndexes, final long possiblePositions, final IInternalMoveList list) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = ChessConstants.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ChessConstants.KNIGHT, pieceIndexes[toIndex]), list);
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}
	
	
	private static void addEpAttacks(final ChessBoard cb, final IInternalMoveList list) {
		if (cb.ep_index == 0) {
			return;
		}
		long piece = cb.getPiecesOfSideToMove(ChessConstants.PAWN) & ChessConstants.PAWN_ATTACKS[1 - cb.color_to_move][cb.ep_index];
		while (piece != 0) {
			addMove(MoveUtil.createEPMove(Long.numberOfTrailingZeros(piece), cb.ep_index), list);
			piece &= piece - 1;
		}
	}
	
	
	private static void addPromotionMove(final int fromIndex, final int toIndex, final IInternalMoveList list) {
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex), list);
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex), list);
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex), list);
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex), list);
	}
	
	
	private static void addPromotionAttacks(long moves, final int fromIndex, final int[] piece_indexes, final IInternalMoveList list) {
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex, piece_indexes[toIndex]), list);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex, piece_indexes[toIndex]), list);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex, piece_indexes[toIndex]), list);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex, piece_indexes[toIndex]), list);
			moves &= moves - 1;
		}
	}
}
