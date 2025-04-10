package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluatorFactory implements IEvaluatorFactory {
	
	public BagaturEvaluatorFactory() {
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new BagaturEvaluator(bitboard, evalCache, null);
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new BagaturEvaluator(bitboard, evalCache, evalConfig);
	}
	
}
