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

	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_O	=	0.668238390541664;
	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_E	=	2.3752607239029313;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.0;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.6042896201234712;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6445845477596686;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.20069308209495662;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.3607115557300546;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.0;

	public static final double PIECE_SQUARE_TABLE_O	=	1.0163968538118242;
	public static final double PIECE_SQUARE_TABLE_E	=	0.6047488532420567;

	public static final double PAWN_DOUBLE_O	=	2.2907989775497115;
	public static final double PAWN_DOUBLE_E	=	0.4505256907519537;

	public static final double PAWN_CONNECTED_O	=	1.36581044813321;
	public static final double PAWN_CONNECTED_E	=	0.8426981177007916;

	public static final double PAWN_NEIGHBOUR_O	=	2.2902673045123922;
	public static final double PAWN_NEIGHBOUR_E	=	0.5352947880458128;

	public static final double PAWN_ISOLATED_O	=	0.65349700173986;
	public static final double PAWN_ISOLATED_E	=	0.9246330628039068;

	public static final double PAWN_BACKWARD_O	=	0.3583703390878174;
	public static final double PAWN_BACKWARD_E	=	1.5560197561289089;

	public static final double PAWN_INVERSE_O	=	2.440407806147226;
	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_O	=	0.23601031361043068;
	public static final double PAWN_PASSED_E	=	1.1810275235663357;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.37499419084559216;
	public static final double PAWN_PASSED_CANDIDATE_E	=	0.0931805051181649;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1.4694684366167003;

	public static final double PAWN_SHIELD_O	=	0.6735808499736997;
	public static final double PAWN_SHIELD_E	=	0.44697234032371064;

	public static final double MOBILITY_KNIGHT_O	=	0.7844784593540903;
	public static final double MOBILITY_KNIGHT_E	=	1.3878956976372232;

	public static final double MOBILITY_BISHOP_O	=	0.814425500963551;
	public static final double MOBILITY_BISHOP_E	=	1.2995474399639257;

	public static final double MOBILITY_ROOK_O	=	1.210315495662792;
	public static final double MOBILITY_ROOK_E	=	0.600855594652176;

	public static final double MOBILITY_QUEEN_O	=	1.4047159291677478;
	public static final double MOBILITY_QUEEN_E	=	0.9622854023085144;

	public static final double MOBILITY_KING_O	=	1.0097772968369316;
	public static final double MOBILITY_KING_E	=	0.6994726763158098;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.8837604634620049;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.2583302002223458;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.6960367890827093;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.0;

	public static final double THREAT_PAWN_PUSH_O	=	0.9470774836993395;
	public static final double THREAT_PAWN_PUSH_E	=	1.7044236746487953;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.1615829728957754;
	public static final double THREAT_PAWN_ATTACKS_E	=	1.6249597037264243;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.4229023571922386;
	public static final double THREAT_MAJOR_ATTACKED_E	=	0.6132345208223757;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.25049989635430797;
	public static final double THREAT_PAWN_ATTACKED_E	=	2.8637492421128554;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.2518411163908452;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	2.656604102451486;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.2648585977494031;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	4.121851671484796;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.7480626704647414;
	public static final double THREAT_ROOK_ATTACKED_E	=	2.0495952709713263;

	public static final double THREAT_NIGHT_FORK_O	=	0.07449456795595437;
	public static final double THREAT_NIGHT_FORK_E	=	0.3781865907336044;

	public static final double THREAT_NIGHT_FORK_KING_O	=	0.4445577812995442;
	public static final double THREAT_NIGHT_FORK_KING_E	=	0.0;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.0;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.0;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.7903105844256038;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.6490240307830173;

	public static final double OTHERS_HANGING_O	=	0.9587541945797199;
	public static final double OTHERS_HANGING_E	=	1.0548544470339205;

	public static final double OTHERS_HANGING_2_O	=	0.9482664648719593;
	public static final double OTHERS_HANGING_2_E	=	1.0350653294335077;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.4992991465541852;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.658798143210928;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.0;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.30861942792672564;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.0;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1.2851441083020514;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.05166674629006998;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.2092577974812293;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.028308682903743897;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	1.0499923122665697;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	9.3048182233136;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.5246806159249995;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5949604580416531;
	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.0;
	public static final double OTHERS_BISHOP_PAWNS_E	=	0.0;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.9380042503133398;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.4530080514896558;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.0;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.0;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.0833312990165296;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.8646903945143916;

	public static final double OTHERS_IN_CHECK_O	=	1.0;
	public static final double OTHERS_IN_CHECK_E	=	1.0;

	public static final double KING_SAFETY_O	=	0.5601173918757779;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	1.3158795570120836;
	public static final double SPACE_E	=	0.1288180151587428;
}

