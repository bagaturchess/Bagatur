package bagaturchess.engines.evaladapters.chess22k;

import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BLACK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.WHITE;


/**
 * Values have been tuned using the Texel's tuning method
 */
public class EvalConstants {
	//@formatter:off
	
	public static final int SIDE_TO_MOVE_BONUS = 16; //cannot be tuned //TODO lower in endgame
	public static final int IN_CHECK = 20;
	
	public static final int SCORE_DRAW 						= 0;
	public static final int SCORE_DRAWISH					= 10;
	public static final int SCORE_DRAWISH_KING_CORNERED		= 20;
	public static final int SCORE_MATE_BOUND 				= 30000;
	
	// other
	public static final int[] OTHER_SCORES = {-8, 12, 18, 8, 18, 12, 150, 12, 56};
	public static final int IX_ROOK_FILE_SEMI_OPEN	 		= 0;
	public static final int IX_ROOK_FILE_SEMI_OPEN_ISOLATED = 1;
	public static final int IX_ROOK_FILE_OPEN 				= 2;
	public static final int IX_ROOK_7TH_RANK 				= 3;
	public static final int IX_ROOK_BATTERY 				= 4;
	public static final int IX_BISHOP_LONG 					= 5;
	public static final int IX_BISHOP_PRISON 				= 6;
	public static final int IX_SPACE 						= 7;
	public static final int IX_DRAWISH 						= 8;
	
	// threats
	public static final int[] THREATS_MG = {38, 68, 100, 16, 56, 144, 66, 52, 8, 16, -6};
	public static final int[] THREATS_EG = {36, 10, -38, 16, 40, 156, 6, -12, 20, 4, 6};
	public static final int[] THREATS = new int[THREATS_MG.length];
	public static final int IX_MULTIPLE_PAWN_ATTACKS 		= 0;
	public static final int IX_PAWN_ATTACKS 				= 1;
	public static final int IX_QUEEN_ATTACKED 				= 2;
	public static final int IX_PAWN_PUSH_THREAT 			= 3;
	public static final int IX_NIGHT_FORK 					= 4;
	public static final int IX_NIGHT_FORK_KING 				= 5;
	public static final int IX_ROOK_ATTACKED 				= 6;
	public static final int IX_QUEEN_ATTACKED_MINOR			= 7;
	public static final int IX_MAJOR_ATTACKED				= 8;
	public static final int IX_UNUSED_OUTPOST				= 9;
	public static final int IX_PAWN_ATTACKED 				= 10;
	
	// pawn
	public static final int[] PAWN_SCORES = {6, 10, 12, 6};
	public static final int IX_PAWN_DOUBLE 					= 0;
	public static final int IX_PAWN_ISOLATED 				= 1;
	public static final int IX_PAWN_BACKWARD 				= 2;
	public static final int IX_PAWN_INVERSE					= 3;
	
	// imbalance
	public static final int[] IMBALANCE_SCORES = {32, 54, 16};
	public static final int IX_ROOK_PAIR		 			= 0;
	public static final int IX_BISHOP_DOUBLE 				= 1;
	public static final int IX_QUEEN_NIGHT 					= 2;
	
	public static final int[] PHASE 					= {0, 0, 6, 6, 13, 28};
	
