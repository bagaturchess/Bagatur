package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.MoveUtil;


public class Killers implements IKillers {
	
	
	private final IBetaCutoffMoves[][] KILLER_MOVES = new IBetaCutoffMoves[2][EngineConstants.MAX_PLIES];
	
	
	public Killers() {
		
		for (int i = 0; i < KILLER_MOVES.length; i++) {
			
			for (int j = 0; j < KILLER_MOVES[i].length; j++) {
				
				KILLER_MOVES[i][j] = new BetaCutoffMoves_LastIn();
			}
		}
	}
	
	
	@Override
	public void clear() {
		
		for (int i = 0; i < KILLER_MOVES.length; i++) {
			
			for (int j = 0; j < KILLER_MOVES[i].length; j++) {
				
				KILLER_MOVES[i][j].clear();
			}
		}
	}
	
	
	@Override
	public void addKillerMove(final int color, final int move, final int ply) {
		
		KILLER_MOVES[color][ply].addMove(move);
	}
	
	
	@Override
	public int getKiller1(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest1();
	}


	@Override
	public int getKiller2(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest2();
	}
	
	
	@Override
	public int getKiller3(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest3();
	}


	@Override
	public int getKiller4(final int color, final int ply) {
		
		return KILLER_MOVES[color][ply].getBest4();
	}
	
	
	static interface IBetaCutoffMoves {
		
		void addMove(int move);
		
		int getBest1();
		
		int getBest2();
		
		int getBest3();
		
		int getBest4();
		
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


		@Override
		public int getBest3() {

			throw new UnsupportedOperationException(); 
		}


		@Override
		public int getBest4() {

			throw new UnsupportedOperationException(); 
		}
	}

	
	private static final class BetaCutoffMoves_LastIn implements IBetaCutoffMoves {
		
		
		private int best_move1;
		private int best_move2;
		private int best_move3;
		private int best_move4;
		
		
		private BetaCutoffMoves_LastIn() {
			
		}
		
		
		@Override
		public void addMove(int move) {
			
			if (best_move1 != move) {
				
				//Shift moves
				best_move4 = best_move3;
				best_move3 = best_move2;
				best_move2 = best_move1;
				
				best_move1 = move;
			}
		}
		
		
		@Override
		public int getBest1() {
			
			return best_move1;
		}
		
		
		@Override
		public int getBest2() {
			
			return best_move2;
		}
		
		
		@Override
		public int getBest3() {
			
			return best_move3;
		}


		@Override
		public int getBest4() {
			
			return best_move4;
		}
		
		
		@Override
		public void clear() {
			
			//Keep the moves
		}
	}
}
