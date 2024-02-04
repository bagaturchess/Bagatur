package bagaturchess.deeplearning_deepnetts.impl1.eval;


import java.io.File;
import java.io.IOException;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;	
	private NeuralNetwork<?> network;
	
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	double[] inputs_d;
	float[] inputs_f;
	
	
	NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) throws ClassNotFoundException, IOException {
		this(_bitboard, _evalCache, _evalConfig, (FeedForwardNetwork) FileIO.createFromFile(new File("net.dn.bin")));
	}
	
	
	public NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig, FeedForwardNetwork _network) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		network = _network;
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		
		if (network.getInputLayer().getWidth() != 55) {
			throw new IllegalStateException("network inputs size is not 55");
		}	
		
		inputs_d = new double[network.getInputLayer().getWidth()];
		inputs_f = new float[network.getInputLayer().getWidth()];
	}
	
	
	public float[] getInputs() {
		return inputs_f;
	}
	
	
	@Override
	protected int phase1() {
		
		for (int i = 0; i < inputs_d.length; i++) {
			inputs_d[i] = 0;
		}
		filler.fillSignals(inputs_d, 0);
		
		for (int i = 0; i < inputs_d.length; i++) {
			inputs_f[i] = (float) inputs_d[i];
		}
		
		network.setInput(new Tensor(inputs_f));
		network.forward();
		float actualWhitePlayerEval = network.getOutput()[0];

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