	public static final int[] MATERIAL 					= {0, 100, 396, 416, 706, 1302, 3000};
	public static final int[] PINNED 					= {0, -2, 14, 42, 72, 88};
	public static final int[] PINNED_ATTACKED			= {0, 28, 128, 274, 330, 210};
	public static final int[] DISCOVERED		 		= {0, -14, 128, 110, 180, 0, 28};
	public static final int[] KNIGHT_OUTPOST			= {0, 0, 10, 26, 24, 36, 8, 38};
	public static final int[] BISHOP_OUTPOST			= {0, 0, 22, 22, 20, 22, 52, 50};
	public static final int[] DOUBLE_ATTACKED 			= {0, 16, 34, 64, -4, -6, 0};
	public static final int[] HANGING 					= {0, 16, 6, 0, -10, -18, 48}; //qsearch could set the other in check
	public static final int[] HANGING_2 				= {0, 38, 90, 94, 52, -230};
	public static final int[] ROOK_TRAPPED 				= {64, 62, 28};
	public static final int[] ONLY_MAJOR_DEFENDERS 		= {0, 6, 14, 24, 4, 10, 0};
	public static final int[] NIGHT_PAWN				= {68, -14, -2, 2, 8, 12, 20, 30, 36};
	public static final int[] ROOK_PAWN					= {48, -4, -4, -4, -4, 0, 0, 0, 0};
	public static final int[] BISHOP_PAWN 				= {-20, -8, -6, 0, 6, 12, 22, 32, 46};
	public static final int[] SPACE 					= {0, 0, 0, 0, 0, -6, -6, -8, -7, -4, -4, -2, 0, -1, 0, 3, 7};
	
	public static final int[] PAWN_BLOCKAGE 			= {0, 0, -10, 2, 6, 28, 66, 196};
	public static final int[] PAWN_CONNECTED			= {0, 0, 12, 14, 20, 58, 122};
	public static final int[] PAWN_NEIGHBOUR	 		= {0, 0, 4, 10, 26, 88, 326};
	
	public static final int[][] SHIELD_BONUS_MG			= {	{0, 18, 14, 4, -24, -38, -270},
															{0, 52, 36, 6, -44, 114, -250},
															{0, 52, 4, 4, 46, 152, 16},
															{0, 16, 4, 6, -16, 106, 2}};
	public static final int[][] SHIELD_BONUS_EG			= {	{0, -48, -18, -16, 8, -30, -28},
															{0, -16, -26, -10, 42, 6, 20},
															{0, 0, 8, 0, 28, 24, 38},
															{0, -22, -14, 0, 38, 10, 60}};
	public static final int[][] SHIELD_BONUS 			= new int[4][7];

	public static final int[] PASSED_SCORE_MG			= {0, -4, -2, 0, 18, 22, -6};
	public static final int[] PASSED_SCORE_EG			= {0, 18, 18, 38, 62, 136, 262};
	
	public static final int[] PASSED_CANDIDATE			= {0, 2, 2, 8, 14, 40};
	
	public static final float[] PASSED_KING_MULTI 		= {0, 1.4f, 1.3f, 1.1f, 1.1f, 1.0f, 0.8f, 0.8f};														
	public static final float[] PASSED_MULTIPLIERS	= {
			0.5f,	// blocked
			1.2f,	// next square attacked
			0.4f,	// enemy king in front
			1.2f,	// next square defended
			0.7f,	// attacked
			1.6f,	// defended by rook from behind
			0.6f,	// attacked by rook from behind
			1.7f	// no enemy attacks in front
	};	
	
	//concept borrowed from Ed Schroder
	public static final int[] KS_SCORES = { //TODO negative values? //TODO first values are not used
			0, 0, 0, 0, -140, -150, -120, -90, -40, 40, 70, 
			80, 100, 110, 130, 160, 200, 230, 290, 330, 400, 
			480, 550, 630, 660, 700, 790, 860, 920, 1200 };
	public static final int[] KS_QUEEN_TROPISM 		= {0, 0, 1, 1, 1, 1, 0, 0};	// index 0 and 1 are never evaluated	
	public static final int[] KS_RANK 				= {0, 0, 1, 1, 0, 0, 0, 0};
	public static final int[] KS_CHECK				= {0, 0, 3, 2, 3};
	public static final int[] KS_UCHECK				= {0, 0, 1, 1, 1};
	public static final int[] KS_CHECK_QUEEN 		= {0, 0, 0, 0, 2, 3, 4, 4, 4, 4, 3, 3, 3, 2, 1, 1, 0};
	public static final int[] KS_NO_FRIENDS 		= {6, 4, 0, 5, 5, 5, 6, 6, 7, 8, 9, 9};
	public static final int[] KS_ATTACKS 			= {0, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 2, 9};
	public static final int[] KS_DOUBLE_ATTACKS 	= {0, 1, 3, 5, 2, -8, 0, 0, 0};
	public static final int[] KS_ATTACK_PATTERN		= {	
		 //                                                 Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  
		 // 	                    R  R  R  R  R  R  R  R                          R  R  R  R  R  R  R  R  
		 //             B  B  B  B              B  B  B  B              B  B  B  B              B  B  B  B  
		 //       N  N        N  N        N  N        N  N        N  N        N  N        N  N        N  N  
		 //    P     P     P     P     P     P     P     P     P     P     P     P     P     P     P     P
			4, 1, 2, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 3, 3, 1, 2, 3, 3, 3, 3, 4, 4
	};
	
