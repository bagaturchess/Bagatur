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

	public static final double MATERIAL_PAWN_O	=	0.7089989169569277;

	public static final double MATERIAL_KNIGHT_O	=	0.8829486685109591;

	public static final double MATERIAL_BISHOP_O	=	0.8821979173315927;

	public static final double MATERIAL_ROOK_O	=	0.9362451000941908;

	public static final double MATERIAL_QUEEN_O	=	0.7258212199978359;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	1.861141125799113;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	1.8507468167409054;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	2.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1.692692029094454;

	public static final double PIECE_SQUARE_TABLE_O	=	1.071420493590172;

	public static final double PAWN_DOUBLE_O	=	1.1041997848520781;

	public static final double PAWN_CONNECTED_O	=	1.2286146024059958;

	public static final double PAWN_NEIGHBOUR_O	=	1.4970675726780236;

	public static final double PAWN_ISOLATED_O	=	0.8528510150416089;

	public static final double PAWN_BACKWARD_O	=	2.0;

	public static final double PAWN_INVERSE_O	=	2.0;

	public static final double PAWN_PASSED_O	=	0.8152633265821461;

	public static final double PAWN_PASSED_CANDIDATE_O	=	2.0;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.3139315211513891;

	public static final double PAWN_SHIELD_O	=	0.348320663784975;

	public static final double MOBILITY_KNIGHT_O	=	1.4429045779680507;

	public static final double MOBILITY_BISHOP_O	=	1.5268039223123544;

	public static final double MOBILITY_ROOK_O	=	1.3634161918077858;

	public static final double MOBILITY_QUEEN_O	=	0.04099298311221335;

	public static final double MOBILITY_KING_O	=	0.01870304940735445;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.6795575501731151;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.07101881544490407;

	public static final double THREAT_PAWN_PUSH_O	=	0.6984718122090661;

	public static final double THREAT_PAWN_ATTACKS_O	=	0.7350711793627702;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_O	=	2.0;

	public static final double THREAT_PAWN_ATTACKED_O	=	0.02924654374963478;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	0.3296158041246563;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	0.40381413124379345;

	public static final double THREAT_ROOK_ATTACKED_O	=	0.6361716440302961;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.8756658892641207;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.23528985289023932;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	2.0;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.5168547107274939;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.05787633542854349;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1.3564656127651775;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	0.27198694543234236;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.3569123293616632;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.01;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.09167377343964342;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	1.9479343455273124;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.9069201863799216;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.9279611386531349;

	public static final double OTHERS_CASTLING_O	=	0.8494844005469219;

	public static final double OTHERS_PINNED_O	=	0.6292491906021946;

	public static final double OTHERS_DISCOVERED_O	=	1.1438864112133365;

	public static final double KING_SAFETY_O	=	1.7289822486023678;

	public static final double SPACE_O	=	2.0;

	public static final double MATERIAL_PAWN_E	=	1.3582978991005679;

	public static final double MATERIAL_KNIGHT_E	=	1.110700016267235;

	public static final double MATERIAL_BISHOP_E	=	1.1985971859903422;

	public static final double MATERIAL_ROOK_E	=	1.294367318658782;

	public static final double MATERIAL_QUEEN_E	=	1.045256105986817;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	1.3530324336374702;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.9447810990114397;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.0;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	1.9269773834403687;

	public static final double PIECE_SQUARE_TABLE_E	=	2.0;

	public static final double PAWN_DOUBLE_E	=	2.0;

	public static final double PAWN_CONNECTED_E	=	1.3529262298108697;

	public static final double PAWN_NEIGHBOUR_E	=	0.9731883461409531;

	public static final double PAWN_ISOLATED_E	=	1.2998654480935659;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	2.0;

	public static final double PAWN_PASSED_E	=	1.1934897948786547;

	public static final double PAWN_PASSED_CANDIDATE_E	=	2.0;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.42289981088360046;

	public static final double PAWN_SHIELD_E	=	0.017310437794314248;

	public static final double MOBILITY_KNIGHT_E	=	0.29199989366304474;

	public static final double MOBILITY_BISHOP_E	=	0.4667378724256522;

	public static final double MOBILITY_ROOK_E	=	0.0;

	public static final double MOBILITY_QUEEN_E	=	0.014918925860850855;

	public static final double MOBILITY_KING_E	=	0.029000012922970424;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	1.426763272156249;

	public static final double THREAT_UNUSED_OUTPOST_E	=	2.0;

	public static final double THREAT_PAWN_PUSH_E	=	0.8762909310598311;

	public static final double THREAT_PAWN_ATTACKS_E	=	0.4034230192101468;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.0;

	public static final double THREAT_MAJOR_ATTACKED_E	=	0.1915191068648365;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	0.0;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	0.0;

	public static final double THREAT_ROOK_ATTACKED_E	=	2.0;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	2.0;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.09562456717354431;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	0.05337634582133323;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.029982531879521955;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.0;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.7764406681315382;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	1.1206649606887737;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	2.0;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.0;

	public static final double OTHERS_BISHOP_PAWNS_E	=	1.1655789524149636;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	2.0;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	1.3273295621925632;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.3986175858350625;

	public static final double OTHERS_CASTLING_E	=	2.0;

	public static final double OTHERS_PINNED_E	=	0.3212004196072214;

	public static final double OTHERS_DISCOVERED_E	=	0.06963120376514034;

	public static final double KING_SAFETY_E	=	0.700399492440114;

	public static final double SPACE_E	=	0.8331319733471209;
}

