package bagaturchess.engines.bagatur.eval_v15;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class EvalFactory_V15 implements IEvaluatorFactory {
	
	
	public EvalFactory_V15() {
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return create(bitboard, evalCache, null);
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new Eval_V15(bitboard, evalCache, evalConfig);
	}
	
}
