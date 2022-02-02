package bagaturchess.learning.goldmiddle.impl4.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConfigurationImpl;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases_GOLDENMIDDLE extends BagaturEvaluator_Phases {
	
	
	public BagaturEvaluator_Phases_GOLDENMIDDLE(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig, new EvalComponentsProcessor_Weights(_bitboard));
	}
	
	
	@Override
	protected boolean useDefaultMaterial() {
		
		return false;
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
