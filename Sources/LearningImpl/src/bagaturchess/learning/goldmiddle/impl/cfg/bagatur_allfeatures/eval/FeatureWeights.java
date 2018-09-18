package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights {
	
	
	public static final double MATERIAL_DOUBLE_BISHOPS_O	=	21.362022906674554;
	public static final double MATERIAL_DOUBLE_BISHOPS_E	=	44.140522940085454;

	public static final double PST_O	=	1.014641489847541;
	public static final double PST_E	=	0.693892476658763;

	public static final double STANDARD_TEMPO_O	=	0.0;
	public static final double STANDARD_TEMPO_E	=	0.0;

	public static final double STANDARD_CASTLING_O	=	11.356403752359714;
	public static final double STANDARD_CASTLING_E	=	0.0;

	public static final double STANDARD_FIANCHETTO_O	=	31.934434664670082;
	public static final double STANDARD_FIANCHETTO_E	=	0.0;

	public static final double STANDARD_TRAP_BISHOP_O	=	-120;
	public static final double STANDARD_TRAP_BISHOP_E	=	0.0;

	public static final double STANDARD_BLOCKED_PAWN_O	=	-11.333036839123494;
	public static final double STANDARD_BLOCKED_PAWN_E	=	0.0;

	public static final double STANDARD_KINGS_OPPOSITION_O	=	0.0;
	public static final double STANDARD_KINGS_OPPOSITION_E	=	50;

	public static final double PAWNS_KING_GUARDS_O	=	1.8082127492779472;
	public static final double PAWNS_KING_GUARDS_E	=	0.0;

	public static final double PAWNS_DOUBLED_O	=	1.2999729215841807;
	public static final double PAWNS_DOUBLED_E	=	0.0;

	public static final double PAWNS_ISOLATED_O	=	0.7330641556111389;
	public static final double PAWNS_ISOLATED_E	=	0.7316225900094305;

	public static final double PAWNS_BACKWARD_O	=	0.0;
	public static final double PAWNS_BACKWARD_E	=	0.2683279968565827;

	public static final double PAWNS_SUPPORTED_O	=	2.3221658486355223;
	public static final double PAWNS_SUPPORTED_E	=	0.0;

	public static final double PAWNS_CANDIDATE_O	=	0.0;
	public static final double PAWNS_CANDIDATE_E	=	0.0;

	public static final double PAWNS_PASSED_SUPPORTED_O	=	1.4273034754000777;
	public static final double PAWNS_PASSED_SUPPORTED_E	=	0.6817482653761819;

	public static final double PAWNS_PASSED_O	=	1.547372687449966;
	public static final double PAWNS_PASSED_E	=	1;

	public static final double PAWNS_KING_F_O	=	0.0;
	public static final double PAWNS_KING_F_E	=	5.585158802720236;

	public static final double PAWNS_KING_FF_O	=	0.0;
	public static final double PAWNS_KING_FF_E	=	0.0;

	public static final double PAWNS_KING_OP_F_O	=	0.0;
	public static final double PAWNS_KING_OP_F_E	=	0.0;

	public static final double PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PASSED_UNSTOPPABLE_E	=	550;

	public static final double PAWNS_PASSED_STOPPERS_O	=	0.0;
	public static final double PAWNS_PASSED_STOPPERS_E	=	0.0;

	public static final double PAWNS_ROOK_OPENED_O	=	21.0635776055622;
	public static final double PAWNS_ROOK_OPENED_E	=	0.0;

	public static final double PAWNS_ROOK_SEMIOPENED_O	=	4.80143738306778;
	public static final double PAWNS_ROOK_SEMIOPENED_E	=	0.0;

	public static final double PAWNS_ROOK_7TH2TH_O	=	0.6036675712863798;
	public static final double PAWNS_ROOK_7TH2TH_E	=	11.490558629870621;

	public static final double PAWNS_QUEEN_7TH2TH_O	=	0.0;
	public static final double PAWNS_QUEEN_7TH2TH_E	=	0.0;

	public static final double PAWNS_KING_OPENED_O	=	-11.621036297136985;
	public static final double PAWNS_KING_OPENED_E	=	6.628623098708186;

	public static final double MOBILITY_KNIGHT_O	=	2.560188927021664;
	public static final double MOBILITY_KNIGHT_E	=	0.9234852467865711;

	public static final double MOBILITY_BISHOP_O	=	1.5680040238937043;
	public static final double MOBILITY_BISHOP_E	=	0.8735865674825682;

	public static final double MOBILITY_ROOK_O	=	1.8041117249099046;
	public static final double MOBILITY_ROOK_E	=	1.7879028311914311;

	public static final double MOBILITY_QUEEN_O	=	1.3713156349148121;
	public static final double MOBILITY_QUEEN_E	=	0.0;

	public static final double KNIGHT_OUTPOST_O	=	1.5968934501084704;
	public static final double KNIGHT_OUTPOST_E	=	0.0;

	public static final double BISHOP_OUTPOST_O	=	0.0;
	public static final double BISHOP_OUTPOST_E	=	0.0;

	public static final double BISHOP_BAD_O	=	3.0767360611700356;
	public static final double BISHOP_BAD_E	=	0.2532908456440789;

	public static final double KING_SAFETY_O	=	2.9043523078475593;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	0.78816769319685;
	public static final double SPACE_E	=	0.20859555922658685;

	public static final double HUNGED_O	=	2.0934555479308083;
	public static final double HUNGED_E	=	1.315727963682778;

	public static final double MOBILITY_KNIGHT_S_O	=	0.7512195230646853;
	public static final double MOBILITY_KNIGHT_S_E	=	0.709261381784784;

	public static final double MOBILITY_BISHOP_S_O	=	2.1488194147992696;
	public static final double MOBILITY_BISHOP_S_E	=	0.22053681919182752;

	public static final double MOBILITY_ROOK_S_O	=	0.0;
	public static final double MOBILITY_ROOK_S_E	=	0.6579804987632654;

	public static final double MOBILITY_QUEEN_S_O	=	7.087468068275545;
	public static final double MOBILITY_QUEEN_S_E	=	0.0;

	public static final double TRAPED_O	=	0.0;
	public static final double TRAPED_E	=	0.0;

	public static final double PASSERS_FRONT_ATTACKS_O	=	0.0;
	public static final double PASSERS_FRONT_ATTACKS_E	=	3.422842036663984;
}
