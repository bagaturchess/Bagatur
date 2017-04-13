package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.filler;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class ALL_BoardConfig implements IBoardConfig {
	
	
	private double MATERIAL_PAWN_O 		= 73;
	private double MATERIAL_PAWN_E 		= 97;
	
	private double MATERIAL_KNIGHT_O 	= 295;
	private double MATERIAL_KNIGHT_E 	= 325;
	
	private double MATERIAL_BISHOP_O 	= 323;
	private double MATERIAL_BISHOP_E 	= 350;
	
	private double MATERIAL_ROOK_O 		= 502;
	private double MATERIAL_ROOK_E 		= 536;
	
	private double MATERIAL_QUEEN_O 	= 982;
	private double MATERIAL_QUEEN_E 	= 1035;
	
	private double MATERIAL_KING_O 		= 0;
	private double MATERIAL_KING_E 		= 0;
	
	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	
	private static final double[] KING_O			= Utils.reverseSpecial_100_256(new double[] {
			 94,  120,   78,   31,   31,   78, 120,  94,
			116,  158,   93,   48,   48,   93, 158, 116,		 
			147,  188,  113,   70,   70,  113, 188, 147,			 
			177,  205,  143,   94,   94,  143, 205, 177,			 
			204,  212,  175,  137,  137,  175, 212, 204,	
			226,  271,  202,  136,  136,  202, 271, 226,
			289,  329,  263,  205,  205,  263, 329, 289,
			291,  344,  294,  219,  219,  294, 344, 291,   
			});
	
	private static final double[] KING_E			= Utils.reverseSpecial_100_256(new double[] {
			 30,   76,  101,  111,  111,  101,  76,  30,
			 72,  121,  142,  161,  161,  142, 121,  72,
			118,  178,  199,  197,  197,  199, 178, 118,	 
			132,  187,  224,  227,  227,  224, 187, 132,		 
			131,  194,  194,  204,  204,  194, 194, 131,			 
			109,  164,  195,  191,  191,  195, 164, 109,
			 70,  119,  170,  159,  159,  170, 119,  70,
			 28,   76,  103,  112,  112,  103,  76,  28,
			});
	
	private static final double[] PAWN_O			= Utils.reverseSpecial_100_256(new double[] {
			  0,   0,   0,    0,    0,   0,   0,    0,
			 -9,  15,  -8,   -4,   -4,  -8,  15,   -9,
			-11, -13,  -6,   -2,   -2,  -6, -13,  -11,			  
			-11,   0,   3,   21,   21,   3,   0,  -11,			  
			-22, -14,  20,   35,   35,  20, -14,  -22,			  
			-23,  -7,  19,   24,   24,  19,  -7,  -23,		
			-16,   1,   7,    3,    3,   7,   1,  -16,			  
			  0,   0,   0,    0,    0,   0,   0,    0,   
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial_100_256(new double[] {   
			 0,    0,   0,    0,    0,   0,    0,   0,
			 3,   -9,   1,   18,   18,   1,   -9,   3,
			 8,   -5,   2,    4,    4,   2,   -5,   8,			 
			 8,    9,   7,   -6,   -6,   7,    9,   8,
			 3,    3,  -8,   -3,   -3,  -8,    3,   3,			 
			-4,   -5,   5,    4,    4,   5,   -5,  -4,
			 7,   -4,   8,   -2,   -2,   8,   -4,   7,
			 0,    0,   0,    0,    0,   0,    0,   0,
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial_100_256(new double[] {
			-195,  -66,  -42,  -29,  -29,  -42,  -66, -195,
			 -62,  -17,    5,   14,   14,    5,  -17,  -62,
			 -11,   37,   56,   71,   71,   56,   37,  -11,
			 -26,   16,   38,   50,   50,   38,   16,  -26,
			 -25,   18,   43,   47,   47,   43,   18,  -25,
			 -71,  -22,    0,    9,    9,    0,  -22,  -71,
			 -83,  -43,  -21,  -10,  -10,  -21,  -43,  -83,
			-143,  -96,  -80,  -73,  -73,  -80,  -96, -143,
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial_100_256(new double[] {
			-110,  -90,  -50,  -13,  -13,  -50,  -90, -110,
			 -64,  -50,  -24,   13,   13,  -24,  -50,  -64,			
			 -55,  -38,   -8,   27,   27,   -8,  -38,  -55,			
			 -46,  -25,    2,   41,   41,    2,  -25,  -46,			
			 -41,  -25,    7,   38,   38,    7,  -25,  -41,			
			 -50,  -39,   -8,   28,   28,   -8,  -39,  -50,			
			 -69,  -55,  -17,    9,    9,  -17,  -55,  -69,			
			 -97,  -82,  -46,  -14,  -14,  -46,  -82,  -97,
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial_100_256(new double[] {
			-45,  -21,  -29,  -39,  -39,  -29,  -21,  -45,
			-33,    7,   -4,  -12,  -12,   -4,    7,  -33,
			-27,    6,    2,   -8,   -8,    2,    6,  -27,
			-21,   14,    6,   -1,   -1,    6,   14,  -21,
			-21,   18,   11,    0,    0,   11,   18,  -21,
			-19,   17,   11,    1,    1,   11,   17,  -19,
			-30,   10,    2,   -9,   -9,    2,   10,  -30,
			-54,  -23,  -35,  -44,  -44,  -35,  -23,  -54,
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial_100_256(new double[] {
			-65,  -42,  -46,  -27,  -27,  -46,  -42,  -65,
			-44,  -21,  -22,   -4,   -4,  -22,  -21,  -44,
			-35,  -13,  -10,    1,    1,  -10,  -13,  -35,
			-36,  -14,  -17,    3,    3,  -17,  -14,  -36,
			-36,  -13,  -15,    7,    7,  -15,  -13,  -36,
			-32,   -9,  -13,    8,    8,  -13,   -9,  -32,
			-43,  -17,  -23,   -5,   -5,  -23,  -17,  -43,
			-68,  -40,  -46,  -28,  -28,  -46,  -40,  -68,
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial_100_256(new double[] {   
			-23,  -15,  -11,   -5,   -5,  -11,  -15,  -23,
			-12,    4,    8,   12,   12,    8,    4,  -12,
			-21,   -7,    0,    2,    2,    0,   -7,  -21,
			-22,   -7,    0,    1,    1,    0,   -7,  -22,
			-22,   -6,   -1,    2,    2,   -1,   -6,  -22,
			-21,   -9,   -4,    2,    2,   -4,   -9,  -21,
			-21,   -8,   -3,    0,    0,   -3,   -8,  -21,
			-25,  -16,  -16,   -9,   -9,  -16,  -16,  -25,
			});

	private static final double[] ROOK_E			= Utils.reverseSpecial_100_256(new double[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
		  
	private static final double[] QUEEN_O			= Utils.reverseSpecial_100_256(new double[] {   
		   -1,   -4,   -1,    0,   0,  -1,  -4,   -1,
		   -2,    7,    7,    6,   6,   7,   7,   -2,		   
		   -2,    6,    8,   10,  10,   8,   6,   -2,
		   -3,    9,    8,    7,   7,   8,   9,   -3,
		   -1,    8,   10,    7,   7,  10,   8,   -1,	   
		   -2,    6,    9,    9,   9,   9,   6,   -2,		   
		   -4,    6,    9,    8,   8,   9,   6,   -4,		   
			0,   -3,   -4,   -1,  -1,  -4,  -3,   0,
			});

	private static final double[] QUEEN_E			= Utils.reverseSpecial_100_256(new double[] {
			-75,   -54,   -44,   -30,  -30,  -44,  -54,  -75,
			-54,   -30,   -21,    -7,   -7,  -21,  -30,  -54,		
			-40,   -16,   -11,     3,    3,  -11,  -16,  -40,
			-27,    -5,    10,    23,   23,   10,   -5,  -27,
			-29,    -5,     9,    17,   17,    9,   -5,  -29,
			-39,   -17,    -7,     5,    5,   -7,  -17,  -39,
			-58,   -30,   -21,    -4,   -4,  -21,  -30,  -58,			
			-70,   -57,   -41,   -29,  -29,  -41,  -57,  -70,
			});
	
	
	public boolean getFieldsStatesSupport() {
		return false;
	}
	
	
	@Override
	public double[] getPST_PAWN_O() {
		return PAWN_O;
	}

	@Override
	public double[] getPST_PAWN_E() {
		return PAWN_E;
	}

	@Override
	public double[] getPST_KING_O() {
		return KING_O;
	}

	@Override
	public double[] getPST_KING_E() {
		return KING_E;
	}

	@Override
	public double[] getPST_KNIGHT_O() {
		return KNIGHT_O;
	}

	@Override
	public double[] getPST_KNIGHT_E() {
		return KNIGHT_E;
	}

	@Override
	public double[] getPST_BISHOP_O() {
		return BISHOP_O;
	}

	@Override
	public double[] getPST_BISHOP_E() {
		return BISHOP_E;
	}

	@Override
	public double[] getPST_ROOK_O() {
		return ROOK_O;
	}

	@Override
	public double[] getPST_ROOK_E() {
		return ROOK_E;
	}

	@Override
	public double[] getPST_QUEEN_O() {
		return QUEEN_O;
	}

	@Override
	public double[] getPST_QUEEN_E() {
		return QUEEN_E;
	}


	@Override
	public double getMaterial_PAWN_O() {
		return MATERIAL_PAWN_O;
	}


	@Override
	public double getMaterial_PAWN_E() {
		return MATERIAL_PAWN_E;
	}


	@Override
	public double getMaterial_KING_O() {
		return MATERIAL_KING_O;
	}


	@Override
	public double getMaterial_KING_E() {
		return MATERIAL_KING_E;
	}


	@Override
	public double getMaterial_KNIGHT_O() {
		return MATERIAL_KNIGHT_O;
	}


	@Override
	public double getMaterial_KNIGHT_E() {
		return MATERIAL_KNIGHT_E;
	}


	@Override
	public double getMaterial_BISHOP_O() {
		return MATERIAL_BISHOP_O;
	}


	@Override
	public double getMaterial_BISHOP_E() {
		return MATERIAL_BISHOP_E;
	}


	@Override
	public double getMaterial_ROOK_O() {
		return MATERIAL_ROOK_O;
	}


	@Override
	public double getMaterial_ROOK_E() {
		return MATERIAL_ROOK_E;
	}


	@Override
	public double getMaterial_QUEEN_O() {
		return MATERIAL_QUEEN_O;
	}


	@Override
	public double getMaterial_QUEEN_E() {
		return MATERIAL_QUEEN_E;
	}


	@Override
	public double getMaterial_BARIER_NOPAWNS_O() {
		return MATERIAL_BARIER_NOPAWNS_O;
	}


	@Override
	public double getMaterial_BARIER_NOPAWNS_E() {
		return MATERIAL_BARIER_NOPAWNS_E;
	}
	
	@Override
	public double getWeight_PST_PAWN_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_PAWN_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_KING_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_KING_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_KNIGHT_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_KNIGHT_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_BISHOP_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_BISHOP_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_ROOK_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_ROOK_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_QUEEN_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_QUEEN_E() {
		return 1;
	}
}
