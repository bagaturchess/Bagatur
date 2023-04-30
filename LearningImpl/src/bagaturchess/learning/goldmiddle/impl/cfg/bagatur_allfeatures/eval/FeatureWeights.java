package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights {
	
	
	public static final double MATERIAL_DOUBLE_BISHOPS_O	=	23.160637259783936;
	public static final double MATERIAL_DOUBLE_BISHOPS_E	=	47.95543551002477;

	public static final double PST_O	=	0.7005886059399954;
	public static final double PST_E	=	1.0169995123248852;

	public static final double STANDARD_TEMPO_O	=	1.0555542551192472;
	public static final double STANDARD_TEMPO_E	=	0.8607216609863126;

	public static final double STANDARD_CASTLING_O	=	9.4439389370765;
	public static final double STANDARD_CASTLING_E	=	0.0;

	public static final double STANDARD_FIANCHETTO_O	=	21.89654443923605;
	public static final double STANDARD_FIANCHETTO_E	=	0.0;

	public static final double STANDARD_TRAP_BISHOP_O	=	-120;
	public static final double STANDARD_TRAP_BISHOP_E	=	0.0;

	public static final double STANDARD_BLOCKED_PAWN_O	=	-12.62812427552849;
	public static final double STANDARD_BLOCKED_PAWN_E	=	0.0;

	public static final double STANDARD_KINGS_OPPOSITION_O	=	0.0;
	public static final double STANDARD_KINGS_OPPOSITION_E	=	50;

	public static final double PAWNS_KING_GUARDS_O	=	2.2949734439359717;
	public static final double PAWNS_KING_GUARDS_E	=	0.0;

	public static final double PAWNS_DOUBLED_O	=	1.2170705071004482;
	public static final double PAWNS_DOUBLED_E	=	0.06020367330907593;

	public static final double PAWNS_ISOLATED_O	=	0.8617507229779382;
	public static final double PAWNS_ISOLATED_E	=	0.6073075784591521;

	public static final double PAWNS_BACKWARD_O	=	0.7573046791766854;
	public static final double PAWNS_BACKWARD_E	=	1.2804161411141735;

	public static final double PAWNS_SUPPORTED_O	=	1.1541628562390034;
	public static final double PAWNS_SUPPORTED_E	=	0.06368102777403248;

	public static final double PAWNS_CANDIDATE_O	=	0.465730286715969;
	public static final double PAWNS_CANDIDATE_E	=	0.34367226663121053;

	public static final double PAWNS_PASSED_SUPPORTED_O	=	1.9231860375068914;
	public static final double PAWNS_PASSED_SUPPORTED_E	=	0.9053918195106929;

	public static final double PAWNS_PASSED_O	=	2.788889364235212;
	public static final double PAWNS_PASSED_E	=	1;

	public static final double PAWNS_KING_F_O	=	0.0;
	public static final double PAWNS_KING_F_E	=	4.43081150417076;

	public static final double PAWNS_KING_FF_O	=	0.0;
	public static final double PAWNS_KING_FF_E	=	2.4198151737510174;

	public static final double PAWNS_KING_OP_F_O	=	0.0;
	public static final double PAWNS_KING_OP_F_E	=	2;

	public static final double PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PASSED_UNSTOPPABLE_E	=	550;

	public static final double PAWNS_PASSED_STOPPERS_O	=	0.0;
	public static final double PAWNS_PASSED_STOPPERS_E	=	0.0;

	public static final double PAWNS_ROOK_OPENED_O	=	19.450185970289013;
	public static final double PAWNS_ROOK_OPENED_E	=	1.012873536820457;

	public static final double PAWNS_ROOK_SEMIOPENED_O	=	4.04418564473244;
	public static final double PAWNS_ROOK_SEMIOPENED_E	=	1.6489256726828572;

	public static final double PAWNS_ROOK_7TH2TH_O	=	13.574836146809437;
	public static final double PAWNS_ROOK_7TH2TH_E	=	3.653435217598271;

	public static final double PAWNS_QUEEN_7TH2TH_O	=	0.11832457007382421;
	public static final double PAWNS_QUEEN_7TH2TH_E	=	6.591055338678529;

	public static final double PAWNS_KING_OPENED_O	=	-12.157630916261184;
	public static final double PAWNS_KING_OPENED_E	=	6.528499068208229;

	public static final double MOBILITY_KNIGHT_O	=	1.9174823628170556;
	public static final double MOBILITY_KNIGHT_E	=	1.0390797703403882;

	public static final double MOBILITY_BISHOP_O	=	1.5217100516754685;
	public static final double MOBILITY_BISHOP_E	=	1.0205809226604579;

	public static final double MOBILITY_ROOK_O	=	1.1685760851266969;
	public static final double MOBILITY_ROOK_E	=	2.5111606365686217;

	public static final double MOBILITY_QUEEN_O	=	0.5269853984695055;
	public static final double MOBILITY_QUEEN_E	=	0.5086489426838672;

	public static final double KNIGHT_OUTPOST_O	=	2.668362597235545;
	public static final double KNIGHT_OUTPOST_E	=	0.018290685416386607;

	public static final double BISHOP_OUTPOST_O	=	1.2171624931577236;
	public static final double BISHOP_OUTPOST_E	=	0.6010578523791601;

	public static final double BISHOP_BAD_O	=	1.5135402931027537;
	public static final double BISHOP_BAD_E	=	0.4361782243950871;

	public static final double KING_SAFETY_O	=	2.666090839677147;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	0.49178379572950587;
	public static final double SPACE_E	=	0.3546766082489865;

	public static final double HUNGED_O	=	1.3141203788601261;
	public static final double HUNGED_E	=	1.8130256928868909;

	public static final double MOBILITY_KNIGHT_S_O	=	0.6733328670206458;
	public static final double MOBILITY_KNIGHT_S_E	=	0.9140474918712359;

	public static final double MOBILITY_BISHOP_S_O	=	1.4863247524029488;
	public static final double MOBILITY_BISHOP_S_E	=	0.7649825338569627;

	public static final double MOBILITY_ROOK_S_O	=	1.0384314593650399;
	public static final double MOBILITY_ROOK_S_E	=	0.33498040762538084;

	public static final double MOBILITY_QUEEN_S_O	=	4.077723481082999;
	public static final double MOBILITY_QUEEN_S_E	=	2.8239286707315814;

	public static final double TRAPED_O	=	0.0;
	public static final double TRAPED_E	=	0.0;

	public static final double PASSERS_FRONT_ATTACKS_O	=	0.01700756067678029;
	public static final double PASSERS_FRONT_ATTACKS_E	=	4.176159341707151;
}
