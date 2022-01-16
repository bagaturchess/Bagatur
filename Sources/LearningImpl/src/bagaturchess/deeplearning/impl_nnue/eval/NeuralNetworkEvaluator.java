package bagaturchess.deeplearning.impl_nnue.eval;


import java.io.FileInputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
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
	
	float[][][] inputs_3d = new float[8][8][15];
	
	
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
		
		inputs_3d[0][0][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		inputs_3d[0][1][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_WHITE) ? 1 : 0;
		inputs_3d[0][2][12] = bitboard.hasRightsToQueenCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		inputs_3d[0][3][12] = bitboard.hasRightsToKingCastle(Constants.COLOUR_BLACK) ? 1 : 0;
		
		int moves_before_draw = bitboard.getDraw50movesRule() - 37;
		
		if (moves_before_draw >= 0) {
			
			int file = moves_before_draw & 7;
			int rank = moves_before_draw >>> 3;
			
			inputs_3d[file][rank][13] = 1;
		}
		
		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
			
			inputs_3d[0][0][14] = 1;
			inputs_3d[0][1][14] = 0;
			
		} else {
			
			inputs_3d[0][0][14] = 0;
			inputs_3d[0][1][14] = 1;
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
