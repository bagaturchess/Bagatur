package bagaturchess.nnue_v6;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class NNUEInference {

    public static final int INPUT_DIM = 3072;
    public static final int HIDDEN_DIM = 256;
    public static final int FINAL_DIM = 32;
    public static final int OUTPUT_DIM = 1;

    private final AffineTransformSparseInput layer1 = new AffineTransformSparseInput(INPUT_DIM, HIDDEN_DIM);
    private final ClippedReLU relu1 = new ClippedReLU(HIDDEN_DIM);
    private final AffineTransform layer2 = new AffineTransform(HIDDEN_DIM, FINAL_DIM);
    private final SqrClippedReLU relu2 = new SqrClippedReLU(FINAL_DIM);
    private final AffineTransform layer3 = new AffineTransform(FINAL_DIM, OUTPUT_DIM);

    public boolean loadNetwork(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
        return layer1.readParameters(dis) &&
               layer2.readParameters(dis) &&
               layer3.readParameters(dis);
    }

    public int evaluate(byte[] input) {
        int[] buf1 = new int[HIDDEN_DIM];
        byte[] buf2 = new byte[HIDDEN_DIM];
        int[] buf3 = new int[FINAL_DIM];
        byte[] buf4 = new byte[FINAL_DIM];
        int[] output = new int[OUTPUT_DIM];

        layer1.propagateSparse(input, buf1);
        relu1.propagate(buf1, buf2);
        layer2.propagate(buf2, buf3);
        relu2.propagate(buf3, buf4);
        layer3.propagate(buf4, output);

        return output[0];
    }

    public static class AffineTransformSparseInput {
        private final int outDim;
        private final Map<Integer, Integer>[] weights; // input index -> weight
        private final int[] biases;

        @SuppressWarnings("unchecked")
        public AffineTransformSparseInput(int inDim, int outDim) {
            this.outDim = outDim;
            this.biases = new int[outDim];
            this.weights = new Map[outDim];
            for (int i = 0; i < outDim; ++i) {
                weights[i] = new HashMap<>();
            }
        }

        public boolean readParameters(DataInputStream in) throws IOException {
            for (int i = 0; i < outDim; ++i)
                biases[i] = readIntLE(in);

            for (int i = 0; i < outDim; ++i) {
                for (int j = 0; j < INPUT_DIM; ++j) {
                    byte w = in.readByte();
                    if (w != 0) {
                        weights[i].put(j, (int) w);
                    }
                }
            }
            return true;
        }

        public void propagateSparse(byte[] input, int[] output) {
            Arrays.fill(output, 0);
            for (int i = 0; i < outDim; ++i) {
                int sum = biases[i];
                for (Map.Entry<Integer, Integer> entry : weights[i].entrySet()) {
                    int j = entry.getKey();
                    int w = entry.getValue();
                    if (input[j] != 0) {
                        sum += w;
                    }
                }
                output[i] = sum;
            }
        }
    }

    public static class AffineTransform {
        private final int inDim, outDim;
        private final int[][] weights;
        private final int[] biases;

        public AffineTransform(int inDim, int outDim) {
            this.inDim = inDim;
            this.outDim = outDim;
            this.weights = new int[outDim][inDim];
            this.biases = new int[outDim];
        }

        public boolean readParameters(DataInputStream in) throws IOException {
            for (int i = 0; i < outDim; ++i)
                biases[i] = readIntLE(in);
            for (int i = 0; i < outDim; ++i)
                for (int j = 0; j < inDim; ++j)
                    weights[i][j] = in.readByte();
            return true;
        }

        public void propagate(byte[] input, int[] output) {
            for (int i = 0; i < outDim; ++i) {
                int sum = biases[i];
                for (int j = 0; j < inDim; ++j)
                    sum += weights[i][j] * (input[j] & 0xFF);
                output[i] = sum;
            }
        }
    }

    public static class ClippedReLU {
        private final int dim;
        public ClippedReLU(int dim) { this.dim = dim; }
        public void propagate(int[] input, byte[] output) {
            for (int i = 0; i < dim; ++i) {
                int val = input[i] >> 6;
                output[i] = (byte) Math.max(0, Math.min(127, val));
            }
        }
    }

    public static class SqrClippedReLU {
        private final int dim;
        public SqrClippedReLU(int dim) { this.dim = dim; }
        public void propagate(int[] input, byte[] output) {
            for (int i = 0; i < dim; ++i) {
                long x = input[i];
                long sq = x * x;
                output[i] = (byte) Math.min(127, sq >> (6 * 2 + 7));
            }
        }
    }

    private static int readIntLE(DataInputStream in) throws IOException {
        byte[] buf = new byte[4];
        in.readFully(buf);
        return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
    	NNUEInference inference = new NNUEInference();
    	
		inference.loadNetwork(new FileInputStream(new File("./nn-b1a57edbea57.nnue")));
		
		IBitBoard board = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		byte[] input = new byte[INPUT_DIM];
		
		HalfKPFeatureExtractor.extract(board, input);
		
		int value = inference.evaluate(input);
		
		System.out.println(value / 256);
    }
} 
