package bagaturchess.bitboard.impl3.internal;


import static bagaturchess.bitboard.impl3.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.WHITE;

import bagaturchess.bitboard.api.IHistoryProvider;
import bagaturchess.bitboard.common.Properties;


public final class MoveGenerator {
	
	
	private final int[] moves 									= new int[30000];
	private final long[] moveScores 							= new long[30000];
	private final int[] nextToGenerate 							= new int[EngineConstants.MAX_PLIES * 2];
	private final int[] nextToMove 								= new int[EngineConstants.MAX_PLIES * 2];
	private int currentPly;

	private int root_search_first_move_index;
	
	
	public MoveGenerator() {
		
		clearHistoryHeuristics();
	}
	
	
	public void clearHistoryHeuristics() {
		
		currentPly = 0;
	}
	
	
	public void setRootSearchFirstMoveIndex(int _root_search_first_move_index) {
		
		root_search_first_move_index = _root_search_first_move_index;
	}
	
	
	public void startPly() {
		nextToGenerate[currentPly + 1] = nextToGenerate[currentPly];
		nextToMove[currentPly + 1] = nextToGenerate[currentPly];
		currentPly++;
	}

	
	public void endPly() {
		currentPly--;
	}

	
	public int next() {
		return moves[nextToMove[currentPly]++];
	}

	public long getScore() {
		
		long val = moveScores[nextToMove[currentPly] - 1];
		
		if (val < 0) {
			
			throw new IllegalStateException("getScore: val=" + val);
		}
		
		return val;
	}

	public int previous() {
		if (nextToMove[currentPly] - 1 < 0) {
			return 0;
		}
		
		return moves[nextToMove[currentPly] - 1];
	}

	public boolean hasNext() {
		return nextToGenerate[currentPly] != nextToMove[currentPly];
	}

	public int collectMoves(final bagaturchess.bitboard.api.IInternalMoveList list) {
		final int start = nextToMove[currentPly];
		final int end = nextToGenerate[currentPly];
		for (int i = start; i < end; i++) {
			list.reserved_add(moves[i]);
		}
		nextToMove[currentPly] = end;
		return end - start;
	}

	public void addMove(final int move) {
		moves[nextToGenerate[currentPly]++] = move;
	}

