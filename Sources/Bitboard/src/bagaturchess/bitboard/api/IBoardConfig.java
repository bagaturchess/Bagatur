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
package bagaturchess.bitboard.api;


public interface IBoardConfig {
	
	public boolean getFieldsStatesSupport();
	
	public double getWeight_PST_PAWN_O();
	public double getWeight_PST_PAWN_E();
	
	public double getWeight_PST_KING_O();
	public double getWeight_PST_KING_E();
	
	public double getWeight_PST_KNIGHT_O();
	public double getWeight_PST_KNIGHT_E();
	
	public double getWeight_PST_BISHOP_O();
	public double getWeight_PST_BISHOP_E();
	
	public double getWeight_PST_ROOK_O();
	public double getWeight_PST_ROOK_E();
	
	public double getWeight_PST_QUEEN_O();
	public double getWeight_PST_QUEEN_E();
	
	public double[] getPST_PAWN_O();
	public double[] getPST_PAWN_E();
	
	public double[] getPST_KING_O();
	public double[] getPST_KING_E();
	
	public double[] getPST_KNIGHT_O();
	public double[] getPST_KNIGHT_E();
	
	public double[] getPST_BISHOP_O();
	public double[] getPST_BISHOP_E();
	
	public double[] getPST_ROOK_O();
	public double[] getPST_ROOK_E();
	
	public double[] getPST_QUEEN_O();
	public double[] getPST_QUEEN_E();
	
	public double getMaterial_PAWN_O();
	public double getMaterial_PAWN_E();
	
	public double getMaterial_KING_O();
	public double getMaterial_KING_E();
	
	public double getMaterial_KNIGHT_O();
	public double getMaterial_KNIGHT_E();
	
	public double getMaterial_BISHOP_O();
	public double getMaterial_BISHOP_E();
	
	public double getMaterial_ROOK_O();
	public double getMaterial_ROOK_E();
	
	public double getMaterial_QUEEN_O();
	public double getMaterial_QUEEN_E();
	
	public double getMaterial_BARIER_NOPAWNS_O();
	public double getMaterial_BARIER_NOPAWNS_E();
}
