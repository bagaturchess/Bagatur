package bagaturchess.search.impl.history;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import java.util.Arrays;

import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class CombinedHistory implements IHistoryTable {
	
	
	public static final int MOVE_SCORE_SCALE 					= 1000;
	
	
	private int scale;
	
	private final long[][] HH_MOVES1 	= new long[2][64 * 64];
	private final long[][] BF_MOVES1 	= new long[2][64 * 64];
	
	private final long[][][] HH_MOVES2 	= new long[2][7][64];
	private final long[][][] BF_MOVES2 	= new long[2][7][64];
	
	private final IBetaCutoffMoves[][] KILLER_MOVES 			= new IBetaCutoffMoves[2][EngineConstants.MAX_PLIES];
	private final IBetaCutoffMoves[][][] COUNTER_MOVES_LASTIN	= new IBetaCutoffMoves[2][7][64];
	private final IBetaCutoffMoves[][][] COUNTER_MOVES_COUNTS	= new IBetaCutoffMoves[2][7][64];
	
	
	public CombinedHistory() {
		
		scale = MOVE_SCORE_SCALE;
		
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
			
			for (int i = 0; i < 7; i++) {
			    for (int j = 0; j < 64; j++) {
			        moves_piece_to[i][j] = 0;
			        counts[i][j] = 0L;
			    }
			}
			
			best_move1 = 0;
			best_move2 = 0;
			max_count = 0;
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
			
			best_move1 = 0;
			best_move2 = 0;
		}
	}
}
