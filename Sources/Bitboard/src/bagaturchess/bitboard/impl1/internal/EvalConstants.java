package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import bagaturchess.bitboard.api.IBoardConfig;


public class EvalConstants {
	
	public static final int SIDE_TO_MOVE_BONUS 				= 0;
	
	public static final int SCORE_DRAW 						= 0;
	
	// other
	public static final int[] OTHER_SCORES = {-8, 16, 18, 8, 18, 12, -158, 12, 56, 20, -44, 28, -8};
	public static final int IX_ROOK_FILE_SEMI_OPEN	 		= 0;
	public static final int IX_ROOK_FILE_SEMI_OPEN_ISOLATED = 1;
	public static final int IX_ROOK_FILE_OPEN 				= 2;
	public static final int IX_ROOK_7TH_RANK 				= 3;
	public static final int IX_ROOK_BATTERY 				= 4;
	public static final int IX_BISHOP_LONG 					= 5;
	public static final int IX_BISHOP_PRISON 				= 6;
	public static final int IX_SPACE 						= 7;
	public static final int IX_DRAWISH 						= 8;
	public static final int IX_CASTLING						= 9;
	public static final int IX_ROOK_TRAPPED					= 10;
	public static final int IX_OUTPOST						= 11;
	public static final int IX_ONLY_MAJOR_DEFENDERS			= 12;
	
	// threats
	public static final int[] THREATS_MG = {34, 68, 108, 12, 66, 52, 8, 16, -6};
	public static final int[] THREATS_EG = {32, 14, -38, 16, 10, -12, 24, 4, 10};
	public static final int IX_MULTIPLE_PAWN_ATTACKS 		= 0;
	public static final int IX_PAWN_ATTACKS 				= 1;
	public static final int IX_QUEEN_ATTACKED 				= 2;
	public static final int IX_PAWN_PUSH_THREAT 			= 3;
	public static final int IX_ROOK_ATTACKED 				= 4;
	public static final int IX_QUEEN_ATTACKED_MINOR			= 5;
	public static final int IX_MAJOR_ATTACKED				= 6;
	public static final int IX_UNUSED_OUTPOST				= 7;
	public static final int IX_PAWN_ATTACKED 				= 8;
	
	// pawn
	public static final int[] PAWN_SCORES = {10, 10, 12, 6};
	public static final int IX_PAWN_DOUBLE 					= 0;
	public static final int IX_PAWN_ISOLATED 				= 1;
	public static final int IX_PAWN_BACKWARD 				= 2;
	public static final int IX_PAWN_INVERSE					= 3;
	
	// imbalance
	public static final int[] IMBALANCE_SCORES = {-28, 54, 20};
	public static final int IX_ROOK_PAIR		 			= 0;
	public static final int IX_BISHOP_DOUBLE 				= 1;
	public static final int IX_QUEEN_NIGHT 					= 2;
	
	public static final int[] PHASE 					= {0, 0, 3, 3, 5, 9};
	
	public static final int[] MATERIAL 					= {0, 100, 396, 416, 706, 1302, 3000};
	public static final int[] MATERIAL_SEE				= {0, 100, 300, 300, 500, 900, 3000};
	public static final int[] NIGHT_PAWN				= {68, -14, -2, 2, 8, 12, 20, 30, 36, 55, 70, 70, 70};
	public static final int[] ROOK_PAWN					= {48, -4, -4, -4, -4, 0, 0, 0, 0, 0, 0, 0, 0};
	
	public static final int[] PINNED 					= {0, 2, -18, -54, -68, -84};
	public static final int[] DISCOVERED		 		= {0, -14, 128, 110, 180, 0, 28};
	public static final int[] DOUBLE_ATTACKED 			= {0, 16, 34, 64, -4, -6, 0};
	public static final int[] BISHOP_PAWN 				= {20, 8, 6, 0, -6, -12, -22, -32, -46};
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

