package bagaturchess.nnue;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;

public class NNUE {

    private static final int PS_W_PAWN = 1;
    private static final int PS_B_PAWN = 1 * 64 + 1;
    private static final int PS_W_KNIGHT = 2 * 64 + 1;
    private static final int PS_B_KNIGHT = 3 * 64 + 1;
    private static final int PS_W_BISHOP = 4 * 64 + 1;
    private static final int PS_B_BISHOP = 5 * 64 + 1;
    private static final int PS_W_ROOK = 6 * 64 + 1;
    private static final int PS_B_ROOK = 7 * 64 + 1;
    private static final int PS_W_QUEEN = 8 * 64 + 1;
    private static final int PS_B_QUEEN = 9 * 64 + 1;
    private static final int PS_END = 10 * 64 + 1;

    private static final int FV_SCALE = 16;
    private static final int SHIFT = 6;

    private static final int kHalfDimensions = 256;
    private static final int FtInDims = 64 * PS_END;
    private static final int FtOutDims = kHalfDimensions * 2;

    private static final int[] PieceToIndex0 = {0, 0, PS_W_QUEEN, PS_W_ROOK, PS_W_BISHOP, PS_W_KNIGHT, PS_W_PAWN,
            0, PS_B_QUEEN, PS_B_ROOK, PS_B_BISHOP, PS_B_KNIGHT, PS_B_PAWN, 0};
    private static final int[] PieceToIndex1 = {0, 0, PS_B_QUEEN, PS_B_ROOK, PS_B_BISHOP, PS_B_KNIGHT, PS_B_PAWN,
            0, PS_W_QUEEN, PS_W_ROOK, PS_W_BISHOP, PS_W_KNIGHT, PS_W_PAWN, 0};
    private static final int[][] PieceToIndex = {PieceToIndex0, PieceToIndex1};

    private static final int[] hidden1_weights = new int[32 * 512];
    private static final int[] hidden2_weights = new int[32 * 32];
    private static final int[] output_weights = new int[1 * 32];

    private static final int[] hidden1_biases = new int[32];
    private static final int[] hidden2_biases = new int[32];
    private static final int[] output_biases = new int[1];

    private static final int[] ft_biases = new int[kHalfDimensions];
    private static final int[] ft_weights = new int[kHalfDimensions * FtInDims];

    private static final int NnueVersion = 0x7AF32F16;
    private static final int TransformerStart = 3 * 4 + 177;
    private static final int NetworkStart = TransformerStart + 4 + 2 * 256 + 2 * 256 * 64 * 641;
    

    static {
    	
        // Initialize the NNUE functions here
    	File net = new File("./nn-6b4236f2ec01.nnue");
    	//File net = new File("./nn-04cf2b4ed1da.nnue");
    	nnue_init(net.toURI());
    }
    
    private static void nnue_init(URI evalFile) {
        System.out.println("Loading NNUE : " + evalFile);
        if (load_eval_file(evalFile)) {
            System.out.println("NNUE loaded!");
        } else {
            System.out.println("NNUE file not found!");
        }
    }

