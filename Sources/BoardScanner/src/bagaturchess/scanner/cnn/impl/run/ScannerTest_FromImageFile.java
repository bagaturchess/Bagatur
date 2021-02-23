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
package bagaturchess.scanner.cnn.impl.run;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.BoardScanner;
import bagaturchess.scanner.cnn.impl.BoardScanner_Gray;
import bagaturchess.scanner.cnn.impl.model.NetworkModel;
import bagaturchess.scanner.cnn.impl.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;


public class ScannerTest_FromImageFile {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			//NetworkModel netmodel = new NetworkModel_Gray("scanner.bin", boardProperties.getSquareSize());
			//NetworkModel netmodel = new NetworkModel_Gray("scanner.chesscom.bin", boardProperties.getSquareSize());
			NetworkModel netmodel = new NetworkModel_Gray("scanner.lichessorg.bin", boardProperties.getSquareSize());
			BoardScanner scanner = new BoardScanner_Gray(netmodel);
			
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/lichess.org/test8.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/lichess.org/set1/input1.png"));
			BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/chess.com/set1/input1.png"));
			boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
			int[][] boardMatrix= ScannerUtils.convertToGrayMatrix(boardImage);
			
			double probability = scanner.getAccumulatedProbability(boardMatrix);
			
			System.out.println(probability);
			
			String fen = scanner.scan(boardMatrix);
			
			System.out.println(fen);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
