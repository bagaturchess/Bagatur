package bagaturchess.learning.goldmiddle.impl.cfg.old0;


public interface FeaturesConstants {
	
	
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
	public static final int FEATURE_ID_PENETRATION_OP	   = 4104;
	public static final int FEATURE_ID_PENETRATION_OP_S	   = 4105;
	public static final int FEATURE_ID_PENETRATION_KING	   = 4106;
	public static final int FEATURE_ID_PENETRATION_KING_S  = 4107;
	public static final int FEATURE_ID_ROOKS_PAIR_H        = 4110;
	public static final int FEATURE_ID_ROOKS_PAIR_V        = 4120;
	
	public static final int FEATURE_ID_TRAP		           = 4150;
	/*public static final int FEATURE_ID_TRAP_KNIGHT         = 4130;
	public static final int FEATURE_ID_TRAP_BISHOP         = 4140;
	public static final int FEATURE_ID_TRAP_ROOK           = 4150;
	public static final int FEATURE_ID_TRAP_QUEEN          = 4160;*/
	
	public static final int FEATURE_ID_PIN_KING            = 4210;
	public static final int FEATURE_ID_PIN_BIGGER_PIECE    = 4220;
	public static final int FEATURE_ID_PIN_EQUAL_PIECE     = 4230;
	public static final int FEATURE_ID_PIN_LOWER_PIECE     = 4240;
	public static final int FEATURE_ID_PIN_BK              = 4170;
	public static final int FEATURE_ID_PIN_BQ              = 4180;
	public static final int FEATURE_ID_PIN_BR              = 4190;
	public static final int FEATURE_ID_PIN_BN              = 4200;
	public static final int FEATURE_ID_PIN_RK              = 4210;
	public static final int FEATURE_ID_PIN_RQ              = 4220;
	public static final int FEATURE_ID_PIN_RB              = 4230;
	public static final int FEATURE_ID_PIN_RN              = 4240;
	public static final int FEATURE_ID_PIN_QK              = 4250;
	public static final int FEATURE_ID_PIN_QQ              = 4260;
	public static final int FEATURE_ID_PIN_QN              = 4270;
	public static final int FEATURE_ID_PIN_QR              = 4280;
	public static final int FEATURE_ID_PIN_QB              = 4290;
	
	public static final int FEATURE_ID_ATTACK_BIGGER_PIECE = 4310;
	public static final int FEATURE_ID_ATTACK_EQUAL_PIECE  = 4320;
	public static final int FEATURE_ID_ATTACK_LOWER_PIECE  = 4330;
	/*public static final int FEATURE_ID_ATTACK_BN           = 4300;
	public static final int FEATURE_ID_ATTACK_BR           = 4310;
	public static final int FEATURE_ID_ATTACK_NB           = 4320;
	public static final int FEATURE_ID_ATTACK_NR           = 4330;
	public static final int FEATURE_ID_ATTACK_NQ           = 4340;
	public static final int FEATURE_ID_ATTACK_RB           = 4350;
	public static final int FEATURE_ID_ATTACK_RN           = 4360;
	public static final int FEATURE_ID_ATTACK_QN           = 4370;
	public static final int FEATURE_ID_ATTACK_QB           = 4380;
	public static final int FEATURE_ID_ATTACK_QR           = 4390;*/
	
	public static final int FEATURE_ID_HUNGED_PIECES       = 4400;
	public static final int FEATURE_ID_HUNGED_PAWNS        = 4410;
	public static final int FEATURE_ID_HUNGED_ALL          = 4420;
	/*public static final int FEATURE_ID_HUNGED_PIECES_1     = 4400;
	public static final int FEATURE_ID_HUNGED_PIECES_2     = 4401;
	public static final int FEATURE_ID_HUNGED_PIECES_3     = 4402;
	public static final int FEATURE_ID_HUNGED_PIECES_4     = 4403;
	public static final int FEATURE_ID_HUNGED_PAWNS_1      = 4410;
	public static final int FEATURE_ID_HUNGED_PAWNS_2      = 4411;
	public static final int FEATURE_ID_HUNGED_PAWNS_3      = 4412;
	public static final int FEATURE_ID_HUNGED_PAWNS_4      = 4413;
	public static final int FEATURE_ID_HUNGED_ALL_1        = 4420;
	public static final int FEATURE_ID_HUNGED_ALL_2        = 4421;
	public static final int FEATURE_ID_HUNGED_ALL_3        = 4422;
	public static final int FEATURE_ID_HUNGED_ALL_4        = 4423;*/
	
	
	/**
	 * FIELDS STATES ITERATION
	 */
	public static final int FEATURE_ID_PST_CONTROL_EQ      = 5010;
	public static final int FEATURE_ID_PST_CONTROL_MORE    = 5020;
	
	
	/**
	 * Features which could be implemented:
	 * 
	 * HIGH PRIORITY
	 * -  Attacked pieces
	 * -  Knights outpost
	 * 
	 * LOW PRIORITY
	 * -  Connected bishop and queen
	 * -  King possition as a function of the pawns structure and/or passed pawns if any (for endgame only)
	 * -  Fiancheto bishop
	 * -  Fiancheto like caslting and pawn structure but missing bishop
	 * -  Drawing with opposite bishops and pawns only (this is not feature, should be implemented in the search alg)
	 * -  Moved F pawn before and after castling
	 * -  Missing G pawn before and after castling
	 * -  Traps - static (patterns like [bishop on h7 & pawn on g6])
	 * 
	 * IMPLEMENTED
	 * -  Pinned pieces (different cases - BNK, BNQ, BNR etc.)
	 * -  Colour to move - tempo
	 * -  Traps - dinamic (piece have 0 or 1 safe fields to go)
	 * -  Queens on 7th
	 * -  Reinsured pieces
	 * -  Hanging pieces (by overall count and by type)
	 * -  Safe attacks
	 * -  Passer stoppers - pieces which are in front of the passer
	 */
}
