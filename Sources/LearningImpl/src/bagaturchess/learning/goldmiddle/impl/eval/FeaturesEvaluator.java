/*
 * Created on Aug 5, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package bagaturchess.learning.goldmiddle.impl.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class FeaturesEvaluator implements IEvaluator {
	
	
	private IBitBoard bitboard;

	private ISignals signals;
	
	private ISignalFiller filler;
	
	private Features_Splitter features_splitter;
	
	
	public FeaturesEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, ISignalFiller _filler, Features_Splitter _features_splitter, ISignals _signals) {
		
		bitboard = _bitboard;
		
		features_splitter = _features_splitter;
		
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
		
		signals.clear();
		
		IFeature[] features = features_splitter.getFeatures(bitboard);
		
		
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
		
		
		int colour = bitboard.getColourToMove();	
		
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
