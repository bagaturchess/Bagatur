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


import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.uci.api.ChannelManager;


public class TTable_Impl2 implements ITTable {
	
	
	private static final int FLAG = 12;
	private static final int MOVE = 14;
	private static final int SCORE = 48;
	

	private long[] keys;
	
	private long[] values;
	
	private long counter_usage;
	
	private long counter_tries;
	
	private long counter_hits;
	
	
	public TTable_Impl2(long size_in_bytes) {
		
		if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("TTable_Impl2: bytes_count=" + size_in_bytes);
		
		long maxEntries = size_in_bytes / 16; //two longs per entry and one long has 8 bytes: 2 * 8 = 16
		
		if (maxEntries > 1073741823) { //1073741823 = 2^30 - 1 (should work on 32 and 64 bits), 2147483647 = 2^31 - 1 (should work on 64 bits only)
			maxEntries = 1073741823;
			if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("TTable_Impl2: limited to " + 1073741823 + " entries.");
		}
		
		if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("TTable_Impl2: maxEntries=" + maxEntries);
		
		keys = new long[(int) maxEntries];
		
		values = new long[(int) maxEntries];
	}


	@Override
	public int getUsage() {
		return (int) (counter_usage * 100 / keys.length);
	}
	
	
	@Override
	public int getHitRate() {
		if (counter_tries == 0) return 0;
		return (int) (counter_hits * 100 / counter_tries);
	}
	
	
	@Override
	public void correctAllDepths(final int reduction) {
	}
	
	
	@Override
	public void get(long key, ITTEntry entry) {
		
		counter_tries++;
		
		if (counter_tries % 100000000 == 0) {
			if (ChannelManager.getChannel() != null) ChannelManager.getChannel().dump("TTable_Impl2.get: TableID=" + this.hashCode() + ", HitRate=" + getHitRate() + "%, Usage=" + getUsage() + "%");
		}
		
		long value = getTTValue(key);
		
		if (entry.isEmpty()) {
			
			if (value != 0) {
				
				entry.setIsEmpty(false);
				
				entry.setDepth(getDepth(value));
				entry.setFlag(getFlag(value));
				entry.setEval(getScore(value));
				entry.setBestMove(getMove(value));
			}
			
		} else {
			
			if (value != 0) {
				
				if (getDepth(value) > entry.getDepth()) {
					
					//entry.setIsEmpty(false);
					entry.setDepth(getDepth(value));
					entry.setFlag(getFlag(value));
					entry.setEval(getScore(value));
					entry.setBestMove(getMove(value));
				}
			}
		}
	}
	
	
	@Override
	public void put(long hashkey, int depth, int eval, int alpha, int beta, int bestmove) {
		int flag = ITTEntry.FLAG_EXACT;
		if (eval >= beta) {
			flag = ITTEntry.FLAG_LOWER;
		} else if (eval <= alpha) {
			flag = ITTEntry.FLAG_UPPER;
		}
		addValue(hashkey, eval, depth, flag, bestmove);
	}
	
	
	private long getTTValue(final long key) {

		final int index = getIndex(key);

		for (int i = 0; i < 4; i++) {
			
			
			long stored_key 	= keys[index + i];
			
			long value 			= values[index + i];
			
			
			if ((stored_key ^ value) == key) {
				
				counter_hits++;
				
				return value;
			}
		}
		
		return 0;
	}
	
	
	private int getIndex(final long key) {
		
		long index = (int) (key ^ (key >>> 32));
		
		if (index < 0) {
			
			index = -index;
		}
		
		index = index % (keys.length - 3);
		
		index = 4 * (index / 4);
		
		return (int) index;
	}
	
	
	private void addValue(final long key, int score, final int depth, final int flag, final int move) {

		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
			//Assert.isTrue(move != 0);
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
			//Assert.isTrue(MoveUtil.getSourcePieceIndex(move) != 0);
		}

		final int index = getIndex(key);
		int replacedDepth = Integer.MAX_VALUE;
		int replacedIndex = index;
		for (int i = index; i < index + 4; i++) {

			if (keys[i] == 0) {
				replacedIndex = i;
				counter_usage++;
				break;
			}

			long currentValue = values[i];
			
			int currentDepth = getDepth(currentValue);
			
			if ((keys[i] ^ currentValue) == key) {
				
				if (currentDepth > depth /*&& flag != ITTEntry.FLAG_EXACT*/) {
					
					continue;
				}
				
				replacedIndex = i;
				
				break;
			}
			
			// replace the lowest depth
			if (currentDepth < replacedDepth) {
				
				replacedDepth = currentDepth;
				
				replacedIndex = i;
			}
		}
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}

		final long value = createValue(score, move, flag, depth);
		keys[replacedIndex] = key ^ value;
		values[replacedIndex] = value;
	}
	
	
	private static int getScore(final long value) {
		
		int score = (int) (value >> SCORE);

		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}

		return score;
	}
	
	
	private static int getDepth(final long value) {
		return (int) (value & 0xff);
	}
	
	
	private static int getFlag(final long value) {
		return (int) (value >>> FLAG & 3);
	}
	
	
	private static int getMove(final long value) {
		return (int) (value >>> MOVE & 0x3fffff);
	}
	
	
	// SCORE,HALF_MOVE_COUNTER,MOVE,FLAG,DEPTH
	private static long createValue(final long score, final long move, final long flag, final long depth) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
			Assert.isTrue(depth <= 255);
		}
		return score << SCORE | move << MOVE | flag << FLAG | depth;
	}
}
