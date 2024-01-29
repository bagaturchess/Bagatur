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

	public static final double MATERIAL_PAWN_O	=	0.4868298593900383;

	public static final double MATERIAL_KNIGHT_O	=	0.5317888782772203;

	public static final double MATERIAL_BISHOP_O	=	0.5958978212167744;

	public static final double MATERIAL_ROOK_O	=	0.6188608027505909;

	public static final double MATERIAL_QUEEN_O	=	0.4762106430847727;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	1.6054529072579355;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.9809376930098922;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.8816712543408105;

	public static final double PIECE_SQUARE_TABLE_O	=	0.6898981152288535;

	public static final double PAWN_DOUBLE_O	=	0.0;

	public static final double PAWN_CONNECTED_O	=	0.7493593071806031;

	public static final double PAWN_NEIGHBOUR_O	=	0.7772953831687781;

	public static final double PAWN_ISOLATED_O	=	0.3459514341965993;

	public static final double PAWN_BACKWARD_O	=	0.4833987932914077;

	public static final double PAWN_INVERSE_O	=	0.031155131581674454;

	public static final double PAWN_PASSED_O	=	0.37357447222540513;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.3100974659370334;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.4538675357070392;

	public static final double PAWN_SHIELD_O	=	0.35259916246936157;

	public static final double MOBILITY_KNIGHT_O	=	1.1221318724844547;

	public static final double MOBILITY_BISHOP_O	=	0.9845554758734895;

	public static final double MOBILITY_ROOK_O	=	0.5408307151349573;

	public static final double MOBILITY_QUEEN_O	=	0.6498247714944163;

	public static final double MOBILITY_KING_O	=	0.028234670754317025;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.36097619067370434;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.29854656058082935;

	public static final double THREAT_PAWN_PUSH_O	=	0.4284054283252101;

	public static final double THREAT_PAWN_ATTACKS_O	=	0.613098103193669;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.127698730277552;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.06226948358965855;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.355568604558239;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.2978738993224831;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.33560666359153574;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.4728642625376388;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.14347469664777246;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.5842207376491294;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.28269085629495994;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.198414353193201;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.42217249276788016;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.9487009615861209;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	1.507424317171827;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	1.2503915992983354;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.01;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.6404916122682229;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.578026178882503;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.09680167178837785;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.0230322107284826;

	public static final double OTHERS_CASTLING_O	=	0.6242154140819822;

	public static final double OTHERS_PINNED_O	=	0.2806315050878942;

	public static final double OTHERS_DISCOVERED_O	=	0.5263035802073598;

	public static final double KING_SAFETY_O	=	1.1377344096787738;

	public static final double SPACE_O	=	1.6516562721312744;

	public static final double MATERIAL_PAWN_E	=	0.9392599156102174;

	public static final double MATERIAL_KNIGHT_E	=	0.7162595587136366;

	public static final double MATERIAL_BISHOP_E	=	0.8120828855480442;

	public static final double MATERIAL_ROOK_E	=	0.8272046993674128;

	public static final double MATERIAL_QUEEN_E	=	0.7162006073809273;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.29307529390946546;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.030451831104217737;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.7240003583707537;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.07524625231653437;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.10821821833469661;

	public static final double PIECE_SQUARE_TABLE_E	=	2.0;

	public static final double PAWN_DOUBLE_E	=	1.6083305365553329;

	public static final double PAWN_CONNECTED_E	=	0.6158774465834832;

	public static final double PAWN_NEIGHBOUR_E	=	0.7540813055849522;

	public static final double PAWN_ISOLATED_E	=	1.177116627351469;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_E	=	0.6902829239558931;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.35245825838073364;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.400769108449512;

	public static final double PAWN_SHIELD_E	=	0.03587380452040573;

	public static final double MOBILITY_KNIGHT_E	=	0.03817382265294695;

	public static final double MOBILITY_BISHOP_E	=	0.26116567197037754;

	public static final double MOBILITY_ROOK_E	=	0.039925888433388124;

	public static final double MOBILITY_QUEEN_E	=	0.03076567821814911;

	public static final double MOBILITY_KING_E	=	0.0288542494085221;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.7224296849233481;

	public static final double THREAT_UNUSED_OUTPOST_E	=	1.5077414662719484;

	public static final double THREAT_PAWN_PUSH_E	=	0.7169906297598292;

	public static final double THREAT_PAWN_ATTACKS_E	=	0.433187847434293;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.0130064219175353;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.08089461523630016;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	0.0;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.3494600739775007;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.4338743344121825;

	public static final double OTHERS_ROOK_BATTERY_E	=	1.2114766977436195;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.033108426063304906;

	public static final double OTHERS_ROOK_TRAPPED_E	=	1.3594330236382224;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.06505872990703149;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.09896269132807478;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.40677776323677045;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.5619730850886013;

	public static final double OTHERS_BISHOP_PRISON_E	=	1.7445320139336231;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.11847809334693948;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.5937161498109533;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.4041559085851889;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.0703894370013773;

	public static final double OTHERS_CASTLING_E	=	2.0;

	public static final double OTHERS_PINNED_E	=	0.43056313275364483;

	public static final double OTHERS_DISCOVERED_E	=	0.0;

	public static final double KING_SAFETY_E	=	0.41274059353621745;

	public static final double SPACE_E	=	0.7875056053868584;
}

