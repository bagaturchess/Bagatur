package bagaturchess.search.impl.history;


public class VolatilityHistory extends StatHistory {

	public VolatilityHistory() {
		super(50, 16);
	}


	// Updates volatility history with observed score drift in centipawns.
	public void update(int color, long pawnHash, int drift, int depth) {
		int clamped = Math.max(0, Math.min(maxVal, drift));
		addCapped(color, pawnHash, clamped);
	}
}
