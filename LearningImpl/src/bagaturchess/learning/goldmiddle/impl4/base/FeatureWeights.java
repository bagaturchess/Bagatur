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

	public static final double MATERIAL_PAWN_O	=	0.5784954118863109;

	public static final double MATERIAL_KNIGHT_O	=	0.7761125414158323;

	public static final double MATERIAL_BISHOP_O	=	0.7934040369017215;

	public static final double MATERIAL_ROOK_O	=	0.7195422514484597;

	public static final double MATERIAL_QUEEN_O	=	0.7761125414158323;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.6879971448823266;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.6052579311912211;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.8383008003362855;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.8172315702524645;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.3327643682866048;

	public static final double PIECE_SQUARE_TABLE_O	=	0.6607202419131901;

	public static final double PAWN_DOUBLE_O	=	0.3350436068776203;

	public static final double PAWN_CONNECTED_O	=	0.6126919252600227;

	public static final double PAWN_NEIGHBOUR_O	=	0.5948985421620152;

	public static final double PAWN_ISOLATED_O	=	0.5188956650527428;

	public static final double PAWN_BACKWARD_O	=	0.4895079130231809;

	public static final double PAWN_INVERSE_O	=	0.5271674233142326;

	public static final double PAWN_PASSED_O	=	0.5396404499869618;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.5558228329643122;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.9525742461278508;

	public static final double PAWN_SHIELD_O	=	0.4630679144053057;

	public static final double MOBILITY_KNIGHT_O	=	0.5460136451582881;

	public static final double MOBILITY_BISHOP_O	=	0.5913233999841437;

	public static final double MOBILITY_ROOK_O	=	0.5088410833221626;

	public static final double MOBILITY_QUEEN_O	=	0.3928616310275764;

	public static final double MOBILITY_KING_O	=	0.43156054541760624;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.6178582471458146;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.5182933533869746;

	public static final double THREAT_PAWN_PUSH_O	=	0.4075503941266596;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.0868166193859237;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.8154218207040691;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.8081600371784339;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.48766507271217535;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.1724324592471396;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.4260102939810482;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.8777751393140313;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.5381067871609361;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.5323769537523534;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.4456576480401409;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.5061857665869801;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.5145858127158431;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.6574025330325518;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.5292081290352205;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.6518185467280709;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.43084906797360917;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.6519529759320785;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.4985128614077467;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.6944770751242116;

	public static final double OTHERS_CASTLING_O	=	0.6894123234723718;

	public static final double OTHERS_PINNED_O	=	0.585591827111575;

	public static final double OTHERS_DISCOVERED_O	=	0.4739002949130369;

	public static final double KING_SAFETY_O	=	0.9860496424409474;

	public static final double SPACE_O	=	0.7936937836610378;

	public static final double MATERIAL_PAWN_E	=	0.7647474246323746;

	public static final double MATERIAL_KNIGHT_E	=	0.6800656349824491;

	public static final double MATERIAL_BISHOP_E	=	0.6862143535537805;

	public static final double MATERIAL_ROOK_E	=	0.709753644473913;

	public static final double MATERIAL_QUEEN_E	=	0.7647474246323746;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.7270215470549424;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.375257413934201;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.8413010363056894;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.4334727152503332;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.2272346385516128;

	public static final double PIECE_SQUARE_TABLE_E	=	0.5327614749762156;

	public static final double PAWN_DOUBLE_E	=	0.3577627956434346;

	public static final double PAWN_CONNECTED_E	=	0.6516612548856318;

	public static final double PAWN_NEIGHBOUR_E	=	0.5583676851131107;

	public static final double PAWN_ISOLATED_E	=	0.42398375553675205;

	public static final double PAWN_BACKWARD_E	=	0.3861246385017017;

	public static final double PAWN_INVERSE_E	=	0.6575962697747935;

	public static final double PAWN_PASSED_E	=	0.6680712271931565;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.6514667988578163;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.5442012653645552;

	public static final double PAWN_SHIELD_E	=	0.520176893047943;

	public static final double MOBILITY_KNIGHT_E	=	0.5454944545470624;

	public static final double MOBILITY_BISHOP_E	=	0.5758536424357883;

	public static final double MOBILITY_ROOK_E	=	0.41845504173876324;

	public static final double MOBILITY_QUEEN_E	=	0.24794239554738004;

	public static final double MOBILITY_KING_E	=	0.4939743056062735;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.7250218667284526;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.6810506172032142;

	public static final double THREAT_PAWN_PUSH_E	=	0.4550090970472177;

	public static final double THREAT_PAWN_ATTACKS_E	=	1.1060319227400017;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.7390986068604164;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.902990640294502;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.39020259333444024;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.7551249585100294;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1.3196231124161695;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.4174801946623734;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.5;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.5129812977012238;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.6801273870985425;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.5345890625194303;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.540461393540049;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.4727872642553303;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.6060930842743706;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.5172111250701777;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.9073078142890587;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.5;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.5091826508213894;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.48633395485252456;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.5791135189681417;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.9893538120908208;

	public static final double OTHERS_CASTLING_E	=	1.2592006776359834;

	public static final double OTHERS_PINNED_E	=	0.6078019493946523;

	public static final double OTHERS_DISCOVERED_E	=	0.37721452819276025;

	public static final double KING_SAFETY_E	=	0.5795264047720405;

	public static final double SPACE_E	=	0.5138443273626321;
}

