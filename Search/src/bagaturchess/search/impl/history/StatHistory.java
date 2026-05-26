package bagaturchess.search.impl.history;


import bagaturchess.bitboard.impl.utils.VarStatistic;


public abstract class StatHistory {

	protected static final int SIZE = 16384;
	protected static final int MASK = SIZE - 1;

	protected final int maxVal;
	protected final int maxCount;
	protected final VarStatistic[][] table;


	protected StatHistory(int maxVal, int maxCount) {
		this.maxVal   = maxVal;
		this.maxCount = maxCount;
		this.table    = new VarStatistic[2][SIZE];
		for (int c = 0; c < 2; c++)
			for (int i = 0; i < SIZE; i++)
				table[c][i] = new VarStatistic();
	}


	public void reset() {
		for (int c = 0; c < 2; c++)
			for (int i = 0; i < SIZE; i++)
				table[c][i].clear();
	}


	// Returns mean value in centipawns
	public int get(int color, long hashKey) {
		return (int) table[color][(int) (hashKey & MASK)].getEntropy();
	}


	// Returns standard deviation in centipawns
	public int getDisperse(int color, long hashKey) {
		return (int) table[color][(int) (hashKey & MASK)].getDisperse();
	}


	// Adds an observation, but first halves the entry's count if it has reached maxCount.
	// This keeps the effective Welford learning rate (1/count) bounded, similar to a classic EMA.
	protected void addCapped(int color, long hashKey, double value) {
		VarStatistic entry = table[color][(int) (hashKey & MASK)];
		if (entry.getCount() >= maxCount) {
			entry.norm();
		}
		entry.addValue(value);
	}
}
