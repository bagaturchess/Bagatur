package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.Evaluator;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConfigurationImpl;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConstants;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private final IEvalComponentsProcessor evalComponentsProcessor_ones;
	private final IEvalComponentsProcessor evalComponentsProcessor_weights;
	
	private IEvalComponentsProcessor evalComponentsProcessor;
	
	private final EvalInfo evalInfo;
	private final ChessBoard board;
	
	private long countOnes;
	private long countWeights;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalInfo = new EvalInfo();

		evalComponentsProcessor_ones = new EvalComponentsProcessor_Ones(evalInfo);
		evalComponentsProcessor_weights = new EvalComponentsProcessor_Weights(evalInfo);
	}
	
	
	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		
		evalComponentsProcessor = evalComponentsProcessor_ones;
		//evalComponentsProcessor = evalComponentsProcessor_weights;
		
		double eval = super.fullEval(depth, alpha, beta, rootColour);
		
		/*
		//countOnes++;
		
		if (Math.abs(eval) < 150) {
			
			countWeights++;
			
			evalComponentsProcessor = evalComponentsProcessor_weights;
			
			eval = super.fullEval(depth, alpha, beta, rootColour, false);
			
			//System.out.println("all: " + countOnes + ", weights " + countWeights);
		}*/
		
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalInfo.clearEvals();
		
		evalInfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected double phase1() {
		
		return Evaluator.eval1(bitboard.getBoardConfig(), board, evalInfo, evalComponentsProcessor);
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
	
	
	private class EvalComponentsProcessor_Weights implements IEvalComponentsProcessor {
		
		
		private final EvalInfo evalInfo;
		
		private Features_Splitter features_splitter;
		
		
		private EvalComponentsProcessor_Weights(final EvalInfo _evalInfo) {
			
			evalInfo = _evalInfo;
			
			try {
				
				features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, Bagatur_V20_FeaturesConfigurationImpl.class.getName());
				
			} catch (Exception e) {

				throw new RuntimeException(e);
			}
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			
			IFeature[] features = features_splitter.getFeatures(BagaturEvaluator_Phases.this.bitboard);
			
			if (evalPhaseID == EVAL_PHASE_ID_1) {
				
				evalInfo.eval_o_part1 += value_o * features[componentID].getWeight();
				
				evalInfo.eval_e_part1 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_2) {
				
				evalInfo.eval_o_part2 += value_o * features[componentID].getWeight();
				
				evalInfo.eval_e_part2 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_3) {
				
				evalInfo.eval_o_part3 += value_o * features[componentID].getWeight();
				
				evalInfo.eval_e_part3 += value_e * features[componentID].getWeight();
					
			} else if (evalPhaseID == EVAL_PHASE_ID_4) {
				
				evalInfo.eval_o_part4 += value_o * features[componentID].getWeight();
					
				evalInfo.eval_e_part4 += value_e * features[componentID].getWeight();
				
			} else if (evalPhaseID == EVAL_PHASE_ID_5) {
				
				evalInfo.eval_o_part5 += value_o * features[componentID].getWeight();
				
				evalInfo.eval_e_part5 += value_e * features[componentID].getWeight();
					
			} else {
				
				throw new IllegalStateException();
			}
		}
	}
	
	
	private class EvalComponentsProcessor_Ones implements IEvalComponentsProcessor {
		
		
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
