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

import javax.imageio.ImageIO;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.nnet.ConvolutionalNetwork;
import org.neuroph.nnet.learning.ConvolutionalBackpropagation;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.utils.ScannerUtils;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class ScannerLearningVisitor implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private long startTime;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private String PIECES_SET = "set1";
	
	private Image[] piecesImages = new Image[13];
	
	private int IMAGE_SIZE = 64;
	private int SQUARE_SIZE = IMAGE_SIZE / 8;
	
	private Color BLACK_SQUARE = new Color(120, 120, 120);
	private Color WHITE_SQUARE = new Color(220, 220, 220);
	
	private static final String NET_FILE = "scanner.bin";
	private ConvolutionalNetwork network;
	
	
	public ScannerLearningVisitor() throws Exception {
		
		loadPiecesImages();
		
		if ((new File(NET_FILE)).exists() ){
			
			network = (ConvolutionalNetwork) NeuralNetwork.createFromFile(NET_FILE);
			
		} else {
			
			network = new ConvolutionalNetwork.Builder()
					.withInputLayer(IMAGE_SIZE, IMAGE_SIZE, 3)
                    .withConvolutionLayer(8, 8, 1)
                    .withPoolingLayer(2, 2)
                    .withConvolutionLayer(8, 8, 1)
                    .withPoolingLayer(2, 2)
                    .withFullConnectedLayer(64 * 13)
                    .build();
			
            ConvolutionalBackpropagation backPropagation = new ConvolutionalBackpropagation();
            backPropagation.setLearningRate(1);
            
            //backPropagation.setMaxError(maxError);
            backPropagation.setMaxIterations(1);
            backPropagation.addListener(new LearningEventListener() {
				@Override
				public void handleLearningEvent(LearningEvent arg0) {
					//System.out.println("handleLearningEvent");
				}
			});
            backPropagation.setErrorFunction(new MeanSquaredError());

            network.setLearningRule(backPropagation);
		}
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
        
		BufferedImage image = createBoardImage(bitboard.toEPD());
		//saveImage(bitboard.toEPD(), image);
		double[] expected_input = convertToFlatRGBArray(image);
		double[] expected_output = createOutputArray(bitboard);
		
		network.setInput(expected_input);
		network.calculate();
		double[] actual_output = network.getOutput();
		
		//String fen = ScannerUtils.convertOutputToFEN(actual_output);
		//System.out.println(fen + " > " + bitboard.toEPD());
		
		sumDiffs1 += sumExpectedOutput(expected_output);
		sumDiffs2 += sumDeltaOutput(expected_output, actual_output);
		
		DataSet dataset = new DataSet(expected_input.length, expected_output.length);
		dataset.add(new DataSetRow(expected_input, expected_output));
		network.learn(dataset);
		
		counter++;
		if ((counter % 100) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%" + ", network error is " + network.getLearningRule().getTotalNetworkError());

			network.save(NET_FILE);
		}
	}


	private double sumExpectedOutput(double[] expected_output) {
		double sum = 0;
		for (int i = 0; i < expected_output.length; i++) {
			sum += Math.abs(0 - expected_output[i]);
		}
		return sum;
	}
	
	
	private double sumDeltaOutput(double[] expected_output, double[] actual_output) {
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
	
	
	private double[] createOutputArray(IBitBoard bitboard) {
		
		double[] result = new double[64 * 13];
		{
			long bb_w_king = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING);
			long bb_w_queens = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN);
			long bb_w_rooks = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK);
			long bb_w_bishops = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP);
			long bb_w_knights = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT);
			long bb_w_pawns = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN);
			
			int squareID_w_king = Long.numberOfTrailingZeros(bb_w_king);
			result[64 * Constants.PID_W_KING + squareID_w_king] = 1;
			
	        while (bb_w_pawns != 0) {
	        	int squareID_pawn = Long.numberOfTrailingZeros(bb_w_pawns);
	        	result[64 * Constants.PID_W_PAWN + squareID_pawn] = 1;
	        	bb_w_pawns &= bb_w_pawns - 1;
	        }
	        
	        while (bb_w_knights != 0) {
	        	int squareID_knight = Long.numberOfTrailingZeros(bb_w_knights);
	        	result[64 * Constants.PID_W_KNIGHT + squareID_knight] = 1;
	        	bb_w_knights &= bb_w_knights - 1;
	        }
	        
	        while (bb_w_bishops != 0) {
	        	int squareID_bishop = Long.numberOfTrailingZeros(bb_w_bishops);
	        	result[64 * Constants.PID_W_BISHOP + squareID_bishop] = 1;
	        	bb_w_bishops &= bb_w_bishops - 1;
	        }
	        
	        while (bb_w_rooks != 0) {
	        	int squareID_rook = Long.numberOfTrailingZeros(bb_w_rooks);
	        	result[64 * Constants.PID_W_ROOK + squareID_rook] = 1;
	        	bb_w_rooks &= bb_w_rooks - 1;
	        }
	        
	        while (bb_w_queens != 0) {
	        	int squareID_queen = Long.numberOfTrailingZeros(bb_w_queens);
	        	result[64 * Constants.PID_W_QUEEN + squareID_queen] = 1;
	        	bb_w_queens &= bb_w_queens - 1;
	        }
		}
        
		{
			long bb_b_king = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING);
			long bb_b_queens = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN);
			long bb_b_rooks = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK);
			long bb_b_bishops = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP);
			long bb_b_knights = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT);
			long bb_b_pawns = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN);
			
			int squareID_b_king = Long.numberOfTrailingZeros(bb_b_king);
			result[64 * Constants.PID_B_KING + squareID_b_king] = 1;
			
	        while (bb_b_pawns != 0) {
	        	int squareID_pawn = Long.numberOfTrailingZeros(bb_b_pawns);
	        	result[64 * Constants.PID_B_PAWN + squareID_pawn] = 1;
	        	bb_b_pawns &= bb_b_pawns - 1;
	        }
	        
	        while (bb_b_knights != 0) {
	        	int squareID_knight = Long.numberOfTrailingZeros(bb_b_knights);
	        	result[64 * Constants.PID_B_KNIGHT + squareID_knight] = 1;
	        	bb_b_knights &= bb_b_knights - 1;
	        }
	        
	        while (bb_b_bishops != 0) {
	        	int squareID_bishop = Long.numberOfTrailingZeros(bb_b_bishops);
	        	result[64 * Constants.PID_B_BISHOP + squareID_bishop] = 1;
	        	bb_b_bishops &= bb_b_bishops - 1;
	        }
	        
	        while (bb_b_rooks != 0) {
	        	int squareID_rook = Long.numberOfTrailingZeros(bb_b_rooks);
	        	result[64 * Constants.PID_B_ROOK + squareID_rook] = 1;
	        	bb_b_rooks &= bb_b_rooks - 1;
	        }
	        
	        while (bb_b_queens != 0) {
	        	int squareID_queen = Long.numberOfTrailingZeros(bb_b_queens);
	        	result[64 * Constants.PID_B_QUEEN + squareID_queen] = 1;
	        	bb_b_queens &= bb_b_queens - 1;
	        }
		}
        
		return result;
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
	
	
	private double[] convertToFlatRGBArray(BufferedImage image) {
		int count = 0;
		double[] inputs = new double[3 * IMAGE_SIZE * IMAGE_SIZE];
		for (int i = 0; i < IMAGE_SIZE; i++) {
			for (int j = 0; j < IMAGE_SIZE; j++) {
				
				int rgb = image.getRGB(i, j);
				
				//int alpha = (rgb & 0xff000000) >>> 24;
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				
				inputs[count + 0] = red;
				inputs[count + 1] = green;
				inputs[count + 2] = blue;
				count += 3;
			}
		}
		return inputs;
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
	
	
	private void saveImage(String fen, BufferedImage image) {
		try {
			File file = new File("./data/" + (fen + ".jpg").replace('/', '_'));
			ImageIO.write(image, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
