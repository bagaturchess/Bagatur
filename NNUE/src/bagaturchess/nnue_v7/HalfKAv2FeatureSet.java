package bagaturchess.nnue_v7;

/**
 * Exact HalfKAv2_hm feature indexing from the uploaded older Stockfish-based code.
 */
final class HalfKAv2FeatureSet {
    static final int HASH_VALUE = 0x7f234cb8;

    static final int DIMENSIONS = 64 * (11 * 64) / 2; // 22528

    private static final int PS_NONE = 0;
    private static final int PS_W_PAWN = 0;
    private static final int PS_B_PAWN = 1 * 64;
    private static final int PS_W_KNIGHT = 2 * 64;
    private static final int PS_B_KNIGHT = 3 * 64;
    private static final int PS_W_BISHOP = 4 * 64;
    private static final int PS_B_BISHOP = 5 * 64;
    private static final int PS_W_ROOK = 6 * 64;
    private static final int PS_B_ROOK = 7 * 64;
    private static final int PS_W_QUEEN = 8 * 64;
    private static final int PS_B_QUEEN = 9 * 64;
    private static final int PS_KING = 10 * 64;
    private static final int PS_NB = 11 * 64;

    private static final int[][] PIECE_SQUARE_INDEX = {
            {PS_NONE, PS_W_PAWN, PS_W_KNIGHT, PS_W_BISHOP, PS_W_ROOK, PS_W_QUEEN, PS_KING, PS_NONE,
                    PS_NONE, PS_B_PAWN, PS_B_KNIGHT, PS_B_BISHOP, PS_B_ROOK, PS_B_QUEEN, PS_KING, PS_NONE},
            {PS_NONE, PS_B_PAWN, PS_B_KNIGHT, PS_B_BISHOP, PS_B_ROOK, PS_B_QUEEN, PS_KING, PS_NONE,
                    PS_NONE, PS_W_PAWN, PS_W_KNIGHT, PS_W_BISHOP, PS_W_ROOK, PS_W_QUEEN, PS_KING, PS_NONE}
    };

    private static final int[] KING_BUCKETS = {
            b(28), b(29), b(30), b(31), b(31), b(30), b(29), b(28),
            b(24), b(25), b(26), b(27), b(27), b(26), b(25), b(24),
            b(20), b(21), b(22), b(23), b(23), b(22), b(21), b(20),
            b(16), b(17), b(18), b(19), b(19), b(18), b(17), b(16),
            b(12), b(13), b(14), b(15), b(15), b(14), b(13), b(12),
            b(8), b(9), b(10), b(11), b(11), b(10), b(9), b(8),
            b(4), b(5), b(6), b(7), b(7), b(6), b(5), b(4),
            b(0), b(1), b(2), b(3), b(3), b(2), b(1), b(0)
    };

    private static final int[] ORIENT_TBL = {
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0,
            7,7,7,7,0,0,0,0
    };

    static int makeIndex(int perspective, int square, int piece, int kingSquare) {
        int flip = 56 * perspective;
        return (square ^ ORIENT_TBL[kingSquare] ^ flip)
                + PIECE_SQUARE_INDEX[perspective][piece]
                + KING_BUCKETS[kingSquare ^ flip];
    }

    private static int b(int v) {
        return v * PS_NB;
    }

    static final int[][][] FEATURE_INDEX = buildFeatureIndex();

    private static int[][][] buildFeatureIndex() {
        int[][][] t = new int[2][64][16 * 64];
        for (int perspective = 0; perspective < 2; perspective++) {
            for (int king = 0; king < 64; king++) {
                int[] row = t[perspective][king];
                for (int piece = 0; piece < 16; piece++) {
                    for (int square = 0; square < 64; square++) {
                        row[(piece << 6) | square] = makeIndex(perspective, square, piece, king);
                    }
                }
            }
        }
        return t;
    }
}


