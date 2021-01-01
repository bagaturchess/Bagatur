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

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;


public class Matcher_Composite extends Matcher_Base {
	
	
	private List<Matcher_Base> matchers = new ArrayList<Matcher_Base>();
	private List<Matcher_Base> matchers_128 = new ArrayList<Matcher_Base>();
	
	public Matcher_Composite(int imageSize) throws IOException {
		
		super(null);
		
		matchers.add(new ChessCom(imageSize));
		matchers.add(new LichessOrg(imageSize));
		
		matchers_128.add(new ChessCom(128));
		matchers_128.add(new LichessOrg(128));
	}
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard) {
		
		ResultPair<Integer, MatrixUtils.PatternMatchingData> best_whiteKingData = null;
		ResultPair<Integer, MatrixUtils.PatternMatchingData> best_blackKingData = null;
		
		int best_index = 0;
		double best_delta = Double.MAX_VALUE;
		for (int i = 0; i < matchers_128.size(); i++) {
			
			ResultPair<Integer, MatrixUtils.PatternMatchingData> whiteKingData =
					matchers_128.get(i).scanForPiece(grayBoard, Constants.PID_W_KING);
			
			System.out.println("Matcher_Composite: scan: " + matchers_128.get(i).getClass().getCanonicalName()
					+ " white king id is " + whiteKingData.getFirst() + " delta is " + whiteKingData.getSecond().delta);
			
			ResultPair<Integer, MatrixUtils.PatternMatchingData> blackKingData =
					matchers_128.get(i).scanForPiece(grayBoard, Constants.PID_B_KING);
			
			System.out.println("Matcher_Composite: scan: " + matchers_128.get(i).getClass().getCanonicalName()
					+ " black king id is " + blackKingData.getFirst() + " delta is " + blackKingData.getSecond().delta);
			
			double cur_delta = 0;
			cur_delta += whiteKingData.getSecond().delta;
			cur_delta += blackKingData.getSecond().delta;
			if (cur_delta < best_delta) {
				best_delta = cur_delta;
				best_index = i;
				best_whiteKingData = whiteKingData;
				best_blackKingData = blackKingData;
			}
		}
		
		System.out.println("Matcher_Composite: scan: Selected matcher is " + matchers.get(best_index).getClass().getCanonicalName());
		
		//return matchers.get(best_index).scan(grayBoard, best_whiteKingData.getFirst(), best_blackKingData.getFirst());
		ResultPair<String, MatchingStatistics> result = matchers.get(best_index).scan(grayBoard, -1, -1, false);
		
		if (matchers.get(best_index).getTotalDeltaThreshold() < result.getSecond().totalDelta) {
			System.out.println("Matcher_Composite: scan: total delta is " + result.getSecond().totalDelta + " start search again ...");
			result = matchers.get(best_index).scan(grayBoard, -1, -1, true);
		}
		
		return result;
	}
	
	
	@Override
	protected ResultPair<Integer, MatrixUtils.PatternMatchingData> scanForPiece(int[][] grayBoard, int pid) {
		throw new UnsupportedOperationException();
	}


	@Override
	protected double getTotalDeltaThreshold() {
		throw new UnsupportedOperationException();
	}
}
