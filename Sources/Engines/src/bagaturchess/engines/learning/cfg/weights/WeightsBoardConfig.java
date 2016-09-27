package bagaturchess.engines.learning.cfg.weights;


import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.learning.impl.filler.SignalFillerConstants;


public class WeightsBoardConfig implements IBoardConfig {
	
	
	public double MATERIAL_PAWN_O	=	64.75080071928197;
	public double MATERIAL_PAWN_E	=	77.53525162786624;

	public double MATERIAL_KNIGHT_O	=	333.6746827139804;
	public double MATERIAL_KNIGHT_E	=	317.1892865063512;

	public double MATERIAL_BISHOP_O	=	361.3342555595208;
	public double MATERIAL_BISHOP_E	=	321.77115331599146;

	public double MATERIAL_ROOK_O	=	448.90806056762875;
	public double MATERIAL_ROOK_E	=	536.9497924131576;

	public double MATERIAL_QUEEN_O	=	1072.3475721290154;
	public double MATERIAL_QUEEN_E	=	957.1414302489349;
	
	public double MATERIAL_KING_O = 2000;
	public double MATERIAL_KING_E = 2000;
	
	public double MATERIAL_BARIER_NOPAWNS_O	= Math.max(MATERIAL_KNIGHT_O, MATERIAL_BISHOP_O) + MATERIAL_PAWN_O;
	public double MATERIAL_BARIER_NOPAWNS_E	= Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E) + MATERIAL_PAWN_E;
	
	public double PST_PAWN_O	=	0.7161581238840979;
	public double PST_PAWN_E	=	0.815180822137494;

	public double PST_KING_O	=	1.1697774766425277;
	public double PST_KING_E	=	1.11975105253063;

	public double PST_KNIGHT_O	=	0.9661744034826641;
	public double PST_KNIGHT_E	=	1.0048734975653686;

	public double PST_BISHOP_O	=	0.6828676891673773;
	public double PST_BISHOP_E	=	1.8601681397361356;

	public double PST_ROOK_O	=	0.7370084728426224;
	public double PST_ROOK_E	=	1.089103585542932;

	public double PST_QUEEN_O	=	0.28913087002821136;
	public double PST_QUEEN_E	=	1.0911584966785814;
    
	
	public WeightsBoardConfig() {
		// TODO Auto-generated constructor stub
	}
	
	
	public WeightsBoardConfig(String[] args) {
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
