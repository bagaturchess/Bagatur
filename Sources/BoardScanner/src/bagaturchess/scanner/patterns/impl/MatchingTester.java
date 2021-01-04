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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.MatrixUtils.PatternMatchingData;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class MatchingTester {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(512, "set3");
			
			Image pieceImage = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_PAWN, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize());
			BufferedImage piece = createPattern(pieceImage,
					boardProperties.getSquareSize(),
					new Color(220, 220, 220));
			//ImageIO.read(new File("./res/set3_b_p.png"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/lichess.org/test1.png"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/chess.com/test1.png"));
			//image_board = ScannerUtils.resizeImage(image_board, 192);
			//ScannerUtils.saveImage("board", image_board, "png");
			int[][] grayBoard = ScannerUtils.convertToGrayMatrix(piece);
			
			
			BufferedImage pattern = ImageIO.read(new File("./res/set3_b_p_short.png"));
			
			PatternMatchingData best = null;
			
			for (int size = 8; size <= boardProperties.getSquareSize(); size++) {
				Image patternImage = pattern.getScaledInstance(size, size, Image.SCALE_SMOOTH);
				BufferedImage patternScaled = createPattern(patternImage, size, ScannerUtils.getAVG(piece));
				int[][] grayPattern = ScannerUtils.convertToGrayMatrix(patternScaled);
				
				//BufferedImage resultImage = ScannerUtils.createGrayImage(grayPattern);
				//ScannerUtils.saveImage("pattern" + size, resultImage, "png");
				
				PatternMatchingData data = MatrixUtils.matchImages(grayBoard, grayPattern);
				if (best == null || best.delta > data.delta) {
					best = data;
				}
			}
			
			printInfo(grayBoard, best, "match_" + best.size + "_" + best.delta);
			
			BufferedImage resultImage = ScannerUtils.createGrayImage(grayBoard);
			ScannerUtils.saveImage("test", resultImage, "png");
            
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
	
	
	private static BufferedImage createPattern(Image piece, int size, Color bgcolor) {
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		piece = piece.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		g.drawImage(piece, 0, 0, null);
		return imagePiece;
	}
}
