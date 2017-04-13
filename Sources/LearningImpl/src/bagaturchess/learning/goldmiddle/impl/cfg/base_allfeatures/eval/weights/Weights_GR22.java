package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.eval.weights;


/*
 * Weaker with 123 ELO vs Bagatur Eval
 */

public interface Weights_GR22 {
	public static final double MATERIAL_PAWN_O	=	78.58396821932342;
	public static final double MATERIAL_PAWN_E	=	129.92218925051975;

	public static final double MATERIAL_KNIGHT_O	=	394.2654293263638;
	public static final double MATERIAL_KNIGHT_E	=	385.59244752267375;

	public static final double MATERIAL_BISHOP_O	=	399.46695714234374;
	public static final double MATERIAL_BISHOP_E	=	392.98770358105224;

	public static final double MATERIAL_ROOK_O	=	551.6029181888441;
	public static final double MATERIAL_ROOK_E	=	617.2393722291257;

	public static final double MATERIAL_QUEEN_O	=	1294.4310419739359;
	public static final double MATERIAL_QUEEN_E	=	1053.6277263950349;

	public static final double KINGSAFE_CASTLING_O	=	12.302716306029824;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	8.89688073329309;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	39.868418709466695;
	public static final double BISHOPS_DOUBLE_E	=	57.58929063426491;

	public static final double KNIGHTS_DOUBLE_O	=	5.688675114927065;
	public static final double KNIGHTS_DOUBLE_E	=	9.47283299839115;

	public static final double ROOKS_DOUBLE_O	=	35.3404962972705;
	public static final double ROOKS_DOUBLE_E	=	0.1425145828069869;

	public static final double ROOKS_5PAWNS_O	=	1.3502580256181473;
	public static final double ROOKS_5PAWNS_E	=	0.2004984674560796;

	public static final double KNIGHTS_5PAWNS_O	=	6.295853209072537;
	public static final double KNIGHTS_5PAWNS_E	=	7.593570710373551;

	public static final double KINGSAFE_F_O	=	-9.351647535373358;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-0.020782890981549673;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	0.044129461533840265;
	public static final double KINGS_DISTANCE_E	=	0.7066715818086351;

	public static final double PAWNS_DOUBLED_O	=	-0.1950003296457763;
	public static final double PAWNS_DOUBLED_E	=	-20.651585657879707;

	public static final double PAWNS_ISOLATED_O	=	-7.660684087636444;
	public static final double PAWNS_ISOLATED_E	=	-18.446160457883344;

	public static final double PAWNS_BACKWARD_O	=	-5.6581199468610714;
	public static final double PAWNS_BACKWARD_E	=	-2.0118087508704243;

	public static final double PAWNS_SUPPORTED_O	=	8.919894615520002;
	public static final double PAWNS_SUPPORTED_E	=	12.114609353707962;

	public static final double PAWNS_CANNOTBS_O	=	-2.942141799551968;
	public static final double PAWNS_CANNOTBS_E	=	-1.2424059706491637;

	public static final double PAWNS_PASSED_O	=	5.971021759609328;
	public static final double PAWNS_PASSED_E	=	6.5700523464823295;

	public static final double PAWNS_PASSED_RNK_O	=	0.7807534913051575;
	public static final double PAWNS_PASSED_RNK_E	=	1.0837043790929897;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_PSTOPPERS_O	=	0.05438593797439326;
	public static final double PAWNS_PSTOPPERS_E	=	2.206709554599167;

	public static final double PAWNS_CANDIDATE_RNK_O	=	1.5367650289503563;
	public static final double PAWNS_CANDIDATE_RNK_E	=	1.0724608057446492;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	2.6865044420895106;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.9524046224667667;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.8116040746101987;

	public static final double PAWNS_ISLANDS_O	=	-1.47530557311606;
	public static final double PAWNS_ISLANDS_E	=	-0.38444690641322354;

	public static final double PAWNS_GARDS_O	=	22.71072735271075;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-11.869021293264527;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	1.380385709892497;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	4.9249695727009435;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-48.916880447781686;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-31.507774247697405;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-20.641091251488767;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-2.6633982270165224;
	public static final double PAWNS_WEAK_E	=	-0.1984297449627995;

	public static final double SPACE_O	=	0.6147285057327595;
	public static final double SPACE_E	=	0.8826382610600931;

	public static final double ROOK_INFRONT_PASSER_O	=	-1.1696877413776468;
	public static final double ROOK_INFRONT_PASSER_E	=	-0.34575070052082457;

	public static final double ROOK_BEHIND_PASSER_O	=	0.40050124312423263;
	public static final double ROOK_BEHIND_PASSER_E	=	1.4040051535550397;

	public static final double PST_PAWN_O	=	0.7700358345266054;
	public static final double PST_PAWN_E	=	0.5040506240049432;

	public static final double PST_KING_O	=	1.8161207210344756;
	public static final double PST_KING_E	=	1.8745172166691093;

