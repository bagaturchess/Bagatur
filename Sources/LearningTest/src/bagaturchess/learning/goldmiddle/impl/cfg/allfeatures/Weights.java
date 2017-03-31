package bagaturchess.learning.goldmiddle.impl.cfg.allfeatures;


public interface Weights {
	
	public static final double MATERIAL_PAWN_O	=	61.34258911913671;
	public static final double MATERIAL_PAWN_E	=	82.65333490039654;

	public static final double MATERIAL_KNIGHT_O	=	340.96899622162607;
	public static final double MATERIAL_KNIGHT_E	=	304.22774471011076;

	public static final double MATERIAL_BISHOP_O	=	350.13194372594427;
	public static final double MATERIAL_BISHOP_E	=	291.3291856004035;

	public static final double MATERIAL_ROOK_O	=	459.81655512032546;
	public static final double MATERIAL_ROOK_E	=	481.41609196667184;

	public static final double MATERIAL_QUEEN_O	=	1078.1603691114763;
	public static final double MATERIAL_QUEEN_E	=	809.600316463999;

	public static final double KINGSAFE_CASTLING_O	=	7.9563686001985605;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	1.646664197584136;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	24.61784792084093;
	public static final double BISHOPS_DOUBLE_E	=	57.20258778812245;

	public static final double KNIGHTS_DOUBLE_O	=	3.2259111688150517;
	public static final double KNIGHTS_DOUBLE_E	=	3.698629516492896;

	public static final double ROOKS_DOUBLE_O	=	30.67691090147877;
	public static final double ROOKS_DOUBLE_E	=	17.14201474031199;

	public static final double ROOKS_5PAWNS_O	=	1.0652903465593888;
	public static final double ROOKS_5PAWNS_E	=	0.4984822550639128;

	public static final double KNIGHTS_5PAWNS_O	=	1.448181604821289;
	public static final double KNIGHTS_5PAWNS_E	=	8.191566977008952;

	public static final double KINGSAFE_F_O	=	-6.46489367777904;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-11.199383455999982;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	-0.055544893291957574;
	public static final double KINGS_DISTANCE_E	=	0.03245712335642885;

	public static final double PAWNS_DOUBLED_O	=	-0.537123916638058;
	public static final double PAWNS_DOUBLED_E	=	-2.9711299975192325;

	public static final double PAWNS_ISOLATED_O	=	-10.220504965721457;
	public static final double PAWNS_ISOLATED_E	=	-11.196753553652046;

	public static final double PAWNS_BACKWARD_O	=	-4.4394802715408925;
	public static final double PAWNS_BACKWARD_E	=	-1.1459304254750458;

	public static final double PAWNS_SUPPORTED_O	=	4.360663175375174;
	public static final double PAWNS_SUPPORTED_E	=	6.698706179995694;

	public static final double PAWNS_CANNOTBS_O	=	-2.051489311455921;
	public static final double PAWNS_CANNOTBS_E	=	-2.1646853317540313;

	public static final double PAWNS_PASSED_O	=	10.999437220527264;
	public static final double PAWNS_PASSED_E	=	4.116583792950015;

	public static final double PAWNS_PASSED_RNK_O	=	0.7591023674583026;
	public static final double PAWNS_PASSED_RNK_E	=	0.8659374768330532;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_PSTOPPERS_O	=	0.042068521691595175;
	public static final double PAWNS_PSTOPPERS_E	=	0.3176293378065275;
	
	public static final double PAWNS_CANDIDATE_RNK_O	=	0.35345049485021407;
	public static final double PAWNS_CANDIDATE_RNK_E	=	0.28498942639061736;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	1.4659192186371128;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.8048131567539932;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.8948253012638445;

	public static final double PAWNS_ISLANDS_O	=	-0.8393659870617335;
	public static final double PAWNS_ISLANDS_E	=	-0.2910654298392872;

	public static final double PAWNS_GARDS_O	=	5.484115941360342;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-4.428950637622364;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	0.7934524708630696;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	4.167087010250499;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-39.84819236130881;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-25.80731718680908;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-15.15031872800534;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-2.886487757581686;
	public static final double PAWNS_WEAK_E	=	-0.614436188344715;

	public static final double SPACE_O	=	0.8589078525370424;
	public static final double SPACE_E	=	0.5089282147084904;

	public static final double ROOK_INFRONT_PASSER_O	=	0.0;
	public static final double ROOK_INFRONT_PASSER_E	=	0.0;

	public static final double ROOK_BEHIND_PASSER_O	=	1.2396760217157803;
	public static final double ROOK_BEHIND_PASSER_E	=	7.266680644326737;

	public static final double PST_PAWN_O	=	0.7712310464144383;
	public static final double PST_PAWN_E	=	0.7028076515778304;

