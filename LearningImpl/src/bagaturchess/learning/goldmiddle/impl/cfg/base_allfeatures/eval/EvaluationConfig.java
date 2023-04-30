package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.eval;


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
		return WeightsEvaluatorFactory.class.getName();
	}

	@Override
	public String getPawnsCacheFactoryClassName() {
		return WeightsPawnsEvalFactory.class.getName();
	}

}
