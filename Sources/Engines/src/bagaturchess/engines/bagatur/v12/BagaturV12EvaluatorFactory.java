package bagaturchess.engines.bagatur.v12;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.impl.eval.cfg.IWeightsEvalConfig;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturV12EvaluatorFactory implements IEvaluatorFactory {
	
	
	public BagaturV12EvaluatorFactory() {
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new BagaturV12Evaluator(bitboard, evalCache, null);
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new BagaturV12Evaluator(bitboard, evalCache, (IWeightsEvalConfig) evalConfig);
	}
}
