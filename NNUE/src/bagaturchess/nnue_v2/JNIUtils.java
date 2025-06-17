package bagaturchess.nnue_v2;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.uci.api.ChannelManager;


public class JNIUtils {

	
	private static final String FILE_SCHEME = "file";
	
	
	static {
		
		loadLib();
	}
	
	
	// Bitmask: 1 = AVX2, 2 = AVX512F
    public static native int detectSIMD();
	
    
    public static native void accumulateVectorized_avx2(short[] accumulator, short[] L1Weights, boolean add);
    
    
    public static native void accumulateVectorized_avx512(short[] accumulator, short[] L1Weights, boolean add);
    
    
    public static native int evaluateVectorized_avx2(short[] L2Weights, short[] UsValues, short[] ThemValues);
	
    
    public static native int evaluateVectorized_avx512(short[] L2Weights, short[] UsValues, short[] ThemValues);
	
	
    public static boolean isAVX2Supported() {
    	
        return (detectSIMD() & 1) != 0;
    }

    
    public static boolean isAVX512Supported() {
    	
        return (detectSIMD() & 2) != 0;
    }
    
    
	public static void loadLib() {
		
		try {
        	
            String libName = System.mapLibraryName("VectorEval");
            Path jarfile = Paths.get(NNUEJNIBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File libFile = jarfile.getParent().resolve(libName).toFile();
            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + libFile);
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump(libName + " is now loaded");
            } else {
                URL classpathLibUrl = NNUEJNIBridge.class.getClassLoader().getResource(libName);
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + classpathLibUrl);
                if (classpathLibUrl != null && FILE_SCHEME.equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()){
                    File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                    System.load(classpathLibFile.getAbsolutePath());
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the resources directory");
                } else {
                	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                    System.loadLibrary("VectorEval");
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }
            
        } catch (Throwable t) {
        	
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JNNUE library " + t);
        }
	}
}
