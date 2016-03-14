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
package bagaturchess.search.api;

import bagaturchess.bitboard.common.Utils;


public interface IEvaluator {
	
	public static final int MAX_EVAL = 100000;
	public static final int MIN_EVAL = -MAX_EVAL;
	
	/**
	 * Static constants necessary for the evaluation
	 */
	public static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,

	});
	
	public static final int[] VERTICAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			  63,  62,  61,  60,  59,  58,  57,  56,
			  55,  54,  53,  52,  51,  50,  49,  48,
			  47,  46,  45,  44,  43,  42,  41,  40,
			  39,  38,  37,  36,  35,  34,  33,  32,
			  31,  30,  29,  28,  27,  26,  25,  24,
			  23,  22,  21,  20,  19,  18,  17,  16,
			  15,  14,  13,  12,  11,  10,   9,  8,
			   7,   6,   5,   4,   3,   2,   1,  0,

	});
	
	public int eval(int depth, int alpha, int beta, boolean pvNode, int rootColour);
	public void beforeSearch();
	public int roughEval(int depth, int rootColour);
	public int fastEval(int depth, int rootColour);
	public int lazyEval(int depth, int alpha, int beta, int rootColour);
	public double fullEval(int depth, int alpha, int beta, int rootColour);
	
	public int getMaterialQueen();
	
	public int getMaterial(int pieceType);
}
