package bagaturchess.deeplearning.impl_nnue_v3;


import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfig implements IEvalConfig {
	
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
		return NNUEEvaluatorFactory.class.getName();
	}
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory.class.getName();
	}
}
