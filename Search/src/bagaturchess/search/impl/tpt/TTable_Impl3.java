/*
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
package bagaturchess.search.impl.tpt;


import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.uci.api.ChannelManager;


/**
 * Two-bound transposition table optimised for MTD(f). Each cluster slot stores
 * a fail-high (LOWER) bound AND a fail-low (UPPER) bound independently so that
 * successive zero-window probes from opposite sides can both return early
 * cutoffs without forcing one another out of the table.
 *
 * Slot layout: three longs - [mixedKey, lowerValue, upperValue].
 *
 * mixedKey = hashKey ^ (lowerValue + upperValue), so a torn / inconsistent
 * read across the three longs is very unlikely to pass the key check.
 *
 * lowerValue / upperValue pack {depth, bestMove, generation, score} in the
 * same bit layout as TTable_Impl2 (the flag field is unused because the bound
 * direction is implicit in which long holds the data). A zero value means
 * "no bound stored on this side".
 */
public class TTable_Impl3 implements ITTable {


	private static final int CLUSTER_SIZE = 4;
	private static final int ENTRY_LONGS  = 3;     // mixedKey + lowerValue + upperValue
	private static final long VALID_MASK  = 1L << 8;

	// Same bit layout as Impl2 so the helper accessors are identical. The
	// flag field at bits 9-10 is unused (always 0).
	private static final int MOVE_SHIFT   = 11;    // Move is 22 bits (bits 11-32)
	private static final int GEN_SHIFT    = 34;    // Generation is 8 bits (bits 34-41)
	private static final int SCORE_SHIFT  = 42;    // Score is 22 bits (bits 42-63, signed)

	private static final int MIN_SCORE = -2097152;
	private static final int MAX_SCORE = 2097151;


	private final int maxEntries;

	// Flat array of clusters: [mixedKey0, lower0, upper0, mixedKey1, lower1, upper1, ...]
	private final long[] table;

	// 8-bit wraparound generation counter; bumped by correctAllDepths between iterations.
	private byte generation = 0;

	private long counter_usage;
	private long counter_tries;
	private long counter_hits;


	public TTable_Impl3(long sizeInBytes) {
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
			ChannelManager.getChannel().dump("TTable_Impl3 initialized with " + maxEntries + " entries.");
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
		generation += (byte) reduction;
	}


