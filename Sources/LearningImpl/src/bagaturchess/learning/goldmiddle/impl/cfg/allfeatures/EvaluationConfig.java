package bagaturchess.learning.goldmiddle.impl.cfg.allfeatures;


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
	public String getEvaluatorFactoryClassName() {
		return WeightsEvaluatorFactory.class.getName();
	}

	@Override
	public String getPawnsCacheFactoryClassName() {
		return WeightsPawnsEvalFactory.class.getName();
	}

}
