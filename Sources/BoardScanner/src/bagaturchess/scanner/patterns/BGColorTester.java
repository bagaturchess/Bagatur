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

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class BGColorTester {
	
	
	public static void main(String[] args) {
		
		try {
			
			BufferedImage image = ImageIO.read(new File("./data/tests/14_square.png"));
			image = ScannerUtils.convertToGrayScale(image);
			int[][] grayImage = ScannerUtils.convertToGrayMatrix(image);
			
			BufferedImage resultImage = ScannerUtils.createGrayImage(grayImage);
			ScannerUtils.saveImage("input", resultImage, "png");
			
			for (int i = 0; i < grayImage.length; i++) {
				for (int j = 0; j < grayImage.length; j++) {
					System.out.println(grayImage[i][j]);
				}
			}
			
			int bgcolor = ScannerUtils.getAVG(grayImage);
			System.out.println("bgcolor=" + bgcolor);
			
			int[][] generated = ScannerUtils.createSquareImage(bgcolor, grayImage.length);
			BufferedImage resultImage1 = ScannerUtils.createGrayImage(generated);
			ScannerUtils.saveImage("test", resultImage1, "png");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
