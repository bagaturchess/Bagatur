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
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Pawns
		createFeature(new_featuresSet, FEATURE_ID_PAWN_DOUBLE						, "PAWN.DOUBLE"							, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_CONNECTED					, "PAWN.CONNECTED"						, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_NEIGHBOUR					, "PAWN.NEIGHBOUR"						, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED						, "PAWN.PASSED"							, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_PASSED_UNSTOPPABLE			, "PAWN.PASSED.UNSTOPPABLE"				, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_PAWN_SHIELD						, "PAWN.SHIELD"							, GROUP1         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Mobility
		createFeature(new_featuresSet, FEATURE_ID_MOBILITY_KNIGHT					, "MOBILITY.KNIGHT"						, GROUP2         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MOBILITY_BISHOP					, "MOBILITY.BISHOP"						, GROUP2         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MOBILITY_ROOK						, "MOBILITY.ROOK"						, GROUP2         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MOBILITY_QUEEN					, "MOBILITY.QUEEN"						, GROUP2         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_MOBILITY_KING						, "MOBILITY.KING"						, GROUP2         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );

		
		//King safety and space
		createFeature(new_featuresSet, FEATURE_ID_KING_SAFETY						, "KING.SAFETY"							, GROUP3         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		createFeature(new_featuresSet, FEATURE_ID_SPACE								, "SPACE"								, GROUP3         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );

		
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
