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
import bagaturchess.uci.api.ChannelManager;


public class TTable_Impl2 implements ITTable {


	private static final int CLUSTER_SIZE = 4;
	private static final int ENTRY_LONGS  = 2;
	private static final long VALID_MASK  = 1L << 8;

	private static final int FLAG_SHIFT   = 9;
	private static final int MOVE_SHIFT   = 11; // Move is 22 bits (bits 11-32)
	private static final int GEN_SHIFT    = 34; // Generation is 8 bits (bits 34-41)
	private static final int SCORE_SHIFT  = 42; // Score is 22 bits (bits 42-63)


	private final int maxEntries;

	// Interleaved layout: [mixedKey0, value0, mixedKey1, value1, ...]
	// mixedKey is hashKey ^ value, so a torn/inconsistent read is very unlikely to pass the key check.
	private final long[] table;

	// Incremented by correctAllDepths(); 8-bit wrap-around acts as generation counter.
	private byte generation = 0;

	private long counter_usage;
	private long counter_tries;
	private long counter_hits;


	public TTable_Impl2(long sizeInBytes) {
	    long maxEntriesLong = Long.highestOneBit(sizeInBytes / (ENTRY_LONGS * Long.BYTES));
	    if (maxEntriesLong < CLUSTER_SIZE) {
	        throw new IllegalArgumentException("Insufficient memory allocated.");
	    }

	    final long maxArrayEntries = Long.highestOneBit(Integer.MAX_VALUE / ENTRY_LONGS);
	    if (maxEntriesLong > maxArrayEntries) {
	        maxEntriesLong = maxArrayEntries;
	    }

	    this.maxEntries = (int) maxEntriesLong;
	    this.table = new long[maxEntries * ENTRY_LONGS];

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

	// O(1): bump the generation counter instead of iterating the whole table.
	// Entries from previous generations have lower effective quality and are replaced first.
	@Override
	public void correctAllDepths(int reduction) {
		generation += (byte) reduction;
	}

	@Override
	public void get(long key, ITTEntry entry) {
		counter_tries++;
		entry.setIsEmpty(true);

		final long[] localTable = table;
		int pos = getIndex(key) * ENTRY_LONGS;
		for (int i = 0; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {
			long storedValue = localTable[pos + 1];
			if (storedValue == 0) {
				continue;
			}
			long storedKey = localTable[pos];
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
	public final void put(long hashkey, int depth, int eval, int alpha, int beta, int bestmove) {

		// 22-bit signed score range: [-2097152, 2097151]
		if (eval > 2097151 || eval < -2097152) {

			return;
		}

		int flag = ITTEntry.FLAG_EXACT;

		if (eval >= beta) {

			flag = ITTEntry.FLAG_LOWER;

		} else if (eval <= alpha) {

			flag = ITTEntry.FLAG_UPPER;
		}

		addValue(hashkey, eval, depth, flag, bestmove);
	}

	private final void addValue(final long new_key, final int new_score, final int new_depth, final int new_flag, final int new_move) {

	    final long[] localTable = table;
	    final int start_pos = getIndex(new_key) * ENTRY_LONGS;

	    int replace_pos     = -1;
	    int replace_quality = Integer.MAX_VALUE;

	    for (int i = 0, pos = start_pos; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {

	        long stored_value = localTable[pos + 1];

	        if (stored_value == 0) {
	            replace_pos = pos;
	            counter_usage++;
	            break;
	        }

	        long stored_key = localTable[pos];

	        if ((stored_key ^ stored_value) == new_key) {
	            // Same key: replace if new entry is not much shallower, or is an exact bound.
	            int stored_depth = getDepth(stored_value);
	            if (new_depth >= stored_depth - 4 || new_flag == ITTEntry.FLAG_EXACT) {
	                replace_pos = pos;
	            } else {
	                return; // Existing same-key entry is deeper and more precise - keep it.
	            }
	            break;
	        }

	        // Different key: track the lowest-quality candidate for eviction.
	        // Quality = depth minus a penalty for how many generations old the entry is.
	        int gen_diff = (generation - getGeneration(stored_value)) & 0xFF;
	        int quality  = getDepth(stored_value) - 4 * gen_diff;
	        if (replace_pos == -1 || quality < replace_quality) {
	            replace_quality = quality;
	            replace_pos = pos;
	        }
	    }

	    if (replace_pos == -1) {
	        return;
	    }

	    // Preserve the best move from the displaced same-key entry when the new entry has none.
	    int best_move = new_move;
	    if (best_move == 0) {
	        long stored_value = localTable[replace_pos + 1];
	        if (stored_value != 0 && (localTable[replace_pos] ^ stored_value) == new_key) {
	            best_move = getMove(stored_value);
	        }
	    }

	    final long new_value = createValue(new_score, best_move, new_flag, new_depth, generation);
	    localTable[replace_pos]     = new_key ^ new_value;
	    localTable[replace_pos + 1] = new_value;
	}

	private int getIndex(long key) {
		return ((int) (key ^ (key >>> 32)) & (maxEntries - CLUSTER_SIZE));
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

	private static int getGeneration(long value) {
		return (int) ((value >>> GEN_SHIFT) & 0xFF);
	}

	private static long createValue(int score, int move, int flag, int depth, byte gen) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0 && depth <= 255);
		}
		return VALID_MASK
		    | ((long) score << SCORE_SHIFT)
		    | ((long) (gen & 0xFF) << GEN_SHIFT)
		    | ((long) move << MOVE_SHIFT)
		    | ((long) flag << FLAG_SHIFT)
		    | depth;
	}
}
