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
package bagaturchess.search.impl.evalcache;


import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.Util;


public class EvalCache_Impl2 implements IEvalCache {
	
	
	private static final int SCORE 					= 48;
	
	
	private int POWER_2_TT_ENTRIES 					= 23;
	
	private int keyShifts;
	public int maxEntries;

	private long[] keys;
	private long[] values;

	private long usageCounter;

	
	public EvalCache_Impl2(int sizeInMB) {
		
		POWER_2_TT_ENTRIES = (int) (Math.log(sizeInMB) / Math.log(2) + 16);
		
		keyShifts = 64 - POWER_2_TT_ENTRIES;
		maxEntries = (int) Util.POWER_LOOKUP[POWER_2_TT_ENTRIES] + 3;
		
		keys = new long[maxEntries];
		values = new long[maxEntries];
		
		usageCounter = 0;
	}
	
	
	@Override
	public void get(long key, IEvalEntry entry) {
		
		entry.setIsEmpty(true);
		
		long value = getValue(key);
		if (value != 0) {
			entry.setIsEmpty(false);
			entry.setEval(getScore(value));
			entry.setLevel((byte)5);
		}
	}
	
	
	@Override
	public void put(long hashkey, int level, double eval) {
		addValue(hashkey, (int) eval);
	}
	
	
	@Override
	public int getHitRate() {
		return (int) (usageCounter * 1000 / maxEntries);
	}
	
	
	private int getIndex(final long key) {
		return (int) (key >>> keyShifts);
	}
	
	
	private int getScore(final long value) {
		int score = (int) (value >> SCORE);

		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}

		return score;
	}
	
	
	private long getValue(final long key) {

		final int index = getIndex(key);

		for (int i = 0; i < 4; i++) {
			long value = values[index + i];
			if ((keys[index + i] ^ value) == key) {
				return value;
			}
		}
		
		return 0;
	}
	

	private void addValue(final long key, int score) {

		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
			Assert.isTrue(score != ChessConstants.SCORE_NOT_RUNNING);
		}

		final int index = getIndex(key);
		//int replacedDepth = Integer.MAX_VALUE;
		int replacedIndex = index;
		for (int i = index; i < index + 4; i++) {

			if (keys[i] == 0) {
				replacedIndex = i;
				usageCounter++;
				break;
			}

			long currentValue = values[i];
			//int currentDepth = getDepth(currentValue);
			if ((keys[i] ^ currentValue) == key) {
				replacedIndex = i;
				break;
			}

			// replace the lowest depth
			//if (currentDepth < replacedDepth) {
				replacedIndex = i;
				//replacedDepth = currentDepth;
			//}
		}
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}

		final long value = createValue(score);
		keys[replacedIndex] = key ^ value;
		values[replacedIndex] = value;
	}
	
	
	private long createValue(final long score) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}
		return score << SCORE;
	}
}
