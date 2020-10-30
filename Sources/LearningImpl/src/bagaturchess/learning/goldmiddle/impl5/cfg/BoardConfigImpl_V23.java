package bagaturchess.learning.goldmiddle.impl5.cfg;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl_V23 implements IBoardConfig {
	
	public static final double MATERIAL_PAWN_O	=	126;
	public static final double MATERIAL_PAWN_E	=	208;

	public static final double MATERIAL_KNIGHT_O	=	781;
	public static final double MATERIAL_KNIGHT_E	=	854;

	public static final double MATERIAL_BISHOP_O	=	825;
	public static final double MATERIAL_BISHOP_E	=	915;

	public static final double MATERIAL_ROOK_O	=	1276;
	public static final double MATERIAL_ROOK_E	=	1380;

	public static final double MATERIAL_QUEEN_O	=	2538;
	public static final double MATERIAL_QUEEN_E	=	2682;
	
	private double MATERIAL_KING_O = 5000;
	private double MATERIAL_KING_E = 5000;
	
	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
			0, 0, 0, 0, 0, 0, 0, 0, 
			-7, 7, -3, -13, 5, -16, 10, -8, 
			5, -12, -7, 22, -8, -5, -15, -8, 
			13, 0, -13, 1, 11, -2, -13, 5, 
			-4, -23, 6, 20, 40, 17, 4, -8, 
			-9, -15, 11, 15, 32, 22, 5, -22, 
			3, 3, 10, 19, 16, 19, 7, -5, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, -11, 12, 21, 25, 19, 4, 7, 
			28, 20, 21, 28, 30, 7, 6, 13, 
			10, 5, 4, -5, -5, -5, 14, 9, 
			6, -2, -8, -4, -13, -12, -10, -9, 
			-10, -10, -10, 4, 4, 3, -6, -4, 
			-10, -6, 10, 0, 14, 7, -5, -19, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
			-201, -83, -56, -26, -26, -56, -83, -201, 
			-67, -27, 4, 37, 37, 4, -27, -67, 
			-9, 22, 58, 53, 53, 58, 22, -9, 
			-34, 13, 44, 51, 51, 44, 13, -34, 
			-35, 8, 40, 49, 49, 40, 8, -35, 
			-61, -17, 6, 12, 12, 6, -17, -61, 
			-77, -41, -27, -15, -15, -27, -41, -77, 
			-175, -92, -74, -73, -73, -74, -92, -175, 
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
			-100, -88, -56, -17, -17, -56, -88, -100, 
			-69, -50, -51, 12, 12, -51, -50, -69, 
			-51, -44, -16, 17, 17, -16, -44, -51, 
			-45, -16, 9, 39, 39, 9, -16, -45, 
			-35, -2, 13, 28, 28, 13, -2, -35, 
			-40, -27, -8, 29, 29, -8, -27, -40, 
			-67, -54, -18, 8, 8, -18, -54, -67, 
			-96, -65, -49, -21, -21, -49, -65, -96, 
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
			-48, 1, -14, -23, -23, -14, 1, -48, 
			-17, -14, 5, 0, 0, 5, -14, -17, 
			-16, 6, 1, 11, 11, 1, 6, -16, 
			-12, 29, 22, 31, 31, 22, 29, -12, 
			-5, 11, 25, 39, 39, 25, 11, -5, 
			-7, 21, -5, 17, 17, -5, 21, -7, 
			-15, 8, 19, 4, 4, 19, 8, -15, 
			-53, -5, -8, -23, -23, -8, -5, -53, 
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
			-46, -42, -37, -24, -24, -37, -42, -46, 
			-31, -20, -1, 1, 1, -1, -20, -31, 
			-30, 6, 4, 6, 6, 4, 6, -30, 
			-17, -1, -14, 15, 15, -14, -1, -17, 
			-20, -6, 0, 17, 17, 0, -6, -20, 
			-16, -1, -2, 10, 10, -2, -1, -16, 
			-37, -13, -17, 1, 1, -17, -13, -37, 
			-57, -30, -37, -12, -12, -37, -30, -57, 
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
			-17, -19, -1, 9, 9, -1, -19, -17, 
			-2, 12, 16, 18, 18, 16, 12, -2, 
			-22, -2, 6, 12, 12, 6, -2, -22, 
			-27, -15, -4, 3, 3, -4, -15, -27, 
			-13, -5, -4, -6, -6, -4, -5, -13, 
			-25, -11, -1, 3, 3, -1, -11, -25, 
			-21, -13, -8, 6, 6, -8, -13, -21, 
			-31, -20, -14, -5, -5, -14, -20, -31, 
			});

	private static final double[] ROOK_E			= Utils.reverseSpecial(new double[] {
			18, 0, 19, 13, 13, 19, 0, 18, 
			4, 5, 20, -5, -5, 20, 5, 4, 
			6, 1, -7, 10, 10, -7, 1, 6, 
			-5, 8, 7, -6, -6, 7, 8, -5, 
			-6, 1, -9, 7, 7, -9, 1, -6, 
			6, -8, -2, -6, -6, -2, -8, 6, 
			-12, -9, -1, -2, -2, -1, -9, -12, 
			-9, -13, -10, -9, -9, -10, -13, -9, 
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
			59, 89, 45, -1, -1, 45, 89, 59, 
			88, 120, 65, 33, 33, 65, 120, 88, 
			123, 145, 81, 31, 31, 81, 145, 123, 
			154, 179, 105, 70, 70, 105, 179, 154, 
			164, 190, 138, 98, 98, 138, 190, 164, 
			195, 258, 169, 120, 120, 169, 258, 195, 
			278, 303, 234, 179, 179, 234, 303, 278, 
			271, 327, 271, 198, 198, 271, 327, 271, 
			});
	
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
			11, 59, 73, 78, 78, 73, 59, 11, 
			47, 121, 116, 131, 131, 116, 121, 47, 
			92, 172, 184, 191, 191, 184, 172, 92, 
			96, 166, 199, 199, 199, 199, 166, 96, 
			103, 156, 172, 172, 172, 172, 156, 103, 
			88, 130, 169, 175, 175, 169, 130, 88, 
			53, 100, 133, 135, 135, 133, 100, 53, 
			1, 45, 85, 76, 76, 85, 45, 1, 
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
		   { S_o(  0, 0), S_o(  0,  0), S_o(  0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0) },
		   { S_o(  3,-10), S_o(  3, -6), S_o( 10, 10), S_o( 19,  0), S_o( 16, 14), S_o( 19,  7), S_o(  7, -5), S_o( -5,-19) },
		   { S_o( -9,-10), S_o(-15,-10), S_o( 11,-10), S_o( 15,  4), S_o( 32,  4), S_o( 22,  3), S_o(  5, -6), S_o(-22, -4) },
		   { S_o( -4,  6), S_o(-23, -2), S_o(  6, -8), S_o( 20, -4), S_o( 40,-13), S_o( 17,-12), S_o(  4,-10), S_o( -8, -9) },
		   { S_o( 13, 10), S_o(  0,  5), S_o(-13,  4), S_o(  1, -5), S_o( 11, -5), S_o( -2, -5), S_o(-13, 14), S_o(  5,  9) },
		   { S_o(  5, 28), S_o(-12, 20), S_o( -7, 21), S_o( 22, 28), S_o( -8, 30), S_o( -5,  7), S_o(-15,  6), S_o( -8, 13) },
		   { S_o( -7,  0), S_o(  7,-11), S_o( -3, 12), S_o(-13, 21), S_o(  5, 25), S_o(-16, 19), S_o( 10,  4), S_o( -8,  7) },
		   { S_o(  0, 0), S_o(  0,  0), S_o(  0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0), S_o( 0, 0) },
	};
	
	private static int [][] pst_pawns_e = {
		   { S_e(  0, 0), S_e(  0,  0), S_e(  0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0) },
		   { S_e(  3,-10), S_e(  3, -6), S_e( 10, 10), S_e( 19,  0), S_e( 16, 14), S_e( 19,  7), S_e(  7, -5), S_e( -5,-19) },
		   { S_e( -9,-10), S_e(-15,-10), S_e( 11,-10), S_e( 15,  4), S_e( 32,  4), S_e( 22,  3), S_e(  5, -6), S_e(-22, -4) },
		   { S_e( -4,  6), S_e(-23, -2), S_e(  6, -8), S_e( 20, -4), S_e( 40,-13), S_e( 17,-12), S_e(  4,-10), S_e( -8, -9) },
		   { S_e( 13, 10), S_e(  0,  5), S_e(-13,  4), S_e(  1, -5), S_e( 11, -5), S_e( -2, -5), S_e(-13, 14), S_e(  5,  9) },
		   { S_e(  5, 28), S_e(-12, 20), S_e( -7, 21), S_e( 22, 28), S_e( -8, 30), S_e( -5,  7), S_e(-15,  6), S_e( -8, 13) },
		   { S_e( -7,  0), S_e(  7,-11), S_e( -3, 12), S_e(-13, 21), S_e(  5, 25), S_e(-16, 19), S_e( 10,  4), S_e( -8,  7) },
		   { S_e(  0, 0), S_e(  0,  0), S_e(  0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0), S_e( 0, 0) },
	};
	
	private static int [][] pst_knights_o = {
		   { S_o(-175, -96), S_o(-92,-65), S_o(-74,-49), S_o(-73,-21) },
		   { S_o( -77, -67), S_o(-41,-54), S_o(-27,-18), S_o(-15,  8) },
		   { S_o( -61, -40), S_o(-17,-27), S_o(  6, -8), S_o( 12, 29) },
		   { S_o( -35, -35), S_o(  8, -2), S_o( 40, 13), S_o( 49, 28) },
		   { S_o( -34, -45), S_o( 13,-16), S_o( 44,  9), S_o( 51, 39) },
		   { S_o(  -9, -51), S_o( 22,-44), S_o( 58,-16), S_o( 53, 17) },
		   { S_o( -67, -69), S_o(-27,-50), S_o(  4,-51), S_o( 37, 12) },
		   { S_o(-201,-100), S_o(-83,-88), S_o(-56,-56), S_o(-26,-17) }
	};
		
	private static int [][] pst_knights_e = {
		   { S_e(-175, -96), S_e(-92,-65), S_e(-74,-49), S_e(-73,-21) },
		   { S_e( -77, -67), S_e(-41,-54), S_e(-27,-18), S_e(-15,  8) },
		   { S_e( -61, -40), S_e(-17,-27), S_e(  6, -8), S_e( 12, 29) },
		   { S_e( -35, -35), S_e(  8, -2), S_e( 40, 13), S_e( 49, 28) },
		   { S_e( -34, -45), S_e( 13,-16), S_e( 44,  9), S_e( 51, 39) },
		   { S_e(  -9, -51), S_e( 22,-44), S_e( 58,-16), S_e( 53, 17) },
		   { S_e( -67, -69), S_e(-27,-50), S_e(  4,-51), S_e( 37, 12) },
		   { S_e(-201,-100), S_e(-83,-88), S_e(-56,-56), S_e(-26,-17) }
	};
	
	private static int [][] pst_bishops_o = {
		   { S_o(-53,-57), S_o( -5,-30), S_o( -8,-37), S_o(-23,-12) },
		   { S_o(-15,-37), S_o(  8,-13), S_o( 19,-17), S_o(  4,  1) },
		   { S_o( -7,-16), S_o( 21, -1), S_o( -5, -2), S_o( 17, 10) },
		   { S_o( -5,-20), S_o( 11, -6), S_o( 25,  0), S_o( 39, 17) },
		   { S_o(-12,-17), S_o( 29, -1), S_o( 22,-14), S_o( 31, 15) },
		   { S_o(-16,-30), S_o(  6,  6), S_o(  1,  4), S_o( 11,  6) },
		   { S_o(-17,-31), S_o(-14,-20), S_o(  5, -1), S_o(  0,  1) },
		   { S_o(-48,-46), S_o(  1,-42), S_o(-14,-37), S_o(-23,-24) }
	};
		
	private static int [][] pst_bishops_e = {
		   { S_e(-53,-57), S_e( -5,-30), S_e( -8,-37), S_e(-23,-12) },
		   { S_e(-15,-37), S_e(  8,-13), S_e( 19,-17), S_e(  4,  1) },
		   { S_e( -7,-16), S_e( 21, -1), S_e( -5, -2), S_e( 17, 10) },
		   { S_e( -5,-20), S_e( 11, -6), S_e( 25,  0), S_e( 39, 17) },
		   { S_e(-12,-17), S_e( 29, -1), S_e( 22,-14), S_e( 31, 15) },
		   { S_e(-16,-30), S_e(  6,  6), S_e(  1,  4), S_e( 11,  6) },
		   { S_e(-17,-31), S_e(-14,-20), S_e(  5, -1), S_e(  0,  1) },
		   { S_e(-48,-46), S_e(  1,-42), S_e(-14,-37), S_e(-23,-24) }
	};
	
	private static int [][] pst_rooks_o = {
		   { S_o(-31, -9), S_o(-20,-13), S_o(-14,-10), S_o(-5, -9) },
		   { S_o(-21,-12), S_o(-13, -9), S_o( -8, -1), S_o( 6, -2) },
		   { S_o(-25,  6), S_o(-11, -8), S_o( -1, -2), S_o( 3, -6) },
		   { S_o(-13, -6), S_o( -5,  1), S_o( -4, -9), S_o(-6,  7) },
		   { S_o(-27, -5), S_o(-15,  8), S_o( -4,  7), S_o( 3, -6) },
		   { S_o(-22,  6), S_o( -2,  1), S_o(  6, -7), S_o(12, 10) },
		   { S_o( -2,  4), S_o( 12,  5), S_o( 16, 20), S_o(18, -5) },
		   { S_o(-17, 18), S_o(-19,  0), S_o( -1, 19), S_o( 9, 13) }
	};
		
	private static int [][] pst_rooks_e = {
		   { S_e(-31, -9), S_e(-20,-13), S_e(-14,-10), S_e(-5, -9) },
		   { S_e(-21,-12), S_e(-13, -9), S_e( -8, -1), S_e( 6, -2) },
		   { S_e(-25,  6), S_e(-11, -8), S_e( -1, -2), S_e( 3, -6) },
		   { S_e(-13, -6), S_e( -5,  1), S_e( -4, -9), S_e(-6,  7) },
		   { S_e(-27, -5), S_e(-15,  8), S_e( -4,  7), S_e( 3, -6) },
		   { S_e(-22,  6), S_e( -2,  1), S_e(  6, -7), S_e(12, 10) },
		   { S_e( -2,  4), S_e( 12,  5), S_e( 16, 20), S_e(18, -5) },
		   { S_e(-17, 18), S_e(-19,  0), S_e( -1, 19), S_e( 9, 13) }
	};
	
	private static int [][] pst_queens_o = {
		   { S_o( 3,-69), S_o(-5,-57), S_o(-5,-47), S_o( 4,-26) },
		   { S_o(-3,-55), S_o( 5,-31), S_o( 8,-22), S_o(12, -4) },
		   { S_o(-3,-39), S_o( 6,-18), S_o(13, -9), S_o( 7,  3) },
		   { S_o( 4,-23), S_o( 5, -3), S_o( 9, 13), S_o( 8, 24) },
		   { S_o( 0,-29), S_o(14, -6), S_o(12,  9), S_o( 5, 21) },
		   { S_o(-4,-38), S_o(10,-18), S_o( 6,-12), S_o( 8,  1) },
		   { S_o(-5,-50), S_o( 6,-27), S_o(10,-24), S_o( 8, -8) },
		   { S_o(-2,-75), S_o(-2,-52), S_o( 1,-43), S_o(-2,-36) }
	};
	
	private static int [][] pst_queens_e = {
		   { S_e( 3,-69), S_e(-5,-57), S_e(-5,-47), S_e( 4,-26) },
		   { S_e(-3,-55), S_e( 5,-31), S_e( 8,-22), S_e(12, -4) },
		   { S_e(-3,-39), S_e( 6,-18), S_e(13, -9), S_e( 7,  3) },
		   { S_e( 4,-23), S_e( 5, -3), S_e( 9, 13), S_e( 8, 24) },
		   { S_e( 0,-29), S_e(14, -6), S_e(12,  9), S_e( 5, 21) },
		   { S_e(-4,-38), S_e(10,-18), S_e( 6,-12), S_e( 8,  1) },
		   { S_e(-5,-50), S_e( 6,-27), S_e(10,-24), S_e( 8, -8) },
		   { S_e(-2,-75), S_e(-2,-52), S_e( 1,-43), S_e(-2,-36) }
	};
	
	private static int [][] pst_kings_o = {
		   { S_o(271,  1), S_o(327, 45), S_o(271, 85), S_o(198, 76) },
		   { S_o(278, 53), S_o(303,100), S_o(234,133), S_o(179,135) },
		   { S_o(195, 88), S_o(258,130), S_o(169,169), S_o(120,175) },
		   { S_o(164,103), S_o(190,156), S_o(138,172), S_o( 98,172) },
		   { S_o(154, 96), S_o(179,166), S_o(105,199), S_o( 70,199) },
		   { S_o(123, 92), S_o(145,172), S_o( 81,184), S_o( 31,191) },
		   { S_o( 88, 47), S_o(120,121), S_o( 65,116), S_o( 33,131) },
		   { S_o( 59, 11), S_o( 89, 59), S_o( 45, 73), S_o( -1, 78) }
	};
	
	private static int [][] pst_kings_e = {
		   { S_e(271,  1), S_e(327, 45), S_e(271, 85), S_e(198, 76) },
		   { S_e(278, 53), S_e(303,100), S_e(234,133), S_e(179,135) },
		   { S_e(195, 88), S_e(258,130), S_e(169,169), S_e(120,175) },
		   { S_e(164,103), S_e(190,156), S_e(138,172), S_e( 98,172) },
		   { S_e(154, 96), S_e(179,166), S_e(105,199), S_e( 70,199) },
		   { S_e(123, 92), S_e(145,172), S_e( 81,184), S_e( 31,191) },
		   { S_e( 88, 47), S_e(120,121), S_e( 65,116), S_e( 33,131) },
		   { S_e( 59, 11), S_e( 89, 59), S_e( 45, 73), S_e( -1, 78) }
	};
	
	private static final int S_o(int o, int e) {
		return o;
	}
	
	private static final int S_e(int o, int e) {
		return e;
	}
	
	public static void main(String[] args) {
		//dumpMatrix(pst_kings_e);
		dumpMatrix_pawns(pst_pawns_e);
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
	
	
	private static void dumpMatrix_pawns(int[][] pst) {
		for (int rank = pst.length - 1; rank>=0; rank--) {
			String rankLine = "";
			for (int file = 0; file < pst[rank].length; file++) {
				rankLine += pst[rank][file] + ", ";
			}
			System.out.println(rankLine);
		}
	}
}
