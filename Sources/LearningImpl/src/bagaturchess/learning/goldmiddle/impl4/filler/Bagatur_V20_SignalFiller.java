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
package bagaturchess.learning.goldmiddle.impl4.filler;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.impl4.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl4.base.EvalUtil;
import bagaturchess.learning.goldmiddle.impl4.base.IEvalComponentsProcessor;


public class Bagatur_V20_SignalFiller implements ISignalFiller {
	
	
	private final ChessBoard board;
	private final EvalInfo evalInfo;
	
	
	public Bagatur_V20_SignalFiller(IBitBoard bitboard) {
		board = ((BoardImpl)bitboard).getChessBoard();
		evalInfo = new EvalInfo();
	}
	
	
	@Override
	public void fill(ISignals signals) {
		
		evalInfo.clearEvals1();
		evalInfo.clearEvals2();
		evalInfo.fillBoardInfo(board);
		
		IEvalComponentsProcessor evalComponentsProcessor = new EvalComponentsProcessor(signals);
		
		EvalUtil.eval1(board, evalInfo, evalComponentsProcessor);
		EvalUtil.eval2(board, evalInfo, evalComponentsProcessor);
	}
	
	
	@Override
	public void fillByComplexity(int complexity, ISignals signals) {
		switch(complexity) {
			case IFeatureComplexity.STANDARD:
				fill(signals);
				return;
			case IFeatureComplexity.PAWNS_STRUCTURE:
				//fillPawnSignals(signals);
				return;
			case IFeatureComplexity.PIECES_ITERATION:
				//fillPiecesIterationSignals(signals);
				return;
			case IFeatureComplexity.MOVES_ITERATION:
				//fillMovesIterationSignals(signals);
				return;
			case IFeatureComplexity.FIELDS_STATES_ITERATION:
				//throw new UnsupportedOperationException("FIELDS_STATES_ITERATION");
				return;
			default:
				throw new IllegalStateException("complexity=" + complexity);
		}
	}
	
	
	private final class EvalComponentsProcessor implements IEvalComponentsProcessor {
		
		
		private final ISignals signals;
		
		
		private EvalComponentsProcessor(final ISignals _signals) {
			signals = _signals;
		}
		
		
		@Override
		public void addEvalComponent(int evalPhaseID, int componentID, int value_o, int value_e, double weight_o, double weight_e) {
			
			double openningPart = (EvalUtil.PHASE_TOTAL - board.phase) / (double) EvalUtil.PHASE_TOTAL;
			//double openningPart = board.phase / (double) EvalUtil.PHASE_TOTAL;
			
			signals.getSignal(componentID).addStrength(interpolateInternal(value_o, value_e, openningPart), openningPart);
		}
		
		
		private double interpolateInternal(double o, double e, double openningPart) {
			return (o * openningPart + e * (1 - openningPart));
		}
	}
}
