package bagaturchess.learning.impl.eval.cfg;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.learning.impl.filler.SignalFillerConstants;


public class LearningBoardConfigImpl implements IBoardConfig {
	
	
	protected double MATERIAL_PAWN_O = 96;
	protected double MATERIAL_PAWN_E = 103;
	
	protected double MATERIAL_KNIGHT_O = 314;
	protected double MATERIAL_KNIGHT_E = 292;
	
	protected double MATERIAL_BISHOP_O = 291;
	protected double MATERIAL_BISHOP_E = 300;
	
	protected double MATERIAL_ROOK_O = 495;
	protected double MATERIAL_ROOK_E = 526;
	
	protected double MATERIAL_QUEEN_O = 1265;
	protected double MATERIAL_QUEEN_E = 979;
	
	protected double MATERIAL_KING_O = 941;
	protected double MATERIAL_KING_E = 997;
	
	protected double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	protected double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	
	
	public boolean getFieldsStatesSupport() {
		return false;
		//return true;
	}
	
	
	@Override
	public double[] getPST_PAWN_O() {
		return SignalFillerConstants.PAWN_O;
	}
	
	@Override
	public double[] getPST_PAWN_E() {
		return SignalFillerConstants.PAWN_E;
	}
	
	@Override
	public double[] getPST_KING_O() {
		return SignalFillerConstants.KING_O;
	}
	
	@Override
	public double[] getPST_KING_E() {
		return SignalFillerConstants.KING_E;
	}
	
	@Override
	public double[] getPST_KNIGHT_O() {
		return SignalFillerConstants.KNIGHT_O;
	}
	
	@Override
	public double[] getPST_KNIGHT_E() {
		return SignalFillerConstants.KNIGHT_E;
	}
	
	@Override
	public double[] getPST_BISHOP_O() {
		return SignalFillerConstants.BISHOP_O;
	}
	
	@Override
	public double[] getPST_BISHOP_E() {
		return SignalFillerConstants.BISHOP_E;
	}
	
	@Override
	public double[] getPST_ROOK_O() {
		return SignalFillerConstants.ROOK_O;
	}
	
	@Override
	public double[] getPST_ROOK_E() {
		return SignalFillerConstants.ROOK_E;
	}
	
	@Override
	public double[] getPST_QUEEN_O() {
		return SignalFillerConstants.QUEEN_O;
	}
	
	@Override
	public double[] getPST_QUEEN_E() {
		return SignalFillerConstants.QUEEN_E;
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
