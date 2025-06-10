package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class ContinuationHistory implements IHistoryTable {
	
	
	private final IHistoryTable[][] prevmoves = new IHistoryTable[7][64];
	
	
	public ContinuationHistory() {
		
		for (int piece = 0; piece <= 6; piece++) {
			for (int to = 0; to < 64; to++) {
				prevmoves[piece][to] = new HistoryTable();
			}
		}
	}
	
	
	public void clear() {

		for (int piece = 0; piece <= 6; piece++) {
			for (int to = 0; to < 64; to++) {
				prevmoves[piece][to].clear();
			}
		}
	}
	
	
	public void registerGood(final int prevmove, final int move, final int depth) {
		prevmoves[MoveUtil.getSourcePieceIndex(prevmove)][MoveUtil.getToIndex(prevmove)].registerGood(prevmove, move, depth);
	}
	
	
	public void registerBad(final int prevmove, final int move, final int depth) {
		prevmoves[MoveUtil.getSourcePieceIndex(prevmove)][MoveUtil.getToIndex(prevmove)].registerBad(prevmove, move, depth);
	}
	
	
	public void registerAll(final int prevmove, final int move, final int depth) {
		prevmoves[MoveUtil.getSourcePieceIndex(prevmove)][MoveUtil.getToIndex(prevmove)].registerAll(prevmove, move, depth);
	}
	
	
	public int getScores(int prevmove, int move) {
		return prevmoves[MoveUtil.getSourcePieceIndex(prevmove)][MoveUtil.getToIndex(prevmove)].getScores(prevmove, move);
	}
}
