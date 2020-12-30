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


import java.io.IOException;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.ImageProperties;
import bagaturchess.scanner.cnn.impl.utils.MatrixUtils;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class Lichess extends Matcher_Base {
	
	
	private ImageProperties imageProperties;
	
	
	public Lichess(int imageSize) throws IOException {
		super(new ImageProperties(imageSize, "set1"));
	}
	
	
	public String scan(int[][] grayBoard) {
		
		int[] pids = new int[64];
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int filedID = 63 - (file + 8 * rank);
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				int pid = getPID(squareMatrix, i, j, filedID);
				pids[filedID] = pid;
			}
		}
		
		return ScannerUtils.createFENFromPIDs(pids);
	}
	
	
	private int getPID(int[][] graySquareMatrix, int i1, int j1, int filedID) {
		
		int bgcolor = ScannerUtils.getAVG(graySquareMatrix);
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		for (int pid = Constants.PID_W_PAWN; pid <= Constants.PID_B_KING; pid++) {
			
			float sizeDeltaPercent = 0.2f;
			int maxSize = graySquareMatrix.length;
			int startSize = (int) ((1 - sizeDeltaPercent) * maxSize);
			
			for (int size = startSize; size <= maxSize; size++) {
				int[][] grayPattern = ScannerUtils.createPieceImage(imageProperties, pid, bgcolor, size);
				
				MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(graySquareMatrix, grayPattern);
				
				if (bestData == null || bestData.delta > curData.delta) {
					bestData.delta = curData.delta;
					bestPID = pid;
				}
			}
		}
		
		printInfo(graySquareMatrix, bestData, "" + filedID);
		
		return bestPID;
	}
}
