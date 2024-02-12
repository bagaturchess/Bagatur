package bagaturchess.deeplearning_neuroph.impl.eval.pst_and_allfeatures;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning_neuroph.NeuralNetworkUtils;
import bagaturchess.deeplearning_neuroph.impl.NeuralNetworkUtils_PST_And_AllFeatures;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_SignalFiller_InArray;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NeuralNetworkEvaluator extends BaseEvaluator {
	
	
	private IBitBoard bitboard;	
	private MultiLayerPerceptron network;
	
	
	private Bagatur_ALL_SignalFiller_InArray filler;
	double[] inputs;
	
	
	NeuralNetworkEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		network = (MultiLayerPerceptron) NeuralNetwork.createFromFile("net.bin");
		
		filler = new Bagatur_ALL_SignalFiller_InArray(bitboard);
		inputs = new double[NeuralNetworkUtils_PST_And_AllFeatures.getInputsSize()];
	}
	
	
	@Override
	protected int phase1() {
		
		NeuralNetworkUtils.clearInputsArray(inputs);
		NeuralNetworkUtils_PST_And_AllFeatures.fillInputs(network, inputs, bitboard, filler);
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
