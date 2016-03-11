package bagaturchess.engines.learning.cfg.weights.boardtune;


public class WeightsBoardConfig_LKG0_1 extends WeightsBoardConfig_LKG0 {
	
	
	public WeightsBoardConfig_LKG0_1() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG0_1(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//LKG0_1
		//MATERIAL_BISHOP_E-0.15	=	1,	2344,	140,	44,	242,	0.18181818181818182
		//LKG0	=	2,	2330,	166,	30,	294,	0.10204081632653061
		
		MATERIAL_BISHOP_E -= 0.15 * MATERIAL_BISHOP_E;
	}
}
