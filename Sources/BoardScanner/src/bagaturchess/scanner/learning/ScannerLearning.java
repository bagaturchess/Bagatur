package bagaturchess.scanner.learning;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import bagaturchess.scanner.impl.ImageProperties;
import bagaturchess.scanner.impl.ScannerUtils;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class ScannerLearning {
	
	
	private static final String NET_FILE = "scanner.bin";
	private static ConvolutionalNetwork network;
	
	private static BackpropagationTrainer trainer;
	private static ScannerDataSet dataset;
	
	
	public static void main(String[] args) {
		
		try {
			
			ImageProperties imageProperties = new ImageProperties(192, "set1");
			
			BufferedImage boardImage = ImageIO.read(new File("./data/tests/test6.png"));
			boardImage = ScannerUtils.resizeImage(boardImage, imageProperties.getImageSize());
			boardImage = ScannerUtils.convertToGrayScale(boardImage);
			ScannerUtils.saveImage("test6_converted", boardImage);
			
			DataSetInitPair pair = new DataSetInitPair_ByBoardImage(ScannerUtils.convertToGrayMatrix(boardImage));
			
			List<int[]> grayImages = pair.getGrayImages();
			List<Integer> pids = pair.getPIDs();
			
			dataset = new ScannerDataSet();
			for (int i = 0; i < grayImages.size(); i++) {
				float[] networkInput = ScannerUtils.convertInt2Float(grayImages.get(i));
				float[] networkOutput = new float[14];
				networkOutput[pids.get(i)] = 1;
				dataset.addItem(networkInput, networkOutput);
			}
			
			
			if ((new File(NET_FILE)).exists() ){
				
				System.out.println("Loading network ...");
				
				
				network = (ConvolutionalNetwork) FileIO.createFromFile(new File(NET_FILE));
				
				
				System.out.println("Network loaded.");
				
			} else {
				
				System.out.println("Creating network ...");
				
				
				network =  ConvolutionalNetwork.builder()
		                .addInputLayer(imageProperties.getSquareSize(), imageProperties.getSquareSize(), 1)
		                .addConvolutionalLayer(5, 5, 56)
		                .addMaxPoolingLayer(2, 2)
		                .addConvolutionalLayer(5, 5, 14)
		                //.addFullyConnectedLayer(64)
		                .addOutputLayer(14, ActivationType.SOFTMAX)
		                .hiddenActivationFunction(ActivationType.LINEAR)
		                .lossFunction(LossType.CROSS_ENTROPY)
		                .randomSeed(777)
		                .build();
				
	            
				System.out.println("Network created.");
			}
			
			trainer = new BackpropagationTrainer(network);
			
			trainer.setLearningRate(0.001f);
	        
	        trainer.setBatchMode(true);
	        trainer.setBatchSize(grayImages.size());
	        
	        trainer.addListener(new TrainingListener() {
	        	
	        	
	        	private int iteration = 0;
	        	private long startTime = System.currentTimeMillis();
	        	
	        	
				@Override
				public void handleEvent(TrainingEvent event) {
					
					if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
						
						int success = 0;
						int failure = 0;
						for (int i = 0; i < grayImages.size(); i++) {
							
							float[] networkInput = ScannerUtils.convertInt2Float(grayImages.get(i));
							network.setInput(new Tensor(networkInput));
							network.forward();
							float[] actual_output = network.getOutput();
							
							float maxValue = 0;
							int maxIndex = 0;
							for (int j = 0; j < actual_output.length; j++) {
								if (maxValue < actual_output[j]) {
									maxValue = actual_output[j];
									maxIndex = j;
								}
							}
							
							if (maxIndex == pids.get(i)) {
								success++;
							} else {
								failure++;
							}
						}
						
						
						System.out.println("End iteration " + iteration
								+ ": Time " + (System.currentTimeMillis() - startTime)
								+ "ms, Training loss is " + event.getSource().getTrainingLoss()
								+ ", Success is " + success / (float)(success + failure)
						);
						
						iteration++;
						
						if (!Float.isNaN(event.getSource().getTrainingLoss())
								&& !Float.isInfinite(event.getSource().getTrainingLoss())) {
							try {
								FileIO.writeToFile(network, NET_FILE);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else if (event.getType().equals(TrainingEvent.Type.ITERATION_FINISHED)) {
						//System.out.println("done");
					}
				}
			});
	        
	        
	        trainer.train(dataset);
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
