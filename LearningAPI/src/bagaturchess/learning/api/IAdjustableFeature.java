package bagaturchess.learning.api;




public interface IAdjustableFeature extends IFeature {
	
	public void adjust(ISignal signal, double amount, double openningPart);
	
	public void applyChanges();
	
	public double getLearningSpeed();
	
	public void clear();
}