	public static final int[] KS_OTHER	= {		
			3,		// queen-touch check
			4,		// king at blocked first rank check
			1		// open file
	};		
		
	public static final int[] MOBILITY_KNIGHT_MG	= {-34, -16, -6, 0, 12, 16, 26, 28, 56};
	public static final int[] MOBILITY_KNIGHT_EG	= {-98, -34, -12, 0, 4, 12, 12, 14, 4};
	public static final int[] MOBILITY_BISHOP_MG	= {-16, 2, 16, 24, 28, 36, 38, 40, 36, 42, 58, 82, 28, 120};
	public static final int[] MOBILITY_BISHOP_EG	= {-36, -8, 6, 18, 28, 28, 36, 38, 42, 40, 32, 34, 54, 32};
	public static final int[] MOBILITY_ROOK_MG 		= {-34, -24, -18, -14, -12, -4, 0, 8, 16, 26, 30, 40, 52, 68, 66};
	public static final int[] MOBILITY_ROOK_EG 		= {-38, -12, 0, 8, 18, 24, 28, 28, 34, 34, 38, 40, 40, 42, 46};
	public static final int[] MOBILITY_QUEEN_MG		= {-12, -14, -10, -14, -8, -6, -8, -8, -6, -4, -2, -2, -4, 2, 0, 0, 2, 16, 8, 22, 32, 66, 48, 156, 172, 236, 68, 336};
	public static final int[] MOBILITY_QUEEN_EG 	= {-28, -82, -102, -82, -76, -54, -40, -24, -10, -2, 8, 24, 30, 32, 38, 54, 60, 46, 70, 72, 66, 66, 52, 18, -8, -32, 64, -94};
	public static final int[] MOBILITY_KING_MG		= {-10, -12, -8, 0, 10, 26, 36, 70, 122};
	public static final int[] MOBILITY_KING_EG		= {-38, -2, 8, 8, 2, -12, -12, -26, -60};
	public static final int[] MOBILITY_KNIGHT		= new int[MOBILITY_KNIGHT_MG.length];
	public static final int[] MOBILITY_BISHOP		= new int[MOBILITY_BISHOP_MG.length];
	public static final int[] MOBILITY_ROOK			= new int[MOBILITY_ROOK_MG.length];
	public static final int[] MOBILITY_QUEEN		= new int[MOBILITY_QUEEN_MG.length];
	public static final int[] MOBILITY_KING			= new int[MOBILITY_KING_MG.length];
		
	/** piece, color, square */
	public static final int[][][] PSQT				= new int[7][2][64];
	public static final int[][][] PSQT_MG			= new int[7][2][64];
	public static final int[][][] PSQT_EG			= new int[7][2][64];
	
