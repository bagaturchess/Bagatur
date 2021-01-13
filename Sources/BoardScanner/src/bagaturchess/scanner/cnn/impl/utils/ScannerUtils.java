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
package bagaturchess.scanner.cnn.impl.utils;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ScannerUtils {
	
	
	public static final Color[] GRAY_COLORS = new Color[256];
	
	static {
		
		for (int r = 0; r < 256; r++) {
			for (int g = 0; g < 256; g++) {
				for (int b = 0; b < 256; b++) {
					int gray = (int) (r * 0.2989d + g * 0.5870 + b * 0.1142);
					//if (GRAY_COLORS[gray] == null) {
						GRAY_COLORS[gray] = new Color(r, g, b);
					//}
				}
			}
		}
	}
	
	
	public static float[] convertInt2Float(int[] array) {
		float[] result = new float[array.length];
		for (int i = 0 ; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}
	
	
	public static float[][] convertInt2Float(int[][] array) {
		float[][] result = new float[array.length][array.length];
		for (int i = 0 ; i < array.length; i++) {
			for (int j = 0 ; j < array.length; j++) {
				result[i][j] = array[i][j];
			}
		}
		return result;
	}
	
	
	public static float[][][] convertInt2Float(int[][][] array) {
		float[][][] result = new float[array.length][array.length][array[0][0].length];
		for (int i = 0 ; i < array.length; i++) {
			for (int j = 0 ; j < array[0].length; j++) {
				for (int k = 0 ; k < array[0][0].length; k++) {
					result[i][j][k] = array[i][j][k];
				}
			}
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
	
	
	public static BufferedImage createPieceImage(BoardProperties boardProperties, int pid, Color squareColour) {
		BufferedImage image = new BufferedImage(boardProperties.getSquareSize(), boardProperties.getSquareSize(), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		g.setColor(squareColour);
		g.fillRect(0, 0, boardProperties.getSquareSize(), boardProperties.getSquareSize());
		
		if (pid != 0) {
			g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), 0, 0, boardProperties.getSquareSize(), boardProperties.getSquareSize(), squareColour, null);
		}
			
		return image;
	}
	
	
	public static final int[][] createPieceImage(BoardProperties boardProperties, int pid, int bgcolor, int targetAvg, int size) {
		
		//System.out.println("IN targetAvg=" + targetAvg);
		
		int[][] bestImage = createPieceImageWithPieceColorAdjustment(boardProperties, pid, bgcolor, size, 0);
		int bestAvg = MatrixUtils.getAVG(bestImage);
		
		int colorAdjustment_up = 1;
		while (true) {
			int[][] currentImage = createPieceImageWithPieceColorAdjustment(boardProperties, pid, bgcolor, size, colorAdjustment_up);
			int curAvg = MatrixUtils.getAVG(currentImage);
			if (Math.abs(targetAvg - curAvg) <= Math.abs(targetAvg - bestAvg)) {
				//System.out.println("colorAdjustment_up=" + colorAdjustment_up + ", Math.abs(targetAvg - curAvg)=" + Math.abs(targetAvg - curAvg) + ", Math.abs(targetAvg - bestAvg)=" + Math.abs(targetAvg - bestAvg));
				bestImage = currentImage;
				bestAvg = curAvg;
				colorAdjustment_up++;
			} else {
				//System.out.println("break");
				break;
			}
		}
		
		int colorAdjustment_down = -1;
		while (true) {
			int[][] currentImage = createPieceImageWithPieceColorAdjustment(boardProperties, pid, bgcolor, size, colorAdjustment_down);
			int curAvg = MatrixUtils.getAVG(currentImage);
			if (Math.abs(targetAvg - curAvg) <= Math.abs(targetAvg - bestAvg)) {
				//System.out.println("colorAdjustment_down=" + colorAdjustment_down + ", Math.abs(targetAvg - curAvg)=" + Math.abs(targetAvg - curAvg) + ", Math.abs(targetAvg - bestAvg)=" + Math.abs(targetAvg - bestAvg));
				bestImage = currentImage;
				bestAvg = curAvg;
				colorAdjustment_down--;
			} else {
				//System.out.println("break");
				break;
			}
		}
		
		//System.out.println("OUT bestAvg=" + bestAvg);
		
		return bestImage;
	}
	
	
	private static final int[][] createPieceImageWithPieceColorAdjustment(BoardProperties boardProperties, int pid, int bgcolor, int size, int colorAdjustment) {
		
		Image piece = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, boardProperties.getPiecesSetFileNamePrefix(), size);
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(GRAY_COLORS[bgcolor]);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		Image pieceScaled = piece;//.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		g.drawImage(pieceScaled, 0, 0, null);
		
		int rgb_00 = imagePiece.getRGB(0, 0);
		int red_00 = (rgb_00 & 0xff0000) >> 16;
		int green_00 = (rgb_00 & 0xff00) >> 8;
		int blue_00 = rgb_00 & 0xff;
		if (red_00 != green_00 || blue_00 != green_00) {
			throw new IllegalStateException();
		}
		if (green_00 != bgcolor) {
			//throw new IllegalStateException();
		}
		
		
		for (int i = 0; i < imagePiece.getHeight(); i++) {
			for (int j = 0; j < imagePiece.getHeight(); j++) {
				
				int rgb = imagePiece.getRGB(i, j);
				
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
			    
				if (red != green || blue != green) {
					//throw new IllegalStateException();
				}
				
				//System.out.println(green + ", green_00=" + green_00);
				
				if (green != green_00) {
					if (true) throw new IllegalStateException(); 
					Color c = new Color(
								Math.min(255, Math.max(0, red + colorAdjustment)),
								Math.min(255, Math.max(0, green + colorAdjustment)),
								Math.min(255, Math.max(0, blue + colorAdjustment))
							);
					
					imagePiece.setRGB(i, j, c.getRGB());
				}
			}
		}
		
		return ScannerUtils.convertToGrayMatrix(imagePiece);
	}
	
	
	public static final int[][][] createPieceImage(BoardProperties boardProperties, int pid, Color bgcolor, int size) {
		Image piece = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, boardProperties.getPiecesSetFileNamePrefix(), size);
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		Image pieceScaled = piece;//.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		g.drawImage(pieceScaled, 0, 0, null);
		return ScannerUtils.convertToRGBMatrix(imagePiece);
	}
	
	
	public static final int[][] createPieceImage(String pieceSetName, int pid, int bgcolor, int size) {
		Image piece = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, pieceSetName, size);
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(GRAY_COLORS[bgcolor]);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		Image pieceScaled = piece;//.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		g.drawImage(pieceScaled, 0, 0, null);
		//imagePiece = ScannerUtils.convertToGrayScale(imagePiece);
		return ScannerUtils.convertToGrayMatrix(imagePiece);
	}
	
	
	public static final int[][] createSquareImage(int bgcolor, int size) {
		BufferedImage imageSquare = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imageSquare.getGraphics();
		g.setColor(GRAY_COLORS[bgcolor]);
		g.fillRect(0, 0, imageSquare.getWidth(), imageSquare.getHeight());
		//imageSquare = ScannerUtils.convertToGrayScale(imageSquare);
		return ScannerUtils.convertToGrayMatrix(imageSquare);
	}
	
	
	private static void fillRandom(BufferedImage imageSquare) {
		for (int i = 0; i < imageSquare.getHeight(); i++) {
			for (int j = 0; j < imageSquare.getHeight(); j++) {
				int rand = (int) (Math.random() * 256);
				Color c = GRAY_COLORS[rand];
				imageSquare.setRGB(i, j, c.getRGB());
			}
		}
	}
	
	
	public static void saveImage(String fen, BufferedImage image, String formatName) {
		try {
			File file = new File("./data/" + (fen + "." + formatName).replace('/', '_'));
			ImageIO.write(image, formatName, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static BufferedImage createRGBImage(int[][][] matrix) {
		BufferedImage image = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				Color c = new Color(matrix[i][j][0], matrix[i][j][1], matrix[i][j][2]);
				image.setRGB(i, j, c.getRGB());
			}
		}
		return image;
	}
	
	
	public static BufferedImage createGrayImage(int[][] matrix) {
		BufferedImage image = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				Color c = GRAY_COLORS[matrix[i][j]];
				image.setRGB(i, j, c.getRGB());
			}
		}
		return image;
	}
	
	
	public static BufferedImage createGrayImage(float[] vector) {
		int size = (int) Math.sqrt(vector.length);
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < vector.length; i++) {
			if (true) throw new IllegalStateException();
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
					//throw new IllegalStateException();
				}
				
				//inputs[count] = (red + green + blue) / 3;
			    //inputs[count++] = red * 0.299 + green * 0.587 + blue * 0.114;
				inputs[i][j] = (int) (red * 0.2989d + green * 0.5870 + blue * 0.1140);
			}
		}
		
		return inputs;
	}
	
	
	public static int[][] invertGrayMatrix(int[][] matrix) {
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				result[i][j] = 255 - matrix[i][j];
			}
		}
		
		return result;
	}
	
	
	public static int[][][] convertToRGBMatrix(BufferedImage image) {
		
		if (image.getHeight() != image.getWidth()) {
			throw new IllegalStateException();
		}
		
		int[][][] inputs = new int[image.getHeight()][image.getHeight()][3];
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				
				int rgb = image.getRGB(i, j);
				
				//int alpha = (rgb & 0xff000000) >>> 24;
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				
				inputs[i][j][0] = red;
				inputs[i][j][1] = green;
				inputs[i][j][2] = blue;
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
	
	
	public static BufferedImage resizeImage(BufferedImage image, int squareSize) {
		
		Image scaled =  image.getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH);
		
		BufferedImage result = new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
	    
		/*g.setComposite(AlphaComposite.Src);
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 */
		
		g.drawImage(scaled, 0, 0, squareSize, squareSize, null);
		
		return result;
	}
	
	
	public static BufferedImage enlarge(BufferedImage image, int initialSize, double scale, Color bgcolor) {
		
		BufferedImage result = new BufferedImage((int) (initialSize * scale), (int) (initialSize * scale), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, result.getWidth(), result.getHeight());
		g.drawImage(image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH),
				(int) (initialSize * (scale - 1) / 2f),
				(int) (initialSize * (scale - 1) / 2f),
				image.getWidth(), image.getHeight(), null);
		
		return result;
	}
	
	
	public static double compareImages(BufferedImage image1, BufferedImage image2) {
	    
		int width1 = image1.getWidth(); 
	    int width2 = image2.getWidth(); 
	    int height1 = image1.getHeight(); 
	    int height2 = image2.getHeight(); 
	
	    if ((width1 != width2) || (height1 != height2)) {
	        throw new IllegalStateException("Different dimensions");
	    }
	    
        long delta = 0; 
        for (int y = 0; y < height1; y++) { 
            for (int x = 0; x < width1; x++) { 
                int rgb1 = image1.getRGB(x, y); 
                int red1 = (rgb1 >> 16) & 0xff; 
                int green1 = (rgb1 >> 8) & 0xff; 
                int blue1 = (rgb1) & 0xff; 
                
                int rgb2 = image2.getRGB(x, y); 
                int red2 = (rgb2 >> 16) & 0xff; 
                int green2 = (rgb2 >> 8) & 0xff; 
                int blue2 = (rgb2) & 0xff; 
                
                delta += Math.abs(red1 - red2); 
                delta += Math.abs(green1 - green2); 
                delta += Math.abs(blue1 - blue2); 
            } 
        } 
        
        double all_pixels_count = width1 * height1 * 3; 
        
        double avg = delta / all_pixels_count; 
        
        return avg / 255; 
	}
}
