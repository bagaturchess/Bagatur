package bagaturchess.engines.cfg.materialonly;


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
	
	
	@Override
	public boolean isTrainingMode() {
		return false;
	}
	
	
	public String getEvaluatorFactoryClassName() {
		return bagaturchess.engines.cfg.materialonly.MaterialEvaluatorFactory.class.getName();
	}
	
	
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
	}
}
