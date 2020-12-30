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

import bagaturchess.scanner.cnn.impl.ImageProperties;
import bagaturchess.scanner.common.ResultPair;


public class Matcher_Composite extends Matcher_Base {
	
	
	List<Matcher_Base> matchers = new ArrayList<Matcher_Base>();
	
	
	public Matcher_Composite(int imageSize) throws IOException {
		super(new ImageProperties(imageSize));
		matchers.add(new ChessCom(imageSize));
		matchers.add(new LichessOrg(imageSize));
	}
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		
		MatchingStatistics bestStat = null;
		String bestFEN = null;
		
		for (int i = 0; i < matchers.size(); i++) {
			
			ResultPair<String, MatchingStatistics> currentMatch = matchers.get(i).scan(grayBoard);
			String currentFEN = currentMatch.getFirst();
			MatchingStatistics currentStat = currentMatch.getSecond();
			
			if (bestStat == null || bestStat.totalDelta > currentStat.totalDelta) {
				bestStat = currentStat;
				bestFEN = currentFEN;
			}
		}
		
		return new ResultPair<String, MatchingStatistics>(bestFEN, bestStat);
	}
}
