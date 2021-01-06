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


public class ImagePreProcessor_Impl4 extends ImagePreProcessor_Base {
	
	
	public ImagePreProcessor_Impl4(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("input", "png", image);
		
		int[][] iteration_array = grayBoard;
		
		int iterations = 20;
		while (iterations > 0) {
			float[][] scores = new float[iteration_array.length][iteration_array.length];
			for (int i = 0; i < iteration_array.length; i++) {
				for (int j = 0; j < iteration_array.length; j++) {
					int cur_color = iteration_array[i][j];
					if (cur_color != 0) {
						int startX = Math.max(0, i - iteration_array.length / 16);
						int endX = Math.min(iteration_array.length, i + iteration_array.length / 16);
						int startY = Math.max(0, j - iteration_array.length / 16);
						int endY = Math.min(iteration_array.length, j + iteration_array.length / 16);
						for (int i1 = startX; i1 < endX; i1++) {
							for (int j1 = startY; j1 < endY; j1++) {
								int cur_neighbour = iteration_array[i1][j1];
								if (cur_neighbour != 0) {
									int delta = cur_color - cur_neighbour == 0 ? 1 : cur_color - cur_neighbour;
									scores[i][j] += 1 / (float) Math.abs(delta);
								}
							}
						}
					}
				}
			}
			
			VarStatistic scoresStat = new VarStatistic(false);
			for (int i = 0; i < scores.length; i++) {
				for (int j = 0; j < scores.length; j++) {
					float score = scores[i][j];
					if (score != 0) {
						scoresStat.addValue(score, score);
					}	
				}
			}
			
			int[][] result_tmp = new int[iteration_array.length][iteration_array.length];
			for (int i = 0; i < iteration_array.length; i++) {
				for (int j = 0; j < iteration_array.length; j++) {
					if (scores[i][j] >= scoresStat.getEntropy() - scoresStat.getDisperse() && scores[i][j] <= scoresStat.getEntropy() + scoresStat.getDisperse()) {
						result_tmp[i][j] = iteration_array[i][j];
					}
				}
			}
			
			Object resultImageTmp = ImageHandlerSingleton.getInstance().createGrayImage(result_tmp);
			ImageHandlerSingleton.getInstance().saveImage("rotate_filtered" + iterations, "png", resultImageTmp);
			
			iterations--;
			
			iteration_array = result_tmp;
		}
		
		return null;
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
		
		
		private boolean isInitialized() {
			return minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
		}
	}
}
