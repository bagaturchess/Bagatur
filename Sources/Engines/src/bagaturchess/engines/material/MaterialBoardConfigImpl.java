package bagaturchess.engines.material;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;


public class MaterialBoardConfigImpl implements IBoardConfig {
	
	
	private double MATERIAL_PAWN_O = 100;
	private double MATERIAL_PAWN_E = 100;
		
	private double MATERIAL_KNIGHT_O = 300;
	private double MATERIAL_KNIGHT_E = 300;
	
	private double MATERIAL_BISHOP_O = 300;
	private double MATERIAL_BISHOP_E = 300;
	
	private double MATERIAL_ROOK_O = 500;
	private double MATERIAL_ROOK_E = 500;
	
	private double MATERIAL_QUEEN_O = 900;
	private double MATERIAL_QUEEN_E = 900;
	
	private double MATERIAL_KING_O = 1600;
	private double MATERIAL_KING_E = 1600;

	
	private double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	private double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	
	private static final double[] ZEROS			= Utils.reverseSpecial(new double[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0, 
			0,   0,   0,   0,   0,   0,   0,   0,  
			0,   0,   0,   0,   0,   0,   0,   0,
			0,   0,   0,   0,   0,   0,   0,   0,
			0,   0,   0,   0,   0,   0,   0,   0,
			0,   0,   0,   0,   0,   0,   0,   0,
			0,   0,   0,   0,   0,   0,   0,   0,
			});
	
	
	public boolean getFieldsStatesSupport() {
		return false;
	}
	
	
	@Override
	public double[] getPST_PAWN_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_PAWN_E() {
		return ZEROS;
	}

	@Override
	public double[] getPST_KING_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_KING_E() {
		return ZEROS;
	}

	@Override
	public double[] getPST_KNIGHT_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_KNIGHT_E() {
		return ZEROS;
	}

	@Override
	public double[] getPST_BISHOP_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_BISHOP_E() {
		return ZEROS;
	}

	@Override
	public double[] getPST_ROOK_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_ROOK_E() {
		return ZEROS;
	}

	@Override
	public double[] getPST_QUEEN_O() {
		return ZEROS;
	}

	@Override
	public double[] getPST_QUEEN_E() {
		return ZEROS;
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
