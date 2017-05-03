package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights_org {
	
	
	public static final int MATERIAL_DOUBLE_BISHOPS_O 		= 40;
	public static final int MATERIAL_DOUBLE_BISHOPS_E 		= 50;
	
	public static final int PST_O 							= 1;
	public static final int PST_E 							= 1;
	
	public static final int STANDARD_TEMPO_O				= 25;
	public static final int STANDARD_TEMPO_E				= 35;
	public static final int STANDARD_CASTLING_O				= 10;
	public static final int STANDARD_CASTLING_E				= 0;
	public static final int STANDARD_FIANCHETTO_O         	= 30;
	public static final int STANDARD_TRAP_BISHOP_O			= -120;
	public static final int STANDARD_BLOCKED_PAWN_O			= -30;
	
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
	public static final double PAWNS_CANDIDATE_E			= 1.5;
	public static final double PAWNS_PASSED_O				= 1;
	public static final double PAWNS_PASSED_E				= 1.5;
	public static final double PAWNS_PASSED_SUPPORTED_O		= 1;
	public static final double PAWNS_PASSED_SUPPORTED_E		= 1.5;
	
	public static final double PAWNS_KING_F_O				= 0;
	public static final double PAWNS_KING_F_E				= 0.3;
	public static final double PAWNS_KING_FF_O				= 0;
	public static final double PAWNS_KING_FF_E				= 0.3;
	public static final double PAWNS_KING_OP_F_O			= 0;
	public static final double PAWNS_KING_OP_F_E			= 0.3;
	public static final int PASSED_UNSTOPPABLE_O			= 0;
	public static final int PASSED_UNSTOPPABLE_E			= 550;
	public static final double PAWNS_PASSED_STOPPERS_O		= 0;
	public static final double PAWNS_PASSED_STOPPERS_E		= 0.75;
	
	public static final int PAWNS_KING_GUARDS_O				= 4;
	public static final int PAWNS_KING_OPENED_O				= -12;
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
	
	public static final double KING_SAFETY_O				= 3;
	public static final double KING_SAFETY_E				= 0;
	public static final double SPACE_O						= 0.3;
	public static final double SPACE_E						= 0.15;
	public static final double HUNGED_O						= 2;
	public static final double HUNGED_E						= 4;
	
	public static final double MOBILITY_KNIGHT_S_O			= 1;
	public static final double MOBILITY_KNIGHT_S_E			= 1;
	public static final double MOBILITY_BISHOP_S_O			= 1;
	public static final double MOBILITY_BISHOP_S_E			= 1;
	public static final double MOBILITY_ROOK_S_O			= 1;
	public static final double MOBILITY_ROOK_S_E			= 1;
	public static final double MOBILITY_QUEEN_S_O			= 1;
	public static final double MOBILITY_QUEEN_S_E			= 1;
	
	public static final double TRAPED_O						= 1;
	public static final double TRAPED_E						= 0.83;
	
	public static final double PASSERS_FRONT_ATTACKS_O 		= 0;
	public static final double PASSERS_FRONT_ATTACKS_E 		= 1.12;
}
