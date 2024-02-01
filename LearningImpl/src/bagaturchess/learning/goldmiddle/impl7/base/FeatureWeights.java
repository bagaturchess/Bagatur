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

	public static final double MATERIAL_PAWN_O	=	1.6496175596464488;

	public static final double MATERIAL_KNIGHT_O	=	1.335144313045473;

	public static final double MATERIAL_BISHOP_O	=	1.4725172575480874;

	public static final double MATERIAL_ROOK_O	=	1.0300443374635715;

	public static final double MATERIAL_QUEEN_O	=	1.3718579985280828;

	public static final double PAWN_DOUBLE_O	=	0.0;

	public static final double PAWN_CONNECTED_O	=	0.19257573872736775;

	public static final double PAWN_NEIGHBOUR_O	=	0.5099802470013906;

	public static final double PAWN_ISOLATED_O	=	1.614047760494525;

	public static final double PAWN_BACKWARD_O	=	0.09003580020556708;

	public static final double PAWN_INVERSE_O	=	0.015509041780480931;

	public static final double PAWN_PASSED_O	=	0.6162560176919623;

	public static final double PAWN_PASSED_CANDIDATE_O	=	0.35973254626212686;

	public static final double PAWN_PASSED_UNSTOPPABLE_O	=	0.01;

	public static final double PAWN_SHIELD_O	=	1.034206336902427;

	public static final double MOBILITY_KNIGHT_O	=	3.427638546636466;

	public static final double MOBILITY_BISHOP_O	=	1.7813114589875225;

	public static final double MOBILITY_ROOK_O	=	1.7431941864276304;

	public static final double MOBILITY_QUEEN_O	=	1.7684286706937578;

	public static final double MOBILITY_KING_O	=	0.029870265719017578;

	public static final double MATERIAL_PAWN_E	=	1.5252282102982124;

	public static final double MATERIAL_KNIGHT_E	=	1.2656974401618257;

	public static final double MATERIAL_BISHOP_E	=	1.4217121663611498;

	public static final double MATERIAL_ROOK_E	=	1.3376550414801764;

	public static final double MATERIAL_QUEEN_E	=	1.3033356378694123;

	public static final double PAWN_DOUBLE_E	=	0.7655929961368872;

	public static final double PAWN_CONNECTED_E	=	0.948547431284103;

	public static final double PAWN_NEIGHBOUR_E	=	0.6441052091587909;

	public static final double PAWN_ISOLATED_E	=	1.8715412374245244;

	public static final double PAWN_BACKWARD_E	=	0.0;

	public static final double PAWN_INVERSE_E	=	0.9764238450594017;

	public static final double PAWN_PASSED_E	=	1.2151204961953506;

	public static final double PAWN_PASSED_CANDIDATE_E	=	0.17757457752768005;

	public static final double PAWN_PASSED_UNSTOPPABLE_E	=	0.4678136670866437;

	public static final double PAWN_SHIELD_E	=	0.03525415402758819;

	public static final double MOBILITY_KNIGHT_E	=	1.2357652204366694;

	public static final double MOBILITY_BISHOP_E	=	0.45168751560844594;

	public static final double MOBILITY_ROOK_E	=	0.3200027172214343;

	public static final double MOBILITY_QUEEN_E	=	0.41321662924938235;

	public static final double MOBILITY_KING_E	=	0.0;
}

