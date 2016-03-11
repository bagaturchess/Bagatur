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
package bagaturchess.bitboard.impl.dummy;

import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.state.FiguresStateListener;

public class DummyMaterial implements FiguresStateListener {

	//BaseEvaluation material;
	
	public DummyMaterial() {
		//material = new BaseEvaluation();
	}
	
	public void added(int figureID) {
		// TODO Auto-generated method stub
		
	}

	public void clearSameFigureMoves() {
		// TODO Auto-generated method stub
		
	}

	public int getBlackFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getBlackMaterial() {
		return (long) ((long) 100 * Math.random());
	}

	public int getBlackSameFigureMoves() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWhiteFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getWhiteMaterial() {
		return (long) ((long) 100 * Math.random());
	}

	public int getWhiteSameFigureMoves() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean inEndGame() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean inMiddleEndGame() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean inMiddleGame() {
		// TODO Auto-generated method stub
		return false;
	}

	public void killed(int figureID) {
		// TODO Auto-generated method stub
		
	}

	public void moveBackward(long[] move) {
		// TODO Auto-generated method stub
		
	}

	public void moveForward(long[] move) {
		// TODO Auto-generated method stub
		
	}

	public void produced(int figureID) {
		// TODO Auto-generated method stub
		
	}

	public void released(int figureID) {
		// TODO Auto-generated method stub
		
	}

	public void revived(int figureID) {
		// TODO Auto-generated method stub
		
	}

	public int getMovesCount(int figureID) {
		// TODO Auto-generated method stub
		return 0;
	}

}
