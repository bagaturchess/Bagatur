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
	
	public static final double MATERIAL_PAWN_O	=	0.6002024113773633;

	public static final double MATERIAL_KNIGHT_O	=	0.8558497171730809;

	public static final double MATERIAL_BISHOP_O	=	0.8532052411739823;

	public static final double MATERIAL_ROOK_O	=	0.7849074406854007;

	public static final double MATERIAL_QUEEN_O	=	0.6455678194813289;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.7458778408111477;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.7149895845143636;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.9271177719832093;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	1.1368375413335294;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.8997562291379797;

	public static final double PIECE_SQUARE_TABLE_O	=	0.9050000111956145;

	public static final double PAWN_DOUBLE_O	=	1.0102240426449611;

	public static final double PAWN_CONNECTED_O	=	1.0507688338456287;

	public static final double PAWN_NEIGHBOUR_O	=	0.8814170336507148;

	public static final double PAWN_ISOLATED_O	=	0.8466405615005665;

	public static final double PAWN_BACKWARD_O	=	1.1535656678088388;

	public static final double PAWN_INVERSE_O	=	1.00892738020916;

	public static final double PAWN_PASSED_O	=	0.5442723102365531;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.918170633814245;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.5268486709435557;

	public static final double PAWN_SHIELD_O	=	0.6778160134120828;

	public static final double MOBILITY_KNIGHT_O	=	0.748618108980419;

	public static final double MOBILITY_BISHOP_O	=	0.8077280819812085;

	public static final double MOBILITY_ROOK_O	=	0.8205878868363359;

	public static final double MOBILITY_QUEEN_O	=	0.5524035537708608;

	public static final double MOBILITY_KING_O	=	0.7518136655953326;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	1.1057472485603186;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.5650079012546921;

	public static final double THREAT_PAWN_PUSH_O	=	0.7851980356897662;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.8365469392761888;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	1.0066133970206472;

	public static final double THREAT_MAJOR_ATTACKED_O	=	1.8934457596561438;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.9274075846244701;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.8097093335793213;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	2.651388146348429;

	public static final double THREAT_ROOK_ATTACKED_O	=	1.549781033225566;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	1.0;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.79012498573715;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.9229485486701512;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.4886799334286337;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.7535677057199244;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.630273461494276;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.3867545081640735;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.5898730899283562;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.8210867523881189;

	public static final double OTHERS_BISHOP_PRISON_O	=	1.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	1.095205715599617;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.9225776659962874;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.8971866496549212;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.7848933510169923;

	public static final double OTHERS_CASTLING_O	=	1.0478072054920318;

	public static final double OTHERS_PINNED_O	=	0.9982257840493644;

	public static final double OTHERS_DISCOVERED_O	=	0.8694671157125143;

	public static final double KING_SAFETY_O	=	1.691902698898854;

	public static final double SPACE_O	=	1.1950930640401074;

	public static final double MATERIAL_PAWN_E	=	0.82068726567114;

	public static final double MATERIAL_KNIGHT_E	=	0.5660077061633061;

	public static final double MATERIAL_BISHOP_E	=	0.5808126580106464;

	public static final double MATERIAL_ROOK_E	=	0.6381801922346915;

	public static final double MATERIAL_QUEEN_E	=	0.33601155894145324;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.8072376945551007;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.8274228025544819;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6588283400188489;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.4209361320137177;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.9690270914290136;

	public static final double PIECE_SQUARE_TABLE_E	=	0.7405337383858244;

	public static final double PAWN_DOUBLE_E	=	1.1456278968332367;

	public static final double PAWN_CONNECTED_E	=	0.8682573146263893;

	public static final double PAWN_NEIGHBOUR_E	=	0.6717940637273797;

	public static final double PAWN_ISOLATED_E	=	0.7649943996764113;

	public static final double PAWN_BACKWARD_E	=	0.9965606655668715;

	public static final double PAWN_INVERSE_E	=	0.8810895868365147;

	public static final double PAWN_PASSED_E	=	0.7328340241413759;

	public static final double PAWN_PASSED_CANDIDATE_E	=	1.0970084958023278;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.4643749184778489;

	public static final double PAWN_SHIELD_E	=	1.0808368130670192;

	public static final double MOBILITY_KNIGHT_E	=	0.583057192561409;

	public static final double MOBILITY_BISHOP_E	=	0.6071488863006077;

	public static final double MOBILITY_ROOK_E	=	0.6119611987933772;

	public static final double MOBILITY_QUEEN_E	=	0.30586825967035197;

	public static final double MOBILITY_KING_E	=	1.2460200236505614;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.8919358787595882;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.55916515450002;

	public static final double THREAT_PAWN_PUSH_E	=	0.743197769376553;

	public static final double THREAT_PAWN_ATTACKS_E	=	2.1673482004879814;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.9840675655066962;

	public static final double THREAT_MAJOR_ATTACKED_E	=	2.11853616906634;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.9574139780143095;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.25692435729630203;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	0.2042916023811559;

	public static final double THREAT_ROOK_ATTACKED_E	=	2.5445929043622706;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	1.0;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.8953609495984856;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.8538362893339936;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.4180853218059964;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.6815501534417195;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.4245582180029911;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.9805261983869248;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.7318381434754957;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.8485668922324789;

	public static final double OTHERS_BISHOP_PRISON_E	=	1.0;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.7946218934489688;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.7125835131862629;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.8102761856547257;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.8059918965857604;

	public static final double OTHERS_CASTLING_E	=	0.6648943249683976;

	public static final double OTHERS_PINNED_E	=	1.1614000326551919;

	public static final double OTHERS_DISCOVERED_E	=	0.6959612115726808;

	public static final double KING_SAFETY_E	=	0.5544896581410039;

	public static final double SPACE_E	=	1.1560730923350073;
}

