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
package bagaturchess.scanner.patterns.impl1.matchers;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public abstract class Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT = 0.25f;
	private static final int MAX_ROTATION_PERCENT = 0;
	
	
	protected BoardProperties boardProperties;
	
	
	protected Matcher_Base(BoardProperties _imageProperties) throws IOException {
		boardProperties = _imageProperties;
	}
	
	
	protected abstract double getTotalDeltaThreshold();
	
	
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		return scan(grayBoard, false);
	}
	
	
	protected ResultPair<String, MatchingStatistics> scan(int[][] grayBoard, boolean iterateBGColors) {
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException();
		}
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		
		Set<Integer> emptySquares = ScannerUtils.getEmptySquares(grayBoard);
		
		ResultPair<Integer, Integer> bgcolorsOfSquares = ScannerUtils.getSquaresColor(grayBoard, emptySquares);
		
		int[] pids = new int[64];
		
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				int pid = Constants.PID_NONE;
				//if (!emptySquares.contains(fieldID)) {
					
					int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
					int bgcolor_avg = (int) ScannerUtils.calculateColorStats(squareMatrix).getEntropy();
					
					MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
					bestPatternData.x = 0;
					bestPatternData.y = 0;
					bestPatternData.size = squareMatrix.length;
					//printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
					
					Set<Integer> pidsToSearch = new HashSet<Integer>();
					pidsToSearch.add(Constants.PID_NONE);
					if (fieldID >= 8 && fieldID <= 56) pidsToSearch.add(Constants.PID_W_PAWN);
					pidsToSearch.add(Constants.PID_W_KNIGHT);
					pidsToSearch.add(Constants.PID_W_BISHOP);
					pidsToSearch.add(Constants.PID_W_ROOK);
					pidsToSearch.add(Constants.PID_W_QUEEN);
					pidsToSearch.add(Constants.PID_W_KING);
					if (fieldID >= 8 && fieldID <= 56) pidsToSearch.add(Constants.PID_B_PAWN);
					pidsToSearch.add(Constants.PID_B_KNIGHT);
					pidsToSearch.add(Constants.PID_B_BISHOP);
					pidsToSearch.add(Constants.PID_B_ROOK);
					pidsToSearch.add(Constants.PID_B_QUEEN);
					pidsToSearch.add(Constants.PID_B_KING);
					
					List<Integer> bgcolors = new ArrayList<Integer>();
					if (!iterateBGColors) {
						//bgcolors.add(bgcolor_avg);
						bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
					}
					
					ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData
						= getPID(squareMatrix, true, bgcolors, pidsToSearch, fieldID);
					pid = pidAndData.getFirst();
					MatrixUtils.PatternMatchingData data = pidAndData.getSecond();
					result.totalDelta += data.delta;
				//}
				pids[fieldID] = pid;
			}
		}
		
		result.totalDelta = result.totalDelta / (double) (64 - emptySquares.size());
		//result.totalDelta *= boardProperties.getSquareSize() * Math.sqrt(boardProperties.getSquareSize());
		
		return new ResultPair<String, MatchingStatistics> (ScannerUtils.createFENFromPIDs(pids), result);
	}
	
	
	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][] graySquareMatrix, boolean iterateSize,
			List<Integer> bgcolors, Set<Integer> pids, int fieldID) {
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		int counter = 0;
		
		int maxSize = graySquareMatrix.length;
		int startSize = iterateSize ? (int) ((1 - SIZE_DELTA_PERCENT) * maxSize) : maxSize;
		
		for (int size = startSize; size <= maxSize; size++) {
			
			for (int angle = -MAX_ROTATION_PERCENT; angle <= MAX_ROTATION_PERCENT; angle++) {
				
				MatrixUtils.PatternMatchingData curData_best  = null;
				
				for (Integer pid : pids) {
					
					if (bgcolors != null && bgcolors.size() > 0) {
						
						for (int i = 0; i < bgcolors.size(); i++) {
							
							int bgcolor = bgcolors.get(i);
							
							int[][] grayPattern = pid == Constants.PID_NONE ?
									ScannerUtils.createSquareImage(bgcolor, size)
									: ScannerUtils.createPieceImage(boardProperties, pid, bgcolor, size);
							if (angle != 0) {
								grayPattern = MatrixUtils.rotateMatrix(grayPattern, angle);
							}
							MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
							
							if (curData_best == null || curData_best.delta > curData.delta) {
								curData_best = curData;
							}
						}
						
					} else {
					
						MatrixUtils.PatternMatchingData[] curData = new MatrixUtils.PatternMatchingData[256];
						
						int bgcolor = (int) ScannerUtils.calculateColorStats(graySquareMatrix).getEntropy();
						
						int[][] grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(bgcolor, size)
								: ScannerUtils.createPieceImage(boardProperties, pid, bgcolor, size);
						if (angle != 0) {
							grayPattern = MatrixUtils.rotateMatrix(grayPattern, angle);
						}
						curData[bgcolor] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						curData[bgcolor].color = bgcolor;
						
						MatrixUtils.PatternMatchingData curData_best_up = curData[bgcolor];
						
						int lowColor_up = bgcolor;
						int highColor_up = 255;
						int midColor_up;
						while(lowColor_up <= highColor_up) {
							
							midColor_up = (lowColor_up + highColor_up) / 2;
							grayPattern = pid == Constants.PID_NONE ?
									ScannerUtils.createSquareImage(midColor_up, size)
									: ScannerUtils.createPieceImage(boardProperties, pid, midColor_up, size);
							if (angle != 0) {
								grayPattern = MatrixUtils.rotateMatrix(grayPattern, angle);
							}
							curData[midColor_up] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
							
							if (curData[midColor_up].delta < curData_best_up.delta) {
								curData_best_up = curData[midColor_up];
								lowColor_up = midColor_up + 1;
							} else {
								highColor_up = midColor_up - 1;
							}
						}
						
						
						MatrixUtils.PatternMatchingData curData_best_down = curData[bgcolor];
						
						int lowColor_down = 0;
						int highColor_down = bgcolor;
						int midColor_down;
						while(lowColor_down <= highColor_down) {
							
							midColor_down = (lowColor_down + highColor_down) / 2;
							grayPattern = pid == Constants.PID_NONE ?
									ScannerUtils.createSquareImage(midColor_down, size)
									: ScannerUtils.createPieceImage(boardProperties, pid, midColor_down, size);
							if (angle != 0) {
								grayPattern = MatrixUtils.rotateMatrix(grayPattern, angle);
							}
							curData[midColor_down] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
							
							if (curData[midColor_down].delta < curData_best_up.delta) {
								curData_best_up = curData[midColor_down];
								highColor_down = midColor_down - 1;
							} else {
								lowColor_down = midColor_down + 1;
							}
						}
						
						curData_best = curData_best_up.delta < curData_best_down.delta ? curData_best_up : curData_best_down;
					}
					
					if (bestData == null || bestData.delta > curData_best.delta) {
						bestData = curData_best;
						bestPID = pid;
						
						//printInfo(bestData, "" + fieldID + "_best" + (counter++));
					}
				}
			}
		}
		
		//printInfo(graySquareMatrix, bestData, "" + fieldID + "_matching");
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	protected static void printInfo(int[][] board, MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = board[matcherData.x + i][matcherData.y + j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	protected static void printInfo(MatrixUtils.PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = matcherData.pattern[i][j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
}
