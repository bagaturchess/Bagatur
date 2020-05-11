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

	public static final double MATERIAL_PAWN_O	=	1;
	public static final double MATERIAL_PAWN_E	=	1;

	public static final double MATERIAL_KNIGHT_O	=	1;
	public static final double MATERIAL_KNIGHT_E	=	1;

	public static final double MATERIAL_BISHOP_O	=	1;
	public static final double MATERIAL_BISHOP_E	=	1;

	public static final double MATERIAL_ROOK_O	=	1;
	public static final double MATERIAL_ROOK_E	=	1;

	public static final double MATERIAL_QUEEN_O	=	1;
	public static final double MATERIAL_QUEEN_E	=	1;
	
	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_O	=	1;
	public static final double MATERIAL_IMBALANCE_KNIGHT_PAWNS_E	=	1;

	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_O	=	1;
	public static final double MATERIAL_IMBALANCE_ROOK_PAWNS_E	=	1;

	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_O	=	1;
	public static final double MATERIAL_IMBALANCE_BISHOP_DOUBLE_E	=	1;

	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O	=	1;
	public static final double MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E	=	1;

	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_O	=	1;
	public static final double MATERIAL_IMBALANCE_ROOK_PAIR_E	=	1;

	public static final double PIECE_SQUARE_TABLE_O	=	1;
	public static final double PIECE_SQUARE_TABLE_E	=	1;

	public static final double PAWN_DOUBLE_O	=	1;
	public static final double PAWN_DOUBLE_E	=	1;

	public static final double PAWN_CONNECTED_O	=	1;
	public static final double PAWN_CONNECTED_E	=	1;

	public static final double PAWN_NEIGHBOUR_O	=	1;
	public static final double PAWN_NEIGHBOUR_E	=	1;

	public static final double PAWN_ISOLATED_O	=	1;
	public static final double PAWN_ISOLATED_E	=	1;

	public static final double PAWN_BACKWARD_O	=	1;
	public static final double PAWN_BACKWARD_E	=	1;

	public static final double PAWN_INVERSE_O	=	1;
	public static final double PAWN_INVERSE_E	=	1;

	public static final double PAWN_PASSED_O	=	0;
	public static final double PAWN_PASSED_E	=	1;

	public static final double PAWN_PASSED_CANDIDATE_O	=	1;
	public static final double PAWN_PASSED_CANDIDATE_E	=	1;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0;
	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	1;

	public static final double PAWN_SHIELD_O	=	1;
	public static final double PAWN_SHIELD_E	=	1;

	public static final double MOBILITY_KNIGHT_O	=	1;
	public static final double MOBILITY_KNIGHT_E	=	1;

	public static final double MOBILITY_BISHOP_O	=	1;
	public static final double MOBILITY_BISHOP_E	=	1;

	public static final double MOBILITY_ROOK_O	=	1;
	public static final double MOBILITY_ROOK_E	=	1;

	public static final double MOBILITY_QUEEN_O	=	1;
	public static final double MOBILITY_QUEEN_E	=	1;

	public static final double MOBILITY_KING_O	=	1;
	public static final double MOBILITY_KING_E	=	1;

	public static final double THREAT_DOUBLE_ATTACKED_O	=	1;
	public static final double THREAT_DOUBLE_ATTACKED_E	=	1;

	public static final double THREAT_UNUSED_OUTPOST_O	=	1;
	public static final double THREAT_UNUSED_OUTPOST_E	=	1;

	public static final double THREAT_PAWN_PUSH_O	=	1;
	public static final double THREAT_PAWN_PUSH_E	=	1;

	public static final double THREAT_PAWN_ATTACKS_O	=	1;
	public static final double THREAT_PAWN_ATTACKS_E	=	1;

	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O	=	1;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E	=	1;

	public static final double THREAT_MAJOR_ATTACKED_O	=	1;
	public static final double THREAT_MAJOR_ATTACKED_E	=	1;

	public static final double THREAT_PAWN_ATTACKED_O	=	1;
	public static final double THREAT_PAWN_ATTACKED_E	=	1;

	public static final double THREAT_QUEEN_ATTACKED_ROOK_O	=	1;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E	=	1;

	public static final double THREAT_QUEEN_ATTACKED_MINOR_O	=	1;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E	=	1;

	public static final double THREAT_ROOK_ATTACKED_O	=	1;
	public static final double THREAT_ROOK_ATTACKED_E	=	1;
	
	public static final double OTHERS_SIDE_TO_MOVE_O	=	1;
	public static final double OTHERS_SIDE_TO_MOVE_E	=	1;

	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_O	=	1;
	public static final double OTHERS_ONLY_MAJOR_DEFENDERS_E	=	1;

	public static final double OTHERS_ROOK_BATTERY_O	=	1;
	public static final double OTHERS_ROOK_BATTERY_E	=	1;

	public static final double OTHERS_ROOK_7TH_RANK_O	=	1;
	public static final double OTHERS_ROOK_7TH_RANK_E	=	1;

	public static final double OTHERS_ROOK_TRAPPED_O	=	1;
	public static final double OTHERS_ROOK_TRAPPED_E	=	1;

	public static final double OTHERS_ROOK_FILE_OPEN_O	=	1;
	public static final double OTHERS_ROOK_FILE_OPEN_E	=	1;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O	=	1;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E	=	1;

	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_O	=	1;
	public static final double OTHERS_ROOK_FILE_SEMI_OPEN_E	=	1;

	public static final double OTHERS_BISHOP_OUTPOST_O	=	1;
	public static final double OTHERS_BISHOP_OUTPOST_E	=	1;

	public static final double OTHERS_BISHOP_PRISON_O	=	1;
	public static final double OTHERS_BISHOP_PRISON_E	=	1;

	public static final double OTHERS_BISHOP_PAWNS_O	=	1;
	public static final double OTHERS_BISHOP_PAWNS_E	=	1;

	public static final double OTHERS_BISHOP_CENTER_ATTACK_O	=	1;
	public static final double OTHERS_BISHOP_CENTER_ATTACK_E	=	1;

	public static final double OTHERS_PAWN_BLOCKAGE_O	=	1;
	public static final double OTHERS_PAWN_BLOCKAGE_E	=	1;

	public static final double OTHERS_KNIGHT_OUTPOST_O	=	1;
	public static final double OTHERS_KNIGHT_OUTPOST_E	=	1;

	public static final double OTHERS_CASTLING_O	=	1;
	public static final double OTHERS_CASTLING_E	=	1;
	
	public static final double OTHERS_PINNED_O	=	1;
	public static final double OTHERS_PINNED_E	=	1;
	
	public static final double OTHERS_DISCOVERED_O	=	1;
	public static final double OTHERS_DISCOVERED_E	=	1;	

	public static final double KING_SAFETY_O	=	1;
	public static final double KING_SAFETY_E	=	0;

	public static final double SPACE_O	=	1;
	public static final double SPACE_E	=	0;
}

