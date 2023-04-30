package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


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
		return bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval.BagaturEvaluatorFactory.class.getName();
	}

	@Override
	public String getPawnsCacheFactoryClassName() {
		return bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval.BagaturPawnsEvalFactory.class.getName();
	}

}
