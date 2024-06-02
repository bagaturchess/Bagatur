package bagaturchess.nnue;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
    	
        // Initialize and test the NNUE functions here
    	File net = new File("./nn-6b4236f2ec01.nnue");
    	//File net = new File("./nn-04cf2b4ed1da.nnue");
    	nnue_init(net.toURI());
    }
    
    private static void nnue_init(URI evalFile) {
        //System.out.println("Loading NNUE : " + evalFile);
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
        
        // Permute biases for AVX2
        //permute_biases(hidden1_biases);
        //permute_biases(hidden2_biases);
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

    // Permute biases for AVX2
    /*private static void permute_biases(int[] biases) {
        int[] tmp = new int[32];
        tmp[0] = biases[0];
        tmp[1] = biases[4];
        tmp[2] = biases[1];
        tmp[3] = biases[5];
        tmp[4] = biases[2];
        tmp[5] = biases[6];
        tmp[6] = biases[3];
        tmp[7] = biases[7];
        System.arraycopy(tmp, 0, biases, 0, 32);
    }*/
        
    public static int nnue_evaluate_pos(Position pos) {

        NetData netData = new NetData();

        transform(pos, netData.input, null);

        affine_txfm(netData.input, netData.hidden1_out, FtOutDims, 32,
                hidden1_biases, hidden1_weights, null, null, true);

        affine_txfm(netData.hidden1_out, netData.hidden2_out, 32, 32,
                hidden2_biases, hidden2_weights, null, null, false);

        int out_value = affine_propagate(netData.hidden2_out, output_biases,
                output_weights);

        return out_value / FV_SCALE;
    }

    private static int nnue_evaluate_incremental(Position pos, Move move) {
        update_dirty_pieces(pos, move);
        int out_value;
        int[] input_mask = new int[FtOutDims / 8];
        int[] hidden1_mask = new int[1];

        NetData netData = new NetData();

        transform(pos, netData.input, input_mask);

        affine_txfm(netData.input, netData.hidden1_out, FtOutDims, 32,
                hidden1_biases, hidden1_weights, input_mask, hidden1_mask, true);

        affine_txfm(netData.hidden1_out, netData.hidden2_out, 32, 32,
                hidden2_biases, hidden2_weights, hidden1_mask, null, false);

        out_value = affine_propagate(netData.hidden2_out, output_biases,
                output_weights);

        return out_value / FV_SCALE;
    }
    
    // Update dirty pieces based on the move
    private static void update_dirty_pieces(Position pos, Move move) {
        
    	DirtyPiece dirtyPiece = pos.nnue[0].dirtyPiece;
        dirtyPiece.dirtyNum = 1;
        dirtyPiece.pc[0] = move.piece;
        dirtyPiece.from[0] = move.from;
        dirtyPiece.to[0] = move.to;
        pos.nnue[0].accumulator.computedAccumulation = false;
        
        // Update color to move
        pos.player = 1 - pos.player;
        
        // Update pieces and squares arrays
        for (int i = 0; i < pos.pieces.length; i++) {
            if (pos.squares[i] == move.from) {
                pos.squares[i] = move.to;
                break;
            }
        }
        
        // Handle capture
        for (int i = 0; i < pos.pieces.length; i++) {
            if (pos.squares[i] == move.to && pos.pieces[i] != move.piece) {
                // Remove captured piece
                for (int j = i; j < pos.pieces.length - 1; j++) {
                    pos.pieces[j] = pos.pieces[j + 1];
                    pos.squares[j] = pos.squares[j + 1];
                }
                pos.pieces[pos.pieces.length - 1] = 0;
                pos.squares[pos.squares.length - 1] = 0;
                break;
            }
        }
    }
    
    private static void transform(Position pos, byte[] output, int[] outMask) {
        //if (!update_accumulator(pos))
            refresh_accumulator(pos);

        int[][] accumulation = pos.nnue[0].accumulator.accumulation;

        int[] perspectives = {pos.player, 1 - pos.player};
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

    private static void refresh_accumulator(Position pos) {
        Accumulator accumulator = pos.nnue[0].accumulator;

        int[][] activeIndices = new int[2][30]; // Adjust the size based on expected active indices
        int[] activeSizes = new int[2]; // To track the number of active indices for each player
        append_active_indices(pos, activeIndices, activeSizes);

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

    // Move class to store move information
    public static class Move {
        int piece;
        int from;
        int to;

        public Move(int piece, int from, int to) {
            this.piece = piece;
            this.from = from;
            this.to = to;
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
        
    	Position pos = new Position(square, piece, player[0]);
    	
        int eval = nnue_evaluate_pos(pos);
        System.out.println("Evaluation: " + eval);
        
        // Example of incremental evaluation after a move
        /*Move move = new Move(6, 12, 28); //e2e4
        int evalAfterMove = nnue_evaluate_incremental(pos, move);
        System.out.println("Evaluation after move: " + evalAfterMove);
        */
        
    	long startTime = System.currentTimeMillis();
    	int count = 0;
    	while (true) {
    		int evaluationN = nnue_evaluate_pos(pos);
    		count++;
    		if (count % 10000 == 0) {
    			System.out.println("NPS: " + count / Math.max(1, (System.currentTimeMillis() - startTime) / 1000));
    			System.out.println("Evaluation: " + evaluationN);
    		}
    		pos.clear();
    	}
    }
}
