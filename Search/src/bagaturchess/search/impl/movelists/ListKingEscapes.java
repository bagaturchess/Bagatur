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


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public class ListKingEscapes implements ISearchMoveList {
	
	private int ply;
	
	private long[] escapes; 
	private int escapes_size; 
	
	private int cur;
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	
	private int tptMove = 0;
	private int prevBestMove = 0;
	
	private SearchEnv env;
	
	
	public ListKingEscapes(SearchEnv _env, int _ply) { 
		env = _env;
		escapes = new long[62];
		
		ply = _ply;
	}
	
	public void clear() {
		escapes_size = 0;
		cur = 0;
		generated = false;
		tptTried = false;
		tptPlied = false;
		tptMove = 0;
		prevBestMove = 0;
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && env.getBitboard().isPossible(tptMove)) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		if (!generated) {
			/*if (!env.getBitboard().isInCheck()) {
				throw new IllegalStateException();
			}*/
			env.getBitboard().genKingEscapes(this);
			generated = true;							
		}
		
		if (cur < escapes_size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(escapes, 1, escapes_size);
				if (env.getSearchConfig().sortMoveLists()) Utils.bubbleSort(1, escapes_size, escapes);
			}
			return (int) escapes[cur++];
		} else {
			return 0;
		}
	}
	
	public int size() {
		return escapes_size;
	}
	
	public void reserved_add(int move) {
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
		}
		
		int killer1Move = env.getHistory_All().getKiller1(env.getBitboard().getColourToMove(), ply);
		int killer2Move = env.getHistory_All().getKiller2(env.getBitboard().getColourToMove(), ply);
		int counterMove1 = env.getHistory_All().getCounter1(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		int counterMove2 = env.getHistory_All().getCounter2(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		
		
		long ordval = 100000 * 100;
		
		if (tptMove == move) {
			
			ordval += 20000 * 100;
		
		}
		
		if (prevBestMove == move) {
			
			ordval += 5000 * 100;
		
		}
		
		if (killer1Move == move) {
			
			ordval += 5000 * 100;
			
		}
		
		if (killer2Move == move) {
			
			ordval += 4000 * 100;
			
		}
		
		if (counterMove1 == move) {
			
			ordval += 3000 * 100;
			
		}
		
		if (counterMove2 == move) {
			
			ordval += 2000 * 100;
			
		}
		
		if (MoveUtil.isQuiet(move)) {
			
			ordval += env.getHistory_All().getScores(env.getBitboard().getColourToMove(), move);
			
		} else {
			
			if (env.getBitboard().getSEEScore(move) >= 0) {
			
				ordval += 7000 * 100 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
				
			} else {
				
				ordval += -5000 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
			}
		}
		
		
		long move_ord = MoveInt.addOrderingValue(move, ordval);
		
		add(move_ord);
	}
	
	private void add(long move) {	
		if (escapes_size == 0) {
			escapes[escapes_size++] = move;
		} else {
			if (move > escapes[0]) {
				escapes[escapes_size++] = escapes[0];
				escapes[0] = move;
			} else {
				escapes[escapes_size++] = move;
			}
		}
	}
	
	/**
	 * Unsupported operations 
	 */
	
	public void reserved_clear() {
		throw new IllegalStateException();
	}
	
	public int reserved_getCurrentSize() {
		throw new IllegalStateException();
	}

	public int[] reserved_getMovesBuffer() {
		throw new IllegalStateException();
	}

	public void reserved_removeLast() {
		throw new IllegalStateException();
	}
	
	public void setPrevBestMove(int prevBestMove) {
		this.prevBestMove = prevBestMove;
	}

	public void setTptMove(int tptMove) {
		this.tptMove = tptMove;
	}

	public void setPrevpvMove(int move) {
		throw new UnsupportedOperationException();
	}
	
	public void countSuccess(int bestmove) {
	}

	public void countTotal(int move) {
	}

	public void newSearch() {
	}

	@Override
	public void setMateMove(int mateMove) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void reset() {
		cur = 0;
	}
}
