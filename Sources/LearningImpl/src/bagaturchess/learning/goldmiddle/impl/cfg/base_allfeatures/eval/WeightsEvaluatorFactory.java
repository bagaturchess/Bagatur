package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class WeightsEvaluatorFactory implements IEvaluatorFactory {
	
	
	public WeightsEvaluatorFactory() {
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new WeightsEvaluator(bitboard, evalCache, null);
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new WeightsEvaluator(bitboard, evalCache, evalConfig);
	}
}
