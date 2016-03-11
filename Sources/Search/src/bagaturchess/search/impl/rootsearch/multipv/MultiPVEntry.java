package bagaturchess.search.impl.rootsearch.multipv;


import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearchInfo;


public class MultiPVEntry implements Comparable<MultiPVEntry> {
	
	
	private int move;
	private ISearchInfo info;
	
	
	MultiPVEntry(int _move) {
		move = _move;
	}
	
	
	public int getMove() {
		return move;
	}
	
	
	public int getEval() {
		if (info == null) return 0; 
		return info.getEval();
	}
	
	
	public ISearchInfo getInfo() {
		return info;
	}
	
	
	public void setInfo(ISearchInfo info) {
		
		if (info == null) {
			throw new IllegalStateException("info is null");
		}
		if (info.getPV() == null) {
			throw new IllegalStateException("info.getPV() is null");
		}
		
		int[] new_pv = new int[] {move};
		new_pv = new int[info.getPV().length + 1];
		new_pv[0] = move;
		System.arraycopy(info.getPV(), 0, new_pv, 1, info.getPV().length);
		
		info.setPV(new_pv);
		info.setBestMove(move);
		info.setEval(-info.getEval());
		
		this.info = info;
	}
	
	
	@Override
	public int compareTo(MultiPVEntry o) {
		
		if (move == o.move) {
			return 0;
		}
		
		int delta = o.getEval() - getEval();
		if (delta == 0) {
			return 1;
		} else {
			return delta;
		}
	}
	
	
	@Override
	public boolean equals(Object o) {
		MultiPVEntry other = (MultiPVEntry) o;
		return move == other.move;
	}
	
	
	@Override
	public int hashCode() {
		return move;
	}
	
	
	@Override
	public String toString() {
		String result = "[MultiPVEntry]: ";
		result += MoveInt.moveToString(move);
		result += ", eval = " + getEval() + ", pv = " + MoveInt.moveToString(move) + ", " + MoveInt.movesToString(info.getPV());
		return result;
	}
}
