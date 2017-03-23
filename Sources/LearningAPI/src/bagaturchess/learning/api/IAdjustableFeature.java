package bagaturchess.learning.api;




public interface IAdjustableFeature extends IFeature {
	public void adjust(ISignal signal, double amount, double openningPart);
	public void applyChanges();
	public void clear();
}
