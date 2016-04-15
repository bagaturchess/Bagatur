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
package bagaturchess.search.impl.alg.impl5_scratch;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl1.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearchMoveList;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.history.HistoryTable;
import bagaturchess.search.impl.utils.Sorting;


public class ListAll implements ISearchMoveList {
	
	
	private long[] moves; 
	private int size; 
	
	private int cur;
	
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	private int tptMove = 0;
	
	private int prevprevbest;
	private int matemove;
	private int prevPvMove;
	
	protected SearchEnv env;
	
	
	public ListAll(SearchEnv _env) { 
		env = _env;
		moves = new long[256];
	}
	
	protected HistoryTable getHistoryTable() {
		return env.getHistory_all();
	}
	
	public void clear() {
		cur = 0;
		size = 0;
		
		generated = false;
		tptTried = false;
		tptPlied = false;
		tptMove = 0;
		prevprevbest = 0;
		matemove = 0;
		prevPvMove = 0;
	}
	
	
	private boolean isOk(int move) {
		return !MoveInt.isCastling(move) && !MoveInt.isEnpassant(move);
	}
	
	
	public int next() {
		
		if (!tptTried) {
			tptTried = true;
			if (tptMove != 0 && isOk(tptMove) && env.getBitboard().isPossible(tptMove)) {
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

	
	public void genMoves() {
		
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
		
		getHistoryTable().countMove(move);
		
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
			ordval += 10000;
		}
		
		if (MoveInt.isCaptureOrPromotion(move)) {
			int see = env.getBitboard().getSee().evalExchange(move);
			ordval += 2 * BaseEvalWeights.getFigureMaterialSEE(Constants.TYPE_KING);
			ordval += see;
			/*if (see >= 0) {
				ordval += 2 * BaseEvalWeights.getFigureMaterialSEE(Constants.TYPE_KING);
				ordval += see;
			} else {
				ordval += see;
			}*/
			
			//ordval += 2 * BaseEvalWeights.getFigureMaterialSEE(Constants.TYPE_KING);
			//ordval += getMVLScore(move);
		}
		
		if (move == matemove) {
			ordval += 300;
		}
		
		if (move == prevprevbest) {
			ordval += 200;
		}
		
		if (move == prevPvMove) {
			ordval += 100;
		}
		
		/*int[] mateKillers = getHistoryTable().getMateKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<mateKillers.length; i++) {
			if (move == mateKillers[i]) {
				ordval += 100;
				break;
			}
		}
		
		int[] killers = getHistoryTable().getNonCaptureKillers(env.getBitboard().getColourToMove());
		for (int i=0; i<killers.length; i++) {
			if (move == killers[i]) {
				ordval += 100;
				break;
			}
		}*/
		
		if (env.getBitboard().isPasserPush(move)) {
			ordval += 50;
		}
		
		if (getHistoryTable().getCounterMove(env.getBitboard().getLastMove()) == move) {
			ordval += 200;
		} else {
			if (getHistoryTable().getCounterMove2(env.getBitboard().getLastMove()) == move) {
				ordval += 200;
			} else {
				if (getHistoryTable().getCounterMove3(env.getBitboard().getLastMove()) == move) {
					ordval += 200;
				}
			}
		}
		
		
		double historyRating = (getHistoryTable().getScores(move) / (double) getHistoryTable().getMaxRate());
		ordval += 100D * historyRating;
		
		return ordval;
	}
	
	
	private int getMVLScore(int move) {
		
		int gain = env.getBitboard().getMaterialFactor().getMaterialGain(move);
		
		int pieceValue = BaseEvalWeights.getFigureMaterialSEE(MoveInt.getFigureType(move));
		
		return 10 * gain - pieceValue;
	}
	
		
	public void countStatistics(int move) {
	}
	
	
	public void updateStatistics(int bestmove) {
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
	
	
	public void setTptMove(int _tptMove) {
		tptMove = _tptMove;
	}
	
	
	public void setPrevBestMove(int prevBestMove) {
		prevprevbest = prevBestMove;
	}
	
	
	public void setMateMove(int matemove) {
		this.matemove = matemove;
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


	public void setPrevpvMove(int _prevpvMove) {
		prevPvMove = _prevpvMove;
	}

	public boolean hasDominantMove() {
		return false;
	}

	@Override
	public void newSearch() {
		// TODO Auto-generated method stub
		
	}
}
