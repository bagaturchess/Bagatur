package bagaturchess.nnue_v2;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;


/**
 * Experiment by probing via Bullet NNUE with 1 layers
 */
public class NNUE {
	
	public static final boolean DO_INCREMENTAL_UPDATES = true;
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private static final int COLOR_STRIDE = 64 * 6;
	private static final int PIECE_STRIDE = 64;

	private static final int HIDDEN_SIZE = 1536;
	private static final int FEATURE_SIZE = 768;
	private static final int OUTPUT_BUCKETS = 8;
	private static final int DIVISOR = (32 + OUTPUT_BUCKETS - 1) / OUTPUT_BUCKETS;
	private static final int INPUT_BUCKET_SIZE = 7;
	// @formatter:off
	private static final int[] INPUT_BUCKETS = new int[] {
			0, 0, 1, 1, 2, 2, 3, 3,
			4, 4, 4, 4, 5, 5, 5, 5,
			6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6,
	};
	// @formatter:on

	private static final int SCALE = 400;
	private static final int QA = 255;
	private static final int QB = 64;

	private static final VectorSpecies<Short> SHORT_SPECIES = ShortVector.SPECIES_PREFERRED;
	private static final VectorSpecies<Integer> INT_SPECIES = SHORT_SPECIES.withLanes(int.class);

	private static short[] L1Weights;
	private static short[] L1Biases;
	private static short[][] L2Weights;
	private static int[][] L2Weights_int;
	private static short outputBiases[];
	
