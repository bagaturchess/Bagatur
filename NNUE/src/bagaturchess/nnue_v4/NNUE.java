package bagaturchess.nnue_v4;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.nnue_v2.NNUEProbeUtils;
import bagaturchess.uci.api.ChannelManager;


/**
 * Experiment by probing via Alexandria's NNUE
 */
public class NNUE {
	
    // Net arch: (768 -> L1_SIZE) x 2 -> (L2_SIZE -> L3_SIZE -> 1) x OUTPUT_BUCKETS
    private static final int NUM_INPUTS = 768;
    private static final int L1_SIZE = 1536;
    private static final int L2_SIZE = 8;
    private static final int L3_SIZE = 32;
    
    private static final short FT_QUANT = 255;
    
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private static final int COLOR_STRIDE = 64 * 6;
	private static final int PIECE_STRIDE = 64;
	
    public static float[] FTWeightsUnquantised = new float[NUM_INPUTS * L1_SIZE];
    public static float[] FTBiasesUnquantised = new float[L1_SIZE];
    public static short[] FTWeights = new short[NUM_INPUTS * L1_SIZE];
    public static short[] FTBiases = new short[L1_SIZE];
    
    public static class Accumulator {
        public short[][] values = new short[2][L1_SIZE];
    }
    
    static {
		
		loadLib();
	}

	public static void loadLib() {
		
		try {
        	
            String libName = System.mapLibraryName("nnue");
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
                    System.loadLibrary("nnue");
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }
            
        } catch (Throwable t) {
        	
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JNNUE library " + t);
        }
	}
    
    public static native int init(String file);
    		
    public static native int eval(short[] us, short[] them, int bucket);
    
    public static int eval(Accumulator boardAccumulator, int[] white_pieces, int[] white_squares, int[] black_pieces, int[] black_squares, int colorToMove, int bucket) {
    	
    	accumulate(boardAccumulator, white_pieces, white_squares, black_pieces, black_squares);
    	
    	return eval(boardAccumulator.values[colorToMove], boardAccumulator.values[1 - colorToMove], bucket);
    }
    
    public static void loadNet(String filename) throws IOException {
        
        File file = new File(filename);
        	
		byte[] evalData = Files.readAllBytes(Paths.get(file.toURI()));
    
		ByteBuffer buffer = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);
		
        FloatBuffer fbuffer = buffer.asFloatBuffer();
        
        fbuffer.get(FTWeightsUnquantised);
        
        fbuffer.get(FTBiasesUnquantised);
        
        // Quantize FT Weights and Biases
        for (int i = 0; i < NUM_INPUTS * L1_SIZE; i++) {
            FTWeights[i] = (short) Math.round(FTWeightsUnquantised[i] * FT_QUANT);
        }

        for (int i = 0; i < L1_SIZE; i++) {
            FTBiases[i] = (short) Math.round(FTBiasesUnquantised[i] * FT_QUANT);
        }
 	}
 
    private static void accumulate(Accumulator boardAccumulator, int[] white_pieces, int[] white_squares, int[] black_pieces, int[] black_squares) {	
    	
        for (int i = 0; i < L1_SIZE; i++) {
            boardAccumulator.values[0][i] = FTBiases[i];
            boardAccumulator.values[1][i] = FTBiases[i];
        }

		for (int i = 0; i < white_pieces.length; i++) {
			int piece = white_pieces[i];
			if (piece == -1) {
				break;
			}
			int square = white_squares[i];
            int index_white = getIndex(square, WHITE, getPieceType(piece), WHITE);
            int index_black = getIndex(square, WHITE, getPieceType(piece), BLACK);
            for (int j = 0; j < L1_SIZE; j++) {
                boardAccumulator.values[0][j] += FTWeights[index_white * L1_SIZE + j];
                boardAccumulator.values[1][j] += FTWeights[index_black * L1_SIZE + j];
            }
 		}
		
		for (int i = 0; i < black_pieces.length; i++) {
			int piece = black_pieces[i];
			if (piece == -1) {
				break;
			}
			int square = black_squares[i];
            int index_white = getIndex(square, BLACK, getPieceType(piece), WHITE);
            int index_black = getIndex(square, BLACK, getPieceType(piece), BLACK);
            for (int j = 0; j < L1_SIZE; j++) {
                boardAccumulator.values[0][j] += FTWeights[index_white * L1_SIZE + j];
                boardAccumulator.values[1][j] += FTWeights[index_black * L1_SIZE + j];
            }
        }
    }
    
    private static int getIndex(int square, int piece_side, int piece_type, int perspective)
	{
		return perspective == WHITE
				? piece_side * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ square
				: (piece_side ^ 1) * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ (square ^ 0b111000);
	}
	
    private static int getPieceType(int piece) {
        return piece;
    }
    
    
    public static void main(String[] args) throws IOException {
		String fen0 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen1 = "4kq2/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen2 = "4k3/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen3 = "4kq2/8/8/8/8/8/8/3K4 w - - 0 1";
		String fen4 = "4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen5 = "rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w KQkq - 0 1";
		String fen6 = "4kr2/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen7 = "4k3/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen8 = "4kr2/8/8/8/8/8/8/3K4 w - - 0 1";
        
		try {
			
			evaluate(fen0);
	        evaluate(fen1);
	        evaluate(fen2);
	        evaluate(fen3);
	        evaluate(fen4);
	        evaluate(fen5);
	        evaluate(fen6);
	        evaluate(fen7);
	        evaluate(fen8);
	        
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    
    private static final NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
    private static final Accumulator accumulators = new Accumulator();
    
    static {
    	
        try {
        	
			NNUE.init("params.bin");
			NNUE.loadNet("params.bin");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    	
	private static void evaluate(String fen) throws IOException {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		int eval = NNUE.eval(accumulators, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares, bitboard.getColourToMove(), (pieces_count - 2) / 32);
		
		System.out.println("fen=" + fen + ", eval=" + eval);
	}
}
