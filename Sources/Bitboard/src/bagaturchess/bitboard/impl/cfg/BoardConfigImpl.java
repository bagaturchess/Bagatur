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
package bagaturchess.bitboard.impl.cfg;


import bagaturchess.bitboard.api.IBoardConfig;


public class BoardConfigImpl implements IBoardConfig {
	
	private double[] zeros = new double[64];
	
	public boolean getFieldsStatesSupport() {
		return false;
	}
	
	@Override
	public double[] getPST_PAWN_O() {
		return zeros;
	}

	@Override
	public double[] getPST_PAWN_E() {
		return zeros;
	}

	@Override
	public double[] getPST_KING_O() {
		return zeros;
	}

	@Override
	public double[] getPST_KING_E() {
		return zeros;
	}

	@Override
	public double[] getPST_KNIGHT_O() {
		return zeros;
	}

	@Override
	public double[] getPST_KNIGHT_E() {
		return zeros;
	}

	@Override
	public double[] getPST_BISHOP_O() {
		return zeros;
	}

	@Override
	public double[] getPST_BISHOP_E() {
		return zeros;
	}

	@Override
	public double[] getPST_ROOK_O() {
		return zeros;
	}

	@Override
	public double[] getPST_ROOK_E() {
		return zeros;
	}

	@Override
	public double[] getPST_QUEEN_O() {
		return zeros;
	}

	@Override
	public double[] getPST_QUEEN_E() {
		return zeros;
	}

	@Override
	public double getMaterial_PAWN_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_PAWN_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_KING_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_KING_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_KNIGHT_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_KNIGHT_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_BISHOP_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_BISHOP_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_ROOK_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_ROOK_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_QUEEN_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaterial_QUEEN_E() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public double getMaterial_BARIER_NOPAWNS_O() {
		throw new UnsupportedOperationException();
	}


	@Override
	public double getMaterial_BARIER_NOPAWNS_E() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getWeight_PST_PAWN_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_PAWN_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_KING_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_KING_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_KNIGHT_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_KNIGHT_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_BISHOP_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_BISHOP_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_ROOK_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_ROOK_E() {
		return 1;
	}

	@Override
	public double getWeight_PST_QUEEN_O() {
		return 1;
	}

	@Override
	public double getWeight_PST_QUEEN_E() {
		return 1;
	}
}
