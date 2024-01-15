package bagaturchess.nnue;


public class NNUEJNIBridge {

	public static native void init(String filename);
	
	public static native int eval(String fen);
}
