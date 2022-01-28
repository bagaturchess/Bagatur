package bagaturchess.deeplearning.impl4_v20.eval;


import java.io.FileInputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl4_v20.IMPL4_Constants;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;	
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	
	float[] inputs_f;
	
	private NeuralNetwork network;
	
	//private ActivationFunction activation_function = ActivationFunction.SIGMOID;
	private ActivationFunction activation_function = ActivationFunction.LINEAR;

	
	public NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
				
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(IMPL4_Constants.NET_FILE));
			
			network = (NeuralNetwork) ois.readObject();
			
			ois.close();
			
			if (network.getInputLayer().getWidth() != 55) {
				
				throw new IllegalStateException("network inputs size is " + network.getInputLayer().getWidth());
			}	
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
		
		inputs_f = new float[network.getInputLayer().getWidth()];
	}
	
	
	@Override
	protected double phase1() {
		
		for (int i = 0; i < inputs_f.length; i++) {
			
			inputs_f[i] = 0;
		}
		
		filler.fillSignals(inputs_f, 0);
		
		network.setInput(new Tensor(inputs_f));
		
		float actualWhitePlayerEval = network.getOutput()[0];

		actualWhitePlayerEval = activation_function.getx(actualWhitePlayerEval);
		
		return actualWhitePlayerEval;
	}
	
	
	@Override
	protected double phase2() {

		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected double phase3() {
		
		int eval = 0;
				
		return eval;
	}
	
	
	@Override
	protected double phase4() {
		
		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected double phase5() {
		
		int eval = 0;
		
		return eval;
	}
}
