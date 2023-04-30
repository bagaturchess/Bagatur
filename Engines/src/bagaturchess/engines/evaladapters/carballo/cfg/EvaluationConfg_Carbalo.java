package bagaturchess.engines.evaladapters.carballo.cfg;


import bagaturchess.engines.evaladapters.carballo.eval.BagaturEvaluatorFactory;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfg_Carbalo implements IEvalConfig {
	
	
	@Override
	public boolean useLazyEval() {
		return true;
	}
	
	
	@Override
	public boolean useEvalCache() {
		return true;
	}
	
	
	@Override
	public boolean isTrainingMode() {
		return false;
	}
	
	
	@Override
	public String getEvaluatorFactoryClassName() {
		return BagaturEvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
	}
}
