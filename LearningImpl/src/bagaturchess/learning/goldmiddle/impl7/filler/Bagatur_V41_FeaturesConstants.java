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
package bagaturchess.learning.goldmiddle.impl7.filler;


public interface Bagatur_V41_FeaturesConstants {
	
	
	//Material
	public static final int FEATURE_ID_MATERIAL_PAWN       					= 1;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     					= 2;
	public static final int FEATURE_ID_MATERIAL_BISHOP     					= 3;
	public static final int FEATURE_ID_MATERIAL_ROOK      					= 4;
	public static final int FEATURE_ID_MATERIAL_QUEEN      					= 5;
		
	
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
	
	
	//King safety and space
	public static final int FEATURE_ID_KING_SAFETY 							= 54;
	public static final int FEATURE_ID_SPACE 								= 55;
}
