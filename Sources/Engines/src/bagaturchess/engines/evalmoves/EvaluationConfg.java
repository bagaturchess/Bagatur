package bagaturchess.engines.evalmoves;


import bagaturchess.search.api.IEvalConfig;


public class EvaluationConfg implements IEvalConfig {
	
	
	@Override
	public boolean useLazyEval() {
		return false;
	}
	
	
	@Override
	public boolean useEvalCache() {
		return false;
	}
	
	
	@Override
	public String getEvaluatorFactoryClassName() {
		return EvaluatorMovesFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return PawnsEvalFactory.class.getName();
	}
}
