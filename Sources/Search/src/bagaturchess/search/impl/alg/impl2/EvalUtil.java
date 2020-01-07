package bagaturchess.search.impl.alg.impl2;


import java.util.Arrays;

import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.Util;


public class EvalUtil {

	public static int POWER_2_TT_ENTRIES 					= 23;
	
	private static int keyShifts;
	public static int maxEntries;

	private static long[] keys;
	private static long[] values;

	private static long usageCounter;
	
	private static final int SCORE = 48;

	public static boolean isInitialized = false;

	public static void init(final boolean force) {
		if (force || !isInitialized) {
			keyShifts = 64 - POWER_2_TT_ENTRIES;
			maxEntries = (int) Util.POWER_LOOKUP[POWER_2_TT_ENTRIES] + 3;

			keys = new long[maxEntries];
			values = new long[maxEntries];
			usageCounter = 0;

			isInitialized = true;
		}
	}

	public static void clearValues() {
		if (!isInitialized) {
			return;
		}
		Arrays.fill(keys, 0);
		Arrays.fill(values, 0);
		usageCounter = 0;
	}

	public static long getValue(final long key) {

		final int index = getIndex(key);

		for (int i = 0; i < 4; i++) {
			long value = values[index + i];
			if ((keys[index + i] ^ value) == key) {
				return value;
			}
		}
		
		return 0;
	}

	private static int getIndex(final long key) {
		return (int) (key >>> keyShifts);
	}

	public static void addValue(final long key, int score) {

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

	public static int getScore(final long value) {
		int score = (int) (value >> SCORE);

		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}

		return score;
	}

	
	private static long createValue(final long score) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(score >= Util.SHORT_MIN && score <= Util.SHORT_MAX);
		}
		return score << SCORE;
	}

	public static void setSizeMB(int value) {
		switch (value) {
		case 1:
		case 2:
		case 4:
		case 8:
		case 16:
		case 32:
		case 64:
		case 128:
		case 256:
		case 512:
		case 1024:
		case 2048:
		case 4096:
		case 8192:
		case 16384:
			int power2Entries = (int) (Math.log(value) / Math.log(2) + 16);
			if (POWER_2_TT_ENTRIES != power2Entries) {
				POWER_2_TT_ENTRIES = power2Entries;
				init(true);
			}
			break;
		default:
			throw new RuntimeException("Hash-size must be between 1-16384 mb and a multiple of 2");
		}
	}

	public static long getUsagePercentage() {
		return usageCounter * 1000 / EvalUtil.maxEntries;
	}
}
