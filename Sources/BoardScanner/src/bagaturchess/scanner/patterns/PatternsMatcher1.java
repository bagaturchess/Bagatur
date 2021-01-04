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


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.matchers.Matcher_Base;
import bagaturchess.scanner.patterns.matchers.Matcher_Composite;
import bagaturchess.scanner.patterns.matchers.MatchingStatistics;


public class PatternsMatcher1 {
	
	
	public static void main(String[] args) {
		
		try {
			
			BufferedImage image_board = ImageIO.read(new File("./data/tests/test13.png"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/lichess.org/test1.png"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/chess.com/test1.png"));
			image_board = ScannerUtils.resizeImage(image_board, 256);
			ScannerUtils.saveImage("board_original", image_board, "png");
			int[][] grayBoard = ScannerUtils.convertToGrayMatrix(image_board);
			//grayBoard = filterBackground(grayBoard);
			ScannerUtils.saveImage("board_gray", ScannerUtils.createGrayImage(grayBoard), "png");
			
			Matcher_Base matcher = new Matcher_Composite(256);
			ResultPair<String, MatchingStatistics> result = matcher.scan(grayBoard);
            System.out.println(result.getFirst() + " " + result.getSecond().totalDelta);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static int[][] filterBackground(int[][] grayBoard) {
		
		Set<Integer> emptySquares = ScannerUtils.getEmptySquares(grayBoard);
		ResultPair<Integer, Integer> bgcolors = ScannerUtils.getSquaresColor(grayBoard, emptySquares);
		
		//ScannerUtils.get
		
		return setBackground(grayBoard, emptySquares, bgcolors.getFirst(), bgcolors.getSecond(), 128);
	}
	
	
	private static int[][] setBackground(int[][] grayBoard, Set<Integer> emptySquares, int whiteSquare, int blackSquare, int color) {
		
		int[][] result = new int[grayBoard.length][grayBoard.length];
		
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				int bgcolor = (file + rank) % 2 == 0 ? whiteSquare : blackSquare;
				
				if (!emptySquares.contains(fieldID)) {
					for (int i1 = i; i1 < i + grayBoard.length / 8; i1++) {
						for (int j1 = j; j1 < j + grayBoard.length / 8; j1++) {
							if (Math.abs(grayBoard[i1][j1] - bgcolor) <= -1) {
								result[i1][j1] = color;
							} else {
								result[i1][j1] = grayBoard[i1][j1];
							}
						}
					}
				}
			}
		}
		
		return result;
	}
}
