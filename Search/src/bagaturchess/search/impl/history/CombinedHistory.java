package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class CombinedHistory implements IHistoryTable {
	
	
	public static final int MOVE_SCORE_SCALE 					= 1000;
	
	
	private int scale;
	
	private final long[][] HH_MOVES1 							= new long[2][64 * 64];
	private final long[][] BF_MOVES1 							= new long[2][64 * 64];
	
	private final long[][][] HH_MOVES2 							= new long[2][7][64];
	private final long[][][] BF_MOVES2 							= new long[2][7][64];
	
	private final IBetaCutoffMoves[][] KILLER_MOVES 			= new IBetaCutoffMoves[2][EngineConstants.MAX_PLIES];
	private final IBetaCutoffMoves[][][] COUNTER_MOVES_LASTIN	= new IBetaCutoffMoves[2][7][64];
	private final IBetaCutoffMoves[][][] COUNTER_MOVES_COUNTS	= new IBetaCutoffMoves[2][7][64];
	
	
	public CombinedHistory() {
		
		scale = MOVE_SCORE_SCALE;
		
		for (int i = 0; i < KILLER_MOVES.length; i++) {
			
			for (int j = 0; j < KILLER_MOVES[i].length; j++) {
				
				KILLER_MOVES[i][j] = new BetaCutoffMoves_LastIn();
			}
		}
		
		for (int i = 0; i < COUNTER_MOVES_LASTIN.length; i++) {
			
			for (int j = 0; j < COUNTER_MOVES_LASTIN[i].length; j++) {
				
				for (int k = 0; k < COUNTER_MOVES_LASTIN[i][j].length; k++) {
					
					COUNTER_MOVES_LASTIN[i][j][k] = new BetaCutoffMoves_LastIn();
				}
			}
		}

		for (int i = 0; i < COUNTER_MOVES_COUNTS.length; i++) {

			for (int j = 0; j < COUNTER_MOVES_COUNTS[i].length; j++) {

				for (int k = 0; k < COUNTER_MOVES_COUNTS[i][j].length; k++) {

					COUNTER_MOVES_COUNTS[i][j][k] = new BetaCutoffMoves_Counts();
				}
			}
		}
		
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
		
		for (int i = 0; i < KILLER_MOVES.length; i++) {
			
			for (int j = 0; j < KILLER_MOVES[i].length; j++) {
				
				KILLER_MOVES[i][j].clear();
			}
		}
		
		for (int i = 0; i < COUNTER_MOVES_LASTIN.length; i++) {
			
			for (int j = 0; j < COUNTER_MOVES_LASTIN[i].length; j++) {
				
				for (int k = 0; k < COUNTER_MOVES_LASTIN[i][j].length; k++) {
					
					COUNTER_MOVES_LASTIN[i][j][k].clear();
				}
			}
		}

		for (int i = 0; i < COUNTER_MOVES_COUNTS.length; i++) {

			for (int j = 0; j < COUNTER_MOVES_COUNTS[i].length; j++) {

				for (int k = 0; k < COUNTER_MOVES_COUNTS[i][j].length; k++) {

					COUNTER_MOVES_COUNTS[i][j][k].clear();
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
	
	
	public void addKillerMove(final int color, final int move, final int ply) {
		
		KILLER_MOVES[color][ply].addMove(move);
	}
	
	
	public void addCounterMove(final int color, final int parentMove, final int counterMove) {
		
		COUNTER_MOVES_LASTIN[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].addMove(counterMove);

		COUNTER_MOVES_COUNTS[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].addMove(counterMove);
	}
	

	public int getCounter1(final int color, final int parentMove) {
		
		return COUNTER_MOVES_LASTIN[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].getBest1();
		//return COUNTER_MOVES_COUNTS[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].getBest1();
	}
	
	
	public int getCounter2(final int color, final int parentMove) {
		
		return COUNTER_MOVES_COUNTS[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].getBest1();
		//return COUNTER_MOVES_LASTIN[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].getBest1();
	}
	
	
	public int getKiller1(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest1();
	}

	
	public int getKiller2(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest2();
	}
	
	
	static interface IBetaCutoffMoves {
		
		void addMove(int move);
		
		int getBest1();
		
		int getBest2();
		
		void clear();
	}
	
	
	private static final class BetaCutoffMoves_Counts implements IBetaCutoffMoves {
		
		
		private int[][] moves_piece_to;
		private long[][] counts;

		private int best_move1;
		private int best_move2;
		private long max_count;
		
		
		private BetaCutoffMoves_Counts() {
			
			moves_piece_to = new int[7][64];
			counts = new long[7][64];
			
			clear();
		}
		
		
		public void addMove(int move) {
			
			int piece = MoveUtil.getSourcePieceIndex(move);
			int to = MoveUtil.getToIndex(move);
			
			moves_piece_to[piece][to] = move;
			counts[piece][to]++;
			
			if (counts[piece][to] > max_count) {
				
				max_count = counts[piece][to];
				best_move2 = best_move1;
				best_move1 = move;
			}
		}
		
		
		public int getBest1() {
			
			return best_move1;
		}
		
		
		public int getBest2() {
			
			return best_move2;
		}


		@Override
		public void clear() {
			
		    max_count = 0;
		    best_move1 = 0;
		    best_move2 = 0;

		    for (int piece = 0; piece < 7; piece++) {
		        for (int to = 0; to < 64; to++) {
		            counts[piece][to] /= 2;

		            if (counts[piece][to] > max_count) {
		                max_count = counts[piece][to];
		                best_move2 = best_move1;
		                best_move1 = moves_piece_to[piece][to];
		            }
		        }
		    }
		}
	}

	
	private static final class BetaCutoffMoves_LastIn implements IBetaCutoffMoves {
		
		
		private int best_move1;
		private int best_move2;
		
		
		private BetaCutoffMoves_LastIn() {
			
		}
		
		
		public void addMove(int move) {
			
			if (best_move1 != move) {
				
				best_move2 = best_move1;
				
				best_move1 = move;
			}
		}
		
		
		public int getBest1() {
			
			return best_move1;
		}
		
		
		public int getBest2() {
			
			return best_move2;
		}
		
		
		@Override
		public void clear() {
			
			//Keep moves
		}
	}
}
