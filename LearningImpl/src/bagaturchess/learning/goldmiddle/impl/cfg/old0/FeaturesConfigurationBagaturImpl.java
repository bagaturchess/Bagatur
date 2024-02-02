package bagaturchess.learning.goldmiddle.impl.cfg.old0;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureArray;
import bagaturchess.learning.impl.features.advanced.AdjustableFeaturePST;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;
import bagaturchess.learning.impl.utils.PSTConstants;


public class FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       , "MATERIAL.PAWN"       , GROUP1        , 0, 2000,  54, 0, 2000,  98 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     , "MATERIAL.KNIGHT"     , GROUP1        , 0, 2000,  352, 0, 2000,  324 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     , "MATERIAL.BISHOP"     , GROUP1        , 0, 2000,  401, 0, 2000,  324 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       , "MATERIAL.ROOK"       , GROUP1        , 0, 2000,  492, 0, 2000,  553 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      , "MATERIAL.QUEEN"      , GROUP1        , 0, 2000,  1241, 0, 2000,  1023 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_CASTLING   , "KINGSAFE.CASTLING"   , GROUP1        , 0, 50,  10.030, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_FIANCHETTO , "KINGSAFE.FIANCHETTO" , GROUP1        , 0, 100, 1, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_DOUBLE      , "BISHOPS.DOUBLE"	    , GROUP1        , 0, 100,  39.227, 0, 200,  40.227 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_DOUBLE      , "KNIGHTS.DOUBLE"	    , GROUP1        , 0, 100,  39.227, 0, 200,  40.227 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_DOUBLE      	, "ROOKS.DOUBLE"	    , GROUP1        , 0, 100,  39.227, 0, 200,  40.227 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_F_PAWN     , "KINGSAFE.F"          , GROUP1        , -50, 0, -4.056, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_G_PAWN     , "KINGSAFE.G"          , GROUP1        , -50, 0, -10.653, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGS_DISTANCE  	, "KINGS.DISTANCE"      , GROUP1        , -50, 50, 1, -50, 50,  1 ));
		/*add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_KINGS_DISTANCE  	    , "KINGS.DISTANCE"      , STANDARD        ,
				PSTConstants.createArray(8, 0), PSTConstants.createArray(8, 0), new double[] {0,   0,   0,   0,   0,   0,  0, 0},
				PSTConstants.createArray(8, -128), PSTConstants.createArray(8, 128), new double[] {0,   0,   0,   0,   0,   0,  0, 0}
		));*/
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_ROOKS      	, "ROOKS.5PAWNS"	    , GROUP1        , -50, 50, 1, -50, 50,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_KNIGHTS     	, "KNIGHTS.5PAWNS"	    , GROUP1        , -50, 50, 1, -50, 50,  1 ));
		
		
		/**
		 * PAWNS ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_DOUBLED       , "PAWNS.DOUBLED"       , GROUP2 , -50,  0, -0.142, -50,  0, -16.763 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISOLATED      , "PAWNS.ISOLATED"      , GROUP2 , -50,  0,-14.305, -50,  0, -14.237 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_BACKWARD      , "PAWNS.BACKWARD"      , GROUP2 , -50,  0, -2.985, -50,  0, -2.776 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SUPPORTED     , "PAWNS.SUPPORTED"     , GROUP2 ,   0, 50,  0.923,   0, 50,  3.935 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANNOTBS      , "PAWNS.CANNOTBS"      , GROUP2 , -50,  0, -0.175, -50,  0, -0.607 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED        , "PAWNS.PASSED"        , GROUP2 ,   0, 50,  9.276,   0, 50,  3.482 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_RNK    , "PAWNS.PASSED.RNK"    , GROUP2 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_UNSTOPPABLE_PASSER  , "PAWNS.UNSTOPPABLE.PASSER" , GROUP2 ,   0, 0,  0,   550, 550,  550 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANDIDATE     , "PAWNS.CANDIDATE.RNK" , GROUP2 ,  0,  5,  1,  0,  5,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F  	, "KINGS.PASSERS.F"     , GROUP2 ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_FF  	, "KINGS.PASSERS.FF"    , GROUP2 ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F_OP   , "KINGS.PASSERS.F.OP"  , GROUP2 ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISLANTS       , "PAWNS.ISLANDS"       , GROUP2 , -50, 0, -0.447, -50,  0, -0.84 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS         , "PAWNS.GARDS"         , GROUP2 ,   0,  50,   6.761, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS_REM     , "PAWNS.GARDS.REM"     , GROUP2 , -50,   0,     -4.584, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS        , "PAWNS.STORMS"        , GROUP2 ,   0,  50,   1.905, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS_CLS    , "PAWNS.STORMS.CLS"    , GROUP2 ,   0,  50,   1.453, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_OPENNED       , "PAWNS.OPENNED"       , GROUP2 , -50,   0, -46.105, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OWN    , "PAWNS.SEMIOP.OWN"    , GROUP2 , -50,   0, -25.944, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OP     , "PAWNS.SEMIOP.OP"     , GROUP2 , -50,   0,  -12.728, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_WEAK          , "PAWNS.WEAK"          , GROUP2 , -50,   0,  -1,  -50,  0, -1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE        		, "SPACE"         	    , GROUP2 ,   0,  10, 0.514, 0, 10, 0.757));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_INFRONT_PASSER , "ROOK.INFRONT.PASSER" , GROUP2 , -50,   0, -1, -50,  0, -1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_BEHIND_PASSER  , "ROOK.BEHIND.PASSER"  , GROUP2 ,   0,  50, 1, 0,  50, 1));
		
		
		/**
		 * PIECES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_PAWN			, "PST.PAWN"   		    , GROUP3 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KING			, "PST.KING"   		    , GROUP3 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KNIGHT		    , "PST.KNIGHTS" 		, GROUP3 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_BISHOP		    , "PST.BISHOPS"  		, GROUP3 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_ROOK			, "PST.ROOKS"		  	, GROUP3 ,  0,  5,  1,  0,  5,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_QUEEN			, "PST.QUEENS"   		, GROUP3 ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_BAD         , "BISHOPS.BAD"   	    , GROUP3 , -50, 0, -1, -50, 0, -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_OUTPOST     , "KNIGHT.OUTPOST"      , GROUP3 ,   0, 50, 18.943,   0, 50, 1.002 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_OPENED        , "ROOKS.OPENED"        , GROUP3 ,  0, 50, 46.048,  0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_SEMIOPENED    , "ROOKS.SEMIOPENED"    , GROUP3 ,  0, 50, 20.527,  0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_KNIGHT      , "TROPISM.KNIGHT"      , GROUP3 ,  0, 10, 0.086, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_BISHOP      , "TROPISM.BISHOP"      , GROUP3 ,  0, 10, 0.476, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_ROOK        , "TROPISM.ROOK"        , GROUP3 ,  0, 10, 0.156, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_QUEEN       , "TROPISM.QUEEN"       , GROUP3 ,  0, 10, 0.187, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_QUEENS_7TH_2TH      , "QUEENS.7TH.2TH"      , GROUP3 ,  0, 50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_7TH_2TH       , "ROOKS.7TH.2TH"       , GROUP3 ,  0, 50,  10,  0,  50,  10 ));
		
		
		/**
		 * MOVES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L1    	    , "KINGSAFETY.L1"   	, GROUP4  , 0, 100, 10, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L2    	    , "KINGSAFETY.L2"   	, GROUP4  , 0, 100, 10, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT 	, "MOBILITY.KNIGHT" 	, GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP 	, "MOBILITY.BISHOP" 	, GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK   	, "MOBILITY.ROOK"   	, GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN  	, "MOBILITY.QUEEN"  	, GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT_S   , "MOBILITY.KNIGHT.S"   , GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP_S   , "MOBILITY.BISHOP.S"   , GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK_S     , "MOBILITY.ROOK.S"     , GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN_S    , "MOBILITY.QUEEN.S"    , GROUP4  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_H   	    , "ROOKS.PAIR.H"    	, GROUP4  ,  0, 50, 10,  0, 50, 10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_V   	    , "ROOKS.PAIR.V"    	, GROUP4  ,  0, 50, 10,  0, 50, 10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BK              , "PIN.BK"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BQ              , "PIN.BQ"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BR              , "PIN.BR"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BN              , "PIN.BN"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RK              , "PIN.RK"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RQ              , "PIN.RQ"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RB              , "PIN.RB"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RN              , "PIN.RN"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QK              , "PIN.QK"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QQ              , "PIN.QQ"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QN              , "PIN.QN"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QR              , "PIN.QR"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QB              , "PIN.QB"              , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BIGGER_PIECE , "ATTACK.BIGGER"       , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_EQUAL_PIECE  , "ATTACK.EQ"           , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_LOWER_PIECE  , "ATTACK.LOWER"        , GROUP4  ,  0,  50,  10,  0,  50,  10 ));
		
		/*add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BN           , "ATTACK.BN"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BR           , "ATTACK.BR"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_NB           , "ATTACK.NB"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_NR           , "ATTACK.NR"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_NQ           , "ATTACK.NQ"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_RB           , "ATTACK.RB"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_RN           , "ATTACK.RN"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_QB           , "ATTACK.QB"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_QN           , "ATTACK.QN"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_QR           , "ATTACK.QR"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));*/
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP  				, "TRAP"      			, GROUP4  ,  -50,  0,  -10,  -50,  0,  -10 ));
		
		/*add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_KNIGHT         , "TRAP.KNIGHT"         , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_BISHOP         , "TRAP.BISHOP"         , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_ROOK           , "TRAP.ROOK"           , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_QUEEN          , "TRAP.QUEEN"          , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));*/
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PIECES       , "HUNGED.PIECE"        , GROUP4  ,  -50,  0, -20,  -50,  0, -20 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PAWNS       	, "HUNGED.PAWS"        	, GROUP4  ,  -50,  0, -20,  -50,  0, -20 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_ALL       	, "HUNGED.ALL"        	, GROUP4  ,  -50,  0, -20,  -50,  0, -20 ));
		
		
		/**
		 * FIELDS STATES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_EQ , "CONTROL_EQ" , GROUP5,
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128), PSTConstants.createArray(64, 0),
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128),PSTConstants.createArray(64, 0)
		));
		add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_MORE , "CONTROL_MORE" , GROUP5,
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128), PSTConstants.createArray(64, 0),
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128),PSTConstants.createArray(64, 0)
		));
		
		
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
