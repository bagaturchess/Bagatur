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
package bagaturchess.learning.goldmiddle.impl4.filler;


public interface Bagatur_V20_FeaturesConstants {
	
	
	//Material
	public static final int FEATURE_ID_MATERIAL_PAWN       					= 1;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     					= 2;
	public static final int FEATURE_ID_MATERIAL_BISHOP     					= 3;
	public static final int FEATURE_ID_MATERIAL_ROOK      					= 4;
	public static final int FEATURE_ID_MATERIAL_QUEEN      					= 5;
	
	
	//Imbalance
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_KNIGHT_PAWNS 		= 6;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS		= 7;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE 	= 8;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS 	= 9;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR 		= 10;
	
	
	//PST
	public static final int FEATURE_ID_PIECE_SQUARE_TABLE 					= 11;
	
	
	//Pawns
	public static final int FEATURE_ID_PAWN_DOUBLE 							= 12;
	public static final int FEATURE_ID_PAWN_CONNECTED 						= 13;
	public static final int FEATURE_ID_PAWN_NEIGHBOUR 						= 14;
	public static final int FEATURE_ID_PAWN_ISOLATED 						= 15;
	public static final int FEATURE_ID_PAWN_BACKWARD 						= 16;
	public static final int FEATURE_ID_PAWN_INVERSE 						= 17;
	public static final int FEATURE_ID_PAWN_PASSED							= 18;
	public static final int FEATURE_ID_PAWN_PASSED_CANDIDATE 				= 19;
	public static final int FEATURE_ID_PAWN_PASSED_UNSTOPPABLE	 			= 20;
	public static final int FEATURE_ID_PAWN_SHIELD							= 21;
	
	
	//Mobility
	public static final int FEATURE_ID_MOBILITY_KNIGHT						= 22;
	public static final int FEATURE_ID_MOBILITY_BISHOP						= 23;
	public static final int FEATURE_ID_MOBILITY_ROOK						= 24;
	public static final int FEATURE_ID_MOBILITY_QUEEN						= 25;
	public static final int FEATURE_ID_MOBILITY_KING						= 26;
	
	
	//Threats
	public static final int FEATURE_ID_THREAT_DOUBLE_ATTACKED 				= 27;
	public static final int FEATURE_ID_THREAT_UNUSED_OUTPOST				= 28;
	public static final int FEATURE_ID_THREAT_PAWN_PUSH						= 29;
	public static final int FEATURE_ID_THREAT_PAWN_ATTACKS 					= 30;
	public static final int FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS 		= 31;
	public static final int FEATURE_ID_THREAT_MAJOR_ATTACKED 				= 32;
	public static final int FEATURE_ID_THREAT_PAWN_ATTACKED 				= 33;
	public static final int FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK			= 34;
	public static final int FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR 			= 35;
	public static final int FEATURE_ID_THREAT_ROOK_ATTACKED 				= 36;
	
	
	//Others
	public static final int FEATURE_ID_OTHERS_SIDE_TO_MOVE	 				= 37;
	public static final int FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS			= 38;
	public static final int FEATURE_ID_OTHERS_ROOK_BATTERY 					= 39;
	public static final int FEATURE_ID_OTHERS_ROOK_7TH_RANK 				= 40;
	public static final int FEATURE_ID_OTHERS_ROOK_TRAPPED 					= 41;
	public static final int FEATURE_ID_OTHERS_ROOK_FILE_OPEN 				= 42;
	public static final int FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED	= 43;
	public static final int FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN 			= 44;
	public static final int FEATURE_ID_OTHERS_BISHOP_OUTPOST 				= 45;
	public static final int FEATURE_ID_OTHERS_BISHOP_PRISON					= 46;
	public static final int FEATURE_ID_OTHERS_BISHOP_PAWNS 					= 47;
	public static final int FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK 			= 48;
	public static final int FEATURE_ID_OTHERS_PAWN_BLOCKAGE 				= 49;
	public static final int FEATURE_ID_OTHERS_KNIGHT_OUTPOST 				= 50;
	public static final int FEATURE_ID_OTHERS_CASTLING 						= 51;
	public static final int FEATURE_ID_OTHERS_PINNED 						= 52;
	public static final int FEATURE_ID_OTHERS_DISCOVERED					= 53;
	
	
	//King safety and space
	public static final int FEATURE_ID_KING_SAFETY 							= 54;
	public static final int FEATURE_ID_SPACE 								= 55;
}
