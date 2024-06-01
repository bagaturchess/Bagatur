package bagaturchess.nnue.experiments;

import java.io.*;
import java.net.URI;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.IntStream;

public class NNUE_V2 {

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

    private static final int NnueVersion = 0x7AF32F16;
    private static final int FV_SCALE = 16;
    private static final int SHIFT = 6;

    private static final int kHalfDimensions = 256;
    private static final int FtInDims = 64 * PS_END;
    private static final int FtOutDims = kHalfDimensions * 2;

    private static final int white = 0;
    private static final int black = 1;

    private static final int[] PieceToIndexW = {0, 0, PS_W_QUEEN, PS_W_ROOK, PS_W_BISHOP, PS_W_KNIGHT, PS_W_PAWN, 0, PS_B_QUEEN, PS_B_ROOK, PS_B_BISHOP, PS_B_KNIGHT, PS_B_PAWN, 0};
    private static final int[] PieceToIndexB = {0, 0, PS_B_QUEEN, PS_B_ROOK, PS_B_BISHOP, PS_B_KNIGHT, PS_B_PAWN, 0, PS_W_QUEEN, PS_W_ROOK, PS_W_BISHOP, PS_W_KNIGHT, PS_W_PAWN, 0};

    
    // Constants for offsets in the evaluation file
    private static final int TransformerStart = 3 * 4 + 177; // 3 integers + 177 bytes
    private static final int NetworkStart = TransformerStart + 4 + 2 * 256 + 2 * 256 * 64 * 641;

    
    private static int[] ft_biases = new int[kHalfDimensions];
    private static int[] ft_weights = new int[kHalfDimensions * FtInDims];

    private static int[] hidden1_biases = new int[32];
    private static int[] hidden2_biases = new int[32];
    private static int[] output_biases = new int[1];

    private static byte[] hidden1_weights = new byte[32 * 512];
    private static byte[] hidden2_weights = new byte[32 * 32];
    private static byte[] output_weights = new byte[1 * 32];

    private static final int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int orient(int c, int s) {
        return s ^ (c == white ? 0x00 : 0x3f);
    }

    private static int makeIndex(int c, int s, int pc, int ksq) {
        return orient(c, s) + (c == white ? PieceToIndexW[pc] : PieceToIndexB[pc]) + PS_END * ksq;
    }

    private static void appendActiveIndices(Position pos, IndexList[] active) {
        for (int c = 0; c < 2; c++) {
            halfKpAppendActiveIndices(pos, c, active[c]);
        }
    }

    private static void halfKpAppendActiveIndices(Position pos, int c, IndexList active) {
        int ksq = orient(c, pos.squares[c]);
        for (int i = 2; pos.pieces[i] != 0; i++) {
            int sq = pos.squares[i];
            int pc = pos.pieces[i];
            active.values[active.size++] = makeIndex(c, sq, pc, ksq);
        }
    }

    private static void appendChangedIndices(Position pos, IndexList[] removed, IndexList[] added, boolean[] reset) {
        DirtyPiece dp = pos.nnue[0].dirtyPiece;

        if (pos.nnue[1].accumulator.computedAccumulation) {
            for (int c = 0; c < 2; c++) {
                reset[c] = dp.pc[0] == KING(c);
                if (reset[c]) {
                    halfKpAppendActiveIndices(pos, c, added[c]);
                } else {
                    halfKpAppendChangedIndices(pos, c, dp, removed[c], added[c]);
                }
            }
        } else {
            DirtyPiece dp2 = pos.nnue[1].dirtyPiece;
            for (int c = 0; c < 2; c++) {
                reset[c] = dp.pc[0] == KING(c) || dp2.pc[0] == KING(c);
                if (reset[c]) {
                    halfKpAppendActiveIndices(pos, c, added[c]);
                } else {
                    halfKpAppendChangedIndices(pos, c, dp, removed[c], added[c]);
                    halfKpAppendChangedIndices(pos, c, dp2, removed[c], added[c]);
                }
            }
        }
    }

    private static void halfKpAppendChangedIndices(Position pos, int c, DirtyPiece dp, IndexList removed, IndexList added) {
        int ksq = orient(c, pos.squares[c]);
        for (int i = 0; i < dp.dirtyNum; i++) {
            int pc = dp.pc[i];
            if (IS_KING(pc)) continue;
            if (dp.from[i] != 64) {
                removed.values[removed.size++] = makeIndex(c, dp.from[i], pc, ksq);
            }
            if (dp.to[i] != 64) {
                added.values[added.size++] = makeIndex(c, dp.to[i], pc, ksq);
            }
        }
    }

