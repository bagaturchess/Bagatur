package bagaturchess.learning.goldmiddle.impl8.filler;


import java.util.Set;
import java.util.TreeSet;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeaturePST;


public class Bagatur_PST_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_PST_FeaturesConstants {
	
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Piece-Square Tables
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_PAWN 		, "PST.PAWN"    , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_KNIGHT    , "PST.KNIGHT"  , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_BISHOP    , "PST.BISHOP"  , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_ROOK      , "PST.ROOK"    , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_QUEEN     , "PST.QUEEN"   , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		createFeature_PST(new_featuresSet, FEATURE_ID_PST_KING      , "PST.KING"    , GROUP1         , createArray(64, -2000), createArray(64, 2000), createArray(64, 100));
		
		
		int max_id = 0;
		
		for (IFeature feature: new_featuresSet) {
			
			if (feature.getId() > max_id) {
				
				max_id = feature.getId();
			}
		}
		
		
		IFeature[] result = new IFeature[max_id + 1];
		
		for (IFeature feature: new_featuresSet) {
			
			result[feature.getId()] = feature;
		}
		
		
		return result;
	}
	
	
	private void createFeature_PST(Set<IFeature> featuresSet, int id, String name, int complexity,
			double[] min, double[] max, double[] initial) {
		
		add(featuresSet, new AdjustableFeaturePST(id, name, complexity, min, max, initial, null, null, null));
	}
	
	
	private static double[] createArray(int size, double value)  {
		
		double[] result = new double[size];
		
		for (int i = 0; i < result.length; i++) {
			
			result[i] = value;
		}
		
		return result;
	}
	
	
	protected void add(Set<IFeature> featuresSet, IFeature feature) {
		
		if (featuresSet.contains(feature)) {
			
			throw new IllegalStateException("Duplicated feature id " + feature.getId());
			
		} else {
			
			featuresSet.add(feature);
		}
	}
}
