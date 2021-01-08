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

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.FilterInfo;
import bagaturchess.scanner.common.KMeans;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ImagePreProcessor_Rotate extends ImagePreProcessor_Base {
	
	
	private static final int MAX_ROTATION_PERCENT = 5;
	
	
	public ImagePreProcessor_Rotate(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("Rotate_input", "png", image);
		
		KMeans kmeans = new KMeans(4, grayBoard);
		int[] clustersIndexes = kmeans.get2MaxWeightsIndexes();
		
		int[][] boardPixels_c1 = new int[grayBoard.length][grayBoard.length];
		int[][] boardPixels_c2 = new int[grayBoard.length][grayBoard.length];
		for (int index = 0; index < clustersIndexes.length; index++) {
			int centoridID = clustersIndexes[index];
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					if (centoridID == kmeans.centroids_ids[i][j]) {
						if (index == 0) {
							boardPixels_c1[i][j] = grayBoard[i][j];
						} else {
							boardPixels_c2[i][j] = grayBoard[i][j];
						}
					}
				}
			}
		}
		
		int gray1 = (int) MatrixUtils.calculateColorStats(boardPixels_c1, 0).getEntropy();
		int gray2 = (int) MatrixUtils.calculateColorStats(boardPixels_c2, 0).getEntropy();
		
		VarStatistic colorStat = MatrixUtils.calculateColorStats(grayBoard, -1);
		
		Map<Integer, Integer> colorsCounts = new HashMap<Integer, Integer>();
		
		int[][] result_tmp = new int[grayBoard.length][grayBoard.length];
		for (int i = 0; i < grayBoard.length; i++) {
			for (int j = 0; j < grayBoard.length; j++) {
				int cur_color = grayBoard[i][j];
				
				if (Math.abs(gray1 - cur_color) <= colorStat.getDisperse() / 3
						|| Math.abs(gray2 - cur_color) <= colorStat.getDisperse() / 3) {
					result_tmp[i][j] = grayBoard[i][j];
					
					if (colorsCounts.containsKey(cur_color)) {
						Integer count = colorsCounts.get(cur_color);
						colorsCounts.put(cur_color, count + 1);
					} else {
						colorsCounts.put(cur_color, 1);
					}
				}
			}
		}
		
		Object resultImageTmp = ImageHandlerSingleton.getInstance().createGrayImage(result_tmp);
		ImageHandlerSingleton.getInstance().saveImage("Rotate_filtered", "png", resultImageTmp);
		
		VarStatistic colorsCountStat = new VarStatistic(false);
		for (int color : colorsCounts.keySet()) {
			int count = colorsCounts.get(color);
			colorsCountStat.addValue(count, count);
		}
		
		FilterInfo bestInfo = null;
		for (float angleInDegrees = -MAX_ROTATION_PERCENT; angleInDegrees <= MAX_ROTATION_PERCENT; angleInDegrees += 1) {
			
			int[][] source = grayBoard;
			if (angleInDegrees != 0) {
				Object grayImage = ImageHandlerSingleton.getInstance().createGrayImage(source);
				grayImage = ImageHandlerSingleton.getInstance().rotateImageByDegrees(grayImage, angleInDegrees);
				source = ImageHandlerSingleton.getInstance().convertToGrayMatrix(grayImage);
			}
			
			FilterInfo curInfo = getSizes(source, colorsCounts, colorsCountStat);
			curInfo.angleInDegrees = angleInDegrees;
			curInfo.source = source;
			
			if (bestInfo == null || bestInfo.isSmaller(curInfo)) {
				bestInfo = curInfo;
			}
		}
		
		if (bestInfo.isInitialized()) {
			int[][] result = new int[bestInfo.maxX - bestInfo.minX + 1][bestInfo.maxY - bestInfo.minY + 1];
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[0].length; j++) {
					result[i][j] = bestInfo.source[bestInfo.minX + i][bestInfo.minY + j];
				}
			}
			
			
			Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);
			resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
			//resultImage = ImageHandlerSingleton.getInstance().enlarge(resultImage, 1.025f, ImageHandlerSingleton.getInstance().getAVG(resultImage));
			//resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
			
			ImageHandlerSingleton.getInstance().saveImage("Rotate_result", "png", resultImage);
			
			return resultImage;
		} else {
			return image;
		}
	}
	
	
	private FilterInfo getSizes(int[][] source, Map<Integer, Integer> colorsCounts, VarStatistic colorsCountStat) {
		FilterInfo finfo = new FilterInfo();
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				int color = source[i][j];
				Integer colorCount = colorsCounts.get(color);
				if (colorCount != null
						&& colorCount > colorsCountStat.getEntropy() - colorsCountStat.getDisperse()) {
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
}
