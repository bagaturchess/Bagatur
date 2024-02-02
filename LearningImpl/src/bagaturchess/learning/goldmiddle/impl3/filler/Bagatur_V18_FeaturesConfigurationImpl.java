package bagaturchess.learning.goldmiddle.impl3.filler;


import java.util.Set;
import java.util.TreeSet;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.impl.features.advanced.AdjustableFeatureSingle;


public class Bagatur_V18_FeaturesConfigurationImpl implements IFeaturesConfiguration, IFeatureComplexity, Bagatur_V18_FeaturesConstants {
	
	
	public IFeature[] getDefinedFeatures() {
		
		
		Set<IFeature> new_featuresSet = new TreeSet<IFeature>();
		
		
		//Material
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_PAWN       				, "MATERIAL.PAWN"       				, GROUP1         , 0, 2000,  80, 0, 2000,  65 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_KNIGHT     				, "MATERIAL.KNIGHT"     				, GROUP1         , 0, 2000,  375, 0, 2000,  311 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_BISHOP     				, "MATERIAL.BISHOP"     				, GROUP1         , 0, 2000,  360, 0, 2000,  343 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_ROOK       				, "MATERIAL.ROOK"       				, GROUP1         , 0, 2000,  455, 0, 2000,  582 ));
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_QUEEN      				, "MATERIAL.QUEEN"      				, GROUP1         , 0, 2000,  1072, 0, 2000,  1027 ));
		
		
		//Imbalance
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MATERIAL_IMBALANCE					, "MATERIAL.IMBALANCE"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		//PST
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PIECE_SQUARE_TABLE					, "PIECE.SQUARE.TABLE"					, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		//Pawns
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PAWNS								, "PAWNS"								, GROUP1         , 0, 100,  1, 0, 100,  1 ));

		//Pieces - mobility, outposts and others
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_MOBILITY_OUTPOST_OTHERS				, "MOBILITY.OUTPOST.OTHERS"				, GROUP1         , 0, 100,  1, 0, 100,  1 ));

		//King - safety
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_KING_SAFETY							, "KING.SAFETY"							, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		//Threats
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_THREATS								, "THREATS"								, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		//Passed pawns
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_PASSED_PAWNS						, "PASSED.PAWNS"						, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
		//Space
		add(new_featuresSet, new AdjustableFeatureSingle(FEATURE_ID_SPACE 								, "SPACE"								, GROUP1         , 0, 100,  1, 0, 100,  1 ));
		
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
