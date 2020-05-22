package bagaturchess.learning.goldmiddle.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.Bagatur_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.Bagatur_ALL_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.ALL_LearningInputImpl;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class FeaturesEvaluatorFactory implements IEvaluatorFactory {
	
	public FeaturesEvaluatorFactory() {
	}
	
	
	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
		ISignalFiller filler = input.createFiller(bitboard);
		
		Features features = createFeatures();
		ISignals signals = features.createSignals();
		return new FeaturesEvaluator(bitboard, evalCache, filler, features, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
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
