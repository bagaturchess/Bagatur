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
package bagaturchess.search.impl.alg.impl7_old_reimpl;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl1.movegen.MoveInt;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.alg.iter.OrderingStatistics;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.HistoryTable;
import bagaturchess.search.impl.utils.Sorting;



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
	
	
	private static final int ORD_VAL_SHIFT           =  1;//HistoryTable.MAX_SCORES + 200;
	
	
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
	private boolean tptTried;
	private boolean tptPlied;
	
	private int tptMove = 0;
	private int prevBestMove = 0;
	private int prevPvMove = 0;
	private int mateMove = 0;
	
	private boolean hasDominantMove;
	
	private SearchEnv env;
	
	private OrderingStatistics orderingStatistics;
	
	
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
		
		tptTried = false;
		tptPlied = false;
		
		tptMove = 0;
		prevBestMove = 0;
		prevPvMove = 0;
		mateMove = 0;
		hasDominantMove = false;
	}
	
	private boolean isOk(int move) {
		return !MoveInt.isCastling(move) && !MoveInt.isEnpassant(move);
	}
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) /*&& env.getBitboard().isPossible(tptMove)*/) {
				tptPlied = true;
				return tptMove;
			}
		}
		
		if (!generated) {
			genMoves();							
		}
		
		if (cur < size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(moves, 1, size);
				if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(1, size, moves);
			}
			int move = (int) moves[cur++];
			return move;
		} else {
			return 0;
		}
	}

	public int getOrdVal(int bestmove) {
		for (int i=0; i<size; i++) {
			if ((int)moves[i] == bestmove) {
				return MoveInt.getOrderingValue(moves[i]);
			}
		}
		return 0;
	}
	
	public void genMoves() {
		
		//if (env.getBitboard().isInCheck()) {
		//	throw new IllegalStateException();
		//}
		
		int[] ob_moves = null;
		if (env.getOpeningBook() != null) {
			ob_moves = env.getOpeningBook().getAllMoves(env.getBitboard().getHashKey(), env.getBitboard().getColourToMove());	
			if (ob_moves != null && ob_moves.length == 0) {
				ob_moves = null;
			}
		}
		
		if (ob_moves != null) {
			for (int i=0; i<ob_moves.length; i++) {
				reserved_add(ob_moves[i]);
			}
		} else {
			env.getBitboard().genAllMoves(this);
		}
		
		generated = true;
	}
	
	public int size() {
		return size;
	}
	
	public void reserved_add(int move) {
		
		env.getHistory_all().countMove(move);
		
		if (!env.getSearchConfig().sortMoveLists()) {
			add(move);
		}
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
		}
		
		int ordval = genOrdVal(move);
		
		long move_ord = MoveInt.addOrderingValue(move, ordval);
		
		add(move_ord);
	}

	 
	 private int genOrdVal(int move) {
		
		int ordval = 0;
		
		if (move == tptMove) {
			hasDominantMove = true;
			ordval += ORD_VAL_SHIFT * ORD_VAL_TPT_MOVE * orderingStatistics.getOrdVal_TPT();
			orderingStatistics.tpt_count++;
		}
		
		if (move == prevPvMove) {
			hasDominantMove = true;
			ordval += ORD_VAL_SHIFT * ORD_VAL_PREVPV_MOVE * orderingStatistics.getOrdVal_PREVPV();
			orderingStatistics.prevpv_count++;
		}
		
		if (prevBestMove != 0 && MoveInt.getColour(move) != MoveInt.getColour(prevBestMove)) {
			throw new IllegalStateException();
		}
		
		if (move == prevBestMove) {
			hasDominantMove = true;
			ordval += ORD_VAL_SHIFT * ORD_VAL_PREV_BEST_MOVE * orderingStatistics.getOrdVal_PREVBEST();
			orderingStatistics.prevbest_count++;
		}
		
		if (mateMove != 0 && MoveInt.getColour(move) != MoveInt.getColour(mateMove)) {
			throw new IllegalStateException();
		}
		
		if (move == mateMove) {
			hasDominantMove = true;
			ordval += ORD_VAL_SHIFT * ORD_VAL_MATE_MOVE * orderingStatistics.getOrdVal_MATEMOVE();
			orderingStatistics.matemove_count++;
		}
		
		int[] mateKillers = env.getHistory_all().getMateKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<mateKillers.length; i++) {
			if (move == mateKillers[i]) {
				hasDominantMove = true;
				ordval += ORD_VAL_SHIFT * ORD_VAL_MATE_KILLER * orderingStatistics.getOrdVal_MATEKILLER();
				orderingStatistics.matekiller_count++;
				break;
			}
		}
		
		int[] killers = env.getHistory_all().getNonCaptureKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<killers.length; i++) {
			if (move == killers[i]) {
				hasDominantMove = true;
				ordval += ORD_VAL_SHIFT * ORD_VAL_KILLER * orderingStatistics.getOrdVal_KILLER();
				orderingStatistics.killer_count++;
				break;
			}
		}
		
		
		if (env.getBitboard().isPasserPush(move)) {
			ordval += ORD_VAL_SHIFT * ORD_VAL_PASSER_PUSH * orderingStatistics.getOrdVal_PASSER();
			orderingStatistics.passer_count++;
		}
		
		if (MoveInt.isCastling(move)) {
			ordval += ORD_VAL_SHIFT * ORD_VAL_CASTLING * orderingStatistics.getOrdVal_CASTLING();
			orderingStatistics.castling_count++;
		}
		
		if (MoveInt.isCaptureOrPromotion(move)) {
			
			/*
			TODO: Add it to the method wich calculate order val
			int gain = 0;
			if (MoveInt.isCapture(move)) {
				gain += BaseEvalWeights.getFigureMaterialSEE(MoveInt.getCapturedFigureType(move));
			}
			if (MoveInt.isPromotion(move)) {
				gain += BaseEvalWeights.getFigureMaterialSEE(MoveInt.getPromotionFigureType(move));
			}
			int piece = BaseEvalWeights.getFigureMaterialSEE(MoveInt.getFigureType(move));
			
			int see = 10 * gain - piece;*/
			
			int see = env.getBitboard().getSee().evalExchange(move);
			if (see > 0) {
				hasDominantMove = true;
			}
			
			if (see > 0) {
				ordval += ORD_VAL_SHIFT * ORD_VAL_WIN_CAP * orderingStatistics.getOrdVal_WINCAP() + see;
				orderingStatistics.wincap_count++;
			} else if (see == 0) {
				ordval += ORD_VAL_SHIFT * ORD_VAL_EQ_CAP * orderingStatistics.getOrdVal_EQCAP();
				orderingStatistics.eqcap_count++;
			} else {
				ordval += ORD_VAL_SHIFT * ORD_VAL_LOSE_CAP + see;
				orderingStatistics.losecap_count++;
			}
		}
		
		if (env.getHistory_all().getCounterMove(env.getBitboard().getLastMove()) == move) {
			ordval += ORD_VAL_SHIFT * ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
			orderingStatistics.counter_count++;
		} else {
			if (env.getHistory_all().getCounterMove2(env.getBitboard().getLastMove()) == move) {
				ordval += ORD_VAL_SHIFT * ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
				orderingStatistics.counter_count++;
			} else {
				if (env.getHistory_all().getCounterMove3(env.getBitboard().getLastMove()) == move) {
					ordval += ORD_VAL_SHIFT * ORD_VAL_COUNTER * orderingStatistics.getOrdVal_COUNTER();
					orderingStatistics.counter_count++;
				}
			}
		}
		
		ordval += ORD_VAL_SHIFT * (env.getHistory_all().getScores(move) / (double) env.getHistory_all().getMaxRate())
					* orderingStatistics.getOrdVal_HISTORY();
		
		ordval += ORD_VAL_SHIFT * env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(move) * orderingStatistics.getOrdVal_PST();
		
		return ordval;
	}
	
	 
	static int count = 0;
	public void countStatistics(int move) {
		if (move == 0) {
			return;
		}
		
		count++;
		if (count % 100000 == 0) {
			//System.out.println(orderingStatistics);
		}
		
		genOrdVal(move);
	}
	
	public void updateStatistics(int bestmove) {
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
		
		int[] mateKillers = env.getHistory_all().getMateKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<mateKillers.length; i++) {
			if (bestmove == mateKillers[i]) {
				orderingStatistics.matekiller_best++;
				break;
			}
		}
		
		int[] killers = env.getHistory_all().getNonCaptureKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<killers.length; i++) {
			if (bestmove == killers[i]) {
				orderingStatistics.killer_best++;
				break;
			}
		}
		
		
		if (env.getBitboard().isPasserPush(bestmove)) {
			orderingStatistics.passer_best++;
		}
		
		if (MoveInt.isCastling(bestmove)) {
			orderingStatistics.castling_best++;
		}
		
		if (MoveInt.isCaptureOrPromotion(bestmove)) {
			
			int see = env.getBitboard().getSee().evalExchange(bestmove);
			if (see > 0) {
				hasDominantMove = true;
			}
			
			if (see > 0) {
				orderingStatistics.wincap_best++;
			} else if (see == 0) {
				orderingStatistics.eqcap_best++;
			} else {
				orderingStatistics.losecap_best++;
			}
		}
		
		if (env.getHistory_all().getCounterMove(env.getBitboard().getLastMove()) == bestmove) {
			orderingStatistics.counter_best++;
		} else {
			if (env.getHistory_all().getCounterMove2(env.getBitboard().getLastMove()) == bestmove) {
				orderingStatistics.counter_best++;
			} else {
				if (env.getHistory_all().getCounterMove3(env.getBitboard().getLastMove()) == bestmove) {
					orderingStatistics.counter_best++;
				}
			}
		}
		
		orderingStatistics.history_best += (env.getHistory_all().getScores(bestmove) / (double) env.getHistory_all().getMaxRate());
		orderingStatistics.history_count += 1;
		
		orderingStatistics.pst_best += env.getBitboard().getBaseEvaluation().getPSTMoveGoodPercent(bestmove);
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

	public boolean hasDominantMove() {
		return hasDominantMove;
	}
}