	public void setMVVLVAScores(final ChessBoard cb) {
		
		final int scale = 100;
		
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			
			int move = moves[j];
			
			//getAttackedPieceIndex and getSourcePieceIndex returns value in [1, 6]
			
			int score = (6 * MoveUtil.getAttackedPieceIndex(move) - 1 * MoveUtil.getSourcePieceIndex(move));
			
			if (MoveUtil.isPromotion(move)) {
				
				//MoveUtil.getMoveType(move) returns value in [2, 5] when the move is promotion
				score += 1 * MoveUtil.getMoveType(move);
				
			}
			
			moveScores[j] = scale * score;
		}
	}
	
	public void setSEEScores(final ChessBoard cb) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			moveScores[j] = SEEUtil.getSeeCaptureScore(cb, moves[j]);
		}
	}
	
	public int getCountGoodAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) > 0) count++;
		}
		return count;
	}
	
	
	public int getCountEqualAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) == 0) count++;
		}
		return count;
	}
	
	
	public int getCountBadAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) < 0) count++;
		}
		return count;
	}
	
	
	public int getCountGoodAndEqualAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) >= 0) count++;
		}
		return count;
	}
	
	
	public int getCountMoves() {
		return nextToGenerate[currentPly] - nextToMove[currentPly];
	}
	
	
	public void setHHScores(final int inCheck, final int colorToMove, final int parentMove,
			IHistoryProvider history_provider) {
		
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			
			int move = moves[j];
			
			long score = history_provider.getScores(colorToMove, move);
						
			if (score < 0) {
				
				throw new IllegalStateException("score < 0");
			}
			
			moveScores[j] = score;
		}
	}
	
	
	public void setRootScores(final ChessBoard cb, final int parentMove, final int ttMove, final int ply,
			IHistoryProvider history_provider) {
		
		int killer1Move = history_provider.getKiller1(cb.colorToMove, ply);
		int killer2Move = history_provider.getKiller2(cb.colorToMove, ply);
		int counterMove1 = history_provider.getCounter1(cb.colorToMove, parentMove);
		int counterMove2 = history_provider.getCounter2(cb.colorToMove, parentMove);
		
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			
			int cur_move = moves[j];
			
			moveScores[j] = 0;
			
			if (ttMove == cur_move) {
				
				moveScores[j] += 20000 * 100;
			
			}
			
			if (killer1Move == cur_move) {
				
				moveScores[j] += 5000 * 100;
				
			}
			
			if (killer2Move == cur_move) {
				
				moveScores[j] += 4000 * 100;
				
			}
			
			if (counterMove1 == cur_move) {
				
				moveScores[j] += 3000 * 100;
				
			}
			
			if (counterMove2 == cur_move) {
				
				moveScores[j] += 2000 * 100;
				
			}
			
			if (MoveUtil.isQuiet(cur_move)) {
				
				moveScores[j] += history_provider.getScores(cb.colorToMove, cur_move);
				
			} else {
				
				if (SEEUtil.getSeeCaptureScore(cb, cur_move) >= 0) {
				
					moveScores[j] += 7000 * 100 + 100 * (MoveUtil.getAttackedPieceIndex(cur_move) * 6 - MoveUtil.getSourcePieceIndex(cur_move));
					
				} else {
					
					moveScores[j] += -5000 + 100 * (MoveUtil.getAttackedPieceIndex(cur_move) * 6 - MoveUtil.getSourcePieceIndex(cur_move));
				}
			}
		}
	}
	
	
	/*public void setAllScores(final ChessBoard cb, final int parentMove, final int ttMove, int counterMove, int killer1Move, int killer2Move) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (ttMove == moves[j]) {
				moveScores[j] = 10000;
			} else if (counterMove == moves[j]) {
				moveScores[j] = 300;
			} else if (killer1Move == moves[j]) {
				moveScores[j] = 500;
			} else if (killer2Move == moves[j]) {
				moveScores[j] = 400;
			} else if (!MoveUtil.isQuiet(moves[j])) {
				moveScores[j] = 1000 + SEEUtil.getSeeCaptureScore(cb, moves[j]);
			} else {
				moveScores[j] = getHHScore(cb.colorToMove, MoveUtil.getFromToIndex(moves[j]), MoveUtil.getSourcePieceIndex(moves[j]), MoveUtil.getToIndex(moves[j]), parentMove);
			}
			//System.out.println("moveScores[j]=" + moveScores[j]);
		}
	}*/
	
	
	public void sort() {

		final int start_index = nextToMove[currentPly];
		final int end_index = nextToGenerate[currentPly] - 1;

		if (end_index <= start_index) {
			return;
		}

		// Keep sorting deterministic and allocation-free in the search hot path.
		// The previous periodic Random-based shuffle improved nondeterminism, but it
		// costs NPS every time move ordering is scored and sorted.
		for (int i = start_index, j = i; i < end_index; j = ++i) {
			final long score = moveScores[i + 1];
			final int move = moves[i + 1];
			while (score > moveScores[j]) {
				moveScores[j + 1] = moveScores[j];
				moves[j + 1] = moves[j];
				if (j-- == start_index) {
					break;
				}
			}
			moveScores[j + 1] = score;
			moves[j + 1] = move;
		}
	}
	

	/*public String getMovesAsString() {
		StringBuilder sb = new StringBuilder();
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			sb.append(new MoveWrapper(moves[j]) + ", ");
		}
		return sb.toString();
	}
	*/
	
	
	
	
	/**
	 * Moves generation
	 */
	
	
	public void generateMoves(final ChessBoard cb) {

		final long checkingPieces = cb.checkingPieces;
		if (checkingPieces == 0) {
			generateNotInCheckMoves(cb);
			return;
		}

		if ((checkingPieces & (checkingPieces - 1)) == 0) {
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(checkingPieces)]) {
			case PAWN:
			case NIGHT:
				addKingMoves(cb);
				return;
			default:
				generateOutOfSlidingCheckMoves(cb);
				return;
			}
		}

		// double check, only the king can move
		addKingMoves(cb);
	}

	public void generateAttacks(final ChessBoard cb) {

		final long checkingPieces = cb.checkingPieces;
		if (checkingPieces == 0) {
			generateNotInCheckAttacks(cb);
			return;
		}

		if ((checkingPieces & (checkingPieces - 1)) == 0) {
			generateOutOfCheckAttacks(cb);
			return;
		}

		// double check, only the king can attack
		addKingAttacks(cb);
	}

	public void generateAll(final ChessBoard cb) {

		final long checkingPieces = cb.checkingPieces;
		if (checkingPieces == 0) {
			generateNotInCheckAttacks(cb);
			generateNotInCheckMoves(cb);
			return;
		}

		if ((checkingPieces & (checkingPieces - 1)) == 0) {
			generateOutOfCheckAttacks(cb);
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(checkingPieces)]) {
			case PAWN:
			case NIGHT:
				addKingMoves(cb);
				return;
			default:
				generateOutOfSlidingCheckMoves(cb);
				return;
			}
		}

		// double check, only the king can move/capture
		addKingAttacks(cb);
		addKingMoves(cb);
	}

	private void generateNotInCheckMoves(final ChessBoard cb) {

		// non pinned pieces
		addKingMoves(cb);
		addQueenMoves(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addRookMoves(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addBishopMoves(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addNightMoves(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.emptySpaces);
		addPawnMoves(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, cb.emptySpaces);

		// pinned pieces
		long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces;
		final int kingIndex = cb.kingIndex[cb.colorToMove];
		while (piece != 0) {
			final long fromMask = piece & -piece;
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			final long possiblePinnedMoves = cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[fromIndex][kingIndex];
			switch (cb.pieceIndexes[fromIndex]) {
			case PAWN:
				addPawnMoves(fromMask, cb, possiblePinnedMoves);
				break;
			case BISHOP:
				addBishopMoves(fromMask, cb.allPieces, possiblePinnedMoves);
				break;
			case ROOK:
				addRookMoves(fromMask, cb.allPieces, possiblePinnedMoves);
				break;
			case QUEEN:
				addQueenMoves(fromMask, cb.allPieces, possiblePinnedMoves);
			}
			piece ^= fromMask;
		}

	}

	private void generateOutOfSlidingCheckMoves(final ChessBoard cb) {

		// TODO when check is blocked -> pinned piece

		// move king or block sliding piece
		final long inBetween = ChessConstants.IN_BETWEEN[cb.kingIndex[cb.colorToMove]][Long.numberOfTrailingZeros(cb.checkingPieces)];
		if (inBetween != 0) {
			addNightMoves(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, inBetween);
			addBishopMoves(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addRookMoves(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addQueenMoves(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addPawnMoves(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, inBetween);
		}

		addKingMoves(cb);
	}

	private void generateNotInCheckAttacks(final ChessBoard cb) {

		final long enemies = cb.friendlyPieces[cb.colorToMoveInverse];

		// non pinned pieces
		addEpAttacks(cb);
		addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, enemies, cb.emptySpaces);
		addNightAttacks(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.pieceIndexes, enemies);
		addRookAttacks(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb, enemies);
		addBishopAttacks(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb, enemies);
		addQueenAttacks(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb, enemies);
		addKingAttacks(cb);

		// pinned pieces
		long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces;
		final int kingIndex = cb.kingIndex[cb.colorToMove];
		while (piece != 0) {
			final long fromMask = piece & -piece;
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			final long possiblePinnedAttacks = enemies & ChessConstants.PINNED_MOVEMENT[fromIndex][kingIndex];
			switch (cb.pieceIndexes[fromIndex]) {
			case PAWN:
				addPawnAttacksAndPromotions(fromMask, cb, possiblePinnedAttacks, 0);
				break;
			case BISHOP:
				addBishopAttacks(fromMask, cb, possiblePinnedAttacks);
				break;
			case ROOK:
				addRookAttacks(fromMask, cb, possiblePinnedAttacks);
				break;
			case QUEEN:
				addQueenAttacks(fromMask, cb, possiblePinnedAttacks);
			}
			piece ^= fromMask;
		}

	}

	private void generateOutOfCheckAttacks(final ChessBoard cb) {
		// Capture the checking piece. Quiet promotions are legal here only when they
		// block a single sliding check; otherwise they were filtered later by
		// ChessBoard.isLegal(move), which is no longer in the hot generation path.
		addEpAttacks(cb);
		long promotionPushTargets = 0;
		if ((cb.checkingPieces & (cb.checkingPieces - 1)) == 0) {
			final int checkerIndex = Long.numberOfTrailingZeros(cb.checkingPieces);
			switch (cb.pieceIndexes[checkerIndex]) {
			case BISHOP:
			case ROOK:
			case QUEEN:
				promotionPushTargets = cb.emptySpaces & ChessConstants.IN_BETWEEN[cb.kingIndex[cb.colorToMove]][checkerIndex];
			}
		}
		addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, cb.checkingPieces, promotionPushTargets);
		addNightAttacks(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.pieceIndexes, cb.checkingPieces);
		addBishopAttacks(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addRookAttacks(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addQueenAttacks(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addKingAttacks(cb);
	}

	private void addPawnAttacksAndPromotions(final long pawns, final ChessBoard cb, final long enemies, final long emptySpaces) {

		if (pawns == 0) {
			return;
		}

		if (cb.colorToMove == WHITE) {

			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[WHITE] & Bitboard.getBlackPawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = StaticMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, PAWN, cb.pieceIndexes[toIndex]));
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
					addPromotionMove(fromIndex, fromIndex + 8);
				}

				// promotion attacks
				addPromotionAttacks(StaticMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies, fromIndex, cb.pieceIndexes);

				piece &= piece - 1;
			}
		} else {
			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[BLACK] & Bitboard.getWhitePawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = StaticMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, PAWN, cb.pieceIndexes[toIndex]));
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
					addPromotionMove(fromIndex, fromIndex - 8);
				}

				// promotion attacks
				addPromotionAttacks(StaticMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies, fromIndex, cb.pieceIndexes);

				piece &= piece - 1;
			}
		}
	}

	private void addBishopAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, BISHOP, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addRookAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ROOK, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addQueenAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, QUEEN, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addBishopMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), BISHOP));
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}

	private void addQueenMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), QUEEN));
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}

	private void addRookMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ROOK));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addNightMoves(long piece, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), NIGHT));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addPawnMoves(final long pawns, final ChessBoard cb, final long possiblePositions) {

		if (pawns == 0) {
			return;
		}

		if (cb.colorToMove == WHITE) {
			// 1-move
			long piece = pawns & (possiblePositions >>> 8) & Bitboard.RANK_23456;
			while (piece != 0) {
				addMove(MoveUtil.createWhitePawnMove(Long.numberOfTrailingZeros(piece)));
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions >>> 16) & Bitboard.RANK_2;
			while (piece != 0) {
				if ((cb.emptySpaces & (Long.lowestOneBit(piece) << 8)) != 0) {
					addMove(MoveUtil.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)));
				}
				piece &= piece - 1;
			}
		} else {
			// 1-move
			long piece = pawns & (possiblePositions << 8) & Bitboard.RANK_34567;
			while (piece != 0) {
				addMove(MoveUtil.createBlackPawnMove(Long.numberOfTrailingZeros(piece)));
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions << 16) & Bitboard.RANK_7;
			while (piece != 0) {
				if ((cb.emptySpaces & (Long.lowestOneBit(piece) >>> 8)) != 0) {
					addMove(MoveUtil.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)));
				}
				piece &= piece - 1;
			}
		}
	}

	private void addKingMoves(final ChessBoard cb) {
		
		if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves");
		
		final int fromIndex = cb.kingIndex[cb.colorToMove];
		final long occupiedWithoutKing = cb.allPieces ^ Util.POWER_LOOKUP[fromIndex];
		final long[] enemyPieces = cb.pieces[cb.colorToMoveInverse];
		final int enemyMajorPieces = MaterialUtil.getMajorPieces(cb.materialKey, cb.colorToMoveInverse);
		
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.emptySpaces;
		
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			if (!CheckUtil.isInCheckIncludingKing(toIndex, cb.colorToMove, enemyPieces, occupiedWithoutKing, enemyMajorPieces)) {
				addMove(MoveUtil.createMove(fromIndex, toIndex, KING));
			}
			moves &= moves - 1;
		}

		
		// castling
		if (cb.checkingPieces == 0) {
			
			if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: cb.colorToMove=" + cb.colorToMove + ", cb.castlingRights=" + cb.castlingRights + ", cb.castlingConfig=" + cb.castlingConfig);
			
			long castlingIndexes = CastlingUtil.getCastlingIndexes(cb.colorToMove, cb.castlingRights, cb.castlingConfig);
			
			if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: castlingIndexes=" + castlingIndexes);
			
			while (castlingIndexes != 0) {
				
				final int toIndex_king = Long.numberOfTrailingZeros(castlingIndexes);
				
				if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: toIndex_king=" + toIndex_king);
				
				// no piece in between and king does not pass through check?
				if (CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) {
					
					addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king));
				}
				
				castlingIndexes &= castlingIndexes - 1;
			}
		}
	}

	private void addKingAttacks(final ChessBoard cb) {
		final int fromIndex = cb.kingIndex[cb.colorToMove];
		final long occupiedWithoutKing = cb.allPieces ^ Util.POWER_LOOKUP[fromIndex];
		final long[] enemyPieces = cb.pieces[cb.colorToMoveInverse];
		final int enemyMajorPieces = MaterialUtil.getMajorPieces(cb.materialKey, cb.colorToMoveInverse);
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.friendlyPieces[cb.colorToMoveInverse];
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			if (!CheckUtil.isInCheckIncludingKing(toIndex, cb.colorToMove, enemyPieces, occupiedWithoutKing, enemyMajorPieces)) {
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, KING, cb.pieceIndexes[toIndex]));
			}
			moves &= moves - 1;
		}
	}

	private void addNightAttacks(long piece, final int[] pieceIndexes, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, NIGHT, pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addEpAttacks(final ChessBoard cb) {
		if (cb.epIndex == 0) {
			return;
		}
		long piece = cb.pieces[cb.colorToMove][PAWN] & StaticMoves.PAWN_ATTACKS[cb.colorToMoveInverse][cb.epIndex];
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			if (cb.isLegalEPMove(fromIndex)) {
				addMove(MoveUtil.createEPMove(fromIndex, cb.epIndex));
			}
			piece &= piece - 1;
		}
	}

	private void addPromotionMove(final int fromIndex, final int toIndex) {
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex));
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex));
		if (EngineConstants.GENERATE_BR_PROMOTIONS) {
			addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex));
			addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex));
		}
	}

	private void addPromotionAttacks(long moves, final int fromIndex, final int[] pieceIndexes) {
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex, pieceIndexes[toIndex]));
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex, pieceIndexes[toIndex]));
			if (EngineConstants.GENERATE_BR_PROMOTIONS) {
				addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex, pieceIndexes[toIndex]));
				addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex, pieceIndexes[toIndex]));
			}
			moves &= moves - 1;
		}
	}
	
	

}
