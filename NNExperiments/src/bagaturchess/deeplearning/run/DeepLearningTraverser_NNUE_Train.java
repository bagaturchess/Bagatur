package bagaturchess.deeplearning.run;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import bagaturchess.deeplearning.impl_nnue.NNUE_Constants;
import bagaturchess.deeplearning.impl_nnue.visitors.DeepLearningVisitorImpl_NNUE_DataSetLoader;
import bagaturchess.deeplearning.impl_nnue.visitors.DeepLearningVisitorImpl_NNUE_PrintSuccessRate;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;


public class DeepLearningTraverser_NNUE_Train {
	
	
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
			
			
			DeepLearningVisitorImpl_NNUE_DataSetLoader loader = new DeepLearningVisitorImpl_NNUE_DataSetLoader();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			PositionsTraverser.traverseAll(filePath, loader, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
			
			ConvolutionalNetwork network = createConvolutionalNetwork();
			
			while (true) {
				
				network.getTrainer().train(loader.getDataSet());
				
				saveNetwork(network);
				
				DeepLearningVisitorImpl_NNUE_PrintSuccessRate printer = new DeepLearningVisitorImpl_NNUE_PrintSuccessRate();
				
				PositionsTraverser.traverseAll(filePath, printer, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
	
	
	private static ConvolutionalNetwork createConvolutionalNetwork() {
		
		ConvolutionalNetwork network = ConvolutionalNetwork.builder()
				.addInputLayer(8, 8, 15)
				.addFullyConnectedLayer(64)
				//.addConvolutionalLayer(2, 2, 15)
				//.addConvolutionalLayer(4, 4, 15)
				//.addConvolutionalLayer(8, 8, 15)
				.hiddenActivationFunction(ActivationType.TANH)
				//.addMaxPoolingLayer(2, 2, 1)
				//.addConvolutionalLayer(3, 3, 15)
				//.addMaxPoolingLayer(2, 2, 1)
				.addOutputLayer(1, ActivationType.SIGMOID)
				.build();
	
		network.getTrainer().setLearningRate(1f);
		
		network.getTrainer().setBatchMode(true);
		//network.getTrainer().setBatchSize(10000);
		
		//throw new IlrlegalStateException();
		
		
		network.getTrainer().addListener(new TrainingListener() {
			
			int epoch = 1;
			
			@Override
			public void handleEvent(TrainingEvent event) {
				
				if (event.getType() == TrainingEvent.Type.EPOCH_FINISHED) {
					
					event.getSource().stop();
					
					System.out.println(
							"Epoch: " + epoch
							+ ", Accuracy: " + event.getSource().getTrainingAccuracy()
							+ ", Loss: " + event.getSource().getTrainingLoss()
						);
					
					epoch++;
				}
			}
		});
		
		return network;
	}
	
	
	private static void saveNetwork(NeuralNetwork network) {
		
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NNUE_Constants.NET_FILE));
			
			oos.writeObject(network);
			
			oos.close();			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
