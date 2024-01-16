package bagaturchess.deeplearning.impl_nnue_v2.eval;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import java.io.File;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.nnue.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	static {
		
		File dll = new File("./JNNUE.dll");
		
		System.load(dll.getAbsolutePath());
		
		File nnue = new File("nn-6b4236f2ec01.nnue");
		
		NNUEJNIBridge.init(nnue.getName());
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		String fen = bitboard.toEPD();
		//System.out.println("fen=" + fen);
		
		int score = NNUEJNIBridge.eval(fen);
			
		//System.out.println("NNUE score=" + score);
	}
	
	private IBitBoard bitboard;
	
	private NNUEProbeUtils.Input input;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		input = new NNUEProbeUtils.Input();
	}
	
	
	@Override
	protected int phase1() {
		

		NNUEProbeUtils.fillInput(bitboard, input);
		
		int actualWhitePlayerEval = NNUEJNIBridge.eval(input.color, input.pieces, input.squares) / 3;
		
		//String fen = bitboard.toEPD();
		
		//int actualWhitePlayerEval = NNUEJNIBridge.eval(fen) / 3;
		
		if (bitboard.getColourToMove() == BLACK) {
			
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		return (int) actualWhitePlayerEval;
	}
	
	
	@Override
	protected int phase2() {

		int eval = 0;
		
		return eval;
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
