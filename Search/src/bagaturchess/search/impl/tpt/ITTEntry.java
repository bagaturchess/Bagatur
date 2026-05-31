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
package bagaturchess.search.impl.tpt;


public interface ITTEntry {


	public static final int FLAG_EXACT = 0;
	public static final int FLAG_LOWER = 1;
	public static final int FLAG_UPPER = 2;


	public boolean isEmpty();
	public int getDepth();
	public int getFlag();
	public int getEval();
	public int getBestMove();

	public void setIsEmpty(boolean isEmpty);
	public void setDepth(int depth);
	public void setFlag(int flag);
	public void setEval(int eval);
	public void setBestMove(int bestmove);


	// =====================================================================
	// Two-bound API (used by MTD(f)-friendly TT implementations such as
	// TTable_Impl3). Single-bound implementations expose whichever bound
	// matches the legacy flag - hasLowerBound() returns true for FLAG_LOWER
	// and FLAG_EXACT, hasUpperBound() returns true for FLAG_UPPER and
	// FLAG_EXACT - so callers written against the new API still work when
	// fed entries populated by single-bound tables.
	// =====================================================================

	public default boolean hasLowerBound() {
		return !isEmpty() && (getFlag() == FLAG_LOWER || getFlag() == FLAG_EXACT);
	}

	public default int getLowerEval()      { return getEval(); }
	public default int getLowerDepth()     { return getDepth(); }
	public default int getLowerBestMove()  { return getBestMove(); }

	public default boolean hasUpperBound() {
		return !isEmpty() && (getFlag() == FLAG_UPPER || getFlag() == FLAG_EXACT);
	}

	public default int getUpperEval()      { return getEval(); }
	public default int getUpperDepth()     { return getDepth(); }
	public default int getUpperBestMove()  { return getBestMove(); }

	public default void setLowerBound(boolean has, int eval, int depth, int bestMove) {
		// No-op for single-bound implementations.
	}

	public default void setUpperBound(boolean has, int eval, int depth, int bestMove) {
		// No-op for single-bound implementations.
	}
}
