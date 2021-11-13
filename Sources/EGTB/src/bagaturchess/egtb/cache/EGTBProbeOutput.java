package bagaturchess.egtb.cache;


import bagaturchess.bitboard.impl.movegen.MoveInt;


public class EGTBProbeOutput implements Comparable<EGTBProbeOutput> {
	
	
	public final static int DRAW    = 0;
    public final static int WMATE   = 1;
    public final static int BMATE   = 2;
    public final static int UNKNOWN = 3;
    
    
    public int move; //optional
    public int result;
    public int movesToMate; // Full moves to mate, or 0 if DRAW or UNKNOWN.
    
    
    public EGTBProbeOutput() {
    	this(UNKNOWN, 0);
    }
    
    public EGTBProbeOutput(int[] result) {
    	this(result[0], result[1]);
    }
    
    public EGTBProbeOutput(int _result, int _movesToMate) {
    	result = _result;
    	movesToMate = _movesToMate;
    }
    
    
    public EGTBProbeOutput(int _move, int _result, int _movesToMate) {
    	move = _move;
    	result = _result;
    	movesToMate = _movesToMate;
    }
    
    
	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
 	 * than, equal to, or greater than the specified object.
	 */
	public int compareTo(EGTBProbeOutput other) {
		
		if (result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.DRAW) {
			return -1;//equals
		} else if (result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.WMATE) {
			return -10;
		} else if (result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.BMATE) {
			return +10;
		} else if (result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.WMATE && other.result == EGTBProbeOutput.DRAW) {
			return +10;
		} else if (result == EGTBProbeOutput.WMATE && other.result == EGTBProbeOutput.WMATE) {
			int diff = other.movesToMate - movesToMate;
			if (diff == 0) {
				return -1;//equals
			}
			return diff;
		} else if (result == EGTBProbeOutput.WMATE && other.result == EGTBProbeOutput.BMATE) {
			return +10;
		} else if (result == EGTBProbeOutput.WMATE && other.result == EGTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.BMATE && other.result == EGTBProbeOutput.DRAW) {
			return -10;
		} else if (result == EGTBProbeOutput.BMATE && other.result == EGTBProbeOutput.WMATE) {
			return -10;
		} else if (result == EGTBProbeOutput.BMATE && other.result == EGTBProbeOutput.BMATE) {
			int diff = other.movesToMate - movesToMate;
			if (diff == 0) {
				return -1;//equals
			}
			return -diff;
		} else if (result == EGTBProbeOutput.BMATE && other.result == EGTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.UNKNOWN && other.result == EGTBProbeOutput.DRAW) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.UNKNOWN && other.result == EGTBProbeOutput.WMATE) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.UNKNOWN && other.result == EGTBProbeOutput.BMATE) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		} else if (result == EGTBProbeOutput.UNKNOWN && other.result == EGTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == EGTBProbeOutput.DRAW && other.result == EGTBProbeOutput.UNKNOWN");
		}
		
		throw new IllegalStateException();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof EGTBProbeOutput) {
			EGTBProbeOutput other = (EGTBProbeOutput) o;
			return result == other.result && movesToMate == other.movesToMate;
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return 100 * (result + 1)+ movesToMate;
	}
	
	
    @Override
    public String toString() {
    	String msg = "";
    	//msg += (move == 0) ? "" : MoveInt.moveToString(move) + " ";
    	switch(result) {
    		case DRAW:
    			msg += "DRAW";
    			break;
    		case WMATE:
    			msg += "WMATE in " + movesToMate;
    			break;
    		case BMATE:
    			msg += "BMATE in " + movesToMate;
    			break;
    		case UNKNOWN:
    			msg += "UNKNOWN";
    			break;
    	}
    	return msg;
    }
}
