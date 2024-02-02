package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class BagaturEval_FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, BagaturEval_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL       	  , "MATERIAL"       	, GROUP4         , 0, 50, 1.167, 0, 50, 0.801 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD       	  , "STANDARD"       	, GROUP4         , 0, 50, 0.191, 0, 50, 0.070 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST		       	  , "PST"       	 	, GROUP4         , 0, 50, 1.051, 0, 50, 0.653 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STANDARD    , "PAWNS_STANDARD"    , GROUP4         , 0, 50, 0.704, 0, 50, 0.435 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED      , "PAWNS_PASSED"      , GROUP4         , 0, 50, 0.034, 0, 50, 1.711 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_KING , "PAWNS_PASSED_KING" , GROUP4         , 0, 50, 0.000, 0, 50, 2.408 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS   , "PAWNS_PSTOPPERS"   , GROUP4         , 0, 50, 0.083, 0, 50, 0.462 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS_A , "PAWNS_PSTOPPERS_A" , GROUP4         , 0, 50, 0.098, 0, 50, 1.058 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOKQUEEN   , "PAWNS_ROOKQUEEN"   , GROUP4         , 0, 50, 0.660, 0, 50, 2.004 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY       	  , "MOBILITY"       	, GROUP4         , 0, 50, 0.757, 0, 50, 0.038 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_S        , "MOBILITY_S"        , GROUP4         , 0, 50, 1.714, 0, 50, 0.860 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFETY        , "KINGSAFETY"        , GROUP4         , 0, 50, 3.378, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE       	  , "SPACE"       		, GROUP4         , 0, 50, 0.704, 0, 50, 0.222 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED       	  , "HUNGED"       		, GROUP4         , 0, 50, 1.341, 0, 50, 2.180 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAPPED       	  , "TRAPPED"       	, GROUP4         , 0, 50, 0.442, 0, 50, 0.605 ));
		
		return new_featuresSet.toArray(new IFeature[0]);
	}
	
	
	protected void add(Set<IFeature> featuresSet, IFeature feature) {
		if (featuresSet.contains(feature)) {
			throw new IllegalStateException("Duplicated feature id " + feature.getId());
		} else {
			featuresSet.add(feature);
		}
	}
}