    private static boolean load_eval_file(URI evalFile) {
        try {
            byte[] evalData = Files.readAllBytes(Paths.get(evalFile));
            if (!verify_net(evalData)) return false;
            init_weights(evalData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean verify_net(byte[] evalData) {
        ByteBuffer buffer = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.getInt(0) != NnueVersion) return false;
        if (buffer.getInt(4) != 0x3e5aa6ee) return false;
        if (buffer.getInt(8) != 177) return false;
        if (buffer.getInt(TransformerStart) != 0x5d69d7b8) return false;
        if (buffer.getInt(NetworkStart) != 0x63337156) return false;
        return true;
    }

    private static void init_weights(byte[] evalData) {
        ByteBuffer buffer = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);
        int offset = TransformerStart + 4;

        // Read transformer
        for (int i = 0; i < kHalfDimensions; i++) {
            ft_biases[i] = buffer.getShort(offset);
            offset += 2;
        }
        for (int i = 0; i < kHalfDimensions * FtInDims; i++) {
            ft_weights[i] = buffer.getShort(offset);
            offset += 2;
        }

        // Read network
        offset += 4;
        for (int i = 0; i < 32; i++) {
            hidden1_biases[i] = buffer.getInt(offset);
            offset += 4;
        }
        offset = read_hidden_weights(hidden1_weights, 512, offset, buffer);
        for (int i = 0; i < 32; i++) {
            hidden2_biases[i] = buffer.getInt(offset);
            offset += 4;
        }
        offset = read_hidden_weights(hidden2_weights, 32, offset, buffer);
        for (int i = 0; i < 1; i++) {
            output_biases[i] = buffer.getInt(offset);
            offset += 4;
        }
        read_output_weights(output_weights, buffer, offset);
    }

    private static int read_hidden_weights(int[] w, int dims, int offset, ByteBuffer buffer) {
        for (int r = 0; r < 32; r++) {
            for (int c = 0; c < dims; c++) {
                w[wt_idx(r, c, dims)] = buffer.get(offset++);
            }
        }
        return offset;
    }

    private static void read_output_weights(int[] w, ByteBuffer buffer, int offset) {
        for (int i = 0; i < 32; i++) {
            w[i] = buffer.get(offset++);
        }
    }

    private static int wt_idx(int r, int c, int dims) {
        return c * 32 + r;
    }
    
    
    //Instance caches    
    private int[][] activeIndices = new int[2][30]; // Adjust the size based on expected active indices
    private int[] activeSizes = new int[2]; // To track the number of active indices for each player
    
    private NetData netData;
    private Position pos;
    private MoveListener incremential_updates;
    
    public NNUE(IBitBoard bitboard) {
    	
    	netData = new NetData();
    	pos = new Position();
    	incremential_updates = new IncrementalUpdates(bitboard);
    }
    
    public int nnue_evaluate_pos(int color, int[] pieces, int[] squares, boolean incremental_updates) {
    	
    	//netData.clear();
    	
    	//TODO: comment this to skip incremental updates
		//if (!incremental_updates) {
			pos.player = color;
			pos.pieces = pieces;
			pos.squares = squares;
	        refresh_accumulator();
		//}
    	
    	
        transform(pos.player, pos.nnue[0].accumulator.accumulation, netData.input, null);

        affine_txfm(netData.input, netData.hidden1_out, FtOutDims, 32,
                hidden1_biases, hidden1_weights, null, null, true);

        affine_txfm(netData.hidden1_out, netData.hidden2_out, 32, 32,
                hidden2_biases, hidden2_weights, null, null, false);

        int out_value = affine_propagate(netData.hidden2_out, output_biases,
                output_weights);

        return out_value / FV_SCALE;
    }

    public MoveListener getIncrementalUpdates() {
    	
    	return incremential_updates;
    }
    
    private void refresh_accumulator() {
        
        activeSizes[0] = 0;
        activeSizes[1] = 0;
        append_active_indices(pos, activeIndices, activeSizes);

        Accumulator accumulator = pos.nnue[0].accumulator;
        
        for (int c = 0; c < 2; c++) {
            // Copy biases to the accumulator
            System.arraycopy(ft_biases, 0, accumulator.accumulation[c], 0, kHalfDimensions);

            // Accumulate weights based on active indices
            for (int k = 0; k < activeSizes[c]; k++) {
                int index = activeIndices[c][k];
                int offset = kHalfDimensions * index;

                for (int j = 0; j < kHalfDimensions; j++) {
                    accumulator.accumulation[c][j] += ft_weights[offset + j];
                }
            }
        }

        accumulator.computedAccumulation = true;
    }
    
    private static void transform(int color, int[][] accumulation, byte[] output, int[] outMask) {
    	
        int[] perspectives = {color, 1 - color};
        for (int p = 0; p < 2; p++) {
            int offset = kHalfDimensions * p;

            for (int i = 0; i < kHalfDimensions; i++) {
                int sum = accumulation[perspectives[p]][i];
                output[offset + i] = (byte) clamp(sum, 0, 127);
            }
        }
    }

    private static int affine_propagate(byte[] input, int[] biases, int[] weights) {
        int sum = biases[0];
        for (int j = 0; j < 32; j++) {
            sum += weights[j] * input[j];
        }
        return sum;
    }

    private static void affine_txfm(byte[] input, byte[] output, int inDims, int outDims, int[] biases, int[] weights,
                                    int[] inMask, int[] outMask, boolean pack8_and_calc_mask) {
        int[] tmp = new int[outDims];

        for (int i = 0; i < outDims; i++)
            tmp[i] = biases[i];

        for (int idx = 0; idx < inDims; idx++)
            if (input[idx] != 0)
                for (int i = 0; i < outDims; i++)
                    tmp[i] += input[idx] * weights[outDims * idx + i];

        for (int i = 0; i < outDims; i++)
            output[i] = (byte) clamp(tmp[i] >> SHIFT, 0, 127);
    }

    private static boolean update_accumulator(Position pos) {
        
        Accumulator accumulator = pos.nnue[0].accumulator;
        if (accumulator.computedAccumulation)
            return true;
        
        Accumulator prevAcc = null;
        if ((pos.nnue[1] == null || !(prevAcc = pos.nnue[1].accumulator).computedAccumulation)
                && (pos.nnue[2] == null || !(prevAcc = pos.nnue[2].accumulator).computedAccumulation))
           return false;
        
        int[][] removed_indices = new int[2][30];
        int[][] added_indices = new int[2][30];
        boolean[] reset = new boolean[2];
        append_changed_indices(pos, removed_indices, added_indices, reset);

        for (int c = 0; c < 2; c++) {
            if (reset[c]) {
                System.arraycopy(ft_biases, 0, accumulator.accumulation[c], 0, ft_biases.length);
            } else {
            	
            	// Difference calculation for the deactivated features
                System.arraycopy(prevAcc.accumulation[c], 0, accumulator.accumulation[c], 0, kHalfDimensions);

                for (int index : removed_indices[c]) {
                    int offset = kHalfDimensions * index;

                    for (int j = 0; j < kHalfDimensions; j++)
                        accumulator.accumulation[c][j] -= ft_weights[offset + j];
                }
            }

            // Difference calculation for the activated features
            for (int index : added_indices[c]) {
                int offset = kHalfDimensions * index;

                for (int j = 0; j < kHalfDimensions; j++)
                    accumulator.accumulation[c][j] += ft_weights[offset + j];
            }
        }

        accumulator.computedAccumulation = true;
        return true;
    }

    private static void append_changed_indices(Position pos, int[][] removed, int[][] added, boolean[] reset) {
        DirtyPiece dp = pos.nnue[0].dirtyPiece;
        if (pos.nnue[1].accumulator.computedAccumulation) {
            for (int c = 0; c < 2; c++) {
                reset[c] = dp.pc[0] == KING(c);
                if (reset[c])
                    half_kp_append_active_indices(pos, c, added[c], new int[]{0});
                else
                    half_kp_append_changed_indices(pos, c, dp, removed[c], added[c]);
            }
        } else {
            DirtyPiece dp2 = pos.nnue[1].dirtyPiece;
            for (int c = 0; c < 2; c++) {
                reset[c] = dp.pc[0] == KING(c) || dp2.pc[0] == KING(c);
                if (reset[c])
                    half_kp_append_active_indices(pos, c, added[c], new int[]{0});
                else {
                    half_kp_append_changed_indices(pos, c, dp, removed[c], added[c]);
                    half_kp_append_changed_indices(pos, c, dp2, removed[c], added[c]);
                }
            }
        }
    }

    // Append active indices method
    private static void append_active_indices(Position pos, int[][] active, int[] activeSizes) {
        for (int c = 0; c < 2; c++) {
            activeSizes[c] = 0;
            half_kp_append_active_indices(pos, c, active[c], activeSizes);
        }
    }

    private static void half_kp_append_active_indices(Position pos, int c, int[] active, int[] activeSizes) {
        int ksq = pos.squares[c];
        ksq = orient(c, ksq);
        for (int i = 2; pos.pieces[i] != 0; i++) {
            int sq = pos.squares[i];
            int pc = pos.pieces[i];
            active[activeSizes[c]++] = make_index(c, sq, pc, ksq);
        }
    }

    private static void half_kp_append_changed_indices(Position pos, int c, DirtyPiece dp, int[] removed, int[] added) {
        int ksq = pos.squares[c];
        ksq = orient(c, ksq);
        int removedSize = 0;
        int addedSize = 0;
        for (int i = 0; i < dp.dirtyNum; i++) {
            int pc = dp.pc[i];
            if (isKing(pc)) continue;
            if (dp.from[i] != 64)
                removed[removedSize++] = make_index(c, dp.from[i], pc, ksq);
            if (dp.to[i] != 64)
                added[addedSize++] = make_index(c, dp.to[i], pc, ksq);
        }
    }
    
    private static int make_index(int c, int s, int pc, int ksq) {
        return orient(c, s) + PieceToIndex[c][pc] + PS_END * ksq;
    }
    
    private static int orient(int c, int s) {
        return s ^ (c == 0 ? 0x00 : 0x3f);
    }
    
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    private static boolean isKing(int p) {
        return (p == PieceType.wking || p == PieceType.bking);
    }
    
    private static int KING(int c) {
        return (c == 0 ? PieceType.bking : PieceType.wking);
    }
    
    private static class NetData {
    	
        byte[] input = new byte[FtOutDims];
        byte[] hidden1_out = new byte[32];
        byte[] hidden2_out = new byte[32];
		
        public void clear() {
			Arrays.fill(input, (byte)0);
			Arrays.fill(hidden1_out, (byte)0);
			Arrays.fill(hidden2_out, (byte)0);
		}
    }

    public class PieceType {
        static final int EMPTY = 0;
        static final int wking = 1;
        static final int bking = 7;
        static final int wqueen = 2;
        static final int bqueen = 8;
        static final int wrook = 3;
        static final int brook = 9;
        static final int wbishop = 4;
        static final int bbishop = 10;
        static final int wknight = 5;
        static final int bknight = 11;
        static final int wpawn = 6;
        static final int bpawn = 12;
    }
    
    public static class Position {
        
    	NNUEData[] nnue = new NNUEData[3];
        public int player;
        public int[] pieces = new int[33];
        public int[] squares = new int[33];
        
        public Position() {
            for (int i = 0; i < nnue.length; i++) {
                nnue[i] = new NNUEData();
            }
            clear();
        }
        
        public Position(int[] _squares, int[] _pieces, int _player) {
        	this();
        	squares = _squares;
        	pieces = _pieces;
        	player = _player;
        }
        
        public void clear() {
    		nnue[0].accumulator.computedAccumulation = false;
    		nnue[1].accumulator.computedAccumulation = false;
    		nnue[2].accumulator.computedAccumulation = false;
        }
    }
    
    private static class NNUEData {
        Accumulator accumulator = new Accumulator();
        DirtyPiece dirtyPiece = new DirtyPiece();
    }

    private static class Accumulator {
        boolean computedAccumulation;
        int[][] accumulation = new int[2][256];
    }
    
    private static class DirtyPiece {
        int dirtyNum;
        int[] pc = new int[30];
        int[] from = new int[30];
        int[] to = new int[30];
    }
    
    private class IncrementalUpdates implements MoveListener {
    	
    	
    	private IBitBoard bitboard;
    	private NNUEProbeUtils.Input input;
    	
    	
    	IncrementalUpdates(IBitBoard _bitboard) {
    		
    		bitboard 	= _bitboard;
    		input 		= new NNUEProbeUtils.Input();
    		
    	}
    	
    	
    	//@Override
    	public final void preForwardMove(int color, int move) {

    		//Do nothing
    	}
    	
    	
    	//@Override
    	public final void postForwardMove(int color, int move) {
    		
    		//TODO: comment this to skip incremental updates
    		//move(move, color, bitboard);
    	}
    	
    	
    	//@Override
    	public final void preBackwardMove(int color, int move) {
    		//Do nothing
    	}
    	
    	//@Override
    	public final void postBackwardMove(int color, int move) {
    		//TODO: comment this to skip incremental updates
    		//unmove(move, color, bitboard);
    	}
    	
    	
    	//@Override
    	public final void addPiece_Special(int color, int type) {
    		//Do nothing
    	}
    	
    	
    	//@Override
    	public final void initially_addPiece(int color, int type, long bb_pieces) {
    		
    		//Do nothing
    	}
    	
    	
    	public final void move(int move, int color, IBitBoard board) {
    		
    		NNUEProbeUtils.fillInput(bitboard, input);
			pos.player = input.color;
			pos.pieces = input.pieces;
			pos.squares = input.squares;
			
    		int pieceType = board.getMoveOps().getFigureType(move);
    		int fromFieldID = board.getMoveOps().getFromFieldID(move);
    		int toFieldID = board.getMoveOps().getToFieldID(move);   		
    		
    		if (pieceType == Figures.TYPE_KING
    				|| board.getMoveOps().isCastling(move)
    				|| board.getMoveOps().isEnpassant(move)
    				//|| board.getMoveOps().isCapture(move)
    				|| board.getMoveOps().isPromotion(move)) {
    			
    			refresh_accumulator();
    			
    		} else {
    			
    			//Make index and update accumulator
    			color = NNUEProbeUtils.convertColor(color);
    			int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    			int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    			int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    			
    	        int ksq = pos.squares[color];
    	        ksq = orient(color, ksq);
    	        
    	        int index_from = make_index(color, square_from, piece, ksq);
    	        int offset_from = kHalfDimensions * index_from;
                for (int j = 0; j < kHalfDimensions; j++)
                    pos.nnue[0].accumulator.accumulation[color][j] -= ft_weights[offset_from + j];
                
    	        int index_to = make_index(color, square_to, piece, ksq);
    	        int offset_to = kHalfDimensions * index_to;
                for (int j = 0; j < kHalfDimensions; j++)
                    pos.nnue[0].accumulator.accumulation[color][j] += ft_weights[offset_to + j];
                
                if (board.getMoveOps().isCapture(move)) {
                	
                	color = 1 - color;
                	
        	        int ksq_op = pos.squares[color];
        	        ksq_op = orient(color, ksq_op);
        	        
                	int piece_captured = board.getMoveOps().getCapturedFigureType(move);
                	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, color);
                	
                	int index_to_captured = make_index(color, square_to, piece_captured, ksq_op);
        	        int offset_to_captured = kHalfDimensions * index_to_captured;
                    for (int j = 0; j < kHalfDimensions; j++)
                        pos.nnue[0].accumulator.accumulation[color][j] -= ft_weights[offset_to_captured + j];
                }
    		}
    	}


    	public final void unmove(int move, int color, IBitBoard board) {
    		
    		NNUEProbeUtils.fillInput(bitboard, input);
			pos.player = input.color;
			pos.pieces = input.pieces;
			pos.squares = input.squares;
			
    		int pieceType = board.getMoveOps().getFigureType(move);
    		int fromFieldID = board.getMoveOps().getFromFieldID(move);
    		int toFieldID = board.getMoveOps().getToFieldID(move);   		
    		
    		if (pieceType == Figures.TYPE_KING
    				|| board.getMoveOps().isCastling(move)
    				|| board.getMoveOps().isEnpassant(move)
    				//|| board.getMoveOps().isCapture(move)
    				|| board.getMoveOps().isPromotion(move)) {
    			
    			refresh_accumulator();
    			
    		} else {
    			
    			//Make index and update accumulator
    			color = NNUEProbeUtils.convertColor(color);
    			int piece = NNUEProbeUtils.convertPiece(pieceType, color);
    			int square_from = NNUEProbeUtils.convertSquare(fromFieldID);
    			int square_to = NNUEProbeUtils.convertSquare(toFieldID);
    			
    	        int ksq = pos.squares[color];
    	        ksq = orient(color, ksq);
    	        
    	        int index_from = make_index(color, square_from, piece, ksq);
    	        int offset_from = kHalfDimensions * index_from;
                for (int j = 0; j < kHalfDimensions; j++)
                    pos.nnue[0].accumulator.accumulation[color][j] += ft_weights[offset_from + j];
                
    	        int index_to = make_index(color, square_to, piece, ksq);
    	        int offset_to = kHalfDimensions * index_to;
                for (int j = 0; j < kHalfDimensions; j++)
                    pos.nnue[0].accumulator.accumulation[color][j] -= ft_weights[offset_to + j];
                
                if (board.getMoveOps().isCapture(move)) {
                	
                	color = 1 - color;
                	
        	        int ksq_op = pos.squares[color];
        	        ksq_op = orient(color, ksq_op);
        	        
                	int piece_captured = board.getMoveOps().getCapturedFigureType(move);
                	piece_captured = NNUEProbeUtils.convertPiece(piece_captured, color);
                	
                	int index_to_captured = make_index(color, square_to, piece_captured, ksq_op);
        	        int offset_to_captured = kHalfDimensions * index_to_captured;
                    for (int j = 0; j < kHalfDimensions; j++)
                        pos.nnue[0].accumulator.accumulation[color][j] += ft_weights[offset_to_captured + j];
                }
    		}
    	}
    }
    
