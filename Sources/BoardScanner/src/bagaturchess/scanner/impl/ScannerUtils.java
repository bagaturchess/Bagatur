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
package bagaturchess.scanner.impl;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.internal.ChessConstants;


public class ScannerUtils {
	
	
	public static float[] convertInt2Float(int[] array) {
		float[] result = new float[array.length];
		for (int i = 0 ; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}
	
	
	public static int[] convertFloat2Int(float[] array) {
		int[] result = new int[array.length];
		for (int i = 0 ; i < array.length; i++) {
			result[i] = (int) array[i];
		}
		return result;
	}
	
	
	public static BufferedImage createSquareImage(ImageProperties imageProperties, int pid, Color squareColour) {
		BufferedImage image = new BufferedImage(imageProperties.getSquareSize(), imageProperties.getSquareSize(), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		g.setColor(squareColour);
		g.fillRect(0, 0, imageProperties.getSquareSize(), imageProperties.getSquareSize());
		
		if (pid != 0) {
			g.drawImage(imageProperties.getPiecesImages()[pid], 0, 0, imageProperties.getSquareSize(), imageProperties.getSquareSize(), squareColour, null);
		}
			
		return image;
	}
	
	
	public static BufferedImage createBoardImage(ImageProperties imageProperties, String fen) {
		
		BufferedImage image = new BufferedImage(imageProperties.getImageSize(), imageProperties.getImageSize(), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if ((i + j) % 2 == 0) {
					g.setColor(imageProperties.getColorWhiteSquare());
				} else {
					g.setColor(imageProperties.getColorBlackSquare());
				}
				
				g.fillRect(i * imageProperties.getSquareSize(), j * imageProperties.getSquareSize(), imageProperties.getSquareSize(), imageProperties.getSquareSize());
			}
		}
		
		String[] fenArray = fen.split(" ");
		int positionCount = 63;
		for (int i = 0; i < fenArray[0].length(); i++) {
			
			int x = (7 - positionCount % 8) * imageProperties.getSquareSize();
			int y = (7 - positionCount / 8) * imageProperties.getSquareSize();
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
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_PAWN], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'N':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_KNIGHT], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'B':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_BISHOP], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'R':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_ROOK], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'Q':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_QUEEN], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'K':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_W_KING], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'p':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_PAWN], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'n':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_KNIGHT], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'b':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_BISHOP], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'r':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_ROOK], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'q':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_QUEEN], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			case 'k':
				g.drawImage(imageProperties.getPiecesImages()[Constants.PID_B_KING], x, y, imageProperties.getSquareSize(), imageProperties.getSquareSize(), whiteSquare ? imageProperties.getColorWhiteSquare() : imageProperties.getColorBlackSquare(), null);
				positionCount--;
				break;
			}
		}
		
		return image;
	}
	
	
	public static String createFENFromPIDs(int[] pids) {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 63; i >= 0; i--) {
			if (pids[i] >= 1 && pids[i] <= 6) {
				sb.append(ChessConstants.FEN_WHITE_PIECES[Constants.PIECE_IDENTITY_2_TYPE[pids[i]]]);
			} else {
				sb.append(ChessConstants.FEN_BLACK_PIECES[Constants.PIECE_IDENTITY_2_TYPE[pids[i]]]);
			}
			
			if (i % 8 == 0 && i != 0) {
				sb.append("/");
			}
		}
		
		String fen = sb.toString();
		fen = fen.replaceAll("11111111", "8");
		fen = fen.replaceAll("1111111", "7");
		fen = fen.replaceAll("111111", "6");
		fen = fen.replaceAll("11111", "5");
		fen = fen.replaceAll("1111", "4");
		fen = fen.replaceAll("111", "3");
		fen = fen.replaceAll("11", "2");
		return fen;
	}
	
	
	public static void saveImage(String fen, BufferedImage image, String formatName) {
		try {
			File file = new File("./data/" + (fen + "." + formatName).replace('/', '_'));
			ImageIO.write(image, formatName, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static BufferedImage createGrayImage(int[][] matrix) {
		BufferedImage image = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				Color c = new Color(matrix[i][j], matrix[i][j], matrix[i][j]);
				image.setRGB(i, j, c.getRGB());
			}
		}
		return image;
	}
	
	
	public static BufferedImage createGrayImage(float[] vector) {
		int size = (int) Math.sqrt(vector.length);
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < vector.length; i++) {
			Color c = new Color((int)vector[i], (int)vector[i], (int)vector[i]);
			image.setRGB(i / size, i % size, c.getRGB());
		}
		return image;
	}
	
	
	public static float[] convertToFlatGrayArray(BufferedImage image) {
		
		if (image.getHeight() != image.getWidth()) {
			throw new IllegalStateException();
		}
		
		int count = 0;
		float[] inputs = new float[image.getHeight() * image.getHeight()];
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				
				int rgb = image.getRGB(i, j);
				
				//int alpha = (rgb & 0xff000000) >>> 24;
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
			    
				if (red != green || blue != green) {
					throw new IllegalStateException();
				}
				
				//inputs[count] = (red + green + blue) / 3;
			    //inputs[count++] = red * 0.299 + green * 0.587 + blue * 0.114;
				inputs[count++] = green;
			}
		}
		
		return inputs;
	}
	
	
	public static int[][] convertToGrayMatrix(BufferedImage image) {
		
		if (image.getHeight() != image.getWidth()) {
			throw new IllegalStateException();
		}
		
		int[][] inputs = new int[image.getHeight()][image.getHeight()];
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				
				int rgb = image.getRGB(i, j);
				
				//int alpha = (rgb & 0xff000000) >>> 24;
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
			    
				if (red != green || blue != green) {
					throw new IllegalStateException();
				}
				
				//inputs[count] = (red + green + blue) / 3;
			    //inputs[count++] = red * 0.299 + green * 0.587 + blue * 0.114;
				inputs[i][j] = green;
			}
		}
		
		return inputs;
	}
	
	
	public static int[] convertToFlatGrayArray(int[][] matrix) {
		
		int count = 0;
		int[] inputs = new int[matrix.length * matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				int grayPixel = matrix[i][j];
				inputs[count++] = grayPixel;
			}
		}
		
		return inputs;
	}

	
	public static double[] convertToFlatRGBArray(BufferedImage image) {
		
		if (image.getHeight() != image.getWidth()) {
			throw new IllegalStateException();
		}
		
		int count = 0;
		double[] inputs = new double[3 * image.getHeight() * image.getHeight()];
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				
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
	
	
	public static BufferedImage convertToGrayScale(BufferedImage image) {
	  BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	  Graphics g = result.getGraphics();
	  g.drawImage(image, 0, 0, null);
	  return result;
	}
	
	
	public static BufferedImage resizeImage(BufferedImage image, int squareSize) {
		  BufferedImage result = new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_RGB);
		  Graphics g = result.getGraphics();
		  g.drawImage(image, 0, 0, squareSize, squareSize, null);
		  return result;
	}
}
