package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.eval.weights;


/*
 * Weaker with 115 ELO vs Bagatur Eval
 */

public interface Weights_SF7 {
	public static final double MATERIAL_PAWN_O	=	60.846650684082576;
	public static final double MATERIAL_PAWN_E	=	81.98633311013695;

	public static final double MATERIAL_KNIGHT_O	=	339.2368870350284;
	public static final double MATERIAL_KNIGHT_E	=	307.46589745375144;

	public static final double MATERIAL_BISHOP_O	=	350.3295960632247;
	public static final double MATERIAL_BISHOP_E	=	294.2812903320736;

	public static final double MATERIAL_ROOK_O	=	458.3790766305261;
	public static final double MATERIAL_ROOK_E	=	486.90179420315917;

	public static final double MATERIAL_QUEEN_O	=	1078.9019540039876;
	public static final double MATERIAL_QUEEN_E	=	812.9810025556329;

	public static final double KINGSAFE_CASTLING_O	=	7.847088550561575;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	2.007878680245523;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	24.16363693512937;
	public static final double BISHOPS_DOUBLE_E	=	56.410495969352965;

	public static final double KNIGHTS_DOUBLE_O	=	2.7095600408293596;
	public static final double KNIGHTS_DOUBLE_E	=	3.347180583883046;

	public static final double ROOKS_DOUBLE_O	=	30.275788042557565;
	public static final double ROOKS_DOUBLE_E	=	15.993825215232961;

	public static final double ROOKS_5PAWNS_O	=	1.0632318652435069;
	public static final double ROOKS_5PAWNS_E	=	0.48269074439710724;

	public static final double KNIGHTS_5PAWNS_O	=	1.4052377971657704;
	public static final double KNIGHTS_5PAWNS_E	=	9.810111664471384;

	public static final double KINGSAFE_F_O	=	-7.36103923331502;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-10.986634425126203;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	-0.13878895110134234;
	public static final double KINGS_DISTANCE_E	=	0.018481933728698625;

	public static final double PAWNS_DOUBLED_O	=	-0.8418432685728588;
	public static final double PAWNS_DOUBLED_E	=	-3.127038580202888;

	public static final double PAWNS_ISOLATED_O	=	-10.246175865795227;
	public static final double PAWNS_ISOLATED_E	=	-11.19619813515305;

	public static final double PAWNS_BACKWARD_O	=	-4.262462250601169;
	public static final double PAWNS_BACKWARD_E	=	-1.0427274345515851;

	public static final double PAWNS_SUPPORTED_O	=	4.445453856867746;
	public static final double PAWNS_SUPPORTED_E	=	7.135720860346474;

	public static final double PAWNS_CANNOTBS_O	=	-2.150575221555373;
	public static final double PAWNS_CANNOTBS_E	=	-2.0875807706342626;

	public static final double PAWNS_PASSED_O	=	8.779994645537341;
	public static final double PAWNS_PASSED_E	=	3.8718914394891772;

	public static final double PAWNS_PASSED_RNK_O	=	0.5735431957136053;
	public static final double PAWNS_PASSED_RNK_E	=	0.8403462953183584;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_PSTOPPERS_O	=	0.11754183129495135;
	public static final double PAWNS_PSTOPPERS_E	=	0.8707358963875771;

	public static final double PAWNS_CANDIDATE_RNK_O	=	0.38039154613861925;
	public static final double PAWNS_CANDIDATE_RNK_E	=	0.31214677620005293;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	1.3643959384225988;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.7339430523909631;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.8649629491337119;

	public static final double PAWNS_ISLANDS_O	=	-0.873504485727804;
	public static final double PAWNS_ISLANDS_E	=	-0.264240660402547;

	public static final double PAWNS_GARDS_O	=	4.58676220732068;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-4.4471554383672425;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	0.5614800671169209;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	4.351553551677859;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-41.05463506403343;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-25.88472944129923;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-16.002691673419086;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-3.2485941787524553;
	public static final double PAWNS_WEAK_E	=	-0.5996726599097331;

	public static final double SPACE_O	=	0.9103996275835978;
	public static final double SPACE_E	=	0.49536362633253017;

	public static final double ROOK_INFRONT_PASSER_O	=	-0.012510159034893007;
	public static final double ROOK_INFRONT_PASSER_E	=	0.0;

	public static final double ROOK_BEHIND_PASSER_O	=	0.8171224048702599;
	public static final double ROOK_BEHIND_PASSER_E	=	5.1250067998619615;

	public static final double PST_PAWN_O	=	0.8182280994072413;
	public static final double PST_PAWN_E	=	0.7321939310980201;

	public static final double PST_KING_O	=	1.1523918770371369;
	public static final double PST_KING_E	=	0.9136072678356588;