    // Constants for FEN decoding
    private static final String PIECE_NAME = "_KQRBNPkqrbnp_";
    private static final String RANK_NAME = "_12345678";
    private static final String FILE_NAME = "abcdefgh";
    private static final String COL_NAME = "WwBb";
    private static final String CAS_NAME = "KQkq";
    
    public static void decodeFEN(String fen, int[] player, int[] castle, int[] fifty, int[] moveNumber, int[] piece, int[] square) {
        Arrays.fill(piece, 0);
        Arrays.fill(square, 0);

        int index = 2;
        int pIndex = 0;
        char[] fenChars = fen.toCharArray();
        
        for (int r = 7; r >= 0; r--) {
            for (int f = 0; f <= 7; f++) {
                int sq = r * 8 + f;
                char currentChar = fenChars[pIndex];

                if (PIECE_NAME.indexOf(currentChar) != -1) {
                    int pc = PIECE_NAME.indexOf(currentChar);
                    if (pc == 1) {
                        piece[0] = pc;
                        square[0] = sq;
                    } else if (pc == 7) {
                        piece[1] = pc;
                        square[1] = sq;
                    } else {
                        piece[index] = pc;
                        square[index] = sq;
                        index++;
                    }
                } else if (RANK_NAME.indexOf(currentChar) != -1) {
                    int emptySquares = RANK_NAME.indexOf(currentChar);
                    f += emptySquares - 1;
                }
                pIndex++;
            }
            pIndex++;
        }
        piece[index] = 0;
        square[index] = 0;
        
        char currentChar = fenChars[pIndex];
        if (COL_NAME.indexOf(currentChar) != -1) {
            player[0] = (COL_NAME.indexOf(currentChar) >= 2) ? 1 : 0;
        }
        pIndex += 2;

        currentChar = fenChars[pIndex];
        castle[0] = 0;
        if (currentChar == '-') {
            pIndex++;
        } else {
            while (CAS_NAME.indexOf(currentChar) != -1) {
                castle[0] |= (1 << CAS_NAME.indexOf(currentChar));
                pIndex++;
                currentChar = fenChars[pIndex];
            }
        }

        int epsquare;
        pIndex++;
        currentChar = fenChars[pIndex];
        if (currentChar == '-') {
            epsquare = 0;
            pIndex++;
        } else {
            epsquare = FILE_NAME.indexOf(currentChar);
            pIndex++;
            epsquare += 16 * RANK_NAME.indexOf(fenChars[pIndex]);
            pIndex++;
        }
        square[index] = epsquare;

        pIndex++;
        if (pIndex < fenChars.length && Character.isDigit(fenChars[pIndex]) && (Character.isDigit(fenChars[pIndex + 1]) || fenChars[pIndex + 1] == ' ')) {
            String remaining = new String(fenChars, pIndex, fenChars.length - pIndex).trim();
            String[] parts = remaining.split(" ");
            fifty[0] = Integer.parseInt(parts[0]);
            moveNumber[0] = Integer.parseInt(parts[1]);
            if (moveNumber[0] <= 0) moveNumber[0] = 1;
        } else {
            fifty[0] = 0;
            moveNumber[0] = 1;
        }
    }
    
