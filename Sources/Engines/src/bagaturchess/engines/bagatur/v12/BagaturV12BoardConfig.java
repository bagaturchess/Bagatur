package bagaturchess.engines.bagatur.v12;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.learning.impl.filler.SignalFillerConstants;


public class BagaturV12BoardConfig implements IBoardConfig {
	
	
    public double MATERIAL_PAWN_O = 88.53199920069409;
    public double MATERIAL_PAWN_E = 110.02070622910514;
    public double MATERIAL_KNIGHT_O = 430.49111046321144;
    public double MATERIAL_KNIGHT_E = 391.9227332285586;
    public double MATERIAL_BISHOP_O = 443.634581290182;
    public double MATERIAL_BISHOP_E = 425.5057547760457;
    public double MATERIAL_ROOK_O = 616.3349023784737;
    public double MATERIAL_ROOK_E = 685.5644647191372;
    public double MATERIAL_QUEEN_O = 1341.269951331634;
    public double MATERIAL_QUEEN_E = 1250.323241281083;
	
	public double MATERIAL_KING_O = 2000;
	public double MATERIAL_KING_E = 2000;
	
	public double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	public double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	public double PST_PAWN_O = 0.5710827986385989;
	public double PST_PAWN_E = 0.9927040298136856;
	public double PST_KING_O = 1.4152868740093765;
    public double PST_KING_E = 1.6886842944327374;
    public double PST_KNIGHT_O = 1.020352936358533;
    public double PST_KNIGHT_E = 1.2065438212857906;
    public double PST_BISHOP_O = 1.1449938687083554;
    public double PST_BISHOP_E = 1.587546500773107;
    public double PST_ROOK_O = 1.862971446158503;
    public double PST_ROOK_E = 0.7278444261035139;
    public double PST_QUEEN_O = 0.10831914054754384;
    public double PST_QUEEN_E = 0.8606995592185696;
    
	
	public BagaturV12BoardConfig() {
		// TODO Auto-generated constructor stub
	}
	
	
	public BagaturV12BoardConfig(String[] args) {
		// TODO Auto-generated constructor stub
	}


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
		return PST_PAWN_O;
	}
	
	
	@Override
	public double getWeight_PST_PAWN_E() {
		return PST_PAWN_E;
	}
	
	
	@Override
	public double getWeight_PST_KING_O() {
		return PST_KING_O;
	}
	
	
	@Override
	public double getWeight_PST_KING_E() {
		return PST_KING_E;
	}
	
	
	@Override
	public double getWeight_PST_KNIGHT_O() {
		return PST_KNIGHT_O;
	}
	
	
	@Override
	public double getWeight_PST_KNIGHT_E() {
		return PST_KNIGHT_E;
	}
	
	
	@Override
	public double getWeight_PST_BISHOP_O() {
		return PST_BISHOP_O;
	}
	
	
	@Override
	public double getWeight_PST_BISHOP_E() {
		return PST_BISHOP_E;
	}
	
	
	@Override
	public double getWeight_PST_ROOK_O() {
		return PST_ROOK_O;
	}
	
	
	@Override
	public double getWeight_PST_ROOK_E() {
		return PST_ROOK_E;
	}
	
	
	@Override
	public double getWeight_PST_QUEEN_O() {
		return PST_QUEEN_O;
	}
	
	
	@Override
	public double getWeight_PST_QUEEN_E() {
		return PST_QUEEN_E;
	}
}
