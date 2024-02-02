package bagaturchess.learning.goldmiddle.impl7.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.api.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl7.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl7.base.Evaluator;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private final ChessBoard board;
	
	private final EvalInfo evalInfo;
	
	private IEvalComponentsProcessor evalComponentsProcessor;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		//this(_bitboard, _evalCache, _evalConfig, new EvalComponentsProcessor_Ones());
		this(_bitboard, _evalCache, _evalConfig, new EvalComponentsProcessor_Weights());
	}
	
	
	protected BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig, IEvalComponentsProcessor _evalComponentsProcessor) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalComponentsProcessor = _evalComponentsProcessor;
		
		evalInfo = new EvalInfo();
		
		evalComponentsProcessor.setEvalInfo(evalInfo);
	}
	
	
	@Override
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int eval = super.fullEval(depth, alpha, beta, rootColour);
		
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalInfo.clearEvals();
		
		evalInfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected int phase1() {
		
		return Evaluator.eval1(bitboard.getBoardConfig(), board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase2() {
		
		return Evaluator.eval2(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase3() {
		
		return Evaluator.eval3(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase4() {
		
		return Evaluator.eval4(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase5() {
		
		return Evaluator.eval5(board, evalInfo, evalComponentsProcessor);
	}
	
	
	private static final class EvalComponentsProcessor_Ones implements IEvalComponentsProcessor {
		
		
		private EvalInfo evalinfo;
		
		
		private EvalComponentsProcessor_Ones() {
		}
		
		
		@Override
		public void setEvalInfo(Object _evalinfo) {
			
			evalinfo = (EvalInfo) _evalinfo;
		}
		
		
		@Override
		public final void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				
				evalinfo.eval_o_part1 += value_o;
				
				evalinfo.eval_e_part1 += value_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				
				evalinfo.eval_o_part2 += value_o;
				
				evalinfo.eval_e_part2 += value_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				
				evalinfo.eval_o_part3 += value_o;
				
				evalinfo.eval_e_part3 += value_e;
					
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				
				evalinfo.eval_o_part4 += value_o;
					
				evalinfo.eval_e_part4 += value_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				
				evalinfo.eval_o_part5 += value_o;
				
				evalinfo.eval_e_part5 += value_e;
					
			} else {
				
				throw new IllegalStateException();
			}
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID,
				int fieldID, int value_o, int value_e, double weight_o,
				double weight_e) {

			throw new UnsupportedOperationException();
		}
	}
	
	
	private static final class EvalComponentsProcessor_Weights implements IEvalComponentsProcessor {
		
		
		private EvalInfo evalinfo;
		
		
		private EvalComponentsProcessor_Weights() {
		}
		
		
		@Override
		public void setEvalInfo(Object _evalinfo) {
			
			evalinfo = (EvalInfo) _evalinfo;
		}
		
		
		@Override
		public final void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				
				evalinfo.eval_o_part1 += value_o * weight_o;
				
				evalinfo.eval_e_part1 += value_e * weight_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				
				evalinfo.eval_o_part2 += value_o * weight_o;
				
				evalinfo.eval_e_part2 += value_e * weight_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				
				evalinfo.eval_o_part3 += value_o * weight_o;
				
				evalinfo.eval_e_part3 += value_e * weight_e;
					
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				
				evalinfo.eval_o_part4 += value_o * weight_o;
					
				evalinfo.eval_e_part4 += value_e * weight_e;
				
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				
				evalinfo.eval_o_part5 += value_o * weight_o;
				
				evalinfo.eval_e_part5 += value_e * weight_e;
					
			} else {
				
				throw new IllegalStateException();
			}
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID,
				int fieldID, int value_o, int value_e, double weight_o,
				double weight_e) {

			throw new UnsupportedOperationException();
		}
	}
}
