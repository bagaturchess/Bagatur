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
package bagaturchess.search.impl.history;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;


public class HistoryTable {
	
	static final short MIN_VALUE = 0;
	
	public static final short MAX_SCORES = 1024;
	
	
	private boolean ONE_TABLE = true;
	
	private HistoryTable_PerColour history_w;
	private HistoryTable_PerColour history_b;
	
	
	//private Killers nonCaptureKillers;
	//private Killers mateKillers;
	
	
	public HistoryTable(IBinarySemaphore _semaphore) {
		
		init();
		
	}
	
	private void init() {
		
		//nonCaptureKillers = new Killers(10);
		//mateKillers = new Killers(10);
		
		history_w = new HistoryTable_PerColour(new BinarySemaphore_Dummy());
		if (!ONE_TABLE) {
			history_b = new HistoryTable_PerColour(new BinarySemaphore_Dummy());
		}
	}
	
	private HistoryTable_PerColour getHistoryTable_PerColour(int colour) {
		
		if (ONE_TABLE) {
			return history_w;
		}
		
		if (colour == Constants.COLOUR_WHITE) {
			return history_w;
		} else {
			return history_b;
		}
	}
	
	public void counterMove(int oldmove, int move) {
		getHistoryTable_PerColour(MoveInt.getColour(move)).counterMove(oldmove, move);
	}
	
	
	public int getCounterMove(int oldmove) {
		if (oldmove == 0) {
			return 0;
		}
		return getHistoryTable_PerColour(MoveInt.getOpponentColour(oldmove)).getCounterMove(oldmove);
	}
	
	public int getCounterMove2(int oldmove) {
		if (oldmove == 0) {
			return 0;
		}
		return getHistoryTable_PerColour(MoveInt.getOpponentColour(oldmove)).getCounterMove2(oldmove);
	}
	
	public int getCounterMove3(int oldmove) {
		if (oldmove == 0) {
			return 0;
		}
		return getHistoryTable_PerColour(MoveInt.getOpponentColour(oldmove)).getCounterMove3(oldmove);
	}
	
	public void countMove(int move) {
		getHistoryTable_PerColour(MoveInt.getColour(move)).countMove(move);
	}
	
	public boolean isGoodMove(int move) {
		return getHistoryTable_PerColour(MoveInt.getColour(move)).isGoodMove(move);
	}
	
	public double getGoodMoveScores(int move) {
		return getHistoryTable_PerColour(MoveInt.getColour(move)).getGoodMoveScores(move);
	}
	
	public void goodMove(int move) {
		goodMove(move, 1, false);
	}
	
	public void goodMove(int move, int point, boolean isMate) {
		getHistoryTable_PerColour(MoveInt.getColour(move)).goodMove(move, point, isMate);
	}
	
	public int getScores(int move) {
		return getHistoryTable_PerColour(MoveInt.getColour(move)).getScores(move);
	}
	
	public void clear() {
		init();
	}
	
	/*
	public int[] getNonCaptureKillers(int colour) {
		int[] result =  nonCaptureKillers.getKillers(colour);
		return result;
	}
	
	public int[] getMateKillers(int colour) {
		int[] result =  mateKillers.getKillers(colour);
		return result;
	}
	 */

	public int getMaxRate(int move) {
		return getHistoryTable_PerColour(MoveInt.getColour(move)).getMaxRate();
	}
}
