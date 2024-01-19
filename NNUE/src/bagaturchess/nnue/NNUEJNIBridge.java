package bagaturchess.nnue;


import java.io.File;


public class NNUEJNIBridge {

	
	public static final String NET_NAME = "nn-6b4236f2ec01.nnue";
	
	
	public static void loadLib() {
		
		try {
			
			File library = new File("./bin/JNNUE.dll");
			
			System.load(library.getAbsolutePath());
		
		} catch(Throwable t) {
			
			t.printStackTrace();
		}
	}
	
	
	public static void init() {

		init(NET_NAME);
	}
	
	
	public static native void init(String filename);
	
	public static native int eval(String fen);
	
	public static native int eval(int color, int[] pieces, int[] squares);
}
