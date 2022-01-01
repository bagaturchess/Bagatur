package bagaturchess.learning.goldmiddle.impl4.cfg;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl_V20 implements IBoardConfig {
	
	// 100, 396, 416, 706, 1302, 3000
	public static final double MATERIAL_PAWN_O	=	100;
	public static final double MATERIAL_PAWN_E	=	100;

	public static final double MATERIAL_KNIGHT_O	=	400;
	public static final double MATERIAL_KNIGHT_E	=	400;

	public static final double MATERIAL_BISHOP_O	=	425;
	public static final double MATERIAL_BISHOP_E	=	425;

	public static final double MATERIAL_ROOK_O	=	700;
	public static final double MATERIAL_ROOK_E	=	700;

	public static final double MATERIAL_QUEEN_O	=	1300;
	public static final double MATERIAL_QUEEN_E	=	1300;
	
	private static final double MATERIAL_KING_O = 3000;
	private static final double MATERIAL_KING_E = 3000;
	
	private static final double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private static final double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	private static final double[] KING_O			= Utils.reverseSpecial(new double[] {
			 -50,180,-26,  2,  2,-26,180,-50,
			  48,-10,-64,-24,-24,-64,-10, 48,
			  42, 52, 44,-44,-44, 44, 52, 42,
			 -40,-22,-46,-92,-92,-46,-22,-40,
			 -40,-20,-20,-76,-76,-20,-20,-40,
			  14, 16,  2,-10,-10,  2, 16, 14,
			  30, 10,-40,-56,-56,-40, 10, 30,
			  34, 44,  8, 18, 18,  8, 44, 34
			});
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
			 -90,-90, -2,-56,-56, -2,-90,-90,
			 -34, 14, 40, 24, 24, 40, 14,-34,
			  -8, 32, 36, 38, 38, 36, 32, -8,
			   0, 38, 44, 50, 50, 44, 38,  0,
			 -12, 14, 28, 44, 44, 28, 14,-12,
			 -18,  8, 18, 24, 24, 18,  8,-18,
			 -40, -4, 16, 24, 24, 16, -4,-40,
			 -74,-44,-24,-34,-34,-24,-44,-74
			});
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
			   0,  0,  0,  0,  0,  0,  0,  0,
			   164,156,174,218,218,174,156,164,
			    12, 26, 66, 58, 58, 66, 26, 12,
			   -16, -4, -2, 14, 14, -2, -4,-16,
			   -32,-28,-12,  2,  2,-12,-28,-32,
			   -30,-20,-16,-16,-16,-16,-20,-30,
			   -24,  4,-16,-10,-10,-16,  4,-24,
			     0,  0,  0,  0,  0,  0,  0,  0
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
			   0,  0,  0,  0,  0,  0,  0,  0,
			   -44,-34,-50,-60,-60,-50,-34,-44,
			    32, 16, -8,-22,-22, -8, 16, 32,
			    28, 14,  6, -4, -4,  6, 14, 28,
			    16, 12,  2, -2, -2,  2, 12, 16,
			     6,  4,  4, 10, 10,  4,  4,  6,
			    16,  4, 14, 22, 22, 14,  4, 16,
			     0,  0,  0,  0,  0,  0,  0,  0
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
			 -214,-112,-130,-34,-34,-130,-112,-214,
			 -80,-62,  2,-34,-34,  2,-62,-80,
			 -24, 44, 18, 36, 36, 18, 44,-24,
			  18, 42, 38, 50, 50, 38, 42, 18,
			   8, 36, 36, 36, 36, 36, 36,  8,
			   8, 38, 34, 40, 40, 34, 38,  8,
			   0,  0, 24, 36, 36, 24,  0,  0,
			 -30,  6, -4, 20, 20, -4,  6,-30
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
			 -16,  2, 32, 18, 18, 32,  2,-16,
			   2, 28, 12, 40, 40, 12, 28,  2,
			  -6,  6, 32, 28, 28, 32,  6, -6,
			  16, 20, 38, 42, 42, 38, 20, 16,
			  10, 18, 30, 40, 40, 30, 18, 10,
			   6,  8, 16, 30, 30, 16,  8,  6,
			 -10,  8,  6, 14, 14,  6,  8,-10,
			 -10,  2,  8, 16, 16,  8,  2,-10
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
			 -18, 12,-92,-84,-84,-92, 12,-18,
			 -44, -8,  0,-18,-18,  0, -8,-44,
			  40, 48, 42, 34, 34, 42, 48, 40,
			  28, 34, 40, 58, 58, 40, 34, 28,
			  28, 34, 36, 62, 62, 36, 34, 28,
			  36, 54, 50, 40, 40, 50, 54, 36,
			  36, 60, 48, 42, 42, 48, 60, 36,
			   8, 32, 30, 48, 48, 30, 32,  8
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
			 -34,-18, -6,  0,  0, -6,-18,-34,
			  -4,-18, -4, -6, -6, -4,-18, -4,
			 -14,-12,-18, -8, -8,-18,-12,-14,
			  -6, -6,  0,  2,  2,  0, -6, -6,
			 -22,-12, -6, -6, -6, -6,-12,-22,
			 -20,-16,-18,  0,  0,-18,-16,-20,
			 -32,-40,-24,-12,-12,-24,-40,-32,
			 -36,-18,-12,-18,-18,-12,-18,-36
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
			 -36,-26,-72, -4, -4,-72,-26,-36,
			 -36,-20, 14, 22, 22, 14,-20,-36,
			 -28,  2, -2, -8, -8, -2,  2,-28,
			 -40,-22, 10,  6,  6, 10,-22,-40,
			 -44,-14,-22,  2,  2,-22,-14,-44,
			 -38,-12, -2, -4, -4, -2,-12,-38,
			 -48, -2, -6, 10, 10, -6, -2,-48,
			 -10,-12,  0, 14, 14,  0,-12,-10
			});

	private static final double[] ROOK_E			= Utils.reverseSpecial(new double[] {
			  44, 46, 64, 46, 46, 64, 46, 44,
			  40, 40, 32, 24, 24, 32, 40, 40,
			  36, 36, 34, 34, 34, 34, 36, 36,
			  42, 38, 40, 34, 34, 40, 38, 42,
			  34, 32, 36, 24, 24, 36, 32, 34,
			  22, 26, 14, 14, 14, 14, 26, 22,
			  18,  6, 10, 10, 10, 10,  6, 18,
			   6, 16, 12,  2,  2, 12, 16,  6
			});
	

	private static final double[] QUEEN_O			= Utils.reverseSpecial(new double[] {
			 -72,-44,-78,-66,-66,-78,-44,-72,
			 -38,-88,-70,-82,-82,-70,-88,-38,
			  -8,-38,-44,-64,-64,-44,-38, -8,
			 -38,-44,-46,-60,-60,-46,-44,-38,
			 -24,-34,-22,-30,-30,-22,-34,-24,
			  -8, 10,-14,-10,-10,-14, 10, -8,
			  -4, 12, 26, 20, 20, 26, 12, -4,
			  14,  6, 12, 24, 24, 12,  6, 14
			});

	private static final double[] QUEEN_E			= Utils.reverseSpecial(new double[] {
			  32, 14, 46, 38, 38, 46, 14, 32,
			   6, 22, 16, 48, 48, 16, 22,  6,
			  -4,  6, 12, 42, 42, 12,  6, -4,
			  36, 30, 14, 34, 34, 14, 30, 36,
			  14, 22,  4, 22, 22,  4, 22, 14,
			   6,-36,  0, -6, -6,  0,-36,  6,
			 -26,-42,-40,-18,-18,-40,-42,-26,
			 -42,-36,-32,-30,-30,-32,-36,-42
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