	static {
		
		try {
			
			InputStream is = null;
			
			File file = new File("./network_bagatur.nnue");
			
			if (file.exists()) {
				
				is = new FileInputStream(file);
				
			} else {
				
				is = NNUE.class.getResourceAsStream("/network_bagatur.nnue");
			}
			
			DataInputStream networkData = new DataInputStream(
					new BufferedInputStream(
							is, 16 * 4096
					)
			);
			
			loadNetwork(networkData);
		
			networkData.close();
			
		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
	
	
	private final static int screlu[] = new int[Short.MAX_VALUE - Short.MIN_VALUE + 1];
	
	static {
		
		for(int i = Short.MIN_VALUE; i <= Short.MAX_VALUE;i ++) {
			
			screlu[i - (int) Short.MIN_VALUE] = screlu((short)(i));
		}
	}
	
	private static int screlu(short i) {
		int v = Math.max(0, Math.min(i, QA));
		return v * v;
	}
	
	
	private IncrementalUpdates incremental_updates;
	private DirtyPieces dirtyPieces;
	private Accumulators accumulators;
	private NNUEProbeUtils.Input input;
	private IBitBoard bitboard;
	
	
	public NNUE(IBitBoard _bitboard) {
		
		bitboard = _bitboard;
		
		accumulators = new Accumulators(this);
		
		input = new NNUEProbeUtils.Input();
		
		if (DO_INCREMENTAL_UPDATES) {
			
			dirtyPieces = new DirtyPieces();
			
			incremental_updates = new IncrementalUpdates(bitboard);
			bitboard.addMoveListener(incremental_updates);
		}
	}

	
	private static void loadNetwork(DataInputStream networkData) throws IOException {
		
		L1Weights = new short[FEATURE_SIZE * INPUT_BUCKET_SIZE * HIDDEN_SIZE];

		for (int i = 0; i < FEATURE_SIZE * INPUT_BUCKET_SIZE; i++) {
			
			final int offset = i * HIDDEN_SIZE;
			
			for (int j = 0; j < HIDDEN_SIZE; j++) {
				
				L1Weights[offset + j] = toLittleEndian(networkData.readShort());
			}
		}

		L1Biases = new short[HIDDEN_SIZE];

		for (int i = 0; i < HIDDEN_SIZE; i++) {
			
			L1Biases[i] = toLittleEndian(networkData.readShort());
		}

		L2Weights = new short[OUTPUT_BUCKETS][HIDDEN_SIZE * 2];

		for (int i = 0; i < HIDDEN_SIZE * 2; i++) {
			
			for (int j = 0; j < OUTPUT_BUCKETS; j++) {
				
				L2Weights[j][i] = toLittleEndian(networkData.readShort());
			}
		}

		outputBiases = new short[OUTPUT_BUCKETS];

		for (int i = 0; i < OUTPUT_BUCKETS; i++) {

			outputBiases[i] = toLittleEndian(networkData.readShort());
		}

		L2Weights_int = new int[OUTPUT_BUCKETS][HIDDEN_SIZE * 2];
		for (int b = 0; b < OUTPUT_BUCKETS; b++) {
			for (int i = 0; i < HIDDEN_SIZE * 2; i++) {
				L2Weights_int[b][i] = L2Weights[b][i];
			}
		}

		//24 non zero shorts left at the end of the file
		/*for (int i = 0; i < 24; i++) {
			System.out.println(toLittleEndian(networkData.readShort()));
		}*/
		
		networkData.close();
	}

	
	private static short toLittleEndian(short input) {
		return (short) (((input & 0xFF) << 8) | ((input & 0xFF00) >> 8));
	}
	
	
	public int evaluate() {
		
		if (DO_INCREMENTAL_UPDATES && !incremental_updates.must_refresh) {
			
			incremental_update_accumulators();
			
		} else {
			
			NNUEProbeUtils.fillInput(bitboard, input);
			
			accumulators.fullAccumulatorUpdate(input.white_king_sq, input.black_king_sq, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares);
		}
		
		if (DO_INCREMENTAL_UPDATES) {
			
			incremental_updates.reset();
		}
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		int eval = bitboard.getColourToMove() == NNUE.WHITE ?
		        evaluate(accumulators.getWhiteAccumulator(), accumulators.getBlackAccumulator(), pieces_count)
		        :
		        evaluate(accumulators.getBlackAccumulator(), accumulators.getWhiteAccumulator(), pieces_count);
		        
		return eval;
	}
	
	
    private void incremental_update_accumulators() {
		final NNUEAccumulator whiteAcc = accumulators.getWhiteAccumulator();
		final NNUEAccumulator blackAcc = accumulators.getBlackAccumulator();

		for (int i = 0; i < dirtyPieces.dirtyNum; i++) {

			if (dirtyPieces.from[i] == dirtyPieces.to[i]) continue;

			final int piece_color = dirtyPieces.c[i];
			final int piece = dirtyPieces.pc[i];
			final int index_to_remove = dirtyPieces.from[i];
			final int index_to_add = dirtyPieces.to[i];

			if (index_to_remove < 64 && index_to_add < 64) {

				addSubWeights_both(
						whiteAcc.values,
						getL1WeightOffset(getIndex(index_to_add, piece_color, piece, WHITE), whiteAcc.bucketIndex),
						getL1WeightOffset(getIndex(index_to_remove, piece_color, piece, WHITE), whiteAcc.bucketIndex),
						blackAcc.values,
						getL1WeightOffset(getIndex(index_to_add, piece_color, piece, BLACK), blackAcc.bucketIndex),
						getL1WeightOffset(getIndex(index_to_remove, piece_color, piece, BLACK), blackAcc.bucketIndex));

			} else {

				if (index_to_remove < 64) {
					subWeights_both(
							whiteAcc.values,
							getL1WeightOffset(getIndex(index_to_remove, piece_color, piece, WHITE), whiteAcc.bucketIndex),
							blackAcc.values,
							getL1WeightOffset(getIndex(index_to_remove, piece_color, piece, BLACK), blackAcc.bucketIndex));
				}

				if (index_to_add < 64) {
					addWeights_both(
							whiteAcc.values,
							getL1WeightOffset(getIndex(index_to_add, piece_color, piece, WHITE), whiteAcc.bucketIndex),
							blackAcc.values,
							getL1WeightOffset(getIndex(index_to_add, piece_color, piece, BLACK), blackAcc.bucketIndex));
				}
			}
		}
    }
    
    
	public static int evaluate(NNUEAccumulator us, NNUEAccumulator them, int pieces_count) {
		
		int outputBucket = chooseOutputBucket(pieces_count);

		int[] L2Weights = NNUE.L2Weights_int[outputBucket];
		short[] UsValues = us.values;
		short[] ThemValues = them.values;

		int eval = evaluateVectorized(UsValues, ThemValues, L2Weights);
		
		eval /= QA;
		eval += NNUE.outputBiases[outputBucket];
		
		eval *= SCALE;
		eval /= QA * QB;
		
		return eval;
	}
	
	
	private static int evaluateVectorized(short[] UsValues, short[] ThemValues, int[] L2Weights) {

		IntVector acc0 = IntVector.zero(INT_SPECIES);
		IntVector acc1 = IntVector.zero(INT_SPECIES);
		IntVector acc2 = IntVector.zero(INT_SPECIES);
		IntVector acc3 = IntVector.zero(INT_SPECIES);

		final int shortStep = SHORT_SPECIES.length();
		final int intStep = INT_SPECIES.length();
		final int unrolledStep = shortStep * 2;

		int i = 0;
		final int unrolledBound = HIDDEN_SIZE - unrolledStep + 1;

		for (; i < unrolledBound; i += unrolledStep) {

			ShortVector usA = ShortVector.fromArray(SHORT_SPECIES, UsValues, i)
					.max((short) 0).min((short) QA);
			ShortVector themA = ShortVector.fromArray(SHORT_SPECIES, ThemValues, i)
					.max((short) 0).min((short) QA);

			IntVector usA0 = (IntVector) usA.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector usA1 = (IntVector) usA.convertShape(VectorOperators.S2I, INT_SPECIES, 1);
			IntVector themA0 = (IntVector) themA.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector themA1 = (IntVector) themA.convertShape(VectorOperators.S2I, INT_SPECIES, 1);

			IntVector usWeightsA0 = IntVector.fromArray(INT_SPECIES, L2Weights, i);
			IntVector usWeightsA1 = IntVector.fromArray(INT_SPECIES, L2Weights, i + intStep);
			IntVector themWeightsA0 = IntVector.fromArray(INT_SPECIES, L2Weights, i + HIDDEN_SIZE);
			IntVector themWeightsA1 = IntVector.fromArray(INT_SPECIES, L2Weights, i + HIDDEN_SIZE + intStep);

			acc0 = acc0.add(usA0.mul(usA0).mul(usWeightsA0)
					.add(themA0.mul(themA0).mul(themWeightsA0)));
			acc1 = acc1.add(usA1.mul(usA1).mul(usWeightsA1)
					.add(themA1.mul(themA1).mul(themWeightsA1)));

			final int j = i + shortStep;

			ShortVector usB = ShortVector.fromArray(SHORT_SPECIES, UsValues, j)
					.max((short) 0).min((short) QA);
			ShortVector themB = ShortVector.fromArray(SHORT_SPECIES, ThemValues, j)
					.max((short) 0).min((short) QA);

			IntVector usB0 = (IntVector) usB.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector usB1 = (IntVector) usB.convertShape(VectorOperators.S2I, INT_SPECIES, 1);
			IntVector themB0 = (IntVector) themB.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector themB1 = (IntVector) themB.convertShape(VectorOperators.S2I, INT_SPECIES, 1);

			IntVector usWeightsB0 = IntVector.fromArray(INT_SPECIES, L2Weights, j);
			IntVector usWeightsB1 = IntVector.fromArray(INT_SPECIES, L2Weights, j + intStep);
			IntVector themWeightsB0 = IntVector.fromArray(INT_SPECIES, L2Weights, j + HIDDEN_SIZE);
			IntVector themWeightsB1 = IntVector.fromArray(INT_SPECIES, L2Weights, j + HIDDEN_SIZE + intStep);

			acc2 = acc2.add(usB0.mul(usB0).mul(usWeightsB0)
					.add(themB0.mul(themB0).mul(themWeightsB0)));
			acc3 = acc3.add(usB1.mul(usB1).mul(usWeightsB1)
					.add(themB1.mul(themB1).mul(themWeightsB1)));
		}

		IntVector acc = acc0.add(acc1).add(acc2).add(acc3);

		final int upperBound = SHORT_SPECIES.loopBound(HIDDEN_SIZE);

		for (; i < upperBound; i += shortStep) {

			ShortVector us = ShortVector.fromArray(SHORT_SPECIES, UsValues, i)
					.max((short) 0).min((short) QA);
			ShortVector them = ShortVector.fromArray(SHORT_SPECIES, ThemValues, i)
					.max((short) 0).min((short) QA);

			IntVector us0 = (IntVector) us.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector us1 = (IntVector) us.convertShape(VectorOperators.S2I, INT_SPECIES, 1);
			IntVector them0 = (IntVector) them.convertShape(VectorOperators.S2I, INT_SPECIES, 0);
			IntVector them1 = (IntVector) them.convertShape(VectorOperators.S2I, INT_SPECIES, 1);

			IntVector usWeights0 = IntVector.fromArray(INT_SPECIES, L2Weights, i);
			IntVector usWeights1 = IntVector.fromArray(INT_SPECIES, L2Weights, i + intStep);
			IntVector themWeights0 = IntVector.fromArray(INT_SPECIES, L2Weights, i + HIDDEN_SIZE);
			IntVector themWeights1 = IntVector.fromArray(INT_SPECIES, L2Weights, i + HIDDEN_SIZE + intStep);

			acc = acc.add(us0.mul(us0).mul(usWeights0).add(them0.mul(them0).mul(themWeights0)));
			acc = acc.add(us1.mul(us1).mul(usWeights1).add(them1.mul(them1).mul(themWeights1)));
		}

		int eval = acc.reduceLanes(VectorOperators.ADD);

		for (; i < HIDDEN_SIZE; i++) {
			eval += screlu[UsValues[i] - Short.MIN_VALUE] * L2Weights[i]
					+ screlu[ThemValues[i] - Short.MIN_VALUE] * L2Weights[i + HIDDEN_SIZE];
		}

		return eval;
	}
	
	
	private static void addWeights(short[] values, int weightsOffset) {

		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;

		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i))
					.intoArray(values, i);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step))
					.intoArray(values, i + step);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step * 2))
					.intoArray(values, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step * 3))
					.intoArray(values, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i))
					.intoArray(values, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			values[i] += weights[weightsOffset + i];
		}
	}
	
	
	private static void subWeights(short[] values, int weightsOffset) {

		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;

		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i))
					.intoArray(values, i);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step))
					.intoArray(values, i + step);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 2)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step * 2))
					.intoArray(values, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 3)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i + step * 3))
					.intoArray(values, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsOffset + i))
					.intoArray(values, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			values[i] -= weights[weightsOffset + i];
		}
	}
	
	
	private static void addSubWeights(short[] values, int weightsToAddOffset, int weightsToSubOffset) {

		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;

		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToSubOffset + i))
					.intoArray(values, i);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToAddOffset + i + step))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToSubOffset + i + step))
					.intoArray(values, i + step);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToAddOffset + i + step * 2))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToSubOffset + i + step * 2))
					.intoArray(values, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, values, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToAddOffset + i + step * 3))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToSubOffset + i + step * 3))
					.intoArray(values, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, values, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, weightsToSubOffset + i))
					.intoArray(values, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			values[i] += weights[weightsToAddOffset + i] - weights[weightsToSubOffset + i];
		}
	}
	
	
	private static void addSubWeights_both(
			short[] wValues, int wAddOffset, int wSubOffset,
			short[] bValues, int bAddOffset, int bSubOffset) {
		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;
		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wSubOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bSubOffset + i))
					.intoArray(bValues, i);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wAddOffset + i + step))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wSubOffset + i + step))
					.intoArray(wValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bAddOffset + i + step))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bSubOffset + i + step))
					.intoArray(bValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wAddOffset + i + step * 2))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wSubOffset + i + step * 2))
					.intoArray(wValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bAddOffset + i + step * 2))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bSubOffset + i + step * 2))
					.intoArray(bValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wAddOffset + i + step * 3))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wSubOffset + i + step * 3))
					.intoArray(wValues, i + step * 3);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bAddOffset + i + step * 3))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bSubOffset + i + step * 3))
					.intoArray(bValues, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wSubOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bAddOffset + i))
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bSubOffset + i))
					.intoArray(bValues, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			wValues[i] += weights[wAddOffset + i] - weights[wSubOffset + i];
			bValues[i] += weights[bAddOffset + i] - weights[bSubOffset + i];
		}
	}


	private static void addWeights_both(
			short[] wValues, int wOffset,
			short[] bValues, int bOffset) {
		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;
		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i))
					.intoArray(bValues, i);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step))
					.intoArray(wValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step))
					.intoArray(bValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step * 2))
					.intoArray(wValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 2)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step * 2))
					.intoArray(bValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step * 3))
					.intoArray(wValues, i + step * 3);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 3)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step * 3))
					.intoArray(bValues, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.add(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i))
					.intoArray(bValues, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			wValues[i] += weights[wOffset + i];
			bValues[i] += weights[bOffset + i];
		}
	}


	private static void subWeights_both(
			short[] wValues, int wOffset,
			short[] bValues, int bOffset) {
		final short[] weights = L1Weights;
		final int step = SHORT_SPECIES.length();
		final int step4 = step * 4;
		int i = 0;
		for (; i <= HIDDEN_SIZE - step4; i += step4) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i))
					.intoArray(bValues, i);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step))
					.intoArray(wValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step))
					.intoArray(bValues, i + step);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 2)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step * 2))
					.intoArray(wValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 2)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step * 2))
					.intoArray(bValues, i + step * 2);
			ShortVector.fromArray(SHORT_SPECIES, wValues, i + step * 3)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i + step * 3))
					.intoArray(wValues, i + step * 3);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i + step * 3)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i + step * 3))
					.intoArray(bValues, i + step * 3);
		}
		for (; i < SHORT_SPECIES.loopBound(HIDDEN_SIZE); i += step) {
			ShortVector.fromArray(SHORT_SPECIES, wValues, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, wOffset + i))
					.intoArray(wValues, i);
			ShortVector.fromArray(SHORT_SPECIES, bValues, i)
					.sub(ShortVector.fromArray(SHORT_SPECIES, weights, bOffset + i))
					.intoArray(bValues, i);
		}
		for (; i < HIDDEN_SIZE; i++) {
			wValues[i] -= weights[wOffset + i];
			bValues[i] -= weights[bOffset + i];
		}
	}


	private static int getL1WeightOffset(int featureIndex, int bucketIndex) {
		return (featureIndex + bucketIndex * FEATURE_SIZE) * HIDDEN_SIZE;
	}
	
	
	public static int chooseOutputBucket(int pieces_count) {
		return (pieces_count - 2) / DIVISOR;
	}
	
	
	public static int chooseInputBucket(int king_sq, int side) {
		return side == WHITE ? INPUT_BUCKETS[king_sq]
				: INPUT_BUCKETS[king_sq ^ 0b111000];
	}
	
	
	public static int getIndex(int square, int piece_side, int piece_type, int perspective) {
		//System.out.println("square=" + square + ", piece_side=" + piece_side + ", piece_type=" + piece_type + ", perspective=" + perspective);
		return perspective == WHITE
				? piece_side * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ square
				: (piece_side ^ 1) * COLOR_STRIDE + piece_type * PIECE_STRIDE
						+ (square ^ 0b111000);
	}
	
	
	public static class NNUEAccumulator {
		
		private short[] values = new short[HIDDEN_SIZE];
		private int bucketIndex;
		NNUE network;

		public NNUEAccumulator(NNUE network, int bucketIndex) {
			this.network = network;
			this.bucketIndex = bucketIndex;
			System.arraycopy(NNUE.L1Biases, 0, values, 0, HIDDEN_SIZE);
		}

		public void reset() {
			
			System.arraycopy(NNUE.L1Biases, 0, values, 0, HIDDEN_SIZE);
		}

		public void setBucketIndex(int bucketIndex) {
			this.bucketIndex = bucketIndex;
		}

		public void add(int featureIndex) {
			
			addWeights(values, getL1WeightOffset(featureIndex, bucketIndex));
		}
		
		public void sub(int featureIndex) {
			
			subWeights(values, getL1WeightOffset(featureIndex, bucketIndex));
		}
		
		public void addSub(int featureIndexToAdd, int featureIndexToSub) {
			
			addSubWeights(values,
					getL1WeightOffset(featureIndexToAdd, bucketIndex),
					getL1WeightOffset(featureIndexToSub, bucketIndex));
		}
	}
	
	
    private class IncrementalUpdates implements MoveListener {
    	
    	
    	private IBitBoard bitboard;
    	private IMoveOps moveOps;
    	private boolean must_refresh; 
    	private int capture_marker; //Necessary because we cannot identify correctly the captured piece in addDurtyPiece
    	private int promotion_marker; //Necessary because we cannot identify correctly the captured piece in addDurtyPiece
    	
    	IncrementalUpdates(IBitBoard _bitboard) {
    		
    		bitboard = _bitboard;
    		moveOps = bitboard.getMoveOps();
    		must_refresh = true;
    		capture_marker = 64;
    		promotion_marker = 128;
    	}
    	
    	int all;
    	int refreshes;
    	
    	void reset() {
    		all++;
    		if (must_refresh) refreshes++;
    		if (all % 100000 == 0) {
    			//System.out.println("refreshes=" + (refreshes / (double) all));
    		}
    		
    		
    		must_refresh = false;
    		dirtyPieces.dirtyNum = 0;
    		capture_marker = 64;//reset it to not have type overflow
    		promotion_marker = 128;//reset it to not have type overflow
    	}
    	
    	
    	//@Override
    	public final void preForwardMove(int color, int move) {

    		//Do nothing
    	}
    	
    	
    	//@Override
    	public final void postForwardMove(int color, int move) {
    		
    		if (2 * dirtyPieces.dirtyNum >= bitboard.getMaterialState().getPiecesCount()) {
    			//Refresh will be faster
    			must_refresh = true;
    		}
    		
    		if (must_refresh) {
    			
    			return;
    		}
    		
    		int pieceType = moveOps.getFigureType(move);
    		int fromFieldID = moveOps.getFromFieldID(move);
    		int toFieldID = moveOps.getToFieldID(move);
    		
    		if (moveOps.isCastling(move) || moveOps.isEnpassant(move)) {
    			must_refresh = true;
    			return;
    		}
    		
    		color = NNUEProbeUtils.convertColor(color);
    		int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    		int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    		int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    		
    		if (pieceType == Constants.TYPE_KING
    				&& chooseInputBucket(square_from, color) != chooseInputBucket(square_to, color)) {
    			must_refresh = true;
    			return;
    		}
    		
    		addDurtyPiece(color, piece, square_from, square_to);
    		
    		if (moveOps.isCapture(move)) {
    			
    			int color_op = 1 - color;
        	        
            	int piece_captured = moveOps.getCapturedFigureType(move);
            	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, color_op);
            	
            	addDurtyPiece(color_op, piece_captured, square_to, capture_marker++);
    		}
    		
    		if (moveOps.isPromotion(move)) {
        	        
            	int piece_promoted = moveOps.getPromotionFigureType(move);
            	piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);
            	
            	addDurtyPiece(color, piece_promoted, promotion_marker, square_to);
            	addDurtyPiece(color, piece, square_to, promotion_marker);
            	promotion_marker++;
    		}
    	}

		//@Override
    	public final void preBackwardMove(int color, int move) {
    		//Do nothing
    	}
    	
    	//@Override
    	public final void postBackwardMove(int color, int move) {
    		
    		if (2 * dirtyPieces.dirtyNum >= bitboard.getMaterialState().getPiecesCount()) {
    			//Refresh will be faster
    			must_refresh = true;
    		}
    		
    		if (must_refresh) {
    			
    			return;
    		}
    		
    		
    		int pieceType = moveOps.getFigureType(move);
    		int fromFieldID = moveOps.getFromFieldID(move);
    		int toFieldID = moveOps.getToFieldID(move);
    		
    		if (moveOps.isCastling(move) || moveOps.isEnpassant(move)) {
    			must_refresh = true;
    			return;
    		}
    		
    		color = NNUEProbeUtils.convertColor(color);
    		int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    		int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    		int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    		
    		if (pieceType == Constants.TYPE_KING
    				&& chooseInputBucket(square_from, color) != chooseInputBucket(square_to, color)) {
    			must_refresh = true;
    			return;
    		}
    		
    		addDurtyPiece(color, piece, square_to, square_from);
    		
    		if (moveOps.isCapture(move)) {
    			
    			int op_color = 1 - color;
        	        
            	int piece_captured = moveOps.getCapturedFigureType(move);
            	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, op_color);
            	
            	addDurtyPiece(op_color, piece_captured, capture_marker++, square_to);
            	
            	//System.out.println("capture_marker=" + capture_marker);
    		}
    		
    		if (moveOps.isPromotion(move)) {
        	        
            	int piece_promoted = moveOps.getPromotionFigureType(move);
            	piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);
            	
            	addDurtyPiece(color, piece_promoted, square_to, promotion_marker);
            	addDurtyPiece(color, piece, promotion_marker, square_to);
            	promotion_marker++;
    		}
    	}
    	
    	
    	private void addDurtyPiece(int color, int piece, int square_remove, int square_add) {
		
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
    				
    				throw new IllegalStateException("dirty_pieces.to[index]=" + dirty_pieces.to[index] + ", square_from=" + square_remove + ", piece=" + piece);
    			}
        		//dirty_pieces.from[index] = square_from;
        		dirty_pieces.to[index] = square_add;
    			
    		} else {
    			
    			dirty_pieces.dirtyNum++;
    			
    			dirty_pieces.c[index] = color;
        		dirty_pieces.pc[index] = piece;
        		dirty_pieces.from[index] = square_remove;
        		dirty_pieces.to[index] = square_add;
    		}
		}
    	
    	
    	//@Override
    	public final void addPiece_Special(int color, int type) {
    		//Do nothing
    	}
    	
    	
    	//@Override
    	public final void initially_addPiece(int color, int type, long bb_pieces) {
    		
    		//Do nothing
    	}
    }
    
    
    private static class DirtyPieces {
        int dirtyNum;
        int[] c = new int[300];
        int[] pc = new int[300];
        int[] from = new int[300];
        int[] to = new int[300];
    }
}