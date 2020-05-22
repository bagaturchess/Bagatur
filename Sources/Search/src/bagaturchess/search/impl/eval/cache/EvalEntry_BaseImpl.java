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
package bagaturchess.search.impl.eval.cache;


public class EvalEntry_BaseImpl implements IEvalEntry {

		
	protected boolean empty;
	protected byte level;
	protected int eval;
	
	
	@Override
	public boolean isEmpty() {
		return empty;
	}
	

	@Override
	public byte getLevel() {
		return level;
	}


	@Override
	public int getEval() {
		return eval;
	}


	@Override
	public void setEval(int _eval) {
		eval = _eval;
	}
	
	
	@Override
	public void setLevel(byte _level) {
		level = _level;
	}
	
	
	@Override
	public void setIsEmpty(boolean _empty) {
		empty = _empty;
	}
}
