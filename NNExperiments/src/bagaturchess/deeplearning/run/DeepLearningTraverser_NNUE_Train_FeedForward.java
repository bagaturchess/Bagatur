package bagaturchess.deeplearning.run;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.visitors.DeepLearningVisitorImpl_NNUE_PrintSuccessRate_FeedForward;
import bagaturchess.deeplearning.impl_nnue.visitors.DeepLearningVisitorImpl_NNUE_Train_FeedForward;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class DeepLearningTraverser_NNUE_Train_FeedForward {
	
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		
		long startTime = System.currentTimeMillis();
		
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			//String filePath = "./stockfish-14.1.cg";
			//String filePath = "./glaurung-2.2.cg";
			//String filePath = "./NNUE_big.cg";
			String filePath = "./NNUE.cg";
			
			
			FeedForwardNetwork network = createFeedforwardNetwork();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			int iteration = 0;
			
			while (true) {
				
				iteration++;
				
				System.out.println("Iteration: " + iteration);
				
				PositionsVisitor trainer = new DeepLearningVisitorImpl_NNUE_Train_FeedForward(network);
				
				PositionsTraverser.traverseAll(filePath, trainer, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
				
				saveNetwork(network);
				
				PositionsVisitor success_rate_printer = new DeepLearningVisitorImpl_NNUE_PrintSuccessRate_FeedForward(ActivationFunction.SIGMOID);
				
				PositionsTraverser.traverseAll(filePath, success_rate_printer, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
	
	
	private static FeedForwardNetwork createFeedforwardNetwork() {
		
		FeedForwardNetwork network = FeedForwardNetwork.builder()
				.addInputLayer(768)
				.addFullyConnectedLayer(4)
				//.addFullyConnectedLayer(4)
				//.addFullyConnectedLayer(2)
				//.addFullyConnectedLayer(1)
				//.addConvolutionalLayer(2, 2, 15)
				//.addConvolutionalLayer(4, 4, 15)
				//.addConvolutionalLayer(8, 8, 15)
				//.addMaxPoolingLayer(2, 2, 1)
				//.addConvolutionalLayer(3, 3, 15)
				//.addMaxPoolingLayer(2, 2, 1)
				.hiddenActivationFunction(ActivationType.TANH)
				.randomSeed(135)
				.addOutputLayer(1, ActivationType.SIGMOID)
				.lossFunction(LossType.CROSS_ENTROPY)
				.build();
		
		network.getTrainer().setLearningRate(1f);
		
		network.getTrainer().setBatchMode(true);
		//network.getTrainer().setBatchSize(CHUNK_SIZE);
		
		//throw new IlrlegalStateException();
		
		
		network.getTrainer().addListener(new TrainingListener() {
			
			int epoch = 1;
			
			@Override
			public void handleEvent(TrainingEvent event) {
				
				if (event.getType() == TrainingEvent.Type.EPOCH_FINISHED) {
					
					event.getSource().stop();
					
					/*System.out.println(
							"Epoch: " + epoch
							//+ ", Accuracy: " + event.getSource().getTrainingAccuracy()
							//+ ", Loss: " + event.getSource().getTrainingLoss()
						);
					*/
					
					epoch++;
				}
			}
		});
		
		return network;
	}
	
	
	private static void saveNetwork(NeuralNetwork<?> network) {
		
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NNUE_Constants.NET_FILE));
			
			oos.writeObject(network);
			
			oos.close();			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}