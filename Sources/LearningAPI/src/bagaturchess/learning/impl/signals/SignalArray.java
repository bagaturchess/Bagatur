package bagaturchess.learning.impl.signals;


import bagaturchess.learning.api.ISignal;


public class SignalArray implements ISignal {
	
	//private static int MAX_PARALLEL_COMPONENTS = 64;
	
	//private int max_id;
	private int max_parallel_subsignals;
	private int cur_size;
	private int[] subIDs;
	private double[] subSignals;
	
	
	//public SignalArray() {
	//	this(MAX_PARALLEL_COMPONENTS);
	//}
	
	public SignalArray(/*int _max_id,*/ int _max_parallel_subsignals) {
		super();
		
		//max_id = _max_id;
		max_parallel_subsignals = _max_parallel_subsignals;
		subIDs = new int[max_parallel_subsignals];
		subSignals = new double[max_parallel_subsignals];
	}
	
	
	@Override
	public void clear() {
		cur_size = 0;
	}
	
	
	@Override
	public double getStrength() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public void addStrength(double signalStrength, double opening_part) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double getStrength(int subid) {
		
		for (int i=0; i<cur_size; i++) {
			if (subIDs[i] == subid) {
				return subSignals[i];
			}
		}
		
		return 0;
	}
	
	
	@Override
	public void addStrength(int subid, double _signalStrength, double opening_part) {
		
		if (cur_size >= max_parallel_subsignals) {
			throw new IllegalStateException("cur_size=" + cur_size + ", max_parallel_subsignals=" + max_parallel_subsignals);
		}
		
		boolean found = false;
		for (int i=0; i<cur_size; i++) {
			if (subIDs[i] == subid) {
				subSignals[i] += _signalStrength;
				found = true;
				break;
			}
		}
		
		if (!found) {
			subIDs[cur_size] = subid;
			subSignals[cur_size] = _signalStrength;
			cur_size++;
		}
	}
	
	
	public int getSubsignalsCount() {
		return cur_size;
	}
	
	public int[] getSubIDs() {
		return subIDs;
	}
	
	public double[] getSubsignals() {
		return subSignals;
	}
}
