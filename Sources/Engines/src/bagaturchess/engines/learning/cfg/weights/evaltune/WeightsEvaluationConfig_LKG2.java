package bagaturchess.engines.learning.cfg.weights.evaltune;


public class WeightsEvaluationConfig_LKG2 extends WeightsEvaluationConfig_LKG1 {
	
	
	public WeightsEvaluationConfig_LKG2() {
		super();
		init();
	}
	
	
	public WeightsEvaluationConfig_LKG2(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//BISHOPS_BAD_O-0.50	=	28,	10,	8
		//...
		//TRAP_E-0.50	=	25,	9,	9
		//PIN_LOWER_E-0.50	=	24,	9,	9
		//KINGS_PASSERS_FF_E+0.50	=	24,	9,	9
		//PAWNS_ISOLATED_E-0.50	=	24,	9,	9
		//QUEENS_7TH_2TH_E-0.50	=	24,	9,	9
		//PIN_LOWER_O+0.50	=	24,	9,	9
		//ROOKS_DOUBLE_O-0.50	=	23,	9,	9
		//PAWNS_SEMIOP_OP_O-0.50	=	23,	9,	9
		//TROPISM_ROOK_O-0.50	=	23,	9,	9
		//KINGSAFE_F_O+0.50	=	23,	9,	9
		//KNIGHTS_DOUBLE_O+0.50	=	23,	9,	9
		
		BISHOPS_BAD_O -= 0.50 * BISHOPS_BAD_O;
		TRAP_E -= 0.50 * TRAP_E;
		PIN_LOWER_E -= 0.50 * PIN_LOWER_E;
		KINGS_PASSERS_FF_E += 0.50 * KINGS_PASSERS_FF_E;
		PAWNS_ISOLATED_E -= 0.50 * PAWNS_ISOLATED_E;
		QUEENS_7TH_2TH_E -= 0.50 * QUEENS_7TH_2TH_E;
		PIN_LOWER_O += 0.50 * PIN_LOWER_O;
		ROOKS_DOUBLE_O -= 0.50 * ROOKS_DOUBLE_O;
		PAWNS_SEMIOP_OP_O -= 0.50 * PAWNS_SEMIOP_OP_O;
		TROPISM_ROOK_O -= 0.50 * TROPISM_ROOK_O;
		KINGSAFE_F_O += 0.50 * KINGSAFE_F_O;
		KNIGHTS_DOUBLE_O += 0.50 * KNIGHTS_DOUBLE_O;
	}
}
