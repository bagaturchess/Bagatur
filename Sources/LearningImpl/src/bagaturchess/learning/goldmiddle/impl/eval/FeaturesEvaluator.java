/*
 * Created on Aug 5, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package bagaturchess.learning.goldmiddle.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.eval.cache.IEvalEntry;


public class FeaturesEvaluator implements IEvaluator {
	
	
	private boolean useEvalCache = false;
	
	private IBitBoard bitboard;
	private Features features;
	private IFeature[][] features_byComp;
	private ISignals signals;
	
	private ISignalFiller filler;
	
	private int[] max_eval_byComp;
	private VarStatistic[] max_eval_byComp_statsitics;
	
	private int[] eval_buff;
	
	private IEvalCache evalCache;
	private IEvalEntry cached = new EvalEntry_BaseImpl();
	
	/*public FeaturesEvaluator(IBitBoard _bitboard, EvalCache_Impl1 _evalCache) {
		this(_bitboard, _evalCache, new SignalFiller(_bitboard), null, null);
	}*/
	
	
	public FeaturesEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, ISignalFiller _filler, Features _features, ISignals _signals) {
		
		bitboard = _bitboard;
		evalCache = _evalCache;
		features = _features;
		features_byComp = features.getAllByComplexity();
		
		max_eval_byComp = new int[features_byComp.length];
		eval_buff = new int[features_byComp.length];
		max_eval_byComp_statsitics = new VarStatistic[features_byComp.length];
		for (int i=0; i<max_eval_byComp_statsitics.length; i++) {
			max_eval_byComp_statsitics[i] = new VarStatistic(false);
		}
		
		signals = _signals;
		
		filler = _filler;
	}
	
	
	public void beforeSearch() {
		for (int i=0; i<max_eval_byComp_statsitics.length; i++) {
			max_eval_byComp_statsitics[i].devideMax(2);
		}
	}
	
	public int roughEval(int depth, int rootColour) {
		//if (true) return 0;
		
		//signals.clear();
		
		int colour = bitboard.getColourToMove();
		long hashkey = bitboard.getHashKey();
		
		if (useEvalCache) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				//if (!cached.isSketch()) {
					int eval = (int) cached.getEval();
					if (colour == Figures.COLOUR_WHITE) {
						return eval;
					} else {
						return -eval;
					}
				//} else {
				//	throw new IllegalStateException("cached.isSketch()");
				//}
			}
		}
			
		IFeature[] featuresByComplexity = features_byComp[IFeatureComplexity.STANDARD];
		for (int i=0; i<featuresByComplexity.length; i++) {
			signals.getSignal(featuresByComplexity[i].getId()).clear();
		}
		
		//Fill only the comp part of the signals
		filler.fillByComplexity(IFeatureComplexity.STANDARD, signals);
		
		//Calc current eval
		double cur_eval = 0;
		for (int i=0; i<featuresByComplexity.length; i++) {
			cur_eval += featuresByComplexity[i].eval(signals.getSignal(featuresByComplexity[i].getId()), bitboard.getMaterialFactor().getOpenningPart());
		}
		
		double eval = cur_eval;
		
		if (eval > IEvaluator.MAX_EVAL || eval < IEvaluator.MIN_EVAL) {
			throw new IllegalStateException("eval=" + eval);
		}
		
		if (colour == Figures.COLOUR_WHITE) {
			return (int) eval;
		} else {
			return - (int) eval;
		}
	}
	
	
	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour, FullEvalFlag flag) {
		throw new UnsupportedOperationException();
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.api.IEvaluator#lazyEval(int, int, int, int)
	 */
	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		//if (true) return 0;
		
		//signals.clear();
		
		int colour = bitboard.getColourToMove();
		long hashkey = bitboard.getHashKey();
		
		if (useEvalCache) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				//if (!cached.isSketch()) {
					int eval = (int) cached.getEval();
					if (colour == Figures.COLOUR_WHITE) {
						return eval;
					} else {
						return -eval;
					}
				//} else {
				//	throw new IllegalStateException("cached.isSketch()");
				//}
			}
		}
		
		double eval = 0;
		
		boolean fullEval = true;
		for (int comp=0; comp<features_byComp.length && comp <= IFeatureComplexity.FIELDS_STATES_ITERATION; comp++) {
			
			/*if (comp == FeatureComplexity.MOVES_ITERATION && bitboard.getBaseEvaluation().getTotalFactor() <= 31) {
				break;
			}*/
			/*if (comp == FeatureComplexity.PIECES_ITERATION && bitboard.getBaseEvaluation().getTotalFactor() <= 15) {
				break;
			}*/
			
			IFeature[] featuresByComplexity = features_byComp[comp];
			for (int i=0; i<featuresByComplexity.length; i++) {
				signals.getSignal(featuresByComplexity[i].getId()).clear();
			}
			
			//signals.clear();
			/*for (int i=0; i<features.length; i++) {
				Feature f = features[i];
				signals.getSignal(f.getId()).clear();
			}*/
			
			//Fill only the comp part of the signals
			filler.fillByComplexity(comp, signals);
			
			//Calc current eval
			double cur_eval = 0;
			for (int i=0; i<featuresByComplexity.length; i++) {
				cur_eval += featuresByComplexity[i].eval(signals.getSignal(featuresByComplexity[i].getId()), bitboard.getMaterialFactor().getOpenningPart());
			}
			
			//Update max evals
			int diff = (int) (Math.abs(cur_eval) - max_eval_byComp[comp]);
			if (diff > 0) {
				max_eval_byComp[comp] += diff;
				//dumpBounds();
			}
			
			//Add to total eval
			eval_buff[comp] = (int) cur_eval;
			eval += cur_eval;
			
			//Break the cycle if the eval is out of range
			if (comp < features_byComp.length - 1) {
				
				int window = getWindow(comp, false);
				
				if (//eval < alpha - max_eval_byComp_integral_sum[comp + 1] || eval > beta + max_eval_byComp_integral_sum[comp + 1]
					eval < alpha - window || eval > beta + window) {
					fullEval = false;
					break;
				}
			}
		}
		
		for (int comp=0; comp < max_eval_byComp_statsitics.length; comp++) {
			
			int cur_sum = 0;
			for (int comp1=comp; comp1 < eval_buff.length; comp1++) {
				cur_sum += eval_buff[comp1];
			}
			
			int cur_sum_abs = Math.abs(cur_sum);
			max_eval_byComp_statsitics[comp].addValue(cur_sum_abs, cur_sum_abs);
		}
		
		if (eval > IEvaluator.MAX_EVAL || eval < IEvaluator.MIN_EVAL) {
			throw new IllegalStateException("eval=" + eval);
		}

		if (useEvalCache && fullEval) {
			evalCache.put(hashkey, 5, (int) eval);
		}
		
		int intEval = (int) eval;
		if (colour == Figures.COLOUR_WHITE) {
			return intEval;
		} else {
			return -intEval;
		}
	}
	
	
	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {

		int colour = bitboard.getColourToMove();
		long hashkey = bitboard.getHashKey();
		
		if (useEvalCache) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				//if (!cached.isSketch()) {
					
					int eval = (int) cached.getEval();
					
					if (colour == Figures.COLOUR_WHITE) {
						return eval;
					} else {
						return -eval;
					}
				//} else {
				//	throw new IllegalStateException("cached.isSketch()=" + cached.isSketch());
				//}
			}
		}
		
		
		double eval = 0;
		
		signals.clear();
		
		for (int comp=0; comp<features_byComp.length; comp++) {
			
			//Fill only the comp part of the signals
			filler.fillByComplexity(comp, signals);
			
			//Calc current eval
			double cur_eval = 0;
			IFeature[] featuresByComplexity = features_byComp[comp];
			for (int i=0; i<featuresByComplexity.length; i++) {
				cur_eval += featuresByComplexity[i].eval(signals.getSignal(featuresByComplexity[i].getId()), bitboard.getMaterialFactor().getOpenningPart());
			}
			
			//Add to total eval
			//eval_buff[comp] = (int) cur_eval;
			eval += cur_eval;
		}
		
		if (eval > IEvaluator.MAX_EVAL || eval < IEvaluator.MIN_EVAL) {
			throw new IllegalStateException("eval=" + eval);
		}
		
		if (useEvalCache) {
			evalCache.put(hashkey, 5, (int) eval);
		}
		
		//int intEval = (int) eval;
		
		if (colour == Figures.COLOUR_WHITE) {
			return eval;
		} else {
			return -eval;
		}
	}
	
	
	private int getWindow(int comp, boolean pvNode) {
		if (pvNode) {
			return (int) max_eval_byComp_statsitics[comp + 1].getMaxVal();
			//return (int) (max_eval_byComp_statsitics[comp + 1].getEntropy() + 2 * max_eval_byComp_statsitics[comp + 1].getDisperse());
		} else {
			//return (int) max_eval_byComp_statsitics[comp + 1].getMaxVal();
			return (int) (max_eval_byComp_statsitics[comp + 1].getEntropy() + max_eval_byComp_statsitics[comp + 1].getDisperse());
		}
	}
	
	
	public void fillSignal(Signals signals, int rootColour) {
		
		//signals.clear();
		
		for (int comp=0; comp<features_byComp.length; comp++) {
			
			//Fill only the comp part of the signals
			filler.fillByComplexity(comp, signals);
		}
	}
}
