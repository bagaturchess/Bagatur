package bagaturchess.learning.goldmiddle.impl4.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_V20_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_V20_FeaturesConstants {
	
	
	private static final double INITIAL_WEIGHT 	= 0.01;
	private static final double MAX_WEIGHT 		= 32;
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Material
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, GROUP1         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, GROUP1         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, GROUP1         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, GROUP1         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, GROUP1         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );		
		
		
		//PST
		create2Features(new_featuresSet, FEATURE_ID_PIECE_SQUARE_TABLE					, "PIECE.SQUARE.TABLE"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Pawns
		create2Features(new_featuresSet, FEATURE_ID_PAWN_DOUBLE							, "PAWN.DOUBLE"							, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_CONNECTED						, "PAWN.CONNECTED"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_NEIGHBOUR						, "PAWN.NEIGHBOUR"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_ISOLATED						, "PAWN.ISOLATED"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_BACKWARD						, "PAWN.BACKWARD"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_INVERSE						, "PAWN.INVERSE"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED							, "PAWN.PASSED"							, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED_CANDIDATE				, "PAWN.PASSED.CANDIDATE"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_PASSED_UNSTOPPABLE				, "PAWN.PASSED.UNSTOPPABLE"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_PAWN_SHIELD							, "PAWN.SHIELD"							, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Imbalance
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_IMBALANCE_KNIGHT_PAWNS		, "MATERIAL.IMBALANCE.KNIGHT.PAWNS"		, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS		, "MATERIAL.IMBALANCE.ROOK.PAWNS"		, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE	, "MATERIAL.IMBALANCE.BISHOP.DOUBLE"	, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS	, "MATERIAL.IMBALANCE.QUEEN.KNIGHTS"	, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR		, "MATERIAL.IMBALANCE.ROOK.PAIR"		, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );

		
		//Mobility
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_KNIGHT						, "MOBILITY.KNIGHT"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_BISHOP						, "MOBILITY.BISHOP"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_ROOK						, "MOBILITY.ROOK"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_QUEEN						, "MOBILITY.QUEEN"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_MOBILITY_KING						, "MOBILITY.KING"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Threats
		create2Features(new_featuresSet, FEATURE_ID_THREAT_DOUBLE_ATTACKED				, "THREAT.DOUBLE.ATTACKED"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_UNUSED_OUTPOST				, "THREAT.UNUSED.OUTPOST"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_PAWN_PUSH					, "THREAT.PAWN.PUSH"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_PAWN_ATTACKS					, "THREAT.PAWN.ATTACKS"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS		, "THREAT.MULTIPLE.PAWN.ATTACKS"		, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_MAJOR_ATTACKED				, "THREAT.MAJOR.ATTACKED"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_PAWN_ATTACKED				, "THREAT.PAWN.ATTACKED"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK			, "THREAT.QUEEN.ATTACKED.ROOK"			, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR			, "THREAT.QUEEN.ATTACKED.MINOR"			, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_THREAT_ROOK_ATTACKED				, "THREAT.ROOK.ATTACKED"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		
		//Others
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_SIDE_TO_MOVE					, "OTHERS.SIDE.TO.MOVE"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS			, "OTHERS.ONLY.MAJOR.DEFENDERS"			, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_BATTERY					, "OTHERS.ROOK.BATTERY"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_7TH_RANK				, "OTHERS.ROOK.7TH.RANK"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_TRAPPED					, "OTHERS.ROOK.TRAPPED"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_FILE_OPEN				, "OTHERS.ROOK.FILE.OPEN"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED	, "OTHERS.ROOK.FILE.SEMI.OPEN.ISOLATED"	, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN			, "OTHERS.ROOK.FILE.SEMI.OPEN"			, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_BISHOP_OUTPOST				, "OTHERS.BISHOP.OUTPOST"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_BISHOP_PRISON				, "OTHERS.BISHOP.PRISON"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_BISHOP_PAWNS					, "OTHERS.BISHOP.PAWNS"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK			, "OTHERS.BISHOP.CENTER.ATTACK"			, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_PAWN_BLOCKAGE				, "OTHERS.PAWN.BLOCKAGE"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_KNIGHT_OUTPOST				, "OTHERS.KNIGHT.OUTPOST"				, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_CASTLING						, "OTHERS.CASTLING"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_PINNED						, "OTHERS.PINNED"						, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_OTHERS_DISCOVERED					, "OTHERS.DISCOVERED"					, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		//King safety and space
		create2Features(new_featuresSet, FEATURE_ID_KING_SAFETY							, "KING.SAFETY"							, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		create2Features(new_featuresSet, FEATURE_ID_SPACE								, "SPACE"								, GROUP2         , 0, MAX_WEIGHT, INITIAL_WEIGHT, 0, 0,  0 );
		
		
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
