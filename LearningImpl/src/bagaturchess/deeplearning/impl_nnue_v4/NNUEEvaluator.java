package bagaturchess.deeplearning.impl_nnue_v4;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v2.NNUEProbeUtils;
import bagaturchess.nnue_v3.NNUE_SIMD_AVX2;
import bagaturchess.nnue_v3.NNUE.Accumulator;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
		
	
	private IBitBoard bitboard;
	
    private static NNUE_SIMD_AVX2 nnue = new NNUE_SIMD_AVX2();
    private static final NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
    private static final Accumulator accumulators = new Accumulator();
	
	static {
    	
        try {
        	
			nnue.init("params.bin");
			
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
		
		nnue.accumulate(nnue.accumulators[0], nnue.accumulators[1],
				NNUE_SIMD_AVX2.net.FTBiases, NNUE_SIMD_AVX2.net.FTWeights,
				input.white_pieces, input.white_squares, input.black_pieces, input.black_squares);
		
		//int actualWhitePlayerEval = nnue.output(accumulators, bitboard.getColourToMove(), (bitboard.getMaterialState().getPiecesCount() - 2) / 4);
		//int actualWhitePlayerEval = (int) (1000 * Math.random());
		int actualWhitePlayerEval = nnue.output(nnue.accumulators, bitboard.getColourToMove(), 0);
				
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
