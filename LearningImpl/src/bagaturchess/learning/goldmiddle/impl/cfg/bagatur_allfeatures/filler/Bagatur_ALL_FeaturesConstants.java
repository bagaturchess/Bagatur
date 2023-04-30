package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;


interface Bagatur_ALL_FeaturesConstants {
	
	
	//Standard
	public static final int FEATURE_ID_MATERIAL_PAWN       			= 1010;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     			= 1020;
	public static final int FEATURE_ID_MATERIAL_BISHOP     			= 1030;
	public static final int FEATURE_ID_MATERIAL_ROOK      			= 1040;
	public static final int FEATURE_ID_MATERIAL_QUEEN      			= 1050;
	public static final int FEATURE_ID_MATERIAL_DOUBLE_BISHOP 		= 1060;
	
	public static final int FEATURE_ID_PST					 		= 1065;
	
	public static final int FEATURE_ID_STANDARD_TEMPO 				= 1070;
	public static final int FEATURE_ID_STANDARD_CASTLING 			= 1080;
	public static final int FEATURE_ID_STANDARD_FIANCHETTO 			= 1090;
	public static final int FEATURE_ID_STANDARD_TRAP_BISHOP 		= 1100;
	public static final int FEATURE_ID_STANDARD_BLOCKED_PAWN 		= 1110;
	public static final int FEATURE_ID_STANDARD_KINGS_OPPOSITION 	= 1120;
	
	
	//Pawns
	public static final int FEATURE_ID_PAWNS_KING_GUARDS 			= 1140;
	public static final int FEATURE_ID_PAWNS_DOUBLED 				= 1150;
	public static final int FEATURE_ID_PAWNS_ISOLATED 				= 1160;
	public static final int FEATURE_ID_PAWNS_BACKWARD 				= 1170;
	public static final int FEATURE_ID_PAWNS_SUPPORTED 				= 1180;
	public static final int FEATURE_ID_PAWNS_CANDIDATE 				= 1190;
	public static final int FEATURE_ID_PAWNS_PASSED_SUPPORTED 		= 1200;
	public static final int FEATURE_ID_PAWNS_PASSED 				= 1210;
	public static final int FEATURE_ID_PAWNS_KING_F 				= 1220;
	public static final int FEATURE_ID_PAWNS_KING_FF 				= 1230;
	public static final int FEATURE_ID_PAWNS_KING_OP_F 				= 1240;
	public static final int FEATURE_ID_PASSED_UNSTOPPABLE 			= 1250;
	public static final int FEATURE_ID_PAWNS_PASSED_STOPPERS 		= 1260;
	public static final int FEATURE_ID_PAWNS_ROOK_OPENED 			= 1270;
	public static final int FEATURE_ID_PAWNS_ROOK_SEMIOPENED 		= 1280;
	public static final int FEATURE_ID_PAWNS_ROOK_7TH2TH 			= 1290;
	public static final int FEATURE_ID_PAWNS_QUEEN_7TH2TH 			= 1300;
	public static final int FEATURE_ID_PAWNS_KING_OPENED 			= 1310;
	
	
	//Moves iteration
	public static final int FEATURE_ID_MOBILITY_KNIGHT 				= 1400;
	public static final int FEATURE_ID_MOBILITY_BISHOP 				= 1410;
	public static final int FEATURE_ID_MOBILITY_ROOK 				= 1420;
	public static final int FEATURE_ID_MOBILITY_QUEEN 				= 1430;
	public static final int FEATURE_ID_KNIGHT_OUTPOST 				= 1440;
	public static final int FEATURE_ID_BISHOP_OUTPOST				= 1450;
	public static final int FEATURE_ID_BISHOP_BAD					= 1460;
	
	public static final int FEATURE_ID_KING_SAFETY 					= 1470;
	public static final int FEATURE_ID_SPACE 						= 1480;
	public static final int FEATURE_ID_HUNGED 						= 1490;
	
	public static final int FEATURE_ID_MOBILITY_KNIGHT_S 			= 1500;
	public static final int FEATURE_ID_MOBILITY_BISHOP_S 			= 1510;
	public static final int FEATURE_ID_MOBILITY_ROOK_S 				= 1520;
	public static final int FEATURE_ID_MOBILITY_QUEEN_S 			= 1530;
	public static final int FEATURE_ID_TRAPED 						= 1540;
	public static final int FEATURE_ID_PASSERS_FRONT_ATTACKS 		= 1550;
}
