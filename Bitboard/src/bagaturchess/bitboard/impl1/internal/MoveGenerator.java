package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import java.util.Random;

import bagaturchess.bitboard.api.IHistoryProvider;
import bagaturchess.bitboard.common.Properties;


public final class MoveGenerator {
	
	
	private final int[] moves 									= new int[30000];
	private final long[] moveScores 							= new long[30000];
	private final int[] nextToGenerate 							= new int[EngineConstants.MAX_PLIES * 2];
	private final int[] nextToMove 								= new int[EngineConstants.MAX_PLIES * 2];
	private int currentPly;

	private long counter_sorting;
	
	private Random randomizer = new Random();
	
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
		
		//In order to increase the effect of nondeterminism, ensure first ordering is randomized.
		if (counter_sorting == 0 || counter_sorting % 10 == 0) {
				
			randomize(moveScores, moves, start_index, end_index);
		}
		
		
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
		
		
		//The ELO is smaller with this code enabled. It looks like it is better when all threads start searching the TT move.
		//My explanation is that, they do enough randomization (in this method: randomize(...) before sorting) of the moves with the same scores, so they work in different sub-trees anyway and 
		//as the TT move is most probably the best one, the SMP version goes a few moves deeper for the same time.
		if (false && currentPly == 1) {
		
			//Lazy SMP logic
			//currentPly == 1 (not 0), because first we make currentPly++ and then call sort method
			
			int current_moves_count = (end_index - start_index + 1);
			
			if (current_moves_count >= 2) {
				
				int index_to_swap = root_search_first_move_index % current_moves_count;
				
				long score 									= moveScores[start_index + index_to_swap];
				int move 									= moves[start_index + index_to_swap];
				
				moveScores[start_index + index_to_swap] 	= moveScores[start_index];
				moves[start_index + index_to_swap] 			= moves[start_index];
				
				moveScores[start_index] 					= score;
				moves[start_index] 							= move;
			}
		}
		
		
		counter_sorting++;
	}
	
	
	private void randomize(long[] arr1, int[] arr2, int start, int end) {
		
	    for (int i = end; i > start + 1; i--) {
	    	
	    	int rnd_index = start + randomizer.nextInt(i - start);	    
	    	
	    	long tmp1 = arr1[i-1];
	    	arr1[i-1] = arr1[rnd_index];
	    	arr1[rnd_index] = tmp1;
	    	
	    	int tmp2 = arr2[i-1];
	    	arr2[i-1] = arr2[rnd_index];
	    	arr2[rnd_index] = tmp2;
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

		switch (Long.bitCount(cb.checkingPieces)) {
		case 0:
			// not in-check
			generateNotInCheckMoves(cb);
			break;
		case 1:
			// in-check
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(cb.checkingPieces)]) {
			case PAWN:
				// fall-through
			case NIGHT:
				// move king
				addKingMoves(cb);
				break;
			default:
				generateOutOfSlidingCheckMoves(cb);
			}
			break;
		default:
			// double check, only the king can move
			addKingMoves(cb);
		}
	}

	public void generateAttacks(final ChessBoard cb) {

		switch (Long.bitCount(cb.checkingPieces)) {
		case 0:
			// not in-check
			generateNotInCheckAttacks(cb);
			break;
		case 1:
			generateOutOfCheckAttacks(cb);
			break;
		default:
			// double check, only the king can attack
			addKingAttacks(cb);
		}
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
		while (piece != 0) {
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
			case PAWN:
				addPawnMoves(Long.lowestOneBit(piece), cb,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case BISHOP:
				addBishopMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case ROOK:
				addRookMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case QUEEN:
				addQueenMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
			}
			piece &= piece - 1;
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
		while (piece != 0) {
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
			case PAWN:
				addPawnAttacksAndPromotions(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]], 0);
				break;
			case BISHOP:
				addBishopAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case ROOK:
				addRookAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case QUEEN:
				addQueenAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
			}
			piece &= piece - 1;
		}

	}

	private void generateOutOfCheckAttacks(final ChessBoard cb) {
		// attack attacker
		addEpAttacks(cb);
		addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, cb.checkingPieces, cb.emptySpaces);
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
		
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.emptySpaces;
		
		while (moves != 0) {
			
			addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), KING));
			
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
				
				// no piece in between?
				if (CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) {
					
					addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king));
				}
				
				castlingIndexes &= castlingIndexes - 1;
			}
		}
	}

	private void addKingAttacks(final ChessBoard cb) {
		final int fromIndex = cb.kingIndex[cb.colorToMove];
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.friendlyPieces[cb.colorToMoveInverse];
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createAttackMove(fromIndex, toIndex, KING, cb.pieceIndexes[toIndex]));
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
			addMove(MoveUtil.createEPMove(Long.numberOfTrailingZeros(piece), cb.epIndex));
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
