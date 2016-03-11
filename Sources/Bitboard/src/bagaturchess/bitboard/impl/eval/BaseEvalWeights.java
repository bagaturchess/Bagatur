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

//import game.chess.engine.impl0.EngineConstants;

public class BaseEvalWeights {
	
	/**
	 *                     A and H pawns are worth about 15% from the others
	 *                     P (pawn)= 1
	 *                     BB(bishop pair)= 1/2
	 *                     R(rook) =  5 
	 *                     B(bishop) = 3 + 1/4
	 *                     N(knight) =  3 + 1/4 
	 *                     Q(queen) =  9 + 3/4
	 */
	
	/**
	 * However in chessprograms that's not the case. Within a few years you find
	 * out that the real values already are closer to, here is what they are in
	 * DIEP :
   * pawn   = 1.0
   * knight = 3.625
   * bishop = 3.675
   * rook   = 5.8
   * queen  = 11.75
	 */
	
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
	
	/*private static final long HALF_PAWN = FIGURE_COST_PAWN / 2;
	private static final long QUARTER_PAWN = HALF_PAWN / 2;
	private static final long EIGHT_PAWN = QUARTER_PAWN / 2;
	private static final long SIXTEENTH_PAWN = EIGHT_PAWN / 2;*/
	
	public static final int FIGURE_COST_KING = 9 * FIGURE_COST_QUEEN_OPENING + 2
			* FIGURE_COST_KNIGHT_OPENING + 2 * FIGURE_COST_OFFICER_OPENING + 2
			* FIGURE_COST_CASTLE_OPENING;

	//private static final int MAX_EVAL_MATERIAL = 2 * FIGURE_COST_KING;
	
	/*private static final long MAX_MATERIAL = FIGURE_COST_KING
										+ 4 * FIGURE_COST_QUEEN
										+ 2 * FIGURE_COST_CASTLE
										+ 2 * FIGURE_COST_OFFICER
										+ 2 * FIGURE_COST_KNIGHT
										+ 8 * FIGURE_COST_PAWN + 1;*/
			
	/*public static int getFigureCost(int type, int factor) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return interpolateByFactor(FIGURE_COST_PAWN_OPENING, FIGURE_COST_PAWN_ENDGAME, factor);
			case Figures.TYPE_KNIGHT:
				return interpolateByFactor(FIGURE_COST_KNIGHT_OPENING, FIGURE_COST_KNIGHT_ENDGAME, factor);
			case Figures.TYPE_OFFICER:
				return interpolateByFactor(FIGURE_COST_OFFICER_OPENING, FIGURE_COST_OFFICER_ENDGAME, factor);
			case Figures.TYPE_CASTLE:
				return interpolateByFactor(FIGURE_COST_CASTLE_OPENING, FIGURE_COST_CASTLE_ENDGAME, factor);
			case Figures.TYPE_QUEEN:
				return interpolateByFactor(FIGURE_COST_QUEEN_OPENING, FIGURE_COST_QUEEN_ENDGAME, factor);
			case Figures.TYPE_KING:
				return FIGURE_COST_KING;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}*/
	
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
		int e_part = FIGURE_FACTOR_MAX - factor;
		
		int result = ((val_o * o_part) + (val_e * e_part)) / FIGURE_FACTOR_MAX;
			
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
	
	/*private static long getMaxMaterial() {
		return MAX_MATERIAL;
	}*/
	
	public static int getMaxMaterialFactor() {
		//if (true) throw new IllegalStateException("Fix my usages"); 
		return FIGURE_FACTOR_MAX;
	}
	
	public static int getFigureMaterialFactor(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return 0;
			case Figures.TYPE_KNIGHT:
				return 3;
			case Figures.TYPE_OFFICER:
				return 3;
			case Figures.TYPE_CASTLE:
				return 5;
			case Figures.TYPE_QUEEN:
				return 9;
			case Figures.TYPE_KING:
				return 0;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
	
	/*private static int getFigureSimpleCost(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return 1;
			case Figures.TYPE_KNIGHT:
				return 3;
			case Figures.TYPE_OFFICER:
				return 3;
			case Figures.TYPE_CASTLE:
				return 5;
			case Figures.TYPE_QUEEN:
				return 9;
			case Figures.TYPE_KING:
				return 50;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}*/
	
	public static void main(String[] args) {
//		/System.out.println("MAX_EVAL_MATERIAL=" + MAX_EVAL_MATERIAL);
	}
}
