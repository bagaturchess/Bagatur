package bagaturchess.learning.goldmiddle.impl.cfg.allfeatures;


public interface Weights {
	public static final double MATERIAL_PAWN_O	=	80.11183394844097;
	public static final double MATERIAL_PAWN_E	=	128.0818561108875;

	public static final double MATERIAL_KNIGHT_O	=	394.5997401990538;
	public static final double MATERIAL_KNIGHT_E	=	385.34570930373445;

	public static final double MATERIAL_BISHOP_O	=	400.2855944803345;
	public static final double MATERIAL_BISHOP_E	=	391.9109469490261;

	public static final double MATERIAL_ROOK_O	=	551.7510383684772;
	public static final double MATERIAL_ROOK_E	=	615.6659195589502;

	public static final double MATERIAL_QUEEN_O	=	1297.0926001668581;
	public static final double MATERIAL_QUEEN_E	=	1044.8733867132148;

	public static final double KINGSAFE_CASTLING_O	=	12.43700145099695;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	9.484398968812926;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	39.08589124319734;
	public static final double BISHOPS_DOUBLE_E	=	57.55649114856937;

	public static final double KNIGHTS_DOUBLE_O	=	6.140909176536103;
	public static final double KNIGHTS_DOUBLE_E	=	10.043887959246423;

	public static final double ROOKS_DOUBLE_O	=	35.74675757283487;
	public static final double ROOKS_DOUBLE_E	=	0.35893339241624955;

	public static final double ROOKS_5PAWNS_O	=	1.3901711251227373;
	public static final double ROOKS_5PAWNS_E	=	0.30525484231314437;

	public static final double KNIGHTS_5PAWNS_O	=	5.759249169956762;
	public static final double KNIGHTS_5PAWNS_E	=	7.74140340649413;

	public static final double KINGSAFE_F_O	=	-9.82797969217816;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-0.04571803039256408;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	0.036849356727287286;
	public static final double KINGS_DISTANCE_E	=	0.699848732109247;

	public static final double PAWNS_DOUBLED_O	=	-0.18710819250178762;
	public static final double PAWNS_DOUBLED_E	=	-18.940393911448187;

	public static final double PAWNS_ISOLATED_O	=	-7.874519871480359;
	public static final double PAWNS_ISOLATED_E	=	-19.071257528004317;

	public static final double PAWNS_BACKWARD_O	=	-5.597163709057048;
	public static final double PAWNS_BACKWARD_E	=	-1.9820592067279206;

	public static final double PAWNS_SUPPORTED_O	=	8.526001422283658;
	public static final double PAWNS_SUPPORTED_E	=	12.18162551313893;

	public static final double PAWNS_CANNOTBS_O	=	-3.1362318564473073;
	public static final double PAWNS_CANNOTBS_E	=	-1.2408428541387555;

	public static final double PAWNS_PASSED_O	=	10.764294465513009;
	public static final double PAWNS_PASSED_E	=	7.025050453652124;

	public static final double PAWNS_PASSED_RNK_O	=	0.8808263374081178;
	public static final double PAWNS_PASSED_RNK_E	=	1.077520335843566;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_PSTOPPERS_O	=	0.0;
	public static final double PAWNS_PSTOPPERS_E	=	0.14172952994690696;

	public static final double PAWNS_CANDIDATE_RNK_O	=	1.4841349680876215;
	public static final double PAWNS_CANDIDATE_RNK_E	=	1.0693568299718297;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	3.1342338522711835;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.8647209632488925;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.8421307958348867;

	public static final double PAWNS_ISLANDS_O	=	-1.3561186336204687;
	public static final double PAWNS_ISLANDS_E	=	-0.3580345549075442;

	public static final double PAWNS_GARDS_O	=	21.879467590264895;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-11.416824314709267;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	1.409210681858184;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	4.825661836109924;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-49.10219588503799;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-32.24152271331251;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-20.716593780581192;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-2.6498816362657958;
	public static final double PAWNS_WEAK_E	=	-0.21932842900180807;

	public static final double SPACE_O	=	0.6058421414000125;
	public static final double SPACE_E	=	0.9152962079044432;

	public static final double ROOK_INFRONT_PASSER_O	=	-0.022487935243103677;
	public static final double ROOK_INFRONT_PASSER_E	=	0.0;

	public static final double ROOK_BEHIND_PASSER_O	=	1.264742507907725;
	public static final double ROOK_BEHIND_PASSER_E	=	3.5110117517219313;

	public static final double PST_PAWN_O	=	0.7249634002692424;
	public static final double PST_PAWN_E	=	0.5177812607635747;

	public static final double PST_KING_O	=	1.8408531803577308;
	public static final double PST_KING_E	=	1.9021957969313636;

