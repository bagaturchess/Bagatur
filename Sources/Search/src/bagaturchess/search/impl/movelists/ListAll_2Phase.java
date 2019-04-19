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
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.utils.Sorting;


public class ListAll_2Phase implements ISearchMoveList {
	
	
	private final int ORD_VAL_TPT_MOVE;
	private final int ORD_VAL_MATE_MOVE;
	private final int ORD_VAL_WIN_CAP;
	private final int ORD_VAL_EQ_CAP;
	private final int ORD_VAL_COUNTER;
	private final int ORD_VAL_PREV_BEST_MOVE;
	private final int ORD_VAL_CASTLING;
	private final int ORD_VAL_MATE_KILLER;
	private final int ORD_VAL_PASSER_PUSH;
	private final int ORD_VAL_KILLER;
	private final int ORD_VAL_PREVPV_MOVE;
	private final int ORD_VAL_LOSE_CAP;
	
	
	private long[] moves; 
	private int size;
	private int cur;
	
	private boolean generated_caps;
	private boolean generated_noncaps;
	
	private boolean tptTried;
	private boolean tptPlied;
	private int tptMove = 0;
	
	private int prevBestMove = 0;
	private int prevPvMove = 0;
	private int mateMove = 0;
	
	private boolean counterTried;
	private boolean counterPlied;
	private int counterMove = 0;
	
	
	private SearchEnv env;
	
	private OrderingStatistics orderingStatistics;
	
	
	public ListAll_2Phase(SearchEnv _env, OrderingStatistics _orderingStatistics) { 
		env = _env;
		moves = new long[256];
		orderingStatistics = _orderingStatistics;
		
		ORD_VAL_TPT_MOVE        = env.getSearchConfig().getOrderingWeight_TPT_MOVE();
		ORD_VAL_MATE_MOVE       = env.getSearchConfig().getOrderingWeight_MATE_MOVE();
		ORD_VAL_WIN_CAP         = env.getSearchConfig().getOrderingWeight_WIN_CAP();
		ORD_VAL_EQ_CAP          = env.getSearchConfig().getOrderingWeight_EQ_CAP();
		ORD_VAL_COUNTER         = env.getSearchConfig().getOrderingWeight_COUNTER();
		ORD_VAL_PREV_BEST_MOVE  = env.getSearchConfig().getOrderingWeight_PREV_BEST_MOVE();
		ORD_VAL_CASTLING 	 	= env.getSearchConfig().getOrderingWeight_CASTLING();
		ORD_VAL_MATE_KILLER     = env.getSearchConfig().getOrderingWeight_MATE_KILLER();
		ORD_VAL_PASSER_PUSH 	= env.getSearchConfig().getOrderingWeight_PASSER_PUSH();
		ORD_VAL_KILLER          = env.getSearchConfig().getOrderingWeight_KILLER();
		ORD_VAL_PREVPV_MOVE     = env.getSearchConfig().getOrderingWeight_PREVPV_MOVE();
		ORD_VAL_LOSE_CAP        = env.getSearchConfig().getOrderingWeight_LOSE_CAP();
	}
	
	public void clear() {
		cur = 0;
		size = 0;
		
		generated_caps = false;
		generated_noncaps = false;
		
		tptTried = false;
		tptPlied = false;
		
		counterTried = false;
		counterPlied = false;
		
		counterMove = 0;
		tptMove = 0;
		prevBestMove = 0;
		prevPvMove = 0;
		mateMove = 0;
	}
	
	@Override
	public String toString() {
		String msg = "";
		
		msg += orderingStatistics.toString();
		
		return msg;
	}
	
