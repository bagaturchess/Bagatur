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

public class PSTConstants_LKG {
	
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
			0,	0,	0,	0,	0,	0,	0,	0,	
			-16,	-5,	-1,	4,	4,	-1,	-5,	-16,	
			-16,	-5,	2,	4,	4,	2,	-5,	-16,	
			-16,	-5,	5,	14,	14,	5,	-5,	-16,	
			-16,	-5,	5,	24,	24,	5,	-5,	-16,	
			-16,	-5,	2,	14,	14,	2,	-5,	-16,	
			-16,	-5,	-1,	4,	4,	-1,	-5,	-16,	
			0,	0,	0,	0,	0,	0,	0,	0,	
	});
	
	public static final int[] BISHOP_O = Utils.reverseSpecial (new int[] {
			-8,	-8,	-6,	-4,	-4,	-6,	-8,	-8,	
			-8,	0,	-2,	0,	0,	-2,	0,	-8,	
			-6,	-2,	3,	1,	1,	3,	-2,	-6,	
			-4,	0,	1,	7,	7,	1,	0,	-4,	
			-4,	0,	1,	7,	7,	1,	0,	-4,	
			-6,	-2,	3,	1,	1,	3,	-2,	-6,	
			-8,	0,	-2,	0,	0,	-2,	0,	-8,	
			-18,	-18,	-16,	-14,	-14,	-16,	-18,	-18,
	});
	
	public static final int[] KNIGHT_O = Utils.reverseSpecial (new int[] {
			-72,	-25,	-15,	-10,	-10,	-15,	-25,	-72,	
			-20,	-10,	0,	4,	4,	0,	-10,	-20,	
			-5,	4,	14,	19,	19,	14,	4,	-5,	
			-5,	4,	14,	19,	19,	14,	4,	-5,	
			-10,	0,	9,	14,	14,	9,	0,	-10,	
			-20,	-10,	0,	4,	4,	0,	-10,	-20,	
			-35,	-25,	-15,	-10,	-10,	-15,	-25,	-35,	
			-50,	-40,	-30,	-25,	-25,	-30,	-40,	-50,	
	});
	
	public static final int[] ROOK_O = Utils.reverseSpecial (new int[] {
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,	
			-6,	-2,	0,	3,	3,	0,	-2,	-6,		
	});
	
	public static final int[] QUEEN_O = Utils.reverseSpecial (new int[] {
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
	});
	
	public static final int[] KING_O = Utils.reverseSpecial (new int[] {
			38,	48,	28,	8,	8,	28,	48,	38,	
			48,	58,	38,	18,	18,	38,	58,	48,	
			58,	68,	48,	28,	28,	48,	68,	58,	
			68,	78,	58,	38,	38,	58,	78,	68,	
			78,	87,	68,	48,	48,	68,	87,	78,	
			87,	98,	78,	58,	58,	78,	98,	87,	
			107,117,98,	78,	78,	98,	117,107,	
			117,128,107,87,	87,107,	128,117,	
	});
	
	
	/**
	 * Ending
	 */
	public static final int[] PAWN_E = Utils.reverseSpecial (new int[] {
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
	});
		
	public static final int[] BISHOP_E = Utils.reverseSpecial (new int[] {
			-18,	-12,	-9,	-6,	-6,	-9,	-12,	-18,	
			-12,	-6,	-3,	0,	0,	-3,	-6,	-12,	
			-9,	-3,	0,	2,	2,	0,	-3,	-9,	
			-6,	0,	2,	5,	5,	2,	0,	-6,	
			-6,	0,	2,	5,	5,	2,	0,	-6,	
			-9,	-3,	0,	2,	2,	0,	-3,	-9,	
			-12,	-6,	-3,	0,	0,	-3,	-6,	-12,	
			-18,	-12,	-9,	-6,	-6,	-9,	-12,	-18,	
	});
	
	public static final int[] KNIGHT_E = Utils.reverseSpecial (new int[] {
			-40,	-30,	-20,	-15,	-15,	-20,	-30,	-40,	
			-30,	-20,	-10,	-5,	-5,	-10,	-20,	-30,	
			-20,	-10,	0,	4,	4,	0,	-10,	-20,	
			-15,	-5,	4,	9,	9,	4,	-5,	-15,	
			-15,	-5,	4,	9,	9,	4,	-5,	-15,	
			-20,	-10,	0,	4,	4,	0,	-10,	-20,	
			-30,	-20,	-10,	-5,	-5,	-10,	-20,	-30,	
			-40,	-30,	-20,	-15,	-15,	-20,	-30,	-40,	
	});
	
	public static final int[] ROOK_E = Utils.reverseSpecial (new int[] {
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
			0,	0,	0,	0,	0,	0,	0,	0,	
	});
	
	public static final int[] QUEEN_E = Utils.reverseSpecial (new int[] {
			-24,	-16,	-12,	-8,	-8,	-12,	-16,	-24,	
			-16,	-8,	-4,	0,	0,	-4,	-8,	-16,	
			-12,	-4,	0,	3,	3,	0,	-4,	-12,	
			-8,	0,	3,	7,	7,	3,	0,	-8,	
			-8,	0,	3,	7,	7,	3,	0,	-8,	
			-12,	-4,	0,	3,	3,	0,	-4,	-12,	
			-16,	-8,	-4,	0,	0,	-4,	-8,	-16,	
			-24,	-16,	-12,	-8,	-8,	-12,	-16,	-24,	
	});
	
	public static final int[] KING_E = Utils.reverseSpecial (new int[] {
			6,	30,	42,	54,	54,	42,	30,	6,	
			30,	54,	66,	78,	78,	66,	54,	30,	
			42,	66,	78,	89,	89,	78,	66,	42,	
			54,	78,	89,	101,101,89,	78,	54,	
			54,	78,	89,	101,101,89,	78,	54,	
			42,	66,	78,	89,	89,	78,	66,	42,	
			30,	54,	66,	78,	78,	66,	54,	30,	
			6,	30,	42,	54,	54,	42,	30,	6,	
	});

	
	
/*	FeaturesPersistency.load: reading binary
	0	9845231020	0.0	1855	3.0341002E7   -144.803  466.877  9,845,231,020.   -4,393,478,990.
	1	3287036001	66.61291142561731	1765	3.0341002E7   1.255  162.56  3,287,036,001.  38,079,597.
	2	3329448989	66.18211413996865	1783	3.0341002E7   1.100  163.378  3,329,448,989.  33,372,349.
	3	3325530766	66.2219123223784	1790	3.0341002E7   0.895  163.139  3,325,530,766.  27,158,890.
	4	3326150768	66.215614836837	1782	3.0341002E7   1.006  163.278  3,326,150,768.  30,517,654.
	5	3324403876	66.2333583717165	1770	3.0341002E7   0.857  163.107  3,324,403,876.  26,008,298.
	6	3325678807	66.22040864004022	1836	3.0341002E7   0.987  163.266  3,325,678,807.  29,954,671.
	7	3324119643	66.23624538370659	1785	3.0341002E7   0.848  163.10  3,324,119,643.  25,736,089.
	8	3325577157	66.22144111962139	1766	3.0341002E7   0.983  163.263  3,325,577,157.  29,815,641.
	9	3324042768	66.23702621860873	1773	3.0341002E7   0.845  163.098  3,324,042,768.  25,652,160.
	10	3325555564	66.22166044408372	1769	3.0341002E7   0.981  163.263  3,325,555,564.  29,772,026.*/
}
