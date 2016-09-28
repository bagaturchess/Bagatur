package bagaturchess.engines.bagatur.eval_v15;


public interface Weights_V15 {
	
	
	/**
	 * Material bonuses and penalties
	 */
	public static final int MATERIAL_DOUBLE_BISHOP_O 		= 40;
	public static final int MATERIAL_DOUBLE_BISHOP_E 		= 50;
	
	
	/**
	 * Standard bonuses and penalties
	 */
	public static final int STANDARD_TEMPO_O				= 25;
	public static final int STANDARD_TEMPO_E				= 35;
	public static final int STANDARD_CASTLING_O				= 10;
	public static final int STANDARD_CASTLING_E				= 0;
	public static final int STANDARD_FIANCHETTO             = 30;
	public static final int STANDARD_TRAP_BISHOP			= -120;
	public static final int STANDARD_BLOCKED_PAWN			= -30;
	public static final int STANDARD_KINGS_OPPOSITION_O 	= 0;
	public static final int STANDARD_KINGS_OPPOSITION_E 	= 50;
	
	
	/**
	 * Pawns bonuses and penalties
	 */
	public static final int PAWNS_KING_GUARDS				= 10;
	public static final int[] PAWNS_DOUBLED_O				= new int[] {-12, -11, -6, -3};
	public static final int[] PAWNS_DOUBLED_E				= new int[] {-19, -17, -16, -13};
	public static final int[] PAWNS_ISOLATED_O				= new int[] {-3, -6, -11, -12};
	public static final int[] PAWNS_ISOLATED_E				= new int[] {-13, -16, -17, -19};
	public static final int[] PAWNS_BACKWARD_O				= new int[] {-1, -3, -5, -6};
	public static final int[] PAWNS_BACKWARD_E				= new int[] {-6, -8, -9, -10};
	public static final int[] PAWNS_SUPPORTED_O				= new int[] {1, 3, 5, 6};
	public static final int[] PAWNS_SUPPORTED_E				= new int[] {6, 8, 9, 10};
	public static final int[] PAWNS_CANDIDATE_O				= new int[] {0, 1, 2, 5, 9, 18};
	public static final int[] PAWNS_CANDIDATE_E				= new int[] {0, 2, 7, 11, 26, 72};
	public static final int[] PAWNS_PASSED_O				= new int[] {0, 11, 11, 11, 26, 40, 65};
	public static final int[] PAWNS_PASSED_E				= new int[] {0, 7, 7, 25, 63, 134, 186};
	public static final int[] PAWNS_PASSED_SUPPORTED_O		= new int[] {0, 13, 13, 21, 41, 62, 124};
	public static final int[] PAWNS_PASSED_SUPPORTED_E		= new int[] {0, 9, 12, 31, 79, 178, 299};
}
