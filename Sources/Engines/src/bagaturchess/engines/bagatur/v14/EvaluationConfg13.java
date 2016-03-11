package bagaturchess.engines.bagatur.v14;


import bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2;
import bagaturchess.engines.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.engines.bagatur.eval.IBagaturEvalConfig;


public class EvaluationConfg13 extends BagaturEvalConfigImpl_v2 implements IBagaturEvalConfig {
	
	
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
		return BagaturEvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
	}
}
