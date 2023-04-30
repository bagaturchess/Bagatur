package bagaturchess.learning.goldmiddle.impl2.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.advanced.AdjustableFeaturePST;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;
import bagaturchess.learning.impl.utils.PSTConstants;


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
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS 		, "MATERIAL.IMBALANCE.NIGHT.PAWNS" 		, STANDARD,
				PSTConstants.createArray(9, -256), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0),
				PSTConstants.createArray(9, -256), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS 		, "MATERIAL.IMBALANCE.ROOK.PAWNS" 		, STANDARD,
				PSTConstants.createArray(9, -256), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0),
				PSTConstants.createArray(9, -256), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE	, "MATERIAL.IMBALANCE.BISHOP.DOUBLE"	, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS	, "MATERIAL.IMBALANCE.QUEEN.KNIGHTS"	, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR		, "MATERIAL.IMBALANCE.ROOK.PAIR"		, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//PST
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIECE_SQUARE_TABLE					, "PIECE.SQUARE.TABLE"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Pawns
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_DOUBLE							, "PAWN.DOUBLE"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_PAWN_CONNECTED 						, "PAWN.CONNECTED" 						, STANDARD,
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_PAWN_NEIGHBOUR 						, "PAWN.NEIGHBOUR" 						, STANDARD,
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_PAWN_PASSED 							, "PAWN.PASSED" 						, STANDARD,
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_PAWN_PASSED_CANDIDATE 				, "PAWN.PASSED.CANDIDATE" 				, STANDARD,
				PSTConstants.createArray(6, 0), PSTConstants.createArray(6, 256), PSTConstants.createArray(6, 0),
				PSTConstants.createArray(6, 0), PSTConstants.createArray(6, 256), PSTConstants.createArray(6, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE				, "PAWN.PASSED.UNSTOPPABLE"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWN_SHIELD							, "PAWN.SHIELD"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Mobility
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MOBILITY_KNIGHT 						, "MOBILITY.KNIGHT" 					, STANDARD,
				PSTConstants.createArray(9, -128), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0),
				PSTConstants.createArray(9, -128), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MOBILITY_BISHOP 						, "MOBILITY.BISHOP" 					, STANDARD,
				PSTConstants.createArray(14, -128), PSTConstants.createArray(14, 256), PSTConstants.createArray(14, 0),
				PSTConstants.createArray(14, -128), PSTConstants.createArray(14, 256), PSTConstants.createArray(14, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MOBILITY_ROOK 						, "MOBILITY.ROOK" 						, STANDARD,
				PSTConstants.createArray(15, -128), PSTConstants.createArray(15, 256), PSTConstants.createArray(15, 0),
				PSTConstants.createArray(15, -128), PSTConstants.createArray(15, 256), PSTConstants.createArray(15, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MOBILITY_QUEEN 						, "MOBILITY.QUEEN" 						, STANDARD,
				PSTConstants.createArray(28, -128), PSTConstants.createArray(28, 256), PSTConstants.createArray(28, 0),
				PSTConstants.createArray(28, -128), PSTConstants.createArray(28, 256), PSTConstants.createArray(28, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_MOBILITY_KING 						, "MOBILITY.KING" 						, STANDARD,
				PSTConstants.createArray(9, -128), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0),
				PSTConstants.createArray(9, -128), PSTConstants.createArray(9, 256), PSTConstants.createArray(9, 0)
		));
		
		
		//Threats
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_THREAT_DOUBLE_ATTACKED 				, "THREAT.DOUBLE.ATTACKED" 				, STANDARD,
				PSTConstants.createArray(7, -128), PSTConstants.createArray(7, 128), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, -128), PSTConstants.createArray(7, 128), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_UNUSED_OUTPOST				, "THREAT.UNUSED.OUTPOST"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_PUSH					, "THREAT.PAWN.PUSH"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_ATTACKS					, "THREAT.PAWN.ATTACKS"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS		, "THREAT.MULTIPLE.PAWN.ATTACKS"		, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_MAJOR_ATTACKED				, "THREAT.MAJOR.ATTACKED"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_PAWN_ATTACKED				, "THREAT.PAWN.ATTACKED"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK			, "THREAT.QUEEN.ATTACKED.ROOK"			, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR			, "THREAT.QUEEN.ATTACKED.MINOR"			, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_ROOK_ATTACKED				, "THREAT.ROOK.ATTACKED"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_NIGHT_FORK					, "THREAT.NIGHT.FORK"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREAT_NIGHT_FORK_KING				, "THREAT.NIGHT.FORK.KING"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//Others
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_SIDE_TO_MOVE					, "OTHERS.SIDE.TO.MOVE"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS 			, "OTHERS.ONLY.MAJOR.DEFENDERS" 		, STANDARD,
				PSTConstants.createArray(7, -256), PSTConstants.createArray(7, 512), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, -256), PSTConstants.createArray(7, 512), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_HANGING 						, "OTHERS.HANGING" 						, STANDARD,
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0),
				PSTConstants.createArray(7, 0), PSTConstants.createArray(7, 256), PSTConstants.createArray(7, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_HANGING_2 					, "OTHERS.HANGING.2" 					, STANDARD,
				PSTConstants.createArray(6, 0), PSTConstants.createArray(6, 1024), PSTConstants.createArray(6, 0),
				PSTConstants.createArray(6, 0), PSTConstants.createArray(6, 1024), PSTConstants.createArray(6, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_BATTERY					, "OTHERS.ROOK.BATTERY"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_7TH_RANK				, "OTHERS.ROOK.7TH.RANK"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_TRAPPED					, "OTHERS.ROOK.TRAPPED"					, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_OPEN				, "OTHERS.ROOK.FILE.OPEN"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED	, "OTHERS.ROOK.FILE.SEMI.OPEN.ISOLATED"	, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN			, "OTHERS.ROOK.FILE.SEMI.OPEN"			, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_BISHOP_OUTPOST 				, "OTHERS.BISHOP.OUTPOST" 				, STANDARD,
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0),
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_PRISON				, "OTHERS.BISHOP.PRISON"				, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_BISHOP_PAWNS 					, "OTHERS.BISHOP.PAWNS" 				, STANDARD,
				PSTConstants.createArray(9, -1024), PSTConstants.createArray(9, 1024), PSTConstants.createArray(9, 0),
				PSTConstants.createArray(9, -1024), PSTConstants.createArray(9, 1024), PSTConstants.createArray(9, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK			, "OTHERS.BISHOP.CENTER.ATTACK"			, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_PAWN_BLOCKAGE 				, "OTHERS.PAWN.BLOCKAGE" 				, STANDARD,
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0),
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0)
		));
		add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_OTHERS_KNIGHT_OUTPOST 				, "OTHERS.KNIGHT.OUTPOST" 				, STANDARD,
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0),
				PSTConstants.createArray(8, -1024), PSTConstants.createArray(8, 1024), PSTConstants.createArray(8, 0)
		));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_OTHERS_IN_CHECK						, "OTHERS.IN.CHECK"						, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
		//King safety and space
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_SAFETY							, "KING.SAFETY"							, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE								, "SPACE"								, STANDARD         , 0, 100,  1, 0, 100,  1 ));
		
		
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
