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


public class BaseEvalImpl implements IBaseEval {

	
	public BaseEvalImpl() {
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_o()
	 */
	@Override
	public int getMaterial_o() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_e()
	 */
	@Override
	public int getMaterial_e() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_o()
	 */
	@Override
	public int getWhiteMaterialPawns_o() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialPawns_e()
	 */
	@Override
	public int getWhiteMaterialPawns_e() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_o()
	 */
	@Override
	public int getBlackMaterialPawns_o() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialPawns_e()
	 */
	@Override
	public int getBlackMaterialPawns_e() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_o()
	 */
	@Override
	public int getWhiteMaterialNonPawns_o() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getWhiteMaterialNonPawns_e()
	 */
	@Override
	public int getWhiteMaterialNonPawns_e() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_o()
	 */
	@Override
	public int getBlackMaterialNonPawns_o() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getBlackMaterialNonPawns_e()
	 */
	@Override
	public int getBlackMaterialNonPawns_e() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_O()
	 */
	@Override
	public int getMaterial_BARIER_NOPAWNS_O() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial_BARIER_NOPAWNS_E()
	 */
	@Override
	public int getMaterial_BARIER_NOPAWNS_E() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPST_o()
	 */
	@Override
	public int getPST_o() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPST_e()
	 */
	@Override
	public int getPST_e() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterial(int)
	 */
	@Override
	public int getMaterial(int pieceType) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getMaterialGain(int)
	 */
	@Override
	public int getMaterialGain(int move) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IBaseEval#getPSTMoveGoodPercent(int)
	 */
	@Override
	public double getPSTMoveGoodPercent(int move) {
		throw new UnsupportedOperationException();
	}

}
