package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.advanced.AdjustableFeaturePST;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;
import bagaturchess.learning.impl.utils.PSTConstants;


public class Bagatur_ALL_FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_ALL_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();

		/**
		 * STANDARD ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       		, "MATERIAL.PAWN"       	, STANDARD         , 0, 2000,  0, 0, 2000,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     		, "MATERIAL.KNIGHT"     	, STANDARD         , 0, 2000,  0, 0, 2000,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     		, "MATERIAL.BISHOP"     	, STANDARD         , 0, 2000,  0, 0, 2000,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       		, "MATERIAL.ROOK"       	, STANDARD         , 0, 2000,  0, 0, 2000,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      		, "MATERIAL.QUEEN"      	, STANDARD         , 0, 2000,  0, 0, 2000,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_DOUBLE_BISHOP  	, "BISHOPS.DOUBLE"	    	, STANDARD         , 0, 100,  0, 0, 200,  0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_TEMPO				, "STANDARD.TEMPO"   		, STANDARD         , 0, 50,  0, 0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_CASTLING			, "STANDARD.CASTLING"   	, STANDARD         , 0, 50,  0, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_FIANCHETTO 		, "STANDARD.FIANCHETTO" 	, STANDARD         , 0, 100, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_TRAP_BISHOP 		, "STANDARD.TRAP.BISHOP" 	, STANDARD         , -150, 0, 0, -150, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_BLOCKED_PAWN 		, "STANDARD.BLOCKED.PAWN" 	, STANDARD         , -100, 0, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_KINGS_OPPOSITION 	, "KINGS.OPPOSITION"   		, STANDARD         , 0, 0, 0, 0, 100, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_STANDARD_DIST_KINGS  	    , "KINGS.DISTANCE"      	, STANDARD         ,
				PSTConstants.createArray(8, -128), PSTConstants.createArray(8, 128), new double[] {0,   0,   0,   0,   0,   0,  0, 0},
				PSTConstants.createArray(8, -128), PSTConstants.createArray(8, 128), new double[] {0,   0,   0,   0,   0,   0,  0, 0}
		));
		
		
		/**
		 * PAWNS ITERATION
		 */
		
		/**
		 * PIECES ITERATION
		 */
		
		
		/**
		 * MOVES ITERATION
		 */
				
		
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
