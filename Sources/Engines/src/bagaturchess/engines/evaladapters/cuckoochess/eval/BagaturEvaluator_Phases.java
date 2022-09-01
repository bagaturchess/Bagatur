package bagaturchess.engines.evaladapters.cuckoochess.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.evaladapters.cuckoochess.Evaluate;
import bagaturchess.engines.evaladapters.cuckoochess.IPosition;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private IPosition pos;
	private Evaluate evaluator;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		pos = new PositionImpl(bitboard);
		evaluator = new Evaluate();
	}
	
	
	public int getMaterialQueen() {
		return 1244;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected int phase1() {
		int eval = evaluator.eval1(pos);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected int phase2() {
		int eval = evaluator.eval2(pos);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase3()
	 */
	@Override
	protected int phase3() {
		int eval = evaluator.eval3(pos);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase4()
	 */
	@Override
	protected int phase4() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase5()
	 */
	@Override
	protected int phase5() {
		// TODO Auto-generated method stub
		return 0;
	}
}
