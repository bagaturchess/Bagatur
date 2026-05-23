package bagaturchess.search.impl.history;


public class VolatilityHistory extends EMAHistory {

	public VolatilityHistory() {
		super(64, 64 * 50);
	}


	// Returns estimated score drift in centipawns: [0, 50]
	public void update(int color, long pawnHash, int drift, int depth) {
		int idx    = (int) (pawnHash & MASK);
		int target = Math.min(drift * grain, maxVal);
		int weight = Math.min(depth, 8);
		int entry  = table[color][idx];
		entry += (target - entry) * weight / 128;
		table[color][idx] = Math.max(0, Math.min(maxVal, entry));
	}
}
