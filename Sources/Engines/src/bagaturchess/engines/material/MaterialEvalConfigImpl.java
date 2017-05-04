package bagaturchess.engines.material;


import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.search.api.IEvalConfig;


public class MaterialEvalConfigImpl implements IEvalConfig {
	
	
	public MaterialEvalConfigImpl() {
		
	}
	
	
	public boolean useEvalCache() {
		return true;
	}
	
	
	public boolean useLazyEval() {
		return true;
	}
	
	
	public String getEvaluatorFactoryClassName() {
		return bagaturchess.engines.material.MaterialEvaluatorFactory.class.getName();
	}
	
	
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
	}
}
