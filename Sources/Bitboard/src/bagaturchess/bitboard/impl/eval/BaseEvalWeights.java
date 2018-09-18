/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl.eval;


import bagaturchess.bitboard.impl.Figures;


public class BaseEvalWeights {
	
	
	private static final int FIGURE_COST_PAWN_OPENING = 100;
	//private static final int FIGURE_COST_PAWN_ENDGAME = 80;
	
	private static final int FIGURE_COST_KNIGHT_OPENING = 325;
	//private static final int FIGURE_COST_KNIGHT_ENDGAME = 325;
	
	private static final int FIGURE_COST_OFFICER_OPENING = 325;
	//private static final int FIGURE_COST_OFFICER_ENDGAME = 325;
	
	private static final int FIGURE_COST_CASTLE_OPENING = 500;
	//private static final int FIGURE_COST_CASTLE_ENDGAME = 500;
	
	private static final int FIGURE_COST_QUEEN_OPENING = 1000;
	//private static final int FIGURE_COST_QUEEN_ENDGAME = 1000;
	
	private static final int FIGURE_FACTOR_PAWN = 0;
	private static final int FIGURE_FACTOR_KNIGHT = 3;	
	private static final int FIGURE_FACTOR_OFFICER = 3;
	private static final int FIGURE_FACTOR_CASTLE = 5;
	private static final int FIGURE_FACTOR_QUEEN = 9;
	private static final int FIGURE_FACTOR_KING = 0;
	private static final int FIGURE_FACTOR_MAX = 2 * FIGURE_FACTOR_QUEEN + 4 * FIGURE_FACTOR_CASTLE
												+ 4 * FIGURE_FACTOR_OFFICER + 4 * FIGURE_FACTOR_KNIGHT;
	
	private static final int FIGURE_COST_PAWN_SEE = 100;
	private static final int FIGURE_COST_KNIGHT_SEE = 300;
	private static final int FIGURE_COST_OFFICER_SEE = 300;
	private static final int FIGURE_COST_CASTLE_SEE = 500;
	private static final int FIGURE_COST_QUEEN_SEE = 900;
	private static final int FIGURE_COST_KING_SEE = 3600;
		
	private static final int FIGURE_COST_KING = 9 * FIGURE_COST_QUEEN_OPENING + 2
			* FIGURE_COST_KNIGHT_OPENING + 2 * FIGURE_COST_OFFICER_OPENING + 2
			* FIGURE_COST_CASTLE_OPENING;
	
	
	public static int getFigureCost(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return FIGURE_COST_PAWN_OPENING;
			case Figures.TYPE_KNIGHT:
				return FIGURE_COST_KNIGHT_OPENING;
			case Figures.TYPE_OFFICER:
				return FIGURE_COST_OFFICER_OPENING;
			case Figures.TYPE_CASTLE:
				return FIGURE_COST_CASTLE_OPENING;
			case Figures.TYPE_QUEEN:
				return FIGURE_COST_QUEEN_OPENING;
			case Figures.TYPE_KING:
				return FIGURE_COST_KING;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
	
	public static final int interpolateByFactor(int val_o, int val_e, int factor) {
		
		if (factor > getMaxMaterialFactor()) {
			factor = getMaxMaterialFactor();
		}
		
		int o_part = factor;
		int e_part = getMaxMaterialFactor() - factor;
		
		int result = ((val_o * o_part) + (val_e * e_part)) / getMaxMaterialFactor();
			
		return result; 
	}
	
	public static final int interpolateByFactorAndColour(int val_o, int val_e, int factor) {
		
		if (factor > getMaxMaterialFactor() / 2) {
			factor = getMaxMaterialFactor() / 2;
		}
		
		int o_part = factor;
		int e_part = (getMaxMaterialFactor() / 2) - factor;
		
		int result = ((val_o * o_part) + (val_e * e_part)) / (getMaxMaterialFactor() / 2);
			
		return result; 
	}
	
	public static int getFigureMaterialSEE(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return FIGURE_COST_PAWN_SEE;
			case Figures.TYPE_KNIGHT:
				return FIGURE_COST_KNIGHT_SEE;
			case Figures.TYPE_OFFICER:
				return FIGURE_COST_OFFICER_SEE;
			case Figures.TYPE_CASTLE:
				return FIGURE_COST_CASTLE_SEE;
			case Figures.TYPE_QUEEN:
				return FIGURE_COST_QUEEN_SEE;
			case Figures.TYPE_KING:
				return FIGURE_COST_KING_SEE;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
	
	
	public static int getMaxMaterialFactor() { 
		return FIGURE_FACTOR_MAX;
	}
	
	
	public static int getFigureMaterialFactor(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return FIGURE_FACTOR_PAWN;
			case Figures.TYPE_KNIGHT:
				return FIGURE_FACTOR_KNIGHT;
			case Figures.TYPE_OFFICER:
				return FIGURE_FACTOR_OFFICER;
			case Figures.TYPE_CASTLE:
				return FIGURE_FACTOR_CASTLE;
			case Figures.TYPE_QUEEN:
				return FIGURE_FACTOR_QUEEN;
			case Figures.TYPE_KING:
				return FIGURE_FACTOR_KING;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
}
