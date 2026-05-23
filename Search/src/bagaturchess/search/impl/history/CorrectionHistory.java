package bagaturchess.search.impl.history;


public class CorrectionHistory extends EMAHistory {

	public CorrectionHistory() {
		super(256, 256 * 32);
	}


	public void clear() {
		for (int c = 0; c < 2; c++) {
			for (int i = 0; i < SIZE; i++) {
				table[c][i] /= 2;
			}
		}
	}


	// Returns correction in centipawns to add to static eval
	public void update(int color, long hashKey, int rawStaticEval, int bestScore, int depth) {
		int idx    = (int) (hashKey & MASK);
		int weight = Math.min(depth + 1, 16);
		int diff   = Math.max(-maxVal / grain,
		             Math.min( maxVal / grain,
		                       depth * (bestScore - rawStaticEval) / 8));
		int entry  = table[color][idx];
		entry += (diff * grain - entry) * weight / grain;
		entry = Math.max(-maxVal, Math.min(maxVal, entry));
		table[color][idx] = entry;
	}
}
