package bagaturchess.nnue_v2;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;


/**
 * Experiment by probing via Bullet NNUE with 1 layers
 */
public class NNUE
{
	
	public static final boolean DO_INCREMENTAL_UPDATES = false;
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	private static final int COLOR_STRIDE = 64 * 6;
	private static final int PIECE_STRIDE = 64;

	private static final int HIDDEN_SIZE = 1024;
	private static final int FEATURE_SIZE = 768;
	private static final int OUTPUT_BUCKETS = 8;
	private static final int DIVISOR = (32 + OUTPUT_BUCKETS - 1) / OUTPUT_BUCKETS;
	private static final int INPUT_BUCKET_SIZE = 7;
	// @formatter:off
	private static final int[] INPUT_BUCKETS = new int[]
	{
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

	private final short[][] L1Weights;
	private final short[] L1Biases;
	private final short[][] L2Weights;
	private final short outputBiases[];
	
	private final static int screlu[] = new int[Short.MAX_VALUE - Short.MIN_VALUE + 1];
	
	
	private IncrementalUpdates incremental_updates;
	private DirtyPieces dirtyPieces;
	private Accumulators accumulators;
	private NNUEProbeUtils.Input input;
	private IBitBoard bitboard;
	
	
	static
	{
		for(int i = Short.MIN_VALUE; i <= Short.MAX_VALUE;i ++)
		{
			screlu[i - (int) Short.MIN_VALUE] = screlu((short)(i));
		}
	}
	
	private static int screlu(short i) {
		int v = Math.max(0, Math.min(i, QA));
		return v * v;
	}
	
	public NNUE(InputStream is, IBitBoard _bitboard) throws IOException {
		DataInputStream networkData = new DataInputStream(
				new BufferedInputStream(
						is, 16 * 4096
				)
		);
		
		L1Weights = new short[FEATURE_SIZE * INPUT_BUCKET_SIZE][HIDDEN_SIZE];

		for (int i = 0; i < FEATURE_SIZE * INPUT_BUCKET_SIZE; i++)
		{
			for (int j = 0; j < HIDDEN_SIZE; j++)
			{
				L1Weights[i][j] = toLittleEndian(networkData.readShort());
			}
		}

		L1Biases = new short[HIDDEN_SIZE];

		for (int i = 0; i < HIDDEN_SIZE; i++)
		{
			L1Biases[i] = toLittleEndian(networkData.readShort());
		}

		L2Weights = new short[OUTPUT_BUCKETS][HIDDEN_SIZE * 2];

		for (int i = 0; i < HIDDEN_SIZE * 2; i++)
		{
			for (int j = 0; j < OUTPUT_BUCKETS; j++)
			{
				L2Weights[j][i] = toLittleEndian(networkData.readShort());
			}
		}

		outputBiases = new short[OUTPUT_BUCKETS];

		for (int i = 0; i < OUTPUT_BUCKETS; i++)
		{
			outputBiases[i] = toLittleEndian(networkData.readShort());
		}
		
		//24 non zero shorts left at the end of the file
		/*for (int i = 0; i < 24; i++) {
			System.out.println(toLittleEndian(networkData.readShort()));
		}*/
		
		networkData.close();
		
		bitboard = _bitboard;
		
		accumulators = new Accumulators(this);
		
		input = new NNUEProbeUtils.Input();
		
		if (DO_INCREMENTAL_UPDATES) {
			
			dirtyPieces = new DirtyPieces();
			
			incremental_updates = new IncrementalUpdates(bitboard);
			bitboard.addMoveListener(incremental_updates);
		}
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
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		int eval = bitboard.getColourToMove() == NNUE.WHITE ?
		        evaluate(this, accumulators.getWhiteAccumulator(), accumulators.getBlackAccumulator(), pieces_count)
		        :
		        evaluate(this, accumulators.getBlackAccumulator(), accumulators.getWhiteAccumulator(), pieces_count);
		        
		return eval;
	}
	
	
    private void incremental_update_accumulators() {
		
		for (int i = 0; i < dirtyPieces.dirtyNum; i++) {
			
			if (dirtyPieces.from[i] == dirtyPieces.to[i]) {
				
				continue;
			}
			
			int piece_color = dirtyPieces.c[i];
			
			int piece = dirtyPieces.pc[i];
			
			int index_to_remove = dirtyPieces.from[i];
			
			if (dirtyPieces.from[i] < 64) {//>=64 marks no entry e.g. during capture or promotion
				
				accumulators.getWhiteAccumulator().sub(getIndex(index_to_remove, piece_color, piece, WHITE));
				accumulators.getBlackAccumulator().sub(getIndex(index_to_remove, piece_color, piece, BLACK));
			}
			
			int index_to_add = dirtyPieces.to[i];
			
			if (dirtyPieces.to[i] < 64) {
				
				accumulators.getWhiteAccumulator().add(getIndex(index_to_add, piece_color, piece, WHITE));
				accumulators.getBlackAccumulator().add(getIndex(index_to_add, piece_color, piece, BLACK));
			}
		}
		
    	incremental_updates.reset();
    }
    
    
	public static int evaluate(NNUE network, NNUEAccumulator us, NNUEAccumulator them, int pieces_count) {
		
		short[] L2Weights = network.L2Weights[chooseOutputBucket(pieces_count)];
		short[] UsValues = us.values;
		short[] ThemValues = them.values;
		
		int eval = 0;
		
		for (int i = 0; i < HIDDEN_SIZE; i++)
		{
			eval += screlu[UsValues[i] - Short.MIN_VALUE] * L2Weights[i]
					+ screlu[ThemValues[i] - Short.MIN_VALUE] * L2Weights[i + HIDDEN_SIZE];
		}
		
		//int eval = JNIUtils.evaluateVectorized(L2Weights, UsValues, ThemValues);
		
		eval /= QA;
		eval += network.outputBiases[chooseOutputBucket(pieces_count)];
		
		eval *= SCALE;
		eval /= QA * QB;
		
		return eval;
	}
	
	/*private static int evaluateVectorized(short[] L2Weights, short[] UsValues, short[] ThemValues) {
		
		int eval = 0;
		
		for (int i = 0; i < HIDDEN_SIZE; i++)
		{
			int us_value = Math.max(0, Math.min(UsValues[i], QA));
			int them_value = Math.max(0, Math.min(ThemValues[i], QA));
			
			eval += us_value * us_value * L2Weights[i]
					+ them_value * them_value * L2Weights[i + HIDDEN_SIZE];
		}
		
		return eval;
	}*/
	
	
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
	
	public static class NNUEAccumulator
	{
		private short[] values = new short[HIDDEN_SIZE];
		private int bucketIndex;
		NNUE network;

		public NNUEAccumulator(NNUE network, int bucketIndex) {
			this.network = network;
			this.bucketIndex = bucketIndex;
			System.arraycopy(network.L1Biases, 0, values, 0, HIDDEN_SIZE);
		}

		public void reset()
		{
			System.arraycopy(network.L1Biases, 0, values, 0, HIDDEN_SIZE);
		}

		public void setBucketIndex(int bucketIndex) {
			this.bucketIndex = bucketIndex;
		}

		public void add(int featureIndex) {
			for (int i = 0; i < HIDDEN_SIZE; i++)
			{
				values[i] += network.L1Weights[featureIndex + bucketIndex * FEATURE_SIZE][i];
			}
		}
		
		public void sub(int featureIndex) {
			for (int i = 0; i < HIDDEN_SIZE; i++)
			{
				values[i] -= network.L1Weights[featureIndex + bucketIndex * FEATURE_SIZE][i];
			}
		}

		public void addsub(int featureIndexToAdd, int featureIndexToSubtract) {
			for (int i = 0; i < HIDDEN_SIZE; i++)
			{
				values[i] += network.L1Weights[featureIndexToAdd + bucketIndex * FEATURE_SIZE][i]
						- network.L1Weights[featureIndexToSubtract + bucketIndex * FEATURE_SIZE][i];
			}
		}
	}
	
	
    private class IncrementalUpdates implements MoveListener {
    	
    	
    	private IBitBoard bitboard;
    	private boolean must_refresh; 
    	private int capture_marker; //Necessary because we cannot identify correctly the captured piece in addDurtyPiece
    	private int promotion_marker; //Necessary because we cannot identify correctly the captured piece in addDurtyPiece
    	
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
    		
       		int pieceType = bitboard.getMoveOps().getFigureType(move);
    		int fromFieldID = bitboard.getMoveOps().getFromFieldID(move);
    		int toFieldID = bitboard.getMoveOps().getToFieldID(move);   		
    		
    		if (pieceType == Constants.TYPE_KING
    				|| bitboard.getMoveOps().isCastling(move)
    				|| bitboard.getMoveOps().isEnpassant(move)
    				//|| bitboard.getMoveOps().isCapture(move)
    				//|| bitboard.getMoveOps().isPromotion(move)
    				) {
    			
    			must_refresh = true;
    			
    		} else {
    			
    			color = NNUEProbeUtils.convertColor(color);
    			int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    			int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    			int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    			
    			addDurtyPiece(color, piece, square_from, square_to);
    			
    			if (bitboard.getMoveOps().isCapture(move)) {
    				
    				int color_op = 1 - color;
        	        
                	int piece_captured = bitboard.getMoveOps().getCapturedFigureType(move);
                	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, color_op);
                	
                	addDurtyPiece(color_op, piece_captured, square_to, capture_marker++);
    			}
    			
    			if (bitboard.getMoveOps().isPromotion(move)) {
        	        
                	int piece_promoted = bitboard.getMoveOps().getPromotionFigureType(move);
                	piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);
                	
                	addDurtyPiece(color, piece_promoted, promotion_marker++, square_to);
                	addDurtyPiece(color, piece, square_to, promotion_marker++);
    			}
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
    		
       		int pieceType = bitboard.getMoveOps().getFigureType(move);
    		int fromFieldID = bitboard.getMoveOps().getFromFieldID(move);
    		int toFieldID = bitboard.getMoveOps().getToFieldID(move);   		
    		
    		if (pieceType == Constants.TYPE_KING
    				|| bitboard.getMoveOps().isCastling(move)
    				|| bitboard.getMoveOps().isEnpassant(move)
    				//|| bitboard.getMoveOps().isCapture(move)
    				//|| bitboard.getMoveOps().isPromotion(move)
    				) {
    			
    			must_refresh = true;
    			
    		} else {
    			
    			color = NNUEProbeUtils.convertColor(color);
    			int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    			int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    			int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    			
    			addDurtyPiece(color, piece, square_to, square_from);
    			
    			if (bitboard.getMoveOps().isCapture(move)) {
    				
    				int op_color = 1 - color;
        	        
                	int piece_captured = bitboard.getMoveOps().getCapturedFigureType(move);
                	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, op_color);
                	
                	addDurtyPiece(op_color, piece_captured, capture_marker++, square_to);
                	
                	//System.out.println("capture_marker=" + capture_marker);
    			}
    			
    			if (bitboard.getMoveOps().isPromotion(move)) {
        	        
                	int piece_promoted = bitboard.getMoveOps().getPromotionFigureType(move);
                	piece_promoted = NNUEProbeUtils.convertPiece(piece_promoted, color);
                	
                	addDurtyPiece(color, piece_promoted, square_to, promotion_marker++);
                	addDurtyPiece(color, piece, promotion_marker++, square_to);
    			}
    		}
    	}
    	
    	
    	private void addDurtyPiece(int color, int piece, int square_remove, int square_add) {
		
    		DirtyPieces dirty_pieces = dirtyPieces;
    		
    		int index = 0;
    		if (square_remove < 64 && square_add < 64) {
    			
        		for (int i = 0; i < dirty_pieces.dirtyNum; i++) {
        			if (piece == dirty_pieces.pc[i]) {
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
    				
    				throw new IllegalStateException();
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
