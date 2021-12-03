package bagaturchess.learning.impl.signals;


import java.util.HashMap;
import java.util.Map;

import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignals;


public class Signals implements ISignals {
	
	
	private final Map<Integer, ISignal> signals;
	
	
	public Signals(IFeature[] features_array) {
		
		signals = new HashMap<Integer, ISignal>();
		
		for (int i=0; i<features_array.length; i++) {
			
			IFeature feature = features_array[i];
			
			if (feature != null) {
				
				signals.put(feature.getId(), feature.createNewSignal());
			}
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
