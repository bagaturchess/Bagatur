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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public abstract class Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT = 0.25f;
	
	
	protected BoardProperties boardProperties;
	
	
	protected Matcher_Base(BoardProperties _imageProperties) {
		boardProperties = _imageProperties;
	}
	
	
	protected abstract double getTotalDeltaThreshold();
	
	
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + grayBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard);
		
		ResultPair<Integer, Integer> bgcolorsOfSquares = MatrixUtils.getSquaresColor(grayBoard, emptySquares);
		
		int[] pids = new int[64];
		int countPIDs = 0;
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				pids[fieldID] = Constants.PID_NONE;
						
				if (!emptySquares.contains(fieldID)) {
					int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
					//int bgcolor_avg = (int) MatrixUtils.calculateColorStats(squareMatrix).getEntropy();
					
					MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
					bestPatternData.x = 0;
					bestPatternData.y = 0;
					bestPatternData.size = squareMatrix.length;
					//ImageHandlerSingleton.getInstance().printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
					
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
					//bgcolors.add(bgcolor_avg);
					bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
					
					ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData
						= getPID(squareMatrix, true, bgcolors, pidsToSearch, fieldID);
					pids[fieldID] = pidAndData.getFirst();
					MatrixUtils.PatternMatchingData data = pidAndData.getSecond();
					result.totalDelta += data.delta;
					//if (pids[fieldID] != Constants.PID_NONE) {
					countPIDs++;
					//}
				}
			}
		}
		
		result.totalDelta = result.totalDelta / (double) (countPIDs);
		//result.totalDelta *= boardProperties.getSquareSize() * Math.sqrt(boardProperties.getSquareSize());
		
		return new ResultPair<String, MatchingStatistics> (BoardUtils.createFENFromPIDs(pids), result);
	}
	
	
	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][] graySquareMatrix, boolean iterateSize,
			List<Integer> bgcolors, Set<Integer> pids, int fieldID) {
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		int counter = 0;
		
		int maxSize = graySquareMatrix.length;
		int startSize = iterateSize ? (int) ((1 - SIZE_DELTA_PERCENT) * maxSize) : maxSize;
				
		for (int size = startSize; size <= maxSize; size++) {
			
			MatrixUtils.PatternMatchingData curData_best  = null;
			
			for (Integer pid : pids) {
				
				for (int i = 0; i < bgcolors.size(); i++) {
					
					int bgcolor = bgcolors.get(i);
					
					int[][] grayPattern = pid == Constants.PID_NONE ?
							ImageHandlerSingleton.getInstance().createSquareImage(bgcolor, size)
							: ImageHandlerSingleton.getInstance().createPieceImage(boardProperties.getPiecesSetFileNamePrefix(), pid, bgcolor, size);
					MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
					
					if (curData_best == null || curData_best.delta > curData.delta) {
						curData_best = curData;
					}
				}
				
				if (bestData == null || bestData.delta > curData_best.delta) {
					bestData = curData_best;
					bestPID = pid;
					
					//ImageHandlerSingleton.getInstance().printInfo(bestData, "" + fieldID + "_best" + (counter++));
				}
			}
		}
		
		//ImageHandlerSingleton.getInstance().printInfo(graySquareMatrix, bestData, "" + fieldID + "_matching");
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
}
