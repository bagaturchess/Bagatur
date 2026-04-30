package bagaturchess.deeplearning.impl_nnue_v7;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import java.io.File;
import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.nnue_v7.BigNnueNetwork;
import bagaturchess.nnue_v7.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private NNUEProbeUtils.Input input;
	
	private BigNnueNetwork nnue;
	
	private BigNnueNetwork.Workspace ws;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		input = new NNUEProbeUtils.Input();
		
		try {
			
			nnue = BigNnueNetwork.load(new File("./nn-b1a57edbea57.nnue"));
		
			ws = nnue.newWorkspace();
			
		} catch (IOException e) {
			
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public boolean useEvalCache_Reads() {
		
		return true;
	}
	
	
	@Override
	protected int phase1() {
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		int pieceCount = bitboard.getMaterialState().getPiecesCount();
		
		int eval = nnue.evaluateAdjustedFast(input, pieceCount, ws);
		
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
