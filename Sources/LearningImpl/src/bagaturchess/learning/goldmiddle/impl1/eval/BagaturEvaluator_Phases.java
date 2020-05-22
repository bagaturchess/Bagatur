package bagaturchess.learning.goldmiddle.impl1.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.goldmiddle.impl1.base.ChessBoard;
import bagaturchess.learning.goldmiddle.impl1.base.Evaluator;
import bagaturchess.learning.goldmiddle.impl1.base.IChessBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private IChessBoard board;
	private Evaluator evaluator;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board = new ChessBoard(bitboard);
		
		evaluator = new Evaluator(board);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected double phase1() {
		int eval = evaluator.getScore1();
		//int eval = (int)(500 * Math.random() - 250);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected double phase2() {
		int eval = evaluator.getScore2();
		
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
