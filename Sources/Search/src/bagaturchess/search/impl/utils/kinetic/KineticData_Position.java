package bagaturchess.search.impl.utils.kinetic;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.search.impl.utils.SearchUtils;


public class KineticData_Position {

	
	private List<KineticData_Piece> pieces = new ArrayList<KineticData_Piece>();
	
	public int root_eval;
	public int[] evals;
	public int[] counts;
	
	
	public KineticData_Position() {
		evals = new int[7];
		counts = new int[7];
	}
	
	
	public KineticData_Piece createNewPieceData(int size, int _fieldID, long _fieldBitboard, int _pieceType) {
		KineticData_Piece new_one = new KineticData_Piece(size, _fieldID, _fieldBitboard, _pieceType);
		pieces.add(new_one);
		return new_one;
	}
	
	
	public void setEvals0(int eval) {
		evals[0] = eval;
		
		if (counts[0] != 0) {
			throw new IllegalStateException();
		}
		counts[0] = 1;
	}

	
	public void setRootEval(int eval) {
		root_eval = eval;
	}
	

	public void addRootEval(int eval) {
		if (SearchUtils.isMateVal(evals[0])) {
			throw new IllegalStateException();
		}
		evals[0] += eval;
		root_eval += eval;
	}
	
	
	public int getRootEval() {
		return root_eval;
	}
	
	
	public void fillVector() {
		
		int count_included_pieces = 0;
		for (int i=0; i<pieces.size(); i++) {
			
			KineticData_Piece piece = pieces.get(i);
			int[] avgs = piece.getAvg();
			int[] p_counts = piece.getCounts();
			
			for (int j=0; j<avgs.length; j++) {
				evals[j] += avgs[j];
				counts[j] += p_counts[j];
				if (p_counts[j] > 0) {
					count_included_pieces++;
				}
			}
		}
		
		for (int i=1; i<evals.length; i++) {
			//In check the pieces are  (with the current implementation)
			evals[i] /= Math.max(1, count_included_pieces);
		}
		
		if (counts[0] != 1) {
			throw new IllegalStateException("counts[0]=" + counts[0]);
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		

		/*for (int i=0; i<pieces.size(); i++) {
			KineticData_Piece piece = pieces.get(i);
			result += piece.toString() + "\r\n";
		}*/
		result += "DATA[";
		for (int i=0; i<evals.length; i++) {
			result += evals[i] + " (" + counts[i] + "), ";
		}
		result += "]";
		
		//result += "DATA[ field: " + field +", level:" + level + ", counts:" + getAll_counts() + ", evals:" + getAll_evals() +  "]";
		
		return result;
	}
}
