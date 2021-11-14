package bagaturchess.egtb.syzygy.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.egtb.syzygy.SyzygyConstants;


public class SyzygyTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//Initialization of the board representation by given FEN
			
			//IBitBoard board  = new Board("3k4/8/8/8/8/8/3P4/3K4 w - -", null, null);//White win
			IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");//White win
			
			//IBitBoard board  = new Board("8/8/8/8/8/7k/5Kp1/8 w - - 0 1", null, null);//Draw
			//IBitBoard board  = new Board("8/8/8/8/8/7k/5Kp1/8 b - - 0 1", null, null);//Black win
			
			
			System.out.println(board);
			
			System.out.println("start probing");
			
			if (SyzygyTBProbing.getSingleton() != null) {
				SyzygyTBProbing.getSingleton().load("C:/Users/i027638/OneDrive - SAP SE/DATA/OWN/chess/EGTB/syzygy");
				
				//boolean available = SyzygyTBProbing.getSingleton().isAvailable(3);
				//System.out.println(available);
				
				//int result1 = SyzygyTBProbing.getSingleton().probeWDL(board);
				//System.out.println(result1);
				
				int result2 = SyzygyTBProbing.getSingleton().probeDTZ(board);
				int dtz = (result2 & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result2 & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				System.out.println(dtz);
				System.out.println(wdl);
				//System.out.println(SyzygyTBProbing.getSingleton().toMove(result2));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
