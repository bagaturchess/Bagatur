package bagaturchess.engines.cfg.materialonly;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl1;
import bagaturchess.search.impl.eval.cache.IEvalCache;



public class MaterialEvaluatorFactory implements IEvaluatorFactory {
	
	public MaterialEvaluatorFactory() {
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new MaterialEvaluator(bitboard, evalCache, null);
	}
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new MaterialEvaluator(bitboard, evalCache, evalConfig);
	}
	
}
