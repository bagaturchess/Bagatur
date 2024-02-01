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
package bagaturchess.learning.goldmiddle.impl7.base;


public interface FeatureWeights {

	public static final double MATERIAL_PAWN_O	=	1.3764745090881427;

	public static final double MATERIAL_KNIGHT_O	=	1.2546670751921574;

	public static final double MATERIAL_BISHOP_O	=	1.446274539873202;

	public static final double MATERIAL_ROOK_O	=	0.9907443110926952;

	public static final double MATERIAL_QUEEN_O	=	1.3628726147161705;

	public static final double PAWN_DOUBLE_O	=	0.0;

	public static final double PAWN_CONNECTED_O	=	0.11452321272994208;

	public static final double PAWN_NEIGHBOUR_O	=	0.17557556985820677;

	public static final double PAWN_ISOLATED_O	=	1.545966973762357;

	public static final double PAWN_BACKWARD_O	=	0.03283787395884315;

	public static final double PAWN_INVERSE_O	=	0.015542175285894867;

	public static final double PAWN_PASSED_O	=	0.709190137164344;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.1684223029826226;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.01;

	public static final double PAWN_SHIELD_O	=	1.15111086907855;

	public static final double MOBILITY_KNIGHT_O	=	1.6780236864427105;

	public static final double MOBILITY_BISHOP_O	=	0.9550624567877216;

	public static final double MOBILITY_ROOK_O	=	1.5103954130241453;

	public static final double MOBILITY_QUEEN_O	=	1.561084499439843;

	public static final double MOBILITY_KING_O	=	0.032605343123736814;

	public static final double KING_SAFETY_O	=	1.4656998769704477;

	public static final double SPACE_O	=	2.935065634737009;
	

	public static final double MATERIAL_PAWN_E	=	1.6114526555583997;

	public static final double MATERIAL_KNIGHT_E	=	1.2737392017716536;

	public static final double MATERIAL_BISHOP_E	=	1.4206079653101378;

	public static final double MATERIAL_ROOK_E	=	1.3355266129332624;

	public static final double MATERIAL_QUEEN_E	=	1.303860094902247;

	public static final double PAWN_DOUBLE_E	=	1.0162572348789383;

	public static final double PAWN_CONNECTED_E	=	0.7029148834987067;

	public static final double PAWN_NEIGHBOUR_E	=	0.5125656629490046;

	public static final double PAWN_ISOLATED_E	=	1.5775654767629008;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	0.0;

	public static final double PAWN_PASSED_E	=	1.1839677888067672;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.0833793730475235;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.5323851832612081;

	public static final double PAWN_SHIELD_E	=	0.028041517457590863;

	public static final double MOBILITY_KNIGHT_E	=	0.5122245970165473;

	public static final double MOBILITY_BISHOP_E	=	0.4221107622661704;

	public static final double MOBILITY_ROOK_E	=	0.3531598732923528;

	public static final double MOBILITY_QUEEN_E	=	0.44273525071451747;

	public static final double MOBILITY_KING_E	=	0.0;

	public static final double KING_SAFETY_E	=	0.5861887120087782;

	public static final double SPACE_E	=	0;
}

