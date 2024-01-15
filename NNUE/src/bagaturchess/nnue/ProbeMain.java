package bagaturchess.nnue;

import java.io.File;


public class ProbeMain {
	
	
	public static void main(String[] args) {
		
		File dll = new File("./JNNUE.dll");
		
		System.load(dll.getAbsolutePath());
		
		
		//File nnue = new File("nn-baff1edbea57.nnue");
		//File nnue = new File("nn-0000000000a0.nnue");
		File nnue = new File("nn-04cf2b4ed1da.nnue");
		
		NNUEJNIBridge.init(nnue.getAbsolutePath());
		
		//NNUE_JNI_Bridge.eval("fen");
	}
}
