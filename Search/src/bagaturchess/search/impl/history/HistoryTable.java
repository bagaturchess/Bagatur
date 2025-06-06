package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class HistoryTable implements IHistoryTable {
	
	
	public static final int MOVE_SCORE_SCALE 					= 1000;
	
	
	private int scale;
	
	private final long[][] HH_MOVES1 							= new long[2][64 * 64];
	private final long[][] BF_MOVES1 							= new long[2][64 * 64];
	
	private final long[][][] HH_MOVES2 							= new long[2][7][64];
	private final long[][][] BF_MOVES2 							= new long[2][7][64];
		
	
	public HistoryTable() {
		
		scale = MOVE_SCORE_SCALE;
		
		clear();
	}
	
	
	public void clear() {

	    for (int color = 0; color <= 1; color++) {
	        for (int i = 0; i < 64 * 64; i++) {
	            HH_MOVES1[color][i] =  HH_MOVES1[color][i] / 2;
	            BF_MOVES1[color][i] = Math.max(1, (long)(BF_MOVES1[color][i] / 2));
	        }

	        for (int piece = 0; piece <= 6; piece++) {
	            for (int to = 0; to < 64; to++) {
	                HH_MOVES2[color][piece][to] = HH_MOVES2[color][piece][to] / 2;
	                BF_MOVES2[color][piece][to] = Math.max(1, (long)(BF_MOVES2[color][piece][to] / 2));
	            }
	        }
	    }
	}
	
	
	public void registerGood(final int color, final int move, final int depth) {
		HH_MOVES1[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		HH_MOVES2[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public void registerBad(final int color, final int move, final int depth) {
		HH_MOVES1[color][MoveUtil.getFromToIndex(move)] -= depth * depth;
		HH_MOVES2[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] -= depth * depth;
	}
	
	
	public void registerAll(final int color, final int move, final int depth) {
		BF_MOVES1[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		BF_MOVES2[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public int getScores(int color, int move) {
		
		int fromToIndex = MoveUtil.getFromToIndex(move);
		int pieceType = MoveUtil.getSourcePieceIndex(move);
		int toIndex = MoveUtil.getToIndex(move);
			
		int value1 = (int) (scale * HH_MOVES1[color][fromToIndex] / BF_MOVES1[color][fromToIndex]);
		int value2 = (int) (scale * HH_MOVES2[color][pieceType][toIndex] / BF_MOVES2[color][pieceType][toIndex]);

		return scale + (value1 + value2) / 2;
	}
}
