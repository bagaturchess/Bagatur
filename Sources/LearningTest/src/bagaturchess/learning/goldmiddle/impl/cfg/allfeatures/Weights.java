package bagaturchess.learning.goldmiddle.impl.cfg.allfeatures;


public interface Weights {
	public static final double MATERIAL_PAWN_O	=	60.793895952258154;
	public static final double MATERIAL_PAWN_E	=	80.86248194322427;

	public static final double MATERIAL_KNIGHT_O	=	336.99501496754993;
	public static final double MATERIAL_KNIGHT_E	=	311.59571291081255;

	public static final double MATERIAL_BISHOP_O	=	350.0069483075047;
	public static final double MATERIAL_BISHOP_E	=	297.7685744565887;

	public static final double MATERIAL_ROOK_O	=	458.32426242747164;
	public static final double MATERIAL_ROOK_E	=	495.1994059149764;

	public static final double MATERIAL_QUEEN_O	=	1077.0087374946222;
	public static final double MATERIAL_QUEEN_E	=	821.4015324740709;

	public static final double KINGSAFE_CASTLING_O	=	7.453037339684986;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	0.9613733254935046;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	23.28008509088633;
	public static final double BISHOPS_DOUBLE_E	=	55.94008297273836;

	public static final double KNIGHTS_DOUBLE_O	=	1.725952425473027;
	public static final double KNIGHTS_DOUBLE_E	=	3.370082429434515;

	public static final double ROOKS_DOUBLE_O	=	28.093498106494522;
	public static final double ROOKS_DOUBLE_E	=	9.982898827551141;

	public static final double ROOKS_5PAWNS_O	=	1.0020246986014634;
	public static final double ROOKS_5PAWNS_E	=	0.4807370134769752;

	public static final double KNIGHTS_5PAWNS_O	=	1.4654405276278244;
	public static final double KNIGHTS_5PAWNS_E	=	11.391981642073882;

	public static final double KINGSAFE_F_O	=	-9.80067068267564;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-12.067303076711266;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	-0.6958096624036965;
	public static final double KINGS_DISTANCE_E	=	-0.014095273813201062;

	public static final double PAWNS_DOUBLED_O	=	-2.664464522909178;
	public static final double PAWNS_DOUBLED_E	=	-1.9275531278266704;

	public static final double PAWNS_ISOLATED_O	=	-9.396949233482207;
	public static final double PAWNS_ISOLATED_E	=	-11.857395142732003;

	public static final double PAWNS_BACKWARD_O	=	-3.4750931354771866;
	public static final double PAWNS_BACKWARD_E	=	-0.6062459231106193;

	public static final double PAWNS_SUPPORTED_O	=	4.245033134461971;
	public static final double PAWNS_SUPPORTED_E	=	7.5075227503247755;

	public static final double PAWNS_CANNOTBS_O	=	-2.9276151577881873;
	public static final double PAWNS_CANNOTBS_E	=	-2.244261451067454;

	public static final double PAWNS_PASSED_O	=	11.548862017445819;
	public static final double PAWNS_PASSED_E	=	4.359165165754665;

	public static final double PAWNS_PASSED_RNK_O	=	0.7202183759836265;
	public static final double PAWNS_PASSED_RNK_E	=	0.8773279655656412;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_PSTOPPERS_O	=	0.0;
	public static final double PAWNS_PSTOPPERS_E	=	0.05696235341413847;

	public static final double PAWNS_CANDIDATE_RNK_O	=	0.33394648617108935;
	public static final double PAWNS_CANDIDATE_RNK_E	=	0.27669788344414326;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	1.5930862267134909;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.6366678640075829;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.8570277250115297;

	public static final double PAWNS_ISLANDS_O	=	-1.7841902831936525;
	public static final double PAWNS_ISLANDS_E	=	-0.14619153056696932;

	public static final double PAWNS_GARDS_O	=	2.9918899935913332;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-4.563993580607245;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	0.18559324357226006;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	4.840222716891731;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-43.25138490381969;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-26.93899115123111;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-16.97283684216782;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-4.163988009197674;
	public static final double PAWNS_WEAK_E	=	-0.2332617518698378;

	public static final double SPACE_O	=	1.1785683171221233;
	public static final double SPACE_E	=	0.5061601033731867;

	public static final double ROOK_INFRONT_PASSER_O	=	0.0;
	public static final double ROOK_INFRONT_PASSER_E	=	0.0;

	public static final double ROOK_BEHIND_PASSER_O	=	1.0901525158821814;
	public static final double ROOK_BEHIND_PASSER_E	=	10.708468718116753;

	public static final double PST_PAWN_O	=	0.852696011534818;
	public static final double PST_PAWN_E	=	0.7316456759232493;

	public static final double PST_KING_O	=	1.1846334966426093;
	public static final double PST_KING_E	=	1.011091751917046;

