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
package bagaturchess.scanner.cnn.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.cnn.impl.model.NetworkModel_Gray;


public class MatcherFinder {
	
	
	private List<BoardScanner> scanners;
	private List<String> netsNames;
	
	
	public MatcherFinder(int squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		
		scanners = new ArrayList<BoardScanner>();
		netsNames = _netsNames;
		
		for (int i = 0; i < netsStreams.size(); i++) {
			scanners.add(new BoardScanner_Gray(new NetworkModel_Gray(netsStreams.get(i), squareSize)));
		}
	}
	
	
	public void findMatcher(Object image) {
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < scanners.size(); i++) {
			String currentName = netsNames.get(i);
			double currentProb = scanners.get(i).getAccumulatedProbability(image);
			
			System.out.println("MatcherFinder: " + currentName + " " + currentProb);
		}
		long endTime = System.currentTimeMillis();
		
		System.out.println("MatcherFinder: Time is " + (endTime - startTime) + " ms");
	}
}
