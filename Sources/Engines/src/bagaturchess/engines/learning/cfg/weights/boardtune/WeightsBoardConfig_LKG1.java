package bagaturchess.engines.learning.cfg.weights.boardtune;


public class WeightsBoardConfig_LKG1 extends WeightsBoardConfig_LKG0 {
	
	
	public WeightsBoardConfig_LKG1() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG1(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//PST_BISHOP_O+0.50	=	22,	8,	8
		//MATERIAL_QUEEN_O+0.50	=	22,	8,	8
		//PST_PAWN_O+0.50	=	21,	8,	8
		//PST_KING_E+0.50	=	20,	8,	8
		//PST_BISHOP_E+0.50	=	19,	8,	8
		//ORG1	=	19,	8,	8
		//PST_QUEEN_E+0.50	=	19,	8,	8
		//MATERIAL_QUEEN_O-0.50	=	19,	8,	8
		//MATERIAL_ROOK_O+0.50	=	18,	8,	8
		//LKG	=	18,	8,	8
		
		PST_BISHOP_O += 0.5 * PST_BISHOP_O;
		MATERIAL_QUEEN_O += 0.5 * MATERIAL_QUEEN_O;
	}
}