	public static final double PST_KNIGHTS_O	=	0.7879210743121581;
	public static final double PST_KNIGHTS_E	=	0.6293832247062117;

	public static final double PST_BISHOPS_O	=	0.7791796852485127;
	public static final double PST_BISHOPS_E	=	0.4664046776537755;

	public static final double PST_ROOKS_O	=	0.840100143377947;
	public static final double PST_ROOKS_E	=	0.6050560028545547;

	public static final double PST_QUEENS_O	=	0.20925710834410435;
	public static final double PST_QUEENS_E	=	0.9165893496622888;

	public static final double BISHOPS_BAD_O	=	-1.0966565273779976;
	public static final double BISHOPS_BAD_E	=	-1.0141945112618729;

	public static final double KNIGHT_OUTPOST_O	=	12.855357360841147;
	public static final double KNIGHT_OUTPOST_E	=	0.178501029451718;

	public static final double ROOKS_OPENED_O	=	20.4545437416856;
	public static final double ROOKS_OPENED_E	=	0.37328273655738836;

	public static final double ROOKS_SEMIOPENED_O	=	6.995415332149187;
	public static final double ROOKS_SEMIOPENED_E	=	9.121412483618116;

	public static final double TROPISM_KNIGHT_O	=	0.08077764659428596;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.291075802234031;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	0.34491597675614694;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	0.16604466668875575;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	11.391710683334669;
	public static final double ROOKS_7TH_2TH_E	=	5.402112786795214;

	public static final double QUEENS_7TH_2TH_O	=	2.540267840628415;
	public static final double QUEENS_7TH_2TH_E	=	6.905630269852015;

	public static final double KINGSAFETY_L1_O	=	35.947768845147586;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	16.13870835155341;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.1918472045796537;
	public static final double MOBILITY_KNIGHT_E	=	0.4996448463228109;

	public static final double MOBILITY_BISHOP_O	=	0.24578186052577383;
	public static final double MOBILITY_BISHOP_E	=	0.4455094216974257;

	public static final double MOBILITY_ROOK_O	=	0.2307951837880381;
	public static final double MOBILITY_ROOK_E	=	0.3470993421589205;

	public static final double MOBILITY_QUEEN_O	=	0.09018668462311191;
	public static final double MOBILITY_QUEEN_E	=	0.8389614124710957;

	public static final double MOBILITY_KNIGHT_S_O	=	0.10943942610801874;
	public static final double MOBILITY_KNIGHT_S_E	=	0.2926270130554574;

	public static final double MOBILITY_BISHOP_S_O	=	0.08441362513024658;
	public static final double MOBILITY_BISHOP_S_E	=	0.12301270578629206;

	public static final double MOBILITY_ROOK_S_O	=	0.07020236733167469;
	public static final double MOBILITY_ROOK_S_E	=	0.12906025691800435;

	public static final double MOBILITY_QUEEN_S_O	=	0.27234362398756856;
	public static final double MOBILITY_QUEEN_S_E	=	0.8424797085256327;

	public static final double ROOKS_PAIR_H_O	=	1.0375296619070253;
	public static final double ROOKS_PAIR_H_E	=	0.9523908421705906;

	public static final double ROOKS_PAIR_V_O	=	0.23773547994208244;
	public static final double ROOKS_PAIR_V_E	=	0.22451351092854777;

	public static final double TRAP_O	=	-0.15938672928435738;
	public static final double TRAP_E	=	-0.02108155491925598;

	public static final double PIN_BIGGER_O	=	16.54430940244522;
	public static final double PIN_BIGGER_E	=	56.85837636844364;

	public static final double PIN_EQ_O	=	7.5228094585277345;
	public static final double PIN_EQ_E	=	0.40555800065749753;

	public static final double PIN_LOWER_O	=	0.4814620101750951;
	public static final double PIN_LOWER_E	=	2.6341386059609246;

	public static final double ATTACK_BIGGER_O	=	30.465527479834094;
	public static final double ATTACK_BIGGER_E	=	51.31293646026617;

	public static final double ATTACK_EQ_O	=	21.002109417536534;
	public static final double ATTACK_EQ_E	=	18.341655425895738;

	public static final double ATTACK_LOWER_O	=	0.9076418927539197;
	public static final double ATTACK_LOWER_E	=	25.755906428322795;

	public static final double HUNGED_PIECE_O	=	0.0;
	public static final double HUNGED_PIECE_E	=	0.0;

	public static final double HUNGED_PAWNS_O	=	0.0;
	public static final double HUNGED_PAWNS_E	=	0.0;

	public static final double HUNGED_ALL_O	=	0.0;
	public static final double HUNGED_ALL_E	=	0.0;

	public static final double PAWNS_PSTOPPERS_A_O	=	0.8003851075454552;
	public static final double PAWNS_PSTOPPERS_A_E	=	1.2304044081084395;
}
