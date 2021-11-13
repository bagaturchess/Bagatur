package bagaturchess.egtb.gaviota.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.egtb.gaviota.GTBProbing;
import bagaturchess.egtb.cache.EGTBCache;
import bagaturchess.egtb.cache.EGTBProbeInput;
import bagaturchess.egtb.cache.EGTBProbeOutput;


public class GaviotaTest {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//Initialization of the board representation by given FEN
			
			IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");
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
			//GTBProbing_NativeWrapper.getInstance().setPath_Async("C:/DATA/OWN/chess/EGTB", 4);
			
			System.out.println("start brobe");
			
			EGTBProbeInput temp_input = new EGTBProbeInput();
			EGTBCache cache_out = new EGTBCache(10000, true, new BinarySemaphore_Dummy());
			
			GTBProbing probing = new GTBProbing();
			probing.setPath_Sync("C:/DATA/OWN/chess/EGTB", 4);
			
			//Blocking probing, which returns also a move
			probeMoveHard(probing, board);
			
			//Non-blocking probing which returns the game result only
			probeHard(probing, board, temp_input, cache_out);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void probeHard(GTBProbing probing, IBitBoard board, EGTBProbeInput temp_input, EGTBCache cache_out)
			throws InterruptedException {
		
		int res[] = new int[2];
		
		probing.probe(board, res, temp_input, cache_out);
		
		
		System.out.println("TEST probeHard: " + new EGTBProbeOutput(res));
	}
	
	
	private static void probeMoveHard(GTBProbing probing, IBitBoard board)
			throws InterruptedException {
		
		int res[] = new int[2];
		probing.probeMove(board, res);

		//System.out.println("TEST probeMoveHard: " + MoveInt.moveToString(res[0]) + " " + res[1]);
	}
}
