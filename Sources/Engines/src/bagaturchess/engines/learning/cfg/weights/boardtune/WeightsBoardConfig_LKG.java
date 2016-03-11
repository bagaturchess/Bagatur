package bagaturchess.engines.learning.cfg.weights.boardtune;


public class WeightsBoardConfig_LKG extends WeightsBoardConfig_LKG2 {
	
	
	public WeightsBoardConfig_LKG() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		//MATERIAL_PAWN_E+0.15	=	23,	8,	7
		//PST_KING_O-0.15	=	20,	8,	7
		//MATERIAL_BISHOP_O-0.15	=	20,	8,	7
		
		//MATERIAL_PAWN_E += 0.15 * MATERIAL_PAWN_E;
		//PST_KING_O -= 0.15 * PST_KING_O;
		//MATERIAL_BISHOP_O -= 0.15 * MATERIAL_BISHOP_O;
	}
}

