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
package bagaturchess.scanner.patterns.impl1;


import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.impl1.matchers.Matcher_Base;
import bagaturchess.scanner.patterns.impl1.matchers.Matcher_Composite;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class PatternsMatcherMain {
	
	
	public static void main(String[] args) {
		
		try {
			
			int INPUT_IMAGE_SIZE = 192;
			
			//Object image_board = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/test13.png");
			//Object image_board = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/lichess.org/test1.png");
			Object image_board = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/chess.com/test1.png");
			image_board = ImageHandlerSingleton.getInstance().resizeImage(image_board, INPUT_IMAGE_SIZE);
			ImageHandlerSingleton.getInstance().saveImage("board_original", "png", image_board);
			int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image_board);
			ImageHandlerSingleton.getInstance().saveImage("board_gray", "png", ImageHandlerSingleton.getInstance().createGrayImage(grayBoard));
			
			Matcher_Base matcher = new Matcher_Composite(INPUT_IMAGE_SIZE);
			ResultPair<String, MatchingStatistics> result = matcher.scan(grayBoard);
            System.out.println(result.getFirst() + " " + result.getSecond().totalDelta);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
