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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.FilterInfo;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ImagePreProcessor_Crop_KMeans extends ImagePreProcessor_Base {
	
	
	public ImagePreProcessor_Crop_KMeans(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("input", "png", image);
		
		Object grayImage = ImageHandlerSingleton.getInstance().createGrayImage(grayBoard);
		ImageHandlerSingleton.getInstance().saveImage("input_gray", "png", grayImage);
		
		//K-Means start
		int NUMBER_OF_CLUSTERS = 4;
		
		//Initialize
		int[] centroids_values = initCentroids(NUMBER_OF_CLUSTERS);
		
		int[][] centroids_ids = new int[grayBoard.length][grayBoard.length];
		
		for (int i = 0; i < grayBoard.length; i++) {
			for (int j = 0; j < grayBoard.length; j++) {
				
				int bestDistance = Integer.MAX_VALUE;
				int bestCentroidID = -1;
				for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
					int distance = Math.abs(grayBoard[i][j] - centroids_values[centroid_id]);
					if (distance < bestDistance) {
						bestDistance = distance;
						bestCentroidID = centroid_id;
					}
				}
				
				centroids_ids[i][j] = bestCentroidID;
			}
		}
		
		
		boolean hasGlobalChange = true;
		
		long[] avgs_sum;
		long[] avgs_cnt;
		
		//Loop until convergence
		while (hasGlobalChange) {
			
			//System.out.println("start iteration " + count++);
			
			//Find avg
			avgs_sum = new long[NUMBER_OF_CLUSTERS];
			avgs_cnt = new long[NUMBER_OF_CLUSTERS];
			
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					int centroid_id = centroids_ids[i][j];
					avgs_sum[centroid_id] += grayBoard[i][j];
					avgs_cnt[centroid_id]++;
				}
			}
			
			for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
				centroids_values[centroid_id] = (int) (avgs_sum[centroid_id] / avgs_cnt[centroid_id]);
				//System.out.println("centroid_id " + centroid_id + " avg " + centroids_values[centroid_id]);
			}
			
			boolean hasChange = false;
			//Adjust values
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {		
					
					int bestDistance = Integer.MAX_VALUE;
					int bestCentroidID = -1;
					for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
						int distance = Math.abs(grayBoard[i][j] - centroids_values[centroid_id]);
						if (distance < bestDistance) {
							bestDistance = distance;
							bestCentroidID = centroid_id;
						}
					}
					
					if (bestCentroidID != centroids_ids[i][j]) {
						centroids_ids[i][j] = bestCentroidID;
						hasChange = true;
					}
				}
			}
			
			hasGlobalChange = hasChange;
		}
		//K-Means end
		
		
		//Print clusters
		/*for (int centoridID = 0; centoridID < centroids_values.length; centoridID++) {
			int[][] result = new int[grayBoard.length][grayBoard.length];
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					int cur_centroid_id = centroids_ids[i][j];
					if (cur_centroid_id == centoridID) {
						result[i][j] = grayBoard[i][j];
					}
				}
			}
			
			Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);
			ImageHandlerSingleton.getInstance().saveImage("kmeans" + centoridID + "_" + centroids_values[centoridID], "png", resultImage);
		}*/
		
		
		int[] weights = new int[centroids_values.length];
		VarStatistic[] avg_x = new VarStatistic[centroids_values.length];
		VarStatistic[] avg_y = new VarStatistic[centroids_values.length];
		for (int centoridID = 0; centoridID < centroids_values.length; centoridID++) {
			
			avg_x[centoridID] = new VarStatistic(false);
			avg_y[centoridID] = new VarStatistic(false);
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					int cur_centroid_id = centroids_ids[i][j];
					if (cur_centroid_id == centoridID) {
						avg_x[centoridID].addValue(i, i);
						avg_y[centoridID].addValue(j, j);
					}
				}
			}
			
			VarStatistic delta_avg = new VarStatistic(false);
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					int cur_centroid_id = centroids_ids[i][j];
					if (cur_centroid_id == centoridID) {
						double distance = calculateDistanceBetweenPoints(avg_x[centoridID].getEntropy(), avg_y[centoridID].getEntropy(), i, j);
						delta_avg.addValue(distance, distance);
						weights[centoridID]++;
					}
				}
			}
			//System.out.println("centoridID " + centoridID + ": avg=" + delta_avg.getEntropy() + ", disp=" + delta_avg.getDisperse() + ", weight=" + weights[centoridID]);
		}
		
		int[] centroidIDs = get2MaxWeightsIndexes(weights);
		int[][] boardPixels = new int[grayBoard.length][grayBoard.length];
		
		for (int index = 0; index < centroidIDs.length; index++) {
			int centoridID = centroidIDs[index];
			List<Point> points = new ArrayList<Point>();
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					int cur_centroid_id = centroids_ids[i][j];
					if (cur_centroid_id == centoridID) {
						double distance = calculateDistanceBetweenPoints(avg_x[centoridID].getEntropy(), avg_y[centoridID].getEntropy(), i, j);
						points.add(new Point(i, j, grayBoard[i][j], distance));
					}
				}
			}
			Collections.sort(points);
			
			float skipPercent = 0.13f;
			
			for (int i = (int) (skipPercent * points.size()); i < points.size(); i++) {
				Point point = points.get(i);
				boardPixels[point.i][point.j] = point.value;
			}
		}
		
		FilterInfo bestInfo = getSizes(boardPixels, 0);
		//System.out.println(bestInfo.minX + " " + bestInfo.minY + " " + bestInfo.maxX + " " + bestInfo.maxY);
		
		/*int[][] result = new int[bestInfo.maxX - bestInfo.minX][bestInfo.maxY - bestInfo.minY];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = grayBoard[bestInfo.minX + i][bestInfo.minY + j];
			}
		}
		
		Object resultImage = ImageHandlerSingleton.getInstance().createGrayImage(result);*/
		
		Object resultImage = ImageHandlerSingleton.getInstance().extractResult(image, bestInfo, 1f);
		
		resultImage = ImageHandlerSingleton.getInstance().resizeImage(resultImage, boardProperties.getImageSize());
		ImageHandlerSingleton.getInstance().saveImage("result", "png", resultImage);
		
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
	
	
	private int[] get2MaxWeightsIndexes(int[] weights) {
		int[] result_indexes = new int[2];
		int[] result_values = new int[2];
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > result_values[0]) {
				result_values[1] = result_values[0];
				result_values[0] = weights[i];
				result_indexes[1] = result_indexes[0];
				result_indexes[0] = i;
			}
		}
		return result_indexes;
	}
	
	
	private int[] initCentroids(int count) {
		int[] centroids_values = new int[count];
		for (int i = 0; i < centroids_values.length; i++) {
			centroids_values[i] = 255 / (i + 1);
		}
		return centroids_values;
	}
	
	
	public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {       
		return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}
	
	
	private static class Point implements Comparable<Point> {
		
		private int i;
		private int j;
		private int value;
		private double distance;
		
		
		private Point(int i, int j, int value, double distance) {
			super();
			this.i = i;
			this.j = j;
			this.value = value;
			this.distance = distance;
		}
		
		
		@Override
		public final int hashCode() {
		    return i * 1000 + j;
		}
		
		
		@Override
		public boolean equals(Object o) {
			return ((Point)o).i == i && ((Point)o).j == j;
		}
		
		
		@Override
		public int compareTo(Point o) {
			if (o == this) {
				return 0;
			}
			
			double delta = o.distance - distance;
			if (delta == 0) {
				return 0;
			} else if (delta > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
