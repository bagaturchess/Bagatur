package bagaturchess.montecarlo;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.montecarlo.MCTS_V2.MCTS;
import bagaturchess.search.api.IEvaluator;


public class MCTS_V2_Play extends MCTS {

	
    protected static final int MAX_EVAL_DIFF 			= 700;
    
    
	MCTS_V2_Play() {
		
		super();
	}
	
	
    protected double simulate(IBitBoard bitboard, IEvaluator evaluator) {
    	
    	double result = 0;
    	
    	List<Integer> moves = new ArrayList<Integer>();
    	
        while (bitboard.getStatus() == IGameStatus.NONE) {
        	
        	if (bitboard.getPlayedMovesCount() >= EngineConstants.MAX_MOVES - 2) {
        		break;
        	}
        	
            List<Integer> legalMoves = genAllLegalMoves(bitboard);
            
            int[] info = getBestMove(legalMoves, bitboard, evaluator);
            int selected_move = info[0];
            int selected_move_eval = info[1];
            
            //Stop the game early if possible
            if (Math.abs(selected_move_eval) >= MAX_EVAL_DIFF) {
            	
            	if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
            		
            		if (selected_move_eval > 0) {
            			
            			result = 1;
            			
            		} else {
            			
            			result = -1;
            		}
            		
            	} else {
            		
            		if (selected_move_eval > 0) {
            			
            			result = -1;
            			
            		} else {
            			
            			result = 1;
            		}
            	}
            	
            	break;
            }
            
            bitboard.makeMoveForward(selected_move);
            
            moves.add(selected_move);
        }
        
        if (result == 0) {
        	result = bitboard.getPlayedMovesCount() >= EngineConstants.MAX_MOVES - 2 ?
        				0 : evaluateResult(bitboard.getStatus());
        }
        
		//Revert moves
		for (int i = moves.size() - 1; i >=0; i--) {
			
			bitboard.makeMoveBackward(moves.get(i));
		}
		
        return result;
    }
    
    
    private double evaluateResult(IGameStatus status) {
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				return 0;
				
			case MATE_WHITE_WIN:
				return 1;
				
			case MATE_BLACK_WIN:
				return -1;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				return 0;
				
			case STALEMATE_BLACK_NO_MOVES:
				return 0;
				
			case DRAW_50_MOVES_RULE:
				return 0;
				
			case NO_SUFFICIENT_MATERIAL:
				return 0;
				
			case PASSER_WHITE:
				throw new IllegalStateException("status=" + status);
				
			case PASSER_BLACK:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_WHITE_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_BLACK_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			default:
				throw new IllegalStateException("status=" + status);
				
		}
    }
}