	@Override
	public void get(long key, ITTEntry entry) {

		counter_tries++;
		entry.setIsEmpty(true);

		final long[] localTable = table;
		int pos = getIndex(key) * ENTRY_LONGS;

		for (int i = 0; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {

			final long lowerValue = localTable[pos + 1];
			final long upperValue = localTable[pos + 2];

			if (lowerValue == 0L && upperValue == 0L) {
				continue;
			}

			final long storedKey = localTable[pos];
			if ((storedKey ^ (lowerValue + upperValue)) != key) {
				continue;
			}

			counter_hits++;
			entry.setIsEmpty(false);

			// Populate the two-bound API directly.
			final boolean haveLower = (lowerValue != 0L);
			final boolean haveUpper = (upperValue != 0L);

			if (haveLower) {
				entry.setLowerBound(true, getScore(lowerValue), getDepth(lowerValue), getMove(lowerValue));
			} else {
				entry.setLowerBound(false, 0, 0, 0);
			}

			if (haveUpper) {
				entry.setUpperBound(true, getScore(upperValue), getDepth(upperValue), getMove(upperValue));
			} else {
				entry.setUpperBound(false, 0, 0, 0);
			}

			// Populate the legacy single-bound view so existing search code that
			// still reads getFlag()/getEval()/getDepth()/getBestMove() keeps
			// working: prefer the deeper bound; fall back to either if depths
			// are equal. When both bounds are stored, expose them as EXACT so
			// callers that already special-case EXACT pick up the tighter info.
			if (haveLower && haveUpper) {
				if (getDepth(lowerValue) >= getDepth(upperValue)) {
					entry.setDepth(getDepth(lowerValue));
					entry.setEval(getScore(lowerValue));
					entry.setBestMove(getMove(lowerValue));
				} else {
					entry.setDepth(getDepth(upperValue));
					entry.setEval(getScore(upperValue));
					entry.setBestMove(getMove(upperValue));
				}
				// Conservatively keep the legacy flag matching whichever bound
				// the legacy fields reflect: LOWER if the score came from the
				// lower entry, UPPER otherwise. (EXACT would imply equality
				// between the two stored scores, which we cannot guarantee.)
				entry.setFlag(getDepth(lowerValue) >= getDepth(upperValue) ? ITTEntry.FLAG_LOWER : ITTEntry.FLAG_UPPER);
			} else if (haveLower) {
				entry.setDepth(getDepth(lowerValue));
				entry.setEval(getScore(lowerValue));
				entry.setBestMove(getMove(lowerValue));
				entry.setFlag(ITTEntry.FLAG_LOWER);
			} else {
				entry.setDepth(getDepth(upperValue));
				entry.setEval(getScore(upperValue));
				entry.setBestMove(getMove(upperValue));
				entry.setFlag(ITTEntry.FLAG_UPPER);
			}

			return;
		}
	}


	@Override
	public final void put(long hashkey, int depth, int eval, int alpha, int beta, int bestmove) {

		if (eval > MAX_SCORE || eval < MIN_SCORE) {
			return;
		}

		// Classify the search result against the supplied window:
		//   eval >= beta   -> fail-high -> LOWER bound (score >= eval)
		//   eval <= alpha  -> fail-low  -> UPPER bound (score <= eval)
		//   else           -> EXACT     -> update both bounds with the same score
		final boolean failHigh = (eval >= beta);
		final boolean failLow  = (eval <= alpha);
		final boolean isExact  = !failHigh && !failLow;

		final boolean updateLower = failHigh || isExact;
		final boolean updateUpper = failLow  || isExact;

		addValue(hashkey, eval, depth, bestmove, updateLower, updateUpper);
	}


	private void addValue(final long new_key, final int new_score, final int new_depth,
			final int new_move, final boolean updateLower, final boolean updateUpper) {

		final long[] localTable = table;
		final int start_pos = getIndex(new_key) * ENTRY_LONGS;

		int replace_pos     = -1;
		int replace_quality = Integer.MAX_VALUE;
		boolean sameKey     = false;

		for (int i = 0, pos = start_pos; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {

			final long stored_lower = localTable[pos + 1];
			final long stored_upper = localTable[pos + 2];

			if (stored_lower == 0L && stored_upper == 0L) {
				// Empty slot always wins over a tracked eviction candidate -
				// using it costs nothing (no useful entry evicted). Counter
				// is incremented unconditionally because an empty slot is hit
				// at most once per put() (we break immediately).
				replace_pos = pos;
				counter_usage++;
				break;
			}

			final long stored_key = localTable[pos];
			if ((stored_key ^ (stored_lower + stored_upper)) == new_key) {
				replace_pos = pos;
				sameKey = true;
				break;
			}

			// Different-key slot: evaluate as eviction candidate. Quality is the
			// best depth held in either bound minus an age penalty.
			final int stored_depth = Math.max(
					stored_lower != 0L ? getDepth(stored_lower) : -1,
					stored_upper != 0L ? getDepth(stored_upper) : -1);
			final int stored_gen   = Math.max(
					stored_lower != 0L ? getGeneration(stored_lower) : 0,
					stored_upper != 0L ? getGeneration(stored_upper) : 0);
			final int gen_diff     = (generation - stored_gen) & 0xFF;
			final int quality      = stored_depth - 4 * gen_diff;

			if (replace_pos == -1 || quality < replace_quality) {
				replace_quality = quality;
				replace_pos = pos;
			}
		}

		if (replace_pos == -1) {
			return;
		}

		long lowerValue;
		long upperValue;

		if (sameKey) {

			// Same key: refine bound(s) we are updating, enforce coherency vs the
			// opposite bound. The invariant we protect is `lower <= upper`. A naive
			// "write each side independently" rule lets shallow refinements clobber
			// the deeper opposite bound's truth, producing entries where lower > upper
			// that mislead both the per-bound cutoffs and the static_eval clamp.
			lowerValue = localTable[replace_pos + 1];
			upperValue = localTable[replace_pos + 2];

			if (updateLower && updateUpper) {

				// EXACT result: both bounds collapse to the same (score, depth, move).
				// Treat the write as atomic — overwrite both, or skip both. Allows
				// the write whenever new_depth is at least as deep (within 4) as
				// the deepest existing bound; otherwise the existing deeper entry
				// is more authoritative and we drop the EXACT write.
				int existingDepth = -1;
				if (lowerValue != 0L) existingDepth = Math.max(existingDepth, getDepth(lowerValue));
				if (upperValue != 0L) existingDepth = Math.max(existingDepth, getDepth(upperValue));

				if (existingDepth < 0 || new_depth >= existingDepth - 4) {
					final int move = (new_move != 0) ? new_move
							: (lowerValue != 0L ? getMove(lowerValue)
									: (upperValue != 0L ? getMove(upperValue) : 0));
					final long newValue = createValue(new_score, move, new_depth, generation);
					lowerValue = newValue;
					upperValue = newValue;
				}
				// else: keep existing entry (its data dominates the new shallow EXACT).

			} else if (updateLower) {

				// Fail-high refines the lower bound only. Two coherency rules:
				//   (a) Skip if new is much shallower than the existing lower.
				//   (b) If new_score > existing upper_score, the two bounds would
				//       contradict. Resolve by depth: deeper wins. Equal-depth ties
				//       go to the new write (latest information).
				if (lowerValue == 0L || new_depth >= getDepth(lowerValue) - 4) {

					boolean canWrite = true;

					if (upperValue != 0L && new_score > getScore(upperValue)) {
						if (new_depth >= getDepth(upperValue)) {
							upperValue = 0L;            // new is at least as deep; drop stale upper
						} else {
							canWrite = false;           // existing deeper upper wins; reject new lower
						}
					}

					if (canWrite) {
						final int move = (new_move != 0) ? new_move
								: (lowerValue != 0L ? getMove(lowerValue) : 0);
						lowerValue = createValue(new_score, move, new_depth, generation);

						if (upperValue != 0L && isStaleAgainst(upperValue, new_depth)) {
							upperValue = 0L;
						}
					}
				}

			} else if (updateUpper) {

				// Fail-low refines the upper bound only. Symmetric to the LOWER case.
				if (upperValue == 0L || new_depth >= getDepth(upperValue) - 4) {

					boolean canWrite = true;

					if (lowerValue != 0L && new_score < getScore(lowerValue)) {
						if (new_depth >= getDepth(lowerValue)) {
							lowerValue = 0L;
						} else {
							canWrite = false;
						}
					}

					if (canWrite) {
						final int move = (new_move != 0) ? new_move
								: (upperValue != 0L ? getMove(upperValue) : 0);
						upperValue = createValue(new_score, move, new_depth, generation);

						if (lowerValue != 0L && isStaleAgainst(lowerValue, new_depth)) {
							lowerValue = 0L;
						}
					}
				}
			}

		} else {

			// Evicting a different key: write fresh bound(s); the other side is empty.
			lowerValue = updateLower ? createValue(new_score, new_move, new_depth, generation) : 0L;
			upperValue = updateUpper ? createValue(new_score, new_move, new_depth, generation) : 0L;
		}

		localTable[replace_pos]     = new_key ^ (lowerValue + upperValue);
		localTable[replace_pos + 1] = lowerValue;
		localTable[replace_pos + 2] = upperValue;
	}


	private int getIndex(long key) {
		return ((int) (key ^ (key >>> 32)) & (maxEntries - CLUSTER_SIZE));
	}


	// Treat the other bound as stale relative to a new write if it is much
	// shallower or from a much older generation; in that case we drop it
	// rather than letting it form an inconsistent (lower > upper) pair with
	// the newly stored side.
	private boolean isStaleAgainst(long otherValue, int newDepth) {
		if (otherValue == 0L) {
			return false;
		}
		if (getDepth(otherValue) < newDepth - 4) {
			return true;
		}
		final int genDiff = (generation - getGeneration(otherValue)) & 0xFF;
		return genDiff > 16;
	}


	private static int getScore(long value) {
		return (int) (value >> SCORE_SHIFT);
	}


	private static int getDepth(long value) {
		return (int) (value & 0xFF);
	}


	private static int getMove(long value) {
		return (int) ((value >>> MOVE_SHIFT) & 0x3FFFFF);
	}


	private static int getGeneration(long value) {
		return (int) ((value >>> GEN_SHIFT) & 0xFF);
	}


	private static long createValue(int score, int move, int depth, byte gen) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0 && depth <= 255);
		}
		return VALID_MASK
				| ((long) score << SCORE_SHIFT)
				| ((long) (gen & 0xFF) << GEN_SHIFT)
				| ((long) move << MOVE_SHIFT)
				| depth;
	}
}
