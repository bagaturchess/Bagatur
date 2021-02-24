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


import java.io.FileInputStream;
import java.io.IOException;

import bagaturchess.scanner.cnn.impl.model.NetworkModel_Gray;


public class MatcherFinder {
	
	
	private BoardScanner scanner_lichessorg1;
	private BoardScanner scanner_chesscom1;
	
	
	public MatcherFinder(int squareSize) throws ClassNotFoundException, IOException {
		scanner_lichessorg1 = new BoardScanner_Gray(new NetworkModel_Gray(new FileInputStream("scanner.lichessorg1.bin"), squareSize));
		scanner_chesscom1 = new BoardScanner_Gray(new NetworkModel_Gray(new FileInputStream("scanner.chesscom1.bin"), squareSize));
	}
	
	
	public String getMatcher(Object image) {
		long startTime = System.currentTimeMillis();
		double prob1 = scanner_lichessorg1.getAccumulatedProbability(image);
		double prob2 = scanner_chesscom1.getAccumulatedProbability(image);
		long endTime = System.currentTimeMillis();
		if (prob1 > prob2) {
			return "MatcherFinder: LiChess.org " + prob1 + " " + prob2 + ", time " + (endTime - startTime);
		} else {
			return "MatcherFinder: Chess.com " + prob1 + " " + prob2 + ", time " + (endTime - startTime);
		}
	}
}
