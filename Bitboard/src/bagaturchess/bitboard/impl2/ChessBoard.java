package bagaturchess.bitboard.impl2;


import java.util.Arrays;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.bitboard.impl1.internal.Zobrist;


public class ChessBoard implements IBitBoard {

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
	
	private int[] played_moves = new int[2048];
	
	private int[] buff_castling_rook_from_to = new int[2];
	
	private IMoveList hasMovesList = new BaseMoveList(333);
	
	private IMoveOps moveOps = new MoveOpsImpl();
	
	private IMaterialFactor materialFactor = new MaterialFactorImpl();
	
	private IPiecesLists pieces = new PiecesListsImpl(this);
	
	private MoveListener[] move_listeners = new MoveListener[0];
	
	private boolean isFRC = true;
	
	
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
			
			doCastling960(move);
			
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
		played_moves[played_moves_count] = move;
		
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

	
	private void doCastling960(int move) {
		
		
		BoardState state_backup = states[played_moves_count];
		state_backup.pinned_pieces = pinned_pieces;
		state_backup.discovered_pieces = discovered_pieces;
		state_backup.checking_pieces = checking_pieces;
		state_backup.ep_index = ep_index;
		state_backup.castling_rights = castling_rights;
		state_backup.last_capture_or_pawn_move_before = last_capture_or_pawn_move_before;
		state_backup.zobrist_key = zobrist_key;
		played_moves[played_moves_count] = move;
		
		played_moves_count++;
		
		
		final int fromIndex_king 	= MoveUtil.getFromIndex(move);
		final int toIndex_king 		= MoveUtil.getToIndex(move);
		final int sourcePieceIndex 	= MoveUtil.getSourcePieceIndex(move);
		
		if (sourcePieceIndex != ChessConstants.KING || !MoveUtil.isCastlingMove(move)) {
			
			throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
		}
		
		CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, buff_castling_rook_from_to);
		final int fromIndex_rook 	= buff_castling_rook_from_to[0];
		final int toIndex_rook 		= buff_castling_rook_from_to[1];
		
		
		if (fromIndex_king == toIndex_king) {
			
			long bb 						= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= bb;
			} else {
				b_all ^= bb;
			}
			xorPieces(color_to_move, ChessConstants.ROOK, bb);
			
