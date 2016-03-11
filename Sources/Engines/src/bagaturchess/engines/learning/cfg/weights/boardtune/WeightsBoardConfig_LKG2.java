package bagaturchess.engines.learning.cfg.weights.boardtune;


public class WeightsBoardConfig_LKG2 extends WeightsBoardConfig_LKG1 {
	
	
	public WeightsBoardConfig_LKG2() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG2(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		//MATERIAL_PAWN_E-0.15	=	26,	9,	9
		//MATERIAL_ROOK_O+0.15	=	23,	9,	9
		//PST_QUEEN_E-0.15	=	23,	9,	9
		
		MATERIAL_PAWN_E -= 0.15 * MATERIAL_PAWN_E;
		MATERIAL_ROOK_O += 0.15 * MATERIAL_ROOK_O;
		PST_QUEEN_E -= 0.15 * PST_QUEEN_E;
	}
}
