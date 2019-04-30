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

public interface IBaseEval {
	
	public int getMaterial_o();
	public int getMaterial_e();
	public int getWhiteMaterialPawns_o();
	public int getWhiteMaterialPawns_e();
	public int getBlackMaterialPawns_o();
	public int getBlackMaterialPawns_e();
	public int getWhiteMaterialNonPawns_o();
	public int getWhiteMaterialNonPawns_e();
	public int getBlackMaterialNonPawns_o();
	public int getBlackMaterialNonPawns_e();
	public int getMaterial_BARIER_NOPAWNS_O();
	public int getMaterial_BARIER_NOPAWNS_E();
	
	public int getPST_o();
	public int getPST_e();
	
	public int getMaterial(int pieceType);
	public int getMaterialGain(int move);
	
	public double getPSTMoveGoodPercent(int move);
}
