package bagaturchess.nnue;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class NNUEMain {
	
	
	public static void main(String[] args) {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		String fen = bitboard.toEPD();
		System.out.println("fen=" + fen);
		
		NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
		
		NNUE nnue = new NNUE(bitboard);
		
    	long startTime = System.currentTimeMillis();
    	int count = 0;
    	while (true) {
    		
    		NNUEProbeUtils.fillInput(bitboard, input);
    		
    		int score = nnue.nnue_evaluate_pos(input.color, input.pieces, input.squares, false);
    		count++;
    		if (count % 100000 == 0) {
    			System.out.println("NPS: " + count / Math.max(1, (System.currentTimeMillis() - startTime) / 1000));
    			System.out.println("Evaluation: " + score);
    		}
    	}
    	
		/*pos.clear();
		NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
		NNUEProbeUtils.fillInput(bitboard, input);
		pos.player = input.color;
		pos.squares = input.squares;
		pos.pieces = input.pieces;
		
		int score = NNUE.nnue_evaluate_pos(pos);
		
		System.out.println("score: " + score);*/
	}
}
