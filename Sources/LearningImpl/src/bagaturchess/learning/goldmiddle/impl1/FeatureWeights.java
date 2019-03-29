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
package bagaturchess.learning.goldmiddle.impl1;


public interface FeatureWeights {
	
	
	//Pawns
	public static final double PASSED_UNSTOPPABLE_O				= 350;
	public static final double PASSED_UNSTOPPABLE_E				= 350;
	public static final double PASSED_PAWNS_O					= 1;
	public static final double PASSED_PAWNS_E					= 1;
	public static final double PAWN_SHIELD_O					= 1;
	public static final double PAWN_SHIELD_E					= 1;
	
	//Mobility
	public static final double MOBILITY_KNIGHT_O				= 1;
	public static final double MOBILITY_KNIGHT_E				= 1;
	public static final double MOBILITY_BISHOP_O				= 1;
	public static final double MOBILITY_BISHOP_E				= 1;
	public static final double MOBILITY_ROOK_O					= 1;
	public static final double MOBILITY_ROOK_E					= 1;
	public static final double MOBILITY_QUEEN_O					= 1;
	public static final double MOBILITY_QUEEN_E					= 1;
	public static final double MOBILITY_KING_O					= 1;
	public static final double MOBILITY_KING_E					= 1;

	//Threats
	public static final double THREAT_DOUBLE_ATTACKED_O 		= 1;
	public static final double THREAT_DOUBLE_ATTACKED_E 		= 1;
	public static final double THREAT_UNUSED_OUTPOST_O 			= 1;
	public static final double THREAT_UNUSED_OUTPOST_E 			= 1;
	public static final double THREAT_PAWN_PUSH_O				= 1;
	public static final double THREAT_PAWN_PUSH_E				= 1;
	public static final double THREAT_PAWN_ATTACKS_O 			= 1;
	public static final double THREAT_PAWN_ATTACKS_E 			= 1;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O 	= 1;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_E 	= 1;
	public static final double THREAT_MAJOR_ATTACKED_O 			= 1;
	public static final double THREAT_MAJOR_ATTACKED_E 			= 1;
	public static final double THREAT_PAWN_ATTACKED_O 			= 1;
	public static final double THREAT_PAWN_ATTACKED_E 			= 1;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_O		= 1;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_E		= 1;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_O 	= 1;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_E 	= 1;
	public static final double THREAT_ROOK_ATTACKED_O 			= 1;
	public static final double THREAT_ROOK_ATTACKED_E 			= 1;
	public static final double THREAT_NIGHT_FORK_O 				= 1;
	public static final double THREAT_NIGHT_FORK_E 				= 1;
	public static final double THREAT_NIGHT_FORK_KING_O 		= 1;
	public static final double THREAT_NIGHT_FORK_KING_E 		= 1;
	
	
}
