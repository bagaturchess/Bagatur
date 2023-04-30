package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.filler;


interface ALL_FeaturesConstants {
	
	
	/**
	 * STANDARD
	 */
	public static final int FEATURE_ID_MATERIAL_PAWN       = 1010;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     = 1020;
	public static final int FEATURE_ID_MATERIAL_BISHOP     = 1030;
	public static final int FEATURE_ID_MATERIAL_ROOK       = 1040;
	public static final int FEATURE_ID_MATERIAL_QUEEN      = 1050;
	public static final int FEATURE_ID_KINGSAFE_CASTLING   = 1060;
	public static final int FEATURE_ID_KINGSAFE_FIANCHETTO = 1070;
	public static final int FEATURE_ID_BISHOPS_DOUBLE      = 1080;
	public static final int FEATURE_ID_KNIGHTS_DOUBLE      = 1081;
	public static final int FEATURE_ID_ROOKS_DOUBLE        = 1082;
	public static final int FEATURE_ID_5PAWNS_ROOKS        = 1085;
	public static final int FEATURE_ID_5PAWNS_KNIGHTS      = 1086;
	public static final int FEATURE_ID_KINGSAFE_F_PAWN     = 1090;
	public static final int FEATURE_ID_KINGSAFE_G_PAWN     = 1100;
	public static final int FEATURE_ID_KINGS_DISTANCE	   = 1110;
	
	
	/**
	 * PAWNS ITERATION
	 */
	public static final int FEATURE_ID_PAWNS_DOUBLED       = 2010;
	public static final int FEATURE_ID_PAWNS_ISOLATED      = 2020;
	public static final int FEATURE_ID_PAWNS_BACKWARD      = 2030;
	public static final int FEATURE_ID_PAWNS_SUPPORTED     = 2040;
	public static final int FEATURE_ID_PAWNS_CANNOTBS      = 2050;
	public static final int FEATURE_ID_PAWNS_PASSED        = 2060;
	public static final int FEATURE_ID_PAWNS_PASSED_RNK    = 2070;
	public static final int FEATURE_ID_UNSTOPPABLE_PASSER  = 2080;
	public static final int FEATURE_ID_PAWNS_PSTOPPERS	   = 2083;
	public static final int FEATURE_ID_PAWNS_CANDIDATE     = 2090;
	public static final int FEATURE_ID_KING_PASSERS_F  	   = 2100;
	public static final int FEATURE_ID_KING_PASSERS_FF 	   = 2110;
	public static final int FEATURE_ID_KING_PASSERS_F_OP   = 2120;
	public static final int FEATURE_ID_PAWNS_ISLANTS       = 2130;
	public static final int FEATURE_ID_PAWNS_GARDS         = 2140;
	public static final int FEATURE_ID_PAWNS_GARDS_REM     = 2150;
	public static final int FEATURE_ID_PAWNS_STORMS        = 2160;
	public static final int FEATURE_ID_PAWNS_STORMS_CLS    = 2170;
	public static final int FEATURE_ID_PAWNS_OPENNED       = 2180;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OWN    = 2190;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OP     = 2200;
	public static final int FEATURE_ID_PAWNS_WEAK          = 2210;
	public static final int FEATURE_ID_SPACE       		   = 2220;
	public static final int FEATURE_ID_ROOK_INFRONT_PASSER = 2230;
	public static final int FEATURE_ID_ROOK_BEHIND_PASSER  = 2240;
	
	
	/**
	 * PIECES ITERATION
	 */
	public static final int FEATURE_ID_PST_PAWN  		   = 3010;
	public static final int FEATURE_ID_PST_KING  		   = 3020;
	public static final int FEATURE_ID_PST_KNIGHT  	       = 3030;
	public static final int FEATURE_ID_PST_BISHOP  		   = 3040;
	public static final int FEATURE_ID_PST_ROOK  		   = 3050;
	public static final int FEATURE_ID_PST_QUEEN  		   = 3060;
	public static final int FEATURE_ID_BISHOPS_BAD		   = 3070;
	public static final int FEATURE_ID_KNIGHTS_OUTPOST	   = 3080;
	public static final int FEATURE_ID_ROOKS_OPENED		   = 3090;
	public static final int FEATURE_ID_ROOKS_SEMIOPENED	   = 3100;
	public static final int FEATURE_ID_TROPISM_KNIGHT  	   = 3110;
	public static final int FEATURE_ID_TROPISM_BISHOP  	   = 3120;
	public static final int FEATURE_ID_TROPISM_ROOK  	   = 3130;
	public static final int FEATURE_ID_TROPISM_QUEEN  	   = 3140;
	public static final int FEATURE_ID_ROOKS_7TH_2TH       = 3150;
	public static final int FEATURE_ID_QUEENS_7TH_2TH      = 3160;
	
	
	/**
	 * MOVES ITERATION
	 */
	public static final int FEATURE_ID_KINGSAFE_L1     	   = 4010;
	public static final int FEATURE_ID_KINGSAFE_L2     	   = 4020;
	public static final int FEATURE_ID_MOBILITY_KNIGHT     = 4030;
	public static final int FEATURE_ID_MOBILITY_BISHOP     = 4040;
	public static final int FEATURE_ID_MOBILITY_ROOK       = 4050;
	public static final int FEATURE_ID_MOBILITY_QUEEN      = 4060;
	public static final int FEATURE_ID_MOBILITY_KNIGHT_S   = 4070;
	public static final int FEATURE_ID_MOBILITY_BISHOP_S   = 4080;
	public static final int FEATURE_ID_MOBILITY_ROOK_S     = 4090;
	public static final int FEATURE_ID_MOBILITY_QUEEN_S    = 4100;
	public static final int FEATURE_ID_ROOKS_PAIR_H        = 4110;
	public static final int FEATURE_ID_ROOKS_PAIR_V        = 4120;
	public static final int FEATURE_ID_TRAP		           = 4150;
	public static final int FEATURE_ID_PIN_BIGGER_PIECE    = 4220;
	public static final int FEATURE_ID_PIN_EQUAL_PIECE     = 4230;
	public static final int FEATURE_ID_PIN_LOWER_PIECE     = 4240;
	public static final int FEATURE_ID_ATTACK_BIGGER_PIECE = 4310;
	public static final int FEATURE_ID_ATTACK_EQUAL_PIECE  = 4320;
	public static final int FEATURE_ID_ATTACK_LOWER_PIECE  = 4330;
	public static final int FEATURE_ID_HUNGED_PIECES       = 4400;
	public static final int FEATURE_ID_HUNGED_PAWNS        = 4410;
	public static final int FEATURE_ID_HUNGED_ALL          = 4420;
	public static final int FEATURE_ID_PAWNS_PSTOPPERS_A   = 4425;
	
	/**
	 * FIELDS STATES ITERATION
	 */
	public static final int FEATURE_ID_PST_CONTROL_EQ      = 5010;
	public static final int FEATURE_ID_PST_CONTROL_MORE    = 5020;
	
}
