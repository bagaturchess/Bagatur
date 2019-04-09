/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.learning.goldmiddle.impl1.base;


public interface FeatureWeights {

	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_O	=	1.3045578094124246;
	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_E	=	1.2068724688596724;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	1.3236510119321399;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	1.6789304520017805;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.7949166212869977;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6051113152278252;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	1.0010193228019848;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.2722398589699468;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1.004857980687999;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.11542222578055573;

	public static final double PIECE_SQUARE_TABLE_O	=	0.9807480143561023;
	public static final double PIECE_SQUARE_TABLE_E	=	0.6889525603392338;

	public static final double PAWN_DOUBLE_O	=	1.2143514419729506;
	public static final double PAWN_DOUBLE_E	=	0.9064736487110888;

	public static final double PAWN_CONNECTED_O	=	1.2846650593698623;
	public static final double PAWN_CONNECTED_E	=	0.7814455757487238;

	public static final double PAWN_NEIGHBOUR_O	=	2.0914565555450175;
	public static final double PAWN_NEIGHBOUR_E	=	0.6941296259468221;

	public static final double PAWN_ISOLATED_O	=	0.942680097322143;
	public static final double PAWN_ISOLATED_E	=	0.7080679462373384;

	public static final double PAWN_BACKWARD_O	=	0.36388670718707744;
	public static final double PAWN_BACKWARD_E	=	1.169070595724146;

	public static final double PAWN_INVERSE_O	=	2.014456856915178;
	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_O	=	0.5088731070341913;
	public static final double PAWN_PASSED_E	=	1.1182346023773497;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1.0316326017823705;
	public static final double PAWN_PASSED_CANDIDATE_E	=	0.21497409792533198;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1.467566518250897;

	public static final double PAWN_SHIELD_O	=	0.6953889739452422;
	public static final double PAWN_SHIELD_E	=	0.4657733235874066;

	public static final double MOBILITY_KNIGHT_O	=	0.8537416766417453;
	public static final double MOBILITY_KNIGHT_E	=	0.9823850774426702;

	public static final double MOBILITY_BISHOP_O	=	0.8294274139776936;
	public static final double MOBILITY_BISHOP_E	=	1.0776252142975244;

	public static final double MOBILITY_ROOK_O	=	1.1229512683330554;
	public static final double MOBILITY_ROOK_E	=	0.826159174186134;

	public static final double MOBILITY_QUEEN_O	=	1.3603524188323324;
	public static final double MOBILITY_QUEEN_E	=	1.0842425917476302;

	public static final double MOBILITY_KING_O	=	1.0877629365372417;
	public static final double MOBILITY_KING_E	=	1.0373598307870606;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	1.0101878387626229;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.128892669384746;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.6825640363685017;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.1451176196457508;

	public static final double THREAT_PAWN_PUSH_O	=	0.871293254444692;
	public static final double THREAT_PAWN_PUSH_E	=	1.8547494938702176;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.1756028007619814;
	public static final double THREAT_PAWN_ATTACKS_E	=	1.4697278727726062;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.302679901495511;
	public static final double THREAT_MAJOR_ATTACKED_E	=	0.8499225118891478;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.6915131991620745;
	public static final double THREAT_PAWN_ATTACKED_E	=	1.998221141587281;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.3915172755344658;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	2.0007781803489193;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.4947062039102756;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1.892700627405393;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.7685874945560502;
	public static final double THREAT_ROOK_ATTACKED_E	=	2.0208275175738226;

	public static final double THREAT_NIGHT_FORK_O	=	0.13875896423695805;
	public static final double THREAT_NIGHT_FORK_E	=	0.2578260353838249;

	public static final double THREAT_NIGHT_FORK_KING_O	=	0.4425447318239932;
	public static final double THREAT_NIGHT_FORK_KING_E	=	0.0;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.0;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.16489271019361745;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	1.0756047102867121;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.7188350465621935;

	public static final double OTHERS_HANGING_O	=	0.9572405551679501;
	public static final double OTHERS_HANGING_E	=	1.2166391353066435;

	public static final double OTHERS_HANGING_2_O	=	0.9637941353029853;
	public static final double OTHERS_HANGING_2_E	=	1.1567760349083032;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.880121599697858;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.535705194885488;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.0;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.3052296377453949;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.038062877918912615;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1.1062088671908386;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.5055891641297775;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.8502302769321938;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.9500982043935455;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.2614155279801749;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	2.754626078955195;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	1.7766376851477572;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5812595631421477;
	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.9351676604614927;
	public static final double OTHERS_BISHOP_PAWNS_E	=	1.2408309172775693;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.8537619935603662;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.6420854576086543;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.0;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.0;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.0450431053301121;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.8243859666384269;

	public static final double OTHERS_IN_CHECK_O	=	1.0;
	public static final double OTHERS_IN_CHECK_E	=	1.0;

	public static final double KING_SAFETY_O	=	0.562015776560893;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	1.3168596636320862;
	public static final double SPACE_E	=	0.2899540786929462;
}

