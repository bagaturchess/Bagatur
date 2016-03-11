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

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class PSTConstants_Bagatur {
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,
	});
	
	public static final int getMoveScores_o(int move) {
		int type = MoveInt.getFigureType(move);
		int from = MoveInt.getFromFieldID(move);
		int to = MoveInt.getToFieldID(move);
		if (!MoveInt.isWhite(move)) {
			from = HORIZONTAL_SYMMETRY[from];
			to = HORIZONTAL_SYMMETRY[to];
		}
		int[] pst = getArray_o(type);
		return pst[to] - pst[from];
	}
	
	public static final int getMoveScores_e(int move) {
		int type = MoveInt.getFigureType(move);
		int from = MoveInt.getFromFieldID(move);
		int to = MoveInt.getToFieldID(move);
		if (!MoveInt.isWhite(move)) {
			from = HORIZONTAL_SYMMETRY[from];
			to = HORIZONTAL_SYMMETRY[to];
		}
		int[] pst = getArray_e(type);
		return pst[to] - pst[from];
	}
	
	public static final int getPieceScores_o(int field, int type) {
		int[] pst = getArray_o(type);
		return pst[field];
	}
	
	public static final int getPieceScores_e(int field, int type) {
		int[] pst = getArray_e(type);
		return pst[field];

	}
	
	public static final int[] getArray_o(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return PAWN_O;
			case Constants.TYPE_KING:
				return KING_O;
			case Constants.TYPE_KNIGHT:
				return KNIGHT_O;
			case Constants.TYPE_BISHOP:
				return BISHOP_O;
			case Constants.TYPE_ROOK:
				return ROOK_O;
			case Constants.TYPE_QUEEN:
				return QUEEN_O;
			default:
				throw new IllegalStateException();
		}
	}
	
	public static final int[] getArray_e(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return PAWN_E;
			case Constants.TYPE_KING:
				return KING_E;
			case Constants.TYPE_KNIGHT:
				return KNIGHT_E;
			case Constants.TYPE_BISHOP:
				return BISHOP_E;
			case Constants.TYPE_ROOK:
				return ROOK_E;
			case Constants.TYPE_QUEEN:
				return QUEEN_E;
			default:
				throw new IllegalStateException();
		}
	}
	
	
	/**
	 * Openning
	 */
	public static final int[] PAWN_O = Utils.reverseSpecial (new int[] {
			  0,   0,    0,   0,   0,    0,   0,    0,   
			  6,   2,    3,  -3,  -3,    3,   2,    6,   
			  9,  10,   12,   7,   7,   12,  10,    9,   
			 -4,   0,    4,   6,   6,    4,   0,   -4,   
			-14,  -4,    2,   4,   4,    2,  -4,  -14,   
			-10,   2,    2,   3,   3,    2,   2,  -10,   
			-15,   1,    0,   1,   1,    0,   1,  -15,   
			  0,   0,    0,   0,   0,    0,   0,    0,   
	});
	
	public static final int[] BISHOP_O = Utils.reverseSpecial (new int[] {
			-1,  -3,  -4,  -6,  -6,  -4,  -3,  -1,   
			-4,  -4,  -2,  -4,  -4,  -2,  -4,  -4,   
			-1,  -1,   0,   0,   0,   0,  -1,  -1,   
			-4,   1,   0,   4,   4,   0,   1,  -4,   
			 0,  -2,  -1,   7,   7,  -1,   0,   0,   
			 2,   1,   5,   2,   2,   5,   1,   2,   
			 8,   9,   3,   1,   1,   3,   9,   8,   
			 7,   4,  -2,   0,   0,  -2,   4,   7,   
	});
	
	public static final int[] KNIGHT_O = Utils.reverseSpecial (new int[] {
			-6,   1,   1,   3,   3,   1,   1,  -6,   
			 5,   5,   7,   6,   6,   7,   5,   5,   
			 4,   9,   2,   5,   5,   2,   9,   4,   
			12,   7,   3,   7,   7,   3,   7,  12,   
			 3,   2,   2,   4,   4,   2,   2,   3,   
		   -10,  -7,  -9,   0,   0,  -9,  -7,  -10,   
			-7,  -4,  -8,  -1,  -1,  -8,  -4,  -7,   
		    -9,  -10, -8,  -4,  -4,  -8, -10,  -9,   
	});
	
	public static final int[] ROOK_O = Utils.reverseSpecial (new int[] {
			  7,   5,   7,   8,   8,   7,   5,   7,   
			  3,   3,   6,   6,   6,   6,   3,   3,   
			  5,   4,   4,   7,   7,   4,   4,   5,   
			 -2,  -1,  -1,   1,   1,  -1,  -1,  -2,   
			 -6,  -4,  -3,  -1,  -1,  -3,  -4,  -6,   
			 -9,  -3,  -3,  -1,  -1,  -3,  -3,  -9,   
			-11,  -6,  -4,   0,   0,  -4,  -6, -11,   
			 -4,   0,   1,   3,   3,   1,   0,  -4,   
	});
	
	public static final int[] QUEEN_O = Utils.reverseSpecial (new int[] {
			 7,    6,   5,   6,   6,   5,   6,   7,   
			 4,   -1,   3,   3,   3,   3,  -1,   4,   
			 2,    3,   5,   5,   5,   5,   3,   2,   
			 0,    2,   2,   2,   2,   2,   2,   0,   
			 0,   -2,   0,   0,   0,   0,  -2,   0,   
			-3,   -1,  -1,   0,   0,  -1,  -1,  -3,   
			-3,   -3,   0,   0,   0,   0,  -3,  -3,   
		    -9,  -10, -11,  -6,  -6, -11, -10,  -9, 
	});
	
	public static final int[] KING_O = Utils.reverseSpecial (new int[] {
			 0,    0,   0,   0,   0,   0,   0,   0,   
			 0,    1,   1,   0,   0,   1,   1,   0,   
			 1,    4,   3,   1,   1,   3,   4,   1,   
			 0,    4,   4,   0,   0,   4,   4,   0,   
			-5,    2,   4,   5,   5,   4,   2,  -5,   
			-7,   -2,   1,   1,   1,   1,  -2,  -7,   
			 6,   10,   2, -12, -12,   2,  10,   6,   
			-1,   10,   5, -40, -10, -25,  10,  -1,
	});
	
	
	/**
	 * Ending
	 */
	public static final int[] PAWN_E = Utils.reverseSpecial (new int[] {
			 0,   0,   0,   0,   0,   0,   0,   0,   
			11,   1,  -3,  -9,  -9,  -3,   1,  11,   
			 9,   9,   6,  -7,  -7,   6,   9,   9,   
			 4,   5,   3,  -1,  -1,   3,   5,   4,   
			-2,  -2,  -2,  -1,  -1,  -2,  -2,  -2,   
			-7,  -3,  -6,  -2,  -2,  -6,  -3,  -7,   
			-2,   1,   2,   0,   0,   2,   1,  -2,   
			 0,   0,   0,   0,   0,   0,   0,   0,   
	});
	
	public static final int[] BISHOP_E = Utils.reverseSpecial (new int[] {
			 0,  -1,  -2,  -3,  -3,  -2,  -1,   0,   
			-1,  -1,  -2,  -3,  -3,  -2,  -1,  -1,   
			 1,   0,   1,  -1,  -1,   1,   0,   1,   
			-2,   0,   0,   1,   1,   0,   0,  -2,   
			-3,   0,   2,   2,   2,   2,   0,  -3,   
			 0,   1,   3,   2,   2,   3,   1,   0,   
			 2,   5,   2,  -1,  -1,   2,   5,   2,   
			 3,   2,   6,   2,   2,   6,   2,   3,
	});
	
	public static final int[] KNIGHT_E = Utils.reverseSpecial (new int[] {
			 0,   1,   0,   2,   2,   0,   1,   0,   
			 0,   1,   1,   4,   4,   1,   1,   0,   
			 1,   1,   3,   5,   5,   3,   1,   1,   
			 4,   9,   4,   9,   9,   4,   9,   4,   
			 1,   1,   1,   3,   3,   1,   1,   1,   
			-1,  -6,  -9,  -5,  -5,  -9,  -6,  -1,   
			-1,  -1,  -7,  -6,  -6,  -7,  -1,  -1,   
			-1,  -4,  -2,   1,   1,  -2,  -4,  -1,   
	});
	
	public static final int[] ROOK_E = Utils.reverseSpecial (new int[] {
			 4,   5,   6,   7,   7,   6,   5,   4,   
			 1,   5,   6,   5,   5,   6,   5,   1,   
			 4,   5,   6,   6,   6,   6,   5,   4,   
			 0,   0,  -1,  -1,  -1,  -1,   0,   0,   
			-4,  -3,  -2,  -3,  -3,  -2,  -3,  -4,   
			-2,  -3,  -3,  -3,  -3,  -3,  -3,  -2,   
			-1,  -2,  -1,  -1,  -1,  -1,  -2,  -1,   
			-4,  -4,  -3,  -2,  -2,  -3,  -4,  -4,   
	});
	
	public static final int[] QUEEN_E = Utils.reverseSpecial (new int[] {
			 2,   3,   4,   5,   5,   4,   3,   2,   
			 3,   2,   3,   4,   4,   3,   2,   3,   
			 3,   2,   3,   4,   4,   3,   2,   3,   
			 2,   2,   3,   3,   3,   3,   2,   2,   
			-1,  -1,   0,   1,   1,   0,  -1,  -1,   
			-1,  -3,  -3,  -3,  -3,  -3,  -3,  -1,   
			-1,  -2,  -5,  -6,  -6,  -5,  -2,  -1,   
			-3,  -3,  -4,  -5,  -5,  -4,  -3,  -3,  
	});
	
	public static final int[] KING_E = Utils.reverseSpecial (new int[] {
			  1,   0,   0,   0,   0,   0,   0,   1,   
			  1,   6,   4,   2,   2,   4,   6,   1,   
			  3,  15,  11,   5,   5,  11,  15,   3,   
			  0,  10,  12,   2,   2,  12,  10,   0,   
			 -6,   5,  10,  11,  11,  10,   5,  -6,   
			-10,   5,  10,  11,  11,  10,   5, -10,   
			-22,  -7,  -2,   9,   9,  -2,  -7, -22,   
			-40, -25, -15, -10, -10, -15, -25, -40, 
	});
}
