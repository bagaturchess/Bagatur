package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights {
	
	
	public static final int MATERIAL_DOUBLE_BISHOP_O 		= 40;
	public static final int MATERIAL_DOUBLE_BISHOP_E 		= 50;
	
	public static final int STANDARD_TEMPO_O				= 25;
	public static final int STANDARD_TEMPO_E				= 35;
	public static final int STANDARD_CASTLING_O				= 10;
	public static final int STANDARD_CASTLING_E				= 0;
	public static final int STANDARD_FIANCHETTO         	= 30;
	public static final int STANDARD_TRAP_BISHOP			= -120;
	public static final int STANDARD_BLOCKED_PAWN			= -30;
	
	public static final int STANDARD_KINGS_OPPOSITION_O 	= 0;
	public static final int STANDARD_KINGS_OPPOSITION_E 	= 50;
	
	
	public static final double PAWNS_DOUBLED_O				= 1;
	public static final double PAWNS_DOUBLED_E				= 1;
	public static final double PAWNS_ISOLATED_O				= 1;
	public static final double PAWNS_ISOLATED_E				= 1;
	public static final double PAWNS_BACKWARD_O				= 1;
	public static final double PAWNS_BACKWARD_E				= 1;
	public static final double PAWNS_SUPPORTED_O			= 1;
	public static final double PAWNS_SUPPORTED_E			= 1;
	public static final double PAWNS_CANDIDATE_O			= 1;
	public static final double PAWNS_CANDIDATE_E			= 1;
	public static final double PAWNS_PASSED_O				= 1;
	public static final double PAWNS_PASSED_E				= 1;
	public static final double PAWNS_PASSED_SUPPORTED_O		= 1;
	public static final double PAWNS_PASSED_SUPPORTED_E		= 1;
	
	public static final double PAWNS_KING_F_O				= 1;
	public static final double PAWNS_KING_F_E				= 1;
	public static final double PAWNS_KING_FF_O				= 1;
	public static final double PAWNS_KING_FF_E				= 1;
	public static final double PAWNS_KING_OP_F_O			= 1;
	public static final double PAWNS_KING_OP_F_E			= 1;
	public static final int PASSED_UNSTOPPABLE 				= 550;
	public static final double PAWNS_PASSED_STOPPERS		= 1;
	
	public static final int PAWNS_KING_GUARDS				= 4;
	public static final int PAWNS_KING_OPENED				= -12;
	public static final int PAWNS_ROOK_OPENED_O				= 22;
	public static final int PAWNS_ROOK_OPENED_E				= 16;
	public static final int PAWNS_ROOK_SEMIOPENED_O			= 7;
	public static final int PAWNS_ROOK_SEMIOPENED_E			= 20;
	public static final int PAWNS_ROOK_7TH2TH_O				= 3;
	public static final int PAWNS_ROOK_7TH2TH_E				= 36;
	public static final int PAWNS_QUEEN_7TH2TH_O			= 0;
	public static final int PAWNS_QUEEN_7TH2TH_E			= 26;
	
	
	public static final double MOBILITY_KNIGHT_O			= 1;
	public static final double MOBILITY_KNIGHT_E			= 1;
	public static final double MOBILITY_BISHOP_O			= 1;
	public static final double MOBILITY_BISHOP_E			= 1;
	public static final double MOBILITY_ROOK_O				= 1;
	public static final double MOBILITY_ROOK_E				= 1;
	public static final double MOBILITY_QUEEN_O				= 1;
	public static final double MOBILITY_QUEEN_E				= 1;
	
	public static final double KNIGHT_OUTPOST_O				= 1;
	public static final double KNIGHT_OUTPOST_E				= 1;
	public static final double BISHOP_OUTPOST_O				= 1;
	public static final double BISHOP_OUTPOST_E				= 1;
	public static final double BISHOP_BAD_O					= 5;
	public static final double BISHOP_BAD_E					= 10;
	
	public static final double KING_SAFETY_O				= 1;
	public static final int SPACE_O							= 1;
	public static final int SPACE_E							= 1;
	public static final double HUNGED_O						= 1;
	public static final double HUNGED_E						= 1;
	
	public static final int[] MOBILITY_KNIGHT_S_O	= new int[] {-25, -14, -8, -1, 5, 8, 12, 18, 22};
	public static final int[] MOBILITY_KNIGHT_S_E	= new int[] {-25, -15, -6, 7, 10, 17, 21, 23, 25};
	public static final int[] MOBILITY_BISHOP_S_O	= new int[] {-15, -11, -8, -6, -4, -2, 0, 2, 4, 7, 10, 12, 14, 16};
	public static final int[] MOBILITY_BISHOP_S_E	= new int[] {-14, 0, 2, 7, 7, 8, 8, 11, 13, 13, 14, 15, 15, 15};
	public static final int[] MOBILITY_ROOK_S_O		= new int[] {-13, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 14, 14};
	public static final int[] MOBILITY_ROOK_S_E		= new int[] {-26, -21, -16, -7, -5, 0, 3, 6, 6, 7, 9, 22, 24, 25, 26};
	public static final int[] MOBILITY_QUEEN_S_O	= new int[] {-12, -10, -8, -5, -2, 0, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12};
	public static final int[] MOBILITY_QUEEN_S_E	= new int[] {-17, -16, -15, -14, -14, -13, -8, -6, -1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10, 11, 12, 13, 13, 14, 15, 16, 17};
	

	
	public static final int TRAPED_O				= -1;
	public static final int TRAPED_E				= -1;
}
