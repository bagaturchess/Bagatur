package com.winkelhagen.chess.syzygy;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * A bridge between Java and the Fathom library to access Syzygy tablebases.
 *
 * https://github.com/ljgw
 * 
 * to compile the JSyzygy library on linux (libJSyzygy.so) the following cookbook can be executed
 * <ul><li>generate the SyzygyBridge header file</li></ul>
 * <pre>javac -cp ~/.m2/repository/org/apache/logging/log4j/log4j-api/2.11.1/log4j-api-2.11.1.jar src/main/java/com/winkelhagen/chess/syzygy/SyzygyBridge.java -h .</pre>
 * <ul><li>compile the Fathom library</li></ul>
 * <pre>Linux (ubuntu 18.04.1 LTS):
 * gcc -std=gnu99 -O2 -Wall -D TB_USE_ATOMIC -D TB_NO_HW_POP_COUNT -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o libJSyzygy.so tbprobe.c</pre>
 * <pre>Windows (10, mingw-w64\x86_64-8.1.0-win32-seh-rt_v6-rev0):
 * gcc -std=gnu99 -O2 -Wall -D TB_USE_ATOMIC -DTB_NO_HW_POP_COUNT -fPIC -I"%JAVA_HOME%/include" -I"%JAVA_HOME%/include/win32" -shared -o JSyzygy.dll tbprobe.c</pre>
 */
public class SyzygyBridge {

    private static boolean libLoaded = false;
    private static int tbLargest = 0;

    private SyzygyBridge(){}

    private static final String FILE_SCHEME = "file";

    /*
     * just loading the SyzygyBridge class will trigger loading the JSyzygy library via JNI.
     */
    static {
        try {
            String libName = System.mapLibraryName("JSyzygy");
            Path jarfile = Paths.get(SyzygyBridge.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File libFile = jarfile.getParent().resolve(libName).toFile();
            System.out.println("Looking for " + libName + " at location " + libName + " " + libFile);
            if (libFile.exists()) {
                System.load(libFile.getAbsolutePath());
                System.out.println("Loaded " + libName);
            } else {
                URL classpathLibUrl = SyzygyBridge.class.getClassLoader().getResource(libName);
                System.out.println("Looking for " + libName + " at location " + classpathLibUrl);
                if (classpathLibUrl != null && FILE_SCHEME.equalsIgnoreCase(classpathLibUrl.toURI().getScheme()) && Paths.get(classpathLibUrl.toURI()).toFile().exists()){
                    File classpathLibFile = Paths.get(classpathLibUrl.toURI()).toFile();
                    System.load(classpathLibFile.getAbsolutePath());
                    System.out.println("Loaded " + libName + " located in the resources directory");
                } else {
                	System.out.println("Looking for " + libName + " at java.library.path: " + System.getProperty("java.library.path"));
                    System.loadLibrary("JSyzygy");
                    System.out.println("Loaded " + libName + " located in the java library path");
                }
            }
            libLoaded = true;
        } catch (URISyntaxException | UnsatisfiedLinkError | RuntimeException e) {
        	System.out.println("unable to load JSyzygy library " + e);
        }
    }

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
        System.out.println("loading syzygy tablebases from " + path);
        if (tbLargest>0){
        	System.out.println("Syzygy tablebases are already loaded");
            return tbLargest;
        }
        boolean result = init(path);

        if (result) {
            tbLargest = getTBLargest();
        } else {
            tbLargest = -1;
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
    public static int probeSyzygyWDL(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int ep, boolean turn){ //NOSONAR
        return probeWDL(white, black, kings, queens, rooks, bishops, knights, pawns, ep, turn);
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


    private static native boolean init(String path);
    private static native int getTBLargest();
    private static native int probeWDL(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int ep, boolean turn); //NOSONAR
    private static native int probeDTZ(long white, long black, long kings, long queens, long rooks, long bishops, long knights, long pawns, int rule50, int ep, boolean turn); //NOSONAR

}
