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


public class CombinedHistory {
	
	
	private int scale 					= 1000;
	
	private final long[][] HH_MOVES1 	= new long[2][64 * 64];
	private final long[][] BF_MOVES1 	= new long[2][64 * 64];
	
	private final long[][][] HH_MOVES2 	= new long[2][7][64];
	private final long[][][] BF_MOVES2 	= new long[2][7][64];
	
	
	public CombinedHistory(int _scale) {
		
		scale = _scale;
		
		clear();
	}
	
	
	public void clear() {
		
		Arrays.fill(HH_MOVES1[WHITE], 0);
		Arrays.fill(HH_MOVES1[BLACK], 0);
		
		Arrays.fill(BF_MOVES1[WHITE], 1);
		Arrays.fill(BF_MOVES1[BLACK], 1);
		
		Arrays.fill(HH_MOVES2[WHITE][0], 0);
		Arrays.fill(HH_MOVES2[WHITE][PAWN], 0);
		Arrays.fill(HH_MOVES2[WHITE][NIGHT], 0);
		Arrays.fill(HH_MOVES2[WHITE][BISHOP], 0);
		Arrays.fill(HH_MOVES2[WHITE][ROOK], 0);
		Arrays.fill(HH_MOVES2[WHITE][QUEEN], 0);
		Arrays.fill(HH_MOVES2[WHITE][KING], 0);
		
		Arrays.fill(HH_MOVES2[BLACK][0], 0);
		Arrays.fill(HH_MOVES2[BLACK][PAWN], 0);
		Arrays.fill(HH_MOVES2[BLACK][NIGHT], 0);
		Arrays.fill(HH_MOVES2[BLACK][BISHOP], 0);
		Arrays.fill(HH_MOVES2[BLACK][ROOK], 0);
		Arrays.fill(HH_MOVES2[BLACK][QUEEN], 0);
		Arrays.fill(HH_MOVES2[BLACK][KING], 0);
		
		Arrays.fill(BF_MOVES2[WHITE][0], 1);
		Arrays.fill(BF_MOVES2[WHITE][PAWN], 1);
		Arrays.fill(BF_MOVES2[WHITE][NIGHT], 1);
		Arrays.fill(BF_MOVES2[WHITE][BISHOP], 1);
		Arrays.fill(BF_MOVES2[WHITE][ROOK], 1);
		Arrays.fill(BF_MOVES2[WHITE][QUEEN], 1);
		Arrays.fill(BF_MOVES2[WHITE][KING], 1);
		
		Arrays.fill(BF_MOVES2[BLACK][0], 1);
		Arrays.fill(BF_MOVES2[BLACK][PAWN], 1);
		Arrays.fill(BF_MOVES2[BLACK][NIGHT], 1);
		Arrays.fill(BF_MOVES2[BLACK][BISHOP], 1);
		Arrays.fill(BF_MOVES2[BLACK][ROOK], 1);
		Arrays.fill(BF_MOVES2[BLACK][QUEEN], 1);
		Arrays.fill(BF_MOVES2[BLACK][KING], 1);	
	}
	
	
	public void addValue_Good(final int color, final int move, final int depth) {
		HH_MOVES1[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		HH_MOVES2[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public void addValue_All(final int color, final int move, final int depth) {
		BF_MOVES1[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		BF_MOVES2[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public int getScore(final int color, final int move) {
		
		int fromToIndex = MoveUtil.getFromToIndex(move);
		int pieceType = MoveUtil.getSourcePieceIndex(move);
		int toIndex = MoveUtil.getToIndex(move);
			
		int value1 = (int) (scale * HH_MOVES1[color][fromToIndex] / BF_MOVES1[color][fromToIndex]);
		int value2 = (int) (scale * HH_MOVES2[color][pieceType][toIndex] / BF_MOVES2[color][pieceType][toIndex]);

		return value1 + value2;
	}
}
