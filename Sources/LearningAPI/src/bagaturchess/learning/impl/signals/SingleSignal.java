package bagaturchess.learning.impl.signals;


import bagaturchess.learning.api.ISignal;


public class SingleSignal implements ISignal {
	
	
	private double signalStrength;
	
	
	@Override
	public void clear() {
		signalStrength = 0;
	}
	
	
	@Override
	public double getStrength() {
		return signalStrength;
	}
	
	
	@Override
	public void addStrength(double _signalStrength, double opening_part) {
		signalStrength += _signalStrength;
	}
	
	
	@Override
	public double getStrength(int subid) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public void addStrength(int subid, double _signalStrength, double opening_part) {
		throw new IllegalStateException();
	}
}
