package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.advanced.AdjustableFeaturePST;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;
import bagaturchess.learning.impl.utils.PSTConstants;


public class ALL_FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, ALL_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       , "MATERIAL.PAWN"       , GROUP1         , 0, 2000,  62.391, 0, 2000,  85.814 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     , "MATERIAL.KNIGHT"     , GROUP1         , 0, 2000,  343.788, 0, 2000,  295.904 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     , "MATERIAL.BISHOP"     , GROUP1         , 0, 2000,  346.895, 0, 2000,  289.478 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       , "MATERIAL.ROOK"       , GROUP1         , 0, 2000,  459.381, 0, 2000,  467.684 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      , "MATERIAL.QUEEN"      , GROUP1         , 0, 2000,  1075.244, 0, 2000,  813.064 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_CASTLING   , "KINGSAFE.CASTLING"   , GROUP1         , 0, 50,  7.988, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_FIANCHETTO , "KINGSAFE.FIANCHETTO" , GROUP1         , 0, 100, 1, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_DOUBLE      , "BISHOPS.DOUBLE"	    , GROUP1         , 0, 100,  30.175, 0, 200,  54.041 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_DOUBLE      , "KNIGHTS.DOUBLE"	    , GROUP1        , 0, 100,  6.412, 0, 200,  6.646 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_DOUBLE      	, "ROOKS.DOUBLE"	    , GROUP1        , 0, 100,  33.938, 0, 200,  22.201 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_ROOKS      	, "ROOKS.5PAWNS"	    , GROUP1        , -50, 50, 1.134, -50, 50,  0.535 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_KNIGHTS     	, "KNIGHTS.5PAWNS"	    , GROUP1        , -50, 50, 1.398, -50, 50,  4.356 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_F_PAWN     , "KINGSAFE.F"          , GROUP1        , -50, 0, -3.317, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_G_PAWN     , "KINGSAFE.G"          , GROUP1        , -50, 0, -10.838, 0, 0,  0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGS_DISTANCE  	, "KINGS.DISTANCE"      , GROUP1        , -50, 50, 0.017, -50, 50,  0.101 ));
		
		/**
		 * PAWNS ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_DOUBLED       , "PAWNS.DOUBLED"       , GROUP2 , -50,  0, -0.194, -50,  0, -4.298 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISOLATED      , "PAWNS.ISOLATED"      , GROUP2 , -50,  0,-10.627, -50,  0, -10.979 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_BACKWARD      , "PAWNS.BACKWARD"      , GROUP2 , -50,  0, -4.396, -50,  0, -1.897 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SUPPORTED     , "PAWNS.SUPPORTED"     , GROUP2 ,   0, 50,  3.592,   0, 50,  4.924 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANNOTBS      , "PAWNS.CANNOTBS"      , GROUP2 , -50,  0, -1.258, -50,  0, -1.966 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED        , "PAWNS.PASSED"        , GROUP2 ,   0, 50, 10.163,   0, 50,  3.410 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_RNK    , "PAWNS.PASSED.RNK"    , GROUP2 ,   0,  2,  0.787,  0,  2,  0.859));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_UNSTOPPABLE_PASSER  , "PAWNS.UNSTOPPABLE.PASSER" , GROUP2 ,   0, 0,  0,   550, 700,  550 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS     , "PAWNS_PSTOPPERS"     , GROUP2         , 0, 50, 0.083, 0, 50, 0.462 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANDIDATE     , "PAWNS.CANDIDATE.RNK" , GROUP2 ,   0,  5,  0.412,  0,  5,  0.302));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F  	, "KINGS.PASSERS.F"     , GROUP2 ,   0,   0, 0, 0, 20, 1.340 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_FF  	, "KINGS.PASSERS.FF"    , GROUP2 ,   0,   0, 0, 0, 20, 0.979 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F_OP   , "KINGS.PASSERS.F.OP"  , GROUP2 ,   0,   0, 0, 0, 20, 1.877 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISLANTS       , "PAWNS.ISLANDS"       , GROUP2 , -50,  50, -0.536, -50,  50, -0.507 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS         , "PAWNS.GARDS"         , GROUP2 ,   0,  50,   8.411, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS_REM     , "PAWNS.GARDS.REM"     , GROUP2 , -50,   0,   -4.707, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS        , "PAWNS.STORMS"        , GROUP2 ,   0,  50,   1.982, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS_CLS    , "PAWNS.STORMS.CLS"    , GROUP2 ,   0,  50,   2.924, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_OPENNED       , "PAWNS.OPENNED"       , GROUP2 , -50,   0, -35.881, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OWN    , "PAWNS.SEMIOP.OWN"    , GROUP2 , -50,   0, -26.449, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OP     , "PAWNS.SEMIOP.OP"     , GROUP2 , -50,   0, -11.361, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_WEAK          , "PAWNS.WEAK"          , GROUP2 , -50,   0,  -2.085,  -50,  0, -0.945));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE        		, "SPACE"         	    , GROUP2 ,   0,  10, 0.613, 0, 10, 0.565));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_INFRONT_PASSER , "ROOK.INFRONT.PASSER" , GROUP2 , -50,   0, -0.019, -50,  0, 0));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_BEHIND_PASSER  , "ROOK.BEHIND.PASSER"  , GROUP2 ,   0,  50, 1.245, 0,  50, 3.475));
		
		/**
		 * PIECES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_PAWN			, "PST.PAWN"   		    , GROUP3 ,  0,  2,  0.651,  0,  2,  0.741));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KING			, "PST.KING"   		    , GROUP3 ,  0,  2,  1.008,  0,  2,  0.912));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KNIGHT		    , "PST.KNIGHTS" 		, GROUP3 ,  0,  2,  0.641,  0,  2,  0.633));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_BISHOP		    , "PST.BISHOPS"  		, GROUP3 ,  0,  2,  0.592,  0,  2,  0.450));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_ROOK			, "PST.ROOKS"		  	, GROUP3 ,  0,  5,  0.781,  0,  5,  0.649));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_QUEEN			, "PST.QUEENS"   		, GROUP3 ,  0,  2,  0.331,  0,  2,  0.966));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_BAD         , "BISHOPS.BAD"   	    , GROUP3 , -50, 0, -0.894, -50, 0, -0.985 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_OUTPOST     , "KNIGHT.OUTPOST"      , GROUP3 ,   0, 50,14.938,   0, 50, 0.526 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_OPENED        , "ROOKS.OPENED"        , GROUP3 ,  0, 50, 16.513,  0, 50,  0.909 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_SEMIOPENED    , "ROOKS.SEMIOPENED"    , GROUP3 ,  0, 50,  8.937,  0, 50,  2.092 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_KNIGHT      , "TROPISM.KNIGHT"      , GROUP3 ,  0, 10, 0.070, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_BISHOP      , "TROPISM.BISHOP"      , GROUP3 ,  0, 10, 0.348, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_ROOK        , "TROPISM.ROOK"        , GROUP3 ,  0, 10, 0.202, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_QUEEN       , "TROPISM.QUEEN"       , GROUP3 ,  0, 10, 0.148, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_7TH_2TH       , "ROOKS.7TH.2TH"       , GROUP3 ,  0, 50,  8.056,  0,  50,  7.939 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_QUEENS_7TH_2TH      , "QUEENS.7TH.2TH"      , GROUP3 ,  0, 50,  5.735,  0,  50,  9.276 ));
		
		
		/**
		 * MOVES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L1    	    , "KINGSAFETY.L1"   	, GROUP4  , 0, 100, 30.900, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L2    	    , "KINGSAFETY.L2"   	, GROUP4  , 0, 100, 12.867, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT 	, "MOBILITY.KNIGHT" 	, GROUP4  ,  0,  2,  0.277,  0,  2,  0.587));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP 	, "MOBILITY.BISHOP" 	, GROUP4  ,  0,  2,  0.238,  0,  2,  0.578));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK   	, "MOBILITY.ROOK"   	, GROUP4  ,  0,  2,  0.220,  0,  2,  0.480));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN  	, "MOBILITY.QUEEN"  	, GROUP4  ,  0,  2,  0.190,  0,  2,  0.908));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT_S   , "MOBILITY.KNIGHT.S"   , GROUP4  ,  0,  2,  0.140,  0,  2,  0.348));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP_S   , "MOBILITY.BISHOP.S"   , GROUP4  ,  0,  2,  0.113,  0,  2,  0.290));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK_S     , "MOBILITY.ROOK.S"     , GROUP4  ,  0,  2,  0.118,  0,  2,  0.325));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN_S    , "MOBILITY.QUEEN.S"    , GROUP4  ,  0,  2,  0.292,  0,  2,  0.911));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_H   	    , "ROOKS.PAIR.H"    	, GROUP4  ,  0, 50, 1.124,  0, 50, 3.964 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_V   	    , "ROOKS.PAIR.V"    	, GROUP4  ,  0, 50, 1.412,  0, 50, 3.710 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP  				, "TRAP"      			, GROUP4  ,  -50,  0,  -0.274,  -50,  0,  -0.121 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BIGGER_PIECE 	, "PIN.BIGGER"       	, GROUP4  ,  0,  100,  16.731,  0,  100,  55.964 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_EQUAL_PIECE  	, "PIN.EQ"           	, GROUP4  ,  0,  100,  10.631,  0,  100,  7.708 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_LOWER_PIECE  	, "PIN.LOWER"        	, GROUP4  ,  0,  100,  2.355,  0,  100,  9.281 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BIGGER_PIECE , "ATTACK.BIGGER"       , GROUP4  ,  0,  100,  31.874,  0,  100,  52.656 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_EQUAL_PIECE  , "ATTACK.EQ"           , GROUP4  ,  0,  100,  18.373,  0,  100,  21.258 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_LOWER_PIECE  , "ATTACK.LOWER"        , GROUP4  ,  0,  100,  3.356,  0,  100,  21.805 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PIECES       , "HUNGED.PIECE"        , GROUP4  ,  -100,  0, -0.043,  -100,  0, -0.449 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PAWNS       	, "HUNGED.PAWNS"       	, GROUP4  ,  -100,  0, -0.523,  -100,  0, -0.098 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_ALL       	, "HUNGED.ALL"        	, GROUP4  ,  -100,  0, -0.221,  -100,  0, -0.255 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS_A   , "PAWNS_PSTOPPERS_A"   , GROUP4         , 0, 50, 0, 0, 50, 0 ));
		
		/**
		 * FIELDS STATES ITERATION
		 */
		/*add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_EQ , "CONTROL.EQ" , FIELDS_STATES_ITERATION,
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128), PSTConstants.createArray(64, 0),
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128),PSTConstants.createArray(64, 0)
		));
		add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_MORE , "CONTROL.MORE" , FIELDS_STATES_ITERATION,
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128), PSTConstants.createArray(64, 0),
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128),PSTConstants.createArray(64, 0)
		));*/
		
		
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
