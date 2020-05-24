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

/**
 * @author I027638
 *
 */
public interface ITTable {

	public abstract void correctAllDepths(int reduction);

	public abstract void get(long key, ITTEntry entry);

	public abstract TPTEntry put(long hashkey, int _smaxdepth, int _sdepth,
			int _colour, int _eval, int _alpha, int _beta, int _bestmove,
			byte movenumber);
	
	public abstract int getUsage();

}