	private boolean isOk(int move) {
		return !env.getBitboard().isCastlingMove(move) && !env.getBitboard().isEnpassantMove(move);
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) && env.getBitboard().isPossible(tptMove)) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		/*if (!counterTried && env.getBitboard().getLastMove() != 0) {
			counterTried = true;
			counterMove = env.getHistory_All().getCounterMove1(env.getBitboard().getLastMove());
			if (counterMove != 0 && isOk(counterMove) && env.getBitboard().isPossible(counterMove)) {
				counterPlied = true;
				return counterMove;
			} else {
				counterMove = env.getHistory_All().getCounterMove2(env.getBitboard().getLastMove());
				if (counterMove != 0 && isOk(counterMove) && env.getBitboard().isPossible(counterMove)) {
					counterPlied = true;
					return counterMove;
				} else {
					counterMove = env.getHistory_All().getCounterMove3(env.getBitboard().getLastMove());
					if (counterMove != 0 && isOk(counterMove) && env.getBitboard().isPossible(counterMove)) {
						counterPlied = true;
						return counterMove;
					}
				}
			}
		}*/
		
		if (!generated_caps) {
			genMoves_caps();
			if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(cur, size, moves);
		}
		
		if (!generated_noncaps && cur >= size) {
			genMoves_noncaps();
			if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(cur, size, moves);
		}
		
		if (cur < size) {
			
			int move = (int) moves[cur++];
			
			return move;
			
		} else {
			return 0;	
		}
	}
	
	
	public void genMoves_caps() {
		
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		env.getBitboard().genCapturePromotionMoves(this);
		
		generated_caps = true;
	}
	
	public void genMoves_noncaps() {
		
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		env.getBitboard().genNonCaptureNonPromotionMoves(this);
		
		generated_noncaps = true;
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
		
		if (move == counterMove) {
			if (counterPlied) {
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
		
		long ordval = 0;
		
		if (move == tptMove) {
			ordval += ORD_VAL_TPT_MOVE * orderingStatistics.getOrdVal_TPT();
		}
		
		if (move == prevPvMove) {
			ordval += ORD_VAL_PREVPV_MOVE * orderingStatistics.getOrdVal_PREVPV();
		}
		
		/*if (prevBestMove != 0 && MoveInt.getColour(move) != MoveInt.getColour(prevBestMove)) {
			throw new IllegalStateException();
		}*/
		
		if (move == prevBestMove) {
			ordval += ORD_VAL_PREV_BEST_MOVE * orderingStatistics.getOrdVal_PREVBEST();
		}
		
		/*if (mateMove != 0 && MoveInt.getColour(move) != MoveInt.getColour(mateMove)) {
			throw new IllegalStateException();
		}*/
		
		if (move == mateMove) {
			ordval += ORD_VAL_MATE_MOVE * orderingStatistics.getOrdVal_MATEMOVE();
		}
		
		if (env.getBitboard().isPasserPush(move)) {
			ordval += ORD_VAL_PASSER_PUSH * orderingStatistics.getOrdVal_PASSER();
		}
		
		if (env.getBitboard().isCastlingMove(move)) {
			ordval += ORD_VAL_CASTLING * orderingStatistics.getOrdVal_CASTLING();
		}
		
		if (env.getBitboard().isCaptureOrPromotionMove(move)) {
			
			int see = env.getBitboard().getSEEScore(move);
			
			if (see > 0) {
				ordval += ORD_VAL_WIN_CAP * orderingStatistics.getOrdVal_WINCAP() + see;
			} else if (see == 0) {
				ordval += ORD_VAL_EQ_CAP * orderingStatistics.getOrdVal_EQCAP();
			} else {
				ordval += ORD_VAL_LOSE_CAP * orderingStatistics.getOrdVal_LOSECAP() /*+ see*/;
			}
		}
		
		if (env.getHistory_All().getCounterMove1(env.getBitboard().getLastMove()) == move) {
			ordval += ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
		} else {
			if (env.getHistory_All().getCounterMove2(env.getBitboard().getLastMove()) == move) {
				ordval += ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
			} else {
				if (env.getHistory_All().getCounterMove3(env.getBitboard().getLastMove()) == move) {
					ordval += ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
				}
			}
		}
		
		ordval += env.getHistory_All().getScores(move) * orderingStatistics.getOrdVal_HISTORY();
		
		//ordval += env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(move) * orderingStatistics.getOrdVal_PST();
		
		
		return ordval;
	}
	
	
	public void countTotal(int move) {
		
		if (move == tptMove) {
			orderingStatistics.tpt_count++;
		}
		
		if (move == prevPvMove) {
			orderingStatistics.prevpv_count++;
		}
		
		if (move == prevBestMove) {
			orderingStatistics.prevbest_count++;
		}
		
		if (move == mateMove) {
			orderingStatistics.matemove_count++;
		}
				
		if (env.getBitboard().isPasserPush(move)) {
			orderingStatistics.passer_count++;
		}
		
		if (env.getBitboard().isCastlingMove(move)) {
			orderingStatistics.castling_count++;
		}
		
		if (env.getBitboard().isCaptureOrPromotionMove(move)) {
			
			int see = env.getBitboard().getSEEScore(move);
			
			if (see > 0) {
				orderingStatistics.wincap_count++;
			} else if (see == 0) {
				orderingStatistics.eqcap_count++;
			} else {
				orderingStatistics.losecap_count++;
			}
		}
		
		if (env.getHistory_All().getCounterMove1(env.getBitboard().getLastMove()) == move) {
			orderingStatistics.counter_count++;
		} else {
			if (env.getHistory_All().getCounterMove2(env.getBitboard().getLastMove()) == move) {
				orderingStatistics.counter_count++;
			} else {
				if (env.getHistory_All().getCounterMove3(env.getBitboard().getLastMove()) == move) {
					orderingStatistics.counter_count++;
				}
			}
		}
	}
	
	
	public void countSuccess(int bestmove) {
		if (bestmove == 0) {
			return;
		}
		
		if (bestmove == tptMove) {
			orderingStatistics.tpt_best++;
		}
		
		if (bestmove == prevPvMove) {
			orderingStatistics.prevpv_best++;
		}
		
		if (bestmove == prevBestMove) {
			orderingStatistics.prevbest_best++;
		}
		
		if (bestmove == mateMove) {
			orderingStatistics.matemove_best++;
		}
		
		if (env.getBitboard().isPasserPush(bestmove)) {
			orderingStatistics.passer_best++;
		}
		
		if (env.getBitboard().isCastlingMove(bestmove)) {
			orderingStatistics.castling_best++;
		}
		
		if (env.getBitboard().isCaptureOrPromotionMove(bestmove)) {
			
			int see = env.getBitboard().getSEEScore(bestmove);
			
			if (see > 0) {
				orderingStatistics.wincap_best++;
			} else if (see == 0) {
				orderingStatistics.eqcap_best++;
			} else {
				orderingStatistics.losecap_best++;
			}
		}
		
		if (env.getHistory_All().getCounterMove1(env.getBitboard().getLastMove()) == bestmove) {
			orderingStatistics.counter_best++;
		} else {
			if (env.getHistory_All().getCounterMove2(env.getBitboard().getLastMove()) == bestmove) {
				orderingStatistics.counter_best++;
			} else {
				if (env.getHistory_All().getCounterMove3(env.getBitboard().getLastMove()) == bestmove) {
					orderingStatistics.counter_best++;
				}
			}
		}
		
		orderingStatistics.history_best += env.getHistory_All().getScores(bestmove);
		orderingStatistics.history_count += 1;
		
		//orderingStatistics.pst_best += env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(bestmove);
		orderingStatistics.pst_count += 1;
	}
	
	private void add(long move) {	
		if (size == 0) {
			moves[size] = move;
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
		
		if (env.getHistory_All().getCounterMove1(env.getBitboard().getLastMove()) == move) {
			return true;
		} else {
			if (env.getHistory_All().getCounterMove2(env.getBitboard().getLastMove()) == move) {
				return true;
			} else {
				if (env.getHistory_All().getCounterMove3(env.getBitboard().getLastMove()) == move) {
					return true;
				}
			}
		}

		/*
		if( env.getHistory().getScores(move) >= 0.5 ) {
			return true;
		}
		*/
		
		/*
		if( env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(move) >= 0.5 ) {
			return true;
		}
		*/
		
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
		clear();
	}
}
