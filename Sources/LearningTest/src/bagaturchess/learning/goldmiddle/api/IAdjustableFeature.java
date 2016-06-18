package bagaturchess.learning.goldmiddle.api;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;


public interface IAdjustableFeature extends IFeature {
	public void adjust(ISignal signal, double amount, double openningPart);
	public void applyChanges();
	public void clear();
}
