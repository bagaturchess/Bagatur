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
package bagaturchess.learning.goldmiddle.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.goldmiddle.impl3.cfg.EvaluationConfig_V18;
import bagaturchess.learning.goldmiddle.impl3.eval.BagaturEvaluator_Phases;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class EvalDiffVisitorImpl implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private IEvaluator evaluator;
	
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;
	
	
	public EvalDiffVisitorImpl() throws Exception {
	}
	
	
	public void newAdjustment(double actualWhitePlayerEval, double expectedWhitePlayerEval, double openingPart) {
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + status);
		}
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		double actualWhitePlayerEval = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		newAdjustment(actualWhitePlayerEval, expectedWhitePlayerEval, openingPart);
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		iteration++;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
		
		evaluator = new BagaturEvaluator_Phases(bitboard, null, new EvaluationConfig_V18());
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success percent before this iteration: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
	}
}
