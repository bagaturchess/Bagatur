package bagaturchess.deeplearning.impl.eval.pst;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.deeplearning.impl.NeuralNetworkUtils_PST;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;	
	private MultiLayerPerceptron network;
	
	private double[] inputs = new double[NeuralNetworkUtils_PST.getInputsSize()];
	
	
	NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		network = (MultiLayerPerceptron) NeuralNetwork.createFromFile("net.bin");
	}
	
	
	@Override
	protected int phase1() {
		
		NeuralNetworkUtils.clearInputsArray(inputs);
		NeuralNetworkUtils_PST.fillInputs(network, inputs, bitboard);
		NeuralNetworkUtils.calculate(network);
		double actualWhitePlayerEval = NeuralNetworkUtils.getOutput(network);

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
