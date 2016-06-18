package cfg;


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
		return "eval.EvaluatorLearningFactory";
	}

	@Override
	public String getPawnsCacheFactoryClassName() {
		return "bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory";
	}

}
