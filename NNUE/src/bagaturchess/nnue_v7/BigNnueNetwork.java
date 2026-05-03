package bagaturchess.nnue_v7;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public final class BigNnueNetwork {

    private static final boolean DO_INCREMENTAL_UPDATES = true;

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

    private static final String DEFAULT_NET_FILE = "./nn-b1a57edbea57.nnue";

    private static final VectorSpecies<Short> SHORT_SPECIES = ShortVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;

    private static final FeatureTransformer FEATURE_TRANSFORMER;
    private static final Architecture[] STACKS;
    private static final String DESCRIPTION;

    static {
        try {
            StaticNetworkData data = loadStaticNetwork(new File(DEFAULT_NET_FILE));
            FEATURE_TRANSFORMER = data.featureTransformer;
            STACKS = data.stacks;
            DESCRIPTION = data.description;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final IBitBoard bitboard;
    private final Workspace workspace;
    private final NNUEProbeUtils.Input input;

    private IncrementalUpdates incremental_updates;
    private DirtyPieces dirtyPieces;

    public BigNnueNetwork(IBitBoard _bitboard) {
        bitboard = _bitboard;
        workspace = new Workspace();
        input = new NNUEProbeUtils.Input();

        if (DO_INCREMENTAL_UPDATES) {
            dirtyPieces = new DirtyPieces();
            incremental_updates = new IncrementalUpdates(bitboard);
            bitboard.addMoveListener(incremental_updates);
        }
    }

    public String description() {
        return DESCRIPTION;
    }

    /** Incremental path with full-refresh fallback. */
    public int evaluate() {
        int pieceCount = bitboard.getMaterialState().getPiecesCount();
        int bucket = bucketForPieceCount(pieceCount);

        if (!DO_INCREMENTAL_UPDATES) {
            NNUEProbeUtils.fillInput(bitboard, input);
            FEATURE_TRANSFORMER.refreshFromScratch(input, workspace);
            workspace.accumulatorsValid = true;
            return evaluateFromAccumulators(input.color, workspace, bucket, 1);
        }

        if (incremental_updates.must_refresh || !workspace.accumulatorsValid) {
            NNUEProbeUtils.fillInput(bitboard, input);
            FEATURE_TRANSFORMER.refreshFromScratch(input, workspace);
            workspace.accumulatorsValid = true;
        } else {
            input.color = NNUEProbeUtils.convertColor(bitboard.getColourToMove());
            FEATURE_TRANSFORMER.updateAccumulatorsFromDirty(dirtyPieces, workspace);
        }

        int eval = evaluateFromAccumulators(input.color, workspace, bucket, 1);

        incremental_updates.reset();

        return eval;
    }

    private static int evaluateFromAccumulators(int stm, Workspace ws, int bucket, int mode) {
        int psqtInternal = FEATURE_TRANSFORMER.transformFromAccumulators(stm, ws.transformed, bucket, ws);
        int positionalInternal = STACKS[bucket].propagate(ws.transformed, ws);

        if (mode == 0) {
            return (psqtInternal + positionalInternal) / OUTPUT_SCALE;
        }

        return ((1024 - DELTA) * psqtInternal + (1024 + DELTA) * positionalInternal)
                / (1024 * OUTPUT_SCALE);
    }

    private static StaticNetworkData loadStaticNetwork(File file) throws IOException {
        FeatureTransformer ft = new FeatureTransformer(TRANSFORMED_DIMS);
        Architecture[] stacks = new Architecture[LAYER_STACKS];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Architecture(TRANSFORMED_DIMS, L2, L3);
        }

        LittleEndianDataInput in = new LittleEndianDataInput(new FileInputStream(file));
        try {
            long version = in.readU32();
            long hash = in.readU32();
            int descSize = in.readI32();

            if ((int) version != VERSION) {
                throw new IOException("Unsupported NNUE version: 0x" + Long.toHexString(version));
            }

            int expectedNetworkHash = networkHash();
            if ((int) hash != expectedNetworkHash) {
                throw new IOException(
                        "Big-net hash mismatch. Expected 0x" + Integer.toHexString(expectedNetworkHash)
                        + " got 0x" + Long.toHexString(hash));
            }

            String description = in.readString(descSize);

            readSectionHeader(in, featureTransformerHash());
            ft.read(in);

            int archHash = architectureHash();
            for (int i = 0; i < LAYER_STACKS; i++) {
                readSectionHeader(in, archHash);
                stacks[i].read(in);
            }

            if (in.peek() != -1) {
                throw new IOException("Trailing data after end of big NNUE file");
            }

            return new StaticNetworkData(description, ft, stacks);
        } finally {
            in.close();
        }
    }

    private static final class StaticNetworkData {
        final String description;
        final FeatureTransformer featureTransformer;
        final Architecture[] stacks;

        StaticNetworkData(String description, FeatureTransformer featureTransformer, Architecture[] stacks) {
            this.description = description;
            this.featureTransformer = featureTransformer;
            this.stacks = stacks;
        }
    }

    public static int layerStackBucket(NNUEProbeUtils.Input input) {
        return bucketForPieceCount(pieceCount(input));
    }

    public static int bucketForPieceCount(int pieces) {
        int bucket = (pieces - 1) / 4;
        if (bucket < 0) {
            bucket = 0;
        }
        if (bucket >= LAYER_STACKS) {
            bucket = LAYER_STACKS - 1;
        }
        return bucket;
    }

    public static int pieceCount(NNUEProbeUtils.Input input) {
        int count = 0;
        while (count < input.pieces.length && input.pieces[count] != 0) {
            count++;
        }
        return count;
    }

    private static void readSectionHeader(LittleEndianDataInput in, int expected) throws IOException {
        int got = in.readI32();
        if (got != expected) {
            throw new IOException(
                    "Section hash mismatch. Expected 0x" + Integer.toHexString(expected)
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

    @SuppressWarnings("unused")
    private static void validateInput(NNUEProbeUtils.Input input) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (input.color != 0 && input.color != 1) {
            throw new IllegalArgumentException("color must be 0 or 1, got " + input.color);
        }
        if (input.pieces[0] != 6 || input.pieces[1] != 14) {
            throw new IllegalArgumentException(
                    "pieces[0] must be white king (6) and pieces[1] must be black king (14)");
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

        final int[] psqtWhite = new int[PSQT_BUCKETS];
        final int[] psqtBlack = new int[PSQT_BUCKETS];

        int whiteKingSquare = -1;
        int blackKingSquare = -1;
        boolean accumulatorsValid;

        final int[] fc0Out = new int[L2 + 1];
        final byte[] sqr0 = new byte[L2];
        final byte[] relu0 = new byte[L2];
        final int[] fc1Out = new int[L3];
        final byte[] fc2In = new byte[L3];

        final int[] fc1In0Int = new int[L2];
        final int[] fc1In1Int = new int[L2];
        final int[] fc2InInt = new int[L3];
    }

    private static final class FeatureTransformer {
        private final int transformedDims;
        private final short[] biases;
        private final short[] weights;
        private final int[] psqtWeights;
        private final int[] weightOffsets;

        FeatureTransformer(int transformedDims) {
            this.transformedDims = transformedDims;
            this.biases = new short[transformedDims];
            this.weights = new short[HalfKAv2FeatureSet.DIMENSIONS * transformedDims];
            this.psqtWeights = new int[HalfKAv2FeatureSet.DIMENSIONS * PSQT_BUCKETS];
            this.weightOffsets = new int[HalfKAv2FeatureSet.DIMENSIONS];

            for (int i = 0; i < weightOffsets.length; i++) {
                weightOffsets[i] = i * transformedDims;
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

        void refreshFromScratch(NNUEProbeUtils.Input input, Workspace ws) {
            System.arraycopy(biases, 0, ws.accWhite, 0, transformedDims);
            System.arraycopy(biases, 0, ws.accBlack, 0, transformedDims);
            Arrays.fill(ws.psqtWhite, 0);
            Arrays.fill(ws.psqtBlack, 0);

            ws.whiteKingSquare = input.squares[0];
            ws.blackKingSquare = input.squares[1];

            final int[] pieces = input.pieces;
            final int[] squares = input.squares;

            final short[] accW = ws.accWhite;
            final short[] accB = ws.accBlack;
            final short[] localWeights = weights;
            final int[] localPsqtWeights = psqtWeights;

            final int[] lutW = HalfKAv2FeatureSet.FEATURE_INDEX[0][ws.whiteKingSquare];
            final int[] lutB = HalfKAv2FeatureSet.FEATURE_INDEX[1][ws.blackKingSquare];

            for (int i = 0; i < pieces.length; i++) {
                final int piece = pieces[i];
                if (piece == 0) {
                    break;
                }

                final int square = squares[i];
                final int pieceSquare = (piece << 6) | square;

                final int indexW = lutW[pieceSquare];
                final int indexB = lutB[pieceSquare];

                addFeature(localWeights, weightOffsets[indexW], accW, transformedDims);
                addFeature(localWeights, weightOffsets[indexB], accB, transformedDims);

                final int psqtBaseW = indexW * PSQT_BUCKETS;
                final int psqtBaseB = indexB * PSQT_BUCKETS;

                for (int b = 0; b < PSQT_BUCKETS; b++) {
                    ws.psqtWhite[b] += localPsqtWeights[psqtBaseW + b];
                    ws.psqtBlack[b] += localPsqtWeights[psqtBaseB + b];
                }
            }
        }

        void updateAccumulatorsFromDirty(DirtyPieces dirty, Workspace ws) {
            final int[] lutW = HalfKAv2FeatureSet.FEATURE_INDEX[0][ws.whiteKingSquare];
            final int[] lutB = HalfKAv2FeatureSet.FEATURE_INDEX[1][ws.blackKingSquare];

            for (int i = 0; i < dirty.dirtyNum; i++) {
                final int piece = dirty.pc[i];

                final int from = dirty.from[i];
                if (from < 64) {
                    applyDelta(piece, from, -1, lutW, lutB, ws);
                }

                final int to = dirty.to[i];
                if (to < 64) {
                    applyDelta(piece, to, +1, lutW, lutB, ws);
                }
            }
        }

        int transformFromAccumulators(int stm, byte[] output, int bucket, Workspace ws) {
            final int psqt = ((stm == 0 ? ws.psqtWhite[bucket] - ws.psqtBlack[bucket]
                                        : ws.psqtBlack[bucket] - ws.psqtWhite[bucket])) / 2;

            final short[] first = (stm == 0 ? ws.accWhite : ws.accBlack);
            final short[] second = (stm == 0 ? ws.accBlack : ws.accWhite);

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

        private void applyDelta(int piece, int square, int sign, int[] lutW, int[] lutB, Workspace ws) {
            final int pieceSquare = (piece << 6) | square;

            final int indexW = lutW[pieceSquare];
            final int indexB = lutB[pieceSquare];

            final int offsetW = weightOffsets[indexW];
            final int offsetB = weightOffsets[indexB];

            if (sign > 0) {
                addFeature(weights, offsetW, ws.accWhite, transformedDims);
                addFeature(weights, offsetB, ws.accBlack, transformedDims);
            } else {
                subFeature(weights, offsetW, ws.accWhite, transformedDims);
                subFeature(weights, offsetB, ws.accBlack, transformedDims);
            }

            final int psqtBaseW = indexW * PSQT_BUCKETS;
            final int psqtBaseB = indexB * PSQT_BUCKETS;

            if (sign > 0) {
                for (int b = 0; b < PSQT_BUCKETS; b++) {
                    ws.psqtWhite[b] += psqtWeights[psqtBaseW + b];
                    ws.psqtBlack[b] += psqtWeights[psqtBaseB + b];
                }
            } else {
                for (int b = 0; b < PSQT_BUCKETS; b++) {
                    ws.psqtWhite[b] -= psqtWeights[psqtBaseW + b];
                    ws.psqtBlack[b] -= psqtWeights[psqtBaseB + b];
                }
            }
        }

        private static void addFeature(short[] w, int offset, short[] acc, int len) {
            int i = 0;
            int upper = SHORT_SPECIES.loopBound(len);

            for (; i < upper; i += SHORT_SPECIES.length()) {
                ShortVector.fromArray(SHORT_SPECIES, acc, i)
                        .add(ShortVector.fromArray(SHORT_SPECIES, w, offset + i))
                        .intoArray(acc, i);
            }

            for (; i < len; i++) {
                acc[i] = (short) (acc[i] + w[offset + i]);
            }
        }

        private static void subFeature(short[] w, int offset, short[] acc, int len) {
            int i = 0;
            int upper = SHORT_SPECIES.loopBound(len);

            for (; i < upper; i += SHORT_SPECIES.length()) {
                ShortVector.fromArray(SHORT_SPECIES, acc, i)
                        .sub(ShortVector.fromArray(SHORT_SPECIES, w, offset + i))
                        .intoArray(acc, i);
            }

            for (; i < len; i++) {
                acc[i] = (short) (acc[i] - w[offset + i]);
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

            toUnsignedInts(ws.sqr0, l2, ws.fc1In0Int);
            toUnsignedInts(ws.relu0, l2, ws.fc1In1Int);

            fc1.propagatePair(ws.fc1In0Int, ws.fc1In1Int, ws.fc1Out);

            clippedRelu(ws.fc1Out, l3, ws.fc2In);
            toUnsignedInts(ws.fc2In, l3, ws.fc2InInt);

            int out = fc2.propagateScalar32(ws.fc2InInt);
            int fwdOut = ws.fc0Out[l2] * (600 * OUTPUT_SCALE) / (127 * (1 << WEIGHT_SCALE_BITS));
            return out + fwdOut;
        }
    }

    private static final class AffineSparse {
        private final int inputs;
        private final int outputs;
        private final int paddedInputs;
        private final int[] biases;
        private final int[] weightsByInput;

        AffineSparse(int inputs, int outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.paddedInputs = ceil(inputs, MAX_SIMD_WIDTH);
            this.biases = new int[outputs];
            this.weightsByInput = new int[paddedInputs * outputs];
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

            final int[] w = weightsByInput;
            final int upperOut = INT_SPECIES.loopBound(outputs);

            for (int i = 0; i < inputs; i++) {
                int v = input[i] & 0xff;
                if (v == 0) {
                    continue;
                }

                int base = i * outputs;
                IntVector vv = IntVector.broadcast(INT_SPECIES, v);

                int o = 0;
                for (; o < upperOut; o += INT_SPECIES.length()) {
                    IntVector.fromArray(INT_SPECIES, out, o)
                            .add(IntVector.fromArray(INT_SPECIES, w, base + o).mul(vv))
                            .intoArray(out, o);
                }

                for (; o < outputs; o++) {
                    out[o] += w[base + o] * v;
                }
            }
        }
    }

    private static final class Affine {
        private final int inputs;
        private final int outputs;
        private final int paddedInputs;
        private final int[] biases;
        private final int[] weights;

        Affine(int inputs, int outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.paddedInputs = ceil(inputs, MAX_SIMD_WIDTH);
            this.biases = new int[outputs];
            this.weights = new int[outputs * paddedInputs];
        }

        void read(LittleEndianDataInput in) throws IOException {
            for (int i = 0; i < outputs; i++) {
                biases[i] = in.readI32();
            }
            for (int i = 0; i < weights.length; i++) {
                weights[i] = in.readI8();
            }
        }

        void propagatePair(int[] input0, int[] input1, int[] out) {
            System.arraycopy(biases, 0, out, 0, outputs);

            final int[] w = weights;
            final int split = inputs >>> 1;
            final int upper = INT_SPECIES.loopBound(split);

            for (int o = 0; o < outputs; o++) {
                int sum = out[o];
                int base = o * paddedInputs;

                IntVector acc0 = IntVector.zero(INT_SPECIES);
                int i = 0;
                for (; i < upper; i += INT_SPECIES.length()) {
                    acc0 = acc0.add(
                            IntVector.fromArray(INT_SPECIES, w, base + i)
                                    .mul(IntVector.fromArray(INT_SPECIES, input0, i)));
                }
                sum += acc0.reduceLanes(VectorOperators.ADD);
                for (; i < split; i++) {
                    sum += w[base + i] * input0[i];
                }

                int base2 = base + split;
                IntVector acc1 = IntVector.zero(INT_SPECIES);
                i = 0;
                for (; i < upper; i += INT_SPECIES.length()) {
                    acc1 = acc1.add(
                            IntVector.fromArray(INT_SPECIES, w, base2 + i)
                                    .mul(IntVector.fromArray(INT_SPECIES, input1, i)));
                }
                sum += acc1.reduceLanes(VectorOperators.ADD);
                for (; i < split; i++) {
                    sum += w[base2 + i] * input1[i];
                }

                out[o] = sum;
            }
        }

        int propagateScalar32(int[] input) {
            int sum = biases[0];
            final int[] w = weights;

            IntVector acc = IntVector.zero(INT_SPECIES);
            int i = 0;
            int upper = INT_SPECIES.loopBound(inputs);

            for (; i < upper; i += INT_SPECIES.length()) {
                acc = acc.add(
                        IntVector.fromArray(INT_SPECIES, w, i)
                                .mul(IntVector.fromArray(INT_SPECIES, input, i)));
            }

            sum += acc.reduceLanes(VectorOperators.ADD);

            for (; i < inputs; i++) {
                sum += w[i] * input[i];
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

    private static void toUnsignedInts(byte[] input, int len, int[] out) {
        for (int i = 0; i < len; i++) {
            out[i] = input[i] & 0xff;
        }
    }

    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static int ceil(int n, int base) {
        return ((n + base - 1) / base) * base;
    }

    private final class IncrementalUpdates implements MoveListener {

        private final IBitBoard bitboard;
        private boolean must_refresh;
        private int capture_marker;
        private int promotion_marker;

        IncrementalUpdates(IBitBoard _bitboard) {
            bitboard = _bitboard;
            must_refresh = true;
            capture_marker = 64;
            promotion_marker = 128;
        }

        int all;
        int refreshes;

        void reset() {
            all++;
            if (must_refresh) {
                refreshes++;
            }

            must_refresh = false;
            dirtyPieces.dirtyNum = 0;
            capture_marker = 64;
            promotion_marker = 128;
        }

        public final void preForwardMove(int color, int move) {
        }

        public final void postForwardMove(int color, int move) {
            if (2 * dirtyPieces.dirtyNum >= bitboard.getMaterialState().getPiecesCount()) {
                must_refresh = true;
            }

            if (must_refresh) {
                return;
            }

            int pieceType = bitboard.getMoveOps().getFigureType(move);
            int fromFieldID = bitboard.getMoveOps().getFromFieldID(move);
            int toFieldID = bitboard.getMoveOps().getToFieldID(move);

            if (pieceType == Constants.TYPE_KING
                    || bitboard.getMoveOps().isCastling(move)
                    || bitboard.getMoveOps().isEnpassant(move)) {

                must_refresh = true;

            } else {
                color = NNUEProbeUtils.convertColor(color);
                int piece = NNUEProbeUtils.convertPiece(pieceType, color);
                int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
                int square_to = NNUEProbeUtils.convertSquare(toFieldID);

                addDirtyPiece(color, piece, square_from, square_to);

                if (bitboard.getMoveOps().isCapture(move)) {
                    int color_op = 1 - color;
                    int piece_captured = bitboard.getMoveOps().getCapturedFigureType(move);
                    piece_captured = NNUEProbeUtils.convertPiece(piece_captured, color_op);
                    addDirtyPiece(color_op, piece_captured, square_to, capture_marker++);
                }

                if (bitboard.getMoveOps().isPromotion(move)) {
                    int piece_promoted = bitboard.getMoveOps().getPromotionFigureType(move);
                    piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);

                    addDirtyPiece(color, piece_promoted, promotion_marker, square_to);
                    addDirtyPiece(color, piece, square_to, promotion_marker);
                    promotion_marker++;
                }
            }
        }

        public final void preBackwardMove(int color, int move) {
        }

        public final void postBackwardMove(int color, int move) {
            if (2 * dirtyPieces.dirtyNum >= bitboard.getMaterialState().getPiecesCount()) {
                must_refresh = true;
            }

            if (must_refresh) {
                return;
            }

            int pieceType = bitboard.getMoveOps().getFigureType(move);
            int fromFieldID = bitboard.getMoveOps().getFromFieldID(move);
            int toFieldID = bitboard.getMoveOps().getToFieldID(move);

            if (pieceType == Constants.TYPE_KING
                    || bitboard.getMoveOps().isCastling(move)
                    || bitboard.getMoveOps().isEnpassant(move)) {

                must_refresh = true;

            } else {
                color = NNUEProbeUtils.convertColor(color);
                int piece = NNUEProbeUtils.convertPiece(pieceType, color);
                int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
                int square_to = NNUEProbeUtils.convertSquare(toFieldID);

                addDirtyPiece(color, piece, square_to, square_from);

                if (bitboard.getMoveOps().isCapture(move)) {
                    int op_color = 1 - color;
                    int piece_captured = bitboard.getMoveOps().getCapturedFigureType(move);
                    piece_captured = NNUEProbeUtils.convertPiece(piece_captured, op_color);
                    addDirtyPiece(op_color, piece_captured, capture_marker++, square_to);
                }

                if (bitboard.getMoveOps().isPromotion(move)) {
                    int piece_promoted = bitboard.getMoveOps().getPromotionFigureType(move);
                    piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);

                    addDirtyPiece(color, piece_promoted, square_to, promotion_marker);
                    addDirtyPiece(color, piece, promotion_marker, square_to);
                    promotion_marker++;
                }
            }
        }

        private void addDirtyPiece(int color, int piece, int square_remove, int square_add) {
            DirtyPieces dirty_pieces = dirtyPieces;

            int index = 0;
            if (square_remove < 64 && square_add < 64) {
                for (int i = 0; i < dirty_pieces.dirtyNum; i++) {
                    if (piece == dirty_pieces.pc[i] && color == dirty_pieces.c[i]) {
                        if (square_remove == dirty_pieces.to[i]) {
                            break;
                        }
                    }
                    index++;
                }
            } else {
                index = dirty_pieces.dirtyNum;
            }

            if (index < dirty_pieces.dirtyNum) {
                if (dirty_pieces.c[index] != color) {
                    throw new IllegalStateException("dirty_pieces.c[index]=" + dirty_pieces.c[index] + ", color=" + color);
                }

                if (dirty_pieces.to[index] != square_remove) {
                    throw new IllegalStateException("dirty_pieces.to[index]=" + dirty_pieces.to[index]
                            + ", square_from=" + square_remove + ", piece=" + piece);
                }

                dirty_pieces.to[index] = square_add;

            } else {
                dirty_pieces.dirtyNum++;
                dirty_pieces.c[index] = color;
                dirty_pieces.pc[index] = piece;
                dirty_pieces.from[index] = square_remove;
                dirty_pieces.to[index] = square_add;
            }
        }

        public final void addPiece_Special(int color, int type) {
        }

        public final void initially_addPiece(int color, int type, long bb_pieces) {
        }
    }

    private static final class DirtyPieces {
        int dirtyNum;
        int[] c = new int[300];
        int[] pc = new int[300];
        int[] from = new int[300];
        int[] to = new int[300];
    }
}