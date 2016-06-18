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
package com.bagaturchess.ucitournament.framework.match;


import java.io.IOException;

import bagaturchess.ucitracker.impl.Engine;


public class MatchRunner_FixedNodes extends MatchRunner {
	
	private int nodes;
	
	public MatchRunner_FixedNodes(int _nodes) {
		if (_nodes < 1) {
			throw new IllegalStateException("Depth should be more than 0 but depth=" + _nodes);
		}
		nodes = _nodes;
	}
	
	@Override
	protected void go(Engine engine) throws IOException {
		engine.go_FixedNodes(nodes);
	}

	@Override
	protected void afterGo(int colourToMove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeGo(int colourToMove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getRemainingTime(int colourToMove) {
		return 10;
	}
}
