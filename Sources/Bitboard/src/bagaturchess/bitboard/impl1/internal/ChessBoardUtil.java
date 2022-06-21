package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import java.util.Arrays;


public class ChessBoardUtil {

	public static final String ALL_FIELD_NAMES[] = new String[] {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", };
	
	public static ChessBoard getNewCB() {
		return getNewCB(ChessConstants.FEN_START);
	}

	public static ChessBoard getNewCB(String fen) {
		ChessBoard cb = new ChessBoard();
		setFenValues(fen, cb);
		init(cb);
		return cb;
	}

	private static void setFenValues(String fen, ChessBoard cb) {
		cb.moveCounter = 0;

		String[] fenArray = fen.split(" ");

		// 1: pieces: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
		setPieces(cb, fenArray[0]);

		// 2: active-color: w
		cb.colorToMove = fenArray[1].equals("w") ? WHITE : BLACK;

		// 3: castling: KQkq
		cb.castlingRights = 15;
		if (fenArray.length > 2) {
			if (!fenArray[2].contains("K")) {
				cb.castlingRights &= 7;
			}
			if (!fenArray[2].contains("Q")) {
				cb.castlingRights &= 11;
			}
			if (!fenArray[2].contains("k")) {
				cb.castlingRights &= 13;
			}
			if (!fenArray[2].contains("q")) {
				cb.castlingRights &= 14;
			}
		} else {
			// try to guess the castling rights
			if (cb.kingIndex[WHITE] != cb.castlingConfig.from_SquareID_king_w) {
				cb.castlingRights &= 3; // 0011
			}
			if (cb.kingIndex[BLACK] != cb.castlingConfig.from_SquareID_king_b) {
				cb.castlingRights &= 12; // 1100
			}
		}

		if (fenArray.length > 3) {
			// 4: en-passant: -
			if (fenArray[3].equals("-") || fenArray[3].equals("–")) {
				cb.epIndex = 0;
			} else {
				cb.epIndex = 104 - fenArray[3].charAt(0) + 8 * (Integer.parseInt(fenArray[3].substring(1)) - 1);
			}
		}
		
		if (fenArray.length > 4) {
			
			//5: half-counter since last capture or pawn advance: 1
			String lastCaptureOrPawnMoveBefore = fenArray[4];

			cb.lastCaptureOrPawnMoveBefore = Integer.parseInt(lastCaptureOrPawnMoveBefore);
			
			// 6: counter: 1
			cb.moveCounter = Integer.parseInt(fenArray[5]) * 2;
			if (cb.colorToMove == BLACK) {
				cb.moveCounter++;
			}
		} else {
			// if counter is not set, try to guess
			// assume in the beginning every 2 moves, a pawn is moved
			int pawnsNotAtStartingPosition = 16 - Long.bitCount(cb.pieces[WHITE][PAWN] & Bitboard.RANK_2)
					- Long.bitCount(cb.pieces[BLACK][PAWN] & Bitboard.RANK_7);
			cb.moveCounter = pawnsNotAtStartingPosition * 2;
		}
	}

	private static void calculateZobristKeys(ChessBoard cb) {
		cb.zobristKey = 0;

		for (int color = 0; color < 2; color++) {
			for (int piece = PAWN; piece <= KING; piece++) {
				long pieces = cb.pieces[color][piece];
				while (pieces != 0) {
					cb.zobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][color][piece];
					pieces &= pieces - 1;
				}
			}
		}

		cb.zobristKey ^= Zobrist.castling[cb.castlingRights];
		if (cb.colorToMove == WHITE) {
			cb.zobristKey ^= Zobrist.sideToMove;
		}
		cb.zobristKey ^= Zobrist.epIndex[cb.epIndex];
	}

