package bagaturchess.nnue;


public class NNUE_JNI_Bridge {

	public static native boolean init(String filename);
	
	public static native int eval(String fen);
}
