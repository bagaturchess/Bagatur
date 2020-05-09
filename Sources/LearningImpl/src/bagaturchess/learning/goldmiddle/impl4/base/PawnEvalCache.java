package bagaturchess.learning.goldmiddle.impl4.base;


import java.util.Arrays;

import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;


public class PawnEvalCache {

	private static final int POWER_2_TABLE_SHIFTS = 64 - EngineConstants.POWER_2_PAWN_EVAL_ENTRIES;

	// keys, scores, passedPawnsOutposts
	private static final long[] keys = new long[(1 << EngineConstants.POWER_2_PAWN_EVAL_ENTRIES) * 3];

	public static void clearValues() {
		Arrays.fill(keys, 0);
	}

	public static int updateBoardAndGetScore(final ChessBoard cb, final EvalInfo evalInfo) {

		if (!EngineConstants.ENABLE_PAWN_EVAL_CACHE) {
			return ChessConstants.CACHE_MISS;
		}

		final int index = getIndex(cb.pawnZobristKey);
		final long xorKey = keys[index];
		final int score = (int) keys[index + 1];
		final long passedPawnsAndOutpostsValue = keys[index + 2];

		if ((xorKey ^ score ^ passedPawnsAndOutpostsValue) == cb.pawnZobristKey) {
			if (!EngineConstants.TEST_EVAL_CACHES) {
				evalInfo.passedPawnsAndOutposts = passedPawnsAndOutpostsValue;
			}
			return score;
		}
		
		return ChessConstants.CACHE_MISS;
	}

	public static void addValue(final long key, final int score, final long passedPawnsAndOutpostsValue) {

		final int index = getIndex(key);
		keys[index] = key ^ score ^ passedPawnsAndOutpostsValue;
		keys[index + 1] = score;
		keys[index + 2] = passedPawnsAndOutpostsValue;
	}

	private static int getIndex(final long key) {
		return (int) (key >>> POWER_2_TABLE_SHIFTS) * 3;
	}

	public static int getUsage() {
		return 0;//Util.getUsagePercentage(keys);
	}

}