	public static final double PST_KING_O	=	1.1198280892079555;
	public static final double PST_KING_E	=	0.9932441404977812;

	public static final double PST_KNIGHTS_O	=	0.7650222362865452;
	public static final double PST_KNIGHTS_E	=	0.6257677400092013;

	public static final double PST_BISHOPS_O	=	0.7427214763480573;
	public static final double PST_BISHOPS_E	=	0.4529619192512181;

	public static final double PST_ROOKS_O	=	0.8334237387925632;
	public static final double PST_ROOKS_E	=	0.6293928557481407;

	public static final double PST_QUEENS_O	=	0.21627293999981156;
	public static final double PST_QUEENS_E	=	0.9461716863639211;

	public static final double BISHOPS_BAD_O	=	-1.0515639712867455;
	public static final double BISHOPS_BAD_E	=	-1.010519969211092;

	public static final double KNIGHT_OUTPOST_O	=	13.399070104789631;
	public static final double KNIGHT_OUTPOST_E	=	0.2568420354673486;

	public static final double ROOKS_OPENED_O	=	19.304026648085944;
	public static final double ROOKS_OPENED_E	=	0.423657058450663;

	public static final double ROOKS_SEMIOPENED_O	=	7.053686069759017;
	public static final double ROOKS_SEMIOPENED_E	=	3.5563466966048733;

	public static final double TROPISM_KNIGHT_O	=	0.08061023283941496;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.3031698705183463;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	0.31186444273924396;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	0.16379959295796576;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	10.558241326199242;
	public static final double ROOKS_7TH_2TH_E	=	6.608936497442014;

	public static final double QUEENS_7TH_2TH_O	=	3.1637863839256313;
	public static final double QUEENS_7TH_2TH_E	=	7.255964408211219;

	public static final double KINGSAFETY_L1_O	=	36.115594831315555;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	16.126185897426286;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.21285305681896222;
	public static final double MOBILITY_KNIGHT_E	=	0.5111179062726113;

	public static final double MOBILITY_BISHOP_O	=	0.24636105557425947;
	public static final double MOBILITY_BISHOP_E	=	0.465729003129953;

	public static final double MOBILITY_ROOK_O	=	0.2285275728202045;
	public static final double MOBILITY_ROOK_E	=	0.3662383922856686;

	public static final double MOBILITY_QUEEN_O	=	0.10689896306655527;
	public static final double MOBILITY_QUEEN_E	=	0.853315217451292;

	public static final double MOBILITY_KNIGHT_S_O	=	0.11793497895456044;
	public static final double MOBILITY_KNIGHT_S_E	=	0.30328600812441875;

	public static final double MOBILITY_BISHOP_S_O	=	0.08911757315661037;
	public static final double MOBILITY_BISHOP_S_E	=	0.15471078957746337;

	public static final double MOBILITY_ROOK_S_O	=	0.07901972737070359;
	public static final double MOBILITY_ROOK_S_E	=	0.1604480577297098;

	public static final double MOBILITY_QUEEN_S_O	=	0.27776049073776893;
	public static final double MOBILITY_QUEEN_S_E	=	0.8583124538619835;

	public static final double ROOKS_PAIR_H_O	=	1.0313554715130957;
	public static final double ROOKS_PAIR_H_E	=	1.2249467374770877;

	public static final double ROOKS_PAIR_V_O	=	0.41097743853795277;
	public static final double ROOKS_PAIR_V_E	=	0.4770959259479873;

	public static final double TRAP_O	=	-0.1774753866342124;
	public static final double TRAP_E	=	-0.030790123256786577;

	public static final double PIN_BIGGER_O	=	16.37699125609572;
	public static final double PIN_BIGGER_E	=	57.77896809268112;

	public static final double PIN_EQ_O	=	7.719751515033199;
	public static final double PIN_EQ_E	=	0.8775612348701884;

	public static final double PIN_LOWER_O	=	0.6513595869640849;
	public static final double PIN_LOWER_E	=	3.6556955188820366;

	public static final double ATTACK_BIGGER_O	=	30.448008609600624;
	public static final double ATTACK_BIGGER_E	=	51.35474180261027;

	public static final double ATTACK_EQ_O	=	20.724990903097268;
	public static final double ATTACK_EQ_E	=	18.946723434829416;

	public static final double ATTACK_LOWER_O	=	1.249387969767513;
	public static final double ATTACK_LOWER_E	=	25.841301840255046;

	public static final double HUNGED_PIECE_O	=	0.0;
	public static final double HUNGED_PIECE_E	=	0.0;

	public static final double HUNGED_PAWNS_O	=	0.0;
	public static final double HUNGED_PAWNS_E	=	0.0;

	public static final double HUNGED_ALL_O	=	2;
	public static final double HUNGED_ALL_E	=	4;
}
