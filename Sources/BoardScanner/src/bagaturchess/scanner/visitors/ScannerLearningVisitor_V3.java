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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.visrec.ml.data.DataSet;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
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
	
	
	private int IMAGE_SIZE = 64;
	private int SQUARE_SIZE = IMAGE_SIZE / 8;
	
	private String PIECES_SET = "set1";
	private Image[] piecesImages = new Image[13];
	
	private Color BLACK_SQUARE = new Color(120, 120, 120);
	private Color WHITE_SQUARE = new Color(220, 220, 220);
	
	
	private static final String NET_FILE = "scanner.bin";
	private ConvolutionalNetwork network;
	
	private BackpropagationTrainer trainer;
	private ScannerDataSet dataset;
	
	
	public ScannerLearningVisitor_V3() throws Exception {
		
		loadPiecesImages();
		
		if ((new File(NET_FILE)).exists() ){
			
			System.out.println("Loading network ...");
			
			
			network = (ConvolutionalNetwork) FileIO.createFromFile(new File(NET_FILE));
			
			
			System.out.println("Network loaded.");
			
		} else {
			
			System.out.println("Creating network ...");
			
			
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(IMAGE_SIZE, IMAGE_SIZE, 1)
	                .addConvolutionalLayer(3, 3, 64)
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
			
			BufferedImage image = createBoardImage(fen);
			image = ScannerUtils.convertToGrayScale(image);
			//ScannerUtils.saveImage(bitboard.toEPD(), image);
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
	
	
	private BufferedImage createBoardImage(String fen) {
		
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if ((i + j) % 2 == 0) {
					g.setColor(WHITE_SQUARE);
				} else {
					g.setColor(BLACK_SQUARE);
				}
				
				g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
		
		String[] fenArray = fen.split(" ");
		int positionCount = 63;
		for (int i = 0; i < fenArray[0].length(); i++) {

			int x = (7 - positionCount % 8) * SQUARE_SIZE;
			int y = (7 - positionCount / 8) * SQUARE_SIZE;
			boolean whiteSquare = (7 - positionCount % 8 + 7 - positionCount / 8) % 2 == 0;
					
			final char character = fenArray[0].charAt(i);
			switch (character) {
			case '/':
				continue;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
				positionCount -= Character.digit(character, 10);
				break;
			case 'P':
				g.drawImage(piecesImages[Constants.PID_W_PAWN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'N':
				g.drawImage(piecesImages[Constants.PID_W_KNIGHT], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'B':
				g.drawImage(piecesImages[Constants.PID_W_BISHOP], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'R':
				g.drawImage(piecesImages[Constants.PID_W_ROOK], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'Q':
				g.drawImage(piecesImages[Constants.PID_W_QUEEN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'K':
				g.drawImage(piecesImages[Constants.PID_W_KING], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'p':
				g.drawImage(piecesImages[Constants.PID_B_PAWN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'n':
				g.drawImage(piecesImages[Constants.PID_B_KNIGHT], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'b':
				g.drawImage(piecesImages[Constants.PID_B_BISHOP], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'r':
				g.drawImage(piecesImages[Constants.PID_B_ROOK], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'q':
				g.drawImage(piecesImages[Constants.PID_B_QUEEN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'k':
				g.drawImage(piecesImages[Constants.PID_B_KING], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			}
		}
		
		return image;
	}
	
	
	private void loadPiecesImages() throws IOException{
		
		piecesImages[Constants.PID_W_KING] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_k.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_QUEEN] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_q.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_ROOK] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_r.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_BISHOP] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_b.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_KNIGHT] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_n.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_PAWN] = ImageIO.read(new File("./res/" + PIECES_SET + "_w_p.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		
		piecesImages[Constants.PID_B_KING] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_k.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_QUEEN] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_q.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_ROOK] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_r.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_BISHOP] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_b.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_KNIGHT] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_n.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_PAWN] = ImageIO.read(new File("./res/" + PIECES_SET + "_b_p.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
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
