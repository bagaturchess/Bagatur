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
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.BoardScanner;
import bagaturchess.scanner.cnn.impl.BoardScanner_Gray;
import bagaturchess.scanner.cnn.impl.model.NetworkModel;
import bagaturchess.scanner.cnn.impl.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;


public class ScannerTest_FromImageFile_CNNs {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/lichess.org/test8.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/chess.com/test5.png"));
			BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/lichess.org/set1/input7.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/chess.com/set1/input7.png"));
			boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
			int[][] boardMatrix = ScannerUtils.convertToGrayMatrix(boardImage);
			
			double probability1 = getMaxProbability(
					new String[] {"scanner.lichessorg.set1.1.bin",
									"scanner.lichessorg.set1.2.bin",
									"scanner.lichessorg.set1.3.bin",
									"scanner.lichessorg.set1.4.bin"
									}, boardMatrix);
			
			double probability2 = getMaxProbability(
					new String[] {"scanner.chesscom.set1.1.bin",
									"scanner.chesscom.set1.2.bin",
									"scanner.chesscom.set1.3.bin",
									"scanner.chesscom.set1.4.bin"
									}, boardMatrix);
			
			System.out.println("lichessorg=" + probability1 + ", chesscom=" + probability2);
			
			//String fen = scanner.scan(boardMatrix);
			
			//System.out.println(fen);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static float getMaxProbability(String[] CNNs, int[][] boardMatrix) throws ClassNotFoundException, IOException {
		
		BoardProperties boardProperties = new BoardProperties(256);
		
		float result = 0;
		for (String cnn: CNNs) {
			NetworkModel netmodel = new NetworkModel_Gray(cnn, boardProperties.getSquareSize());
			BoardScanner scanner = new BoardScanner_Gray(netmodel);
			double current = scanner.getAccumulatedProbability(boardMatrix);
			if (current > result) {
				result = (float) current;
			}
		}
		
		return result;
	}
}
