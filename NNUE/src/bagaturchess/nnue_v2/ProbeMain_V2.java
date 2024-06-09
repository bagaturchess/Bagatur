package bagaturchess.nnue_v2;


import java.io.IOException;
import java.util.Arrays;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;


public class ProbeMain_V2 {    
	
	public static void main(String[] args) {
		
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		//String fen = "4kq2/8/8/8/8/8/8/2QK4 w - - 0 1";
		//String fen = "4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		
        IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
        
        int pieces_count = bitboard.getMaterialState().getPiecesCount();
        System.out.println("pieces_count: " + pieces_count);
        System.out.println("side: " + bitboard.getColourToMove());
        
		try {
			
			NNUE network = new NNUE("./network.data");
			
	        Accumulators accumulators = new Accumulators(network);
	        
			NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
			
			
	        NNUEProbeUtils.fillInput(bitboard, input);
	        
	        System.out.println("input.white_king_sq: " + input.white_king_sq);
	        System.out.println("input.black_king_sq: " + input.black_king_sq);
	        System.out.println("input.white_pieces: " + Arrays.toString(input.white_pieces));
	        System.out.println("input.white_squares: " + Arrays.toString(input.white_squares));
	        System.out.println("input.black_pieces: " + Arrays.toString(input.black_pieces));
	        System.out.println("input.black_squares: " + Arrays.toString(input.black_squares));
	        
	        long startTime = System.currentTimeMillis();
	    	int count = 0;
	    	while (true) {
	    		
		        NNUEProbeUtils.fillInput(bitboard, input);
		        
		        accumulators.fullAccumulatorUpdate(input.white_king_sq, input.black_king_sq, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares);
		        
		        int eval = bitboard.getColourToMove() == NNUE.WHITE ?
			        NNUE.evaluate(network, accumulators.getWhiteAccumulator(), accumulators.getBlackAccumulator(), pieces_count)
			        :
			        NNUE.evaluate(network, accumulators.getBlackAccumulator(), accumulators.getWhiteAccumulator(), pieces_count);
		        
	    		if (count % 1000000 == 0) {
	    			System.out.println("Evaluation per second: " + count / Math.max(1, (System.currentTimeMillis() - startTime) / 1000));
	    			System.out.println("Evaluation: " + eval);
	    		}
	    		count++;
	    	}
	        
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
