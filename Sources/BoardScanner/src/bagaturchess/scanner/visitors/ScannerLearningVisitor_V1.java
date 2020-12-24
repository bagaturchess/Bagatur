/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.scanner.visitors;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.visrec.ml.data.DataSet;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.scanner.utils.ImageProperties;
import bagaturchess.scanner.utils.ScannerUtils;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.data.MLDataItem;
import deepnetts.data.TabularDataSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class ScannerLearningVisitor_V1 implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private long startTime;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	
	private ImageProperties imageProperties;
	
	
	private static final String NET_FILE = "scanner.bin";
	private ConvolutionalNetwork network;
	
	private BackpropagationTrainer trainer;
	
	
	public ScannerLearningVisitor_V1() throws Exception {
		
		imageProperties = new ImageProperties();
		
		if ((new File(NET_FILE)).exists() ){
			
			System.out.println("Loading network ...");
			
			
			network = (ConvolutionalNetwork) FileIO.createFromFile(new File(NET_FILE));
			
			
			System.out.println("Network loaded.");
			
		} else {
			
			System.out.println("Creating network ...");
			
			
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(imageProperties.IMAGE_SIZE, imageProperties.IMAGE_SIZE, 1)
	                .addConvolutionalLayer(3, 3, 32)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(3, 3, 16)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(3, 3, 16)
	                .addOutputLayer(64 * 13, ActivationType.SIGMOID)
	                .hiddenActivationFunction(ActivationType.RELU)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(123)
	                .build();
			
            
			System.out.println("Network created.");
		}
		
		trainer = new BackpropagationTrainer(network);
		
		trainer.setLearningRate(0.001f);
        //trainer.setLearningRate(0.00001f);//For ActivationType.LINEAR
        
        trainer.setMaxEpochs(1);
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
        
		BufferedImage image = ScannerUtils.createBoardImage(imageProperties, bitboard.toEPD());
		image = ScannerUtils.convertToGrayScale(image);
		//ScannerUtils.saveImage(bitboard.toEPD(), image);
		float[] expected_input = ScannerUtils.convertToFlatGrayArray(image);
		float[] expected_output = ScannerUtils.createOutputArray(bitboard);
		
		network.setInput(new Tensor(expected_input));
		network.forward();
		float[] actual_output = network.getOutput();
		
		//String fen = ScannerUtils.convertOutputToFEN(actual_output);
		//System.out.println(fen + " < " + bitboard.toEPD());
		
		sumDiffs1 += sumExpectedOutput(expected_output);
		sumDiffs2 += sumDeltaOutput(expected_output, actual_output);
		
		DataSet<MLDataItem> list = new DataSet() {

			@Override
			public List getItems() {
				List result = new ArrayList<>();
				result.add(new TabularDataSet.Item(expected_input, expected_output));
				return result;
			}

			@Override
			public DataSet[] split(double... parts) {
				throw new IllegalStateException();
			}

			@Override
			public String[] getTargetNames() {
				String[] result = new String[64 * 13];
				
				for (int i = 0; i < result.length; i++) {
					result[i] = "LABEL" + i;
				}
						
				return result;
			}

			@Override
			public void setColumnNames(String[] columnNames) {
				throw new IllegalStateException();
			}

			@Override
			public String[] getColumnNames() {
				throw new IllegalStateException();
			}
		};
		
		trainer.train(list);
		
		counter++;
		if ((counter % 100) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, error " + network.getLossFunction().getTotal());

			try {
				FileIO.writeToFile(network, NET_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private double sumExpectedOutput(float[] expected_output) {
		double sum = 0;
		for (int i = 0; i < expected_output.length; i++) {
			sum += Math.abs(0 - expected_output[i]);
		}
		return sum;
	}
	
	
	private double sumDeltaOutput(float[] expected_output, float[] actual_output) {
		double sum = 0;
		for (int i = 0; i < expected_output.length; i++) {
			sum += Math.abs(expected_output[i] - actual_output[i]);
		}
		return sum;
	}
	
	
	@Override
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		
		sumDiffs1 = 1;
		sumDiffs2 = 1;
	}
	
	
	@Override
	public void end() {
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		//network.save(NET_FILE);
	}
}
