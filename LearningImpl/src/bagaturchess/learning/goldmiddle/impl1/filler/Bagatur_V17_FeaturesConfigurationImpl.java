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
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, GROUP1         , 0, 2000,  80, 0, 2000,  65 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, GROUP1         , 0, 2000,  375, 0, 2000,  311 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, GROUP1         , 0, 2000,  360, 0, 2000,  343 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, GROUP1         , 0, 2000,  455, 0, 2000,  582 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, GROUP1         , 0, 2000,  1072, 0, 2000,  1027 ));
		
		
		//Imbalance
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS		, "MATERIAL.IMBALANCE.NIGHT.PAWNS"		, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS		, "MATERIAL.IMBALANCE.ROOK.PAWNS"		, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE	, "MATERIAL.IMBALANCE.BISHOP.DOUBLE"	, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS	, "MATERIAL.IMBALANCE.QUEEN.KNIGHTS"	, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR		, "MATERIAL.IMBALANCE.ROOK.PAIR"		, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//PST
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIECE_SQUARE_TABLE					, "PIECE.SQUARE.TABLE"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Pawns
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_DOUBLE							, "PAWN.DOUBLE"							, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_CONNECTED						, "PAWN.CONNECTED"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_NEIGHBOUR						, "PAWN.NEIGHBOUR"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED							, "PAWN.PASSED"							, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE				, "PAWN.PASSED.UNSTOPPABLE"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_SHIELD							, "PAWN.SHIELD"							, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Mobility
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT						, "MOBILITY.KNIGHT"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP						, "MOBILITY.BISHOP"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK						, "MOBILITY.ROOK"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN						, "MOBILITY.QUEEN"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KING						, "MOBILITY.KING"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Threats
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_DOUBLE_ATTACKED				, "THREAT.DOUBLE.ATTACKED"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_UNUSED_OUTPOST				, "THREAT.UNUSED.OUTPOST"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_PUSH					, "THREAT.PAWN.PUSH"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_ATTACKS					, "THREAT.PAWN.ATTACKS"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS		, "THREAT.MULTIPLE.PAWN.ATTACKS"		, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_MAJOR_ATTACKED				, "THREAT.MAJOR.ATTACKED"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_ATTACKED				, "THREAT.PAWN.ATTACKED"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK			, "THREAT.QUEEN.ATTACKED.ROOK"			, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR			, "THREAT.QUEEN.ATTACKED.MINOR"			, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_ROOK_ATTACKED				, "THREAT.ROOK.ATTACKED"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_NIGHT_FORK					, "THREAT.NIGHT.FORK"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_NIGHT_FORK_KING				, "THREAT.NIGHT.FORK.KING"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Others
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_SIDE_TO_MOVE					, "OTHERS.SIDE.TO.MOVE"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS			, "OTHERS.ONLY.MAJOR.DEFENDERS"			, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_HANGING						, "OTHERS.HANGING"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_HANGING_2					, "OTHERS.HANGING.2"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_BATTERY					, "OTHERS.ROOK.BATTERY"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_7TH_RANK				, "OTHERS.ROOK.7TH.RANK"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_TRAPPED					, "OTHERS.ROOK.TRAPPED"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_OPEN				, "OTHERS.ROOK.FILE.OPEN"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED	, "OTHERS.ROOK.FILE.SEMI.OPEN.ISOLATED"	, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN			, "OTHERS.ROOK.FILE.SEMI.OPEN"			, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_OUTPOST				, "OTHERS.BISHOP.OUTPOST"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_PRISON				, "OTHERS.BISHOP.PRISON"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_PAWNS					, "OTHERS.BISHOP.PAWNS"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK			, "OTHERS.BISHOP.CENTER.ATTACK"			, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_PAWN_BLOCKAGE				, "OTHERS.PAWN.BLOCKAGE"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_KNIGHT_OUTPOST				, "OTHERS.KNIGHT.OUTPOST"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_IN_CHECK						, "OTHERS.IN.CHECK"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
		//King safety and space
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_SAFETY							, "KING.SAFETY"							, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE								, "SPACE"								, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		
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
