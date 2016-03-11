/*
 * Created on Aug 5, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package bagaturchess.learning.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConstants;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.Features;
import bagaturchess.learning.impl.filler.SignalFiller;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.EvaluatorAdapter;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.EvalEntry;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public class FeaturesEvaluator extends EvaluatorAdapter implements IFeaturesConstants {
	
	
	private boolean useEvalCache = false;
	
	private IBitBoard bitboard;
	private IEvalCache evalCache;
	private Features features;
	private IFeature[][] features_byComp;
	private ISignals signals;
	
	private ISignalFiller filler;
	
	private int[] max_eval_byComp;
	private VarStatistic[] max_eval_byComp_statsitics;
	
	private int[] eval_buff;
	
	
	public FeaturesEvaluator(IBitBoard _bitboard, EvalCache _evalCache) {
		this(_bitboard, _evalCache, new SignalFiller(_bitboard), null, null);
	}
	
	
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
		//long hashkey = bitboard.getHashKey();
		
		
		double eval = 0;
			
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
		
		
		if (eval > IEvaluator.MAX_EVAL || eval < IEvaluator.MIN_EVAL) {
			throw new IllegalStateException("eval=" + eval);
		}
		
		int intEval = (int) eval;
		if (colour == Figures.COLOUR_WHITE) {
			return intEval;
		} else {
			return -intEval;
		}
	}
	
	
	@Override
	public int eval(int depth, int alpha, int beta, boolean pvNode, int rootColour) {
		
		
		//if (true) return 0;
		
		//signals.clear();
		
		int colour = bitboard.getColourToMove();
		long hashkey = bitboard.getHashKey();
		
		if (useEvalCache) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				if (!cached.isSketch()) {
					int eval = (int) cached.getEval();
					evalCache.unlock();
					if (colour == Figures.COLOUR_WHITE) {
						return eval;
					} else {
						return -eval;
					}
				} else {
					int eval = (int) cached.getEval();
					if (colour != Figures.COLOUR_WHITE) {
						eval = -eval;
					}
					int window = getWindow(1, pvNode);
					
					if (eval < alpha - window || eval > beta + window) {
						evalCache.unlock();
						return eval;
					}
				}
			}
			evalCache.unlock();
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
				
				int window = getWindow(comp, pvNode);
				
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

		if (useEvalCache) {
			evalCache.lock();
			evalCache.put(hashkey, (int) eval, !fullEval);
			evalCache.unlock();
		}
		
		int intEval = (int) eval;
		if (colour == Figures.COLOUR_WHITE) {
			return intEval;
		} else {
			return -intEval;
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
	
	
	/*public double fullEval(int rootColour) {
		
	}*/
	
	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {

		int colour = bitboard.getColourToMove();
		long hashkey = bitboard.getHashKey();
		
		if (useEvalCache) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				if (!cached.isSketch()) {
					
					int eval = (int) cached.getEval();
					evalCache.unlock();
					
					if (colour == Figures.COLOUR_WHITE) {
						return eval;
					} else {
						return -eval;
					}
				} else {
					throw new IllegalStateException("cached.isSketch()=" + cached.isSketch());
				}
			}
			evalCache.unlock();
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
			evalCache.lock();
			evalCache.put(hashkey, (int) eval, false);
			evalCache.unlock();
		}
		
		//int intEval = (int) eval;
		
		if (colour == Figures.COLOUR_WHITE) {
			return eval;
		} else {
			return -eval;
		}
	}
	
	/*private void dumpBounds() {
		String result = "";
		result += "MAX_EVALS: ";
		for (int i=0; i<max_eval_byComp.length; i++) {
			result += ", " + max_eval_byComp[i];
		}
		result += "\r\nMAX_EVALS_STATISTICS: ";
		for (int i=0; i<max_eval_byComp_statsitics.length - 1; i++) {
			result += ", " + getWindow(i, false);
		}
		
		System.out.println(result);
	}*/
	
	public void fillSignal(Signals signals, int rootColour) {
		
		//signals.clear();
		
		for (int comp=0; comp<features_byComp.length; comp++) {
			
			//Fill only the comp part of the signals
			filler.fillByComplexity(comp, signals);
		}
	}
}
