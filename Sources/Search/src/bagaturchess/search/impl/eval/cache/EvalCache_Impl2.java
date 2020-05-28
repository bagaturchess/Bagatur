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
package bagaturchess.search.impl.eval.cache;


import bagaturchess.bitboard.impl1.internal.Util;


public class EvalCache_Impl2 implements IEvalCache {
	
	private int POWER_2_ENTRIES;
	
	private int keyShifts;
	public int maxEntries;
	
	private long[] keys;
	
	private long counter_tries;
	private long counter_hits;
	
	
	public EvalCache_Impl2(int sizeInMB) {
		
		POWER_2_ENTRIES = (int) (Math.log(sizeInMB) / Math.log(2) + 16);
		
		keyShifts = 64 - POWER_2_ENTRIES;
		maxEntries = (int) Util.POWER_LOOKUP[POWER_2_ENTRIES];
		
		keys = new long[2 * maxEntries];
	}
	
	
	@Override
	public void get(long key, IEvalEntry entry) {
		
		counter_tries++;
		
		entry.setIsEmpty(true);
		
		int value = getValue(key);
		if (value != 0) {
			entry.setIsEmpty(false);
			entry.setEval(value);
			entry.setLevel((byte)5);
		}
	}
	
	
	@Override
	public void put(long hashkey, int level, double eval) {
		addValue(hashkey, (int) eval);
	}
	
	
	@Override
	public int getHitRate() {
		return (int) (counter_hits * 100 / counter_tries);
	}
	
	
	private int getIndex(final long key) {
		return (int) (key >>> keyShifts);
	}
	
	
	private int getValue(final long key) {
		final int index = getIndex(key);
		final long storedKey = keys[index];
		final long score = keys[index + 1];
		if (storedKey == keys[index]) {//Optimistic read locking
			if ((storedKey ^ score) == key) {
				counter_hits++;
				return (int) score;
			}
		}
		return 0;
	}
	

	private void addValue(final long key, final int score) {
		final int index = getIndex(key);
		keys[index] = key ^ score;
		keys[index + 1] = score;
	}
}
