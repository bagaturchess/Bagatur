package bagaturchess.search.impl.alg;


public class SearchStatistics {
	
	
	public static int TYPE_NODES_ALL 				= 0;
	public static int TYPE_NODES_PV 					= 1;
	public static int TYPE_NODES_NONPV 				= 2;
	public static int TYPE_NODES_SEARCH 				= 3;
	public static int TYPE_NODES_QSEARCH 			= 4;
	public static int TYPE_TT_HIT 					= 5;
	public static int TYPE_TT_CUTOFF 				= 6;
	public static int TYPE_STATIC_NULL_MOVE_TRIES 	= 7;
	public static int TYPE_STATIC_NULL_MOVE_OK 		= 8;
	public static int TYPE_NULL_MOVE_TRIES 			= 9;
	public static int TYPE_NULL_MOVE_OK 				= 10;
	public static int TYPE_RAZORING_TRIES 			= 11;
	public static int TYPE_RAZORING_OK 				= 12;
	public static int TYPE_SME_TRIES 				= 13;
	public static int TYPE_SME_OK 					= 14;
	public static int TYPE_LMP_OK 					= 15;
	public static int TYPE_FUTILITY_SEARCH_OK 		= 16;
	public static int TYPE_SEE_SEARCH_ATTACKS_OK 	= 17;
	public static int TYPE_SEE_SEARCH_NONATTACKS_OK 	= 18;
	
	
	private int[] counts;
	
	
	public SearchStatistics() {
		
		counts = new int[100];
	}
	
	
	public void register(int type) {
		
		counts[type]++;
	}
	
	
	@Override
	public String toString() {
		
		
		String result = "STATISTICS:";
		
		
		result += "\r\nNODES_ALL = " + counts[TYPE_NODES_ALL];
		result += "\r\nNODES_PV = " + toPercent(counts[TYPE_NODES_PV]);
		result += ", NODES_NONPV = " + toPercent(counts[TYPE_NODES_NONPV]);
		result += "\r\nNODES_SEARCH = " + toPercent(counts[TYPE_NODES_SEARCH]);
		result += ", NODES_QSEARCH = " + toPercent(counts[TYPE_NODES_QSEARCH]);
		result += "\r\nTT_HIT = " + toPercent(counts[TYPE_TT_HIT]);
		result += ", TT_CUTOFF = " + toPercent(counts[TYPE_TT_CUTOFF]);
		result += "\r\nSTATIC_NULL_MOVE_TRIES = " + toPercent(counts[TYPE_STATIC_NULL_MOVE_TRIES]);
		result += ", STATIC_NULL_MOVE_OK = " + toPercent(counts[TYPE_STATIC_NULL_MOVE_OK]);
		result += "\r\nNULL_MOVE_TRIES = " + toPercent(counts[TYPE_NULL_MOVE_TRIES]);
		result += ", NULL_MOVE_OK = " + toPercent(counts[TYPE_NULL_MOVE_OK]);
		result += "\r\nRAZORING_TRIES = " + toPercent(counts[TYPE_RAZORING_TRIES]);
		result += ", RAZORING_OK = " + toPercent(counts[TYPE_RAZORING_OK]);
		result += "\r\nSME_TRIES = " + toPercent(counts[TYPE_SME_TRIES]);
		result += ", SME_OK = " + toPercent(counts[TYPE_SME_OK]);
		result += "\r\nLMP_OK = " + toPercent(counts[TYPE_LMP_OK]);
		result += ", FUTILITY_SEARCH_OK = " + toPercent(counts[TYPE_FUTILITY_SEARCH_OK]);
		result += ", SEE_SEARCH_ATTACKS_OK = " + toPercent(counts[TYPE_SEE_SEARCH_ATTACKS_OK]);
		result += ", SEE_SEARCH_NONATTACKS_OK = " + toPercent(counts[TYPE_SEE_SEARCH_NONATTACKS_OK]);
		
		return result;
	}


	private String toPercent(int value) {
		double percent = 100 * value / (double) counts[TYPE_NODES_ALL];
		String result = String.format("%.2f", percent);
		return result + "%";
	}
}
