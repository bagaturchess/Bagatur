package bagaturchess.deeplearning.impl_nnue_v5;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v2.NNUEProbeUtils;
import bagaturchess.nnue_v4.NNUE;
import bagaturchess.nnue_v4.NNUE.Accumulator;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
		
	
	private IBitBoard bitboard;
	
    private static final NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
    private static final Accumulator accumulators = new Accumulator();
	
	static {
    	
        try {
        	
			NNUE.init("params.bin");
			NNUE.loadNet("params.bin");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
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
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		int actualWhitePlayerEval = NNUE.eval(accumulators, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares, bitboard.getColourToMove(), (pieces_count - 2) / 32);
		
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
