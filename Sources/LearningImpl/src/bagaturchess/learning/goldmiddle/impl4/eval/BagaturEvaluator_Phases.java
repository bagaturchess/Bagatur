package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.impl4.EvalUtil;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private ChessBoard board;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board = ((BoardImpl)bitboard).getChessBoard();
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected double phase1() {
		int eval = EvalUtil.getScore(board);
		if (board.colorToMove == bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK) {
			eval = -eval;
		}
		//int eval = (int)(500 * Math.random() - 250);
		
		return eval;
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected double phase2() {
		int eval = 0;
		
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