			piece_indexes[fromIndex_rook] 	= ChessConstants.EMPTY;
			piece_indexes[toIndex_rook] 	= ChessConstants.ROOK;
			
		} else if (fromIndex_rook == toIndex_rook) {
			
			long bb 						= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= bb;
			} else {
				b_all ^= bb;
			}
			xorPieces(color_to_move, ChessConstants.KING, bb);
			
			piece_indexes[fromIndex_king] 	= ChessConstants.EMPTY;
			piece_indexes[toIndex_king] 	= ChessConstants.KING;
			
		} else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
		
			long bb_king					= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook					= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(color_to_move, ChessConstants.ROOK, bb_rook);
			
			piece_indexes[toIndex_rook] 	= ChessConstants.ROOK;
			piece_indexes[toIndex_king] 	= ChessConstants.KING;
			
		} else if (fromIndex_rook == toIndex_king) {
			
			long bb_king					= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook					= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(color_to_move, ChessConstants.ROOK, bb_rook);
			
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= (ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook]);
			} else {
				b_all ^= (ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook]);
			}
			
			piece_indexes[toIndex_rook] 	= ChessConstants.ROOK;
			piece_indexes[toIndex_king] 	= ChessConstants.KING;
			piece_indexes[fromIndex_king] 	= ChessConstants.EMPTY;

			
		} else if (toIndex_rook == fromIndex_king) {
			
			long bb_king					= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook					= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(color_to_move, ChessConstants.ROOK, bb_rook);
			
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= (ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook]);
			} else {
				b_all ^= (ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook]);
			}
			
			piece_indexes[toIndex_rook] 	= ChessConstants.ROOK;
			piece_indexes[toIndex_king] 	= ChessConstants.KING;
			piece_indexes[fromIndex_rook] 	= ChessConstants.EMPTY;
			
		} else {
			
			long bb_king					= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(color_to_move, ChessConstants.KING, bb_king);
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= bb_king;
			} else {
				b_all ^= bb_king;
			}
			
			long bb_rook					= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(color_to_move, ChessConstants.ROOK, bb_rook);
			if (color_to_move == ChessConstants.WHITE) {
				w_all ^= bb_rook;
			} else {
				b_all ^= bb_rook;
			}
			
			piece_indexes[fromIndex_rook] 	= ChessConstants.EMPTY;
			piece_indexes[fromIndex_king] 	= ChessConstants.EMPTY;
			piece_indexes[toIndex_rook] 	= ChessConstants.ROOK;
			piece_indexes[toIndex_king] 	= ChessConstants.KING;
			
		}
		
		
		zobrist_key ^= Zobrist.piece[fromIndex_king][color_to_move][ChessConstants.KING] ^ Zobrist.piece[toIndex_king][color_to_move][ChessConstants.KING];
		zobrist_key ^= Zobrist.piece[fromIndex_rook][color_to_move][ChessConstants.ROOK] ^ Zobrist.piece[toIndex_rook][color_to_move][ChessConstants.ROOK];
		
		if (ep_index != 0) {
			zobrist_key ^= Zobrist.epIndex[ep_index];
			ep_index = 0;
		}
		
		zobrist_key ^= Zobrist.sideToMove;
		
		
		if (castling_rights != 0) {
			
			zobrist_key ^= Zobrist.castling[castling_rights];
			
			castling_rights = CastlingUtil.getKingMovedCastlingRights(castling_rights, color_to_move, castling_config);
			
			zobrist_key ^= Zobrist.castling[castling_rights];
		}
		
		
		all_pieces = w_all | b_all;
		
		empty_spaces = ~all_pieces;
		
		color_to_move = 1 - color_to_move;

		// update checking pieces
		checking_pieces = CheckUtil.getCheckingPieces(this);
		
		
		setPinnedAndDiscoPieces();
		
		
		played_board_states.inc(zobrist_key);
	}
	

	public void undoMove(int move) {
		
		
		if (MoveUtil.isCastlingMove(move)) {
			
			undoCastling960(move);
			
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
	
	
	public void undoCastling960(int move) {
		
		
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
		
		
		final int fromIndex_king 	= MoveUtil.getFromIndex(move);
		final int toIndex_king 		= MoveUtil.getToIndex(move);
		final int sourcePieceIndex 	= MoveUtil.getSourcePieceIndex(move);
		
		if (sourcePieceIndex != ChessConstants.KING || !MoveUtil.isCastlingMove(move)) {
			
			throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
		}
		
		CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, buff_castling_rook_from_to);
		final int fromIndex_rook 	= buff_castling_rook_from_to[0];
		final int toIndex_rook 		= buff_castling_rook_from_to[1];
		
		
		if (fromIndex_king == toIndex_king) {
			
			long bb 							= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			
			xorPieces(1 - color_to_move, ChessConstants.ROOK, bb);
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= bb;
			} else {
				b_all ^= bb;
			}
			
			piece_indexes[fromIndex_rook] 		= ChessConstants.ROOK;
			piece_indexes[toIndex_rook] 		= ChessConstants.EMPTY;
			
		} else if (fromIndex_rook == toIndex_rook) {
			
			long bb 							= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			
			xorPieces(1 - color_to_move, ChessConstants.KING, bb);
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= bb;
			} else {
				b_all ^= bb;
			}
			
			piece_indexes[fromIndex_king] 		= ChessConstants.KING;
			piece_indexes[toIndex_king] 		= ChessConstants.EMPTY;
			
		} else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
		
			long bb_king						= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(1 - color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook						= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(1 - color_to_move, ChessConstants.ROOK, bb_rook);
			
			piece_indexes[toIndex_rook] 		= ChessConstants.KING;
			piece_indexes[toIndex_king] 		= ChessConstants.ROOK;
			
		} else if (fromIndex_rook == toIndex_king) {
			
			long bb_king						= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(1 - color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook						= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(1 - color_to_move, ChessConstants.ROOK, bb_rook);
			
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= (ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook]);
			} else {
				b_all ^= (ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook]);
			}
			
			piece_indexes[toIndex_rook] 		= ChessConstants.EMPTY;
			piece_indexes[toIndex_king] 		= ChessConstants.ROOK;
			piece_indexes[fromIndex_king] 		= ChessConstants.KING;

			
		} else if (toIndex_rook == fromIndex_king) {
			
			long bb_king						= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(1 - color_to_move, ChessConstants.KING, bb_king);
			
			long bb_rook						= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(1 - color_to_move, ChessConstants.ROOK, bb_rook);
			
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= (ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook]);
			} else {
				b_all ^= (ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook]);
			}
			
			piece_indexes[toIndex_rook] 		= ChessConstants.KING;
			piece_indexes[toIndex_king] 		= ChessConstants.EMPTY;
			piece_indexes[fromIndex_rook] 		= ChessConstants.ROOK;
			
		} else {
			
			long bb_king						= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
			xorPieces(1 - color_to_move, ChessConstants.KING, bb_king);
			
			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= bb_king;
			} else {
				b_all ^= bb_king;
			}
			
			long bb_rook						= ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
			xorPieces(1 - color_to_move, ChessConstants.ROOK, bb_rook);

			if (1 - color_to_move == ChessConstants.WHITE) {
				w_all ^= bb_rook;
			} else {
				b_all ^= bb_rook;
			}
			
			piece_indexes[fromIndex_rook] 		= ChessConstants.ROOK;
			piece_indexes[fromIndex_king] 		= ChessConstants.KING;
			piece_indexes[toIndex_rook] 		= ChessConstants.EMPTY;
			piece_indexes[toIndex_king] 		= ChessConstants.EMPTY;
			
		}
		
		
		all_pieces = w_all | b_all;
		
		empty_spaces = ~all_pieces;
		
		color_to_move = 1 - color_to_move;
	}
	
	
	public void doNullMove() {

		BoardState state_backup = states[played_moves_count];
		state_backup.pinned_pieces = pinned_pieces;
		state_backup.discovered_pieces = discovered_pieces;
		state_backup.checking_pieces = checking_pieces;
		state_backup.ep_index = ep_index;
		state_backup.castling_rights = castling_rights;
		state_backup.last_capture_or_pawn_move_before = last_capture_or_pawn_move_before;
		state_backup.zobrist_key = zobrist_key;
		played_moves[played_moves_count] = 0;
		
		played_moves_count++;

		zobrist_key ^= Zobrist.sideToMove;
		if (ep_index != 0) {
			zobrist_key ^= Zobrist.epIndex[ep_index];
			ep_index = 0;
		}
		
		color_to_move = 1 - color_to_move;
		
		played_board_states.inc(zobrist_key);
	}
	
	
	public void undoNullMove() {
		
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

		color_to_move = 1 - color_to_move;
	}
	
	
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
    	
        ChessBoard clone = new ChessBoard();

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
    
    
	@Override
	public int getColourToMove() {
		
		return color_to_move;
	}

	@Override
	public int genAllMoves(IInternalMoveList list) {
		
		MoveGeneration.generateMoves(this, list);
		MoveGeneration.generateAttacks(this, list);
		
		return list.reserved_getCurrentSize();
	}

	@Override
	public int genKingEscapes(IInternalMoveList list) {
		
		return genAllMoves(list);
	}

	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {

		MoveGeneration.generateAttacks(this, list);
		
		return list.reserved_getCurrentSize();
	}
	
	@Override
	public void makeMoveForward(int move) {

		if (move == 0) {
			
			return;
		}
		
		if (move_listeners.length > 0) {
			
			for (int i=0; i<move_listeners.length; i++) {
				
				move_listeners[i].preForwardMove(color_to_move, move);
			}
		}
		
		doMove(move);
		
		if (move_listeners.length > 0) {
			
			for (int i=0; i<move_listeners.length; i++) {
				
				move_listeners[i].postForwardMove(1 - color_to_move, move);
			}
		}
	}

	@Override
	public void makeMoveBackward(int move) {

		if (move_listeners.length > 0) {
			
			for (int i=0; i<move_listeners.length; i++) {
				
				move_listeners[i].preBackwardMove(1 - color_to_move, move);
			}
		}
		
		undoMove(move);
		
		if (move_listeners.length > 0) {
			
			for (int i=0; i<move_listeners.length; i++) {
				
				move_listeners[i].postBackwardMove(color_to_move, move);
			}
		}
	}

	@Override
	public void makeNullMoveForward() {

		doNullMove();
	}

	@Override
	public void makeNullMoveBackward() {

		undoNullMove();
	}

	@Override
	public long getHashKey() {
		
		return zobrist_key;
	}
	
	@Override
	public int getStateRepetition() {
		
		return getRepetition();
	}
	
	@Override
	public String toEPD() {
		
		return ChessBoardBuilder.toString(this, true);
	}
	
	@Override
	public IMoveOps getMoveOps() {
		
		return moveOps;
	}
	
	@Override
	public int getPlayedMovesCount() {
		
		return played_moves_count;
	}

	@Override
	public int[] getPlayedMoves() {
		
		return played_moves;
	}

	@Override
	public int getLastMove() {
		
		if (played_moves_count == 0) {
			
			return 0;
		}
		
		return played_moves[played_moves_count - 1];
	}
	
	@Override
	public boolean isDraw50movesRule() {
		
		return last_capture_or_pawn_move_before >= 100;
	}
	
	@Override
	public int getDraw50movesRule() {
		
		return last_capture_or_pawn_move_before;
	}

	@Override
	public boolean hasSufficientMatingMaterial() {

		return hasSufficientMatingMaterial(ChessConstants.WHITE) || hasSufficientMatingMaterial(ChessConstants.BLACK);
	}

	@Override
	public boolean hasSufficientMatingMaterial(int color) {

		
		
		/**
		 * If has pawn = true
		 */
		long pawns = getPieces(color, ChessConstants.PAWN);
		if (pawns != 0L) {
			return true;
		}
		
		
		/**
		 * If has queen = true
		 */
		long queens = getPieces(color, ChessConstants.QUEEN);
		if (queens != 0L) {
			return true;
		}
		
		
		/**
		 * If has rook = true
		 */
		long rooks = getPieces(color, ChessConstants.ROOK);
		if (rooks != 0L) {
			return true;
		}
		
		
		long bishops = getPieces(color, ChessConstants.BISHOP);
		long knights = getPieces(color, ChessConstants.KNIGHT);
		
		
		/**
		 * If has 3 or more bishops and knights = true
		 */
		if (Utils.countBits(bishops) + Utils.countBits(knights) >= 3) {
			
			return true;
		}
		
		
		/**
		 * If has 2 different colors bishop = true
		 */
		if (bishops != 0L) {
			
			if ((bishops & Fields.ALL_WHITE_FIELDS) != 0 && (bishops & Fields.ALL_BLACK_FIELDS) != 0) {
				
				return true;
			}
		}
		
		
		/**
		 * If has 1 bishop and 1 knight = true
		 */
		if (Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1) {
			
			return true;
		}
		
		
		/**
		 * In all other cases = false
		 */
		return false;
	}

	@Override
	public boolean isInCheck() {

		return checking_pieces != 0;
	}

	@Override
	public boolean isInCheck(int colour) {

		return CheckUtil.isInCheck(this, colour);
	}

	@Override
	public boolean hasMoveInCheck() {
		hasMovesList.clear();
		genKingEscapes(hasMovesList);
		return hasMovesList.reserved_getCurrentSize() > 0;
	}
	
	
	@Override
	public boolean hasMoveInNonCheck() {
		hasMovesList.clear();
		genAllMoves(hasMovesList);
		return hasMovesList.reserved_getCurrentSize() > 0;
	}

	@Override
	public boolean isPossible(int move) {
		
		return isValidMove(move);
	}
	
	@Override
	public CastlingConfig getCastlingConfig() {
		
		return castling_config;
	}
	
	@Override
	public void revert() {
		
		for(int i = played_moves_count - 1; i >= 0; i--) {
			
			int move = played_moves[i];
			
			if (move == 0) {
				
				makeNullMoveBackward();
				
			} else {
				
				makeMoveBackward(move);
			}
		}
	}
	
	@Override
	public IMaterialFactor getMaterialFactor() {

		return materialFactor;
	}
	
	@Override
	public IBaseEval getBaseEvaluation() {

		return null;
	}
	
	@Override
	public IPiecesLists getPiecesLists() {

		return pieces;
	}
	
	@Override
	public void makeMoveForward(String ucimove) {

		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isCheckMove(int move) {

		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasSingleMove() {
		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int[] getMatrix() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public PawnsEvalCache getPawnsCache() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {

		throw new UnsupportedOperationException();
	}

	@Override
	public PawnsModelEval getPawnsStructure() {

		throw new UnsupportedOperationException();
	}

	@Override
	public IBoardConfig getBoardConfig() {

		throw new UnsupportedOperationException();
	}

	@Override
	public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getEnpassantSquareID() {

		throw new UnsupportedOperationException();
	}

	@Override
	public long getHashKeyAfterMove(int move) {

		throw new UnsupportedOperationException();
	}

	@Override
	public long getPawnsHashKey() {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getFigureID(int fieldID) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getFigureType(int fieldID) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getFigureColour(int fieldID) {

		throw new UnsupportedOperationException();
	}

	@Override
	public ISEE getSee() {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getSEEScore(int move) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getSEEFieldScore(int squareID) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void mark() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {

		throw new UnsupportedOperationException();
	}

	@Override
	public IMaterialState getMaterialState() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPasserPush(int move) {

		throw new UnsupportedOperationException();
	}

	@Override
	public int getUnstoppablePasser() {

		throw new UnsupportedOperationException();
	}

	@Override
	public CastlingType getCastlingType(int colour) {

		throw new UnsupportedOperationException();
	}

	@Override
	public CastlingPair getCastlingPair() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRightsToKingCastle(int colour) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRightsToQueenCastle(int colour) {

		throw new UnsupportedOperationException();
	}

	@Override
	public IGameStatus getStatus() {

		throw new UnsupportedOperationException();
	}

	@Override
	public Object getNNUEInputs() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		MoveListener[] oldMoveListeners = move_listeners;
		MoveListener[] newMoveListeners = new MoveListener[move_listeners.length + 1];
		if (oldMoveListeners.length > 0) {
			for (int i=0; i<oldMoveListeners.length; i++) {
				newMoveListeners[i] = oldMoveListeners[i];
			}
		}
		
		newMoveListeners[oldMoveListeners.length] = listener;
		
		move_listeners = newMoveListeners;
	}

	@Override
	public long getFreeBitboard() {

		throw new UnsupportedOperationException();
	}

	@Override
	public long getFiguresBitboardByPID(int pid) {

		throw new UnsupportedOperationException();
	}

	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {

		throw new UnsupportedOperationException();
	}

	@Override
	public long getFiguresBitboardByColour(int colour) {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getAttacksSupport() {

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getFieldsStateSupport() {

		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		
		//throw new UnsupportedOperationException();
	}

	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {

		throw new UnsupportedOperationException();
	}

	@Override
	public IFieldsAttacks getFieldsAttacks() {

		throw new UnsupportedOperationException();
	}
	
	
private class MoveOpsImpl implements IMoveOps {
		
		
		private final int FILES[] = { 7, 6, 5, 4, 3, 2, 1, 0 };
		private final int RANKS[] = { 0, 1, 2, 3, 4, 5, 6, 7 };
		
		
		@Override
		public final int getFigureType(int move) {
			return MoveUtil.getSourcePieceIndex(move);
		}
		
		
		@Override
		public final int getToFieldID(int move) {
			return MoveUtil.getToIndex(move);
		}
		
		@Override
		public final boolean isCapture(int move) {
			return MoveUtil.getAttackedPieceIndex(move) != 0;
		}
		
		
		@Override
		public final boolean isPromotion(int move) {
			return MoveUtil.isPromotion(move);
		}
		
		
		@Override
		public final boolean isCaptureOrPromotion(int move) {
			return isCapture(move) || isPromotion(move);
		}

		
		@Override
		public final boolean isEnpassant(int move) {
			return MoveUtil.isEPMove(move);
		}

		
		@Override
		public final boolean isCastling(int move) {
			return MoveUtil.isCastlingMove(move);
		}
		
		
		@Override
		public final int getFigurePID(int move) {
			
			int pieceType = MoveUtil.getSourcePieceIndex(move);
			int colour = color_to_move;
			
			if (colour == ChessConstants.WHITE) {
				switch(pieceType) {
					case ChessConstants.PAWN: return Constants.PID_W_PAWN;
					case ChessConstants.KNIGHT: return Constants.PID_W_KNIGHT;
					case ChessConstants.BISHOP: return Constants.PID_W_BISHOP;
					case ChessConstants.ROOK: return Constants.PID_W_ROOK;
					case ChessConstants.QUEEN: return Constants.PID_W_QUEEN;
					case ChessConstants.KING: return Constants.PID_W_KING;
				}
			} else {
				switch(pieceType) {
					case ChessConstants.PAWN: return Constants.PID_B_PAWN;
					case ChessConstants.KNIGHT: return Constants.PID_B_KNIGHT;
					case ChessConstants.BISHOP: return Constants.PID_B_BISHOP;
					case ChessConstants.ROOK: return Constants.PID_B_ROOK;
					case ChessConstants.QUEEN: return Constants.PID_B_QUEEN;
					case ChessConstants.KING: return Constants.PID_B_KING;
				}
			}
			
			throw new IllegalStateException("pieceType=" + pieceType);
		}
		
		
		@Override
		public final boolean isCastlingKingSide(int move) {
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == CastlingConfig.G1 || index == CastlingConfig.G8;
			}
			
			return false;
		}
		
		
		@Override
		public final boolean isCastlingQueenSide(int move) {
			
			if (isCastling(move)) {
				int index = MoveUtil.getToIndex(move);
				return index == CastlingConfig.C1 || index == CastlingConfig.C8; 
			}
			
			return false;
		}
		
		
		@Override
		public final int getFromFieldID(int move) {
			return MoveUtil.getFromIndex(move);
		}
		
		
		@Override
		public final int getPromotionFigureType(int move) {
			if (!isPromotion(move)) {
				return 0;
			}
			return MoveUtil.getMoveType(move);
		}
		
		
		@Override
		public final int getCapturedFigureType(int cur_move) {
			return MoveUtil.getAttackedPieceIndex(cur_move);
		}
		
		
		@Override
		public final String moveToString(int move) {
			return (new MoveWrapper(move, isFRC, castling_config)).toString();
		}
		
		
		@Override
		public final void moveToString(int move, StringBuilder text_buffer) {
			(new MoveWrapper(move, isFRC, castling_config)).toString(text_buffer);
		}
		
		
		@Override
		public final int stringToMove(String move) {
			
			throw new UnsupportedOperationException();
			//MoveWrapper moveObj = new MoveWrapper(move, ChessBoard.this, isFRC);
			//return moveObj.move;
		}
		
		
		@Override
		public final int getToField_File(int move) {
			return FILES[getToFieldID(move) & 7];
		}
		
		
		@Override
		public final int getToField_Rank(int move) {
			return RANKS[getToFieldID(move) >>> 3];
		}
		
		
		@Override
		public final int getFromField_File(int move) {
			return FILES[getFromFieldID(move) & 7];
		}
		
		
		@Override
		public final int getFromField_Rank(int move) {
			return RANKS[getFromFieldID(move) >>> 3];
		}
	}


	private static class MaterialFactorImpl implements IMaterialFactor {
		
		
		private static final int TOTAL_FACTOR_MAX = 2 * 9 + 4 * 5 + 4 * 3 + 4 * 3; 
		//public static final int[] PHASE 					= {0, 0, 3, 3, 5, 9};
		
		
		public MaterialFactorImpl() {
		}
		
		
		@Override
		public int getBlackFactor() {
			return 31;
		}
		
		
		@Override
		public int getWhiteFactor() {
			return 31;
		}
		
		
		@Override
		public int getTotalFactor() {
			
			return getWhiteFactor() + getBlackFactor();
		}
		
		
		@Override
		public double getOpenningPart() {
			if (getTotalFactor() < 0) {
				throw new IllegalStateException();
			}
			return Math.min(1, getTotalFactor() / (double) TOTAL_FACTOR_MAX);
		}
		
		
		@Override
		public int interpolateByFactor(int val_o, int val_e) {
			double openningPart = getOpenningPart();
			int result = (int) (val_o * openningPart + (val_e * (1 - openningPart)));
			return result;
		}
		
		
		@Override
		public int interpolateByFactor(double val_o, double val_e) {
			double openningPart = getOpenningPart();
			double result = (val_o * openningPart + (val_e * (1 - openningPart)));
			return (int) result;
		}
	
	
		@Override
		public void addPiece_Special(int pid, int fieldID) {
			// TODO Auto-generated method stub
			
		}
	
	
		@Override
		public void preForwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}
	
	
		@Override
		public void postForwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}
	
	
		@Override
		public void preBackwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}
	
	
		@Override
		public void postBackwardMove(int color, int move) {
			// TODO Auto-generated method stub
			
		}
	
	
		@Override
		public void initially_addPiece(int color, int type, long bb_pieces) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	private static class PiecesListsImpl implements IPiecesLists {
		
		
		private PiecesList list;
		
		
		PiecesListsImpl(IBoard board) {
			list = new PiecesList(board, 8);
			list.add(16);
			list.add(32);
		}
		
		
		@Override
		public PiecesList getPieces(int pid) {
			return list;
		}
		
		
		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#rem(int, int)
		 */
		@Override
		public void rem(int pid, int fieldID) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#add(int, int)
		 */
		@Override
		public void add(int pid, int fieldID) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.bitboard.api.IPiecesLists#move(int, int, int)
		 */
		@Override
		public void move(int pid, int fromFieldID, int toFieldID) {
			throw new UnsupportedOperationException();
		}
	}
}
