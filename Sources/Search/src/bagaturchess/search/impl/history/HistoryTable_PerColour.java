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
import bagaturchess.bitboard.impl.movegen.MoveInt;


public class HistoryTable_PerColour {
	
	
	private static final short MAX_SCORES = 1024;
	
	private static final boolean DEBUG = false;
	
	
	private IBinarySemaphore semaphore;
	
	//private int level; //Debug info
	
	private short scores[][];
	private short counts[][];
	
	private int countermoves[][];
	private int countermoves2[][];
	private int countermoves3[][];
	
	private double maxRate;
	
	
	public HistoryTable_PerColour(IBinarySemaphore _semaphore) {
		init();
		
		semaphore = _semaphore;
	}
	
	private void init() {
		scores = new short[64][64];
		counts = new short[64][64];
		countermoves = new int[64][64];
		countermoves2 = new int[64][64];
		countermoves3 = new int[64][64];
		maxRate = 0;
	}
	
	public void counterMove(int oldmove, int move) {
		final int fromFieldID = MoveInt.getFromFieldID(oldmove);
		final int toFieldID = MoveInt.getToFieldID(oldmove);
		if (countermoves[fromFieldID][toFieldID] != 0) {
			if (countermoves2[fromFieldID][toFieldID] != 0) {
				countermoves3[fromFieldID][toFieldID] = countermoves2[fromFieldID][toFieldID];
			}
			countermoves2[fromFieldID][toFieldID] = countermoves[fromFieldID][toFieldID];
		}
		countermoves[fromFieldID][toFieldID] = move;
	}
	
	public int getCounterMove(int oldmove) {
		final int fromFieldID = MoveInt.getFromFieldID(oldmove);
		final int toFieldID = MoveInt.getToFieldID(oldmove);
		return countermoves[fromFieldID][toFieldID];
	}
	
	public int getCounterMove2(int oldmove) {
		//if (true) return 0;
		final int fromFieldID = MoveInt.getFromFieldID(oldmove);
		final int toFieldID = MoveInt.getToFieldID(oldmove);
		return countermoves2[fromFieldID][toFieldID];
	}
	
	public int getCounterMove3(int oldmove) {
		//if (true) return 0;
		final int fromFieldID = MoveInt.getFromFieldID(oldmove);
		final int toFieldID = MoveInt.getToFieldID(oldmove);
		return countermoves3[fromFieldID][toFieldID];
	}
	
	public void countMove(int move) {
		
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);
		
		lock();
		counts[fromFieldID][toFieldID]++;
		
		if (counts[fromFieldID][toFieldID] >= MAX_SCORES) {
			normalize();
		}
		
		if (DEBUG) {
			verify();
		}
		unlock();
	}
	
	public boolean isGoodMove(int move) {
		lock();
		boolean result = getScores(move) >= getMaxRate() / 2;
		unlock();
		return result;
	}
	
	public double getGoodMoveScores(int move) {
		//lock();
		double result = getScores(move) / (double)(getMaxRate() * 1.0);
		//unlock();
		//System.out.println("getGoodMoveScores=" + result);
		if (result < 0) {
			throw new IllegalStateException();
		}
		if (result > 1) {
			result = 1;
		}
		//System.out.println("getGoodMoveScores=" + result);
		return result;
	}
	
	public void goodMove(int move) {
		goodMove(move, 1);
	}
	
	public void goodMove(int move, int point) {
		
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);
		
		lock();
		//short oldVal = scores[fromFieldID][toFieldID];
		int val = 1;//point;//(int) Math.pow(2, depth);
		scores[fromFieldID][toFieldID] += val;
		short newVal = scores[fromFieldID][toFieldID]; 
		
		int count = counts[fromFieldID][toFieldID];
		if (count < newVal) {
			counts[fromFieldID][toFieldID] = newVal;
		}
		
		double curRate = scores[fromFieldID][toFieldID] / (double)counts[fromFieldID][toFieldID];
		if (curRate > maxRate) {
			maxRate = curRate;
		}
		
		if (newVal >= MAX_SCORES) {
			normalize();
		}
		
		if (DEBUG) {
			verify();
		}
		unlock();
	}
	
	
	private int getScores(int move) {
		
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);
		
		lock();
		short count = counts[fromFieldID][toFieldID];
		short score = scores[fromFieldID][toFieldID];
		
		if (count == 0) {
			unlock();
			return 0;
		}
		int rate = (score * (MAX_SCORES - 1)) / count;
		
		if (rate > getMaxRate()) {
			throw new IllegalStateException("rate=" + rate + ", getMaxRate()=" + getMaxRate());
		}
		unlock();
		
		if (rate > MAX_SCORES) {
			throw new IllegalStateException();
		}
		
		return rate;
	}
	
	/*private int getScores(short scores, int move) {
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);

		return getScores(scores, fromFieldID, toFieldID);
	}
	
	private int getScores(short scores, int figID, int to) {
		short count = counts[figID][to];
		return (scores * (MAX_SCORES - 1)) / count;
	}
	
	private int getScores(short scores, short count) {
		return (scores * (MAX_SCORES - 1)) / count;
	}*/
	
	public void clear() {
		lock();
		init();
		unlock();
	}
	
	private void normalize() {
		
		
		
		//System.out.println("Normalize level " + level);
		
		if (DEBUG) {
			System.out.println("Normalize History Table");
		}
		
		//lock();
		if (DEBUG) {
			verify();
		}
		
		maxRate = 0;
		//for (int i=0;i<128; i++) {
		for (int j=0;j<64; j++) {
				for (int k=0;k<64; k++) {
					short score = scores[j][k];
					int count = counts[j][k];
					scores[j][k] = (short) ((score + 1) / 2);
					counts[j][k] = (short) ((count + 1) / 2);
					if (scores[j][k] > counts[j][k]) {
						throw new IllegalStateException("scores=" + scores[j][k] + " counts=" + counts[j][k]);
					}
					
					double curRate = scores[j][k] / (double)counts[j][k];
					if (curRate > maxRate) {
						maxRate = curRate;
					}
				}
		}
		//}
		
		if (DEBUG) {
			verify();
		}
		//unlock();
	}
	
	private void lock() {
		semaphore.lock();
	}
	
	private void unlock() {
		semaphore.unlock();
	}
	
	private void verify() {
		
	}

	public int getMaxRate() {
		int result = (int) (maxRate * MAX_SCORES);
		if (result <= 0) {
			return 1;
		}
		return result;
	}
}
