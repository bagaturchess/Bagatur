package bagaturchess.learning.goldmiddle.impl.cfg.old1;

public interface IFeaturesConstants {
	
	
	/**
	 * STANDARD
	 */
	public static final int FEATURE_ID_MATERIAL_PAWN       = 0;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     = 1;
	public static final int FEATURE_ID_MATERIAL_BISHOP     = 2;
	public static final int FEATURE_ID_MATERIAL_ROOK       = 3;
	public static final int FEATURE_ID_MATERIAL_QUEEN      = 4;
	
	public static final int FEATURE_ID_KINGSAFE_CASTLING   = 5;
	public static final int FEATURE_ID_KINGSAFE_FIANCHETTO = 6;
	
	public static final int FEATURE_ID_BISHOPS_DOUBLE      = 7;
	
	public static final int FEATURE_ID_KINGSAFE_F_PAWN     = 8;
	public static final int FEATURE_ID_KINGSAFE_G_PAWN     = 9;
	public static final int FEATURE_ID_KINGS_DISTANCE	   = 10;
	
	/**
	 * PAWNS ITERATION
	 */
	public static final int FEATURE_ID_PAWNS_DOUBLED       = 11;
	public static final int FEATURE_ID_PAWNS_ISOLATED      = 12;
	public static final int FEATURE_ID_PAWNS_BACKWARD      = 13;
	public static final int FEATURE_ID_PAWNS_SUPPORTED     = 14;
	public static final int FEATURE_ID_PAWNS_CANNOTBS      = 15;
	public static final int FEATURE_ID_PAWNS_PASSED        = 16;
	public static final int FEATURE_ID_PAWNS_PASSED_RNK    = 17;
	public static final int FEATURE_ID_UNSTOPPABLE_PASSER  = 18;
	public static final int FEATURE_ID_PAWNS_CANDIDATE     = 19;
	public static final int FEATURE_ID_PAWNS_ISLANTS       = 20;
	
	public static final int FEATURE_ID_PAWNS_GARDS         = 21;
	public static final int FEATURE_ID_PAWNS_GARDS_REM     = 22;
	public static final int FEATURE_ID_PAWNS_STORMS        = 23;
	public static final int FEATURE_ID_PAWNS_STORMS_CLS    = 24;
	public static final int FEATURE_ID_PAWNS_OPENNED       = 25;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OWN    = 26;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OP     = 27;
	public static final int FEATURE_ID_PAWNS_WEAK          = 28;
	public static final int FEATURE_ID_SPACE       		   = 29;
	public static final int FEATURE_ID_ROOK_INFRONT_PASSER = 30;
	public static final int FEATURE_ID_ROOK_BEHIND_PASSER  = 31;
	
	
	/**
	 * PIECES ITERATION
	 */
	public static final int FEATURE_ID_PST_PAWN  		   = 32;
	public static final int FEATURE_ID_PST_KING  		   = 33;
	public static final int FEATURE_ID_PST_KNIGHT  	       = 34;
	public static final int FEATURE_ID_PST_BISHOP  		   = 35;
	public static final int FEATURE_ID_PST_ROOK  		   = 36;
	public static final int FEATURE_ID_PST_QUEEN  		   = 37;

	public static final int FEATURE_ID_BISHOPS_BAD		   = 38;
	public static final int FEATURE_ID_KNIGHTS_OUTPOST	   = 39;
	public static final int FEATURE_ID_ROOKS_OPENED		   = 40;
	public static final int FEATURE_ID_ROOKS_SEMIOPENED	   = 41;

	public static final int FEATURE_ID_TROPISM_KNIGHT  	   = 42;
	public static final int FEATURE_ID_TROPISM_BISHOP  	   = 43;
	public static final int FEATURE_ID_TROPISM_ROOK  	   = 44;
	public static final int FEATURE_ID_TROPISM_QUEEN  	   = 45;
	
	public static final int FEATURE_ID_KING_PASSERS_F  	   = 46;
	public static final int FEATURE_ID_KING_PASSERS_FF 	   = 47;
	public static final int FEATURE_ID_KING_PASSERS_F_OP   = 48;
	
	
	/**
	 * MOVES ITERATION
	 */
	public static final int FEATURE_ID_KINGSAFE_L1     	   = 49;
	public static final int FEATURE_ID_KINGSAFE_L2     	   = 50;
	
	public static final int FEATURE_ID_MOBILITY_KNIGHT     = 51;
	public static final int FEATURE_ID_MOBILITY_BISHOP     = 52;
	public static final int FEATURE_ID_MOBILITY_ROOK       = 53;
	public static final int FEATURE_ID_MOBILITY_QUEEN      = 54;
	
	public static final int FEATURE_ID_ROOKS_PAIR_H        = 55;
	public static final int FEATURE_ID_ROOKS_PAIR_V        = 56;
	
	public static final int FEATURE_ID_ROOKS_7TH_2TH       = 57;
	public static final int FEATURE_ID_QUEENS_7TH_2TH      = 58;
	
	
	/**
	 * FIELDS STATES ITERATION
	 */
	public static final int FEATURE_ID_MOBILITY_KNIGHT_S   = 59;
	public static final int FEATURE_ID_MOBILITY_BISHOP_S   = 60;
	public static final int FEATURE_ID_MOBILITY_ROOK_S     = 61;
	public static final int FEATURE_ID_MOBILITY_QUEEN_S    = 62;
	public static final int FEATURE_ID_PST_CONTROL_EQ      = 63;
	public static final int FEATURE_ID_PST_CONTROL_MORE    = 64;
	
	
	
	public static final int FEATURE_ID_PIN_BK              = 65;
	public static final int FEATURE_ID_PIN_BQ              = 66;
	public static final int FEATURE_ID_PIN_BR              = 67;
	public static final int FEATURE_ID_PIN_BN              = 68;
	public static final int FEATURE_ID_PIN_RK              = 69;
	public static final int FEATURE_ID_PIN_RQ              = 70;
	public static final int FEATURE_ID_PIN_RB              = 71;
	public static final int FEATURE_ID_PIN_RN              = 72;
	public static final int FEATURE_ID_PIN_QK              = 73;
	public static final int FEATURE_ID_PIN_QQ              = 74;
	public static final int FEATURE_ID_PIN_QN              = 75;
	public static final int FEATURE_ID_PIN_QR              = 76;
	public static final int FEATURE_ID_PIN_QB              = 77;
	
	public static final int FEATURE_ID_ATTACK_BN           = 78;
	public static final int FEATURE_ID_ATTACK_BR           = 79;
	public static final int FEATURE_ID_ATTACK_NB           = 80;
	public static final int FEATURE_ID_ATTACK_NR           = 81;
	public static final int FEATURE_ID_ATTACK_NQ           = 82;
	public static final int FEATURE_ID_ATTACK_RB           = 83;
	public static final int FEATURE_ID_ATTACK_RN           = 84;
	public static final int FEATURE_ID_ATTACK_QN           = 85;
	public static final int FEATURE_ID_ATTACK_QB           = 86;
	public static final int FEATURE_ID_ATTACK_QR           = 87;
	
	public static final int FEATURE_ID_TRAP_KNIGHT         = 88;
	public static final int FEATURE_ID_TRAP_BISHOP         = 89;
	public static final int FEATURE_ID_TRAP_ROOK           = 90;
	public static final int FEATURE_ID_TRAP_QUEEN          = 91;
	
	public static final int FEATURE_ID_HUNGED_PIECES       = 92;
	
	
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
