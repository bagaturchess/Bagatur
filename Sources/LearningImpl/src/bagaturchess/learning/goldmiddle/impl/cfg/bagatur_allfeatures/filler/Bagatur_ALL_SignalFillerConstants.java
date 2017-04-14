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
package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;

import bagaturchess.bitboard.common.Utils;


public interface Bagatur_ALL_SignalFillerConstants {
	
	
	/**
	 * Pawns
	 */
	public static final int[] PAWNS_DOUBLED_O		= new int[] {-12, -11, -6, -3};
	public static final int[] PAWNS_DOUBLED_E		= new int[] {-19, -17, -16, -13};
	public static final int[] PAWNS_ISOLATED_O		= new int[] {-3, -6, -11, -12};
	public static final int[] PAWNS_ISOLATED_E		= new int[] {-13, -16, -17, -19};
	public static final int[] PAWNS_BACKWARD_O		= new int[] {-1, -3, -5, -6};
	public static final int[] PAWNS_BACKWARD_E		= new int[] {-6, -8, -9, -10};
	public static final int[] PAWNS_SUPPORTED_O		= new int[] {1, 3, 5, 6};
	public static final int[] PAWNS_SUPPORTED_E		= new int[] {6, 8, 9, 10};
	public static final int[] PAWNS_CANDIDATE_O		= new int[] {0, 1, 2, 5, 9, 18};
	public static final int[] PAWNS_CANDIDATE_E		= new int[] {0, 2, 7, 11, 26, 72};
	public static final int[] PAWNS_PASSED_O		= new int[] {0, 11, 11, 11, 26, 40, 65};
	public static final int[] PAWNS_PASSED_E		= new int[] {0, 7, 7, 25, 63, 134, 186};
	public static final int[] PAWNS_PASSED_SUPPORTED_O	= new int[] {0, 13, 13, 21, 41, 62, 124};
	public static final int[] PAWNS_PASSED_SUPPORTED_E	= new int[] {0, 9, 12, 31, 79, 178, 299};
	
	
	/**
	 * Mobility
	 */
	public static final int[] MOBILITY_KNIGHT_O		= new int[] {-21, -9, -4, -1, 2, 5, 7, 8, 8};
	public static final int[] MOBILITY_KNIGHT_E		= new int[] {-34, -20, -7, 4, 7, 13, 15, 17, 19};
	public static final int[] MOBILITY_BISHOP_O		= new int[] {-10, -6, -1, 0, 1, 4, 5, 6, 7, 8, 8, 9, 10, 11};
	public static final int[] MOBILITY_BISHOP_E		= new int[] {-23, -18, -12, -3, 4, 9, 13, 15, 18, 19, 20, 21, 22, 23};
	public static final int[] MOBILITY_ROOK_O		= new int[] {-11, -8, -5, -3, 0, 3, 5, 6, 7, 8, 8, 10, 11, 11, 12};
	public static final int[] MOBILITY_ROOK_E		= new int[] {-9, -4, 0, 0, 1, 1, 1, 2, 6, 7, 7, 8, 8, 8, 9};
	public static final int[] MOBILITY_QUEEN_O		= new int[] {-2, -1, -1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
	public static final int[] MOBILITY_QUEEN_E		= new int[] {-60, -55, -51, -46, -42, -37, -33, -29, -25, -21, -16, -11, -6, -1, 6, 11, 15, 19, 23, 27, 31, 36, 40, 44, 48, 52, 57, 61};

	public static final int[] KNIGHT_OUTPOST_O		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   4,   5,   5,   4,   0,   0,   
			0,   0,   7,   10,  10,  7,   0,   0,   
			0,   0,   8,   5,   5,   8,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final int[] KNIGHT_OUTPOST_E		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   2,   2,   1,   0,   0,   
			0,   0,   3,   3,   3,   3,   0,   0,   
			0,   0,   2,   4,   4,   2,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});

	public static final int[] BISHOP_OUTPOST_O		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   0,   0,   1,   0,   0,   
			0,   0,   3,   3,   3,   3,   0,   0,   
			0,   0,   3,   4,   4,   3,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final int[] BISHOP_OUTPOST_E		= Utils.reverseSpecial(new int[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   1,   1,   1,   1,   0,   0,   
			0,   0,   0,   1,   1,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			});
	
}
