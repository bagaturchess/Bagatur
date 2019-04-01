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
package bagaturchess.learning.goldmiddle.impl1.filler;


public interface Bagatur_V17_FeaturesConstants {
	
	
	//Material
	public static final int FEATURE_ID_MATERIAL_PAWN       					= 1010;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     					= 1020;
	public static final int FEATURE_ID_MATERIAL_BISHOP     					= 1030;
	public static final int FEATURE_ID_MATERIAL_ROOK      					= 1040;
	public static final int FEATURE_ID_MATERIAL_QUEEN      					= 1050;
	public static final int FEATURE_ID_MATERIAL_DOUBLE_BISHOP 				= 1060;
	
	
	//Imbalance
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS 		= 1070;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS		= 1080;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE 	= 1090;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS 	= 1100;
	public static final int FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR 		= 1110;
	
	
	//PST
	public static final int FEATURE_ID_PIECE_SQUARE_TABLE 					= 1120;
	
	
	//Pawns
	public static final int FEATURE_ID_PAWN_DOUBLE 							= 1130;
	public static final int FEATURE_ID_PAWN_CONNECTED 						= 1140;
	public static final int FEATURE_ID_PAWN_NEIGHBOUR 						= 1150;
	public static final int FEATURE_ID_PAWN_ISOLATED 						= 1160;
	public static final int FEATURE_ID_PAWN_BACKWARD 						= 1170;
	public static final int FEATURE_ID_PAWN_INVERSE 						= 1180;
	public static final int FEATURE_ID_PAWN_PASSED							= 1190;
	public static final int FEATURE_ID_PAWN_PASSED_CANDIDATE 				= 1200;
	public static final int FEATURE_ID_PAWN_PASSED_UNSTOPPABLE	 			= 1210;
	public static final int FEATURE_ID_PAWN_SHIELD							= 1220;
	
	
	//Mobility
	public static final int FEATURE_ID_MOBILITY_KNIGHT						= 1230;
	public static final int FEATURE_ID_MOBILITY_BISHOP						= 1240;
	public static final int FEATURE_ID_MOBILITY_ROOK						= 1250;
	public static final int FEATURE_ID_MOBILITY_QUEEN						= 1260;
	public static final int FEATURE_ID_MOBILITY_KING						= 1270;
	
	
	//Threats
	public static final double THREAT_DOUBLE_ATTACKED_O 				= 1;
	public static final double THREAT_UNUSED_OUTPOST_O 					= 1;
	public static final double THREAT_PAWN_PUSH_O						= 1;
	public static final double THREAT_PAWN_ATTACKS_O 					= 1;
	public static final double THREAT_MULTIPLE_PAWN_ATTACKS_O 			= 1;
	public static final double THREAT_MAJOR_ATTACKED_O 					= 1;
	public static final double THREAT_PAWN_ATTACKED_O 					= 1;
	public static final double THREAT_QUEEN_ATTACKED_ROOK_O				= 1;
	public static final double THREAT_QUEEN_ATTACKED_MINOR_O 			= 1;
	public static final double THREAT_ROOK_ATTACKED_O 					= 1;
	public static final double THREAT_NIGHT_FORK_O 						= 1;
	public static final double THREAT_NIGHT_FORK_KING_O 				= 1;
	
	
}
