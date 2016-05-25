package bagaturchess.search.api.internal;


public class CompositeStopper implements ISearchStopper {

	
	private ISearchStopper[] stoppers;
	
	
	public CompositeStopper(ISearchStopper[] _stoppers) {
		stoppers = _stoppers;
	}
	
	
	@Override
	public void markStopped() {
		for (int i = 0; i < stoppers.length; i++) {
			stoppers[i].markStopped();
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
