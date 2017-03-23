package bagaturchess.learning.goldmiddle.impl.cfg.old3;


import java.util.Set;
import java.util.TreeSet;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.baseimpl.FeatureArray;
import bagaturchess.learning.impl.features.baseimpl.FeaturePST;
import bagaturchess.learning.impl.features.baseimpl.FeatureSingle;


public class FeaturesConfigurationBagaturImpl implements IFeaturesConfiguration, IFeatureComplexity, IFeaturesConstants {
	
	
	private static final int START = 0;
	
		
	public IFeature[] getDefinedFeatures() {
		
		//84.0771334032192%
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_MATERIAL_PAWN       , "MATERIAL.PAWN"      , STANDARD       , 0, 2000,  54, 0, 2000,  98 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     , "MATERIAL.KNIGHT"    , STANDARD       , 0, 2000,  352, 0, 2000,  324 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_MATERIAL_BISHOP     , "MATERIAL.BISHOP"    , STANDARD       , 0, 2000,  401, 0, 2000,  324 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_MATERIAL_ROOK       , "MATERIAL.ROOK"      , STANDARD       , 0, 2000,  492, 0, 2000,  553 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_MATERIAL_QUEEN      , "MATERIAL.QUEEN"     , STANDARD       , 0, 2000,  1241, 0, 2000,  1023 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_KINGSAFE_CASTLING   , "KINGSAFE.CASTLING"  , STANDARD       , 0, 50,  10.030, 0, 0,  0 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_KINGSAFE_FIANCHETTO , "KINGSAFE.FIANCHETTO", STANDARD       , 0, 100, 1, 0, 0, 0 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_BISHOPS_DOUBLE    , "BISHOPS.DOUBLE"		 , STANDARD       , 0, 100,  39.227, 0, 200,  40.227 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_KINGSAFE_F_PAWN   , "KINGSAFE.F"         	 , STANDARD         , -50, 0, -4.056, 0, 0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_KINGSAFE_G_PAWN   , "KINGSAFE.G"           , STANDARD         , -50, 0, -10.653, 0, 0,  0 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_DOUBLED     , "PAWNS.DOUBLED"      , PAWNS_STRUCTURE  , -50,  0, -0.142, -50,  0, -16.763 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_ISOLATED    , "PAWNS.ISOLATED"     , PAWNS_STRUCTURE  , -50,  0,-14.305, -50,  0, -14.237 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_BACKWARD    , "PAWNS.BACKWARD"     , PAWNS_STRUCTURE  , -50,  0, -2.985, -50,  0, -2.776 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_SUPPORTED   , "PAWNS.SUPPORTED"    , PAWNS_STRUCTURE  ,   0, 50,  0.923,   0, 50,  3.935 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_CANNOTBS    , "PAWNS.CANNOTBS"     , PAWNS_STRUCTURE  , -50,  0, -0.175, -50,  0, -0.607 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_PASSED      , "PAWNS.PASSED"       , PAWNS_STRUCTURE  ,   0, 50,  9.276,   0, 50,  3.482 ));
		add(new_featuresSet, new FeatureArray(FEATURE_ID_PAWNS_PASSED_RNK  , "PAWNS.PASSED.RNK"    , PAWNS_STRUCTURE,
				new double[] {0, 0, 0, 0, 0, 0, 0, 0}, new double[] {100, 100, 100, 100, 200, 200, 200, 200}, new double[] {0,   13,   14,   31,   56,   100,   100,   0,},
				new double[] {0, 0, 0, 0, 0, 0, 0, 0}, new double[] {100, 100, 100, 100, 200, 200, 200, 200}, new double[] {0,   0,   0,   13,   44,   85,   100,   0, }
		));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_UNSTOPPABLE_PASSER, "PAWNS.UNSTOPPABLE.PASSER", PAWNS_STRUCTURE  ,   0, 2,  1,   0, 2,  1 ));
		add(new_featuresSet, new FeatureArray(FEATURE_ID_PAWNS_CANDIDATE  , "PAWNS.CANDIDATE.RNK"    , PAWNS_STRUCTURE,
				new double[] {0, 0, 0, 0, 0, 0}, new double[] {100, 100, 100, 100, 200, 200}, new double[] {0,   0,   2,   6,   8,   38},
				new double[] {0, 0, 0, 0, 0, 0}, new double[] {100, 100, 100, 100, 200, 200}, new double[] {0,   1,   3,   6,   12,   26}
		));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_ISLANTS     , "PAWNS.ISLANDS"      , PAWNS_STRUCTURE  ,  -50, 0, -0.447, -50,  0, -0.84 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_GARDS       , "PAWNS.GARDS"        , PAWNS_STRUCTURE  , -50, 50,   6.761, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_GARDS_REM   , "PAWNS.GARDS.REM"    , PAWNS_STRUCTURE  , -50, 0,     -4.584, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_STORMS      , "PAWNS.STORMS"       , PAWNS_STRUCTURE  ,   0, 50,   1.905, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_STORMS_CLS  , "PAWNS.STORMS.CLS"   , PAWNS_STRUCTURE  ,   0, 50,   1.453, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_OPENNED     , "PAWNS.OPENNED"      , PAWNS_STRUCTURE  , -50,  0, -46.105, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OWN  , "PAWNS.SEMIOP.OWN"   , PAWNS_STRUCTURE  , -50, 50, -25.944, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_SEMIOP_OP   , "PAWNS.SEMIOP.OP"    , PAWNS_STRUCTURE  , -50, 50,  -12.728, 0,  0,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_PAWNS_WEAK        , "PAWNS.WEAK"         , PAWNS_STRUCTURE  , -50, 50, 0.029, -50, 50, 0.724));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_SPACE        		, "SPACE"         	   , PAWNS_STRUCTURE  ,  0,  10, 0.514, 0, 10, 0.757));
		
		
		//PIECES ITERATION
		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_PAWN					, "PST.PAWN"   	, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.PAWN_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.PAWN_E));

		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_KING				, "PST.KING"   	, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.KING_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.KING_E));
		
		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_KNIGHT				, "PST.KNIGHTS"   	, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.KNIGHT_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.KNIGHT_E));
		
		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_BISHOP				, "PST.BISHOPS"   	, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.BISHOP_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.BISHOP_E));
		
		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_ROOK					, "PST.ROOKS"		, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.ROOK_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.ROOK_E));
		
		add(new_featuresSet, new FeaturePST(FEATURE_ID_PST_QUEEN				, "PST.QUEENS"   	, PIECES_ITERATION,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.QUEEN_O,
				PSTConstants.createArray(64, -128), PSTConstants.createArray(64, 128), PSTConstants.QUEEN_E));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_BISHOPS_BAD       , "BISHOPS.BAD"   	   , PIECES_ITERATION         , -50, 50, 12.900, -50, 50, 0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_KNIGHTS_OUTPOST   , "KNIGHT.OUTPOST"     , PIECES_ITERATION         ,   0, 50, 18.943,   0, 50, 1.002 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_ROOKS_OPENED      , "ROOKS.OPENED"       , PIECES_ITERATION          , -50, 50, 46.048, -50, 50,  0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_ROOKS_SEMIOPENED  , "ROOKS.SEMIOPENED"   , PIECES_ITERATION          , -50, 50, 20.527, -50, 50,  0 ));
		
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_TROPISM_KNIGHT    , "TROPISM.KNIGHT"     , PIECES_ITERATION          , 0, 10, 0.086, 0, 0, 0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_TROPISM_BISHOP    , "TROPISM.BISHOP"     , PIECES_ITERATION          , 0, 10, 0.476, 0, 0, 0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_TROPISM_ROOK      , "TROPISM.ROOK"       , PIECES_ITERATION          , 0, 10, 0.156, 0, 0, 0 ));
		add(new_featuresSet, new FeatureSingle(FEATURE_ID_TROPISM_QUEEN     , "TROPISM.QUEEN"      , PIECES_ITERATION          , 0, 10, 0.187, 0, 0, 0 ));
		
		
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
