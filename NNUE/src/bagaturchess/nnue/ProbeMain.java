package bagaturchess.nnue;

import java.io.File;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class ProbeMain {
	
	
	public static void main(String[] args) {
		
		File dll = new File("./JNNUE.dll");
		
		System.load(dll.getAbsolutePath());
		
		
		//File nnue = new File("nn-baff1edbea57.nnue");
		//File nnue = new File("nn-0000000000a0.nnue");
		File nnue = new File("nn-04cf2b4ed1da.nnue");
		
		NNUEJNIBridge.init(nnue.getAbsolutePath());
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		String fen = bitboard.toEPD();
		System.out.println("fen=" + fen);
		
		int score = NNUEJNIBridge.eval(fen);
			
		System.out.println("score=" + score);
	}
}
