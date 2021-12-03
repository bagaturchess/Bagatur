/*
 * Created on Aug 5, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package bagaturchess.learning.goldmiddle.impl.eval;


import java.util.Map;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class FeaturesEvaluator implements IEvaluator {
	
	
	private IBitBoard bitboard;

	private ISignals signals;
	
	private ISignalFiller filler;
	
	private Map<Integer, IFeature[]> features_by_material_factor;
	
	
	public FeaturesEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, ISignalFiller _filler, Map<Integer, IFeature[]> _features_by_material_factor, ISignals _signals) {
		
		bitboard = _bitboard;
		
		features_by_material_factor = _features_by_material_factor;
		
		signals = _signals;
		
		filler = _filler;
	}
	
	
	public void beforeSearch() {
		//Do nothing
	}
	
	
	public int roughEval(int depth, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour, FullEvalFlag flag) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double fullEval(int depth, int alpha, int beta, int rootColour) {

		int colour = bitboard.getColourToMove();		
		
		signals.clear();
		
		int total_factor = Math.min(63, bitboard.getMaterialFactor().getTotalFactor());
		
		//System.out.println("total_factor=" + total_factor);
		
		IFeature[] features = features_by_material_factor.get(total_factor);
		
		
		filler.fillByComplexity(IFeatureComplexity.STANDARD, signals);
		
		double eval = 0;
		
		for (int i = 0; i < features.length; i++) {
			
			IFeature feature = features[i];
			
			if (feature != null) {
				
				eval += feature.eval(signals.getSignal(feature.getId()), -1);
			}
		}
		
		
		if (eval > IEvaluator.MAX_EVAL || eval < IEvaluator.MIN_EVAL) {
			
			throw new IllegalStateException("eval=" + eval);
		}
		
		
		if (colour == Figures.COLOUR_WHITE) {
			
			return eval;
			
		} else {
			
			return -eval;
		}
	}
	
	
	public void fillSignal(Signals signals, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
}
