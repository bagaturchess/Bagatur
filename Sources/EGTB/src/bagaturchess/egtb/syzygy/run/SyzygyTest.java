package bagaturchess.egtb.syzygy.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.egtb.syzygy.SyzygyConstants;


public class SyzygyTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//Initialization of the board representation by given FEN
			
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");//White win
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("8/8/8/8/8/7k/5Kp1/8 b - - 0 1");//Black win
			IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("8/8/8/8/8/7k/5Kp1/8 w - - 0 1");//Draw
			
			System.out.println(board);
			
			System.out.println("board.getDraw50movesRule()=" + board.getDraw50movesRule());
			
			System.out.println("start probing");
			
			if (SyzygyTBProbing.getSingleton() != null) {
				
				SyzygyTBProbing.getSingleton().load("C:/Users/i027638/OneDrive - SAP SE/DATA/OWN/chess/EGTB/syzygy");
				
				/*long[] out = new long[2];
				SyzygyTBProbing.getSingleton().probeMove(board, out);
				MoveWrapper best_move = new MoveWrapper((int) out[1], false, board.getCastlingConfig());
				System.out.println("best_move=" + best_move);
				*/
				
				//boolean available = SyzygyTBProbing.getSingleton().isAvailable(3);
				//System.out.println(available);
				
				int result1 = SyzygyTBProbing.getSingleton().probeWDL(board);
				int dtz = (result1 & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result1 & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				System.out.println("dtz=" + dtz);
				System.out.println("wdl=" + wdl);
				
				
				/*int result2 = SyzygyTBProbing.getSingleton().probeDTZ(board);
				int dtz = (result2 & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result2 & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				System.out.println("dtz=" + dtz);
				System.out.println("wdl=" + wdl);*/
				
				//System.out.println(SyzygyTBProbing.getSingleton().toMove(result2));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
