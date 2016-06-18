package eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.impl1.Features;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.EvalCache;


public class EvaluatorLearningFactory implements IEvaluatorFactory {
	
	public EvaluatorLearningFactory() {
	}
	
	
	@Override
	public IEvaluator create(IBitBoard bitboard, EvalCache evalCache, IEvalConfig evalConfig) {
		ISignalFiller filler = new BagaturSignalFiller(bitboard);
		Features features = createFeatures();
		ISignals signals = features.createSignals();
		return new EvaluatorLearning(bitboard, evalCache, filler, features, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, EvalCache evalCache) {
		ISignalFiller filler = new BagaturSignalFiller(bitboard);
		Features features = createFeatures();
		ISignals signals = features.createSignals();
		return new EvaluatorLearning(bitboard, evalCache, filler, features, signals);
	}
	
	
	private Features createFeatures() {
		Features features = null;
		try {
			//features = Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName());
			//features = Features.load(FeaturesConfigurationBagaturImpl.class.getName());
			features = Features.load();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return features;
	}
}
