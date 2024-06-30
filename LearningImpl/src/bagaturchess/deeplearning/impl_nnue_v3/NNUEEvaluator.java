package bagaturchess.deeplearning.impl_nnue_v3;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import java.io.IOException;
import java.util.Arrays;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v2.Accumulators;
import bagaturchess.nnue_v2.NNUE;
import bagaturchess.nnue_v2.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
		
	
	private IBitBoard bitboard;
	
	private NNUEProbeUtils.Input input;
	
	private static NNUE nnue;
	
	private Accumulators accumulators;
	
	private int[] vectorevalbuff = new int[8];
	
	static {
		
		try {
			
			nnue = new NNUE("./network_bagatur_v1.nnue");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
        accumulators = new Accumulators(nnue);
        
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
		
		accumulators.fullAccumulatorUpdate(input.white_king_sq, input.black_king_sq, input.white_pieces, input.white_squares, input.black_pieces, input.black_squares);
		
		int pieces_count = bitboard.getMaterialState().getPiecesCount();
		
		Arrays.fill(vectorevalbuff, 0);
		
		int actualWhitePlayerEval = bitboard.getColourToMove() == NNUE.WHITE ?
		        NNUE.evaluate(nnue, accumulators.getWhiteAccumulator(), accumulators.getBlackAccumulator(), pieces_count, vectorevalbuff)
		        :
		        NNUE.evaluate(nnue, accumulators.getBlackAccumulator(), accumulators.getWhiteAccumulator(), pieces_count, vectorevalbuff);
		
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
