package bagaturchess.search.impl.rootsearch.mixed;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch;


public class FinishCallback_StartParallelSearch implements IFinishCallback {
	
	
	private MTDParallelSearch parallelSearch;
	private final IBitBoard bitboardForSetup;
	private final ISearchMediator mediator;
	private final int startIteration;
	private final int maxIterations;
	private final boolean useMateDistancePrunning;
	private IFinishCallback finishCallback;
	
	
	public FinishCallback_StartParallelSearch(MTDParallelSearch _parallelSearch, IBitBoard _bitboardForSetup, ISearchMediator _mediator,
			int _startIteration, int _maxIterations, boolean _useMateDistancePrunning, IFinishCallback _finishCallback) {
		parallelSearch = _parallelSearch;
		bitboardForSetup = _bitboardForSetup;
		mediator = _mediator;
		startIteration = _startIteration;
		maxIterations = _maxIterations;
		useMateDistancePrunning = _useMateDistancePrunning;
		finishCallback = _finishCallback;
	}


	public void ready() {
		parallelSearch.negamax(bitboardForSetup, mediator, startIteration, maxIterations, useMateDistancePrunning, finishCallback, null);
	}
}
