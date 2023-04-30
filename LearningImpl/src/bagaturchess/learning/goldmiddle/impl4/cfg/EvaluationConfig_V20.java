package bagaturchess.learning.goldmiddle.impl4.cfg;


import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.learning.goldmiddle.impl4.eval.BagaturEvaluatorFactory;
import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfig_V20 implements IEvalConfig {
	
	
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
