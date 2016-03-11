package bagaturchess.learning.api;


public interface ISignal {
	
	public double getStrength();
	public void addStrength(double signalStrength, double opening_part);
	
	public double getStrength(int subid);
	public void addStrength(int subid, double signalStrength, double opening_part);
	
	public void clear();
}
