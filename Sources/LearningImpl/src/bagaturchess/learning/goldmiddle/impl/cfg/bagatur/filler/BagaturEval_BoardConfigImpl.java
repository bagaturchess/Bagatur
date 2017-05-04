package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.filler;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BagaturEval_BoardConfigImpl implements IBoardConfig {
	
	/**
	public static final int MATERIAL_PAWN_O				= 72;
	public static final int MATERIAL_PAWN_E				= 120;
	public static final int MATERIAL_KNIGHT_O			= 246;
	public static final int MATERIAL_KNIGHT_E			= 352;
	public static final int MATERIAL_BISHOP_O			= 262;
	public static final int MATERIAL_BISHOP_E			= 366;
	public static final int MATERIAL_ROOK_O				= 371;
	public static final int MATERIAL_ROOK_E				= 540;//602;
	public static final int MATERIAL_QUEEN_O			= 771;
	public static final int MATERIAL_QUEEN_E			= 1000;//1220;
	public static final int MATERIAL_DOUBLE_KNIGHT_O	= 487;
	public static final int MATERIAL_DOUBLE_KNIGHT_E	= 719;
	public static final int MATERIAL_DOUBLE_BISHOP_O	= 528;
	public static final int MATERIAL_DOUBLE_BISHOP_E	= 797;
	public static final int MATERIAL_DOUBLE_ROOK_O		= 738;
	public static final int MATERIAL_DOUBLE_ROOK_E		= 1080;//1244;
	 */
	
	/*
	private double MATERIAL_PAWN_O = 79;//99;//198;
	private double MATERIAL_PAWN_E = 103;//129;//258;
		
	private double MATERIAL_KNIGHT_O = 326;//408;//817;
	private double MATERIAL_KNIGHT_E = 338;//846;
	
	private double MATERIAL_BISHOP_O = 334;//836;
	private double MATERIAL_BISHOP_E = 342;//857;
	
	private double MATERIAL_ROOK_O = 508;//1270;
	private double MATERIAL_ROOK_E = 511;//1278;
	
	private double MATERIAL_QUEEN_O = 1008;//2521;
	private double MATERIAL_QUEEN_E = 1023;//2558;
	
	private double MATERIAL_KING_O = 1600;//5000;
	private double MATERIAL_KING_E = 1600;//5000;
	*/
	
	
	private double MATERIAL_PAWN_O = 80;//72;
	private double MATERIAL_PAWN_E = 100;//120;
		
	private double MATERIAL_KNIGHT_O = 246;
	private double MATERIAL_KNIGHT_E = 352;
	
	private double MATERIAL_BISHOP_O = 262;
	private double MATERIAL_BISHOP_E = 366;
	
	private double MATERIAL_ROOK_O = 411;//371
	private double MATERIAL_ROOK_E = 570; //540 //602
	
	private double MATERIAL_QUEEN_O = 875;//775;
	private double MATERIAL_QUEEN_E = 1230;//1000;
	
	private double MATERIAL_KING_O = 0;
	private double MATERIAL_KING_E = 0;
	
	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	private static final double[] KING_O			= Utils.reverseSpecial(new double[] {
			0,   0,   1,   0,   0,   1,   0,   0,   
			0,   3,   2,   1,   1,   2,   3,   0,   
			1,   5,   5,   1,   1,   5,   5,   1,   
			-2,   5,   6,   1,   1,   6,   5,   -2,   
			-9,   2,   6,   9,   9,   6,   2,   -9,   
			-9,   -5,   -1,   -2,   -2,   -1,   -5,   -9,   
			10,   12,   3,   -20,   -20,   3,   12,   10,   
			2,   10,   -13,   -27,   -27,   -13,   10,   2,   
			});
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
			2,   0,   2,   0,   0,   2,   0,   2,   
			1,   9,   6,   4,   4,   6,   9,   1,   
			3,   18,   16,   7,   7,   16,   18,   3,   
			-3,   9,   13,   2,   2,   13,   9,   -3,   
			-9,   6,   15,   18,   18,   15,   6,   -9,   
			-13,   7,   16,   19,   19,   16,   7,   -13,   
			-30,   -10,   -2,   13,   13,   -2,   -10,   -30,   
			-55,   -33,   -21,   -14,   -14,   -21,   -33,   -55,   
			});
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			6,   -4,   4,   -3,   -3,   4,   -4,   6,   
			9,   15,   16,   11,   11,   16,   15,   9,   
			-9,   3,   5,   2,   2,   5,   3,   -9,   
			-17,   -2,   2,   -1,   -1,   2,   -2,   -17,   
			-13,   4,   2,   3,   3,   2,   4,   -13,   
			-19,   4,   -1,   0,   0,   -1,   4,   -19,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			15,   2,   -8,   -17,   -17,   -8,   2,   15,   
			10,   9,   3,   -17,   -17,   3,   9,   10,   
			7,   7,   0,   -6,   -6,   0,   7,   7,   
			-1,   -1,   -6,   -6,   -6,   -6,   -1,   -1,   
			-7,   -1,   -7,   -5,   -5,   -7,   -1,   -7,   
			-1,   6,   2,   -2,   -2,   2,   6,   -1,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
			-12,   3,   1,   6,   6,   1,   3,   -12,   
			8,   8,   10,   9,   9,   10,   8,   8,   
			7,   13,   2,   6,   6,   2,   13,   7,   
			15,   8,   4,   9,   9,   4,   8,   15,   
			3,   3,   3,   5,   5,   3,   3,   3,   
			-11,   -8,   -9,   0,   0,   -9,   -8,   -11,   
			-9,   -6,   -9,   -3,   -3,   -9,   -6,   -9,   
			-15,   -12,   -11,   -7,   -7,   -11,   -12,   -15,   
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
			-2,   2,   1,   4,   4,   1,   2,   -2,   
			1,   2,   1,   6,   6,   1,   2,   1,   
			2,   1,   5,   8,   8,   5,   1,   2,   
			6,   12,   6,   11,   11,   6,   12,   6,   
			2,   2,   3,   6,   6,   3,   2,   2,   
			-3,   -9,   -11,   -7,   -7,   -11,   -9,   -3,   
			-2,   -2,   -9,   -9,   -9,   -9,   -2,   -2,   
			-4,   -8,   -2,   1,   1,   -2,   -8,   -4,   
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
			-2,   -6,   -6,   -10,   -10,   -6,   -6,   -2,   
			-6,   -6,   -3,   -6,   -6,   -3,   -6,   -6,   
			-1,   -1,   1,   0,   0,   1,   -1,   -1,   
			-5,   2,   0,   5,   5,   0,   2,   -5,   
			1,   -3,   1,   9,   9,   1,   -3,   1,   
			3,   2,   6,   3,   3,   6,   2,   3,   
			10,   10,   5,   2,   2,   5,   10,   10,   
			9,   6,   -2,   0,   0,   -2,   6,   9,   
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
			-1,   -3,   -4,   -6,   -6,   -4,   -3,   -1,   
			-2,   -2,   -4,   -6,   -6,   -4,   -2,   -2,   
			1,   1,   1,   -3,   -3,   1,   1,   1,   
			-3,   0,   0,   2,   2,   0,   0,   -3,   
			-6,   -1,   4,   4,   4,   4,   -1,   -6,   
			1,   2,   4,   3,   3,   4,   2,   1,   
			4,   6,   3,   -2,   -2,   3,   6,   4,   
			4,   4,   9,   4,   4,   9,   4,   4,   
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
			11,   9,   12,   14,   14,   12,   9,   11,   
			5,   3,   9,   9,   9,   9,   3,   5,   
			6,   5,   6,   9,   9,   6,   5,   6,   
			-5,   -2,   -2,   2,   2,   -2,   -2,   -5,   
			-9,   -6,   -5,   -3,   -3,   -5,   -6,   -9,   
			-12,   -5,   -5,   -1,   -1,   -5,   -5,   -12,   
			-15,   -8,   -6,   0,   0,   -6,   -8,   -15,   
			-5,   -1,   1,   4,   4,   1,   -1,   -5,   
			});

	private static final double[] ROOK_E			= Utils.reverseSpecial(new double[] {
			8,   9,   9,   10,   10,   9,   9,   8,   
			3,   8,   8,   8,   8,   8,   8,   3,   
			6,   7,   9,   8,   8,   9,   7,   6,   
			0,   -1,   -2,   -3,   -3,   -2,   -1,   0,   
			-6,   -5,   -4,   -5,   -5,   -4,   -5,   -6,   
			-3,   -5,   -5,   -5,   -5,   -5,   -5,   -3,   
			-3,   -3,   -2,   -3,   -3,   -2,   -3,   -3,   
			-7,   -6,   -5,   -4,   -4,   -5,   -6,   -7,   
			});

	private static final double[] QUEEN_O			= Utils.reverseSpecial(new double[] {
			12,   10,   10,   10,   10,   10,   10,   12,   
			4,   -3,   4,   4,   4,   4,   -3,   4,   
			2,   4,   6,   8,   8,   6,   4,   2,   
			-1,   2,   3,   3,   3,   3,   2,   -1,   
			-1,   -3,   0,   0,   0,   0,   -3,   -1,   
			-4,   -2,   -3,   -1,   -1,   -3,   -2,   -4,   
			-4,   -5,   -1,   1,   1,   -1,   -5,   -4,   
			-14,   -13,   -14,   -8,   -8,   -14,   -13,   -14,   
			});

	private static final double[] QUEEN_E			= Utils.reverseSpecial(new double[] {
			4,   6,   7,   9,   9,   7,   6,   4,   
			3,   4,   5,   7,   7,   5,   4,   3,   
			6,   4,   5,   6,   6,   5,   4,   6,   
			4,   2,   4,   5,   5,   4,   2,   4,   
			-2,   -2,   -1,   3,   3,   -1,   -2,   -2,   
			-2,   -5,   -5,   -6,   -6,   -5,   -5,   -2,   
			-3,   -4,   -9,   -10,   -10,   -9,   -4,   -3,   
			-6,   -7,   -7,   -10,   -10,   -7,   -7,   -6,   
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
