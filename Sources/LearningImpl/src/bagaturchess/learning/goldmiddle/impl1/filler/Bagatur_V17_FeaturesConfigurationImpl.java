package bagaturchess.learning.goldmiddle.impl1.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_V17_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_V17_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Material
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, STANDARD         , 0, 2000,  80, 0, 2000,  65 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, STANDARD         , 0, 2000,  375, 0, 2000,  311 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, STANDARD         , 0, 2000,  360, 0, 2000,  343 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, STANDARD         , 0, 2000,  455, 0, 2000,  582 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, STANDARD         , 0, 2000,  1072, 0, 2000,  1027 ));
		
		
		//Imbalance
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS		, "MATERIAL.IMBALANCE.NIGHT.PAWNS"		, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS		, "MATERIAL.IMBALANCE.ROOK.PAWNS"		, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE	, "MATERIAL.IMBALANCE.BISHOP.DOUBLE"	, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS	, "MATERIAL.IMBALANCE.QUEEN.KNIGHTS"	, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR		, "MATERIAL.IMBALANCE.ROOK.PAIR"		, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//PST
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIECE_SQUARE_TABLE					, "PIECE.SQUARE.TABLE"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Pawns
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_DOUBLE							, "PAWN.DOUBLE"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_CONNECTED						, "PAWN.CONNECTED"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_NEIGHBOUR						, "PAWN.NEIGHBOUR"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED							, "PAWN.PASSED"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE				, "PASSED.UNSTOPPABLE"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_SHIELD							, "PAWN.SHIELD"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Mobility
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT						, "MOBILITY.KNIGHT"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP						, "MOBILITY.BISHOP"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK						, "MOBILITY.ROOK"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN						, "MOBILITY.QUEEN"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KING						, "MOBILITY.KING"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
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
