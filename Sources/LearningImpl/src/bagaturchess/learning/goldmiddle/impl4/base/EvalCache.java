package bagaturchess.learning.goldmiddle.impl4.base;


import java.util.Arrays;

import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;


public class EvalCache {

	private static final int POWER_2_TABLE_SHIFTS = 64 - EngineConstants.POWER_2_EVAL_ENTRIES;

	// keys, scores
	private static final long[] keys = new long[(1 << EngineConstants.POWER_2_EVAL_ENTRIES) * 2];

	public static void clearValues() {
		Arrays.fill(keys, 0);
	}

	public static int getScore(final long key) {
		final int index = getIndex(key);
		final long storedKey = keys[index];
		final long score = keys[index + 1];

		if ((storedKey ^ score) == key) {
			return (int) score;
		}

		return ChessConstants.CACHE_MISS;
	}
	
	
	public static void addValue(final long key, final int score) {
		if (!EngineConstants.ENABLE_EVAL_CACHE) {
			return;
		}

		final int index = getIndex(key);
		keys[index] = key ^ score;
		keys[index + 1] = score;
	}

	private static int getIndex(final long key) {
		return (int) (key >>> POWER_2_TABLE_SHIFTS) << 1;
	}

	public static int getUsage() {
		return 0;//Util.getUsagePercentage(keys);
	}

}
