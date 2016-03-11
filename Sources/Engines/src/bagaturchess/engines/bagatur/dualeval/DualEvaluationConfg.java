package bagaturchess.engines.bagatur.dualeval;


import bagaturchess.engines.bagatur.v12.BagaturV12PawnsEvalFactory;
import bagaturchess.search.api.IEvalConfig;


public class DualEvaluationConfg implements IEvalConfig {
	
	
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
		return DualEvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return BagaturV12PawnsEvalFactory.class.getName();
	}
}
