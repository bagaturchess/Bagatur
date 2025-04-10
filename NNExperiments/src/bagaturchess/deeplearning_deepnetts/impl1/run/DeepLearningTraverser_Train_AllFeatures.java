package bagaturchess.deeplearning_deepnetts.impl1.run;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import bagaturchess.deeplearning.ActivationFunction;
import bagaturchess.deeplearning_deepnetts.impl1.eval.NeuralNetworkEvaluator;
import bagaturchess.deeplearning_deepnetts.impl1.visitor.DeepLearningVisitorImpl_PrintSuccessRate_AllFeatures;
import bagaturchess.deeplearning_deepnetts.impl1.visitor.DeepLearningVisitorImpl_Train_AllFeatures;
import bagaturchess.deeplearning_deepnetts.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning_deepnetts.impl_nnue.visitors.DeepLearningVisitorImpl_PrintSuccessRate_FeedForward;
import bagaturchess.deeplearning_deepnetts.impl_nnue.visitors.DeepLearningVisitorImpl_Train_FeedForward;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class DeepLearningTraverser_Train_AllFeatures {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			//String filePath = "./stockfish-10.cg";
			//String filePath = "./stockfish-14.1.cg";
			//String filePath = "./stockfish-14.1-4N.cg";
			//String filePath = "./NNUE.cg";
			String filePath = "./NNUE_big_v2.cg";
			
			FeedForwardNetwork network = createFeedforwardNetwork();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			ActivationFunction output_activation_function = ActivationFunction.SIGMOID;
			
			
			int iteration = 0;
			
			while (true) {

				iteration++;
				
				System.out.println("Iteration: " + iteration);
				
				PositionsVisitor trainer = new DeepLearningVisitorImpl_Train_AllFeatures(network, output_activation_function);
				
				PositionsTraverser.traverseAll(filePath, trainer, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
				
				saveNetwork(network);
				
				PositionsVisitor success_rate_printer = new DeepLearningVisitorImpl_PrintSuccessRate_AllFeatures(output_activation_function);
				
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
				.addInputLayer(55)
				.addFullyConnectedLayer(16)
				//.addFullyConnectedLayer(8)
				//.addFullyConnectedLayer(4)
				//.addFullyConnectedLayer(2)
				.hiddenActivationFunction(ActivationType.SOFTMAX)
				.randomSeed(135)
				.addOutputLayer(1, ActivationType.SIGMOID)
				.lossFunction(LossType.CROSS_ENTROPY)
				.build();
		
		//network.getTrainer().setLearningRate(0.000001f);
		network.getTrainer().setLearningRate(0.1f);
		
		network.getTrainer().setBatchMode(true);
		//network.getTrainer().setBatchSize(CHUNK_SIZE);
		
		
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
			
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(NeuralNetworkEvaluator.NET_NAME));
			
			oos.writeObject(network);
			
			oos.close();			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
