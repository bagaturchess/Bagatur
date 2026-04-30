package bagaturchess.deeplearning.impl_nnue_v7;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v7.BigNnueNetwork;
import bagaturchess.nnue_v7.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private BigNnueNetwork nnue;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
			
		nnue = new BigNnueNetwork(bitboard);
	}
	
	
	@Override
	public boolean useEvalCache_Reads() {
		
		return true;
	}
	
	
	@Override
	protected int phase1() {
		
		int eval = nnue.evaluate();
		
		int actualWhitePlayerEval = eval;
		
		if (bitboard.getColourToMove() == BLACK) {
			
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		return actualWhitePlayerEval;
	}
	
	
	@Override
	protected int phase2() {
		
		return 0;
	}
	
	
	@Override
	protected int phase3() {
		
		int eval = 0;
				
		return eval;
	}
	
	
	@Override
	protected int phase4() {
		
		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected int phase5() {
		
		int eval = 0;
		
		return eval;
	}
}
