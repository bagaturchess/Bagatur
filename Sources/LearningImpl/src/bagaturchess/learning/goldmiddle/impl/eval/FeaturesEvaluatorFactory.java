package bagaturchess.learning.goldmiddle.impl.eval;


import java.util.Map;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.learning.impl.features.baseimpl.FeaturesByMaterialFactor;
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
		
		Map<Integer, IFeature[]> features_by_material_factor;
		
		try {
			
			features_by_material_factor = FeaturesByMaterialFactor.load(FeaturesByMaterialFactor.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName()).getFeaturesForEachMaterialFactor();
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		
		ISignals signals = new Signals(features_by_material_factor.get(31));
		
		return new FeaturesEvaluator(bitboard, evalCache, filler, features_by_material_factor, signals);
	}


	@Override
	public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
		ISignalFiller filler = input.createFiller(bitboard);
		
		Map<Integer, IFeature[]> features_by_material_factor;
		
		try {
			
			features_by_material_factor = FeaturesByMaterialFactor.load(FeaturesByMaterialFactor.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName()).getFeaturesForEachMaterialFactor();
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		
		ISignals signals = new Signals(features_by_material_factor.get(31));
		
		return new FeaturesEvaluator(bitboard, evalCache, filler, features_by_material_factor, signals);
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
