package bagaturchess.learning.goldmiddle.impl7.cfg;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl_V41 implements IBoardConfig {
	
	
	public static final double MATERIAL_PAWN_O		= 80;
	public static final double MATERIAL_PAWN_E		= 100;

	public static final double MATERIAL_KNIGHT_O	= 350;
	public static final double MATERIAL_KNIGHT_E	= 400;

	public static final double MATERIAL_BISHOP_O	= 350;
	public static final double MATERIAL_BISHOP_E	= 400;

	public static final double MATERIAL_ROOK_O		= 600;
	public static final double MATERIAL_ROOK_E		= 700;

	public static final double MATERIAL_QUEEN_O		= 1100;
	public static final double MATERIAL_QUEEN_E		= 1300;
	
	private static final double MATERIAL_KING_O 	= 3000;
	private static final double MATERIAL_KING_E 	= 3000;
	
	private static final double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private static final double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	private static final double[] KING_O			= Utils.reverseSpecial(new double[] {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		2, 4, 8, 2, 4, 2, 8, 2,
	});
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
	});
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		16,  16,  16,  16,  16,  16,  16,  16,
		8,  8,  8,  8,  8,  8,  8,  8,
		4,  4,  4,  4,  4,  4,  4,  4,
		2,  2,  2,  2,  2,  2,  2,  2,
		1,  1,  1,  1,  1,  1,  1,  1,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		16,  16,  16,  16,  16,  16,  16,  16,
		8,  8,  8,  8,  8,  8,  8,  8,
		4,  4,  4,  4,  4,  4,  4,  4,
		2,  2,  2,  2,  2,  2,  2,  2,
		1,  1,  1,  1,  1,  1,  1,  1,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
	});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
	});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  4,  4,  2,  2,  4,  4,  0,
		0,  4,  8,  4,  4,  8,  4,  0,
		0,  2,  4,  8,  8,  4,  2,  0,
		0,  2,  4,  8,  8,  4,  2,  0,
		0,  4,  8,  4,  4,  8,  4,  0,
		0,  4,  4,  2,  2,  4,  4,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  4,  4,  2,  2,  4,  4,  0,
		0,  4,  8,  4,  4,  8,  4,  0,
		0,  2,  4,  8,  8,  4,  2,  0,
		0,  2,  4,  8,  8,  4,  2,  0,
		0,  4,  8,  4,  4,  8,  4,  0,
		0,  4,  4,  2,  2,  4,  4,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});

	private static final double[] ROOK_E			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
	});
	

	private static final double[] QUEEN_O			= Utils.reverseSpecial(new double[] {
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  0,  0,  0,  0,  0,
		0,  0,  0,  1,  1,  0,  0,  0,
		0,  0,  1,  2,  2,  1,  0,  0,
		0,  1,  2,  4,  4,  2,  1,  0,
		1,  2,  4,  8,  8,  4,  2,  1,
		2,  4,  8,  8,  8,  8,  4,  2,
	});

	private static final double[] QUEEN_E			= Utils.reverseSpecial(new double[] {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 8, 8, 4, 1, 0,
		0, 1, 4, 4, 4, 4, 1, 0,
		0, 1, 1, 1, 1, 1, 1, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
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
