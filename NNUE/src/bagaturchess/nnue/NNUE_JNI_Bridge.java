package bagaturchess.nnue;


public class NNUE_JNI_Bridge {

	public static native void init(String filename);
	
	public static native int eval(String fen);
}
