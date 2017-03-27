package bagaturchess.learning.goldmiddle.run.cfg;


import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.BagaturPawnsEvalFactory;
import bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluatorFactory;
import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfg implements IEvalConfig {

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
		return FeaturesEvaluatorFactory.class.getName();
	}

	@Override
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
		//return PawnsModelEvalFactory.class.getName();
	}

}
