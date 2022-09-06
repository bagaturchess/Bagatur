package bagaturchess.egtb.syzygy;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import bagaturchess.uci.api.ChannelManager;


/**
 * A bridge between Java and the Fathom library to access Syzygy tablebases.
 *
 * C source code downloaded from:  https://github.com/jdart1/Fathom
 * 
 * For SyzygyLib build instructions see build_syzygy_lib.txt
 */
public class SyzygyJNIBridge {

    private static boolean libLoaded = false;
    private static int tbLargest = 0;

    private SyzygyJNIBridge(){}

    private static final String FILE_SCHEME = "file";


	public static boolean loadNativeLibrary() {
		
        try {
        	
            String libName = System.mapLibraryName("JSyzygy");
            Path jarfile = Paths.get(SyzygyJNIBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File libFile = jarfile.getParent().resolve(libName).toFile();
            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + libFile);
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump(libName + " is now loaded");
            } else {
                URL classpathLibUrl = SyzygyJNIBridge.class.getClassLoader().getResource(libName);
                if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at location " + classpathLibUrl);
                if (classpathLibUrl != null && FILE_SCHEME.equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()){
                    File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                    System.load(classpathLibFile.getAbsolutePath());
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the resources directory");
                } else {
                	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                    System.loadLibrary("JSyzygy");
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }
            
            libLoaded = true;
            
        } catch (Throwable t) {
        	
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JSyzygy library " + t);
        }
        
        return libLoaded;
    }
	
	
    private static native boolean init(String path);
    private static native int getTBLargest();
    private static native int probeDTM(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn);
    private static native int probeWDL(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn);
    private static native int probeDTZ(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn);
    
    
    /**
     *
     * @return true iff the JSyzygy JNI library is loaded.
     */
    public static boolean isLibLoaded(){
        return libLoaded;
    }

    /**
     * determine if Syzygy tablebases are available for the supplied number of pieces (including kings)
     * @param piecesLeft the number of pieces on the board
     * @return true if the JSyzygy JNI library is loaded and tablebases suitable for the supplied number of pieces are loaded.
     */
    public static boolean isAvailable(int piecesLeft){
        return libLoaded && piecesLeft <= tbLargest;
    }

    /**
     * Load the Syzygy tablebases (init in Fathom)
     * @param path the location of the tablebases
     * @return the supported size of the loaded tablebases
     */
    public static synchronized int load(String path){
    	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loading syzygy tablebases from " + path);
        if (tbLargest>0){
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Syzygy tablebases are already loaded");
            return tbLargest;
        }
        boolean result = init(path);

        if (result) {
            tbLargest = getTBLargest();
            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Syzygy tablebases loaded");
        } else {
            tbLargest = -1;
            if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Syzygy tablebases NOT loaded");
        }

        return tbLargest;
    }

    /**
     * Returns the supported size of the loaded tablebases
     * @return the supported size of the loaded tablebases
     */
    public static int getSupportedSize(){
        return tbLargest;
    }

    /**
     * Returns a result containing the Win/Draw/Loss characteristics of the position. Notes: assumes castling is no longer possible and that there is no 50 move rule.
     * @param white all white pieces (bitboard)
     * @param black all black pieces (bitboard)
     * @param kings all kings (bitboard)
     * @param queens all queens (bitboard)
     * @param rooks all rooks (bitboard)
     * @param bishops all bishops (bitboard)
     * @param knights all knights (bitboard)
     * @param pawns all pawns (bitboard)
     * @param ep the square where an En Passant capture can take place (or 0 if there is no En Passant)
     * @param turn true if white is to move, false if black is.
     * @return WDL result (see c code)
     */
    public static int probeSyzygyDTM(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn){ //NOSONAR
        return probeDTM(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }
    
    /**
     * Returns a result containing the Win/Draw/Loss characteristics of the position. Notes: assumes castling is no longer possible and that there is no 50 move rule.
     * @param white all white pieces (bitboard)
     * @param black all black pieces (bitboard)
     * @param kings all kings (bitboard)
     * @param queens all queens (bitboard)
     * @param rooks all rooks (bitboard)
     * @param bishops all bishops (bitboard)
     * @param knights all knights (bitboard)
     * @param pawns all pawns (bitboard)
     * @param ep the square where an En Passant capture can take place (or 0 if there is no En Passant)
     * @param turn true if white is to move, false if black is.
     * @return WDL result (see c code)
     */
    public static int probeSyzygyWDL(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn){ //NOSONAR
        return probeWDL(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }

    /**
     * Returns a result containing the distance to zero (and a move that will decrease the distance to zero). Note: assumes castling is no longer possible.
     * @param white all white pieces (bitboard)
     * @param black all black pieces (bitboard)
     * @param kings all kings (bitboard)
     * @param queens all queens (bitboard)
     * @param rooks all rooks (bitboard)
     * @param bishops all bishops (bitboard)
     * @param knights all knights (bitboard)
     * @param pawns all pawns (bitboard)
     * @param rule50 The 50-move half-move clock
     * @param ep the square where an En Passant capture can take place (or 0 if there is no En Passant)
     * @param turn true if white is to move, false if black is.
     * @return DTZ result (see c code)
     */
    public static int probeSyzygyDTZ(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn){ //NOSONAR
        return probeDTZ(white, black, kings, queens, rooks, bishops, knights, pawns, rule50, ep, turn);
    }
}