	public static final double PST_KNIGHTS_O	=	0.8220943493010032;
	public static final double PST_KNIGHTS_E	=	0.6959161665311633;

	public static final double PST_BISHOPS_O	=	0.7972922696172905;
	public static final double PST_BISHOPS_E	=	0.7477679892721703;

	public static final double PST_ROOKS_O	=	0.8383892872464009;
	public static final double PST_ROOKS_E	=	0.6566824326376021;

	public static final double PST_QUEENS_O	=	0.21773629758139712;
	public static final double PST_QUEENS_E	=	0.9260018027848779;

	public static final double BISHOPS_BAD_O	=	-1.2375951683946205;
	public static final double BISHOPS_BAD_E	=	-1.087061911279854;

	public static final double KNIGHT_OUTPOST_O	=	13.598810250616134;
	public static final double KNIGHT_OUTPOST_E	=	0.04849110475196838;

	public static final double ROOKS_OPENED_O	=	20.6900009908241;
	public static final double ROOKS_OPENED_E	=	0.17037470376963615;

	public static final double ROOKS_SEMIOPENED_O	=	6.22312890113098;
	public static final double ROOKS_SEMIOPENED_E	=	5.509577986483112;

	public static final double TROPISM_KNIGHT_O	=	0.07655600546390967;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.23385219150454561;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	0.4940149781596985;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	0.2401392751659005;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	10.775114279720619;
	public static final double ROOKS_7TH_2TH_E	=	5.2887954850909065;

	public static final double QUEENS_7TH_2TH_O	=	2.238046603576356;
	public static final double QUEENS_7TH_2TH_E	=	6.426459485843537;

	public static final double KINGSAFETY_L1_O	=	36.820181128283814;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	17.27353289412178;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.12414086044147125;
	public static final double MOBILITY_KNIGHT_E	=	0.4843014397384336;

	public static final double MOBILITY_BISHOP_O	=	0.2476761098599108;
	public static final double MOBILITY_BISHOP_E	=	0.43427014885976223;

	public static final double MOBILITY_ROOK_O	=	0.26658696199494314;
	public static final double MOBILITY_ROOK_E	=	0.3545009769646857;

	public static final double MOBILITY_QUEEN_O	=	0.060362845426966454;
	public static final double MOBILITY_QUEEN_E	=	0.8351517001284785;

	public static final double MOBILITY_KNIGHT_S_O	=	0.10650583676390597;
	public static final double MOBILITY_KNIGHT_S_E	=	0.30263374217544486;

	public static final double MOBILITY_BISHOP_S_O	=	0.07684282533016179;
	public static final double MOBILITY_BISHOP_S_E	=	0.07971671782992541;

	public static final double MOBILITY_ROOK_S_O	=	0.0574163582816815;
	public static final double MOBILITY_ROOK_S_E	=	0.08212673969342976;

	public static final double MOBILITY_QUEEN_S_O	=	0.2809211607356475;
	public static final double MOBILITY_QUEEN_S_E	=	0.8347422556266125;

	public static final double ROOKS_PAIR_H_O	=	1.010765277582813;
	public static final double ROOKS_PAIR_H_E	=	0.252249349389761;

	public static final double ROOKS_PAIR_V_O	=	0.23827695153067174;
	public static final double ROOKS_PAIR_V_E	=	0.03648505919908099;

	public static final double TRAP_O	=	-0.13456200653900652;
	public static final double TRAP_E	=	-0.0157103217416477;

	public static final double PIN_BIGGER_O	=	18.311673443203315;
	public static final double PIN_BIGGER_E	=	55.47296075552843;

	public static final double PIN_EQ_O	=	4.579027121714806;
	public static final double PIN_EQ_E	=	0.053739969368472;

	public static final double PIN_LOWER_O	=	0.1810081984853454;
	public static final double PIN_LOWER_E	=	0.8606367667735721;

	public static final double ATTACK_BIGGER_O	=	30.53150837399439;
	public static final double ATTACK_BIGGER_E	=	51.14125843437132;

	public static final double ATTACK_EQ_O	=	21.061313195104017;
	public static final double ATTACK_EQ_E	=	17.949022930839675;

	public static final double ATTACK_LOWER_O	=	0.48400524363768244;
	public static final double ATTACK_LOWER_E	=	25.192782907539854;

	public static final double HUNGED_PIECE_O	=	0.0;
	public static final double HUNGED_PIECE_E	=	0.0;

	public static final double HUNGED_PAWNS_O	=	0.0;
	public static final double HUNGED_PAWNS_E	=	0.0;

	public static final double HUNGED_ALL_O	=	2;
	public static final double HUNGED_ALL_E	=	4;

	public static final double PAWNS_PSTOPPERS_A_O	=	0.098;
	public static final double PAWNS_PSTOPPERS_A_E	=	1.058;


	
}
