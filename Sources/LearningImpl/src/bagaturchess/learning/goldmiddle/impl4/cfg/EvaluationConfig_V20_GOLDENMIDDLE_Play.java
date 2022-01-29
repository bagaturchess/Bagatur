package bagaturchess.learning.goldmiddle.impl4.cfg;


import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.learning.goldmiddle.impl4.eval.BagaturEvaluatorFactory_GOLDENMIDDLE;
import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfig_V20_GOLDENMIDDLE_Play implements IEvalConfig {
	
	
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
		
		return BagaturEvaluatorFactory_GOLDENMIDDLE.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		
		return BagaturPawnsEvalFactory.class.getName();
	}
}
