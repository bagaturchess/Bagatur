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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ImagePreProcessor_Impl2 extends ImagePreProcessor_Base {
	
	
	private static final int MAX_ROTATION_PERCENT = 5;
	
	
	public ImagePreProcessor_Impl2(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard);
		ResultPair<Integer, Integer> bgcolours = MatrixUtils.getSquaresColor(grayBoard, emptySquares);
		VarStatistic colorStat = MatrixUtils.calculateColorStats(grayBoard);
		
		Map<Integer, Integer> colorsCounts = new HashMap<Integer, Integer>();
		
		//int[][] result_tmp = new int[grayBoard.length][grayBoard.length];
		for (int i = 0; i < grayBoard.length; i++) {
			for (int j = 0; j < grayBoard.length; j++) {
				int cur_color = grayBoard[i][j];
				
				if (Math.abs(bgcolours.getFirst() - cur_color) <= colorStat.getDisperse() / 3
						|| Math.abs(bgcolours.getSecond() - cur_color) <= colorStat.getDisperse() / 3) {
					//result_tmp[i][j] = grayBoard[i][j];
					
					if (colorsCounts.containsKey(cur_color)) {
						Integer count = colorsCounts.get(cur_color);
						colorsCounts.put(cur_color, count + 1);
					} else {
						colorsCounts.put(cur_color, 1);
					}
				}
			}
		}
		
		//Object resultImageTmp = ImageHandlerSingleton.getInstance().createGrayImage(result_tmp);
		//ImageHandlerSingleton.getInstance().saveImage("filtered", "png", resultImageTmp);
		
		VarStatistic colorsCountStat = new VarStatistic(false);
		for (int color : colorsCounts.keySet()) {
			int count = colorsCounts.get(color);
			colorsCountStat.addValue(count, count);
		}
		
		
		if (colorsCounts.size() < 2) {
			throw new IllegalStateException("Not enough colors.");
		}
		
		FilterInfo bestInfo = null;
		for (float angleInDegrees = -MAX_ROTATION_PERCENT; angleInDegrees <= MAX_ROTATION_PERCENT; angleInDegrees += 0.1) {
			
			int[][] source = angleInDegrees == 0 ? grayBoard : MatrixUtils.rotateMatrix(grayBoard, angleInDegrees, 0);
			
			FilterInfo curInfo = getSizes(source, colorsCounts, colorsCountStat);
			curInfo.angleInDegrees = angleInDegrees;
			curInfo.source = source;
			
			if (bestInfo == null || bestInfo.isSmaller(curInfo)) {
				bestInfo = curInfo;
			}
		}
		
		int[][] result = new int[bestInfo.maxX - bestInfo.minX][bestInfo.maxY - bestInfo.minY];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = bestInfo.source[bestInfo.minX + i][bestInfo.minY + j];
			}
		}
		
		
		Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);
		resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		//resultImage = ImageHandlerSingleton.getInstance().enlarge(resultImage, 1.025f, ImageHandlerSingleton.getInstance().getAVG(resultImage));
		//resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		
		ImageHandlerSingleton.getInstance().saveImage("filter_result_" +  bestInfo.angleInDegrees, "png", resultImage);
		
		return resultImage;
	}
	
	
	private FilterInfo getSizes(int[][] source, Map<Integer, Integer> colorsCounts, VarStatistic colorsCountStat) {
		FilterInfo finfo = new FilterInfo();
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				int color = source[i][j];
				Integer colorCount = colorsCounts.get(color);
				if (colorCount != null && colorCount > colorsCountStat.getEntropy() - colorsCountStat.getDisperse()) {
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
	}
}