	public static final double PST_KNIGHTS_O	=	1.0790832175535137;
	public static final double PST_KNIGHTS_E	=	0.6487596210512434;

	public static final double PST_BISHOPS_O	=	1.316004272708672;
	public static final double PST_BISHOPS_E	=	0.25774091482436057;

	public static final double PST_ROOKS_O	=	2.2257965823494725;
	public static final double PST_ROOKS_E	=	0.15843198942898132;

	public static final double PST_QUEENS_O	=	0.2090878475911022;
	public static final double PST_QUEENS_E	=	0.8285491077769063;

	public static final double BISHOPS_BAD_O	=	-0.6575354896328592;
	public static final double BISHOPS_BAD_E	=	-0.5662400594288173;

	public static final double KNIGHT_OUTPOST_O	=	8.783505995047365;
	public static final double KNIGHT_OUTPOST_E	=	0.7102946293688812;

	public static final double ROOKS_OPENED_O	=	23.250293997526708;
	public static final double ROOKS_OPENED_E	=	4.137344249767888;

	public static final double ROOKS_SEMIOPENED_O	=	17.80202716892514;
	public static final double ROOKS_SEMIOPENED_E	=	0.18450839294803195;

	public static final double TROPISM_KNIGHT_O	=	0.1133524599401813;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.5677595482447844;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	2.5056220574816828;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	0.9320582724082634;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	0.9044440528110361;
	public static final double ROOKS_7TH_2TH_E	=	21.19048946909011;

	public static final double QUEENS_7TH_2TH_O	=	7.2795939606227025;
	public static final double QUEENS_7TH_2TH_E	=	12.633553639439972;

	public static final double KINGSAFETY_L1_O	=	56.80863557048679;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	18.667622302708867;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.20191887453113538;
	public static final double MOBILITY_KNIGHT_E	=	0.634369631315518;

	public static final double MOBILITY_BISHOP_O	=	0.34984358894026524;
	public static final double MOBILITY_BISHOP_E	=	0.4203677777099888;

	public static final double MOBILITY_ROOK_O	=	0.2387941872473045;
	public static final double MOBILITY_ROOK_E	=	0.2393976371758664;

	public static final double MOBILITY_QUEEN_O	=	0.04849629037373743;
	public static final double MOBILITY_QUEEN_E	=	0.7958983681857555;

	public static final double MOBILITY_KNIGHT_S_O	=	0.028989886584185543;
	public static final double MOBILITY_KNIGHT_S_E	=	0.2952467225326339;

	public static final double MOBILITY_BISHOP_S_O	=	0.131250797351468;
	public static final double MOBILITY_BISHOP_S_E	=	0.09071622063971654;

	public static final double MOBILITY_ROOK_S_O	=	0.07881636413782342;
	public static final double MOBILITY_ROOK_S_E	=	0.15237104236143365;

	public static final double MOBILITY_QUEEN_S_O	=	0.087337548208874;
	public static final double MOBILITY_QUEEN_S_E	=	0.9453626410009979;

	public static final double ROOKS_PAIR_H_O	=	4.534249926760689;
	public static final double ROOKS_PAIR_H_E	=	0.0;

	public static final double ROOKS_PAIR_V_O	=	0.1116915610386718;
	public static final double ROOKS_PAIR_V_E	=	0.020631797116992395;

	public static final double TRAP_O	=	-0.02913096650796659;
	public static final double TRAP_E	=	0.0;

	public static final double PIN_BIGGER_O	=	5.56718257903416;
	public static final double PIN_BIGGER_E	=	0.0;

	public static final double PIN_EQ_O	=	0.7136870820324155;
	public static final double PIN_EQ_E	=	29.725238778635287;

	public static final double PIN_LOWER_O	=	0.9608158563633298;
	public static final double PIN_LOWER_E	=	3.3072340547594465;

	public static final double ATTACK_BIGGER_O	=	0.7957407086526725;
	public static final double ATTACK_BIGGER_E	=	0.802414331540949;

	public static final double ATTACK_EQ_O	=	5.526947212508335;
	public static final double ATTACK_EQ_E	=	1.7443612357844809;

	public static final double ATTACK_LOWER_O	=	0.6149592328516486;
	public static final double ATTACK_LOWER_E	=	1.1946794839235952;

	public static final double HUNGED_PIECE_O	=	-3.3125098389148473;
	public static final double HUNGED_PIECE_E	=	-1.3169131554502305;

	public static final double HUNGED_PAWNS_O	=	-4.260161037116418;
	public static final double HUNGED_PAWNS_E	=	-0.09239773557113773;

	public static final double HUNGED_ALL_O	=	-8.197402471090916;
	public static final double HUNGED_ALL_E	=	-0.41352170996287846;

}
