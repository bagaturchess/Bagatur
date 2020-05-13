package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.EvalUtil;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private final ChessBoard board;
	private final EvalInfo evalInfo;
	private final IEvalComponentsProcessor evalComponentsProcessor;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board = ((BoardImpl)bitboard).getChessBoard();
		evalInfo = new EvalInfo();
		evalComponentsProcessor = new EvalComponentsProcessor(evalInfo);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected double phase1() {
		
		evalInfo.clearEvals();
		evalInfo.fillBoardInfo(board);
		
		return EvalUtil.eval1(board, evalInfo, evalComponentsProcessor);
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected double phase2() {
		
		return EvalUtil.eval2(board, evalInfo, evalComponentsProcessor);
	}


	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase3()
	 */
	@Override
	protected double phase3() {
		
		return EvalUtil.eval3(board, evalInfo, evalComponentsProcessor);
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
	
	
	private static final class EvalComponentsProcessor implements IEvalComponentsProcessor {
		
		
		private final EvalInfo evalInfo;
		
		
		private EvalComponentsProcessor(final EvalInfo _evalInfo) {
			evalInfo = _evalInfo;
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				evalInfo.eval_o_part1 += value_o * weight_o;
				evalInfo.eval_e_part1 += value_e * weight_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				evalInfo.eval_o_part2 += value_o * weight_o;
				evalInfo.eval_e_part2 += value_e * weight_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				evalInfo.eval_o_part3 += value_o * weight_o;
				evalInfo.eval_e_part3 += value_e * weight_e;
			} else {
				throw new IllegalStateException();
			}
		}
	}
}
