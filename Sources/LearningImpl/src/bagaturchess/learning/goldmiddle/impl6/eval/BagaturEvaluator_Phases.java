package bagaturchess.learning.goldmiddle.impl6.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases implements IEvaluator {
	
	
	private bagaturchess.learning.goldmiddle.impl3.eval.BagaturEvaluator_Phases evaluator1;
	private bagaturchess.learning.goldmiddle.impl5.eval.BagaturEvaluator_Phases evaluator2;
	
	
	private static final int combineEvals(double eval1, double eval2) {
		return (int) Math.min(eval1, eval2);
	}
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		evaluator1 = new bagaturchess.learning.goldmiddle.impl3.eval.BagaturEvaluator_Phases(_bitboard, _evalCache, _evalConfig);
		evaluator2 = new bagaturchess.learning.goldmiddle.impl5.eval.BagaturEvaluator_Phases(_bitboard, _evalCache, _evalConfig);
	}


	@Override
	public void beforeSearch() {
		evaluator1.beforeSearch();
		evaluator2.beforeSearch();
	}


	@Override
	public int roughEval(int depth, int rootColour) {
		return combineEvals(evaluator1.roughEval(depth, rootColour), evaluator2.roughEval(depth, rootColour));
	}


	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		return combineEvals(evaluator1.lazyEval(depth, alpha, beta, rootColour), evaluator2.lazyEval(depth, alpha, beta, rootColour));
	}


	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour, FullEvalFlag flag) {
		return combineEvals(evaluator1.lazyEval(depth, alpha, beta, rootColour, flag), evaluator2.lazyEval(depth, alpha, beta, rootColour, flag));
	}


	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		return combineEvals(evaluator1.fullEval(depth, alpha, beta, rootColour), evaluator2.fullEval(depth, alpha, beta, rootColour));
	}
}
