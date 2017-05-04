package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval;


public interface FeatureWeights {
	
	/**
	 * The rest of the weights are inside the config class IBagaturEvalConfig.
	 * It is located in the Engines project.
	 */
	
	public static final double WEIGHT_MATERIAL_O 		= 1.167;
	public static final double WEIGHT_MATERIAL_E 		= 0.799;
	
	public static final double WEIGHT_STANDARD_O 		= 0.183;
	public static final double WEIGHT_STANDARD_E 		= 0;
	
	public static final double WEIGHT_PAWNS_ROOKQUEEN_O	= 0.631;
	public static final double WEIGHT_PAWNS_ROOKQUEEN_E	= 2.013;
}
