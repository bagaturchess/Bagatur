package bagaturchess.learning.goldmiddle.impl.cfg.old3;

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

	
	/**
	 * PAWNS ITERATION
	 */
	public static final int FEATURE_ID_PAWNS_DOUBLED       = 10;
	public static final int FEATURE_ID_PAWNS_ISOLATED      = 11;
	public static final int FEATURE_ID_PAWNS_BACKWARD      = 12;
	public static final int FEATURE_ID_PAWNS_SUPPORTED     = 13;
	public static final int FEATURE_ID_PAWNS_CANNOTBS      = 14;
	public static final int FEATURE_ID_PAWNS_PASSED        = 15;
	public static final int FEATURE_ID_PAWNS_PASSED_RNK    = 16;
	public static final int FEATURE_ID_UNSTOPPABLE_PASSER  = 17;
	public static final int FEATURE_ID_PAWNS_CANDIDATE     = 18;
	public static final int FEATURE_ID_PAWNS_ISLANTS       = 19;
	
	public static final int FEATURE_ID_PAWNS_GARDS         = 20;
	public static final int FEATURE_ID_PAWNS_GARDS_REM     = 21;
	public static final int FEATURE_ID_PAWNS_STORMS        = 22;
	public static final int FEATURE_ID_PAWNS_STORMS_CLS    = 23;
	public static final int FEATURE_ID_PAWNS_OPENNED       = 24;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OWN    = 25;
	public static final int FEATURE_ID_PAWNS_SEMIOP_OP     = 26;
	public static final int FEATURE_ID_PAWNS_WEAK          = 27;
	public static final int FEATURE_ID_SPACE       		   = 28;
	
	/**
	 * PIECES ITERATION
	 */
	public static final int FEATURE_ID_PST_PAWN  		   = 29;
	public static final int FEATURE_ID_PST_KING  		   = 30;
	public static final int FEATURE_ID_PST_KNIGHT  	       = 31;
	public static final int FEATURE_ID_PST_BISHOP  		   = 32;
	public static final int FEATURE_ID_PST_ROOK  		   = 33;
	public static final int FEATURE_ID_PST_QUEEN  		   = 34;

	public static final int FEATURE_ID_BISHOPS_BAD		   = 35;
	public static final int FEATURE_ID_KNIGHTS_OUTPOST	   = 36;
	public static final int FEATURE_ID_ROOKS_OPENED		   = 37;
	public static final int FEATURE_ID_ROOKS_SEMIOPENED	   = 38;

	public static final int FEATURE_ID_TROPISM_KNIGHT  	   = 39;
	public static final int FEATURE_ID_TROPISM_BISHOP  	   = 40;
	public static final int FEATURE_ID_TROPISM_ROOK  	   = 41;
	public static final int FEATURE_ID_TROPISM_QUEEN  	   = 42;
	
	
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
