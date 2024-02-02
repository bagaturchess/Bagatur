package bagaturchess.learning.goldmiddle.impl7.filler;


import java.util.Set;
import java.util.TreeSet;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_V41_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_V41_FeaturesConstants {
	
	
	private static final double INITIAL_WEIGHT = 0.01;
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Material
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Pawns
		createFeature(new_featuresSet, FEATURE_ID_PAWN_DOUBLE						, "PAWN.DOUBLE"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_CONNECTED					, "PAWN.CONNECTED"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_NEIGHBOUR					, "PAWN.NEIGHBOUR"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED						, "PAWN.PASSED"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED_UNSTOPPABLE			, "PAWN.PASSED.UNSTOPPABLE"				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_SHIELD						, "PAWN.SHIELD"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Mobility
		createFeature_Array(new_featuresSet, FEATURE_ID_MOBILITY_KNIGHT				, "MOBILITY.KNIGHT"						, STANDARD         , createArray(9, 0), createArray(9, 4), createArray(9, INITIAL_WEIGHT));
		createFeature_Array(new_featuresSet, FEATURE_ID_MOBILITY_BISHOP				, "MOBILITY.BISHOP"						, STANDARD         , createArray(14, 0), createArray(14, 4), createArray(16, INITIAL_WEIGHT));
		createFeature_Array(new_featuresSet, FEATURE_ID_MOBILITY_ROOK				, "MOBILITY.ROOK"						, STANDARD         , createArray(15, 0), createArray(15, 4), createArray(16, INITIAL_WEIGHT));
		createFeature_Array(new_featuresSet, FEATURE_ID_MOBILITY_QUEEN				, "MOBILITY.QUEEN"						, STANDARD         , createArray(28, 0), createArray(28, 4), createArray(28, INITIAL_WEIGHT));
		createFeature_Array(new_featuresSet, FEATURE_ID_MOBILITY_KING				, "MOBILITY.KING"						, STANDARD         , createArray(9, 0), createArray(9, 4), createArray(9, INITIAL_WEIGHT));

		
		//King safety and space
		createFeature_Array(new_featuresSet, FEATURE_ID_KING_SAFETY					, "KING.SAFETY"							, STANDARD         , createArray(26, 0), createArray(26, 1000), createArray(26, INITIAL_WEIGHT));
		createFeature(new_featuresSet, FEATURE_ID_SPACE								, "SPACE"								, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );

		
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
	
	
	private void createFeature(Set<IFeature> featuresSet, int id, String name, int complexity,
			double min, double max, double initial,
			double dummy_value1, double dummy_value2, double dummy_value3) {
		
		add(featuresSet, new AdjustableFeatureSingle(id			, name, complexity, min, max, initial, 0, 0, 0));
	}
	
	
	private void createFeature_Array(Set<IFeature> featuresSet, int id, String name, int complexity,
			double[] min, double[] max, double[] initial) {
		
		add(featuresSet, new AdjustableFeatureArray(id			, name, complexity, min, max, initial, null, null, null));
	}
	
	
	private double[] createArray(int size, double value)  {
		
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
