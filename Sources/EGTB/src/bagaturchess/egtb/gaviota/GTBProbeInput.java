package bagaturchess.egtb.gaviota;


public class GTBProbeInput {
	
	
	public long hashkey;
	
	public int colourTomove;
	public int enpassSquare;
	
	public final int whiteSquares[] = new int [GTBProbing.MAX_PIECES_COUNT + 1];
	public final int blackSquares[] = new int [GTBProbing.MAX_PIECES_COUNT + 1];
	public final byte whitePieces[] = new byte [GTBProbing.MAX_PIECES_COUNT + 1];
	public final byte blackPieces[] = new byte [GTBProbing.MAX_PIECES_COUNT + 1];
	
	
	public GTBProbeInput() {
        clear();
	}


	public void clear() {
		whitePieces[0] = EGTBProbing.NATIVE_PID_NONE;
    	blackPieces[0] = EGTBProbing.NATIVE_PID_NONE;
	}
}
