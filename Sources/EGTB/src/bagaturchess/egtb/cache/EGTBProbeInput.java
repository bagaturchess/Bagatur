package bagaturchess.egtb.cache;


public class EGTBProbeInput {
	
	
	public long hashkey;
	
	public int colourTomove;
	public int enpassSquare;
	
	public final int whiteSquares[] = new int [EGTBProbing.MAX_PIECES_COUNT + 1];
	public final int blackSquares[] = new int [EGTBProbing.MAX_PIECES_COUNT + 1];
	public final byte whitePieces[] = new byte [EGTBProbing.MAX_PIECES_COUNT + 1];
	public final byte blackPieces[] = new byte [EGTBProbing.MAX_PIECES_COUNT + 1];
	
	
	public EGTBProbeInput() {
        clear();
	}


	public void clear() {
		
		if (true) throw new IllegalStateException();
		
		//whitePieces[0] = EGTBProbing.NATIVE_PID_NONE;
    	//blackPieces[0] = EGTBProbing.NATIVE_PID_NONE;
	}
}
