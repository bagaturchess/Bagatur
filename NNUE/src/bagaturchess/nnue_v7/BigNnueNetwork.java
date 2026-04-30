package bagaturchess.nnue_v7;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public final class BigNnueNetwork {
	

    private static final int VERSION = 0x7AF32F20;
    private static final int OUTPUT_SCALE = 16;
    private static final int WEIGHT_SCALE_BITS = 6;
    private static final int PSQT_BUCKETS = 8;
    private static final int LAYER_STACKS = 8;
    private static final int MAX_SIMD_WIDTH = 32;

    private static final int TRANSFORMED_DIMS = 2560;
    private static final int HALF_DIMS = TRANSFORMED_DIMS / 2;
    private static final int L2 = 15;
    private static final int L3 = 32;
    private static final int DELTA = 24;

    private final FeatureTransformer featureTransformer = new FeatureTransformer(TRANSFORMED_DIMS);
    private final Architecture[] stacks = new Architecture[LAYER_STACKS];
    
    private final ThreadLocal<Workspace> workspaces = new ThreadLocal<Workspace>() {
        @Override
        protected Workspace initialValue() {
            return new Workspace();
        }
    };
    
    private String description;

    private BigNnueNetwork() {
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Architecture(TRANSFORMED_DIMS, L2, L3);
        }
    }

    public static BigNnueNetwork load(File file) throws IOException {
        BigNnueNetwork net = new BigNnueNetwork();
        LittleEndianDataInput in = new LittleEndianDataInput(new FileInputStream(file));
        try {
            net.read(in);
        } finally {
            in.close();
        }
        return net;
    }

    public String description() {
        return description;
    }

    public Workspace newWorkspace() {
        return new Workspace();
    }

    /** Safe fast path: validates input, then evaluates without trace allocation. */
    public int evaluate(NNUEProbeUtils.Input input) {
        validateInput(input);
        return evaluateInto(input, workspaces.get(), layerStackBucket(input), 0);
    }

    /** Safe fast path with caller-owned reusable workspace. */
    public int evaluate(NNUEProbeUtils.Input input, Workspace ws) {
        validateInput(input);
        return evaluateInto(input, ws, layerStackBucket(input), 0);
    }

    /** Unsafe hottest path: no validation, no trace allocation, caller provides pieceCount. */
    public int evaluateFast(NNUEProbeUtils.Input input, int pieceCount, Workspace ws) {
        return evaluateInto(input, ws, bucketForPieceCount(pieceCount), 0);
    }

    /** Safe fast path: validates input, then evaluates adjusted score without trace allocation. */
    public int evaluateAdjusted(NNUEProbeUtils.Input input) {
        validateInput(input);
        return evaluateInto(input, workspaces.get(), layerStackBucket(input), 1);
    }

    /** Safe fast path with caller-owned reusable workspace. */
    public int evaluateAdjusted(NNUEProbeUtils.Input input, Workspace ws) {
        validateInput(input);
        return evaluateInto(input, ws, layerStackBucket(input), 1);
    }

    /** Unsafe hottest path: no validation, no trace allocation, caller provides pieceCount. */
    public int evaluateAdjustedFast(NNUEProbeUtils.Input input, int pieceCount, Workspace ws) {
        return evaluateInto(input, ws, bucketForPieceCount(pieceCount), 1);
    }

    private int evaluateInto(NNUEProbeUtils.Input input, Workspace ws, int bucket, int mode) {
        int psqtInternal = featureTransformer.transformFromScratch(input, ws.transformed, bucket, ws);
        int positionalInternal = stacks[bucket].propagate(ws.transformed, ws);
        if (mode == 0) {
            return (psqtInternal + positionalInternal) / OUTPUT_SCALE;
        }
        return ((1024 - DELTA) * psqtInternal + (1024 + DELTA) * positionalInternal)
                / (1024 * OUTPUT_SCALE);
    }

    public static int layerStackBucket(NNUEProbeUtils.Input input) {
        return bucketForPieceCount(pieceCount(input));
    }

    public static int bucketForPieceCount(int pieces) {
        int bucket = (pieces - 1) / 4;
        if (bucket < 0) bucket = 0;
        if (bucket >= LAYER_STACKS) bucket = LAYER_STACKS - 1;
        return bucket;
    }

    public static int pieceCount(NNUEProbeUtils.Input input) {
        int count = 0;
        while (count < input.pieces.length && input.pieces[count] != 0) {
            count++;
        }
        return count;
    }

    private void read(LittleEndianDataInput in) throws IOException {
        long version = in.readU32();
        long hash = in.readU32();
        int descSize = in.readI32();
        if ((int) version != VERSION) {
            throw new IOException("Unsupported NNUE version: 0x" + Long.toHexString(version));
        }
        int expectedNetworkHash = networkHash();
        if ((int) hash != expectedNetworkHash) {
            throw new IOException("Big-net hash mismatch. Expected 0x" + Integer.toHexString(expectedNetworkHash)
                    + " got 0x" + Long.toHexString(hash));
        }
        description = in.readString(descSize);

        readSectionHeader(in, featureTransformerHash());
        featureTransformer.read(in);

        int archHash = architectureHash();
        for (int i = 0; i < LAYER_STACKS; i++) {
            readSectionHeader(in, archHash);
            stacks[i].read(in);
        }

        if (in.peek() != -1) {
            throw new IOException("Trailing data after end of big NNUE file");
        }
    }

    private static void readSectionHeader(LittleEndianDataInput in, int expected) throws IOException {
        int got = in.readI32();
        if (got != expected) {
            throw new IOException("Section hash mismatch. Expected 0x" + Integer.toHexString(expected)
                    + " got 0x" + Integer.toHexString(got));
        }
    }

    private static int featureTransformerHash() {
        return HalfKAv2FeatureSet.HASH_VALUE ^ (TRANSFORMED_DIMS * 2);
    }

    private static int architectureHash() {
        int h = 0xEC42E90D;
        h ^= TRANSFORMED_DIMS * 2;
        h = affineHash(h, L2 + 1);
        h = activationHash(h);
        h = affineHash(h, L3);
        h = activationHash(h);
        h = affineHash(h, 1);
        return h;
    }

    private static int networkHash() {
        return featureTransformerHash() ^ architectureHash();
    }

    private static int affineHash(int prev, int outputs) {
        int h = 0xCC03DAE4;
        h += outputs;
        h ^= prev >>> 1;
        h ^= prev << 31;
        return h;
    }

    private static int activationHash(int prev) {
        int h = 0x538D24C7;
        h += prev;
        return h;
    }

    private static void validateInput(NNUEProbeUtils.Input input) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (input.color != 0 && input.color != 1) {
            throw new IllegalArgumentException("color must be 0 or 1, got " + input.color);
        }
        if (input.pieces[0] != 6 || input.pieces[1] != 14) {
            throw new IllegalArgumentException("pieces[0] must be white king (6) and pieces[1] must be black king (14)");
        }
        for (int i = 0; i < input.pieces.length; i++) {
            int pc = input.pieces[i];
            if (pc == 0) {
                return;
            }
            int sq = input.squares[i];
            if (sq < 0 || sq >= 64) {
                throw new IllegalArgumentException("square out of range at index " + i + ": " + sq);
            }
        }
        throw new IllegalArgumentException("Input missing terminator piece==0");
    }

    public static final class Workspace {
        final byte[] transformed = new byte[TRANSFORMED_DIMS];
        final short[] accWhite = new short[TRANSFORMED_DIMS];
        final short[] accBlack = new short[TRANSFORMED_DIMS];
        final int[] fc0Out = new int[L2 + 1];
        final byte[] sqr0 = new byte[L2];
        final byte[] relu0 = new byte[L2];
        final int[] fc1Out = new int[L3];
        final byte[] fc2In = new byte[L3];
    }

    private static final class FeatureTransformer {
        private final int halfDims;
        private final short[] biases;
        private final short[] weights;
        private final int[] psqtWeights;
        private final int[] weightOffsets;

        FeatureTransformer(int halfDims) {
            this.halfDims = halfDims;
            this.biases = new short[halfDims];
            this.weights = new short[HalfKAv2FeatureSet.DIMENSIONS * halfDims];
            this.psqtWeights = new int[HalfKAv2FeatureSet.DIMENSIONS * PSQT_BUCKETS];
            this.weightOffsets = new int[HalfKAv2FeatureSet.DIMENSIONS];
            for (int i = 0; i < weightOffsets.length; i++) {
                weightOffsets[i] = i * halfDims;
            }
        }

        void read(LittleEndianDataInput in) throws IOException {
            int[] b = in.readLeb128IntArray(biases.length);
            for (int i = 0; i < b.length; i++) {
                biases[i] = (short) b[i];
            }

            int[] w = in.readLeb128IntArray(weights.length);
            for (int i = 0; i < w.length; i++) {
                weights[i] = (short) w[i];
            }

            int[] p = in.readLeb128IntArray(psqtWeights.length);
            System.arraycopy(p, 0, psqtWeights, 0, p.length);
        }

        int transformFromScratch(NNUEProbeUtils.Input input, byte[] output, int bucket, Workspace ws) {
            System.arraycopy(biases, 0, ws.accWhite, 0, halfDims);
            System.arraycopy(biases, 0, ws.accBlack, 0, halfDims);

            final int whiteKingSquare = input.squares[0];
            final int blackKingSquare = input.squares[1];
            final int[] pieces = input.pieces;
            final int[] squares = input.squares;
            final short[] accW = ws.accWhite;
            final short[] accB = ws.accBlack;
            final short[] localWeights = weights;
            final int[] localPsqtWeights = psqtWeights;
            int psqtW = 0;
            int psqtB = 0;

            final int[] lutW = HalfKAv2FeatureSet.FEATURE_INDEX[0][whiteKingSquare];
            final int[] lutB = HalfKAv2FeatureSet.FEATURE_INDEX[1][blackKingSquare];

            for (int i = 0; i < pieces.length; i++) {
                final int piece = pieces[i];
                if (piece == 0) {
                    break;
                }
                final int square = squares[i];
                final int pieceSquare = (piece << 6) | square;

                int indexW = lutW[pieceSquare];
                int offsetW = weightOffsets[indexW];
                addFeature(localWeights, offsetW, accW, halfDims);
                psqtW += localPsqtWeights[indexW * PSQT_BUCKETS + bucket];

                int indexB = lutB[pieceSquare];
                int offsetB = weightOffsets[indexB];
                addFeature(localWeights, offsetB, accB, halfDims);
                psqtB += localPsqtWeights[indexB * PSQT_BUCKETS + bucket];
            }

            final int stm = input.color;
            final int psqt = ((stm == 0 ? psqtW - psqtB : psqtB - psqtW)) / 2;
            final short[] first = (stm == 0 ? accW : accB);
            final short[] second = (stm == 0 ? accB : accW);

            for (int j = 0; j < HALF_DIMS; j++) {
                int sum0 = clamp(first[j], 0, 127);
                int sum1 = clamp(first[j + HALF_DIMS], 0, 127);
                output[j] = (byte) ((sum0 * sum1) >> 7);
            }
            for (int j = 0; j < HALF_DIMS; j++) {
                int sum0 = clamp(second[j], 0, 127);
                int sum1 = clamp(second[j + HALF_DIMS], 0, 127);
                output[HALF_DIMS + j] = (byte) ((sum0 * sum1) >> 7);
            }
            return psqt;
        }

        private static void addFeature(short[] w, int offset, short[] acc, int len) {
            int j = 0;
            for (; j + 7 < len; j += 8) {
                acc[j    ] = (short) (acc[j    ] + w[offset + j    ]);
                acc[j + 1] = (short) (acc[j + 1] + w[offset + j + 1]);
                acc[j + 2] = (short) (acc[j + 2] + w[offset + j + 2]);
                acc[j + 3] = (short) (acc[j + 3] + w[offset + j + 3]);
                acc[j + 4] = (short) (acc[j + 4] + w[offset + j + 4]);
                acc[j + 5] = (short) (acc[j + 5] + w[offset + j + 5]);
                acc[j + 6] = (short) (acc[j + 6] + w[offset + j + 6]);
                acc[j + 7] = (short) (acc[j + 7] + w[offset + j + 7]);
            }
            for (; j < len; j++) {
                acc[j] = (short) (acc[j] + w[offset + j]);
            }
        }
    }

    private static final class Architecture {
        private final int l2;
        private final int l3;
        private final AffineSparse fc0;
        private final Affine fc1;
        private final Affine fc2;

        Architecture(int transformedDims, int l2, int l3) {
            this.l2 = l2;
            this.l3 = l3;
            this.fc0 = new AffineSparse(transformedDims, l2 + 1);
            this.fc1 = new Affine(l2 * 2, l3);
            this.fc2 = new Affine(l3, 1);
        }

        void read(LittleEndianDataInput in) throws IOException {
            fc0.read(in);
            fc1.read(in);
            fc2.read(in);
        }

        int propagate(byte[] transformedFeatures, Workspace ws) {
            fc0.propagate16(transformedFeatures, ws.fc0Out);
            sqrClippedRelu(ws.fc0Out, l2, ws.sqr0);
            clippedRelu(ws.fc0Out, l2, ws.relu0);
            fc1.propagatePair(ws.sqr0, ws.relu0, ws.fc1Out);
            clippedRelu(ws.fc1Out, l3, ws.fc2In);
            int out = fc2.propagateScalar32(ws.fc2In);
            int fwdOut = ws.fc0Out[l2] * (600 * OUTPUT_SCALE) / (127 * (1 << WEIGHT_SCALE_BITS));
            return out + fwdOut;
        }
    }

    private static final class AffineSparse {
        private final int inputs;
        private final int outputs;
        private final int paddedInputs;
        private final int[] biases;
        private final byte[] weightsByInput;

        AffineSparse(int inputs, int outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.paddedInputs = ceil(inputs, MAX_SIMD_WIDTH);
            this.biases = new int[outputs];
            this.weightsByInput = new byte[paddedInputs * outputs];
        }

        void read(LittleEndianDataInput in) throws IOException {
            for (int i = 0; i < outputs; i++) {
                biases[i] = in.readI32();
            }
            byte[] tmp = new byte[outputs * paddedInputs];
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = in.readI8();
            }
            for (int o = 0; o < outputs; o++) {
                int srcBase = o * paddedInputs;
                for (int i = 0; i < inputs; i++) {
                    weightsByInput[i * outputs + o] = tmp[srcBase + i];
                }
            }
        }

        void propagate16(byte[] input, int[] out) {
            System.arraycopy(biases, 0, out, 0, outputs);
            final byte[] w = weightsByInput;
            int o0,o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15;
            o0=out[0];o1=out[1];o2=out[2];o3=out[3];o4=out[4];o5=out[5];o6=out[6];o7=out[7];
            o8=out[8];o9=out[9];o10=out[10];o11=out[11];o12=out[12];o13=out[13];o14=out[14];o15=out[15];
            for (int i = 0; i < inputs; i++) {
                int v = input[i] & 0xff;
                if (v == 0) continue;
                int base = i << 4;
                o0 += w[base] * v;
                o1 += w[base + 1] * v;
                o2 += w[base + 2] * v;
                o3 += w[base + 3] * v;
                o4 += w[base + 4] * v;
                o5 += w[base + 5] * v;
                o6 += w[base + 6] * v;
                o7 += w[base + 7] * v;
                o8 += w[base + 8] * v;
                o9 += w[base + 9] * v;
                o10 += w[base + 10] * v;
                o11 += w[base + 11] * v;
                o12 += w[base + 12] * v;
                o13 += w[base + 13] * v;
                o14 += w[base + 14] * v;
                o15 += w[base + 15] * v;
            }
            out[0]=o0;out[1]=o1;out[2]=o2;out[3]=o3;out[4]=o4;out[5]=o5;out[6]=o6;out[7]=o7;
            out[8]=o8;out[9]=o9;out[10]=o10;out[11]=o11;out[12]=o12;out[13]=o13;out[14]=o14;out[15]=o15;
        }
    }

    private static final class Affine {
        private final int inputs;
        private final int outputs;
        private final int paddedInputs;
        private final int[] biases;
        private final byte[] weights;

        Affine(int inputs, int outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.paddedInputs = ceil(inputs, MAX_SIMD_WIDTH);
            this.biases = new int[outputs];
            this.weights = new byte[outputs * paddedInputs];
        }

        void read(LittleEndianDataInput in) throws IOException {
            for (int i = 0; i < outputs; i++) {
                biases[i] = in.readI32();
            }
            for (int i = 0; i < weights.length; i++) {
                weights[i] = in.readI8();
            }
        }

        void propagatePair(byte[] input0, byte[] input1, int[] out) {
            System.arraycopy(biases, 0, out, 0, outputs);
            final byte[] w = weights;
            for (int o = 0; o < outputs; o++) {
                int sum = out[o];
                int base = o * paddedInputs;
                for (int i = 0; i < 15; i++) {
                    sum += w[base + i] * (input0[i] & 0xff);
                }
                int base2 = base + 15;
                for (int i = 0; i < 15; i++) {
                    sum += w[base2 + i] * (input1[i] & 0xff);
                }
                out[o] = sum;
            }
        }

        int propagateScalar32(byte[] input) {
            int sum = biases[0];
            final byte[] w = weights;
            for (int i = 0; i < 32; i++) {
                sum += w[i] * (input[i] & 0xff);
            }
            return sum;
        }
    }

    private static void clippedRelu(int[] input, int len, byte[] out) {
        for (int i = 0; i < len; i++) {
            out[i] = (byte) clamp(input[i] >> WEIGHT_SCALE_BITS, 0, 127);
        }
    }


    private static void sqrClippedRelu(int[] input, int len, byte[] out) {
        for (int i = 0; i < len; i++) {
            long v = input[i];
            out[i] = (byte) Math.min(127L, (v * v) >> (2 * WEIGHT_SCALE_BITS + 7));
        }
    }

    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static int ceil(int n, int base) {
        return ((n + base - 1) / base) * base;
    }
}
