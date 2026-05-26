package bagaturchess.search.impl.history;


public class CorrectionHistory extends StatHistory {

	public CorrectionHistory() {
		super(32, 16);
	}


	// Halves the count of each entry — preserves mean but increases weight of future observations (gravity).
	public void clear() {
		for (int c = 0; c < 2; c++)
			for (int i = 0; i < SIZE; i++)
				table[c][i].norm();
	}


	// Updates correction history with observed diff between bestScore and rawStaticEval.
	public void update(int color, long hashKey, int rawStaticEval, int bestScore, int depth) {
		int diff = Math.max(-maxVal, Math.min(maxVal,
		               depth * (bestScore - rawStaticEval) / 8));
		addCapped(color, hashKey, diff);
	}
}
