package bagaturchess.engines.bagatur.eval_v15;


import bagaturchess.search.api.IEvalConfig;


public class EvalConfig_V15 implements IEvalConfig {
	
	
	public boolean useEvalCache() {
		return true;
	}
	
	
	public boolean useLazyEval() {
		return true;
	}
	
	
	public String getEvaluatorFactoryClassName() {
		return EvalFactory_V15.class.getName();
	}
	
	
	public String getPawnsCacheFactoryClassName() {
		return PawnsEvalFactory_V15.class.getName();
	}
}
