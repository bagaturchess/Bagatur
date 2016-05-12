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
import bagaturchess.search.impl.utils.Sorting;


public class ListCapsProm implements ISearchMoveList {
	
	
	private long[] caps; 
	private int caps_size; 

	private int cur;
	private boolean generated;
	private boolean tptTried;
	private boolean tptPlied;
	private int tptMove = 0;
	
	private SearchEnv env;
	
	
	public ListCapsProm(SearchEnv _env) { 
		env = _env;
		caps = new long[62];
	}
	
	
	public void clear() {
		caps_size = 0;
		cur = 0;
		
		generated = false;
		tptTried = false;
		tptPlied = false;
		tptMove = 0;
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
			env.getBitboard().genCapturePromotionMoves(this);
			generated = true;							
		}
		
		if (cur < caps_size) {
			if (cur == 1) {
				if (env.getSearchConfig().randomizeMoveLists()) Utils.randomize(caps, 1, caps_size);
				if (env.getSearchConfig().sortMoveLists()) Sorting.bubbleSort(1, caps_size, caps);
			}
			return (int) caps[cur++];
		} else {
			return 0;
		}
	}

	public int size() {
		return caps_size;
	}
	
	public void reserved_add(int move) {
		
		env.getHistory_all().countMove(move);
		
		if (move == tptMove) {
			if (tptPlied) {
				return;
			}
		}
		
		if (MoveInt.isCaptureOrPromotion(move)) {
			
			int ordval = 0;
			
			if (move == tptMove) {
				ordval += 10000;
			}
			
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
			
			double historyRating = env.getHistory_all().getGoodMoveScores(move);
			if (historyRating < 0 || historyRating > 1) {
				throw new IllegalStateException();
			}
			ordval += 100D * historyRating;
			
			
			long move_ord = MoveInt.addOrderingValue(move, ordval);
			add(move_ord);
			
		} else {
			throw new IllegalStateException();
		}
	}
	
	
	private int getMVLScore(int move) {
		
		int gain = env.getBitboard().getMaterialFactor().getMaterialGain(move);
		
		int pieceValue = BaseEvalWeights.getFigureMaterialSEE(MoveInt.getFigureType(move));
		
		return 10 * gain - pieceValue;
	}
	
	
	private void add(long move) {
		
		if (caps_size == 0) {
			caps[caps_size++] = move;
		} else {
			if (move > caps[0]) {
				caps[caps_size++] = caps[0];
				caps[0] = move;
			} else {
				caps[caps_size++] = move;
			}
		}
	}
	
	
	public void setTptMove(int tptMove) {
		this.tptMove = tptMove;
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


	public void setPrevBestMove(int move) {
		throw new UnsupportedOperationException();
	}

	public void setPrevpvMove(int move) {
		throw new UnsupportedOperationException();
	}
	
	public void updateStatistics(int bestmove) {
		
	}

	public void countStatistics(int move) {
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
