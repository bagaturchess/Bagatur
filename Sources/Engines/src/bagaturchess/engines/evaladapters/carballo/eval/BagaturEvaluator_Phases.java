package bagaturchess.engines.evaladapters.carballo.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.evaladapters.carballo.AttacksInfo;
import bagaturchess.engines.evaladapters.carballo.CompleteEvaluator;
import bagaturchess.engines.evaladapters.carballo.IBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private IBoard board;
	private CompleteEvaluator evaluator;
	private AttacksInfo ai;
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board = new BoardImpl(bitboard);
		
		evaluator = new CompleteEvaluator();
		ai = new AttacksInfo();
	}
	
	
	public int getMaterialQueen() {
		return 1244;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected double phase1() {
		int eval = evaluator.evaluate1(board, ai);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected double phase2() {
		int eval = evaluator.evaluate2(board, ai);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase3()
	 */
	@Override
	protected double phase3() {
		int eval = 0;
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase4()
	 */
	@Override
	protected double phase4() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase5()
	 */
	@Override
	protected double phase5() {
		// TODO Auto-generated method stub
		return 0;
	}
}
