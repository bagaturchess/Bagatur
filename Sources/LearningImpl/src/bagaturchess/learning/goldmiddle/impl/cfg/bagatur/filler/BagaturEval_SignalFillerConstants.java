package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.filler;


import bagaturchess.bitboard.common.Utils;


public interface BagaturEval_SignalFillerConstants {
	
	/**
	 * Standard
	 */
	public static final int MATERIAL_DOUBLE_BISHOP_O 	= 40;
	public static final int MATERIAL_DOUBLE_BISHOP_E 	= 50;
	
	public static final int STANDARD_TEMPO_O			= 25;
	public static final int STANDARD_TEMPO_E			= 35;
	public static final int STANDARD_CASTLING_O			= 10;
	public static final int STANDARD_CASTLING_E			= 0;
	public static final int FIANCHETTO                  = 30;
	public static final int STANDARD_TRAP_BISHOP		= -120;
	public static final int STANDARD_BLOCKED_PAWN		= -30;
	
	public static final int STANDARD_KINGS_OPPOSITION_O = 0;
	public static final int STANDARD_KINGS_OPPOSITION_E = 50;
	
	public static final int[] STANDARD_DIST_KINGS_O		= new int[] {0, 0, 0, 0, 0, 0, 0, 0};
	public static final int[] STANDARD_DIST_KINGS_E		= new int[] {0, 0, 0, 0, 0, 0, 0, 0}; //new int[] {0, 0, 15, 2, 9, 1, 3, 0};
	public static final int[] STANDARD_DIST_QUEENS_O	= new int[] {0, 0, 0, 0, 0, 0, 0, 0};
	public static final int[] STANDARD_DIST_QUEENS_E	= new int[] {0, 0, 0, 0, 0, 0, 0, 0};
	
	public static final int STANDARD_NOQUEEN_O			= 0;//33;//17;
	public static final int STANDARD_NOQUEEN_E			= 0;//33;//17;
	
