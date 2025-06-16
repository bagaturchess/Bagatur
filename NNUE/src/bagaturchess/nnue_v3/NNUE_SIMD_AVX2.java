package bagaturchess.nnue_v3;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.nnue_v2.NNUEProbeUtils;
import bagaturchess.uci.api.ChannelManager;


/**
 * Experiment by probing via Bullet NNUE with 3 layers
 */
public class NNUE_SIMD_AVX2 {
	
    // Net arch: (768 -> L1_SIZE) x 2 -> (L2_SIZE -> L3_SIZE -> 1) x OUTPUT_BUCKETS
    private static final int NUM_INPUTS = 768;
    private static final int L1_SIZE = 3072;
    private static final int L2_SIZE = 15;
    private static final int L3_SIZE = 32;
    private static final int OUTPUT_BUCKETS = 1;
    
    private static final short FT_QUANT = 255;
    private static final short FT_SHIFT = 1;
    private static final short L1_QUANT = 256;
    private static final int NET_SCALE = 400;
    
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private static final int COLOR_STRIDE = 64 * 6;
	private static final int PIECE_STRIDE = 64;
	
	private static final float CLIPPED_MAX = 1.0f;
	
	
	static {
		
		loadLib();
	}

	public static void loadLib() {
		
		try {
        	
            String libName = System.mapLibraryName("SIMD_AVX2");
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
                    System.loadLibrary("SIMD_AVX2");
                    if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Loaded " + libName + " located in the java library path");
                }
            }
            
        } catch (Throwable t) {
        	
        	if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("Unable to load JNNUE library " + t);
        }
	}
	
	
    public static class Network {
        public short[] FTWeights = new short[NUM_INPUTS * L1_SIZE];
        public short[] FTBiases = new short[L1_SIZE];
        public short[][] L1Weights = new short[OUTPUT_BUCKETS][2 * L1_SIZE * L2_SIZE];
        public float[][] L1Biases = new float[OUTPUT_BUCKETS][L2_SIZE];
        public float[][] L2Weights = new float[OUTPUT_BUCKETS][L2_SIZE * L3_SIZE];
        public float[][] L2Biases = new float[OUTPUT_BUCKETS][L3_SIZE];
        public float[][] L3Weights = new float[OUTPUT_BUCKETS][L3_SIZE];
        public float[] L3Biases = new float[OUTPUT_BUCKETS];
    }

    public static class UnquantisedNetwork {
        public float[] FTWeights = new float[NUM_INPUTS * L1_SIZE];
        public float[] FTBiases = new float[L1_SIZE];
        public float[][][] L1Weights = new float[2 * L1_SIZE][OUTPUT_BUCKETS][L2_SIZE];
        public float[][] L1Biases = new float[OUTPUT_BUCKETS][L2_SIZE];
        public float[][][] L2Weights = new float[L2_SIZE][OUTPUT_BUCKETS][L3_SIZE];
        public float[][] L2Biases = new float[OUTPUT_BUCKETS][L3_SIZE];
        public float[][] L3Weights = new float[L3_SIZE][OUTPUT_BUCKETS];
        public float[] L3Biases = new float[OUTPUT_BUCKETS];
    }

    public static Network net = new Network();
    
    final int[] acc_int = new int[L1_SIZE];
    int[] sumsL2 = new int[L2_SIZE];
    float[] sumsL3 = new float[L3_SIZE];
    float[] L1Outputs = new float[L2_SIZE];
    float[] L2Outputs = new float[L3_SIZE];
    float[] L3Output = new float[1];
    
    
    public void init(String filename) throws IOException {
        
        File file = new File(filename);
        	
		byte[] evalData = Files.readAllBytes(Paths.get(file.toURI()));
    
		ByteBuffer buffer = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);

        //System.out.println("Buffer capacity: " + buffer.capacity() + " bytes");
        
        FloatBuffer fbuffer = buffer.asFloatBuffer();
        
        
        UnquantisedNetwork unquantisedNet = new UnquantisedNetwork();
        
        fbuffer.get(unquantisedNet.FTWeights);
        
        fbuffer.get(unquantisedNet.FTBiases);
        
        for (int i = 0; i < 2 * L1_SIZE; i++) {
        	for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
            	fbuffer.get(unquantisedNet.L1Weights[i][bucket]);
            }
        }
        
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
            fbuffer.get(unquantisedNet.L1Biases[bucket]);
        }
        	
        for (int i = 0; i < L2_SIZE; i++) {
        	for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        		fbuffer.get(unquantisedNet.L2Weights[i][bucket]);
        	}
        }

        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
            fbuffer.get(unquantisedNet.L2Biases[bucket]);
        }
        	
        for (int i = 0; i < L3_SIZE; i++) {
        	for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        		unquantisedNet.L3Weights[i][bucket] = fbuffer.get();
        	}
        }
        
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
            unquantisedNet.L3Biases[bucket] = fbuffer.get();
        }
        
        // Quantize FT Weights and Biases
        for (int i = 0; i < NUM_INPUTS * L1_SIZE; i++) {
            net.FTWeights[i] = (short) Math.round(unquantisedNet.FTWeights[i] * FT_QUANT);
        }

        for (int i = 0; i < L1_SIZE; i++) {
            net.FTBiases[i] = (short) Math.round(unquantisedNet.FTBiases[i] * FT_QUANT);
        }

        // Transpose and quantize L1, L2, and L3 weights and biases
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        	
        	for (int i = 0; i < 2; ++i)
                for (int j = 0; j < L2_SIZE; ++j)
                    for (int k = 0; k < L1_SIZE; ++k)
                        net.L1Weights[bucket][  i * L1_SIZE * L2_SIZE
                                              + j * L1_SIZE
                                              + k] = (short) Math.round(unquantisedNet.L1Weights[i * L1_SIZE + k][bucket][j] * L1_QUANT);
        	
            /*for (int i = 0; i < 2 * L1_SIZE; i++) {
                for (int j = 0; j < L2_SIZE; j++) {
                    net.L1Weights[bucket][i * L2_SIZE + j] = (short) Math.round(unquantisedNet.L1Weights[i][bucket][j] * L1_QUANT);
                }
            }*/

            System.arraycopy(unquantisedNet.L1Biases[bucket], 0, net.L1Biases[bucket], 0, L2_SIZE);

            for (int i = 0; i < L2_SIZE; i++) {
                for (int j = 0; j < L3_SIZE; j++) {
                    net.L2Weights[bucket][j * L2_SIZE + i] = unquantisedNet.L2Weights[i][bucket][j];
                }
            }

            System.arraycopy(unquantisedNet.L2Biases[bucket], 0, net.L2Biases[bucket], 0, L3_SIZE);

            for (int i = 0; i < L3_SIZE; i++) {
                net.L3Weights[bucket][i] = unquantisedNet.L3Weights[i][bucket];
            }

            net.L3Biases[bucket] = unquantisedNet.L3Biases[bucket];
        }
    }

    
    public native void accumulate(short[] whiteAccumulator, short[] blackAccumulator,
    		short[] FTBiases, short[] FTWeights,
    		int[] white_pieces, int[] white_squares, int[] black_pieces, int[] black_squares);
    
    
    public native void activateFTAndPropagateL1(short[] us, short[] them,
    		short[] weights, float[] biases,
    		float[] output, final int[] acc_int, final int[] sumsL2);
    
    
    public void propagateL2(float[] inputs, float[] weights, float[] biases, float[] output) {
        
    	Arrays.fill(sumsL3, 0);
    	
        for (int i = 0; i < L3_SIZE; i++) {
        	sumsL3[i] = biases[i];
        }

        for (int i = 0; i < L2_SIZE; i++) {
            for (int out = 0; out < L3_SIZE; out++) {
            	sumsL3[out] += inputs[i] * weights[out * L2_SIZE + i];
            }
        }

        for (int i = 0; i < L3_SIZE; i++) {
            float clipped = Math.max(0.0f, Math.min(sumsL3[i], CLIPPED_MAX));
            output[i] = clipped * clipped;
        }
    }

    public void propagateL3(float[] inputs, float[] weights, float bias, float[] output) {
        float sum = bias;

        for (int i = 0; i < L3_SIZE; i++) {
            sum += inputs[i] * weights[i];
        }
        output[0] = sum;
    }
    
    
    public int output(short[][] boardAccumulator, int sideToMove, int outputBucket) {
    	
    	Arrays.fill(L1Outputs, 0);
    	Arrays.fill(L2Outputs, 0);
    	Arrays.fill(L3Output, 0);

    	short[] us = boardAccumulator[sideToMove];
    	short[] them = boardAccumulator[1 - sideToMove];
        activateFTAndPropagateL1(us, them, net.L1Weights[outputBucket], net.L1Biases[outputBucket], L1Outputs, acc_int, sumsL2);

        propagateL2(L1Outputs, net.L2Weights[outputBucket], net.L2Biases[outputBucket], L2Outputs);
        propagateL3(L2Outputs, net.L3Weights[outputBucket], net.L3Biases[outputBucket], L3Output);

        return (int) (L3Output[0] * NET_SCALE);
    }

	public static int getIndex(int square, int piece_side, int piece_type, int perspective)
	{
		return perspective == WHITE
				? piece_side * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ square
				: (piece_side ^ 1) * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ (square ^ 0b111000);
	}

    private int getPieceType(int piece) {
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
    
    private static NNUE_SIMD_AVX2 nnue = new NNUE_SIMD_AVX2();
    private static final NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
    public final short[][] accumulators = new short[2][L1_SIZE];
    
    static {
    	
        try {
        	
			nnue.init("params.bin");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    
	private static void evaluate(String fen) throws IOException {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
		
		//int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		nnue.accumulate(nnue.accumulators[0], nnue.accumulators[1],
				NNUE_SIMD_AVX2.net.FTBiases, NNUE_SIMD_AVX2.net.FTWeights,
				input.white_pieces, input.white_squares, input.black_pieces, input.black_squares
			);
		
		int eval = nnue.output(nnue.accumulators, bitboard.getColourToMove(), 0);
		
		System.out.println("fen=" + fen + ", eval=" + eval);
	}
}
