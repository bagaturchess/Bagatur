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
package bagaturchess.opening.impl.traverser;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.opening.api.traverser.OpeningsVisitor;



public class OpeningsVisitorImpl implements OpeningsVisitor {
	
	private int counter;
	
	public void visitPosition(IBitBoard bitboard) {
		System.out.println(bitboard);
		counter++;
	}
	
	public void begin() {
		counter = 0;
	}
	
	public void end() {
		System.out.println("Count: " + counter);
	}
}
