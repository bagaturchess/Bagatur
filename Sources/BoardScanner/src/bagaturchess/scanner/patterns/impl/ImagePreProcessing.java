/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.patterns.impl;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.MatrixUtils.PatternMatchingData;
import bagaturchess.scanner.common.ResultPair;


public class ImagePreProcessing {
	
	
	private static final double SIZE_DELTA_PERCENT = 0.1;
	private static final int MAX_ROTATION_PERCENT = 0;
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256, "set3");
			
			BufferedImage image = ImageIO.read(new File("./data/tests/preprocess/test7.png"));
			image = ScannerUtils.resizeImage(image, boardProperties.getImageSize());
			int[][] grayBoard = ScannerUtils.convertToGrayMatrix(image);
			
			Set<Integer> emptySquares = ScannerUtils.getEmptySquares(grayBoard);
			ResultPair<Integer, Integer> bgcolours = ScannerUtils.getSquaresColor(grayBoard, emptySquares);
			
			//int[][] whiteSquare = ScannerUtils.createSquareImage(bgcolours.getFirst(), boardProperties.getImageSize());
			//int[][] blackSquare = ScannerUtils.createSquareImage(bgcolours.getSecond(), boardProperties.getImageSize());
			//ScannerUtils.saveImage("white", ScannerUtils.createGrayImage(whiteSquare), "png");
			//ScannerUtils.saveImage("black", ScannerUtils.createGrayImage(blackSquare), "png");
			
			Color whiteSquareColor = ScannerUtils.GRAY_COLORS[bgcolours.getFirst()];
			Color blackSquareColor = ScannerUtils.GRAY_COLORS[bgcolours.getSecond()];
			
			BufferedImage emptyBoard = ScannerUtils.createBoardImage(boardProperties, "8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);
			ScannerUtils.saveImage("board_empty", emptyBoard, "png");
			
			//image = ScannerUtils.enlarge(image, boardProperties.getImageSize(), 1.125f);
			//grayBoard = ScannerUtils.convertToGrayMatrix(image);
			ScannerUtils.saveImage("board_input", ScannerUtils.createGrayImage(grayBoard), "png");
			
			MatrixUtils.PatternMatchingData bestData = null;
			int maxSize = grayBoard.length;
			int startSize = (int) ((1 - SIZE_DELTA_PERCENT) * maxSize);
			for (int size = startSize; size <= maxSize; size++) {
				for (int angle = -MAX_ROTATION_PERCENT; angle <= MAX_ROTATION_PERCENT; angle++) {
					
					//BufferedImage resizedGrayPattern = ScannerUtils.resizeImage(emptyBoard,(int) (0.9 * size));
					//BufferedImage enlargedGrayPattern = ScannerUtils.enlarge(resizedGrayPattern, resizedGrayPattern.getHeight(), 1.1f);
					//int[][] grayPattern = ScannerUtils.convertToGrayMatrix(enlargedGrayPattern);
					
					int[][] grayPattern = ScannerUtils.convertToGrayMatrix(ScannerUtils.resizeImage(emptyBoard, size));
					
					if (angle != 0) {
						grayPattern = MatrixUtils.rotateMatrix(grayPattern, angle);
					}
					
					MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(grayBoard, grayPattern);
					
					if (bestData == null || bestData.delta > curData.delta) {
						bestData = curData;
					}
				}
			}
			//https://stackoverflow.com/questions/13390238/jtransforms-fft-on-image
			//matched filter in signal processing
			//https://stackoverflow.com/questions/12598818/finding-a-picture-in-a-picture-with-java
			//https://stackoverflow.com/questions/42597094/cross-correlation-with-signals-of-different-lengths-in-java
			//https://stackoverflow.com/questions/13445497/correlation-among-2-images
			
			BufferedImage result = extractResult(image, bestData);
			result = ScannerUtils.enlarge(result, result.getWidth(), 1.03f, ScannerUtils.getAVG(result));
			ScannerUtils.saveImage("result_" + bestData.size + "_" + bestData.angle + "_" + bestData.delta, result, "png");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static BufferedImage extractResult(BufferedImage image, PatternMatchingData matcherData) {
		
		int[][][] print = new int[matcherData.size][matcherData.size][3];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				int rgb = image.getRGB(matcherData.x + i, matcherData.y + j);
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				//int gray = (int) (red * 0.2989d + green * 0.5870 + blue * 0.1140);
				print[i][j][0] = red;
				print[i][j][1] = green;
				print[i][j][2] = blue;
			}
		}
		
		return ScannerUtils.createRGBImage(print);
	}


	protected static void printInfo_RGB(BufferedImage image, MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][][] print = new int[matcherData.size][matcherData.size][3];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				int rgb = image.getRGB(matcherData.x + i, matcherData.y + j);
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				//int gray = (int) (red * 0.2989d + green * 0.5870 + blue * 0.1140);
				print[i][j][0] = red;
				print[i][j][1] = green;
				print[i][j][2] = blue;
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createRGBImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	protected static void printInfo(int[][] board, MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = board[matcherData.x + i][matcherData.y + j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	protected static void printInfo(MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = matcherData.pattern[i][j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
}
