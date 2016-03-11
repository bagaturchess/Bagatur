package bagaturchess.engines.bagatur.cfg.eval;

import bagaturchess.engines.bagatur.eval.IBagaturEvalConfig;
import bagaturchess.search.api.IEvalConfig;


public class BagaturEvalConfigImpl implements IEvalConfig, IBagaturEvalConfig {

	public boolean useEvalCache() {
		return true;
	}
	
	public boolean useLazyEval() {
		return true;
	}
	
	public String getEvaluatorFactoryClassName() {
		return "bagaturchess.engines.bagatur.eval.BagaturEvaluatorFactory";
	}
	
	public String getPawnsCacheFactoryClassName() {
		return "bagaturchess.engines.bagatur.eval.BagaturPawnsEvalFactory";
	}


	@Override
	public double get_WEIGHT_SPACE_E() {
		return 0.15;
	}


	@Override
	public double get_WEIGHT_TRAPPED_E() {
		return 0.83;
	}

	@Override
	public double get_WEIGHT_PAWNS_PSTOPPERS_A_E() {
		return 1.12;
	}
	
	//******
	
	public double get_WEIGHT_KINGSAFETY_O() {
		return 3;
	}
	
	public double get_WEIGHT_KINGSAFETY_E() {
		return 0;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_O() {
		return 1;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_E() {
		return 1.5;
	}
	
	public double get_WEIGHT_SPACE_O() {
		return 0.3;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_KING_E() {
		return 0.3;//0.25;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_KING_O() {
		return 0;
	}
	
	public double get_WEIGHT_PST_O() {
		return 1;
	}

	public double get_WEIGHT_PST_E() {
		return 1;
	}

	public double get_WEIGHT_MOBILITY_O() {
		return 1;
	}
	
	public double get_WEIGHT_MOBILITY_E() {
		return 1;//1
	}
	
	public double get_WEIGHT_MOBILITY_S_O() {
		return 1;
	}
	
	public double get_WEIGHT_MOBILITY_S_E() {
		return 1;//1
	}

	public double get_WEIGHT_PAWNS_STANDARD_O() {
		return 1;
	}
	
	public double get_WEIGHT_PAWNS_STANDARD_E() {
		return 1; // 2
	}
	
	public double get_WEIGHT_HUNGED_O() {
		return 1;
	}

	public double get_WEIGHT_HUNGED_E() {
		return 1;
	}
	
	public double get_WEIGHT_TRAPPED_O() {
		return 1;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_O() {
		return 0;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_E() {
		return 0.75;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_A_O() {
		return 0;
	}

	public double get_WEIGHT_MATERIAL_PAWNS_O() {
		return 1;
	}
	
	public double get_WEIGHT_MATERIAL_PAWNS_E() {
		return 1;
	}
}
