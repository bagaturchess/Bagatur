package bagaturchess.learning.impl.signals;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.Features;


public class Signals implements ISignals {
	
	
	private final Features features;
	private final ISignal[] signals;
	
	
	public Signals(Features _features) {
		features = _features;
		IFeature[] featuresArr = features.getFeatures();
		
		signals = new ISignal[featuresArr.length];
		
		for (int i=0; i<featuresArr.length; i++) {
			signals[i] = featuresArr[i].createNewSignal();
			if (featuresArr[i].getId() != i) {
				throw new IllegalStateException();
			}
		}
	}
	
	public void clear() {
		for (int i=0; i<signals.length; i++) {
			signals[i].clear();
		}
	}
	
	public ISignal getSignal(int id) {
		return signals[id];
	}
}
