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
package bagaturchess.learning.goldmiddle.impl4.base;


public interface FeatureWeights {

	public static final double MATERIAL_PAWN_O	=	0.5789268027563873;

	public static final double MATERIAL_KNIGHT_O	=	0.776315823884392;

	public static final double MATERIAL_BISHOP_O	=	0.7935011471363392;

	public static final double MATERIAL_ROOK_O	=	0.7195509810386622;

	public static final double MATERIAL_QUEEN_O	=	0.6519354957732427;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.688552720570597;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.6068415137876091;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.8388712201186828;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.8179783852065052;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.33267461466522497;

	public static final double PIECE_SQUARE_TABLE_O	=	0.6617674375682708;

	public static final double PAWN_DOUBLE_O	=	0.334237521388063;

	public static final double PAWN_CONNECTED_O	=	0.6141023734199268;

	public static final double PAWN_NEIGHBOUR_O	=	0.5960713638401226;

	public static final double PAWN_ISOLATED_O	=	0.519093615392834;

	public static final double PAWN_BACKWARD_O	=	0.48921522801175155;

	public static final double PAWN_INVERSE_O	=	0.5271665683189118;

	public static final double PAWN_PASSED_O	=	0.5395163765046587;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.5565375595186222;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.9530286998875073;

	public static final double PAWN_SHIELD_O	=	0.46272499663496003;

	public static final double MOBILITY_KNIGHT_O	=	0.5461300632129266;

	public static final double MOBILITY_BISHOP_O	=	0.591311653308221;

	public static final double MOBILITY_ROOK_O	=	0.5091793918590046;

	public static final double MOBILITY_QUEEN_O	=	0.39243686617399925;

	public static final double MOBILITY_KING_O	=	0.4306061503014664;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.6184807514490166;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.5175626236327118;

	public static final double THREAT_PAWN_PUSH_O	=	0.40686962052340603;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.0944573599565564;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.8182758705938413;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.8121137697174334;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.48830848621166956;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.1825350377629873;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.4398619826991355;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.8812414893586337;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.5380937485071285;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.5323820656481061;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.4442294315586833;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.5061948166197623;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.514010496139251;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.6588014722401686;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.5298986247939271;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.6517868187294884;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.4301907830447321;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.6527959521147545;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.4986357959579766;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.695529887155603;

	public static final double OTHERS_CASTLING_O	=	0.6908118964739891;

	public static final double OTHERS_PINNED_O	=	0.5866992066302315;

	public static final double OTHERS_DISCOVERED_O	=	0.4742278578167541;

	public static final double KING_SAFETY_O	=	0.9904157990209883;

	public static final double SPACE_O	=	0.79532722048264;

	public static final double MATERIAL_PAWN_E	=	0.7657492330981635;

	public static final double MATERIAL_KNIGHT_E	=	0.6801967299992506;

	public static final double MATERIAL_BISHOP_E	=	0.686371327295944;

	public static final double MATERIAL_ROOK_E	=	0.7098860377640556;

	public static final double MATERIAL_QUEEN_E	=	0.515948391139744;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.7288094427117866;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.37499138573611934;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.8426306419862908;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.4314705368902323;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.2263343889701624;

	public static final double PIECE_SQUARE_TABLE_E	=	0.532922289566062;

	public static final double PAWN_DOUBLE_E	=	0.35741626775282676;

	public static final double PAWN_CONNECTED_E	=	0.6524683447704527;

	public static final double PAWN_NEIGHBOUR_E	=	0.558293881428565;

	public static final double PAWN_ISOLATED_E	=	0.4238700853870457;

	public static final double PAWN_BACKWARD_E	=	0.38540862436664636;

	public static final double PAWN_INVERSE_E	=	0.6590114993622325;

	public static final double PAWN_PASSED_E	=	0.6683035962445938;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.6531099644677046;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.5424181811506879;

	public static final double PAWN_SHIELD_E	=	0.519983575393829;

	public static final double MOBILITY_KNIGHT_E	=	0.5452782357106943;

	public static final double MOBILITY_BISHOP_E	=	0.5754478814449955;

	public static final double MOBILITY_ROOK_E	=	0.416793718299391;

	public static final double MOBILITY_QUEEN_E	=	0.24634574915252325;

	public static final double MOBILITY_KING_E	=	0.4932585269481682;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.7269695227739056;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.6811689110971137;

	public static final double THREAT_PAWN_PUSH_E	=	0.45446044558248616;

	public static final double THREAT_PAWN_ATTACKS_E	=	1.1144605625880795;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.7410178027488704;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.908659785539944;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.38917296124608874;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.7581310902884433;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1.332954149177097;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.4310765377815389;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.5134203158823109;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.679906072543327;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.5338103555274175;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.5415361070238917;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.47103190260677624;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.6071804068362832;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.5174128363835381;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.9096294691617225;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.5085577936813205;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.48526032163044186;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.5791138719375781;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.9931943594135935;

	public static final double OTHERS_CASTLING_E	=	1.268772373633646;

	public static final double OTHERS_PINNED_E	=	0.6094480340128513;

	public static final double OTHERS_DISCOVERED_E	=	0.3766526960776713;

	public static final double KING_SAFETY_E	=	0.5795570381568416;

	public static final double SPACE_E	=	0.5150217433925917;
}

