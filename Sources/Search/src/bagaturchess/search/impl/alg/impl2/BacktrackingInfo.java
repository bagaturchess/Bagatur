package bagaturchess.search.impl.alg.impl2;

public class BacktrackingInfo {
	
	static int EVAL_NOT_CALCULATED = 123456789;
	
	int colour_to_move;
	long hash_key;
	int hash_move;
	boolean null_move;
	int eval;
	int best_move;
	int mate_move;
	int material_exchanged;
}
