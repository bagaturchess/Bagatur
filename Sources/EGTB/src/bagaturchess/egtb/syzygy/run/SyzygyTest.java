package bagaturchess.egtb.syzygy.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.egtb.syzygy.SyzygyConstants;


public class SyzygyTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));
			
			//Initialization of the board representation by given FEN
			
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");//White win
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/4R3/3K4 w - -");//White win
			IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("8/8/8/8/8/7k/5Kp1/8 b - - 0 1");//Black win
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("8/8/8/8/8/7k/5Kp1/8 w - - 0 1");//Draw
			//IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("8/6P1/8/2kB2K1/8/8/8/4r3 w - - 1 19"); //TDZ is -1
			
			System.out.println(board);
			
			System.out.println("board.getDraw50movesRule()=" + board.getDraw50movesRule());
			
			if (SyzygyTBProbing.getSingleton() != null) {
				
				System.out.println("Loading TBs");
				
				SyzygyTBProbing.getSingleton().load(System.getenv("SYZYGY_HOME"));
				
				boolean available = SyzygyTBProbing.getSingleton().isAvailable(3);
				System.out.println("isAvailable(3)=" + available);
				
				System.out.println("start probing");
				
				long[] out = new long[2];
				SyzygyTBProbing.getSingleton().probeMove(board, out);
				MoveWrapper best_move = new MoveWrapper((int) out[1], false, board.getCastlingConfig());
				System.out.println("best_move=" + best_move);
				
				
				/*int probing_result = SyzygyTBProbing.getSingleton().probeWDL(board);
				int dtz = SyzygyTBProbing.getSingleton().probeDTZ(board);
				int wdl = (probing_result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				System.out.println("probing_result=" + probing_result);
				System.out.println("dtz=" + dtz);
				System.out.println("wdl=" + wdl);
				*/
				
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
