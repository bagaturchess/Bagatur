package bagaturchess.search.impl.utils.kinetic;


public class KineticData_PieceLevel {

	int level;
	long[] excludedToFields;
	int[] counts;
	int[] evals;
	
	
	KineticData_PieceLevel(int size, int _level) {
		
		level = _level;
		
		excludedToFields = new long[size + 1];
		counts = new int[size + 1];
		evals = new int[size + 1];

		
		/*for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}

		for (int i = 0; i < evals.length; i++) {
			evals[i] = -1000000000;
		}*/
	}
	
	
	public long getAll(int maxdepth) {
		long result = 0;
		for (int depth = 0; depth <= maxdepth; depth++) {
			result |= excludedToFields[depth];
		}
		return result;
	}
	
	
	long getLevel(int depth) {
		return excludedToFields[depth];
	}
	
	public void add(int depth, long bitboard) {
		excludedToFields[depth] |= bitboard;
	}

	public void addEval(int depth, int eval) {
		if (depth != level) {
			throw new IllegalStateException("depth=" + depth + ", level=" + level);
		}
		
		counts[depth]++;
		evals[depth] += eval;
		
		
		/*if (eval > evals[depth]) {
			evals[depth] = eval;
			counts[depth] = 1;
		}*/
	}
	
	int getAll_counts() {
		int result = 0;
		/*for (int depth = 0; depth < counts.length; depth++) {
			result += counts[depth];
		}*/
		result += counts[level];
				
		return result;
	}
	
	int getAll_evals() {
		int result = 0;
		/*for (int depth = 0; depth < evals.length; depth++) {
			result += evals[depth];
		}*/
		result += evals[level];
		return result;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "LDATA[counts:" + getAll_counts() + ", evals:" + getAll_evals() +  "]";
		return result;
	}
}
