package bagaturchess.nnue_v3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v2.NNUEProbeUtils;

public class NNUE_Unquantised {
	
    // Net arch: (768 -> L1_SIZE) x 2 -> (L2_SIZE -> L3_SIZE -> 1) x OUTPUT_BUCKETS
    private static final int NUM_INPUTS = 768;
    private static final int L1_SIZE = 1536;
    private static final int L2_SIZE = 16;
    private static final int L3_SIZE = 32;
    private static final int OUTPUT_BUCKETS = 1;
    
    private static final int NET_SCALE = 400;

	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private static final int COLOR_STRIDE = 64 * 6;
	private static final int PIECE_STRIDE = 64;
	
	private static final float CLIPPED_MAX = 1.0f;
	
    public static class Network {
        public float[] FTWeights = new float[NUM_INPUTS * L1_SIZE];
        public float[] FTBiases = new float[L1_SIZE];
        public float[][] L1Weights = new float[OUTPUT_BUCKETS][2 * L1_SIZE * L2_SIZE];
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

    public static class Accumulator {
        public float[][] values = new float[2][L1_SIZE];
    }
    
    public void init(String filename) throws IOException {

        File file = new File(filename);
        	
		byte[] evalData = Files.readAllBytes(Paths.get(file.toURI()));
    
		ByteBuffer buffer = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);

        //System.out.println("Buffer capacity: " + buffer.capacity() + " bytes");
        
        FloatBuffer fbuffer = buffer.asFloatBuffer();
        
        
        UnquantisedNetwork unquantisedNet = new UnquantisedNetwork();
        
        fbuffer.get(unquantisedNet.FTWeights);
        
        fbuffer.get(unquantisedNet.FTBiases);
        
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
            
        	for (int i = 0; i < 2 * L1_SIZE; i++) {
            	fbuffer.get(unquantisedNet.L1Weights[i][bucket]);

            }
        	
            fbuffer.get(unquantisedNet.L1Biases[bucket]);
        }
        
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        	
            for (int i = 0; i < L2_SIZE; i++) {
            	fbuffer.get(unquantisedNet.L2Weights[i][bucket]);
            }

