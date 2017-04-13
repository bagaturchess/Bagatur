package bagaturchess.learning.goldmiddle.impl.cfg.old0;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl implements IBoardConfig {
	
	
	private int MATERIAL_PAWN_O = 96;
	private int MATERIAL_PAWN_E = 103;
		
	private int MATERIAL_KNIGHT_O = 314;
	private int MATERIAL_KNIGHT_E = 292;
	
	private int MATERIAL_BISHOP_O = 291;
	private int MATERIAL_BISHOP_E = 300;
	
	private int MATERIAL_ROOK_O = 495;
	private int MATERIAL_ROOK_E = 526;
	
	private int MATERIAL_QUEEN_O = 1265;
	private int MATERIAL_QUEEN_E = 979;
	
	private int MATERIAL_KING_O = 941;
	private int MATERIAL_KING_E = 997;

	
	private int MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private int MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
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
	
	
	public boolean getFieldsStatesSupport() {
		return false;
		//return true;
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
		// TODO Auto-generated method stub
		return 0;
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
