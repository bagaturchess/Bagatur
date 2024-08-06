package bagaturchess.nnue_v5;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.uci.api.ChannelManager;

/**
 * Experiment by probing via Stockfish's NNUE
 */
public class NNUEBridge {
	
    static {
		
		loadLib();
	}

	public static void loadLib() {
		
		try {
        	
            String libName = System.mapLibraryName("StockfishNNUE");
            Path jarfile = Paths.get(NNUEJNIBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File libFile = jarfile.getParent().resolve(libName).toFile();
            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + libFile);
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump(libName + " is now loaded");
            } else {
                URL classpathLibUrl = NNUEJNIBridge.class.getClassLoader().getResource(libName);
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + classpathLibUrl);
                if (classpathLibUrl != null && "file".equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()){
                    File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                    System.load(classpathLibFile.getAbsolutePath());
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the resources directory");
                } else {
                	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                    System.loadLibrary("StockfishNNUE");
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }
            
        } catch (Throwable t) {
        	
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JNNUE library " + t);
        }
	}

	static {
		
		NNUEBridge.init("nn-b1a57edbea57.nnue", "nn-baff1ede1f90.nnue");
	}
	
    public static native void init(String bigNet, String smallNet);

    public static native int evalFen(String fen);

    public static native int evalArray(int[] pieceBoard, int side, int rule50);

    public static native int fasterEvalArray(int[] pieces, int[] squares, int pieceAmount, int side, int rule50);
}
