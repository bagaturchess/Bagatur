package bagaturchess.search.impl.history;


import java.util.Arrays;


public abstract class EMAHistory {

	protected static final int SIZE = 16384;
	protected static final int MASK = SIZE - 1;

	protected final int grain;
	protected final int maxVal;
	protected final int[][] table;


	protected EMAHistory(int grain, int maxVal) {
		this.grain  = grain;
		this.maxVal = maxVal;
		this.table  = new int[2][SIZE];
	}


	public void reset() {
		for (int c = 0; c < 2; c++) {
			Arrays.fill(table[c], 0);
		}
	}


	// Returns stored value in centipawns
	public int get(int color, long hashKey) {
		return table[color][(int) (hashKey & MASK)] / grain;
	}
}
