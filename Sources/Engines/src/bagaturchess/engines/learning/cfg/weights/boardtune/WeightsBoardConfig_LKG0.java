package bagaturchess.engines.learning.cfg.weights.boardtune;


import bagaturchess.engines.learning.cfg.weights.WeightsBoardConfig;


public class WeightsBoardConfig_LKG0 extends WeightsBoardConfig {
	
	
	public WeightsBoardConfig_LKG0() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG0(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		//MATERIAL_PAWN_O+0.50	=	24,	7,	8
		//MATERIAL_ROOK_O-0.50	=	21,	7,	8
		//PST_KNIGHT_O+0.50	=	20,	7,	8
		//PST_QUEEN_O-0.50	=	18,	8,	7
		//..
		//ORG1	=	16,	8,	7
		//PST_KING_E-0.50	=	16,	8,	7
		//ORG	=	16,	8,	7
		
		MATERIAL_PAWN_O += 0.5 * MATERIAL_PAWN_O;
	}
}
