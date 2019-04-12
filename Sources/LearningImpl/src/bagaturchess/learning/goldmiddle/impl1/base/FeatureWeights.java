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

	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_O	=	1.2716030344419473;
	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_E	=	2.016346910746481;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.6328973223447502;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.05923004296522592;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.9333093791686199;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.49753377599709575;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.8685790007873078;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.7031477013338845;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1.0947160632648953;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.358252353539501;

	public static final double PIECE_SQUARE_TABLE_O	=	0.9289621119959526;
	public static final double PIECE_SQUARE_TABLE_E	=	0.6556387476553137;

	public static final double PAWN_DOUBLE_O	=	1.0392599912353044;
	public static final double PAWN_DOUBLE_E	=	0.5679792578660832;

	public static final double PAWN_CONNECTED_O	=	1.2250660482213573;
	public static final double PAWN_CONNECTED_E	=	1.0155274969721588;

	public static final double PAWN_NEIGHBOUR_O	=	2.048986661222043;
	public static final double PAWN_NEIGHBOUR_E	=	0.8878874571932224;

	public static final double PAWN_ISOLATED_O	=	0.9089832216545887;
	public static final double PAWN_ISOLATED_E	=	0.7249381672909302;

	public static final double PAWN_BACKWARD_O	=	0.44615918004710103;
	public static final double PAWN_BACKWARD_E	=	1.136780350386702;

	public static final double PAWN_INVERSE_O	=	2.1800685473826102;
	public static final double PAWN_INVERSE_E	=	0.016719734376019667;

	public static final double PAWN_PASSED_O	=	0.6707225410890773;
	public static final double PAWN_PASSED_E	=	1.19193950526383;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1.0564063905365035;
	public static final double PAWN_PASSED_CANDIDATE_E	=	0.6077705106432021;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1.2444148026247954;

	public static final double PAWN_SHIELD_O	=	0.6691291328164173;
	public static final double PAWN_SHIELD_E	=	0.5961651552287527;

	public static final double MOBILITY_KNIGHT_O	=	0.8984695805517915;
	public static final double MOBILITY_KNIGHT_E	=	1.0130609915033613;

	public static final double MOBILITY_BISHOP_O	=	0.880349496444232;
	public static final double MOBILITY_BISHOP_E	=	1.1347643056817855;

	public static final double MOBILITY_ROOK_O	=	1.140583894629046;
	public static final double MOBILITY_ROOK_E	=	0.7871753351263363;

	public static final double MOBILITY_QUEEN_O	=	1.3218044016535642;
	public static final double MOBILITY_QUEEN_E	=	1.1251750027044025;

	public static final double MOBILITY_KING_O	=	1.0584173136795016;
	public static final double MOBILITY_KING_E	=	0.9419695786838239;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	1.0534154767868988;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.22648988326204;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.6002202233859693;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.31451605914966396;

	public static final double THREAT_PAWN_PUSH_O	=	0.944794329837539;
	public static final double THREAT_PAWN_PUSH_E	=	1.685461577622104;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.1864003694690717;
	public static final double THREAT_PAWN_ATTACKS_E	=	1.4674841440631479;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.0294374980650054;
	public static final double THREAT_MAJOR_ATTACKED_E	=	1.2695333759698249;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.8567010171558904;
	public static final double THREAT_PAWN_ATTACKED_E	=	1.5420361649618282;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.5174417991650169;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	1.5987571232755802;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.5088228412170301;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1.8409838260698566;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.766079308361756;
	public static final double THREAT_ROOK_ATTACKED_E	=	2.1084525440675277;

	public static final double THREAT_NIGHT_FORK_O	=	0.15408167718177754;
	public static final double THREAT_NIGHT_FORK_E	=	0.31957481722203335;

	public static final double THREAT_NIGHT_FORK_KING_O	=	0.43496232213524455;
	public static final double THREAT_NIGHT_FORK_KING_E	=	0.0;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.0;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.2332110509871826;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	1.1082982055578652;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.5840487220817658;

	public static final double OTHERS_HANGING_O	=	0.9696495910872801;
	public static final double OTHERS_HANGING_E	=	1.2299770089678639;

	public static final double OTHERS_HANGING_2_O	=	0.9788733806158489;
	public static final double OTHERS_HANGING_2_E	=	1.1400035412631195;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.2649443907523088;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.016312173733948304;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.6219936318726094;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.06820119031394818;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.33070799797193695;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.01907437296929807;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1.0865282177104012;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.45219667101913524;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.8735852331116315;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.7217428633279295;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.017933427261567117;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.6047936048524207;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	2.1434379398845937;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	1.5971041328055946;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5250144549569704;
	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.38775789873822036;
	public static final double OTHERS_BISHOP_PAWNS_E	=	0.33279108643499633;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.9673747485743927;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.7261290037707012;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.013100363442517375;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.25711403491715445;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.937089321276615;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.9399997581947844;

	public static final double OTHERS_IN_CHECK_O	=	1.0;
	public static final double OTHERS_IN_CHECK_E	=	1.0;

	public static final double KING_SAFETY_O	=	0.5640017768042179;
	public static final double KING_SAFETY_E	=	0.025197799249366565;

	public static final double SPACE_O	=	1.2445170818310436;
	public static final double SPACE_E	=	0.4386134270790982;
}

