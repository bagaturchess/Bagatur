package bagaturchess.egtb.gaviota;


import bagaturchess.bitboard.impl.movegen.MoveInt;


public class GTBProbeOutput implements Comparable<GTBProbeOutput> {
	
	
	public final static int DRAW    = 0;
    public final static int WMATE   = 1;
    public final static int BMATE   = 2;
    public final static int UNKNOWN = 3;
    
    
    public int move; //optional
    public int result;
    public int movesToMate; // Full moves to mate, or 0 if DRAW or UNKNOWN.
    
    
    public GTBProbeOutput() {
    	this(UNKNOWN, 0);
    }
    
    public GTBProbeOutput(int[] result) {
    	this(result[0], result[1]);
    }
    
    public GTBProbeOutput(int _result, int _movesToMate) {
    	result = _result;
    	movesToMate = _movesToMate;
    }
    
    
    public GTBProbeOutput(int _move, int _result, int _movesToMate) {
    	move = _move;
    	result = _result;
    	movesToMate = _movesToMate;
    }
    
    
	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
 	 * than, equal to, or greater than the specified object.
	 */
	public int compareTo(GTBProbeOutput other) {
		
		if (result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.DRAW) {
			return -1;//equals
		} else if (result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.WMATE) {
			return -10;
		} else if (result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.BMATE) {
			return +10;
		} else if (result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.WMATE && other.result == GTBProbeOutput.DRAW) {
			return +10;
		} else if (result == GTBProbeOutput.WMATE && other.result == GTBProbeOutput.WMATE) {
			int diff = other.movesToMate - movesToMate;
			if (diff == 0) {
				return -1;//equals
			}
			return diff;
		} else if (result == GTBProbeOutput.WMATE && other.result == GTBProbeOutput.BMATE) {
			return +10;
		} else if (result == GTBProbeOutput.WMATE && other.result == GTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.BMATE && other.result == GTBProbeOutput.DRAW) {
			return -10;
		} else if (result == GTBProbeOutput.BMATE && other.result == GTBProbeOutput.WMATE) {
			return -10;
		} else if (result == GTBProbeOutput.BMATE && other.result == GTBProbeOutput.BMATE) {
			int diff = other.movesToMate - movesToMate;
			if (diff == 0) {
				return -1;//equals
			}
			return -diff;
		} else if (result == GTBProbeOutput.BMATE && other.result == GTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.UNKNOWN && other.result == GTBProbeOutput.DRAW) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.UNKNOWN && other.result == GTBProbeOutput.WMATE) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.UNKNOWN && other.result == GTBProbeOutput.BMATE) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		} else if (result == GTBProbeOutput.UNKNOWN && other.result == GTBProbeOutput.UNKNOWN) {
			throw new IllegalStateException("result == GTBProbeOutput.DRAW && other.result == GTBProbeOutput.UNKNOWN");
		}
		
		throw new IllegalStateException();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GTBProbeOutput) {
			GTBProbeOutput other = (GTBProbeOutput) o;
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
    	msg += (move == 0) ? "" : MoveInt.moveToString(move) + " ";
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
