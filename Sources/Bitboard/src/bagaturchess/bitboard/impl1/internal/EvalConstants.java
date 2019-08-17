package bagaturchess.bitboard.impl1.internal;

import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import bagaturchess.bitboard.api.IBoardConfig;


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
	//public static final int[][][] PSQT				= new int[7][2][64];
	public static final int[][][] PSQT_MG			= new int[7][2][64];
	public static final int[][][] PSQT_EG			= new int[7][2][64];
	
	
	static
	{	
		PSQT_MG[ChessConstants.PAWN][WHITE] = new int[] {
				0, 0, 0, 0, 0, 0, 0, 0, 
				-2, 20, -10, -2, -2, -10, 20, -2, 
				-11, -12, -2, 4, 4, -2, -12, -11, 
				-5, -2, -1, 12, 12, -1, -2, -5, 
				-14, -7, 20, 24, 24, 20, -7, -14, 
				-16, -3, 23, 23, 23, 23, -3, -16, 
				-11, 7, 7, 17, 17, 7, 7, -11, 
				0, 0, 0, 0, 0, 0, 0, 0, 
		};
		
		PSQT_EG[ChessConstants.PAWN][WHITE] = new int[] {
				 0, 0, 0, 0, 0, 0, 0, 0, 
				 1, -12, 6, 25, 25, 6, -12, 1, 
				 16, 6, 1, 16, 16, 1, 6, 16, 
				 13, 10, -1, -8, -8, -1, 10, 13, 
				 7, -4, -8, 2, 2, -8, -4, 7, 
				 -2, 2, 6, -1, -1, 6, 2, -2, 
				 -3, -1, 7, 2, 2, 7, -1, -3, 
				 0, 0, 0, 0, 0, 0, 0, 0, 
		};
		
		PSQT_MG[ChessConstants.NIGHT][WHITE] = new int[]{	
				-200, -80, -53, -32, -32, -53, -80, -200, 
				-67, -21, 6, 37, 37, 6, -21, -67, 
				-11, 28, 63, 55, 55, 63, 28, -11, 
				-29, 13, 42, 52, 52, 42, 13, -29, 
				-28, 5, 41, 47, 47, 41, 5, -28, 
				-64, -20, 4, 19, 19, 4, -20, -64, 
				-79, -39, -24, -9, -9, -24, -39, -79, 
				-169, -96, -80, -79, -79, -80, -96, -169, 
		};
		
		PSQT_EG[ChessConstants.NIGHT][WHITE] = new int[]{	
				-98, -89, -53, -16, -16, -53, -89, -98, 
				-64, -45, -37, 16, 16, -37, -45, -64, 
				-51, -38, -17, 19, 19, -17, -38, -51, 
				-41, -20, 4, 35, 35, 4, -20, -41, 
				-36, 0, 13, 34, 34, 13, 0, -36, 
				-38, -33, -5, 27, 27, -5, -33, -38, 
				-70, -56, -15, 6, 6, -15, -56, -70, 
				-105, -74, -46, -18, -18, -46, -74, -105, 
		};
		
		PSQT_MG[ChessConstants.BISHOP][WHITE] = new int[] {
				-47, -7, -17, -29, -29, -17, -7, -47, 
				-19, -13, 7, -11, -11, 7, -13, -19, 
				-17, 14, -6, 6, 6, -6, 14, -17, 
				-8, 27, 13, 30, 30, 13, 27, -8, 
				4, 9, 18, 40, 40, 18, 9, 4, 
				-9, 22, -3, 12, 12, -3, 22, -9, 
				-24, 9, 15, 1, 1, 15, 9, -24, 
				-49, -7, -10, -34, -34, -10, -7, -49, 
		};
		
		PSQT_EG[ChessConstants.BISHOP][WHITE] = new int[]{	
				-55, -32, -36, -17, -17, -36, -32, -55, 
				-34, -10, -12, 6, 6, -12, -10, -34, 
				-24, -2, 0, 13, 13, 0, -2, -24, 
				-26, -4, -7, 14, 14, -7, -4, -26, 
				-26, -3, -5, 16, 16, -5, -3, -26, 
				-23, 0, -3, 16, 16, -3, 0, -23, 
				-34, -9, -14, 4, 4, -14, -9, -34, 
				-58, -31, -37, -19, -19, -37, -31, -58, 
		};
		
		PSQT_MG[ChessConstants.ROOK][WHITE] = new int[] {
				-25, -18, -11, 2, 2, -11, -18, -25, 
				-11, 8, 9, 12, 12, 9, 8, -11, 
				-23, -10, 1, 6, 6, 1, -10, -23, 
				-21, -12, -1, 4, 4, -1, -12, -21, 
				-21, -7, -4, -4, -4, -4, -7, -21, 
				-19, -10, 1, 0, 0, 1, -10, -19, 
				-18, -5, -1, 1, 1, -1, -5, -18, 
				-24, -15, -8, 0, 0, -8, -15, -24, 
		};
		
		PSQT_EG[ChessConstants.ROOK][WHITE] = new int[]{	
				6, 4, 6, 2, 2, 6, 4, 6, 
				-1, 7, 11, -1, -1, 11, 7, -1, 
				3, 2, -1, 3, 3, -1, 2, 3, 
				-7, 5, -5, -7, -7, -5, 5, -7, 
				0, 4, -2, 1, 1, -2, 4, 0, 
				6, -7, 3, 3, 3, 3, -7, 6, 
				-7, -5, -5, -1, -1, -5, -5, -7, 
				0, 3, 0, 3, 3, 0, 3, 0, 
		};
		
		PSQT_MG[ChessConstants.QUEEN][WHITE] = new int[] {
				-2, -2, 1, -2, -2, 1, -2, -2, 
				-5, 6, 10, 8, 8, 10, 6, -5, 
				-4, 10, 6, 8, 8, 6, 10, -4, 
				0, 14, 12, 5, 5, 12, 14, 0, 
				4, 5, 9, 8, 8, 9, 5, 4, 
				-3, 6, 13, 7, 7, 13, 6, -3, 
				-3, 5, 8, 12, 12, 8, 5, -3, 
				3, -5, -5, 4, 4, -5, -5, 3, 
		};
		
		PSQT_EG[ChessConstants.QUEEN][WHITE] = new int[]{	
				-75, -52, -43, -36, -36, -43, -52, -75, 
				-50, -27, -24, -8, -8, -24, -27, -50, 
				-38, -18, -12, 1, 1, -12, -18, -38, 
				-29, -6, 9, 21, 21, 9, -6, -29, 
				-23, -3, 13, 24, 24, 13, -3, -23, 
				-39, -18, -9, 3, 3, -9, -18, -39, 
				-55, -31, -22, -4, -4, -22, -31, -55, 
				-69, -57, -47, -26, -26, -47, -57, -69, 
		};
		
		PSQT_MG[ChessConstants.KING][WHITE] = new int[] {
				64, 87, 49, 0, 0, 49, 87, 64, 
				87, 120, 64, 25, 25, 64, 120, 87, 
				122, 159, 85, 36, 36, 85, 159, 122, 
				145, 176, 112, 69, 69, 112, 176, 145, 
				169, 191, 136, 108, 108, 136, 191, 169, 
				198, 253, 168, 120, 120, 168, 253, 198, 
				277, 305, 241, 183, 183, 241, 305, 277, 
				272, 325, 273, 190, 190, 273, 325, 272, 
		};
		
		PSQT_EG[ChessConstants.KING][WHITE] = new int[] {
				5, 60, 75, 75, 75, 75, 60, 5, 
				40, 99, 128, 141, 141, 128, 99, 40, 
				87, 164, 174, 189, 189, 174, 164, 87, 
				98, 166, 197, 194, 194, 197, 166, 98, 
				103, 152, 168, 169, 169, 168, 152, 103, 
				86, 138, 165, 173, 173, 165, 138, 86, 
				57, 98, 138, 131, 131, 138, 98, 57, 
				0, 41, 80, 93, 93, 80, 41, 0, 
		};
	}
	
	
	public static final void initPSQT(IBoardConfig config) {
		
		PSQT_MG[ChessConstants.PAWN][WHITE] = convertDoubleArray2IntArray(config.getPST_PAWN_O());
		PSQT_EG[ChessConstants.PAWN][WHITE] = convertDoubleArray2IntArray(config.getPST_PAWN_E());
		
		PSQT_MG[ChessConstants.NIGHT][WHITE] = convertDoubleArray2IntArray(config.getPST_KNIGHT_O());
		PSQT_EG[ChessConstants.NIGHT][WHITE] = convertDoubleArray2IntArray(config.getPST_KNIGHT_E());

		PSQT_MG[ChessConstants.BISHOP][WHITE] = convertDoubleArray2IntArray(config.getPST_BISHOP_O());
		PSQT_EG[ChessConstants.BISHOP][WHITE] = convertDoubleArray2IntArray(config.getPST_BISHOP_E());

		PSQT_MG[ChessConstants.ROOK][WHITE] = convertDoubleArray2IntArray(config.getPST_ROOK_O());
		PSQT_EG[ChessConstants.ROOK][WHITE] = convertDoubleArray2IntArray(config.getPST_ROOK_E());
		
		PSQT_MG[ChessConstants.QUEEN][WHITE] = convertDoubleArray2IntArray(config.getPST_QUEEN_O());
		PSQT_EG[ChessConstants.QUEEN][WHITE] = convertDoubleArray2IntArray(config.getPST_QUEEN_E());
		
		PSQT_MG[ChessConstants.KING][WHITE] = convertDoubleArray2IntArray(config.getPST_KING_O());
		PSQT_EG[ChessConstants.KING][WHITE] = convertDoubleArray2IntArray(config.getPST_KING_E());
		
		// create black arrays
		for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++){
			for (int i = 0; i < 64; i++) {
				PSQT_MG[piece][BLACK][i] = -PSQT_MG[piece][WHITE][MIRRORED_UP_DOWN[i]];
				PSQT_EG[piece][BLACK][i] = -PSQT_EG[piece][WHITE][MIRRORED_UP_DOWN[i]];
			}
		}
	}
	
	
	private static final int[] convertDoubleArray2IntArray(double[] src) {
		int[] result = new int[src.length];
		for (int i=0; i<result.length; i++) {
			result[i] = (int) src[i];
		}
		return result;
	}
	
	
	public static final long[] ROOK_PRISON = { 
			0, Bitboard.A8, Bitboard.A8_B8, Bitboard.A8B8C8, 0, Bitboard.G8_H8, Bitboard.H8, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, Bitboard.A1, Bitboard.A1_B1, Bitboard.A1B1C1, 0, Bitboard.G1_H1, Bitboard.H1, 0 
	};
	
	
	public static final long[] BISHOP_PRISON = { 
			0, 0, 0, 0, 0, 0, 0, 0, //8
			Bitboard.B6_C7, 0, 0, 0, 0, 0, 0, Bitboard.G6_F7, //7
			0, 0, 0, 0, 0, 0, 0, 0, //6
			0, 0, 0, 0, 0, 0, 0, 0, //5
			0, 0, 0, 0, 0, 0, 0, 0, //4
			0, 0, 0, 0, 0, 0, 0, 0, //3
			Bitboard.B3_C2, 0, 0, 0, 0, 0, 0, Bitboard.G3_F2, //2
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
		
		/*for (int color = WHITE; color <= BLACK; color++) {
			for (int piece = ChessConstants.PAWN; piece <= ChessConstants.KING; piece++) {
				initMgEg(PSQT[piece][color], PSQT_MG[piece][color], PSQT_EG[piece][color]);
			}
		}*/
	}

	private static void initMgEg(int[] array, int[] arrayMg, int[] arrayEg) {
		for(int i = 0; i < array.length; i++) {
			array[i] = (arrayMg[i] + arrayEg[i]) / 2;
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
