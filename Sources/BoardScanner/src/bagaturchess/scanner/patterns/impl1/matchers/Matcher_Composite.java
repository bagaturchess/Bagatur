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
import java.util.List;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class Matcher_Composite extends Matcher_Base {
	
	
	private List<Matcher_Base> matchers = new ArrayList<Matcher_Base>();
	private List<Matcher_Base> matchers_64 = new ArrayList<Matcher_Base>();
	
	
	public Matcher_Composite(int imageSize) {
		
		super(null);
		
		matchers.add(new Matcher_Set1(imageSize));
		matchers.add(new Matcher_Set3(imageSize));
		matchers.add(new Matcher_Set2(imageSize));
		
		matchers_64.add(new Matcher_Set1(64));
		matchers_64.add(new Matcher_Set3(64));
		matchers_64.add(new Matcher_Set2(64));
	}
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		
		int best_index = 0;
		double best_delta = Double.MAX_VALUE;
		
		int[][] grayBoard_64 = ScannerUtils.convertToGrayMatrix(
					ScannerUtils.resizeImage(ScannerUtils.createGrayImage(grayBoard), 64)
				);
		
		for (int i = 0; i < matchers_64.size(); i++) {
			
			ResultPair<String, MatchingStatistics> result = matchers_64.get(i).scan(grayBoard_64);
			
			MatchingStatistics stat = result.getSecond();
			
			System.out.println("Matcher_Composite: scan: " + matchers_64.get(i).getClass().getCanonicalName()
					+ " " + result.getFirst() + " delta is " + stat.totalDelta);
			
			if (stat.totalDelta < best_delta) {
				best_delta = stat.totalDelta;
				best_index = i;
			}
		}
		
		System.out.println("Matcher_Composite: scan: Selected matcher is " + matchers.get(best_index).getClass().getCanonicalName());
		
		ResultPair<String, MatchingStatistics> result = matchers.get(best_index).scan(grayBoard);
		
		/*if (matchers.get(best_index).getTotalDeltaThreshold() < result.getSecond().totalDelta) {
			System.out.println("Matcher_Composite: scan: " + result.getFirst() + " total delta is " + result.getSecond().totalDelta + " start scan again ...");
			result = matchers.get(best_index).scan(grayBoard, true);
		}*/
		
		return result;
	}


	@Override
	protected double getTotalDeltaThreshold() {
		throw new UnsupportedOperationException();
	}
}
