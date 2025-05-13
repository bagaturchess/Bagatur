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
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;


public class ListAll implements ISearchMoveList {
	
	private int ply;
	
	private long[] moves; 
	private int size;
	private int cur;
	
	private boolean generated;
	private boolean revalue;
	
	private boolean tptTried;
	private boolean tptPlied;
	private int tptMove = 0;
	
	private int prevBestMove = 0;
	private int prevPvMove = 0;
	private int mateMove = 0;
	
	private SearchEnv env;
	
	private boolean reuse_moves = false;
	
	
	public ListAll(SearchEnv _env, int _ply) { 
		env = _env;
		moves = new long[256];
		ply = _ply;
	}
	
	public void clear() {
		cur = 0;
		size = 0;
		
		generated = false;
		revalue 	= false;
		
		tptTried = false;
		tptPlied = false;
		
		tptMove = 0;
		prevBestMove = 0;
		prevPvMove = 0;
		mateMove = 0;
	}
	
	@Override
	public String toString() {
		String msg = "";
		
		//msg += orderingStatistics.toString();
		
		return msg;
	}
	
	private boolean isOk(int move) {
		return !env.getBitboard().getMoveOps().isCastling(move) && !env.getBitboard().getMoveOps().isEnpassant(move);
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) && env.getBitboard().isPossible(tptMove)) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		if (revalue) {
			
			for (int i = 0; i < size; i++) {
				int move = (int) moves[i]; 
				
				if (!env.getBitboard().isPossible(move)) {
					throw new IllegalStateException();
				}
				
				long ordval = genOrdVal(move);
				moves[i] = MoveInt.addOrderingValue(move, ordval);
				
				//Move best move on top
				if (moves[i] > moves[0]) {
					long best_move = moves[i];
					moves[i] = moves[0];
					moves[0] = best_move;
				}
			}
			
			revalue = false;
			
		} else if (!generated) {
			genMoves();							
		}
		
		if (cur < size) {
			
			//int SORT_INDEX = 1;
			//int SORT_INDEX = 2;
			int SORT_INDEX = 3;
			//int SORT_INDEX = (int) Math.max(1, Math.sqrt(size) / 2);
			
			if (SORT_INDEX <= 0) {
				throw new IllegalStateException();
			}
			
			if (cur == 0) {
				//Already sorted in reserved_add
			} else if (cur == SORT_INDEX) {
				//if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, cur, size);
				if (env.getSearchConfig().sortMoveLists()) Utils.bubbleSort(cur, size, moves);
			} else if (cur < SORT_INDEX) {
				for (int i = cur; i < size; i++) {					
					//Move best move on top
					if (moves[i] > moves[cur]) {
						long best_move = moves[i];
						moves[i] = moves[cur];
						moves[cur] = best_move;
					}
				}
			}
			
			int move = (int) moves[cur++];
			
			return move;
			
		} else {
			
			return 0;
		}
	}
	
	
	public void genMoves() {
		
		/*if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}*/
		
		boolean gen = true;
		
		if (env.getOpeningBook() != null) {
			
			IOpeningEntry entry = env.getOpeningBook().getEntry(env.getBitboard().getHashKey(), env.getBitboard().getColourToMove());
			if (entry != null && entry.getWeight() >= OpeningBook.OPENING_BOOK_MIN_MOVES) {
				
				int[] ob_moves = entry.getMoves();
				int[] ob_counts = entry.getCounts();
				
				for (int i=0; i<ob_moves.length; i++) {
					
					//if (env.getSearchConfig().getOpenningBook_Mode() == ISearchConfig_AB.OPENNING_BOOK_MODE_POWER2) {
						//Most played first strategy - use ord val
						long move_ord = MoveInt.addOrderingValue(ob_moves[i], ob_counts == null ? 1 : ob_counts[i]);
						add(move_ord);
						
					//} else {
					//	//Random move - in addition randomize naturally without using ordval scores
					//	add(ob_moves[i]);
					//}
				}
				
				
				gen = false;
			}
		}
		
		if (gen) {
			env.getBitboard().genAllMoves(this);
		}
		
		generated = true;
	}
	
	public int size() {
		return size;
	}
	
	public void reserved_add(int move) {
		
		if (!env.getSearchConfig().sortMoveLists()) {
			add(move);
		}
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
		}
		
		long ordval = genOrdVal(move);
		
		long move_ord = MoveInt.addOrderingValue(move, ordval);
		
		add(move_ord);
	}
	
	
	private static long counter = 0;
	
	
	private long genOrdVal(int move) {
		
		counter++;
		
		if (counter % 1000000 == 0) {
			//System.out.println(orderingStatistics);
		}
		
		
		int killer1Move = env.getHistory_All().getKiller1(env.getBitboard().getColourToMove(), ply);
		int killer2Move = env.getHistory_All().getKiller2(env.getBitboard().getColourToMove(), ply);
		int counterMove1 = env.getHistory_All().getCounter1(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		int counterMove2 = env.getHistory_All().getCounter2(env.getBitboard().getColourToMove(), env.getBitboard().getLastMove());
		
		
		long ordval = 100000 * 100;
		
		if (tptMove == move) {
			
			ordval += 20000 * 100;
		
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
		
		if (prevBestMove == move) {
			
			ordval += 1000 * 100;
		
		}
		
		if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			
			ordval += env.getHistory_All().getScores(env.getBitboard().getColourToMove(), move);
			
		} else {
			
			if (env.getBitboard().getSEEScore(move) >= 0) {
			
				ordval += 7000 * 100 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
				
			} else {
				
				ordval += -5000 + 100 * (MoveUtil.getAttackedPieceIndex(move) * 6 - MoveUtil.getSourcePieceIndex(move));
			}
		}
		
		
		return ordval;
	}
	
	
	private void add(long move) {	
		if (size == 0) {
			moves[0] = move;
		} else {
			if (move > moves[0]) {
				moves[size] = moves[0];
				moves[0] = move;
			} else {
				moves[size] = move;
			}
		}
		size++;
	}
	
	
	public boolean isGoodMove(int move) {
		
		if (move == tptMove) {
			return true;
		}
		
		if (move == prevPvMove) {
			return true;
		}
		
		if (move == prevBestMove) {
			return true;
		}
		
		if (move == mateMove) {
			return true;
		}
		
		/*if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			return true;
		}
		
		if( env.getHistory_All().getScores(move) >= 0.5 ) {
			return true;
		}*/
		
		return false;
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
	
	public void setMateMove(int mateMove) {
		this.mateMove = mateMove;
	}
	
	public void setTptMove(int tptMove) {
		this.tptMove = tptMove;
	}
	
	public void setPrevpvMove(int prevpvMove) {
		this.prevPvMove = prevpvMove;
	}
	
	@Override
	public void newSearch() {
	}

	@Override
	public void reset() {
		
		if (reuse_moves) {
			
			cur = 0;
			
			revalue = true;
			
			tptTried = false;
			tptPlied = false;
			
		} else {
			clear();
		}
	}

	@Override
	public void countSuccess(int bestmove) {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public void countTotal(int move) {
		
		throw new UnsupportedOperationException();
	}
}
