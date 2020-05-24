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
package bagaturchess.search.impl.tpt;


public class TTEntry_BaseImpl implements ITTEntry {

		
	private boolean isEmpty;
	private int depth;
	private int flag;
	private int eval;
	private int bestmove;
	
	
	@Override
	public boolean isEmpty() {
		return isEmpty;
	}
	

	@Override
	public int getDepth() {
		return depth;
	}


	@Override
	public int getFlag() {
		return flag;
	}
	
	
	@Override
	public int getEval() {
		return eval;
	}


	@Override
	public int getBestMove() {
		return bestmove;
	}
	
	
	@Override
	public void setIsEmpty(boolean _isEmpty) {
		isEmpty = _isEmpty;
	}
	
	
	@Override
	public void setDepth(int _depth) {
		depth = _depth;
	}
	
	
	@Override
	public void setFlag(int _flag) {
		flag = _flag;
	}
	
	
	@Override
	public void setEval(int _eval) {
		eval = _eval;
	}
	
	
	@Override
	public void setBestMove(int _bestmove) {
		bestmove = _bestmove;
	}
}
