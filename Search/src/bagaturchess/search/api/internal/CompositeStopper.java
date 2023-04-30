package bagaturchess.search.api.internal;


public class CompositeStopper implements ISearchStopper {

	
	private ISearchStopper[] stoppers;
	private boolean propagateStop;
	
	
	public CompositeStopper(ISearchStopper[] _stoppers, boolean _propagateStop) {
		stoppers = _stoppers;
		propagateStop = _propagateStop;
	}
	
	
	@Override
	public void markStopped() {
		
		if (propagateStop) {
			for (int i = 0; i < stoppers.length; i++) {
				stoppers[i].markStopped();
			}	
		} else {
			//Do Nothing
		}
	}
	

	@Override
	public boolean isStopped() {
		for (int i = 0; i < stoppers.length; i++) {
			if (stoppers[i].isStopped()) return true;
		}
		return false;
	}

	@Override
	public void stopIfNecessary(int maxdepth, int colour, double alpha,
			double beta) throws SearchInterruptedException {
		
		for (int i = 0; i < stoppers.length; i++) {
			stoppers[i].stopIfNecessary(maxdepth, colour, alpha, beta);
		}
	}
}
