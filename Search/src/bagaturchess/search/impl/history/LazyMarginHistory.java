package bagaturchess.search.impl.history;


import java.util.Arrays;


public class LazyMarginHistory extends EMAHistory {
	
	
	public LazyMarginHistory() {
		super(64, 64 * 200);
	}


	@Override
	public void reset() {
	    for (int c = 0; c < 2; c++) {
	        Arrays.fill(table[c], maxVal);
	    }
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
