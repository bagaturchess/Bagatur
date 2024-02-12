package bagaturchess.deeplearning_deepnetts.impl4_v20.eval;


import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfig_NNTraining implements IEvalConfig {
	
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
		return NeuralNetworkEvaluatorFactory.class.getName();
	}
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory.class.getName();
	}

}
