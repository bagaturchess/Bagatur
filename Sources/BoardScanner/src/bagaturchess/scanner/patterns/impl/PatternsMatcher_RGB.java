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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class PatternsMatcher_RGB {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256, "set2");
			
			BufferedImage image_board = ImageIO.read(new File("./data/tests/test11.png"));
			image_board = ScannerUtils.resizeImage(image_board, boardProperties.getImageSize());
			//ScannerUtils.saveImage("board", image_board, "png");
			int[][][] rgbBoard = ScannerUtils.convertToRGBMatrix(image_board);
			//board = transformPattern(board);
			
			BufferedImage resultImage = ScannerUtils.createRGBImage(rgbBoard);
			ScannerUtils.saveImage("board", resultImage, "png");
			
			/*int[][] rotatedBoard = rotateMatrix(board, 0);
			BufferedImage resultImage = ScannerUtils.createGrayImage(rotatedBoard);
			ScannerUtils.saveImage("board_rotated", resultImage, "png");
			board = rotatedBoard;*/
			
			//Set<Integer> emptySquares = ScannerUtils.getEmptySquares(rgbBoard);
			//ResultPair<Integer, Integer> bgcolorsOfSquares = ScannerUtils.getSquaresColor(rgbBoard, emptySquares);
			
			//List<Integer> bgcolors = new ArrayList<Integer>();
			//bgcolors.add(bgcolorsOfSquares.getSecond());
			//bgcolors.add(bgcolorsOfSquares.getFirst());
			//bgcolors.add(ScannerUtils.getAVG(grayBoard));
			
			for (int pid = 1; pid <= 12; pid++) {
			//for (int pid = 5; pid <= 5; pid++) {
				
				MatrixUtils.PatternMatchingData matcherData = matchImages(boardProperties, rgbBoard,
	            		pid,
	            		(Color) ImageHandlerSingleton.getInstance().getAVG(image_board),
	            		boardProperties.getSquareSize(),
	            		0.25f, 0);
	            
				printInfo(rgbBoard, matcherData, "best_" + pid + "_" + matcherData.size + "_" + matcherData.delta);
			}
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void printInfo(MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = matcherData.pattern[i][j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	private static void printInfo(int[][][] board, MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][][] print = new int[matcherData.size][matcherData.size][3];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j][0] = board[matcherData.x + i][matcherData.y + j][0];
				print[i][j][1] = board[matcherData.x + i][matcherData.y + j][1];
				print[i][j][2] = board[matcherData.x + i][matcherData.y + j][2];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createRGBImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	private static final MatrixUtils.PatternMatchingData matchImages(BoardProperties boardProperties, int[][][] graySource, int pid, Color bgcolor, int maxSize, float sizeDeltaPercent, int rotationAngleInDegrees) {
		
		MatrixUtils.PatternMatchingData result = new MatrixUtils.PatternMatchingData();
		result.delta = Double.MAX_VALUE;
		
		int startSize = (int) ((1 - sizeDeltaPercent) * maxSize);
		
		int counter = 0;
		//for (int bgcolor : bgcolors) {
			for (int size = startSize; size <= maxSize; size++) {
				//for (int angle = -rotationAngleInDegrees; angle <= rotationAngleInDegrees; angle++) {
					
					//for (int i = 0; i < graySource.length; i += graySource.length / 8) {
						//for (int j = 0; j < graySource.length; j += graySource.length / 8) {
							
							//int file = i / (graySource.length / 8);
							//int rank = j / (graySource.length / 8);
							//int fieldID = 63 - (file + 8 * rank);
							
							//int bgcolor = (file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond();
									
							//if (!emptySquares.contains(fieldID)) {
								int[][][] grayPiece = ScannerUtils.createPieceImage(boardProperties, pid, bgcolor, size);
								/*if (angle != 0) {
									grayPiece = MatrixUtils.rotateMatrix(grayPiece, angle);
								}*/
								//grayPiece = transformPattern(grayPiece);
								
								
								//BufferedImage resultImage = ScannerUtils.createGrayImage(grayPiece);
								//ScannerUtils.saveImage(size + "_" + grayPiece.toString(), resultImage, "png");
								//int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(graySource, i, j);
								
								MatrixUtils.PatternMatchingData matcherData = MatrixUtils.matchImages(graySource, grayPiece);
								//matcherData.angle = angle;
								
								if (result.delta > matcherData.delta) {
									result = matcherData;
									//printInfo(graySource, matcherData, "matching_" + counter + "_" + matcherData.size + "_" + matcherData.angle + "_source");
									//printInfo(matcherData, "matching_" + counter + "_" + fieldID + "_" + matcherData.size + "_" + matcherData.angle + "_pattern");
									counter++;
									
									//matcherData.x += i;
									//matcherData.y += j;
								}
							//}
						//}
					//}
				//}
			}
		//}
		
		return result;
	}
}
