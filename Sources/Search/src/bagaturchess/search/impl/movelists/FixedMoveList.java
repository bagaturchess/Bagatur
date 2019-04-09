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
package bagaturchess.search.impl.movelists;

import bagaturchess.search.api.internal.ISearchMoveList;

public class FixedMoveList implements ISearchMoveList {
	
	private int[] moves;
	private int count;
	private int cur = 0;
	
	public FixedMoveList() {
		
	}
	
	public void setMoves(int[] _moves) {
		moves = _moves;
		count = moves.length;
	}
	
	public void reserved_clear() {
		throw new UnsupportedOperationException();
	}
	
	public final void reserved_add(int move) {
		throw new UnsupportedOperationException();
	}
	
	public final void reserved_removeLast() {
		throw new UnsupportedOperationException();
	}
	
	public final int reserved_getCurrentSize() {
		throw new UnsupportedOperationException();
	}
	
	public final int[] reserved_getMovesBuffer() {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		cur = 0;
	}

	public int next() {
		if (cur < count) {
			return moves[cur++];
		} else {
			return 0;
		}
	}

	public int size() {
		return count;
	}

	public void setPrevBestMove(int move) {
		// TODO Auto-generated method stub
		
	}

	public void setPrevpvMove(int move) {
		// TODO Auto-generated method stub
		
	}

	public void setTptMove(int move) {
		// TODO Auto-generated method stub
		
	}

	public void countSuccess(int bestmove) {
		throw new UnsupportedOperationException();
	}

	public void countTotal(int move) {
		// TODO Auto-generated method stub
		
	}

	public void newSearch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMateMove(int mateMove) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		cur = 0;
	}
}
