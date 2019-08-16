package bagaturchess.learning.goldmiddle.impl3.cfg;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl_V18 implements IBoardConfig {
	
	public static final double MATERIAL_PAWN_O	=	136;
	public static final double MATERIAL_PAWN_E	=	208;

	public static final double MATERIAL_KNIGHT_O	=	782;
	public static final double MATERIAL_KNIGHT_E	=	865;

	public static final double MATERIAL_BISHOP_O	=	830;
	public static final double MATERIAL_BISHOP_E	=	918;

	public static final double MATERIAL_ROOK_O	=	1289;
	public static final double MATERIAL_ROOK_E	=	1378;

	public static final double MATERIAL_QUEEN_O	=	2529;
	public static final double MATERIAL_QUEEN_E	=	2687;
	
	private double MATERIAL_KING_O = 3000;
	private double MATERIAL_KING_E = 3000;
	
	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
			0, 0, 0, 0, 0, 0, 0, 0, 
			-2, 20, -10, -2, -2, -10, 20, -2, 
			-11, -12, -2, 4, 4, -2, -12, -11, 
			-5, -2, -1, 12, 12, -1, -2, -5, 
			-14, -7, 20, 24, 24, 20, -7, -14, 
			-16, -3, 23, 23, 23, 23, -3, -16, 
			-11, 7, 7, 17, 17, 7, 7, -11, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
			 0, 0, 0, 0, 0, 0, 0, 0, 
			 1, -12, 6, 25, 25, 6, -12, 1, 
			 16, 6, 1, 16, 16, 1, 6, 16, 
			 13, 10, -1, -8, -8, -1, 10, 13, 
			 7, -4, -8, 2, 2, -8, -4, 7, 
			 -2, 2, 6, -1, -1, 6, 2, -2, 
			 -3, -1, 7, 2, 2, 7, -1, -3, 
			 0, 0, 0, 0, 0, 0, 0, 0, 
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
			-200, -80, -53, -32, -32, -53, -80, -200, 
			-67, -21, 6, 37, 37, 6, -21, -67, 
			-11, 28, 63, 55, 55, 63, 28, -11, 
			-29, 13, 42, 52, 52, 42, 13, -29, 
			-28, 5, 41, 47, 47, 41, 5, -28, 
			-64, -20, 4, 19, 19, 4, -20, -64, 
			-79, -39, -24, -9, -9, -24, -39, -79, 
			-169, -96, -80, -79, -79, -80, -96, -169, 
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
			-98, -89, -53, -16, -16, -53, -89, -98, 
			-64, -45, -37, 16, 16, -37, -45, -64, 
			-51, -38, -17, 19, 19, -17, -38, -51, 
			-41, -20, 4, 35, 35, 4, -20, -41, 
			-36, 0, 13, 34, 34, 13, 0, -36, 
			-38, -33, -5, 27, 27, -5, -33, -38, 
			-70, -56, -15, 6, 6, -15, -56, -70, 
			-105, -74, -46, -18, -18, -46, -74, -105, 
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
			-47, -7, -17, -29, -29, -17, -7, -47, 
			-19, -13, 7, -11, -11, 7, -13, -19, 
			-17, 14, -6, 6, 6, -6, 14, -17, 
			-8, 27, 13, 30, 30, 13, 27, -8, 
			4, 9, 18, 40, 40, 18, 9, 4, 
			-9, 22, -3, 12, 12, -3, 22, -9, 
			-24, 9, 15, 1, 1, 15, 9, -24, 
			-49, -7, -10, -34, -34, -10, -7, -49, 
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
			-55, -32, -36, -17, -17, -36, -32, -55, 
			-34, -10, -12, 6, 6, -12, -10, -34, 
			-24, -2, 0, 13, 13, 0, -2, -24, 
			-26, -4, -7, 14, 14, -7, -4, -26, 
			-26, -3, -5, 16, 16, -5, -3, -26, 
			-23, 0, -3, 16, 16, -3, 0, -23, 
			-34, -9, -14, 4, 4, -14, -9, -34, 
			-58, -31, -37, -19, -19, -37, -31, -58, 
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
			-25, -18, -11, 2, 2, -11, -18, -25, 
			-11, 8, 9, 12, 12, 9, 8, -11, 
			-23, -10, 1, 6, 6, 1, -10, -23, 
			-21, -12, -1, 4, 4, -1, -12, -21, 
			-21, -7, -4, -4, -4, -4, -7, -21, 
			-19, -10, 1, 0, 0, 1, -10, -19, 
			-18, -5, -1, 1, 1, -1, -5, -18, 
			-24, -15, -8, 0, 0, -8, -15, -24, 
			});

	private static final double[] ROOK_E			= Utils.reverseSpecial(new double[] {
			6, 4, 6, 2, 2, 6, 4, 6, 
			-1, 7, 11, -1, -1, 11, 7, -1, 
			3, 2, -1, 3, 3, -1, 2, 3, 
			-7, 5, -5, -7, -7, -5, 5, -7, 
			0, 4, -2, 1, 1, -2, 4, 0, 
			6, -7, 3, 3, 3, 3, -7, 6, 
			-7, -5, -5, -1, -1, -5, -5, -7, 
			0, 3, 0, 3, 3, 0, 3, 0, 
			});
	

	private static final double[] QUEEN_O			= Utils.reverseSpecial(new double[] {
			-2, -2, 1, -2, -2, 1, -2, -2, 
			-5, 6, 10, 8, 8, 10, 6, -5, 
			-4, 10, 6, 8, 8, 6, 10, -4, 
			0, 14, 12, 5, 5, 12, 14, 0, 
			4, 5, 9, 8, 8, 9, 5, 4, 
			-3, 6, 13, 7, 7, 13, 6, -3, 
			-3, 5, 8, 12, 12, 8, 5, -3, 
			3, -5, -5, 4, 4, -5, -5, 3, 
			});
	
	
	private static final double[] QUEEN_E			= Utils.reverseSpecial(new double[] {
			-75, -52, -43, -36, -36, -43, -52, -75, 
			-50, -27, -24, -8, -8, -24, -27, -50, 
			-38, -18, -12, 1, 1, -12, -18, -38, 
			-29, -6, 9, 21, 21, 9, -6, -29, 
			-23, -3, 13, 24, 24, 13, -3, -23, 
			-39, -18, -9, 3, 3, -9, -18, -39, 
			-55, -31, -22, -4, -4, -22, -31, -55, 
			-69, -57, -47, -26, -26, -47, -57, -69, 
			});
	
	
	private static final double[] KING_O			= Utils.reverseSpecial(new double[] {
			64, 87, 49, 0, 0, 49, 87, 64, 
			87, 120, 64, 25, 25, 64, 120, 87, 
			122, 159, 85, 36, 36, 85, 159, 122, 
			145, 176, 112, 69, 69, 112, 176, 145, 
			169, 191, 136, 108, 108, 136, 191, 169, 
			198, 253, 168, 120, 120, 168, 253, 198, 
			277, 305, 241, 183, 183, 241, 305, 277, 
			272, 325, 273, 190, 190, 273, 325, 272, 
			});
	
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
			5, 60, 75, 75, 75, 75, 60, 5, 
			40, 99, 128, 141, 141, 128, 99, 40, 
			87, 164, 174, 189, 189, 174, 164, 87, 
			98, 166, 197, 194, 194, 197, 166, 98, 
			103, 152, 168, 169, 169, 168, 152, 103, 
			86, 138, 165, 173, 173, 165, 138, 86, 
			57, 98, 138, 131, 131, 138, 98, 57, 
			0, 41, 80, 93, 93, 80, 41, 0, 
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
	
	
	private static int [][] pst_pawns_o = {
	   { S_o(  0, 0), S_o(  0,  0), S_o(  0, 0), S_o( 0, 0) },
	   { S_o(-11,-3), S_o(  7, -1), S_o(  7, 7), S_o(17, 2) },
	   { S_o(-16,-2), S_o( -3,  2), S_o( 23, 6), S_o(23,-1) },
	   { S_o(-14, 7), S_o( -7, -4), S_o( 20,-8), S_o(24, 2) },
	   { S_o( -5,13), S_o( -2, 10), S_o( -1,-1), S_o(12,-8) },
	   { S_o(-11,16), S_o(-12,  6), S_o( -2, 1), S_o( 4,16) },
	   { S_o( -2, 1), S_o( 20,-12), S_o(-10, 6), S_o(-2,25) }
	};
	
	private static int [][] pst_pawns_e = {
		   { S_e(  0, 0), S_e(  0,  0), S_e(  0, 0), S_e( 0, 0) },
		   { S_e(-11,-3), S_e(  7, -1), S_e(  7, 7), S_e(17, 2) },
		   { S_e(-16,-2), S_e( -3,  2), S_e( 23, 6), S_e(23,-1) },
		   { S_e(-14, 7), S_e( -7, -4), S_e( 20,-8), S_e(24, 2) },
		   { S_e( -5,13), S_e( -2, 10), S_e( -1,-1), S_e(12,-8) },
		   { S_e(-11,16), S_e(-12,  6), S_e( -2, 1), S_e( 4,16) },
		   { S_e( -2, 1), S_e( 20,-12), S_e(-10, 6), S_e(-2,25) }
	};
	
	private static int [][] pst_knights_o = {
		   { S_o(-169,-105), S_o(-96,-74), S_o(-80,-46), S_o(-79,-18) },
		   { S_o( -79, -70), S_o(-39,-56), S_o(-24,-15), S_o( -9,  6) },
		   { S_o( -64, -38), S_o(-20,-33), S_o(  4, -5), S_o( 19, 27) },
		   { S_o( -28, -36), S_o(  5,  0), S_o( 41, 13), S_o( 47, 34) },
		   { S_o( -29, -41), S_o( 13,-20), S_o( 42,  4), S_o( 52, 35) },
		   { S_o( -11, -51), S_o( 28,-38), S_o( 63,-17), S_o( 55, 19) },
		   { S_o( -67, -64), S_o(-21,-45), S_o(  6,-37), S_o( 37, 16) },
		   { S_o(-200, -98), S_o(-80,-89), S_o(-53,-53), S_o(-32,-16) }
	};
		
	private static int [][] pst_knights_e = {
		   { S_e(-169,-105), S_e(-96,-74), S_e(-80,-46), S_e(-79,-18) },
		   { S_e( -79, -70), S_e(-39,-56), S_e(-24,-15), S_e( -9,  6) },
		   { S_e( -64, -38), S_e(-20,-33), S_e(  4, -5), S_e( 19, 27) },
		   { S_e( -28, -36), S_e(  5,  0), S_e( 41, 13), S_e( 47, 34) },
		   { S_e( -29, -41), S_e( 13,-20), S_e( 42,  4), S_e( 52, 35) },
		   { S_e( -11, -51), S_e( 28,-38), S_e( 63,-17), S_e( 55, 19) },
		   { S_e( -67, -64), S_e(-21,-45), S_e(  6,-37), S_e( 37, 16) },
		   { S_e(-200, -98), S_e(-80,-89), S_e(-53,-53), S_e(-32,-16) }
	};
	
	private static int [][] pst_bishops_o = {
		   { S_o(-49,-58), S_o(- 7,-31), S_o(-10,-37), S_o(-34,-19) },
		   { S_o(-24,-34), S_o(  9, -9), S_o( 15,-14), S_o(  1,  4) },
		   { S_o( -9,-23), S_o( 22,  0), S_o( -3, -3), S_o( 12, 16) },
		   { S_o(  4,-26), S_o(  9, -3), S_o( 18, -5), S_o( 40, 16) },
		   { S_o( -8,-26), S_o( 27, -4), S_o( 13, -7), S_o( 30, 14) },
		   { S_o(-17,-24), S_o( 14, -2), S_o( -6,  0), S_o(  6, 13) },
		   { S_o(-19,-34), S_o(-13,-10), S_o(  7,-12), S_o(-11,  6) },
		   { S_o(-47,-55), S_o( -7,-32), S_o(-17,-36), S_o(-29,-17) }
	};
		
	private static int [][] pst_bishops_e = {
		   { S_e(-49,-58), S_e(- 7,-31), S_e(-10,-37), S_e(-34,-19) },
		   { S_e(-24,-34), S_e(  9, -9), S_e( 15,-14), S_e(  1,  4) },
		   { S_e( -9,-23), S_e( 22,  0), S_e( -3, -3), S_e( 12, 16) },
		   { S_e(  4,-26), S_e(  9, -3), S_e( 18, -5), S_e( 40, 16) },
		   { S_e( -8,-26), S_e( 27, -4), S_e( 13, -7), S_e( 30, 14) },
		   { S_e(-17,-24), S_e( 14, -2), S_e( -6,  0), S_e(  6, 13) },
		   { S_e(-19,-34), S_e(-13,-10), S_e(  7,-12), S_e(-11,  6) },
		   { S_e(-47,-55), S_e( -7,-32), S_e(-17,-36), S_e(-29,-17) }
	};
	
	private static int [][] pst_rooks_o = {
		   { S_o(-24, 0), S_o(-15, 3), S_o( -8, 0), S_o( 0, 3) },
		   { S_o(-18,-7), S_o( -5,-5), S_o( -1,-5), S_o( 1,-1) },
		   { S_o(-19, 6), S_o(-10,-7), S_o(  1, 3), S_o( 0, 3) },
		   { S_o(-21, 0), S_o( -7, 4), S_o( -4,-2), S_o(-4, 1) },
		   { S_o(-21,-7), S_o(-12, 5), S_o( -1,-5), S_o( 4,-7) },
		   { S_o(-23, 3), S_o(-10, 2), S_o(  1,-1), S_o( 6, 3) },
		   { S_o(-11,-1), S_o(  8, 7), S_o(  9,11), S_o(12,-1) },
		   { S_o(-25, 6), S_o(-18, 4), S_o(-11, 6), S_o( 2, 2) }
	};
		
	private static int [][] pst_rooks_e = {
		   { S_e(-24, 0), S_e(-15, 3), S_e( -8, 0), S_e( 0, 3) },
		   { S_e(-18,-7), S_e( -5,-5), S_e( -1,-5), S_e( 1,-1) },
		   { S_e(-19, 6), S_e(-10,-7), S_e(  1, 3), S_e( 0, 3) },
		   { S_e(-21, 0), S_e( -7, 4), S_e( -4,-2), S_e(-4, 1) },
		   { S_e(-21,-7), S_e(-12, 5), S_e( -1,-5), S_e( 4,-7) },
		   { S_e(-23, 3), S_e(-10, 2), S_e(  1,-1), S_e( 6, 3) },
		   { S_e(-11,-1), S_e(  8, 7), S_e(  9,11), S_e(12,-1) },
		   { S_e(-25, 6), S_e(-18, 4), S_e(-11, 6), S_e( 2, 2) }
	};
	
	private static int [][] pst_queens_o = {
		   { S_o(  3,-69), S_o(-5,-57), S_o(-5,-47), S_o( 4,-26) },
		   { S_o( -3,-55), S_o( 5,-31), S_o( 8,-22), S_o(12, -4) },
		   { S_o( -3,-39), S_o( 6,-18), S_o(13, -9), S_o( 7,  3) },
		   { S_o(  4,-23), S_o( 5, -3), S_o( 9, 13), S_o( 8, 24) },
		   { S_o(  0,-29), S_o(14, -6), S_o(12,  9), S_o( 5, 21) },
		   { S_o( -4,-38), S_o(10,-18), S_o( 6,-12), S_o( 8,  1) },
		   { S_o( -5,-50), S_o( 6,-27), S_o(10,-24), S_o( 8, -8) },
		   { S_o( -2,-75), S_o(-2,-52), S_o( 1,-43), S_o(-2,-36) }
	};
		
	private static int [][] pst_queens_e = {
		   { S_e(  3,-69), S_e(-5,-57), S_e(-5,-47), S_e( 4,-26) },
		   { S_e( -3,-55), S_e( 5,-31), S_e( 8,-22), S_e(12, -4) },
		   { S_e( -3,-39), S_e( 6,-18), S_e(13, -9), S_e( 7,  3) },
		   { S_e(  4,-23), S_e( 5, -3), S_e( 9, 13), S_e( 8, 24) },
		   { S_e(  0,-29), S_e(14, -6), S_e(12,  9), S_e( 5, 21) },
		   { S_e( -4,-38), S_e(10,-18), S_e( 6,-12), S_e( 8,  1) },
		   { S_e( -5,-50), S_e( 6,-27), S_e(10,-24), S_e( 8, -8) },
		   { S_e( -2,-75), S_e(-2,-52), S_e( 1,-43), S_e(-2,-36) }
	};
	
	private static int [][] pst_kings_o = {
		   { S_o(272,  0), S_o(325, 41), S_o(273, 80), S_o(190, 93) },
		   { S_o(277, 57), S_o(305, 98), S_o(241,138), S_o(183,131) },
		   { S_o(198, 86), S_o(253,138), S_o(168,165), S_o(120,173) },
		   { S_o(169,103), S_o(191,152), S_o(136,168), S_o(108,169) },
		   { S_o(145, 98), S_o(176,166), S_o(112,197), S_o(69, 194) },
		   { S_o(122, 87), S_o(159,164), S_o(85, 174), S_o(36, 189) },
		   { S_o(87,  40), S_o(120, 99), S_o(64, 128), S_o(25, 141) },
		   { S_o(64,   5), S_o(87,  60), S_o(49,  75), S_o(0,   75) }
	};
		
	private static int [][] pst_kings_e = {
		   { S_e(272,  0), S_e(325, 41), S_e(273, 80), S_e(190, 93) },
		   { S_e(277, 57), S_e(305, 98), S_e(241,138), S_e(183,131) },
		   { S_e(198, 86), S_e(253,138), S_e(168,165), S_e(120,173) },
		   { S_e(169,103), S_e(191,152), S_e(136,168), S_e(108,169) },
		   { S_e(145, 98), S_e(176,166), S_e(112,197), S_e(69, 194) },
		   { S_e(122, 87), S_e(159,164), S_e(85, 174), S_e(36, 189) },
		   { S_e(87,  40), S_e(120, 99), S_e(64, 128), S_e(25, 141) },
		   { S_e(64,   5), S_e(87,  60), S_e(49,  75), S_e(0,   75) }
	};
	
	private static final int S_o(int o, int e) {
		return o;
	}
	
	private static final int S_e(int o, int e) {
		return e;
	}
	
	public static void main(String[] args) {
		dumpMatrix(pst_kings_e);
	}


	private static void dumpMatrix(int[][] pst) {
		for (int rank = pst.length - 1; rank>=0; rank--) {
			String rankLine = "";
			for (int file = 0; file < pst[rank].length; file++) {
				rankLine += pst[rank][file] + ", ";
			}
			for (int file = pst[rank].length - 1; file >= 0; file--) {
				rankLine += pst[rank][file] + ", ";
			}
			System.out.println(rankLine);
		}
	}
}
