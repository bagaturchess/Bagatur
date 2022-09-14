package bagaturchess.bitboard.impl1.internal;


public class EngineConstants {

	//@formatter:off
	
	public static final int MAX_MOVES 							= 768;
	public static final int MAX_PLIES 							= 2 * MAX_MOVES;
	public static final int MAX_THREADS							= 64;
	public static final boolean ENABLE_PONDERING				= true;
	public static final boolean GENERATE_BR_PROMOTIONS 			= true;

	public static final boolean ASSERT 							= false;

	public static final boolean TEST_EVAL_VALUES 				= false;
	public static final boolean TEST_EVAL_CACHES	 			= false;
	public static final boolean TEST_TT_VALUES 					= false;

	// Repetition-table
	public static final boolean ENABLE_REPETITION_TABLE 		= true;

	// TT values
	public static int POWER_2_TT_ENTRIES 						= 23;
	public static final boolean VERIFY_TT_MOVE 					= false;

	// Search improvements
	public static final boolean ENABLE_COUNTER_MOVES 			= true;
	public static final boolean ENABLE_KILLER_MOVES 			= true;
	public static final boolean ENABLE_HISTORY_HEURISTIC 		= true;
	public static final boolean ENABLE_ASPIRATION 				= true;
	public static final int ASPIRATION_WINDOW_DELTA 			= 20;
	public static final boolean ENABLE_IID 						= true;
	public static final int IID_REDUCTION 						= 1;

	// Search extensions
	public static final boolean ENABLE_CHECK_EXTENSION 			= true;
	public static final boolean ENABLE_ENDGAME_EXTENSION		= true;
	public static final int ENDGAME_EXTENSION_DEPTH				= 3;

	// Search reductions
	public static final boolean ENABLE_NULL_MOVE 				= true;
	public static final boolean ENABLE_LMR 						= true;
	public static final boolean ENABLE_LMR_STATS				= false;
	public static final boolean ENABLE_LMR_STATS_DECISION		= false;
	public static final boolean ENABLE_LMP 						= true;
	public static final boolean ENABLE_LMP_STATS_DECISION		= false;
	public static final boolean ENABLE_PVS 						= true;
	public static final boolean ENABLE_MATE_DISTANCE_PRUNING 	= true;
	public static final boolean ENABLE_STATIC_NULL_MOVE 		= true;
	public static final boolean ENABLE_RAZORING 				= true;
	public static final boolean ENABLE_FUTILITY_PRUNING 		= true;
	public static final boolean ENABLE_SEE_PRUNING 				= true;
	public static final boolean ENABLE_Q_PRUNE_BAD_CAPTURES 	= true;
	public static final boolean ENABLE_Q_FUTILITY_PRUNING 		= true;
	public static final boolean USE_TT_SCORE_AS_EVAL 			= true;
	public static final boolean USE_MOVE_SCORE_AS_EVAL 			= false;

	// Evaluation-function
	public static final boolean ENABLE_EVAL_CACHE				= true;
	public static final int POWER_2_EVAL_ENTRIES 				= 14;
	public static final boolean ENABLE_MATERIAL_CACHE			= true;
	public static final int POWER_2_MATERIAL_ENTRIES 			= 11;
	public static final boolean ENABLE_PAWN_EVAL_CACHE 			= true;
	public static final int POWER_2_PAWN_EVAL_ENTRIES 			= 13;
}
