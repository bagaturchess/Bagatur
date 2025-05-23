package bagaturchess.bitboard.impl2;


import java.util.Arrays;

import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.Zobrist;


public class ChessBoard {

	/**
	 * Bitboards
	 */
	public long w_king;
	public long w_queens;
	public long w_rooks;
	public long w_bishops;
	public long w_knights;
	public long w_pawns;
	public long w_all;
	
	public long b_king;
	public long b_queens;
	public long b_rooks;
	public long b_bishops;
	public long b_knights;
	public long b_pawns;
	public long b_all;
	
	public long all_pieces;
	public long empty_spaces;
	
	public long pinned_pieces;
	public long discovered_pieces;
	public long checking_pieces;
	
	/**
	 * Other fields
	 */
	public int color_to_move;
	public int ep_index;
	
	public int[] piece_indexes;
	
	/** 4 bits: white-king,white-queen,black-king,black-queen */
	public int castling_rights;
	
	public CastlingConfig castling_config;
	
	public int last_capture_or_pawn_move_before;

	public StackLongInt played_board_states = new StackLongInt(9631);//MUST BE PRIME NUMBER
	
	public long zobrist_key;
	
	public int played_moves_count;
	
	private BoardState[] states = new BoardState[2048];
	
	public ChessBoard() {
		
		piece_indexes = new int[64];
		
		for (int i = 0; i < states.length; i++) {
			
			states[i] = new BoardState();
		}		
	}
	
	
	public long getPieces(int color, int type) {
		
		switch(color) {
			case ChessConstants.WHITE:
				switch(type) {
					case ChessConstants.PAWN:
						return w_pawns;
					case ChessConstants.KNIGHT:
						return w_knights;
					case ChessConstants.BISHOP:
						return w_bishops;
					case ChessConstants.ROOK:
						return w_rooks;
					case ChessConstants.QUEEN:
						return w_queens;
					case ChessConstants.KING:
						return w_king;
					default:
						throw new IllegalStateException("type=" + type);
				}
			case ChessConstants.BLACK:
				switch(type) {
					case ChessConstants.PAWN:
						return b_pawns;
					case ChessConstants.KNIGHT:
						return b_knights;
					case ChessConstants.BISHOP:
						return b_bishops;
					case ChessConstants.ROOK:
						return b_rooks;
					case ChessConstants.QUEEN:
						return b_queens;
					case ChessConstants.KING:
						return b_king;
					default:
						throw new IllegalStateException("type=" + type);
				}
			default:
				throw new IllegalStateException("color=" + color);
		}
	}
	
	
	public void setPieces(int color, int type, long bitboard) {
		switch(color) {
			case ChessConstants.WHITE:
				switch(type) {
					case ChessConstants.PAWN:
						w_pawns = bitboard;
						break;
					case ChessConstants.KNIGHT:
						w_knights = bitboard;
						break;
					case ChessConstants.BISHOP:
						w_bishops = bitboard;
						break;
					case ChessConstants.ROOK:
						w_rooks = bitboard;
						break;
					case ChessConstants.QUEEN:
						w_queens = bitboard;
						break;
					case ChessConstants.KING:
						w_king = bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			case ChessConstants.BLACK:
				switch(type) {
					case ChessConstants.PAWN:
						b_pawns = bitboard;
						break;
					case ChessConstants.KNIGHT:
						b_knights = bitboard;
						break;
					case ChessConstants.BISHOP:
						b_bishops = bitboard;
						break;
					case ChessConstants.ROOK:
						b_rooks = bitboard;
						break;
					case ChessConstants.QUEEN:
						b_queens = bitboard;
						break;
					case ChessConstants.KING:
						b_king = bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			default:
				throw new IllegalStateException("color=" + color);
		}
	}
	
	
	public void xorPieces(int color, int type, long bitboard) {
		switch(color) {
			case ChessConstants.WHITE:
				switch(type) {
					case ChessConstants.PAWN:
						w_pawns ^= bitboard;
						break;
					case ChessConstants.KNIGHT:
						w_knights ^= bitboard;
						break;
					case ChessConstants.BISHOP:
						w_bishops ^= bitboard;
						break;
					case ChessConstants.ROOK:
						w_rooks ^= bitboard;
						break;
					case ChessConstants.QUEEN:
						w_queens ^= bitboard;
						break;
					case ChessConstants.KING:
						w_king ^= bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			case ChessConstants.BLACK:
				switch(type) {
					case ChessConstants.PAWN:
						b_pawns ^= bitboard;
						break;
					case ChessConstants.KNIGHT:
						b_knights ^= bitboard;
						break;
					case ChessConstants.BISHOP:
						b_bishops ^= bitboard;
						break;
					case ChessConstants.ROOK:
						b_rooks ^= bitboard;
						break;
					case ChessConstants.QUEEN:
						b_queens ^= bitboard;
						break;
					case ChessConstants.KING:
						b_king ^= bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			default:
				throw new IllegalStateException("color=" + color);
		}
	}
	
	
	public void orPieces(int color, int type, long bitboard) {
		switch(color) {
			case ChessConstants.WHITE:
				switch(type) {
					case ChessConstants.PAWN:
						w_pawns |= bitboard;
						break;
					case ChessConstants.KNIGHT:
						w_knights |= bitboard;
						break;
					case ChessConstants.BISHOP:
						w_bishops |= bitboard;
						break;
					case ChessConstants.ROOK:
						w_rooks |= bitboard;
						break;
					case ChessConstants.QUEEN:
						w_queens |= bitboard;
						break;
					case ChessConstants.KING:
						w_king |= bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			case ChessConstants.BLACK:
				switch(type) {
					case ChessConstants.PAWN:
						b_pawns |= bitboard;
						break;
					case ChessConstants.KNIGHT:
						b_knights |= bitboard;
						break;
					case ChessConstants.BISHOP:
						b_bishops |= bitboard;
						break;
					case ChessConstants.ROOK:
						b_rooks |= bitboard;
						break;
					case ChessConstants.QUEEN:
						b_queens |= bitboard;
						break;
					case ChessConstants.KING:
						b_king |= bitboard;
						break;
					default:
						throw new IllegalStateException("type=" + type);
				}
				break;
			default:
				throw new IllegalStateException("color=" + color);
		}
	}
	
	
	public long getPiecesOfSideToMove(int type) {
		
		return getPieces(color_to_move, type);
	}
	
	
	public long getPiecesOfSideNotToMove(int type) {
		
		return getPieces(1 - color_to_move, type);
	}
	
	
	public long getPieces_All(int color) {
		
		return color == ChessConstants.WHITE ? w_all : b_all;
	}
	
	
	public long getPiecesOfSideToMove_All() {
		
		return getPieces_All(color_to_move);
	}
	
	
	public long getPiecesOfSideNotToMove_All() {
		
		return getPieces_All(1 - color_to_move);
	}
	
	
	public int getKingIndex(int color) {
		
		int index = color == ChessConstants.WHITE ? Long.numberOfTrailingZeros(w_king) : Long.numberOfTrailingZeros(b_king);
		
		if (index == 64) {
			
			throw new IllegalStateException("No king: color=" + color);
		}
		
		return index;
	}
	
	
	public int getKingIndexOfSideToMove() {
		
		return getKingIndex(color_to_move);
	}
	
	
	public int getKingIndexOfSideNotToMove() {
		
		return getKingIndex(1 - color_to_move);
	}
	
	
	public void doMove(int move) {

		if (MoveUtil.isCastlingMove(move)) {
			
			//doCastling960(move);
			
			return;
		}
		
		
		BoardState state_backup = states[played_moves_count];
		state_backup.pinned_pieces = pinned_pieces;
		state_backup.discovered_pieces = discovered_pieces;
		state_backup.checking_pieces = checking_pieces;
		state_backup.ep_index = ep_index;
		state_backup.castling_rights = castling_rights;
		state_backup.last_capture_or_pawn_move_before = last_capture_or_pawn_move_before;
		state_backup.zobrist_key = zobrist_key;
		
		played_moves_count++;
		
		
		final int fromIndex = MoveUtil.getFromIndex(move);
		int toIndex = MoveUtil.getToIndex(move);
		long toMask = 1L << toIndex;
		final long fromToMask = (1L << fromIndex) ^ toMask;
		final int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
		final int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
		
		if (fromIndex == toIndex) {
			
			throw new IllegalStateException("doMove: fromIndex == toIndex");
		}
		
		if (attackedPieceIndex != 0 || sourcePieceIndex == ChessConstants.PAWN) {
			last_capture_or_pawn_move_before = 0;
		} else {
			last_capture_or_pawn_move_before++;
		}
		
		zobrist_key ^= Zobrist.piece[fromIndex][color_to_move][sourcePieceIndex] ^ Zobrist.piece[toIndex][color_to_move][sourcePieceIndex] ^ Zobrist.sideToMove;
		if (ep_index != 0) {
			zobrist_key ^= Zobrist.epIndex[ep_index];
			ep_index = 0;
		}

		if (color_to_move == ChessConstants.WHITE) {
			w_all ^= fromToMask;
		} else {
			b_all ^= fromToMask;
		}
		xorPieces(color_to_move, sourcePieceIndex, fromToMask);
		piece_indexes[fromIndex] = ChessConstants.EMPTY;
		piece_indexes[toIndex] = sourcePieceIndex;
		
		switch (sourcePieceIndex) {
		case ChessConstants.PAWN:
			if (MoveUtil.isPromotion(move)) {
				
				xorPieces(color_to_move, ChessConstants.PAWN, toMask);
				orPieces(color_to_move, MoveUtil.getMoveType(move), toMask);
				piece_indexes[toIndex] = MoveUtil.getMoveType(move);
				zobrist_key ^= Zobrist.piece[toIndex][color_to_move][ChessConstants.PAWN] ^ Zobrist.piece[toIndex][color_to_move][MoveUtil.getMoveType(move)];
			} else {
				// 2-move
				if (ChessConstants.IN_BETWEEN[fromIndex][toIndex] != 0) {
					if ((ChessConstants.PAWN_ATTACKS[color_to_move][Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex])]
							& getPieces(1 - color_to_move, ChessConstants.PAWN)) != 0) {
						ep_index = Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex]);
						zobrist_key ^= Zobrist.epIndex[ep_index];
					}
				}
			}
			break;

		case ChessConstants.ROOK:
			
			if (castling_rights != 0) {
				
				zobrist_key ^= Zobrist.castling[castling_rights];
				
				castling_rights = CastlingUtil.getRookMovedOrAttackedCastlingRights(castling_rights, fromIndex, castling_config);
				
				zobrist_key ^= Zobrist.castling[castling_rights];
			}
			
			break;

		case ChessConstants.KING:
			
			if (castling_rights != 0) {
				
				if (MoveUtil.isCastlingMove(move)) {
					
					throw new IllegalStateException("Castling");
				}
				
				zobrist_key ^= Zobrist.castling[castling_rights];
				
				castling_rights = CastlingUtil.getKingMovedCastlingRights(castling_rights, color_to_move, castling_config);
				
				zobrist_key ^= Zobrist.castling[castling_rights];
			}
		}

		// piece hit?
		switch (attackedPieceIndex) {
		case ChessConstants.EMPTY:
			break;
		case ChessConstants.PAWN:
			if (MoveUtil.isEPMove(move)) {
				toIndex += ChessConstants.COLOR_FACTOR_8[1 - color_to_move];
				toMask = ChessConstants.POWER_LOOKUP[toIndex];
				piece_indexes[toIndex] = ChessConstants.EMPTY;
			}

			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= toMask;
			} else {
				b_all ^= toMask;
			}
			
			xorPieces(1 - color_to_move, ChessConstants.PAWN, toMask);
			
			zobrist_key ^= Zobrist.piece[toIndex][1 - color_to_move][ChessConstants.PAWN];
			
			break;
			
		case ChessConstants.ROOK:
			
			if (castling_rights != 0) {
				
				zobrist_key ^= Zobrist.castling[castling_rights];
				
				castling_rights = CastlingUtil.getRookMovedOrAttackedCastlingRights(castling_rights, toIndex, castling_config);
				
				zobrist_key ^= Zobrist.castling[castling_rights];
			}
			// fall-through
		default:
			
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= toMask;
			} else {
				b_all ^= toMask;
			}
			
			xorPieces(1 - color_to_move, attackedPieceIndex, toMask);
			
			zobrist_key ^= Zobrist.piece[toIndex][1 - color_to_move][attackedPieceIndex];
		}

