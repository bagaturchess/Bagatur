package bagaturchess.nnue_v2;


import java.io.FileInputStream;
import java.io.IOException;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;


public class ProbeMain_V2 {    
	
	
	public static void main(String[] args) {
		
		String fen0 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen1 = "4kq2/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen2 = "4k3/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen3 = "4kq2/8/8/8/8/8/8/3K4 w - - 0 1";
		String fen4 = "4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen5 = "rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w KQkq - 0 1";
		String fen6 = "4kr2/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen7 = "4k3/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen8 = "4kr2/8/8/8/8/8/8/3K4 w - - 0 1";
        
		try {
			
			evaluate(fen0);
	        evaluate(fen1);
	        evaluate(fen2);
	        evaluate(fen3);
	        evaluate(fen4);
	        evaluate(fen5);
	        evaluate(fen6);
	        evaluate(fen7);
	        evaluate(fen8);
	        
	        /*IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen1);
	        
			int pieces_count = bitboard.getMaterialState().getPiecesCount();
			
	        System.out.println("input.white_king_sq: " + input.white_king_sq);
	        System.out.println("input.black_king_sq: " + input.black_king_sq);
	        System.out.println("input.white_pieces: " + Arrays.toString(input.white_pieces));
	        System.out.println("input.white_squares: " + Arrays.toString(input.white_squares));
	        System.out.println("input.black_pieces: " + Arrays.toString(input.black_pieces));
	        System.out.println("input.black_squares: " + Arrays.toString(input.black_squares));
	        //System.out.println("pieces_count: " + pieces_count);
        	//System.out.println("side: " + bitboard.getColourToMove());
         
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
	    	}*/
	        
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private static void evaluate(String fen) throws IOException {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
		
		NNUE network = new NNUE(new FileInputStream("./network_bagatur.nnue"), bitboard);
		
		int eval = network.evaluate();
		
		System.out.println("fen=" + fen + ", eval=" + eval);
	}
}
