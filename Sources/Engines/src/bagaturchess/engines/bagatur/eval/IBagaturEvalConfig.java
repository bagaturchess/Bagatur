package bagaturchess.engines.bagatur.eval;

import bagaturchess.search.api.IEvalConfig;




public interface IBagaturEvalConfig extends IEvalConfig {
	
	public double get_WEIGHT_KINGSAFETY_O();
	public double get_WEIGHT_KINGSAFETY_E();
	
	public double get_WEIGHT_PAWNS_PASSED_O();
	public double get_WEIGHT_PAWNS_PASSED_E();
	
	public double get_WEIGHT_PAWNS_PASSED_KING_O();
	public double get_WEIGHT_PAWNS_PASSED_KING_E();

	public double get_WEIGHT_PAWNS_STANDARD_O();
	public double get_WEIGHT_PAWNS_STANDARD_E();
	
	public double get_WEIGHT_SPACE_O();
	public double get_WEIGHT_SPACE_E();
	
	public double get_WEIGHT_PST_O();
	public double get_WEIGHT_PST_E();

	public double get_WEIGHT_MOBILITY_O();
	public double get_WEIGHT_MOBILITY_E();

	public double get_WEIGHT_MOBILITY_S_O();
	public double get_WEIGHT_MOBILITY_S_E();

	public double get_WEIGHT_HUNGED_O();
	public double get_WEIGHT_HUNGED_E();

	public double get_WEIGHT_TRAPPED_O();
	public double get_WEIGHT_TRAPPED_E();
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_O();
	public double get_WEIGHT_PAWNS_PSTOPPERS_E();

	public double get_WEIGHT_PAWNS_PSTOPPERS_A_O();
	public double get_WEIGHT_PAWNS_PSTOPPERS_A_E();
}
