package bagaturchess.learning.goldmiddle.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.impl.cfg.stockfish7.SF7_LearningInputImpl;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class FeaturesEvaluatorFactory implements IEvaluatorFactory {
	
	public FeaturesEvaluatorFactory() {
	}
	
	
	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		
		ILearningInput input = new SF7_LearningInputImpl();
		ISignalFiller filler = input.createFiller(bitboard);
		
		Features features = createFeatures();
		ISignals signals = features.createSignals();
		return new FeaturesEvaluator(bitboard, evalCache, filler, features, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		ILearningInput input = new SF7_LearningInputImpl();
		ISignalFiller filler = input.createFiller(bitboard);
		
		Features features = createFeatures();
		ISignals signals = features.createSignals();
		return new FeaturesEvaluator(bitboard, evalCache, filler, features, signals);
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
