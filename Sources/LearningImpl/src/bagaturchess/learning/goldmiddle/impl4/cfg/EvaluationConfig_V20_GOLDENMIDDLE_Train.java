package bagaturchess.learning.goldmiddle.impl4.cfg;


import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.learning.goldmiddle.impl4.eval.BagaturEvaluatorFactory_GOLDENMIDDLE;
import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfig_V20_GOLDENMIDDLE_Train implements IEvalConfig {
	
	
	@Override
	public boolean useLazyEval() {
		
		return false;
	}
	
	
	@Override
	public boolean useEvalCache() {
		
		return false;
	}
	
	
	@Override
	public boolean isTrainingMode() {
		
		return true;
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
