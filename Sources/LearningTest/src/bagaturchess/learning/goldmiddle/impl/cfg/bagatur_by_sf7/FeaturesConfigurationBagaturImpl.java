package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_by_sf7;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL       	  , "MATERIAL"       	, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD       	  , "STANDARD"       	, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST		       	  , "PST"       	 	, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STANDARD    , "PAWNS_STANDARD"    , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED      , "PAWNS_PASSED"      , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_KING , "PAWNS_PASSED_KING" , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS   , "PAWNS_PSTOPPERS"   , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS_A , "PAWNS_PSTOPPERS_A" , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOKQUEEN   , "PAWNS_ROOKQUEEN"   , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY       	  , "MOBILITY"       	, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_S        , "MOBILITY_S"        , MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFETY        , "KINGSAFETY"        , MOVES_ITERATION         , 0, 5, 1, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE       	  , "SPACE"       		, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED       	  , "HUNGED"       		, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAPPED       	  , "TRAPPED"       	, MOVES_ITERATION         , 0, 5, 1, 0, 5, 1 ));
		
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
