package bagaturchess.search.impl.history;


public class CorrectionHistory {

	private static final int SIZE  = 16384;
	private static final int MASK  = SIZE - 1;
	private static final int GRAIN = 256;
	private static final int MAX_VAL = GRAIN * 32;

	// Indexed by [color][pawnHash & MASK], stored in units of 1/GRAIN centipawns
	private final int[][] table = new int[2][SIZE];


	public void clear() {
		for (int c = 0; c < 2; c++) {
			for (int i = 0; i < SIZE; i++) {
				table[c][i] /= 2;
			}
		}
	}

	public void reset() {
		for (int c = 0; c < 2; c++) {
			java.util.Arrays.fill(table[c], 0);
		}
	}


	// Returns correction in centipawns to add to static eval
	public int get(int color, long hashKey) {
		return table[color][(int) (hashKey & MASK)] / GRAIN;
	}


	public void update(int color, long hashKey, int rawStaticEval, int bestScore, int depth) {
		int idx    = (int) (hashKey & MASK);
		int weight = Math.min(depth + 1, 16);
		// Scale diff by depth: deeper searches are more reliable ground truth.
		// Clamp to +-MAX_VAL/GRAIN so no single node can push entry to maximum.
		int diff = Math.max(-MAX_VAL / GRAIN,
		           Math.min( MAX_VAL / GRAIN,
		                     depth * (bestScore - rawStaticEval) / 8));
		int entry = table[color][idx];
		// Exponential moving average toward diff * GRAIN
		entry += (diff * GRAIN - entry) * weight / GRAIN;
		entry = Math.max(-MAX_VAL, Math.min(MAX_VAL, entry));
		table[color][idx] = entry;
	}
}
