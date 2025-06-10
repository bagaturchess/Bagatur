package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class HistoryTable implements IHistoryTable {
	
	
	public static final int MOVE_SCORE_SCALE 	= 1000;
	
	
	private int scale;
	
	private final long[][] BONUS 	= new long[7][64];
	private final long[][] ALL 		= new long[7][64];
		
	
	public HistoryTable() {
		
		scale = MOVE_SCORE_SCALE;
		
		clear();
	}
	
	
	public void clear() {

        for (int piece = 0; piece <= 6; piece++) {
            for (int to = 0; to < 64; to++) {
                BONUS[piece][to] = BONUS[piece][to] / 2;
                ALL[piece][to] = Math.max(1, (long)(ALL[piece][to] / 2));
            }
        }
	}
	
	
	public void registerGood(final int prevmove, final int move, final int depth) {
		BONUS[MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public void registerBad(final int prevmove, final int move, final int depth) {
		BONUS[MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] -= depth * depth;
	}
	
	
	public void registerAll(final int prevmove, final int move, final int depth) {
		ALL[MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public int getScores(int prevmove, int move) {
		
		int pieceType = MoveUtil.getSourcePieceIndex(move);
		int toIndex = MoveUtil.getToIndex(move);
			
		int value = (int) (scale * BONUS[pieceType][toIndex] / ALL[pieceType][toIndex]);

		return scale + value;
	}
}
