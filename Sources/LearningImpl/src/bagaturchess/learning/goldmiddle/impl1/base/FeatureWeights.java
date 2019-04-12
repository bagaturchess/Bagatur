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

	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_O	=	0.8292165247751222;
	public static final double MATERIAL_IMBALANCE_NIGHT_PAWNS_E	=	2.337043236934307;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.034871483447875315;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.6322994840612148;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.6262170621781923;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	0.31519490896052466;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.0324008102227003;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.4788128382130924;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.0;

	public static final double PIECE_SQUARE_TABLE_O	=	1.0155308449610758;
	public static final double PIECE_SQUARE_TABLE_E	=	0.6034019716917971;

	public static final double PAWN_DOUBLE_O	=	2.1888590124456972;
	public static final double PAWN_DOUBLE_E	=	0.492923197730428;

	public static final double PAWN_CONNECTED_O	=	1.3552319880814496;
	public static final double PAWN_CONNECTED_E	=	0.8848690172374618;

	public static final double PAWN_NEIGHBOUR_O	=	2.2828251114947764;
	public static final double PAWN_NEIGHBOUR_E	=	0.5590331330316131;

	public static final double PAWN_ISOLATED_O	=	0.6959886713841541;
	public static final double PAWN_ISOLATED_E	=	0.8728417105940242;

	public static final double PAWN_BACKWARD_O	=	0.33740021104291656;
	public static final double PAWN_BACKWARD_E	=	1.5190449836905933;

	public static final double PAWN_INVERSE_O	=	2.4217584574523645;
	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_O	=	1;//0.28886735736064045;
	public static final double PAWN_PASSED_E	=	1.1679355471582253;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1;//0.4583220942997007;
	public static final double PAWN_PASSED_CANDIDATE_E	=	1;//0.10975113895144344;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1.4403921966227626;

	public static final double PAWN_SHIELD_O	=	0.6743978856635646;
	public static final double PAWN_SHIELD_E	=	0.4498618724103228;

	public static final double MOBILITY_KNIGHT_O	=	0.7880963637335189;
	public static final double MOBILITY_KNIGHT_E	=	1.385420109938542;

	public static final double MOBILITY_BISHOP_O	=	0.8176187830585843;
	public static final double MOBILITY_BISHOP_E	=	1.2740107981178432;

	public static final double MOBILITY_ROOK_O	=	1.2097970684547878;
	public static final double MOBILITY_ROOK_E	=	0.6016000697126167;

	public static final double MOBILITY_QUEEN_O	=	1.4006721570644032;
	public static final double MOBILITY_QUEEN_E	=	1.010710407672654;

	public static final double MOBILITY_KING_O	=	1.0177298369138743;
	public static final double MOBILITY_KING_E	=	0.6957304645883313;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.8912793947623033;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.2545945494006072;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.6912958716386087;
	public static final double THREAT_UNUSED_OUTPOST_E	=	0.0;

	public static final double THREAT_PAWN_PUSH_O	=	0.9417661837991799;
	public static final double THREAT_PAWN_PUSH_E	=	1.702408082268009;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.1585896205068797;
	public static final double THREAT_PAWN_ATTACKS_E	=	1.6283208416854833;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.4112505700184834;
	public static final double THREAT_MAJOR_ATTACKED_E	=	0.6192051882761219;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.2971194508155467;
	public static final double THREAT_PAWN_ATTACKED_E	=	2.8224226165440567;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.2539004028859926;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	2.620544418011923;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1.2575365628223643;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	4.179694457157629;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.7439327379502583;
	public static final double THREAT_ROOK_ATTACKED_E	=	2.0670492479886096;

	public static final double THREAT_NIGHT_FORK_O	=	0.07849474680762347;
	public static final double THREAT_NIGHT_FORK_E	=	0.3790284764717165;

	public static final double THREAT_NIGHT_FORK_KING_O	=	0.4437811093943208;
	public static final double THREAT_NIGHT_FORK_KING_E	=	0.0;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.0;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.013144463417760589;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.8161461682162127;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.6290557741523923;

	public static final double OTHERS_HANGING_O	=	0.9635300030879002;
	public static final double OTHERS_HANGING_E	=	1.06021018415078;

	public static final double OTHERS_HANGING_2_O	=	0.95433398525092;
	public static final double OTHERS_HANGING_2_E	=	1.0374507169103575;

	public static final double OTHERS_ROOK_BATTERY_O	=	1.4681525979793564;
	public static final double OTHERS_ROOK_BATTERY_E	=	0.0;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.675398573873106;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.0;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.3093162102781914;
	public static final double OTHERS_ROOK_TRAPPED_E	=	0.0;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1.2838725503912654;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.08079015614043715;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.1860357957036958;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.07209914262467344;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.0;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	0.8943972497902106;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	9.124026006061552;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.8519723333278243;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.6172500024303192;
	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.0;
	public static final double OTHERS_BISHOP_PAWNS_E	=	0.0;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.9441746110267327;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.4466573024644876;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.0;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.0;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.0835711579038405;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	0.8548702696688225;

	public static final double OTHERS_IN_CHECK_O	=	1.0;
	public static final double OTHERS_IN_CHECK_E	=	1.0;

	public static final double KING_SAFETY_O	=	0.5600372768266487;
	public static final double KING_SAFETY_E	=	0.0;

	public static final double SPACE_O	=	1.3075194615436971;
	public static final double SPACE_E	=	0.1580961463716476;
}

