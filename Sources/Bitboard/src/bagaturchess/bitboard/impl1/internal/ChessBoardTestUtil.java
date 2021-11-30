package bagaturchess.bitboard.impl1.internal;

import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

public class ChessBoardTestUtil {

	public static void testValues(ChessBoard cb) {

		long iterativeZK = cb.zobristKey;
		long iterativeZKPawn = cb.pawnZobristKey;
		long iterativeWhitePieces = cb.friendlyPieces[WHITE];
		long iterativeBlackPieces = cb.friendlyPieces[BLACK];
		long iterativeAllPieces = cb.allPieces;
		long pinnedPieces = cb.pinnedPieces;
		long discoveredPieces = cb.discoveredPieces;
		int iterativePsqt = cb.psqtScore_mg;
		long whiteKingArea = cb.kingArea[WHITE];
		long blackKingArea = cb.kingArea[BLACK];
		int material_factor_white = cb.material_factor_white;
		int material_factor_black = cb.material_factor_black;
		long materialKey = cb.materialKey;
		int[] testPieceIndexes = new int[64];
		System.arraycopy(cb.pieceIndexes, 0, testPieceIndexes, 0, cb.pieceIndexes.length);

		Assert.isTrue(Long.numberOfTrailingZeros(cb.pieces[WHITE][KING]) == cb.kingIndex[WHITE], "Long.numberOfTrailingZeros(cb.pieces[WHITE][KING]) == cb.kingIndex[WHITE]");
		Assert.isTrue(Long.numberOfTrailingZeros(cb.pieces[BLACK][KING]) == cb.kingIndex[BLACK], "Long.numberOfTrailingZeros(cb.pieces[BLACK][KING]) == cb.kingIndex[BLACK]");

		ChessBoardUtil.init(cb);

		// zobrist keys
		Assert.isTrue(iterativeZK == cb.zobristKey, "iterativeZK == cb.zobristKey");
		Assert.isTrue(iterativeZKPawn == cb.pawnZobristKey, "iterativeZKPawn == cb.pawnZobristKey");

		// king area
		Assert.isTrue(whiteKingArea == cb.kingArea[WHITE], "whiteKingArea == cb.kingArea[WHITE]");
		Assert.isTrue(blackKingArea == cb.kingArea[BLACK], "blackKingArea == cb.kingArea[BLACK]");

		// pinned and discovered pieces
		Assert.isTrue(pinnedPieces == cb.pinnedPieces, "pinnedPieces == cb.pinnedPieces");
		Assert.isTrue(discoveredPieces == cb.discoveredPieces, "discoveredPieces == cb.discoveredPieces");

		// combined pieces
		Assert.isTrue(iterativeWhitePieces == cb.friendlyPieces[WHITE], "iterativeWhitePieces == cb.friendlyPieces[WHITE]");
		Assert.isTrue(iterativeBlackPieces == cb.friendlyPieces[BLACK], "iterativeBlackPieces == cb.friendlyPieces[BLACK]");
		Assert.isTrue(iterativeAllPieces == cb.allPieces, "iterativeAllPieces == cb.allPieces");
		Assert.isTrue((iterativeBlackPieces & iterativeWhitePieces) == 0, "(iterativeBlackPieces & iterativeWhitePieces) == 0");

		// psqt
		Assert.isTrue(iterativePsqt == cb.psqtScore_mg, "iterativePsqt == cb.psqtScore_mg");

		// piece-indexes
		for (int i = 0; i < testPieceIndexes.length; i++) {
			Assert.isTrue(testPieceIndexes[i] == cb.pieceIndexes[i], "testPieceIndexes[i] == cb.pieceIndexes[i]");
		}

		Assert.isTrue(material_factor_white == cb.material_factor_white, "material_factor_white == cb.material_factor_white");
		Assert.isTrue(material_factor_black == cb.material_factor_black, "material_factor_black == cb.material_factor_black");
		Assert.isTrue(materialKey == cb.materialKey, "materialKey == cb.materialKey");
	}

	public static ChessBoard getHorizontalMirroredCb(ChessBoard cb) {
		ChessBoard testCb = ChessBoard.getTestInstance();

		for (int color = ChessConstants.WHITE; color <= ChessConstants.BLACK; color++) {
			for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
				testCb.pieces[color][piece] = Util.mirrorHorizontal(cb.pieces[color][piece]);
			}
		}

		testCb.colorToMove = cb.colorToMove;
		ChessBoardUtil.init(testCb);
		testCb.moveCounter = cb.moveCounter;
		return testCb;
	}

	public static ChessBoard getVerticalMirroredCb(ChessBoard cb) {
		ChessBoard testCb = ChessBoard.getTestInstance();

		for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
			testCb.pieces[WHITE][piece] = Util.mirrorVertical(cb.pieces[BLACK][piece]);
		}
		for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
			testCb.pieces[BLACK][piece] = Util.mirrorVertical(cb.pieces[WHITE][piece]);
		}

		testCb.colorToMove = cb.colorToMoveInverse;
		ChessBoardUtil.init(testCb);
		testCb.moveCounter = cb.moveCounter;
		return testCb;
	}

}
