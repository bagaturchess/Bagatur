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
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class BGColorTester {
	
	
	private static final Color[] GRAY_COLORS = new Color[256];
	
	static {
		
		for (int r = 0; r < 256; r++) {
			for (int g = 0; g < 256; g++) {
				for (int b = 0; b < 256; b++) {
					int gray = (int) (r * 0.2989d + g * 0.5870 + b * 0.1140);
					GRAY_COLORS[gray] = new Color(r, g, b);
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		
		try {
			
			int[][] imageMatrix = createSquareImage(GRAY_COLORS[200], 64);
			System.out.println(imageMatrix[0][0]);
			BufferedImage image = ScannerUtils.createGrayImage(imageMatrix);
			ScannerUtils.saveImage("source", image, "png");
			
			int avg = ScannerUtils.getAVG(imageMatrix);
			System.out.println(avg);
			
			
			/*BufferedImage image = ImageIO.read(new File("./data/tests/14_square.png"));
			image = ScannerUtils.convertToGrayScale(image);
			int[][] grayImage = ScannerUtils.convertToGrayMatrix(image);
			
			BufferedImage resultImage = ScannerUtils.createGrayImage(grayImage);
			ScannerUtils.saveImage("input", resultImage, "png");
			
			int count = 0;
			long gray = 0;
			for (int i = 0; i < grayImage.length; i++) {
				for (int j = 0; j < grayImage.length; j++) {
					gray += grayImage[i][j] * grayImage[i][j];
					count++;
				}
			}
			
			int bgcolor = ScannerUtils.getAVG(grayImage);
			int bgcolor1 = (int) (gray / (count * count));
			System.out.println("bgcolor=" + bgcolor + ", calc=" + bgcolor1);
			
			int[][] generated = ScannerUtils.createSquareImage(bgcolor, grayImage.length);
			BufferedImage resultImage1 = ScannerUtils.createGrayImage(generated);
			ScannerUtils.saveImage("test", resultImage1, "png");
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static final int[][] createSquareImage(Color bgcolor, int size) {
		BufferedImage imageSquare = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imageSquare.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, imageSquare.getWidth(), imageSquare.getHeight());
		//imageSquare = ScannerUtils.convertToGrayScale(imageSquare);
		return convertToGrayMatrix(imageSquare);
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
				
				//inputs[count] = (red + green + blue) / 3;
			    //inputs[count++] = red * 0.299 + green * 0.587 + blue * 0.114;
				inputs[i][j] = (int) (red * 0.2989d + green * 0.5870d + blue * 0.1140d);
			}
		}
		
		return inputs;
	}
}
