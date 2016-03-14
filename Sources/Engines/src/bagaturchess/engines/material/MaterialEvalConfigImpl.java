package bagaturchess.engines.material;


import bagaturchess.search.api.IEvalConfig;


public class MaterialEvalConfigImpl implements IEvalConfig {
	
	
	public MaterialEvalConfigImpl() {
		
	}
	
	
	public boolean useEvalCache() {
		return false;
	}
	
	
	public boolean useLazyEval() {
		return false;
	}
	
	
	public String getEvaluatorFactoryClassName() {
		return bagaturchess.engines.material.MaterialEvaluatorFactory1.class.getName();
	}
	
	
	public String getPawnsCacheFactoryClassName() {
		return bagaturchess.engines.bagatur.eval.BagaturPawnsEvalFactory.class.getName();
	}
}
