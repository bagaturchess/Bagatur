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
package bagaturchess.bitboard.impl.state;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.movegen.Move;

public class MoveMaterialGain {
	
	/*public static long calculateGain(long[] move, int factor) {
		final long masks = move[0];
		
		if ((masks & Move.MASK_CAPTURE) != Bits.NUMBER_0 || (masks & Move.MASK_PROMOTION) != Bits.NUMBER_0) {
			return getMoveWeightQSearch(masks, move, factor);
		}

		return 0;
	}

	public static int getMoveWeightQSearch(long mask, long[] move, int factor) {
		int result = 0;
		
		if ((mask & Move.MASK_CAPTURE) != Bits.NUMBER_0) {
			final int capturedFigureType = Move.getCapturedFigureType(move);
			result += BaseEvalWeights.getFigureCost(capturedFigureType, factor);
		}
		
		if ((mask & Move.MASK_PROMOTION) != Bits.NUMBER_0) {
			int promotionFigureType = Move.getPromotionFigureType(move);
			result += BaseEvalWeights.getFigureCost(promotionFigureType, factor);
			result -= BaseEvalWeights.getFigureCost(Figures.TYPE_PAWN, factor);
		}
		
		return result;
	}*/
}
