package bagaturchess.deeplearning.impl_nnue.eval;


import java.io.FileInputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.visitors.ActivationFunctions;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;
	
	private NeuralNetwork network;
	
	float[][][] inputs_3d = new float[8][8][12];
	
	
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
	protected double phase1() {
		
		float[] inputs = (float[]) bitboard.getNNUEInputs();
		
		for (int index = 0; index < inputs.length; index++) {
			
			int piece_type = index / 64;
			
			if (piece_type < 0 || piece_type > 11) {
				
				throw new IllegalStateException("piece_type=" + piece_type);
			}
			
			int sqare_id = index % 64;
			int file = sqare_id & 7;
			int rank = sqare_id >>> 3;
			
			inputs_3d[file][rank][piece_type] = inputs[index];
		}
		
		Tensor tensor = new Tensor(inputs_3d);
		
		//Tensor tensor = new Tensor((float[]) bitboard.getNNUEInputs());
		
		network.setInput(tensor);
		
		//forward method is already called in setInput(tensor)
		//network.forward();
		
		float actualWhitePlayerEval = network.getOutput()[0];

		actualWhitePlayerEval = ActivationFunctions.sigmoid_getx(actualWhitePlayerEval);
		
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
