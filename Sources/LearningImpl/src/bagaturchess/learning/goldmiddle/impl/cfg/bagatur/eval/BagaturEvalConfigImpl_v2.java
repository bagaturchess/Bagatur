package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval;


import bagaturchess.search.api.IEvalConfig;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Double;


public class BagaturEvalConfigImpl_v2 implements IEvalConfig, IBagaturEvalConfig, IUCIOptionsProvider {
	
	
	private double WEIGHT_KINGSAFETY_O 			= 3.379;
	private double WEIGHT_KINGSAFETY_E 			= 0;
	
	private double WEIGHT_MOBILITY_O 			= 0.751;
	private double WEIGHT_MOBILITY_E 			= 0;
	
	private double WEIGHT_MOBILITY_S_O 			= 1.734;
	private double WEIGHT_MOBILITY_S_E 			= 0.934;
	
	private double WEIGHT_SPACE_O 				= 0.704;
	private double WEIGHT_SPACE_E 				= 0.179;
	
	private double WEIGHT_TRAPPED_O 			= 0.403;
	private double WEIGHT_TRAPPED_E 			= 0.601;
	
    private double WEIGHT_HUNGED_O 				= 1.328;//2 - 80 for 2 hanging
    private double WEIGHT_HUNGED_E 				= 2.149;//4 - 160 for 2 hanging
	
	private double WEIGHT_PST_O 				= 1.082;
	private double WEIGHT_PST_E 				= 0.622;
	
	private double WEIGHT_MATERIAL_PAWNS_O 		= 1;
	private double WEIGHT_MATERIAL_PAWNS_E 		= 1;
	
	private double WEIGHT_PAWNS_STANDARD_O 		= 0.698;
	private double WEIGHT_PAWNS_STANDARD_E 		= 0.440;
	
	private double WEIGHT_PAWNS_PASSED_O 		= 0;
	private double WEIGHT_PAWNS_PASSED_E 		= 1.737;
	
	private double WEIGHT_PAWNS_PASSED_KING_O 	= 0;
	private double WEIGHT_PAWNS_PASSED_KING_E 	= 2.444;
	
	private double WEIGHT_PAWNS_PSTOPPERS_O 	= 0;
	private double WEIGHT_PAWNS_PSTOPPERS_E 	= 0.418;
	
	private double WEIGHT_PAWNS_PSTOPPERS_A_O 	= 0;
	private double WEIGHT_PAWNS_PSTOPPERS_A_E 	= 1.357;
	
	
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin_Double("Evaluation [King Safety Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_KINGSAFETY_O / 10.0),
			//new UCIOptionSpin("Evaluation [King Safety Endgame]"			,  0.0, "type spin default 0 min 0 max 100"	, WEIGHT_KINGSAFETY_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Mobility Opening]"				, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MOBILITY_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Mobility Endgame]"				, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MOBILITY_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Safe Mobility Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MOBILITY_S_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Safe Mobility Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MOBILITY_S_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Cental Space Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_SPACE_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Cental Space Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_SPACE_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Trapped Piece Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_TRAPPED_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Trapped Piece Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_TRAPPED_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Hunged Piece Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_HUNGED_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Hunged Piece Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_HUNGED_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Piece-Square Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PST_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Piece-Square Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PST_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Pawn Material Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MATERIAL_PAWNS_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Pawn Material Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_MATERIAL_PAWNS_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Pawns Structure Opening]"		, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_STANDARD_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Pawns Structure Endgame]"		, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_STANDARD_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Pawns Opening]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PASSED_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Pawns Endgame]"			, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PASSED_E / 10.0),
			//new UCIOptionSpin("Evaluation [Passed King Distance Opening]"	,  0.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PASSED_KING_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed King Distance Endgame]"	, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PASSED_KING_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Stoppers Opening]"		,  0.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PSTOPPERS_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Stoppers Endgame]"		, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PSTOPPERS_E / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Stoppers Attack Opening]"	,  0.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PSTOPPERS_A_O / 10.0),
			new UCIOptionSpin_Double("Evaluation [Passed Stoppers Attack Endgame]"	, 10.0, "type spin default 10 min 0 max 100", WEIGHT_PAWNS_PSTOPPERS_A_E / 10.0),
	};
	
	
	public BagaturEvalConfigImpl_v2() {
		
	}
	
