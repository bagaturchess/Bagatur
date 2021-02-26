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
package bagaturchess.scanner.cnn.dataset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.impl.learning.DataSetInitPair;
import bagaturchess.scanner.cnn.impl.learning.DataSetInitPair_ByBoardImage_Gray;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;

public class FileGenerator {

	public static void main(String[] args) {
		
		try {
			
			File rootDir = new File("./data/CNNinput/lichessorg1");
			File genDir = new File(rootDir, "training");
			genDir.mkdirs();
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			String[] inputFiles = new String[] {
					/*"./data/tests/cnn/chess.com/set1/input1.png",
					"./data/tests/cnn/chess.com/set1/input2.png",
					"./data/tests/cnn/chess.com/set1/input3.png",
					"./data/tests/cnn/chess.com/set1/input4.png",
					"./data/tests/cnn/chess.com/set1/input5.png",
					"./data/tests/cnn/chess.com/set1/input6.png",
					"./data/tests/cnn/chess.com/set1/input7.png",
					*/
					
					"./data/tests/cnn/lichess.org/set1/input1.png",
					"./data/tests/cnn/lichess.org/set1/input2.png",
					"./data/tests/cnn/lichess.org/set1/input3.png",
					"./data/tests/cnn/lichess.org/set1/input4.png",
					"./data/tests/cnn/lichess.org/set1/input5.png",
					"./data/tests/cnn/lichess.org/set1/input6.png",
					"./data/tests/cnn/lichess.org/set1/input7.png",
					
				};
			
			DataSetInitPair[] pairs = getInitPairs(boardProperties, inputFiles);
			
			final List<Object> images = new ArrayList<Object>();
			final List<Integer> pids = new ArrayList<Integer>();
			
			for (int i = 0; i < pairs.length; i++) {
				images.addAll(pairs[i].getImages());
				pids.addAll(pairs[i].getPIDs());
			}
			
			
			for (int i = 0; i < images.size(); i++) {
				int[][] matrix = (int[][]) images.get(i);
				BufferedImage image = ScannerUtils.createGrayImage(matrix);
				int pid = pids.get(i);
				File curDir = new File(genDir, "" + pid);
				curDir.mkdirs();
				
				File imageFile = new File(curDir, "" + System.currentTimeMillis() + ".png");
				ImageIO.write(image, "png", imageFile);
				
				Thread.currentThread().sleep(3);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static DataSetInitPair[] getInitPairs(BoardProperties boardProperties, String[] fileNames) throws IOException {
		DataSetInitPair[] result = new DataSetInitPair[fileNames.length];
		for (int i = 0; i < result.length; i++) {			
			result[i] = getInitPair(boardProperties, fileNames[i]);
		}
		return result;
	}
	
	
	private static DataSetInitPair getInitPair(BoardProperties boardProperties, String fileName) throws IOException {
		BufferedImage boardImage = ImageIO.read(new File(fileName));
		boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
		DataSetInitPair pair = new DataSetInitPair_ByBoardImage_Gray(boardImage);
		return pair;
	}
}