		all_pieces = w_all | b_all;
		empty_spaces = ~all_pieces;


		color_to_move = 1 - color_to_move;
		

		// update checking pieces
		if (isDiscoveredMove(fromIndex)) {
			checking_pieces = CheckUtil.getCheckingPieces(this);
		} else {
			if (MoveUtil.isNormalMove(move)) {
				checking_pieces = CheckUtil.getCheckingPieces(this, sourcePieceIndex);
			} else {
				checking_pieces = CheckUtil.getCheckingPieces(this);
			}
		}

		setPinnedAndDiscoPieces();
		
		played_board_states.inc(zobrist_key);
	}

	
	/*private void doCastling960(int move) {
		
		
		final int fromIndex_king 	= MoveUtil.getFromIndex(move);
		final int toIndex_king 		= MoveUtil.getToIndex(move);
		final int sourcePieceIndex 	= MoveUtil.getSourcePieceIndex(move);
		
		if (sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)) {
			
			throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
		}
		
		CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, buff_castling_rook_from_to);
		final int fromIndex_rook 	= buff_castling_rook_from_to[0];
		final int toIndex_rook 		= buff_castling_rook_from_to[1];
		
		
		if (fromIndex_king == toIndex_king) {
			
			long bb 						= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			
			pieces[colorToMove][ROOK] 		^= bb;
			friendlyPieces[colorToMove] 	^= bb;
			
			pieceIndexes[fromIndex_rook] 	= EMPTY;
			pieceIndexes[toIndex_rook] 		= ROOK;
			
		} else if (fromIndex_rook == toIndex_rook) {
			
			long bb 						= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			
			pieces[colorToMove][KING] 		^= bb;
			friendlyPieces[colorToMove] 	^= bb;
			
			pieceIndexes[fromIndex_king] 	= EMPTY;
			pieceIndexes[toIndex_king] 		= KING;
			
		} else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
		
			long bb_king					= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMove][KING] 		^= bb_king;
			
			long bb_rook					= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMove][ROOK] 		^= bb_rook;
			
			pieceIndexes[toIndex_rook] 		= ROOK;
			pieceIndexes[toIndex_king] 		= KING;
			
		} else if (fromIndex_rook == toIndex_king) {
			
			long bb_king					= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMove][KING] 		^= bb_king;
			
			long bb_rook					= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMove][ROOK] 		^= bb_rook;
			
			friendlyPieces[colorToMove] 	^= (Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_rook]);
			
			pieceIndexes[toIndex_rook] 		= ROOK;
			pieceIndexes[toIndex_king] 		= KING;
			pieceIndexes[fromIndex_king] 	= EMPTY;

			
		} else if (toIndex_rook == fromIndex_king) {
			
			long bb_king					= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMove][KING] 		^= bb_king;
			
			long bb_rook					= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMove][ROOK] 		^= bb_rook;
			
			friendlyPieces[colorToMove] 	^= (Util.POWER_LOOKUP[toIndex_king] | Util.POWER_LOOKUP[fromIndex_rook]);
			
			pieceIndexes[toIndex_rook] 		= ROOK;
			pieceIndexes[toIndex_king] 		= KING;
			pieceIndexes[fromIndex_rook] 	= EMPTY;
			
		} else {
			
			long bb_king					= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMove][KING] 		^= bb_king;
			friendlyPieces[colorToMove] 	^= bb_king;
			
			long bb_rook					= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMove][ROOK] 		^= bb_rook;
			friendlyPieces[colorToMove] 	^= bb_rook;
			
			pieceIndexes[fromIndex_rook] 	= EMPTY;
			pieceIndexes[fromIndex_king] 	= EMPTY;
			pieceIndexes[toIndex_rook] 		= ROOK;
			pieceIndexes[toIndex_king] 		= KING;
			
		}
		
		
		updateKingValues(colorToMove, toIndex_king);
		
		zobristKey 						^= Zobrist.piece[fromIndex_king][colorToMove][KING] ^ Zobrist.piece[toIndex_king][colorToMove][KING];
		zobristKey 						^= Zobrist.piece[fromIndex_rook][colorToMove][ROOK] ^ Zobrist.piece[toIndex_rook][colorToMove][ROOK];
		
		if (epIndex != 0) {
			zobristKey ^= Zobrist.epIndex[epIndex];
			epIndex = 0;
		}
		
		zobristKey ^= Zobrist.sideToMove;
		
		
		if (castlingRights != 0) {
			
			zobristKey ^= Zobrist.castling[castlingRights];
			
			if (Properties.DUMP_CASTLING) System.out.println("ChessBoard.doMove/KING: castlingRights=" + castlingRights);
			
			castlingRights = CastlingUtil.getKingMovedCastlingRights(castlingRights, colorToMove, castlingConfig);
			
			if (Properties.DUMP_CASTLING) System.out.println("ChessBoard.doMove/KING: NEW castlingRights=" + castlingRights);
			
			zobristKey ^= Zobrist.castling[castlingRights];
		}
		
		
		psqtScore_mg					+= EvalConstants.PSQT_MG[KING][colorToMove][toIndex_king] - EvalConstants.PSQT_MG[KING][colorToMove][fromIndex_king];
		psqtScore_eg 					+= EvalConstants.PSQT_EG[KING][colorToMove][toIndex_king] - EvalConstants.PSQT_EG[KING][colorToMove][fromIndex_king];
		
		psqtScore_mg					+= EvalConstants.PSQT_MG[ROOK][colorToMove][toIndex_rook] - EvalConstants.PSQT_MG[ROOK][colorToMove][fromIndex_rook];
		psqtScore_eg 					+= EvalConstants.PSQT_EG[ROOK][colorToMove][toIndex_rook] - EvalConstants.PSQT_EG[ROOK][colorToMove][fromIndex_rook];
		
		
		allPieces = friendlyPieces[colorToMove] | friendlyPieces[colorToMoveInverse];
		
		emptySpaces = ~allPieces;
		
		changeSideToMove();

		// update checking pieces
		checkingPieces = CheckUtil.getCheckingPieces(this);
		
		// TODO can this be done incrementally?
		setPinnedAndDiscoPieces();

		if (EngineConstants.ASSERT) {
			
			ChessBoardTestUtil.testValues(this);
		}
		
		
		playedBoardStates.inc(zobristKey);
	}*/
	

	public void undoMove(int move) {
		
		
		if (MoveUtil.isCastlingMove(move)) {
			
			//undoCastling960(move);
			
			return;
		}
		
		
		played_board_states.dec(zobrist_key);
		
		
		played_moves_count--;
		
		BoardState state_backup = states[played_moves_count];
		pinned_pieces = state_backup.pinned_pieces;
		discovered_pieces = state_backup.discovered_pieces;
		checking_pieces = state_backup.checking_pieces;
		ep_index = state_backup.ep_index;
		castling_rights = state_backup.castling_rights;
		last_capture_or_pawn_move_before = state_backup.last_capture_or_pawn_move_before;
		zobrist_key = state_backup.zobrist_key;
		
		
		final int fromIndex = MoveUtil.getFromIndex(move);
		int toIndex = MoveUtil.getToIndex(move);
		long toMask = 1L << toIndex;
		final long fromToMask = (1L << fromIndex) ^ toMask;
		final int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
		final int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);

		if (fromIndex == toIndex) {
			
			throw new IllegalStateException("undoMove: fromIndex == toIndex");
		}
		
		
		// undo move
		if (1 - color_to_move == ChessConstants.WHITE) {
			w_all ^= fromToMask;
		} else {
			b_all ^= fromToMask;
		}
		xorPieces(1 - color_to_move, sourcePieceIndex, fromToMask);

		piece_indexes[fromIndex] = sourcePieceIndex;
		
		switch (sourcePieceIndex) {
		case ChessConstants.EMPTY:
			// not necessary but provides a table-index
			break;
		case ChessConstants.PAWN:
			if (MoveUtil.isPromotion(move)) {

				xorPieces(1 - color_to_move, ChessConstants.PAWN, toMask);
				xorPieces(1 - color_to_move, MoveUtil.getMoveType(move), toMask);
			}
			break;
			
		case ChessConstants.KING:
			
			if (MoveUtil.isCastlingMove(move)) {
				
				throw new IllegalStateException("Castling");
			}
		}

		// undo hit
		switch (attackedPieceIndex) {
		case ChessConstants.EMPTY:
			break;
		case ChessConstants.PAWN:
			if (MoveUtil.isEPMove(move)) {
				piece_indexes[toIndex] = ChessConstants.EMPTY;
				toIndex += ChessConstants.COLOR_FACTOR_8[color_to_move];
				toMask = ChessConstants.POWER_LOOKUP[toIndex];
			}
			
			orPieces(color_to_move, attackedPieceIndex, toMask);
			
			if (color_to_move == ChessConstants.WHITE) {
				w_all |= toMask;
			} else {
				b_all |= toMask;
			}
			
			break;
		default:

			orPieces(color_to_move, attackedPieceIndex, toMask);

			if (color_to_move == ChessConstants.WHITE) {
				w_all |= toMask;
			} else {
				b_all |= toMask;
			}
		}

		piece_indexes[toIndex] = attackedPieceIndex;
		all_pieces = w_all | b_all;
		empty_spaces = ~all_pieces;

		color_to_move = 1 - color_to_move;
	}
	
	
	/*public void undoCastling960(int move) {
		
		
		played_board_states.dec(zobrist_key);
		
		
		final int fromIndex_king 	= MoveUtil.getFromIndex(move);
		final int toIndex_king 		= MoveUtil.getToIndex(move);
		final int sourcePieceIndex 	= MoveUtil.getSourcePieceIndex(move);
		
		if (sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)) {
			
			throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
		}
		
		CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, buff_castling_rook_from_to);
		final int fromIndex_rook 	= buff_castling_rook_from_to[0];
		final int toIndex_rook 		= buff_castling_rook_from_to[1];
		
		
		if (fromIndex_king == toIndex_king) {
			
			long bb 							= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			
			pieces[colorToMoveInverse][ROOK] 	^= bb;
			friendlyPieces[colorToMoveInverse] 	^= bb;
			
			pieceIndexes[fromIndex_rook] 		= ROOK;
			pieceIndexes[toIndex_rook] 			= EMPTY;
			
		} else if (fromIndex_rook == toIndex_rook) {
			
			long bb 							= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			
			pieces[colorToMoveInverse][KING] 	^= bb;
			friendlyPieces[colorToMoveInverse] 	^= bb;
			
			pieceIndexes[fromIndex_king] 		= KING;
			pieceIndexes[toIndex_king] 			= EMPTY;
			
		} else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
		
			long bb_king						= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMoveInverse][KING] 	^= bb_king;
			
			long bb_rook						= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMoveInverse][ROOK] 	^= bb_rook;
			
			pieceIndexes[toIndex_rook] 			= KING;
			pieceIndexes[toIndex_king] 			= ROOK;
			
		} else if (fromIndex_rook == toIndex_king) {
			
			long bb_king						= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMoveInverse][KING] 	^= bb_king;
			
			long bb_rook						= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMoveInverse][ROOK] 	^= bb_rook;
			
			friendlyPieces[colorToMoveInverse] 	^= (Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_rook]);
			
			pieceIndexes[toIndex_rook] 			= EMPTY;
			pieceIndexes[toIndex_king] 			= ROOK;
			pieceIndexes[fromIndex_king] 		= KING;

			
		} else if (toIndex_rook == fromIndex_king) {
			
			long bb_king						= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMoveInverse][KING] 	^= bb_king;
			
			long bb_rook						= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMoveInverse][ROOK] 	^= bb_rook;
			
			friendlyPieces[colorToMoveInverse] 	^= (Util.POWER_LOOKUP[toIndex_king] | Util.POWER_LOOKUP[fromIndex_rook]);
			
			pieceIndexes[toIndex_rook] 			= KING;
			pieceIndexes[toIndex_king] 			= EMPTY;
			pieceIndexes[fromIndex_rook] 		= ROOK;
			
		} else {
			
			long bb_king						= Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
			pieces[colorToMoveInverse][KING] 	^= bb_king;
			friendlyPieces[colorToMoveInverse] 	^= bb_king;
			
			long bb_rook						= Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
			pieces[colorToMoveInverse][ROOK] 	^= bb_rook;
			friendlyPieces[colorToMoveInverse] 	^= bb_rook;
			
			pieceIndexes[fromIndex_rook] 		= ROOK;
			pieceIndexes[fromIndex_king] 		= KING;
			pieceIndexes[toIndex_rook] 			= EMPTY;
			pieceIndexes[toIndex_king] 			= EMPTY;
			
		}
		
		
		updateKingValues(colorToMoveInverse, fromIndex_king);
		
				
		psqtScore_mg					+= EvalConstants.PSQT_MG[KING][colorToMoveInverse][fromIndex_king] - EvalConstants.PSQT_MG[KING][colorToMoveInverse][toIndex_king];
		psqtScore_eg 					+= EvalConstants.PSQT_EG[KING][colorToMoveInverse][fromIndex_king] - EvalConstants.PSQT_EG[KING][colorToMoveInverse][toIndex_king];
		
		psqtScore_mg					+= EvalConstants.PSQT_MG[ROOK][colorToMoveInverse][fromIndex_rook] - EvalConstants.PSQT_MG[ROOK][colorToMoveInverse][toIndex_rook];
		psqtScore_eg 					+= EvalConstants.PSQT_EG[ROOK][colorToMoveInverse][fromIndex_rook] - EvalConstants.PSQT_EG[ROOK][colorToMoveInverse][toIndex_rook];
		
		
		allPieces = friendlyPieces[colorToMove] | friendlyPieces[colorToMoveInverse];
		
		emptySpaces = ~allPieces;
		
		changeSideToMove();

		if (EngineConstants.ASSERT) {
			
			ChessBoardTestUtil.testValues(this);
		}
	}*/
	
	
	public void setPinnedAndDiscoPieces() {

		pinned_pieces = 0;
		discovered_pieces = 0;

		for (int kingColor = ChessConstants.WHITE; kingColor <= ChessConstants.BLACK; kingColor++) {

			int enemyColor = 1 - kingColor;

			if ((getPieces(enemyColor, ChessConstants.BISHOP)
					| getPieces(enemyColor, ChessConstants.ROOK)
					| getPieces(enemyColor, ChessConstants.QUEEN)) == 0) {
				continue;
			}

			int kingIndex = getKingIndex(kingColor);
			
			long enemyPiece = (getPieces(enemyColor, ChessConstants.BISHOP) | getPieces(enemyColor, ChessConstants.QUEEN)) & MagicUtil.getBishopMovesEmptyBoard(kingIndex)
					| (getPieces(enemyColor, ChessConstants.ROOK) | getPieces(enemyColor, ChessConstants.QUEEN)) & MagicUtil.getRookMovesEmptyBoard(kingIndex);
			while (enemyPiece != 0) {
				final long checkedPiece = ChessConstants.IN_BETWEEN[kingIndex][Long.numberOfTrailingZeros(enemyPiece)] & all_pieces;
				if (Long.bitCount(checkedPiece) == 1) {
					pinned_pieces |= checkedPiece & getPieces_All(kingColor);
					discovered_pieces |= checkedPiece & getPieces_All(enemyColor);
				}
				enemyPiece &= enemyPiece - 1;
			}

		}
	}
	
	
	private boolean isDiscoveredMove(final int fromIndex) {
		return (discovered_pieces & (1L << fromIndex)) != 0;
	}
	
	
	public boolean isValidMove(final int move) {
		
		// check piece at from square
		final int fromIndex = MoveUtil.getFromIndex(move);
		final long fromSquare = ChessConstants.POWER_LOOKUP[fromIndex];
		if ((getPiecesOfSideToMove(MoveUtil.getSourcePieceIndex(move)) & fromSquare) == 0) {
			return false;
		}

		// check piece at to square
		final int toIndex = MoveUtil.getToIndex(move);
		final long toSquare = ChessConstants.POWER_LOOKUP[toIndex];
		final int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
		if (attackedPieceIndex == 0) {
			if (MoveUtil.isCastlingMove(move)) {
				//The rook or king can be on the to_square
				if (piece_indexes[toIndex] != ChessConstants.EMPTY && piece_indexes[toIndex] != ChessConstants.ROOK && piece_indexes[toIndex] != ChessConstants.KING) {
					return false;
				}
			} else {
				if (piece_indexes[toIndex] != ChessConstants.EMPTY) {
					return false;
				}
			}
		} else {
			if ((getPiecesOfSideNotToMove(attackedPieceIndex) & toSquare) == 0 && !MoveUtil.isEPMove(move)) {
				return false;
			}
		}


		// check if move is possible
		switch (MoveUtil.getSourcePieceIndex(move)) {
		case ChessConstants.PAWN:
			if (MoveUtil.isEPMove(move)) {
				if (toIndex != ep_index) {
					return false;
				}
				return isLegalEPMove(fromIndex);
			} else {
				if (color_to_move == ChessConstants.WHITE) {
					if (fromIndex > toIndex) {
						return false;
					}
					// 2-move
					if (toIndex - fromIndex == 16 && (all_pieces & ChessConstants.POWER_LOOKUP[fromIndex + 8]) != 0) {
						return false;
					}
				} else {
					if (fromIndex < toIndex) {
						return false;
					}
					// 2-move
					if (fromIndex - toIndex == 16 && (all_pieces & ChessConstants.POWER_LOOKUP[fromIndex - 8]) != 0) {
						return false;
					}
				}
			}
			break;
		case ChessConstants.KNIGHT:
			break;
		case ChessConstants.BISHOP:
			// fall-through
		case ChessConstants.ROOK:
			// fall-through
		case ChessConstants.QUEEN:
			if ((ChessConstants.IN_BETWEEN[fromIndex][toIndex] & all_pieces) != 0) {
				return false;
			}
			break;
			
		case ChessConstants.KING:
			
			if (MoveUtil.isCastlingMove(move)) {
				
				long castlingIndexes = CastlingUtil.getCastlingIndexes(color_to_move, castling_rights, castling_config);
				
				while (castlingIndexes != 0) {
					
					if (toIndex == Long.numberOfTrailingZeros(castlingIndexes)) {
						
						return CastlingUtil.isValidCastlingMove(this, fromIndex, toIndex);
					}
					
					castlingIndexes &= castlingIndexes - 1;
				}
				
				return false;
			}
			
			return isLegalKingMove(move);
		}

		if ((fromSquare & pinned_pieces) != 0) {
			if ((ChessConstants.PINNED_MOVEMENT[fromIndex][getKingIndexOfSideToMove()] & toSquare) == 0) {
				return false;
			}
		}

		if (checking_pieces != 0) {
			if (attackedPieceIndex == 0) {
				return isLegalNonKingMove(move);
			} else {
				if (Long.bitCount(checking_pieces) >= 2) {
					return false;
				}
				return (toSquare & checking_pieces) != 0;
			}
		}

		return true;
	}
	
	
	private boolean isLegalKingMove(final int move) {
		return !CheckUtil.isInCheckIncludingKing(this, MoveUtil.getToIndex(move), color_to_move,
				all_pieces ^ ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)]);
	}
	
	
	private boolean isLegalNonKingMove(final int move) {
		return !CheckUtil.isInCheck(this, getKingIndexOfSideToMove(), color_to_move,
				all_pieces ^ ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ ChessConstants.POWER_LOOKUP[MoveUtil.getToIndex(move)]);
	}
	
	
	private boolean isLegalEPMove(final int fromIndex) {

		// do move, check if in check, undo move. slow but also not called very often

		final long fromToMask = ChessConstants.POWER_LOOKUP[fromIndex] ^ ChessConstants.POWER_LOOKUP[ep_index];

		// do-move and hit
		if (color_to_move == ChessConstants.WHITE) {
			w_all ^= fromToMask;
		} else {
			b_all ^= fromToMask;
		}
		xorPieces(1 - color_to_move, ChessConstants.PAWN, ChessConstants.POWER_LOOKUP[ep_index + ChessConstants.COLOR_FACTOR_8[1 - color_to_move]]);
		all_pieces = w_all | b_all ^ ChessConstants.POWER_LOOKUP[ep_index + ChessConstants.COLOR_FACTOR_8[1 - color_to_move]];

		/* Check if is in check */
		final boolean isInCheck = CheckUtil.getCheckingPieces(this) != 0;

		// undo-move and hit
		if (color_to_move == ChessConstants.WHITE) {
			w_all ^= fromToMask;
		} else {
			b_all ^= fromToMask;
		}
		xorPieces(1 - color_to_move, ChessConstants.PAWN, ChessConstants.POWER_LOOKUP[ep_index + ChessConstants.COLOR_FACTOR_8[1 - color_to_move]]);
		all_pieces = w_all | b_all;

		return !isInCheck;

	}
	
	
	public int getRepetition() {
		int count = played_board_states.get(zobrist_key);
		if (count == StackLongInt.NO_VALUE) {
			return 0;
		} else return count;
	}
	
	
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChessBoard that = (ChessBoard) obj;

        return w_king == that.w_king &&
                w_queens == that.w_queens &&
                w_rooks == that.w_rooks &&
                w_bishops == that.w_bishops &&
                w_knights == that.w_knights &&
                w_pawns == that.w_pawns &&
                w_all == that.w_all &&
                b_king == that.b_king &&
                b_queens == that.b_queens &&
                b_rooks == that.b_rooks &&
                b_bishops == that.b_bishops &&
                b_knights == that.b_knights &&
                b_pawns == that.b_pawns &&
                b_all == that.b_all &&
                all_pieces == that.all_pieces &&
                empty_spaces == that.empty_spaces &&
                pinned_pieces == that.pinned_pieces &&
                discovered_pieces == that.discovered_pieces &&
                checking_pieces == that.checking_pieces &&
                color_to_move == that.color_to_move &&
                ep_index == that.ep_index &&
                castling_rights == that.castling_rights &&
                last_capture_or_pawn_move_before == that.last_capture_or_pawn_move_before &&
                played_moves_count == that.played_moves_count &&
                zobrist_key == that.zobrist_key &&
                Arrays.equals(piece_indexes, that.piece_indexes)
                ;
    }
    
    
    @Override
    public ChessBoard clone() {
    	
        ChessBoard clone = new ChessBoard(); //(ChessBoard) super.clone();

        clone.w_king = this.w_king;
        clone.w_queens = this.w_queens;
        clone.w_rooks = this.w_rooks;
        clone.w_bishops = this.w_bishops;
        clone.w_knights = this.w_knights;
        clone.w_pawns = this.w_pawns;
        clone.w_all = this.w_all;

        clone.b_king = this.b_king;
        clone.b_queens = this.b_queens;
        clone.b_rooks = this.b_rooks;
        clone.b_bishops = this.b_bishops;
        clone.b_knights = this.b_knights;
        clone.b_pawns = this.b_pawns;
        clone.b_all = this.b_all;

        clone.all_pieces = this.all_pieces;
        clone.empty_spaces = this.empty_spaces;
        clone.pinned_pieces = this.pinned_pieces;
        clone.discovered_pieces = this.discovered_pieces;
        clone.checking_pieces = this.checking_pieces;

        clone.color_to_move = this.color_to_move;
        clone.ep_index = this.ep_index;
        clone.castling_rights = this.castling_rights;
        clone.last_capture_or_pawn_move_before = this.last_capture_or_pawn_move_before;
        clone.played_moves_count = this.played_moves_count;
        clone.zobrist_key = this.zobrist_key;

        clone.piece_indexes = this.piece_indexes.clone();
        
        return clone;
    }

    
    private static class BoardState {
    	
    	public long pinned_pieces;
    	public long discovered_pieces;
    	public long checking_pieces;
    	
    	public int ep_index;
    	public int castling_rights;
    	public int last_capture_or_pawn_move_before;
    	
    	public long zobrist_key;
    }
}
