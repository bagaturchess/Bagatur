package bagaturchess.nnue_v6;

import java.util.Arrays;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v5.NNUEProbeUtils;

public class HalfKPFeatureExtractor {
	
	
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    
	public static final int DIM = 3072;  // 2 sides × 3 major piece types × 64 squares × 8 half-KP positions


	/*public static void extract(IBitBoard board, byte[] output) {
	    
		if (output.length != DIM) throw new IllegalArgumentException("Expected input length = " + DIM);
	
		Arrays.fill(output, (byte) 0);
	
		NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
		NNUEProbeUtils.fillInput(board, input);
		
		int activeOffset   = 0;         // perspective of side to move
		int passiveOffset  = 1536;      // opponent's perspective
		
		for (int i = 2; i < input.pieces.length; i++) {
			
			if (input.pieces[i] == 0) {
				
				break;
			}
			
			int featureIndex = -1;
			switch(input.pieces[i]) {
				case 1:
					featureIndex = getFeatureIndex(input.color == WHITE ? activeOffset : passiveOffset, 0, input.squares[i]);
					break;
				case 2:
					featureIndex = getFeatureIndex(input.color == WHITE ? activeOffset : passiveOffset, 1, input.squares[i]);
					break;
				case 3:
					featureIndex = getFeatureIndex(input.color == WHITE ? activeOffset : passiveOffset, 2, input.squares[i]);
					break;
				case 4:
					featureIndex = getFeatureIndex(input.color == WHITE ? activeOffset : passiveOffset, 3, input.squares[i]);
					break;
				case 5:
					featureIndex = getFeatureIndex(input.color == WHITE ? activeOffset : passiveOffset, 4, input.squares[i]);
					break;
				case 6:
					//Skip king
					break;
				case 9:
					featureIndex = getFeatureIndex(input.color == BLACK ? activeOffset : passiveOffset, 0, input.squares[i]);
					break;
				case 10:
					featureIndex = getFeatureIndex(input.color == BLACK ? activeOffset : passiveOffset, 1, input.squares[i]);
					break;
				case 11:
					featureIndex = getFeatureIndex(input.color == BLACK ? activeOffset : passiveOffset, 2, input.squares[i]);
					break;
				case 12:
					featureIndex = getFeatureIndex(input.color == BLACK ? activeOffset : passiveOffset, 3, input.squares[i]);
					break;
				case 13:
					featureIndex = getFeatureIndex(input.color == BLACK ? activeOffset : passiveOffset, 4, input.squares[i]);
					break;
				case 14:
					//Skip king
					break;
				default:
					throw new IllegalStateException("input.pieces[i]=" + input.pieces[i]);
		    }
			
			if (featureIndex != -1) output[featureIndex] = 1;
		}
	}


    private static int getFeatureIndex(int kingOffset, int type, int square) {
		return kingOffset + 64 * type + square;
	}*/

    public static final int FEATURE_DIM = 3072;
    private static final int KING_PERSPECTIVE_DIM = 1536;

    public static final int TYPE_PAWN   = 0;
    public static final int TYPE_KNIGHT = 1;
    public static final int TYPE_BISHOP = 2;
    public static final int TYPE_ROOK   = 3;
    public static final int TYPE_QUEEN  = 4;
    public static final int TYPE_KING   = 5;

    public static void extract(IBitBoard board, byte[] output) {

        if (output.length != FEATURE_DIM) {
            throw new IllegalArgumentException("Expected input length = " + FEATURE_DIM);
        }

        Arrays.fill(output, (byte) 0);

        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        NNUEProbeUtils.fillInput(board, input);

        int us = input.color;
        int them = us ^ 1;

        int kingUsSq = -1;
        int kingThemSq = -1;

        // Extract king squares first
        for (int i = 0; i < input.pieces.length; i++) {
            int piece = input.pieces[i];
            if (piece == 0) break;
            
            int square = input.squares[i];

            int kingUsCode   = TYPE_KING + 8 * us;
            int kingThemCode = TYPE_KING + 8 * them;

            if (piece == kingUsCode) {
                kingUsSq = square;
            } else if (piece == kingThemCode) {
                kingThemSq = square;
            }
            
            if (kingUsSq != -1 && kingThemSq != -1) {
            	
            	break;
            }
        }

        if (kingUsSq == -1 || kingThemSq == -1) {
            throw new IllegalStateException("Both kings must be present in the input.");
        }

        // Encode non-king pieces from both sides
        for (int i = 0; i < input.pieces.length; i++) {
            int piece = input.pieces[i];
            if (piece == 0) break;

            int square = input.squares[i];
            if (piece == 6 || piece == 14) continue; // Skip kings

            int color = piece >= 9 ? BLACK : WHITE;
            int type = to0BasedType(piece); // now safe

            int idx0 = getHalfKPIndex(color == input.color, type, color, square);
            output[idx0] = 1;

            int idx1 = getHalfKPIndex(!(color == input.color), type, color, square);
            output[idx1] = 1;
        }
    }
    
    
    private static int to0BasedType(int piece) {
        switch (piece) {
            case 1:  case 9:  return 0; // Pawn
            case 2:  case 10: return 1; // Knight
            case 3:  case 11: return 2; // Bishop
            case 4:  case 12: return 3; // Rook
            case 5:  case 13: return 4; // Queen
            default:
                throw new IllegalArgumentException("Invalid or unsupported piece code: " + piece);
        }
    }
    
    
    private static int getHalfKPIndex(boolean sideToMove, int pieceType0Based, int color, int square) {
    	
    	if (sideToMove) {
    		
    		return ((pieceType0Based * 2 + color) * 64) + square;
    		
    	} else {
    	
    		return 1536 + ((pieceType0Based * 2 + color) * 64) + square;
    	}
    }
}
