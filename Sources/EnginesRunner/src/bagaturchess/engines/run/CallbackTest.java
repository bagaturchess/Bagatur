package bagaturchess.engines.run;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchMediator;

public class CallbackTest implements IFinishCallback {
	
	
	private IRootSearch search;
	private IEvaluator evaluator;
	private IBitBoard bitboard;
	
	private ISearchMediator mediator;
	
	private int depth = 0;
	
	
	public CallbackTest(IRootSearch _search, IEvaluator _evaluator, IBitBoard _bitboard) {
		search = _search;
		evaluator = _evaluator;
		bitboard = _bitboard;
		mediator = new MediatorDummper(bitboard, evaluator, 5000000, true);
	}
	
	
	@Override
	public void ready() {
		
		depth++;
		
		//ISearchMediator mediator1 = new MediatorDummper(bitboard, evaluator, 5000000, true);
		
		search.negamax(bitboard, mediator, depth, depth, true, this, null);

	}
}
