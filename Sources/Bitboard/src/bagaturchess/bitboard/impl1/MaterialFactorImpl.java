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

import bagaturchess.bitboard.api.IMaterialFactor;

/**
 * @author i027638
 *
 */
public class MaterialFactorImpl implements IMaterialFactor {

	/**
	 * 
	 */
	public MaterialFactorImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#getBlackFactor()
	 */
	@Override
	public int getBlackFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#getWhiteFactor()
	 */
	@Override
	public int getWhiteFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#getTotalFactor()
	 */
	@Override
	public int getTotalFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#getOpenningPart()
	 */
	@Override
	public double getOpenningPart() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactor(int, int)
	 */
	@Override
	public int interpolateByFactor(int val_o, int val_e) {
		// TODO Auto-generated method stub
		return (val_o + val_e) / 2;
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactorAndColour(int, int, int)
	 */
	@Override
	public int interpolateByFactorAndColour(int colour, int val_o, int val_e) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IMaterialFactor#interpolateByFactor(double, double)
	 */
	@Override
	public int interpolateByFactor(double val_o, double val_e) {
		// TODO Auto-generated method stub
		return 0;
	}

}
