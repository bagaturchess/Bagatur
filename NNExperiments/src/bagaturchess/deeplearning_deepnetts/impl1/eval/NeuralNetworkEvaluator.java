package bagaturchess.deeplearning_deepnetts.impl1.eval;


import java.io.File;
import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.util.FileIO;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	public static final String NET_NAME = "net.dn.bin";
	
	
	private IBitBoard bitboard;
	
	private FeedForwardNetwork network;
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	
	float[] inputs_f;
	
	private ActivationFunction activation_function = ActivationFunction.SIGMOID;
	
	
	NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) throws ClassNotFoundException, IOException {
		this(_bitboard, _evalCache, _evalConfig,
				(FeedForwardNetwork) FileIO.createFromFile(new File(NET_NAME)));
	}
	
	
	public NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig, FeedForwardNetwork _network) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		network = _network;
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		if (network.getInputLayer().getWidth() != 55) {
			throw new IllegalStateException("network inputs size is not 55");
		}	
		
		inputs_f = new float[network.getInputLayer().getWidth()];
	}
	
	
	@Override
	protected int phase1() {
		
		for (int i = 0; i < inputs_f.length; i++) {
			inputs_f[i] = 0;
		}
		
		filler.fillSignals(inputs_f, 0);
		
		network.setInput(inputs_f);
		
		//network.forward();
		
		float actualWhitePlayerEval = network.getOutput()[0];

		actualWhitePlayerEval = activation_function.getx(actualWhitePlayerEval);
		
		return (int) actualWhitePlayerEval;
	}
	
	
	@Override
	protected int phase2() {

		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected int phase3() {
		
		int eval = 0;
				
		return eval;
	}
	
	
	@Override
	protected int phase4() {
		
		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected int phase5() {
		
		int eval = 0;
		
		return eval;
	}
}
