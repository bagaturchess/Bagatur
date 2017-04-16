package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


public interface FeatureWeights_org {


	public static final double MATERIAL_DOUBLE_BISHOPS_O	=	31.93581202208902;
	public static final double MATERIAL_DOUBLE_BISHOPS_E	=	50.0258077558985;

	public static final double PST_O	=	1.4210610999755608;
	public static final double PST_E	=	1.173786815310564;

	public static final double STANDARD_TEMPO_O	=	13.836754244102465;
	public static final double STANDARD_TEMPO_E	=	1.394882271630647;

	public static final double STANDARD_CASTLING_O	=	13.724413294895655;
	public static final double STANDARD_CASTLING_E	=	0.0;

	public static final double STANDARD_FIANCHETTO_O	=	0.08618119089488416;
	public static final double STANDARD_FIANCHETTO_E	=	0.0;

	public static final double STANDARD_TRAP_BISHOP_O	=	-61.2692010231268;
	public static final double STANDARD_TRAP_BISHOP_E	=	-47.54758800138753;

	public static final double STANDARD_BLOCKED_PAWN_O	=	-1.3419684259883582;
	public static final double STANDARD_BLOCKED_PAWN_E	=	0.0;

	public static final double STANDARD_KINGS_OPPOSITION_O	=	0.0;
	public static final double STANDARD_KINGS_OPPOSITION_E	=	0.0;

	public static final double PAWNS_KING_GUARDS_O	=	9.1613233750381;
	public static final double PAWNS_KING_GUARDS_E	=	0.0;

	public static final double PAWNS_DOUBLED_O	=	1.6090471128257597;
	public static final double PAWNS_DOUBLED_E	=	0.6463641488898149;

	public static final double PAWNS_ISOLATED_O	=	0.5555498451771783;
	public static final double PAWNS_ISOLATED_E	=	1.3900429397869152;

	public static final double PAWNS_BACKWARD_O	=	1.122375074781971;
	public static final double PAWNS_BACKWARD_E	=	2.580264978175211;

	public static final double PAWNS_SUPPORTED_O	=	1.7133992472679733;
	public static final double PAWNS_SUPPORTED_E	=	0.1785714083746427;

	public static final double PAWNS_CANDIDATE_O	=	4.504355580737771;
	public static final double PAWNS_CANDIDATE_E	=	1.6397503814840744;

	public static final double PAWNS_PASSED_SUPPORTED_O	=	2.3031406543701376;
	public static final double PAWNS_PASSED_SUPPORTED_E	=	1.4685822408606701;

	public static final double PAWNS_PASSED_O	=	4.983703363490012;
	public static final double PAWNS_PASSED_E	=	0.019844546335608042;

	public static final double PAWNS_KING_F_O	=	0.0;
	public static final double PAWNS_KING_F_E	=	9.28603333720855;

	public static final double PAWNS_KING_FF_O	=	0.0;
	public static final double PAWNS_KING_FF_E	=	2.2573805906116435;

	public static final double PAWNS_KING_OP_F_O	=	0.0;
	public static final double PAWNS_KING_OP_F_E	=	0.6626140030381598;

	public static final double PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PASSED_UNSTOPPABLE_E	=	0.0;

	public static final double PAWNS_PASSED_STOPPERS_O	=	0.0;
	public static final double PAWNS_PASSED_STOPPERS_E	=	0.0;

	public static final double PAWNS_ROOK_OPENED_O	=	14.954380973636365;
	public static final double PAWNS_ROOK_OPENED_E	=	15.990948695067864;

	public static final double PAWNS_ROOK_SEMIOPENED_O	=	10.959879435091217;
	public static final double PAWNS_ROOK_SEMIOPENED_E	=	0.43921998797470174;

	public static final double PAWNS_ROOK_7TH2TH_O	=	10.76299165595431;
	public static final double PAWNS_ROOK_7TH2TH_E	=	16.924302739831447;

	public static final double PAWNS_QUEEN_7TH2TH_O	=	16.392739283605703;
	public static final double PAWNS_QUEEN_7TH2TH_E	=	2.091544612740822;

	public static final double PAWNS_KING_OPENED_O	=	-9.93134545100315;
	public static final double PAWNS_KING_OPENED_E	=	12.495089170567534;

	public static final double MOBILITY_KNIGHT_O	=	3.1223891297479502;
	public static final double MOBILITY_KNIGHT_E	=	1.0631970902562673;

	public static final double MOBILITY_BISHOP_O	=	2.511141696380688;
	public static final double MOBILITY_BISHOP_E	=	0.796409535140043;

	public static final double MOBILITY_ROOK_O	=	1.7864972172366254;
	public static final double MOBILITY_ROOK_E	=	0.9967536970196726;

	public static final double MOBILITY_QUEEN_O	=	0.07553874836776885;
	public static final double MOBILITY_QUEEN_E	=	0.02127771310394556;

	public static final double KNIGHT_OUTPOST_O	=	1.9496355111001002;
	public static final double KNIGHT_OUTPOST_E	=	0.0;

	public static final double BISHOP_OUTPOST_O	=	2.432899244798851;
	public static final double BISHOP_OUTPOST_E	=	1.652467177031604;

	public static final double BISHOP_BAD_O	=	0.09657146473829128;
	public static final double BISHOP_BAD_E	=	0.25220125783905006;

	public static final double KING_SAFETY_O	=	3.623873280282296;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	0.6552485268140964;
	public static final double SPACE_E	=	0.7415545760620151;

	public static final double HUNGED_O	=	0.0;
	public static final double HUNGED_E	=	0.20194099586952743;

	public static final double MOBILITY_KNIGHT_S_O	=	0.5219841185457408;
	public static final double MOBILITY_KNIGHT_S_E	=	0.5492768439182836;

	public static final double MOBILITY_BISHOP_S_O	=	1.4698070312668792;
	public static final double MOBILITY_BISHOP_S_E	=	0.4266110871072428;

	public static final double MOBILITY_ROOK_S_O	=	2.639530770885151;
	public static final double MOBILITY_ROOK_S_E	=	0.17441116565704082;

	public static final double MOBILITY_QUEEN_S_O	=	1.3471235976323075;
	public static final double MOBILITY_QUEEN_S_E	=	1.0982197247833707;

	public static final double TRAPED_O	=	0.0;
	public static final double TRAPED_E	=	0.0;

	public static final double PASSERS_FRONT_ATTACKS_O	=	0.020783256292033293;
	public static final double PASSERS_FRONT_ATTACKS_E	=	6.123048366918392;


}
