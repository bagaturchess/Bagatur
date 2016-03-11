package bagaturchess.egtb.gaviota.run;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.egtb.gaviota.GTBProbeOutput;
import bagaturchess.egtb.gaviota.GTBProbing;
import bagaturchess.egtb.gaviota.GTBProbing_NativeWrapper;
import bagaturchess.egtb.gaviota.cache.GTBCache_IN;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;


public class GaviotaTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//Initialization of the board representation by given FEN
			
			IBitBoard board  = new Board("3k4/8/8/8/8/8/3P4/3K4 w - -", null, null);
			//IBitBoard board  = new Board("3k4/8/8/8/8/8/3K4/4R3 w - -", null, null);
			
			//IBitBoard board  = new Board("3k4/8/8/8/8/8/3P4/3K4 w - -", null, null);
			//IBitBoard board  = new Board("3k4/3p4/8/8/8/8/8/3K4 w - -", null, null);
			
			//IBitBoard board  = new Board("4k3/8/8/8/8/8/3R4/3K4 w - -", null, null);
			//IBitBoard board  = new Board("4k3/8/8/8/8/8/3R4/3K4 b - -", null, null);
			//IBitBoard board  = new Board("4k3/4r3/8/8/8/8/8/3K4 w - -", null, null);
			//IBitBoard board  = new Board("4k3/4r3/8/8/8/8/8/3K4 b - -", null, null);
			
			//IBitBoard board  = new Board("4k3/8/8/8/8/8/3Q4/3K4 w - -", null, null);
			//IBitBoard board  = new Board("4k3/8/8/8/8/8/3Q4/3K4 b - -", null, null);
			//IBitBoard board  = new Board("4k3/4q3/8/8/8/8/8/3K4 w - -", null, null);
			//IBitBoard board  = new Board("4k3/4q3/8/8/8/8/8/3K4 b - -", null, null);
			
			//IBitBoard board = new Board(Constants.INITIAL_BOARD, null, null);
			
			System.out.println(board);
			
			//Initialize the path to Gaviota EGTB Files as well as the native cache size in megabytes 
			GTBProbing_NativeWrapper.getInstance().setPath_Sync("C:/EGTB", 16);
			
			
			GTBProbing probing = new GTBProbing(new GTBCache_OUT(10000, true, new BinarySemaphore_Dummy()), new GTBCache_IN(100));

			//Blocking probing, which returns also a move
			probeMoveHard(probing, board);
			
			//Non-blocking probing which returns the game result only
			probeSoft(probing, board);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void probeSoft(GTBProbing probing, IBitBoard board)
			throws InterruptedException {
		
		int res[] = new int[2];
		
		probing.probe(board, res);
		
		//Wait the in-chache's filler thread to load the entry
		Thread.sleep(200);
		
		probing.probe(board, res);
		
		System.out.println("TEST probeSoft: " + new GTBProbeOutput(res));
	}
	
	
	private static void probeMoveHard(GTBProbing probing, IBitBoard board)
			throws InterruptedException {
		
		int res[] = new int[2];
		probing.probeMove(board, res);

		System.out.println("TEST probeMoveHard: " + MoveInt.moveToString(res[0]) + " " + res[1]);
	}
}