	private static void calculatePawnZobristKeys(ChessBoard cb) {
		cb.pawnZobristKey = 0;

		long pieces = cb.pieces[WHITE][PAWN];
		while (pieces != 0) {
			cb.pawnZobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][WHITE][PAWN];
			pieces &= pieces - 1;
		}
		pieces = cb.pieces[BLACK][PAWN];
		while (pieces != 0) {
			cb.pawnZobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][BLACK][PAWN];
			pieces &= pieces - 1;
		}
	}

	// rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR
	private static void setPieces(final ChessBoard cb, final String fenPieces) {

		// clear pieces
		for (int color = 0; color < 2; color++) {
			for (int pieceIndex = 1; pieceIndex <= KING; pieceIndex++) {
				cb.pieces[color][pieceIndex] = 0;
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
				cb.pieces[WHITE][PAWN] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'N':
				cb.pieces[WHITE][NIGHT] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'B':
				cb.pieces[WHITE][BISHOP] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'R':
				cb.pieces[WHITE][ROOK] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'Q':
				cb.pieces[WHITE][QUEEN] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'K':
				cb.pieces[WHITE][KING] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'p':
				cb.pieces[BLACK][PAWN] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'n':
				cb.pieces[BLACK][NIGHT] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'b':
				cb.pieces[BLACK][BISHOP] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'r':
				cb.pieces[BLACK][ROOK] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'q':
				cb.pieces[BLACK][QUEEN] |= Util.POWER_LOOKUP[positionCount--];
				break;
			case 'k':
				cb.pieces[BLACK][KING] |= Util.POWER_LOOKUP[positionCount--];
				break;
			}
		}
	}
	
	
	static void init(ChessBoard cb) {

		calculateMaterialZobrist(cb);

		cb.updateKingValues(WHITE, Long.numberOfTrailingZeros(cb.pieces[WHITE][KING]));
		cb.updateKingValues(BLACK, Long.numberOfTrailingZeros(cb.pieces[BLACK][KING]));

		cb.colorToMoveInverse = 1 - cb.colorToMove;
		cb.friendlyPieces[WHITE] = cb.pieces[WHITE][PAWN] | cb.pieces[WHITE][BISHOP] | cb.pieces[WHITE][NIGHT] | cb.pieces[WHITE][KING] | cb.pieces[WHITE][ROOK]
				| cb.pieces[WHITE][QUEEN];
		cb.friendlyPieces[BLACK] = cb.pieces[BLACK][PAWN] | cb.pieces[BLACK][BISHOP] | cb.pieces[BLACK][NIGHT] | cb.pieces[BLACK][KING] | cb.pieces[BLACK][ROOK]
				| cb.pieces[BLACK][QUEEN];
		cb.allPieces = cb.friendlyPieces[WHITE] | cb.friendlyPieces[BLACK];
		cb.emptySpaces = ~cb.allPieces;

		Arrays.fill(cb.pieceIndexes, ChessConstants.EMPTY);
		for (int color = 0; color < cb.pieces.length; color++) {
			for (int pieceIndex = 1; pieceIndex < cb.pieces[0].length; pieceIndex++) {
				long piece = cb.pieces[color][pieceIndex];
				while (piece != 0) {
					cb.pieceIndexes[Long.numberOfTrailingZeros(piece)] = pieceIndex;
					piece &= piece - 1;
				}
			}
		}

		cb.checkingPieces = CheckUtil.getCheckingPieces(cb);
		cb.setPinnedAndDiscoPieces();
		cb.psqtScore_mg = 0;
		cb.psqtScore_eg = 0;
		calculatePositionScores(cb);

		cb.material_factor_white =
				+ (Long.bitCount(cb.pieces[WHITE][NIGHT]) * EvalConstants.PHASE[NIGHT]
				+ Long.bitCount(cb.pieces[WHITE][BISHOP]) * EvalConstants.PHASE[BISHOP]
				+ Long.bitCount(cb.pieces[WHITE][ROOK]) * EvalConstants.PHASE[ROOK]
				+ Long.bitCount(cb.pieces[WHITE][QUEEN]) * EvalConstants.PHASE[QUEEN]);
		 
		cb.material_factor_black = 
				+ (Long.bitCount(cb.pieces[BLACK][NIGHT]) * EvalConstants.PHASE[NIGHT]
				+ Long.bitCount(cb.pieces[BLACK][BISHOP]) * EvalConstants.PHASE[BISHOP]
				+ Long.bitCount(cb.pieces[BLACK][ROOK]) * EvalConstants.PHASE[ROOK]
				+ Long.bitCount(cb.pieces[BLACK][QUEEN]) * EvalConstants.PHASE[QUEEN]);
		
		calculatePawnZobristKeys(cb);
		calculateZobristKeys(cb);
		
		cb.playedBoardStates.inc(cb.zobristKey);
		
		setCastling960Configuration(cb);
	}
	
	
	private static final void setCastling960Configuration(final ChessBoard cb) {
		
		
		/*
		Commented in order to prevent potential null pointer exceptions, when cb.castlingConfig is not set and stay null.
		if (cb.castlingRights == 0) {
			
			return;
		}*/
		
		
		long bb_king_w = cb.pieces[WHITE][KING];
		long bb_king_b = cb.pieces[BLACK][KING];
		
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
		
		if (count_w_kings > 1) {
			
			throw new IllegalStateException("More than 1 white king");
		}
		
		if (count_b_kings > 1) {
			
			throw new IllegalStateException("More than 1 black king");
		}
		
		
		int rook_kingside_w = CastlingConfig.H1;
		
		//White kingside //0100
		if ((cb.castlingRights & 8) != 0) {
			
			for (int square_id = king_w_square_id; square_id >= CastlingConfig.H1; square_id--) {
				
				int source_piece_type = cb.pieceIndexes[square_id];
				
				if (source_piece_type == ROOK) {
				
					rook_kingside_w = square_id;
					
					break;
				}
			}
		}
		
		
		int rook_queenside_w = CastlingConfig.A1;
		
		//White queenside //0100
		if ((cb.castlingRights & 4) != 0) {
			
			for (int square_id = king_w_square_id; square_id <= CastlingConfig.A1; square_id++) {
				
				int source_piece_type = cb.pieceIndexes[square_id];
				
				if (source_piece_type == ROOK) {
				
					rook_queenside_w = square_id;
					
					break;
				}
			}
		}
		
		
		int rook_kingside_b = CastlingConfig.H8;
		
		//Black kingside //0010
		if ((cb.castlingRights & 2) != 0) {
			
			for (int square_id = king_b_square_id; square_id >= CastlingConfig.H8; square_id--) {
				
				int source_piece_type = cb.pieceIndexes[square_id];
				
				if (source_piece_type == ROOK) {
				
					rook_kingside_b = square_id;
					
					break;
				}
			}
		}
		
		
		int rook_queenside_b = CastlingConfig.A8;
		
		//Black queenside //0001
		if ((cb.castlingRights & 1) != 0) {
			
			for (int square_id = king_b_square_id; square_id <= CastlingConfig.A8; square_id++) {
				
				int source_piece_type = cb.pieceIndexes[square_id];
				
				if (source_piece_type == ROOK) {
				
					rook_queenside_b = square_id;
					
					break;
				}
			}
		}
		
		
		CastlingConfig castlingConfig = new CastlingConfig(king_w_square_id, rook_kingside_w, rook_queenside_w, king_b_square_id, rook_kingside_b, rook_queenside_b);
		
		cb.castlingConfig = castlingConfig;
	}
	
	
	private static void calculatePositionScores(final ChessBoard cb) {
		
		for (int color = WHITE; color <= BLACK; color++) {
			for (int pieceType = PAWN; pieceType <= KING; pieceType++) {
				long piece = cb.pieces[color][pieceType];
				while (piece != 0) {
					cb.psqtScore_mg += EvalConstants.PSQT_MG[pieceType][color][Long.numberOfTrailingZeros(piece)];
					cb.psqtScore_eg += EvalConstants.PSQT_EG[pieceType][color][Long.numberOfTrailingZeros(piece)];
					piece &= piece - 1;
				}
			}
		}
	}
	
	
	private static void calculateMaterialZobrist(final ChessBoard cb) {
		cb.materialKey = 0;
		for (int color = WHITE; color <= BLACK; color++) {
			for (int piece = PAWN; piece <= QUEEN; piece++) {
				cb.materialKey += Long.bitCount(cb.pieces[color][piece]) * MaterialUtil.VALUES[color][piece];
			}
		}
	}

	public static String toString(ChessBoard cb, boolean add_ep) {
		// TODO castling, EP, moves
		StringBuilder sb = new StringBuilder();
		for (int i = 63; i >= 0; i--) {
			if ((cb.friendlyPieces[WHITE] & Util.POWER_LOOKUP[i]) != 0) {
				sb.append(ChessConstants.FEN_WHITE_PIECES[cb.pieceIndexes[i]]);
			} else {
				sb.append(ChessConstants.FEN_BLACK_PIECES[cb.pieceIndexes[i]]);
			}
			if (i % 8 == 0 && i != 0) {
				sb.append("/");
			}
		}

		// color to move
		String colorToMove = cb.colorToMove == WHITE ? "w" : "b";
		sb.append(" ").append(colorToMove).append(" ");

		// castling rights
		if (cb.castlingRights == 0) {
			sb.append("-");
		} else {
			if ((cb.castlingRights & 8) != 0) { // 1000
				sb.append("K");
			}
			if ((cb.castlingRights & 4) != 0) { // 0100
				sb.append("Q");
			}
			if ((cb.castlingRights & 2) != 0) { // 0010
				sb.append("k");
			}
			if ((cb.castlingRights & 1) != 0) { // 0001
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
		if (add_ep && cb.epIndex != 0) {
			fen += ALL_FIELD_NAMES[cb.epIndex];
		} else {
			fen += "-";
		}
		
		fen += " ";
		fen += cb.lastCaptureOrPawnMoveBefore;
		
		fen += " ";
		fen += ((cb.playedMovesCount + 1) / 2 + 1);

		
		return fen;
	}
}
