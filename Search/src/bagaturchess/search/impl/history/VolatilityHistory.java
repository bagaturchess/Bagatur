package bagaturchess.search.impl.history;


import java.util.Arrays;


/**
 * Search Volatility History (SVH).
 *
 * Tracks how much the minimax score shifts between successive iterative-deepening
 * iterations for each (color, pawnHash) pair.  A large score drift means the
 * position is tactically "volatile" and the search is unreliable at shallow depth;
 * a small drift means the position is stable and aggressive pruning is safe.
 *
 * Stored as an EMA of |score(depth D) - score(depth D-1)|, indexed by
 * [color][pawnHash & MASK].  get() returns the drift estimate in centipawns.
 *
 * Applications:
 *   - LMR: reduce less for volatile position types
 *   - Static null move pruning: require larger margin for volatile positions
 *   - Null move reduction: reduce less aggressively for volatile positions
 */
public class VolatilityHistory {

	private static final int SIZE    = 16384;
	private static final int MASK    = SIZE - 1;
	private static final int GRAIN   = 64;
	private static final int MAX_VAL = GRAIN * 50; // cap at ~50 cp drift


	// Stored in units of 1/GRAIN centipawns; [color][pawnHash & MASK]
	private final int[][] table = new int[2][SIZE];


	public void reset() {
		for (int c = 0; c < 2; c++) {
			Arrays.fill(table[c], 0);
		}
	}


	// Returns estimated score drift in centipawns: [0, 50]
	public int get(int color, long pawnHash) {
		return table[color][(int) (pawnHash & MASK)] / GRAIN;
	}


	/**
	 * @param drift   |bestScore - previousIterationScore| in centipawns
	 * @param depth   current search depth (deeper measurements are more reliable)
	 */
	public void update(int color, long pawnHash, int drift, int depth) {
		int idx    = (int) (pawnHash & MASK);
		int target = Math.min(drift * GRAIN, MAX_VAL);
		int weight = Math.min(depth, 8);
		int entry  = table[color][idx];
		// EMA: 6% learning rate at depth 8, slower at shallower depths
		entry += (target - entry) * weight / 128;
		table[color][idx] = Math.max(0, Math.min(MAX_VAL, entry));
	}
}
