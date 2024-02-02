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

	public static final double MATERIAL_PAWN_O	=	0.9693414614015275;

	public static final double MATERIAL_KNIGHT_O	=	0.9898485721715271;

	public static final double MATERIAL_BISHOP_O	=	1.0351464690056034;

	public static final double MATERIAL_ROOK_O	=	0.9385446308088787;

	public static final double MATERIAL_QUEEN_O	=	0.8777385727474616;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.9898299589550033;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.9415811072772248;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	1.0463329760063758;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	1.0786387070785555;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1.072519592922118;

	public static final double PIECE_SQUARE_TABLE_O	=	1.131331777589354;

	public static final double PAWN_DOUBLE_O	=	1.0202206926494077;

	public static final double PAWN_CONNECTED_O	=	1.0993069953700094;

	public static final double PAWN_NEIGHBOUR_O	=	1.1869794564136462;

	public static final double PAWN_ISOLATED_O	=	1.0153205492199084;

	public static final double PAWN_BACKWARD_O	=	1.1335774262333045;

	public static final double PAWN_INVERSE_O	=	1.1298811824165362;

	public static final double PAWN_PASSED_O	=	0.8023129884769261;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1.2168271827102157;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.0;

	public static final double PAWN_SHIELD_O	=	0.8606469137328677;

	public static final double MOBILITY_KNIGHT_O	=	1.261205393489312;

	public static final double MOBILITY_BISHOP_O	=	1.212981969939158;

	public static final double MOBILITY_ROOK_O	=	1.0883490699793799;

	public static final double MOBILITY_QUEEN_O	=	1.0672436379668768;

	public static final double MOBILITY_KING_O	=	0.9129117005488114;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.958489283228848;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.8873365054670558;

	public static final double THREAT_PAWN_PUSH_O	=	0.9042159190340743;

	public static final double THREAT_PAWN_ATTACKS_O	=	0.7513264545414443;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.5583707403926461;

	public static final double THREAT_MAJOR_ATTACKED_O	=	0.9957398434007017;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.7937113480086536;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.6953976199725592;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.9089208831059589;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.8673341277372163;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	1.0;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	1.0804311898607368;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.9724011129854114;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1.2715073380150885;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.7367381694019738;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.9936298798732139;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.0943858903925043;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.9280046962360783;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.8882551678245997;

	public static final double OTHERS_BISHOP_PRISON_O	=	1.0;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.9702834403629931;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	1.1617301841095284;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	1.0711062159510165;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1.02553427989322;

	public static final double OTHERS_CASTLING_O	=	1.2047199960879393;

	public static final double OTHERS_PINNED_O	=	0.8125808558876315;

	public static final double OTHERS_DISCOVERED_O	=	0.8278782534916379;

	public static final double KING_SAFETY_O	=	1.3475653552907418;

	public static final double SPACE_O	=	1.3030956410082746;

	public static final double MATERIAL_PAWN_E	=	1.3036604720453806;

	public static final double MATERIAL_KNIGHT_E	=	1.1297556380428446;

	public static final double MATERIAL_BISHOP_E	=	1.1292951186903624;

	public static final double MATERIAL_ROOK_E	=	1.2050399872368305;

	public static final double MATERIAL_QUEEN_E	=	0.917064369113025;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	1.0768365967850324;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.5744254061245234;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	1.2028367789267327;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.3801234572292064;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.7373464574644939;

	public static final double PIECE_SQUARE_TABLE_E	=	1.0522005101129275;

	public static final double PAWN_DOUBLE_E	=	0.9330336770565849;

	public static final double PAWN_CONNECTED_E	=	1.2651214165472564;

	public static final double PAWN_NEIGHBOUR_E	=	1.0941810160470649;

	public static final double PAWN_ISOLATED_E	=	0.8963011399147296;

	public static final double PAWN_BACKWARD_E	=	0.5477298279669016;

	public static final double PAWN_INVERSE_E	=	0.8136400353142779;

	public static final double PAWN_PASSED_E	=	1.2325010314138867;

	public static final double PAWN_PASSED_CANDIDATE_E	=	1.3360212732490033;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.808791141423188;

	public static final double PAWN_SHIELD_E	=	1.0260732446848817;

	public static final double MOBILITY_KNIGHT_E	=	1.0599648885881146;

	public static final double MOBILITY_BISHOP_E	=	1.0650616567457478;

	public static final double MOBILITY_ROOK_E	=	1.1400053659293028;

	public static final double MOBILITY_QUEEN_E	=	0.8607988692123645;

	public static final double MOBILITY_KING_E	=	0.9565287945667645;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.1293121596796376;

	public static final double THREAT_UNUSED_OUTPOST_E	=	1.4447659580023562;

	public static final double THREAT_PAWN_PUSH_E	=	1.1166312710706585;

	public static final double THREAT_PAWN_ATTACKS_E	=	0.805028524789006;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.5074779871394787;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.9367844739648776;

	public static final double THREAT_PAWN_ATTACKED_E	=	1.2994741107583958;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	10.568673735800086;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	2.5217752080543;

	public static final double THREAT_ROOK_ATTACKED_E	=	1.1879188081670133;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	1.0;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	0.9751855803253695;

	public static final double OTHERS_ROOK_BATTERY_E	=	1.1375040515216812;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	1.0809008607936632;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.39986382521987796;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.9610376453405824;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	1.08356850441188;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	1.0143434435016954;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	1.450769478776928;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.08779149519890266;

	public static final double OTHERS_BISHOP_PAWNS_E	=	1.0457879466376645;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	1.058303070126858;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	1.1730581837078888;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.2222402721928325;

	public static final double OTHERS_CASTLING_E	=	3.5152204165453;

	public static final double OTHERS_PINNED_E	=	0.8598893073471204;

	public static final double OTHERS_DISCOVERED_E	=	0.6342809002336206;

	public static final double KING_SAFETY_E	=	0.9852921166642001;

	public static final double SPACE_E	=	0;
}

