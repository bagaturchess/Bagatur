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
package bagaturchess.learning.goldmiddle.impl3.filler;


public interface Bagatur_V18_FeaturesConstants {
	
	
	//Material
	public static final int FEATURE_ID_MATERIAL_PAWN       					= 1010;
	public static final int FEATURE_ID_MATERIAL_KNIGHT     					= 1020;
	public static final int FEATURE_ID_MATERIAL_BISHOP     					= 1030;
	public static final int FEATURE_ID_MATERIAL_ROOK      					= 1040;
	public static final int FEATURE_ID_MATERIAL_QUEEN      					= 1050;
	public static final int FEATURE_ID_MATERIAL_DOUBLE_BISHOP 				= 1060;
	
	
	//Imbalance	
	public static final int FEATURE_ID_MATERIAL_IMBALANCE 					= 1070;
	
	//PST
	public static final int FEATURE_ID_PIECE_SQUARE_TABLE 					= 1080;
	
	//Pawns
	public static final int FEATURE_ID_PAWNS 								= 1090;
	
	public static final int FEATURE_ID_MOBILITY_OUTPOST_OTHERS				= 1100;
	
	public static final int FEATURE_ID_KING_SAFETY							= 1110;
	
	public static final int FEATURE_ID_THREATS								= 1120;
	
	public static final int FEATURE_ID_PASSED_PAWNS							= 1130;

	public static final int FEATURE_ID_SPACE								= 1140;
}
