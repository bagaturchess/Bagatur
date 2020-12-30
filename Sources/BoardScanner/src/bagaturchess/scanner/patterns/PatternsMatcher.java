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
package bagaturchess.scanner.patterns;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.cnn.impl.ImageProperties;
import bagaturchess.scanner.cnn.impl.utils.MatrixUtils;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class PatternsMatcher {
	
	
	public static void main(String[] args) {
		
		try {
			
			ImageProperties imageProperties = new ImageProperties(256, "set1");
			
			BufferedImage image_board = ImageIO.read(new File("./data/tests/lichess.org/test1.png"));
			image_board = ScannerUtils.resizeImage(image_board, imageProperties.getImageSize());
			image_board = ScannerUtils.convertToGrayScale(image_board);
			//ScannerUtils.saveImage("board", image_board, "png");
			int[][] board = ScannerUtils.convertToGrayMatrix(image_board);
			board = transformPattern(board);
			
			BufferedImage resultImage = ScannerUtils.createGrayImage(board);
			ScannerUtils.saveImage("board", resultImage, "png");
			
			/*int[][] rotatedBoard = rotateMatrix(board, 0);
			BufferedImage resultImage = ScannerUtils.createGrayImage(rotatedBoard);
			ScannerUtils.saveImage("board_rotated", resultImage, "png");
			board = rotatedBoard;*/
			
			for (int pid = 1; pid <= 12; pid++) {
				MatrixUtils.PatternMatchingData matcherData = matchImages(board,
	            		imageProperties.getPiecesImages()[pid],
	            		ScannerUtils.getAVG(image_board),
	            		imageProperties.getSquareSize(),
	            		0.1f, 0);
	            
				printInfo(board, matcherData, pid + "_" + matcherData.size + "_" + matcherData.angle);   
			}
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void printInfo(int[][] board, MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = board[matcherData.x + i][matcherData.y + j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	private static final MatrixUtils.PatternMatchingData matchImages(int[][] graySource, Image pieceImage, Color bgcolor, int maxSize, float sizeDeltaPercent, int rotationAngleInDegrees) {
		
		MatrixUtils.PatternMatchingData result = new MatrixUtils.PatternMatchingData();
		result.delta = Double.MAX_VALUE;
		
		int startSize = (int) ((1 - sizeDeltaPercent) * maxSize);
		
		for (int size = startSize; size <= maxSize; size++) {
			for (int angle = -rotationAngleInDegrees; angle <= rotationAngleInDegrees; angle++) {
				
				BufferedImage image_piece = createPattern(pieceImage, size, bgcolor);
				int[][] grayPiece = ScannerUtils.convertToGrayMatrix(image_piece);
				if (angle != 0) {
					grayPiece = MatrixUtils.rotateMatrix(grayPiece, angle);
				}
				grayPiece = transformPattern(grayPiece);
				
				
				//BufferedImage resultImage = ScannerUtils.createGrayImage(grayPiece);
				//ScannerUtils.saveImage(size + "_" + grayPiece.toString(), resultImage, "png");
				
				MatrixUtils.PatternMatchingData matcherData = MatrixUtils.matchImages(graySource, grayPiece);
				matcherData.angle = angle;
				
				if (result.delta > matcherData.delta) {
					result = matcherData;
				}
			}
		}
		
		return result;
	}
	
	
	private static int[][] transformPattern(int[][] grayPattern) {
		
		VarStatistic stat = new VarStatistic(false);
		int min = 255;
		int max = 0;
		for (int i = 0; i < grayPattern.length; i++) {
			for (int j = 0; j < grayPattern.length; j++) {
				int cur = grayPattern[i][j];
				if (cur < min) {
					min = cur;
				}
				if (cur > max) {
					max = cur;
				}
				stat.addValue(cur, cur);
			}
		}
		//System.out.println("avg=" + stat.getEntropy() + ", disp=" + stat.getDisperse());
		
		int[][] result = new int[grayPattern.length][grayPattern.length];
		
		for (int i = 0; i < grayPattern.length; i++) {
			for (int j = 0; j < grayPattern.length; j++) {
				int pixel = (int) (1 * stat.getDisperse() + (grayPattern[i][j] - stat.getEntropy()));
				float multiplier = (float) (255 / (float) 2 * stat.getDisperse());
				pixel = (int) (pixel * multiplier);
				pixel = Math.max(0, pixel);
				pixel = Math.min(255, pixel);
				result[i][j] = pixel;
			}
		}
		
		return result;
	}


	private static BufferedImage createPattern(Image piece, int size, Color bgcolor) {
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		piece = piece.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		g.drawImage(piece, 0, 0, null);
		imagePiece = ScannerUtils.convertToGrayScale(imagePiece);
		return imagePiece;
	}
}
