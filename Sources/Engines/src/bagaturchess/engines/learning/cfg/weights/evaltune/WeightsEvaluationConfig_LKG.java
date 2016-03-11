package bagaturchess.engines.learning.cfg.weights.evaltune;


public class WeightsEvaluationConfig_LKG extends WeightsEvaluationConfig_LKG2 {
	
	
	public WeightsEvaluationConfig_LKG() {
		super();
		init();
	}
	
	
	public WeightsEvaluationConfig_LKG(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//PAWNS_GARDS_REM_O-0.50	=	27,	9,	9
		//---SPACE_E+0.50	=	26,	9,	9
		//PAWNS5_ROOKS_O+0.50	=	26,	9,	9
		//---SPACE_E-0.50	=	25,	9,	9
		//MOBILITY_ROOK_S_E-0.50	=	25,	9,	9
		
		PAWNS_GARDS_REM_O -= 0.50 * PAWNS_GARDS_REM_O;
		PAWNS5_ROOKS_O += 0.50 * PAWNS5_ROOKS_O;
		MOBILITY_ROOK_S_E -= 0.50 * MOBILITY_ROOK_S_E;
	}
}
