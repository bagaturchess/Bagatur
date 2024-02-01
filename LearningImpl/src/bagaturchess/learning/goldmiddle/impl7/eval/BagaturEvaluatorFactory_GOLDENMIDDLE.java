package bagaturchess.learning.goldmiddle.impl7.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluatorFactory_GOLDENMIDDLE implements IEvaluatorFactory {
	
	
	public BagaturEvaluatorFactory_GOLDENMIDDLE() {
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		return new BagaturEvaluator_Phases_GOLDENMIDDLE(bitboard, evalCache, null);
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		
		return new BagaturEvaluator_Phases_GOLDENMIDDLE(bitboard, evalCache, evalConfig);
	}
}
