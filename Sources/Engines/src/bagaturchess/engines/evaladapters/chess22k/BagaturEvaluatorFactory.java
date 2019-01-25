package bagaturchess.engines.evaladapters.chess22k;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturEvaluatorFactory implements IEvaluatorFactory {
	
	public BagaturEvaluatorFactory() {
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new BagaturEvaluator_Phases(bitboard, evalCache, null);
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new BagaturEvaluator_Phases(bitboard, evalCache, evalConfig);
	}
	
}