	public static final double PST_KNIGHTS_O	=	1.1049496545530457;
	public static final double PST_KNIGHTS_E	=	0.633730452117576;

	public static final double PST_BISHOPS_O	=	1.3116210090541038;
	public static final double PST_BISHOPS_E	=	0.26338083443021304;

	public static final double PST_ROOKS_O	=	2.193686218947277;
	public static final double PST_ROOKS_E	=	0.10577999134283939;

	public static final double PST_QUEENS_O	=	0.21433172624449734;
	public static final double PST_QUEENS_E	=	0.6654301876255021;

	public static final double BISHOPS_BAD_O	=	-0.6570927648345702;
	public static final double BISHOPS_BAD_E	=	-0.585961348794019;

	public static final double KNIGHT_OUTPOST_O	=	8.309131859334588;
	public static final double KNIGHT_OUTPOST_E	=	0.7358521098263724;

	public static final double ROOKS_OPENED_O	=	23.67816131677911;
	public static final double ROOKS_OPENED_E	=	5.422609206231311;

	public static final double ROOKS_SEMIOPENED_O	=	18.803306255349334;
	public static final double ROOKS_SEMIOPENED_E	=	1.1107599328370572;

	public static final double TROPISM_KNIGHT_O	=	0.11208263388474492;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.5825416668090544;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	2.54871650992481;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	1.0786813682487355;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	0.5632454989908653;
	public static final double ROOKS_7TH_2TH_E	=	22.202441400430736;

	public static final double QUEENS_7TH_2TH_O	=	7.867676643298315;
	public static final double QUEENS_7TH_2TH_E	=	8.253044849963445;

	public static final double KINGSAFETY_L1_O	=	56.78304885629658;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	18.46110877447119;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.18272049482163208;
	public static final double MOBILITY_KNIGHT_E	=	0.6466868363343754;

	public static final double MOBILITY_BISHOP_O	=	0.3606520770306391;
	public static final double MOBILITY_BISHOP_E	=	0.41073616979889244;

	public static final double MOBILITY_ROOK_O	=	0.23086299333245086;
	public static final double MOBILITY_ROOK_E	=	0.22032556437052575;

	public static final double MOBILITY_QUEEN_O	=	0.04457066067871495;
	public static final double MOBILITY_QUEEN_E	=	0.76871926121285;

	public static final double MOBILITY_KNIGHT_S_O	=	0.023926675853580416;
	public static final double MOBILITY_KNIGHT_S_E	=	0.2952277132565962;

	public static final double MOBILITY_BISHOP_S_O	=	0.1305154136446196;
	public static final double MOBILITY_BISHOP_S_E	=	0.07389246943010497;

	public static final double MOBILITY_ROOK_S_O	=	0.0734191277334245;
	public static final double MOBILITY_ROOK_S_E	=	0.14981187237864066;

	public static final double MOBILITY_QUEEN_S_O	=	0.0764313096599256;
	public static final double MOBILITY_QUEEN_S_E	=	0.9081335300390795;

	public static final double ROOKS_PAIR_H_O	=	5.123197695277928;
	public static final double ROOKS_PAIR_H_E	=	0.0;

	public static final double ROOKS_PAIR_V_O	=	0.027875171018536975;
	public static final double ROOKS_PAIR_V_E	=	0.01391537106002948;

	public static final double TRAP_O	=	-0.023774447918070804;
	public static final double TRAP_E	=	0.0;

	public static final double PIN_BIGGER_O	=	5.691329098352015;
	public static final double PIN_BIGGER_E	=	0.0;

	public static final double PIN_EQ_O	=	0.7654043511226899;
	public static final double PIN_EQ_E	=	25.682352281502865;

	public static final double PIN_LOWER_O	=	0.9597193793981492;
	public static final double PIN_LOWER_E	=	2.3303451525410153;

	public static final double ATTACK_BIGGER_O	=	0.897952057012278;
	public static final double ATTACK_BIGGER_E	=	0.7935448205075653;

	public static final double ATTACK_EQ_O	=	5.339439081629483;
	public static final double ATTACK_EQ_E	=	2.036949273342921;

	public static final double ATTACK_LOWER_O	=	0.6429726761391807;
	public static final double ATTACK_LOWER_E	=	0.878326753866648;

	public static final double HUNGED_PIECE_O	=	-3.5597340354071276;
	public static final double HUNGED_PIECE_E	=	-1.195084000198038;

	public static final double HUNGED_PAWNS_O	=	-3.5967217088731203;
	public static final double HUNGED_PAWNS_E	=	-0.06604818770272736;

	public static final double HUNGED_ALL_O	=	-8.275706670780071;
	public static final double HUNGED_ALL_E	=	-0.3009161814180049;

	public static final double PAWNS_PSTOPPERS_A_O	=	0.018196410598507344;
	public static final double PAWNS_PSTOPPERS_A_E	=	0.5370722422796627;
}
