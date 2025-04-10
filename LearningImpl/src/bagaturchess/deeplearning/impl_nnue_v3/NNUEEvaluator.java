package bagaturchess.deeplearning.impl_nnue_v3;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v2.NNUE;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private NNUE nnue;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		nnue = new NNUE(bitboard);
	}
	
	
	@Override
	public boolean useEvalCache_Reads() {
		
		return true;
	}
	
	
	@Override
	protected int phase1() {
		
		int actualWhitePlayerEval = nnue.evaluate();
		
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
