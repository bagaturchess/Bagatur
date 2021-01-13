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
package bagaturchess.scanner.patterns.impl1.preprocess;


import java.io.IOException;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ImagePreProcessor_WhiteBackground extends ImagePreProcessor_Base {
	
	
	public ImagePreProcessor_WhiteBackground(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("WhiteBackground_input", "png", image);
		
		ResultPair<Integer, Integer> squareColors = MatrixUtils.getSquaresColor(grayBoard);
		
		int size = (int) (1.1 * grayBoard.length);
		int[][] result = new int[size][size];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result.length; j++) {
				result[i][j] = squareColors.getFirst();
			}
		}
		
		int shift = (size - grayBoard.length) / 2;
		for (int i = 0; i < grayBoard.length; i++) {
			for (int j = 0; j < grayBoard.length; j++) {
				result[i + shift][j + shift] = grayBoard[i][j];
			}
		}
		
		Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);
		
		resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		ImageHandlerSingleton.getInstance().saveImage("WhiteBackground_result", "png", resultImage);
		
		return resultImage;
	}
}