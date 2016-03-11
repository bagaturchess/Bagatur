package bagaturchess.engines.bagatur.dualeval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.bagatur.eval.BagaturEvaluatorFactory;
import bagaturchess.engines.bagatur.v12.BagaturV12EvaluatorFactory;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class DualEvaluatorFactory implements IEvaluatorFactory {
	
	
	private IEvaluatorFactory factory_o;
	private IEvaluatorFactory factory_e;
	
	
	public DualEvaluatorFactory() {
		factory_o = new BagaturEvaluatorFactory();
		factory_e = new BagaturV12EvaluatorFactory();
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		return new DualEvaluator(bitboard, factory_o.create(bitboard, evalCache), factory_e.create(bitboard, evalCache));
	}
	
	
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		return new DualEvaluator(bitboard, factory_o.create(bitboard, evalCache, evalConfig), factory_e.create(bitboard, evalCache, evalConfig));
	}
}
