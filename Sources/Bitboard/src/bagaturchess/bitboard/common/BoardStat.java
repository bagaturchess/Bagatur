/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.common;

import java.util.ArrayList;
import java.util.List;

public class BoardStat {
	public Measure forwardMove = new Measure("forwardMove");
	public Measure backwardMove = new Measure("backwardMove");
	public Measure forwardNullMove = new Measure("forwardNullMove");
	public Measure backwardNullMove = new Measure("backwardNullMove");
	
	public Measure allMoves = new Measure("allMoves");
	public Measure capturePromotionMoves = new Measure("capturePromotionMoves");
	public Measure nonCaptureNonPromotionMoves = new Measure("nonCaptureNonPromotionMoves");
	public Measure promotions = new Measure("promotions");
	public Measure promotions2 = new Measure("promotions2");
	public Measure directCheckMoves = new Measure("directCheckMoves");
	public Measure hiddenCheckMoves = new Measure("hiddenCheckMoves");
	public Measure allCheckMoves = new Measure("allCheckMoves");
	public Measure capturePromotionCheckMoves = new Measure("capturePromotionCheckMoves");
	public Measure kingEscapes = new Measure("kingEscapes");
	
	public Measure hasMove = new Measure("hasMove");
	public Measure hasMoveInNonCheck = new Measure("hasMoveInNonCheck");
	public Measure hasMoveInCheck = new Measure("hasMoveInCheck");

	public Measure countMoves = new Measure("countMoves");
	public Measure countMovesInCheck = new Measure("countMovesInCheck");
	public Measure countMovesInNonCheck = new Measure("countMovesInNonCheck");
	public Measure countCheckMoves = new Measure("countCheckMoves");
	public Measure countCapturePromotionMovesInNonCheck = new Measure("countCapturePromotionMovesInNonCheck");
	public Measure countCapturePromotionCheckMovesInNonCheck = new Measure("countCapturePromotionCheckMovesInNonCheck");
	
	public Measure isInCheck = new Measure("isInCheck");
	public Measure isCheckMove = new Measure("isCheckMove");
	public Measure isDirectCheckMove = new Measure("isDirectCheckMove");
	public Measure checksCount = new Measure("checksCount");
	public Measure isPossible = new Measure("isPossible");
	
	public Measure stateRepetition = new Measure("stateRepetition");
	public Measure fillCheckKeepers = new Measure("fillCheckKeepers");
	
	public Measure testing = new Measure("testing");
	
	private List<Measure> measures = new ArrayList<Measure>();
	
	
	public BoardStat() {
		measures.add(forwardMove);
		measures.add(backwardMove);
		measures.add(forwardNullMove);
		measures.add(backwardNullMove);
		
		measures.add(allMoves);
		measures.add(capturePromotionMoves);
		measures.add(nonCaptureNonPromotionMoves);
		measures.add(promotions);
		measures.add(promotions2);
		measures.add(directCheckMoves);
		measures.add(hiddenCheckMoves);
		measures.add(allCheckMoves);
		measures.add(capturePromotionCheckMoves);
		measures.add(kingEscapes);
		
		measures.add(hasMove);
		measures.add(hasMoveInNonCheck);
		measures.add(hasMoveInCheck);

		measures.add(countMoves);
		measures.add(countMovesInCheck);
		measures.add(countMovesInNonCheck);
		measures.add(countCheckMoves);
		measures.add(countCapturePromotionMovesInNonCheck);
		measures.add(countCapturePromotionCheckMovesInNonCheck);
		
		measures.add(isInCheck);
		measures.add(isCheckMove);
		measures.add(isDirectCheckMove);
		measures.add(checksCount);
		
		measures.add(isPossible);
		
		measures.add(stateRepetition);
		measures.add(fillCheckKeepers);
		measures.add(testing);
	}
	
	public void clear() {
		forwardMove.clear();
		backwardMove.clear();
		forwardNullMove.clear();
		backwardNullMove.clear();
		
		allMoves.clear();
		capturePromotionMoves.clear();
		nonCaptureNonPromotionMoves.clear();
		promotions.clear();
		promotions2.clear();
		directCheckMoves.clear();
		hiddenCheckMoves.clear();
		allCheckMoves.clear();
		capturePromotionCheckMoves.clear();
		kingEscapes.clear();
		
		hasMove.clear();
		hasMoveInNonCheck.clear();
		hasMoveInCheck.clear();
		
		countMoves.clear();
		countMovesInCheck.clear();
		countMovesInNonCheck.clear();
		countCheckMoves.clear();
		countCapturePromotionMovesInNonCheck.clear();
		countCapturePromotionCheckMovesInNonCheck.clear();
		
		isInCheck.clear();
		isCheckMove.clear();
		isDirectCheckMove.clear();
		checksCount.clear();
		
		isPossible.clear();
		
		stateRepetition.clear();
		fillCheckKeepers.clear();
		testing.clear();
	}
	
	public static class Measure {
		
		public String name;
		public long calls;
		public long callOutput;
		public double callAVGTimeInNanos;
		
		private long startTime = 0;
		
		public Measure(String _name) {
			name = _name;
		}
		
		public void start() {
			startTime = System.nanoTime();
		}
		
		public void stop(long _callOutput) {
			callOutput += _callOutput;
			
			//An = An-1 * ((n-1)/n) + an/n
			long endTime = System.nanoTime();
			long timeInNanos = endTime - startTime;
			calls++;
			callAVGTimeInNanos = callAVGTimeInNanos * ((calls - 1) / (double) calls)
									+ timeInNanos / (double) calls;
		}
		
		public void clear() {
			calls = 0;
			callAVGTimeInNanos = 0;
			callOutput = 0;
		}
		
		public String toString() {
			String result = "";
			result += "" + name + ", " + calls + ", " + callAVGTimeInNanos
			+ ", " + callOutput + ", " + (calls != 0 ? (callOutput / calls) : 0);
			return result;
		}
	}
	
	public String toString() {
		String result = "";
		for (Measure tmp: measures) {
			result += tmp + "\r\n";
		}
		return result;
	}
}
