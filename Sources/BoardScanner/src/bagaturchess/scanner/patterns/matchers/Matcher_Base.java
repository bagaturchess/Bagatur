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
package bagaturchess.scanner.patterns.matchers;


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
import bagaturchess.scanner.cnn.impl.ImageProperties;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;


public abstract class Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT = 0.25f;
	
	
	protected ImageProperties imageProperties;
	
	
	protected Matcher_Base(ImageProperties _imageProperties) throws IOException {
		imageProperties = _imageProperties;
	}
	
	
	protected abstract double getTotalDeltaThreshold();
	
	
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		return scan(grayBoard, -1, -1, false);
	}
	
	
	protected ResultPair<Integer, MatrixUtils.PatternMatchingData> scanForPiece(int[][] grayBoard, int pid) {
		
		if (grayBoard.length != imageProperties.getImageSize()) {
			throw new IllegalStateException();
		}
		
		MatrixUtils.PatternMatchingData bestData = null;
		Integer bestSquare = null;
		
		Set<Integer> emptySquares = getEmptySquares(grayBoard);
		
		ResultPair<Integer, Integer> bgcolorsOfSquares = getSquaresColor(grayBoard, emptySquares);
		
		Set<Integer> pidsToSearch = new HashSet<Integer>();
		pidsToSearch.add(pid);
		
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (!emptySquares.contains(fieldID)) {
					
					int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
					
					int bgcolor_avg = (int) calculateColorStats(squareMatrix).getEntropy();
					
					List<Integer> bgcolors = new ArrayList<Integer>();
					//bgcolors.add(bgcolor_avg);
					bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
					
					ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData
						= getPID(squareMatrix, true, bgcolors, pidsToSearch, fieldID);
					if (pid != pidAndData.getFirst()) {
						throw new IllegalStateException();
					}
					
					MatrixUtils.PatternMatchingData curData = pidAndData.getSecond();
					if (bestData == null || bestData.delta > curData.delta) {
						bestData = curData;
						bestSquare = fieldID;
					}
				}
			}
		}
		
		bestData.delta *= imageProperties.getSquareSize() * Math.sqrt(imageProperties.getSquareSize());
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(
				bestSquare,
				bestData
				);
	}
	
	
	protected ResultPair<String, MatchingStatistics> scan(int[][] grayBoard, int whiteKingSquareID, int blackKingSquareID, boolean iterateBGColors) {
		
		if (grayBoard.length != imageProperties.getImageSize()) {
			throw new IllegalStateException();
		}
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		
		Set<Integer> emptySquares = getEmptySquares(grayBoard);
		
		ResultPair<Integer, Integer> bgcolorsOfSquares = getSquaresColor(grayBoard, emptySquares);
		
		int[] pids = new int[64];
		if (whiteKingSquareID != -1) {
			pids[whiteKingSquareID] = Constants.PID_W_KING;
		}
		if (blackKingSquareID != -1) {
			pids[blackKingSquareID] = Constants.PID_B_KING;
		}
		
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (fieldID == whiteKingSquareID || fieldID == blackKingSquareID) {
					continue;
				}
				
				int pid = Constants.PID_NONE;
				if (!emptySquares.contains(fieldID)) {
					
					int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
					int bgcolor_avg = (int) calculateColorStats(grayBoard).getEntropy();
					
					MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
					bestPatternData.x = 0;
					bestPatternData.y = 0;
					bestPatternData.size = squareMatrix.length;
					printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
					
					Set<Integer> pidsToSearch = new HashSet<Integer>();
					if (fieldID >= 8 && fieldID <= 56) pidsToSearch.add(Constants.PID_W_PAWN);
					pidsToSearch.add(Constants.PID_W_KNIGHT);
					pidsToSearch.add(Constants.PID_W_BISHOP);
					pidsToSearch.add(Constants.PID_W_ROOK);
					pidsToSearch.add(Constants.PID_W_QUEEN);
					if (whiteKingSquareID == -1) pidsToSearch.add(Constants.PID_W_KING);
					if (fieldID >= 8 && fieldID <= 56) pidsToSearch.add(Constants.PID_B_PAWN);
					pidsToSearch.add(Constants.PID_B_KNIGHT);
					pidsToSearch.add(Constants.PID_B_BISHOP);
					pidsToSearch.add(Constants.PID_B_ROOK);
					pidsToSearch.add(Constants.PID_B_QUEEN);
					if (blackKingSquareID == -1) pidsToSearch.add(Constants.PID_B_KING);
					
					List<Integer> bgcolors = new ArrayList<Integer>();
					if (!iterateBGColors) {
						bgcolors.add(bgcolor_avg);
						bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
					}
					
					ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData
						= getPID(squareMatrix, true, bgcolors, pidsToSearch, fieldID);
					pid = pidAndData.getFirst();
					MatrixUtils.PatternMatchingData data = pidAndData.getSecond();
					result.totalDelta += data.delta;
				}
				pids[fieldID] = pid;
				
				//System.out.println(squareDisperse);
			}
		}
		
		result.totalDelta = result.totalDelta / (double) (64 - emptySquares.size());
		result.totalDelta *= imageProperties.getSquareSize() * Math.sqrt(imageProperties.getSquareSize());
		
		return new ResultPair<String, MatchingStatistics> (ScannerUtils.createFENFromPIDs(pids), result);
	}
	
	
	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][] graySquareMatrix, boolean iterateSize, List<Integer> bgcolors, Set<Integer> pids, int fieldID) {
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		for (Integer pid : pids) {
			
			int maxSize = graySquareMatrix.length;
			int startSize = iterateSize ? (int) ((1 - SIZE_DELTA_PERCENT) * maxSize) : maxSize;
			
			for (int size = startSize; size <= maxSize; size++) {
				
				MatrixUtils.PatternMatchingData curData_best  = null;
				
				if (bgcolors != null && bgcolors.size() > 0) {
					
					for (int i = 0; i < bgcolors.size(); i++) {
						
						int bgcolor = bgcolors.get(i);
						
						int[][] grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(bgcolor, size)
								: ScannerUtils.createPieceImage(imageProperties, pid, bgcolor, size);
						MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						
						if (curData_best == null || curData_best.delta > curData.delta) {
							curData_best = curData;
						}
					}
					
				} else {
				
					MatrixUtils.PatternMatchingData[] curData = new MatrixUtils.PatternMatchingData[256];
					
					int bgcolor = (int) calculateColorStats(graySquareMatrix).getEntropy();
					
					int[][] grayPattern = pid == Constants.PID_NONE ?
							ScannerUtils.createSquareImage(bgcolor, size)
							: ScannerUtils.createPieceImage(imageProperties, pid, bgcolor, size);
					curData[bgcolor] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
					curData[bgcolor].color = bgcolor;
					
					MatrixUtils.PatternMatchingData curData_best_up = curData[bgcolor];
					
					/*int lowColor_up = bgcolor;
					int highColor_up = 255;
					int midColor_up;
					while(iterateColor && lowColor_up <= highColor_up) {
						
						midColor_up = (lowColor_up + highColor_up) / 2;
						grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(midColor_up, size)
								: ScannerUtils.createPieceImage(imageProperties, pid, midColor_up, size);
						curData[midColor_up] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						
						if (curData[midColor_up].delta < curData_best_up.delta) {
							curData_best_up = curData[midColor_up];
							lowColor_up = midColor_up + 1;
						} else {
							highColor_up = midColor_up - 1;
						}
					}*/
					
					for (int color = bgcolor + 1; color < 256; color++) {
						grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(color, size)
								: ScannerUtils.createPieceImage(imageProperties, pid, color, size);
						curData[color] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						curData[color].color = color;
						if (curData[color].delta >= curData[color - 1].delta) {
							break;
						}
						curData_best_up = curData[color];
					}
					
					MatrixUtils.PatternMatchingData curData_best_down = curData[bgcolor];
					
					/*int lowColor_down = 0;
					int highColor_down = bgcolor;
					int midColor_down;
					while(iterateColor && lowColor_down <= highColor_down) {
						
						midColor_down = (lowColor_down + highColor_down) / 2;
						grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(midColor_down, size)
								: ScannerUtils.createPieceImage(imageProperties, pid, midColor_down, size);
						curData[midColor_down] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						
						if (curData[midColor_down].delta < curData_best_up.delta) {
							curData_best_up = curData[midColor_down];
							highColor_down = midColor_down - 1;
						} else {
							lowColor_down = midColor_down + 1;
						}
					}*/
					
					for (int color = bgcolor - 1; color >= 0; color--) {
						grayPattern = pid == Constants.PID_NONE ?
								ScannerUtils.createSquareImage(color, size)
								: ScannerUtils.createPieceImage(imageProperties, pid, color, size);
						curData[color] = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
						curData[color].color = color;
						if (curData[color].delta >= curData[color + 1].delta) {
							break;
						}
						curData_best_down = curData[color];
					}
					
					curData_best = curData_best_up.delta < curData_best_down.delta ? curData_best_up : curData_best_down;
				}
				
				if (bestData == null || bestData.delta > curData_best.delta) {
					bestData = curData_best;
					bestPID = pid;
				}
			}
		}
		
		
		if (this instanceof ChessCom) {
			
			printInfo(graySquareMatrix, bestData, "" + fieldID);
		}
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	private static VarStatistic calculateColorStats(int[][] grayMatrix) {
		
		VarStatistic stat = new VarStatistic(false);
		
		for (int i = 0; i < grayMatrix.length; i++) {
			for (int j = 0; j < grayMatrix.length; j++) {
				int cur = grayMatrix[i][j];
				stat.addValue(cur, cur);
			}
		}
		
		return stat;
	}
	
	
	protected Set<Integer> getEmptySquares(int[][] grayBoard) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		VarStatistic colorDeviations = new VarStatistic(false);
		Map<Integer, VarStatistic> squaresStats = new HashMap<Integer, VarStatistic>();
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				
				VarStatistic squareStat = calculateColorStats(squareMatrix);
				squaresStats.put(fieldID, squareStat);
				
				colorDeviations.addValue(squareStat.getDisperse(), squareStat.getDisperse());
			}
		}
		
		for (Integer fieldID: squaresStats.keySet()) {
			VarStatistic squareStat = squaresStats.get(fieldID);
			if (squareStat.getDisperse() < colorDeviations.getEntropy() - colorDeviations.getDisperse() / 7.9f) {
				emptySquaresIDs.add(fieldID);
			}
		}
		
		return emptySquaresIDs;
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
	
	
	private ResultPair<Integer, Integer> getSquaresColor(int[][] grayBoard, Set<Integer> emptySquares) {
		
		VarStatistic stat_all = new VarStatistic(false);
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (emptySquares.contains(fieldID)) {
					for (int i1 = i; i1 < i + grayBoard.length / 8; i1++) {
						for (int j1 = j; j1 < j + grayBoard.length / 8; j1++) {
							stat_all.addValue(grayBoard[i1][j1], grayBoard[i1][j1]);
						}
					}
				}
			}
		}
		
		VarStatistic stat_white = new VarStatistic(false);
		VarStatistic stat_black = new VarStatistic(false);
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (emptySquares.contains(fieldID)) {
					for (int i1 = i; i1 < i + grayBoard.length / 8; i1++) {
						for (int j1 = j; j1 < j + grayBoard.length / 8; j1++) {
							if (grayBoard[i1][j1] > stat_all.getEntropy()) {
								stat_white.addValue(grayBoard[i1][j1], grayBoard[i1][j1]);
							} else {
								stat_black.addValue(grayBoard[i1][j1], grayBoard[i1][j1]);
							}
						}
					}
				}
			}
		}
		
		return new ResultPair<Integer, Integer>((int)stat_white.getEntropy(), (int)stat_black.getEntropy());
	}
	
	
	private int[][] setBackground(int[][] grayBoard, Set<Integer> emptySquares, int color) {
		
		Set<Integer> skipColors = new HashSet<Integer>();
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (emptySquares.contains(fieldID)) {
					for (int i1 = i; i1 < i + grayBoard.length / 8; i1++) {
						for (int j1 = j; j1 < j + grayBoard.length / 8; j1++) {
							skipColors.add(grayBoard[i1][j1]);
						}
					}
				}
			}
		}
		
		int[][] result = new int[grayBoard.length][grayBoard.length];
		for (int i = 0; i < grayBoard.length; i++) {
			for (int j = 0; j < grayBoard.length; j++) {
				if (skipColors.contains(grayBoard[i][j])) {
					result[i][j] = color;
				} else {
					result[i][j] = grayBoard[i][j];
				}
			}
		}
		
		return result;
	}
}
