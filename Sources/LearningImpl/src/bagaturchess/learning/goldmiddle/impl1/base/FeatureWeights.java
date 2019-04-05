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
	

	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_O	=	1.2048503272972018;
	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_E	=	1.3540063071992692;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.43910942374524414;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.9018072044392903;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.8270618278516081;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6306942552668984;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.7558264154836487;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.5846288127777824;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1.0169364989100358;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.10800608161222208;

	public static final double PIECE_SQUARE_TABLE_O	=	1.0749079660482341;
	public static final double PIECE_SQUARE_TABLE_E	=	0.7031569671621443;

	public static final double PAWN_DOUBLE_O	=	2.2227126198314844;
	public static final double PAWN_DOUBLE_E	=	0.4168524031888213;

	public static final double PAWN_CONNECTED_O	=	1.121379898314322;
	public static final double PAWN_CONNECTED_E	=	0.5709219196384829;

	public static final double PAWN_NEIGHBOUR_O	=	1.1691076262741038;
	public static final double PAWN_NEIGHBOUR_E	=	0.8854615807450738;

	public static final double PAWN_ISOLATED_O	=	0.8372112456126334;
	public static final double PAWN_ISOLATED_E	=	0.43907107007754037;

	public static final double PAWN_BACKWARD_O	=	0.43584017549270243;
	public static final double PAWN_BACKWARD_E	=	1.1548882192754075;

	public static final double PAWN_INVERSE_O	=	1.775968869545295;
	public static final double PAWN_INVERSE_E	=	0.025508043027007196;

	public static final double PAWN_PASSED_O	=	0.4681683864757687;
	public static final double PAWN_PASSED_E	=	0.7877227644951005;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.5789575890760934;
	public static final double PAWN_PASSED_CANDIDATE_E	=	0.5988865502491775;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1.699406305655834;

	public static final double PAWN_SHIELD_O	=	0.6351678882672557;
	public static final double PAWN_SHIELD_E	=	0.4699305029907402;

	public static final double MOBILITY_KNIGHT_O	=	1.5091995183623765;
	public static final double MOBILITY_KNIGHT_E	=	1.0297532411816597;

	public static final double MOBILITY_BISHOP_O	=	0.9462793076177312;
	public static final double MOBILITY_BISHOP_E	=	0.8215619565041249;

	public static final double MOBILITY_ROOK_O	=	1.2937684624192252;
	public static final double MOBILITY_ROOK_E	=	0.8558927347127204;

	public static final double MOBILITY_QUEEN_O	=	2.4338888878389913;
	public static final double MOBILITY_QUEEN_E	=	0.15103576183202022;

	public static final double MOBILITY_KING_O	=	0.7704850495452492;
	public static final double MOBILITY_KING_E	=	0.262238416837902;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.6462810856792165;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.6199170933635083;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.5570299214501636;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.277981892314339;

	public static final double THREAT_PAWN_PUSH_O	=	1.1376815203354123;
	public static final double THREAT_PAWN_PUSH_E	=	1.4787602508399493;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.3234636264156117;
	public static final double THREAT_PAWN_ATTACKS_E	=	2.2225100654376373;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.9797190831496643;
	public static final double THREAT_MAJOR_ATTACKED_E	=	1.0031228975967996;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.30603743576535525;
	public static final double THREAT_PAWN_ATTACKED_E	=	1.1773812293000485;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.7631227917472355;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	7.6645609546603675;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.9360051751340661;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	13.166722028613211;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.8366509521315826;
	public static final double THREAT_ROOK_ATTACKED_E	=	3.7201930565405408;

	public static final double THREAT_NIGHT_FORK_O	=	0.10300051765041005;
	public static final double THREAT_NIGHT_FORK_E	=	0.27911095260487584;

	public static final double THREAT_NIGHT_FORK_KING_O	=	0.4091719496899175;
	public static final double THREAT_NIGHT_FORK_KING_E	=	0.0;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.0;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.027112056265987575;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.9753636348034449;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.48711102185202615;

	public static final double OTHERS_HANGING_O	=	3.1956769388172503;
	public static final double OTHERS_HANGING_E	=	1.1327325732056759;

	public static final double OTHERS_HANGING_2_O	=	1.6410993867670298;
	public static final double OTHERS_HANGING_2_E	=	1.1947839001240605;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.5138195320756427;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.0141656931601268;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.02624019620669861;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.2659498920367311;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.0;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.5624417795680597;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.3581681104295696;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.31621641682220714;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.5864574033730088;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.6447264494232144;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.18856689661455256;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.48268659183282275;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.6090854034487705;
	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.4784772589471352;
	public static final double OTHERS_BISHOP_PAWNS_E	=	0.8544223467599128;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.7521604145550952;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.49365554542706247;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	1.1280304082611747;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.1284917223045031;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.2832018649156678;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.2719829114186131;

	public static final double OTHERS_IN_CHECK_O	=	1.0;
	public static final double OTHERS_IN_CHECK_E	=	1.0;

	public static final double KING_SAFETY_O	=	0.6134723786421433;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	1.4084977366518123;
	public static final double SPACE_E	=	0.2750331620736511;


}

