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

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ImagePreProcessor_Impl3 extends ImagePreProcessor_Base {
	
	
	private static final int MAX_ROTATION_PERCENT = 5;
	
	
	public ImagePreProcessor_Impl3(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("input", "png", image);
		
		//Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard);
		//ResultPair<Integer, Integer> bgcolours = MatrixUtils.getSquaresColor(grayBoard, emptySquares);
		int[][] iteration_array = grayBoard;
		int interations = 10;
		while (interations > 0) {
			VarStatistic colorStat = MatrixUtils.calculateColorStats(iteration_array, 0);
			int[][] result_tmp = new int[iteration_array.length][iteration_array.length];
			for (int i = 0; i < iteration_array.length; i++) {
				for (int j = 0; j < iteration_array.length; j++) {
					int cur_color = iteration_array[i][j];
					if (cur_color >= colorStat.getEntropy() - colorStat.getDisperse()
							&& cur_color <= colorStat.getEntropy() + colorStat.getDisperse()) {
						result_tmp[i][j] = iteration_array[i][j];
					}
				}
			}
			Object resultImageTmp = ImageHandlerSingleton.getInstance().createGrayImage(result_tmp);
			ImageHandlerSingleton.getInstance().saveImage("rotate_filtered" + interations, "png", resultImageTmp);
			
			iteration_array = result_tmp;
			
			interations--;
		}
		
		FilterInfo bestInfo = getSizes(iteration_array, 0);
		
		int[][] result = new int[bestInfo.maxX - bestInfo.minX][bestInfo.maxY - bestInfo.minY];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = grayBoard[bestInfo.minX + i][bestInfo.minY + j];
			}
		}
		
		Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);
		resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		//resultImage = ImageHandlerSingleton.getInstance().enlarge(resultImage, 1.025f, ImageHandlerSingleton.getInstance().getAVG(resultImage));
		//resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		
		ImageHandlerSingleton.getInstance().saveImage("rotate_filter_result_" +  bestInfo.angleInDegrees, "png", resultImage);
		
		return resultImage;
	}
	
	
	private FilterInfo getSizes(int[][] source, int skipValue) {
		FilterInfo finfo = new FilterInfo();
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				int color = source[i][j];
				if (color != skipValue) {
					if (i < finfo.minX) {
						finfo.minX = i;
					}
					if (i > finfo.maxX) {
						finfo.maxX = i;
					}
					if (j < finfo.minY) {
						finfo.minY = j;
					}
					if (j > finfo.maxY) {
						finfo.maxY = j;
					}
				}
			}
		}
		return finfo;
	}
	
	
	private static class FilterInfo {
		
		private int minX = Integer.MAX_VALUE;
		private int minY = Integer.MAX_VALUE;
		private int maxX = Integer.MIN_VALUE;
		private int maxY = Integer.MIN_VALUE;
		private float angleInDegrees = 0;
		private int[][] source;
		
		private boolean isSmaller(FilterInfo info) {
			return maxX - minX > info.maxX - info.minX && maxY - minY > info.maxY - info.minY;
		}
		
		
		private boolean isInitialized() {
			return minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
		}
	}
}
