package bagaturchess.engines.learning.cfg.weights.evaltune;


public class WeightsEvaluationConfig_LKG1 extends WeightsEvaluationConfig_LKG0 {
	
	
	public WeightsEvaluationConfig_LKG1() {
		super();
		init();
	}
	
	
	public WeightsEvaluationConfig_LKG1(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//ATTACK_LOWER_O-0.50	=	28,	9,	9
		//PAWNS_BACKWARD_E+0.50	=	26,	9,	9
		//PAWNS_BACKWARD_E-0.50	=	25,	9,	9
		//MOBILITY_QUEEN_O+0.50	=	25,	9,	9
		//BAGATUR12D	=	24,	10,	8
		//ROOKS_DOUBLE_O+0.50	=	24,	9,	9
		//PIN_BIGGER_O+0.50	=	24,	9,	9
		//PAWNS5_KNIGHTS_E-0.50	=	24,	9,	9
		//MOBILITY_ROOK_S_E-0.50	=	24,	9,	9
		//MOBILITY_ROOK_E+0.50	=	24,	9,	9
		
		ATTACK_LOWER_O -= 0.50 * ATTACK_LOWER_O;
		PAWNS_BACKWARD_E -= 0.50 * PAWNS_BACKWARD_E;
		MOBILITY_QUEEN_O += 0.50 * MOBILITY_QUEEN_O;
		ROOKS_DOUBLE_O += 0.50 * ROOKS_DOUBLE_O;
		PIN_BIGGER_O += 0.50 * PIN_BIGGER_O;
		PAWNS5_KNIGHTS_E -= 0.50 * PAWNS5_KNIGHTS_E;
		MOBILITY_ROOK_S_E -= 0.50 * MOBILITY_ROOK_S_E;
		MOBILITY_ROOK_E += 0.50 * MOBILITY_ROOK_E;
	}
}
