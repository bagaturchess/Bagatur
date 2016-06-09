package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;


public class PonderingStopper implements ISearchStopper {
	
	
	private boolean stopped;
	private ISearchStopper secondaryStopper;
	
	
	public PonderingStopper() {
	}
	
	
	@Override
	public void markStopped() {
		stopped = true;
	}
	
	
	@Override
	public boolean isStopped() {
		return stopped;
	}
	
	
	@Override
	public void stopIfNecessary(int maxdepth, int colour, double alpha,
			double beta) throws SearchInterruptedException {
		
		if (maxdepth <= 1) {
			return;
		}
		
		if (stopped) {
			throw new SearchInterruptedException();
		}
		
		if (secondaryStopper != null) {
			try {
				secondaryStopper.stopIfNecessary(maxdepth, colour, alpha, beta);
			} catch(SearchInterruptedException sie) {
				//dumpper.dump("secondary stopper: " + timeController);
				//dumpper.dump("Stopping: secondaryStopper");
				throw sie;
			}
		}
	}
}
