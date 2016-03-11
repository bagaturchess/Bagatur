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
package bagaturchess.bitboard.impl.movelist;

public class BaseMoveList implements IMoveList {
	
	private int[] moves;
	private int count;
	private int cur = 0;
	
	public BaseMoveList(int max) {
		moves = new int[max];
	}
	
	
	public BaseMoveList() {
		this(100);
	}
	
	public void reserved_clear() {
		count = 0;
	}
	
	public final void reserved_add(int move) {
		//System.out.println(MoveInt.moveToString(move));
		moves[count++] = move;
		
		/*if (board != null && !board.isPossible(move)) {
			board.isPossible(move);
			throw new IllegalStateException();
		}*/
		
		/*if (MoveInt.getFigurePID(move) > 7) {
			//throw new IllegalStateException();
		}
		
		if (MoveInt.getCapturedFigurePID(move) > 7) {
			//throw new IllegalStateException();
		}*/
	}
	
	public final void reserved_removeLast() {
		count--;
	}
	
	public final int reserved_getCurrentSize() {
		return count;
	}
	
	public final int[] reserved_getMovesBuffer() {
		return moves;
	}

	public void clear() {
		reserved_clear();
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
}
