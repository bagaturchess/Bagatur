package bagaturchess.learning.impl.eval.cfg;


public interface Weights_Manual {

	public static final double PENETRATION_OP_O	=	0.3;
	public static final double PENETRATION_OP_E	=	0;

	public static final double PENETRATION_OP_S_O	=	0.7;
	public static final double PENETRATION_OP_S_E	=	0;
	
	public static final double PENETRATION_KING_O	=	1;
	public static final double PENETRATION_KING_E	=	0;

	public static final double PENETRATION_KING_S_O	=	2;
	public static final double PENETRATION_KING_S_E	=	0;
	
	public static final double KINGSAFE_FIANCHETTO_O	=	10.0;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;
	
	public static final double KINGS_DISTANCE_O	=	0;
	public static final double KINGS_DISTANCE_E	=	2;
	
	public static final double ROOK_BEHIND_PASSER_O	=	10;
	public static final double ROOK_BEHIND_PASSER_E	=	20;

	public static final double TRAP_KNIGHT_O	=	-3;
	public static final double TRAP_KNIGHT_E	=	-4;
	public static final double TRAP_BISHOP_O	=	-3;
	public static final double TRAP_BISHOP_E	=	-4;
	public static final double TRAP_ROOK_O		=	-3;
	public static final double TRAP_ROOK_E		=	-4;
	public static final double TRAP_QUEEN_O		=	-3;
	public static final double TRAP_QUEEN_E		=	-4;
	
	public static final double PIN_BK_O	=	30;
	public static final double PIN_BK_E	=	30;
	public static final double PIN_BQ_O	=	20;
	public static final double PIN_BQ_E	=	20;
	public static final double PIN_BR_O	=	20;
	public static final double PIN_BR_E	=	20;
	public static final double PIN_BN_O	=	15;
	public static final double PIN_BN_E	=	15;
	public static final double PIN_RK_O	=	30;
	public static final double PIN_RK_E	=	30;
	public static final double PIN_RQ_O	=	20;
	public static final double PIN_RQ_E	=	20;
	public static final double PIN_RB_O	=	10;
	public static final double PIN_RB_E	=	10;
	public static final double PIN_RN_O	=	10;
	public static final double PIN_RN_E	=	10;
	public static final double PIN_QK_O	=	30;
	public static final double PIN_QK_E	=	30;
	public static final double PIN_QQ_O	=	0;
	public static final double PIN_QQ_E	=	0;
	public static final double PIN_QN_O	=	10;
	public static final double PIN_QN_E	=	10;
	public static final double PIN_QR_O	=	10;
	public static final double PIN_QR_E	=	10;
	public static final double PIN_QB_O	=	10;
	public static final double PIN_QB_E	=	10;
	
	public static final double ATTACK_BN_O	=	20;
	public static final double ATTACK_BN_E	=	20;
	public static final double ATTACK_BR_O	=	30;
	public static final double ATTACK_BR_E	=	30;
	public static final double ATTACK_NB_O	=	20;
	public static final double ATTACK_NB_E	=	20;
	public static final double ATTACK_NR_O	=	30;
	public static final double ATTACK_NR_E	=	30;
	public static final double ATTACK_NQ_O	=	30;
	public static final double ATTACK_NQ_E	=	30;
	public static final double ATTACK_RB_O	=	15;
	public static final double ATTACK_RB_E	=	15;
	public static final double ATTACK_RN_O	=	15;
	public static final double ATTACK_RN_E	=	15;
	public static final double ATTACK_QN_O	=	15;
	public static final double ATTACK_QN_E	=	15;
	public static final double ATTACK_QB_O	=	15;
	public static final double ATTACK_QB_E	=	15;
	public static final double ATTACK_QR_O	=	15;
	public static final double ATTACK_QR_E	=	15;
	
	public static final double HUNGED_PIECE_1_O	=	-30;
	public static final double HUNGED_PIECE_1_E	=	-30;
	public static final double HUNGED_PIECE_2_O	=	-70;
	public static final double HUNGED_PIECE_2_E	=	-70;
	public static final double HUNGED_PIECE_3_O	=	-120;
	public static final double HUNGED_PIECE_3_E	=	-120;
	public static final double HUNGED_PIECE_4_O	=	-180;
	public static final double HUNGED_PIECE_4_E	=	-180;
	public static final double HUNGED_PAWNS_1_O	=	-20;
	public static final double HUNGED_PAWNS_1_E	=	-20;
	public static final double HUNGED_PAWNS_2_O	=	-40;
	public static final double HUNGED_PAWNS_2_E	=	-40;
	public static final double HUNGED_PAWNS_3_O	=	-70;
	public static final double HUNGED_PAWNS_3_E	=	-70;
	public static final double HUNGED_PAWNS_4_O	=	-110;
	public static final double HUNGED_PAWNS_4_E	=	-110;
	public static final double HUNGED_ALL_1_O	=	-10;
	public static final double HUNGED_ALL_1_E	=	-10;
	public static final double HUNGED_ALL_2_O	=	-20;
	public static final double HUNGED_ALL_2_E	=	-20;
	public static final double HUNGED_ALL_3_O	=	-40;
	public static final double HUNGED_ALL_3_E	=	-40;
	public static final double HUNGED_ALL_4_O	=	-80;
	public static final double HUNGED_ALL_4_E	=	-80;
}
