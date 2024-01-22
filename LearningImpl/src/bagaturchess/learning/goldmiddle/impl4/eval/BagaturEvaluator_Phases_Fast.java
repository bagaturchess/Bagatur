package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.Evaluator_Fast;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases_Fast extends BaseEvaluator {
	
	
	private final EvalInfo evalinfo;
	
	private final ChessBoard board;
	
	
	public BagaturEvaluator_Phases_Fast(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalinfo = new EvalInfo();
	}
	
	
	@Override
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int eval = super.fullEval(depth, alpha, beta, rootColour);
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalinfo.clearEvals();
		
		evalinfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected int phase1() {
		
		return Evaluator_Fast.eval1(bitboard.getBoardConfig(), board, evalinfo);
	}
	
	
	@Override
	protected int phase2() {
		
		return Evaluator_Fast.eval2(board, evalinfo);
	}
	
	
	@Override
	protected int phase3() {
		
		return Evaluator_Fast.eval3(board, evalinfo);
	}
	
	
	@Override
	protected int phase4() {
		
		return Evaluator_Fast.eval4(board, evalinfo);
	}
	
	
	@Override
	protected int phase5() {
		
		return Evaluator_Fast.eval5(board, evalinfo);
	}
}
