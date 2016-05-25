package bagaturchess.engines.run.api;


import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.uci.api.BestMoveSender;


public class RunAPIMediator implements ISearchMediator {
	
	
	private long startTime;
	private IRunAPIStatus status;
	private ISearchStopper stopper;
	private ISearchInfo lastinfo;
	private RunAPIBestMoveSender sender;
	
	
	public RunAPIBestMoveSender getSender() {
		return sender;
	}


	public RunAPIMediator(IRunAPIStatus _status, int millis) {
		status = _status;
		startTime = System.currentTimeMillis();
		stopper = new RunAPISearchStopper(startTime + millis);
		sender = new RunAPIBestMoveSender();
	}
	
	
	@Override
	public ISearchStopper getStopper() {
		return stopper;
	}
	
	
	@Override
	public void changedMajor(ISearchInfo info) {
		
		lastinfo = info;
		
		String bestLineString = 
	  			"D: " + info.getDepth() +
	  			"	SD: " + info.getSelDepth() +
	  			" Time: " + ((System.currentTimeMillis()-startTime)/(double)1000) + " s" +
	  			//" Mate: " + info.isMateScore() +
	  			"	Eval: " + (info.isMateScore() ? (info.getMateScore() + "M") : info.getEval() ) +
	  			"	NPS: " + (int)(info.getSearchedNodes()/((System.currentTimeMillis()-startTime)/(double)1000)) +
	  			//" Thread: " + Thread.currentThread().getName() +
	  			"	PV: " + MoveInt.movesToString(info.getPV());
		
		status.sendInfoLine(bestLineString);
	}
	
	@Override
	public void dump(String msg) {
		System.out.println(msg);
	}
	
	@Override
	public void dump(Throwable t) {
		t.printStackTrace();
	}
	
	@Override
	public void changedMinor(ISearchInfo info) {
		//Do nothing
	}
	
	@Override
	public ISearchInfo getLastInfo() {
		return lastinfo;
	}

	@Override
	public void registerInfoObject(ISearchInfo info) {
		//Do nothing
	}
	
	@Override
	public void send(String msg) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public BestMoveSender getBestMoveSender() {
		return sender;
	}

	@Override
	public void startIteration(int iteration) {
		//Do nothing
	}


	@Override
	public int getTrustWindow_BestMove() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int getTrustWindow_AlphaAspiration() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int getTrustWindow_MTD_Step() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void setStopper(ISearchStopper _stopper) {
		stopper = _stopper;
	}
}