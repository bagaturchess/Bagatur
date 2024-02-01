package bagaturchess.learning.goldmiddle.impl7.filler;


import java.util.Set;
import java.util.TreeSet;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_V41_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_V41_FeaturesConstants {
	
	
	private static final double INITIAL_WEIGHT = 0.01;
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Material
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Pawns
		create2Features(new_featuresSet, FEATURE_ID_PAWN_DOUBLE							, "PAWN.DOUBLE"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_CONNECTED						, "PAWN.CONNECTED"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_NEIGHBOUR						, "PAWN.NEIGHBOUR"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED							, "PAWN.PASSED"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED_UNSTOPPABLE				, "PAWN.PASSED.UNSTOPPABLE"				, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_SHIELD							, "PAWN.SHIELD"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Mobility
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_KNIGHT						, "MOBILITY.KNIGHT"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_BISHOP						, "MOBILITY.BISHOP"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_ROOK						, "MOBILITY.ROOK"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_QUEEN						, "MOBILITY.QUEEN"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_KING						, "MOBILITY.KING"						, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );

		
		//King safety and space
		create2Features(new_featuresSet, FEATURE_ID_KING_SAFETY							, "KING.SAFETY"							, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_SPACE								, "SPACE"								, STANDARD         , 0, 4, INITIAL_WEIGHT, 0, 0,  0 );

		
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
	
	
	private void create2Features(Set<IFeature> featuresSet, int id, String name, int complexity,
			double min, double max, double initial,
			double dummy_value1, double dummy_value2, double dummy_value3) {
		
		add(featuresSet, new AdjustableFeatureSingle(id			, name, complexity, min, max, initial, 0, 0, 0));
		
		//add(featuresSet, new AdjustableFeatureSingle(id + 1000	, name + ".E", complexity, min, max, initial, 0, 0, 0));
	}
	
	
	protected void add(Set<IFeature> featuresSet, IFeature feature) {
		
		if (featuresSet.contains(feature)) {
			
			throw new IllegalStateException("Duplicated feature id " + feature.getId());
			
		} else {
			
			featuresSet.add(feature);
		}
	}
}