	@Override
	public void registerProviders(IUCIOptionsRegistry registry) {
		//Do Nothing	
	}
	
	@Override
	public UCIOption[] getSupportedOptions() {
		return options;
	}
	
	
	@Override
	public boolean applyOption(UCIOption option) {
		if ("Evaluation [King Safety Opening]".equals(option.getName())) {
			WEIGHT_KINGSAFETY_O = (Double) option.getValue();
			return true;
		} /*else if ("Evaluation [King Safety Endgame]".equals(option.getName())) {
			WEIGHT_KINGSAFETY_E = (Double) option.getValue();
			return true;
			
		}*/ else if ("Evaluation [Mobility Opening]".equals(option.getName())) {
			WEIGHT_MOBILITY_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Mobility Endgame]".equals(option.getName())) {
			WEIGHT_MOBILITY_E = (Double) option.getValue();
			return true;
			
		} else if ("Evaluation [Safe Mobility Opening]".equals(option.getName())) {
			WEIGHT_MOBILITY_S_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Safe Mobility Endgame]".equals(option.getName())) {
			WEIGHT_MOBILITY_S_E = (Double) option.getValue();
			return true;
			
		} else if ("Evaluation [Cental Space Opening]".equals(option.getName())) {
			WEIGHT_SPACE_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Cental Space Endgame]".equals(option.getName())) {
			WEIGHT_SPACE_E = (Double) option.getValue();
			return true;

		} else if ("Evaluation [Trapped Piece Opening]".equals(option.getName())) {
			WEIGHT_TRAPPED_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Trapped Piece Endgame]".equals(option.getName())) {
			WEIGHT_TRAPPED_E = (Double) option.getValue();
			return true;

		} else if ("Evaluation [Hunged Piece Opening]".equals(option.getName())) {
			WEIGHT_HUNGED_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Hunged Piece Endgame]".equals(option.getName())) {
			WEIGHT_HUNGED_E = (Double) option.getValue();
			return true;

		} else if ("Evaluation [Piece-Square Opening]".equals(option.getName())) {
			WEIGHT_PST_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Piece-Square Endgame]".equals(option.getName())) {
			WEIGHT_PST_E = (Double) option.getValue();
			return true;
		
		} else if ("Evaluation [Pawn Material Opening]".equals(option.getName())) {
			WEIGHT_MATERIAL_PAWNS_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Pawn Material Endgame]".equals(option.getName())) {
			WEIGHT_MATERIAL_PAWNS_E = (Double) option.getValue();
			return true;
		
		} else if ("Evaluation [Pawns Structure Opening]".equals(option.getName())) {
			WEIGHT_PAWNS_STANDARD_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Pawns Structure Endgame]".equals(option.getName())) {
			WEIGHT_PAWNS_STANDARD_E = (Double) option.getValue();
			return true;
			
		} else if ("Evaluation [Passed Pawns Opening]".equals(option.getName())) {
			WEIGHT_PAWNS_PASSED_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Passed Pawns Endgame]".equals(option.getName())) {
			WEIGHT_PAWNS_PASSED_E = (Double) option.getValue();
			return true;

		/*} else if ("Evaluation [Passed King Distance Opening]".equals(option.getName())) {
			WEIGHT_PAWNS_PASSED_KING_O = (Double) option.getValue();
			return true;*/
		} else if ("Evaluation [Passed King Distance Endgame]".equals(option.getName())) {
			WEIGHT_PAWNS_PASSED_KING_E = (Double) option.getValue();
			return true;

		} else if ("Evaluation [Passed Stoppers Opening]".equals(option.getName())) {
			WEIGHT_PAWNS_PSTOPPERS_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Passed Stoppers Endgame]".equals(option.getName())) {
			WEIGHT_PAWNS_PSTOPPERS_E = (Double) option.getValue();
			return true;

		} else if ("Evaluation [Passed Stoppers Attack Opening]".equals(option.getName())) {
			WEIGHT_PAWNS_PSTOPPERS_A_O = (Double) option.getValue();
			return true;
		} else if ("Evaluation [Passed Stoppers Attack Endgame]".equals(option.getName())) {
			WEIGHT_PAWNS_PSTOPPERS_A_E = (Double) option.getValue();
			return true;
			
		}
		
		return false;
	}

	
	public boolean useEvalCache() {
		return true;
	}
	
	
	public boolean useLazyEval() {
		return true;
	}
	
	
	public String getEvaluatorFactoryClassName() {
		return BagaturEvaluatorFactory.class.getName();
	}
	
	
	public String getPawnsCacheFactoryClassName() {
		return BagaturPawnsEvalFactory.class.getName();
	}


	@Override
	public double get_WEIGHT_SPACE_E() {
		return WEIGHT_SPACE_E;
	}


	@Override
	public double get_WEIGHT_TRAPPED_E() {
		return WEIGHT_TRAPPED_E;
	}
	
	
	@Override
	public double get_WEIGHT_PAWNS_PSTOPPERS_A_E() {
		return WEIGHT_PAWNS_PSTOPPERS_A_E;
	}
	
	
	public double get_WEIGHT_KINGSAFETY_O() {
		return WEIGHT_KINGSAFETY_O;
	}
	
	public double get_WEIGHT_KINGSAFETY_E() {
		return WEIGHT_KINGSAFETY_E;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_O() {
		return WEIGHT_PAWNS_PASSED_O;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_E() {
		return WEIGHT_PAWNS_PASSED_E;
	}
	
	public double get_WEIGHT_SPACE_O() {
		return WEIGHT_SPACE_O;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_KING_E() {
		return WEIGHT_PAWNS_PASSED_KING_E;
	}
	
	public double get_WEIGHT_PAWNS_PASSED_KING_O() {
		return WEIGHT_PAWNS_PASSED_KING_O;
	}
	
	public double get_WEIGHT_PST_O() {
		return WEIGHT_PST_O;
	}

	public double get_WEIGHT_PST_E() {
		return WEIGHT_PST_E;
	}

	public double get_WEIGHT_MOBILITY_O() {
		return WEIGHT_MOBILITY_O;
	}
	
	public double get_WEIGHT_MOBILITY_E() {
		return WEIGHT_MOBILITY_E;
	}
	
	public double get_WEIGHT_MOBILITY_S_O() {
		return WEIGHT_MOBILITY_S_O;
	}
	
	public double get_WEIGHT_MOBILITY_S_E() {
		return WEIGHT_MOBILITY_S_E;
	}

	public double get_WEIGHT_PAWNS_STANDARD_O() {
		return WEIGHT_PAWNS_STANDARD_O;
	}
	
	public double get_WEIGHT_PAWNS_STANDARD_E() {
		return WEIGHT_PAWNS_STANDARD_E;
	}
	
	public double get_WEIGHT_HUNGED_O() {
		return WEIGHT_HUNGED_O;
	}

	public double get_WEIGHT_HUNGED_E() {
		return WEIGHT_HUNGED_E;
	}
	
	public double get_WEIGHT_TRAPPED_O() {
		return WEIGHT_TRAPPED_O;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_O() {
		return WEIGHT_PAWNS_PSTOPPERS_O;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_E() {
		return WEIGHT_PAWNS_PSTOPPERS_E;
	}
	
	public double get_WEIGHT_PAWNS_PSTOPPERS_A_O() {
		return WEIGHT_PAWNS_PSTOPPERS_A_O;
	}

	public double get_WEIGHT_MATERIAL_PAWNS_O() {
		return WEIGHT_MATERIAL_PAWNS_O;
	}
	
	public double get_WEIGHT_MATERIAL_PAWNS_E() {
		return WEIGHT_MATERIAL_PAWNS_E;
	}
}
