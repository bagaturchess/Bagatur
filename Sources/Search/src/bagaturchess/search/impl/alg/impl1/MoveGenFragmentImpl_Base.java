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
package bagaturchess.search.impl.alg.impl1;


import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;


public abstract class MoveGenFragmentImpl_Base implements IMoveGenFragment {
	
	
	protected ChessBoard cb;
	protected MoveGenerator gen;
	
	private long count_cutoffs;
	private long count_totals;
	
	
	public MoveGenFragmentImpl_Base(ChessBoard _cb, MoveGenerator _gen) {
		cb = _cb;
		gen = _gen;
	}

	
	@Override
	public String toString() {
		return this.getClass().getName() + " " + getRate() + " " + count_cutoffs + " " + count_totals;
	}
	
	
	protected void count_move_cutoff(int depth) {
		count_cutoffs += depth * depth;
	}
	
	
	protected void count_move_total(int count, int depth) {
		count_totals += count * depth * depth;
	}
	
	
	@Override
	public double getRate() {
		return count_cutoffs / (double) count_totals;
	}
	
	
	@Override
	public int compareTo(IMoveGenFragment other) {
		if (getRate() == other.getRate()) {
			return 1;
		}else {
			return (int) (1000 * (other.getRate() - getRate()));
		}
	}
	
	
	@Override
	public boolean isLegal(int move) {
		return cb.isLegal(move);
	}
	
	
	@Override
	public int getSearchedMove() {
		return 0;
	}
}