	static
	{	
		PSQT_MG[ChessConstants.PAWN][WHITE] = new int[] {
				   0,  0,  0,  0,  0,  0,  0,  0,
				   164,156,174,218,218,174,156,164,
				    12, 26, 66, 58, 58, 66, 26, 12,
				   -16, -4, -2, 14, 14, -2, -4,-16,
				   -32,-28,-12,  2,  2,-12,-28,-32,
				   -30,-20,-16,-16,-16,-16,-20,-30,
				   -24,  4,-16,-10,-10,-16,  4,-24,
				     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		PSQT_EG[ChessConstants.PAWN][WHITE] = new int[] {
				   0,  0,  0,  0,  0,  0,  0,  0,
				   -44,-34,-50,-60,-60,-50,-34,-44,
				    32, 16, -8,-22,-22, -8, 16, 32,
				    28, 14,  6, -4, -4,  6, 14, 28,
				    16, 12,  2, -2, -2,  2, 12, 16,
				     6,  4,  4, 10, 10,  4,  4,  6,
				    16,  4, 14, 22, 22, 14,  4, 16,
				     0,  0,  0,  0,  0,  0,  0,  0
		};
		
		PSQT_MG[ChessConstants.NIGHT][WHITE] = new int[]{	
				 -214,-112,-130,-34,-34,-130,-112,-214,
				 -80,-62,  2,-34,-34,  2,-62,-80,
				 -24, 44, 18, 36, 36, 18, 44,-24,
				  18, 42, 38, 50, 50, 38, 42, 18,
				   8, 36, 36, 36, 36, 36, 36,  8,
				   8, 38, 34, 40, 40, 34, 38,  8,
				   0,  0, 24, 36, 36, 24,  0,  0,
				 -30,  6, -4, 20, 20, -4,  6,-30
		};
		
		PSQT_EG[ChessConstants.NIGHT][WHITE] = new int[]{	
				 -16,  2, 32, 18, 18, 32,  2,-16,
				   2, 28, 12, 40, 40, 12, 28,  2,
				  -6,  6, 32, 28, 28, 32,  6, -6,
				  16, 20, 38, 42, 42, 38, 20, 16,
				  10, 18, 30, 40, 40, 30, 18, 10,
				   6,  8, 16, 30, 30, 16,  8,  6,
				 -10,  8,  6, 14, 14,  6,  8,-10,
				 -10,  2,  8, 16, 16,  8,  2,-10
		};
		
		PSQT_MG[ChessConstants.BISHOP][WHITE] = new int[] {
				 -18, 12,-92,-84,-84,-92, 12,-18,
				 -44, -8,  0,-18,-18,  0, -8,-44,
				  40, 48, 42, 34, 34, 42, 48, 40,
				  28, 34, 40, 58, 58, 40, 34, 28,
				  28, 34, 36, 62, 62, 36, 34, 28,
				  36, 54, 50, 40, 40, 50, 54, 36,
				  36, 60, 48, 42, 42, 48, 60, 36,
				   8, 32, 30, 48, 48, 30, 32,  8
		};
		
		PSQT_EG[ChessConstants.BISHOP][WHITE] = new int[]{	
				 -34,-18, -6,  0,  0, -6,-18,-34,
				  -4,-18, -4, -6, -6, -4,-18, -4,
				 -14,-12,-18, -8, -8,-18,-12,-14,
				  -6, -6,  0,  2,  2,  0, -6, -6,
				 -22,-12, -6, -6, -6, -6,-12,-22,
				 -20,-16,-18,  0,  0,-18,-16,-20,
				 -32,-40,-24,-12,-12,-24,-40,-32,
				 -36,-18,-12,-18,-18,-12,-18,-36
		};
		
		PSQT_MG[ChessConstants.ROOK][WHITE] = new int[] {
				 -36,-26,-72, -4, -4,-72,-26,-36,
				 -36,-20, 14, 22, 22, 14,-20,-36,
				 -28,  2, -2, -8, -8, -2,  2,-28,
				 -40,-22, 10,  6,  6, 10,-22,-40,
				 -44,-14,-22,  2,  2,-22,-14,-44,
				 -38,-12, -2, -4, -4, -2,-12,-38,
				 -48, -2, -6, 10, 10, -6, -2,-48,
				 -10,-12,  0, 14, 14,  0,-12,-10
		};
		
		PSQT_EG[ChessConstants.ROOK][WHITE] = new int[]{	
				  44, 46, 64, 46, 46, 64, 46, 44,
				  40, 40, 32, 24, 24, 32, 40, 40,
				  36, 36, 34, 34, 34, 34, 36, 36,
				  42, 38, 40, 34, 34, 40, 38, 42,
				  34, 32, 36, 24, 24, 36, 32, 34,
				  22, 26, 14, 14, 14, 14, 26, 22,
				  18,  6, 10, 10, 10, 10,  6, 18,
				   6, 16, 12,  2,  2, 12, 16,  6
		};
		
		PSQT_MG[ChessConstants.QUEEN][WHITE] = new int[] {
				 -72,-44,-78,-66,-66,-78,-44,-72,
				 -38,-88,-70,-82,-82,-70,-88,-38,
				  -8,-38,-44,-64,-64,-44,-38, -8,
				 -38,-44,-46,-60,-60,-46,-44,-38,
				 -24,-34,-22,-30,-30,-22,-34,-24,
				  -8, 10,-14,-10,-10,-14, 10, -8,
				  -4, 12, 26, 20, 20, 26, 12, -4,
				  14,  6, 12, 24, 24, 12,  6, 14
		};
		
		PSQT_EG[ChessConstants.QUEEN][WHITE] = new int[]{	
				  32, 14, 46, 38, 38, 46, 14, 32,
				   6, 22, 16, 48, 48, 16, 22,  6,
				  -4,  6, 12, 42, 42, 12,  6, -4,
				  36, 30, 14, 34, 34, 14, 30, 36,
				  14, 22,  4, 22, 22,  4, 22, 14,
				   6,-36,  0, -6, -6,  0,-36,  6,
				 -26,-42,-40,-18,-18,-40,-42,-26,
				 -42,-36,-32,-30,-30,-32,-36,-42
		};
		
		PSQT_MG[ChessConstants.KING][WHITE] = new int[] {
				 -50,180,-26,  2,  2,-26,180,-50,
				  48,-10,-64,-24,-24,-64,-10, 48,
				  42, 52, 44,-44,-44, 44, 52, 42,
				 -40,-22,-46,-92,-92,-46,-22,-40,
				 -40,-20,-20,-76,-76,-20,-20,-40,
				  14, 16,  2,-10,-10,  2, 16, 14,
				  30, 10,-40,-56,-56,-40, 10, 30,
				  34, 44,  8, 18, 18,  8, 44, 34
		};
		
		PSQT_EG[ChessConstants.KING][WHITE] = new int[] {
				 -90,-90, -2,-56,-56, -2,-90,-90,
				 -34, 14, 40, 24, 24, 40, 14,-34,
				  -8, 32, 36, 38, 38, 36, 32, -8,
				   0, 38, 44, 50, 50, 44, 38,  0,
				 -12, 14, 28, 44, 44, 28, 14,-12,
				 -18,  8, 18, 24, 24, 18,  8,-18,
				 -40, -4, 16, 24, 24, 16, -4,-40,
				 -74,-44,-24,-34,-34,-24,-44,-74
		};
		
	}
	
	public static final long[] ROOK_PRISON = { 
			0, Evaluator_BaseImpl.A8, Evaluator_BaseImpl.A8_B8, Evaluator_BaseImpl.A8B8C8, 0, Evaluator_BaseImpl.G8_H8, Evaluator_BaseImpl.H8, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, Evaluator_BaseImpl.A1, Evaluator_BaseImpl.A1_B1, Evaluator_BaseImpl.A1B1C1, 0, Evaluator_BaseImpl.G1_H1, Evaluator_BaseImpl.H1, 0 
	};
	
	public static final long[] BISHOP_PRISON = { 
			0, 0, 0, 0, 0, 0, 0, 0, //8
			Evaluator_BaseImpl.B6_C7, 0, 0, 0, 0, 0, 0, Evaluator_BaseImpl.G6_F7, //7
			0, 0, 0, 0, 0, 0, 0, 0, //6
			0, 0, 0, 0, 0, 0, 0, 0, //5
			0, 0, 0, 0, 0, 0, 0, 0, //4
			0, 0, 0, 0, 0, 0, 0, 0, //3
			Evaluator_BaseImpl.B3_C2, 0, 0, 0, 0, 0, 0, Evaluator_BaseImpl.G3_F2, //2
			0, 0, 0, 0, 0, 0, 0, 0  //1
		 // A  B  C  D  E  F  G  H
	};
	
	public static final int[] PROMOTION_SCORE = {
			0,
			0,
			MATERIAL[ChessConstants.NIGHT] 	- MATERIAL[ChessConstants.PAWN],
			MATERIAL[ChessConstants.BISHOP] - MATERIAL[ChessConstants.PAWN],
			MATERIAL[ChessConstants.ROOK] 	- MATERIAL[ChessConstants.PAWN],
			MATERIAL[ChessConstants.QUEEN] 	- MATERIAL[ChessConstants.PAWN],
	};
	
	
	public static void initMgEg() {
		initMgEg(MOBILITY_KNIGHT,	MOBILITY_KNIGHT_MG,	MOBILITY_KNIGHT_EG);
		initMgEg(MOBILITY_BISHOP, 	MOBILITY_BISHOP_MG, MOBILITY_BISHOP_EG);
		initMgEg(MOBILITY_ROOK,		MOBILITY_ROOK_MG,	MOBILITY_ROOK_EG);
		initMgEg(MOBILITY_QUEEN,	MOBILITY_QUEEN_MG,	MOBILITY_QUEEN_EG);
		initMgEg(MOBILITY_KING,		MOBILITY_KING_MG,	MOBILITY_KING_EG);
		initMgEg(THREATS,			THREATS_MG,			THREATS_EG);
		
		for (int i = 0; i < 4; i++) {
			initMgEg(SHIELD_BONUS[i], SHIELD_BONUS_MG[i], SHIELD_BONUS_EG[i]);
		}
		
		for (int color = WHITE; color <= BLACK; color++) {
			for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
				initMgEg(PSQT[piece][color], PSQT_MG[piece][color], PSQT_EG[piece][color]);
			}
		}
	}