    public static void main(String[] args) {

        // FEN decoding
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        int[] player = new int[1];
        int[] castle = new int[1];
        int[] fifty = new int[1];
        int[] moveNumber = new int[1];
        int[] piece = new int[33];
        int[] square = new int[33];

        decodeFEN(fen, player, castle, fifty, moveNumber, piece, square);

        System.out.println("Player: " + player[0]);
        System.out.println("Castle rights: " + castle[0]);
        System.out.println("Fifty-move rule: " + fifty[0]);
        System.out.println("Move number: " + moveNumber[0]);

        System.out.println("Pieces: " + Arrays.toString(piece));
        System.out.println("Squares: " + Arrays.toString(square));
        //System.out.println("output_weights: " + Arrays.toString(output_weights));
        //System.out.println("output_biases: " + Arrays.toString(output_biases));
        
    	NNUE nnue = new NNUE(null);
    	
        int eval = nnue.nnue_evaluate_pos(player[0], piece, square, false);
        System.out.println("Evaluation: " + eval);
        
        // Example of incremental evaluation after a move
        /*Move move = new Move(6, 12, 28); //e2e4
        int evalAfterMove = nnue_evaluate_incremental(pos, move);
        System.out.println("Evaluation after move: " + evalAfterMove);
        */
        
    	long startTime = System.currentTimeMillis();
    	int count = 0;
    	while (true) {
    		int evaluationN = nnue.nnue_evaluate_pos(player[0], piece, square, false);
    		count++;
    		if (count % 10000 == 0) {
    			System.out.println("NPS: " + count / Math.max(1, (System.currentTimeMillis() - startTime) / 1000));
    			System.out.println("Evaluation: " + evaluationN);
    		}
    	}
    }
}