	/**
	 * Pawns
	 */
	public static final int[] PAWNS_DOUBLED_O		= new int[] {-12, -11, -6, -3};
	public static final int[] PAWNS_DOUBLED_E		= new int[] {-19, -17, -16, -13};
	public static final int[] PAWNS_ISOLATED_O		= new int[] {-3, -6, -11, -12};
	public static final int[] PAWNS_ISOLATED_E		= new int[] {-13, -16, -17, -19};
	public static final int[] PAWNS_BACKWARD_O		= new int[] {-1, -3, -5, -6};
	public static final int[] PAWNS_BACKWARD_E		= new int[] {-6, -8, -9, -10};
	public static final int[] PAWNS_SUPPORTED_O		= new int[] {1, 3, 5, 6};
	public static final int[] PAWNS_SUPPORTED_E		= new int[] {6, 8, 9, 10};
	public static final int[] PAWNS_CANDIDATE_O		= new int[] {0, 1, 2, 5, 9, 18};
	public static final int[] PAWNS_CANDIDATE_E		= new int[] {0, 2, 7, 11, 26, 72};
	public static final int[] PAWNS_PASSED_O		= new int[] {0, 11, 11, 11, 26, 40, 65};
	public static final int[] PAWNS_PASSED_E		= new int[] {0, 7, 7, 25, 63, 134, 186};
	public static final int[] PAWNS_PASSED_SUPPORTED_O	= new int[] {0, 13, 13, 21, 41, 62, 124};
	public static final int[] PAWNS_PASSED_SUPPORTED_E	= new int[] {0, 9, 12, 31, 79, 178, 299};
	
	
	public static final int PAWNS_KING_GUARDS		= 4;
	public static final int PAWNS_KING_OPENED		= -12;
	public static final int PAWNS_ROOK_OPENED_O		= 22;
	public static final int PAWNS_ROOK_OPENED_E		= 16;
	public static final int PAWNS_ROOK_SEMIOPENED_O	= 7;
	public static final int PAWNS_ROOK_SEMIOPENED_E	= 20;
	public static final int PAWNS_ROOK_7TH2TH_O		= 3;
	public static final int PAWNS_ROOK_7TH2TH_E		= 36;
	public static final int PAWNS_QUEEN_7TH2TH_O	= 0;
	public static final int PAWNS_QUEEN_7TH2TH_E	= 26;
	
	
	/**
	 * Mobility
	 */
	public static final int[] MOBILITY_KNIGHT_O		= new int[] {-21, -9, -4, -1, 2, 5, 7, 8, 8};
	public static final int[] MOBILITY_KNIGHT_E		= new int[] {-34, -20, -7, 4, 7, 13, 15, 17, 19};
	public static final int[] MOBILITY_BISHOP_O		= new int[] {-10, -6, -1, 0, 1, 4, 5, 6, 7, 8, 8, 9, 10, 11};
	public static final int[] MOBILITY_BISHOP_E		= new int[] {-23, -18, -12, -3, 4, 9, 13, 15, 18, 19, 20, 21, 22, 23};
	public static final int[] MOBILITY_ROOK_O		= new int[] {-11, -8, -5, -3, 0, 3, 5, 6, 7, 8, 8, 10, 11, 11, 12};
	public static final int[] MOBILITY_ROOK_E		= new int[] {-9, -4, 0, 0, 1, 1, 1, 2, 6, 7, 7, 8, 8, 8, 9};
	public static final int[] MOBILITY_QUEEN_O		= new int[] {-2, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
	public static final int[] MOBILITY_QUEEN_E		= new int[] {-60, -55, -51, -46, -42, -37, -33, -29, -25, -21, -16, -11, -6, -1, 6, 11, 15, 19, 23, 27, 31, 36, 40, 44, 48, 52, 57, 61};
	
	
	/**
	 * Fields states iteration
	 */
	public static final int[] KING_SAFETY			= new int[] {0, -1, -2, -6, -7, -8, -9, -10, -11, -12, -13, -14, -15, -16, -17, -18, -19, -20, -21, -22, -23, -26, -28, -30, -33, -34, -37, -40, -41, -43, -49, -50, -55, -58, -60, -64, -68, -69, -76, -77, -79, -90, -91, -92, -96, -97, -98, -102, -103, -104, -110, -111, -112, -113, -113, -114, -115, -116, -118, -119, -120, -121, -122, -123, -124, -125, -125, -126, -127, -128, -129, -130, -131, -132, -133, -134, -135, -136, -137, -138, -139, -140, -141, -142, -143, -144, -145, -146, -147, -148, -149, -150, -151, -152, -153, -154, -155, -156, -157, -158, -159};
	public static final int[] HUNGED_O				= new int[] {0, -25, -41, -51, -62, -73, -83, -94, -104, -114, -125};
	public static final int[] HUNGED_E				= new int[] {0, -14, -20, -26, -32, -38, -43, -49, -55, -67, -77};
	public static final int[] MOBILITY_KNIGHT_S_O	= new int[] {-25, -14, -8, -1, 5, 8, 12, 18, 22};
	public static final int[] MOBILITY_KNIGHT_S_E	= new int[] {-25, -15, -6, 7, 10, 17, 21, 23, 25};
	public static final int[] MOBILITY_BISHOP_S_O	= new int[] {-15, -11, -8, -6, -4, -2, 0, 2, 4, 7, 10, 12, 14, 16};
	public static final int[] MOBILITY_BISHOP_S_E	= new int[] {-14, 0, 2, 7, 7, 8, 8, 11, 13, 13, 14, 15, 15, 15};
	public static final int[] MOBILITY_ROOK_S_O		= new int[] {-13, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 14, 14};
	public static final int[] MOBILITY_ROOK_S_E		= new int[] {-26, -21, -16, -7, -5, 0, 3, 6, 6, 7, 9, 22, 24, 25, 26};
	public static final int[] MOBILITY_QUEEN_S_O	= new int[] {-12, -10, -8, -5, -2, 0, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12};
	public static final int[] MOBILITY_QUEEN_S_E	= new int[] {-17, -16, -15, -14, -14, -13, -8, -6, -1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 9, 10, 11, 12, 13, 13, 14, 15, 16, 17};
	public static final int SPACE_O					= 1;
	public static final int SPACE_E					= 1;
	
	public static final int TRAPED_O				= -1;
	public static final int TRAPED_E				= -1;
	
	
	public static final int[] KNIGHT_OUTPOST_O		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   4,   5,   5,   4,   0,   0,   
			0,   0,   7,   10,  10,  7,   0,   0,   
			0,   0,   8,   5,   5,   8,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final int[] KNIGHT_OUTPOST_E		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   2,   2,   1,   0,   0,   
			0,   0,   3,   3,   3,   3,   0,   0,   
			0,   0,   2,   4,   4,   2,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});

	public static final int[] BISHOP_OUTPOST_O		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   0,   0,   1,   0,   0,   
			0,   0,   3,   3,   3,   3,   0,   0,   
			0,   0,   3,   4,   4,   3,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final int[] BISHOP_OUTPOST_E		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   1,   1,   1,   0,   0,   
			0,   0,   0,   1,   1,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
}
