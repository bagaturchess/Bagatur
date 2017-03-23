package bagaturchess.engines.bagatur.v15;


public interface Weights_V15 {
	
	
	/**
	 * Material bonuses and penalties
	 */
	public static final int MATERIAL_DOUBLE_BISHOP_O 		= 40;
	public static final int MATERIAL_DOUBLE_BISHOP_E 		= 50;
	
	
	/**
	 * Standard bonuses and penalties
	 */
	public static final int STANDARD_TEMPO_O				= 33;
	public static final int STANDARD_TEMPO_E				= 33;
	public static final int STANDARD_CASTLING_O				= 25;
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
	public static final int[] PAWNS_DOUBLED_O				= new int[] {-11, -7, -7, -3};
	public static final int[] PAWNS_DOUBLED_E				= new int[] {-19, -15, -15, -11};
	public static final int[] PAWNS_ISOLATED_O				= new int[] {-14, -18, -18, -22};
	public static final int[] PAWNS_ISOLATED_E				= new int[] {-12, -16, -16, -20};
	public static final int[] PAWNS_BACKWARD_O				= new int[] {-18, -22, -22, -26};
	public static final int[] PAWNS_BACKWARD_E				= new int[] {-9, -13, -13, -17};
	public static final int[] PAWNS_SUPPORTED_O				= new int[] {4, 8, 8, 12};
	public static final int[] PAWNS_SUPPORTED_E				= new int[] {1, 5, 5, 9};
	public static final int[] PAWNS_CANDIDATE_O				= new int[] {0, 5, 10, 15, 20, 25};
	public static final int[] PAWNS_CANDIDATE_E				= new int[] {0, 10, 20, 30, 40, 70};
	public static final int[] PAWNS_PASSED_O				= new int[] {0, 20, 30, 40, 50, 60, 70};
	public static final int[] PAWNS_PASSED_E				= new int[] {0, 40, 60, 80, 100, 130, 180};
	public static final int[] PAWNS_PASSED_SUPPORTED_O		= new int[] {0, 40, 50, 60, 70, 80, 90};
	public static final int[] PAWNS_PASSED_SUPPORTED_E		= new int[] {0, 60, 80, 100, 120, 150, 200};
	
	public static final int PAWNS_KING_OPENED_O				= -20;
	public static final int PAWNS_ROOK_OPENED_O				= 30;
	public static final int PAWNS_ROOK_OPENED_E				= 30;
	public static final int PAWNS_ROOK_SEMIOPENED_O			= 20;
	public static final int PAWNS_ROOK_SEMIOPENED_E			= 20;
	public static final int PAWNS_ROOK_7TH2TH_O				= 20;
	public static final int PAWNS_ROOK_7TH2TH_E				= 20;
	public static final int PAWNS_QUEEN_7TH2TH_O			= 15;
	public static final int PAWNS_QUEEN_7TH2TH_E			= 15;
}
