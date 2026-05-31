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


public class TTEntry_BaseImpl implements ITTEntry {


	private boolean isEmpty;
	private int depth;
	private int flag;
	private int eval;
	private int bestmove;

	// Two-bound state (populated by TTable_Impl3 only; single-bound tables leave these unused).
	private boolean hasLower;
	private int lowerEval;
	private int lowerDepth;
	private int lowerBestMove;

	private boolean hasUpper;
	private int upperEval;
	private int upperDepth;
	private int upperBestMove;


	@Override
	public boolean isEmpty() {
		return isEmpty;
	}


	@Override
	public int getDepth() {
		return depth;
	}


	@Override
	public int getFlag() {
		return flag;
	}


	@Override
	public int getEval() {
		return eval;
	}


	@Override
	public int getBestMove() {
		return bestmove;
	}


	@Override
	public void setIsEmpty(boolean _isEmpty) {
		isEmpty = _isEmpty;
		if (_isEmpty) {
			// Clear two-bound state on logical reset so stale values from
			// the previous probe don't leak into the next caller.
			hasLower = false;
			hasUpper = false;
		}
	}


	@Override
	public void setDepth(int _depth) {
		depth = _depth;
	}


	@Override
	public void setFlag(int _flag) {
		flag = _flag;
	}


	@Override
	public void setEval(int _eval) {
		eval = _eval;
	}


	@Override
	public void setBestMove(int _bestmove) {
		bestmove = _bestmove;
	}


	// =====================================================================
	// Two-bound accessors (overriding the no-op defaults in ITTEntry).
	// =====================================================================

	// Backward compatibility: when a single-bound TT (Impl1 / Impl2) populates
	// this entry it only calls setFlag/setEval/setDepth/setBestMove and never
	// calls setLowerBound/setUpperBound, so the explicit hasLower/hasUpper
	// flags stay false. Fall back to the legacy flag so callers that use the
	// two-bound API still see the right bound exposed.

	@Override
	public boolean hasLowerBound() {
		return hasLower
				|| (!isEmpty && (flag == FLAG_LOWER || flag == FLAG_EXACT));
	}

	@Override
	public int getLowerEval() {
		return hasLower ? lowerEval : eval;
	}

	@Override
	public int getLowerDepth() {
		return hasLower ? lowerDepth : depth;
	}

	@Override
	public int getLowerBestMove() {
		return hasLower ? lowerBestMove : bestmove;
	}

	@Override
	public boolean hasUpperBound() {
		return hasUpper
				|| (!isEmpty && (flag == FLAG_UPPER || flag == FLAG_EXACT));
	}

	@Override
	public int getUpperEval() {
		return hasUpper ? upperEval : eval;
	}

	@Override
	public int getUpperDepth() {
		return hasUpper ? upperDepth : depth;
	}

	@Override
	public int getUpperBestMove() {
		return hasUpper ? upperBestMove : bestmove;
	}

	@Override
	public void setLowerBound(boolean has, int evalArg, int depthArg, int bestMove) {
		this.hasLower = has;
		if (has) {
			this.lowerEval = evalArg;
			this.lowerDepth = depthArg;
			this.lowerBestMove = bestMove;
		}
	}

	@Override
	public void setUpperBound(boolean has, int evalArg, int depthArg, int bestMove) {
		this.hasUpper = has;
		if (has) {
			this.upperEval = evalArg;
			this.upperDepth = depthArg;
			this.upperBestMove = bestMove;
		}
	}
}
