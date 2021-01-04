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

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class BGColorTester {
	
	
	public static void main(String[] args) {
		
		try {
			
			int[][] imageMatrix = ScannerUtils.createSquareImage(137, 64);
			System.out.println(imageMatrix[0][0]);
			BufferedImage image = ScannerUtils.createGrayImage(imageMatrix);
			ScannerUtils.saveImage("source", image, "png");
			
			imageMatrix = ScannerUtils.convertToGrayMatrix(image);
			
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
}
