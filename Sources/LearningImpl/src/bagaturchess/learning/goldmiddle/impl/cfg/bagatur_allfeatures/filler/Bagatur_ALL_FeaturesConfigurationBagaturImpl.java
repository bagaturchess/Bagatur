package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_ALL_FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_ALL_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		/**
		 * STANDARD ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       		, "MATERIAL.PAWN"       		, STANDARD         , 0, 2000,  80, 0, 2000,  65 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     		, "MATERIAL.KNIGHT"     		, STANDARD         , 0, 2000,  375, 0, 2000,  311 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     		, "MATERIAL.BISHOP"     		, STANDARD         , 0, 2000,  360, 0, 2000,  343 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       		, "MATERIAL.ROOK"       		, STANDARD         , 0, 2000,  455, 0, 2000,  582 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      		, "MATERIAL.QUEEN"      		, STANDARD         , 0, 2000,  1072, 0, 2000,  1027 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_DOUBLE_BISHOP  	, "MATERIAL.DOUBLE.BISHOPS"		, STANDARD         , 0, 100,  43, 0, 200,  43 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PST  						, "PST"							, STANDARD         , 0, 100,  0, 0, 100,  0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_TEMPO				, "STANDARD.TEMPO"   			, STANDARD         , 0, 50,  0, 0, 50,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_CASTLING			, "STANDARD.CASTLING"   		, STANDARD         , 0, 50,  22, 0, 0,  0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_FIANCHETTO 		, "STANDARD.FIANCHETTO" 		, STANDARD         , 0, 100, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_TRAP_BISHOP 		, "STANDARD.TRAP.BISHOP" 		, STANDARD         , -200, 0, 0, -200, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_BLOCKED_PAWN 		, "STANDARD.BLOCKED.PAWN" 		, STANDARD         , -100, 0, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_STANDARD_KINGS_OPPOSITION 	, "STANDARD.KINGS.OPPOSITION" 	, STANDARD         , 0, 0, 0, 0, 100, 0 ));
		
		
		/**
		 * PAWNS ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_KING_GUARDS 			, "PAWNS.KING.GUARDS"   		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_DOUBLED  	    		, "PAWNS.DOUBLED"      			, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ISOLATED  	    	, "PAWNS.ISOLATED"      		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_BACKWARD  	    	, "PAWNS.BACKWARD"      		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_SUPPORTED  	    	, "PAWNS.SUPPORTED"      		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_CANDIDATE  	    	, "PAWNS.CANDIDATE"      		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_SUPPORTED  	, "PAWNS.PASSED.SUPPORTED"      , PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED			  	, "PAWNS.PASSED"      			, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_KING_F			  	, "PAWNS.KING.F"      			, PAWNS_STRUCTURE  , 0, 0, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_KING_FF			  	, "PAWNS.KING.FF"      			, PAWNS_STRUCTURE  , 0, 0, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_KING_OP_F			  	, "PAWNS.KING.OP.F"      		, PAWNS_STRUCTURE  , 0, 0, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PASSED_UNSTOPPABLE 			, "PASSED.UNSTOPPABLE"   		, PAWNS_STRUCTURE  , 0, 700, 0, 0, 700, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_PASSED_STOPPERS		, "PAWNS.PASSED.STOPPERS"      	, PAWNS_STRUCTURE  , 0, 0, 0, 0, 100, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOK_OPENED 			, "PAWNS.ROOK.OPENED"   		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOK_SEMIOPENED 		, "PAWNS.ROOK.SEMIOPENED"   	, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_ROOK_7TH2TH 			, "PAWNS.ROOK.7TH2TH"   		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_QUEEN_7TH2TH 			, "PAWNS.QUEEN.7TH2TH"   		, PAWNS_STRUCTURE  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS_KING_OPENED 			, "PAWNS.KING.OPENED"   		, PAWNS_STRUCTURE  , -100, 100, 0, -100, 100, 0 ));
		
		
		/**
		 * PIECES ITERATION
		 */
		//N/A
		
		
		/**
		 * MOVES ITERATION
		 */
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT 			, "MOBILITY.KNIGHT"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP 			, "MOBILITY.BISHOP"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK 				, "MOBILITY.ROOK"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN 				, "MOBILITY.QUEEN"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KNIGHT_OUTPOST 				, "KNIGHT.OUTPOST"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOP_OUTPOST 				, "BISHOP.OUTPOST"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_BISHOP_BAD 					, "BISHOP.BAD"   				, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_SAFETY 				, "KING.SAFETY"   				, MOVES_ITERATION  , 0, 100, 0, 0, 0, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE 						, "SPACE"   					, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_HUNGED 						, "HUNGED"   					, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_KNIGHT_S 			, "MOBILITY.KNIGHT.S"   		, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_BISHOP_S			, "MOBILITY.BISHOP.S"			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_ROOK_S				, "MOBILITY.ROOK.S"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_QUEEN_S			, "MOBILITY.QUEEN.S"   			, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_TRAPED 						, "TRAPED"   					, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PASSERS_FRONT_ATTACKS		, "PASSERS.FRONT.ATTACKS"		, MOVES_ITERATION  , 0, 100, 0, 0, 100, 0 ));
		
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
