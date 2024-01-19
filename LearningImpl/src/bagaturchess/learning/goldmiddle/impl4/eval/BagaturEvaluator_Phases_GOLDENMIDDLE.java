package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.Evaluator;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConfigurationImpl;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases_GOLDENMIDDLE extends BaseEvaluator {
	
	
	private IEvalComponentsProcessor evalComponentsProcessor;
	
	private final EvalInfo evalinfo;
	
	private final ChessBoard board;
	
	
	public BagaturEvaluator_Phases_GOLDENMIDDLE(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		this(_bitboard, _evalCache, _evalConfig, new EvalComponentsProcessor_Weights(_bitboard));
	}


	protected BagaturEvaluator_Phases_GOLDENMIDDLE(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig, IEvalComponentsProcessor _evalComponentsProcessor) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalComponentsProcessor = _evalComponentsProcessor;
		
		evalinfo = new EvalInfo();
		
		evalComponentsProcessor.setEvalInfo(evalinfo);
	}
	
	
	@Override
	protected boolean useDefaultMaterial() {
		
		return false;
	}
	
	
	@Override
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int eval = super.fullEval(depth, alpha, beta, rootColour);
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalinfo.clearEvals();
		
		evalinfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected int phase1() {
		
		return Evaluator.eval1(!useDefaultMaterial(), bitboard.getBoardConfig(), board, evalinfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase2() {
		
		return Evaluator.eval2(board, evalinfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase3() {
		
		return Evaluator.eval3(board, evalinfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase4() {
		
		return Evaluator.eval4(board, evalinfo, evalComponentsProcessor);
	}
	
	
	@Override
	protected int phase5() {
		
		return Evaluator.eval5(board, evalinfo, evalComponentsProcessor);
	}
	
	
	private static class EvalComponentsProcessor_Weights implements IEvalComponentsProcessor {
		
		
		private IBitBoard bitboard;	
		
		private EvalInfo evalinfo;
		
		private Features_Splitter features_splitter;
		
		
		private EvalComponentsProcessor_Weights(IBitBoard _bitboard) {
			
			bitboard = _bitboard;
			
			try {
				
				features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, Bagatur_V20_FeaturesConfigurationImpl.class.getName());
				
			} catch (Exception e) {

				throw new RuntimeException(e);
			}
		}
		
		
		@Override
		public void setEvalInfo(EvalInfo _evalinfo) {
			
			evalinfo = _evalinfo;
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			
			IFeature[] features = features_splitter.getFeatures(bitboard);
			
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				
				evalinfo.eval_o_part1 += value_o * features[componentID].getWeight();
				
				evalinfo.eval_e_part1 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				
				evalinfo.eval_o_part2 += value_o * features[componentID].getWeight();
				
				evalinfo.eval_e_part2 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				
				evalinfo.eval_o_part3 += value_o * features[componentID].getWeight();
				
				evalinfo.eval_e_part3 += value_e * features[componentID].getWeight();
					
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				
				evalinfo.eval_o_part4 += value_o * features[componentID].getWeight();
					
				evalinfo.eval_e_part4 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				
				evalinfo.eval_o_part5 += value_o * features[componentID].getWeight();
				
				evalinfo.eval_e_part5 += value_e * features[componentID].getWeight();
					
			} else {
				
				throw new IllegalStateException();
			}
		}
	}
}