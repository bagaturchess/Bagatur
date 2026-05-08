package bagaturchess.search.impl.alg;


public class SearchStatistics {
	
	
	public static int TYPE_MOVES_ALL 				= 0;
	public static int TYPE_NODES_ALL 				= 1;
	public static int TYPE_NODES_PV 					= 2;
	public static int TYPE_NODES_NONPV 				= 3;
	public static int TYPE_NODES_SEARCH 				= 4;
	public static int TYPE_NODES_QSEARCH 			= 5;
	public static int TYPE_TT_HIT 					= 6;
	public static int TYPE_TT_CUTOFF 				= 7;
	public static int TYPE_STATIC_NULL_MOVE_TRIES 	= 8;
	public static int TYPE_STATIC_NULL_MOVE_OK 		= 9;
	public static int TYPE_NULL_MOVE_TRIES 			= 10;
	public static int TYPE_NULL_MOVE_OK 				= 11;
	public static int TYPE_RAZORING_TRIES 			= 12;
	public static int TYPE_RAZORING_OK 				= 13;
	public static int TYPE_SME_TRIES 				= 14;
	public static int TYPE_SME_OK 					= 15;
	public static int TYPE_LMP_OK 					= 16;
	public static int TYPE_FUTILITY_SEARCH_OK 		= 17;
	public static int TYPE_SEE_SEARCH_ATTACKS_OK 	= 18;
	public static int TYPE_SEE_SEARCH_NONATTACKS_OK 	= 19;
	
	
	private double[] counts;
	
	
	public SearchStatistics() {
		
		counts = new double[100];
	}
	
	
	public void register(int type, double depth) {
		
		counts[type] += Math.pow(2, depth);
	}
	
	
	@Override
	public String toString() {
		
		
		String result = "STATISTICS:";
		
		
		result += "\r\nMOVES_ALL = " + counts[TYPE_MOVES_ALL];
		result += ", NODES_ALL = " + counts[TYPE_NODES_ALL];
		result += "\r\nNODES_PV = " + toPercentByAllNodes(counts[TYPE_NODES_PV]);
		result += ", NODES_NONPV = " + toPercentByAllNodes(counts[TYPE_NODES_NONPV]);
		result += "\r\nNODES_SEARCH = " + toPercentByAllNodes(counts[TYPE_NODES_SEARCH]);
		result += ", NODES_QSEARCH = " + toPercentByAllNodes(counts[TYPE_NODES_QSEARCH]);
		result += "\r\nTT_HIT = " + toPercentByAllNodes(counts[TYPE_TT_HIT]);
		result += ", TT_CUTOFF = " + toPercentByAllNodes(counts[TYPE_TT_CUTOFF]);
		result += "\r\nSTATIC_NULL_MOVE_TRIES = " + toPercentByAllNodes(counts[TYPE_STATIC_NULL_MOVE_TRIES]);
		result += ", STATIC_NULL_MOVE_OK = " + toPercentByAllNodes(counts[TYPE_STATIC_NULL_MOVE_OK]);
		result += "\r\nNULL_MOVE_TRIES = " + toPercentByAllNodes(counts[TYPE_NULL_MOVE_TRIES]);
		result += ", NULL_MOVE_OK = " + toPercentByAllNodes(counts[TYPE_NULL_MOVE_OK]);
		result += "\r\nRAZORING_TRIES = " + toPercentByAllNodes(counts[TYPE_RAZORING_TRIES]);
		result += ", RAZORING_OK = " + toPercentByAllNodes(counts[TYPE_RAZORING_OK]);
		result += "\r\nSME_TRIES = " + toPercentByAllNodes(counts[TYPE_SME_TRIES]);
		result += ", SME_OK = " + toPercentByAllNodes(counts[TYPE_SME_OK]);
		result += "\r\nLMP_OK = " + toPercentByAllMoves(counts[TYPE_LMP_OK]);
		result += ", FUTILITY_SEARCH_OK = " + toPercentByAllMoves(counts[TYPE_FUTILITY_SEARCH_OK]);
		result += ", SEE_SEARCH_ATTACKS_OK = " + toPercentByAllMoves(counts[TYPE_SEE_SEARCH_ATTACKS_OK]);
		result += ", SEE_SEARCH_NONATTACKS_OK = " + toPercentByAllMoves(counts[TYPE_SEE_SEARCH_NONATTACKS_OK]);
		
		return result;
	}


	private String toPercentByAllNodes(double value) {
		double percent = 100 * value / (double) counts[TYPE_NODES_ALL];
		String result = String.format("%.2f", percent);
		return result + "%";
	}
	
	
	private String toPercentByAllMoves(double value) {
		double percent = 100 * value / (double) counts[TYPE_MOVES_ALL];
		String result = String.format("%.2f", percent);
		return result + "%";
	}
}
