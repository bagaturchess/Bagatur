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
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;


public class Matcher_Composite extends Matcher_Base {
	
	
	private static final int CLASSIFIERS_SIZE = 64;
	
	
	List<Matcher_Base> matchers = new ArrayList<Matcher_Base>();
	List<Matcher_Base> matchers_classifiers = new ArrayList<Matcher_Base>();
	
	
	public Matcher_Composite(int imageSize) throws IOException {
		
		super(null);
		
		matchers.add(new ChessCom(imageSize));
		matchers.add(new LichessOrg(imageSize));
		
		matchers_classifiers.add(new ChessCom(CLASSIFIERS_SIZE));
		matchers_classifiers.add(new LichessOrg(CLASSIFIERS_SIZE));
	}
	
	
	protected MatrixUtils.PatternMatchingData scanForPiece(int[][] grayBoard, int pid) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		
		MatchingStatistics bestStat = null;
		int bestMatcherIndex = -1;
		
		int[][] grayBoard_short = ScannerUtils.convertToGrayMatrix(ScannerUtils.resizeImage(ScannerUtils.createGrayImage(grayBoard), CLASSIFIERS_SIZE));
		for (int i = 0; i < matchers_classifiers.size(); i++) {
			
			ResultPair<String, MatchingStatistics> currentMatch = matchers_classifiers.get(i).scan(grayBoard_short);
			MatchingStatistics currentStat = currentMatch.getSecond();
			
			if (bestStat == null || bestStat.totalDelta > currentStat.totalDelta) {
				bestStat = currentStat;
				bestMatcherIndex = i;
			}
		}
		
		return matchers.get(bestMatcherIndex).scan(grayBoard);
	}
}
