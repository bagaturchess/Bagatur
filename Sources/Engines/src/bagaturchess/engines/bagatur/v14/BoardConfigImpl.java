package bagaturchess.engines.bagatur.v14;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class BoardConfigImpl implements IBoardConfig {
	
	private static double[] zeros = new double[64];
	
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
	
	private double MATERIAL_PAWN_O = 92;//72;
	private double MATERIAL_PAWN_E = 92;//120;
		
	private double MATERIAL_KNIGHT_O = 385;
	private double MATERIAL_KNIGHT_E = 385;
	
	private double MATERIAL_BISHOP_O = 385;
	private double MATERIAL_BISHOP_E = 385;
	
	private double MATERIAL_ROOK_O = 385;
	private double MATERIAL_ROOK_E = 385; //540 //602
	
	private double MATERIAL_QUEEN_O = 593;
	private double MATERIAL_QUEEN_E = 593; //1220
	
	private double MATERIAL_KING_O = 1244;
	private double MATERIAL_KING_E = 1244;

	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	private static final double[] KING_O			= Utils.reverseSpecial(new double[] {
			-22,-35,-40,-40,-40,-40,-35,-22,
            -22,-35,-40,-40,-40,-40,-35,-22,
            -25,-35,-40,-45,-45,-40,-35,-25,
            -15,-30,-35,-40,-40,-35,-30,-15,
            -10,-15,-20,-25,-25,-20,-15,-10,
              4, -2, -5,-15,-15, -5, -2,  4,
             16, 14,  7, -3, -3,  7, 14, 16,
             24, 24,  9,  0,  0,  9, 24, 24   
			});
	
	private static final double[] KING_E			= Utils.reverseSpecial(new double[] {
			0,  8, 16, 24, 24, 16,  8,  0,
            8, 16, 24, 32, 32, 24, 16,  8,
           16, 24, 32, 40, 40, 32, 24, 16,
           24, 32, 40, 48, 48, 40, 32, 24,
           24, 32, 40, 48, 48, 40, 32, 24,
           16, 24, 32, 40, 40, 32, 24, 16,
            8, 16, 24, 32, 32, 24, 16,  8,
            0,  8, 16, 24, 24, 16,  8,  0  
			});
	
	private static final double[] PAWN_O			= Utils.reverseSpecial(new double[] {
			0,  0,  0,  0,  0,  0,  0,  0,
            8, 16, 24, 32, 32, 24, 16,  8,
            3, 12, 20, 28, 28, 20, 12,  3,
           -5,  4, 10, 20, 20, 10,  4, -5,
           -6,  4,  5, 16, 16,  5,  4, -6,
           -6,  4,  2,  5,  5,  2,  4, -6,
           -6,  4,  4,-15,-15,  4,  4, -6,
            0,  0,  0,  0,  0,  0,  0,  0 
			});
	
	private static final double[] PAWN_E			= Utils.reverseSpecial(new double[] {
			0,  0,  0,  0,  0,  0,  0,  0,
            25, 40, 45, 45, 45, 45, 40, 25,
            17, 32, 35, 35, 35, 35, 32, 17,
             5, 24, 24, 24, 24, 24, 24,  5,
            -9, 11, 11, 11, 11, 11, 11, -9,
           -17,  3,  3,  3,  3,  3,  3,-17,
           -20,  0,  0,  0,  0,  0,  0,-20,
             0,  0,  0,  0,  0,  0,  0,  0  
			});
	
	public static final double[] KNIGHT_O			= Utils.reverseSpecial(new double[] {
			-53,-42,-32,-21,-21,-32,-42,-53,
            -42,-32,-10,  0,  0,-10,-32,-42,
            -21,  5, 10, 16, 16, 10,  5,-21,
            -18,  0, 10, 21, 21, 10,  0,-18,
            -18,  0,  3, 21, 21,  3,  0,-18,
            -21,-10,  0,  0,  0,  0,-10,-21,
            -42,-32,-10,  0,  0,-10,-32,-42,
            -53,-42,-32,-21,-21,-32,-42,-53  
			});
	
	private static final double[] KNIGHT_E			= Utils.reverseSpecial(new double[] {
			 -56,-44,-34,-22,-22,-34,-44,-56,
             -44,-34,-10,  0,  0,-10,-34,-44,
             -22,  5, 10, 17, 17, 10,  5,-22,
             -19,  0, 10, 22, 22, 10,  0,-19,
             -19,  0,  3, 22, 22,  3,  0,-19,
             -22,-10,  0,  0,  0,  0,-10,-22,
             -44,-34,-10,  0,  0,-10,-34,-44,
             -56,-44,-34,-22,-22,-34,-44,-56  
			});
	
	public static final double[] BISHOP_O			= Utils.reverseSpecial(new double[] {
			 0,  0,  0,  0,  0,  0,  0,  0,
             0,  4,  2,  2,  2,  2,  4,  0,
             0,  2,  4,  4,  4,  4,  2,  0,
             0,  2,  4,  4,  4,  4,  2,  0,
             0,  2,  4,  4,  4,  4,  2,  0,
             0,  3,  4,  4,  4,  4,  3,  0,
             0,  4,  2,  2,  2,  2,  4,  0,
            -5, -5, -7, -5, -5, -7, -5, -5
			});
	
	private static final double[] BISHOP_E			= Utils.reverseSpecial(new double[] {
			0,  0,  0,  0,  0,  0,  0,  0,
            0,  2,  2,  2,  2,  2,  2,  0,
            0,  2,  4,  4,  4,  4,  2,  0,
            0,  2,  4,  4,  4,  4,  2,  0,
            0,  2,  4,  4,  4,  4,  2,  0,
            0,  2,  4,  4,  4,  4,  2,  0,
            0,  2,  2,  2,  2,  2,  2,  0,
            0,  0,  0,  0,  0,  0,  0,  0  
			});
	
	private static final double[] ROOK_O			= Utils.reverseSpecial(new double[] {
			0,  3,  5,  5,  5,  5,  3,  0,
            15, 20, 20, 20, 20, 20, 20, 15,
             0,  0,  0,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0,  0,  0,  0,
            -2,  0,  0,  0,  0,  0,  0, -2,
            -2,  0,  0,  2,  2,  0,  0, -2,
            -3,  2,  5,  5,  5,  5,  2, -3,
             0,  3,  5,  5,  5,  5,  3,  0  
			});

	private static final double[] ROOK_E			= zeros;

	private static final double[] QUEEN_O			= Utils.reverseSpecial(new double[] {
			-10, -5,  0,  0,  0,  0, -5,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
             0,  5,  5,  6,  6,  5,  5,  0,
             0,  5,  6,  6,  6,  6,  5,  0,
             0,  5,  6,  6,  6,  6,  5,  0,
             0,  5,  5,  6,  6,  5,  5,  0,
            -5,  0,  5,  5,  5,  5,  0, -5,
           -10, -5,  0,  0,  0,  0, -5,-10   
			});

	private static final double[] QUEEN_E			= zeros;
	
	
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
