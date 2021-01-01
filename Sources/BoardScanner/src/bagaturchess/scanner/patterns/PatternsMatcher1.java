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
import bagaturchess.scanner.patterns.matchers.Matcher_Base;
import bagaturchess.scanner.patterns.matchers.Matcher_Composite;


public class PatternsMatcher1 {
	
	
	public static void main(String[] args) {
		
		try {
			
			BufferedImage image_board = ImageIO.read(new File("./data/tests/test4.jpg"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/lichess.org/test4.png"));
			//BufferedImage image_board = ImageIO.read(new File("./data/tests/chess.com/test1.png"));
			image_board = ScannerUtils.resizeImage(image_board, 192);
			image_board = ScannerUtils.convertToGrayScale(image_board);
			//ScannerUtils.saveImage("board", image_board, "png");
			int[][] grayBoard = ScannerUtils.convertToGrayMatrix(image_board);
			
			Matcher_Base matcher = new Matcher_Composite(192);
			String fen = matcher.scan(grayBoard);
            System.out.println(fen);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
