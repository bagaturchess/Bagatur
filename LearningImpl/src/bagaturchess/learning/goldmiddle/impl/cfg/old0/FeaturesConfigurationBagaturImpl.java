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
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       , "MATERIAL.PAWN"       , STANDARD        , 0, 2000,  54, 0, 2000,  98 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     , "MATERIAL.KNIGHT"     , STANDARD        , 0, 2000,  352, 0, 2000,  324 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     , "MATERIAL.BISHOP"     , STANDARD        , 0, 2000,  401, 0, 2000,  324 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       , "MATERIAL.ROOK"       , STANDARD        , 0, 2000,  492, 0, 2000,  553 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      , "MATERIAL.QUEEN"      , STANDARD        , 0, 2000,  1241, 0, 2000,  1023 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_CASTLING   , "KINGSAFE.CASTLING"   , STANDARD        , 0, 50,  10.030, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_FIANCHETTO , "KINGSAFE.FIANCHETTO" , STANDARD        , 0, 100, 1, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_DOUBLE      , "BISHOPS.DOUBLE"	    , STANDARD        , 0, 100,  39.227, 0, 200,  40.227 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_DOUBLE      , "KNIGHTS.DOUBLE"	    , STANDARD        , 0, 100,  39.227, 0, 200,  40.227 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_DOUBLE      	, "ROOKS.DOUBLE"	    , STANDARD        , 0, 100,  39.227, 0, 200,  40.227 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_F_PAWN     , "KINGSAFE.F"          , STANDARD        , -50, 0, -4.056, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_G_PAWN     , "KINGSAFE.G"          , STANDARD        , -50, 0, -10.653, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGS_DISTANCE  	, "KINGS.DISTANCE"      , STANDARD        , -50, 50, 1, -50, 50,  1 ));
		/*add(new_featuresSet, new AdjustableFeatureArray(FEATURE_ID_KINGS_DISTANCE  	    , "KINGS.DISTANCE"      , STANDARD        ,
				PSTConstants.createArray(8, 0), PSTConstants.createArray(8, 0), new double[] {0,   0,   0,   0,   0,   0,  0, 0},
				PSTConstants.createArray(8, -128), PSTConstants.createArray(8, 128), new double[] {0,   0,   0,   0,   0,   0,  0, 0}
		));*/
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_ROOKS      	, "ROOKS.5PAWNS"	    , STANDARD        , -50, 50, 1, -50, 50,  1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_5PAWNS_KNIGHTS     	, "KNIGHTS.5PAWNS"	    , STANDARD        , -50, 50, 1, -50, 50,  1 ));
		
		
		/**
		 * PAWNS ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_DOUBLED       , "PAWNS.DOUBLED"       , PAWNS_STRUCTURE , -50,  0, -0.142, -50,  0, -16.763 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISOLATED      , "PAWNS.ISOLATED"      , PAWNS_STRUCTURE , -50,  0,-14.305, -50,  0, -14.237 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_BACKWARD      , "PAWNS.BACKWARD"      , PAWNS_STRUCTURE , -50,  0, -2.985, -50,  0, -2.776 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SUPPORTED     , "PAWNS.SUPPORTED"     , PAWNS_STRUCTURE ,   0, 50,  0.923,   0, 50,  3.935 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANNOTBS      , "PAWNS.CANNOTBS"      , PAWNS_STRUCTURE , -50,  0, -0.175, -50,  0, -0.607 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED        , "PAWNS.PASSED"        , PAWNS_STRUCTURE ,   0, 50,  9.276,   0, 50,  3.482 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_RNK    , "PAWNS.PASSED.RNK"    , PAWNS_STRUCTURE ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_UNSTOPPABLE_PASSER  , "PAWNS.UNSTOPPABLE.PASSER" , PAWNS_STRUCTURE ,   0, 0,  0,   550, 550,  550 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANDIDATE     , "PAWNS.CANDIDATE.RNK" , PAWNS_STRUCTURE ,  0,  5,  1,  0,  5,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F  	, "KINGS.PASSERS.F"     , PAWNS_STRUCTURE ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_FF  	, "KINGS.PASSERS.FF"    , PAWNS_STRUCTURE ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_PASSERS_F_OP   , "KINGS.PASSERS.F.OP"  , PAWNS_STRUCTURE ,   0,   0, 0, 0, 10, 1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISLANTS       , "PAWNS.ISLANDS"       , PAWNS_STRUCTURE , -50, 0, -0.447, -50,  0, -0.84 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS         , "PAWNS.GARDS"         , PAWNS_STRUCTURE ,   0,  50,   6.761, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_GARDS_REM     , "PAWNS.GARDS.REM"     , PAWNS_STRUCTURE , -50,   0,     -4.584, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS        , "PAWNS.STORMS"        , PAWNS_STRUCTURE ,   0,  50,   1.905, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_STORMS_CLS    , "PAWNS.STORMS.CLS"    , PAWNS_STRUCTURE ,   0,  50,   1.453, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_OPENNED       , "PAWNS.OPENNED"       , PAWNS_STRUCTURE , -50,   0, -46.105, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OWN    , "PAWNS.SEMIOP.OWN"    , PAWNS_STRUCTURE , -50,   0, -25.944, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OP     , "PAWNS.SEMIOP.OP"     , PAWNS_STRUCTURE , -50,   0,  -12.728, 0,  0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_WEAK          , "PAWNS.WEAK"          , PAWNS_STRUCTURE , -50,   0,  -1,  -50,  0, -1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE        		, "SPACE"         	    , PAWNS_STRUCTURE ,   0,  10, 0.514, 0, 10, 0.757));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_INFRONT_PASSER , "ROOK.INFRONT.PASSER" , PAWNS_STRUCTURE , -50,   0, -1, -50,  0, -1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOK_BEHIND_PASSER  , "ROOK.BEHIND.PASSER"  , PAWNS_STRUCTURE ,   0,  50, 1, 0,  50, 1));
		
		
		/**
		 * PIECES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_PAWN			, "PST.PAWN"   		    , PIECES_ITERATION ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KING			, "PST.KING"   		    , PIECES_ITERATION ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_KNIGHT		    , "PST.KNIGHTS" 		, PIECES_ITERATION ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_BISHOP		    , "PST.BISHOPS"  		, PIECES_ITERATION ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_ROOK			, "PST.ROOKS"		  	, PIECES_ITERATION ,  0,  5,  1,  0,  5,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST_QUEEN			, "PST.QUEENS"   		, PIECES_ITERATION ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOPS_BAD         , "BISHOPS.BAD"   	    , PIECES_ITERATION , -50, 0, -1, -50, 0, -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHTS_OUTPOST     , "KNIGHT.OUTPOST"      , PIECES_ITERATION ,   0, 50, 18.943,   0, 50, 1.002 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_OPENED        , "ROOKS.OPENED"        , PIECES_ITERATION ,  0, 50, 46.048,  0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_SEMIOPENED    , "ROOKS.SEMIOPENED"    , PIECES_ITERATION ,  0, 50, 20.527,  0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_KNIGHT      , "TROPISM.KNIGHT"      , PIECES_ITERATION ,  0, 10, 0.086, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_BISHOP      , "TROPISM.BISHOP"      , PIECES_ITERATION ,  0, 10, 0.476, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_ROOK        , "TROPISM.ROOK"        , PIECES_ITERATION ,  0, 10, 0.156, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TROPISM_QUEEN       , "TROPISM.QUEEN"       , PIECES_ITERATION ,  0, 10, 0.187, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_QUEENS_7TH_2TH      , "QUEENS.7TH.2TH"      , PIECES_ITERATION ,  0, 50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_7TH_2TH       , "ROOKS.7TH.2TH"       , PIECES_ITERATION ,  0, 50,  10,  0,  50,  10 ));
		
		
		/**
		 * MOVES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L1    	    , "KINGSAFETY.L1"   	, MOVES_ITERATION  , 0, 100, 10, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KINGSAFE_L2    	    , "KINGSAFETY.L2"   	, MOVES_ITERATION  , 0, 100, 10, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT 	, "MOBILITY.KNIGHT" 	, MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP 	, "MOBILITY.BISHOP" 	, MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK   	, "MOBILITY.ROOK"   	, MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN  	, "MOBILITY.QUEEN"  	, MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT_S   , "MOBILITY.KNIGHT.S"   , MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP_S   , "MOBILITY.BISHOP.S"   , MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK_S     , "MOBILITY.ROOK.S"     , MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN_S    , "MOBILITY.QUEEN.S"    , MOVES_ITERATION  ,  0,  2,  1,  0,  2,  1));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_H   	    , "ROOKS.PAIR.H"    	, MOVES_ITERATION  ,  0, 50, 10,  0, 50, 10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ROOKS_PAIR_V   	    , "ROOKS.PAIR.V"    	, MOVES_ITERATION  ,  0, 50, 10,  0, 50, 10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BK              , "PIN.BK"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BQ              , "PIN.BQ"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BR              , "PIN.BR"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_BN              , "PIN.BN"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RK              , "PIN.RK"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RQ              , "PIN.RQ"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RB              , "PIN.RB"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_RN              , "PIN.RN"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QK              , "PIN.QK"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QQ              , "PIN.QQ"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QN              , "PIN.QN"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QR              , "PIN.QR"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIN_QB              , "PIN.QB"              , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_BIGGER_PIECE , "ATTACK.BIGGER"       , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_EQUAL_PIECE  , "ATTACK.EQ"           , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_ATTACK_LOWER_PIECE  , "ATTACK.LOWER"        , MOVES_ITERATION  ,  0,  50,  10,  0,  50,  10 ));
		
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
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP  				, "TRAP"      			, MOVES_ITERATION  ,  -50,  0,  -10,  -50,  0,  -10 ));
		
		/*add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_KNIGHT         , "TRAP.KNIGHT"         , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_BISHOP         , "TRAP.BISHOP"         , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_ROOK           , "TRAP.ROOK"           , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAP_QUEEN          , "TRAP.QUEEN"          , MOVES_ITERATION  ,  -50,  0,  -1,  -50,  0,  -1 ));*/
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PIECES       , "HUNGED.PIECE"        , MOVES_ITERATION  ,  -50,  0, -20,  -50,  0, -20 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_PAWNS       	, "HUNGED.PAWS"        	, MOVES_ITERATION  ,  -50,  0, -20,  -50,  0, -20 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED_ALL       	, "HUNGED.ALL"        	, MOVES_ITERATION  ,  -50,  0, -20,  -50,  0, -20 ));
		
		
		/**
		 * FIELDS STATES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_EQ , "CONTROL_EQ" , FIELDS_STATES_ITERATION,
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128), PSTConstants.createArray(64, 0),
				PSTConstants.createArray(64, 0), PSTConstants.createArray(64, 128),PSTConstants.createArray(64, 0)
		));
		add(new_featuresSet, new AdjustableFeaturePST(FEATURE_ID_PST_CONTROL_MORE , "CONTROL_MORE" , FIELDS_STATES_ITERATION,
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