    private static final boolean IS_KING(int p) {
        return p == PS_W_KNIGHT || p == PS_B_KNIGHT;
    }

    private static final int KING(int c) {
        return c == white ? PS_W_KNIGHT : PS_B_KNIGHT;
    }

    private static void readOutputWeights(byte[] w, ByteBuffer d) {
        for (int i = 0; i < 32; i++) {
            int c = i;
            w[c] = d.get();
        }
    }

    private static void initWeights(byte[] evalData) {
        ByteBuffer d = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);
        d.position(TransformerStart + 4);

        for (int i = 0; i < kHalfDimensions; i++) {
            ft_biases[i] = d.getShort();
        }
        for (int i = 0; i < kHalfDimensions * FtInDims; i++) {
            ft_weights[i] = d.getShort();
        }

        d.position(d.position() + 4);
        for (int i = 0; i < 32; i++) {
            hidden1_biases[i] = d.getInt();
        }
        d.get(hidden1_weights);
        for (int i = 0; i < 32; i++) {
            hidden2_biases[i] = d.getInt();
        }
        d.get(hidden2_weights);
        for (int i = 0; i < 1; i++) {
            output_biases[i] = d.getInt();
        }
        readOutputWeights(output_weights, d);
    }

    private static boolean verifyNet(byte[] evalData) {
        ByteBuffer d = ByteBuffer.wrap(evalData).order(ByteOrder.LITTLE_ENDIAN);
        if (d.getInt() != NnueVersion) return false;
        if (d.getInt() != 0x3e5aa6ee) return false;
        if (d.getInt() != 177) return false;
        d.position(TransformerStart);
        if (d.getInt() != 0x5d69d7b8) return false;
        d.position(NetworkStart);
        if (d.getInt() != 0x63337156) return false;
        return true;
    }

    private static boolean loadEvalFile(URI evalFile) {
        byte[] evalData;
        try {
            evalData = Files.readAllBytes(Paths.get(evalFile));
        } catch (IOException e) {
            return false;
        }
        boolean success = verifyNet(evalData);
        if (success) initWeights(evalData);
        return success;
    }

    public static void nnueInit(URI evalFile) {
        System.out.println("Loading NNUE : %s\n" + evalFile);
        if (loadEvalFile(evalFile)) {
            System.out.println("NNUE loaded !");
            return;
        }
        System.out.println("NNUE file not found!");
    }

    public static int nnueEvaluate(Position pos) {
        //NNUEData nnue = new NNUEData();
        //nnue.accumulator.computedAccumulation = false;

        return nnueEvaluatePos(pos);
    }

    private static int nnueEvaluatePos(Position pos) {
        int[] input = new int[FtOutDims];
        int[] hidden1_out = new int[32];
        int[] hidden2_out = new int[32];

        int[] input_mask = new int[FtOutDims / 8];
        int[] hidden1_mask = new int[8];

        transform(pos, input, input_mask);

        affineTxfm(input, hidden1_out, FtOutDims, 32, hidden1_biases, hidden1_weights, input_mask, hidden1_mask, true);

        affineTxfm(hidden1_out, hidden2_out, 32, 32, hidden2_biases, hidden2_weights, hidden1_mask, null, false);

        int out_value = affinePropagate(hidden2_out, output_biases, output_weights);

        return out_value / FV_SCALE;
    }

    private static void transform(Position pos, int[] output, int[] outMask) {
        if (!updateAccumulator(pos)) {
            refreshAccumulator(pos);
        }

        int[][] accumulation = pos.nnue[0].accumulator.accumulation;
        int[] perspectives = {pos.player, pos.player ^ 1};

        for (int p = 0; p < 2; p++) {
            int offset = kHalfDimensions * p;
            for (int i = 0; i < kHalfDimensions; i++) {
                int sum = accumulation[perspectives[p]][i];
                output[offset + i] = clamp(sum, 0, 127);
            }
        }
    }

    private static void refreshAccumulator(Position pos) {
        Accumulator accumulator = pos.nnue[0].accumulator;

        IndexList[] activeIndices = {new IndexList(), new IndexList()};
        appendActiveIndices(pos, activeIndices);

        for (int c = 0; c < 2; c++) {
            System.arraycopy(ft_biases, 0, accumulator.accumulation[c], 0, kHalfDimensions);

            for (int k = 0; k < activeIndices[c].size; k++) {
                int index = activeIndices[c].values[k];
                int offset = kHalfDimensions * index;
                for (int j = 0; j < kHalfDimensions; j++) {
                    accumulator.accumulation[c][j] += ft_weights[offset + j];
                }
            }
        }

        accumulator.computedAccumulation = true;
    }

    private static boolean updateAccumulator(Position pos) {
        Accumulator accumulator = pos.nnue[0].accumulator;
        if (accumulator.computedAccumulation) {
            return true;
        }

        Accumulator prevAcc;
        if ((pos.nnue[1] == null || !(prevAcc = pos.nnue[1].accumulator).computedAccumulation)
                && (pos.nnue[2] == null || !(prevAcc = pos.nnue[2].accumulator).computedAccumulation)) {
            return false;
        }

        IndexList[] removedIndices = {new IndexList(), new IndexList()};
        IndexList[] addedIndices = {new IndexList(), new IndexList()};
        boolean[] reset = new boolean[2];
        appendChangedIndices(pos, removedIndices, addedIndices, reset);

        for (int c = 0; c < 2; c++) {
            if (reset[c]) {
                System.arraycopy(ft_biases, 0, accumulator.accumulation[c], 0, kHalfDimensions);
            } else {
                System.arraycopy(prevAcc.accumulation[c], 0, accumulator.accumulation[c], 0, kHalfDimensions);
                for (int k = 0; k < removedIndices[c].size; k++) {
                    int index = removedIndices[c].values[k];
                    int offset = kHalfDimensions * index;
                    for (int j = 0; j < kHalfDimensions; j++) {
                        accumulator.accumulation[c][j] -= ft_weights[offset + j];
                    }
                }
            }

            for (int k = 0; k < addedIndices[c].size; k++) {
                int index = addedIndices[c].values[k];
                int offset = kHalfDimensions * index;
                for (int j = 0; j < kHalfDimensions; j++) {
                    accumulator.accumulation[c][j] += ft_weights[offset + j];
                }
            }
        }

        accumulator.computedAccumulation = true;
        return true;
    }

    private static void affineTxfm(int[] input, int[] output, int inDims, int outDims, int[] biases, byte[] weights, int[] inMask, int[] outMask, boolean pack8AndCalcMask) {
        int[] tmp = new int[outDims];
        System.arraycopy(biases, 0, tmp, 0, outDims);

        for (int idx = 0; idx < inDims; idx++) {
            if (input[idx] != 0) {
                for (int i = 0; i < outDims; i++) {
                    tmp[i] += input[idx] * weights[outDims * idx + i];
                }
            }
        }

        for (int i = 0; i < outDims; i++) {
            output[i] = clamp(tmp[i] >> SHIFT, 0, 127);
        }
    }

    private static int affinePropagate(int[] input, int[] biases, byte[] weights) {
        int sum = biases[0];
        for (int j = 0; j < 32; j++) {
            sum += weights[j] * input[j];
        }
        return sum;
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
        // Initialize and test the NNUE functions here
    	File net = new File("./nn-6b4236f2ec01.nnue");
    	//File net = new File("./nn-04cf2b4ed1da.nnue");
        nnueInit(net.toURI());

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
        
    	Position pos = new Position(square, piece, player[0]);
    	
    	int evaluation = nnueEvaluate(pos);
    	System.out.println("Evaluation: " + evaluation);
    	
    	/*long startTime = System.currentTimeMillis();
    	int count = 0;
    	while (true) {
    		int evaluationN = nnueEvaluate(pos);
    		count++;
    		if (count % 10000 == 0) {
    			System.out.println("NPS: " + count / Math.max(1, (System.currentTimeMillis() - startTime) / 1000));
    			System.out.println("Evaluation: " + evaluationN);
    		}
    	}*/
    }
}

class Position {
    NNUEData[] nnue = new NNUEData[3];
    int player;
    int[] pieces;
    int[] squares;
    
    public Position() {
        for (int i = 0; i < nnue.length; i++) {
            nnue[i] = new NNUEData();
        }
    }
    
    public Position(int[] _squares, int[] _pieces, int _player) {
    	this();
    	squares = _squares;
    	pieces = _pieces;
    	player = _player;
    }
}

class NNUEData {
    Accumulator accumulator = new Accumulator();
    DirtyPiece dirtyPiece = new DirtyPiece();
}

class Accumulator {
    boolean computedAccumulation;
    int[][] accumulation = new int[2][256];
}

class IndexList {
    int size;
    int[] values = new int[30];
}

class DirtyPiece {
    int dirtyNum;
    int[] pc = new int[30];
    int[] from = new int[30];
    int[] to = new int[30];
}
