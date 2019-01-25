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
package bagaturchess.engines.evaladapters.chess22k;


import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BISHOP;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BLACK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.NIGHT;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.QUEEN;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.ROOK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.WHITE;


public class EvalInfo {

	// attack boards
	public final long[][] attacks = new long[2][7];
	public final long[] attacksAll = new long[2];
	public final long[] doubleAttacks = new long[2];
	public final int[] kingAttackersFlag = new int[2];

	public long passedPawnsAndOutposts;
	
	public void clearEvalAttacks() {
		kingAttackersFlag[WHITE] = 0;
		kingAttackersFlag[BLACK] = 0;
		attacks[WHITE][NIGHT] = 0;
		attacks[BLACK][NIGHT] = 0;
		attacks[WHITE][BISHOP] = 0;
		attacks[BLACK][BISHOP] = 0;
		attacks[WHITE][ROOK] = 0;
		attacks[BLACK][ROOK] = 0;
		attacks[WHITE][QUEEN] = 0;
		attacks[BLACK][QUEEN] = 0;
		doubleAttacks[WHITE] = 0;
		doubleAttacks[BLACK] = 0;
	}
}
