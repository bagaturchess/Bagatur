package bagaturchess.learning.impl.signals;


import java.util.HashMap;
import java.util.Map;

import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.Features;


public class Signals implements ISignals {
	
	
	private final Features features;
	private final Map<Integer, ISignal> signals;
	
	
	public Signals(Features _features) {
		features = _features;
		IFeature[] featuresArr = features.getFeatures();
		
		signals = new HashMap<Integer, ISignal>();
		
		for (int i=0; i<featuresArr.length; i++) {
			signals.put(featuresArr[i].getId(), featuresArr[i].createNewSignal());
		}
	}
	
	public void clear() {
		for (ISignal signal: signals.values()) {
			signal.clear();
		}
	}
	
	public ISignal getSignal(int id) {
		return signals.get(id);
	}
}
