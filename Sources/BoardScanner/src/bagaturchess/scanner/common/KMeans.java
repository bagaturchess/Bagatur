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
package bagaturchess.scanner.common;


public class KMeans {
	
	
	public int[] centroids_values;
	public int[][] centroids_ids;
	public int[] weights;
	private long[] avgs_sum;
	private long[] avgs_cnt;
	
	
	public KMeans(int K, int[][] source) {
		
		//K-Means start
		int NUMBER_OF_CLUSTERS = K;
		
		//Initialize
		centroids_values = initCentroids(NUMBER_OF_CLUSTERS);
		
		centroids_ids = new int[source.length][source.length];
		
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				
				int bestDistance = Integer.MAX_VALUE;
				int bestCentroidID = -1;
				for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
					int distance = Math.abs(source[i][j] - centroids_values[centroid_id]);
					if (distance < bestDistance) {
						bestDistance = distance;
						bestCentroidID = centroid_id;
					}
				}
				
				centroids_ids[i][j] = bestCentroidID;
			}
		}
		
		
		boolean hasGlobalChange = true;
		
		//Loop until convergence
		while (hasGlobalChange) {
			
			//System.out.println("start iteration " + count++);
			
			//Find avg
			avgs_sum = new long[NUMBER_OF_CLUSTERS];
			avgs_cnt = new long[NUMBER_OF_CLUSTERS];
			
			for (int i = 0; i < source.length; i++) {
				for (int j = 0; j < source.length; j++) {
					int centroid_id = centroids_ids[i][j];
					avgs_sum[centroid_id] += source[i][j];
					avgs_cnt[centroid_id]++;
				}
			}
			
			for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
				centroids_values[centroid_id] = (int) (avgs_sum[centroid_id] / avgs_cnt[centroid_id]);
				//System.out.println("centroid_id " + centroid_id + " avg " + centroids_values[centroid_id]);
			}
			
			boolean hasChange = false;
			//Adjust values
			for (int i = 0; i < source.length; i++) {
				for (int j = 0; j < source.length; j++) {		
					
					int bestDistance = Integer.MAX_VALUE;
					int bestCentroidID = -1;
					for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
						int distance = Math.abs(source[i][j] - centroids_values[centroid_id]);
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
		
		//Init weights
		weights = new int[NUMBER_OF_CLUSTERS];
		for (int i = 0; i < source.length; i++) {
			for (int j = 0; j < source.length; j++) {
				int cur_centroid_id = centroids_ids[i][j];
				weights[cur_centroid_id]++;
			}
		}
	}
	
	
	public int[] get2MaxWeightsIndexes() {
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
}