	public static final int[] PASSED_SCORE_EG			= {0, 14, 18, 34, 62, 128, 238};
	public static final int[] PASSED_CANDIDATE			= {0, 2, 2, 8, 14, 40};
	public static final float[] PASSED_KING_MULTI 		= {0, 1.5f, 1.3f, 1.2f, 1.1f, 1.0f, 0.8f, 0.8f, 0.9f};														
	public static final float[] PASSED_MULTIPLIERS		= {
			0.5f,	// blocked
			1.3f,	// next square attacked
			0.4f,	// enemy king in front
			1.2f,	// next square defended
			0.7f,	// attacked
			1.7f,	// defended by rook from behind
			0.6f,	// attacked by rook from behind
			1.8f	// no enemy attacks in front
	};	
	
	//concept borrowed from Ed Schroder
	public static final int[] KS_SCORES = { //TODO negative values? //TODO first values are not used
			0, -10, 0, 0, -150, -150, -130, -100, -50, 40, 
			70, 80, 100, 110, 130, 160, 190, 240, 290, 
			330, 400, 480, 540, 630, 620, 800 };
	public static final int[] KS_QUEEN_TROPISM 		= {0, 0, 1, 1, 1, 1, 0, 0};	// index 0 and 1 are never evaluated	
	public static final int[] KS_CHECK_QUEEN 		= {0, 0, 0, 0, 2, 3, 4, 4, 4, 4, 3, 3, 3, 2, 0, 0, 0};
	public static final int[] KS_NO_FRIENDS 		= {6, 4, 0, 5, 5, 5, 6, 6, 7, 8, 9, 10};
	public static final int[] KS_ATTACKS 			= {0, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 2, 9};
	public static final int[] KS_DOUBLE_ATTACKS 	= {0, 1, 3, 4, 0, -6, 0, 0, 0};
	public static final int[] KS_ATTACK_PATTERN		= {	
		 //                                                 Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  
		 // 	                    R  R  R  R  R  R  R  R                          R  R  R  R  R  R  R  R  
		 //             B  B  B  B              B  B  B  B              B  B  B  B              B  B  B  B  
		 //       N  N        N  N        N  N        N  N        N  N        N  N        N  N        N  N  
		 //    P     P     P     P     P     P     P     P     P     P     P     P     P     P     P     P
			4, 1, 2, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 3, 3, 1, 1, 3, 3, 3, 3, 4, 4
	};
	
	public static final int[] KS_OTHER	= {		
			3,		// queen-touch check
			4,		// king blocked at first rank check
			3,		// safe check
			1		// unsafe check
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
	
	/** piece, color, square */
	public static final int[][][] PSQT_MG			= new int[7][2][64];
	public static final int[][][] PSQT_EG			= new int[7][2][64];
	
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

	public static final int[] PROMOTION_SCORE_SEE = {
			0,
			0,
			MATERIAL_SEE[ChessConstants.NIGHT] 	- MATERIAL_SEE[ChessConstants.PAWN],
			MATERIAL_SEE[ChessConstants.BISHOP] - MATERIAL_SEE[ChessConstants.PAWN],
			MATERIAL_SEE[ChessConstants.ROOK] 	- MATERIAL_SEE[ChessConstants.PAWN],
			MATERIAL_SEE[ChessConstants.QUEEN] 	- MATERIAL_SEE[ChessConstants.PAWN],
	};
	
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
	
	
	private static void initMgEg(int[] array, int[] arrayMg, int[] arrayEg) {
		for(int i = 0; i < array.length; i++) {
			array[i] = score(arrayMg[i], arrayEg[i]);
		}
	}
	
	public static int score(final int mgScore, final int egScore) {
		return (mgScore << 16) + egScore;
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
	}
	
	public static void main(String[] args) {
		//increment a psqt with a constant
		for(int i=0; i<64; i++) {
			PSQT_EG[ChessConstants.KING][WHITE][i]+=20;
		}
		//System.out.println(PsqtTuning.getArrayFriendlyFormatted(PSQT_EG[ChessConstants.KING][WHITE]));
	}
}
