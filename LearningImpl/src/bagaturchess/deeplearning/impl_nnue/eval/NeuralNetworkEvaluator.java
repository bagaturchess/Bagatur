package bagaturchess.deeplearning.impl_nnue.eval;


import java.io.FileInputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private NeuralNetwork network;
	
	float[][][] inputs_3d = new float[8][8][15];
	
	private ActivationFunction activation_function = ActivationFunction.SIGMOID;
	
	
	NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NNUE_Constants.NET_FILE));
			
			network = (NeuralNetwork) ois.readObject();
			
			ois.close();
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
		
	}
	
	
	@Override
	protected int phase1() {
		
		Tensor tensor = NNUE_Constants.createInput(bitboard, inputs_3d);
		
		network.setInput(tensor);
		
		//forward method is already called in setInput(tensor)
		//network.forward();
		
		float actualWhitePlayerEval = network.getOutput()[0];

		actualWhitePlayerEval = activation_function.SIGMOID.getx(actualWhitePlayerEval);
		
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
