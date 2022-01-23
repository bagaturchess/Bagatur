package bagaturchess.selfplay.train;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.visitors.DataSet_1;
import bagaturchess.search.api.IEvaluator;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class Trainer_Base implements Trainer {
	
	
	private static final float LEARNING_RATE 		= 1f;//0.001f;
	
	
	private String filename_NN;
	
	private DataSet_1 dataset;
	
	private List<Tensor> inputs_per_move;
	
	private ActivationFunction activation_function = ActivationFunction.SIGMOID;
	
	
	public Trainer_Base(String _filename_NN) {
		
		filename_NN = _filename_NN;
		
		if (!(new File(filename_NN)).exists()) {
			
			throw new IllegalStateException("NN file not found: " + filename_NN);
			
		}
		
		inputs_per_move = new ArrayList<Tensor>();
	}
	
	
	@Override
	public void clear() {
		
		inputs_per_move.clear();
		
		dataset = new DataSet_1();
	}
	
	
	@Override
	public void doEpoch() throws Exception {
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename_NN));
		
		NeuralNetwork network = (NeuralNetwork) ois.readObject();
		
		ois.close();
		
        // create a trainer and train network
		BackpropagationTrainer trainer = (BackpropagationTrainer) network.getTrainer();
        
        trainer.setMaxEpochs(1)
        		.setLearningRate(LEARNING_RATE)
                .setMaxError(0.000001f)
                ;
                //.setBatchMode(true)
                //.setBatchSize(1000);
        
		
		
        trainer.train(dataset);
		
		
		FileIO.writeToFile(network, filename_NN);
	}
	
	
	@Override
	public void addBoardPosition(IBitBoard bitboard) {
		
		float[][][] inputs_3d = new float[8][8][15];
		
		Tensor tensor = NNUE_Constants.createInput(bitboard, inputs_3d);
		
		inputs_per_move.add(tensor);
	}
	
	
	@Override
	public void setGameOutcome(float game_result) {
		
		boolean draw = game_result == 0;
		
		boolean white_win = game_result == 1;
		
		float step;
		
		if (draw) {
			
			step = 0;
					
		} else if (white_win) {
			
			step = +(float) ((activation_function.gety(IEvaluator.MAX_EVAL) - 0.5) / (float) inputs_per_move.size());
			
		} else {
			
			step = -(float) ((0.5 - activation_function.gety(IEvaluator.MIN_EVAL)) / (float) inputs_per_move.size());
		}
		
		for (int i = 0; i < inputs_per_move.size(); i++) {
			
	        float[] output = new float[1];
	        
	        output[0] = (float) (0.5 + i * step);
	        
	        dataset.addItem(inputs_per_move.get(i), output);
		}
	}
}
