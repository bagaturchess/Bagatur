package bagaturchess.search.impl.history;


public class LazyMarginHistory extends EMAHistory {

	public LazyMarginHistory() {
		super(64, 64 * 200);
	}


	// divergence = |roughEval - fullEval| in centipawns
	public void update(int color, long hashKey, int divergence) {
		int idx    = (int) (hashKey & MASK);
		int target = Math.min(divergence * grain, maxVal);
		int entry  = table[color][idx];
		entry += (target - entry) / 16;
		table[color][idx] = Math.max(0, Math.min(maxVal, entry));
	}
}
