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


public interface FeatureWeights_NNTuning {

	public static final double MATERIAL_PAWN_O	=	0.46571962390446814;
	public static final double MATERIAL_PAWN_E	=	1.4043278060733533;

	public static final double MATERIAL_KNIGHT_O	=	0.6549767575212412;
	public static final double MATERIAL_KNIGHT_E	=	0.7746004884916401;

	public static final double MATERIAL_BISHOP_O	=	0.7137880744156244;
	public static final double MATERIAL_BISHOP_E	=	0.7856244902572077;

	public static final double MATERIAL_ROOK_O	=	0.5387027309991215;
	public static final double MATERIAL_ROOK_E	=	0.961841526589432;

	public static final double MATERIAL_QUEEN_O	=	0.5317891262019162;
	public static final double MATERIAL_QUEEN_E	=	0.605814323479163;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	1.301063258816534;
	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	1.4786718447951877;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	1.6907072015076323;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.21549425042280626;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	1.0603227737727077;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.5524317013893816;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.31276712398561785;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	4.468486218727959;

	public static final double PIECE_SQUARE_TABLE_O	=	0.7624968621126992;
	public static final double PIECE_SQUARE_TABLE_E	=	0.2321188681845692;

	public static final double PAWN_DOUBLE_O	=	1.93559408655271;
	public static final double PAWN_DOUBLE_E	=	3.6912356639635693;

	public static final double PAWN_CONNECTED_O	=	0.9930459044486398;
	public static final double PAWN_CONNECTED_E	=	0.2474522319982168;

	public static final double PAWN_NEIGHBOUR_O	=	0.7672230574177984;
	public static final double PAWN_NEIGHBOUR_E	=	0.0;

	public static final double PAWN_ISOLATED_O	=	0.1445437429658671;
	public static final double PAWN_ISOLATED_E	=	0.0;

	public static final double PAWN_BACKWARD_O	=	0.4898284815988185;
	public static final double PAWN_BACKWARD_E	=	0.4384427777577314;

	public static final double PAWN_INVERSE_O	=	1.9086804222667717;
	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_O	=	0.0;
	public static final double PAWN_PASSED_E	=	1.1454151240747632;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1.1700290819092665;
	public static final double PAWN_PASSED_CANDIDATE_E	=	0.12134798214732148;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.075534094090807;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.0;

	public static final double PAWN_SHIELD_O	=	0.7524274244086873;
	public static final double PAWN_SHIELD_E	=	0.705388727883818;

	public static final double MOBILITY_KNIGHT_O	=	0.728286361848223;
	public static final double MOBILITY_KNIGHT_E	=	3.3317766926483885;

	public static final double MOBILITY_BISHOP_O	=	0.7154837356162778;
	public static final double MOBILITY_BISHOP_E	=	1.2257107764727027;

	public static final double MOBILITY_ROOK_O	=	1.1325294601696774;
	public static final double MOBILITY_ROOK_E	=	0.2887066426467479;

	public static final double MOBILITY_QUEEN_O	=	1.9056327182773805;
	public static final double MOBILITY_QUEEN_E	=	0.057497641436222144;

	public static final double MOBILITY_KING_O	=	0.39671312983989043;
	public static final double MOBILITY_KING_E	=	0.17944391577948704;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.9670428321108258;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.0;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.4561818061774681;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.562640634259897;

	public static final double THREAT_PAWN_PUSH_O	=	0.01303089308445334;
	public static final double THREAT_PAWN_PUSH_E	=	2.4246825651978416;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.9638023861091516;
	public static final double THREAT_PAWN_ATTACKS_E	=	10.469684293202771;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	1.9723900548534654;
	public static final double THREAT_MAJOR_ATTACKED_E	=	5.551892354240476;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.039891780791517735;
	public static final double THREAT_PAWN_ATTACKED_E	=	8.793171918001734;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.33985372837607764;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	16.0;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	5.387370204868142;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	16.0;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.0;
	public static final double THREAT_ROOK_ATTACKED_E	=	12.36171543925302;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	3.8666411941808687;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	9.56993946225479;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.19813710935798967;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.1451233700581639;

	public static final double OTHERS_ROOK_BATTERY_O	=	2.436500571087292;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.122761144571975;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.0;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.4808875409603402;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.0;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1.441159892083192;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	1.0330039329823417;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.4512357680512176;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.3111080058743981;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	3.61194517333281;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	1.4516081461989423;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.4929543024549517;

	public static final double OTHERS_BISHOP_PRISON_O	=	1.0;
	public static final double OTHERS_BISHOP_PRISON_E	=	1.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.2538692846754056;
	public static final double OTHERS_BISHOP_PAWNS_E	=	0.8621698492094462;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.20760645503002653;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	1.1664200569279066;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.09093698489237445;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.0;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.6078264936021895;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.7722869391510208;

	public static final double OTHERS_CASTLING_O	=	0.0;
	public static final double OTHERS_CASTLING_E	=	0.0;

	public static final double OTHERS_PINNED_O	=	0.04191412060233072;
	public static final double OTHERS_PINNED_E	=	1.5557226983876573;

	public static final double OTHERS_DISCOVERED_O	=	0.5873609119796729;
	public static final double OTHERS_DISCOVERED_E	=	0.0;

	public static final double KING_SAFETY_O	=	0.31169052394172897;
	public static final double KING_SAFETY_E	=	0.41985507525873605;

	public static final double SPACE_O	=	0.8632889317455031;
	public static final double SPACE_E	=	1.7929411501239547;

}