            fbuffer.get(unquantisedNet.L2Biases[bucket]);
        }
        
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        	
            for (int i = 0; i < L3_SIZE; i++) {
            	unquantisedNet.L3Weights[i][bucket] = fbuffer.get();
            }
            
            unquantisedNet.L3Biases[bucket] = fbuffer.get();
        }
        
        // Quantize FT Weights and Biases
        for (int i = 0; i < NUM_INPUTS * L1_SIZE; i++) {
            net.FTWeights[i] = unquantisedNet.FTWeights[i];
        }

        for (int i = 0; i < L1_SIZE; i++) {
            net.FTBiases[i] = unquantisedNet.FTBiases[i];
        }

        // Transpose and quantize L1, L2, and L3 weights and biases
        for (int bucket = 0; bucket < OUTPUT_BUCKETS; bucket++) {
        	
            for (int i = 0; i < 2 * L1_SIZE; i++) {
                for (int j = 0; j < L2_SIZE; j++) {
                    net.L1Weights[bucket][i * L2_SIZE + j] = unquantisedNet.L1Weights[i][bucket][j];
                }
            }

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

    public void accumulate(Accumulator boardAccumulator, int[] white_pieces, int[] white_squares, int[] black_pieces, int[] black_squares) {	
    	
    	System.arraycopy(net.FTBiases, 0, boardAccumulator.values[0], 0, L1_SIZE);
    	System.arraycopy(net.FTBiases, 0, boardAccumulator.values[1], 0, L1_SIZE);
    	
        /*for (int i = 0; i < L1_SIZE; i++) {
        	
            boardAccumulator.values[0][i] = net.FTBiases[i];
            boardAccumulator.values[1][i] = net.FTBiases[i];
        }*/
        
		for (int i = 0; i < white_pieces.length; i++) {
			int piece = white_pieces[i];
			if (piece == -1) {
				break;
			}
			int square = white_squares[i];
            int index_white = getIndex(square, WHITE, getPieceType(piece), WHITE);
            int index_black = getIndex(square, WHITE, getPieceType(piece), BLACK);
            for (int j = 0; j < L1_SIZE; j++) {
                boardAccumulator.values[0][j] += net.FTWeights[index_white * L1_SIZE + j];
                boardAccumulator.values[1][j] += net.FTWeights[index_black * L1_SIZE + j];
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
                boardAccumulator.values[0][j] += net.FTWeights[index_white * L1_SIZE + j];
                boardAccumulator.values[1][j] += net.FTWeights[index_black * L1_SIZE + j];
            }
        }
    }

    public void activateFTAndPropagateL1(float[] us, float[] them, float[] weights, float[] biases, float[] output) {
        
    	float[] sums = new float[L2_SIZE];
    	
        int weightOffset = 0;
        float[][] accs = { us, them };

        for (float[] acc : accs) {
            for (int i = 0; i < L1_SIZE; i++) {
                float clipped = Math.max(0, Math.min(acc[i], CLIPPED_MAX));
                float squared = clipped * clipped;

                for (int out = 0; out < L2_SIZE; out++) {
                    sums[out] += squared * weights[weightOffset + out * L1_SIZE + i];
                }
            }
            weightOffset += L1_SIZE * L2_SIZE;
        }

        for (int i = 0; i < L2_SIZE; i++) {
            float sumDiv = (float) 1;
            float clipped = Math.max(0.0f, Math.min((sums[i] / sumDiv) + biases[i], CLIPPED_MAX));
            output[i] = clipped * clipped;
        }
    }

    public void propagateL2(float[] inputs, float[] weights, float[] biases, float[] output) {
        float[] sums = new float[L3_SIZE];

        for (int i = 0; i < L3_SIZE; i++) {
            sums[i] = biases[i];
        }

        for (int i = 0; i < L2_SIZE; i++) {
            for (int out = 0; out < L3_SIZE; out++) {
                sums[out] += inputs[i] * weights[out * L2_SIZE + i];
            }
        }

        for (int i = 0; i < L3_SIZE; i++) {
            float clipped = Math.max(0.0f, Math.min(sums[i], CLIPPED_MAX));
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

    public int output(Accumulator boardAccumulator, int sideToMove, int outputBucket) {
        float[] L1Outputs = new float[L2_SIZE];
        float[] L2Outputs = new float[L3_SIZE];
        float[] L3Output = new float[1];

        float[] us = boardAccumulator.values[sideToMove];
        float[] them = boardAccumulator.values[1 - sideToMove];
        activateFTAndPropagateL1(us, them, net.L1Weights[outputBucket], net.L1Biases[outputBucket], L1Outputs);

        propagateL2(L1Outputs, net.L2Weights[outputBucket], net.L2Biases[outputBucket], L2Outputs);
        propagateL3(L2Outputs, net.L3Weights[outputBucket], net.L3Biases[outputBucket], L3Output);

        return (int) (L3Output[0] * NET_SCALE);
    }

	public static int getIndex(int square, int piece_side, int piece_type, int perspective)
	{
		//System.out.println("square=" + square + ", piece_side=" + piece_side + ", piece_type=" + piece_type + ", perspective=" + perspective);
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
    
    private static NNUE_Unquantised nnue = new NNUE_Unquantised();
    private static final NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
    private static final Accumulator accumulators = new Accumulator();
    
    static {
    	
        try {
        	
			nnue.init("params.bin");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    	
	private static void evaluate(String fen) throws IOException {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		nnue.accumulate(accumulators, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares);
		
		int eval = nnue.output(accumulators, bitboard.getColourToMove(), (pieces_count - 2) / 32);
		
		System.out.println("fen=" + fen + ", eval=" + eval);
	}
}
