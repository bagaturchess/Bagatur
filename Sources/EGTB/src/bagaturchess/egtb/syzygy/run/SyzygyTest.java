package bagaturchess.egtb.syzygy.run;


import com.winkelhagen.chess.syzygy.SyzygyBridge;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.egtb.syzygy.Syzygy;
import bagaturchess.egtb.syzygy.SyzygyConstants;


public class SyzygyTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//Initialization of the board representation by given FEN
			
			//IBitBoard board  = new Board("3k4/8/8/8/8/8/3P4/3K4 w - -", null, null);//White win
			//IBitBoard board  = new Board("4k3/3r1p2/8/8/8/8/8/4KQ2 w - - 0 1", null, null);//White win
			
			//IBitBoard board  = new Board("8/8/8/8/8/7k/5Kp1/8 w - - 0 1", null, null);//Draw
			IBitBoard board  = new Board("8/8/8/8/8/7k/5Kp1/8 b - - 0 1", null, null);//Black win
			
			
			System.out.println(board);
			
			System.out.println("start brobe");
			
			SyzygyBridge.load("C:/Users/i027638/OneDrive - SAP SE/DATA/OWN/chess/EGTB/syzygy");
			
			boolean available = Syzygy.isAvailable(3);
			System.out.println(available);
			
			int result1 = Syzygy.probeWDL(board);
			System.out.println(result1);
			
			int result2 = Syzygy.probeDTZ(board);
			int dtz = (result2 & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
			int wdl = (result2 & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
			System.out.println(dtz);
			System.out.println(wdl);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
