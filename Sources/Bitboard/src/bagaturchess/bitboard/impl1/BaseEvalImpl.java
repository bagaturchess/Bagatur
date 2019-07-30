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
package bagaturchess.bitboard.impl1;

import bagaturchess.bitboard.api.IBaseEval;

/**
 * @author i027638
 *
 */
public class BaseEvalImpl implements IBaseEval {

	/**
	 * 
	 */
	public BaseEvalImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_o()
	 */
	@Override
	public int getMaterial_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_e()
	 */
	@Override
	public int getMaterial_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_o()
	 */
	@Override
	public int getWhiteMaterialPawns_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_e()
	 */
	@Override
	public int getWhiteMaterialPawns_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_o()
	 */
	@Override
	public int getBlackMaterialPawns_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_e()
	 */
	@Override
	public int getBlackMaterialPawns_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_o()
	 */
	@Override
	public int getWhiteMaterialNonPawns_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_e()
	 */
	@Override
	public int getWhiteMaterialNonPawns_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_o()
	 */
	@Override
	public int getBlackMaterialNonPawns_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_e()
	 */
	@Override
	public int getBlackMaterialNonPawns_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_O()
	 */
	@Override
	public int getMaterial_BARIER_NOPAWNS_O() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_E()
	 */
	@Override
	public int getMaterial_BARIER_NOPAWNS_E() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPST_o()
	 */
	@Override
	public int getPST_o() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPST_e()
	 */
	@Override
	public int getPST_e() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial(int)
	 */
	@Override
	public int getMaterial(int pieceType) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterialGain(int)
	 */
	@Override
	public int getMaterialGain(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPSTMoveGoodPercent(int)
	 */
	@Override
	public double getPSTMoveGoodPercent(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

}
