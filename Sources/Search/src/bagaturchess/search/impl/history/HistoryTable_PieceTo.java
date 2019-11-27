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
package bagaturchess.search.impl.history;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.Constants;


public class HistoryTable_PieceTo implements IHistoryTable {
	
	
	private long[][] success;
	private long[][] failures;

	private static final int MAX_COUNTERS = 5;
	private int[][][] counters;
	private IBoard board;
	
	
	public HistoryTable_PieceTo(IBoard _board) {
		board = _board;
		success 	= new long[Constants.PID_MAX][64];
		failures 	= new long[Constants.PID_MAX][64];
		counters 	= new int[Constants.PID_MAX][64][MAX_COUNTERS];
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#newSearch()
	 */
	@Override
	public void newSearch() {
		for (int i = 0; i < success.length; i++) {
			for (int j = 0; j < success[i].length; j++) {
				success[i][j] /= 2;
			}
		}
		for (int i = 0; i < failures.length; i++) {
			for (int j = 0; j < failures[i].length; j++) {
				failures[i][j] /= 2;
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#countFailure(int)
	 */
	@Override
	public void countFailure(int move, int depth) {
		
		int pid = board.getMoveOps().getFigurePID(move);
		int to = board.getMoveOps().getToFieldID(move);
		
		failures[pid][to] += depth * depth;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#countSuccess(int, int)
	 */
	@Override
	public void countSuccess(int move, int depth) {
		
		int pid = board.getMoveOps().getFigurePID(move);
		int to = board.getMoveOps().getToFieldID(move);
		
		success[pid][to] += depth * depth;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getScores(int)
	 */
	@Override
	public double getScores(int move) {
		
		int pid = board.getMoveOps().getFigurePID(move);
		int to = board.getMoveOps().getToFieldID(move);
		
		long success_scores  = success[pid][to];
		long failures_scores = failures[pid][to];
		
		if (success_scores + failures_scores > 0) {
			return success_scores / (double)(success_scores + failures_scores);
		} else {
			return 0;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#addCounterMove(int, int)
	 */
	@Override
	public void addCounterMove(int last_move, int counter_move) {
		
		if (board.getMoveOps().isCaptureOrPromotion(counter_move)) {
			return;
		}
		
		if (last_move == 0) {
			return;
		}
		
		int pid = board.getMoveOps().getFigurePID(last_move);
		int to = board.getMoveOps().getToFieldID(last_move);
		
		if (!isCounterMove(last_move, counter_move)) {
			int[] counter_moves = counters[pid][to];
			for (int i = counter_moves.length - 1; i>=1; i--) {
				counter_moves[i] = counter_moves[i - 1];
			}
			counter_moves[0] = counter_move;
		}
	}
	
	
	@Override
	public boolean isCounterMove(int last_move, int move) {
		
		if (last_move == 0) {
			return false;
		}
		
		int pid = board.getMoveOps().getFigurePID(last_move);
		int to = board.getMoveOps().getToFieldID(last_move);
		
		int[] counter_moves = counters[pid][to];
				
		for (int i=0; i<counter_moves.length; i++) {
			if (counter_moves[i] == move) {
				return true;
			}
		}
		
		return false;
	}
}
