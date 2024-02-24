package bagaturchess.learning.goldmiddle.impl8.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluatorFactory_PST_GOLDENMIDDLE implements IEvaluatorFactory {
	
	
	public BagaturEvaluatorFactory_PST_GOLDENMIDDLE() {
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		return new BagaturEvaluator_Phases_PST_GOLDENMIDDLE(bitboard, evalCache, null);
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		
		return new BagaturEvaluator_Phases_PST_GOLDENMIDDLE(bitboard, evalCache, evalConfig);
	}
}