	private static void initMgEg(int[] array, int[] arrayMg, int[] arrayEg) {
		for(int i = 0; i < array.length; i++) {
			array[i] = Evaluator.score(arrayMg[i], arrayEg[i]);
		}
	}
	
	public static final int[] MIRRORED_LEFT_RIGHT = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_LEFT_RIGHT[i] = (i / 8) * 8 + 7 - (i & 7);
		}
	}

	public static final int[] MIRRORED_UP_DOWN = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_UP_DOWN[i] = (7 - i / 8) * 8 + (i & 7);
		}
	}
	
	static {
		
		// fix white arrays
		for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++){
			Util.reverse(PSQT_MG[piece][ChessConstants.WHITE]);
			Util.reverse(PSQT_EG[piece][ChessConstants.WHITE]);
		}

		// create black arrays
		for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++){
			for (int i = 0; i < 64; i++) {
				PSQT_MG[piece][BLACK][i] = -PSQT_MG[piece][WHITE][MIRRORED_UP_DOWN[i]];
				PSQT_EG[piece][BLACK][i] = -PSQT_EG[piece][WHITE][MIRRORED_UP_DOWN[i]];
			}
		}
		
		Util.reverse(ROOK_PRISON);
		Util.reverse(BISHOP_PRISON);
		
		initMgEg();
	}
	
	public static void main(String[] args) {
		//increment a psqt with a constant
		for(int i=0; i<64; i++) {
			PSQT_EG[ChessConstants.KING][WHITE][i]+=20;
		}
		//System.out.println(PsqtTuning.getArrayFriendlyFormatted(PSQT_EG[ChessConstants.KING][WHITE]));
	}

}
