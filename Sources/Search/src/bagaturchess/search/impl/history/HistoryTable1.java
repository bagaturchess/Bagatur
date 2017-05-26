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

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;


public class HistoryTable1 implements IHistoryTable {
	
	
	private int[][] success;
	private int[][] failures;
	
	private int[][] counters1;
	private int[][] counters2;
	private int[][] counters3;
	
	
	public HistoryTable1() {
		success 	= new int[Constants.PID_MAX][64];
		failures 	= new int[Constants.PID_MAX][64];
		
		counters1 	= new int[Constants.PID_MAX][64];
		counters2 	= new int[Constants.PID_MAX][64];
		counters3 	= new int[Constants.PID_MAX][64];
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
		
		int pid = MoveInt.getFigurePID(move);
		int to = MoveInt.getToFieldID(move);
		
		failures[pid][to] += depth * depth;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#countSuccess(int, int)
	 */
	@Override
	public void countSuccess(int move, int depth) {
		
		int pid = MoveInt.getFigurePID(move);
		int to = MoveInt.getToFieldID(move);
		
		success[pid][to] += depth * depth;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getScores(int)
	 */
	@Override
	public double getScores(int move) {
		
		int pid = MoveInt.getFigurePID(move);
		int to = MoveInt.getToFieldID(move);
		
		int success_scores 	= success[pid][to];
		int failures_scores = failures[pid][to];
		
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
		
		int pid = MoveInt.getFigurePID(last_move);
		int to = MoveInt.getToFieldID(last_move);
		
		counters3[pid][to] = counters2[pid][to];
		counters2[pid][to] = counters1[pid][to];
		counters1[pid][to] = counter_move;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove1(int)
	 */
	@Override
	public int getCounterMove1(int last_move) {
		
		int pid = MoveInt.getFigurePID(last_move);
		int to = MoveInt.getToFieldID(last_move);
		
		return counters1[pid][to];
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove2(int)
	 */
	@Override
	public int getCounterMove2(int last_move) {
		
		int pid = MoveInt.getFigurePID(last_move);
		int to = MoveInt.getToFieldID(last_move);
		
		return counters2[pid][to];
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.history.IHistoryTable#getCounterMove3(int)
	 */
	@Override
	public int getCounterMove3(int last_move) {
		
		int pid = MoveInt.getFigurePID(last_move);
		int to = MoveInt.getToFieldID(last_move);
		
		return counters3[pid][to];
	}
}
