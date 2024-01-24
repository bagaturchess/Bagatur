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

	public static final double MATERIAL_PAWN_O	=	0.32956359948134184;

	public static final double MATERIAL_KNIGHT_O	=	0.40940866380526686;

	public static final double MATERIAL_BISHOP_O	=	0.4557628591070892;

	public static final double MATERIAL_ROOK_O	=	0.4794901025109175;

	public static final double MATERIAL_QUEEN_O	=	0.24754748446235367;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	0.7747014603252362;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	0.2177941555197104;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	0.6328325991790946;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	1.7560069488117633;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	0.8228102181315562;

	public static final double PIECE_SQUARE_TABLE_O	=	0.5156044928096407;

	public static final double PAWN_DOUBLE_O	=	0.0;

	public static final double PAWN_CONNECTED_O	=	0.25319910965436704;

	public static final double PAWN_NEIGHBOUR_O	=	0.1545923258455302;

	public static final double PAWN_ISOLATED_O	=	0.9942525072086937;

	public static final double PAWN_BACKWARD_O	=	0.08357495795245751;

	public static final double PAWN_INVERSE_O	=	0.023410201179360834;

	public static final double PAWN_PASSED_O	=	0.3058389945001702;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.15370345995157308;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	1.322715161871799;

	public static final double PAWN_SHIELD_O	=	0.1265260749803508;

	public static final double MOBILITY_KNIGHT_O	=	0.25839648684881883;

	public static final double MOBILITY_BISHOP_O	=	0.33037569257669563;

	public static final double MOBILITY_ROOK_O	=	0.9960722712152005;

	public static final double MOBILITY_QUEEN_O	=	0.2710607062452485;

	public static final double MOBILITY_KING_O	=	0.024963509434386705;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	0.47387270809599696;

	public static final double THREAT_UNUSED_OUTPOST_O	=	0.05794387631505079;

	public static final double THREAT_PAWN_PUSH_O	=	0.32141149507490585;

	public static final double THREAT_PAWN_ATTACKS_O	=	1.2490534181602695;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	0.23686225123561447;

	public static final double THREAT_MAJOR_ATTACKED_O	=	1.6159737134503747;

	public static final double THREAT_PAWN_ATTACKED_O	=	1.1630409674572388;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1.9310731261766227;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	2.0;

	public static final double THREAT_ROOK_ATTACKED_O	=	1.0052553452648303;

	public static final double OTHERS_SIDE_TO_MOVE_O	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	0.0905214533914453;

	public static final double OTHERS_ROOK_BATTERY_O	=	0.2355622423162527;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	0.04052823994779289;

	public static final double OTHERS_ROOK_TRAPPED_O	=	0.4310767111096383;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	0.05556813473258654;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	0.26963521541841;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	1.4094789083780124;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	0.678080916347534;

	public static final double OTHERS_BISHOP_PRISON_O	=	0.01;

	public static final double OTHERS_BISHOP_PAWNS_O	=	0.4705332223101242;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	0.43577115893517865;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	0.04484967815729846;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	0.3729345407985795;

	public static final double OTHERS_CASTLING_O	=	0.6697896717443647;

	public static final double OTHERS_PINNED_O	=	0.24973611087946;

	public static final double OTHERS_DISCOVERED_O	=	1.0196797713593835;

	public static final double KING_SAFETY_O	=	1.3387254502733823;

	public static final double SPACE_O	=	1.1692516049239279;

	public static final double MATERIAL_PAWN_E	=	0.6079024661415343;

	public static final double MATERIAL_KNIGHT_E	=	0.4711223841713479;

	public static final double MATERIAL_BISHOP_E	=	0.4697067907696099;

	public static final double MATERIAL_ROOK_E	=	0.5050987884521589;

	public static final double MATERIAL_QUEEN_E	=	0.3369924813404375;

	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	0.2695197397284444;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	0.09431458962946572;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	0.7607888202142716;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	0.12579574572824395;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	0.0;

	public static final double PIECE_SQUARE_TABLE_E	=	0.11193926759441039;

	public static final double PAWN_DOUBLE_E	=	1.4109143130539752;

	public static final double PAWN_CONNECTED_E	=	0.5179169956879408;

	public static final double PAWN_NEIGHBOUR_E	=	0.24772255097532267;

	public static final double PAWN_ISOLATED_E	=	0.5436498878018772;

	public static final double PAWN_BACKWARD_E	=	0.388080868876677;

	public static final double PAWN_INVERSE_E	=	0.9433524248384251;

	public static final double PAWN_PASSED_E	=	0.48255806642879245;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.30707454176760135;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.4065524892820454;

	public static final double PAWN_SHIELD_E	=	0.017237072120222446;

	public static final double MOBILITY_KNIGHT_E	=	0.3965906149367431;

	public static final double MOBILITY_BISHOP_E	=	0.23272762531659766;

	public static final double MOBILITY_ROOK_E	=	0.06266950160068785;

	public static final double MOBILITY_QUEEN_E	=	0.016022779741578425;

	public static final double MOBILITY_KING_E	=	0.3925801852986067;

	public static final double THREAT_DOUBLE_ATTACKED_E	=	0.9372989375151427;

	public static final double THREAT_UNUSED_OUTPOST_E	=	0.5926831481089209;

	public static final double THREAT_PAWN_PUSH_E	=	0.5634910091377038;

	public static final double THREAT_PAWN_ATTACKS_E	=	1.5475082304829726;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	0.01966761074310829;

	public static final double THREAT_MAJOR_ATTACKED_E	=	1.8596920110500625;

	public static final double THREAT_PAWN_ATTACKED_E	=	0.08214225180541342;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	1.547158714401025;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	2.0;

	public static final double THREAT_ROOK_ATTACKED_E	=	2.0;

	public static final double OTHERS_SIDE_TO_MOVE_E	=	0.01;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1.0446032763862911;

	public static final double OTHERS_ROOK_BATTERY_E	=	0.4995953409318094;

	public static final double OTHERS_ROOK_7TH_RANK_E	=	1.1267373366424054;

	public static final double OTHERS_ROOK_TRAPPED_E	=	0.8331193947383099;

	public static final double OTHERS_ROOK_FILE_OPEN_E	=	0.13845494655640542;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	0.06922914793342747;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	1.390144127425469;

	public static final double OTHERS_BISHOP_OUTPOST_E	=	0.963299635454757;

	public static final double OTHERS_BISHOP_PRISON_E	=	0.011813625642268543;

	public static final double OTHERS_BISHOP_PAWNS_E	=	0.07532607323113877;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	0.07658420079294816;

	public static final double OTHERS_PAWN_BLOCKAGE_E	=	0.2596238711447726;

	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1.1411826325038152;

	public static final double OTHERS_CASTLING_E	=	1.3039057052890501;

	public static final double OTHERS_PINNED_E	=	1.068710913578759;

	public static final double OTHERS_DISCOVERED_E	=	0.28772757962324863;

	public static final double KING_SAFETY_E	=	0.48644887984886104;

	public static final double SPACE_E	=	1.0563310096401584;
}

