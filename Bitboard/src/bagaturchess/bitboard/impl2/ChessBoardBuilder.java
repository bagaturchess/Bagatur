package bagaturchess.bitboard.impl2;


import java.util.Arrays;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.Zobrist;


public class ChessBoardBuilder {
	
	
	public static final String FEN_WHITE_PIECES[] = { "1", "P", "N", "B", "R", "Q", "K" };
	public static final String FEN_BLACK_PIECES[] = { "1", "p", "n", "b", "r", "q", "k" };

	public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	public static final String[] ALL_FIELD_NAMES = new String[] {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", };
	
	
	public static ChessBoard getNewCB() {
		return getNewCB(FEN_START);
	}
	
	
	public static ChessBoard getNewCB(String fen) {
		
		String[] fenArray = fen.split(" ");
		
		ChessBoard cb = new ChessBoard();
		
		setFenValues(fenArray, cb);
		
		boolean[] castling_rights = new boolean[4];
		
		init(cb, castling_rights);		
		
		cb.played_board_states.inc(cb.zobrist_key);
		
		setCastling960Configuration(cb);
		
		if (fenArray.length > 2) {
			
			cb.played_board_states.dec(cb.zobrist_key);
			
			cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
			
			getCastlingRights(fenArray[2], cb.castling_config, castling_rights);
			
			setCastlingRights(castling_rights, cb);
			
			cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
			
			cb.played_board_states.inc(cb.zobrist_key);
		}
		
		return cb;
	}
	

	private static void setFenValues(String[] fenArray, ChessBoard cb) {
		
		
		cb.played_moves_count = 0;

		
		// 1: pieces: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
		setPieces(cb, fenArray[0]);

		
		// 2: active-color: w
		cb.color_to_move = fenArray[1].equals("w") ? ChessConstants.WHITE : ChessConstants.BLACK;

		
		if (fenArray.length > 3) {
			// 4: en-passant: -
			if (fenArray[3].equals("-") || fenArray[3].equals("–")) {
				cb.ep_index = 0;
			} else {
				cb.ep_index = 104 - fenArray[3].charAt(0) + 8 * (Integer.parseInt(fenArray[3].substring(1)) - 1);
			}
		}
		
		
		if (fenArray.length > 4) {
			
			//5: half-counter since last capture or pawn advance: 1
			String lastCaptureOrPawnMoveBefore = fenArray[4].equals("-") ? "1" : fenArray[4];

			cb.last_capture_or_pawn_move_before = Integer.parseInt(lastCaptureOrPawnMoveBefore);
			
			// 6: counter: 1
			cb.played_moves_count = Integer.parseInt(fenArray[5].equals("-") ? "1" : fenArray[5]) * 2;
			if (cb.color_to_move == ChessConstants.BLACK) {
				cb.played_moves_count++;
			}
		}
	}

	
	private static void setCastlingRights(boolean[] rights, ChessBoard cb) {
			
		cb.castling_rights = 15;
		
		if (!rights[0]) {
			
			cb.castling_rights &= 7;
		}
		
		if (!rights[1]) {
			
			cb.castling_rights &= 11;
		}
		
		if (!rights[2]) {
			
			cb.castling_rights &= 13;
		}
		
		if (!rights[3]) {
			
			cb.castling_rights &= 14;
		}
	}
	
	
	private static final void getCastlingRights(String str, CastlingConfig castlingConfig, boolean[] result) {
		
		
		if (str.length() == 0) {
			
			return;
		}
		
		
		if (str.contains("K") || str.contains("Q") || str.contains("k") || str.contains("q")) {
			
			if (str.contains("K")) {
				
				result[0] = true;
			}
			
			if (str.contains("Q")) {
				
				result[1] = true;
			}
			
			if (str.contains("k")) {
				
				result[2] = true;
			}
			
			if (str.contains("q")) {
				
				result[3] = true;
			}
			
		} else {
			
			String rook_file_kingside_w 	= "" + (char) (104 - castlingConfig.from_SquareID_rook_kingside_w % 8);
			String rook_file_queenside_w 	= "" + (char) (104 - castlingConfig.from_SquareID_rook_queenside_w % 8);
			String rook_file_kingside_b 	= "" + (char) (104 - castlingConfig.from_SquareID_rook_kingside_b % 8);
			String rook_file_queenside_b 	= "" + (char) (104 - castlingConfig.from_SquareID_rook_queenside_b % 8);
			
			//System.out.println("getCastlingRights: rook_file_kingside_w=" + rook_file_kingside_w);
			//System.out.println("getCastlingRights: rook_file_queenside_w=" + rook_file_queenside_w);
			//System.out.println("getCastlingRights: rook_file_kingside_b=" + rook_file_kingside_b);
			//System.out.println("getCastlingRights: rook_file_queenside_b=" + rook_file_queenside_b);
			
			
			for (int i = 0; i < str.length(); i++) {
				
				String current_file_name = str.substring(i, i + 1);
				
				//System.out.println("getCastlingRights: current_file_name=" + current_file_name);
				
				if (current_file_name.equals(current_file_name.toUpperCase())) {
					
					current_file_name = current_file_name.toLowerCase();
					
					if (current_file_name.equals(rook_file_kingside_w)) {
						
						result[0] = true;
					}
					
					if (current_file_name.equals(rook_file_queenside_w)) {
						
						result[1] = true;
					}
					
				} else {
					
					if (current_file_name.equals(rook_file_kingside_b)) {
						
						result[2] = true;
					}
					
					if (current_file_name.equals(rook_file_queenside_b)) {
						
						result[3] = true;
					}
				}
			}
		}
	}
	
	
	private static void calculateZobristKeys(ChessBoard cb) {
		cb.zobrist_key = 0;

		for (int color = 0; color < 2; color++) {
			for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
				long pieces = cb.getPieces(color, piece);
				while (pieces != 0) {
					cb.zobrist_key ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][color][piece];
					pieces &= pieces - 1;
				}
			}
		}

		cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
		if (cb.color_to_move == ChessConstants.WHITE) {
			cb.zobrist_key ^= Zobrist.sideToMove;
		}
		cb.zobrist_key ^= Zobrist.epIndex[cb.ep_index];
	}
	

	// rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
	private static void setPieces(final ChessBoard cb, final String fenPieces) {

		// clear pieces
		for (int color = 0; color < 2; color++) {
			for (int type = 1; type <= ChessConstants.KING; type++) {
				cb.setPieces(color, type, 0L);
			}
		}

		int positionCount = 63;
		for (int i = 0; i < fenPieces.length(); i++) {

			final char character = fenPieces.charAt(i);
			switch (character) {
			case '/':
				continue;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
				positionCount -= Character.digit(character, 10);
				break;
			case 'P':
				cb.w_pawns |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'N':
				cb.w_knights |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'B':
				cb.w_bishops |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'R':
				cb.w_rooks |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'Q':
				cb.w_queens |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'K':
				cb.w_king |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'p':
				cb.b_pawns |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'n':
				cb.b_knights |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'b':
				cb.b_bishops |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'r':
				cb.b_rooks |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'q':
				cb.b_queens |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			case 'k':
				cb.b_king |= ChessConstants.POWER_LOOKUP[positionCount--];
				break;
			}
		}
	}
	
	
	private static final void init(ChessBoard cb, boolean[] rights) {

		cb.w_all = cb.w_pawns | cb.w_bishops | cb.w_knights | cb.w_king | cb.w_rooks | cb.w_queens;
		cb.b_all = cb.b_pawns | cb.b_bishops | cb.b_knights | cb.b_king | cb.b_rooks | cb.b_queens;
		cb.all_pieces = cb.w_all | cb.b_all;
		cb.empty_spaces = ~cb.all_pieces;

		Arrays.fill(cb.piece_indexes, ChessConstants.EMPTY);
		for (int color = ChessConstants.WHITE; color <= ChessConstants.BLACK; color++) {
			for (int type = ChessConstants.PAWN; type <= ChessConstants.KING; type++) {
				long piece = cb.getPieces(color, type);
				while (piece != 0) {
					cb.piece_indexes[Long.numberOfTrailingZeros(piece)] = type;
					piece &= piece - 1;
				}
			}
		}

		cb.checking_pieces = CheckUtil.getCheckingPieces(cb);
		cb.setPinnedAndDiscoPieces();

		setCastlingRights(rights, cb);
		
		calculateZobristKeys(cb);
	}
	
	
	private static final void setCastling960Configuration(final ChessBoard cb) {
		
		
		long bb_king_w = cb.getPieces(ChessConstants.WHITE, ChessConstants.KING);
		long bb_king_b = cb.getPieces(ChessConstants.BLACK, ChessConstants.KING);
		
		if (bb_king_w == 0) {
			
			throw new IllegalStateException("No white king");
		}
		
		if (bb_king_w == 0) {
			
			throw new IllegalStateException("No black king");
		}
		
		
		int king_w_square_id = CastlingConfig.E1;
		int king_b_square_id = CastlingConfig.E8;
		
		
		int count_w_kings = 0;
		while (bb_king_w != 0) {
			
			king_w_square_id = Long.numberOfTrailingZeros(bb_king_w);
			
			bb_king_w &= bb_king_w - 1;
			
			count_w_kings++;
		}
		
		int count_b_kings = 0;
		while (bb_king_b != 0) {
			
			king_b_square_id = Long.numberOfTrailingZeros(bb_king_b);
			
			bb_king_b &= bb_king_b - 1;
			
			count_b_kings++;
		}
		
		//There should be a possibility to have 2 kings in order to be able to edit the board in android version (e.g. Bagatur app)
		if (count_w_kings > 2) {
			
			throw new IllegalStateException("More than 2 white king");
		}
		
		if (count_b_kings > 2) {
			
			throw new IllegalStateException("More than 2 black king");
		}
		
		
		int rook_kingside_w = CastlingConfig.H1;
		
			
		for (int square_id = king_w_square_id; square_id >= CastlingConfig.H1; square_id--) {
			
			int source_piece_type = cb.piece_indexes[square_id];
			
			if (source_piece_type == ChessConstants.ROOK) {
			
				rook_kingside_w = square_id;
				
				break;
			}
		}
		
		
		int rook_queenside_w = CastlingConfig.A1;
			
		for (int square_id = king_w_square_id; square_id <= CastlingConfig.A1; square_id++) {
			
			int source_piece_type = cb.piece_indexes[square_id];
			
			if (source_piece_type == ChessConstants.ROOK) {
			
				rook_queenside_w = square_id;
				
				break;
			}
		}
		
		
		int rook_kingside_b = CastlingConfig.H8;
		
		for (int square_id = king_b_square_id; square_id >= CastlingConfig.H8; square_id--) {
			
			int source_piece_type = cb.piece_indexes[square_id];
			
			if (source_piece_type == ChessConstants.ROOK) {
			
				rook_kingside_b = square_id;
				
				break;
			}
		}
		
		
		int rook_queenside_b = CastlingConfig.A8;
			
		for (int square_id = king_b_square_id; square_id <= CastlingConfig.A8; square_id++) {
			
			int source_piece_type = cb.piece_indexes[square_id];
			
			if (source_piece_type == ChessConstants.ROOK) {
			
				rook_queenside_b = square_id;
				
				break;
			}
		}
		
		
		CastlingConfig castlingConfig = new CastlingConfig(king_w_square_id, rook_kingside_w, rook_queenside_w, king_b_square_id, rook_kingside_b, rook_queenside_b);
		
		cb.castling_config = castlingConfig;
	}
	

	public static String toString(ChessBoard cb, boolean add_ep) {
		
		// TODO castling, EP, moves
		StringBuilder sb = new StringBuilder();
		for (int i = 63; i >= 0; i--) {
			if ((cb.getPieces_All(ChessConstants.WHITE) & ChessConstants.POWER_LOOKUP[i]) != 0) {
				sb.append(FEN_WHITE_PIECES[cb.piece_indexes[i]]);
			} else {
				sb.append(FEN_BLACK_PIECES[cb.piece_indexes[i]]);
			}
			if (i % 8 == 0 && i != 0) {
				sb.append("/");
			}
		}

		// color to move
		String colorToMove = cb.color_to_move == ChessConstants.WHITE ? "w" : "b";
		sb.append(" ").append(colorToMove).append(" ");

		//System.out.println("Board.toString: cb.castlingRights=" + cb.castlingRights);
		
		// castling rights
		if (cb.castling_rights == 0) {
			sb.append("-");
		} else {
			if ((cb.castling_rights & 8) != 0) { // 1000
				sb.append("K");
			}
			if ((cb.castling_rights & 4) != 0) { // 0100
				sb.append("Q");
			}
			if ((cb.castling_rights & 2) != 0) { // 0010
				sb.append("k");
			}
			if ((cb.castling_rights & 1) != 0) { // 0001
				sb.append("q");
			}
		}

		String fen = sb.toString();
		fen = fen.replaceAll("11111111", "8");
		fen = fen.replaceAll("1111111", "7");
		fen = fen.replaceAll("111111", "6");
		fen = fen.replaceAll("11111", "5");
		fen = fen.replaceAll("1111", "4");
		fen = fen.replaceAll("111", "3");
		fen = fen.replaceAll("11", "2");
		
		fen += " ";
		if (add_ep && cb.ep_index != 0) {
			fen += ALL_FIELD_NAMES[cb.ep_index];
		} else {
			fen += "-";
		}
		
		fen += " ";
		fen += cb.last_capture_or_pawn_move_before;
		
		fen += " ";
		fen += ((cb.played_moves_count + 1) / 2 + 1);

		
		//System.out.println("Board.toString: fen=" + fen);
		
		return fen;
	}
}
