package bagaturchess.search.impl.utils.kinetic;


public class KineticData_Piece {
	
	
	private KineticData_PieceLevel[] levels;
	private int fieldID;
	private long fieldBitboard;
	int pieceType;
	
	
	public KineticData_Piece(int size, int _fieldID, long _fieldBitboard, int _pieceType) {
		
		levels = new KineticData_PieceLevel[size + 1];
		
		fieldID = _fieldID;
		fieldBitboard = _fieldBitboard;
		pieceType = _pieceType;
		
		for (int i=0; i<=size; i++) {
			levels[i] = new KineticData_PieceLevel(size, i);
			levels[i].excludedToFields[0] = fieldBitboard;
		}
	}
	
	public KineticData_PieceLevel getLevel(int level) {
		return levels[level];
	}
	
	
	public int[] getAvg() {
		int[] avgs = new int[levels.length];
		for (int i=0; i<levels.length; i++) {
			if (levels[i].getAll_counts() != 0) {
				avgs[i] = levels[i].getAll_evals() / levels[i].getAll_counts();				
			} else {
				avgs[i] = 0;
			}
		}
		return avgs;
	}

	public int[] getCounts() {
		int[] counts = new int[levels.length];
		for (int i=0; i<levels.length; i++) {
			counts[i] = levels[i].getAll_counts();
		}
		return counts;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += "DATA[field:" + fieldID + ", ";
		
		result += " evals ";
		
		for (int i=0; i<levels.length; i++) {
			int avg = 0;
			if (levels[i].getAll_counts() != 0) {
				avg = levels[i].getAll_evals() / levels[i].getAll_counts();				
			}
			result += avg + " ";
		}
		
		result += "]";
		
		return result;
	}
}
