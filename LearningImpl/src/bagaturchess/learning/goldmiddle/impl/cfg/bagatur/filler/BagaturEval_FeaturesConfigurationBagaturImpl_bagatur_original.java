package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class BagaturEval_FeaturesConfigurationBagaturImpl_bagatur_original implements IFeaturesConfiguration, IFeatureComplexity, BagaturEval_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL       	  , "MATERIAL"       	, MOVES_ITERATION         , 1.31, 1.31, 1.31, 1.31, 1.31, 1.31 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD       	  , "STANDARD"       	, MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST		       	  , "PST"       	 	, MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STANDARD    , "PAWNS_STANDARD"    , MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED      , "PAWNS_PASSED"      , MOVES_ITERATION         , 1, 1, 1, 1.5, 1.5, 1.5 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_KING , "PAWNS_PASSED_KING" , MOVES_ITERATION         , 0, 0, 0, 0.3, 0.3, 0.3 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS   , "PAWNS_PSTOPPERS"   , MOVES_ITERATION         , 0, 0, 0, 0.75, 0.75, 0.75));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS_A , "PAWNS_PSTOPPERS_A" , MOVES_ITERATION         , 0, 0, 0, 1.12, 1.12, 1.12 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOKQUEEN   , "PAWNS_ROOKQUEEN"   , MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY       	  , "MOBILITY"       	, MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_S        , "MOBILITY_S"        , MOVES_ITERATION         , 1, 1, 1, 1, 1, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFETY        , "KINGSAFETY"        , MOVES_ITERATION         , 3, 3, 3, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE       	  , "SPACE"       		, MOVES_ITERATION         , 0.3, 0.3, 0.3, 0.15, 0.15, 0.15 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED       	  , "HUNGED"       		, MOVES_ITERATION         , 2, 2, 2, 4, 4, 4 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAPPED       	  , "TRAPPED"       	, MOVES_ITERATION         , 1, 1, 1, 0.83, 0.83, 0.83 ));
		
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
