package bagaturchess.nnue_v7;

import java.util.Arrays;


/** Helper for testing the big-only NNUE port from a FEN string. */
public final class FenPosition {
    private final int[] board = new int[64];
    private final int[] kingSquares = {-1, -1};
    private int sideToMove;

    public FenPosition(String fen) {
        String[] parts = fen.trim().split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("FEN must contain board and side-to-move fields");
        }
        Arrays.fill(board, 0);
        parseBoard(parts[0]);
        sideToMove = "w".equals(parts[1]) ? 0 : 1;
        if (kingSquares[0] < 0 || kingSquares[1] < 0) {
            throw new IllegalArgumentException("Both kings must exist in FEN");
        }
    }

    public NNUEProbeUtils.Input toInput() {
    	NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        input.color = sideToMove;
        int idx = 0;
        input.pieces[idx] = 6;
        input.squares[idx++] = kingSquares[0];
        input.pieces[idx] = 14;
        input.squares[idx++] = kingSquares[1];
        for (int sq = 0; sq < 64; sq++) {
            int pc = board[sq];
            if (pc == 0 || pc == 6 || pc == 14) {
                continue;
            }
            input.pieces[idx] = pc;
            input.squares[idx] = sq;
            idx++;
        }
        input.pieces[idx] = 0;
        input.squares[idx] = 0;
        return input;
    }

    private void parseBoard(String boardPart) {
        int rank = 7;
        int file = 0;
        for (int i = 0; i < boardPart.length(); i++) {
            char c = boardPart.charAt(i);
            if (c == '/') {
                rank--;
                file = 0;
                continue;
            }
            if (c >= '1' && c <= '8') {
                file += c - '0';
                continue;
            }
            int sq = rank * 8 + file;
            int pc = pieceCode(c);
            board[sq] = pc;
            if (pc == 6) {
                kingSquares[0] = sq;
            } else if (pc == 14) {
                kingSquares[1] = sq;
            }
            file++;
        }
    }

    private static int pieceCode(char c) {
        switch (c) {
            case 'P': return 1;
            case 'N': return 2;
            case 'B': return 3;
            case 'R': return 4;
            case 'Q': return 5;
            case 'K': return 6;
            case 'p': return 9;
            case 'n': return 10;
            case 'b': return 11;
            case 'r': return 12;
            case 'q': return 13;
            case 'k': return 14;
            default: throw new IllegalArgumentException("Bad FEN piece: " + c);
        }
    }
}
