package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.Evaluator;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConstants;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private final ChessBoard board;
	private final EvalInfo evalInfo;
	private final IEvalComponentsProcessor evalComponentsProcessor_ones;
	private final IEvalComponentsProcessor evalComponentsProcessor_weights;
	
	private IEvalComponentsProcessor evalComponentsProcessor;
	
	private long countOnes;
	private long countWeights;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board = ((BoardImpl)bitboard).getChessBoard();
		evalInfo = new EvalInfo();

		evalComponentsProcessor_ones = new EvalComponentsProcessor_Ones(evalInfo);
		evalComponentsProcessor_weights = new EvalComponentsProcessor_Weights(evalInfo);
	}
	
	
	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		countOnes++;
		evalComponentsProcessor = evalComponentsProcessor_ones;
		double eval = super.fullEval(depth, alpha, beta, rootColour);
		/*if (Math.abs(eval) < 150) {
			countWeights++;
			evalComponentsProcessor = evalComponentsProcessor_weights;
			eval = super.fullEval(depth, alpha, beta, rootColour, false);
			//System.out.println("all: " + countOnes + ", weights " + countWeights);
		}*/
		return eval;
	}
	
	
	@Override
	protected double phase1() {
		
		evalInfo.clearEvals();
		evalInfo.fillBoardInfo(board);
		
		return Evaluator.eval1(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected double phase2() {
		
		return Evaluator.eval2(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected double phase3() {
		
		return Evaluator.eval3(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected double phase4() {
		
		return Evaluator.eval4(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected double phase5() {
		
		return Evaluator.eval5(board, evalInfo, evalComponentsProcessor);
	}
	
	
	private static final class EvalComponentsProcessor_Weights implements IEvalComponentsProcessor {
		
		
		private final EvalInfo evalInfo;
		
		
		private EvalComponentsProcessor_Weights(final EvalInfo _evalInfo) {
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
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				evalInfo.eval_o_part4 += value_o * weight_o;
				evalInfo.eval_e_part4 += value_e * weight_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				evalInfo.eval_o_part5 += value_o * weight_o;
				evalInfo.eval_e_part5 += value_e * weight_e;
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	
	private static final class EvalComponentsProcessor_Ones implements IEvalComponentsProcessor {
		
		
		private final EvalInfo evalInfo;
		
		
		private EvalComponentsProcessor_Ones(final EvalInfo _evalInfo) {
			evalInfo = _evalInfo;
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				evalInfo.eval_o_part1 += value_o;
				evalInfo.eval_e_part1 += value_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				evalInfo.eval_o_part2 += value_o;
				evalInfo.eval_e_part2 += value_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				evalInfo.eval_o_part3 += value_o;
				if (componentID != Bagatur_V20_FeaturesConstants.FEATURE_ID_KING_SAFETY) {
					evalInfo.eval_e_part3 += value_e;
				}
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				if (componentID != Bagatur_V20_FeaturesConstants.FEATURE_ID_PAWN_PASSED
						&& componentID != Bagatur_V20_FeaturesConstants.FEATURE_ID_PAWN_PASSED_UNSTOPPABLE) {
					evalInfo.eval_o_part4 += value_o;
				}
				evalInfo.eval_e_part4 += value_e;
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				evalInfo.eval_o_part5 += value_o;
				if (componentID != Bagatur_V20_FeaturesConstants.FEATURE_ID_SPACE) {
					evalInfo.eval_e_part5 += value_e;
				}
			} else {
				throw new IllegalStateException();
			}
		}
	}
}
