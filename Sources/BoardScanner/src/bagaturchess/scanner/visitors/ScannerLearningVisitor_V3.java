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

import bagaturchess.bitboard.api.BoardUtils;
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
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.FileIO;


public class ScannerLearningVisitor_V3 implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private long startTime;
	
	
	private ImageProperties imageProperties;
	
	
	private static final String NET_FILE = "scanner.bin";
	private ConvolutionalNetwork network;
	
	private BackpropagationTrainer trainer;
	private ScannerDataSet dataset;
	
	
	public ScannerLearningVisitor_V3() throws Exception {
		
		imageProperties = new ImageProperties();
		
		if ((new File(NET_FILE)).exists() ){
			
			System.out.println("Loading network ...");
			
			
			network = (ConvolutionalNetwork) FileIO.createFromFile(new File(NET_FILE));
			
			
			System.out.println("Network loaded.");
			
		} else {
			
			System.out.println("Creating network ...");
			
			
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(imageProperties.IMAGE_SIZE, imageProperties.IMAGE_SIZE, 1)
	                .addConvolutionalLayer(3, 3, 256)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(3, 3, 64)
	                .addOutputLayer(64 * 14, ActivationType.SIGMOID)
	                .hiddenActivationFunction(ActivationType.LINEAR)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(777)
	                .build();
			
            
			System.out.println("Network created.");
		}
		
		trainer = new BackpropagationTrainer(network);
		
		trainer.setLearningRate(0.0001f);
        
        trainer.setBatchMode(true);
        trainer.setBatchSize(769);
        
        trainer.addListener(new TrainingListener() {
        	
			@Override
			public void handleEvent(TrainingEvent event) {
				
				if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
					
					System.out.println("End iteration " + iteration
							+ ": Time " + (System.currentTimeMillis() - startTime)
							+ "ms, Training loss is " + event.getSource().getTrainingLoss()
							//+ ", Training accuracy is " + event.getSource().getTrainingAccuracy()
							//+ ", Loss function total is " + network.getLossFunction().getTotal()
					);
					
					iteration++;
					
					try {
						FileIO.writeToFile(network, NET_FILE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (event.getType().equals(TrainingEvent.Type.ITERATION_FINISHED)) {
					//System.out.println("done");
				}
			}
		});
        
        dataset = new ScannerDataSet();
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
        
		if (iteration > 1) {
			return;
		}
		
		if (dataset.size() > 0) {
			return;
		}
		
		List<String> fens = ScannerUtils.generateFENsOfSinglePiece();
		for (int i = 0; i < fens.size(); i++) {
			String fen = fens.get(i);
			IBitBoard cur_bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
			
			BufferedImage image = ScannerUtils.createBoardImage(imageProperties, fen);
			image = ScannerUtils.convertToGrayScale(image);
			//ScannerUtils.saveImage(fen, image);
			float[] expected_input = ScannerUtils.convertToFlatGrayArray(image);
			float[] expected_output = ScannerUtils.createOutputArray(cur_bitboard);
			
			dataset.addItem(expected_input, expected_output);
		}
		
		counter++;
	}
	
	
	@Override
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
	}
	
	
	@Override
	public void end() {
		
		System.out.println("Games loaded.");
		
		System.out.println("Start learning ...");
		
		trainer.train(dataset);
	}
	
	
	private static class ScannerDataSet implements DataSet<MLDataItem> {
		
		
		private List<MLDataItem> items;
		private String[] targetNames;
		
		
		private ScannerDataSet() {
			
			items = new ArrayList<MLDataItem>();
			
			targetNames = new String[64 * 13];
			for (int i = 0; i < targetNames.length; i++) {
				targetNames[i] = "LABEL" + i;
			}
		}
		
		
		private void addItem(float[] inputs, float[] outputs) {
			items.add(new TabularDataSet.Item(inputs, outputs));
			//System.out.println("items size is " + items.size());
		}
		
		
		@Override
		public List<MLDataItem> getItems() {
			return items;
		}
		
		
		@Override
		public DataSet<MLDataItem>[] split(double... parts) {
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String[] getTargetNames() {
			return targetNames;
		}
		
		
		@Override
		public void setColumnNames(String[] columnNames) {
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String[] getColumnNames() {
			throw new UnsupportedOperationException();
		}
	}
}
