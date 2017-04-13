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
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       , "MATERIAL.PAWN"       , STANDARD         , 0, 2000,  62.391, 0, 2000,  85.814 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     , "MATERIAL.KNIGHT"     , STANDARD         , 0, 2000,  343.788, 0, 2000,  295.904 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     , "MATERIAL.BISHOP"     , STANDARD         , 0, 2000,  346.895, 0, 2000,  289.478 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       , "MATERIAL.ROOK"       , STANDARD         , 0, 2000,  459.381, 0, 2000,  467.684 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      , "MATERIAL.QUEEN"      , STANDARD         , 0, 2000,  1075.244, 0, 2000,  813.064 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_CASTLING   , "KINGSAFE.CASTLING"   , STANDARD         , 0, 50,  7.988, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_FIANCHETTO , "KINGSAFE.FIANCHETTO" , STANDARD         , 0, 100, 1, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_DOUBLE      , "BISHOPS.DOUBLE"	    , STANDARD         , 0, 100,  30.175, 0, 200,  54.041 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_DOUBLE      , "KNIGHTS.DOUBLE"	    , STANDARD        , 0, 100,  6.412, 0, 200,  6.646 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_DOUBLE      	, "ROOKS.DOUBLE"	    , STANDARD        , 0, 100,  33.938, 0, 200,  22.201 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_ROOKS      	, "ROOKS.5PAWNS"	    , STANDARD        , -50, 50, 1.134, -50, 50,  0.535 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_KNIGHTS     	, "KNIGHTS.5PAWNS"	    , STANDARD        , -50, 50, 1.398, -50, 50,  4.356 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_F_PAWN     , "KINGSAFE.F"          , STANDARD        , -50, 0, -3.317, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_G_PAWN     , "KINGSAFE.G"          , STANDARD        , -50, 0, -10.838, 0, 0,  0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGS_DISTANCE  	, "KINGS.DISTANCE"      , STANDARD        , -50, 50, 0.017, -50, 50,  0.101 ));
		
		/**
		 * PAWNS ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_DOUBLED       , "PAWNS.DOUBLED"       , PAWNS_STRUCTURE , -50,  0, -0.194, -50,  0, -4.298 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISOLATED      , "PAWNS.ISOLATED"      , PAWNS_STRUCTURE , -50,  0,-10.627, -50,  0, -10.979 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_BACKWARD      , "PAWNS.BACKWARD"      , PAWNS_STRUCTURE , -50,  0, -4.396, -50,  0, -1.897 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SUPPORTED     , "PAWNS.SUPPORTED"     , PAWNS_STRUCTURE ,   0, 50,  3.592,   0, 50,  4.924 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANNOTBS      , "PAWNS.CANNOTBS"      , PAWNS_STRUCTURE , -50,  0, -1.258, -50,  0, -1.966 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED        , "PAWNS.PASSED"        , PAWNS_STRUCTURE ,   0, 50, 10.163,   0, 50,  3.410 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_RNK    , "PAWNS.PASSED.RNK"    , PAWNS_STRUCTURE ,   0,  2,  0.787,  0,  2,  0.859));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_UNSTOPPABLE_PASSER  , "PAWNS.UNSTOPPABLE.PASSER" , PAWNS_STRUCTURE ,   0, 0,  0,   550, 700,  550 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS     , "PAWNS_PSTOPPERS"     , PAWNS_STRUCTURE         , 0, 50, 0.083, 0, 50, 0.462 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANDIDATE     , "PAWNS.CANDIDATE.RNK" , PAWNS_STRUCTURE ,   0,  5,  0.412,  0,  5,  0.302));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F  	, "KINGS.PASSERS.F"     , PAWNS_STRUCTURE ,   0,   0, 0, 0, 20, 1.340 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_FF  	, "KINGS.PASSERS.FF"    , PAWNS_STRUCTURE ,   0,   0, 0, 0, 20, 0.979 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F_OP   , "KINGS.PASSERS.F.OP"  , PAWNS_STRUCTURE ,   0,   0, 0, 0, 20, 1.877 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISLANTS       , "PAWNS.ISLANDS"       , PAWNS_STRUCTURE , -50,  50, -0.536, -50,  50, -0.507 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS         , "PAWNS.GARDS"         , PAWNS_STRUCTURE ,   0,  50,   8.411, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS_REM     , "PAWNS.GARDS.REM"     , PAWNS_STRUCTURE , -50,   0,   -4.707, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS        , "PAWNS.STORMS"        , PAWNS_STRUCTURE ,   0,  50,   1.982, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS_CLS    , "PAWNS.STORMS.CLS"    , PAWNS_STRUCTURE ,   0,  50,   2.924, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_OPENNED       , "PAWNS.OPENNED"       , PAWNS_STRUCTURE , -50,   0, -35.881, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OWN    , "PAWNS.SEMIOP.OWN"    , PAWNS_STRUCTURE , -50,   0, -26.449, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OP     , "PAWNS.SEMIOP.OP"     , PAWNS_STRUCTURE , -50,   0, -11.361, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_WEAK          , "PAWNS.WEAK"          , PAWNS_STRUCTURE , -50,   0,  -2.085,  -50,  0, -0.945));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE        		, "SPACE"         	    , PAWNS_STRUCTURE ,   0,  10, 0.613, 0, 10, 0.565));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_INFRONT_PASSER , "ROOK.INFRONT.PASSER" , PAWNS_STRUCTURE , -50,   0, -0.019, -50,  0, 0));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_BEHIND_PASSER  , "ROOK.BEHIND.PASSER"  , PAWNS_STRUCTURE ,   0,  50, 1.245, 0,  50, 3.475));
		
		/**
		 * PIECES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_PAWN			, "PST.PAWN"   		    , PIECES_ITERATION ,  0,  2,  0.651,  0,  2,  0.741));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KING			, "PST.KING"   		    , PIECES_ITERATION ,  0,  2,  1.008,  0,  2,  0.912));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KNIGHT		    , "PST.KNIGHTS" 		, PIECES_ITERATION ,  0,  2,  0.641,  0,  2,  0.633));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_BISHOP		    , "PST.BISHOPS"  		, PIECES_ITERATION ,  0,  2,  0.592,  0,  2,  0.450));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_ROOK			, "PST.ROOKS"		  	, PIECES_ITERATION ,  0,  5,  0.781,  0,  5,  0.649));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_QUEEN			, "PST.QUEENS"   		, PIECES_ITERATION ,  0,  2,  0.331,  0,  2,  0.966));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_BAD         , "BISHOPS.BAD"   	    , PIECES_ITERATION , -50, 0, -0.894, -50, 0, -0.985 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_OUTPOST     , "KNIGHT.OUTPOST"      , PIECES_ITERATION ,   0, 50,14.938,   0, 50, 0.526 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_OPENED        , "ROOKS.OPENED"        , PIECES_ITERATION ,  0, 50, 16.513,  0, 50,  0.909 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_SEMIOPENED    , "ROOKS.SEMIOPENED"    , PIECES_ITERATION ,  0, 50,  8.937,  0, 50,  2.092 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_KNIGHT      , "TROPISM.KNIGHT"      , PIECES_ITERATION ,  0, 10, 0.070, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_BISHOP      , "TROPISM.BISHOP"      , PIECES_ITERATION ,  0, 10, 0.348, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_ROOK        , "TROPISM.ROOK"        , PIECES_ITERATION ,  0, 10, 0.202, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_QUEEN       , "TROPISM.QUEEN"       , PIECES_ITERATION ,  0, 10, 0.148, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_7TH_2TH       , "ROOKS.7TH.2TH"       , PIECES_ITERATION ,  0, 50,  8.056,  0,  50,  7.939 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_QUEENS_7TH_2TH      , "QUEENS.7TH.2TH"      , PIECES_ITERATION ,  0, 50,  5.735,  0,  50,  9.276 ));
		
		
		/**
		 * MOVES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L1    	    , "KINGSAFETY.L1"   	, MOVES_ITERATION  , 0, 100, 30.900, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L2    	    , "KINGSAFETY.L2"   	, MOVES_ITERATION  , 0, 100, 12.867, 0, 0, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT 	, "MOBILITY.KNIGHT" 	, MOVES_ITERATION  ,  0,  2,  0.277,  0,  2,  0.587));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP 	, "MOBILITY.BISHOP" 	, MOVES_ITERATION  ,  0,  2,  0.238,  0,  2,  0.578));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK   	, "MOBILITY.ROOK"   	, MOVES_ITERATION  ,  0,  2,  0.220,  0,  2,  0.480));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN  	, "MOBILITY.QUEEN"  	, MOVES_ITERATION  ,  0,  2,  0.190,  0,  2,  0.908));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT_S   , "MOBILITY.KNIGHT.S"   , MOVES_ITERATION  ,  0,  2,  0.140,  0,  2,  0.348));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP_S   , "MOBILITY.BISHOP.S"   , MOVES_ITERATION  ,  0,  2,  0.113,  0,  2,  0.290));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK_S     , "MOBILITY.ROOK.S"     , MOVES_ITERATION  ,  0,  2,  0.118,  0,  2,  0.325));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN_S    , "MOBILITY.QUEEN.S"    , MOVES_ITERATION  ,  0,  2,  0.292,  0,  2,  0.911));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_H   	    , "ROOKS.PAIR.H"    	, MOVES_ITERATION  ,  0, 50, 1.124,  0, 50, 3.964 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_V   	    , "ROOKS.PAIR.V"    	, MOVES_ITERATION  ,  0, 50, 1.412,  0, 50, 3.710 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP  				, "TRAP"      			, MOVES_ITERATION  ,  -50,  0,  -0.274,  -50,  0,  -0.121 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BIGGER_PIECE 	, "PIN.BIGGER"       	, MOVES_ITERATION  ,  0,  100,  16.731,  0,  100,  55.964 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_EQUAL_PIECE  	, "PIN.EQ"           	, MOVES_ITERATION  ,  0,  100,  10.631,  0,  100,  7.708 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_LOWER_PIECE  	, "PIN.LOWER"        	, MOVES_ITERATION  ,  0,  100,  2.355,  0,  100,  9.281 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BIGGER_PIECE , "ATTACK.BIGGER"       , MOVES_ITERATION  ,  0,  100,  31.874,  0,  100,  52.656 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_EQUAL_PIECE  , "ATTACK.EQ"           , MOVES_ITERATION  ,  0,  100,  18.373,  0,  100,  21.258 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_LOWER_PIECE  , "ATTACK.LOWER"        , MOVES_ITERATION  ,  0,  100,  3.356,  0,  100,  21.805 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PIECES       , "HUNGED.PIECE"        , MOVES_ITERATION  ,  -100,  0, -0.043,  -100,  0, -0.449 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PAWNS       	, "HUNGED.PAWNS"       	, MOVES_ITERATION  ,  -100,  0, -0.523,  -100,  0, -0.098 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_ALL       	, "HUNGED.ALL"        	, MOVES_ITERATION  ,  -100,  0, -0.221,  -100,  0, -0.255 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PSTOPPERS_A   , "PAWNS_PSTOPPERS_A"   , MOVES_ITERATION         , 0, 50, 0, 0, 50, 0 ));
		
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
