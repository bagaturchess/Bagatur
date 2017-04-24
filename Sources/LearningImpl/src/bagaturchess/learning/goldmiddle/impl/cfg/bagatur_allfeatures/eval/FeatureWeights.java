package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights {
	
	
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
	
	
	public static final double MOBILITY_KNIGHT_O			=	1.9174823628170556;
	public static final double MOBILITY_KNIGHT_E			=	1.0390797703403882;
	public static final double MOBILITY_BISHOP_O			=	1.5217100516754685;
	public static final double MOBILITY_BISHOP_E			=	1.0205809226604579;
	public static final double MOBILITY_ROOK_O				=	1.1685760851266969;
	public static final double MOBILITY_ROOK_E				=	2.5111606365686217;
	public static final double MOBILITY_QUEEN_O				=	0.5269853984695055;
	public static final double MOBILITY_QUEEN_E				=	0.5086489426838672;
	
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
	
	public static final double MOBILITY_KNIGHT_S_O			=	0.6733328670206458;
	public static final double MOBILITY_KNIGHT_S_E			=	0.9140474918712359;
	public static final double MOBILITY_BISHOP_S_O			=	1.4863247524029488;
	public static final double MOBILITY_BISHOP_S_E			=	0.7649825338569627;
	public static final double MOBILITY_ROOK_S_O			=	1.0384314593650399;
	public static final double MOBILITY_ROOK_S_E			=	0.33498040762538084;
	public static final double MOBILITY_QUEEN_S_O			=	4.077723481082999;
	public static final double MOBILITY_QUEEN_S_E			=	2.8239286707315814;
	
	public static final double TRAPED_O						= 1;
	public static final double TRAPED_E						= 0.83;
	
	public static final double PASSERS_FRONT_ATTACKS_O 		= 0;
	public static final double PASSERS_FRONT_ATTACKS_E 		= 1.12;
}
