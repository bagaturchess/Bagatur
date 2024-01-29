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

	public static final double MATERIAL_PAWN_O	=	0.2823096913718286;

	public static final double MATERIAL_KNIGHT_O	=	0.5615467211361024;

	public static final double MATERIAL_BISHOP_O	=	0.5952941368266245;

	public static final double MATERIAL_ROOK_O	=	0.5097578855419893;

	public static final double MATERIAL_QUEEN_O	=	0.4404256436118208;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.5399464138512867;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	1.8319298061574059;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	1.5840107684171085;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.6476239541289396;

	public static final double PIECE_SQUARE_TABLE_O	=	0.7571587640508578;

	public static final double PAWN_DOUBLE_O	=	0.015604517442710917;

	public static final double PAWN_CONNECTED_O	=	0.0;

	public static final double PAWN_NEIGHBOUR_O	=	0.7949591718940838;

	public static final double PAWN_ISOLATED_O	=	0.34441898005546834;

	public static final double PAWN_BACKWARD_O	=	2.0;

	public static final double PAWN_INVERSE_O	=	2.0;

	public static final double PAWN_PASSED_O	=	0.011297480266755448;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.8488948508896871;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.021705872860826394;

	public static final double PAWN_SHIELD_O	=	0.05912005862436471;

	public static final double MOBILITY_KNIGHT_O	=	0.045458166074447165;

	public static final double MOBILITY_BISHOP_O	=	0.686100988662307;

	public static final double MOBILITY_ROOK_O	=	0.0;

	public static final double MOBILITY_QUEEN_O	=	1.0049500191181746;

	public static final double MOBILITY_KING_O	=	0.0;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.14344764402368831;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.13930734384224466;

	public static final double THREAT_PAWN_PUSH_O	=	1.1075543185344947;

	public static final double THREAT_PAWN_ATTACKS_O	=	0.9693918471854683;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.04099617555072816;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.3597900062172926;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.0828807213658327;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.5061745225736741;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.5669249188510719;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.1469255054551696;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.011066883263874;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.9780165460286655;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.04686446250856748;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	2.0;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.0;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.01;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.4209930970737213;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	2.0;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.6340141027479288;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.056095673347074805;

	public static final double OTHERS_CASTLING_O	=	0.05146753061842988;

	public static final double OTHERS_PINNED_O	=	0.7029835142527139;

	public static final double OTHERS_DISCOVERED_O	=	0.9604473184315416;

	public static final double KING_SAFETY_O	=	1.500001827701419;

	public static final double SPACE_O	=	2.0;

	public static final double MATERIAL_PAWN_E	=	0.8602709934977086;

	public static final double MATERIAL_KNIGHT_E	=	0.779424513339074;

	public static final double MATERIAL_BISHOP_E	=	0.9132626340770923;

	public static final double MATERIAL_ROOK_E	=	0.9269126465911035;

	public static final double MATERIAL_QUEEN_E	=	0.6595093801153825;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	1.9082748409486163;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.48159895365167676;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.09523342531417839;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.01069031159193054;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	2.0;

	public static final double PIECE_SQUARE_TABLE_E	=	1.1264937209167416;

	public static final double PAWN_DOUBLE_E	=	1.6807465636691992;

	public static final double PAWN_CONNECTED_E	=	0.05271019039835463;

	public static final double PAWN_NEIGHBOUR_E	=	0.04288693432409943;

	public static final double PAWN_ISOLATED_E	=	1.000634440937156;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	0.012765721061038153;

	public static final double PAWN_PASSED_E	=	0.7604956188380778;

	public static final double PAWN_PASSED_CANDIDATE_E	=	1.2554014854190771;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.20653756048300503;

	public static final double PAWN_SHIELD_E	=	0.030399251331047147;

	public static final double MOBILITY_KNIGHT_E	=	1.0809020518768657;

	public static final double MOBILITY_BISHOP_E	=	0.5497436446367888;

	public static final double MOBILITY_ROOK_E	=	0.2235441410684418;

	public static final double MOBILITY_QUEEN_E	=	0.29085917514662546;

	public static final double MOBILITY_KING_E	=	0.2164536292072775;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.10194694877785226;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.7017689048333394;

	public static final double THREAT_PAWN_PUSH_E	=	0.12170358444054181;

	public static final double THREAT_PAWN_ATTACKS_E	=	0.8028344167601855;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.8880904709153874;

	public static final double THREAT_MAJOR_ATTACKED_E	=	1.2846220562018973;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	0.0;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.934466112206885;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	2.0;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.04124993737806967;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	1.8232869397147131;

	public static final double OTHERS_ROOK_TRAPPED_E	=	1.4955926956658665;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.19513800417451627;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.10639373689739406;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.5077905324872315;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.8563072657504819;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.8729723128422457;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.19352913015170645;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.26351195651309606;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.09473970716579316;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.6460615041336859;

	public static final double OTHERS_CASTLING_E	=	0.07312070632873169;

	public static final double OTHERS_PINNED_E	=	0.1499368246519472;

	public static final double OTHERS_DISCOVERED_E	=	0.16026838420589717;

	public static final double KING_SAFETY_E	=	0.11285336783198546;

	public static final double SPACE_E	=	0.41451508664443;
}

