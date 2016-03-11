package bagaturchess.engines.learning.cfg.weights.boardtune;


public class WeightsBoardConfig_LKG3 extends WeightsBoardConfig_LKG2 {
	
	
	public WeightsBoardConfig_LKG3() {
		super();
		init();
	}
	
	
	public WeightsBoardConfig_LKG3(String[] args) {
		super(args);
		init();
	}
	
	
	private void init() {
		
		//LKG3
		//MATERIAL_BISHOP_E-0.15	=	1,	2344,	140,	44,	242,	0.18181818181818182
		//LKG0	=	2,	2330,	166,	30,	294,	0.10204081632653061
		
		MATERIAL_BISHOP_E -= 0.15 * MATERIAL_BISHOP_E;
	}
}
