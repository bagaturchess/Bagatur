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
package bagaturchess.scanner.patterns.impl1.preprocess;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.common.ResultPair;


public class ImagePreProcessing {
	
	
	private static final double SIZE_DELTA_PERCENT = 0.1;
	private static final int MAX_ROTATION_PERCENT = 0;
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256, "set3");
			
			BufferedImage image = ImageIO.read(new File("./data/tests/preprocess/test7.png"));
			image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
			int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
			
			Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard);
			ResultPair<Integer, Integer> bgcolours = MatrixUtils.getSquaresColor(grayBoard, emptySquares);
			
			//int[][] whiteSquare = ScannerUtils.createSquareImage(bgcolours.getFirst(), boardProperties.getImageSize());
			//int[][] blackSquare = ScannerUtils.createSquareImage(bgcolours.getSecond(), boardProperties.getImageSize());
			//ScannerUtils.saveImage("white", ScannerUtils.createGrayImage(whiteSquare), "png");
			//ScannerUtils.saveImage("black", ScannerUtils.createGrayImage(blackSquare), "png");
			
			Color whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(bgcolours.getFirst());
			Color blackSquareColor = ImageHandlerSingleton.getInstance().getColor(bgcolours.getSecond());
			
			BufferedImage emptyBoard = ImageHandlerSingleton.getInstance().createBoardImage(boardProperties, "8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);
			ImageHandlerSingleton.getInstance().saveImage("board_empty", "png", emptyBoard);
			
			//image = ScannerUtils.enlarge(image, boardProperties.getImageSize(), 1.125f);
			//grayBoard = ScannerUtils.convertToGrayMatrix(image);
			ImageHandlerSingleton.getInstance().saveImage("board_input", "png", ImageHandlerSingleton.getInstance().createGrayImage(grayBoard));
			
			MatrixUtils.PatternMatchingData bestData = null;
			int maxSize = grayBoard.length;
			int startSize = (int) ((1 - SIZE_DELTA_PERCENT) * maxSize);
			for (int size = startSize; size <= maxSize; size++) {
				for (int angle = -MAX_ROTATION_PERCENT; angle <= MAX_ROTATION_PERCENT; angle++) {
					
					//BufferedImage resizedGrayPattern = ScannerUtils.resizeImage(emptyBoard,(int) (0.9 * size));
					//BufferedImage enlargedGrayPattern = ScannerUtils.enlarge(resizedGrayPattern, resizedGrayPattern.getHeight(), 1.1f);
					//int[][] grayPattern = ScannerUtils.convertToGrayMatrix(enlargedGrayPattern);
					
					int[][] grayPattern = ImageHandlerSingleton.getInstance().convertToGrayMatrix(ImageHandlerSingleton.getInstance().resizeImage(emptyBoard, size));
					
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
			
			BufferedImage result = ImageHandlerSingleton.getInstance().extractResult(image, bestData);
			result = ImageHandlerSingleton.getInstance().enlarge(result, result.getWidth(), 1.03f, ImageHandlerSingleton.getInstance().getAVG(result));
			ImageHandlerSingleton.getInstance().saveImage("result_" + bestData.size + "_" + bestData.angle + "_" + bestData.delta, "png", result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
