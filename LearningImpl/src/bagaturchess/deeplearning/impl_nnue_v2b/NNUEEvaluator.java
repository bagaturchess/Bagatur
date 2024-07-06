package bagaturchess.deeplearning.impl_nnue_v2b;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v5.NNUEBridge;
import bagaturchess.nnue_v5.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private NNUEProbeUtils.Input input;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		input = new NNUEProbeUtils.Input();
	}
	
	
	@Override
	public boolean useEvalCache_Reads() {
		
		return true;
	}
	
	
	@Override
	protected int phase1() {
		
		return 0;
	}
	
	
	@Override
	protected int phase2() {
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		int actualWhitePlayerEval = NNUEBridge.fasterEvalArray(input.pieces, input.squares,
				bitboard.getMaterialState().getPiecesCount(),
				bitboard.getColourToMove(),
				bitboard.getDraw50movesRule());
		
		if (bitboard.getColourToMove() == BLACK) {
			
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		return actualWhitePlayerEval;
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
