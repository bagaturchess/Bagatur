package bagaturchess.nnue;

import java.io.File;


public class ProbeMain {
	
	
	public static void main(String[] args) {
		
		File dll = new File("./JNNUE.dll");
		
		System.load(dll.getAbsolutePath());
		
		NNUE_JNI_Bridge.init("./nn-baff1edbea57.nnue");
		
		//NNUE_JNI_Bridge.eval("fen");
	}
}
