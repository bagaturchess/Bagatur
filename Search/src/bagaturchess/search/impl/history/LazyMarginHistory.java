package bagaturchess.search.impl.history;


public class LazyMarginHistory extends StatHistory {

	public LazyMarginHistory() {
		super(600, 16);
	}


	// divergence = |roughEval - fullEval| in centipawns
	public void update(int color, long hashKey, int divergence) {
		int clamped = Math.max(0, Math.min(maxVal, divergence));
		addCapped(color, hashKey, clamped);
	}
}
