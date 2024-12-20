package bagaturchess.montecarlo;


import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.montecarlo.MCTS_V2.MCTS;
import bagaturchess.search.api.IEvaluator;


public class MCTS_V2_Eval extends MCTS {

	
	MCTS_V2_Eval() {
		
		super();
	}
	
	
    protected double simulate(IBitBoard bitboard, IEvaluator evaluator) {
    	
        List<Integer> legalMoves = genAllLegalMoves(bitboard);
        
        int[] info = getBestMove(legalMoves, bitboard, evaluator);
        int selected_move = info[0];
        int selected_move_eval = info[1];
		
        if (bitboard.getColourToMove() == Constants.COLOUR_BLACK) {
        	
        	selected_move_eval = -selected_move_eval;
        }
        
        return selected_move_eval;
    }
}
