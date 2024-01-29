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

	public static final double MATERIAL_PAWN_O	=	0.4628383402644211;

	public static final double MATERIAL_KNIGHT_O	=	0.524366465938769;

	public static final double MATERIAL_BISHOP_O	=	0.592252927500943;

	public static final double MATERIAL_ROOK_O	=	0.6148452521319457;

	public static final double MATERIAL_QUEEN_O	=	0.4753524478663928;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	1.8793648890349688;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	1.0460962213869023;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.8573357171189065;

	public static final double PIECE_SQUARE_TABLE_O	=	0.6946819832222364;

	public static final double PAWN_DOUBLE_O	=	0.7654118671625619;

	public static final double PAWN_CONNECTED_O	=	0.9340341669126572;

	public static final double PAWN_NEIGHBOUR_O	=	1.0066086602198354;

	public static final double PAWN_ISOLATED_O	=	0.4910297562907169;

	public static final double PAWN_BACKWARD_O	=	0.5038146411543263;

	public static final double PAWN_INVERSE_O	=	0.08945596208471365;

	public static final double PAWN_PASSED_O	=	0.4053400087235183;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.48476489797779565;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.42195100184946926;

	public static final double PAWN_SHIELD_O	=	0.34367205156288383;

	public static final double MOBILITY_KNIGHT_O	=	1.0929088669577385;

	public static final double MOBILITY_BISHOP_O	=	0.9757021799878205;

	public static final double MOBILITY_ROOK_O	=	0.8219496574076625;

	public static final double MOBILITY_QUEEN_O	=	0.487237029516821;

	public static final double MOBILITY_KING_O	=	0.02433292130781415;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.48575364791786035;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.2428565192750893;

	public static final double THREAT_PAWN_PUSH_O	=	0.417270143452042;

	public static final double THREAT_PAWN_ATTACKS_O	=	0.588311634556746;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.08587872797896338;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.1502183213470614;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.26589722135853033;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.23899712101349715;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.301220310870737;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.5004975216114236;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.18234867695338883;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.0482588869708829;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.2568397630259264;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.24054637663343417;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.48485030258067663;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.4265648225722574;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	1.3240500553014867;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	1.2199524688727181;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.01;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.5653986558463643;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.705859854371181;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.14400500469463057;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.9926578215492783;

	public static final double OTHERS_CASTLING_O	=	0.6000068430806623;

	public static final double OTHERS_PINNED_O	=	0.26497586653192085;

	public static final double OTHERS_DISCOVERED_O	=	0.5553178476784904;

	public static final double KING_SAFETY_O	=	1.135229069474216;

	public static final double SPACE_O	=	1.6154948737187829;

	public static final double MATERIAL_PAWN_E	=	0.9280804228117365;

	public static final double MATERIAL_KNIGHT_E	=	0.7126129084368595;

	public static final double MATERIAL_BISHOP_E	=	0.8120204651945009;

	public static final double MATERIAL_ROOK_E	=	0.8241670992577229;

	public static final double MATERIAL_QUEEN_E	=	0.7147467075591591;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.49647348939048813;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6994321144312163;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.025991498159514785;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.062227698220266595;

	public static final double PIECE_SQUARE_TABLE_E	=	2.0;

	public static final double PAWN_DOUBLE_E	=	1.7707311625240574;

	public static final double PAWN_CONNECTED_E	=	0.704219325830046;

	public static final double PAWN_NEIGHBOUR_E	=	0.7483620657474493;

	public static final double PAWN_ISOLATED_E	=	1.055746574719546;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_E	=	0.7025540930585675;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.7798017224790696;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.3953889576722583;

	public static final double PAWN_SHIELD_E	=	0.04202711709404042;

	public static final double MOBILITY_KNIGHT_E	=	0.0367486213342965;

	public static final double MOBILITY_BISHOP_E	=	0.24651883204828662;

	public static final double MOBILITY_ROOK_E	=	0.035384921062575056;

	public static final double MOBILITY_QUEEN_E	=	0.02908906594632822;

	public static final double MOBILITY_KING_E	=	0.015334313201448025;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.8854887220436929;

	public static final double THREAT_UNUSED_OUTPOST_E	=	1.6595372274085947;

	public static final double THREAT_PAWN_PUSH_E	=	0.5282935645958504;

	public static final double THREAT_PAWN_ATTACKS_E	=	0.37631560652876206;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.013419149732992424;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.02395169498538514;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	0.0;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.3590493490683302;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.5491542782705485;

	public static final double OTHERS_ROOK_BATTERY_E	=	1.1491231863348097;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.013899019300455749;

	public static final double OTHERS_ROOK_TRAPPED_E	=	1.3403528348448746;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.05966089739328406;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.1504963539624941;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.17731855631979945;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.554981324594764;

	public static final double OTHERS_BISHOP_PRISON_E	=	1.7736477864410587;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.12629100756699188;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.7578258303926951;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.52654196436091;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.0919998374378332;

	public static final double OTHERS_CASTLING_E	=	2.0;

	public static final double OTHERS_PINNED_E	=	0.4034898656518719;

	public static final double OTHERS_DISCOVERED_E	=	0.0;

	public static final double KING_SAFETY_E	=	0.42013715516422934;

	public static final double SPACE_E	=	0.7689566983972904;
}

