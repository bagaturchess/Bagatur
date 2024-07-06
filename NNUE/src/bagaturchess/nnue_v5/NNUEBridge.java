package bagaturchess.nnue_v5;


import java.io.File;

/**
 * Experiment by probing via Stockfish's NNUE
 */
public class NNUEBridge {
	
    static {
    	
        File dll = new File("probe.dll");
        
        System.load(dll.getAbsolutePath());
    }

	static {
		
		NNUEBridge.init("nn-b1a57edbea57.nnue", "nn-baff1ede1f90.nnue");
	}
	
    public static native void init(String bigNet, String smallNet);

    public static native int evalFen(String fen);

    public static native int evalArray(int[] pieceBoard, int side, int rule50);

    public static native int fasterEvalArray(int[] pieces, int[] squares, int pieceAmount, int side, int rule50);
}
