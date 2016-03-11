package bagaturchess.engines.learning.cfg;


import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory;
import bagaturchess.learning.impl.eval.FeaturesEvaluatorFactory;
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
		return FeaturesEvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return PawnsModelEvalFactory.class.getName();
	}
}
