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

	public static final double MATERIAL_PAWN_O	=	0.5863420596553173;

	public static final double MATERIAL_KNIGHT_O	=	0.7772707365870575;

	public static final double MATERIAL_BISHOP_O	=	0.7935419035174985;

	public static final double MATERIAL_ROOK_O	=	0.7190530789277395;

	public static final double MATERIAL_QUEEN_O	=	0.6255047865312556;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.6985085137447838;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.6488410857582467;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.8509343414123658;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.8376771247597046;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.33100395943376476;

	public static final double PIECE_SQUARE_TABLE_O	=	0.6889708936859309;

	public static final double PAWN_DOUBLE_O	=	0.3143266322549482;

	public static final double PAWN_CONNECTED_O	=	0.6540741062279237;

	public static final double PAWN_NEIGHBOUR_O	=	0.628652687544038;

	public static final double PAWN_ISOLATED_O	=	0.5229140239706992;

	public static final double PAWN_BACKWARD_O	=	0.4818252540626859;

	public static final double PAWN_INVERSE_O	=	0.5275733748483831;

	public static final double PAWN_PASSED_O	=	0.5325962615761565;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.5748775363603819;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.9562854724735248;

	public static final double PAWN_SHIELD_O	=	0.45147370242111295;

	public static final double MOBILITY_KNIGHT_O	=	0.54736217816542;

	public static final double MOBILITY_BISHOP_O	=	0.5908474200030696;

	public static final double MOBILITY_ROOK_O	=	0.5195042855522819;

	public static final double MOBILITY_QUEEN_O	=	0.3798307290648821;

	public static final double MOBILITY_KING_O	=	0.4043450974233327;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.6356071619509069;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.49350037572534494;

	public static final double THREAT_PAWN_PUSH_O	=	0.38886107015318705;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.31281088413518;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.8871203300112035;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.9290249384500622;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.5066700980187372;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.492912220355582;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.8704373038228455;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.97821654865402;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.5365859568406623;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.5330978893002768;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.4033691421220137;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.5073999382865827;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.4959754474645797;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.700431163952609;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.548229656818569;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.6453139100410592;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.4140880922488354;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.6758126770957553;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.502264762012135;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.7170684829185735;

	public static final double OTHERS_CASTLING_O	=	0.7277407619801686;

	public static final double OTHERS_PINNED_O	=	0.6189114326208208;

	public static final double OTHERS_DISCOVERED_O	=	0.4909933479395378;

	public static final double KING_SAFETY_O	=	1.1143498821191187;

	public static final double SPACE_O	=	0.8393102323583491;

	public static final double MATERIAL_PAWN_E	=	0.7873772326396882;

	public static final double MATERIAL_KNIGHT_E	=	0.682366796049846;

	public static final double MATERIAL_BISHOP_E	=	0.689385765861764;

	public static final double MATERIAL_ROOK_E	=	0.7132332989442368;

	public static final double MATERIAL_QUEEN_E	=	0.5168374571869393;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.7772937619489211;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.36637503915272596;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.8767330609848667;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.37683223992292836;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.20236089043123395;

	public static final double PIECE_SQUARE_TABLE_E	=	0.5377445535162237;

	public static final double PAWN_DOUBLE_E	=	0.3510140309661018;

	public static final double PAWN_CONNECTED_E	=	0.6715530659288164;

	public static final double PAWN_NEIGHBOUR_E	=	0.5506035165846905;

	public static final double PAWN_ISOLATED_E	=	0.42189639049953664;

	public static final double PAWN_BACKWARD_E	=	0.36653003130659306;

	public static final double PAWN_INVERSE_E	=	0.7008658813921437;

	public static final double PAWN_PASSED_E	=	0.6707868317970568;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.6994009363637556;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.5032108777802474;

	public static final double PAWN_SHIELD_E	=	0.5127040550482895;

	public static final double MOBILITY_KNIGHT_E	=	0.5376653144312864;

	public static final double MOBILITY_BISHOP_E	=	0.5617432685422686;

	public static final double MOBILITY_ROOK_E	=	0.3720241271761086;

	public static final double MOBILITY_QUEEN_E	=	0.20497952674187886;

	public static final double MOBILITY_KING_E	=	0.472271241238816;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.7836299736396188;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.6818723748568097;

	public static final double THREAT_PAWN_PUSH_E	=	0.43750327826763435;

	public static final double THREAT_PAWN_ATTACKS_E	=	1.3593676554405345;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.7809949895210978;

	public static final double THREAT_MAJOR_ATTACKED_E	=	1.0781782902792207;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.3586351595599413;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.8502105475689865;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1.773869434205635;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.8461371860919045;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.5257985009195135;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.6727917362605558;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.5105947542768248;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.5757988099942134;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.42321987804289507;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.6418389703531913;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.519806940857564;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.9672090851468162;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.4917718252299657;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.45386745265891026;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.5783208272719874;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.0953049156622867;

	public static final double OTHERS_CASTLING_E	=	1.5511419554980002;

	public static final double OTHERS_PINNED_E	=	0.6554872432835184;

	public static final double OTHERS_DISCOVERED_E	=	0.36322143419892194;

	public static final double KING_SAFETY_E	=	0.5800037752022673;

	public static final double SPACE_E	=	0.5528843121948097;
}

