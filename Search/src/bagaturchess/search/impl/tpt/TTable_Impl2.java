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
package bagaturchess.search.impl.tpt;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.uci.api.ChannelManager;


public class TTable_Impl2 implements ITTable {

	private static final int FLAG_SHIFT = 9;
	private static final int MOVE_SHIFT = 11;
	private static final int SCORE_SHIFT = 45;

	private final ByteBuffer keys;
	private final ByteBuffer values;

	private long counter_usage;
	private long counter_tries;
	private long counter_hits;

	private final int maxEntries;
	
	
	public TTable_Impl2(long sizeInBytes) {
	    long maxEntriesLong = Long.highestOneBit(sizeInBytes / 16);
	    if (maxEntriesLong < 4) {
	        throw new IllegalArgumentException("Insufficient memory allocated.");
	    }

	    long byteSize = maxEntriesLong * 8;
	    if (byteSize > Integer.MAX_VALUE) {
	        maxEntriesLong = Integer.MAX_VALUE / 8;
	        byteSize = maxEntriesLong * 8L;
	    }

	    this.maxEntries = (int) maxEntriesLong;

	    keys = ByteBuffer.allocateDirect((int) byteSize).order(ByteOrder.nativeOrder());
	    values = ByteBuffer.allocateDirect((int) byteSize).order(ByteOrder.nativeOrder());

	    if (ChannelManager.getChannel() != null) {
	        ChannelManager.getChannel().dump("TTable_Impl2 initialized with " + maxEntries + " entries.");
	    }
	}

	@Override
	public int getUsage() {
		return (int) (counter_usage * 100 / maxEntries);
	}

	@Override
	public int getHitRate() {
		return counter_tries == 0 ? 0 : (int) (counter_hits * 100 / counter_tries);
	}

	@Override
	public void correctAllDepths(int reduction) {
		// No implementation needed
	}

	@Override
	public void get(long key, ITTEntry entry) {
		counter_tries++;
		entry.setIsEmpty(true);

		int index = getIndex(key);
		for (int i = 0; i < 4; i++) {
			long storedKey = keys.getLong((index + i) * 8);
			long storedValue = values.getLong((index + i) * 8);
			if ((storedKey ^ storedValue) == key) {
				counter_hits++;
				entry.setIsEmpty(false);
				entry.setDepth(getDepth(storedValue));
				entry.setFlag(getFlag(storedValue));
				entry.setEval(getScore(storedValue));
				entry.setBestMove(getMove(storedValue));
				return;
			}
		}
	}

	@Override
	public void put(long hashKey, int depth, int eval, int alpha, int beta, int bestMove) {
		int flag = eval >= beta ? ITTEntry.FLAG_LOWER : eval <= alpha ? ITTEntry.FLAG_UPPER : ITTEntry.FLAG_EXACT;
		long newValue = createValue(eval, bestMove, flag, depth);
		int index = getIndex(hashKey);

		int replaceIndex = index;
		int minDepth = Integer.MAX_VALUE;

		for (int i = 0; i < 4; i++) {
			int currIdx = index + i;
			long storedKey = keys.getLong(currIdx * 8);
			long storedValue = values.getLong(currIdx * 8);
			
			if (storedKey == 0 || (storedKey ^ storedValue) == hashKey) {
				replaceIndex = currIdx;
				break;
			}

			int storedDepth = getDepth(storedValue);
			if (storedDepth < minDepth) {
				minDepth = storedDepth;
				replaceIndex = currIdx;
			}
		}

		keys.putLong(replaceIndex * 8, hashKey ^ newValue);
		values.putLong(replaceIndex * 8, newValue);
		counter_usage++;
	}

	private int getIndex(long key) {
		return ((int) (key ^ (key >>> 32)) & (maxEntries - 4));
	}

	private static int getScore(long value) {
		return (int) (value >> SCORE_SHIFT);
	}

	private static int getDepth(long value) {
		return (int) (value & 0xFF);
	}

	private static int getFlag(long value) {
		return (int) ((value >>> FLAG_SHIFT) & 0x3);
	}

	private static int getMove(long value) {
		return (int) ((value >>> MOVE_SHIFT) & 0x3FFFFF);
	}

	private static long createValue(int score, int move, int flag, int depth) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0 && depth <= 255);
		}
		return ((long) score << SCORE_SHIFT) | ((long) move << MOVE_SHIFT) | ((long) flag << FLAG_SHIFT) | depth;
	}
}
