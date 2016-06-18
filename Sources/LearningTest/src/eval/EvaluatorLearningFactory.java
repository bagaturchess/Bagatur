package eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.Features;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class EvaluatorLearningFactory implements IEvaluatorFactory {
	
	
	public EvaluatorLearningFactory() {
	}
	
	
	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		ISignalFiller filler = new BagaturSignalFiller(bitboard);
		Features features = null;
		try {
			features = Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		ISignals signals = features.createSignals();
		return new EvaluatorLearning(bitboard, evalCache, filler, features, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		ISignalFiller filler = new BagaturSignalFiller(bitboard);
		Features features = null;
		try {
			features = Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		ISignals signals = features.createSignals();
		return new EvaluatorLearning(bitboard, evalCache, filler, features, signals);
	}
}
