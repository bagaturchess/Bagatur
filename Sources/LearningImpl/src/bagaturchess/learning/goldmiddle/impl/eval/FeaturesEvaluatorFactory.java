package bagaturchess.learning.goldmiddle.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
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
		
		Features_Splitter features_splitter;
		
		try {
			
			features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		
		ISignals signals = new Signals(features_splitter.getFeatures(bitboard));
		
		return new FeaturesEvaluator(bitboard, evalCache, filler, features_splitter, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
		ISignalFiller filler = input.createFiller(bitboard);
		
		Features_Splitter features_splitter;
		
		try {
			
			features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		
		ISignals signals = new Signals(features_splitter.getFeatures(bitboard));
		
		return new FeaturesEvaluator(bitboard, evalCache, filler, features_splitter, signals);
	}
}
