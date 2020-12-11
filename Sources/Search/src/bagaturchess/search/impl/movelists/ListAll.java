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


public class ListAll implements ISearchMoveList {
	
	
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
	
	
	/*private static final int ORD_VAL_TPT_MOVE        = 10000;
	private static final int ORD_VAL_WIN_CAP         =  7000;
	private static final int ORD_VAL_EQ_CAP          =  6000;
	private static final int ORD_VAL_PREV_BEST_MOVE  =  5000;
	private static final int ORD_VAL_PASSER_PUSH 	 =  4000;
	private static final int ORD_VAL_KILLER          =  3000;
	private static final int ORD_VAL_PREVPV_MOVE     =  2000;
	private static final int ORD_VAL_CASTLING 	 	 =  1000;
	private static final int ORD_VAL_MATE_KILLER     =  0;
	private static final int ORD_VAL_COUNTER         =  0;
	private static final int ORD_VAL_LOSE_CAP        =  -2000;*/
	
	
	/* results after EPD with 300
	TPT        :	3401018	3399999	0.9997003838262544
	WINCAP     :	12984646	7094787	0.5463981844402998
	EQCAP      :	1968000	787004	0.39990040650406505
	COUNTER    :	5433269	4278731	0.7875058275229885
	MATEKILLER :	3307481	1110691	0.3358117552300376
	PREVBEST   :	3224678	1307687	0.40552483069627415
	MATEMOVE   :	396442	374522	0.9447081792544685
	KILLER     :	27081099	2727510	0.10071637048407821
	PASSER     :	6821955	1089638	0.15972518141793665
	PREVPV     :	2362549	492377	0.20840922241189494
	CASTLING   :	266115	46222	0.17369182496289198
	LOSECAP    :	10593708	847645	0.9199859954607018
	HISTORY    :	14665489	3057408	0.20847639972392426
	PST        :	14665489	2241164	0.15281896839549955
	*/
	
	
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
	
	private OrderingStatistics orderingStatistics;
	
	private boolean reuse_moves = false;
	
	
	public ListAll(SearchEnv _env, OrderingStatistics _orderingStatistics) { 
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
		
		msg += orderingStatistics.toString();
		
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
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, cur, size);
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
		
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		boolean gen = true;
		
		if (env.getOpeningBook() != null) {
			
			IOpeningEntry entry = env.getOpeningBook().getEntry(env.getBitboard().getHashKey(), env.getBitboard().getColourToMove());
			if (entry != null && entry.getWeight() >= OpeningBook.OPENNING_BOOK_MIN_MOVES) {
				
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
		
		long ordval = 10000;
		
		
		if (move == tptMove) {
			ordval += ORD_VAL_TPT_MOVE * orderingStatistics.getOrdVal_TPT();
		}
		
		
		if (move == prevPvMove) {
			ordval += ORD_VAL_PREVPV_MOVE * orderingStatistics.getOrdVal_PREVPV();
		}
		
		
		if (move == prevBestMove) {
			ordval += ORD_VAL_PREV_BEST_MOVE * orderingStatistics.getOrdVal_PREVBEST();
		}
		
		
		if (move == mateMove) {
			ordval += ORD_VAL_MATE_MOVE * orderingStatistics.getOrdVal_MATEMOVE();
		}
		
		
		if (env.getBitboard().getMoveOps().isCastling(move)) {
			ordval += ORD_VAL_CASTLING * orderingStatistics.getOrdVal_CASTLING();
		}
		
		
		if (env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			
			int see = env.getBitboard().getSEEScore(move);
			
			if (see > 0) {
				ordval += ORD_VAL_WIN_CAP * orderingStatistics.getOrdVal_WINCAP() + see;
			} else if (see == 0) {
				ordval += ORD_VAL_EQ_CAP * orderingStatistics.getOrdVal_EQCAP();
			} else {
				ordval += ORD_VAL_LOSE_CAP * orderingStatistics.getOrdVal_LOSECAP() + see / 100;
			}
		}
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			ordval += ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
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
		
		/*if (env.getBitboard().isPasserPush(move)) {
			orderingStatistics.passer_count++;
		}*/
		
		if (env.getBitboard().getMoveOps().isCastling(move)) {
			orderingStatistics.castling_count++;
		}
		
		if (env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
			
			int see = env.getBitboard().getSEEScore(move);
			
			if (see > 0) {
				orderingStatistics.wincap_count++;
			} else if (see == 0) {
				orderingStatistics.eqcap_count++;
			} else {
				orderingStatistics.losecap_count++;
			}
		}
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			orderingStatistics.counter_count++;
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
		
		/*if (env.getBitboard().isPasserPush(bestmove)) {
			orderingStatistics.passer_best++;
		}*/
		
		if (env.getBitboard().getMoveOps().isCastling(bestmove)) {
			orderingStatistics.castling_best++;
		}
		
		if (env.getBitboard().getMoveOps().isCaptureOrPromotion(bestmove)) {
			
			int see = env.getBitboard().getSEEScore(bestmove);
			
			if (see > 0) {
				orderingStatistics.wincap_best++;
			} else if (see == 0) {
				orderingStatistics.eqcap_best++;
			} else {
				orderingStatistics.losecap_best++;
			}
		}
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), bestmove)) {
			orderingStatistics.counter_best++;
		}
		
		orderingStatistics.history_best += env.getHistory_All().getScores(bestmove);
		orderingStatistics.history_count += 1;
		
		//orderingStatistics.pst_best += env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(bestmove);
		//orderingStatistics.pst_count += 1;
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
		
		if (env.getHistory_All().isCounterMove(env.getBitboard().getLastMove(), move)) {
			return true;
		}
		
		if( env.getHistory_All().getScores(move) >= 0.5 ) {
			return true;
		}
		
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
}
