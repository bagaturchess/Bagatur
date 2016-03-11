/*
 * Created on Sep 28, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package bagaturchess.learning.impl.filler;


import bagaturchess.bitboard.common.Utils;


public class SignalFillerConstants {
	
	public static final int[] HUNGED_PIECES_O   = new int[] {0,   -1,   -2,   -4,   -8};
	public static final int[] HUNGED_PIECES_E   = new int[] {0,   -1,   -2,   -4,   -8};
	
	public static final int[] HUNGED_PAWNS_O   	= new int[] {0,   -1,   -2,   -4,   -8};
	public static final int[] HUNGED_PAWNS_E   	= new int[] {0,   -1,   -2,   -4,   -8};
	
	public static final int[] HUNGED_ALL_O   	= new int[] {0,   -1,   -2,   -3,   -4};
	public static final int[] HUNGED_ALL_E   	= new int[] {0,   -1,   -2,   -3,   -4};
	
	//0,   19,   27,   32,   50,   71,   93,   0,   		0,   11,   27,   47,   75,   140,   205,   0,
	//0,   4,   22,   47,   78,   120,   173,   0,   		0,   0,   11,   31,   68,   128,   199,   0,   
	public static final int[] PASSERS_RANK_O   = new int[] {0,   19,   27,   32,   50,    71,    93,   0};
	public static final int[] PASSERS_RANK_E   = new int[] {0,   11,   27,   47,   75,   140,   205,   0};
	
	//0,   12,   7,   7,   2,   4,   		0,   0,   4,   8,   10,   18,   
	//0,   0,   4,   11,   13,   36,   		0,   6,   6,   11,   17,   25,   
	public static final int[] PASSERS_CANDIDATE_RANK_O   = new int[] {0,   0,   5,   11,   13,   37};
	public static final int[] PASSERS_CANDIDATE_RANK_E   = new int[] {0,   5,   7,   11,   17,   25};

	
	public static final int[] PASSERS_KING_CLOSENESS_FRONTFIELD 		= new int[] { 3,    3,    2,   1,  -1,  -2,  -3,  -4};
	public static final int[] PASSERS_KING_CLOSENESS_FRONTFRONTFIELD 	= new int[] { 4,    3,    2,   1,  -1,  -2,  -3,  -4};
	public static final int[] PASSERS_KING_CLOSENESS_FRONTFIELD_OP 	= new int[] {-7,   -6,   -4,  -2,   2,   4,   6,   8};
	
	public static final int[] KING_SAFETY_KNIGHTS_ATTACKS = new int[] {0, 2, 3, 3, 4, 5, 6, 7, 8};
	public static final int[] KING_SAFETY_BISHOPS_ATTACKS = new int[] {0, 2, 3, 3, 4, 5, 6, 7, 8, 9};
	public static final int[] KING_SAFETY_ROOKS_ATTACKS   = new int[] {0, 2, 3, 3, 4, 5, 6, 7, 8, 9, 10};
	public static final int[] KING_SAFETY_QUEENS_ATTACKS  = new int[] {0, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3};
	public static final int[] KING_SAFETY_PAWNS_ATTACKS   = new int[] {0, 2, 2};
	
	public static final int[] MOBILITY_KNIGHT_O   = new int[] {  -8,  -4,  -2,  -1,   0,   2,   4,  6,  8 };
	public static final int[] MOBILITY_KNIGHT_E   = new int[] {  -8,  -4,  -2,  -1,   0,   2,   4,  6,  8 };
	public static final int[] MOBILITY_BISHOP_O   = new int[] { -16,  -8,  -4,  -2,  -1,   0,   2,  4,  6,  8,  10,  12,  14,  16};
	public static final int[] MOBILITY_BISHOP_E   = new int[] { -16,  -8,  -4,  -2,  -1,   0,   2,  4,  6,  8,  10,  12,  14,  16};
	public static final int[] MOBILITY_ROOK_O     = new int[] { -16,  -8,  -4,  -2,  -1,   0,   2,  4,  6,  8,  10,  12,  14,  15, 16};
	public static final int[] MOBILITY_ROOK_E     = new int[] { -16,  -8,  -4,  -2,  -1,   0,   2,  4,  6,  8,  10,  12,  14,  15, 16};
	public static final int[] MOBILITY_QUEEN_O    = new int[] { -64, -32, -16,  -8,  -4,  -2,  -1,   0,   2,   4,   6,   8,  10,   12,   14,   16,   18,   20,   22,   24,   26,   28,   30,   32,   34,   36,   38,   40};
	public static final int[] MOBILITY_QUEEN_E    = new int[] { -64, -32, -16,  -8,  -4,  -2,  -1,   0,   2,   4,   6,   8,  10,   12,   14,   16,   18,   20,   22,   24,   26,   28,   30,   32,   34,   36,   38,   40};
	
	public static final int[] KING_DISTANCE_O   = new int[] {0,   0,   0,   0,   0,    0,    0,   0};
	public static final int[] KING_DISTANCE_E   = new int[] {0,   0,   16,   8,   4,    2,    1,   0};
	
	
	/**
	 * KNIGHTS
	 */
	public static final double[] KNIGHT_O = Utils.reverseSpecial ( new double[]{
			-141, -13,   1,  17,  17,   1, -13, -141, 
			  -9,  15,  18,  31,  31,  18,  15,   -9, 
			   4,  42,  53,  65,  65,  53,  42,    4, 
			   5,  16,  32,  36,  36,  32,  16,    5, 
			 -13,   8,  21,  23,  23,  21,   8,  -13, 
			 -21,   0,  10,  21,  21,  10,   0,  -21, 
			 -35, -19,  -2,   4,   4,  -2, -19,  -35, 
			 -70, -36, -23, -23, -23, -23, -36,  -70, 
	});
	
	public static final double[] KNIGHT_E = Utils.reverseSpecial ( new double[]{
			-31, -10, -15, -19, -19, -15, -10, -31, 
			-12, -16,  -5,  -3,  -3,  -5, -16, -12, 
			  5,  10,  14,  18,  18,  14,  10,   5, 
			 -4,  14,  18,  25,  25,  18,  14,  -4, 
			-12,   9,  20,  24,  24,  20,   9, -12, 
			-16,  -4,   9,  10,  10,   9,  -4, -16, 
			-21,  -2,   3,   8,   8,   3,  -2, -21, 
			-28, -12,  -1,   2,   2,  -1, -12, -28, 
	});
	
	
	/**
	 * BISHOPS
	 */
	
	public static final double[] BISHOP_O = Utils.reverseSpecial ( new double[]{
			 -9, -12, -5, -7, -7, -5, -12,  -9, 
			 -3,   4, -1,  3,  3, -1,   4,  -3, 
			 -6,  -2, 13, 13, 13, 13,  -2,  -6, 
			-10,  -7,  4, 15, 15,  4,  -7, -10, 
			-10,  -7,  4, 15, 15,  4,  -7, -10, 
			 -6,  -2, 13, 13, 13, 13,  -2,  -6, 
			 -3,   4, -1,  3,  3, -1,   4,  -3, 
			 -9, -12, -5, -7, -7, -5, -12,  -9, 
	});
	
	public static final double[] BISHOP_E = Utils.reverseSpecial ( new double[]{
			-4, -2, -6, -3, -3, -6, -2, -4, 
			-3, -1,  1, -5, -5,  1, -1, -3, 
			-7,  4,  4,  1,  1,  4,  4, -7, 
			-1,  0,  5,  9,  9,  5,  0, -1, 
			-1,  0,  5,  9,  9,  5,  0, -1, 
			-7,  4,  4,  1,  1,  4,  4, -7, 
			-3, -1,  1, -5, -5,  1, -1, -3, 
			-4, -2, -6, -3, -3, -6, -2, -4, 
	});

	
	/**
	 * ROOKS
	 */
	
	public static final double[] ROOK_O = Utils.reverseSpecial ( new double[]{	           
			 11,  19,  23,  27,  27,  23,  19,  11, 
			  8,  14,  24,  26,  26,  24,  14,   8, 
			 -4,   0,  14,  17,  17,  14,   0,  -4, 
			-11,  -6,   7,  10,  10,   7,  -6, -11, 
			-12, -12,  -4,   1,   1,  -4, -12, -12, 
			-20, -12, -12,  -7,  -7, -12, -12, -20, 
			-19, -14, -11, -10, -10, -11, -14, -19, 
			 -9,  -9,  -8,  -4,  -4,  -8,  -9,  -9,   
	});
	
	public static final double[] ROOK_E = Utils.reverseSpecial ( new double[]{	           
			 0,  0,   0,   0,   0,   0,  0,  0, 
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0, 
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0, 
	});
	
	
	/**
	 * QUEENS
	 */
	
	public static final double[] QUEEN_O = Utils.reverseSpecial ( new double[]{	
			 27,  42,  75,  78,  78,  75,  42,  27, 
			 -5,  -4,   2,   2,   2,   2,  -4,  -5, 
			 -7,  -6,   1,   2,   2,   1,  -6,  -7, 
			 -7,  -6,   1,   2,   2,   1,  -6,  -7, 
			 -8,  -7,  -2,  -2,  -2,  -2,  -7,  -8, 
			-14, -11,  -5,  -5,  -5,  -5, -11, -14, 
			-15, -11,  -9,  -8,  -8,  -9, -11, -15, 
			-21, -20, -18, -16, -16, -18, -20, -21,     
	});
	
	public static final double[] QUEEN_E = Utils.reverseSpecial ( new double[]{	
			  26,   32,   39,  41,  41,   39,   32,   26,
			  24,   30,   37,  39,  39,   37,   30,   24, 
			  13,   19,   20,  26,  26,   20,   19,   13, 
			  10,   17,   18,  19,  19,   18,   17,   10, 
			   9,   15,   19,  24,  24,   19,   15,    9, 
			   7,   13,   19,  21,  21,   19,   13,    7, 
			  -1,   12,   18,  19,  19,   18,   12,   -1, 
			 -24,   -9,   -1,  11,  11,   -1,   -9,  -24,
	});
	
	/**
	 * PAWNS 
	 */
	
	public static final double[] PAWN_O = Utils.reverseSpecial ( new double[]{	
			  0,   0,   0,   0,   0,   0,   0,   0, 
			  0,   0,   0,   0,   0,   0,   0,   0,
			  4,  18,  52,  56,  56,  52,  18,   4, 
			 -8,  13,  29,  31,  31,  29,  13,  -8, 
			-15,   4,  16,  22,  22,  16,   4, -15, 
			-19,  -8,  12,  15,  15,  12,  -8, -19, 
			-22, -22,   0,  -1,  -1,   0, -22, -22, 
			  0,   0,   0,   0,   0,   0,   0,   0,  
	});
	
	public static final double[] PAWN_E = Utils.reverseSpecial ( new double[]{	  
			 0,  0,   0,   0,   0,   0,  0,  0, 
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0, 
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0,
			 0,  0,   0,   0,   0,   0,  0,  0, 
	});
	
	/**
	 * KINGS
	 */
	
	public static final double[] KING_O = Utils.reverseSpecial ( new double[]{
			-105,   -105,   -105,   -105,   -105,   -105,   -105,   -105,
			 -95,    -95,    -95,    -95,    -95,    -95,    -95,    -95,  
			 -85,    -85,    -85,    -85,    -85,    -85,    -85,    -85,  
			 -75,    -75,    -75,    -75,    -75,    -75,    -75,    -75,   
			 -64,    -64,    -64,    -64,    -64,    -64,    -64,    -64,   
			 -60,    -32,    -35,    -48,    -48,    -35,    -32,    -60,   
			 -10,     16,     -8,    -20,    -20,     -8,     16,    -10,   
			  -5,     25,     -8,    -30,      0,     -8,     25,     -5,   
	});	
	
	public static final double[] KING_E = Utils.reverseSpecial ( new double[]{
			-16,  -2,   6,   2,   2,   6,  -2, -16, 
			 -7,  12,  21,  14,  14,  21,  12,  -7, 
			  0,  25,  26,  27,  27,  26,  25,   0, 
			  5,  27,  33,  37,  37,  33,  27,   5, 
			-23,  11,  17,  20,  20,  17,  11, -23, 
			-15,   0,  15,  19,  19,  15,   0, -15, 
			-40, -14,  -1,   5,   5,  -1, -14, -40, 
			-77, -54, -30, -26, -26, -30, -54, -77,   
	});	
}
