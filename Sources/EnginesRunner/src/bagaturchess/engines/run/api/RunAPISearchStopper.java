package bagaturchess.engines.run.api;


import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;


public class RunAPISearchStopper implements ISearchStopper {
	
	
	private ISearchStopper internal;
	private long stopTime;
	private boolean stopped;
	
	
	RunAPISearchStopper(long _stopTime) {
		stopTime = _stopTime;
	}
	
	
	public boolean isStopped() {
		if (stopped) return true;
		if (internal != null) return internal.isStopped();
		return false;
	}
	
	
	public void markStopped() {
		throw new UnsupportedOperationException();
	}
	
	
	public void setSecondaryStopper(ISearchStopper secondaryStopper) {
		internal = secondaryStopper;
	}
	
	
	public void stopIfNecessary(int maxdepth, int colour, double alpha, double beta) throws SearchInterruptedException {
		if (System.currentTimeMillis() > stopTime) {
			stopped = true;
			throw new SearchInterruptedException();
		}
		if (internal != null) internal.stopIfNecessary(maxdepth, colour, alpha, beta);
	}
}
